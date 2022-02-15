package com.digital.opuserp.test;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import com.digital.opuserp.domain.AlteracoesHaver;
import com.digital.opuserp.domain.AlteracoesHaverCab;
import com.digital.opuserp.domain.Haver;
import com.digital.opuserp.domain.HaverCab;
import com.digital.opuserp.domain.HaverDetalhe;

public class ScriptHaverVincularTest {

	static EntityManager em;
	static EntityManagerFactory emf;
	public static void main(String[] args){
		ajustar();
		//ajustar_alteracoes();
		ajustar_haver_detalhe();
	}
	
	private static void ajustar_haver_detalhe(){
		emf = Persistence.createEntityManagerFactory("OpusERP4");
		em = emf.createEntityManager();
		
		Query q = em.createQuery("select h from Haver h", Haver.class);
		
		em.getTransaction().begin();
		for (Haver haver : (List<Haver>)q.getResultList()) {
			Query qHaverCab = em.createQuery("select hc from HaverCab hc where hc.cliente =:cliente", HaverCab.class);
			qHaverCab.setParameter("cliente", haver.getCliente());
			
			HaverCab havercab = (HaverCab)qHaverCab.getSingleResult();
			
			HaverDetalhe haver_entrada = new HaverDetalhe(null, haver.getHaver_cab(), "ENTRADA", haver.getValor(), haver.getDoc(),haver.getnDoc(), haver.getReferente(),"", haver.getData_emissao(), haver.getUsuario(),"ATIVO");
			em.persist(haver_entrada); 
			
			Query qAlteracoes = em.createQuery("select ah from AlteracoesHaver ah where ah.haver=:haver and ah.tipo != 'ABERTO'", AlteracoesHaver.class);
			qAlteracoes.setParameter("haver", haver);
			
			for (AlteracoesHaver ah : (List<AlteracoesHaver>)qAlteracoes.getResultList()) {
				HaverDetalhe haver_saida = new HaverDetalhe(null, haver.getHaver_cab(), "SAIDA", ah.getValor_utilizado(), haver.getDoc(),haver.getnDoc(), haver.getReferente(),ah.getTipo(), haver.getData_emissao(), ah.getOperador().getUsername(),"ATIVO");
				em.persist(haver_saida); 
			}
		}
		em.getTransaction().commit();
		
		System.out.println("TERMINOU");
	}
	
	private static void ajustar_alteracoes(){
		emf = Persistence.createEntityManagerFactory("OpusERP4");
		em = emf.createEntityManager();
		
		Query q = em.createQuery("select a from AlteracoesHaver a", AlteracoesHaver.class);
		
		em.getTransaction().begin();
		for (AlteracoesHaver alteracao_haver : (List<AlteracoesHaver>)q.getResultList()) {
			
			String tipo = alteracao_haver.getTipo().contains("ABERTO") ? "ENTRADA" : "SAIDA";
			
			Query q1 = em.createQuery("select hc from HaverCab hc where hc.cliente =:cliente", HaverCab.class);
			q1.setParameter("cliente", alteracao_haver.getHaver().getCliente());
			
			if(q1.getResultList().size() ==1){
				HaverCab haver_cab = (HaverCab)q1.getSingleResult();
				AlteracoesHaverCab alteracao = new AlteracoesHaverCab(null, alteracao_haver.getData_alteracao(), haver_cab, tipo, alteracao_haver.getTipo(), alteracao_haver.getValor_haver(), 
					alteracao_haver.getValor_saldo(), alteracao_haver.getValor_utilizado(), alteracao_haver.getOperador().getId());
				em.persist(alteracao); 
			}
		}
		em.getTransaction().commit();
	}
	
	
	private static void ajustar(){
		emf = Persistence.createEntityManagerFactory("OpusERP4");
		em = emf.createEntityManager();
		
		Query q = em.createQuery("select h from Haver h", Haver.class);
		
		em.getTransaction().begin();
		for (Haver haver : (List<Haver>)q.getResultList()) {
			Query qHaverCab = em.createQuery("select hc from HaverCab hc where hc.cliente =:cliente", HaverCab.class);
			qHaverCab.setParameter("cliente", haver.getCliente());
			
			if(qHaverCab.getResultList().size() == 0){
				HaverCab haverCab = new HaverCab(null, haver.getCliente(),new Date(), haver.getValor_disponivel() > 0 ? "DISPONIVEL" : "UTILIZADO");
				em.persist(haverCab);
				
				haver.setHaver_cab(haverCab); 
			}
			
			if(qHaverCab.getResultList().size() == 1){
				HaverCab havercab = (HaverCab)qHaverCab.getSingleResult();
				
//				double valor_total = havercab.getValor_total() + haver.getValor_disponivel();
//				
//				havercab.setValor_total(valor_total);
//				if(havercab.getValor_total()>0){
//					havercab.setStatus("DISPONIVEL");
//				}else{
//					havercab.setStatus("UTILIZADO");
//				}
				em.merge(havercab);
				
				haver.setHaver_cab(havercab); 
			}
			
			em.merge(haver);
		}
		em.getTransaction().commit();
	}
}

package com.digital.opuserp.test;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import com.digital.opuserp.domain.AcessoCliente;
import com.digital.opuserp.domain.AlteracoesMateriais;
import com.digital.opuserp.domain.AlteracoesContrato;

public class LogAlteracaoMaterialScript {

	static EntityManager em;
	static EntityManagerFactory emf;
	public static void main(String args[]){
		adicionarLogVinculo();
	}
	public static void adicionarLogVinculo(){
		emf = Persistence.createEntityManagerFactory("OpusERP4");
		em = emf.createEntityManager();		
		
		Query q = em.createQuery("select a from AcessoCliente a where a.status_2 != 'PENDENTE_INSTALACAO'",AcessoCliente.class);
		List<AcessoCliente> list = q.getResultList();
		
		em.getTransaction().begin();
		for (AcessoCliente acessoCliente : list) {
						
			Query qLog = em.createQuery("select am from AlterarcoesContrato am where am.contrato=:contrato and am.tipo = 'LIBERAÇÃO DE ACESSO' order by am.data_alteracao desc", AlteracoesContrato.class);
			qLog.setParameter("contrato", acessoCliente);
			
			if(qLog.getResultList().size() > 0){
				AlteracoesContrato logAlte = (AlteracoesContrato)qLog.getResultList().get(0);				
				//em.persist(new AlteracoesMateriais(null, acessoCliente, acessoCliente.getRegime(), acessoCliente.getEndereco_mac(), "VINCULADO", logAlte.getData_alteracao()));					
				
				System.out.println("CONTRATO :"+acessoCliente.getId()+" VINCULADO NA DATA :"+logAlte.getData_alteracao());
				System.out.println("===================================================");
			}else{
				if(acessoCliente.getData_instalacao() != null){
				//	em.persist(new AlteracoesMateriais(null, acessoCliente, acessoCliente.getRegime(), acessoCliente.getEndereco_mac(), "VINCULADO", acessoCliente.getData_instalacao()));
					
					System.out.println("CONTRATO :"+acessoCliente.getId()+" VINCULADO NA DATA :"+acessoCliente.getData_instalacao());
					System.out.println("===================================================");
				}else{
					System.out.println("CONTRATO :"+acessoCliente.getId()+" SEM DATA ");
					System.out.println("===================================================");
				}
			}			
		}
		em.getTransaction().commit();
		System.out.println("TERMINADO");

	}
//	public static void adicionarLogEstornoOuRemocao(){
//		
//		emf = Persistence.createEntityManagerFactory("OpusERP4");
//		em = emf.createEntityManager();		
//		
//		Query q = em.createQuery("select a from AcessoCliente a where a.status_2 = 'ENCERRADO'",AcessoCliente.class);
//		List<AcessoCliente> list = q.getResultList();
//		
//		em.getTransaction().begin();
//		for (AcessoCliente acessoCliente : list) {
//			Query qLog = em.createQuery("select am from AlterarcoesContrato am where am.contrato=:contrato and am.tipo = 'ENCERRAMENTO CONTRATO' order by am.data_alteracao desc", AlterarcoesContrato.class);
//			qLog.setParameter("contrato", acessoCliente);
//			
//			if(qLog.getResultList().size() > 0){
//				AlterarcoesContrato logAlte = (AlterarcoesContrato)qLog.getResultList().get(0);
//				
//				if(acessoCliente.getRegime().equals("COMODATO")){
//				//	em.persist(new AlteracoesMateriais(null, acessoCliente, acessoCliente.getRegime(), acessoCliente.getEndereco_mac(), "ESTORNADO", logAlte.getData_alteracao()));
//				}else{
//				//	em.persist(new AlteracoesMateriais(null, acessoCliente, acessoCliente.getRegime(), acessoCliente.getEndereco_mac(), "REMOVIDO", logAlte.getData_alteracao()));
//				}
//				
//				System.out.println("CONTRATO :"+acessoCliente.getId()+" ENCERRADO NA DATA :"+logAlte.getData_alteracao());
//				System.out.println("===================================================");
//			}else{
//				System.out.println("CONTRATO :"+acessoCliente.getId()+" DATA DE ENCERRAMENTO NAO ENCONTRADA");
//			}
//		}
//		em.getTransaction().commit();
//	}
}

package com.digital.opuserp.test;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.ContasReceberDAO;
import com.digital.opuserp.domain.ContaBancaria;
import com.digital.opuserp.domain.ContasReceber;
import com.digital.opuserp.domain.ControleTitulo;
import com.digital.opuserp.util.ConnUtil;

public class AtualizarBoletosSincred {

	static EntityManager em;
	static EntityManagerFactory emf;
	public static void main(String[] args) {		
		System.out.println("teste");
		start();
	}
	
	private static void start() {
		emf = Persistence.createEntityManagerFactory("OpusERP4");
		em = emf.createEntityManager();		
		
		Query q = em.createNativeQuery("SELECT * FROM  contas_receber WHERE  N_NUMERO_SICRED IS NULL AND  N_NUMERO IS NOT NULL AND  STATUS_2 =  'ABERTO' AND REMESSA_ENVIADA = 'SIM' LIMIT 0 , 100");
		
		for (ContasReceber boleto: (List<ContasReceber>)q.getResultList()) {
			System.out.println(boleto.getId());
		
				
				gerarNossoNumero(boleto.getId());
				
				
		
		}
	}
	
	public static boolean gerarNossoNumero(Integer codBoleto){
		EntityManager em = ConnUtil.getEntity();
		try{
			
			ContasReceber boleto = em.find(ContasReceber.class, codBoleto);
			if(boleto != null){
				if(boleto.getN_numero_sicred() == null || boleto.getN_numero_sicred().isEmpty() || boleto.getN_numero_sicred().equals("")){
					
					Query qControleNovo = em.createQuery("select c from ControleTitulo c where c.nome=:nome and c.empresa_id =:empresa", ControleTitulo.class);
					qControleNovo.setParameter("nome", boleto.getControle());
					qControleNovo.setParameter("empresa", OpusERP4UI.getEmpresa().getId());
					
					ContaBancaria cb = null;
					if(qControleNovo.getResultList().size() == 1){
						
						ControleTitulo ct = (ControleTitulo)qControleNovo.getSingleResult();
						cb = ct.getConta_bancaria_bkp();
					}
					
					String NumeroNovo = "";
					if(cb != null){
						if(cb.getNome_banco().equals("SICRED")){					
							NumeroNovo = ContasReceberDAO.calcularNossoNumeroSicred(boleto.getCliente().getId(),cb);				
						}
						
						if(cb.getNome_banco().equals("BANCO DO BRASIL")){
							//NumeroNovo = calcularNossoNumero(boleto.getCliente().getId());
						}
					}
					
					boleto.setRemessaEnviada(null);
					boleto.setN_numero_sicred(NumeroNovo);
					em.getTransaction().begin();
					em.merge(boleto);
					em.getTransaction().commit();
					
					return true;
				}
				
				return false ;
			}
			
			
			return false ;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
}

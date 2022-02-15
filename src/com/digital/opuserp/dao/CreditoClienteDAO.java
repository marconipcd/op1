package com.digital.opuserp.dao;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.domain.CreditoCliente;
import com.digital.opuserp.util.ConnUtil;

public class CreditoClienteDAO {
	
	EntityManager em;
	
	
	public CreditoClienteDAO(){
		em = ConnUtil.getEntity();
	}
	
	public static String getSaldo(Integer codCliente){
		try {
			EntityManager em = ConnUtil.getEntity();
			Query q = em.createNativeQuery("SELECT * FROM credito_cliente WHERE CLIENTES_ID = "+codCliente, CreditoCliente.class);	
			
			if(q.getResultList().size() == 1){
				CreditoCliente credito = (CreditoCliente) q.getSingleResult();
				return credito.getSaldo();
			}else{
				return null;
			}						
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	public String getLimiteCredito(Integer codCliente){
		try {
				
			Query q = em.createNativeQuery("SELECT * FROM credito_cliente WHERE CLIENTES_ID = "+codCliente);	
			
			Object resultPrime = q.getSingleResult();
			Object[] obPrime = (Object[]) resultPrime;
			String limite = obPrime[3].toString();
			return limite;
		} catch (Exception e) {
			System.out.println("Erro ao buscar o saldo: "+e.getMessage()+".\n Causado por: "+e.getCause());
			return null;
		}
		
	}
	
	public static void setSaldo(Integer codCliente, Double valor){
		
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createNativeQuery("SELECT * FROM credito_cliente WHERE CLIENTES_ID = "+codCliente, CreditoCliente.class);	
		
		if(q.getResultList().size() == 1){
			CreditoCliente credito = (CreditoCliente) q.getSingleResult();
			
			//em.getTransaction().begin();
			
			Double saldo = Double.parseDouble(credito.getSaldo());
			Double novoSaldo = saldo - valor;
			credito.setSaldo(novoSaldo.toString());
			
			em.merge(credito);
			
			//em.getTransaction().commit();
		}
	}
	public static void devolverSaldo(Integer codCliente, Double valor){
		
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createNativeQuery("SELECT * FROM credito_cliente WHERE CLIENTES_ID = "+codCliente, CreditoCliente.class);	
		
		if(q.getResultList().size() == 1){
			CreditoCliente credito = (CreditoCliente) q.getSingleResult();
			
			//em.getTransaction().begin();
			
			Double saldo = Double.parseDouble(credito.getSaldo());
			Double novoSaldo = saldo + valor;
			credito.setSaldo(novoSaldo.toString());
			
			em.merge(credito);
			
			//em.getTransaction().commit();
		}
	}
	
	public static void atualizarSaldo(Integer codCliente, Double valorNovo, Double valorAntigo){
		
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createNativeQuery("SELECT * FROM credito_cliente WHERE CLIENTES_ID = "+codCliente, CreditoCliente.class);	
		
		if(q.getResultList().size() == 1){
			CreditoCliente credito = (CreditoCliente) q.getSingleResult();
			
			Double saldo = Double.parseDouble(credito.getSaldo());

			Double valor = null;
			if(valorAntigo < valorNovo){				
				valor = valorNovo - valorAntigo;				
				Double novoSaldo = saldo - valor;
				credito.setSaldo(novoSaldo.toString());
				
			}else if(valorAntigo > valorNovo){
				valor = valorAntigo - valorNovo;				
				Double novoSaldo = saldo + valor;
				credito.setSaldo(novoSaldo.toString());
			}
			
			em.merge(credito);
			
			//em.getTransaction().commit();
		}
	}
	
	public void setSaldoEditor(Integer codCliente, Double valor){		
		try {
			
			Query q = em.createNativeQuery("SELECT * FROM credito_cliente WHERE CLIENTES_ID = "+codCliente, CreditoCliente.class);	
			
			if(q.getResultList().size() == 1){
				CreditoCliente credito = (CreditoCliente) q.getSingleResult();
				
				//em.getTransaction().begin();
				
				Double saldo = Double.parseDouble(credito.getSaldo());
				Double novoSaldo = saldo + valor;
				credito.setSaldo(novoSaldo.toString());
				
				em.merge(credito);
				
				//em.getTransaction().commit();
			}	
		} catch (Exception e) {
			System.out.println("Erro ao Extornar o saldo: "+e.getMessage()+".\n Causado por: "+e.getCause());
		}
	}
	
	
}

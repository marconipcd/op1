package com.digital.opuserp.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.AcessoCliente;
import com.digital.opuserp.domain.ArquivosContrato;
import com.digital.opuserp.domain.ArquivosContrato2;
import com.digital.opuserp.util.ConnUtil;

public class ArquivosContratoDAO {
	
	public static boolean excluir(ArquivosContrato ar){
		EntityManager em = ConnUtil.getEntity();
		
		try {
			
			//listarArquivos(contrato)
			
			em.getTransaction().begin();
			em.remove(ar);
			em.getTransaction().commit();
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static ArquivosContrato buscarArquivo(AcessoCliente contrato){
		EntityManager em = ConnUtil.getEntity();

		try{
			Query q = em.createQuery("select a from ArquivosContrato a where a.contrato=:contrato", ArquivosContrato.class);
			q.setParameter("contrato", contrato);
			
			if(q.getResultList().size() == 1){
				return (ArquivosContrato)q.getSingleResult();
			}
			
			
			return null;
		}catch(Exception e){
			e.printStackTrace();
			
			return null;
		}
	}
	
	public static List<ArquivosContrato2> listarArquivos2(AcessoCliente contrato){
		EntityManager em = ConnUtil.getEntity();

		try{
			Query q = em.createQuery("select a from ArquivosContrato2 a where a.contrato=:contrato", ArquivosContrato2.class);
			q.setParameter("contrato", contrato);
					
			return q.getResultList();
			
			
		}catch(Exception e){
			e.printStackTrace();
			
			return null;
		}
	}
	
	public static List<ArquivosContrato> listarArquivos(AcessoCliente contrato){
		EntityManager em = ConnUtil.getEntity();

		try{
			Query q = em.createQuery("select a from ArquivosContrato a where a.contrato=:contrato", ArquivosContrato.class);
			q.setParameter("contrato", contrato);
					
			return q.getResultList();
			
			
		}catch(Exception e){
			e.printStackTrace();
			
			return null;
		}
	}
	
	
	public static void save(ArquivosContrato fileOsp){
		EntityManager em = ConnUtil.getEntity();
		
		AcessoCliente ac = fileOsp.getContrato();
		ac.setArquivo_upload(new String().valueOf(listarArquivos(fileOsp.getContrato()).size()+1));
		
		try{
			em.getTransaction().begin();
		
			em.merge(ac);
			
			fileOsp.setUsuario(OpusERP4UI.getUsuarioLogadoUI().getUsername());
			em.persist(fileOsp);
			
			em.getTransaction().commit();
		}catch(Exception e){
			e.printStackTrace();
			
		}
	}
	
	public static void save2(ArquivosContrato2 fileOsp){
		EntityManager em = ConnUtil.getEntity();
		
		AcessoCliente ac = fileOsp.getContrato();
		ac.setArquivo_upload(new String().valueOf(listarArquivos2(fileOsp.getContrato()).size()+1));
		
		try{
			em.getTransaction().begin();
		
			//em.merge(ac);
			
			fileOsp.setUsuario(OpusERP4UI.getUsuarioLogadoUI().getUsername());
			em.persist(fileOsp);
			
			em.getTransaction().commit();
		}catch(Exception e){
			e.printStackTrace();
			
		}
	}
}

package com.digital.opuserp.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.AcessoCliente;
import com.digital.opuserp.domain.ArquivosContrato;
import com.digital.opuserp.domain.ArquivosOse;
import com.digital.opuserp.domain.ArquivosOse2;
import com.digital.opuserp.domain.Ose;
import com.digital.opuserp.util.ConnUtil;

public class ArquivosOseDAO {
	public static boolean excluir(ArquivosOse ar){
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

	public static ArquivosOse buscarArquivo(Ose contrato){
		EntityManager em = ConnUtil.getEntity();

		try{
			Query q = em.createQuery("select a from ArquivosOse a where a.ose=:ose", ArquivosOse.class);
			q.setParameter("ose", contrato);
			
			if(q.getResultList().size() == 1){
				return (ArquivosOse)q.getSingleResult();
			}
			
			
			return null;
		}catch(Exception e){
			e.printStackTrace();
			
			return null;
		}
	}
	
	public static boolean excluirArquivos(Ose os){
		
		try{
			
			List<ArquivosOse> arquivos = listarArquivos(os);
			for (ArquivosOse arquivo : arquivos) {
				excluir(arquivo);
			}
			
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public static List<ArquivosOse> listarArquivos(Ose contrato){
		EntityManager em = ConnUtil.getEntity();

		try{
			Query q = em.createQuery("select a from ArquivosOse a where a.ose=:ose", ArquivosOse.class);
			q.setParameter("ose", contrato);
					
			return q.getResultList();
			
			
		}catch(Exception e){
			e.printStackTrace();
			
			return null;
		}
	}
	
	public static List<ArquivosOse2> listarArquivos2(Ose contrato){
		EntityManager em = ConnUtil.getEntity();

		try{
			Query q = em.createQuery("select a from ArquivosOse2 a where a.ose=:ose", ArquivosOse2.class);
			q.setParameter("ose", contrato.getId());
					
			return q.getResultList();
						
		}catch(Exception e){
			e.printStackTrace();
			
			return null;
		}
	}
	
	
	public static void save(ArquivosOse fileOsp){
		EntityManager em = ConnUtil.getEntity();
		
		Ose ac = fileOsp.getOse();
		ac.setArquivo_upload(new String().valueOf(listarArquivos(fileOsp.getOse()).size()+1));
		
		try{
			em.getTransaction().begin();
		
			fileOsp.setUsuario(OpusERP4UI.getUsuarioLogadoUI().getUsername());
			
			em.merge(ac);
			em.persist(fileOsp);
			
			em.getTransaction().commit();
		}catch(Exception e){
			e.printStackTrace();
			
		}
	}
	
	public static void save2(ArquivosOse2 fileOsp){
		EntityManager em = ConnUtil.getEntity();
		
		//	Ose ac = fileOsp.getOse();
		//	ac.setArquivo_upload(new String().valueOf(listarArquivos(fileOsp.getOse()).size()+1));
		
		try{
			em.getTransaction().begin();
		
			fileOsp.setUsuario(OpusERP4UI.getUsuarioLogadoUI().getUsername());
			
			//em.merge(ac);
			em.persist(fileOsp);
			
			em.getTransaction().commit();
		}catch(Exception e){
			e.printStackTrace();
			
		}
	}
}

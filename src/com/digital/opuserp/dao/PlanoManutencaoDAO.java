package com.digital.opuserp.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.domain.ContratoManutencao;
import com.digital.opuserp.domain.PlanosManutencao;
import com.digital.opuserp.util.ConnUtil;

public class PlanoManutencaoDAO {
	
	public static PlanosManutencao find(Integer id){
		try{
			EntityManager em = ConnUtil.getEntity();
			
			
			return em.find(PlanosManutencao.class, id);
					
		}catch(Exception e){
			e.printStackTrace();
			
			return null;
		}
	}

	public static boolean save(PlanosManutencao cm){
		
		try{
			EntityManager em = ConnUtil.getEntity();
			
			em.getTransaction().begin();
			
			if(cm.getId() != null){
				em.merge(cm);
			}else{
				em.persist(cm); 
			}
			em.getTransaction().commit();
			
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		
	}
	
	public static boolean checkUsoPlanoManutencao(PlanosManutencao pm){
		
		EntityManager em = ConnUtil.getEntity();
		
		Query q = em.createQuery("select cm from ContratoManutencao cm where cm.plano_manutencao =:pm", ContratoManutencao.class);
		q.setParameter("pm", pm);
		
		if(q.getResultList().size() == 0){
			return false;
		}
		
		return true;
	}
	
	public static boolean remove(PlanosManutencao pm){
		try{
			EntityManager em = ConnUtil.getEntity();
			
			em.getTransaction().begin();
			em.remove(em.find(PlanosManutencao.class, pm.getId()));
			em.getTransaction().commit();
			
			return true;
		}catch(Exception e){
			e.printStackTrace();
			
			return false;
		}
	}
}

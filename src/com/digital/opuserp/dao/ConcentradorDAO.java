package com.digital.opuserp.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.AcessoCliente;
import com.digital.opuserp.domain.Concentrador;
import com.digital.opuserp.util.ConnUtil;

public class ConcentradorDAO {

	EntityManager em;
	
	public static List<Concentrador> getConcentradores(){
		EntityManager em = ConnUtil.getEntity();
		
		try{
			Query q = em.createQuery("select c from Concentrador c where c.empresa_id =:empresa_id", Concentrador.class);
			q.setParameter("empresa_id", OpusERP4UI.getEmpresa().getId());
			
			return q.getResultList();
		}catch(Exception e){
            return null;
            
        }
	}
	
	public boolean allowRemove(Integer codBase){
		
		EntityManager em = ConnUtil.getEntity();
		
		boolean allow = true;
		
		try{
			Query qAcesso = em.createQuery("select ac from AcessoCliente ac where ac.base = :Base and ac.status_2 != 'ENCERRADO'", AcessoCliente.class);
			qAcesso.setParameter("Base", new Concentrador(codBase));
			qAcesso.setMaxResults(1);
			if(qAcesso.getResultList().size() > 0){
				allow = false;
			}	
			return allow;
		}catch(Exception e){
            return false;
            
        }
	}
}

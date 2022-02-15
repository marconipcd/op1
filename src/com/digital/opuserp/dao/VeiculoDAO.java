package com.digital.opuserp.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.Veiculos;
import com.digital.opuserp.util.ConnUtil;

public class VeiculoDAO {
	
	EntityManager em;

	public VeiculoDAO() {
		em = ConnUtil.getEntity();
		
	}
	
	public boolean allowRemove(Integer codVeiculo, Integer codEmpresa){
		
		boolean allow = true;
		//Verificar se Tem vinculo com OSE 
		Query qAcesso = em.createQuery("select ose from Ose ose where ose.veiculo_id = :Veiculos and ose.empresa_id = :codEmpresa");
		qAcesso.setParameter("Veiculos", codVeiculo);
		qAcesso.setParameter("codEmpresa", codEmpresa);
		if(qAcesso.getResultList().size() > 0){
			allow = false;
		}
		return allow;
	}
	
	public static Veiculos find(Integer codVeiculo){
		EntityManager em = ConnUtil.getEntity();
		return em.find(Veiculos.class, codVeiculo);
	}
	
	public static Veiculos findbyCod(String codVeiculo){
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createQuery("select v from Veiculos v where v.cod_veiculo =:cod", Veiculos.class);
		q.setParameter("cod", codVeiculo);
		
		if(q.getResultList().size() == 1){
			return (Veiculos)q.getSingleResult();
		}
		return null;
	}
	
	
	public static List<Veiculos> getVeiculos(){
		
		EntityManager em = ConnUtil.getEntity();
		
		Query q = em.createQuery("select v from Veiculos v where v.empresa_id=:e", Veiculos.class);
		q.setParameter("e", OpusERP4UI.getEmpresa().getId());
		 
		return q.getResultList();
	}

}

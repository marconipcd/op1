package com.digital.opuserp.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.AlertaPendencia;
import com.digital.opuserp.domain.Crm;
import com.digital.opuserp.domain.Setores;
import com.digital.opuserp.domain.SetoresUsuario;
import com.digital.opuserp.util.ConnUtil;

public class AlertaPendenciaDAO {

	public static boolean allowShowAlert(Setores s){
		
		try{
			List<SetoresUsuario> setorUsuario = SetoresDAO.getSetoresByUser();
			
			for (SetoresUsuario setoresUsuario : setorUsuario) {
				if(s.equals(SetoresDAO.find(setoresUsuario.getSetor_id())) || s.getSetor_root().equals(SetoresDAO.find(setoresUsuario.getSetor_id()))){
					return true;
				}
			}
			
			return false;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public static Integer getPendenciasCrm(Integer codSubmodulo){
		EntityManager em = ConnUtil.getEntity();;
		try{
			Query q = em.createQuery("select a from AlertaPendencia a where a.empresa=:empresa and a.submodulo_id=:submodulo and a.status='ATIVO'", AlertaPendencia.class);
			q.setParameter("empresa", OpusERP4UI.getEmpresa());
			q.setParameter("submodulo", codSubmodulo);
			
			List<AlertaPendencia> listPendencia = q.getResultList();
			List<AlertaPendencia> listAllow = new ArrayList<>();
			
			for (AlertaPendencia ap : listPendencia) {
				Crm crm = CrmDAO.find(ap.getCodigo_tabela());				
				if(ap.getCodigo_tabela() != null && crm != null && allowShowAlert(crm.getSetor())){
					listAllow.add(ap);
				}
			}
					
			return listAllow.size();
		}catch(Exception e){
			e.printStackTrace();
			return 0;
		}
	}
	
	public static Integer getPendencias(Integer codSubmodulo){
		
		EntityManager em = ConnUtil.getEntity();;
		
		try{
			Query q = em.createQuery("select a from AlertaPendencia a where a.empresa=:empresa and a.submodulo_id=:submodulo and a.status='ATIVO'", AlertaPendencia.class);
			q.setParameter("empresa", OpusERP4UI.getEmpresa());
			q.setParameter("submodulo", codSubmodulo);
					
			return q.getResultList().size();
		}catch(Exception e){
			e.printStackTrace();
			return 0;
		}
	}
	
	public static void addPendencia(Integer codSubModulo, Integer codTabela, String descricao){
		EntityManager em = ConnUtil.getEntity();;

		try{
		
		if(codSubModulo > 0 && codTabela > 0){
			Query q = em.createQuery("select a from AlertaPendencia a where a.empresa=:empresa and a.submodulo_id=:submodulo and a.codigo_tabela=:codTabela and a.status='ATIVO'", AlertaPendencia.class);
			q.setParameter("empresa", OpusERP4UI.getEmpresa());
			q.setParameter("submodulo", codSubModulo);
			q.setParameter("codTabela", codTabela);
			
			if(q.getResultList().size() == 0){
				try{
					em.getTransaction().begin();
					em.persist(new AlertaPendencia(null, OpusERP4UI.getEmpresa(), codSubModulo, codTabela, descricao, "ATIVO", new Date()));
					em.getTransaction().commit();
				}catch(Exception e){
					em.getTransaction().rollback();
					e.printStackTrace();
				}
			}
		}
		
		}catch(Exception e){
			e.printStackTrace();
			
		}
	}
	
	public static void removePendencia(Integer codSubModulo, Integer codTabela){
		
		EntityManager em = ConnUtil.getEntity();;
		
		try{
			Query q = em.createQuery("select a from AlertaPendencia a where a.empresa=:empresa and a.submodulo_id=:submodulo and a.codigo_tabela=:codTabela and a.status='ATIVO'", AlertaPendencia.class);
			q.setParameter("empresa", OpusERP4UI.getEmpresa());
			q.setParameter("submodulo", codSubModulo);
			q.setParameter("codTabela", codTabela);
		
		
			em.getTransaction().begin();
			for(AlertaPendencia a : (List<AlertaPendencia>)q.getResultList()){
				em.remove(a);
			}
			em.getTransaction().commit();
			//OpusERP4UI.getRefreshMenu();
		}catch(Exception e){
			em.getTransaction().rollback();
			e.printStackTrace();
		}
	}
}

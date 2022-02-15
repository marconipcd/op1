package com.digital.opuserp.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.SubModulo;
import com.digital.opuserp.domain.SubModuloUsuario;
import com.digital.opuserp.util.ConnUtil;

public class SubModuloDAO {

	
	
	public static List<SubModulo> getSuModuloByModulo(Integer codModulo){
		
		List<SubModulo> submodulos = new ArrayList<>();
		List<SubModuloUsuario> listSubUser = SubModuloDAO.getSubModulosByUsuario();
		
		if(listSubUser != null){
			for (SubModuloUsuario subUser : listSubUser) {
				if(subUser.getSubmodulo().getModulo_id().equals(codModulo)){
					submodulos.add(subUser.getSubmodulo());				
				}
			}
		}
		
		return submodulos;
	}
	
	public static Integer findToId(String nome){
		EntityManager em = ConnUtil.getEntity();
		try{
			Query q = em.createQuery("select s from SubModulo s where s.nome = :nomeSubModulo");			
			q.setParameter("nomeSubModulo", nome);
			
			if(q.getResultList().size() == 1){
				SubModulo sm = (SubModulo)q.getSingleResult();
				return sm.getId();
			}else{
				return 0;
			}
		}catch(Exception e){	
			e.printStackTrace();
			return 0;
		}
	}
	
	public static List<SubModuloUsuario> getSubModulosByUsuario(){
		EntityManager em = ConnUtil.getEntity();
		
		try{
			Query q = em.createQuery("select s from SubModuloUsuario s where s.usuario =:usuario and s.empresa =:empresa  order by s.submodulo.ordem asc", SubModuloUsuario.class);
			q.setParameter("usuario", OpusERP4UI.getUsuarioLogadoUI());
			q.setParameter("empresa", OpusERP4UI.getEmpresa());
			
			return q.getResultList();
		}catch(Exception e){
			return null;
		}
	}
	public static List<SubModuloUsuario> getSubModulosByUsuarioPorModulo(Integer modulo){
		EntityManager em = ConnUtil.getEntity();
		
		try{
			Query q = em.createQuery("select s from SubModuloUsuario s where s.submodulo.modulo_id =:modulo and "
					+ "s.usuario =:usuario and s.empresa =:empresa  order by s.submodulo.ordem asc", SubModuloUsuario.class);
			q.setParameter("usuario", OpusERP4UI.getUsuarioLogadoUI());
			q.setParameter("empresa", OpusERP4UI.getEmpresa());
			q.setParameter("modulo", modulo);
			
			return q.getResultList();
		}catch(Exception e){
			return null;
		}
	}
}

package com.digital.opuserp.dao;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.Empresa;
import com.digital.opuserp.domain.EmpresasUsuario;
import com.digital.opuserp.domain.Modulo;
import com.digital.opuserp.domain.ModulosEmpresa;
import com.digital.opuserp.domain.Usuario;
import com.digital.opuserp.util.ConnUtil;

public class ModuloEmpresaDAO {
	
	EntityManager em;

	public ModuloEmpresaDAO() {
		
		em = ConnUtil.getEntity();
		
	}
	
	public void adicionarRegistro(Integer codEmpresa, Integer codModulo){
		try {
			
			em.getTransaction().begin();
			em.persist(new ModulosEmpresa(null, new Empresa(codEmpresa), new Modulo(codModulo)));
			em.getTransaction().commit();
			
		} catch (Exception e) {
			System.out.println("ERRO :"+e.getMessage());
			System.out.println("ERRO :"+e.getCause());
			System.out.println("ERRO :"+e.getStackTrace());
		}		
	}
	
	public void deletarRegistro(Integer codEmpresa, Integer codModulo){
		try {
			
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ModulosEmpresa> cq = cb.createQuery(ModulosEmpresa.class);
			Root<ModulosEmpresa> root = cq.from(ModulosEmpresa.class);
			cq.where(cb.and(
						 cb.equal(root.get("empresa"),cb.parameter(Empresa.class, "empresa")),
						 cb.equal(root.get("modulo"),cb.parameter(Modulo.class, "modulo"))
						 ));
			
			TypedQuery tq = em.createQuery(cq);
			tq.setParameter("empresa",new Empresa(codEmpresa));
			tq.setParameter("modulo",new Modulo(codModulo));
			
			em.getTransaction().begin();
			em.remove(tq.getSingleResult());
			em.getTransaction().commit();
			
		} catch (Exception e) {
			System.out.println("ERRO :"+e.getMessage());
			System.out.println("ERRO :"+e.getCause());
			System.out.println("ERRO :"+e.getStackTrace());
			
		}	
	}
	
	public static boolean checkModuloEmpresa(Integer codMoudlo){
		EntityManager emr = ConnUtil.getEntity();
		Query q = emr.createQuery("select m from ModulosEmpresa m where m.modulo =:modulo and m.empresa =:empresa", ModulosEmpresa.class);
		q.setParameter("modulo", new Modulo(codMoudlo));
		q.setParameter("empresa", OpusERP4UI.getEmpresa());
		
		if(q.getResultList().size() >0){
			return true;
		}
		
		return false;
	}

}

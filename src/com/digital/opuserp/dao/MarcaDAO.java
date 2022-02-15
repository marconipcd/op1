package com.digital.opuserp.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.EmpresaGrupoProduto;
import com.digital.opuserp.domain.EmpresaMarcas;
import com.digital.opuserp.domain.GrupoProduto;
import com.digital.opuserp.domain.Marca;
import com.digital.opuserp.domain.Produto;
import com.digital.opuserp.util.ConnUtil;

public class MarcaDAO {
	
	public static Marca find(Integer codMarca){
		EntityManager em = ConnUtil.getEntity();
		return em.find(Marca.class, codMarca);
	}

	 public static List<Marca> getMarcas(String s){
	    	EntityManager em = ConnUtil.getEntity();
	    	Query q = em.createNativeQuery("SELECT * FROM  marcas WHERE  NOME LIKE '%"+s.toUpperCase()+"%' ", Marca.class);
	    	
	    	List<Marca> marcas = new ArrayList<>();
	    	for (Marca marca : (List<Marca>)q.getResultList()) {
				Query q2 = em.createQuery("select ef from EmpresaMarcas ef where ef.marca=:f and ef.empresa=:e", EmpresaMarcas.class);
				q2.setParameter("f", marca);
				q2.setParameter("e", OpusERP4UI.getEmpresa());
				
				if(q2.getResultList().size() > 0){
					marcas.add(marca);
				}
			}
	    	
	    	
	    	return marcas;
	}
	
	public static boolean inUse(Integer codMarca){
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createQuery("select p from Produto p where p.marcasId =:marca", Produto.class);
		q.setParameter("marca", em.find(Marca.class,codMarca));
		
		if(q.getResultList().size() > 0){
			return true;
		}
		
		return false;
	}
	
	public static Marca findbyName(String nome){
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createQuery("select f from Marca f where f.nome =:cod and f.empresa=:empresa_id", Marca.class);
		q.setParameter("cod", nome);
		q.setParameter("empresa_id", OpusERP4UI.getEmpresa().getId());
				
		if(q.getResultList().size() == 1){
			return (Marca)q.getSingleResult();
		}
		
		return null;
	}
	
	public static void save(Marca marca){
		EntityManager em = ConnUtil.getEntity();
		
		em.getTransaction().begin();
		if(marca.getId() == null){
			em.persist(marca);
		}else{
			em.merge(marca);
		}
		em.getTransaction().commit();
		
	}
}

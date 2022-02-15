package com.digital.opuserp.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.Empresa;
import com.digital.opuserp.domain.EmpresaFornecedor;
import com.digital.opuserp.domain.EmpresaGrupoProduto;
import com.digital.opuserp.domain.Fornecedor;
import com.digital.opuserp.domain.GrupoProduto;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.view.util.Notify;

public class GrupoProdutoDAO {

	EntityManager em;
	
	public GrupoProdutoDAO() {
		
    em = ConnUtil.getEntity();
    
	} 
	
	 public static List<GrupoProduto> getGrupos(String s){
	    	EntityManager em = ConnUtil.getEntity();
	    	Query q = em.createNativeQuery("SELECT * FROM  grupo_produto WHERE  NOME_GRUPO LIKE '%"+s.toUpperCase()+"%' ", GrupoProduto.class);
	    	
	    	List<GrupoProduto> grupos = new ArrayList<>();
	    	for (GrupoProduto grupo : (List<GrupoProduto>)q.getResultList()) {
				Query q2 = em.createQuery("select ef from EmpresaGrupoProduto ef where ef.grupoProduto=:f and ef.empresa=:e", EmpresaGrupoProduto.class);
				q2.setParameter("f", grupo);
				q2.setParameter("e", OpusERP4UI.getEmpresa());
				
				if(q2.getResultList().size() > 0){
					grupos.add(grupo);
				}
			}
	    	
	    	
	    	return grupos;
	}
	
	
	public static GrupoProduto findbyName(String nome){
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createQuery("select f from GrupoProduto f where f.nome_grupo =:cod and f.empresa_id=:empresa_id", GrupoProduto.class);
		q.setParameter("cod", nome);
		q.setParameter("empresa_id", OpusERP4UI.getEmpresa().getId());
				
		if(q.getResultList().size() == 1){
			return (GrupoProduto)q.getSingleResult();
		}
		
		return null;
	}
	
	public static GrupoProduto find(Integer id){
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createQuery("select f from GrupoProduto f where f.id =:cod", GrupoProduto.class);
		q.setParameter("cod", id);
		//q.setParameter("empresa_id", OpusERP4UI.getEmpresa().getId());
				
		if(q.getResultList().size() == 1){
			return (GrupoProduto)q.getSingleResult();
		}else{
			Notify.Show("ERRO: Arquivo não encontrado", Notify.TYPE_ERROR);
		}
		
		return null;
	}
	
	public static List<GrupoProduto> getAll(){
		EntityManager em = ConnUtil.getEntity();
		
		Query q = em.createQuery("select f from GrupoProduto f where f.empresa_id=:empresa_id", GrupoProduto.class);		
		q.setParameter("empresa_id", OpusERP4UI.getEmpresa().getId());
				
		return q.getResultList();
	}
    
	public static void cadastrarEmpGrpPro(EmpresaGrupoProduto eGrupo){
		EntityManager em = ConnUtil.getEntity();
		
		em.getTransaction().begin();
		em.persist(eGrupo);
		em.getTransaction().commit();
	}
	
	public static void save(GrupoProduto g){
		EntityManager em = ConnUtil.getEntity();
		
		em.getTransaction().begin();
		if(g.getId() == null){
			em.persist(g);
		}else{
			em.merge(g);
		}
		em.getTransaction().commit();
	}
	
	public static void  removeGrupo(GrupoProduto grupo){
		EntityManager em = ConnUtil.getEntity();
		
		Query qEmpresaGrupoProduto = em.createQuery("select egp from EmpresaGrupoProduto epg where epg.grupoProduto=:grupo", EmpresaGrupoProduto.class);
		qEmpresaGrupoProduto.setParameter("grupo", grupo);
		
		em.getTransaction().begin();
		for (EmpresaGrupoProduto epg : (List<EmpresaGrupoProduto>)qEmpresaGrupoProduto.getResultList()) {
			em.remove(epg);			
		}
		
		em.remove(em.find(GrupoProduto.class, grupo));
		
		em.getTransaction().commit();
		
		
	}
}

	



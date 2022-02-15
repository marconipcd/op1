package com.digital.opuserp.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.Empresa;
import com.digital.opuserp.domain.EmpresasUsuario;
import com.digital.opuserp.domain.LogError;
import com.digital.opuserp.domain.Modulo;
import com.digital.opuserp.domain.ModuloUsuario;
import com.digital.opuserp.domain.ModulosEmpresa;
import com.digital.opuserp.domain.Permissoes;
import com.digital.opuserp.domain.Setores;
import com.digital.opuserp.domain.SetoresUsuario;
import com.digital.opuserp.domain.SubModulo;
import com.digital.opuserp.domain.SubModuloUsuario;
import com.digital.opuserp.domain.Usuario;
import com.digital.opuserp.util.ConnUtil;

public class GerenciarModuloDAO {

	EntityManager em;
	public GerenciarModuloDAO(){
		em = ConnUtil.getEntity();
	}
	
	public List<EmpresasUsuario> getEmpresasUsuarios(Usuario usuario){
		
		try{
			Query q = em.createQuery("select eu from EmpresasUsuario eu where eu.usuario = :user");
			q.setParameter("user", usuario);
			return q.getResultList();
		}catch(Exception  e)
		{
			e.printStackTrace();
			return null;
		}finally{
			//em.close();			
		}
		
	}
	
	public List<Setores> getSetores(Integer codEmpresa){
		
		try{
			Query q = em.createQuery("select s from Setores s where s.empresa_id = :codEmpresa");
			q.setParameter("codEmpresa", codEmpresa);
			return q.getResultList();
		}catch(Exception  e)
		{
			e.printStackTrace();
			return null;
		}finally{
			//em.close();			
		}
		
	}
	
	public List<ModulosEmpresa> getModulos(Integer codEmpresa){
		
		try{
			Query q = em.createQuery("select me from ModulosEmpresa me where me.empresa = :empresa");
			q.setParameter("empresa", new Empresa(codEmpresa));
			return q.getResultList();
		}catch(Exception  e)
		{
			e.printStackTrace();
			return null;
		}finally{
			//em.close();			
		}
		
	}
	
	public List<SubModulo> getSubModulos(Integer codModulo){
		
		try{
			Query q = em.createQuery("select s from SubModulo s where s.modulo_id = :codModulo");
			q.setParameter("codModulo", codModulo);
			return q.getResultList();
		}catch(Exception  e)
		{
			e.printStackTrace();
			return null;
		}finally{
			//em.close();			
		}
		
	}
	
	public boolean checkSetoresUsuarioEmpresa(Integer codUsuario, Integer codEmpresa, Integer codSetor){
		try{
			Query q = em.createQuery("select su from SetoresUsuario su where su.usuario_id = :codUsuario and su.empresa_id = :codEmpresa and su.setor_id = :codSetor");
			q.setParameter("codEmpresa", codEmpresa);
			q.setParameter("codUsuario", codUsuario);
			q.setParameter("codSetor", codSetor);
			
			if(q.getResultList().size()>0){
				return true;
			}else{
				return false;
			}
		}catch (Exception e) {
			e.printStackTrace();
			LogErrorDAO.add(new LogError(null, "GerenciarModuloDAO", "checkPermissaoEmpresaSubModuloUsuario", e.getMessage(), 
					OpusERP4UI.getEmpresa(), OpusERP4UI.getUsuarioLogadoUI()));
			return false;
		}
	}
	public void checkSetoresUsuarioEmpresaInsert(Integer codUsuario, Integer codEmpresa, Integer codSetor){
		try{
			em.getTransaction().begin();
			
			if(!checkSetoresUsuarioEmpresa(codUsuario, codEmpresa, codSetor)){				
				em.persist(new SetoresUsuario(null, codEmpresa, codUsuario, codSetor));					
			}else{
				Query q = em.createQuery("select s from SetoresUsuario s where s.empresa_id = :codEmpresa and s.usuario_id = :codUsuario and s.setor_id = :codSetor");
				q.setParameter("codEmpresa", codEmpresa);
				q.setParameter("codUsuario", codUsuario);
				q.setParameter("codSetor", codSetor);				
				//tq.getResultList().size();				
				em.remove(q.getSingleResult());						
			}
			
			em.getTransaction().commit();
			
		}catch (Exception e) {
			e.printStackTrace();
			em.getTransaction().rollback();
		}
	}
	
	public boolean checkModuloUsuarioEmpresa(Integer codUsuario, Integer codEmpresa, Integer codModulo){
		try{
			
			Query q = em.createQuery("select mu from ModuloUsuario mu where mu.usuario = :usuario and mu.empresa = :empresa and mu.modulo = :modulo");
			q.setParameter("empresa", new Empresa(codEmpresa));
			q.setParameter("usuario", new Usuario(codUsuario));
			q.setParameter("modulo", new Modulo(codModulo));
						
			if(q.getResultList().size()>0){				
				return true;
			}else{				
				return false;
			}
		}catch (Exception e) {
			e.printStackTrace();
			LogErrorDAO.add(new LogError(null, "GerenciarModuloDAO", "checkPermissaoEmpresaSubModuloUsuario", e.getMessage(), 
					OpusERP4UI.getEmpresa(), OpusERP4UI.getUsuarioLogadoUI()));
			return false;
		}
	}
	
	public void checkModuloUsuarioEmpresaInsert(Integer codUsuario, Integer codEmpresa, Integer codModulo){
		try{
			em.getTransaction().begin();
			
			if(!checkModuloUsuarioEmpresa(codUsuario, codEmpresa, codModulo)){
				em.persist(new ModuloUsuario(null, new Empresa(codEmpresa), new Modulo(codModulo), new Usuario(codUsuario)));
			}else{
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<ModuloUsuario> cq = cb.createQuery(ModuloUsuario.class);
				Root<ModuloUsuario> root = cq.from(ModuloUsuario.class);
				cq.where(cb.and(
							 cb.equal(root.get("empresa"),cb.parameter(Empresa.class, "codempresa")),
							 cb.equal(root.get("modulo"),cb.parameter(Modulo.class, "codmodulo")),
							 cb.equal(root.get("usuario"),cb.parameter(Usuario.class, "codusuario"))
							 ));
				
				TypedQuery tq = em.createQuery(cq);
				tq.setParameter("codempresa",new Empresa(codEmpresa));
				tq.setParameter("codmodulo",new Modulo(codModulo));
				tq.setParameter("codusuario",new Usuario(codUsuario));
				
				//tq.getResultList().size();				
				em.remove(tq.getSingleResult());						
			}
			
			em.getTransaction().commit();
			
		}catch (Exception e) {
			e.printStackTrace();
			em.getTransaction().rollback();
		}
	}
	
	public void checkSubModuloUsuarioEmpresaInsert(Integer codUsuario, Integer codEmpresa, Integer codSubModulo){
		try{
			em.getTransaction().begin();
			
			if(!checkSubModuloUsuarioEmpresa(codUsuario, codEmpresa, codSubModulo)){
				System.out.println("Código do Usuário:"+codUsuario);
				System.out.println("Código da Empresa:"+codEmpresa);
				System.out.println("Código do SubModulo:"+codSubModulo);
				//em.persist(new SubModuloUsuario(null, new Empresa(codEmpresa), new SubModulo(codSubModulo), new Usuario(codUsuario)));
				em.persist(new SubModuloUsuario(null, em.find(Empresa.class, codEmpresa), 
						em.find(SubModulo.class, codSubModulo), em.find(Usuario.class, codUsuario)));		
			}else{
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<SubModuloUsuario> cq = cb.createQuery(SubModuloUsuario.class);
				Root<SubModuloUsuario> root = cq.from(SubModuloUsuario.class);
				cq.where(cb.and(
							 cb.equal(root.get("empresa"),cb.parameter(Empresa.class, "codempresa")),
							 cb.equal(root.get("submodulo"),cb.parameter(SubModulo.class, "codsubmodulo")),
							 cb.equal(root.get("usuario"),cb.parameter(Usuario.class, "codusuario"))
							 ));
				
				TypedQuery tq = em.createQuery(cq);
				tq.setParameter("codempresa",new Empresa(codEmpresa));
				tq.setParameter("codsubmodulo",new SubModulo(codSubModulo));
				tq.setParameter("codusuario",new Usuario(codUsuario));
				
				//tq.getResultList().size();				
				em.remove(tq.getSingleResult());						
			}
			
			em.getTransaction().commit();
			
		}catch (Exception e) {
			e.printStackTrace();
			em.getTransaction().rollback();
		}
	}
	
	public boolean checkSubModuloUsuarioEmpresa(Integer codUsuario, Integer codEmpresa, Integer codSubModulo){
		try{
						
			Query q = em.createQuery("select smu from SubModuloUsuario smu where smu.usuario = :usuario and smu.empresa = :empresa and smu.submodulo = :submodulo");
			q.setParameter("empresa", new Empresa(codEmpresa));
			q.setParameter("usuario", new Usuario(codUsuario));
			q.setParameter("submodulo", new SubModulo(codSubModulo));
						
			if(q.getResultList().size()>0){				
				return true;
			}else{				
				return false;
			}
		}catch (Exception e) {
			e.printStackTrace();
			LogErrorDAO.add(new LogError(null, "GerenciarModuloDAO", "checkPermissaoEmpresaSubModuloUsuario", e.getMessage(), 
					OpusERP4UI.getEmpresa(), OpusERP4UI.getUsuarioLogadoUI()));
			return false;
		}
	}
	
	
	public boolean checkPermissaoEmpresaSubModuloUsuario(Integer codSubModulo,Integer  codEmpresa,Integer  codUsuario,String  permissao){
		try{
			
			Query q = em.createQuery("select p from Permissoes p where p.submoduloid = :codSubmodulo and p.usuarioid = :codUsuario and p.empresaid = :codEmpresa and p.permissao = :valorpermissao");
			q.setParameter("codSubmodulo", codSubModulo);
			q.setParameter("codUsuario", codUsuario);
			q.setParameter("codEmpresa", codEmpresa);
			q.setParameter("valorpermissao", permissao);
			
			if(q.getResultList().size()>0){				
				return true;
			}else{				
				return false;
			}
		}catch (Exception e) {
			e.printStackTrace();
			LogErrorDAO.add(new LogError(null, "GerenciarModuloDAO", "checkPermissaoEmpresaSubModuloUsuario", e.getMessage(), 
					OpusERP4UI.getEmpresa(), OpusERP4UI.getUsuarioLogadoUI()));
			return false;
		}
	}
	
	
	public void checkPermissaoEmpresaSubModuloUsuarioInsert(Integer codSubModulo,Integer  codEmpresa,Integer  codUsuario,String  permissao){
		try{
			em.getTransaction().begin();
			
			if(!checkPermissaoEmpresaSubModuloUsuario(codSubModulo, codEmpresa, codUsuario, permissao)){
				
				em.persist(new Permissoes(null, codSubModulo, codUsuario, codEmpresa, permissao));
						
			}else{
				Query q = em.createQuery("select p from Permissoes p where p.submoduloid = :codSubmodulo and p.usuarioid = :codUsuario and p.empresaid = :codEmpresa and p.permissao = :valorpermissao");
				q.setParameter("codSubmodulo", codSubModulo);
				q.setParameter("codUsuario", codUsuario);
				q.setParameter("codEmpresa", codEmpresa);
				q.setParameter("valorpermissao", permissao);
				
				//tq.getResultList().size();				
				em.remove(q.getSingleResult());						
			}
			
			em.getTransaction().commit();
			
		}catch (Exception e) {
			e.printStackTrace();
			if(em.getTransaction().isActive()){
				em.getTransaction().rollback();
			}
		}
	}
		
}

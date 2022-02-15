package com.digital.opuserp.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.RadCheck;
import com.digital.opuserp.domain.RadReply;
import com.digital.opuserp.domain.UsuarioConcentradores;
import com.digital.opuserp.util.ConnUtil;

public class UsuarioConcentradorDAO {

	public static boolean cadastrarUsuario(String usuario, String senha, String grupo){
		
		try{
			EntityManager em  = ConnUtil.getEntity();
			
			em.getTransaction().begin();
				
				em.persist(	new UsuarioConcentradores(null, usuario, senha, OpusERP4UI.getUsuarioLogadoUI().getId(), new Date(), grupo));
				em.persist(new RadCheck(null, usuario, "Password", "==", senha));
				em.persist(new RadReply(null,usuario, "Mikrotik-Group", "=", grupo));
				
			em.getTransaction().commit();
			
			return true;
		}catch(Exception e){
			e.printStackTrace();
			
			return false;
		}
	}
	public static boolean editarUsuario(UsuarioConcentradores u){
		try{
			EntityManager em  = ConnUtil.getEntity();
			
			UsuarioConcentradores usuario = em.find(UsuarioConcentradores.class, u.getId());
			if(usuario != null){
				Query q1 = em.createQuery("select r from RadCheck r where r.username =:u",RadCheck.class);
				q1.setParameter("u", u.getUsuario());
				
				Query q2 = em.createQuery("select r from RadReply r where r.username =:u",RadReply.class);
				q2.setParameter("u", u.getUsuario());
				
				if(q1.getResultList().size() == 1 && q2.getResultList().size() == 1){
					RadCheck r = (RadCheck) q1.getSingleResult();
					r.setUsername(usuario.getUsuario());
					
					RadReply r2 = (RadReply) q2.getSingleResult();
					r2.setUsername(usuario.getUsuario());
					
					em.getTransaction().begin();
						em.merge(r);
						em.merge(r2);
						em.merge(usuario);
					em.getTransaction().commit();
				}else{
					
					List<RadCheck> listRadChecks = q1.getResultList();
					List<RadReply> listRadReply = q2.getResultList();
					
					em.getTransaction().begin();
					
						for (RadReply radReply : listRadReply) {
							em.remove(radReply);
						}
						
						for (RadCheck radCheck : listRadChecks) {
							em.remove(radCheck);
						}
						
						em.persist(new RadCheck(null, usuario.getUsuario(), "Password", "==", usuario.getSenha()));
						em.persist(new RadReply(null,usuario.getUsuario(), "Mikrotik-Group", "=", usuario.getGrupo()));
						em.merge(usuario);
					
					em.getTransaction().commit();
				}
			}
			
			
			
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean buscarUsuarioRadius(String usuario){
		try{
			EntityManager em = ConnUtil.getEntity();
			
			Query q1 = em.createQuery("select r from RadCheck r where r.username =:u",RadCheck.class);
			q1.setParameter("u", usuario);
			
			Query q2 = em.createQuery("select r from RadReply r where r.username =:u",RadReply.class);
			q2.setParameter("u", usuario);
			
			if(q1.getResultList().size() > 0 || q2.getResultList().size() > 0){
				return true;
			}
			
			return false;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean excluirUsuario(UsuarioConcentradores usuario){
		try{
			EntityManager em = ConnUtil.getEntity();
			
			Query q1 = em.createQuery("select r from RadCheck r where r.username =:u",RadCheck.class);
			q1.setParameter("u", usuario.getUsuario());
			
			Query q2 = em.createQuery("select r from RadReply r where r.username =:u",RadReply.class);
			q2.setParameter("u", usuario.getUsuario());
			
			List<RadCheck> listRadChecks = q1.getResultList();
			List<RadReply> listRadReply = q2.getResultList();
			
			em.getTransaction().begin();
			
			
				for (RadReply radReply : listRadReply) {
					em.remove(radReply);
				}
				
				for (RadCheck radCheck : listRadChecks) {
					em.remove(radCheck);
				}
			
				em.remove(em.find(UsuarioConcentradores.class, usuario.getId()));
			em.getTransaction().commit();
			
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
}

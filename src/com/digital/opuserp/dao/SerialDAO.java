package com.digital.opuserp.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.AlteracoesSerial;
import com.digital.opuserp.domain.Produto;
import com.digital.opuserp.domain.SerialProduto;
import com.digital.opuserp.util.ConnUtil;
import com.vaadin.ui.Notification;

public class SerialDAO {

	
	static EntityManager em;
	public SerialDAO(){
		em = ConnUtil.getEntity();
	}
	
	public static boolean checkSerialExist(String serial){
		EntityManager em = ConnUtil.getEntity();
		
		Query q = em.createQuery("select s from SerialProduto s where s.serial =:serial ", SerialProduto.class);
		q.setParameter("serial", serial);
		//q.setParameter("empresa", OpusERP4UI.getEmpresa());
		
		if(q.getResultList().size()>0){
			return true;
		}
		
		return false;
		
	}
	
	public static void changeStatus(SerialProduto s, String status){
		
		EntityManager em = ConnUtil.getEntity();
		
		s.setStatus(status); 
		em.merge(s);	
	}
	
	public void changeStatusInative(Integer cod){
		try{
			if(cod != null && cod >0){
			
				SerialProduto serial = em.find(SerialProduto.class, cod);
				if(serial != null){
					serial.setStatus("COMODATO");
					
					em.getTransaction().begin();
					em.merge(serial);
					em.getTransaction().commit();
				}
			
			}
		
		}catch(Exception e){
			e.printStackTrace();
			
			Notification.show("Houve Algum Problema ao Baixar o Código Serial: "+cod);
		}
	}
	
	public void changeStatusActive(Integer cod){
		try{
			if(cod != null && cod >0){
			
				SerialProduto serial = em.find(SerialProduto.class, cod);
				if(serial != null){
					serial.setStatus("ATIVO");
					
					em.getTransaction().begin();
					em.merge(serial);
					em.getTransaction().commit();
				}
			
			}
		
		}catch(Exception e){
			e.printStackTrace();
			
			Notification.show("Houve Algum Problema ao Baixar o Código Serial: "+cod);
		}
	}
	
	public static SerialProduto addSerial(Integer codProduto, String serial){
		EntityManager em = ConnUtil.getEntity();
		
		try{
			em.getTransaction().begin();
			SerialProduto sp = new SerialProduto(null, new Produto(codProduto), serial, "ATIVO", new Date());
			em.persist(sp);
			em.getTransaction().commit();
			
			return sp;
		}catch(Exception e){
			if(em.getTransaction().isActive()){
				em.getTransaction().rollback();
			}
			e.printStackTrace();
			return null;
		}
	}
		
	public static SerialProduto addNovoSerial(Integer codAcesso, Integer codProduto, String serial){
		EntityManager em = ConnUtil.getEntity();
		
		try{
			em.getTransaction().begin();
			SerialProduto sp = new SerialProduto(null, new Produto(codProduto), serial, "ATIVO", new Date());
			sp.setData_alteracao(new Date());
			sp.setTipo_serial("MAC");
			em.persist(sp);
			em.getTransaction().commit();

			AlteracoesSerialDAO.save(new AlteracoesSerial(null, "ACESSO "+codAcesso+" ESTORNO", sp,OpusERP4UI.getEmpresa(), OpusERP4UI.getUsuarioLogadoUI(), new Date()));

			return sp;
		}catch(Exception e){
			if(em.getTransaction().isActive()){
				em.getTransaction().rollback();
			}
			e.printStackTrace();
			return null;
		}
	}
	public static SerialProduto addNovoSerialSemTrasaction(Integer codAcesso, Integer codProduto, String serial){
		EntityManager em = ConnUtil.getEntity();
		
		try{

			SerialProduto sp = new SerialProduto(null, new Produto(codProduto), serial, "ATIVO", new Date());
			sp.setData_alteracao(new Date());
			sp.setTipo_serial("MAC");
			em.persist(sp);

			AlteracoesSerialDAO.saveNovo(new AlteracoesSerial(null, "ACESSO "+codAcesso+" ESTORNO", sp,OpusERP4UI.getEmpresa(), OpusERP4UI.getUsuarioLogadoUI(), new Date()));

			return sp;
		}catch(Exception e){

			e.printStackTrace();
			return null;
		}
	}
	
	public static SerialProduto addNovo(Integer codProduto, String serial){
		EntityManager em = ConnUtil.getEntity();
		
		try{
//			em.getTransaction().begin();
			SerialProduto sp = new SerialProduto(null, new Produto(codProduto), serial, "ATIVO", new Date());
			sp.setData_alteracao(new Date());
			sp.setTipo_serial("MAC");
			em.persist(sp);
//			em.getTransaction().commit();
			
			AlteracoesSerialDAO.saveNovo(new AlteracoesSerial(null, "CADASTROU", sp,OpusERP4UI.getEmpresa(), OpusERP4UI.getUsuarioLogadoUI(), new Date()));
			
			return sp;
		}catch(Exception e){
			if(em.getTransaction().isActive()){
				em.getTransaction().rollback();
			}
			e.printStackTrace();
			return null;
		}
	}
	
	public static SerialProduto findByNameAndCod(Produto produto, String serial){
		
		EntityManager em = ConnUtil.getEntity();		
		Query q = em.createQuery("select s from SerialProduto s where s.produto = :codProduto and s.serial LIKE:nomeSerial", SerialProduto.class);
		q.setParameter("codProduto", produto);
		q.setParameter("nomeSerial", serial);
		
		if(q.getResultList().size() == 1){
			return (SerialProduto) q.getSingleResult();
		}else{
			return null;
		}
		
	}
	
	
	
	public static List<SerialProduto> findByCodProd(Integer codProduto){
		EntityManager em = ConnUtil.getEntity();
		em = ConnUtil.getEntity();
		Query q = em.createQuery("select s from SerialProduto s where s.produto = :codProduto", SerialProduto.class);
		q.setParameter("codProduto", new Produto(codProduto));
		List<SerialProduto> serialProd = null;
		if(q.getResultList().size() > 0){
			serialProd = q.getResultList();
		}
		return serialProd;
	}
	
	public static List<SerialProduto> findActiveByCodProd(Integer codProduto){
		EntityManager em = ConnUtil.getEntity();
		if(codProduto != null){
			em = ConnUtil.getEntity();
			Query q = em.createQuery("select s from SerialProduto s where s.produto = :codProduto and s.status =:status", SerialProduto.class);
			q.setParameter("codProduto", new Produto(codProduto));
			q.setParameter("status", "ATIVO");
			List<SerialProduto> serialProd = null;
			if(q.getResultList().size() > 0){
				serialProd = q.getResultList();
			}
			return serialProd;
		}
		
		return null;
	}
	
	public static SerialProduto find(Integer codSerial){
		EntityManager em = ConnUtil.getEntity();
		em = ConnUtil.getEntity();
		Query q = em.createQuery("select s from SerialProduto s where s.id = :codSerial", SerialProduto.class);
		q.setParameter("codSerial", codSerial);
		SerialProduto serialProd = null;
		if(q.getResultList().size() > 0){
			serialProd = (SerialProduto) q.getSingleResult();
		}
		return serialProd;
	}
	
	
	public static void atualizaeSerial(SerialProduto serial){
		
		EntityManager em = ConnUtil.getEntity();
		try{
			if(serial != null){			
				em.merge(serial);			
			}		
		}catch(Exception e){
			e.printStackTrace();			
			Notification.show("Houve Algum Problema ao Atualizar p Serial");
		}
	}	

	public void atualizarNovoSerial(SerialProduto serial){
		try{
			if(serial != null){			
//				em.getTransaction().begin();
				em.merge(serial);
//				em.getTransaction().commit();
			}		
		}catch(Exception e){
			e.printStackTrace();			
			Notification.show("Houve Algum Problema ao Atualizar p Serial");
		}
	}	
	
}

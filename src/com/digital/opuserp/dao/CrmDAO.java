package com.digital.opuserp.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.joda.time.DateTime;
import org.joda.time.Seconds;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.Crm;
import com.digital.opuserp.domain.CrmAssunto;
import com.digital.opuserp.domain.CrmFormasContato;
import com.digital.opuserp.domain.Ose;
import com.digital.opuserp.domain.Setores;
import com.digital.opuserp.util.ConnUtil;

public class CrmDAO {

	
	public static boolean registrarAtendimento(Crm crm){
		
		try{
			EntityManager em = ConnUtil.getEntity();
			
			crm.setStatus("EFETUADO");
			crm.setData_efetuado(new Date());
			crm.setOperador(OpusERP4UI.getUsuarioLogadoUI().getUsername());
			crm.setOperador_tratamento(OpusERP4UI.getUsuarioLogadoUI().getUsername());
			
			em.getTransaction().begin();		
			em.persist(crm); 		
			em.getTransaction().commit();
			
			return true;
		}catch(Exception e){
			e.printStackTrace();
			
			return false;
		}
		
	}
	
	public static List<Crm> getCrmByOse(Ose ose){
		
		EntityManager em = ConnUtil.getEntity();
		
		try{
			Query q = em.createQuery("select crm from Crm crm where crm.empresa_id=:empresa and crm.ose=:ose");
			q.setParameter("empresa", OpusERP4UI.getEmpresa().getId());
			q.setParameter("ose", ose);
			
			return q.getResultList();
			
		}catch(Exception e){
			return null;
		}
	}
	
	public static List<Crm> getCrmsPendentes(){
		EntityManager em = ConnUtil.getEntity();
		
		try{
			Query q = em.createQuery("select crm from Crm crm where crm.empresa_id=:empresa and crm.data_agendado<=:data and crm.status!='EFETUADO'", Crm.class);
			q.setParameter("empresa", OpusERP4UI.getEmpresa().getId());
			q.setParameter("data",new Date());
			
			return q.getResultList();
		}catch(Exception e){
			return null;
		}
	}
	
	
	public static boolean checkExists(Setores setor,Cliente cliente,CrmAssunto assunto,CrmFormasContato formaContato,String origem){
		
		EntityManager em = ConnUtil.getEntity();
		
		try{
		Query q = em.createQuery("select crm from Crm crm where crm.empresa_id=:empresa and crm.setor=:setor and crm.cliente=:cliente and crm.crm_assuntos=:assunto and crm.crm_formas_contato=:formacontato and crm.origem=:origem", Crm.class);
		q.setParameter("empresa", OpusERP4UI.getEmpresa().getId());
		q.setParameter("setor", setor);
		q.setParameter("cliente", cliente);
		q.setParameter("assunto", assunto);
		q.setParameter("formacontato", formaContato);
		q.setParameter("origem", origem);
		
		if(q.getResultList().size() > 0){
			return true;
		}
		
		return false;
		}catch(Exception e){
			return false;
		}
	}
	
	
	public static CrmFormasContato getFormaDefault(){
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createQuery("select  cr from CrmFormasContato cr where cr.empresa_id=:empresa_cod and cr.nome='TELEFONE'",CrmFormasContato.class);
		q.setParameter("empresa_cod", OpusERP4UI.getEmpresa().getId());
				
		if(q.getResultList().size() == 1){
			return (CrmFormasContato)q.getSingleResult();
		}
		
		return null;
	}
	
	public static CrmAssunto getAssuntofault(){
		EntityManager em = ConnUtil.getEntity();
		
		try{
		Query q = em.createQuery("select  cr from CrmAssunto cr where cr.empresa_id=:empresa_cod and cr.nome='FELIZ ANIVERSARIO'",CrmAssunto.class);
		q.setParameter("empresa_cod", OpusERP4UI.getEmpresa().getId());
				
		if(q.getResultList().size() == 1){
			return (CrmAssunto)q.getSingleResult();
		}
		
		return null;
		}catch(Exception e){
			return null;
		}
	}
	
	public static Setores getSetorDefault(){
		EntityManager em = ConnUtil.getEntity();
		
		try{
		Query q = em.createQuery("select  cr from Setores cr where cr.empresa_id=:empresa_cod and cr.nome='COMERCIAL'",Setores.class);
		q.setParameter("empresa_cod", OpusERP4UI.getEmpresa().getId());
				
		if(q.getResultList().size() == 1){
			return (Setores)q.getSingleResult();
		}
		
		return null;
		
		}catch(Exception e){
			return null;
		}
	}
	
	
	public static void checkIteracao(Integer codCrm){
		EntityManager em = ConnUtil.getEntity();
		
		try{
		Crm crm = find(codCrm);
		
		
		if(crm != null && crm.getLast_iteracao() != null){
			DateTime data1 = new DateTime(crm.getLast_iteracao());
			DateTime data2 = new DateTime();
				
			
			if(Seconds.secondsBetween(data1, data2).getSeconds() > 15 && crm.getStatus().equals("EM TRATAMENTO")){
				crm.setLast_iteracao(null);
				crm.setStatus("AGENDADO");
				em.getTransaction().begin();
				em.merge(crm);
				em.getTransaction().commit();
			}
		}else{
			if(crm != null && crm.getStatus().equals("EM TRATAMENTO")){
				crm.setStatus("AGENDADO");
				em.getTransaction().begin();
				em.merge(crm);
				em.getTransaction().commit();
			}
		}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void registrarIteracao(Integer codCrm){
		EntityManager em = ConnUtil.getEntity();
		
		try{
		Crm crm = find(codCrm);
		
		if(crm != null){
			crm.setLast_iteracao(new Date());
			crm.setStatus("EM TRATAMENTO");
			em.getTransaction().begin();
			em.merge(crm);
			em.getTransaction().commit();
		}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static Crm find(Integer cod){
		EntityManager em = ConnUtil.getEntity();
		
		try{
		Crm crm = em.find(Crm.class, cod); 
		
		if(crm != null){
			return crm;
		}else{
			return null;
		}
		}catch(Exception e){
			return null;
		}
	}
	
	public static boolean insertCRM(Crm crm){
		EntityManager em = ConnUtil.getEntity();
		try{
			if(crm.getEmpresa_id() == null){
				crm.setEmpresa_id(OpusERP4UI.getEmpresa().getId());
			}
			
			if(crm.getEmpresa_id() == 1){
				if(crm.getSetor() == null){
					crm.setSetor(new Setores(60));
				}
				
				if(crm.getCrm_assuntos() == null){
					crm.setCrm_assuntos(new CrmAssunto(500));
				}
			}
			
			
			em.getTransaction().begin();
			em.persist(crm);
			em.getTransaction().commit();
			
			return true;
		}catch(Exception e){
			e.printStackTrace();
			
			return false;
		}
	}
	
	
	public static void updateCRM(Crm crm){
		EntityManager em = ConnUtil.getEntity();
		
		try{
			em.getTransaction().begin();
			em.merge(crm);
			em.getTransaction().commit();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public static String getNextID(){
		
		EntityManager em = ConnUtil.getEntity();
		
		try{
			em = ConnUtil.getEntity();	
			Query q = em.createNativeQuery("SHOW TABLE STATUS LIKE 'crm'");	
			Object result = q.getSingleResult();
			Object[] ob = (Object[]) result;
			String cod = ob[10].toString();
			
			return cod;
		}catch(Exception e){
			return null;
		}
	}

	
	public static Crm saveCrm(Crm crm){
		
		EntityManager em = ConnUtil.getEntity();
		
	    try {
	    	
	    	
	    		em.getTransaction().begin();
	    	
	    	if(crm.getId() == null) {
	    		em.persist(crm);
	    	} else {
	    		crm = em.merge(crm);
	    	}
	    	try{
	    		em.getTransaction().commit();
	    	}catch(Exception e){
	    		e.printStackTrace();
	    		System.out.println("Erro ao comitar as informações-> EnderecoDAO: line 77 "+e.getMessage());
	    	}

	    	return crm;
	    }catch(Exception e){
			return null;
		}
		  
	}
	
	public static boolean preTratar(Integer codCrm){
		EntityManager em = ConnUtil.getEntity();
		
		try{			
			Crm crm = em.find(Crm.class, codCrm);			
			if(crm != null){
				em.getTransaction().begin();
				
				crm.setOperador_tratamento(OpusERP4UI.getUsuarioLogadoUI().getUsername());
				crm.setStatus("PRE-TRATAMENTO");
				
				em.merge(crm);
				em.getTransaction().commit();		
				
				return true;
			}
			
			return false;
		}catch(Exception e){
			e.printStackTrace();			
			return false;
		}
	}
	
}

package com.digital.opuserp.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.AcessoCliente;
import com.digital.opuserp.domain.ContratosAcesso;
import com.digital.opuserp.domain.PlanoAcesso;
import com.digital.opuserp.domain.RadReply;
import com.digital.opuserp.util.ConnUtil;

public class ContratosAcessoDAO {
	EntityManager em;
	
	public static List<AcessoCliente> getContratosNfe(String nomeCliente){
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createQuery("select a from AcessoCliente a where a.emitir_nfe ='NFE-MOD21' and a.status_2 = 'ATIVO' and a.contrato.tipo_contrato !='GRATIS' and a.cliente.nome_razao like :nomeCliente", AcessoCliente.class);
		q.setParameter("nomeCliente", '%'+nomeCliente+'%');
		
		return q.getResultList();
	}
			
	public static AcessoCliente getContratoPorEndereco(Integer codEndereco){
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createQuery("select a from AcessoCliente a where a.endereco=:end and a.status_2 != 'ENCERRADO'", AcessoCliente.class);
		q.setParameter("end", EnderecoDAO.find(codEndereco));
		
		if(q.getResultList().size() == 1){
			return (AcessoCliente)q.getSingleResult();
		}
		
		return null;
	}
	
	public static List<ContratosAcesso> getContratos(){
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createQuery("select c from ContratosAcesso c where c.empresa_id=:empresa", ContratosAcesso.class);
		q.setParameter("empresa", OpusERP4UI.getEmpresa().getId());
		
		return q.getResultList();
	}

	public ContratosAcessoDAO() {
		em = ConnUtil.getEntity();
	}
	
	
	public static AcessoCliente find(Integer cod){
		if(cod != null){
			EntityManager em = ConnUtil.getEntity();
			return em.find(AcessoCliente.class, cod);
		}
		
		return null;
	}
	
	public static  void teste(){
		EntityManager em = ConnUtil.getEntity();		
		Query q = em.createQuery("select a from AcessoCliente a where a.vlr_carencia = '12 MESES'", AcessoCliente.class);		
	}
	
	public boolean allowRemove(Integer codContratoAcesso){
		
		boolean allow = true;
		//Verificar se o Contrato de Acesso está em uso, 
		
		//Acesso Cliente
		Query qAcesso = em.createQuery("select ac from AcessoCliente ac where ac.contrato = :ContratoAcesso");
		qAcesso.setParameter("ContratoAcesso", new ContratosAcesso(codContratoAcesso));
		qAcesso.setMaxResults(1);
		if(qAcesso.getResultList().size() > 0){
			allow = false;
		}	
		return allow;
	}

	
	public ContratosAcesso getContrato(Integer codPlano){
			
		PlanoAcesso planoAcesso = em.find(PlanoAcesso.class,codPlano);			
		ContratosAcesso contratoAcesso = em.find(ContratosAcesso.class, planoAcesso.getContrato_acesso().getId());		
		if(contratoAcesso != null){
			return contratoAcesso;
		}else{
			return null;			
		}		
	}
	
	public static Boolean getContratoPorMac(String mac){
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createQuery("select a from AcessoCliente a where a.endereco_mac=:mac and a.regime = 'COMODATO'", AcessoCliente.class);
		q.setParameter("mac", mac);
				
			if(q.getResultList().size() > 0){
				return true;
			}else{			
				return false;
			}
	}
	
	public static AcessoCliente getContratoPorLogin(String login){
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createQuery("select a from AcessoCliente a where a.login = :login ", AcessoCliente.class);
		q.setParameter("login", login);
				
		if(q.getResultList().size() == 1){
			return (AcessoCliente)q.getSingleResult();
		}
		
		return null;			
	}
	
	public static List<RadReply> getLoginsBloqueado(){
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createQuery("select r from RadReply r where r.value ='BLOQUEADO' ", RadReply.class);
		
		return q.getResultList();		
	}
	
}

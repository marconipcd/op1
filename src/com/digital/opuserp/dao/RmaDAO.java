package com.digital.opuserp.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.AlteracoesRma;
import com.digital.opuserp.domain.AlteracoesSerial;
import com.digital.opuserp.domain.EcfPreVendaCabecalho;
import com.digital.opuserp.domain.EcfPreVendaDetalhe;
import com.digital.opuserp.domain.Fornecedor;
import com.digital.opuserp.domain.LogProduto;
import com.digital.opuserp.domain.Produto;
import com.digital.opuserp.domain.RmaDetalhe;
import com.digital.opuserp.domain.RmaMestre;
import com.digital.opuserp.util.ConnUtil;

public class RmaDAO {
	
	public static void fechar(Integer rma){
		
		try{
			EntityManager em = ConnUtil.getEntity();
			
			em.getTransaction().begin();
			
			RmaMestre rmaMestre = em.find(RmaMestre.class, rma);
			rmaMestre.setStatus("FECHADO");
			em.merge(rmaMestre);
			
			em.getTransaction().commit();
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void removeItensRma(Integer rma){
		try{
			List<RmaDetalhe> itens = getItensRma(rma);
			
			EntityManager em = ConnUtil.getEntity();
			
			em.getTransaction().begin();
			for (RmaDetalhe item: itens) {
				em.remove(item); 
			}
			em.getTransaction().commit();
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static List<RmaDetalhe> getRmaPorFornecedor(Fornecedor fornecedor){
		try{
			EntityManager em = ConnUtil.getEntity();
			Query q = em.createQuery("select r from RmaDetalhe r where r.fornecedor=:fornecedor", RmaDetalhe.class);
			q.setParameter("fornecedor", fornecedor);
			
			return q.getResultList();
		}catch(Exception e){
			return null;
		}
	}
	
	public static RmaDetalhe find(Integer rma_id){
		try{
			EntityManager em = ConnUtil.getEntity();
			return em.find(RmaDetalhe.class, rma_id);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public static RmaMestre findRMA(Integer rma_id){
		try{
			EntityManager em = ConnUtil.getEntity();
			return em.find(RmaMestre.class, rma_id);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public static boolean registraLog(RmaDetalhe rma, String tipo){
		try{
			EntityManager em  = ConnUtil.getEntity();
			
			em.getTransaction().begin();
			em.persist(new AlteracoesRma(null, rma, tipo, OpusERP4UI.getUsuarioLogadoUI(), new Date()));
			em.getTransaction().commit();
			
			return true;
		}catch(Exception e){
			e.printStackTrace();
			
			return false;
		}
	}
	
	public static boolean mudarStatus(Integer rma_id, String status){
		try{
			EntityManager em = ConnUtil.getEntity();
			
			RmaDetalhe rma = em.find(RmaDetalhe.class, rma_id);
			
			if(rma != null){
				rma.setStatus(status);
				rma.setData_mudanca_status(new Date());
				
				em.getTransaction().begin();
				em.merge(rma);
				em.getTransaction().commit();			

				return true;
			}else{
				return false;
			}
			
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}

	public static boolean remover(Integer rma){
		try{
			EntityManager em = ConnUtil.getEntity();
			RmaDetalhe rmad = em.find(RmaDetalhe.class, rma);
			Query q = em.createQuery("select a from AlteracoesRma a where a.rma =:rma", AlteracoesRma.class);
			q.setParameter("rma", rmad);
			
			em.getTransaction().begin();
			
			
			if(rmad != null && rmad.getId() != null){
				//Remover Pedido
				Query q1 = em.createQuery("select e from EcfPreVendaCabecalho e where e.rma_id=:rma",EcfPreVendaCabecalho.class);
				q1.setParameter("rma", rmad.getId());
				
				if(q1.getResultList().size() > 0){
					for (EcfPreVendaCabecalho itemVenda : (List<EcfPreVendaCabecalho>)q1.getResultList()) {
						
						Query q2 = em.createQuery("select e from EcfPreVendaDetalhe e where e.ecfPreVendaCabecalhoId =:codVenda", EcfPreVendaDetalhe.class);
						q2.setParameter("codVenda", itemVenda.getId());
						
						for (EcfPreVendaDetalhe detalhe : (List<EcfPreVendaDetalhe>)q2.getResultList()) {
							em.remove(detalhe); 
						}
						
						em.remove(itemVenda); 
							
					}
				}
			}
			
			
			//-----Logg
			LogProdutoDAO.registraLogSemTransacao(new LogProduto(null,rmad.getProduto(), "REMOVER RMA",rmad.getProduto().getQtdEstoque(),rmad.getProduto().getQtdEstoque()+1, new Date(), OpusERP4UI.getUsuarioLogadoUI().getUsername()));
			//-----Logg
			
			rmad.getProduto().setQtdEstoque(rmad.getProduto().getQtdEstoque()+1);
			if(rmad.getSerial() != null){
				rmad.getSerial().setStatus("ATIVO");
				em.persist(new AlteracoesSerial(null, "ESTORNADO ", rmad.getSerial(), OpusERP4UI.getEmpresa(), OpusERP4UI.getUsuarioLogadoUI(), new Date()));	
			}
			
			for (AlteracoesRma alteracao : (List<AlteracoesRma>)q.getResultList()) {
				em.remove(alteracao);
			}
			
			em.remove(rmad);
			
			em.getTransaction().commit();
			
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public static RmaDetalhe save(RmaDetalhe Item){
		try{
			EntityManager em = ConnUtil.getEntity();
			
			em.getTransaction().begin();
			
			if(Item.getId() != null){
				em.merge(Item);
			}else{
				em.persist(Item);
			}
			
			Produto p = ProdutoDAO.find(Item.getProduto().getId());
			
			//-----Logg
			LogProdutoDAO.registraLogSemTransacao(new LogProduto(null,Item.getProduto(), "SALVAR ITEM RMA",Item.getProduto().getQtdEstoque(),p.getQtdEstoque()-1, new Date(), OpusERP4UI.getUsuarioLogadoUI().getUsername()));
			//-----Logg
			
			
			Item.getProduto().setQtdEstoque(p.getQtdEstoque()-1);
			em.merge(Item.getProduto());
			
			if(Item.getSerial() != null)
			{
				Item.getSerial().setStatus("AVARIADO");
				em.merge(Item.getSerial());
				
				em.persist(new AlteracoesSerial(null, "RMA "+Item.getId().toString(), Item.getSerial(), OpusERP4UI.getEmpresa(), OpusERP4UI.getUsuarioLogadoUI(), new Date()));				
			}
			
			em.getTransaction().commit();
			
			return Item;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public static RmaMestre saveMestre(RmaMestre rma){
		try{
			EntityManager em = ConnUtil.getEntity();
			
			em.getTransaction().begin();
			em.persist(rma);
			em.getTransaction().commit();
			
			return rma;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}		
	}
	
	public static List<RmaDetalhe> getItensRma(Integer rma){
		EntityManager em = ConnUtil.getEntity();
		
		Query q = em.createQuery("select r from RmaDetalhe r where r.rma_mestre_id =:rma", RmaDetalhe.class);
		q.setParameter("rma", rma);
		
		return q.getResultList();
	}
}

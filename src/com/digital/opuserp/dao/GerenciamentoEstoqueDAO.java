package com.digital.opuserp.dao;

import java.util.Date;

import javax.persistence.EntityManager;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.Produto;
import com.digital.opuserp.domain.TransferenciaProduto;
import com.digital.opuserp.util.ConnUtil;

public class GerenciamentoEstoqueDAO {

	
	public static EntityManager em = ConnUtil.getEntity();
	
	public static boolean AjusteTransferencia(TransferenciaProduto t){
		try{

			Produto p = ProdutoDAO.find(t.getProduto().getId());
			
			em.getTransaction().begin();			

			t.setStatus("CONCLUIDO");
			t.setUsuario_aceitou_transferencia(OpusERP4UI.getUsuarioLogadoUI().getUsername());
			t.setData_conclusao_transferencia(new Date());
			
			em.persist(t);	
			
			if(t.getTipo().equals("DEPOSITO->LOJA")){
				if(t.getQtd() <= p.getQtdEstoqueDeposito()){
					
					p.setQtdEstoque(p.getQtdEstoque()+t.getQtd());
					p.setQtdEstoqueDeposito(p.getQtdEstoqueDeposito()-t.getQtd());
					p.setAjustado("SIM");
										
					em.merge(p);				
					
					em.getTransaction().commit();
					
					return true;
				}else{
					return false;
				}
			}
			
			if(t.getTipo().equals("LOJA->DEPOSITO")){
				if(t.getQtd() <= p.getQtdEstoque()){
					
					p.setQtdEstoqueDeposito(p.getQtdEstoqueDeposito()+t.getQtd());
					p.setQtdEstoque(p.getQtdEstoque()-t.getQtd());
					p.setAjustado("SIM");
					
					em.merge(p);
									
					em.getTransaction().commit();
					
					return true;
				}else{
					return false;
				}
			}
			
			return false;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean TransferirProduto(TransferenciaProduto transferencia){
		
		try{
			em.getTransaction().begin();
			em.persist(transferencia);
			em.getTransaction().commit();
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean AceitarTransferenciaProduto(TransferenciaProduto t){
		
		try{
			Produto p = ProdutoDAO.find(t.getProduto().getId());
			
			if(t.getTipo().equals("DEPOSITO->LOJA")){
				if(t.getQtd() <= p.getQtdEstoqueDeposito()){
					
					p.setQtdEstoque(p.getQtdEstoque()+t.getQtd());
					p.setQtdEstoqueDeposito(p.getQtdEstoqueDeposito()-t.getQtd());
									
					t.setStatus("CONCLUIDO");
					t.setUsuario_aceitou_transferencia(OpusERP4UI.getUsuarioLogadoUI().getUsername());
					t.setData_conclusao_transferencia(new Date());
					
					em.getTransaction().begin();		
					
					em.merge(p);
					em.merge(t);
					
					em.getTransaction().commit();
					
					return true;
				}else{
					return false;
				}
			}
			
			if(t.getTipo().equals("LOJA->DEPOSITO")){
				if(t.getQtd() <= p.getQtdEstoque()){
					
					p.setQtdEstoqueDeposito(p.getQtdEstoqueDeposito()+t.getQtd());
					p.setQtdEstoque(p.getQtdEstoque()-t.getQtd());
					
					em.getTransaction().begin();		
					
					em.merge(p);
					em.merge(t);
					
					em.getTransaction().commit();
					
					return true;
				}else{
					return false;
				}
			}
			
			return false;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		
	}
	
	public static boolean CancelarTransferenciaProduto(TransferenciaProduto t){
		try{
			em.getTransaction().begin();
			em.merge(t);
			em.getTransaction().commit();
			
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}

}

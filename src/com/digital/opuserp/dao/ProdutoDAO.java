package com.digital.opuserp.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.Query;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.Crm;
import com.digital.opuserp.domain.EcfPreVendaDetalhe;
import com.digital.opuserp.domain.Empresa;
import com.digital.opuserp.domain.LogProduto;
import com.digital.opuserp.domain.MovimentoEntCabecalho;
import com.digital.opuserp.domain.MovimentoEntDetalhe;
import com.digital.opuserp.domain.Produto;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.boletos.boleto.bancos.Sicredi;
import com.digital.opuserp.view.util.Notify;

public class ProdutoDAO {

	static EntityManager em;
	
	public static List<Produto> getProdutosPorVenda(Integer cod_pedido){
		
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createQuery("select det from EcfPreVendaDetalhe det where det.ecfPreVendaCabecalhoId=:pedido", EcfPreVendaDetalhe.class);
		q.setParameter("pedido", cod_pedido);
		
		List<Produto> produtos = new ArrayList<>();
		for (EcfPreVendaDetalhe itemPedido : (List<EcfPreVendaDetalhe>)q.getResultList()) {
			produtos.add(ProdutoDAO.find(itemPedido.getProdutoId()));
		}
		
		return produtos;
	}
	
	public static List<Produto> getProdutosMateriais(){
		
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createQuery("select p from Produto p where p.empresaId =:empresa_id and p.grupoId = 21", Produto.class);
		q.setParameter("empresa_id", OpusERP4UI.getEmpresa());
		
		return q.getResultList();
	}
	
	public static void insertProduto(Produto produto){
		try{		
			EntityManager em = ConnUtil.getEntity();
			em.getTransaction().begin();
			em.persist(produto);
			em.getTransaction().commit();
		}catch(Exception e){
			if(em.getTransaction().isActive()){
				em.getTransaction().rollback();
			}
			e.printStackTrace();			
		}
	}
	
	
	public ProdutoDAO(){
		em = ConnUtil.getEntity();
	}
	
	public static void alterar(Produto p){
		EntityManager em = ConnUtil.getEntity();
		
		try{
			//Produto produto = em.find(Produto.class, p.getId());
			//if(produto != null){
				em.getTransaction().begin();
				em.merge(p);
				em.getTransaction().commit();
			//}
		}catch(Exception e){
			if(em.getTransaction().isActive()){
				em.getTransaction().rollback();
			}
			e.printStackTrace();
		}				
	}
	
	public static boolean estornarProdutoQtdDeposito(Produto p, Float qtd, String acao){
		
		EntityManager em = ConnUtil.getEntity();
		
		try{
			
			Produto produto = em.find(Produto.class, p.getId());
			
			if(produto != null){
				
				em.getTransaction().begin();
				//-----Logg
				LogProdutoDAO.registraLogSemTransacao(new LogProduto(null,produto, "EDICAO PRODUTO",produto.getQtdEstoque(),p.getQtdEstoque(), new Date(), OpusERP4UI.getUsuarioLogadoUI().getUsername()));
				//-----Logg
			
				if(acao.equals("S")){
					produto.setQtdEstoqueDeposito(produto.getQtdEstoqueDeposito()-qtd);
				}
				
				if(acao.equals("A")){
					produto.setQtdEstoqueDeposito(produto.getQtdEstoqueDeposito()+qtd);
				}
				
				em.merge(produto);
				em.getTransaction().commit();
				
			//(p.getCod_pro_loja() != null  && OpusERP4UI.getEmpresa().getId() != 6 && OpusERP4UI.getEmpresa().getId() != 4){
			//		PedidoDAO.sincronizarEstoque(p.getCod_pro_loja(), produto.getQtdEstoque().toString());
			//	}
				
				return true;
			}else{
				return false;
			}
		}catch(Exception e){
			
			e.printStackTrace();
			System.out.println("Não é Possivel Editar Produto");
			Notify.Show("Não foi possível atualizar saldo do produto", Notify.TYPE_ERROR);
			
			return false;
		}
		
	}
	
	public static boolean edit(Produto p, Float qtd, String acao){
		
		EntityManager em = ConnUtil.getEntity();
		
		try{
			
			Produto produto = em.find(Produto.class, p.getId());
			
			if(produto != null){
				
				em.getTransaction().begin();
				//-----Logg
				LogProdutoDAO.registraLogSemTransacao(new LogProduto(null,produto, "EDICAO PRODUTO",produto.getQtdEstoque(),p.getQtdEstoque(), new Date(), OpusERP4UI.getUsuarioLogadoUI().getUsername()));
				//-----Logg
			
				if(acao.equals("S")){
					produto.setQtdEstoque(produto.getQtdEstoque()-qtd);
				}
				
				if(acao.equals("A")){
					produto.setQtdEstoque(produto.getQtdEstoque()+qtd);
				}
				
				em.merge(produto);
				em.getTransaction().commit();
				
				if(p.getCod_pro_loja() != null  && OpusERP4UI.getEmpresa().getId() != 6 && OpusERP4UI.getEmpresa().getId() != 4){
					PedidoDAO.sincronizarEstoque(p.getCod_pro_loja(), produto.getQtdEstoque().toString());
				}
				
				return true;
			}else{
				return false;
			}
		}catch(Exception e){
			
			e.printStackTrace();
			System.out.println("Não é Possivel Editar Produto");
			Notify.Show("Não foi possível atualizar saldo do produto", Notify.TYPE_ERROR);
			
			return false;
		}
		
	}
	
	public static void editNovo(Produto p){
		
		EntityManager em = ConnUtil.getEntity();
		
		try{
			
			Produto produto = em.find(Produto.class, p.getId());
			
			if(produto != null){
				
				//-----Logg
				LogProdutoDAO.registraLogSemTransacao(new LogProduto(null,produto, "EDICAO PRODUTO",produto.getQtdEstoque(),p.getQtdEstoque(), new Date(), OpusERP4UI.getUsuarioLogadoUI().getUsername()));
				//-----Logg
			
			
				produto.setQtdEstoque(p.getQtdEstoque());
				
//				em.getTransaction().begin();
				em.merge(produto);
//				em.getTransaction().commit();
			}
		}catch(Exception e){
			
			e.printStackTrace();
			System.out.println("Não é Possivel Editar Produto");
		}
		
	}
	
	public static Produto findByCodBarras(String cod){
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createQuery("select p from Produto p where p.empresaId =:codEmpresa and p.gTin =:cod",Produto.class);
		q.setParameter("codEmpresa", OpusERP4UI.getEmpresa());
		q.setParameter("cod", cod);
		
		if(q.getResultList().size() == 1){			
			return (Produto)q.getSingleResult();
		}
		
		return null;
	}
	
	public static boolean checkExistCodBarras(String cod){
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createQuery("select p from Produto p where p.empresaId =:codEmpresa and p.gTin =:cod",Produto.class);
		q.setParameter("codEmpresa", OpusERP4UI.getEmpresa());
		q.setParameter("cod", cod);
		
		if(q.getResultList().size() > 0){			
			return true;
		}
		
		return false;
	}
	public static Produto limpaCodBarrasDuplicado(String codBarras, Produto p2){
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createQuery("select p from Produto p where p.empresaId =:codEmpresa and p.gTin =:cod",Produto.class);
		q.setParameter("codEmpresa", OpusERP4UI.getEmpresa());
		q.setParameter("cod", codBarras);
		
		if(q.getResultList().size() > 1){
			List<Produto> produtos = q.getResultList();
			
			for (Produto produto : produtos) {
				Produto p = produto;
				if(p.getId() != p2.getId()){
				p.setgTin(p.getId().toString());
				ProdutoDAO.alterar(p);
				}
			}
			
		}
		
		
		return null;
	}
	
	public static Produto find(Integer cod){
		
		try{
			EntityManager em = ConnUtil.getEntity();
			
			Query q = em.createQuery("select p from Produto p where p.id=:cod and p.empresaId=:empresa", Produto.class);
			//q.setLockMode(LockModeType.PESSIMISTIC_READ);
			q.setParameter("cod", cod);
			q.setParameter("empresa", OpusERP4UI.getEmpresa());
			
			if(q.getResultList().size() == 1){
				Produto p = (Produto)q.getSingleResult();			
				return p;
			}else{
				return null;
			}
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

	public String getNextID(){
		try{
			em = ConnUtil.getEntity();	
			Query q = em.createNativeQuery("SHOW TABLE STATUS LIKE 'produto'");	
			Object result = q.getSingleResult();
			Object[] ob = (Object[]) result;
			String cod = ob[10].toString();
			
			return cod;
		}catch (Exception e){
			e.printStackTrace();
			System.out.println("Erro ao tentar pegar o próximo ID do produto: "+e.getMessage());
			return null;
		}
	}
	
	public static void remove(Produto p){
		
		EntityManager em = ConnUtil.getEntity();
		
		try{
			if(p != null){
				em.getTransaction().begin();
				em.remove(em.contains(p) ? p : em.merge(p));
				em.getTransaction().commit();
			}
		}catch(Exception e){
			
			e.printStackTrace();
			System.out.println("Não é Possivel remover o Produto");
		}
		
	}
	
	public static List<MovimentoEntDetalhe> movEntDet(Integer codProduto){
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createQuery("select c from MovimentoEntDetalhe c where c.produtoId =:CodProduto order by c.id desc ",MovimentoEntDetalhe.class);
		q.setParameter("CodProduto", codProduto);
		List<MovimentoEntDetalhe> movimentoDetalhe = null;
		
		if(q.getResultList().size() > 0){
			movimentoDetalhe = q.getResultList();
		}
		return movimentoDetalhe;
	
	}
	
	public static List<EcfPreVendaDetalhe> EntDet(Integer codProduto){
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createQuery("select c from EcfPreVendaDetalhe c where c.produtoId =:CodProduto order by c.id desc ",EcfPreVendaDetalhe.class);
		q.setParameter("CodProduto", codProduto);
		List<EcfPreVendaDetalhe> ECFDetalhe = null;
		
		if(q.getResultList().size() > 0){
			ECFDetalhe = q.getResultList();
		}
		return ECFDetalhe;
		
	}
	
	
	public static MovimentoEntCabecalho movEntCab(Integer codCab){
		EntityManager em = ConnUtil.getEntity();
		Query cab = em.createQuery("select c from MovimentoEntCabecalho c where c.id =:id and c.situacao='F' order by c.id desc",MovimentoEntCabecalho.class);

		cab.setParameter("id", codCab);
		MovimentoEntCabecalho movimentoCabecalho = null;
		
		if(cab.getResultList().size() > 0){
			movimentoCabecalho = (MovimentoEntCabecalho) cab.getSingleResult();
		}
		return movimentoCabecalho;
	
	}
	
	public static List<Produto> listProduto(Integer codEmpresa){
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createQuery("select p from Produto p where p.empresaId =:codEmpresa and p.status = 'ATIVO'",Produto.class);
		q.setParameter("codEmpresa", new Empresa(codEmpresa));
		List<Produto> produto = null;
		
		if(q.getResultList().size() > 0){
			produto =  q.getResultList();
		}
		return produto;
		
	}
	
	public static void saveProduto(Produto p){
		
			EntityManager em =  ConnUtil.getEntity();
			if(p.getId() != null){
				em.merge(p);
			}else{
				em.persist(p); 
			}
    }
	
	
}

package com.digital.opuserp.dao;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.AlteracoesEstoqueMovel;
import com.digital.opuserp.domain.EcfPreVendaCabecalho;
import com.digital.opuserp.domain.EcfPreVendaDetalhe;
import com.digital.opuserp.domain.EstoqueMovel;
import com.digital.opuserp.domain.LogProduto;
import com.digital.opuserp.domain.NaturezaOperacao;
import com.digital.opuserp.domain.Produto;
import com.digital.opuserp.domain.Veiculos;
import com.digital.opuserp.util.ConnUtil;

public class MateriaisAlocadosDAO {

	public static EstoqueMovel alocarMaterialSemEstoque(Veiculos veiculo, Produto p, Integer qtd){
		try{
			EntityManager em = ConnUtil.getEntity();
			
			em.getTransaction().begin();
			
				Query q = em.createQuery("select est from EstoqueMovel est where est.produto=:p and est.veiculo=:v and est.empresa_id=:e", EstoqueMovel.class);
				q.setParameter("p", p);
				q.setParameter("v", veiculo);
				q.setParameter("e", OpusERP4UI.getEmpresa().getId());
				
				EstoqueMovel materialCab;
				
				if(q.getResultList().size() == 1){
					materialCab = (EstoqueMovel)q.getSingleResult();
					
					if(p != null){
					
						materialCab.setStatus("ATIVO"); 
						materialCab.setQtd(materialCab.getQtd()+qtd);
						materialCab.setData_ultima_alteracao(new Date());
						
						em.merge(materialCab);
												
						AlteracoesEstoqueMovelDAO.save(new AlteracoesEstoqueMovel(null, materialCab, OpusERP4UI.getUsuarioLogadoUI(), "ALOCADO", new Date(), new Float(qtd)));
					}
				}else{
					materialCab = new EstoqueMovel();
										
					if(p != null){
						
						materialCab.setEmpresa_id(OpusERP4UI.getEmpresa().getId());
						materialCab.setVeiculo(veiculo);
						materialCab.setData_cadastro(new Date());
						materialCab.setData_ultima_alteracao(new Date());
						materialCab.setStatus("ATIVO"); 
						
						materialCab.setProduto(p);
						materialCab.setQtd(qtd);
						
						em.persist(materialCab);
																		
						AlteracoesEstoqueMovelDAO.save(new AlteracoesEstoqueMovel(null, materialCab, OpusERP4UI.getUsuarioLogadoUI(), "ALOCADO", new Date(), new Float(qtd)));
					}
				}
				
			em.getTransaction().commit();
					
			return materialCab;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public static EstoqueMovel alocarMaterial(Veiculos veiculo, Produto p, Integer qtd, String infoAdicional){
		try{
			EntityManager em = ConnUtil.getEntity();
			
			em.getTransaction().begin();
			
				Query q = em.createQuery("select est from EstoqueMovel est where est.produto=:p and est.veiculo=:v and est.empresa_id=:e", EstoqueMovel.class);
				q.setParameter("p", p);
				q.setParameter("v", veiculo);
				q.setParameter("e", OpusERP4UI.getEmpresa().getId());
				
				EstoqueMovel materialCab;
				
				if(q.getResultList().size() == 1){
					materialCab = (EstoqueMovel)q.getSingleResult();
					
					if(p != null){
					
						materialCab.setStatus("ATIVO"); 
						materialCab.setQtd(materialCab.getQtd()+qtd);
						materialCab.setData_ultima_alteracao(new Date());
						materialCab.setInfo_adicionais(infoAdicional);
						
						p.setQtdEstoqueDeposito(new Float(p.getQtdEstoqueDeposito()-qtd));
						
						LogProdutoDAO.registraLogSemTransacao(new LogProduto(null,p, "ALOCOU PRODUTO",p.getQtdEstoqueDeposito()+qtd,p.getQtdEstoqueDeposito(), new Date(), OpusERP4UI.getUsuarioLogadoUI().getUsername()));
						
						em.merge(materialCab);
						em.merge(p);
						
						final EcfPreVendaCabecalho pedido = new EcfPreVendaCabecalho();
						//pedido.setCliente(cliente);
						pedido.setEmpresa_id(OpusERP4UI.getEmpresa().getId());
						pedido.setData(new Date());
						pedido.setHora_pv(new Date());
						pedido.setTipo("PEDIDO OS");
						pedido.setSituacao("F");
						pedido.setN_os(materialCab.getId());
						
						NaturezaOperacao natureza = NaturezaOperacaoDAO.findbyName("VENDA");
						pedido.setNaturezaOperacao(natureza);
						
						em.persist(pedido);
						
						EcfPreVendaDetalhe detalhe = new EcfPreVendaDetalhe();
						detalhe.setProdutoId(p.getId());
						detalhe.setQuantidade(new Float(qtd));
						detalhe.setEcfPreVendaCabecalhoId(pedido.getId());
						
						em.persist(detalhe);			
						
						
						AlteracoesEstoqueMovelDAO.save(new AlteracoesEstoqueMovel(null, materialCab, OpusERP4UI.getUsuarioLogadoUI(), "ALOCADO", new Date(), new Float(qtd)));
					}
				}else{
					materialCab = new EstoqueMovel();
										
					if(p != null){
						
						materialCab.setEmpresa_id(OpusERP4UI.getEmpresa().getId());
						materialCab.setVeiculo(veiculo);
						materialCab.setData_cadastro(new Date());
						materialCab.setData_ultima_alteracao(new Date());
						materialCab.setStatus("ATIVO"); 
						materialCab.setInfo_adicionais(infoAdicional);
						
						materialCab.setProduto(p);
						materialCab.setQtd(qtd);
						
						//Retira do Estoque Normal
						p.setQtdEstoqueDeposito(new Float(p.getQtdEstoqueDeposito()-qtd));
						
						em.persist(materialCab);
						em.merge(p);
						
						
						final EcfPreVendaCabecalho pedido = new EcfPreVendaCabecalho();
						//pedido.setCliente(cliente);
						pedido.setEmpresa_id(OpusERP4UI.getEmpresa().getId());
						pedido.setData(new Date());
						pedido.setHora_pv(new Date());
						pedido.setTipo("PEDIDO OS");
						pedido.setSituacao("F");
						pedido.setN_os(materialCab.getId());
						
						NaturezaOperacao natureza = NaturezaOperacaoDAO.findbyName("VENDA");
						pedido.setNaturezaOperacao(natureza);
						
						em.persist(pedido);
						
						EcfPreVendaDetalhe detalhe = new EcfPreVendaDetalhe();
						detalhe.setProdutoId(p.getId());
						detalhe.setQuantidade(new Float(qtd));
						detalhe.setEcfPreVendaCabecalhoId(pedido.getId());
						
						em.persist(detalhe);
						
						AlteracoesEstoqueMovelDAO.save(new AlteracoesEstoqueMovel(null, materialCab, OpusERP4UI.getUsuarioLogadoUI(), "ALOCADO", new Date(), new Float(qtd)));
					}
				}
				
			em.getTransaction().commit();
					
			return materialCab;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public static EstoqueMovel find(Integer cod){
		EntityManager em = ConnUtil.getEntity();
		EstoqueMovel estoqMov = em.find(EstoqueMovel.class, cod);
		
		return estoqMov;
	}
	
	public static boolean estornarMaterial(Integer codEstoque, double qtdEstornar){
		try{
			EntityManager em = ConnUtil.getEntity();
			
			EstoqueMovel estoqMov = em.find(EstoqueMovel.class, codEstoque);
			
			if(estoqMov != null){
				em.getTransaction().begin();
				
					Float qtd_estornada = new Float(qtdEstornar);
					if(qtd_estornada < 1){
						qtd_estornada = Float.parseFloat(String.valueOf(estoqMov.getQtd()));
					}
					
					Produto p = ProdutoDAO.find(estoqMov.getProduto().getId());
					if(p != null){
						p.setQtdEstoqueDeposito(new Float(p.getQtdEstoqueDeposito() + qtd_estornada));
					}
					
					estoqMov.setQtd(estoqMov.getQtd() - qtd_estornada);
					if(estoqMov.getQtd() == 0){
						estoqMov.setStatus("INATIVO");
					}
					
					em.merge(estoqMov);
					em.merge(p);
					AlteracoesEstoqueMovelDAO.save(new AlteracoesEstoqueMovel(null, estoqMov, OpusERP4UI.getUsuarioLogadoUI(), "ESTORNADO", new Date(), qtd_estornada));
				
				em.getTransaction().commit();				
			}
			
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean estornarMaterialSemEstoque(Integer codEstoque, double qtdEstornar){
		try{
			EntityManager em = ConnUtil.getEntity();
			
			EstoqueMovel estoqMov = em.find(EstoqueMovel.class, codEstoque);
			
			if(estoqMov != null){
				em.getTransaction().begin();
				
					Float qtd_estornada = new Float(qtdEstornar);
					if(qtd_estornada < 1){
						qtd_estornada = Float.parseFloat(String.valueOf(estoqMov.getQtd()));
					}
					
//					Produto p = ProdutoDAO.find(estoqMov.getProduto().getId());
//					if(p != null){
//						p.setQtdEstoqueDeposito(new Float(p.getQtdEstoqueDeposito() + qtd_estornada));
//					}
					
					estoqMov.setQtd(estoqMov.getQtd() - qtd_estornada);
					if(estoqMov.getQtd() == 0){
						estoqMov.setStatus("INATIVO");
					}
					
					em.merge(estoqMov);
					//em.merge(p);
					AlteracoesEstoqueMovelDAO.save(new AlteracoesEstoqueMovel(null, estoqMov, OpusERP4UI.getUsuarioLogadoUI(), "ESTORNADO", new Date(), qtd_estornada));
				
				em.getTransaction().commit();				
			}
			
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
}

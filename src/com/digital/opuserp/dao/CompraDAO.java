package com.digital.opuserp.dao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.EcfPreVendaCabecalho;
import com.digital.opuserp.domain.EcfPreVendaDetalhe;
import com.digital.opuserp.domain.Empresa;
import com.digital.opuserp.domain.LogProduto;
import com.digital.opuserp.domain.MovimentoEntCabecalho;
import com.digital.opuserp.domain.MovimentoEntDetalhe;
import com.digital.opuserp.domain.Produto;
import com.digital.opuserp.domain.Servico;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.Real;
import com.digital.opuserp.view.util.Notify;

public class CompraDAO {
	
	
	
	public static void removeItens(MovimentoEntCabecalho compra){
		
		for (MovimentoEntDetalhe item : getItensCompra(compra.getId())) {
			deleteItem(item);
		}
	}
	
	public static boolean deleteItem(MovimentoEntDetalhe item){
		EntityManager em = ConnUtil.getEntity();
		try{
			em.getTransaction().begin();
			
			//Remove Seriais Vinculados
//			SeriaisCompraDAO.removeByItem(item);
			
			
			em.remove(item); 			
			em.getTransaction().commit();
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public static MovimentoEntDetalhe saveItem(MovimentoEntDetalhe item){
		EntityManager em = ConnUtil.getEntity();
		try{
			
			
			Query q = em.createQuery("select e from MovimentoEntDetalhe e where e.produtoId=:cod and e.movimentoEntCabecalhoId=:codcompra", MovimentoEntDetalhe.class);
			q.setParameter("cod", item.getProdutoId());
			q.setParameter("codcompra", item.getMovimentoEntCabecalhoId());
			
			if(q.getResultList().size() == 1){
				MovimentoEntDetalhe produtoItem = (MovimentoEntDetalhe)q.getSingleResult();
				produtoItem.setQuantidade(produtoItem.getQuantidade()+item.getQuantidade());
				produtoItem.setValorCusto(item.getValorCusto());
				produtoItem.setValorVenda(item.getValorVenda());
								
				em.getTransaction().begin();
				em.merge(produtoItem);
				em.getTransaction().commit();
				
				return produtoItem;
			}else{		
				em.getTransaction().begin();
				
				if(item.getId() == null){
					em.persist(item);
				}else{
					em.merge(item);
				}
				em.getTransaction().commit();
				return item;
			}
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public static boolean save(MovimentoEntCabecalho compra){
		EntityManager em = ConnUtil.getEntity();
		try{
			em.getTransaction().begin();
			
			if(compra.getId() == null){
				em.persist(compra);
			}else{
				em.merge(compra);
			}
			em.getTransaction().commit();
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		
	}
	
	public static MovimentoEntCabecalho saveAndReturn(MovimentoEntCabecalho compra){
		EntityManager em = ConnUtil.getEntity();

		try{
			if(compra.getEmpresa_id() == null){
				compra.setEmpresa_id(OpusERP4UI.getEmpresa().getId());
				compra.setSituacao("A");
				compra.setCodNf(123);
			}
			
			em.getTransaction().begin();
			em.persist(compra);
			em.getTransaction().commit();
			return compra;
		}catch(Exception e){
//			if(em.getTransaction().isActive()){
//				em.getTransaction().rollback();
//			}
			e.printStackTrace();
			return null;
		}
		
	}

	public static MovimentoEntCabecalho getNewCompra(){
			
		return saveAndReturn(new MovimentoEntCabecalho());
		

	}
	

	
	public static String getNextID(){
		
		EntityManager em = ConnUtil.getEntity();	
		try{
			Query q = em.createNativeQuery("SHOW TABLE STATUS LIKE 'movimento_ent_cabecalho'");	
			Object result = q.getSingleResult();
			Object[] ob = (Object[]) result;
			String cod = ob[10].toString();
			
			return cod;
		}catch (Exception e){
			if(em.getTransaction().isActive()){
			   em.getTransaction().rollback();
			}
			e.printStackTrace();
			System.out.println("Erro ao tentar pegar o próximo ID do compra: "+e.getMessage());
			return null;
		}
	}
	
	public static MovimentoEntCabecalho find(Integer cod){
		try{
			EntityManager em = ConnUtil.getEntity();
			Query q  = em.createQuery("select p from MovimentoEntCabecalho p where p.id =:codcompra", MovimentoEntCabecalho.class);
			q.setParameter("codcompra", cod);
			
			
			if(q.getResultList().size() == 1){
				return (MovimentoEntCabecalho)q.getSingleResult();
			}
			
			return null;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public static MovimentoEntCabecalho find2(Integer cod){
		EntityManager em = ConnUtil.getEntity();
		try{
			return  em.find(MovimentoEntCabecalho.class, cod);
			
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public static List<MovimentoEntDetalhe> getItensCompra(Integer cod){
		EntityManager em = ConnUtil.getEntity();
		
		try{
			Query q = em.createQuery("select epd from MovimentoEntDetalhe epd where epd.movimentoEntCabecalhoId =:codcompra", MovimentoEntDetalhe.class);
			q.setParameter("codcompra", cod);
			
			return q.getResultList();
		}catch(Exception e){
            return null;
            
        }
	}
	
	public static String getLayoutcompra(EcfPreVendaCabecalho compra, boolean allow){
		
		StringBuilder s = new StringBuilder();
		
		
		String ABRE_CENTRALIZAR = new String(new char[]{27,97,1});
		String FECHA_CENTRALIZAR = new String(new char[]{27,97,0});
		
		String ABRE_NEGRITO = new String(new char[]{27,69});
		String FECHA_NEGRITO = new String(new char[]{27,70});
		
		String ABRE_CONDENSED = new String(new char[]{27,15});
		String FECHA_CONDENSED = new String(new char[]{27,15,18});
		
		char GUILHOTINA27 = 27;
		char GUILHOTINA_TOTAL = 119;
		char GUILHOTINA_PARCIAL = 109;
		
		Empresa empresa = OpusERP4UI.getEmpresa();
		
		String quebra = "\n";
		
		if(compra != null && allow){
						
			
			s.append(ABRE_CONDENSED);		
			
			
			
			s.append(ABRE_CENTRALIZAR);		
			s.append(ABRE_NEGRITO);
			s.append(empresa.getNome_fantasia()+"\n");
			s.append(FECHA_NEGRITO);
			s.append(FECHA_CENTRALIZAR);
			
			s.append(ABRE_CENTRALIZAR);		
			s.append(empresa.getRazao_social()+"\n");
			s.append(FECHA_CENTRALIZAR);			

			
			s.append(ABRE_CENTRALIZAR);	
			s.append(empresa.getEndereco()+","+empresa.getNumero()+" "+empresa.getBairro()+"\n");
			s.append(FECHA_CENTRALIZAR);			
			
			s.append(ABRE_CENTRALIZAR);	
			s.append(empresa.getCidade()+","+empresa.getUf()+"\n");
			s.append(FECHA_CENTRALIZAR);			
			
			s.append(ABRE_CENTRALIZAR);	
			s.append("("+empresa.getDdd_fone1()+") "+empresa.getFone1()+"\n");
			s.append(FECHA_CENTRALIZAR);		
			
			s.append(ABRE_CENTRALIZAR);	
			s.append(empresa.getSite()+"\n");
			s.append(FECHA_CENTRALIZAR);

			
			s.append(quebra);
			s.append(quebra);
			
			s.append(ABRE_CENTRALIZAR);	
			if(compra.getTipo().equals("PEDIDO")){
			s.append(ABRE_NEGRITO);
				s.append("PRE-VENDA \n");
			s.append(FECHA_NEGRITO);
			}else{
				s.append(ABRE_NEGRITO);
				s.append("ORCAMENTO \n");
				s.append(FECHA_NEGRITO);
			}
			s.append(FECHA_CENTRALIZAR);
			
			s.append(quebra);
			s.append(quebra);
			
			
			s.append("-----------------------------------------------------------------\n");
			s.append(ABRE_CENTRALIZAR);
			s.append("NAO E DOCUMENTO FISCAL\n");
			s.append("NAO E VALIDO COMO RECIBO\n");
			s.append("NAO E VALIDO COMO GARANTIA DE MERCADORIA\n");
			s.append(FECHA_CENTRALIZAR);
			s.append("-----------------------------------------------------------------\n");
			s.append(quebra);
			s.append(quebra);
			
			
			SimpleDateFormat formHora= new SimpleDateFormat("HH:mm:ss");
			String hora = formHora.format(new Date());
			
			SimpleDateFormat form = new SimpleDateFormat("dd/MM/yyyy");
			String hoje = form.format(new Date());
			
			
			s.append(ABRE_NEGRITO);
			s.append("CODIGO: "+compra.getId().toString()+ "\n");
			s.append(FECHA_NEGRITO);
			s.append("DATA DE EMISSAO: "+hoje+" AS "+hora+ "\n");
			s.append("EMITIDO POR: "+checkText(compra.getVendedor())+ "\n");
			s.append(quebra);				
			s.append("CLIENTE: "+checkText(compra.getCliente().getNome_razao())+ "\n");
			
			if(compra.getCliente().getEndereco_principal() != null){
				s.append("END.: "+checkText(compra.getCliente().getEndereco_principal().getEndereco())+","+checkText(compra.getCliente().getEndereco_principal().getNumero())+" - "+checkText(compra.getCliente().getEndereco_principal().getBairro())+" "+checkText(compra.getCliente().getEndereco_principal().getCidade())+"-"+checkText(compra.getCliente().getEndereco_principal().getUf())+ "\n");
			}else{
				s.append("CLIENTE SEM ENDEREÇO."+"\n");
			}
			
			s.append("FONE: ");
				if(compra.getCliente().getTelefone1() != null && !compra.getCliente().getTelefone1().equals("")){
					s.append("("+compra.getCliente().getDdd_fone1()+") "+compra.getCliente().getTelefone1());
				}
				
				if(compra.getCliente().getTelefone2() != null && !compra.getCliente().getTelefone2().equals("")){
					s.append("/ ("+compra.getCliente().getDdd_fone2()+") "+compra.getCliente().getTelefone2());
				}
				
				if(compra.getCliente().getCelular1() != null && !compra.getCliente().getCelular1().equals("")){
					s.append("/ ("+compra.getCliente().getDdd_cel1()+") "+compra.getCliente().getCelular1());
				}
				
				if(compra.getCliente().getCelular2() != null && !compra.getCliente().getCelular2().equals("")){
					s.append("/ ("+compra.getCliente().getDdd_cel2()+") "+compra.getCliente().getCelular2());
				}
			s.append("\n");
			
			
			
			
			s.append("ENTREGA: "+checkText(compra.getEntregar())+ "\n");
			
			s.append(quebra);
			s.append(quebra);
			s.append("-----------------------------------------------------------------\n");
			s.append(ABRE_NEGRITO);

			s.append("COD     DESCRICAO                               				    \n");			
			s.append("QTD     VALOR R$        TOTAL R$          GARANTIA\n");
			s.append(FECHA_NEGRITO);

			s.append("-----------------------------------------------------------------\n");
			
				List<EcfPreVendaDetalhe> itens = ItensCompraDAO.getItens(compra);
				for (EcfPreVendaDetalhe item : itens) {
					if(compra.getTipoVenda().equals("PRODUTO")){
						
						
			Produto p = ProdutoDAO.find(item.getProdutoId());
			
			s.append(p.getId().toString()+"     "+p.getNome()+"\n");
     		s.append(Real.formatDbToString(item.getQuantidade().toString())+"     "+Real.formatDbToString(item.getValorUnitario().toString())+"   "+Real.formatDbToString(item.getValorUnitario().toString())+"            "+p.getGarantia()+"\n");
					
					
					
					}else{
						
			Servico p = ServicoDAO.find(item.getProdutoId());
			
			s.append(p.getId().toString()+"     "+p.getDescricao()+"\n");
     		s.append(Real.formatDbToString(item.getQuantidade().toString())+"     "+Real.formatDbToString(item.getValorUnitario().toString())+"   "+Real.formatDbToString(item.getValorUnitario().toString())+"            "+"            NENHUMA\n");
     		
					}
				}
			
				
				if(compra.getTipo().equals("PEDIDO")){
				s.append(quebra);
				s.append("FORMA DE PGTO: "+compra.getFormaPagtoID().getNome()+ "\n");
				s.append("N. PARCELAS: 0\n");
				s.append(quebra);
				s.append("VALOR R$: "+Real.formatDbToString(compra.getSubTotal().toString())+ "\n");
				s.append("DESCONTO R$: "+Real.formatDbToString(compra.getTotal_desc().toString())+ "\n");
				s.append("ACRESCIMO R$: "+Real.formatDbToString(compra.getTotal_acres().toString())+ "\n");			
				s.append(quebra);
				s.append(ABRE_NEGRITO);
				s.append("TOTAL R$: "+Real.formatDbToString(compra.getValor().toString())+ "\n");
				s.append(FECHA_NEGRITO);
			}
			
			s.append(quebra);
			s.append(quebra);
			
			s.append(ABRE_CENTRALIZAR);	
			s.append("____________________________________________________\n");
			s.append(FECHA_CENTRALIZAR);	
			s.append(quebra);
			
			
			s.append(FECHA_CONDENSED);
			s.append(GUILHOTINA27);
			s.append(GUILHOTINA_TOTAL);
			
						
			return s.toString();
		
		}
		
		if(compra != null && !allow){
					
			Notify.Show("Imprimindo na Gráfica", Notify.TYPE_NOTICE);
			s.append(ABRE_CONDENSED);		
			
			
			
//			s.append(ABRE_CENTRALIZAR);		
			s.append(ABRE_NEGRITO);
			s.append(empresa.getNome_fantasia()+"\n");
			s.append(FECHA_NEGRITO);
//			s.append(FECHA_CENTRALIZAR);
			
//			s.append(ABRE_CENTRALIZAR);		
			s.append(empresa.getRazao_social()+"\n");
//			s.append(FECHA_CENTRALIZAR);			

			
//			s.append(ABRE_CENTRALIZAR);	
			s.append(empresa.getEndereco()+","+empresa.getNumero()+" "+empresa.getBairro()+"\n");
//			s.append(FECHA_CENTRALIZAR);			
			
//			s.append(ABRE_CENTRALIZAR);	
			s.append(empresa.getCidade()+","+empresa.getUf()+"\n");
//			s.append(FECHA_CENTRALIZAR);			
			
//			s.append(ABRE_CENTRALIZAR);	
			s.append("("+empresa.getDdd_fone1()+") "+empresa.getFone1()+"\n");
//			s.append(FECHA_CENTRALIZAR);		
			
//			s.append(ABRE_CENTRALIZAR);	
			s.append(empresa.getSite()+"\n");
//			s.append(FECHA_CENTRALIZAR);

			
			s.append(quebra);
//			s.append(quebra);
			
//			s.append(ABRE_CENTRALIZAR);	
			if(compra.getTipo().equals("PEDIDO")){
			s.append(ABRE_NEGRITO);
				s.append("        "+"PRE-VENDA \n");
			s.append(FECHA_NEGRITO);
			}else{
				s.append(ABRE_NEGRITO);
				s.append("        "+"ORCAMENTO \n");
				s.append(FECHA_NEGRITO);
			}
//			s.append(FECHA_CENTRALIZAR);
			
			s.append(quebra);
//			s.append(quebra);
			
			
			s.append("------------------------------------------------------------\n");
//			s.append(ABRE_CENTRALIZAR);
			s.append("NAO E DOCUMENTO FISCAL\n");
			s.append("NAO E VALIDO COMO RECIBO\n");
			s.append("NAO E VALIDO COMO GARANTIA DE MERCADORIA\n");
//			s.append(FECHA_CENTRALIZAR);
			s.append("------------------------------------------------------------\n");
			s.append(quebra);
			s.append(quebra);
			
			
			SimpleDateFormat formHora= new SimpleDateFormat("HH:mm:ss");
			String hora = formHora.format(new Date());
			
			SimpleDateFormat form = new SimpleDateFormat("dd/MM/yyyy");
			String hoje = form.format(new Date());
			 
			
			s.append(ABRE_NEGRITO);
			s.append("CODIGO: "+compra.getId().toString()+ "\n");
			s.append(FECHA_NEGRITO);
			s.append("DATA DE EMISSAO: "+hoje+" AS "+hora+ "\n");
			s.append("EMITIDO POR: "+compra.getVendedor()+ "\n");
			s.append(quebra);				
			s.append("CLIENTE: "+compra.getCliente().getNome_razao()+ "\n");
			
			if(compra.getCliente().getEndereco_principal() != null){
				s.append("END.: "+checkText(compra.getCliente().getEndereco_principal().getEndereco())+","+checkText(compra.getCliente().getEndereco_principal().getNumero())+" - "+checkText(compra.getCliente().getEndereco_principal().getBairro())+" "+checkText(compra.getCliente().getEndereco_principal().getCidade())+"-"+checkText(compra.getCliente().getEndereco_principal().getUf())+ "\n");
			}else{
				s.append("END.: Sem Endereço\n");
			}
			
			s.append("FONE: ");
				if(compra.getCliente().getTelefone1() != null && !compra.getCliente().getTelefone1().equals("")){
					s.append("("+compra.getCliente().getDdd_fone1()+") "+compra.getCliente().getTelefone1());
				}
				
				if(compra.getCliente().getTelefone2() != null && !compra.getCliente().getTelefone2().equals("")){
					s.append("/ ("+compra.getCliente().getDdd_fone2()+") "+compra.getCliente().getTelefone2());
				}
				
				if(compra.getCliente().getCelular1() != null && !compra.getCliente().getCelular1().equals("")){
					s.append("/ ("+compra.getCliente().getDdd_cel1()+") "+compra.getCliente().getCelular1());
				}
				
				if(compra.getCliente().getCelular2() != null && !compra.getCliente().getCelular2().equals("")){
					s.append("/ ("+compra.getCliente().getDdd_cel2()+") "+compra.getCliente().getCelular2());
				}
			s.append("\n");
			
			
			
			
			s.append("ENTREGA: "+compra.getEntregar()+ "\n");
			
			s.append(quebra);
			s.append(quebra);
			s.append("------------------------------------------------------------\n");
			s.append(ABRE_NEGRITO);

			s.append("COD     DESCRICAO                         \n");			
			s.append("QTD      VALOR R$        TOTAL R$          GARANTIA\n");
			s.append(FECHA_NEGRITO);

			s.append("------------------------------------------------------------\n");
			
				List<EcfPreVendaDetalhe> itens = ItensCompraDAO.getItens(compra);
				for (EcfPreVendaDetalhe item : itens) {
					if(compra.getTipoVenda().equals("PRODUTO")){						
						
							Produto p = ProdutoDAO.find(item.getProdutoId());
							
							s.append(p.getId().toString()+"     "+p.getNome()+"            \n");
				     		s.append(Real.formatDbToString(item.getQuantidade().toString())+"     "+Real.formatDbToString(item.getValorUnitario().toString())+"   "+Real.formatDbToString(item.getValorUnitario().toString())+"            "+p.getGarantia()+"\n");					
					}else{						
							Servico p = ServicoDAO.find(item.getProdutoId());
							
							s.append(p.getId().toString()+"     "+p.getDescricao()+"            \n");
				     		s.append(Real.formatDbToString(item.getQuantidade().toString())+"     "+Real.formatDbToString(item.getValorUnitario().toString())+"   "+Real.formatDbToString(item.getValorUnitario().toString())+"            "+"            NENHUMA\n");
					}
				}
			
			s.append(quebra);
			s.append("FORMA DE PGTO: "+compra.getFormaPagtoID().getNome()+ "\n");
			s.append("N. PARCELAS: 0\n");
			s.append(quebra);
			s.append("VALOR R$: "+Real.formatDbToString(compra.getSubTotal().toString())+ "\n");
			s.append("DESCONTO R$: "+Real.formatDbToString(compra.getTotal_desc().toString())+ "\n");
			s.append("ACRESCIMO R$: "+Real.formatDbToString(compra.getTotal_acres().toString())+ "\n");			
			s.append(quebra);
			s.append(ABRE_NEGRITO);
			s.append("TOTAL R$: "+Real.formatDbToString(compra.getValor().toString())+ "\n");
			s.append(FECHA_NEGRITO);
			
			s.append(quebra);
			s.append(quebra);
			
//			s.append(ABRE_CENTRALIZAR);	
			s.append("     ____________________________________________________\n");
//			s.append(FECHA_CENTRALIZAR);	
			s.append(quebra);
			
			
			s.append("------------------------------------------------------------\n");
//			s.append(ABRE_CENTRALIZAR);
			s.append("PRAZO MAXIMO DE 90 DIAS P/ RETIRADA DO MATERIAL SOLICITADO.\n");
			s.append("  APOS ESSE PRAZO, O MATERIAL SERA DESCARTADO.\n");			
//			s.append(FECHA_CENTRALIZAR);
			s.append("------------------------------------------------------------\n");
			s.append(quebra);
			s.append(quebra);
			s.append(quebra);
			s.append(quebra);
			s.append(quebra);
			s.append(quebra);
			s.append(quebra);
			s.append(quebra);
			s.append(quebra);
			
			s.append(FECHA_CONDENSED);
//			s.append(GUILHOTINA27);
//			s.append(GUILHOTINA_TOTAL);
			
						
			return s.toString();
		
		}
		
		return "ERRO";
		
		
	}
	
	public static boolean CancelarPedido(MovimentoEntCabecalho compra){
		
		EntityManager em = ConnUtil.getEntity();
		try{
			
			compra.setSituacao("C");
			
			
			List<MovimentoEntDetalhe> itens = getItensCompra(compra.getId());
			em.getTransaction().begin();
			
			
			
			if(itens != null){
				for (MovimentoEntDetalhe item : itens) {
					Produto p = ProdutoDAO.find(item.getProdutoId());
					if(p != null && item.getQuantidade() > new Float(0)){
						//-----Logg
						LogProdutoDAO.registraLogSemTransacao(new LogProduto(null,p, "CANCELAR PEDIDO COMPRA", p.getQtdEstoqueDeposito(),p.getQtdEstoqueDeposito()-item.getQuantidade(), new Date(), OpusERP4UI.getUsuarioLogadoUI().getUsername()));
						//-----Logg
						
						Float qtdOri = p.getQtdEstoqueDeposito()-item.getQuantidade();
						p.setQtdEstoqueDeposito(qtdOri);
						em.merge(p);
					}
					
					
					SeriaisCompraDAO.removeByItem(item);
				}
			}
			
			em.merge(compra);
			em.getTransaction().commit();
			return true;
		}catch(Exception e){
			e.printStackTrace();
			Notify.Show("ERRO: "+e.getLocalizedMessage(),Notify.TYPE_ERROR);		
			return false;
		}
		
	}
	
	
	private static String checkText(String s){
		if(s != null){
			return s;
		}
		
		return "";
	}
	
	
	
	public static List<MovimentoEntDetalhe> findByCabt(Integer codCab){
		EntityManager em = ConnUtil.getEntity();
		
		try{
			Query q = em.createQuery("select c from MovimentoEntDetalhe c where c.movimentoEntCabecalhoId =:codCab order by c.id desc ",MovimentoEntDetalhe.class);
			q.setParameter("codCab", codCab);
			List<MovimentoEntDetalhe> movDetalhe = null;
			
			if(q.getResultList().size() > 0){
				movDetalhe = q.getResultList();
			}
			return movDetalhe;
		}catch(Exception e){
            return null;
            
        }
		
	}
	
	public static MovimentoEntDetalhe findByCabProd(Integer codCab,Integer codProduto){
		EntityManager em = ConnUtil.getEntity();
		
		try{
			Query q = em.createQuery("select c from MovimentoEntDetalhe c where c.movimentoEntCabecalhoId =:codCab and c.produtoId =:codProduto order by c.id desc ",MovimentoEntDetalhe.class);
			q.setParameter("codCab", codCab);
			q.setParameter("codProduto", codProduto);
			MovimentoEntDetalhe movDetalhe = null;
			
			if(q.getResultList().size() == 1 && q.getSingleResult()!=null){
				movDetalhe = (MovimentoEntDetalhe) q.getSingleResult();
			}
			return movDetalhe;
		}catch(Exception e){
            return null;
            
        }
		
	}
	
	public static MovimentoEntDetalhe findByCod(Integer codDet){
		EntityManager em = ConnUtil.getEntity();
		
		try{
			Query q = em.createQuery("select c from MovimentoEntDetalhe c where c.id =:codDet order by c.id desc ",MovimentoEntDetalhe.class);
			q.setParameter("codDet", codDet);
			MovimentoEntDetalhe movDetalhe = null;
			
			if(q.getSingleResult()!=null){
				movDetalhe = (MovimentoEntDetalhe) q.getSingleResult();
			}
			return movDetalhe;
		}catch(Exception e){
            return null;
            
        }
		
	}
	
	
}


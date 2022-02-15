package com.digital.opuserp.dao;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.AlteracoesSerial;
import com.digital.opuserp.domain.EcfPreVendaCabecalho;
import com.digital.opuserp.domain.EcfPreVendaDetalhe;
import com.digital.opuserp.domain.Empresa;
import com.digital.opuserp.domain.Fornecedor;
import com.digital.opuserp.domain.HaverCab;
import com.digital.opuserp.domain.HaverDetalhe;
import com.digital.opuserp.domain.LogPedidos;
import com.digital.opuserp.domain.LogProduto;
import com.digital.opuserp.domain.MovimentoEntCabecalho;
import com.digital.opuserp.domain.MovimentoEntDetalhe;
import com.digital.opuserp.domain.Produto;
import com.digital.opuserp.domain.SeriaisPedido;
import com.digital.opuserp.domain.Servico;
import com.digital.opuserp.domain.TotaisPedido;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.Real;
import com.digital.opuserp.view.util.Notify;
import com.digital.opuserp.view.util.ParameterStringBuilder;

public class PedidoDAO {
	
	public static void removeItens(EcfPreVendaCabecalho pedido){
		
		for (EcfPreVendaDetalhe item : getItensPedido(pedido.getId())) {
			deleteItem(item);
		}
	}
	
	public static boolean deleteItem(EcfPreVendaDetalhe item){
		try{
			EntityManager em = ConnUtil.getEntity();
			em.getTransaction().begin();
			
			//Remove Seriais Vinculados
			
			
			SeriaisPedidoDAO.removeByItem(item);
			
			
			em.remove(item); 			
			em.getTransaction().commit();
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public static EcfPreVendaDetalhe findByCod(Integer codDet){
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createQuery("select c from EcfPreVendaDetalhe c where c.id =:codDet order by c.id desc ",EcfPreVendaDetalhe.class);
		q.setParameter("codDet", codDet);
		EcfPreVendaDetalhe ecfetalhe = null;
		
		if(q.getSingleResult()!=null){
			ecfetalhe = (EcfPreVendaDetalhe) q.getSingleResult();
		}
		return ecfetalhe;
		
	}
	
	public static EcfPreVendaDetalhe saveItem(EcfPreVendaDetalhe item){
		try{
			EntityManager em = ConnUtil.getEntity();
			
			
			Query q = em.createQuery("select e from EcfPreVendaDetalhe e where e.produtoId=:cod and e.ecfPreVendaCabecalhoId=:codPedido", EcfPreVendaDetalhe.class);
			q.setParameter("cod", item.getProdutoId());
			q.setParameter("codPedido", item.getEcfPreVendaCabecalhoId());
			
			if(q.getResultList().size() == 1){
				EcfPreVendaDetalhe produtoItem = (EcfPreVendaDetalhe)q.getSingleResult();
				produtoItem.setQuantidade(produtoItem.getQuantidade()+item.getQuantidade());
				Float vlr_item = item.getValorTotal() != null ? item.getValorTotal() : new Float(0);
				Float vlr_total = produtoItem.getValorTotal() != null ? produtoItem.getValorTotal() : new Float(0);
				produtoItem.setValorTotal(vlr_total + vlr_item);
								
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
	
	public static boolean save(EcfPreVendaCabecalho pedido){
		try{
			EntityManager em = ConnUtil.getEntity();
//			boolean usarTran = true;
//			if(em.getTransaction().isActive()){
//				usarTran = false;
//			}
//			
//			if(usarTran){
				em.getTransaction().begin();
			//}
			
			if(pedido.getId() == null){
				em.persist(pedido);
			}else{
				em.merge(pedido);
			}
			
			//if(usarTran){
				em.getTransaction().commit();
			//}
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		
	}
	
	public static EcfPreVendaCabecalho saveAndReturn(EcfPreVendaCabecalho pedido){
		try{
			if(pedido.getEmpresa_id() == null){
				pedido.setEmpresa_id(OpusERP4UI.getEmpresa().getId());
				pedido.setData(new Date());	
				pedido.setHora_pv(new Date());
				pedido.setSituacao("A");
				pedido.setVendedor(OpusERP4UI.getUsuarioLogadoUI().getUsername());
			}
			
			EntityManager em = ConnUtil.getEntity();
			em.getTransaction().begin();
			em.persist(pedido);
			em.getTransaction().commit();
			return pedido;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}		
	}

	public static boolean CancelarPedido(EcfPreVendaCabecalho pedido){
		
		try{
			EcfPreVendaCabecalho p2 = PedidoDAO.find(pedido.getId());
			
			if(p2.getSituacao().equals("C")){
				return true;
			}
			
			if(pedido != null && !p2.getSituacao().equals("C")){
				EntityManager em = ConnUtil.getEntity();
				
				pedido.setSituacao("C");
				
				List<EcfPreVendaDetalhe> itens = getItensPedido(pedido.getId());
				em.getTransaction().begin();
				
				if(pedido.getTipoVenda() == null || pedido.getTipoVenda().equals("PRODUTO") && pedido.getTipo().equals("DEVOLUCAO")){
					if(itens != null){
						for (EcfPreVendaDetalhe item : itens) {
							
							if(item.getCancelado().equals("N")){
							
								List<SeriaisPedido> serialPedido = null;
								serialPedido = SeriaisPedidoDAO.getSeriais(item);
	
								if(serialPedido != null ){					
									for (SeriaisPedido serialPed : serialPedido) {								
										serialPed.getSerial().setStatus("VENDIDO");
										serialPed.getSerial().setData_alteracao(new Date());
										em.persist(serialPed.getSerial());				
										//em.persist(new AlteracoesSerial(null, "PRE-VENDA ESTORNO "+item	.getEcfPreVendaCabecalhoId(), serialPed,OpusERP4UI.getEmpresa(), OpusERP4UI.getUsuarioLogadoUI(), new Date()));
										em.persist(new AlteracoesSerial(null, "PRE-VENDA ESTORNO "+item.getEcfPreVendaCabecalhoId(), serialPed.getSerial(),OpusERP4UI.getEmpresa(), OpusERP4UI.getUsuarioLogadoUI(), new Date()));
									}																				
								}
								
								Produto p = ProdutoDAO.find(item.getProdutoId());
								if(p != null && item.getQuantidade() > new Float(0)){
									
									//-----Logg
									LogProdutoDAO.registraLogSemTransacao(new LogProduto(null,p, "CANCELAR PEDIDO SAIDA",p.getQtdEstoque(),p.getQtdEstoque()-item.getQuantidade(), new Date(), OpusERP4UI.getUsuarioLogadoUI().getUsername()));
									//-----Logg
									
									Float qtdOri = p.getQtdEstoque()-item.getQuantidade();
									p.setQtdEstoque(qtdOri);
									em.merge(p);
								}
								
								//HAVER				
								HaverCab haver = HaverDAO.buscarHaver(pedido.getCliente());														
								if(haver != null){
//									haver.setValor_total(haver.getValor_total()-pedido.getValor());
//									if(haver.getValor_total() <= 0){
//										haver.setStatus("UTILIZADO");
//									}else{
//										haver.setStatus("DISPONIVEL");
//									}
									em.merge(haver);
								}
								
								Query q = em.createQuery("select  h from HaverDetalhe h where h.n_doc=:n_doc", HaverDetalhe.class);
								q.setParameter("n_doc", pedido.getId().toString());
								
							    for (HaverDetalhe haver_det : (List<HaverDetalhe>)q.getResultList()) {
									haver_det.setStatus("INATIVO");
							    	em.merge(haver_det);
								}
								
								List<EcfPreVendaDetalhe> itens_saida = getItensPedido(pedido.getN_nf_devolucao().getId());
								for (EcfPreVendaDetalhe ecfPreVendaDetalhe : itens_saida) {
									if(ecfPreVendaDetalhe.getProdutoId().equals(item.getProdutoId())){
										ecfPreVendaDetalhe.setCancelado("N");
										em.merge(ecfPreVendaDetalhe);
									}
								}
								
							}
							//SeriaisPedidoDAO.removeByItem(item);
							
							
							
							
						}
					}
				}
				
				if(pedido.getTipoVenda() == null || pedido.getTipoVenda().equals("PRODUTO") && pedido.getTipo().equals("PEDIDO") && pedido.getNaturezaOperacao().getTipo().equals("SAIDA")){
					if(itens != null){
						for (EcfPreVendaDetalhe item : itens) {
							
							if(item.getCancelado().equals("N")){
							
								List<SeriaisPedido> serialPedido = null;
								serialPedido = SeriaisPedidoDAO.getSeriais(item);
	
								if(serialPedido != null ){					
									for (SeriaisPedido serialPed : serialPedido) {								
										serialPed.getSerial().setStatus("ATIVO");
										serialPed.getSerial().setData_alteracao(new Date());
										em.persist(serialPed.getSerial());				
										//em.persist(new AlteracoesSerial(null, "PRE-VENDA ESTORNO "+item	.getEcfPreVendaCabecalhoId(), serialPed,OpusERP4UI.getEmpresa(), OpusERP4UI.getUsuarioLogadoUI(), new Date()));
										em.persist(new AlteracoesSerial(null, "PRE-VENDA ESTORNO "+item.getEcfPreVendaCabecalhoId(), serialPed.getSerial(),OpusERP4UI.getEmpresa(), OpusERP4UI.getUsuarioLogadoUI(), new Date()));
									}																				
								}
								
								Produto p = ProdutoDAO.find(item.getProdutoId());
								if(p != null && item.getQuantidade() > new Float(0)){
									
									//-----Logg
									LogProdutoDAO.registraLogSemTransacao(new LogProduto(null,p, "CANCELAR PEDIDO SAIDA",p.getQtdEstoque(),p.getQtdEstoque()+item.getQuantidade(), new Date(), OpusERP4UI.getUsuarioLogadoUI().getUsername()));
									//-----Logg
									
									Float qtdOri = p.getQtdEstoque()+item.getQuantidade();
									p.setQtdEstoque(qtdOri);
									em.merge(p);
									
									if(p.getCod_pro_loja() != null && OpusERP4UI.getEmpresa().getId() != 6 && OpusERP4UI.getEmpresa().getId() != 4){
										sincronizarEstoque(p.getCod_pro_loja(), p.getQtdEstoque().toString());
									}
								}
							
							}
							//SeriaisPedidoDAO.removeByItem(item);
							
								
						}
					}

					HaverCab haver = HaverDAO.buscarHaver(pedido.getCliente());
//					List<TotaisPedido> totais = TotaisPedidoDAO.getTotais(pedido);
//					for (TotaisPedido totaisPedido : totais) {
//						if(totaisPedido.getHaver() != null && totaisPedido.getHaver().equals(haver)){										
//							haver.setValor_total(haver.getValor_total()+totaisPedido.getValor());
//							em.merge(haver);
//						}
//					}
					
					
				}
				
							
				Query q = em.createQuery("select  h from HaverDetalhe h where h.n_doc=:n_doc", HaverDetalhe.class);
				q.setParameter("n_doc", pedido.getId().toString());
				
			    for (HaverDetalhe haver_det : (List<HaverDetalhe>)q.getResultList()) {
					haver_det.setStatus("INATIVO");
			    	em.merge(haver_det);
				}
				
				em.merge(pedido);
				
				em.persist(new LogPedidos(null, pedido, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "CANCELOU PEDIDO", new Date()));
				
				em.getTransaction().commit();
				
				
				for (EcfPreVendaDetalhe item : itens) {
						Fornecedor Fornecedor = FornecedorDAO.findbyCnpjEmp(OpusERP4UI.getEmpresa().getCnpj(),OpusERP4UI.getEmpresa().getId());
						
						if(Fornecedor == null){
							if(OpusERP4UI.getEmpresa().getId().equals(1)){
								Fornecedor = new Fornecedor(840);
							}else if(OpusERP4UI.getEmpresa().getId().equals(5)){
								Fornecedor = new Fornecedor(913);
							}
						}
						
						MovimentoEntCabecalho compra = new MovimentoEntCabecalho();
						MovimentoEntDetalhe contaDetalhe = new MovimentoEntDetalhe();
						Integer CodId = Integer.parseInt(CompraDAO.getNextID());
						
						compra.setId(CodId); 
						compra.setEmpresa_id(OpusERP4UI.getEmpresa().getId());
						compra.setCodNf(0);
						compra.setFornecedor(Fornecedor);
						compra.setQtdItens(new Float(0));
						compra.setValorTotal(new Float(0));
						compra.setDataHora(new Date());
						compra.setUsuario(OpusERP4UI.getUsuarioLogadoUI().getUsername());
						compra.setSituacao("F");
						compra.setTipo("CANCELAMENTO");
						
						contaDetalhe.setMovimentoEntCabecalhoId(compra.getId());
						contaDetalhe.setProdutoId(item.getProdutoId());
						contaDetalhe.setQuantidade(item.getQuantidade());
						
						CompraDAO.save(compra);
						CompraDAO.saveItem(contaDetalhe);	
				}
				
				
				
				return true;
			}else{
				return true;
			}
		}catch(Exception e){
			e.printStackTrace();
			Notify.Show("ERRO: "+e.getLocalizedMessage(),Notify.TYPE_ERROR);		
			return false;
		}
		
	}
	
	public static void sincronizarEstoque(String cod_loja, String qtdEstoque){
		try{
			URL url = new URL("http://179.127.32.7:8989/testeAPI.php");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			String qtd = qtdEstoque.split("\\.")[0];
			
			Map<String, Object> parameters = new HashMap<>();
			parameters.put("funcao", "sicronizarEstoque");
			parameters.put("codProduto", cod_loja);
			parameters.put("qtdEstoque", qtd); 
			
			conn.setDoOutput(true);
			DataOutputStream out = new DataOutputStream(conn.getOutputStream());
			out.writeBytes(ParameterStringBuilder.getParamsString(parameters));
			out.flush();
			out.close();
			
			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				//Notify.Show("Erro:"+conn.getResponseMessage(), Notify.TYPE_ERROR);
				throw new RuntimeException("Erro ao acessar o webservice : " + conn.getResponseMessage());
			}
	
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
	
			String retorno;											
			//String cod_loja = null;
			while ((retorno = br.readLine()) != null) {
				
				//JSONObject jsonObject = new JSONObject("{"+retorno.split("[{]+")[1]);	
				//cod_loja = String.valueOf((Integer)jsonObject.get("id"));		
				//Notify.Show(retorno, Notify.TYPE_NOTICE); 
				System.out.println(retorno); 
			}
	
			conn.disconnect();
			
			//return cod_loja;
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static EcfPreVendaCabecalho getNextID(){
		
		
		return saveAndReturn(new EcfPreVendaCabecalho());
		
		
//		try{
//			EntityManager em = ConnUtil.getEntityManager();	
//			Query q = em.createNativeQuery("SHOW TABLE STATUS LIKE 'ecf_pre_venda_cabecalho'");	
//			Object result = q.getSingleResult();
//			Object[] ob = (Object[]) result;
//			String cod = ob[10].toString();
//			
//			return cod;
//		}catch (Exception e){
//			e.printStackTrace();
//			System.out.println("Erro ao tentar pegar o próximo ID do Pedido: "+e.getMessage());
//			return null;
//		}
	}
	
	public static boolean foi_usada_em_devolucao(EcfPreVendaCabecalho pedido){
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createQuery("select p from EcfPreVendaCabecalho p where p.n_nf_devolucao =:pedido and p.situacao != 'C'", EcfPreVendaCabecalho.class);
		q.setParameter("pedido", pedido);
		
		if(q.getResultList().size() > 0){
			return true;			
		}
		
		return false;
	}
	
	
	public static EcfPreVendaCabecalho findTypePedido(Integer cod){
		try{
			EntityManager em = ConnUtil.getEntity();
			Query q  = em.createQuery("select p from EcfPreVendaCabecalho p where p.id =:codPedido and p.tipo ='PEDIDO' ", EcfPreVendaCabecalho.class);
			q.setParameter("codPedido", cod);
			//q.setParameter("cliente", new Cliente(0));
			
			if(q.getResultList().size() == 1){
				return (EcfPreVendaCabecalho)q.getSingleResult();
			}
			
			return null;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	
	public static EcfPreVendaCabecalho find(Integer cod){
		try{
			EntityManager em = ConnUtil.getEntity();
			Query q  = em.createQuery("select p from EcfPreVendaCabecalho p where p.id =:codPedido", EcfPreVendaCabecalho.class);
			q.setParameter("codPedido", cod);
			//q.setParameter("cliente", new Cliente(0));
			
			if(q.getResultList().size() == 1){
				return (EcfPreVendaCabecalho)q.getSingleResult();
			}
			
			return null;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public static EcfPreVendaCabecalho find2(Integer cod){
		try{
			EntityManager em = ConnUtil.getEntity();
			return  em.find(EcfPreVendaCabecalho.class, cod);
			
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public static List<EcfPreVendaDetalhe> getItensPedido(Integer cod){
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createQuery("select epd from EcfPreVendaDetalhe epd where epd.ecfPreVendaCabecalhoId =:codPedido", EcfPreVendaDetalhe.class);
		q.setParameter("codPedido", cod);
		
		
		return q.getResultList();
	}
	
	public static List<EcfPreVendaDetalhe> getItensPedidoAtivos(Integer cod){
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createQuery("select epd from EcfPreVendaDetalhe epd where epd.ecfPreVendaCabecalhoId =:codPedido and epd.cancelado ='N'", EcfPreVendaDetalhe.class);
		q.setParameter("codPedido", cod);
		
		return q.getResultList();
	}
	
	public static String getLayoutPedido(EcfPreVendaCabecalho pedido, boolean allow){
		
		StringBuilder s = new StringBuilder();
		
		String ABRE_CENTRALIZAR = new String(new char[]{27,97,1});
		String FECHA_CENTRALIZAR = new String(new char[]{27,97,0});
		
		String ABRE_NEGRITO = new String(new char[]{27,69});
		String FECHA_NEGRITO = new String(new char[]{27,70});
		
		//String ABRE_CONDENSED = new String(new char[]{15});
		//String FECHA_CONDENSED = new String(new char[]{18,10});
		
		char GUILHOTINA27 = 27;
		char GUILHOTINA_TOTAL = 119;
		char GUILHOTINA_PARCIAL = 109;
		
		char QUINZE = 15;
		char DEZOITO = 18;
		char TREZE = 13;
		char DEZ = 10;
		
		Empresa empresa = OpusERP4UI.getEmpresa();
		
		String quebra = "\n";
		
		if(pedido != null && allow){
					
			s.append(QUINZE);		
			
			s.append("           d i g i t a l \n");
			s.append("      Rua Adjar Maciel, 35 Centro\n");
			s.append("    Belo Jardim/PE CEP: 55.150-040\n");
			s.append("       CNPJ: 07.578.965/0001-05 \n");
			s.append("           IE: 18.3.050.0329 \n");
			s.append("       www.digitalonline.com.br \n");
			s.append("          Fone: (81)3726.3125\n");

			s.append(quebra);
			
				
			if(pedido.getTipo().equals("PEDIDO")){
		;
				s.append("              PRE-VENDA \n");
		
			}else{
			
				s.append("       ORCAMENTO \n");
			
			}
			
			
			s.append(quebra);
					
			s.append("---------------------------------------\n");
			;
			s.append("NAO E DOCUMENTO FISCAL\n");
			s.append("NAO E VALIDO COMO RECIBO\n");
			s.append("NAO E VALIDO COMO GARANTIA DE MERCADORIA\n");
			
			s.append("---------------------------------------\n");
			s.append(quebra);
						
			
			SimpleDateFormat formHora= new SimpleDateFormat("HH:mm:ss");
			String hora = formHora.format(pedido.getHora_pv());
			
			SimpleDateFormat form = new SimpleDateFormat("dd/MM/yyyy");
			String hoje = form.format(pedido.getData());
			String cliente = pedido.getCliente() != null ? pedido.getCliente().getNome_razao() : "NENHUM CLIENTE VINCULADO";
			String vendedor = pedido.getVendedor() != null ? pedido.getVendedor() : "NENHUM VENDEDOR";
			String comprador = pedido.getComprador() != null ? pedido.getComprador() : "NENHUM COMPRADOR INFORMADO";
			
			
			s.append("CODIGO: "+pedido.getId().toString()+ "\n");
			
			s.append("NATUREZA OPERACAO: "+pedido.getNaturezaOperacao().getDescricao()+ "\n");
			s.append("DATA DE EMISSAO: "+hoje+" AS "+hora+ "\n");
			s.append("EMITIDO POR: "+vendedor+ "\n");
			s.append(quebra);				
			s.append("CLIENTE: "+cliente+ "\n");
			s.append("COMPRADOR: "+comprador+ "\n");
			
			if(pedido.getCliente() != null && pedido.getCliente().getEndereco_principal() != null){
				s.append("END.: "+checkText(pedido.getCliente().getEndereco_principal().getEndereco())+","+checkText(pedido.getCliente().getEndereco_principal().getNumero())+" - "+checkText(pedido.getCliente().getEndereco_principal().getBairro())+" "+checkText(pedido.getCliente().getEndereco_principal().getCidade())+"-"+checkText(pedido.getCliente().getEndereco_principal().getUf())+ "\n");
			}else{
				s.append("SEM CLIENTE OU ENDEREÇO VINCULADO."+"\n");
			}
			
			if(pedido.getCliente() != null){
				s.append("FONE: ");
					if(pedido.getCliente().getTelefone1() != null && !pedido.getCliente().getTelefone1().equals("")){
						s.append("("+pedido.getCliente().getDdd_fone1()+") "+pedido.getCliente().getTelefone1());
					}
					
					if(pedido.getCliente().getTelefone2() != null && !pedido.getCliente().getTelefone2().equals("")){
						s.append("/ ("+pedido.getCliente().getDdd_fone2()+") "+pedido.getCliente().getTelefone2());
					}
					
					if(pedido.getCliente().getCelular1() != null && !pedido.getCliente().getCelular1().equals("")){
						s.append("/ ("+pedido.getCliente().getDdd_cel1()+") "+pedido.getCliente().getCelular1());
					}
					
					if(pedido.getCliente().getCelular2() != null && !pedido.getCliente().getCelular2().equals("")){
						s.append("/ ("+pedido.getCliente().getDdd_cel2()+") "+pedido.getCliente().getCelular2());
					}
				s.append("\n");
			}
			
			
			
			s.append("ENTREGA: "+checkText(pedido.getEntregar())+ "\n");
			
			s.append(quebra);			
			s.append("---------------------------------------\n");
			

			s.append("COD     DESCRICAO\n");			
			s.append("QTD     VALOR R$        TOTAL R$          GARANTIA\n");
			

			s.append("---------------------------------------\n");
			
				List<EcfPreVendaDetalhe> itens = ItensPedidoDAO.getItens(pedido);
				double totalItens = 0;
				for (EcfPreVendaDetalhe item : itens) {
					if(pedido.getTipoVenda().equals("PRODUTO")){
						
						
			Produto p = ProdutoDAO.find(item.getProdutoId());
			
			s.append(p.getId().toString()+"     "+p.getNome()+"\n");
     		s.append(Real.formatDbToString(item.getQuantidade().toString())+"     "+Real.formatDbToString(item.getValorUnitario().toString())+"            "+Real.formatDbToString(item.getValorTotal().toString())+"             "+p.getGarantia()+"\n");
     		totalItens = totalItens+item.getValorTotal();
					
					
					}else{
						
			Servico p = ServicoDAO.find(item.getProdutoId());
			
			s.append(p.getId().toString()+"     "+p.getNome()+"\n");
     		s.append(Real.formatDbToString(item.getQuantidade().toString())+"     "+Real.formatDbToString(item.getValorUnitario().toString())+"            "+Real.formatDbToString(item.getValorTotal().toString())+"      "+"         NENHUMA\n");
     		
					}
				}
				s.append("---------------------------------------\n");
				s.append(String.format("%29s",Real.formatDbToString(String.valueOf(totalItens))));	
				
				
			//COLOCAR OS SERIAIS
				s.append(quebra);
				for (EcfPreVendaDetalhe item : itens) {
					List<SeriaisPedido> serial = SeriaisPedidoDAO.getSeriais(item);
					if(serial.size()!= 0){
						s.append(item.getProdutoId()+" SN: ");						
					}
					
					Integer i=0;
					for (SeriaisPedido seriaisPedido : serial) {
						if(i.equals(0)){						
							s.append(seriaisPedido.getSerial().getSerial());
						}else{
							s.append(","+seriaisPedido.getSerial().getSerial());
						}
						i++;
					}
					s.append("\n");
				}
				
				
			if(pedido.getTipo().equals("PEDIDO") || pedido.getTipo().equals("DEVOLUCAO")){
				s.append(quebra);
				
				s.append("ACRESCIMO (R$): "+Real.formatDbToString(pedido.getTotal_acres().toString())+ "\n");			
				s.append("SUBTOTAL (R$): "+Real.formatDbToString(String.valueOf(pedido.getSubTotal()+pedido.getTotal_acres()))+ "\n");
				s.append(quebra);
				s.append("DESCONTO (%): "+Real.formatDbToString(pedido.getTotal_desc().toString())+ "\n");
				
				s.append("TOTAL (R$): "+Real.formatDbToString(pedido.getValor().toString())+ "\n");
				
				
				s.append(quebra);
				List<TotaisPedido> totais = TotaisPedidoDAO.getTotais(pedido);
				for (TotaisPedido totaisPedido : totais) {
					
					if(totaisPedido.getForma_pgto() != null){
						if(totaisPedido.getForma_pgto().getNome().equals("BOLETO") || totaisPedido.getForma_pgto().getNome().equals("CARTAO CREDITO")){
							s.append("FORMA DE PGTO: "+totaisPedido.getForma_pgto().getNome()+ " : "+Real.formatDbToString(String.valueOf(totaisPedido.getValor()))+" / "+String.valueOf(totaisPedido.getParcelas())+"\n"); 
						}else{
							
							if(totaisPedido.getForma_pgto() != null){
								s.append("FORMA DE PGTO: "+totaisPedido.getForma_pgto().getNome()+ " : "+Real.formatDbToString(String.valueOf(totaisPedido.getValor()))+"\n");
							}
							
						}
					}else{
						if(totaisPedido.getHaver() != null){
							s.append("FORMA DE PGTO: HAVER: "+Real.formatDbToString(String.valueOf(totaisPedido.getValor()))+"\n");
						}
					}
				}
				
				s.append(quebra);
				s.append("OBS.: "+pedido.getObs()+ "\n");
			}
			
			s.append(quebra);
			s.append(quebra);
			s.append(quebra);
			s.append(quebra);
			
			
				
			s.append("      __________________________\n");		
			s.append(quebra);
				
			
			s.append(DEZOITO+TREZE+DEZ);			
			s.append(GUILHOTINA27);
			s.append(GUILHOTINA_TOTAL);
			
						
			return s.toString();
		
		}
		
		if(pedido != null && !allow){
					
			Notify.Show("Imprimindo na Gráfica", Notify.TYPE_NOTICE);
			s.append(QUINZE);		
			
			
			
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
			if(pedido.getTipo().equals("PEDIDO")){
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
			String cliente = pedido.getCliente() != null ? pedido.getCliente().getNome_razao() : "NENHUM CLIENTE VINCULADO";
			String vendedor = pedido.getVendedor() != null ? pedido.getVendedor() : "NENHUM VENDEDOR";
			String comprador = pedido.getComprador() != null ? pedido.getComprador() : "NENHUM COMPRADOR INFORMADO";
			
			s.append(ABRE_NEGRITO);
			s.append("CODIGO: "+pedido.getId().toString()+ "\n");
			s.append(FECHA_NEGRITO);
			s.append("DATA DE EMISSAO: "+hoje+" AS "+hora+ "\n");
			s.append("EMITIDO POR: "+vendedor+ "\n");
			s.append(quebra);				
			s.append("CLIENTE: "+cliente+ "\n");
			s.append("COMPRADOR: "+comprador+ "\n");
			
			if(pedido.getCliente() !=null && pedido.getCliente().getEndereco_principal() != null){
				s.append("END.: "+checkText(pedido.getCliente().getEndereco_principal().getEndereco())+","+checkText(pedido.getCliente().getEndereco_principal().getNumero())+" - "+checkText(pedido.getCliente().getEndereco_principal().getBairro())+" "+checkText(pedido.getCliente().getEndereco_principal().getCidade())+"-"+checkText(pedido.getCliente().getEndereco_principal().getUf())+ "\n");
			}else{
				s.append("END.: Sem Endereço\n");
			}
			
			if(pedido.getCliente() != null){
				s.append("FONE: ");
					if(pedido.getCliente().getTelefone1() != null && !pedido.getCliente().getTelefone1().equals("")){
						s.append("("+pedido.getCliente().getDdd_fone1()+") "+pedido.getCliente().getTelefone1());
					}
					
					if(pedido.getCliente().getTelefone2() != null && !pedido.getCliente().getTelefone2().equals("")){
						s.append("/ ("+pedido.getCliente().getDdd_fone2()+") "+pedido.getCliente().getTelefone2());
					}
					
					if(pedido.getCliente().getCelular1() != null && !pedido.getCliente().getCelular1().equals("")){
						s.append("/ ("+pedido.getCliente().getDdd_cel1()+") "+pedido.getCliente().getCelular1());
					}
					
					if(pedido.getCliente().getCelular2() != null && !pedido.getCliente().getCelular2().equals("")){
						s.append("/ ("+pedido.getCliente().getDdd_cel2()+") "+pedido.getCliente().getCelular2());
					}
				s.append("\n");
			}
			
			
			
			s.append("ENTREGA: "+pedido.getEntregar()+ "\n");
			
			s.append(quebra);
			s.append(quebra);
			s.append("------------------------------------------------------------\n");
			s.append(ABRE_NEGRITO);

			s.append("COD     DESCRICAO                         \n");			
			s.append("QTD      VALOR R$        TOTAL R$          GARANTIA\n");
			s.append(FECHA_NEGRITO);

			s.append("------------------------------------------------------------\n");
			
				List<EcfPreVendaDetalhe> itens = ItensPedidoDAO.getItens(pedido);
				for (EcfPreVendaDetalhe item : itens) {
					if(pedido.getTipoVenda().equals("PRODUTO")){						
						
							Produto p = ProdutoDAO.find(item.getProdutoId());
							
							s.append(p.getId().toString()+"     "+p.getNome()+"            \n");
				     		s.append(Real.formatDbToString(item.getQuantidade().toString())+"     "+Real.formatDbToString(item.getValorUnitario().toString())+"        "+Real.formatDbToString(item.getValorTotal().toString())+"          "+p.getGarantia()+"\n");					
					}else{						
							Servico p = ServicoDAO.find(item.getProdutoId());
							
							s.append(p.getId().toString()+"     "+p.getNome()+"            \n");
				     		s.append(Real.formatDbToString(item.getQuantidade().toString())+"     "+Real.formatDbToString(item.getValorUnitario().toString())+"        "+Real.formatDbToString(item.getValorTotal().toString())+"     "+"     NENHUMA\n");
					}
				}
			
			s.append(quebra);
			List<TotaisPedido> totais = TotaisPedidoDAO.getTotais(pedido);
			for (TotaisPedido totaisPedido : totais) {
				s.append("FORMA DE PGTO: "+totaisPedido.getForma_pgto().getNome()+ " : "+Real.formatDbToString(String.valueOf(totaisPedido.getValor()))+"\n");					
			}
			
			
			s.append("VALOR R$: "+Real.formatDbToString(pedido.getSubTotal().toString())+ "\n");
			s.append("DESCONTO R$: "+Real.formatDbToString(pedido.getTotal_desc().toString())+ "\n");
			s.append("ACRESCIMO R$: "+Real.formatDbToString(pedido.getTotal_acres().toString())+ "\n");			
			s.append(quebra);
			s.append(ABRE_NEGRITO);
			s.append("TOTAL R$: "+Real.formatDbToString(pedido.getValor().toString())+ "\n");
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
			
			
			s.append(DEZOITO+TREZE+DEZ);			
//			s.append(GUILHOTINA27);
//			s.append(GUILHOTINA_TOTAL);
			
						
			return s.toString();
		
		}
		
		return "ERRO";
		
		
	}
	
	
	private static String checkText(String s){
		if(s != null){
			return s;
		}
		
		return "";
	}
}


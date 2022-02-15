package com.digital.opuserp.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.hibernate.LockMode;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.Months;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.AcessoCliente;
import com.digital.opuserp.domain.AlteracoesSerial;
import com.digital.opuserp.domain.AlteracoesContrato;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.Concentrador;
import com.digital.opuserp.domain.ContasReceber;
import com.digital.opuserp.domain.Endereco;
import com.digital.opuserp.domain.FiltroAcesso;
import com.digital.opuserp.domain.IndicacoesCliente;
import com.digital.opuserp.domain.LogAcoes;
import com.digital.opuserp.domain.LogProduto;
import com.digital.opuserp.domain.NfeMestre;
import com.digital.opuserp.domain.PlanoAcesso;
import com.digital.opuserp.domain.Produto;
import com.digital.opuserp.domain.RadCheck;
import com.digital.opuserp.domain.RadReply;
import com.digital.opuserp.domain.RadUserGgroup;
import com.digital.opuserp.domain.SerialProduto;
import com.digital.opuserp.domain.Swith;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.MikrotikUtil;
import com.digital.opuserp.util.Real;
import com.digital.opuserp.view.util.Notify;


public class AcessoDAO {
	
	
	public static boolean checkVinculoCaixaAtendimento(AcessoCliente contrato){
		
		try{
			EntityManager em = ConnUtil.getEntity();
			Query q = em.createQuery("select s from Swith s where s.contrato_monitoramento =:c", Swith.class);
			q.setParameter("c", contrato);
			
			if(q.getResultList().size() > 0){
				return true;
			}else{
				return false;
			}
			
		}catch(Exception e){
			e.printStackTrace();
			return true;
		}
		
	}
	
	public static boolean save(AcessoCliente contrato){
		try{
			
			EntityManager em = ConnUtil.getEntity();
			em.getTransaction().begin();
			em.merge(contrato);
			em.getTransaction().commit();
			
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	

	/**
	 * 
	 * @param contrato
	 * @param dataPrimeiroBoleto
	 * @param valorPrimeiroBoleto
	 * @return true se concluir com êxito
	 * 
	 * Ações
	 * 	1- Cadastra novo Acesso Cliente
	 * 	2- Gera boletos
	 * 	3- Muda status do cliente para ATIVO
	 * 	4- Gera logs
	 */
	public static boolean novoCadastro(AcessoCliente c){
		EntityManager em = ConnUtil.getEntity();
		try {
			
			//----nfe
			c.setEmitir_nfe("NFE-MOD21");
			c.setEmitir_nfe_automatico("SIM");
			c.setEmitir_nfe_c_boleto_aberto("SIM");
			c.setCfop_nfe(5307);
			//---nfe
			
			c.setValor_beneficio_adesao(Real.formatStringToDBDouble(c.getContrato().getValor_adesao()));
			c.setValor_beneficio_comodato(Real.formatStringToDBDouble(c.getContrato().getValor_equipamento()));
			c.setIsencao_taxa_instalacao(Real.formatStringToDBDouble(c.getContrato().getIsencao_taxa_instalacao()));
			c.setIsencao_prest_serv_manutencao(Real.formatStringToDBDouble(c.getContrato().getIsencao_prest_serv_manutencao()));
			
			c.setStatus_2("PENDENTE_INSTALACAO");
			c.setEmpresa_id(OpusERP4UI.getEmpresa().getId());
			c.setCarencia(c.getContrato().getCarencia());
			
			if(!c.getContrato().getTipo_contrato().equals("PRE-PAGO")){
				Date dataVencContrato = CredenciaisAcessoDAO.calcularDataVencContrato(c.getContrato().getId(), new Date());				
				c.setData_venc_contrato(dataVencContrato);
			}else{
				c.setData_venc_contrato(new Date());
			}
			
			//COMODATO BENEFICIO
			if(c.getRegime().equals("COMODATO")){
				c.setBeneficio_comodato(true);
			}else{
				c.setBeneficio_comodato(false);
			}
			
			Query qIndicacao = em.createQuery("select i from IndicacoesCliente i where i.cliente_indicado=:c", IndicacoesCliente.class);
			qIndicacao.setParameter("c", c.getCliente());
			
			IndicacoesCliente indicacaoCliente = null;
			if(qIndicacao.getResultList().size()==1){
				indicacaoCliente = (IndicacoesCliente)qIndicacao.getSingleResult();
			}
												
			em.getTransaction().begin();											
								
				em.persist(c);		
			
//				if(!c.getContrato().getTipo_contrato().equals("GRATIS")){				
//					CredenciaisAcessoDAO.gerarBoletosAcesso(c.getCliente().getId(),c,c.getContrato(), c.getPlano(), dataPrimeiroBoleto,valorPrimeiroBoleto);
//				}			
				
				ClienteDAO.changeStatusFromActive(c.getCliente());
				if(indicacaoCliente != null){
					indicacaoCliente.setStatus("APROVADO");
					em.merge(indicacaoCliente);
				}
				
			em.getTransaction().commit();
			
			LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Cadastrou Um Novo Contrato de Acesso | COD: "+c.getId().toString()));
			AlteracoesContratoDAO.save(new AlteracoesContrato(null, "CADASTRO DE CONTRATO", c, OpusERP4UI.getUsuarioLogadoUI(), new Date()));
			
			return true;
		} catch (Exception e) {
			if(em.getTransaction().isActive()){
				em.getTransaction().rollback();																	
			}
			e.printStackTrace();			
			
			LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Não Conseguiu Cadastrar Um Novo Contrato de Acesso, Uma Excessão foi Gerada"));
			
			return false;
		}
	}
	
	
	
	public static boolean liberarCartaoCliente(AcessoCliente c,String codigo_antigo, String codigo_novo){
		
		EntityManager em = ConnUtil.getEntity();
		
		try{			
			em.getTransaction().begin();
			
			c.setCodigo_cartao(codigo_novo);
			em.merge(c);
						
			List<ContasReceber> boletos = ContasReceberDAO.procurarTodosBoletosDoAcessoPorContrato(c.getId());

			if(boletos != null){
				for (ContasReceber contasReceber : boletos) {
					contasReceber.setCodigo_cartao(codigo_novo);					
					em.merge(contasReceber);
				}
			}
			
			em.getTransaction().commit();
						
			return true;
		}catch(Exception e){
			e.printStackTrace();
			
			return false;
		}
		
	}
	
	public static boolean liberarBoletos(AcessoCliente c, Date dataPrimeiroBoleto, String valorPrimeiroBoleto){
		EntityManager em = ConnUtil.getEntity();
		try {
														
			em.getTransaction().begin();										
						
				em.merge(c);		 
			
				if(!c.getContrato().getTipo_contrato().equals("GRATIS")){				
					
					List<ContasReceber> boletos = ContasReceberDAO.procurarAbertoBoletosDoAcessoPorContrato(c.getId());
					
					if(boletos != null){
						for (ContasReceber contasReceber : boletos) {
							contasReceber.setStatus("EXCLUIDO");
							em.merge(contasReceber); 
						}					
					}
					CredenciaisAcessoDAO.gerarBoletosAcesso(c.getCliente().getId(),c,c.getContrato(), c.getPlano(), dataPrimeiroBoleto,valorPrimeiroBoleto,"LIBERAR_BOLETO");
				}			
								
			em.getTransaction().commit();
			
			LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Liberou os boletos | COD: "+c.getId().toString()));
			AlteracoesContratoDAO.save(new AlteracoesContrato(null, "LIBERACAO DE BOLETOS", c, OpusERP4UI.getUsuarioLogadoUI(), new Date()));
			
			return true;
		} catch (Exception e) {
			if(em.getTransaction().isActive()){
				em.getTransaction().rollback();																	
			}
			e.printStackTrace();			
			
			LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Não Conseguiu Cadastrar Um Novo Contrato de Acesso, Uma Excessão foi Gerada"));
			
			return false;
		}
	}

	/**
	 * 
	 * @param contrato
	 * @return true se concluir com êxito
	 * 
	 * 
	 * Ações
	 * 	1- Calcula nova Data de vencimento do contrato
	 * 	2- Retira carência
	 * 	3- Atualiza data de renovação atual
	 * 	4- Gera novos boletos a partir da data do ultimo boleto
	 * 	5- Atualiza contrato de Acesso
	 * 	6- Gera logs
	 */
	public static boolean renovarContratoAcesso(AcessoCliente contrato){
		EntityManager em = ConnUtil.getEntity();
				 
		try{
			
			em.getTransaction().begin();
			
				if(contrato.getPlano().getPlano_Renovacao() != null){
					contrato.setPlano(contrato.getPlano().getPlano_Renovacao());
				}
			
				Integer vigencia1 = contrato.getContrato().getVigencia();			
				Date dataVencContratoNova = new Date();
			
				if(!contrato.getContrato().getTipo_contrato().equals("GRATIS")){
					
					List<ContasReceber> resConta = ContasReceberDAO.procurarTodosBoletosDoAcessoPorContrato(contrato.getId());		
					ContasReceber ct = resConta.get(0);
					Date dtVenc = ct.getData_vencimento();
					DateTime dt2 = new DateTime(dtVenc).plusMonths(1);
					DateTime dtVencimentoNovo = new DateTime(dtVenc).plusMonths(vigencia1);
					dtVenc = dt2.toDate();
					
					CredenciaisAcessoDAO.gerarBoletosAcesso(contrato.getCliente().getId(),contrato, contrato.getContrato(), contrato.getPlano(), dtVenc, contrato.getPlano().getValor(),"LIBERAR_BOLETO");
					
					contrato.setData_venc_contrato(dtVencimentoNovo.toDate());
				}else{
					DateTime dtVencimentoNovo = new DateTime(contrato.getData_venc_contrato()).plusMonths(vigencia1);
					contrato.setData_venc_contrato(dtVencimentoNovo.toDate());
				}
				
				contrato.setData_renovacao(new Date());
				contrato.setCarencia("NAO");			
			
				em.merge(contrato);
				
			em.getTransaction().commit();
			
			boolean check = CredenciaisAcessoDAO.DesbloquearContrato(contrato.getId());
			
			LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Renovou Um Novo Contrato de Acesso"));
			AlteracoesContratoDAO.save(new AlteracoesContrato(null, "RENOVAÇÃO DE CONTRATO", contrato, OpusERP4UI.getUsuarioLogadoUI(), new Date()));
			if(contrato.getStatus_2().equals("BLOQUEADO") && check){
				AlteracoesContratoDAO.save(new AlteracoesContrato(null, "CONTRATO DESBLOQUEADO POR RENOVACAO", contrato, OpusERP4UI.getUsuarioLogadoUI(), new Date()));
			}
			
			return true;
		}catch(Exception e){
			if(em.getTransaction().isActive()){
				em.getTransaction().rollback();				
			}
			e.printStackTrace();	
			
			LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Não Conseguiu Renovar Um Contrato de Acesso, Uma Excessão foi Gerada"));
			return false;
		}
			
	}


	/**
	 * 
	 * @param contrato
	 * @param planoNovo
	 * @return
	 * 
	 * Ações
	 * 	1- Atualiza plano do contrato
	 *		2- Altera plano no Radius (tabela RadUserGroup)
	 *     3- Gera boleto com valor correspondente ao plano novo com vencimento para Hoje. 
	 *     4- Gera logs
	 */
	public static boolean comprarCreditos(AcessoCliente contrato, PlanoAcesso planoNovo){
		
		EntityManager em = ConnUtil.getEntity();
		
		try{
									

			em.getTransaction().begin();
		
				contrato.setPlano(planoNovo);	
				
				String nomePlanoRadius = contrato.getContrato().getId().toString()+"_"+planoNovo.getNome();
				CredenciaisAcessoDAO.alterarPlanoLoginRadius(contrato.getLogin(), nomePlanoRadius);				
			
				CredenciaisAcessoDAO.gerarBoletosAcesso(contrato.getCliente().getId(),contrato, contrato.getContrato(), planoNovo, new Date(), planoNovo.getValor(),"LIBERAR_BOLETO");

				em.merge(contrato);
				
			em.getTransaction().commit();
			
			LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Comprou Créditos para um Contrato de Acesso Pré-Pago"));
			AlteracoesContratoDAO.save(new AlteracoesContrato(null, "COMPRA DE CRÉDITOS", contrato, OpusERP4UI.getUsuarioLogadoUI(), new Date()));			
			
			return true;
		}catch(Exception e){
			if(em.getTransaction().isActive()){
				em.getTransaction().rollback();				
			}
			e.printStackTrace();
			Notify.Show("Não Foi Possivel Comprar Créditos de Acesso", Notify.TYPE_ERROR);
			LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Não Consegiu Comprar Créditos para um Contrato de Acesso Pré-Pago"));
			
			return false;
		}
	}

	
	public static boolean reativarAcesso(AcessoCliente contrato){
		
		try{
			EntityManager em = ConnUtil.getEntity();
			
			String endereco_ip = null;
			if(contrato.getEndereco_ip() != null && !contrato.getEndereco_ip().equals("")){
				endereco_ip = contrato.getEndereco_ip();
			}
			
			em.getTransaction().begin();
			CredenciaisAcessoDAO.cadastrarCredenciaisRadius(contrato.getCliente().getNome_razao(), contrato.getLogin(), contrato.getSenha(), endereco_ip, contrato.getContrato().getId().toString()+"_"+contrato.getPlano().getNome(),contrato.getEndereco_mac());
			contrato.setStatus_2("ATIVO");
			em.merge(contrato);
			em.getTransaction().commit();
			
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 
	 * @param contrato
	 * @param codSerial
	 * @return true se for concluído com êxito
	 * 
	 * Ações
	 * 	1- Cadastra credenciais no Radius
	 * 	2- Se o regime do contrato for COMODATO, altera qtd estoque do Produto, altera status do serial e data de alteração do serial
	 * 	3- Gera logs
	 */
	public static boolean liberarAcesso(AcessoCliente contrato, Integer codSerial){
		
		boolean allow = false;
		
		if(contrato.getRegime().equals("COMODATO") && contrato.getMaterial().getQtdEstoqueDeposito() > 0){
			allow = true;
		}
		
		if(contrato.getRegime().equals("PROPRIO (PARCIAL)") && contrato.getOnu().getQtdEstoqueDeposito() > 0){
			allow = true;
		}
		
		if(contrato.getRegime().equals("PROPRIO")){
			allow = true;
		}
		
		if(allow){
		
				EntityManager em = ConnUtil.getEntity();
				
				try{
					
					em.getTransaction().begin();
					
						String endereco_ip = null;
						if(contrato.getEndereco_ip() != null && !contrato.getEndereco_ip().equals("")){
							endereco_ip = contrato.getEndereco_ip();
						}	
				
						CredenciaisAcessoDAO.cadastrarCredenciaisRadius(contrato.getCliente().getNome_razao(), contrato.getLogin(), contrato.getSenha(), endereco_ip, contrato.getContrato().getId().toString()+"_"+contrato.getPlano().getNome(),contrato.getEndereco_mac());
				
						if(contrato.getRegime().equals("COMODATO") || contrato.getRegime().equals("PROPRIO (PARCIAL)")){
				
							if(contrato.getRegime().equals("COMODATO")){
								//-----Logg
								LogProdutoDAO.registraLogSemTransacao(new LogProduto(null, contrato.getMaterial(), "LIBERAR ACESSO COMODATO", contrato.getMaterial().getQtdEstoque(), contrato.getMaterial().getQtdEstoque()-1, new Date(), OpusERP4UI.getUsuarioLogadoUI().getUsername()));
								//-----Logg
								
								contrato.getMaterial().setQtdEstoqueDeposito(contrato.getMaterial().getQtdEstoqueDeposito()-1);
								ProdutoDAO.saveProduto(contrato.getMaterial());
							}
							
							if(contrato.getRegime().equals("PROPRIO (PARCIAL)")){
								//-----Logg
								LogProdutoDAO.registraLogSemTransacao(new LogProduto(null,contrato.getOnu(), "LIBERAR ACESSO PROPRIO (PARCIAL)", contrato.getOnu().getQtdEstoque(), contrato.getOnu().getQtdEstoque()-1, new Date(), OpusERP4UI.getUsuarioLogadoUI().getUsername()));
								//-----Logg
								
								contrato.getOnu().setQtdEstoqueDeposito(contrato.getOnu().getQtdEstoqueDeposito()-1);
								ProdutoDAO.saveProduto(contrato.getOnu());
							}
							
							SerialProduto serial = SerialDAO.find(codSerial);
							
							if(serial != null){
													
								serial.setStatus("COMODATO");
								serial.setData_alteracao(new Date());
								SerialDAO.atualizaeSerial(serial);
								
								em.persist(new AlteracoesSerial(null, "ACESSO "+contrato.getId()+" COMODATO", serial,OpusERP4UI.getEmpresa(), OpusERP4UI.getUsuarioLogadoUI(), new Date()));
							}else{
								throw new Exception("Serial não foi encontrado"); 
							}
						}
						
						if(contrato.getBase().getWireless().equals("SIM")){				
							MikrotikUtil.liberarAccessList(contrato.getBase().getUsuario(), contrato.getBase().getSenha(), contrato.getBase().getEndereco_ip(),Integer.parseInt(contrato.getBase().getPorta_api()),contrato.getId().toString(), contrato.getCliente().getNome_razao(), contrato.getEndereco_mac(), contrato.getInterfaces(),contrato.getSignal_strength());
						}
						
						em.merge(contrato);
					
					em.getTransaction().commit();
					
					LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Liberou o Acesso de Contrato de Acesso"));			
					AlteracoesContratoDAO.save(new AlteracoesContrato(null, "LIBERAÇÃO DE ACESSO", contrato, OpusERP4UI.getUsuarioLogadoUI(), new Date()));		
					
					return true;
					
				}catch(Exception e){
					if(em.getTransaction().isActive()){
						em.getTransaction().rollback();				
					}
					e.printStackTrace();
					Notify.Show("Não Foi Possivel liberar Contrato de Acesso", Notify.TYPE_ERROR);
					LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Não Consegiu Liberar um Contrato de Acesso Pré-Pago"));
					
					return false;
				}
		
		}else{
			
			if(contrato.getRegime().equals("COMODATO")){
				Notify.Show("Não Foi Possivel liberar Contrato de Acesso por que não há saldo de ROTEADOR", Notify.TYPE_ERROR);
			}
			
			if(contrato.getRegime().equals("PROPRIO (PARCIAL)")){
				Notify.Show("Não Foi Possivel liberar Contrato de Acesso por que não há saldo de ONU", Notify.TYPE_ERROR);
			}
			
			
			return false;
		}
		
	}

	public static boolean liberarAcessoComodatoTotal(AcessoCliente contrato, Integer codSerial, Integer codMac){
		
		
		if(contrato.getMaterial().getQtdEstoqueDeposito() > 0 && contrato.getOnu().getQtdEstoqueDeposito() > 0){
				
				EntityManager em = ConnUtil.getEntity();
				try{
					
					em.getTransaction().begin();
						
					String endereco_ip = null;
					if(contrato.getEndereco_ip() != null && !contrato.getEndereco_ip().equals("")){
						endereco_ip = contrato.getEndereco_ip();
					}	
					
					CredenciaisAcessoDAO.cadastrarCredenciaisRadius(contrato.getCliente().getNome_razao(), contrato.getLogin(), contrato.getSenha(), endereco_ip, contrato.getContrato().getId().toString()+"_"+contrato.getPlano().getNome(),contrato.getEndereco_mac());
					
					if(contrato.getRegime().equals("COMODATO (TOTAL)")){
									
						//-----Logg
						LogProdutoDAO.registraLogSemTransacao(new LogProduto(null, contrato.getMaterial(), "LIBERAR ACESSO COMODATO (TOTAL) - MAC", contrato.getMaterial().getQtdEstoque(), contrato.getMaterial().getQtdEstoque()-1, new Date(), OpusERP4UI.getUsuarioLogadoUI().getUsername()));
						LogProdutoDAO.registraLogSemTransacao(new LogProduto(null,contrato.getOnu(), "LIBERAR ACESSO COMODATO (TOTAL) - SERIAL", contrato.getOnu().getQtdEstoque(), contrato.getOnu().getQtdEstoque()-1, new Date(), OpusERP4UI.getUsuarioLogadoUI().getUsername()));
						//-----Logg
									
						contrato.getMaterial().setQtdEstoqueDeposito(contrato.getMaterial().getQtdEstoqueDeposito()-1);
						ProdutoDAO.saveProduto(contrato.getMaterial());
									
						contrato.getOnu().setQtdEstoqueDeposito(contrato.getOnu().getQtdEstoqueDeposito()-1);
						ProdutoDAO.saveProduto(contrato.getOnu());
									
						SerialProduto serial_onu = SerialDAO.find(codSerial);
						SerialProduto serial_roteador = SerialDAO.find(codMac);
						
						if(serial_onu != null){
												
							serial_onu.setStatus("COMODATO");
							serial_onu.setData_alteracao(new Date());
							SerialDAO.atualizaeSerial(serial_onu);
							
							em.persist(new AlteracoesSerial(null, "ACESSO "+contrato.getId()+" COMODATO", serial_onu,OpusERP4UI.getEmpresa(), OpusERP4UI.getUsuarioLogadoUI(), new Date()));
						}else{
							throw new Exception("Serial não foi encontrado"); 
						}
						
						if(serial_roteador != null){
							
							serial_roteador.setStatus("COMODATO");
							serial_roteador.setData_alteracao(new Date());
							SerialDAO.atualizaeSerial(serial_roteador);
							
							em.persist(new AlteracoesSerial(null, "ACESSO "+contrato.getId()+" COMODATO", serial_roteador,OpusERP4UI.getEmpresa(), OpusERP4UI.getUsuarioLogadoUI(), new Date()));
						}else{
							throw new Exception("Serial não foi encontrado"); 
						}
					}
					
					if(contrato.getBase().getWireless().equals("SIM")){				
						MikrotikUtil.liberarAccessList(contrato.getBase().getUsuario(), contrato.getBase().getSenha(), contrato.getBase().getEndereco_ip(),Integer.parseInt(contrato.getBase().getPorta_api()),contrato.getId().toString(), contrato.getCliente().getNome_razao(), contrato.getEndereco_mac(), contrato.getInterfaces(),contrato.getSignal_strength());
					}
					
					em.merge(contrato);
				
					em.getTransaction().commit();
					
					LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Liberou o Acesso de Contrato de Acesso"));			
					AlteracoesContratoDAO.save(new AlteracoesContrato(null, "LIBERAÇÃO DE ACESSO", contrato, OpusERP4UI.getUsuarioLogadoUI(), new Date()));		
					
					return true;
					
				}catch(Exception e){
					if(em.getTransaction().isActive()){
						em.getTransaction().rollback();				
					}
					e.printStackTrace();
					Notify.Show("Não Foi Possivel liberar Contrato de Acesso", Notify.TYPE_ERROR);
					LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Não Consegiu Liberar um Contrato de Acesso Pré-Pago"));
					
					return false;
				}
		
		}else{
			
			Notify.Show("Não Foi Possivel liberar Contrato de Acesso por que não há saldo de ROTEADOR e/ou ONU", Notify.TYPE_ERROR);
			return false;
		}
	}
	
	public static boolean alteraPlanobkp(AcessoCliente contrato, PlanoAcesso planoNovo, NfeMestre nfe, String InstGratis){
		
		EntityManager em = ConnUtil.getEntity();
		Date dataNfe;
		
		try{
			
				//Não deixa passar se tiver algum boleto bloqueado
				if(contrato != null){			
					List<ContasReceber> boletosBloqueados = ContasReceberDAO.procurarBoletosBloqueadosPorContrato(contrato.getId());
					
					if(boletosBloqueados != null){
						throw new Exception("Cliente bloqueado");
					}		
				}
		
				PlanoAcesso planoAntigo = contrato.getPlano();
				boolean apagarBoletos = false;
				if(planoNovo.getId() != planoAntigo.getId()){
					apagarBoletos = true;
				}
														
				String excluirBoletosExistentes = null;
				if(apagarBoletos){
					excluirBoletosExistentes = "SIM";
				}else{
					excluirBoletosExistentes = "NAO";
				}
					
				//caDAO.alterarPlanoAcesso(contrato,apagarBoletosStr,planoNovo);				
				//-------------------------------------------//
				Date dtVencimentoBoleto = null;
				if(!contrato.getContrato().getTipo_contrato().equals("GRATIS")){
					
					List<ContasReceber> resConta = ContasReceberDAO.buscarTitulosNaoVencidosDeAcessoPorContrato(contrato.getId());		
					ContasReceber proximoBoleto = resConta.get(0);
											
					DateTime dtProx = new DateTime(proximoBoleto.getData_vencimento()).plusMonths(1);
					dtVencimentoBoleto = dtProx.toDate();
					
					//Condição que verifica se o boleto localizado esta adiantado
					if(proximoBoleto.getStatus().equals("FECHADO")){
						dtVencimentoBoleto = new DateTime(proximoBoleto.getData_vencimento()).plusMonths(1).toDate();
						
						//Reabrir boleto, gerar log de reabertura para alteração de plano
						proximoBoleto.setStatus("ABERTO");
						proximoBoleto.setData_pagamento(null);
						proximoBoleto.setValor_pagamento(null);
						
						ContasReceberDAO.save(proximoBoleto);
					}
				}
				
				
				
				em.getTransaction().begin();
				
				//Atualiza informações de benefíficio de adesão, comodato e instalação gratis no contrato
				contrato.setValor_beneficio_adesao(Real.formatStringToDBDouble(contrato.getContrato().getValor_adesao()));
				contrato.setValor_beneficio_comodato(Real.formatStringToDBDouble(contrato.getContrato().getValor_equipamento()));
				contrato.setInstalacao_gratis(InstGratis);
				
					if(!contrato.getContrato().getTipo_contrato().equals("GRATIS")){
											
									
						String valorBoleto = "";			
						valorBoleto = CredenciaisAcessoDAO.calcDiferencaValorPlano(contrato.getId(), planoNovo.getId());	
				
						if(excluirBoletosExistentes != null && excluirBoletosExistentes.equals("SIM")){							
							List<ContasReceber> boletosAbertosNaoVencidos = ContasReceberDAO.buscarTitulosAbertosNaoVencidosDeAcessoPorContrato(contrato.getId());
							
							if(boletosAbertosNaoVencidos != null){
								Integer i = 1;
								for(ContasReceber cr: boletosAbertosNaoVencidos){
									
									if(i == 1){
										
										ContasReceber crINN = null;
										if(cr.getN_numero_sicred() != null && !cr.getN_numero_sicred().equals("")){
											
											crINN = new ContasReceber();
											crINN.setEmpresa_id(cr.getEmpresa_id());
											crINN.setCliente(cr.getCliente());
											crINN.setN_numero_sicred_antigo(cr.getN_numero_sicred());
											crINN.setN_numero(null);
											crINN.setStatus("EXCLUIDO");
										}
										
										cr.setValor_titulo(valorBoleto);
									    cr.setN_numero_sicred(null); 
										cr.setN_numero_sicred_antigo(null);
										cr.setN_numero(null); 
										em.merge(cr);
										
								    	if(crINN != null){
									    		em.persist(crINN);											
										}
									}
									
									if(i != 1){
										cr.setStatus("EXCLUIDO");
										cr.setData_exclusao(new Date());
										em.merge(cr);
									}
									
									i++;
								}
							}						
						}
												
						contrato.setData_venc_contrato(CredenciaisAcessoDAO.calcularDataVencContrato(contrato.getContrato().getId(), dtVencimentoBoleto));				
						CredenciaisAcessoDAO.gerarBoletosAcesso(contrato.getCliente().getId(),contrato, contrato.getContrato(), planoNovo, dtVencimentoBoleto, valorBoleto,"ALTERAR_PLANO");	
					}
					
					String carencia = "";
					if(contrato.getContrato().getCarencia().equals("SIM")){
						//Meses da Atual Carencia			
						DateTime dt1 = new DateTime(new Date());
						DateTime dt2 = new DateTime(contrato.getData_venc_contrato());
						
						Integer meses = Months.monthsBetween(dt1, dt2).getMonths();
	
						if(meses>1){
							//SIM
							carencia = "SIM";						
						}else{
							//NAO
							if(contrato.getPlano().getId() == planoNovo.getId()){
								carencia = "NAO";
							}else{
								carencia = "SIM";
							}								
						}
					}else{						
						carencia = "NAO";
					}
					
									
					contrato.setData_alteracao_plano(new Date());
					contrato.setPlano(planoNovo);							
					contrato.setCarencia(carencia);
	
					
					String nomePlanoRadius = contrato.getContrato().getId().toString()+"_"+planoNovo.getNome();
					CredenciaisAcessoDAO.alterarPlanoLoginRadius(contrato.getLogin(), nomePlanoRadius);		
					
					em.merge(contrato);
					
				em.getTransaction().commit();			
								
				if(contrato.getBase() != null && contrato.getLogin() != null){
					MikrotikUtil.desconectarCliente(contrato.getBase().getUsuario(), contrato.getBase().getSenha(), contrato.getBase().getEndereco_ip(), Integer.parseInt(contrato.getBase().getPorta_api()), contrato.getLogin());
				}
		
				LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Fez uma Alteração de Plano para um Contrato de Acesso"));
				AlteracoesContratoDAO.save(new AlteracoesContrato(null, "ALTERAÇÃO DE PLANO", contrato, OpusERP4UI.getUsuarioLogadoUI(), new Date()));
				
				return true;
		
		}catch(Exception e){
			if(em.getTransaction().isActive()){
				em.getTransaction().rollback();				
			}
			
			e.printStackTrace();
			Notify.Show("Não Foi Possivel alterar Plano do Contrato de Acesso", Notify.TYPE_ERROR);
			LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Não Consegiu alterar plano de Acesso"));
			
			return false;
		}
		
	}
	

	/**
	 * 
	 * @param contrato
	 * @param planoNovo
	 * @return true se for concluído com êxito
	 * 
	 * Ações
	 * 	1- Verifica se o Cliente tem boletos Bloqueados
	 * 	2- Se o contrato não for GRÁTIS e tiver boletos abertos não vencidos os Excluí
	 * 	3- Gera novos boletos
	 * 	4- Cálcula nova data de Vencimento do Contrato
	 * 	5- Atualiza carência, plano e data de alteração do plano
	 * 	6- Atualiza plano no Radius
	 * 	7- Atualiza contrato de acesso
	 * 	8- Desconecta cliente do concentrador
	 * 	9- Gera logs
	 */
	public static boolean alteraPlano(AcessoCliente contrato, PlanoAcesso planoNovo, NfeMestre nfe, String InstGratis){
		
		EntityManager em = ConnUtil.getEntity();
		Date dataNfe;
		
		try{
			
				//Não deixa passar se tiver algum boleto bloqueado
				if(contrato != null){			
					List<ContasReceber> boletosBloqueados = ContasReceberDAO.procurarBoletosBloqueadosPorContrato(contrato.getId());
					
					if(boletosBloqueados != null){
						throw new Exception("Cliente bloqueado");
					}		
				}
		
				PlanoAcesso planoAntigo = contrato.getPlano();
				boolean apagarBoletos = false;
				if(planoNovo.getId() != planoAntigo.getId()){
					apagarBoletos = true;
				}
														
				String excluirBoletosExistentes = null;
				if(apagarBoletos){
					excluirBoletosExistentes = "SIM";
				}else{
					excluirBoletosExistentes = "NAO";
				}
					
				//caDAO.alterarPlanoAcesso(contrato,apagarBoletosStr,planoNovo);				
				//-------------------------------------------//
				
				em.getTransaction().begin();
				
				//Atualiza informações de benefíficio de adesão, comodato e instalação gratis no contrato
				contrato.setArquivo_upload(null); 
				contrato.setValor_beneficio_adesao(Real.formatStringToDBDouble(contrato.getContrato().getValor_adesao()));
				contrato.setValor_beneficio_comodato(Real.formatStringToDBDouble(contrato.getContrato().getValor_equipamento()));
				contrato.setInstalacao_gratis(InstGratis);
				
					if(!contrato.getContrato().getTipo_contrato().equals("GRATIS")){
						
						List<ContasReceber> resConta = ContasReceberDAO.buscarTitulosNaoVencidosDeAcessoPorContrato(contrato.getId());		
						ContasReceber proximoBoleto = resConta.get(0);
												
						DateTime dtProx = new DateTime(proximoBoleto.getData_vencimento()).plusMonths(1);
						Date dtVencimentoBoleto = dtProx.toDate();
						
						//Condição que verifica se o boleto localizado esta adiantado
						if(proximoBoleto.getStatus().equals("FECHADO")){
							dtVencimentoBoleto = new DateTime(proximoBoleto.getData_vencimento()).plusMonths(1).toDate();
							
							//Reabrir boleto, gerar log de reabertura para alteração de plano
							
						}
									
						String valorBoleto = "";			
						valorBoleto = CredenciaisAcessoDAO.calcDiferencaValorPlano(contrato.getId(), planoNovo.getId());	
				
						if(excluirBoletosExistentes != null && excluirBoletosExistentes.equals("SIM")){							
							List<ContasReceber> boletosAbertosNaoVencidos = ContasReceberDAO.buscarTitulosAbertosNaoVencidosDeAcessoPorContrato(contrato.getId());
							
							if(boletosAbertosNaoVencidos != null){
								Integer i = 1;
								for(ContasReceber cr: boletosAbertosNaoVencidos){
									
									if(i == 1){
										
										ContasReceber crINN = null;
										if(cr.getN_numero_sicred() != null && !cr.getN_numero_sicred().equals("")){
											
											crINN = new ContasReceber();
											crINN.setEmpresa_id(cr.getEmpresa_id());
											crINN.setCliente(cr.getCliente());
											crINN.setN_numero_sicred_antigo(cr.getN_numero_sicred());
											crINN.setN_numero(null);
											crINN.setStatus("EXCLUIDO");
										}
										
										cr.setValor_titulo(valorBoleto);
									    cr.setN_numero_sicred(null); 
										cr.setN_numero_sicred_antigo(null);
										cr.setN_numero(null); 
										
										if(contrato.getCodigo_cartao() != null){
											cr.setCodigo_cartao(contrato.getCodigo_cartao());
										}
										
										em.merge(cr);
										
								    	if(crINN != null){
									    		em.persist(crINN);											
										}
									}
									
									if(i != 1){
										cr.setStatus("EXCLUIDO");
										cr.setData_exclusao(new Date());
										em.merge(cr);
									}
									
									i++;
								}
							}						
						}
												
						contrato.setData_venc_contrato(CredenciaisAcessoDAO.calcularDataVencContrato(contrato.getContrato().getId(), dtVencimentoBoleto));				
						CredenciaisAcessoDAO.gerarBoletosAcesso(contrato.getCliente().getId(),contrato, contrato.getContrato(), planoNovo, dtVencimentoBoleto, valorBoleto,"ALTERAR_PLANO");	
					}
					
					String carencia = "";
					if(contrato.getContrato().getCarencia().equals("SIM")){
						//Meses da Atual Carencia			
						DateTime dt1 = new DateTime(new Date());
						DateTime dt2 = new DateTime(contrato.getData_venc_contrato());
						
						Integer meses = Months.monthsBetween(dt1, dt2).getMonths();
	
						if(meses>1){
							//SIM
							carencia = "SIM";						
						}else{
							//NAO
							if(contrato.getPlano().getId() == planoNovo.getId()){
								carencia = "NAO";
							}else{
								carencia = "SIM";
							}								
						}
					}else{						
						carencia = "NAO";
					}
					
									
					contrato.setData_alteracao_plano(new Date());
					contrato.setPlano(planoNovo);							
					contrato.setCarencia(carencia);
	
					
					String nomePlanoRadius = contrato.getContrato().getId().toString()+"_"+planoNovo.getNome();
					CredenciaisAcessoDAO.alterarPlanoLoginRadius(contrato.getLogin(), nomePlanoRadius);		
					
					em.merge(contrato);
					
				em.getTransaction().commit();			
								
				if(contrato.getBase() != null && contrato.getLogin() != null){
					MikrotikUtil.desconectarCliente(contrato.getBase().getUsuario(), contrato.getBase().getSenha(), contrato.getBase().getEndereco_ip(), Integer.parseInt(contrato.getBase().getPorta_api()), contrato.getLogin());
				}
				
				
				//Verificações ITTV
				if(contrato.getIttv_id() != null && !contrato.getIttv_id().equals("")){
					if(planoNovo.getPossui_ittv() == null){
						
							//Mudar Status do customer
							String r = IttvDAO.atualizarStatus(contrato.getIttv_id(), "INACTIVE");
							System.out.println(r);						
						
					}else{
						if(!planoNovo.getPossui_ittv().equals("SIM")){
							
							//Mudar Status do customer
							String r = IttvDAO.atualizarStatus(contrato.getIttv_id(), "INACTIVE");
							System.out.println(r);
							
						}
					}
				}
				
		
				LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Fez uma Alteração de Plano para um Contrato de Acesso"));
				AlteracoesContratoDAO.save(new AlteracoesContrato(null, "ALTERAÇÃO DE PLANO", contrato, OpusERP4UI.getUsuarioLogadoUI(), new Date()));
				
				return true;
		
		}catch(Exception e){
			if(em.getTransaction().isActive()){
				em.getTransaction().rollback();				
			}
			
			e.printStackTrace();
			Notify.Show("Não Foi Possivel alterar Plano do Contrato de Acesso", Notify.TYPE_ERROR);
			LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Não Consegiu alterar plano de Acesso"));
			
			return false;
		}
		
	}
	
	
	public static NfeMestre verificaBoletosNfeAllow(Integer contrato_id){
		
		EntityManager em = ConnUtil.getEntity();
		
		List<ContasReceber> boletosAbertosNaoVencidos = ContasReceberDAO.buscarTitulosAbertosNaoVencidosDeAcessoPorContrato(contrato_id);
		if(boletosAbertosNaoVencidos != null){
			for (ContasReceber contasReceber : boletosAbertosNaoVencidos) {
				
				Query q = em.createQuery("select m from NfeMestre m where m.contas_receber =:boleto", NfeMestre.class);
				q.setParameter("boleto",contasReceber);
				
				if(q.getResultList().size() == 1){
					
					return (NfeMestre)q.getSingleResult();
					//return false;
				}
			}
		}
		
		return null;
	}
	
	
	/**
	 * 
	 * @param ac
	 * @return true se concluir com êxito
	 * 
	 * Ações
	 * 	1- Altera Credenciais no Radius
	 * 	2- Altera tabela AcessoCliente
	 * 	3- Gera logs
	 */
	public static boolean alterarCredenciais(AcessoCliente ac, String loginAntigo){
		EntityManager em = ConnUtil.getEntity();
		
		try{		
			em.getTransaction().begin();
			
				String login_novo = ac.getLogin();
				String senha_nova = ac.getSenha();																
	
				AcessoCliente ac2 = em.find(AcessoCliente.class, ac.getId());
				CredenciaisAcessoDAO.alterarCredenciaisRadius(ac.getStatus_2(),ac2.getLogin(), login_novo ,senha_nova,ac.getContrato().getId().toString()+"_"+ac.getPlano().getNome(),ac.getCliente().getNome_razao(), ac.getEndereco_mac(),ac.getEndereco_ip());
				
				em.merge(ac);
				
			em.getTransaction().commit();
			
			LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Fez uma Alteração de Credenciais para um Contrato de Acesso"));
			AlteracoesContratoDAO.save(new AlteracoesContrato(null, "ALTERAÇÃO DE CREDENCIAIS", ac, OpusERP4UI.getUsuarioLogadoUI(), new Date()));
			
			MikrotikUtil.desconectarCliente(ac.getBase().getUsuario(), ac.getBase().getSenha(), ac.getBase().getEndereco_ip(), Integer.parseInt(ac.getBase().getPorta_api()), ac.getLogin());
			
			return true;
		}catch(Exception e){
			
			if(em.getTransaction().isActive()){
				em.getTransaction().rollback();				
			}
			
			e.printStackTrace();
			Notify.Show("Não Foi Possivel alterar Plano do Contrato de Acesso", Notify.TYPE_ERROR);
			LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Não Consegiu alterar plano de Acesso"));
			
			return false;
			
		}
	}
	
	/**
	 * 
	 * @param acNovo
	 * @param acAntigo
	 * @return true se concluir com êxito
	 * 
	 * Ações
	 * 	1- Altera Access-list da Base Antiga para nova
	 * 	2- Altera informação de concentrador na tabela Acesso Cliente
	 * 	3- Gera logs
	 */
	public static boolean alterarConcentrador(AcessoCliente ac, AcessoCliente acAntigo){
		
		EntityManager em = ConnUtil.getEntity();
		
		try{		
				Cliente c = ac.getCliente();
				
				//Pega Base Antiga							
				Concentrador b = acAntigo.getBase();
				Concentrador b_nova = ac.getBase();
											
				//Pega mac Antigo
				String endereco_mac = acAntigo.getEndereco_mac();
				String endereco_mac_novo = ac.getEndereco_mac();
			
				String interfaces = ac.getInterfaces();
				String signal_strength = ac.getSignal_strength();
										
				//Remover Access-List 
				MikrotikUtil.removerAccessList(b.getUsuario(), b.getSenha(), b.getEndereco_ip(),Integer.parseInt(b.getPorta_api()),c.getNome_razao(), endereco_mac);				
				MikrotikUtil.removerAccessList(b_nova.getUsuario(), b_nova.getSenha(), b_nova.getEndereco_ip(),Integer.parseInt(b_nova.getPorta_api()),c.getNome_razao(), endereco_mac);
				
				//Alterar Concentrador
				if(b_nova.getWireless().equals("SIM")){
					MikrotikUtil.liberarAccessList(b_nova.getUsuario(), b_nova.getSenha(), b_nova.getEndereco_ip(),Integer.parseInt(b_nova.getPorta_api()),ac.getId().toString(),c.getNome_razao(), endereco_mac_novo, interfaces, signal_strength);
				}
				
				List<FiltroAcesso> filtros = CredenciaisAcessoDAO.getFiltrosAcesso(ac.getId());
				
				if(filtros != null && filtros.size() > 0){
					
					for (FiltroAcesso filtro : filtros) {
																	
						MikrotikUtil.removerMarcacaoFirewallFiltroBloqContext(ac.getBase().getUsuario(), ac.getBase().getSenha(),ac.getBase().getEndereco_ip(), Integer.parseInt(ac.getBase().getPorta_api()),ac.getId().toString(), ac.getCliente().getNome_razao(), filtro.getPalavra());
						
						MikrotikUtil.criarMarcacaoFirewallFiltroBloqContext(b_nova.getUsuario(), b_nova.getSenha(),b_nova.getEndereco_ip(), Integer.parseInt(b_nova.getPorta_api()),ac.getId().toString(), ac.getCliente().getNome_razao(), filtro.getPalavra());
					}									
				}
				
				em.getTransaction().begin();

					CredenciaisAcessoDAO.alterarCredenciaisRadius(ac.getStatus_2(), ac.getLogin(), ac.getLogin(), ac.getSenha(), ac.getContrato().getId().toString()+"_"+ac.getPlano().getNome(), ac.getCliente().getNome_razao(), ac.getEndereco_mac(), ac.getEndereco_ip());
					em.merge(ac);
				
				em.getTransaction().commit();
		
				LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Fez uma Alteração de Concentrador para um Contrato de Acesso"));
				AlteracoesContratoDAO.save(new AlteracoesContrato(null, "ALTERAÇÃO DE CONCENTRADOR", ac, OpusERP4UI.getUsuarioLogadoUI(), new Date()));
				
				return true;
		
		}catch(Exception e){
			if(em.getTransaction().isActive()){
				em.getTransaction().rollback();				
			}
			
			e.printStackTrace();
			Notify.Show("Não Foi Possivel alterar de Concentrador", Notify.TYPE_ERROR);
			LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Não Consegiu alterar concentrador"));
			
			return false;
		}
		
	}
	
	public static boolean alterarIpFixo(AcessoCliente ac){
		EntityManager em = ConnUtil.getEntity();
		
		try{
			
			em.getTransaction().begin();
			CredenciaisAcessoDAO.alterarCredenciaisRadius(ac.getStatus_2(), ac.getLogin(), ac.getLogin(), ac.getSenha(), ac.getContrato().getId().toString()+"_"+ac.getPlano().getNome(), ac.getCliente().getNome_razao(), ac.getEndereco_mac(), ac.getEndereco_ip());
			em.getTransaction().commit();
			
			LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Fez uma Alteração de IP Fixo para um Contrato de Acesso"));
			AlteracoesContratoDAO.save(new AlteracoesContrato(null, "ALTERAÇÃO DE IP FIXO", ac, OpusERP4UI.getUsuarioLogadoUI(), new Date()));
			
			return true;
		}catch(Exception e){
			return false;
		}
	}
	
	/**
	 * 
	 * @param ac
	 * @param acAntigo
	 * @param motivo
	 * @param codSerial
	 * @return true se concluir com êxito
	 * 
	 * Ações
	 * 	1- Cadastra troca de material na tabela registro_troca_material
	 * 	2- Altera Credenciais no Radius
	 * 	3- Se o Regime do Contrato for COMODATO, altera Qtd no Estoque do Produto, altera Status e cria log de COMODATO para o MAC(Serial)
	 * 	4- Se o mac antigo não existir é cadastrado como ATIVO e gerado Log de ESTORNO e se existir é alterado STATUS para ATIVO e gerado log de ESTORNO
	 * 	5- Altera tabela de acesso cliente
	 * 	6- Gera logs 
	 */
	public static boolean alterarMaterial(AcessoCliente ac, String motivo, Integer codSerial, String macAntigo, Produto materialAntigo, String serialAntigo1){
		
		boolean check = true;
		
		if(ac.getRegime().equals("COMODATO") && ac.getMaterial().getQtdEstoqueDeposito() < 1){
			check  = false;
			Notify.Show("O produto: "+ac.getMaterial().getNome()+" não tem saldo!", Notify.TYPE_ERROR);
		}
		
		if(ac.getRegime().equals("PROPRIO (PARCIAL)") && ac.getOnu().getQtdEstoqueDeposito() < 1){
			check  = false;
			Notify.Show("O produto: "+ac.getOnu().getNome()+" não tem saldo!", Notify.TYPE_ERROR);
		}
		
		
		if(check){
		
				EntityManager em = ConnUtil.getEntity();
				
				
				try{		
						em.getTransaction().begin();
		
								if(materialAntigo != null && ac.getMaterial() != null){
									CredenciaisAcessoDAO.cadastrarTrocaMaterial(materialAntigo, ac.getMaterial(), motivo, new AcessoCliente(ac.getId()), new Date());
								}
								
								CredenciaisAcessoDAO.alterarCredenciaisRadiusMac(ac.getLogin(), ac.getLogin(), ac.getEndereco_mac());
		
								if(ac.getRegime().equals("COMODATO") || ac.getRegime().equals("PROPRIO (PARCIAL)")){
						
									if(codSerial != null){
										SerialProduto serial = SerialDAO.find(codSerial);				
										
										if(serial != null){
											
											if(ac.getRegime().equals("COMODATO")){
												//-----Logg
												LogProdutoDAO.registraLogSemTransacao(new LogProduto(null,ac.getMaterial(), "ALTERAR MATERIAL COMODATO", ac.getMaterial().getQtdEstoqueDeposito(), ac.getMaterial().getQtdEstoqueDeposito()-1, new Date(), OpusERP4UI.getUsuarioLogadoUI().getUsername()));
												//-----Logg
												
												ac.getMaterial().setQtdEstoqueDeposito(ac.getMaterial().getQtdEstoqueDeposito()-1);							
												ProdutoDAO.saveProduto(ac.getMaterial());
											}
											
											if(ac.getRegime().equals("PROPRIO (PARCIAL)")){
												
												//-----Logg
												LogProdutoDAO.registraLogSemTransacao(new LogProduto(null,ac.getOnu(), "ALTERAR MATERIAL PROPRIO (PARCIAL)", ac.getOnu().getQtdEstoqueDeposito(), ac.getOnu().getQtdEstoqueDeposito()-1, new Date(), OpusERP4UI.getUsuarioLogadoUI().getUsername()));
												//-----Logg
												
												ac.getOnu().setQtdEstoqueDeposito(ac.getOnu().getQtdEstoqueDeposito()-1);							
												ProdutoDAO.saveProduto(ac.getOnu());
												
											}
											
											serial.setStatus("COMODATO");
											serial.setData_alteracao(new Date());
											SerialDAO.atualizaeSerial(serial);
											
											AlteracoesSerialDAO.saveNovo(new AlteracoesSerial(null, "ACESSO "+ac.getId()+" COMODATO", serial,OpusERP4UI.getEmpresa(), OpusERP4UI.getUsuarioLogadoUI(), new Date()));
										}else{
											throw new Exception("Serial não encontrado");
										}								
									}
									
									SerialProduto serialAntigo;
									
									if(serialAntigo1 != null){										
										serialAntigo = SerialDAO.findByNameAndCod(materialAntigo, serialAntigo1);
									}else{
										serialAntigo = SerialDAO.findByNameAndCod(materialAntigo, macAntigo);
									}
								
									if(ac.getMaterial().getId().equals(materialAntigo.getId())){	
										//-----Logg
										LogProdutoDAO.registraLogSemTransacao(new LogProduto(null, materialAntigo, "ALTERACAO DE MATERIAL", materialAntigo.getQtdEstoque(), ac.getMaterial().getQtdEstoque(), new Date(), OpusERP4UI.getUsuarioLogadoUI().getUsername()));
										//-----Logg
										
										materialAntigo.setQtdEstoqueDeposito(ac.getMaterial().getQtdEstoqueDeposito());								
									}
									
									if(ac.getRegime().equals("PROPRIO (PARCIAL)") && ac.getOnu().getId().equals(materialAntigo.getId())){
										//-----Logg
										LogProdutoDAO.registraLogSemTransacao(new LogProduto(null, materialAntigo, "ALTERACAO DE MATERIAL PROPRIO (PARCIAL)", materialAntigo.getQtdEstoque(), ac.getOnu().getQtdEstoque(), new Date(), OpusERP4UI.getUsuarioLogadoUI().getUsername()));
										//-----Logg
										
										materialAntigo.setQtdEstoqueDeposito(ac.getOnu().getQtdEstoqueDeposito());	
									}
									
									//-----Logg
									LogProdutoDAO.registraLogSemTransacao(new LogProduto(null, materialAntigo, "ALTERACAO DE MATERIAL", materialAntigo.getQtdEstoque(), materialAntigo.getQtdEstoque()+1, new Date(), OpusERP4UI.getUsuarioLogadoUI().getUsername()));
									//-----Logg
									
									if(codSerial != null){
										materialAntigo.setQtdEstoqueDeposito(materialAntigo.getQtdEstoqueDeposito()+1);
										ProdutoDAO.saveProduto(materialAntigo);
									}
									
									if(serialAntigo != null && codSerial != null){
										
										serialAntigo.setStatus("ATIVO");
										serialAntigo.setData_alteracao(new Date());
										SerialDAO.atualizaeSerial(serialAntigo);
										
										AlteracoesSerialDAO.saveNovo(new AlteracoesSerial(null, "ACESSO "+ac.getId()+" ESTORNO", serialAntigo,OpusERP4UI.getEmpresa(), OpusERP4UI.getUsuarioLogadoUI(), new Date()));
									}else{	
										if(codSerial != null){
											SerialProduto sNovo = SerialDAO.addNovoSerialSemTrasaction(ac.getId(),materialAntigo.getId(), macAntigo);
										}
									}							
								}
								
		
								em.merge(ac);				
						
						em.getTransaction().commit();
		
						Concentrador b = ac.getBase();		
						MikrotikUtil.desconectarCliente(b.getUsuario(), b.getSenha(), b.getEndereco_ip(),Integer.parseInt(b.getPorta_api()), ac.getLogin());
						
						if(b != null){ 
							MikrotikUtil.removerAccessList(b.getUsuario(), b.getSenha(), b.getEndereco_ip(),Integer.parseInt(b.getPorta_api()),ac.getCliente().getNome_razao(), ac.getEndereco_mac());
							MikrotikUtil.liberarAccessList(b.getUsuario(), b.getSenha(), b.getEndereco_ip(),Integer.parseInt(b.getPorta_api()),ac.getId().toString(), ac.getCliente().getNome_razao(), ac.getEndereco_mac(), ac.getInterfaces(), ac.getSignal_strength());
						}	
						
						LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Fez uma Alteração de Material para um Contrato de Acesso"));		
						AlteracoesContratoDAO.save(new AlteracoesContrato(null, "ALTERAÇÃO DE MATERIAL", ac, OpusERP4UI.getUsuarioLogadoUI(), new Date()));
						
						return true;
				}catch(Exception e){
					if(em.getTransaction().isActive()){
						em.getTransaction().rollback();				
					}
					
					e.printStackTrace();
					Notify.Show("Não Foi Possivel alterar Material", Notify.TYPE_ERROR);
					LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Não Consegiu alterar material"));
					
					return false;
				}
		}else{
			Notify.Show("Não Foi Possivel alterar Material", Notify.TYPE_ERROR);
			return false;
		}
		
	}
	
	public static boolean alterarMaterialComodatoTotal(AcessoCliente ac, String motivo, 
			Integer codMacRoteadorNovo,Integer codSerialOnuNovo, 
			String macRoteadorAntigo, Produto roteadorAntigo,
			String serialOnuAntigo, Produto onuAntigo){
		
		boolean check = true;
		
		if(ac.getMaterial().getQtdEstoqueDeposito() < 1){
			check = false;
			Notify.Show("O produto: "+ac.getMaterial().getNome()+" não tem saldo!", Notify.TYPE_ERROR);
		}
		
		if(ac.getOnu().getQtdEstoqueDeposito() < 1){
			check = false;
			Notify.Show("O produto: "+ac.getOnu().getNome()+" não tem saldo!", Notify.TYPE_ERROR);
		}
		
		if(check){
					
				EntityManager em = ConnUtil.getEntity();
				
				try{		
								em.getTransaction().begin();
		
								//Cadastra Troca de Roteador
								if(roteadorAntigo != null && ac.getMaterial() != null){
									CredenciaisAcessoDAO.cadastrarTrocaMaterial(roteadorAntigo, ac.getMaterial(), motivo, new AcessoCliente(ac.getId()), new Date());
								}
		
								//Cadastra Troca de Onu
								if(onuAntigo != null && ac.getOnu() != null){
									CredenciaisAcessoDAO.cadastrarTrocaMaterial(onuAntigo, ac.getOnu(), motivo, new AcessoCliente(ac.getId()), new Date());
								}
								
								CredenciaisAcessoDAO.alterarCredenciaisRadiusMac(ac.getLogin(), ac.getLogin(), ac.getEndereco_mac());
		
								//if(ac.getRegime().equals("COMODATO (TOTAL)")){
				
						
									if(codMacRoteadorNovo != null){
										SerialProduto serial = SerialDAO.find(codMacRoteadorNovo);				
										
										if(serial != null){
											
											//-----Logg
											LogProdutoDAO.registraLogSemTransacao(new LogProduto(null,ac.getMaterial(), "ALTERAR MATERIAL COMODATO", ac.getMaterial().getQtdEstoqueDeposito(), ac.getMaterial().getQtdEstoqueDeposito()-1, new Date(), OpusERP4UI.getUsuarioLogadoUI().getUsername()));
											//-----Logg
											
											if(!ac.getMaterial().getId().equals(roteadorAntigo.getId())){
												ac.getMaterial().setQtdEstoqueDeposito(ac.getMaterial().getQtdEstoqueDeposito()-1);
												ProdutoDAO.saveProduto(ac.getMaterial());
											}
											
																				
											serial.setStatus("COMODATO");
											serial.setData_alteracao(new Date());
											SerialDAO.atualizaeSerial(serial);
											
											AlteracoesSerialDAO.saveNovo(new AlteracoesSerial(null, "ACESSO "+ac.getId()+" COMODATO", serial,OpusERP4UI.getEmpresa(), OpusERP4UI.getUsuarioLogadoUI(), new Date()));
										}else{
											throw new Exception("Roteador não encontrado");
										}								
									}
									
									//Estorno Roteador Antigo.
									if(roteadorAntigo != null && macRoteadorAntigo != null ){
										SerialProduto serialRoteadorAntigo = SerialDAO.findByNameAndCod(roteadorAntigo, macRoteadorAntigo);						
										if(serialRoteadorAntigo != null){
																				
												//-----Logg
												LogProdutoDAO.registraLogSemTransacao(new LogProduto(null,roteadorAntigo, "C_TOTAL - ESTORNOU ROTEADOR ANTIGO", roteadorAntigo.getQtdEstoqueDeposito(), roteadorAntigo.getQtdEstoqueDeposito()+1, new Date(), OpusERP4UI.getUsuarioLogadoUI().getUsername()));
												//-----Logg
												
												if(!ac.getMaterial().getId().equals(roteadorAntigo.getId())){
													roteadorAntigo.setQtdEstoqueDeposito(roteadorAntigo.getQtdEstoqueDeposito()+1);
													ProdutoDAO.saveProduto(roteadorAntigo);									
												}
												
																					
												serialRoteadorAntigo.setStatus("ATIVO");
												serialRoteadorAntigo.setData_alteracao(new Date());
												SerialDAO.atualizaeSerial(serialRoteadorAntigo);
												
												AlteracoesSerialDAO.saveNovo(new AlteracoesSerial(null, "ACESSO "+ac.getId()+" ESTORNO", serialRoteadorAntigo,OpusERP4UI.getEmpresa(), OpusERP4UI.getUsuarioLogadoUI(), new Date()));															
										}else{
											//Cadastrar Novo Mac do Roteador se Não Encontrar.
										}
									}
									
									
									
									//Baixa Onu Nova
									if(codSerialOnuNovo != null){
										SerialProduto serial = SerialDAO.find(codSerialOnuNovo);				
										
										if(serial != null){
											
											//-----Logg
											LogProdutoDAO.registraLogSemTransacao(new LogProduto(null,ac.getOnu(), "ALTERAR MATERIAL COMODATO", ac.getOnu().getQtdEstoqueDeposito(), ac.getOnu().getQtdEstoqueDeposito()-1, new Date(), OpusERP4UI.getUsuarioLogadoUI().getUsername()));
											//-----Logg
												
											if(!ac.getOnu().getId().equals(onuAntigo.getId())){
												ac.getOnu().setQtdEstoqueDeposito(ac.getOnu().getQtdEstoqueDeposito()-1);
												ProdutoDAO.saveProduto(ac.getOnu());
											}
												
																				
											serial.setStatus("COMODATO");
											serial.setData_alteracao(new Date());
											SerialDAO.atualizaeSerial(serial);
											
											AlteracoesSerialDAO.saveNovo(new AlteracoesSerial(null, "ACESSO "+ac.getId()+" COMODATO", serial,OpusERP4UI.getEmpresa(), OpusERP4UI.getUsuarioLogadoUI(), new Date()));
										}else{
											throw new Exception("Serial não encontrado");
										}								
									}
									
									//Estorno Onu Antigo
									if(onuAntigo != null && serialOnuAntigo != null){
										SerialProduto serialOnuAntigo2 = SerialDAO.findByNameAndCod(onuAntigo, serialOnuAntigo);					
										if(serialOnuAntigo2 != null){
																				
												//-----Logg
												LogProdutoDAO.registraLogSemTransacao(new LogProduto(null,onuAntigo, "C_TOTAL - ESTORNOU ONU ANTIGA", onuAntigo.getQtdEstoqueDeposito(), onuAntigo.getQtdEstoqueDeposito()+1, new Date(), OpusERP4UI.getUsuarioLogadoUI().getUsername()));
												//-----Logg
												
												if(!ac.getOnu().getId().equals(onuAntigo.getId())){
													onuAntigo.setQtdEstoqueDeposito(onuAntigo.getQtdEstoqueDeposito()+1);
													ProdutoDAO.saveProduto(onuAntigo);									
												}
												
																					
												serialOnuAntigo2.setStatus("ATIVO");
												serialOnuAntigo2.setData_alteracao(new Date());
												SerialDAO.atualizaeSerial(serialOnuAntigo2);
												
												AlteracoesSerialDAO.saveNovo(new AlteracoesSerial(null, "ACESSO "+ac.getId()+" ESTORNO", serialOnuAntigo2,OpusERP4UI.getEmpresa(), OpusERP4UI.getUsuarioLogadoUI(), new Date()));															
										}else{
											//Cadastrar Novo Mac do Roteador se Não Encontrar.
										}
									}
									
									
									
		
								em.merge(ac);				
						
						em.getTransaction().commit();
		
						Concentrador b = ac.getBase();		
						MikrotikUtil.desconectarCliente(b.getUsuario(), b.getSenha(), b.getEndereco_ip(),Integer.parseInt(b.getPorta_api()), ac.getLogin());
						
						if(b != null){ 
							MikrotikUtil.removerAccessList(b.getUsuario(), b.getSenha(), b.getEndereco_ip(),Integer.parseInt(b.getPorta_api()),ac.getCliente().getNome_razao(), ac.getEndereco_mac());
							MikrotikUtil.liberarAccessList(b.getUsuario(), b.getSenha(), b.getEndereco_ip(),Integer.parseInt(b.getPorta_api()),ac.getId().toString(), ac.getCliente().getNome_razao(), ac.getEndereco_mac(), ac.getInterfaces(), ac.getSignal_strength());
						}	
						
						LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Fez uma Alteração de Material para um Contrato de Acesso"));		
						AlteracoesContratoDAO.save(new AlteracoesContrato(null, "ALTERAÇÃO DE MATERIAL", ac, OpusERP4UI.getUsuarioLogadoUI(), new Date()));
						
						return true;
				}catch(Exception e){
					if(em.getTransaction().isActive()){
						em.getTransaction().rollback();				
					}
					
					e.printStackTrace();
					Notify.Show("Não Foi Possivel alterar Material", Notify.TYPE_ERROR);
					LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Não Consegiu alterar material"));
					
					return false;
				}
		
		}else{
			Notify.Show("Não Foi Possivel alterar Material", Notify.TYPE_ERROR);
			return false;
		}
		
	}
	
	/**
	 * 
	 * @param ac
	 * @return true se concluir com êxito
	 * 
	 * Ações
	 * 	1- Retira Calling-Station-ID do Radius
	 * 	2- Se o regime do contrato for COMODATO estorna o Saldo e Serial
	 *     3- Se o serial não existir cadastra um novo e gera log de EXTORNO
	 *     4- Atualiza Tabela de Acesso Cliente sem Material e MAC
	 *     5- Gera logs
	 */
	public static boolean removerMaterial(AcessoCliente ac){
		
		EntityManager em = ConnUtil.getEntity();
		
		try{
			
				em.getTransaction().begin();
				
					CredenciaisAcessoDAO.alterarCredenciaisRadiusMac(ac.getLogin(), ac.getLogin(), null);
					
					if(ac.getRegime().equals("COMODATO")){					
											
						//-----Logg
						LogProdutoDAO.registraLogSemTransacao(new LogProduto(null, ac.getMaterial(), "REMOVER MATERIAL", ac.getMaterial().getQtdEstoqueDeposito(), ac.getMaterial().getQtdEstoqueDeposito()+1, new Date(), OpusERP4UI.getUsuarioLogadoUI().getUsername()));
						//-----Logg
						
						ac.getMaterial().setQtdEstoqueDeposito(ac.getMaterial().getQtdEstoqueDeposito()+1);
						ProdutoDAO.editNovo(ac.getMaterial());
						
						SerialProduto serial = SerialDAO.findByNameAndCod(ac.getMaterial(), ac.getEndereco_mac());
						
						if(serial != null){
							serial.setStatus("ATIVO");
							serial.setData_alteracao(new Date());
							SerialDAO.atualizaeSerial(serial);
							AlteracoesSerialDAO.saveNovo(new AlteracoesSerial(null, "ACESSO "+ac.getId()+" ESTORNO", serial,OpusERP4UI.getEmpresa(), OpusERP4UI.getUsuarioLogadoUI(), new Date()));
			
						}else{
							SerialProduto sp = SerialDAO.addNovoSerialSemTrasaction(ac.getId(),ac.getMaterial().getId(), ac.getEndereco_mac());						
						}
					}
					
					
					ac.setMaterial(null);
					ac.setEndereco_mac(null);
					em.merge(ac);
				
				em.getTransaction().commit();
				
				Concentrador b = ac.getBase();											
				
				if(b != null){
					MikrotikUtil.desconectarCliente(b.getUsuario(), b.getSenha(), b.getEndereco_ip(),Integer.parseInt(b.getPorta_api()), ac.getLogin());
					MikrotikUtil.removerAccessList(b.getUsuario(), b.getSenha(), b.getEndereco_ip(),Integer.parseInt(b.getPorta_api()), ac.getCliente().getNome_razao(), ac.getEndereco_mac());										
				}										
				
				LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Fez uma Remoção de Material para um Contrato de Acesso"));											
				AlteracoesContratoDAO.save(new AlteracoesContrato(null, "REMOÇÃO DE MATERIAL", ac, OpusERP4UI.getUsuarioLogadoUI(), new Date()));
												
				return true;
		}catch(Exception e){
			if(em.getTransaction().isActive()){
				em.getTransaction().rollback();				
			}
			
			e.printStackTrace();
			Notify.Show("Não Foi Possivel remover Material", Notify.TYPE_ERROR);
			LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Não Consegiu remover material"));
			
			return false;
		}
	}
	
	
	/**
	 * 
	 * @param ac
	 * @param materialAntigo
	 * @param macAntigo
	 * @return true se concluir com êxito
	 * 
	 * Ações
	 * 	1- Cadastra troca de materiais
	 * 	2- Altera Credenciais no Radius
	 * 	3- Altera Quantidade Estoque do material novo
	 * 	4- Altera Quantidade Estoque do material antigo
	 * 	5- Altera ou Cadastra Serial antigo
	 * 	6- Remove access-list e libera novamente
	 * 	7- Atualiza tabela Acesso CLiente
	 * 	8- Gera logs
	 */
	public static boolean  alterarRegimeProprio(AcessoCliente ac, Produto materialAntigo, String macAntigo, Produto onuAntigo, String serialOnuAntigo){
		
		EntityManager em = ConnUtil.getEntity();				
		
		try {							
			Cliente c = ac.getCliente();			
			Produto p = materialAntigo;			
			Produto p_novo = ac.getMaterial();
			
			em.getTransaction().begin();
			
				if(ac.getMaterial() != null && materialAntigo != null){					
					CredenciaisAcessoDAO.cadastrarTrocaMaterial(new Produto(p.getId()), new Produto(p_novo.getId()), "Alteração de Regime Proprio", new AcessoCliente(ac.getId()), new Date());
				}
	
				String endereco_mac_novo = ac.getEndereco_mac();
				
				CredenciaisAcessoDAO.alterarCredenciaisRadiusMac(ac.getLogin(), ac.getLogin(), endereco_mac_novo);			
				
				if(ac.getRegime().equals("PROPRIO") || ac.getRegime().equals("PROPRIO (PARCIAL)")){
						
					//DEVOLVE MATERIAL ANTIGO
					if(materialAntigo != null){
						if(materialAntigo.getId().equals(ac.getMaterial().getId())){
							//-----Logg
							LogProdutoDAO.registraLogSemTransacao(new LogProduto(null, materialAntigo, "ALTERAR REGIME PROPRIO", materialAntigo.getQtdEstoqueDeposito(), ac.getMaterial().getQtdEstoqueDeposito(), new Date(), OpusERP4UI.getUsuarioLogadoUI().getUsername()));
							//-----Logg
							
							materialAntigo.setQtdEstoqueDeposito(ac.getMaterial().getQtdEstoqueDeposito());
						}
						
						//-----Logg
						LogProdutoDAO.registraLogSemTransacao(new LogProduto(null, materialAntigo, "ALTERAR REGIME PROPRIO", materialAntigo.getQtdEstoque(), materialAntigo.getQtdEstoque()+1, new Date(), OpusERP4UI.getUsuarioLogadoUI().getUsername()));
						//-----Logg
						
						materialAntigo.setQtdEstoqueDeposito(materialAntigo.getQtdEstoqueDeposito()+1);
						ProdutoDAO.editNovo(materialAntigo);					

						SerialProduto serial = SerialDAO.findByNameAndCod(materialAntigo,macAntigo);
						
						if(serial != null){
							serial.setStatus("ATIVO");
							serial.setData_alteracao(new Date());																					
							SerialDAO.atualizaeSerial(serial);
							AlteracoesSerialDAO.saveNovo(new AlteracoesSerial(null, "ACESSO "+ac.getId()+" ESTORNO", serial,OpusERP4UI.getEmpresa(), OpusERP4UI.getUsuarioLogadoUI(), new Date()));																					
						}else{
							SerialDAO.addNovoSerialSemTrasaction(ac.getId(),materialAntigo.getId(), macAntigo);													
						}
					}
					
					if(onuAntigo != null){
						
						//-----Logg
						LogProdutoDAO.registraLogSemTransacao(new LogProduto(null, onuAntigo, "ALTERAR REGIME PROPRIO", onuAntigo.getQtdEstoqueDeposito(), onuAntigo.getQtdEstoqueDeposito()+1, new Date(), OpusERP4UI.getUsuarioLogadoUI().getUsername()));
						//-----Logg
						
						onuAntigo.setQtdEstoqueDeposito(onuAntigo.getQtdEstoqueDeposito()+1);
						ProdutoDAO.editNovo(onuAntigo);					

						SerialProduto serial = SerialDAO.findByNameAndCod(onuAntigo,serialOnuAntigo);
						
						if(serial != null){
							serial.setStatus("ATIVO");
							serial.setData_alteracao(new Date());																					
							SerialDAO.atualizaeSerial(serial);
							AlteracoesSerialDAO.saveNovo(new AlteracoesSerial(null, "ACESSO "+ac.getId()+" ESTORNO", serial,OpusERP4UI.getEmpresa(), OpusERP4UI.getUsuarioLogadoUI(), new Date()));																					
						}else{
							SerialDAO.addNovoSerialSemTrasaction(ac.getId(),onuAntigo.getId(), serialOnuAntigo);													
						}
						
						ac.setOnu(null);
						ac.setOnu_serial(null); 
						ac.setGpon(null);
					}
					
					
					//BAIXA ONU JUNTO COM SERIAL
					if(ac.getRegime().equals("PROPRIO (PARCIAL)") && ac.getOnu() != null){
						
							//-----Logg
							LogProdutoDAO.registraLogSemTransacao(new LogProduto(null, ac.getOnu(), "ALTERAR REGIME PROPRIO", ac.getOnu().getQtdEstoqueDeposito(), ac.getOnu().getQtdEstoqueDeposito()-1, new Date(), OpusERP4UI.getUsuarioLogadoUI().getUsername()));
							//-----Logg
							
							ac.getOnu().setQtdEstoqueDeposito(ac.getOnu().getQtdEstoqueDeposito()-1);
							ProdutoDAO.editNovo(ac.getOnu());					

							SerialProduto serial = SerialDAO.findByNameAndCod(ac.getOnu(),ac.getOnu_serial());
							
							if(serial != null){
								serial.setStatus("COMODATO");
								serial.setData_alteracao(new Date());																					
								SerialDAO.atualizaeSerial(serial);
								AlteracoesSerialDAO.saveNovo(new AlteracoesSerial(null, "ACESSO "+ac.getId()+" COMODATO", serial,OpusERP4UI.getEmpresa(), OpusERP4UI.getUsuarioLogadoUI(), new Date()));																					
							}
							
					}
					
				}	
				
				
				
				
				Concentrador b = ac.getBase();
				if(b != null && b.getWireless().equals("SIM")){				 
					MikrotikUtil.removerAccessList(b.getUsuario(), b.getSenha(), b.getEndereco_ip(),Integer.parseInt(b.getPorta_api()),c.getNome_razao(), ac.getEndereco_mac());			
					MikrotikUtil.liberarAccessList(b.getUsuario(), b.getSenha(), b.getEndereco_ip(),Integer.parseInt(b.getPorta_api()), 	ac.getId().toString(), c.getNome_razao(), endereco_mac_novo, ac.getInterfaces(), ac.getSignal_strength());
				}			
				
				ac.setBeneficio_comodato(false); 
				em.merge(ac);			
			em.getTransaction().commit();			
			
			LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "ALTERAÇÃO DE REGIME CONTRATO: "+ac.getId().toString()));			
			AlteracoesContratoDAO.save(new AlteracoesContrato(null, "ALTERAÇÃO DE REGIME PARA "+ac.getRegime(), ac, OpusERP4UI.getUsuarioLogadoUI(), new Date()));
			
			return true;
		} catch (Exception e) {
			if(em.getTransaction().isActive()){
				em.getTransaction().rollback();				
			}
			
			e.printStackTrace();
			Notify.Show("Não Foi Possivel Alterar regime", Notify.TYPE_ERROR);
			LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Não Consegiu alterar Regime"));
			
			return false;
		}
	}
	
	/**
	 * 
	 * @param ac
	 * @param materialAntigo
	 * @param macAntigo
	 * @return true se concluir com êxito
	 * 
	 * Ações
	 * 	1- Cadastra troca de materiais
	 * 	2- Altera Credenciais no Radius
	 * 	3- Altera Quantidade Estoque do material novo
	 * 	4- Altera Quantidade Estoque do material antigo
	 * 	5- Altera ou Cadastra Serial antigo
	 * 	6- Remove access-list e libera novamente
	 * 	7- Atualiza tabela Acesso CLiente
	 * 	8- Gera logs
	 */
	public static boolean  alterarRegimeComodato(AcessoCliente ac, Produto materialAntigo, String macAntigo){
		
		EntityManager em = ConnUtil.getEntity();				
		
		try {							
			Cliente c = ac.getCliente();			
			Produto p = materialAntigo;			
			Produto p_novo = ac.getMaterial();
			
			em.getTransaction().begin();
			
				if(ac.getMaterial() != null && materialAntigo != null){					
					CredenciaisAcessoDAO.cadastrarTrocaMaterial(new Produto(p.getId()), new Produto(p_novo.getId()), "Alteração de Regime Comodato", new AcessoCliente(ac.getId()), new Date());
				}
	
				String endereco_mac_novo = ac.getEndereco_mac();
				
				CredenciaisAcessoDAO.alterarCredenciaisRadiusMac(ac.getLogin(), ac.getLogin(), endereco_mac_novo);			
				
				if(ac.getRegime().equals("COMODATO")){
															
					if(p_novo != null){
						
						//-----Logg
						LogProdutoDAO.registraLogSemTransacao(new LogProduto(null, p_novo, "ALTERAR REGIME COMODATO", p_novo.getQtdEstoqueDeposito(), p_novo.getQtdEstoqueDeposito()-1, new Date(), OpusERP4UI.getUsuarioLogadoUI().getUsername()));
						//-----Logg
						
						p_novo.setQtdEstoqueDeposito(p_novo.getQtdEstoqueDeposito()-1);
						ProdutoDAO.editNovo(p_novo);					

						SerialProduto serial = SerialDAO.findByNameAndCod(p_novo,ac.getEndereco_mac());
						
						if(serial != null){
							serial.setStatus("COMODATO");
							serial.setData_alteracao(new Date());																					
							SerialDAO.atualizaeSerial(serial);
							AlteracoesSerialDAO.saveNovo(new AlteracoesSerial(null, "ACESSO "+ac.getId()+" COMODATO", serial,OpusERP4UI.getEmpresa(), OpusERP4UI.getUsuarioLogadoUI(), new Date()));																					
						}
					}		
				}	
				
				Concentrador b = ac.getBase();
				if(b != null){				 
					MikrotikUtil.removerAccessList(b.getUsuario(), b.getSenha(), b.getEndereco_ip(),Integer.parseInt(b.getPorta_api()),c.getNome_razao(), ac.getEndereco_mac());			
					MikrotikUtil.liberarAccessList(b.getUsuario(), b.getSenha(), b.getEndereco_ip(),Integer.parseInt(b.getPorta_api()), 	ac.getId().toString(), c.getNome_razao(), endereco_mac_novo, ac.getInterfaces(), ac.getSignal_strength());
				}			
				
				ac.setBeneficio_comodato(true); 
				em.merge(ac);			
			em.getTransaction().commit();			
			
			LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "ALTERAÇÃO DE REGIME CONTRATO: "+ac.getId().toString()));			
			AlteracoesContratoDAO.save(new AlteracoesContrato(null, "ALTERAÇÃO DE REGIME PARA COMODATO", ac, OpusERP4UI.getUsuarioLogadoUI(), new Date()));
			
			return true;
		} catch (Exception e) {
			if(em.getTransaction().isActive()){
				em.getTransaction().rollback();				
			}
			
			e.printStackTrace();
			Notify.Show("Não Foi Possivel Alterar regime", Notify.TYPE_ERROR);
			LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Não Consegiu alterar Regime"));
			
			return false;
		}
	}
	
	public static boolean  alterarRegimeProprioParaComodatoTotal(AcessoCliente ac, Integer codSerialRoteador, Integer codSerialOnu){
		
		EntityManager em = ConnUtil.getEntity();				
		
		try {							
			Cliente c = ac.getCliente();			
			Produto roteador = ac.getMaterial();
			Produto onu = ac.getOnu();
			
			em.getTransaction().begin();
			
				String endereco_mac_novo = ac.getEndereco_mac();				
				CredenciaisAcessoDAO.alterarCredenciaisRadiusMac(ac.getLogin(), ac.getLogin(), endereco_mac_novo);			
															
				if(roteador != null){
					
					//-----Logg
					LogProdutoDAO.registraLogSemTransacao(new LogProduto(null, roteador, "ALTERAR REGIME COMODATO TOTAL", roteador.getQtdEstoque(), roteador.getQtdEstoque()-1, new Date(), OpusERP4UI.getUsuarioLogadoUI().getUsername()));
					//-----Logg
					
					roteador.setQtdEstoqueDeposito(roteador.getQtdEstoqueDeposito()-1);
					ProdutoDAO.editNovo(roteador);					

					SerialProduto serialRoteador = SerialDAO.find(codSerialRoteador);
					
					if(serialRoteador != null){
						serialRoteador.setStatus("COMODATO");
						serialRoteador.setData_alteracao(new Date());																					
						SerialDAO.atualizaeSerial(serialRoteador);
						AlteracoesSerialDAO.saveNovo(new AlteracoesSerial(null, "ACESSO "+ac.getId()+" COMODATO", serialRoteador,OpusERP4UI.getEmpresa(), OpusERP4UI.getUsuarioLogadoUI(), new Date()));																					
					}
				}	
				
				if(onu != null){
					
					//-----Logg
					LogProdutoDAO.registraLogSemTransacao(new LogProduto(null, onu, "ALTERAR REGIME COMODATO TOTAL", onu.getQtdEstoque(), onu.getQtdEstoque()-1, new Date(), OpusERP4UI.getUsuarioLogadoUI().getUsername()));
					//-----Logg
					
					onu.setQtdEstoqueDeposito(onu.getQtdEstoqueDeposito()-1);
					ProdutoDAO.editNovo(onu);					

					SerialProduto serialOnu = SerialDAO.find(codSerialOnu);
					
					if(serialOnu != null){
						serialOnu.setStatus("COMODATO");
						serialOnu.setData_alteracao(new Date());																					
						SerialDAO.atualizaeSerial(serialOnu);
						AlteracoesSerialDAO.saveNovo(new AlteracoesSerial(null, "ACESSO "+ac.getId()+" COMODATO", serialOnu,OpusERP4UI.getEmpresa(), OpusERP4UI.getUsuarioLogadoUI(), new Date()));																					
					}
				}	
				
				
				Concentrador b = ac.getBase();
				if(b != null){				 
					MikrotikUtil.removerAccessList(b.getUsuario(), b.getSenha(), b.getEndereco_ip(),Integer.parseInt(b.getPorta_api()),c.getNome_razao(), ac.getEndereco_mac());			
					MikrotikUtil.liberarAccessList(b.getUsuario(), b.getSenha(), b.getEndereco_ip(),Integer.parseInt(b.getPorta_api()), 	ac.getId().toString(), c.getNome_razao(), endereco_mac_novo, ac.getInterfaces(), ac.getSignal_strength());
				}			
				
				ac.setBeneficio_comodato(true); 
				em.merge(ac);			
			em.getTransaction().commit();			
			
			LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "ALTERAÇÃO DE REGIME CONTRATO: "+ac.getId().toString()));			
			AlteracoesContratoDAO.save(new AlteracoesContrato(null, "ALTERAÇÃO DE REGIME PARA COMODATO (TOTAL)", ac, OpusERP4UI.getUsuarioLogadoUI(), new Date()));
			
			return true;
		} catch (Exception e) {
			if(em.getTransaction().isActive()){
				em.getTransaction().rollback();				
			}
			
			e.printStackTrace();
			Notify.Show("Não Foi Possivel Alterar regime", Notify.TYPE_ERROR);
			LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Não Consegiu alterar Regime"));
			
			return false;
		}
	}
	
	public static boolean  alterarRegimeProprioParcialParaComodatoTotal(AcessoCliente ac, Integer codSerialRoteador){
		
		EntityManager em = ConnUtil.getEntity();				
		
		try {							
			Cliente c = ac.getCliente();			
			Produto roteador = ac.getMaterial();
						
			em.getTransaction().begin();
			
				String endereco_mac_novo = ac.getEndereco_mac();				
				CredenciaisAcessoDAO.alterarCredenciaisRadiusMac(ac.getLogin(), ac.getLogin(), endereco_mac_novo);			
															
				if(roteador != null){
					
					//-----Logg
					LogProdutoDAO.registraLogSemTransacao(new LogProduto(null, roteador, "ALTERAR REGIME COMODATO TOTAL", roteador.getQtdEstoque(), roteador.getQtdEstoque()-1, new Date(), OpusERP4UI.getUsuarioLogadoUI().getUsername()));
					//-----Logg
					
					roteador.setQtdEstoqueDeposito(roteador.getQtdEstoqueDeposito()-1);
					ProdutoDAO.editNovo(roteador);					

					SerialProduto serialRoteador = SerialDAO.find(codSerialRoteador);
					
					if(serialRoteador != null){
						serialRoteador.setStatus("COMODATO");
						serialRoteador.setData_alteracao(new Date());																					
						SerialDAO.atualizaeSerial(serialRoteador);
						AlteracoesSerialDAO.saveNovo(new AlteracoesSerial(null, "ACESSO "+ac.getId()+" COMODATO", serialRoteador,OpusERP4UI.getEmpresa(), OpusERP4UI.getUsuarioLogadoUI(), new Date()));																					
					}
				}	
								
				Concentrador b = ac.getBase();
				if(b != null){				 
					MikrotikUtil.removerAccessList(b.getUsuario(), b.getSenha(), b.getEndereco_ip(),Integer.parseInt(b.getPorta_api()),c.getNome_razao(), ac.getEndereco_mac());			
					MikrotikUtil.liberarAccessList(b.getUsuario(), b.getSenha(), b.getEndereco_ip(),Integer.parseInt(b.getPorta_api()), 	ac.getId().toString(), c.getNome_razao(), endereco_mac_novo, ac.getInterfaces(), ac.getSignal_strength());
				}			
				
				ac.setBeneficio_comodato(true); 
				em.merge(ac);			
			em.getTransaction().commit();			
			
			LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "ALTERAÇÃO DE REGIME CONTRATO: "+ac.getId().toString()));			
			AlteracoesContratoDAO.save(new AlteracoesContrato(null, "ALTERAÇÃO DE REGIME PARA COMODATO (TOTAL)", ac, OpusERP4UI.getUsuarioLogadoUI(), new Date()));
			
			return true;
		} catch (Exception e) {
			if(em.getTransaction().isActive()){
				em.getTransaction().rollback();				
			}
			
			e.printStackTrace();
			Notify.Show("Não Foi Possivel Alterar regime", Notify.TYPE_ERROR);
			LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Não Consegiu alterar Regime"));
			
			return false;
		}
	}
	
	public static boolean  alterarRegimeComodatoParaComodatoTotal(AcessoCliente ac, Integer codSerialOnu){
		
		EntityManager em = ConnUtil.getEntity();				
		
		try {							
			Cliente c = ac.getCliente();			
			Produto roteador = ac.getMaterial();
			Produto onu = ac.getOnu();
			
			em.getTransaction().begin();
			
				String endereco_mac_novo = ac.getEndereco_mac();				
				CredenciaisAcessoDAO.alterarCredenciaisRadiusMac(ac.getLogin(), ac.getLogin(), endereco_mac_novo);			
															
								
				if(onu != null){
					
					//-----Logg
					LogProdutoDAO.registraLogSemTransacao(new LogProduto(null, onu, "ALTERAR REGIME COMODATO TOTAL", onu.getQtdEstoque(), onu.getQtdEstoque()-1, new Date(), OpusERP4UI.getUsuarioLogadoUI().getUsername()));
					//-----Logg
					
					onu.setQtdEstoqueDeposito(onu.getQtdEstoqueDeposito()-1);
					ProdutoDAO.editNovo(onu);					

					SerialProduto serialOnu = SerialDAO.find(codSerialOnu);
					
					if(serialOnu != null){
						serialOnu.setStatus("COMODATO");
						serialOnu.setData_alteracao(new Date());																					
						SerialDAO.atualizaeSerial(serialOnu);
						AlteracoesSerialDAO.saveNovo(new AlteracoesSerial(null, "ACESSO "+ac.getId()+" COMODATO", serialOnu,OpusERP4UI.getEmpresa(), OpusERP4UI.getUsuarioLogadoUI(), new Date()));																					
					}
				}	
				
				
				Concentrador b = ac.getBase();
				if(b != null){				 
					MikrotikUtil.removerAccessList(b.getUsuario(), b.getSenha(), b.getEndereco_ip(),Integer.parseInt(b.getPorta_api()),c.getNome_razao(), ac.getEndereco_mac());			
					MikrotikUtil.liberarAccessList(b.getUsuario(), b.getSenha(), b.getEndereco_ip(),Integer.parseInt(b.getPorta_api()), 	ac.getId().toString(), c.getNome_razao(), endereco_mac_novo, ac.getInterfaces(), ac.getSignal_strength());
				}			
				
				ac.setBeneficio_comodato(true); 
				em.merge(ac);			
			em.getTransaction().commit();			
			
			LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "ALTERAÇÃO DE REGIME CONTRATO: "+ac.getId().toString()));			
			AlteracoesContratoDAO.save(new AlteracoesContrato(null, "ALTERAÇÃO DE REGIME PARA COMODATO (TOTAL)", ac, OpusERP4UI.getUsuarioLogadoUI(), new Date()));
			
			return true;
		} catch (Exception e) {
			if(em.getTransaction().isActive()){
				em.getTransaction().rollback();				
			}
			
			e.printStackTrace();
			Notify.Show("Não Foi Possivel Alterar regime", Notify.TYPE_ERROR);
			LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Não Consegiu alterar Regime"));
			
			return false;
		}
	}

	/**
	 * 
	 * @param ac
	 * @param clienteAntigo
	 * @return true se concluir com êxito
	 * 
	 * Ações
	 * 	1- Altera cliente de boletos abertos do Contrato
	 * 	2- Altera status do cliente novo para ATIVO
	 * 	3- Altera cilente da tabela Acesso Cliente
	 * 	4- Gera logs
	 */
	public static boolean alterarTitularidade(AcessoCliente ac, Cliente clienteAntigo){
		
		EntityManager em = ConnUtil.getEntity();
		
		try {
			
			em.getTransaction().begin();			
			
				ContasReceberDAO.mudarTitular(clienteAntigo.getId(),ac.getCliente().getId(),ac.getId());				
				ClienteDAO.changeStatusFromActive(ac.getCliente());
				em.merge(ac);
				
			em.getTransaction().commit();
								
			//Atualiza Access-List
//			if(ac.getBase()!=null && ac.getEndereco_mac() != null && !ac.getEndereco_mac().equals("") && !ac.getEndereco_mac().isEmpty() && ac.getCliente() != null){
//				MikrotikUtil.removerAccessList(ac.getBase().getUsuario(), ac.getBase().getSenha(),ac.getBase().getEndereco_ip() , Integer.parseInt(ac.getBase().getPorta_api()), ac.getCliente().getNome_razao(), ac.getEndereco_mac());				
//				MikrotikUtil.liberarAccessList(ac.getBase().getUsuario(), ac.getBase().getSenha(),ac.getBase().getEndereco_ip() ,  	Integer.parseInt(ac.getBase().getPorta_api()),ac.getId().toString(), ac.getCliente().getNome_razao(), ac.getEndereco_mac(),ac.getInterfaces(), ac.getSignal_strength());
//			}
					
			LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Fez uma Alteração de Titularidade para um Contrato de Acesso"));
			AlteracoesContratoDAO.save(new AlteracoesContrato(null, "ALTERAÇÃO DE TITULARIDADE", ac, OpusERP4UI.getUsuarioLogadoUI(), new Date()));
			
			return true;
		}catch (Exception e) {
			if(em.getTransaction().isActive()){
				em.getTransaction().rollback();				
			}
			
			e.printStackTrace();
			Notify.Show("Não Foi Possivel alterar titularidade", Notify.TYPE_ERROR);
			LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Não Consegiu alterar titularidade"));
			
			return false;
		}		
	}
	
	/**
	 * 
	 * @param contrato
	 * @return true se concluir com êxito
	 * 
	 * Ações
	 * 	1- Altera tabela de acessocliente 
	 * 	2- Gera logs
	 */
	public static boolean alterarFiador(AcessoCliente contrato){
		
		EntityManager em = ConnUtil.getEntity();
		try{

			em.getTransaction().begin();
			em.merge(contrato);
			em.getTransaction().commit();
			
			LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Fez uma Alteração de Fiador para um Contrato de Acesso"));
			AlteracoesContratoDAO.save(new AlteracoesContrato(null, "ALTERAÇÃO DE FIADOR", contrato, OpusERP4UI.getUsuarioLogadoUI(), new Date()));
			
			return true;
		}catch(Exception e){
			if(em.getTransaction().isActive()){
				em.getTransaction().rollback();				
			}
			
			e.printStackTrace();
			Notify.Show("Não Foi Possivel alterar titularidade", Notify.TYPE_ERROR);
			LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Não Consegiu alterar titularidade"));
			
			return false;
		}
	}
	
	public static boolean alterarTipoNF(AcessoCliente contrato){
		
		EntityManager em = ConnUtil.getEntity();
		try{

			em.getTransaction().begin();
			em.merge(contrato);
			em.getTransaction().commit();
			
			LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Fez uma Alteração de Tipo NF para um Contrato de Acesso"));
			AlteracoesContratoDAO.save(new AlteracoesContrato(null, "ALTERAÇÃO DE TIPO NF", contrato, OpusERP4UI.getUsuarioLogadoUI(), new Date()));
			
			return true;
		}catch(Exception e){
			if(em.getTransaction().isActive()){
				em.getTransaction().rollback();				
			}
			
			e.printStackTrace();
			Notify.Show("Não Foi Possivel alterar tipo NF", Notify.TYPE_ERROR);
			LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Não Consegiu alterar tipo NF"));
			
			return false;
		}
	}
	
	/**
	 * 
	 * @param ac
	 * @param novaData
	 * @return true se concluir com êxito
	 * 
	 * Ações
	 * 	1- Altera datas dos Boletos
	 * 	2- Gera logs
	 */
	public static boolean alterarVencimento(AcessoCliente ac, Date novaData){		
		
		EntityManager em = ConnUtil.getEntity();
		
		try{
								
			List<ContasReceber> resConta = ContasReceberDAO.procurarBoletosDoAcessoPorContrato(ac.getId());		
			ContasReceber ct = resConta.get(0);
										
			List<ContasReceber> result = new ArrayList<>();		
			Date novaDataContrato = CredenciaisAcessoDAO.calcularDataVencContrato(ac.getContrato().getId(), novaData);	
			ac.setData_venc_contrato(novaData);			
			
			String valorProrata = ContasReceberDAO.CalcularProRataBoleto(ac, novaData);
			Integer qtdDiasProRata = ContasReceberDAO.CalcularQtdDiasProRata(ac, novaData);
			
			result.add(new ContasReceber(ct.getId(),ct.getN_doc(),ct.getN_numero(),valorProrata,ct.getValor_pagamento(),ct.getData_emissao(),novaData,ct.getData_pagamento(),ct.getData_baixa(),ct.getData_exclusao(),ct.getForma_pgto(),ct.getTipo_baixa(),ct.getControle(),ct.getCentro_custo(),ct.getStatus(),ct.getDesbloqueado(),ct.getBloquear(),ct.getDesbloquear(),ct.getBloqueado(),ct.getTipo_titulo(),new Cliente(ct.getCliente().getId()),ct.getEmpresa_id(), qtdDiasProRata));                        
			
			int i = 0;
			for(ContasReceber cont: resConta){
				
				if(cont.getId() != ct.getId()){
					 	
						i++;
						LocalDate localDate = new LocalDate(novaData);
						LocalDate moreDays = localDate.plusMonths(i);	
						Date dataBoleto = moreDays.toDate();
					
						result.add(new ContasReceber(cont.getId(),cont.getN_doc(),cont.getN_numero(),cont.getValor_titulo(),cont.getValor_pagamento(),cont.getData_emissao(),dataBoleto,cont.getData_pagamento(),cont.getData_baixa(),cont.getData_exclusao(),cont.getForma_pgto(),cont.getTipo_baixa(),cont.getControle(),cont.getCentro_custo(),cont.getStatus(),cont.getDesbloqueado(),cont.getBloquear(),cont.getDesbloquear(),cont.getBloqueado(),cont.getTipo_titulo(),new Cliente(cont.getCliente().getId()),cont.getEmpresa_id()));
						ac.setData_venc_contrato(dataBoleto);			
				}	
			}		
			
			em.getTransaction().begin();
			
		        for(ContasReceber cont: result){			
		        		cont.setPlano_contrato(ac.getPlano().getId());
						em.merge(cont);
				}			
				
		        em.merge(ac);
			em.getTransaction().commit();
			
			LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Fez uma Alteração de Vencimento para um Contrato de Acesso"));	
			AlteracoesContratoDAO.save(new AlteracoesContrato(null, "ALTERAÇÃO DE VENCIMENTO", ac, OpusERP4UI.getUsuarioLogadoUI(), new Date()));
		
			return true;
		}catch(Exception e){

			if(em.getTransaction().isActive()){
				em.getTransaction().rollback();				
			}
			
			e.printStackTrace();
			Notify.Show("Não Foi Possivel alterar vencimento", Notify.TYPE_ERROR);
			LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Não Consegiu alterar vencimento"));
			
			return false;				
		}
		
	}
	
	/**
	 * 
	 * @param ac
	 * @return true se concluir com êxito
	 * 
	 * Ações
	 * 	1- Atualiza tabela acesso cliente com novo endereço
	 * 	2- Gera logs
	 */
	public static boolean alterarEndereco(AcessoCliente ac){
		
		EntityManager em = ConnUtil.getEntity();
		
		try{
			em.getTransaction().begin();
				em.merge(ac);
			em.getTransaction().commit();
			
			LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Fez uma Alteração de Endereço para um Contrato de Acesso"));	
			AlteracoesContratoDAO.save(new AlteracoesContrato(null, "ALTERAÇÃO DE ENDEREÇO", ac, OpusERP4UI.getUsuarioLogadoUI(), new Date()));
			
			return true;
		}catch(Exception e){
			if(em.getTransaction().isActive()){
				em.getTransaction().rollback();				
			}
			
			e.printStackTrace();
			Notify.Show("Não Foi Possivel alterar endereço", Notify.TYPE_ERROR);
			LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Não Consegiu alterar endereço"));
			
			return false;				
		}
	}


	/**
	 * 
	 * @param ac
	 * @return true se concluir com êxito
	 * 
	 */
	public static boolean suspenderContrato(AcessoCliente ac, Date dataFimSuspensao){
		
		EntityManager em = ConnUtil.getEntity();
		
		try{
		
			//Excluir Registros das Tabelas RadCHECK.
			Query qrrc = em.createQuery("select rc from RadCheck rc where rc.username = :usuario", RadCheck.class);
			qrrc.setParameter("usuario", ac.getLogin());
			
			if(qrrc.getResultList().size() >0){
				
				List<RadCheck> credencias = qrrc.getResultList(); 
				for(RadCheck rc:credencias){
					em.remove(rc);
				}
			}
			
			//Excluir Registros das Tabelas RadReply.
			Query qrr = em.createQuery("select rr from RadReply rr where rr.username = :usuario", RadReply.class);
			qrr.setParameter("usuario", ac.getLogin());
			
			if(qrr.getResultList().size() >0){
				
				List<RadReply> marcacoes = qrr.getResultList(); 
				for(RadReply rr:marcacoes){
					em.remove(rr);
				}
			}
			
			//Excluir Registros das Tabelas RadUSerGroup.
			Query qrug = em.createQuery("select rug from RadUserGgroup rug where rug.username = :usuario", RadUserGgroup.class);
			qrug.setParameter("usuario", ac.getLogin());
			
			if(qrug.getResultList().size() >0){
				
				List<RadUserGgroup> planos = qrug.getResultList(); 
				for(RadUserGgroup rug:planos){
					em.remove(rug);
				}
			}
			
			
			List<ContasReceber> resConta = ContasReceberDAO.procurarBoletosDoAcessoPorContrato(ac.getId());		
						
			Date dCadastro = ac.getData_cadastro();
			Integer vigencia = ac.getContrato().getVigencia();			
			Integer mesesUtilizados = Months.monthsBetween(new DateTime(dCadastro), new DateTime(new Date())).getMonths();
			Integer mesesRestantes = vigencia - mesesUtilizados;
			
			
			em.getTransaction().begin();
			
			Date dVencContrato = new DateTime(dataFimSuspensao).plusMonths(mesesRestantes).toDate();
			//ac.setData_venc_contrato(dVencContrato);
			ac.setStatus_2("SUSPENSO");
					
			if(resConta != null){
				int i = 1;
				for(ContasReceber cont: resConta){
					
					cont.setData_vencimento(new DateTime(dataFimSuspensao).plusMonths(i).toDate()); 
					em.merge(cont);	
					
					ac.setData_venc_contrato(new DateTime(dataFimSuspensao).plusMonths(i).toDate());					
					i++;
				}		
			}
							        		        
		    em.merge(ac);
		    
			em.getTransaction().commit();
			
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");			
			
			AlteracoesContratoDAO.save(new AlteracoesContrato(null, "SUSPENSAO DE CONTRATO ATÉ "+sdf.format(dataFimSuspensao).toString(), ac, OpusERP4UI.getUsuarioLogadoUI(), new Date()));
						
			return true;
			
		}catch(Exception e){
			e.printStackTrace();
			
			return false;
		}
		
	}


	public static boolean verificarPendenciaPorEndereco(Endereco endereco){
		
		try{
			EntityManager em = ConnUtil.getEntity();
			
			String regexNova = "^[0-9]{4}/[0-9]{2}-[0-9]{2}/[0-9]{2}";
			String regexAntiga = "^[0-9]{4}/[0-9]{2}/[0-9]{2}";
			
			Query qn = em.createNativeQuery("select * from contas_receber cr where "+				
											"cr.n_doc REGEXP :rNova "+ "and cr.status_2 !='NEGATIVADO' "+				
											"or "+							
											"cr.n_doc REGEXP :rAntiga " + "and cr.status_2 !='NEGATIVADO' " + 
											"ORDER BY cr.data_vencimento DESC ", ContasReceber.class);
			
			qn.setParameter("rNova", regexNova);
			qn.setParameter("rAntiga", regexAntiga);
			
			for (ContasReceber cr : (List<ContasReceber>)qn.getResultList()) {
				String numeroDoc = cr.getN_doc();
				Integer codAcesso = Integer.parseInt(numeroDoc.split("/")[0].toString());				
				AcessoCliente acesso = em.find(AcessoCliente.class,codAcesso);
				
				if(acesso.getEndereco().getEndereco().equals(endereco.getEndereco()) && 
						acesso.getEndereco().getBairro().equals(endereco.getBairro()) &&
						acesso.getEndereco().getNumero().equals(endereco.getNumero()) &&
						acesso.getEndereco().getCidade().equals(acesso.getEndereco().getCidade())){
					
					return true;
					
				}
			}
					
			return false;
		}catch(Exception e){
			e.printStackTrace();
			
			return false;
		}
	}


	public static boolean LiberarColocarVerificacaoPlanoValorMenor(AcessoCliente contrato){
		
		try{
			EntityManager em = ConnUtil.getEntity();
			
			em.getTransaction().begin();
			
			contrato.setN_controla_vlr_plano(!contrato.isN_controla_vlr_plano());		
			em.merge(contrato);
			
			em.getTransaction().commit();
			
			AlteracoesContratoDAO.save(new AlteracoesContrato(null, "LIBEROU/COLOCOU VERIFICAÇÃO DE PLANO DE MENOR VALOR", contrato, OpusERP4UI.getUsuarioLogadoUI(), new Date()));
			
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean LimparDataAlteracaoPlano(AcessoCliente contrato){
		try{
			EntityManager em = ConnUtil.getEntity();
			
			em.getTransaction().begin();
			
			contrato.setData_alteracao_plano(null); 		
			em.merge(contrato);
			
			em.getTransaction().commit();
			
			AlteracoesContratoDAO.save(new AlteracoesContrato(null, "LIMPOU A DATA DE ALTERAÇÃO DE PLANO", contrato, OpusERP4UI.getUsuarioLogadoUI(), new Date()));
			
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}

	public static AcessoCliente buscarContrato(Integer cod){
		try{
			EntityManager em = ConnUtil.getEntity();
			AcessoCliente contrato = em.find(AcessoCliente.class, cod);
			return contrato;
			
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		
	}
}

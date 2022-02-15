package com.digital.opuserp.view.modulos.acesso.contratos_acesso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Months;

import com.digital.opuserp.dao.ContasReceberDAO;
import com.digital.opuserp.domain.AcessoCliente;
import com.digital.opuserp.domain.Concentrador;
import com.digital.opuserp.domain.ContasReceber;
import com.digital.opuserp.domain.RadAcct;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.DataUtil;
import com.digital.opuserp.util.Real;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class VisualizarContratoDadosCliente extends Window {

	private Item item;
	Button btCancelar;
	Button btAtualizar;
	Button btDesconectar;
	VerticalLayout vlRoot;
	FieldGroup fieldGroup;
	Integer codAcesso;
	AcessoCliente ac;
	Concentrador base; 
	TabSheet tbRoot;
	VerticalLayout vlconexao;

	Map<String, String> regTable;
	Map<String, String> activeConnections;
	RadAcct radacct;
	Map<String, String> queue;
	String bc = "0,00";
	String bi = "0,00";
	
	boolean logado;
	boolean statusConcentrador;
	boolean comunicacao;
	
	EntityManager em = ConnUtil.getEntity();
	
	public VisualizarContratoDadosCliente(Item item, String title, boolean modal){
		this.item = item;
		
		
		if (item.getItemProperty("id").getValue() != null) {
			codAcesso = (Integer) item.getItemProperty("id").getValue();
			ac = em.find(AcessoCliente.class,codAcesso);
			if(item.getItemProperty("base").getValue()!=null){
				base = (Concentrador) item.getItemProperty("base").getValue();					
			}
		}
		
		setWidth("950px");
		setHeight("800px");
		
		
		setCaption(title);
		setModal(modal);
		setResizable(false);
		setClosable(false);
		center();
		
		vlRoot = new VerticalLayout();	
		vlRoot.setWidth("100%");
		vlRoot.setMargin(true);
//		vlRoot.setStyleName("border-form");
		
		setContent(new VerticalLayout(){
			{
				setWidth("100%");
				setMargin(true);
				addComponent(vlRoot);
				
				HorizontalLayout hlButtons = new HorizontalLayout();
				hlButtons.setStyleName("hl_buttons_bottom");
				hlButtons.setSpacing(true);
				hlButtons.setMargin(true);
				hlButtons.addComponent(buildBtAtualizar());
				hlButtons.addComponent(buildBtCancelar());
				btCancelar.focus();
				
				addComponent(hlButtons);
				setComponentAlignment(hlButtons, Alignment.BOTTOM_RIGHT);
			}

		});
		
		buildLayout();
	}
	
	String url_chart = "";
	

	
	String carencia = "";
	public Table buildLayout(){
		
		fieldGroup = new FieldGroup(item);

		VerticalLayout vlContato = new VerticalLayout(){
			{
				setMargin(true);
				setStyleName("horizontal-tabsheet");
				
				addComponent(new HorizontalLayout(){
					{
						setWidth("100%");
						
						FormLayout fl0 = new FormLayout(){					
							{
								setMargin(true);
								setSpacing(true);
								setStyleName("form-cutom");		
																
								TextField tfCodAcesso = new TextField("Nº do Contrato");				
								tfCodAcesso.setWidth("80px");	
								
								if(ac != null && ac.getId() != null){
									tfCodAcesso.setValue(ac.getId().toString());
								}
								
								tfCodAcesso.setStyleName("caption-align-fornecedores");
								tfCodAcesso.setReadOnly(true);
					
								addComponent(tfCodAcesso);
								setExpandRatio(tfCodAcesso, 2);						
							}
						};
						
						addComponent(fl0);
						setExpandRatio(fl0, 0.67f);
						
						FormLayout fl1 = new FormLayout(){					
							{
																
								setMargin(true);
								setSpacing(true);
								setStyleName("form-cutom");		
																
								TextField txtCodigoCartao = new TextField("Cartão Cliente");				
								txtCodigoCartao.setWidth("80px");	
								
								if(ac != null && ac.getId() != null && ac.getCodigo_cartao() != null){
									txtCodigoCartao.setValue(ac.getCodigo_cartao().toString());
								}
								
								txtCodigoCartao.setStyleName("caption-align-fornecedores");
								txtCodigoCartao.setReadOnly(true);
					
								addComponent(txtCodigoCartao);
													
							}
						};
						
						addComponent(fl1);
						
						setExpandRatio(fl1, 0.32f);
						
						
					}
				});
				
				
				addComponent(new HorizontalLayout(){
							{	
								addComponent(new FormLayout(){					
									{
										setMargin(true);
										setSpacing(true);
										setStyleName("form-cutom-new");		
																										
										TextField tfCodCliente = new TextField("Cliente");				
										tfCodCliente.setWidth("60px");				
										tfCodCliente.setValue(ac.getCliente().getId().toString());				
										tfCodCliente.setStyleName("caption-align-fornecedores");
										tfCodCliente.setReadOnly(true);
							
										addComponent(tfCodCliente);
										setExpandRatio(tfCodCliente, 2);						
									}
								});
								
								addComponent(new FormLayout(){					
									{
										setMargin(true);
										setSpacing(true);
										setStyleName("form-cutom-new");		
																										
										TextField tfCliente = new TextField();				
										tfCliente.setWidth("460px");				
										tfCliente.setValue(ac.getCliente().getNome_razao());				
//										tfCliente.setStyleName("caption-align-fornecedores");
										tfCliente.setReadOnly(true);
							
										addComponent(tfCliente);
										setExpandRatio(tfCliente, 2);						
									}
								});
								
								addComponent(new FormLayout(){					
									{
										setMargin(true);
										setSpacing(true);
										setStyleName("form-cutom-new");		
																										
										TextField txtCpfCliente = new TextField();				
										txtCpfCliente.setWidth("104px");				
										txtCpfCliente.setValue(ac.getCliente().getDoc_cpf_cnpj());														
										txtCpfCliente.setReadOnly(true);
							
										addComponent(txtCpfCliente);												
									}
								});
								
							}
						});	
				addComponent(new HorizontalLayout(){
							{	
								addComponent(new FormLayout(){					
									{
										setMargin(true);
										setSpacing(true);
										setStyleName("form-cutom");		
									
										TextField tfCodfiador = new TextField("Fiador");				
										tfCodfiador.setWidth("60px");		
										if(ac.getFiador()!=null){
											tfCodfiador.setValue(ac.getFiador().getId().toString());					
										}
										tfCodfiador.setStyleName("caption-align-fornecedores");
										tfCodfiador.setReadOnly(true);
							
										addComponent(tfCodfiador);
										setExpandRatio(tfCodfiador, 2);						
									}
								});
								
								addComponent(new FormLayout(){					
									{
										setMargin(true);
										setSpacing(true);
										setStyleName("form-cutom");		
																		
										TextField tfFiador = new TextField();				
										tfFiador.setWidth("460px");				
										
										if(ac.getFiador()!=null){
											tfFiador.setValue(ac.getFiador().getNome_razao());					
										}
										tfFiador.setReadOnly(true);
							
										addComponent(tfFiador);
										setExpandRatio(tfFiador, 2);						
									}
								});
							}
						});	
				addComponent(new HorizontalLayout(){
							{	
								addComponent(new FormLayout(){					
									{
										setMargin(true);
										setSpacing(true);
										setStyleName("form-cutom");		
									
										TextField tfContratoAtual = new TextField("Contrato Atual");				
										tfContratoAtual.setWidth("360px");		
										tfContratoAtual.setValue(ac.getContrato().getNome());														
										tfContratoAtual.setStyleName("caption-align-fornecedores");
										tfContratoAtual.setReadOnly(true);
							
										addComponent(tfContratoAtual);
										setExpandRatio(tfContratoAtual, 2);						
									}
								});
								
								addComponent(new FormLayout(){					
									{
										setMargin(true);
										setSpacing(true);
										setStyleName("form-cutom");		
																		
										TextField tfContratoRegime = new TextField("Regime");				
										tfContratoRegime.setWidth("117px");				
										tfContratoRegime.setValue(ac.getRegime());					
										tfContratoRegime.setReadOnly(true);
										addComponent(tfContratoRegime);
										setExpandRatio(tfContratoRegime, 2);						
									}
								});
							}
						});	
				
					//ENDEREÇO
						addComponent(new FormLayout(){					
							{
								setMargin(true);
								setSpacing(true);
								setStyleName("form-cutom-new");		
							
								TextField tfCep = new TextField("CEP");				
								tfCep.setWidth("100px");	
								if(ac.getEndereco()!=null && ac.getEndereco().getCep()!=null){
									tfCep.setValue(ac.getEndereco().getCep());														
								}
								tfCep.setStyleName("caption-align-fornecedores");
								tfCep.setReadOnly(true);
					
								addComponent(tfCep);
								setExpandRatio(tfCep, 2);						
							}
						});
								
					addComponent(
							new HorizontalLayout(){
								{
								addComponent(new FormLayout(){					
									{
										setMargin(true);
										setSpacing(true);
										setStyleName("form-cutom");		
																		
										TextField tfEndereco = new TextField("Endereço");				
										tfEndereco.setWidth("550px");	
										if(ac.getEndereco()!=null && ac.getEndereco().getEndereco()!=null){
											tfEndereco.setValue(ac.getEndereco().getEndereco());					
										}
										tfEndereco.setReadOnly(true);
										tfEndereco.setStyleName("caption-align-fornecedores");
										
										addComponent(tfEndereco);
										setExpandRatio(tfEndereco, 2);						
									}
								});
								
								addComponent(new FormLayout(){					
									{
										setMargin(true);
										setSpacing(true);
										setStyleName("form-cutom");		
										
										TextField tfNumero = new TextField("Nº");				
										tfNumero.setWidth("64px");		
										if(ac.getEndereco()!=null && ac.getEndereco().getNumero()!=null){
											tfNumero.setValue(ac.getEndereco().getNumero());					
										}
										tfNumero.setReadOnly(true);
										
										addComponent(tfNumero);
										setExpandRatio(tfNumero, 2);						
									}
								});
							}
						});
				
					addComponent(
							new HorizontalLayout(){
								{		
								addComponent(new FormLayout(){					
									{
										setMargin(true);
										setSpacing(true);
										setStyleName("form-cutom");		
																		
										TextField tfBairro = new TextField("Bairro");				
										tfBairro.setWidth("190px");		
										if(ac.getEndereco()!=null && ac.getEndereco().getBairro()!=null){
											tfBairro.setValue(ac.getEndereco().getBairro());					
										}
										tfBairro.setReadOnly(true);
										tfBairro.setStyleName("caption-align-fornecedores");
										
										addComponent(tfBairro);
										setExpandRatio(tfBairro, 2);						
									}
								});
								
								addComponent(new FormLayout(){					
									{
										setMargin(true);
										setSpacing(true);
										setStyleName("form-cutom");		
										
										TextField tfCidade = new TextField("Cidade");				
										tfCidade.setWidth("140px");	
										if(ac.getEndereco()!=null && ac.getEndereco().getCidade()!=null){
											tfCidade.setValue(ac.getEndereco().getCidade());					
										}
										tfCidade.setReadOnly(true);
										
										addComponent(tfCidade);
										setExpandRatio(tfCidade, 2);						
									}
								});
								
								addComponent(new FormLayout(){					
									{
										setMargin(true);
										setSpacing(true);
										setStyleName("form-cutom");		
										
										TextField tfUf = new TextField("UF");				
										tfUf.setWidth("42px");		
										if(ac.getEndereco()!=null && ac.getEndereco().getUf()!=null){
											tfUf.setValue(ac.getEndereco().getUf());					
										}
										tfUf.setReadOnly(true);
										
										addComponent(tfUf);
										setExpandRatio(tfUf, 2);						
									}
								});
								
								addComponent(new FormLayout(){					
									{
										setMargin(true);
										setSpacing(true);
										setStyleName("form-cutom");		
										
										TextField tfPais = new TextField("Pais");				
										tfPais.setWidth("128px");			
										if(ac.getEndereco()!=null && ac.getEndereco().getPais()!=null){
											tfPais.setValue(ac.getEndereco().getPais());					
										}
										tfPais.setReadOnly(true);
										
										addComponent(tfPais);
										setExpandRatio(tfPais, 2);						
									}
								});	
							}
						});
				
					addComponent(new HorizontalLayout(){
								{		
								addComponent(new FormLayout(){					
									{
										setMargin(true);
										setSpacing(true);
										setStyleName("form-cutom");		
																		
										TextField tfComplemnto= new TextField("Complemento");				
										tfComplemnto.setWidth("285px");	
										if(ac.getEndereco()!=null && ac.getEndereco().getComplemento()!=null){
											tfComplemnto.setValue(ac.getEndereco().getComplemento());					
										}
										tfComplemnto.setReadOnly(true);
										tfComplemnto.setStyleName("caption-align-fornecedores");
										
										addComponent(tfComplemnto);
										setExpandRatio(tfComplemnto, 2);						
									}
								});
								
								addComponent(new FormLayout(){					
									{
										setMargin(true);
										setSpacing(true);
										setStyleName("form-cutom");		
										
										TextField tfReferencia = new TextField("Referencia");				
										tfReferencia.setWidth("282px");			
										if(ac.getEndereco()!=null && ac.getEndereco().getReferencia()!=null){
											tfReferencia.setValue(ac.getEndereco().getReferencia());
										}
										tfReferencia.setReadOnly(true);
										
										addComponent(tfReferencia);
										setExpandRatio(tfReferencia, 2);						
									}
								});
							}
						});
					
					addComponent(new FormLayout(){					
						{
							setMargin(true);
							setSpacing(true);
							setStyleName("form-cutom");		
															
							TextField txtUrlLocalizacao= new TextField("Localização");				
							txtUrlLocalizacao.setWidth("655px");								
							txtUrlLocalizacao.setValue(ac.getEndereco().getLocalizacao() != null ? ac.getEndereco().getLocalizacao() : "");
							
							if(ac.getEndereco().getLocalizacao() != null && !ac.getEndereco().getLocalizacao().equals("")){
								BrowserWindowOpener openIpPool = new BrowserWindowOpener(txtUrlLocalizacao.getValue());
								openIpPool.setFeatures("height=600,width=800");
								openIpPool.extend(txtUrlLocalizacao);
							}
							
							
							txtUrlLocalizacao.setReadOnly(true);
							txtUrlLocalizacao.setStyleName("caption-align-fornecedores");
							
							addComponent(txtUrlLocalizacao);
							
						}
					});
					
					
					addComponent(new FormLayout(){					
						{
							setMargin(true);
							setSpacing(true);
							setStyleName("form-cutom");		
															
							TextField txtEmailCliente= new TextField("E-mail");				
							txtEmailCliente.setWidth("285px");								
							txtEmailCliente.setValue(ac.getCliente().getEmail() != null ? ac.getCliente().getEmail() : "");				
							
							txtEmailCliente.setReadOnly(true);
							txtEmailCliente.setStyleName("caption-align-fornecedores");
							
							addComponent(txtEmailCliente);
							
						}
					});
				
					// CONTRATO
					
					
					addComponent(new HorizontalLayout(){
						{
							
							addComponent(new FormLayout(){
								{
									setMargin(true);
									setSpacing(true);
									setStyleName("form-cutom-new");		
									
									DateTime dt1 = new DateTime();
									DateTime dt2 = new DateTime(ac.getData_venc_contrato());							
									Integer days = Days.daysBetween(dt1, dt2).getDays();								
									String 	dias = days.toString()+" DIAS";
									
									TextField tfTermContrato= new TextField("Carência Atual");				
									tfTermContrato.setWidth("120px");	
									tfTermContrato.setValue(dias);					
									tfTermContrato.setReadOnly(true);
									tfTermContrato.setStyleName("caption-align-fornecedores");
									
									addComponent(tfTermContrato);
								}
							});	
							
							addComponent(new FormLayout(){
								{
									setMargin(true);
									setSpacing(true);
									setStyleName("form-cutom-new");		
											
									
									Integer months = 0;
									if(ac.getCarencia() != null && ac.getCarencia().equals("SIM") && ac.getContrato().getCarencia().equals("SIM")){			
										
										try{
											SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
											
											DateTime dt1 = new DateTime(sdf.parse(sdf.format(new Date())));
											DateTime dt2 = new DateTime(sdf.parse(sdf.format(ac.getData_venc_contrato()))).plusMonths(1);			 
											months = Months.monthsBetween(dt1, dt2).getMonths();
											
											carencia = months.toString()+" MESES";
										}catch(Exception e){
											e.printStackTrace();
										}
										
										if(months > 0){
											carencia = months.toString()+" MESES";
										}else{
											carencia = "SEM CARENCIA";
										}
									}else{
										carencia = "SEM CARENCIA";
									}
									
									TextField tfCarencia= new TextField();				
									tfCarencia.setWidth("120px");								
									tfCarencia.setValue(carencia);										
									tfCarencia.setReadOnly(true);															
									addComponent(tfCarencia);
								}
							});													
						
							addComponent(new VerticalLayout(){
								{
									addComponent(new FormLayout(){
										{
											setMargin(true);
											setSpacing(true);
											setStyleName("form-cutom-new");		
											
											TextField txtBeneficioAdesao= new TextField("Serviço de Manutenção R$");				
											txtBeneficioAdesao.setWidth("120px");	
											txtBeneficioAdesao.setValue(Real.formatDbToString(String.valueOf(ac.getIsencao_prest_serv_manutencao())));					
											txtBeneficioAdesao.setReadOnly(true);
											txtBeneficioAdesao.setStyleName("caption-align-fornecedores");
											
											addComponent(txtBeneficioAdesao);
										}
									});
									
									addComponent(new FormLayout(){
										{
											setMargin(true);
											setSpacing(true);
											setStyleName("form-cutom-new");		
											
											TextField txtBeneficioAdesao= new TextField("Benefício de Adesão R$");				
											txtBeneficioAdesao.setWidth("120px");	
											txtBeneficioAdesao.setValue(!carencia.equals("SEM CARENCIA")?Real.formatDbToString(String.valueOf(ac.getValor_beneficio_adesao())):"0,00");					
											txtBeneficioAdesao.setReadOnly(true);
											txtBeneficioAdesao.setStyleName("caption-align-fornecedores");
											
											addComponent(txtBeneficioAdesao);
										}
									});
									
									
									addComponent(new FormLayout(){
										{
											setMargin(true);
											setSpacing(true);
											setStyleName("form-cutom");		
											
											TextField txtBeneficioComodato= new TextField("Benefício de Comodato R$");				
											txtBeneficioComodato.setWidth("120px");	
											
											
											if(!carencia.equals("SEM CARENCIA")){
												bc = Real.formatDbToString(String.valueOf(ac.getValor_beneficio_comodato()));												
											}
											
											if(ac.getRegime().equals("PROPRIO")){
												bc = "0,00";
											}
											
											
											txtBeneficioComodato.setValue(bc);					
											txtBeneficioComodato.setReadOnly(true);
											txtBeneficioComodato.setStyleName("caption-align-fornecedores");
											
											addComponent(txtBeneficioComodato);
										}
									});
									
									addComponent(new FormLayout(){
										{
											setMargin(true);
											setSpacing(true);
											setStyleName("form-cutom");		
											
											TextField txtBeneficioInstalacao= new TextField("Benefício de Instalação R$");				
											txtBeneficioInstalacao.setWidth("120px");	
											
											if(ac.getInstalacao_gratis() != null && ac.getInstalacao_gratis().equals("SIM")){
												
												if(ac.getCarencia() != null && ac.getCarencia().equals("SIM")){		

													EntityManager em = ConnUtil.getEntity();
													Query qValorBenfInst = em.createNativeQuery("SELECT ts.VALOR FROM ose o, tipos_ose g, tipos_subgrupo ts WHERE "
															+ "o.GRUPO_ID = g.ID AND ts.ID = "
															+ "o.TIPO_SUBGRUPO_ID AND g.NOME LIKE '%INSTALACAO%' AND o.ACESSO_CLIENTE_ID = :contrato ");
													qValorBenfInst.setParameter("contrato", ac.getId());
													
													if(qValorBenfInst.getResultList().size() > 0){
														bi = Real.formatDbToString(qValorBenfInst.getResultList().toArray()[0].toString());
													}
												}else{
													bi = "0,00";
												}
											}else{
												bi = "0,00";
											}
											
											
											txtBeneficioInstalacao.setValue(bi);					
											txtBeneficioInstalacao.setReadOnly(true);
											txtBeneficioInstalacao.setStyleName("caption-align-fornecedores");
											
											addComponent(txtBeneficioInstalacao);
										}
									});
									
									addComponent(new FormLayout(){
										{
											setMargin(true);
											setSpacing(true);
											setStyleName("form-cutom");		
											
											int qtd_boletos_abertos = ContasReceberDAO.procurarBoletosDoAcessoPorContrato(ac.getId()) != null ? ContasReceberDAO.procurarBoletosDoAcessoPorContrato(ac.getId()).size() : 0;
											double valor_multa = 0;
											double valor_adesao = !carencia.equals("SEM CARENCIA") ? ac.getValor_beneficio_adesao() : 0;
											double valor_equipamento = Real.formatStringToDBDouble(bc);
											double valor_instalacao =  Real.formatStringToDBDouble(bi);
											double valor_total = valor_adesao+valor_equipamento+valor_instalacao;
											
											if(qtd_boletos_abertos >= 12){
												valor_multa = valor_total;
											}else if(qtd_boletos_abertos == 11){
												valor_multa = (valor_total*91.67)/100;
											}else if(qtd_boletos_abertos == 10){
												valor_multa = (valor_total*83.34)/100;
											}else if(qtd_boletos_abertos == 9){
												valor_multa = (valor_total*75.01)/100;
											}else if(qtd_boletos_abertos == 8){
												valor_multa = (valor_total*66.68)/100;
											}else if(qtd_boletos_abertos == 7){
												valor_multa = (valor_total*58.35)/100;
											}else if(qtd_boletos_abertos == 6){
												valor_multa = (valor_total*50.02)/100;
											}else if(qtd_boletos_abertos == 5){
												valor_multa = (valor_total*41.69)/100;
											}else if(qtd_boletos_abertos == 4){
												valor_multa = (valor_total*33.36)/100;
											}else if(qtd_boletos_abertos == 3){
												valor_multa = (valor_total*25.03)/100;
											}else if(qtd_boletos_abertos == 2){
												valor_multa = (valor_total*16.70)/100;
											}else if(qtd_boletos_abertos == 1){
												valor_multa = (valor_total*8.37)/100;
											}
											
											TextField txtMultaRescisoria= new TextField("Multa rescisória devida R$");		
											txtMultaRescisoria.setWidth("120px");	
											txtMultaRescisoria.setValue(Real.formatDbToString(String.valueOf(valor_multa)));					
											txtMultaRescisoria.setReadOnly(true);
											txtMultaRescisoria.setStyleName("caption-align-fornecedores");
											txtMultaRescisoria.addStyleName("invalid");
											
											addComponent(txtMultaRescisoria);
										}
									});
								}
							});
						}
					});
//					addComponent(new FormLayout(){					
//						{
//							setMargin(true);
//							setSpacing(true);
//							setStyleName("form-cutom-new");		
//									
//							String carencia = "";
//							Integer months = 0;
//							if(ac.getCarencia() != null && ac.getCarencia().equals("SIM") && ac.getContrato().getCarencia().equals("SIM")){			
//								
//								try{
//									SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//									
//									DateTime dt1 = new DateTime(sdf.parse(sdf.format(new Date())));
//									DateTime dt2 = new DateTime(sdf.parse(sdf.format(ac.getData_venc_contrato())));			 
//									months = Months.monthsBetween(dt1, dt2).getMonths();
//									
//									carencia = months.toString()+" MESES";
//								}catch(Exception e){
//									e.printStackTrace();
//								}
//								
//								if(months > 0){
//									carencia = months.toString()+" MESES";
//								}else{
//									carencia = "SEM CARENCIA";
//								}
//							}else{
//								carencia = "SEM CARENCIA";
//							}
//							
//							TextField tfCarencia= new TextField("Carência Atual");				
//							tfCarencia.setWidth("120px");								
//							tfCarencia.setValue(carencia);										
//							tfCarencia.setReadOnly(true);
//							tfCarencia.setStyleName("caption-align-fornecedores");						
//							addComponent(tfCarencia);
//							setExpandRatio(tfCarencia, 2);						
//						}
//					});
					
//					addComponent(new FormLayout(){					
//						{
//							setMargin(true);
//							setSpacing(true);
//							setStyleName("form-cutom");		
//							
//							DateTime dt1 = new DateTime();
//							DateTime dt2 = new DateTime(ac.getData_venc_contrato());							
//							Integer days = Days.daysBetween(dt1, dt2).getDays();								
//							String 	dias = days.toString()+" DIAS";
//							
//							TextField tfTermContrato= new TextField("Término do Contrato");				
//							tfTermContrato.setWidth("120px");	
//							tfTermContrato.setValue(dias);					
//							tfTermContrato.setReadOnly(true);
//							tfTermContrato.setStyleName("caption-align-fornecedores");
//							
//							addComponent(tfTermContrato);
//							setExpandRatio(tfTermContrato, 2);						
//						}
//					});
					
					addComponent(new FormLayout(){					
						{
							setMargin(true);
							setSpacing(true);
							setStyleName("form-cutom");		
															
							TextField tfVlPlano= new TextField("Valor do Plano (R$)");				
							tfVlPlano.setWidth("80px");	
							tfVlPlano.setValue(ac.getPlano().getValor());					
							tfVlPlano.setReadOnly(true);
							tfVlPlano.setStyleName("caption-align-fornecedores");
							
							addComponent(tfVlPlano);
							setExpandRatio(tfVlPlano, 2);						
						}
					});
					
					addComponent(new FormLayout(){					
						{
							setMargin(true);
							setSpacing(true);
							setStyleName("form-cutom");		
															
							ContasReceberDAO crDAO = new ContasReceberDAO();
							ContasReceber cr = crDAO.buscarTitulosAbertosVencidosDeAcessoPorContrato(codAcesso);

							TextField tfVencAberto= new TextField("Vencimento em aberto");				
							tfVencAberto.setStyleName("caption-align-fornecedores");
							tfVencAberto.setWidth("80px");
							
							if(cr!=null){
								DataUtil dtUtil = new DataUtil();
								String dataVenc = dtUtil.parseDataBra(cr.getData_vencimento().toString());
								tfVencAberto.setValue(dataVenc);								
								tfVencAberto.addStyleName("invalid-cpf");													
							}
			
							tfVencAberto.setReadOnly(true);
							addComponent(tfVencAberto);
							setExpandRatio(tfVencAberto, 2);						
						}
					});
				
				//Datas
					addComponent(
							new HorizontalLayout(){
								{		
								addComponent(new FormLayout(){					
									{
										setMargin(true);
										setSpacing(true);
										setStyleName("form-cutom-new");		
																		
										TextField tfDtCadrastro= new TextField("Data de Cadastro");				
										tfDtCadrastro.setWidth("80px");	
										if(ac.getData_cadastro()!=null){
											
											DataUtil dtUtil = new DataUtil();
											String data = dtUtil.parseDataBra(ac.getData_cadastro().toString());
											tfDtCadrastro.setValue(data);
										}
										tfDtCadrastro.setReadOnly(true);
										tfDtCadrastro.setStyleName("caption-align-fornecedores");
										
										addComponent(tfDtCadrastro);
										setExpandRatio(tfDtCadrastro, 2);						
									}
								});
								
								addComponent(new FormLayout(){					
									{
										setMargin(true);
										setSpacing(true);
										setStyleName("form-cutom-new");		
										
										TextField tfDtAltPlano = new TextField("Data de Alteração do Plano");				
										tfDtAltPlano.setWidth("80px");			
										if(ac.getData_alteracao_plano()!= null){
											
											DataUtil dtUtil = new DataUtil();
											String data = dtUtil.parseDataBra(ac.getData_alteracao_plano().toString());
											tfDtAltPlano.setValue(data);
										}
										tfDtAltPlano.setReadOnly(true);
										tfDtAltPlano.setStyleName("caption-align-visualizar-data");
										
										addComponent(tfDtAltPlano);
										setExpandRatio(tfDtAltPlano, 2);						
									}
								});
							}
						});
					
					addComponent(
							new HorizontalLayout(){
								{		
									addComponent(new FormLayout(){					
										{
											setMargin(true);
											setSpacing(true);
											setStyleName("form-cutom");		
											
											TextField tfDtIntalacao= new TextField("Data de Instalação");				
											tfDtIntalacao.setWidth("80px");	
											if(ac.getData_instalacao()!=null){
												
												DataUtil dtUtil = new DataUtil();
												String data = dtUtil.parseDataBra(ac.getData_instalacao().toString());
												tfDtIntalacao.setValue(data);
											}
											tfDtIntalacao.setReadOnly(true);
											tfDtIntalacao.setStyleName("caption-align-fornecedores");
											
											addComponent(tfDtIntalacao);
											setExpandRatio(tfDtIntalacao, 2);						
										}
									});
									
									addComponent(new FormLayout(){					
										{
											setMargin(true);
											setSpacing(true);
											setStyleName("form-cutom");		
											
											TextField tfDtVencContrato = new TextField("Data de Vencimento do Contrato");				
											tfDtVencContrato.setWidth("80px");			
											if(ac.getData_venc_contrato()!=null){
												
												DataUtil dtUtil = new DataUtil();
												String data = dtUtil.parseDataBra(ac.getData_venc_contrato().toString());
												tfDtVencContrato.setValue(data);												
											}
											tfDtVencContrato.setReadOnly(true);
											tfDtVencContrato.setStyleName("caption-align-visualizar-data");
											
											addComponent(tfDtVencContrato);
											setExpandRatio(tfDtVencContrato, 2);						
										}
									});
								}
							});
					
					addComponent(new FormLayout(){					
						{
							setMargin(true);
							setSpacing(true);
							setStyleName("form-cutom-new");		
																
							TextField tfStatusContrato = new TextField("Status de Contrato");				
							tfStatusContrato.setWidth("160px");				
							tfStatusContrato.setValue(ac.getStatus_2());	
							
							tfStatusContrato.setStyleName("caption-align-fornecedores");
							tfStatusContrato.setReadOnly(true);
				
							addComponent(tfStatusContrato);
							setExpandRatio(tfStatusContrato, 2);	
							
							if(tfStatusContrato.getValue().equals("ATIVO")){
								tfStatusContrato.addStyleName("valid-cpf");
							}else{
								tfStatusContrato.addStyleName("invalid-cpf");
							}
						}
					});
					}
				};
		

		tbRoot = new TabSheet();
		tbRoot.setHeight("650px");
		tbRoot.setWidth("900px");
		tbRoot.addTab(vlContato,"Dados do Contato");
		tbRoot.setStyleName("caption-align-fornecedores-bottom");
		
		vlRoot.addComponent(tbRoot);
		return null;
		
	}

	private Component buildBtAtualizar() {
			btAtualizar = new Button("Atualizar", new Button.ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					//vlRootConexao.replaceComponent(vlconexao, buildVlConecao());		
					
					calcularMulta();
					
				}
			});
			return btAtualizar;
		}
		
		private void calcularMulta(){
			//-----Calcular multa,beneficio;
			//----------------------------------------------
			String carencia = "";
			Integer months = 0;
			if(ac.getCarencia() != null && ac.getCarencia().equals("SIM") && ac.getContrato().getCarencia().equals("SIM")){			
				
				try{
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");				
					DateTime dt1 = new DateTime(sdf.parse(sdf.format(new Date())));
					DateTime dt2 = new DateTime(sdf.parse(sdf.format(ac.getData_venc_contrato()))).plusMonths(1);			 
					months = Months.monthsBetween(dt1, dt2).getMonths();				
					carencia = months.toString()+" MESES";
				}catch(Exception e){
					e.printStackTrace();
				}
				
				if(months > 0){
					carencia = months.toString()+" MESES";
				}else{
					carencia = "SEM CARENCIA";
				}
			}else{
				carencia = "SEM CARENCIA";
			}
			
			String bc = "0,00";
			String bi = "0,00";
			
			if(!carencia.equals("SEM CARENCIA")){
				bc = Real.formatDbToString(String.valueOf(ac.getValor_beneficio_comodato()));												
			}
			
			if(ac.getRegime().equals("PROPRIO")){
				bc = "0,00";
			}
			
			if(ac.getInstalacao_gratis() != null && ac.getInstalacao_gratis().equals("SIM")){
				
				if(ac.getCarencia() != null && ac.getCarencia().equals("SIM")){		

					EntityManager em = ConnUtil.getEntity();
					Query qValorBenfInst = em.createNativeQuery("SELECT ts.VALOR FROM ose o, tipos_ose g, tipos_subgrupo ts WHERE "
							+ "o.GRUPO_ID = g.ID AND ts.ID = "
							+ "o.TIPO_SUBGRUPO_ID AND g.NOME LIKE '%INSTALACAO%' AND o.ACESSO_CLIENTE_ID = :contrato ");
					qValorBenfInst.setParameter("contrato", ac.getId());
					
					if(qValorBenfInst.getResultList().size() > 0){
						bi = Real.formatDbToString(qValorBenfInst.getResultList().toArray()[0].toString());
					}
				}else{
					bi = "0,00";
				}
			}else{
				bi = "0,00";
			}
			
			int qtd_boletos_abertos = ContasReceberDAO.procurarBoletosDoAcessoPorContrato(ac.getId()) != null ? ContasReceberDAO.procurarBoletosDoAcessoPorContrato(ac.getId()).size() : 0;
			double valor_multa = 0;
			double valor_adesao = !carencia.equals("SEM CARENCIA") ? ac.getValor_beneficio_adesao() : 0;
											 //!carencia.equals("SEM CARENCIA")?Real.formatDbToString(String.valueOf(ac.getValor_beneficio_adesao())):"0,00"
			double valor_equipamento = Real.formatStringToDBDouble(bc);
			double valor_instalacao =  Real.formatStringToDBDouble(bi);
			double valor_total = valor_adesao+valor_equipamento+valor_instalacao;
			
			if(qtd_boletos_abertos >= 12){
				valor_multa = valor_total;
			}else if(qtd_boletos_abertos == 11){
				valor_multa = (valor_total*91.67)/100;
			}else if(qtd_boletos_abertos == 10){
				valor_multa = (valor_total*83.34)/100;
			}else if(qtd_boletos_abertos == 9){
				valor_multa = (valor_total*75.01)/100;
			}else if(qtd_boletos_abertos == 8){
				valor_multa = (valor_total*66.68)/100;
			}else if(qtd_boletos_abertos == 7){
				valor_multa = (valor_total*58.35)/100;
			}else if(qtd_boletos_abertos == 6){
				valor_multa = (valor_total*50.02)/100;
			}else if(qtd_boletos_abertos == 5){
				valor_multa = (valor_total*41.69)/100;
			}else if(qtd_boletos_abertos == 4){
				valor_multa = (valor_total*33.36)/100;
			}else if(qtd_boletos_abertos == 3){
				valor_multa = (valor_total*25.03)/100;
			}else if(qtd_boletos_abertos == 2){
				valor_multa = (valor_total*16.70)/100;
			}else if(qtd_boletos_abertos == 1){
				valor_multa = (valor_total*8.37)/100;
			}
			
			//Benefício Adesão
			System.out.println("Adesão: "+Real.formatDbToString(String.valueOf(valor_adesao)));
			//Benefício Comodato
			System.out.println("Equipamento: "+Real.formatDbToString(String.valueOf(valor_equipamento)));
			//Benfício Instalação
			System.out.println("Valor Instalacao: "+Real.formatDbToString(String.valueOf(valor_instalacao)));
			//Multa Recisória
			System.out.println("Multa: "+Real.formatDbToString(String.valueOf(valor_multa)));
			
			//Valor total
			System.out.println("Valor Total: "+Real.formatDbToString(String.valueOf(valor_total)));
			
		}
				
		private Component buildBtCancelar() {
			btCancelar = new Button("Fechar", new Button.ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					close();
				}
			});
			
			ShortcutListener slbtCancelar = new ShortcutListener("Cancelar", ShortcutAction.KeyCode.ESCAPE,null) {
				
				@Override
				public void handleAction(Object sender, Object target) {
					btCancelar.click();
				}
			};
			btCancelar.addShortcutListener(slbtCancelar);
			return btCancelar;
		}
		
}
	

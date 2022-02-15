package com.digital.opuserp.view.modulos.acesso.contratos_acesso;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Months;
import org.json.JSONArray;
import org.json.JSONObject;

import br.com.caelum.stella.boleto.Datas;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.AcessoDAO;
import com.digital.opuserp.dao.AlteracoesContasReceberDAO;
import com.digital.opuserp.dao.ContasReceberDAO;
import com.digital.opuserp.dao.ContratosAcessoDAO;
import com.digital.opuserp.dao.GerarNNDAO;
import com.digital.opuserp.dao.LogDAO;
import com.digital.opuserp.dao.OseDAO;
import com.digital.opuserp.dao.OsiDAO;
import com.digital.opuserp.dao.PlanoAcessoDAO;
import com.digital.opuserp.domain.AcessoCliente;
import com.digital.opuserp.domain.AlteracoesContasReceber;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.ContaBancaria;
import com.digital.opuserp.domain.ContasReceber;
import com.digital.opuserp.domain.ControleTitulo;
import com.digital.opuserp.domain.Endereco;
import com.digital.opuserp.domain.LogAcoes;
import com.digital.opuserp.domain.Ose;
import com.digital.opuserp.domain.Osi;
import com.digital.opuserp.domain.ParametrosBoleto;
import com.digital.opuserp.domain.PlanoAcesso;
import com.digital.opuserp.domain.Produto;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.DataUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.util.Real;
import com.digital.opuserp.util.boletos.boleto.Banco;
import com.digital.opuserp.util.boletos.boleto.Boleto;
import com.digital.opuserp.util.boletos.boleto.Emissor;
import com.digital.opuserp.util.boletos.boleto.Sacado;
import com.digital.opuserp.util.boletos.boleto.bancos.BancoDoBrasil;
import com.digital.opuserp.util.boletos.boleto.bancos.Sicredi;
import com.digital.opuserp.view.modulos.financeiro.contasReceber.ImprimirBoletoBeta;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.data.Property;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class BoletosGeradosImpressaoUtil  extends Window {
	
	JPAContainer<ContasReceber> container;
	
	TextField tfBusca;
	Table tbBoletos;
	Button btImprimir;
	Button btCancelar;
	Integer codContrato;
	
	List<ContasReceber> boletosCliente;	
	VerticalLayout vlContent;
	public BoletosGeradosImpressaoUtil(boolean modal, boolean center, final Integer codContrato){
		
		super("Imprima todos os Boletos Abaixo");
		
		//this.boletosCliente = boletosCliente;
		this.codContrato = codContrato;		
		
			
		setWidth("1000px");
		setHeight("360px");
		//setIcon(new ThemeResource("icons/search-32.png"));
		setModal(modal);
		if(center){
			center();
		}
		
		setClosable(false);
				
		vlContent = new VerticalLayout(){
			{
				setSizeFull();
				setMargin(true);
				
				addComponent(buildTb());
				setExpandRatio(tbBoletos, 2);
				
				HorizontalLayout hlButtons = new HorizontalLayout();
				hlButtons.setSpacing(true);
				hlButtons.setMargin(true);
				hlButtons.setStyleName("hl_buttons_bottom");				
				
				
				
				AcessoCliente contrato = ContratosAcessoDAO.find(codContrato);
				
				if(contrato != null && contrato.getEmitir_nfe_automatico() != null &&  contrato.getEmitir_nfe_automatico().equals("SIM") && contrato.getEmitir_nfe_c_boleto_aberto() != null && contrato.getEmitir_nfe_c_boleto_aberto().equals("SIM")){
					hlButtons.addComponent(buildBtGerarNossoNumero());
				}
				
				
				hlButtons.addComponent(buildBtImprimir());
				
				addComponent(hlButtons);
				setComponentAlignment(hlButtons, Alignment.BOTTOM_RIGHT);
				
			}
		};
		
		setContent(vlContent);
	}
	
	
	
	public Table buildTb(){
		tbBoletos = new Table(){
			@Override
			protected String formatPropertyValue(Object rowId, Object colId,
					Property<?> property) {
				
				return super.formatPropertyValue(rowId, colId, property);			
				
			}		
		};
		
		
		tbBoletos.setSizeFull();
		//tbBoletos.setHeight("230px");
		tbBoletos.setSelectable(false);
		
		tbBoletos.addContainerProperty("Cod", BigInteger.class, null);
		tbBoletos.addContainerProperty("Ndoc", String.class, "");
		tbBoletos.addContainerProperty("Valor", String.class, "");
		tbBoletos.addContainerProperty("Vencimento", String.class, "");
		tbBoletos.addContainerProperty("Nosso Número", String.class, "");
		tbBoletos.addContainerProperty("Status", String.class, "");
		tbBoletos.addContainerProperty("Plano", String.class, "");

		
		refresh();
		
		return tbBoletos;
	}
	
	
	private void ImprimirBoletoContrato(List<ContasReceber> selecteds){
		try {
			
			boolean filtroDataImprimir = true;
			
			//Set<Object> selecteds = (Set<Object>)tb.getValue();
			if(selecteds.size() > 0){
											
				EntityManager em = ConnUtil.getEntity();
					
				List<Boleto> boletosSelecionados = new ArrayList<>();
				for (ContasReceber conta: selecteds) {
					
					ContasReceber boleto = em.find(ContasReceber.class, conta.getId());
					
					String n_numero2 = boleto.getN_numero_sicred() != null ? boleto.getN_numero_sicred() : null;
					if(n_numero2 != null && !n_numero2.substring(0, 4).equals("18/3") && !n_numero2.substring(0, 4).equals("19/3") && !n_numero2.substring(0, 4).equals("20/3")){
						filtroDataImprimir = false;
					}
					
					String controle =  boleto.getControle();
					Query qControle = em.createQuery("select c from ControleTitulo c where c.nome=:nome and c.empresa_id =:empresa", ControleTitulo.class);
					qControle.setParameter("nome", controle);
					qControle.setParameter("empresa", OpusERP4UI.getEmpresa().getId());
					
					ContaBancaria cb = null;
//					if(qControle.getResultList().size() ==1){
//						cb = ((ControleTitulo)qControle.getSingleResult()).getConta_bancaria_bkp();
//					}
																										
					SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
					//sdf1.set
					
					Integer anoEmissao = Integer.parseInt(sdf1.format(boleto.getData_emissao()).substring(0, 4).toString());
					Integer mesEmissao = Integer.parseInt(sdf1.format(boleto.getData_emissao()).substring(4, 6).toString());
					Integer diaEmissao = Integer.parseInt(sdf1.format(boleto.getData_emissao()).substring(6, 8).toString());
					
					Integer anoVencimento = Integer.parseInt(sdf1.format(boleto.getData_vencimento()).substring(0, 4).toString()); 
					Integer mesVencimento = Integer.parseInt(sdf1.format(boleto.getData_vencimento()).substring(4, 6).toString()); 
					Integer diaVencimento = Integer.parseInt(sdf1.format(boleto.getData_vencimento()).substring(6, 8).toString());
														
					String nossoNumero = "";
						
						 	if(boleto.getN_numero() != null){
						 		nossoNumero = boleto.getN_numero();
					        	
					        	
					        	if(boleto.getControle().equals("ACESSO-PRE") || 
					        			boleto.getControle().equals("ACESSO-POS") || 
					        			boleto.getControle().equals("ALUGUEL") ||
					        			boleto.getControle().equals("ASSISTENCIA")){
					        		
					        		//banco= new BancoDoBrasil();
					        		cb = em.find(ContaBancaria.class, 2);
					        		
					        	}else{
					        		cb = em.find(ContaBancaria.class, 2);
					        	}
					        }
					        
					        if(boleto.getN_numero_sicred() != null){
					        	nossoNumero = boleto.getN_numero_sicred();
					        	
					        	if(boleto.getN_numero_sicred().equals("ACESSO-PRE") || 
					        			boleto.getN_numero_sicred().equals("ACESSO-POS") || 
					        			boleto.getN_numero_sicred().equals("ALUGUEL") ||
					        			boleto.getN_numero_sicred().equals("ASSISTENCIA") ||
					        			boleto.getN_numero_sicred().equals("ESPACO-REUNIAO")){
					        		
					        		//banco= new Sicredi();
					        		cb = em.find(ContaBancaria.class, 5);
					        		
					        	}else{
					        		cb = em.find(ContaBancaria.class, 2);
					        	}
					        }
					        
					        
					        if(cb == null){
					        	cb = em.find(ContaBancaria.class, 2);
					        }
						
						
						String nomeCliente = boleto.getCliente().getNome_razao();
						String nDoc =  boleto.getN_doc();

					
						Query qPb = em.createQuery("select pb from ParametrosBoleto pb where pb.cliente_id = :codCliente", ParametrosBoleto.class);
						qPb.setParameter("codCliente",boleto.getCliente().getId() );			
						ParametrosBoleto pb = null;
						boolean cobrarTaxa = true;
						if(qPb.getResultList().size() > 0){
							pb = (ParametrosBoleto) qPb.getSingleResult();
							
							if(!pb.getCobrar_taxa_bancaria()){
								cobrarTaxa = false;
							}
						}
						
						if(qPb.getResultList().size() == 0){
							cobrarTaxa = false;
						}
						
						Double vlrBoleto = null; 
						Double valorBoleto = null;
						
						boolean taxBoleto = true;
						
						try{
							ContasReceber cr = boleto;									
							String codContrato = cr.getN_doc().split("/")[0].toString();									
							PlanoAcesso oPlanoBoleto = PlanoAcessoDAO.find(cr.getPlano_contrato());
							taxBoleto =oPlanoBoleto != null  &&  oPlanoBoleto.getTaxa_boleto().equals("NAO") ? false : true;		
						}catch(Exception e){
							
						}
						
						if(cobrarTaxa){
							if(!taxBoleto){
								cobrarTaxa = false;
							}
						}
						
						if(cobrarTaxa){
							vlrBoleto = Double.parseDouble(Real.formatStringToDB(boleto.getValor_titulo()));
							valorBoleto = vlrBoleto + new Double(cb.getTaxa_boleto());										
						}else{
							valorBoleto = Double.parseDouble(Real.formatStringToDB(boleto.getValor_titulo()));										
						}

						//Sacado
						Cliente sacadoCliente = boleto.getCliente();
						
						Endereco enderecoSacado = null;

						String[] os = nDoc.split("OS");
						Ose ose =null;	
						Osi osi =null;	
						if(os.length > 1 && controle.equals("SERVICO")){
							ose = OseDAO.find(Integer.parseInt(os[1].split("-")[0]));
						}else if(os.length > 1 && controle.equals("ASSISTENCIA")){
							osi = OsiDAO.find(Integer.parseInt(os[1]));
						}
						
						if(!ContasReceberDAO.allowNdocManual(nDoc)){
						//	String codContrato = nDoc.split("/")[0].toString();
							enderecoSacado = sacadoCliente.getEndereco_principal();
							
						}else if(ose!=null){
							enderecoSacado = ose.getEnd();
						}else if(osi!=null){
							enderecoSacado = osi.getEnd();										
						}else{
							//Endereco
							Query qEndereco = em.createQuery("select e from Endereco e where e.clientes =:cliente and e.principal =:principal", Endereco.class);
							qEndereco.setParameter("cliente", sacadoCliente);
							qEndereco.setParameter("principal", true);
							
							
							if(qEndereco.getResultList().size() == 1){
								enderecoSacado = (Endereco)qEndereco.getSingleResult();
							}
						}
						
						if(sacadoCliente != null && enderecoSacado != null){
			
							Datas datas = Datas.novasDatas()
									.comDocumento(diaEmissao, mesEmissao, anoEmissao)
									.comProcessamento(Calendar.getInstance().get(Calendar.DATE), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.YEAR))
									.comVencimento(diaVencimento, mesVencimento, anoVencimento);  
							
							SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
							
							Date dt1 = sdf.parse(diaEmissao+"/"+mesEmissao+"/"+anoEmissao);
							Date dt2 = sdf.parse("27/09/2018");
							
							//if(dt1.before(dt2)){
							//	filtroDataImprimir = false;
							//}

							Emissor emissor= null; 
							
							
							if(cb != null && cb.getId() == 5){
							
							//sicred
								emissor = Emissor.novoEmissor()  
							        .comCedente(OpusERP4UI.getEmpresa().getRazao_social())  
							        .comAgencia(Integer.parseInt(cb.getAgencia_banco())).comDigitoAgencia('8')  
							        .comContaCorrente(cb.getCod_beneficiario())  
							        .comNumeroConvenio(cb.getConvenio() != null && !cb.getConvenio().equals("") ? Integer.parseInt(cb.getConvenio()) : 0).comDigitoContaCorrente('8')									           
							        .comCarteira(cb.getCarteira() != null && !cb.getCarteira().equals("") ? Integer.parseInt(cb.getCarteira()) : 0)  
							        .comNossoNumero(nossoNumero)
							        .comPostoBeneficiario(cb.getPosto_beneficiario())
							        .comEndereco(OpusERP4UI.getEmpresa().getEndereco()+", "+OpusERP4UI.getEmpresa().getNumero()+" "+OpusERP4UI.getEmpresa().getBairro()+" - "+OpusERP4UI.getEmpresa().getCidade()+" - "+OpusERP4UI.getEmpresa().getUf());
							
							
							}
							
							
							
							
							
							if(cb != null && cb.getId() == 	2){
							//bb
							
							emissor = Emissor.novoEmissor()  
						            .comCedente(OpusERP4UI.getEmpresa().getRazao_social())  
						            .comAgencia(Integer.parseInt(cb.getAgencia_banco())).comDigitoAgencia('8')  
						            .comContaCorrente(cb.getN_conta())  
						            .comNumeroConvenio(Integer.parseInt(cb.getConvenio())).comDigitoContaCorrente('8')									           
						            .comCarteira(Integer.parseInt(cb.getCarteira()))  
						            .comNossoNumero(nossoNumero)
						            .comEndereco(OpusERP4UI.getEmpresa().getEndereco()+", "+OpusERP4UI.getEmpresa().getNumero()+" "+OpusERP4UI.getEmpresa().getBairro()+" - "+OpusERP4UI.getEmpresa().getCidade()+" - "+OpusERP4UI.getEmpresa().getUf());  
							}
							
							
							

					        Sacado sacado = Sacado.novoSacado()  
					        		.comNome(nomeCliente)  
					        		.comCpf(sacadoCliente.getDoc_cpf_cnpj())  
					        		.comEndereco(enderecoSacado.getEndereco()+","+enderecoSacado.getNumero())  
					        		.comBairro(enderecoSacado.getBairro())  
					        		.comCep(enderecoSacado.getCep())  
					        		.comCidade(enderecoSacado.getCidade())  
					        		.comUf(enderecoSacado.getUf())  
					        		.comReferencia(enderecoSacado.getReferencia())
					        		.comComplemento(enderecoSacado.getComplemento());

					        
					        Banco banco = null;
					        
//					        if(cb.getCod_banco().equals("748")){								        
//					        	banco= new Sicredi();
//					        }
//					        
//					        if(cb.getCod_banco().equals("001")){								        
//					        	banco= new BancoDoBrasil();
//					        }
					        
					        
					        String n_numero = "";
					        if(boleto.getN_numero() != null){
					        	n_numero = boleto.getN_numero();
					        	
					        	
					        	if(boleto.getControle().equals("ACESSO-PRE") || 
					        			boleto.getControle().equals("ACESSO-POS") || 
					        			boleto.getControle().equals("SERVICO")|| 
					        			boleto.getControle().equals("PRODUTO")||								        			
					        			boleto.getControle().equals("ALUGUEL") ||
					        			boleto.getControle().equals("ASSISTENCIA")){
					        		
					        		banco= new BancoDoBrasil();
					        		cb = em.find(ContaBancaria.class, 2);
					        		
					        	}
					        }
					        
					        if(boleto.getN_numero_sicred() != null){
					        	n_numero = boleto.getN_numero_sicred();
					        	
					        	if(boleto.getControle().equals("ACESSO-PRE") || 
					        			boleto.getControle().equals("ACESSO-POS") || 
					        			boleto.getControle().equals("SERVICO") ||								        			
					        			boleto.getControle().equals("PRODUTO")||								        			
					        			boleto.getControle().equals("ALUGUEL") ||
					        			boleto.getControle().equals("ASSISTENCIA") ||
					        			boleto.getControle().equals("ESPACO-REUNIAO")){
					        		
					        		banco= new Sicredi();
					        		cb = em.find(ContaBancaria.class, 5);
					        		
					        	}
					        }
					        
					        
					        if(banco == null){
					        	banco= new BancoDoBrasil();
				        		cb = em.find(ContaBancaria.class, 2);
					        }

					        
					        					        
							Boleto boleto1 = Boleto.novoBoleto()
									.comCodBoleto(boleto.getId())
						            .comBanco(banco)  
						            .comDatas(datas)						              
						            .comEmissor(emissor)  
						            .comSacado(sacado)  
						            .comValorBoleto(valorBoleto)  
						            .comNumeroDoDocumento(nDoc)  
						            .comInstrucoes(cb.getInstrucoes1(), cb.getInstrucoes2(), cb.getInstrucoes3())							            
						            .comDescricoes(cb.getDemonstrativo1(), cb.getDemonstrativo2(), cb.getDemonstrativo3())
						            .comNossoNumero(n_numero)
						            .comContaBancaria(cb) 
						            .comQtd(boleto.getQuantidade()).comCnpj(OpusERP4UI.getEmpresa().getCnpj()).comControle(boleto.getControle());		
							
							
							boletosSelecionados.add(boleto1);		
							
						}

					AlteracoesContasReceberDAO.save(new AlteracoesContasReceber(null, "IMPRIMIU UM BOLETO", boleto,
							OpusERP4UI.getEmpresa(), OpusERP4UI.getUsuarioLogadoUI(), new Date()));
					
				}
				
				Window window;
				window = new Window();
				window.setCaption("Imprimir Boletos");
				window.setWidth("800px");
				window.setHeight("600px");
				window.setResizable(true);
				window.center();
				window.setModal(true);
				window.setStyleName("disable_scroolbar");		
				window.setCloseShortcut(KeyCode.ESCAPE, null);
			
				if(boletosSelecionados.size() > 0 && filtroDataImprimir){

					StreamResource resource = new StreamResource(new ImprimirBoletoBeta(boletosSelecionados), "Boletos.pdf");
					resource.getStream();			        
					resource.setMIMEType("application/pdf");		
					resource.setCacheTime(0);
					
					Embedded e = new Embedded();
					e.setSizeFull();
					e.setType(Embedded.TYPE_BROWSER); 
					e.setSource(resource);			     
					window.setContent(e);						
				
					getUI().addWindow(window);
					window.focus();
				}else{
					if(!filtroDataImprimir){
						Notify.Show("Um ou mais boleto(s) precisam ser reemitido(s)", Notify.TYPE_ERROR);
					}
				}
				
				window.addCloseListener(new Window.CloseListener() {
					
					@Override
					public void windowClose(CloseEvent e) {						
						//tb.focus();						
					}
				});
				
				LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Imprimiu um ou mais Boleto(s)"));
			
			
			}
		} catch (Exception e) {
			e.printStackTrace();
			Notify.Show("ERRO: "+e.getLocalizedMessage(), Notify.TYPE_ERROR);
		}
	}
	
	
	Button btGerarNossoNumero;
	private Button buildBtGerarNossoNumero(){
		
		btGerarNossoNumero = new Button("Gerar Nosso Número", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
			
				GenericDialog gd = new GenericDialog("Confirme para continuar!", "Deseja realmente gerar Nosso Número para os boletos selecionados ?", true, true);
				gd.addListerner(new GenericDialog.DialogListerner() {
					
					@Override
					public void onClose(DialogEvent event) {
						
						if(event.isConfirm()){
							List<Integer> codigos = new ArrayList<>();
							
							for(ContasReceber boleto: boletosCliente){
								Integer codBoleto = boleto.getId();
								codigos.add(codBoleto);
							}
							
							GerarNNDAO.gerar(codigos);	
							refresh();
						}
						
					}
				});
				
				getUI().addWindow(gd);
			}
		});
		
		return btGerarNossoNumero;
	}
	
	private void refresh(){
		
		boletosCliente = ContasReceberDAO.procurarAbertoBoletosDoAcessoPorContrato(codContrato);
		
		tbBoletos.removeAllItems();
		
		Integer i = 0;
		for (ContasReceber contasReceber : boletosCliente) {
			
			PlanoAcesso plano = PlanoAcessoDAO.find(contasReceber.getPlano_contrato());
			String nomePlano = plano != null && plano.getNome() != null ? plano.getNome() : "SEM PLANO";
			
			tbBoletos.addItem(new Object[]{
					new BigInteger(String.valueOf(contasReceber.getId())), 
					contasReceber.getN_doc(),
					"R$ "+contasReceber.getValor_titulo(), 
					DataUtil.formatDateBra(contasReceber.getData_vencimento()),
					contasReceber.getN_numero_sicred(),
					contasReceber.getStatus(),nomePlano}, i);
			i++;
		}
		
		tbBoletos.sort(new Object[]{"Cod"}, new boolean[]{true});
	}

	private Button buildBtImprimir() {
		btImprimir = new Button("Imprimir todos os boletos", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
					//ImprimirBoletoContrato(boletosCliente);
					//gerarCarne();
				gerarCarneSemCodBarras();
					//close(); 
			}	
		});
		btImprimir.addStyleName("default");
				
		return btImprimir;
	}
	
	private void gerarCarneSemCodBarras(){
		
		try{
			
			
			if(boletosCliente.size() > 0){
				
				List<String> links= new ArrayList<>();
				EntityManager em = ConnUtil.getEntity();
				
				
				for (ContasReceber boleto:boletosCliente) {
					
					String cod_boleto = boleto.getId().toString();					
					links.add("URLhttp://172.17.0.13/boletoSemCodigo/index.php?b="+cod_boleto);  
				}
				
				
				if(links.size() > 0){
					
					String urls = "";
					for (String s: links) {
						urls += s;
					}
					
					final String url = "http://172.17.0.13/boletoSemCodigo/montarBoletos.php?url="+urls;

					Window wVideo  = new Window("Imprimir boletos");
			        wVideo.setResizable(false); 
			        wVideo.setModal(true);
			        wVideo.center();
			        wVideo.setWidth("942px");
			        wVideo.setHeight("680px");
			        
			        wVideo.setContent(new VerticalLayout(){
			        	{
			        		setMargin(true);
			        		setSpacing(true);
			        		
			        		setWidth("940px");
					        setHeight("633px");
			        		
			        		String link = url;
			        		
			        		final Embedded video = new Embedded(null,new ExternalResource(link));
					        video.setType(Embedded.TYPE_BROWSER);
					        video.setSizeFull();

			        		addComponent(video);
			        		
			        	}
			        });
			        
			        
			        wVideo.addCloseListener(new CloseListener() {
						
						@Override
						public void windowClose(CloseEvent e) {
							close();
						}
					});
			        
			        getUI().addWindow(wVideo);
					
					
					
					//refresh(tfBusca.getValue());
					
				}else{
					Notify.Show("Não foi possível imprimir nenhum boleto!", Notify.TYPE_ERROR);
				}
				
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
private boolean detalhesTransacao(String transacao) {
		
		if(transacao != null && !transacao.equals("")){
			
			try{
				
	            String url = "http://172.17.0.13/boleto/detalhesTransacao.php";

	            HttpURLConnection httpClient = (HttpURLConnection) new URL(url).openConnection();

	            //add reuqest header
	            httpClient.setRequestMethod("POST");
	            httpClient.setRequestProperty("User-Agent", "Mozilla/5.0");
	            httpClient.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
	            
	            String urlParameters = "cod="+Integer.parseInt(transacao);
	            		
	            httpClient.setDoOutput(true);
	            try (DataOutputStream wr = new DataOutputStream(httpClient.getOutputStream())) {
	                wr.writeBytes(urlParameters);
	                wr.flush();
	            }

        		
	            try (BufferedReader in = new BufferedReader(new InputStreamReader(httpClient.getInputStream()))) {

	                String line;
	                StringBuilder response = new StringBuilder();

	                while ((line = in.readLine()) != null) {
	                    response.append(line);
	                }

	                if(response.toString() != null && !response.toString().equals("")){
	                	JSONObject json = new JSONObject(response.toString());
	                	
	                	final String vencimento = json.getJSONObject("data").getJSONObject("payment").getJSONObject("banking_billet").get("expire_at").toString();
	                	final String codigo_barras = json.getJSONObject("data").getJSONObject("payment").getJSONObject("banking_billet").get("barcode").toString();
	                	final String criado = json.getJSONObject("data").getJSONObject("payment").get("created_at").toString();
	                	final String status = json.getJSONObject("data").get("status").toString();
	                			                	
	                	final JSONArray arrayHistory = json.getJSONObject("data").getJSONArray("history");
	                	
//	                	System.out.println("Vencimento:"+vencimento);
//	                	System.out.println("Código Barras:"+codigo_barras);
//	                	System.out.println("Criado:"+criado);
//	                	System.out.println("Status:"+status);
	                	
	                	
	                	
	                	
	                	Window janelaDetalhe = new Window("Detalhes", new VerticalLayout(){
	                		{
	                			setSizeFull();     				                			
	                			setMargin(true);
	                			addStyleName("border-form");
	                			
	                			{
	                				addComponent(new FormLayout(){					
	                					{
	                						
	                						setMargin(true);
	                						setSpacing(true);
	                						addStyleName("form-cutom");						
	                									
	                						TextField txtDataCadastro = new TextField("Data de cadastro");
	                						txtDataCadastro.setValue(criado);
	                						txtDataCadastro.setEnabled(false);
	                						
	                						addComponent(txtDataCadastro);
	                						
	                						TextField txtVencimento = new TextField("Data de vencimento");
	                						txtVencimento.setValue(vencimento);
	                						txtVencimento.setEnabled(false);
	                						
	                						addComponent(txtVencimento);
	                							                						
	                						TextField txtCodBarras = new TextField("Código de barras");
	                						txtCodBarras.setValue(codigo_barras);
	                						txtCodBarras.setEnabled(false);
	                						
	                						addComponent(txtCodBarras);
	                						
	                						TextField txtStatus = new TextField("Status");
	                						txtStatus.setValue(status); 
	                						txtStatus.setEnabled(false);
	                						
	                						addComponent(txtStatus);
	                						
	                						Table tbHistory = new Table();
	                						tbHistory.setWidth("634px");
	                						tbHistory.setHeight("269px");
	                						
	                						tbHistory.addContainerProperty("Mensagem", String.class, "");
	                						tbHistory.addContainerProperty("Data", String.class, "");
	                						
	                						tbHistory.setVisibleColumns(new Object[]{"Mensagem","Data"}); 
	                						
	                						for(int i=0; i<arrayHistory.length(); i++){
	                							
	                	                		String msg = arrayHistory.getJSONObject(i).get("message").toString();
	                	                		String data = arrayHistory.getJSONObject(i).get("created_at").toString();
	                	                		
	                	                		tbHistory.addItem(new Object[]{msg,data}, tbHistory.getItemIds().size()+1);
	                	                		
	                	                	}
	                						
	                						addComponent(tbHistory);
	                						
	                					}
	                				});
	                			}
	                		}
	                	});
	                	
	                	
	                	janelaDetalhe.setWidth("817px");
	                	janelaDetalhe.setHeight("452px");
	                	
	                	janelaDetalhe.setModal(true);
	                	janelaDetalhe.center();
	                	getUI().addWindow(janelaDetalhe); 
	                	
	                }               
	            }
				
				return true;
				
				}catch(Exception e){
					e.printStackTrace();
					return false;
				}
	}
		
		return false;
    }

private String detalharTransacao(String transacao) {
	
	try{
	String url = "https://digitalonline.com.br/2viaEmissaoVariosBoletos/boleto/api.php";
	
    HttpsURLConnection httpClient = (HttpsURLConnection) new URL(url).openConnection();

    //add reuqest header
    httpClient.setRequestMethod("POST");
    httpClient.setRequestProperty("User-Agent", "Mozilla/5.0");
    httpClient.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

    String urlParameters = 
    		"funcao=detalharTransacao&"
    		+ "transacao="+transacao;
    
    httpClient.setDoOutput(true);
    try (DataOutputStream wr = new DataOutputStream(httpClient.getOutputStream())) {
        wr.writeBytes(urlParameters);
        wr.flush();
    }

	
    try (BufferedReader in = new BufferedReader(
            new InputStreamReader(httpClient.getInputStream()))) {

        String line;
        StringBuilder response = new StringBuilder();

        while ((line = in.readLine()) != null) {
            response.append(line);
        }

        org.json.JSONObject json = new org.json.JSONObject(response.toString());		                
        org.json.JSONObject jsonData = new org.json.JSONObject(json.get("data").toString());
         
        String retorno = new org.json.JSONObject(new org.json.JSONObject(jsonData.get("payment").toString()).get("banking_billet").toString()).get("link").toString();  
    
        return retorno;	        
    }
    
    
	
	}catch(Exception e){
		e.printStackTrace();
		return null;
	}
}
private ContaBancaria getContaBancaria(Integer codContaReceber){
	EntityManager em = ConnUtil.getEntity();
	
	
	String controle =  ContasReceberDAO.find(codContaReceber).getControle();
	Query qControle = em.createQuery("select c from ControleTitulo c where c.nome=:nome and c.empresa_id =:empresa", ControleTitulo.class);
	qControle.setParameter("nome", controle);
	qControle.setParameter("empresa", OpusERP4UI.getEmpresa().getId());
	
	ContaBancaria contaBancaria = ((ControleTitulo)qControle.getSingleResult()).getConta_bancaria();
	
	return contaBancaria;
}

private double calcularAtraso(Integer codContaReceber){
	
	ContasReceber boleto = ContasReceberDAO.find(codContaReceber);
	PlanoAcesso plano = boleto != null && boleto.getPlano_contrato() != null ? PlanoAcessoDAO.find(boleto.getPlano_contrato()) : null; 
	String valorTitulo = boleto.getValor_titulo();

	ContaBancaria contaBancaria = getContaBancaria(codContaReceber);
	
	if(contaBancaria != null){
		
		DateTime dt1 = new DateTime(boleto.getData_vencimento());
		DateTime dt2 = new DateTime(new Date());
 
		Integer dias = Days.daysBetween(dt1, dt2).getDays();
		Integer meses = Months.monthsBetween(dt1, dt2).getMonths();
		
		Double juros;
		Double multa;
		double taxa_negativacao;
		double  atualizacao_monetaria;
		
		Double jurosTotais;
		if(dias > Integer.parseInt(contaBancaria.getTolerancia_juros_multa()))
		{
			juros = ((Double.parseDouble(contaBancaria.getJuros()) * dias));
			multa = Double.parseDouble(contaBancaria.getMulta());					
			
//			tfDesconto.setReadOnly(false);
//			tfDesconto.setValue("0,00");
//			tfDesconto.setReadOnly(true);
		}else{
			juros = 0.00;
			multa = 0.00;
		}
		
		if(dias > 364){
			taxa_negativacao = contaBancaria.getTaxa_negativacao();
			double taxa_monetaria = contaBancaria.getAtualizacao_monetaria() * meses;
			double vlr_taxa_monetraria = Real.formatStringToDBDouble(boleto.getValor_titulo()) * taxa_monetaria / 100;					
			atualizacao_monetaria = vlr_taxa_monetraria;
			
			if(!boleto.getStatus().equals("NEGATIVADO")){
				taxa_negativacao = 0;
			}						
		}else{
			atualizacao_monetaria = 0.00;
			taxa_negativacao = 0.00;
		}
		
		jurosTotais = juros + multa;
		
		Float valorDocTotal = Float.parseFloat(Real.formatStringToDB(valorTitulo)) + Float.parseFloat(Real.formatStringToDB(contaBancaria.getTaxa_boleto()));
		Double acrescimo = jurosTotais * valorDocTotal/100;
		
		Double vlrJuros1 = Double.parseDouble(juros.toString()) * Double.parseDouble(Real.formatStringToDB(valorTitulo))/100;
		Double vlrJuros = vlrJuros1;
		
		Double vlrMulta1 = Double.parseDouble(multa.toString()) * Double.parseDouble(Real.formatStringToDB(valorTitulo))/100;				
		Double vlrMulta = vlrMulta1;
		
		Double novoValorDoc =  valorDocTotal + acrescimo;
		
//		tfDiasEmAtraso.setReadOnly(false);
//		tfDiasEmAtraso.setValue(dias > 0 ? dias.toString() : "0");
//		tfDiasEmAtraso.setReadOnly(true);
//		
//		tfJuros.setReadOnly(false);
		
		Double jurosFinal = juros;
		Double vlrJurosArredondado = vlrJuros;
//		tfJuros.setValue(Real.formatDbToString(vlrJurosArredondado.toString()));
//		tfJuros.setReadOnly(true);
//		
//		tfMulta.setReadOnly(false);
//		tfMulta.setValue(Real.formatDbToString(vlrMulta.toString()));
//		tfMulta.setReadOnly(true);				
		
		EntityManager em = ConnUtil.getEntity();
		Query qPb = em.createQuery("select pb from ParametrosBoleto pb where pb.cliente_id = :codCliente", ParametrosBoleto.class);
		qPb.setParameter("codCliente",boleto.getCliente().getId());			
		ParametrosBoleto pb = null;
		boolean cobrarTaxa = true;
		if(qPb.getResultList().size() > 0){
			pb = (ParametrosBoleto) qPb.getSingleResult();
			
			if(!pb.getCobrar_taxa_bancaria()){
				cobrarTaxa = false;
			}
		}
		
		boolean taxBoleto =plano != null  &&  plano.getTaxa_boleto().equals("NAO")  ? false : true;		
		if(!cobrarTaxa && taxBoleto){
			taxBoleto = !taxBoleto;
		}
		
		if(!taxBoleto){
//			tfTaxa.setReadOnly(false);
//			tfTaxa.setValue("0,00");
//			tfTaxa.setReadOnly(true);				
		}
		
		Double taxaBoleto = taxBoleto ?  Double.parseDouble(contaBancaria.getTaxa_boleto()) : 0;
		Double valorFinal = Double.parseDouble(Real.formatStringToDB(valorTitulo)) + taxaBoleto  + vlrJuros1 + vlrMulta1 + taxa_negativacao + atualizacao_monetaria;
						
		//Double desconto = tfDesconto.getValue() != null && !tfDesconto.getValue().equals("") && !tfDesconto.getValue().equals("0,00") ? Real.formatStringToDBDouble(tfDesconto.getValue()) : 0;
		
		
		return valorFinal;
		
//		tfValorPagmento.setValue(Real.formatDbToString(valorFinal1.toString()));			
//		
//		txtTaxaNegativacao.setReadOnly(false);
//		txtTaxaNegativacao.setValue(Real.formatDbToString(String.valueOf(taxa_negativacao)));
//		txtTaxaNegativacao.setReadOnly(true);
//						
//		txtValorAtualizacaoMonetaria.setReadOnly(false);
//		txtValorAtualizacaoMonetaria.setValue(Real.formatDbToString(String.valueOf(atualizacao_monetaria)));
//		txtValorAtualizacaoMonetaria.setReadOnly(true);
//		
//		lbJuros.setValue(contaBancaria.getJuros()+"% ao dia");
//		lbMulta.setValue(Real.formatDbToString(contaBancaria.getMulta())+"%");
//		lbAtualizacaoMonetaria.setValue(Real.formatDbToString(String.valueOf(contaBancaria.getAtualizacao_monetaria()))+"% ao mês");

		
	}
	
	return 0;
	
	
}

private String retirarAcentos(String s){
	
	s = s.replaceAll(",","");
	
	s = s.replaceAll("Ç","C");
	s = s.replaceAll("É","E");
	s = s.replaceAll("Í","I");
	s = s.replaceAll("Ó","O");
	s = s.replaceAll("Ú","U");
	s = s.replaceAll("Á","A");
	
	s = s.replaceAll("Ã","A");
	s = s.replaceAll("Õ","O");
	
	
	return s;
}
	
	
	private void gerarCarne(){
		
		try{
			
			
			if(boletosCliente.size() > 0){
				
				List<String> links= new ArrayList<>();
				EntityManager em = ConnUtil.getEntity();
				
				
				for (ContasReceber boleto1: boletosCliente) {
				
					ContasReceber boleto = ContasReceberDAO.find(boleto1.getId());
					
					if(boleto.getTransacao_gerencianet() != null && !boleto.getTransacao_gerencianet().equals("")){
						
						if(boleto.getN_numero_gerencianet() != null && !boleto.getN_numero_gerencianet().equals("")){
							links.add("URLhttp://172.17.0.13/boletoNovo/boletoUnico.php?url2="+boleto.getN_numero_gerencianet());
						}else{
							//Pegar a URL
							String detalhes = detalharTransacao(boleto.getTransacao_gerencianet());
							if(detalhes != null && !detalhes.equals("")){
								em.getTransaction().begin();
								boleto.setN_numero_gerencianet(detalhes);
								em.merge(boleto);
								em.getTransaction().commit();
								
								links.add("URLhttp://172.17.0.13/boletoNovo/boletoUnico.php?url2="+boleto.getN_numero_gerencianet());
							}
						}
					}else{
						
							boolean atrasado = false;
							double valor_corrigido = 0;
							if(new DateTime(boleto.getData_vencimento()).isBeforeNow()){
								atrasado = true;								
								valor_corrigido = calcularAtraso(boleto.getId());								
							}
					
							SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");							
							String descricao = "Boleto";           
				            
							String valor = boleto.getValor_titulo().replace(",", "").replace(".", "");	
				            if(atrasado){
				            	valor = Real.formatDbToString(String.valueOf(valor_corrigido)).replace(",", "").replace(".", "");
				            }
				            
				            String nome_cliente =retirarAcentos(boleto.getCliente().getNome_razao());            
				            String cpf = boleto.getCliente().getDoc_cpf_cnpj();
				            String telefone = boleto.getCliente().getDdd_fone1()+""+boleto.getCliente().getTelefone1();
				            String email = boleto.getCliente().getEmail() != null ? boleto.getCliente().getEmail() : "";            
				            String vencimento = sdf.format(boleto.getData_vencimento());
				            String codBoleto = boleto.getId().toString();
				            
				            String url = "http://172.17.0.13/boleto/api.php";
		
				            HttpURLConnection httpClient = (HttpURLConnection) new URL(url).openConnection();
		
				            //add reuqest header
				            httpClient.setRequestMethod("POST");
				            httpClient.setRequestProperty("User-Agent", "Mozilla/5.0");
				            httpClient.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
				            
				            String codContrato = "";
				            if(ContasReceberDAO.ChecarPadraoAcessoNDoc(boleto.getN_doc())){
				            	codContrato = boleto.getN_doc().split("/")[0].toString();	
				            }
		
				            String urlParameters = "funcao=gerarBoletos&"
				            		+ "descricao="+descricao+"&"
				            		+ "valor="+valor+"&"
				            		+ "quantidade=1&"
				            		+ "nome_cliente="+nome_cliente+"&"
				            		+ "cpf="+cpf+"&"
				            		+ "telefone="+telefone+"&"
				            		+ "vencimento="+vencimento+"&"
				            		+ "codBoleto="+codBoleto+"&"
				            		+ "email="+email+"&"
				            		+ "codContrato="+codContrato;
	        
				            httpClient.setDoOutput(true);
				            try (DataOutputStream wr = new DataOutputStream(httpClient.getOutputStream())) {
				                wr.writeBytes(urlParameters);
				                wr.flush();
				            }

				            try (BufferedReader in = new BufferedReader(
				                    new InputStreamReader(httpClient.getInputStream()))) {
		
				                String line;
				                StringBuilder response = new StringBuilder();
		
				                while ((line = in.readLine()) != null) {
				                    response.append(line);
				                }
		
				                System.out.println(response.toString());
				                
				                org.json.JSONObject json = new org.json.JSONObject(response.toString());		                
				    	        org.json.JSONObject jsonData = new org.json.JSONObject(json.get("data").toString());
				    	         
				    	        String link = jsonData.get("link").toString();
				    	        String transacao = jsonData.get("charge_id").toString();
				    	        
				    	        System.out.println(link);
				    	        System.out.println(transacao);
		
				    	        boleto.setN_numero_gerencianet(link); 
				    	        boleto.setTransacao_gerencianet(transacao);
				    	        
				    	        em.getTransaction().begin();
				    	        em.merge(boleto);
				    	        em.getTransaction().commit();
				    	        
				    	        links.add("URLhttp://172.17.0.13/boletoNovo/boletoUnico.php?url2="+boleto.getN_numero_gerencianet());
				    	        
				            }
		            
		            
					}
  
				}
				
				
				if(links.size() > 0){
					
					String urls = "";
					for (String s: links) {
						urls += s;
					}
					
					final String url = "http://172.17.0.13/boletoNovo/pegaCodigoHtml2.php?url="+urls;

					Window wVideo  = new Window("Imprimir boletos");
			        wVideo.setResizable(false); 
			        wVideo.setModal(true);
			        wVideo.center();
			        wVideo.setWidth("942px");
			        wVideo.setHeight("680px");
			        
			        wVideo.setContent(new VerticalLayout(){
			        	{
			        		setMargin(true);
			        		setSpacing(true);
			        		
			        		setWidth("940px");
					        setHeight("633px");
			        		
			        		String link = url;
			        		
			        		final Embedded video = new Embedded(null,new ExternalResource(link));
					        video.setType(Embedded.TYPE_BROWSER);
					        video.setSizeFull();

			        		addComponent(video);
			        		
			        	}
			        });
			        
			        getUI().addWindow(wVideo);
			        
			        
			        
			        wVideo.addCloseListener(new CloseListener() {
						
						@Override
						public void windowClose(CloseEvent e) {
							close();
						}
					});
					
					
					
					//refresh(tfBusca.getValue());
					
				}else{
					Notify.Show("Não foi possível imprimir nenhum boleto!", Notify.TYPE_ERROR);
				}
				
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

		
	public void addListerner(ProdutoListerner target){
		try {
			Method method = ProdutoListerner.class.getDeclaredMethod("onSelected",ProdutoEvent.class);
			addListener(ProdutoEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(ProdutoListerner target){
		removeListener(ProdutoEvent.class, target);
	}
	public static class ProdutoEvent extends Event{

		private Produto produto;
		
		
		public ProdutoEvent(Component source, Produto produto) {
			super(source);		
			this.produto  = produto;
			
		}

		public Produto getProduto() {
								
			return produto;
		}		
	}
	public interface ProdutoListerner{
		public void onSelected(ProdutoEvent event);
	}
}


package com.digital.opuserp.view.modulos.acesso.contratos_acesso;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Months;

import com.digital.opuserp.dao.ContasReceberDAO;
import com.digital.opuserp.dao.CredenciaisAcessoDAO;
import com.digital.opuserp.domain.AcessoCliente;
import com.digital.opuserp.domain.Concentrador;
import com.digital.opuserp.domain.ContasReceber;
import com.digital.opuserp.domain.FiltroAcesso;
import com.digital.opuserp.domain.RadAcct;
import com.digital.opuserp.domain.RadUserGroupDAO;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.DataUtil;
import com.digital.opuserp.util.MikrotikUtil;
import com.digital.opuserp.util.Real;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.ExternalResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.Reindeer;

public class VisualizarContratoInfoTecnica extends Window {

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
	VerticalLayout vlRootConexao;
	VerticalLayout vlRootConsumo;
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
	
	public VisualizarContratoInfoTecnica(Item item, String title, boolean modal){
		this.item = item;
		
		
		if (item.getItemProperty("id").getValue() != null) {
			codAcesso = (Integer) item.getItemProperty("id").getValue();
			ac = em.find(AcessoCliente.class,codAcesso);
			if(item.getItemProperty("base").getValue()!=null){
				base = (Concentrador) item.getItemProperty("base").getValue();					
			}
		}
		
		setWidth("950px");
		setHeight("729px");
		
		
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
	public VerticalLayout buildVlConsumo(){
			
		
		vlRootConsumo = new VerticalLayout(){
			{
				try {
					
					URL url = new URL("http://172.17.0.15/cacti/graph_view.php?action=list&page=1&host_id=0&rows=50000000000&graph_template_id=0&filter=&graph_add=&graph_remove=");
					
					BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

			        String inputLine;
			        int i=1;
			        String id_chart ="";
			        while ((inputLine = in.readLine()) != null){    	
			        	
			        	String s = "pppoe-"+ac.getLogin();
			        	if(inputLine.contains(s+"<")){
			        		id_chart = inputLine.split("href=")[1].split("'")[1].split("&")[0].split("=")[1];
			        		url_chart = "http://172.17.0.15/cacti/graph_image.php?action=view&local_graph_id="+inputLine.split("href=")[1].split("'")[1].split("&")[0].split("=")[1]+"&rra_id=";
			        	}

			        	i++;
			        }
			        in.close();
			        
			        final Embedded img5 = new Embedded("Hora",new ExternalResource(url_chart+"5"));
			        img5.setType(Embedded.TYPE_IMAGE);
			        img5.setWidth("595px");
			        img5.setHeight("210px");
			        img5.setStyleName("center-margin");
			        			        
			        final Embedded img1 = new Embedded("Diário",new ExternalResource(url_chart+"1"));
			        img1.setType(Embedded.TYPE_IMAGE);
			        img1.setWidth("595px");
			        img1.setHeight("210px");
			        img1.setStyleName("center-margin");
			        
			        final Embedded img2 = new Embedded("Semanal",new ExternalResource(url_chart+"2"));
			        img2.setType(Embedded.TYPE_IMAGE);
			        img2.setWidth("595px");
			        img2.setHeight("210px");
			        img2.setStyleName("center-margin");
			        
			        final Embedded img3 = new Embedded("Mensal",new ExternalResource(url_chart+"3"));
			        img3.setType(Embedded.TYPE_IMAGE);
			        img3.setWidth("595px");
			        img3.setHeight("210px");
			        img3.setStyleName("center-margin");
			        
			        final Embedded img4 = new Embedded("Anual",new ExternalResource(url_chart+"4"));
			        img4.setType(Embedded.TYPE_IMAGE);
			        img4.setWidth("595px");
			        img4.setHeight("210px");
			        img4.setStyleName("center-margin");
			        
			        
			        Button btRealTime = new Button("Tempo Real");
			        btRealTime.setStyleName(Reindeer.BUTTON_LINK);
			        btRealTime.addStyleName("btRealTime");
			       // btRealTime.addStyleName("menos-20");
			        
			        Button btRefresh = new Button("Atualizar", new Button.ClickListener() {
						
						@Override
						public void buttonClick(ClickEvent event) {
							img5.setSource(null);
							img5.setSource(new ExternalResource(url_chart+"5"));
							img1.setSource(null);
							img1.setSource(new ExternalResource(url_chart+"1"));
							img2.setSource(null);
							img2.setSource(new ExternalResource(url_chart+"2"));
							img3.setSource(null);
							img3.setSource(new ExternalResource(url_chart+"3"));
							img4.setSource(null); 
							img4.setSource(new ExternalResource(url_chart+"4"));
							
						}
					});
			        btRefresh.setStyleName(Reindeer.BUTTON_LINK);
			        btRefresh.addStyleName("btRealTime");
			        
			        
			        BrowserWindowOpener openIpPool = new BrowserWindowOpener("http://172.17.0.15/cacti/plugins/realtime/graph_popup_rt.php?local_graph_id="+id_chart);
					openIpPool.setFeatures("height=300,width=200");
					openIpPool.extend(btRealTime);
			        
			        	
			        //addComponent(btRefresh);
			        addComponent(btRealTime); 	
			        addComponent(img5);
			        addComponent(img1);
			        addComponent(img2);
			        addComponent(img3);
			        addComponent(img4);
				} catch (Exception e) {

					e.printStackTrace();
				}
			}
		};
		
		vlRootConsumo.setSizeFull();
		
		return vlRootConsumo;
	}
	
	public VerticalLayout buildVlConecao(){
		
		CredenciaisAcessoDAO caDAO = new CredenciaisAcessoDAO();
		radacct = caDAO.buscarRadacct(ac.getLogin());
		
		vlconexao = new VerticalLayout(){
			{
				
				if(base!=null){
					queue = MikrotikUtil.listarQueuesList(base.getUsuario(),base.getSenha(),base.getEndereco_ip(),Integer.parseInt(base.getPorta_api()),ac.getLogin());
				}
				
				setMargin(true);
				addComponent(new FormLayout(){					
					{
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom");		
						
						TextField tfStatusConcent= new TextField("Status Concentrador");				
						tfStatusConcent.setWidth("150px");	
						
						if(base!=null){
							statusConcentrador = MikrotikUtil.testconexao(base.getUsuario(),base.getSenha(),base.getEndereco_ip(),Integer.parseInt(base.getPorta_api()));
						}
	
						if(statusConcentrador){						
							tfStatusConcent.setValue("UP");	
						}else{
							tfStatusConcent.setValue("DOWN");	
						}
						tfStatusConcent.setReadOnly(true);
						tfStatusConcent.setStyleName("caption-align-visualizar-conexao");
						
						addComponent(tfStatusConcent);
						setExpandRatio(tfStatusConcent, 2);		
						
						if(tfStatusConcent.getValue().equals("UP")){
							tfStatusConcent.addStyleName("valid-cpf");
						}else{
							tfStatusConcent.addStyleName("invalid-cpf");
						}
					}
				});
								
						addComponent(new FormLayout(){					
							{
								setMargin(true);
								setSpacing(true);
								setStyleName("form-cutom-new");		
								
								TextField tfComunConcen= new TextField("Comunicação Concentrador");				
								tfComunConcen.setWidth("150px");			

								if(base!=null){
									comunicacao =	MikrotikUtil.buscarComunicacao(base.getUsuario(), base.getSenha(),base.getEndereco_ip(), Integer.parseInt(base.getPorta_api()), ac.getEndereco_mac());
									logado = MikrotikUtil.buscarStatusConexao(base.getUsuario(), base.getSenha(),base.getEndereco_ip(), Integer.parseInt(base.getPorta_api()), ac.getLogin());
								}
	
								if(comunicacao || logado){
									tfComunConcen.setReadOnly(false);						
									tfComunConcen.setValue("ESTABELECIDA");
									tfComunConcen.setReadOnly(true);										
								}else{
									tfComunConcen.setReadOnly(false);						
									tfComunConcen.setValue("NÃO ESTABELECIDA");						
									tfComunConcen.setReadOnly(true);												
								}
								
								tfComunConcen.setReadOnly(true);
								tfComunConcen.setStyleName("caption-align-visualizar-conexao");
								
								addComponent(tfComunConcen);
								setExpandRatio(tfComunConcen, 2);	
								
								if(tfComunConcen.getValue().equals("ESTABELECIDA")){
									tfComunConcen.addStyleName("valid-cpf");
								}else{
									tfComunConcen.addStyleName("invalid-cpf");
								}
							}
						});
						
						

				addComponent(new HorizontalLayout(){
					{
					addComponent(new FormLayout(){					
						{
							setMargin(true);
							setSpacing(true);
							setStyleName("form-cutom");		
							
							TextField tfStatusConexão= new TextField("Status de Conexão");				
							tfStatusConexão.setWidth("150px");			

							if(logado){
								tfStatusConexão.setReadOnly(false);						
								tfStatusConexão.setValue("LOGADO");
								tfStatusConexão.setReadOnly(true);										
							}else{
								tfStatusConexão.setReadOnly(false);						
								tfStatusConexão.setValue("NÃO LOGADO");						
								tfStatusConexão.setReadOnly(true);												
							}
							
							tfStatusConexão.setReadOnly(true);
							tfStatusConexão.setStyleName("caption-align-visualizar-conexao");
							
							addComponent(tfStatusConexão);
							setExpandRatio(tfStatusConexão, 2);	
							
							if(tfStatusConexão.getValue().equals("LOGADO")){
								tfStatusConexão.addStyleName("valid-cpf");
							}else{
								tfStatusConexão.addStyleName("invalid-cpf");
							}
						}
					});

					
					btDesconectar = new Button("Desconectar", new Button.ClickListener() {
						
						@Override
						public void buttonClick(ClickEvent event) {
							
							if(item != null && base!=null){
								boolean check = MikrotikUtil.desconectarCliente(base.getUsuario(), base.getSenha(), base.getEndereco_ip(), Integer.parseInt(base.getPorta_api()),ac.getLogin());
								if(check){
									Notification.show("O Cliente foi Desconectado Com Sucesso!");
								}else{
									Notification.show("O Cliente já esta Desconectado!");
								}
							}
							
							vlRootConexao.replaceComponent(vlconexao, buildVlConecao());
							
						}
					});	
					
					btDesconectar.setStyleName("atualizar");
					
					FormLayout flmBt = new FormLayout(){
						{					
							setMargin(true);
							setSpacing(true);	
							setStyleName("form-cutom");
							addComponent(btDesconectar);							
						}
					};
					
					addComponent(flmBt);
			
				}
			});

				addComponent(new FormLayout(){					
					{
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom-new");		
						
						TextField tfRadioName= new TextField("Radio Name");				
						tfRadioName.setWidth("150px");			
		
						if(base!=null){
							regTable = MikrotikUtil.buscarRegistationTable(base.getUsuario(), base.getSenha(),base.getEndereco_ip(), Integer.parseInt(base.getPorta_api()), ac.getEndereco_mac());
							activeConnections  = MikrotikUtil.buscarActiveConnections(base.getUsuario(), base.getSenha(),base.getEndereco_ip(), Integer.parseInt(base.getPorta_api()), ac.getEndereco_mac());
						}
						
						if(regTable!=null && regTable.get("radio-name") !=null){
							
							tfRadioName.setValue(regTable.get("radio-name"));												
						}
						tfRadioName.setReadOnly(true);
						tfRadioName.setStyleName("caption-align-visualizar-conexao");
						
						addComponent(tfRadioName);
						setExpandRatio(tfRadioName, 2);						
					}
				});
				
				addComponent(new FormLayout(){					
					{
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom");		
						
						TextField tfModoConexão= new TextField("Modo de Conexão");				
						tfModoConexão.setWidth("150px");	
						
						if(radacct!=null){
							if(radacct.getFramedprotocol().equals("PPP")){
								tfModoConexão.setValue("PPPOE");				
							}else{
								tfModoConexão.setValue("HOTSPOT");
							}

						}else{
							tfModoConexão.setValue("");																										
						}
						
						tfModoConexão.setReadOnly(true);
						tfModoConexão.setStyleName("caption-align-visualizar-conexao");
						
						addComponent(tfModoConexão);
						setExpandRatio(tfModoConexão, 2);						
					}
				});
				
				addComponent(new FormLayout(){					
					{
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom");		
						
						TextField tfUptime= new TextField("Uptime");				
						tfUptime.setWidth("150px");			

						if(activeConnections!=null && activeConnections.get("uptime") !=null){	
							tfUptime.setValue(activeConnections.get("uptime"));												
						}
						
						tfUptime.setReadOnly(true);
						tfUptime.setStyleName("caption-align-visualizar-conexao");
						
						addComponent(tfUptime);
						setExpandRatio(tfUptime, 2);						
					}
				});
				
				addComponent(new HorizontalLayout(){
				{
					addComponent(new FormLayout(){					
						{
							setMargin(true);
							setSpacing(true);
							setStyleName("form-cutom");		
							
							TextField tfEndIpPool= new TextField("Endereço Ip Adress List");				
							tfEndIpPool.setWidth("200px");	
													
							if(logado){		 							
								String ip = MikrotikUtil.getRemoteIpPPOE(ac.getBase().getEndereco_ip(), ac.getBase().getUsuario(), ac.getBase().getSenha(), ac.getLogin());
								tfEndIpPool.setValue(ip);										
//								BrowserWindowOpener openIpPool = new BrowserWindowOpener("http://"+ip);
//								openIpPool.setFeatures("height=600,width=800");
//								openIpPool.extend(tfEndIpPool);
							}
							tfEndIpPool.setReadOnly(true);
							tfEndIpPool.setStyleName("caption-align-visualizar-conexao");
							
							addComponent(tfEndIpPool);
							setExpandRatio(tfEndIpPool, 2);		
						}
					});
				
					addComponent(new FormLayout(){					
						{
							setMargin(true);
							setSpacing(true);
							setStyleName("form-cutom-new");		
							
							TextField tfQueuesUp= new TextField("Queues Upload");				
							tfQueuesUp.setWidth("80px");			
							
							if(queue!=null && queue.get("max-limit")!=null){		
								String queueUp [] = queue.get("max-limit").split("/");
								tfQueuesUp.setValue(queueUp[0]);												
							}
							
							String bloqueado = RadUserGroupDAO.getPlanoByUsername(ac.getLogin());
							
							tfQueuesUp.setReadOnly(true);
							tfQueuesUp.setStyleName("caption-align-visualizar-UpDown");
							
							if(!bloqueado.equals("BLOQUEADO")){							
								tfQueuesUp.addStyleName("valid-cpf");
							}else{
								tfQueuesUp.addStyleName("invalid-cpf");
							}
							
							addComponent(tfQueuesUp);
							setExpandRatio(tfQueuesUp, 2);						
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
								setStyleName("form-cutom-new");		
								
								TextField tfEndIpAdList= new TextField("Endereço Fixo");				
								tfEndIpAdList.setWidth("200px");			
								
								if(ac.getEndereco_ip()!=null){
									tfEndIpAdList.setValue(ac.getEndereco_ip());											
								}
								tfEndIpAdList.setReadOnly(true);
								tfEndIpAdList.setStyleName("caption-align-visualizar-conexao");
								
								addComponent(tfEndIpAdList);
								setExpandRatio(tfEndIpAdList, 2);										
							}
						});
						
						addComponent(new FormLayout(){					
							{
								setMargin(true);
								setSpacing(true);
								setStyleName("form-cutom");		
								
								
								TextField tfQueuesDown= new TextField("Queues Download");				
								tfQueuesDown.setWidth("80px");	
								
								if(queue!=null && queue.get("max-limit")!=null){		
									String queueDown [] = queue.get("max-limit").split("/");
									tfQueuesDown.setValue(queueDown[1]);												
								}
								String bloqueado = RadUserGroupDAO.getPlanoByUsername(ac.getLogin());
		
								tfQueuesDown.setReadOnly(true);
								tfQueuesDown.setStyleName("caption-align-visualizar-UpDown");
								
								if(!bloqueado.equals("BLOQUEADO")){
									tfQueuesDown.addStyleName("valid-cpf");
								}else{
									tfQueuesDown.addStyleName("invalid-cpf");
								}
								
								addComponent(tfQueuesDown);
								setExpandRatio(tfQueuesDown, 2);						
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
								setStyleName("form-cutom-new");		
								
								TextField tfMacAdress= new TextField("MAC Adress");				
								tfMacAdress.setWidth("200px");			

								if(regTable!=null && regTable.get("mac-address") !=null){	
									tfMacAdress.setValue(regTable.get("mac-address"));												
								}
								
								tfMacAdress.setReadOnly(true);
								tfMacAdress.setStyleName("caption-align-visualizar-conexao");
								
								addComponent(tfMacAdress);
								setExpandRatio(tfMacAdress, 2);						
							}
						});
						
						addComponent(new FormLayout(){					
							{
								setMargin(true);
								setSpacing(true);
								setStyleName("form-cutom-new");		
								
								TextField txtStatusPlano= new TextField("Status Plano");				
								txtStatusPlano.setWidth("80px");			

								
								txtStatusPlano.setValue(RadUserGroupDAO.getPlanoByUsername(ac.getLogin()));												
																
								txtStatusPlano.setReadOnly(true);
								txtStatusPlano.setStyleName("caption-align-visualizar-UpDown");
								
								if(!txtStatusPlano.getValue().equals("BLOQUEADO")){
									txtStatusPlano.addStyleName("valid-cpf");
								}else{
									txtStatusPlano.addStyleName("invalid-cpf");
								}
								
								addComponent(txtStatusPlano);
								setExpandRatio(txtStatusPlano, 2);						
							}
						});
					}
				});
				
				
				addComponent(new FormLayout(){					
					{
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom");		
						
						TextField tfInterface= new TextField("Interface");				
						tfInterface.setWidth("300px");			

						if(regTable!=null && regTable.get("interface") !=null){	
							tfInterface.setValue(regTable.get("interface"));												
						}
											
						tfInterface.setReadOnly(true);
						tfInterface.setStyleName("caption-align-visualizar-conexao");
						
						addComponent(tfInterface);
						setExpandRatio(tfInterface, 2);						
					}
				});
				
				addComponent(new FormLayout(){					
					{
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom");		
						
						TextField tfTxRx= new TextField("Signal Strength (dBm)");				
						tfTxRx.setWidth("150px");			

						//TODO revisar busaca
						if(regTable!=null && regTable.get("signal-strength") !=null && regTable.get("signal-strength") !=null){	
													
							tfTxRx.setValue(regTable.get("signal-strength"));												
						}
						
						tfTxRx.setReadOnly(true);
						tfTxRx.setStyleName("caption-align-visualizar-conexao");
						
						addComponent(tfTxRx);
						setExpandRatio(tfTxRx, 2);						
					}
				});
				
				addComponent(new FormLayout(){					
					{
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom");		
						
						//TODO revisar busaca
						TextField tfTxRxCCQ= new TextField("TX/RX CCQ (%)");				
						tfTxRxCCQ.setWidth("150px");			
						
						if(regTable!=null && regTable.get("tx-rate") !=null && regTable.get("rx-rate")!=null){
							tfTxRxCCQ.setValue(regTable.get("tx-ccq")+"/"+regTable.get("rx-ccq"));												
						}
						
						tfTxRxCCQ.setReadOnly(true);
						tfTxRxCCQ.setStyleName("caption-align-visualizar-conexao");
						
						addComponent(tfTxRxCCQ);
						setExpandRatio(tfTxRxCCQ, 2);						
					}
				});
				
				addComponent(new FormLayout(){					
					{
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom");		
						
						TextField tfDistanceKm= new TextField("Distance (km)");				
						tfDistanceKm.setWidth("150px");			

						if(regTable!=null && regTable.get("distance") !=null){	
							tfDistanceKm.setValue(regTable.get("distance"));												
						}
						
						tfDistanceKm.setReadOnly(true);
						tfDistanceKm.setStyleName("caption-align-visualizar-conexao");
						
						addComponent(tfDistanceKm);
						setExpandRatio(tfDistanceKm, 2);						
					}
				});
				
				addComponent(new FormLayout(){					
					{
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom");		
						
						TextField tfTxRxRate= new TextField("TXRX Rate");				
						tfTxRxRate.setWidth("150px");			

						if(regTable!=null && regTable.get("tx-rate")!=null){	
							tfTxRxRate.setValue(regTable.get("tx-rate")+"/"+regTable.get("rx-rate"));												
						}
						
						tfTxRxRate.setReadOnly(true);
						tfTxRxRate.setStyleName("caption-align-visualizar-conexao");
						
						addComponent(tfTxRxRate);
						setExpandRatio(tfTxRxRate, 2);						
					}
				});
				
				addComponent(new FormLayout(){					
					{
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom");		
						
						TextField tfPthrougtput= new TextField("P Througtput (kpps)");				
						tfPthrougtput.setWidth("150px");			

						if(regTable!=null && regTable.get("p-throughput")!=null){	
							tfPthrougtput.setValue(regTable.get("p-throughput"));												
						}
						
						tfPthrougtput.setReadOnly(true);
						tfPthrougtput.setStyleName("caption-align-visualizar-conexao");
						
						addComponent(tfPthrougtput);
						setExpandRatio(tfPthrougtput, 2);						
					}
				});	
			}
		};
		
		return vlconexao;
	}
	String carencia = "";
	public Table buildLayout(){
		
		fieldGroup = new FieldGroup(item);

		
		VerticalLayout vlInformacoes = new VerticalLayout(){
			{				
				setMargin(true);
				addComponent(new FormLayout(){					
					{
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom");		
						
						TextField tfPlanoAtual = new TextField("Plano Atual");				
						tfPlanoAtual.setWidth("120px");			
						if(ac.getPlano().getNome()!=null){

							tfPlanoAtual.setValue(ac.getPlano().getNome());												
						}
						tfPlanoAtual.setReadOnly(true);
						tfPlanoAtual.setStyleName("caption-align-fornecedores");
						
						addComponent(tfPlanoAtual);
						setExpandRatio(tfPlanoAtual, 2);						
					}
				});
				
				addComponent(new FormLayout(){					
					{
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom");		
						
						TextField tfUploadDownload = new TextField("Upload/Download");				
						tfUploadDownload.setWidth("120px");			
						if(ac.getPlano()!=null){

							String rateLimit[] = ac.getPlano().getRate_limit().toString().split("/");
							String up = rateLimit[0];
							String down = rateLimit[1];
							tfUploadDownload.setValue(up+"/"+down);												
						}
						tfUploadDownload.setReadOnly(true);
						tfUploadDownload.setStyleName("caption-align-fornecedores");
						
						addComponent(tfUploadDownload);
						setExpandRatio(tfUploadDownload, 2);						
					}
				});
				
				addComponent(new FormLayout(){					
					{
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom-new");		
						
						TextField tfBaseUser = new TextField("Usuário");				
						tfBaseUser.setWidth("120px");			
						if(ac!=null){
							
							tfBaseUser.setValue(ac.getLogin());												
						}
						tfBaseUser.setReadOnly(true);
						tfBaseUser.setStyleName("caption-align-fornecedores");
						
						addComponent(tfBaseUser);
						setExpandRatio(tfBaseUser, 2);						
					}
				});
				
				addComponent(new FormLayout(){					
					{
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom");		
						
						TextField tfBaseSenha= new TextField("Senha");				
						tfBaseSenha.setWidth("120px");			
						if(ac!=null){
							
							tfBaseSenha.setValue(ac.getSenha());												
						}
						tfBaseSenha.setReadOnly(true);
						tfBaseSenha.setStyleName("caption-align-fornecedores");
						
						addComponent(tfBaseSenha);
						setExpandRatio(tfBaseSenha, 2);						
					}
				});
				
				addComponent(new FormLayout(){					
					{
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom");		
						
						TextField txtPacoteAutocen = new TextField("Pacote Autocensura");				
						txtPacoteAutocen.setWidth("120px");			
						if(ac!=null){
							
							txtPacoteAutocen.setValue(ac.getPlano().getQtd_censura().toString());												
						}
						txtPacoteAutocen.setReadOnly(true);
						txtPacoteAutocen.setStyleName("caption-align-fornecedores");
						
						addComponent(txtPacoteAutocen);												
					}
				});
				
				addComponent(new FormLayout(){					
					{
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom-new");		
						
						CredenciaisAcessoDAO caDAO = new CredenciaisAcessoDAO();
						
						List<FiltroAcesso>	result = caDAO.buscarFiltrobyCodAcesso(codAcesso);
						
						StringBuilder resultFiltro = new StringBuilder();
						
						if(result!=null){
							Integer i= 0;
							for (FiltroAcesso r: result){
								if(i == 0){
									resultFiltro = resultFiltro.append(r.getNome());	
								}else{
									resultFiltro = resultFiltro.append(", "+r.getNome());
								}
								i++;
							}
						}
						TextField tfCensura= new TextField("Autocensura");				
						tfCensura.setWidth("650px");
	
						tfCensura.setValue(resultFiltro.toString());												

						tfCensura.setReadOnly(true);
						tfCensura.setStyleName("caption-align-fornecedores");
						
						addComponent(tfCensura);
						setExpandRatio(tfCensura, 2);						
					}
				});
				
				addComponent(new FormLayout(){					
					{
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom-new");		
						
						TextField tfConcentrador= new TextField("Concentrador");				
						tfConcentrador.setWidth("200px");			
						if(ac.getBase()!=null){
							
							tfConcentrador.setValue(ac.getBase().getIdentificacao());												
						}
						tfConcentrador.setReadOnly(true);
						tfConcentrador.setStyleName("caption-align-fornecedores");
						
						addComponent(tfConcentrador);
						setExpandRatio(tfConcentrador, 2);						
					}
				});
				
				addComponent(new FormLayout(){					
					{
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom");		
						
						TextField tfInterface= new TextField("Interface");				
						tfInterface.setWidth("300px");			
						if(ac.getInterfaces()!=null){
							
							tfInterface.setValue(ac.getInterfaces());												
						}
						tfInterface.setReadOnly(true);
						tfInterface.setStyleName("caption-align-fornecedores");
						
						addComponent(tfInterface);
						setExpandRatio(tfInterface, 2);						
					}
				});
				
				addComponent(new FormLayout(){					
					{
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom");		
						
						TextField tfSignal= new TextField("Signal Strength");				
						tfSignal.setWidth("200px");			
						if(ac.getSignal_strength()!=null){
							
							tfSignal.setValue(ac.getSignal_strength());												
						}
						tfSignal.setReadOnly(true);
						tfSignal.setStyleName("caption-align-fornecedores");
						
						addComponent(tfSignal);
						setExpandRatio(tfSignal, 2);						
					}
				});
				
				addComponent(new FormLayout(){					
					{
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom");		
						
						TextField tfSwithl= new TextField("Swith");				
						tfSwithl.setWidth("200px");			
						if(ac.getSwith()!=null){
							
							tfSwithl.setValue(ac.getSwith().getIdentificacao());												
						}
						tfSwithl.setReadOnly(true);
						tfSwithl.setStyleName("caption-align-fornecedores");
						
						addComponent(tfSwithl);
						setExpandRatio(tfSwithl, 2);						
					}
				});
				
				addComponent(new FormLayout(){					
					{
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom-new");		
						
						TextField tfMaterial= new TextField("Material");				
						tfMaterial.setWidth("450px");			
						if(ac.getMaterial()!=null){
							
							tfMaterial.setValue(ac.getMaterial().getNome());												
						}
						tfMaterial.setReadOnly(true);
						tfMaterial.setStyleName("caption-align-fornecedores");
						
						addComponent(tfMaterial);
						setExpandRatio(tfMaterial, 2);						
					}
				});
				
				addComponent(new FormLayout(){					
					{
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom");		
						
						TextField tfEndMac= new TextField("Endereço MAC");				
						tfEndMac.setWidth("200px");			
						if(ac.getEndereco_mac()!=null){
							
							tfEndMac.setValue(ac.getEndereco_mac());												
						}
						tfEndMac.setReadOnly(true);
						tfEndMac.setStyleName("caption-align-fornecedores");
						
						addComponent(tfEndMac);
						setExpandRatio(tfEndMac, 2);						
					}
				});	
				
				
				addComponent(new FormLayout(){					
					{
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom-new");		
						
						TextField tfOnu= new TextField("Onu");				
						tfOnu.setWidth("220px");			
						if(ac.getMaterial()!=null){
							
							tfOnu.setValue(ac.getOnu() != null ? ac.getOnu().getNome() : "");												
						}
						tfOnu.setReadOnly(true);
						tfOnu.setStyleName("caption-align-fornecedores");
						
						addComponent(tfOnu);
						setExpandRatio(tfOnu, 2);						
					}
				});
				
				addComponent(new FormLayout(){					
					{
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom");		
						
						TextField tfOnu= new TextField("Onu Serial");				
						tfOnu.setWidth("220px");			
						if(ac.getMaterial()!=null){
							
							tfOnu.setValue(ac.getOnu_serial() != null ? ac.getOnu_serial() : "");												
						}
						tfOnu.setReadOnly(true);
						tfOnu.setStyleName("caption-align-fornecedores");
						
						addComponent(tfOnu);
						setExpandRatio(tfOnu, 2);						
					}
				});
				
				addComponent(new FormLayout(){					
					{
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom");		
						
						TextField tfOnu= new TextField("Olt");				
						tfOnu.setWidth("220px");			
						if(ac.getMaterial()!=null){
							
							tfOnu.setValue(ac.getSwith() != null && ac.getSwith().getOlt() != null ? ac.getSwith().getOlt() : "");												
						}
						tfOnu.setReadOnly(true);
						tfOnu.setStyleName("caption-align-fornecedores");
						
						addComponent(tfOnu);
						setExpandRatio(tfOnu, 2);						
					}
				});
				
				addComponent(new FormLayout(){					
					{
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom");		
						
						TextField tfOnu= new TextField("Gpon");				
						tfOnu.setWidth("220px");			
						if(ac.getMaterial()!=null){
							
							tfOnu.setValue(ac.getSwith() != null && ac.getSwith().getPon() != null && ac.getGpon() != null ? ac.getSwith().getPon() +"/"+ ac.getGpon() : "");												
						}
						tfOnu.setReadOnly(true);
						tfOnu.setStyleName("caption-align-fornecedores");
						
						addComponent(tfOnu);
						setExpandRatio(tfOnu, 2);						
					}
				});
			}				
		};
		
		
		vlRootConexao = new VerticalLayout(){
			{
				addComponent(buildVlConecao());
			}
		};
		
		vlRootConsumo = new VerticalLayout(){
			{
				addComponent(buildVlConsumo());
			}
		};
		

		tbRoot = new TabSheet();
		tbRoot.setHeight("587px");
		tbRoot.setWidth("900px");
	
		tbRoot.addTab(vlInformacoes,"Informações Técnicas");
		tbRoot.addTab(vlRootConexao,"Conexão");
		tbRoot.addTab(vlRootConsumo,"Gráficos");
		tbRoot.setStyleName("caption-align-fornecedores-bottom");
		
		vlRoot.addComponent(tbRoot);
		return null;
		
		}

		private Component buildBtAtualizar() {
			btAtualizar = new Button("Atualizar", new Button.ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					vlRootConexao.replaceComponent(vlconexao, buildVlConecao());		
					
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
	

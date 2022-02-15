package com.digital.opuserp.view.modulos.acesso.contratos_acesso;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import com.digital.opuserp.dao.ContratosAcessoDAO;
import com.digital.opuserp.dao.CredenciaisAcessoDAO;
import com.digital.opuserp.domain.AcessoCliente;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.MikrotikUtil;
import com.digital.opuserp.util.Validator;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Reindeer;

public class AlterarIpFixoEditor extends Window {

	
	Item item;
	Button btSalvar; 
	Button btCancelar;
	FormLayout flPrincipal;	
	VerticalLayout vlRoot;
	
	ComboBox cbPlanos;
	ComboBox cbExclusaoBoletos;
	Cliente Clientelecionado;
	Cliente FiadorSelecionado;
	
	Integer codCliente;
	Integer codFiador;
	
	FieldGroup fieldGroup;
	
	String loginAtual;	
	
	Integer codAcesso;	
	static AcessoCliente acesso;
	TextField txtEnderecoIpFixo;
	boolean logado;
	boolean comunicacao;
	TextField txtComunicacaoConcentrador;
	TextField txtStatusConexao;
	TextField txtEnderecoIpPool;
		
	
	private ComboBox cbEnderecos;
	
	boolean valid_ip = true;
	
	public AlterarIpFixoEditor(Item item, String title, boolean modal){
		this.item = item;
		
		if (item.getItemProperty("login").getValue() != null) {
			loginAtual = item.getItemProperty("login").getValue().toString();
		}
		
		if (item.getItemProperty("id").getValue() != null) {
			codAcesso = (Integer)item.getItemProperty("id").getValue();
			acesso = ContratosAcessoDAO.find(codAcesso);
		}
	
		setWidth("745px");
		setHeight("336px");
		
		setCaption(title);
		setModal(modal);
		setResizable(false);
		setClosable(false);
		center();
		
		vlRoot = new VerticalLayout();	
		vlRoot.setWidth("100%");
		vlRoot.setMargin(true);
		vlRoot.setStyleName("border-form");
		
		setContent(new VerticalLayout(){
			{
				setWidth("100%");
				setMargin(true);
				addComponent(vlRoot);

				HorizontalLayout hlButtons = new HorizontalLayout();
				hlButtons.setStyleName("hl_buttons_bottom");
				hlButtons.setSpacing(true);
				hlButtons.setMargin(true);
				hlButtons.addComponent(buildBtCancelar());
				hlButtons.addComponent(buildBtSalvar());
				
				addComponent(hlButtons);
				setComponentAlignment(hlButtons, Alignment.BOTTOM_RIGHT);
			}
		});
		buildLayout();
	}
	
		
	public void buildLayout(){
		
		fieldGroup = new FieldGroup(item);
		
		vlRoot.addComponent(
			new FormLayout(){					
			{
				setStyleName("form-cutom");
				setMargin(true);
				setSpacing(true);
				
				TextField tfCodContrato = new TextField("Nº de Contrato");
				tfCodContrato.setValue(acesso.getId().toString());
				tfCodContrato.setReadOnly(true);
				tfCodContrato.setWidth("62px");
				tfCodContrato.setStyleName("caption-align-ip-fixo");
				
				addComponent(tfCodContrato);
			}
		});
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
					
					TextField txtCliente = new TextField("Cliente");
					txtCliente.setValue(acesso.getCliente().getNome_razao());
					txtCliente.setReadOnly(true);
					txtCliente.setWidth("362px");
					txtCliente.setStyleName("caption-align-ip-fixo");
					
					addComponent(txtCliente);
				}
			});
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom-new");
					setMargin(true);
					setSpacing(true);
					
					TextField txtStatusConcentrador = new TextField("Status Concentrador");
					txtStatusConcentrador.setValue(item.getItemProperty("id").toString());
					txtStatusConcentrador.setReadOnly(true);
					txtStatusConcentrador.setWidth("142px");
					txtStatusConcentrador.addStyleName("caption-align-ip-fixo");
					
					addComponent(txtStatusConcentrador);
					
					
					boolean statusConcentrador = MikrotikUtil.testconexao(acesso.getBase().getUsuario(),acesso.getBase().getSenha(),acesso.getBase().getEndereco_ip(),Integer.parseInt(acesso.getBase().getPorta_api()));
					
					if(statusConcentrador){
						txtStatusConcentrador.setReadOnly(false);
						txtStatusConcentrador.setValue("UP");
						txtStatusConcentrador.setReadOnly(true);
						txtStatusConcentrador.addStyleName("valid-cpf");
					}else{
						txtStatusConcentrador.setReadOnly(false);
						txtStatusConcentrador.setValue("DOWN");
						txtStatusConcentrador.setReadOnly(true);
						txtStatusConcentrador.addStyleName("invalid-cpf");
					}
					
				}
			});
		
		vlRoot.addComponent(new HorizontalLayout(){
			{
				addComponent(new FormLayout(){					
						{
							setStyleName("form-cutom-new");
							setMargin(true);
							setSpacing(true);
							
							txtComunicacaoConcentrador = new TextField("Comunicação Concentrador");
							txtComunicacaoConcentrador.setValue(item.getItemProperty("id").toString());
							txtComunicacaoConcentrador.setReadOnly(true);
							txtComunicacaoConcentrador.setWidth("142px");
							txtComunicacaoConcentrador.addStyleName("caption-align-ip-fixo");
							
							addComponent(txtComunicacaoConcentrador);
							
							comunicacao =	MikrotikUtil.buscarComunicacao(acesso.getBase().getUsuario(),acesso.getBase().getSenha(),acesso.getBase().getEndereco_ip(), Integer.parseInt(acesso.getBase().getPorta_api()),  acesso.getEndereco_mac());
							logado = MikrotikUtil.buscarStatusConexao(acesso.getBase().getUsuario(),acesso.getBase().getSenha(),acesso.getBase().getEndereco_ip(), Integer.parseInt(acesso.getBase().getPorta_api()), acesso.getLogin());
							
							if(comunicacao || logado){
								txtComunicacaoConcentrador.setReadOnly(false);
								txtComunicacaoConcentrador.setValue("ESTABELECIDA");
								txtComunicacaoConcentrador.setReadOnly(true);
								txtComunicacaoConcentrador.addStyleName("valid-cpf");
							}else{
								txtComunicacaoConcentrador.setReadOnly(false);
								txtComunicacaoConcentrador.setValue("NÃO ESTABELECIDA");
								txtComunicacaoConcentrador.setReadOnly(true);
								txtComunicacaoConcentrador.addStyleName("invalid-cpf");
							}
						}
					});
				
				Button btAtualizar = new Button("Atualizar");
    			btAtualizar.addStyleName(Reindeer.BUTTON_SMALL);
    			btAtualizar.addStyleName("bt_alterar_ip");
    			
    			btAtualizar.addClickListener(new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						comunicacao =	MikrotikUtil.buscarComunicacao(acesso.getBase().getUsuario(),acesso.getBase().getSenha(),acesso.getBase().getEndereco_ip(), Integer.parseInt(acesso.getBase().getPorta_api()),  acesso.getEndereco_mac());
						logado = MikrotikUtil.buscarStatusConexao(acesso.getBase().getUsuario(),acesso.getBase().getSenha(),acesso.getBase().getEndereco_ip(), Integer.parseInt(acesso.getBase().getPorta_api()), acesso.getLogin());
						
						if(comunicacao || logado){
							txtComunicacaoConcentrador.setReadOnly(false);
							txtComunicacaoConcentrador.setValue("ESTABELECIDA");
							txtComunicacaoConcentrador.setReadOnly(true);
							txtComunicacaoConcentrador.removeStyleName("invalid-cpf");
							txtComunicacaoConcentrador.addStyleName("valid-cpf");
						}else{
							txtComunicacaoConcentrador.setReadOnly(false);
							txtComunicacaoConcentrador.setValue("NÃO ESTABELECIDA");
							txtComunicacaoConcentrador.setReadOnly(true);
							txtComunicacaoConcentrador.removeStyleName("valid-cpf");
							txtComunicacaoConcentrador.addStyleName("invalid-cpf");
						}
						
						if(logado){
							txtStatusConexao.setReadOnly(false);
							txtStatusConexao.setValue("LOGADO");
							txtStatusConexao.setReadOnly(true);
							txtStatusConexao.removeStyleName("invalid-cpf");
							txtStatusConexao.addStyleName("valid-cpf");
						}else{
							txtStatusConexao.setReadOnly(false);
							txtStatusConexao.setValue("NÃO LOGADO");
							txtStatusConexao.setReadOnly(true);
							txtStatusConexao.removeStyleName("valid-cpf");
							txtStatusConexao.addStyleName("invalid-cpf");
						}
						
						txtEnderecoIpPool.setReadOnly(false);		
						
						String ip = MikrotikUtil.getRemoteIpPPOE(acesso.getBase().getEndereco_ip(),acesso.getBase().getUsuario(), acesso.getBase().getSenha(), acesso.getLogin());
						txtEnderecoIpPool.setValue(ip);										
						BrowserWindowOpener openIpPool = new BrowserWindowOpener("http://"+ip);
						openIpPool.setFeatures("height=600,width=800");
						openIpPool.extend(txtEnderecoIpPool);
				
						txtEnderecoIpPool.setValue(ip);
						txtEnderecoIpPool.setReadOnly(true);	
					}
				});
    			
    			addComponent(btAtualizar);
				
			}
		});
		    vlRoot.addComponent(new HorizontalLayout(){
		    	{
		    			addComponent(new FormLayout(){					
							{
								setStyleName("form-cutom");
								setMargin(true);
								setSpacing(true);
								
								txtStatusConexao = new TextField("Status da Conexão");
								txtStatusConexao.setValue(item.getItemProperty("id").toString());
								txtStatusConexao.setReadOnly(true);
								txtStatusConexao.setWidth("142px");
								txtStatusConexao.addStyleName("caption-align-ip-fixo");
								
								addComponent(txtStatusConexao);
								
								
								
								if(logado){
									txtStatusConexao.setReadOnly(false);
									txtStatusConexao.setValue("LOGADO");
									txtStatusConexao.setReadOnly(true);
									txtStatusConexao.removeStyleName("invalid-cpf");
									txtStatusConexao.addStyleName("valid-cpf");
								}else{
									txtStatusConexao.setReadOnly(false);
									txtStatusConexao.setValue("NÃO LOGADO");
									txtStatusConexao.setReadOnly(true);
									txtStatusConexao.removeStyleName("valid-cpf");
									txtStatusConexao.addStyleName("invalid-cpf");
								}
							}
						});
		    			
		    			Button btDesconectar = new Button("Desconectar");
		    			btDesconectar.addStyleName(Reindeer.BUTTON_SMALL);
		    			btDesconectar.addStyleName("bt_alterar_ip");
		    			
		    			btDesconectar.addClickListener(new Button.ClickListener() {
							
							@Override
							public void buttonClick(ClickEvent event) {
								boolean check = MikrotikUtil.desconectarCliente(acesso.getBase().getUsuario(), acesso.getBase().getSenha(), acesso.getBase().getEndereco_ip(), Integer.parseInt(acesso.getBase().getPorta_api()),acesso.getLogin());
								if(check){
									Notification.show("O Cliente foi Desconectado Com Sucesso!");
								}else{
									Notification.show("O Cliente já esta Desconectado!");
								}
							}
						});
		    			addComponent(btDesconectar);
		    	}
		    });
		    vlRoot.addComponent(
					new FormLayout(){					
					{
						setStyleName("form-cutom");
						setMargin(true);
						setSpacing(true);
						
						txtEnderecoIpPool = new TextField("Endereço IP Pool");
						//txtEnderecoIpPool.setValue(item.getItemProperty("id").toString());
						txtEnderecoIpPool.setReadOnly(true);
						txtEnderecoIpPool.setWidth("142px");
						txtEnderecoIpPool.setStyleName("caption-align-ip-fixo");
						
						txtEnderecoIpPool.setReadOnly(false);		
										
						String ip = MikrotikUtil.getRemoteIpPPOE(acesso.getBase().getEndereco_ip(),acesso.getBase().getUsuario(), acesso.getBase().getSenha(), acesso.getLogin());
						txtEnderecoIpPool.setValue(ip);										
						BrowserWindowOpener openIpPool = new BrowserWindowOpener("http://"+ip+":1802");
						openIpPool.setFeatures("height=600,width=800");
						openIpPool.extend(txtEnderecoIpPool);
				
						txtEnderecoIpPool.setValue(ip);
						txtEnderecoIpPool.setReadOnly(true);						
						
						addComponent(txtEnderecoIpPool);
					}
				});
		    vlRoot.addComponent(new HorizontalLayout(){
		    	{
		    			addComponent(new FormLayout(){					
							{
								setStyleName("form-cutom");
								setMargin(true);
								setSpacing(true);
								
								txtEnderecoIpFixo = new TextField("Endereço IP Fixo");
								txtEnderecoIpFixo.setValue(acesso.getEndereco_ip());						
								txtEnderecoIpFixo.setWidth("142px");
								txtEnderecoIpFixo.addStyleName("caption-align-ip-fixo");
								txtEnderecoIpFixo.setNullRepresentation("");
								txtEnderecoIpFixo.setImmediate(true);
								txtEnderecoIpFixo.addTextChangeListener(new FieldEvents.TextChangeListener() {
									
									@Override
									public void textChange(TextChangeEvent event) {
										
										if(event.getText() != null && !event.getText().equals("")){
											boolean disponivel = CredenciaisAcessoDAO.ipDisponibility(event.getText());
											boolean valido = Validator.IPAddress(event.getText());
											
											if(disponivel && valido){
												txtEnderecoIpFixo.removeStyleName("invalid-cpf");
												txtEnderecoIpFixo.addStyleName("valid-cpf");
												valid_ip = true;
											}else{
												txtEnderecoIpFixo.removeStyleName("valid-cpf");
												txtEnderecoIpFixo.addStyleName("invalid-cpf");
												valid_ip = false;
											}
										}else{
											valid_ip = true;
										}
									}
								});
								
								addComponent(txtEnderecoIpFixo);
							}
						});
		    			
		    			Button btPool = new Button("Pool");
		    			btPool.addStyleName(Reindeer.BUTTON_SMALL);
		    			btPool.addStyleName("bt_alterar_ip");
		    			
		    			btPool.addClickListener(new  Button.ClickListener() {
							
							@Override
							public void buttonClick(ClickEvent event) {
								Window w = new Window("Informações do Concentrador "+acesso.getBase().getIdentificacao());
								//w.setHeight("120px");
								w.setWidth("600px");
								w.setModal(true);
								w.center();
								
								final Table tbPool = new Table("Pools");
								tbPool.setWidth("100%");
								tbPool.setHeight("120px");
								tbPool.setSelectable(true);
								tbPool.addContainerProperty("Nome", String.class, "");
								tbPool.addContainerProperty("Ranges", String.class, "");
								tbPool.addContainerProperty("Next-Pool", String.class, "");

								List<Map<String, String>> list =	MikrotikUtil.getInfoPool(acesso.getBase().getUsuario(), acesso.getBase().getSenha(), acesso.getBase().getEndereco_ip(), Integer.parseInt(acesso.getBase().getPorta_api()));
							    
								for(Map<String, String> mItem : list){								
									tbPool.addItem(new Object[]{mItem.get("name"),mItem.get("ranges"),mItem.get("next-pool")}, tbPool.getItemIds().size()+1);
								}
								
								final Table tbOspfNetworks = new Table("Networks");
								tbOspfNetworks.setWidth("100%");
								tbOspfNetworks.setHeight("120px");
								tbOspfNetworks.setSelectable(true);
								tbOspfNetworks.addContainerProperty("Network", String.class, "");
								tbOspfNetworks.addContainerProperty("Area", String.class, "");							

								List<Map<String, String>> listOspfNetwork =	MikrotikUtil.getInfoOspfNetwork(acesso.getBase().getUsuario(), acesso.getBase().getSenha(), acesso.getBase().getEndereco_ip(), Integer.parseInt(acesso.getBase().getPorta_api()));
							    
								for(Map<String, String> mItem : listOspfNetwork){								
									tbOspfNetworks.addItem(new Object[]{mItem.get("network"),mItem.get("area")}, tbOspfNetworks.getItemIds().size()+1);
								}
								
								JPAContainer<AcessoCliente> containerAcesso = JPAContainerFactory.make(AcessoCliente.class, ConnUtil.getEntity());
								containerAcesso.addContainerFilter(Filters.isNotEmpty("endereco_ip"));
								containerAcesso.setReadOnly(true); 
								
								final Table tbIpsFixados = new Table("Ips Indisponíveis", containerAcesso);
								tbIpsFixados.setWidth("100%");
								tbIpsFixados.setHeight("120px");
								tbIpsFixados.setSelectable(true);
								tbIpsFixados.setVisibleColumns(new Object[]{"endereco_ip"});
								tbIpsFixados.setColumnHeader("endereco_ip", "Endereço IP");
								tbIpsFixados.sort(new Object[]{"endereco_ip"},new boolean[]{true});
							    
								w.setContent(new VerticalLayout(){
									{
										setMargin(true);
										setSpacing(true);
										
										addComponent(tbPool);
										addComponent(tbOspfNetworks);
										addComponent(tbIpsFixados); 
									}
								});
								
								getUI().addWindow(w);
							}
						});
		    			
		    			addComponent(btPool);
		    			
		    			
		    	}
		    });
		    
		
		
		
		

	
	}

	private void concluirAlteracao(){
		
		if(valid_ip){
			try {	
				if(fieldGroup.isValid()){
					try {		
						fieldGroup.commit();											
						fireEvent(new AlterarIpFixoEvent(getUI(),true,item,txtEnderecoIpFixo.getValue()));													
					} catch (Exception e) {											
						Notification.show("ERRO: Não foi Possivel Salvar as Alterações!");		
						e.printStackTrace();													
					}
				}else{
					
					   for (Field<?> field: fieldGroup.getFields()) {						
							if(!field.isValid()){
								field.addStyleName("invalid-txt");
							}else{
								field.removeStyleName("invalid-txt");
							}
					    }

					    if(!cbEnderecos.isValid()){
					    	cbEnderecos.addStyleName("invalid-txt");
					    }else{
					    	cbEnderecos.removeStyleName("invalid-txt");  	
					    }
					Notification.show("Não é Possivel Salvar, Verifique os Campos Obrigatórios/Inválidos e Tente Novamente!");
				}
			} catch (Exception e) {											
				Notification.show("ERRO: Não foi Possivel Salvar as Alterações!");		
				e.printStackTrace();						
			}
		}else{
			Notify.Show("Endereço de Ip Inválido/Incorreto", Notify.TYPE_ERROR);
		}
		
	}
	public Button buildBtSalvar() {
		
		btSalvar = new Button("Gravar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {			
				concluirAlteracao();				
			}
		});
		

		ShortcutListener slbtOK = new ShortcutListener("Ok", ShortcutAction.KeyCode.ENTER,null) {
			
			@Override
			public void handleAction(Object sender, Object target) {
				btSalvar.click();
			}
		};
		
		btSalvar.addShortcutListener(slbtOK);		
		btSalvar.setStyleName("default");
		//btSalvar.setEnabled(false);
		return btSalvar;
	}

	
	public Button buildBtCancelar() {
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
		btCancelar.setEnabled(true);
		
		return btCancelar;
	}
	
	
	public void addListerner(AlterarIpFixoListerner target){
		try {
			Method method = AlterarIpFixoListerner.class.getDeclaredMethod("onClose", AlterarIpFixoEvent.class);
			addListener(AlterarIpFixoEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(AlterarIpFixoListerner target){
		removeListener(AlterarIpFixoEvent.class, target);
	}
	public static class AlterarIpFixoEvent extends Event{
		
		
		private boolean confirm;
		private Item item;
		
		private String endereco_ip;
		
		public AlterarIpFixoEvent(Component source,boolean confirm,Item item, String endereco_ip ) {
			super(source);			
			this.confirm = confirm;
			this.item = item;
			this.endereco_ip = endereco_ip;
						
		}

		public Item getItem(){	
			return item;
		}

		public boolean isConfirm() {
			return confirm;
		}	
		public String getEnderecoIp(){
			return endereco_ip;
		}
		public AcessoCliente getAcesso(){
			return acesso;
		}
		
	}
	public interface AlterarIpFixoListerner extends Serializable{
		public void onClose(AlterarIpFixoEvent event);
	}
}


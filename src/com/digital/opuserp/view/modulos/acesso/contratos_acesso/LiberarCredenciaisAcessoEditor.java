package com.digital.opuserp.view.modulos.acesso.contratos_acesso;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.CredenciaisAcessoDAO;
import com.digital.opuserp.dao.ProdutoDAO;
import com.digital.opuserp.dao.SwithDAO;
import com.digital.opuserp.domain.AcessoCliente;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.Concentrador;
import com.digital.opuserp.domain.ContratosAcesso;
import com.digital.opuserp.domain.Produto;
import com.digital.opuserp.domain.SerialProduto;
import com.digital.opuserp.domain.Swith;
import com.digital.opuserp.interfaces.GenericEditor;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.util.MikrotikUtil;
import com.digital.opuserp.util.SignalStrengthUtil;
import com.digital.opuserp.util.Validator;
import com.digital.opuserp.util.mk_bean.WirelessInterface;
import com.digital.opuserp.view.modulos.cadastro.transportadoras.TransportadoraEditor.TransportadoraEvent;
import com.digital.opuserp.view.util.MaterialUtil;
import com.digital.opuserp.view.util.MaterialUtil.MaterialEvent;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.fieldfactory.SingleSelectConverter;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;
import com.vaadin.ui.themes.Reindeer;

public class LiberarCredenciaisAcessoEditor extends Window implements GenericEditor {
	Item item;
	Button btSalvar; 
	Button btCancelar;
	FormLayout flPrincipal;
	FieldGroup fieldGroup;
	VerticalLayout vlRoot;
	Button btPool;
	
	Concentrador concentrador;
	
	
	boolean valid_ip = false;
	boolean valid_mac = false;
	boolean valid_serial_material_comodato = false;
	boolean valid_usuario = true;
	boolean valid_senha = true;
	boolean Validar_signal = false;
	
	ComboBox cbInterface;
	
	String loginAtual;
	String ipAtual;
	Produto material_selecionado;
	Produto material_selecionado_comodato;
	Integer codAcesso;
	
	private TextField tfCodMaterial;
	private TextField tfDescricaoMaterial;
	private TextField tfDescricaoMaterialComodato;
	private ComboBox cbSerial;
	private ComboBox cbSerialMaterialComodato;
	private TextField txtSerial;
	private TextField txtMacOpcional;
	private ComboBox cbCaixaAtendimento;
	private TextField txtPON;
	private TextField txtGpon;
	private TextField txtOLT;
	private TextField txtSinalDb;
	private ComboBox cbConcentradores;
	
	private JPAContainer<SerialProduto> containerSeriais;
	private ContratosAcesso contrato;
	
	public LiberarCredenciaisAcessoEditor(Item item, String title, boolean modal){
		this.item = item;
		
		if(item.getItemProperty("material") != null){
			material_selecionado = (Produto)item.getItemProperty("material").getValue();
		}
				
		contrato = (ContratosAcesso) item.getItemProperty("contrato").getValue();
		
		setWidth("755px");
		
		if(item.getItemProperty("regime").getValue().toString().equals("PROPRIO (PARCIAL)") || item.getItemProperty("regime").getValue().toString().equals("COMODATO (TOTAL)") ){
			setHeight("686px");
		}else{		
			setHeight("548px");
		}
				
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
		
		
		if (item.getItemProperty("login").getValue() != null) {
			loginAtual = item.getItemProperty("login").getValue().toString();
		}
		if (item.getItemProperty("endereco_ip").getValue() != null) {
			ipAtual = item.getItemProperty("endereco_ip").getValue().toString();
		}
		
		
		buildLayout();
	}
	
	
	public void buildLayout(){
		
		
		fieldGroup = new FieldGroup(item);
		
		
		vlRoot.addComponent(new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
					
					TextField tfCodContrato = new TextField("Contrato");
					tfCodContrato.setValue(item.getItemProperty("id").getValue().toString());
					tfCodContrato.setReadOnly(true);
					tfCodContrato.setWidth("62px");
					tfCodContrato.setStyleName("caption-align");
					
					addComponent(tfCodContrato);
				}
		});
					
		

		vlRoot.addComponent(new FormLayout(){					
			{
				setStyleName("form-cutom");
				setMargin(true);
				setSpacing(true);
				
				Cliente ca = (Cliente)item.getItemProperty("cliente").getValue();
				TextField tfContratoAtual = new TextField("Cliente");
				tfContratoAtual.setValue(ca.getNome_razao());
				tfContratoAtual.setReadOnly(true);
				tfContratoAtual.setStyleName("caption-align");
				tfContratoAtual.setWidth("360px");				
				
				
				addComponent(tfContratoAtual);	
				
			}
		});
		
		
		
		vlRoot.addComponent(new FormLayout(){					
			{
				setStyleName("form-cutom-new");
				setMargin(true);
				setSpacing(true);
					
				codAcesso = (Integer) item.getItemProperty("id").getValue();
				
				addComponent(fieldGroup.buildAndBind("Usuário", "login"));
				fieldGroup.getField("login").setRequired(true);
				((TextField)fieldGroup.getField("login")).setNullRepresentation("");
				((TextField)fieldGroup.getField("login")).setImmediate(true);
				((TextField)fieldGroup.getField("login")).setStyleName("caption-align");
				((TextField)fieldGroup.getField("login")).setMaxLength(100);
				((TextField)fieldGroup.getField("login")).setDescription("Apenas letras minúsculas");
				((TextField)fieldGroup.getField("login")).setInputPrompt(codAcesso.toString());
				
				if(((TextField)fieldGroup.getField("login")).getValue() != null && !((TextField)fieldGroup.getField("login")).getValue().equals("") && !((TextField)fieldGroup.getField("login")).getValue().isEmpty()){
					
					if(!((TextField)fieldGroup.getField("login")).getValue().equals(loginAtual)){
						
						CredenciaisAcessoDAO caDAO = new CredenciaisAcessoDAO();				
						
						Pattern pattern = Pattern.compile("[a-z-_0-9]{4,}");
						Matcher matcher = pattern.matcher(((TextField)fieldGroup.getField("login")).getValue());
						if(caDAO.usernameDisponibility(((TextField)fieldGroup.getField("login")).getValue()) && matcher.matches()){
							((TextField)fieldGroup.getField("login")).addStyleName("valid-cpf");
							((TextField)fieldGroup.getField("login")).removeStyleName("invalid-cpf");
							valid_usuario = true;
						}else{
							((TextField)fieldGroup.getField("login")).removeStyleName("valid-cpf");
							((TextField)fieldGroup.getField("login")).addStyleName("invalid-cpf");
							valid_usuario = false;
						}
					}else{
						((TextField)fieldGroup.getField("login")).addStyleName("valid-cpf");
						((TextField)fieldGroup.getField("login")).removeStyleName("invalid-cpf");
						valid_usuario = true;
					}
				}
				
				((TextField)fieldGroup.getField("login")).setTextChangeEventMode(TextChangeEventMode.LAZY);
				((TextField)fieldGroup.getField("login")).addListener(new FieldEvents.TextChangeListener() {
					
					@Override
					public void textChange(TextChangeEvent event) {
						if(event.getText() != null && !event.getText().equals("")){
							
							if(!event.getText().equals(loginAtual)){
							
								CredenciaisAcessoDAO caDAO = new CredenciaisAcessoDAO();
								
								 Pattern pattern = Pattern.compile("[a-z-_0-9]{4,}");
							     Matcher matcher = pattern.matcher(event.getText());
	
								if(caDAO.usernameDisponibility(event.getText()) && matcher.matches()){
									event.getComponent().addStyleName("valid-cpf");
									event.getComponent().removeStyleName("invalid-cpf");
									valid_usuario = true;
								}else
								{
									event.getComponent().removeStyleName("valid-cpf");
									event.getComponent().addStyleName("invalid-cpf");
									valid_usuario = false;
								}
							
							}else{
								event.getComponent().addStyleName("valid-cpf");
								event.getComponent().removeStyleName("invalid-cpf");
								valid_usuario = true;
							}
						}
					}
				});
			}
		});
		
		vlRoot.addComponent(new FormLayout(){					
			{
				setStyleName("form-cutom");
				setMargin(true);
				setSpacing(true);

				addComponent(fieldGroup.buildAndBind("Senha", "senha"));				
				fieldGroup.getField("senha").setRequired(true);
				((TextField)fieldGroup.getField("senha")).setNullRepresentation("");
				((TextField)fieldGroup.getField("senha")).setImmediate(true);
				((TextField)fieldGroup.getField("senha")).setStyleName("caption-align");
				((TextField)fieldGroup.getField("senha")).setMaxLength(100);	
				((TextField)fieldGroup.getField("senha")).setInputPrompt(codAcesso.toString());
				
				if(((TextField)fieldGroup.getField("senha")).getValue() != null && !((TextField)fieldGroup.getField("senha")).getValue().equals("") && !((TextField)fieldGroup.getField("senha")).getValue().isEmpty()){

					 Pattern pattern = Pattern.compile("[A-Za-z0-9]{4,}");
				     Matcher matcher = pattern.matcher(((TextField)fieldGroup.getField("senha")).getValue());
					
					if(matcher.matches()){
						((TextField)fieldGroup.getField("senha")).removeStyleName("invalid-cpf");
						((TextField)fieldGroup.getField("senha")).addStyleName("valid-cpf");
						valid_senha = true;
					}else
					{
						((TextField)fieldGroup.getField("senha")).removeStyleName("valid-cpf");
						((TextField)fieldGroup.getField("senha")).addStyleName("invalid-cpf");
						valid_senha = false;
					}
				}
				
				((TextField)fieldGroup.getField("senha")).setTextChangeEventMode(TextChangeEventMode.LAZY);
				((TextField)fieldGroup.getField("senha")).addListener(new FieldEvents.TextChangeListener() {
					
					@Override
					public void textChange(TextChangeEvent event) {
						if(event.getText() != null && !event.getText().equals("")){

							 Pattern pattern = Pattern.compile("[A-Za-z0-9]{4,}");
						     Matcher matcher = pattern.matcher(event.getText());
							
							if(matcher.matches()){
								event.getComponent().removeStyleName("invalid-cpf");
								event.getComponent().addStyleName("valid-cpf");
								valid_senha = true;
							}else
							{
								event.getComponent().removeStyleName("valid-cpf");
								event.getComponent().addStyleName("invalid-cpf");
								valid_senha = false;
							}
						}
					}
				});
			}
		});
		
		
		
		
		
		cbInterface = new ComboBox("Interface");
		cbInterface.setNullSelectionAllowed(false);
		cbInterface.setRequired(true);
		//cbInterface.setItemCaptionPropertyId("name");
		//cbInterface.setConverter(com.vaadin.data.util.converter.ConverterUtil.convertFromModel(modelValue, presentationType, converter, locale));
		cbInterface.setStyleName("caption-align");
		cbInterface.setWidth("190px");
		
		
		vlRoot.addComponent(new FormLayout(){					
			{
				setStyleName("form-cutom-new");
				setMargin(true);
				setSpacing(true);
					
				JPAContainer<Concentrador> containerConcentradores = JPAContainerFactory.make(Concentrador.class, ConnUtil.getEntity());
				containerConcentradores.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
				containerConcentradores.addContainerFilter(Filters.eq("status", "ATIVO"));
				containerConcentradores.sort(new Object[]{"identificacao"}, new boolean[]{true});
				
				cbConcentradores = new ComboBox("Concentrador", containerConcentradores);
				cbConcentradores.setNullSelectionAllowed(false);
				cbConcentradores.setRequired(true);
				cbConcentradores.setImmediate(true);
				cbConcentradores.setItemCaptionPropertyId("identificacao");

				cbConcentradores.setConverter(new SingleSelectConverter(cbConcentradores));
			
				cbConcentradores.setStyleName("caption-align");
				cbConcentradores.setWidth("250px");
				
				cbConcentradores.addValueChangeListener(new Property.ValueChangeListener() {
					
					@Override
					public void valueChange(ValueChangeEvent event) {
						
						try{		
							if(cbConcentradores != null && cbConcentradores.getValue() != null){
								EntityItem<Concentrador> eItemConc = (EntityItem<Concentrador>)cbConcentradores.getItem(cbConcentradores.getValue());
								concentrador = eItemConc.getEntity();
								if(btPool != null){
									btPool.setEnabled(true);
								}
								
								String usuario = cbConcentradores.getItem(cbConcentradores.getValue()).getItemProperty("usuario").getValue().toString();
								String senha = cbConcentradores.getItem(cbConcentradores.getValue()).getItemProperty("senha").getValue().toString();
								String ip = cbConcentradores.getItem(cbConcentradores.getValue()).getItemProperty("endereco_ip").getValue().toString();
								Integer porta = Integer.parseInt(cbConcentradores.getItem(cbConcentradores.getValue()).getItemProperty("porta_api").getValue().toString());
								String fibra = cbConcentradores.getItem(cbConcentradores.getValue()).getItemProperty("fibra").getValue() != null && 
										!cbConcentradores.getItem(cbConcentradores.getValue()).getItemProperty("fibra").getValue().toString().equals("") ? 
												cbConcentradores.getItem(cbConcentradores.getValue()).getItemProperty("fibra").getValue().toString() : "NAO";
												
								if(fibra.equals("SIM")){
									if(txtSinalDb != null){
										txtSinalDb.setEnabled(true);
										txtSinalDb.setRequired(true);
									}
									
									if(txtGpon != null){
										txtGpon.setEnabled(true);
										txtGpon.setRequired(true);
									}
								}else{
									if(txtSinalDb != null){
										txtSinalDb.setEnabled(true);
										txtSinalDb.setValue("");
										txtSinalDb.setEnabled(false);
										txtSinalDb.setRequired(false);
									}
									
									
									if(txtGpon != null){
										
										txtGpon.setEnabled(true);
										txtGpon.setValue("");
										txtGpon.setEnabled(false);
										txtGpon.setRequired(false);
									}
								}
								
								List<Map<String, String>> result  = MikrotikUtil.listarInterfacesWireless(usuario, senha, ip, porta);
								BeanItemContainer<WirelessInterface> interfaces = new BeanItemContainer<>(WirelessInterface.class);
								
								if(result != null){
									for (Map<String, String> wirelessInterface : result) {
								
										String mtu = wirelessInterface.get("mtu");
										if(mtu.equals("1500")){								
											WirelessInterface wire = new WirelessInterface();
											wire.setName(wirelessInterface.get("name")); 
											interfaces.addBean(wire);
										}
		
									}						
								}
								interfaces.size();
								
								cbInterface.setContainerDataSource(interfaces);
								cbInterface.setItemCaptionPropertyId("name");
								
								
								Integer codConcentrador = (Integer)cbConcentradores.getItem(cbConcentradores.getValue()).getItemProperty("id").getValue();
								List<Swith> swiths = SwithDAO.getSwithsbyConcentrador(codConcentrador);
								
								if(swiths != null && swiths.size() > 0){
								
									JPAContainer<Swith> swithsContainer = JPAContainerFactory.make(Swith.class, ConnUtil.getEntity());
									swithsContainer.addContainerFilter(Filters.eq("concentrador", new Concentrador(codConcentrador)));
									swithsContainer.sort(new Object[]{"identificacao"}, new boolean[]{true});
									
									if(cbCaixaAtendimento != null){
										cbCaixaAtendimento.setEnabled(true);
										cbCaixaAtendimento.setRequired(true);
										cbCaixaAtendimento.setContainerDataSource(swithsContainer);
										cbCaixaAtendimento.setItemCaptionPropertyId("identificacao");
										cbCaixaAtendimento.setConverter(new SingleSelectConverter(cbCaixaAtendimento));
										fieldGroup.bind(cbCaixaAtendimento, "swith");
									}
									
								}else{
									cbCaixaAtendimento.setEnabled(false);
									cbCaixaAtendimento.setRequired(false);
								}
							
							
							}
						}catch(Exception e){
							e.printStackTrace();							
							Notify.Show("ERRO: "+e.getLocalizedMessage(), Notify.TYPE_ERROR);
						}
						
					}
				});
				
				addComponent(cbConcentradores);		
				
				fieldGroup.bind(cbConcentradores, "base");
			}
		});
		vlRoot.addComponent(new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);					
										
					addComponent(cbInterface);
					
					if(item.getItemProperty("interfaces").getValue() != null){
						
						String interfaces = item.getItemProperty("interfaces").getValue().toString();
						for (Object itemId: cbInterface.getItemIds()) {
							
							if(cbInterface.getItem(itemId).getItemProperty("name") != null && cbInterface.getItem(itemId).getItemProperty("name").getValue().toString().equals(interfaces)){
								cbInterface.select(itemId);
								break;
							}							
						}												
					}
					
					//fieldGroup.bind(cbInterface, "interfaces");
				}
			});
		vlRoot.addComponent(new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
					
					addComponent(fieldGroup.buildAndBind("Signal Strength", "signal_strength"));					
					((TextField)fieldGroup.getField("signal_strength")).setNullRepresentation("");
					((TextField)fieldGroup.getField("signal_strength")).setStyleName("caption-align");
					((TextField)fieldGroup.getField("signal_strength")).setInputPrompt("-120..120");
					((TextField)fieldGroup.getField("signal_strength")).setMaxLength(9);
					((TextField)fieldGroup.getField("signal_strength")).setImmediate(true);
					((TextField)fieldGroup.getField("signal_strength")).addListener(new FieldEvents.TextChangeListener() {
						
						@Override
						public void textChange(TextChangeEvent event) {
								
								if(event.getText()!=null && !event.getText().equals("")){

									boolean valid = SignalStrengthUtil.validSignalStrength(event.getText());
									if(valid){
										event.getComponent().setStyleName("valid-cpf");
										Validar_signal = true;
									}else{
										event.getComponent().setStyleName("invalid-cpf");
										Validar_signal = false;	
									}

								}
							}
					});	
				}
		});
		vlRoot.addComponent(new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
					
					cbCaixaAtendimento = new ComboBox("Caixa Atendimento");
					cbCaixaAtendimento.setRequired(true);
					cbCaixaAtendimento.setNullSelectionAllowed(false);
					cbCaixaAtendimento.setEnabled(false);
					cbCaixaAtendimento.setStyleName("caption-align");
					
					cbCaixaAtendimento.addValueChangeListener(new Property.ValueChangeListener() {
						
						@Override
						public void valueChange(ValueChangeEvent event) {
							if(txtPON != null && cbCaixaAtendimento.getValue() != null && cbCaixaAtendimento.getItem(cbCaixaAtendimento.getValue()).getItemProperty("pon").getValue() != null){
								txtPON.setEnabled(true);
								txtPON.setValue(cbCaixaAtendimento.getItem(cbCaixaAtendimento.getValue()).getItemProperty("pon").getValue().toString());
								txtPON.setEnabled(false);
							}else{
								if(txtPON != null){
									txtPON.setEnabled(true);
									txtPON.setValue("");
									txtPON.setEnabled(false);
								}
							}
							
							if(txtOLT != null && cbCaixaAtendimento.getValue() != null && cbCaixaAtendimento.getItem(cbCaixaAtendimento.getValue()).getItemProperty("olt").getValue() != null){
								txtOLT.setEnabled(true);
								txtOLT.setValue(cbCaixaAtendimento.getItem(cbCaixaAtendimento.getValue()).getItemProperty("olt").getValue().toString());
								txtOLT.setEnabled(false);
							}else{
								if(txtOLT != null){
									txtOLT.setEnabled(true);
									txtOLT.setValue("");
									txtOLT.setEnabled(false);
								}
							}
						}
					});
					
					addComponent(cbCaixaAtendimento);
									
				}
		});
		vlRoot.addComponent(new HorizontalLayout(){					
				{
					setStyleName("form-cutom-new");
					setMargin(true);
					setSpacing(true);
					
					final TextField txtEnderecoIP = new TextField("Endereço Fixo");
					addComponent(new FormLayout(){
						{							
							txtEnderecoIP.setNullRepresentation("");
							txtEnderecoIP.setStyleName("caption-align");
							txtEnderecoIP.setMaxLength(100);
							txtEnderecoIP.setDescription("Informar o IP do Cliente");
							
							addComponent(txtEnderecoIP);						
						}
					});
					
					
					btPool = new Button("Pools", new Button.ClickListener() {
						
						@Override
						public void buttonClick(ClickEvent event) {
							
							Window w = new Window("Informações do Concentrador "+concentrador.getIdentificacao());
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

							List<Map<String, String>> list =	MikrotikUtil.getInfoPool(concentrador.getUsuario(), concentrador.getSenha(), concentrador.getEndereco_ip(), Integer.parseInt(concentrador.getPorta_api()));
						    
							for(Map<String, String> mItem : list){								
								tbPool.addItem(new Object[]{mItem.get("name"),mItem.get("ranges"),mItem.get("next-pool")}, tbPool.getItemIds().size()+1);
							}
							
							final Table tbOspfNetworks = new Table("Networks");
							tbOspfNetworks.setWidth("100%");
							tbOspfNetworks.setHeight("120px");
							tbOspfNetworks.setSelectable(true);
							tbOspfNetworks.addContainerProperty("Network", String.class, "");
							tbOspfNetworks.addContainerProperty("Area", String.class, "");							

							List<Map<String, String>> listOspfNetwork =	MikrotikUtil.getInfoOspfNetwork(concentrador.getUsuario(), concentrador.getSenha(), concentrador.getEndereco_ip(), Integer.parseInt(concentrador.getPorta_api()));
						    
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
					btPool.setEnabled(false);
					btPool.addStyleName(Reindeer.BUTTON_SMALL);					
					addComponent(btPool);					
					setComponentAlignment(btPool, Alignment.MIDDLE_CENTER);
					
					fieldGroup.bind(txtEnderecoIP, "endereco_ip");
									
					if(item.getItemProperty("endereco_ip") != null &&
							item.getItemProperty("endereco_ip").getValue() != null &&
							!item.getItemProperty("endereco_ip").getValue().toString().isEmpty() && 
							!item.getItemProperty("endereco_ip").getValue().toString().equals("")){
						
						if(txtEnderecoIP.getValue() != ipAtual){
							
							if(Validator.IPAddress(((TextField)fieldGroup.getField("endereco_ip")).getValue())){
								txtEnderecoIP.setStyleName("valid-cpf");
								valid_ip = true;
							}else{
								txtEnderecoIP.setStyleName("invalid-cpf");
								valid_ip = false;
							}
						}else{
							txtEnderecoIP.setStyleName("valid-cpf");
							valid_ip = true;						
						}
					}
					
					txtEnderecoIP.setTextChangeEventMode(TextChangeEventMode.LAZY);
					txtEnderecoIP.setId("txtEnderecoIP");
					
					JavaScript.getCurrent().execute("$('#txtEnderecoIP').mask('0ZZ.0ZZ.0ZZ.0ZZ', {translation: {'Z': \"[0-9]?\"}})");
					
					txtEnderecoIP.addListener(new FieldEvents.TextChangeListener() {
						
						@Override
						public void textChange(TextChangeEvent event) {
						
							if(!event.getText().equals("") && event.getText() != null){
															
								if(Validator.IPAddress(event.getText())){
									
									if(CredenciaisAcessoDAO.ipDisponibility(event.getText())){
										event.getComponent().setStyleName("valid-cpf");
										valid_ip = true;
									}else{
										event.getComponent().setStyleName("invalid-cpf");
										valid_ip = false;
									}
								}else{
									event.getComponent().setStyleName("invalid-cpf");
									valid_ip = false;
								}
							}
						}
					});
				}
			});
		
		vlRoot.addComponent(new FormLayout(){					
				{
					setStyleName("form-cutom-new");
					setMargin(true);
					setSpacing(true);
											
									
					TextField tfRegime = new TextField("Regime");
					tfRegime.setStyleName("caption-align");					
					tfRegime.setRequired(true);
					tfRegime.setWidth("241px");
					tfRegime.setValue(item.getItemProperty("regime").getValue().toString());
					tfRegime.setReadOnly(true);
					
															
					addComponent(tfRegime);					
					
				}
		});
		
		
		if(item.getItemProperty("regime").getValue().toString().equals("PROPRIO (PARCIAL)") || 
				item.getItemProperty("regime").getValue().toString().equals("COMODATO (TOTAL)")){
			
			vlRoot.addComponent(new HorizontalLayout(){
				{
												
					
					
					final TextField tfCodMaterialComodato = new TextField("ONU");				
					tfCodMaterialComodato.setWidth("60px");				
					tfCodMaterialComodato.setNullRepresentation("");
					tfCodMaterialComodato.setStyleName("caption-align");
					tfCodMaterialComodato.setImmediate(true);
							
									
					tfCodMaterialComodato.addListener(new TextChangeListener() {
						
						@Override
						public void textChange(TextChangeEvent event) {
							ProdutoDAO cDAO = new ProdutoDAO();
							material_selecionado_comodato = new Produto();
							
							if(event.getText()!=null && !event.getText().isEmpty() && !event.getText().equals("")){
								
															
								material_selecionado_comodato = cDAO.find(Integer.parseInt(event.getText()));		
								
								if(material_selecionado_comodato != null){
									tfDescricaoMaterialComodato.setReadOnly(false);
									tfDescricaoMaterialComodato.setValue(material_selecionado_comodato.getNome());
							 		tfDescricaoMaterialComodato.setReadOnly(true);
									
									if(cbSerialMaterialComodato != null){
										cbSerialMaterialComodato.setEnabled(true);
										cbSerialMaterialComodato.setContainerDataSource(buildContainerSeriais(material_selecionado_comodato.getId()));		
										cbSerialMaterialComodato.setItemCaptionPropertyId("serial");
										cbSerialMaterialComodato.setItemCaptionPropertyId("serial");
									}
								}else {
									tfDescricaoMaterialComodato.setReadOnly(false);
									tfDescricaoMaterialComodato.setValue("");
									tfDescricaoMaterialComodato.setReadOnly(true);		
									
									cbSerialMaterialComodato.setEnabled(false);
									cbSerialMaterialComodato.setContainerDataSource(null);
								}
							}else{
								tfDescricaoMaterialComodato.setReadOnly(false);
								tfDescricaoMaterialComodato.setValue("");
								tfDescricaoMaterialComodato.setReadOnly(true);
								
								cbSerialMaterialComodato.setEnabled(false);
								cbSerialMaterialComodato.setContainerDataSource(null);
							}
						}
					});

					tfCodMaterialComodato.setRequired(true);		
					tfDescricaoMaterialComodato = new TextField();
					tfDescricaoMaterialComodato.setReadOnly(true);
					tfDescricaoMaterialComodato.setWidth("450px");			
										
					
								
					final Button btSearchMaterialComodato = new Button();
					btSearchMaterialComodato.setStyleName(BaseTheme.BUTTON_LINK);
					btSearchMaterialComodato.setIcon(new ThemeResource("icons/search-16.png"));
					btSearchMaterialComodato.addListener(new Button.ClickListener() {
						
						@Override
						public void buttonClick(ClickEvent event) {
							
							MaterialUtil cUtil = new MaterialUtil(true, true);
							cUtil.addListerner(new MaterialUtil.MaterialListerner() {
								
								@Override
								public void onSelected(MaterialEvent event) {
										
										if(event.getMaterial() != null){
											tfCodMaterialComodato.setValue(event.getMaterial().getId().toString());
											tfDescricaoMaterialComodato.setReadOnly(false);
											tfDescricaoMaterialComodato.setValue(event.getMaterial().getNome());
											tfDescricaoMaterialComodato.setReadOnly(true);
											material_selecionado_comodato = event.getMaterial();		
											
											if(cbSerialMaterialComodato != null){
												cbSerialMaterialComodato.setEnabled(true);
												cbSerialMaterialComodato.setContainerDataSource(buildContainerSeriais(event.getMaterial().getId()));
												cbSerialMaterialComodato.setItemCaptionPropertyId("serial");
											}
										}
									}							
							});
							
							getUI().addWindow(cUtil);
						}
					});			
					
					FormLayout frmCodigoCliente = new FormLayout(){
						{
							
							setMargin(true);
							setSpacing(true);
							setStyleName("form-cutom-new");		
													
							addComponent(tfCodMaterialComodato);							
						}
					};
					addComponent(frmCodigoCliente);
			
					FormLayout frmButtonSearchMaterialComodato =new FormLayout(){
						{
							
							setMargin(true);
							setSpacing(true);
							setStyleName("form-cutom-new_hide_error_cell");							
							addComponent(btSearchMaterialComodato);							
						}
					}; 
								
					FormLayout frmDescMaterialComodato = new FormLayout(){
						{
							
							setMargin(true);
							setSpacing(true);						
							setStyleName("form-cutom");		
							addStyleName("form-cutom-new_hide_require");
							
							addComponent(tfDescricaoMaterialComodato);							
						}
					}; 
					addComponent(frmButtonSearchMaterialComodato);
					addComponent(frmDescMaterialComodato);
					setExpandRatio(frmDescMaterialComodato, 1);	

				}	
			});
			
			vlRoot.addComponent(new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
										
					
						
					cbSerialMaterialComodato = new ComboBox("Serial ONU");
					cbSerialMaterialComodato.setEnabled(false);
					cbSerialMaterialComodato.setNullSelectionAllowed(false);
					cbSerialMaterialComodato.setStyleName("caption-align");
					cbSerialMaterialComodato.setRequired(true);
					cbSerialMaterialComodato.setImmediate(true);
					
					cbSerialMaterialComodato.addValueChangeListener(new Property.ValueChangeListener() {
						
						@Override
						public void valueChange(ValueChangeEvent event) {
							if(cbSerialMaterialComodato.isValid()){
								valid_serial_material_comodato = true;
							}else{
								valid_serial_material_comodato = false;
							}
						}
					});
					
					addComponent(cbSerialMaterialComodato);
									
					
				}
			});
			
			vlRoot.addComponent(new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
					
					txtOLT = new TextField("OLT");
					txtOLT.setStyleName("caption-align");
					txtOLT.setEnabled(false);
					
					addComponent(txtOLT);
									
				}
			});	
			
			vlRoot.addComponent(new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
									
					txtPON = new TextField("GPON");
					txtPON.setStyleName("caption-align-concentrador-gpon-liberar");
					txtPON.setEnabled(false);
					txtPON.setWidth("37px");
					
					if(cbCaixaAtendimento.getValue() != null && cbCaixaAtendimento.getItem(cbCaixaAtendimento.getValue()).getItemProperty("pon").getValue() != null){
						txtPON.setEnabled(true);
						txtPON.setValue(cbCaixaAtendimento.getItem(cbCaixaAtendimento.getValue()).getItemProperty("pon").getValue().toString());
						txtPON.setEnabled(false);
					}
									
					txtGpon = new TextField();
					txtGpon.setNullRepresentation("");			
					txtGpon.setMaxLength(2);
					txtGpon.setStyleName("input-concentrador-gpon-correcao-liberar");
					txtGpon.setWidth("75px");				
					txtGpon.setId("txtGpon");
					JavaScript.getCurrent().execute("$('#txtGpon').mask('99')");
					
					
					FormLayout fl1 = new FormLayout();
					fl1.setStyleName("form-cutom");
					fl1.setMargin(true);
					fl1.setSpacing(true);
					fl1.addComponent(txtPON);
					
					FormLayout fl2 = new FormLayout();
					fl2.setStyleName("form-cutom");
					fl2.setMargin(true);
					fl2.setSpacing(true);
					fl2.addComponent(txtGpon);
							
					HorizontalLayout hl = new HorizontalLayout();
					hl.setWidth("100%");
					hl.setSpacing(false);
					hl.setMargin(false); 
					hl.addComponent(fl1);
					hl.addComponent(fl2);
								
					addComponent(hl);	
									
					fieldGroup.bind(txtGpon, "gpon");
				}
			});
			
			vlRoot.addComponent(new FormLayout(){
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
					
					String fibra = cbConcentradores.getValue() != null && cbConcentradores.getItem(cbConcentradores.getValue()).getItemProperty("fibra").getValue() != null && 
							!cbConcentradores.getItem(cbConcentradores.getValue()).getItemProperty("fibra").getValue().toString().equals("") ? 
									cbConcentradores.getItem(cbConcentradores.getValue()).getItemProperty("fibra").getValue().toString() : "NAO";
									
									
					
					
					txtSinalDb = (TextField)fieldGroup.buildAndBind("Sinal Db", "sinal_db");	
					txtSinalDb.setId("txtSinalDb");
					txtSinalDb.setWidth("60px");				
					txtSinalDb.setStyleName("caption-align");
					txtSinalDb.setNullRepresentation("");					
					txtSinalDb.setRequired(true);
					txtSinalDb.setMaxLength(5);
					
					addComponent(txtSinalDb);
					JavaScript.getCurrent().execute("$('#txtSinalDb').maskMoney({decimal:'.'})");
					
					
					if(fibra.equals("SIM")){
						txtSinalDb.setEnabled(true);
						txtSinalDb.setRequired(true);
					}else{
						txtSinalDb.setValue("");
						txtSinalDb.setEnabled(false);
						txtSinalDb.setRequired(false);
						
					}
					
					
					Label lb = new Label("-");
					lb.setWidth("1%");
					lb.setStyleName("lbMinusCaixaAtendimento");
					
					addComponent(lb); 
				}
			});
			
		}
		
		vlRoot.addComponent(new HorizontalLayout(){
			{
											
				boolean preencher = false;
				if(item.getItemProperty("id").getValue() != null && item.getItemProperty("cliente").getValue() != null){
					preencher = true;
				}
				
				final TextField tfCodMaterial = new TextField("Equipamento");				
				tfCodMaterial.setWidth("60px");				
				tfCodMaterial.setNullRepresentation("");
				tfCodMaterial.setStyleName("caption-align");
				tfCodMaterial.setImmediate(true);
								
				tfCodMaterial.addListener(new TextChangeListener() {
					
					@Override
					public void textChange(TextChangeEvent event) {
						ProdutoDAO cDAO = new ProdutoDAO();
						material_selecionado = new Produto();
						
						if(event.getText()!=null && !event.getText().isEmpty() && !event.getText().equals("")){
							
														
							material_selecionado = cDAO.find(Integer.parseInt(event.getText()));		
							
							if(material_selecionado != null){
								tfDescricaoMaterial.setReadOnly(false);
								tfDescricaoMaterial.setValue(material_selecionado.getNome());
								tfDescricaoMaterial.setReadOnly(true);
								
								if(cbSerial != null){
									cbSerial.setEnabled(true);
									cbSerial.setContainerDataSource(buildContainerSeriais(material_selecionado.getId()));		
									cbSerial.setItemCaptionPropertyId("serial");cbSerial.setItemCaptionPropertyId("serial");
								}
							}else {
								tfDescricaoMaterial.setReadOnly(false);
								tfDescricaoMaterial.setValue("");
								tfDescricaoMaterial.setReadOnly(true);		
								
								if(cbSerial != null){
									cbSerial.setEnabled(false);
									cbSerial.setContainerDataSource(null);
								}
							}
						}else{
							tfDescricaoMaterial.setReadOnly(false);
							tfDescricaoMaterial.setValue("");
							tfDescricaoMaterial.setReadOnly(true);
							
							if(cbSerial != null){
								cbSerial.setEnabled(false);
								cbSerial.setContainerDataSource(null);
							}
						}
					}
				});

				tfCodMaterial.setRequired(true);		
				tfDescricaoMaterial = new TextField();
				tfDescricaoMaterial.setReadOnly(true);
				tfDescricaoMaterial.setWidth("450px");			
							
				final Button btSearchCliente = new Button();
				btSearchCliente.setStyleName(BaseTheme.BUTTON_LINK);
				btSearchCliente.setIcon(new ThemeResource("icons/search-16.png"));
				btSearchCliente.addListener(new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						
						MaterialUtil cUtil = new MaterialUtil(true, true);
						cUtil.addListerner(new MaterialUtil.MaterialListerner() {
							
							@Override
							public void onSelected(MaterialEvent event) {
									
									if(event.getMaterial() != null){
										tfCodMaterial.setValue(event.getMaterial().getId().toString());
										tfDescricaoMaterial.setReadOnly(false);
										tfDescricaoMaterial.setValue(event.getMaterial().getNome());
										tfDescricaoMaterial.setReadOnly(true);
										material_selecionado = event.getMaterial();		
										
										if(cbSerial != null){
											cbSerial.setEnabled(true);
											cbSerial.setContainerDataSource(buildContainerSeriais(event.getMaterial().getId()));
											cbSerial.setItemCaptionPropertyId("serial");
										}
									}
								}							
						});
						
						getUI().addWindow(cUtil);
					}
				});			
				
				FormLayout frmCodigoCliente = new FormLayout(){
					{
						
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom-new");		
												
						addComponent(tfCodMaterial);	
						if(material_selecionado != null){
							tfCodMaterial.setValue(material_selecionado.getId().toString());
						}
					}
				};
				addComponent(frmCodigoCliente);
		
				FormLayout frmButtonSearchCliente =new FormLayout(){
					{
						
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom-new_hide_error_cell");							
						addComponent(btSearchCliente);							
					}
				}; 
							
				FormLayout frmDescCliente = new FormLayout(){
					{
						
						setMargin(true);
						setSpacing(true);						
						setStyleName("form-cutom");		
						addStyleName("form-cutom-new_hide_require");
						
						addComponent(tfDescricaoMaterial);							
					}
				}; 
				addComponent(frmButtonSearchCliente);
				addComponent(frmDescCliente);
				setExpandRatio(frmDescCliente, 1);	

			}	
		});
		
					
			
		vlRoot.addComponent(new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
										
					
					if(item.getItemProperty("regime").getValue().equals("COMODATO") || item.getItemProperty("regime").getValue().equals("COMODATO (TOTAL)")){					
						cbSerial = new ComboBox("Endereço MAC/Serial");
						cbSerial.setEnabled(false);
						cbSerial.setNullSelectionAllowed(false);
						cbSerial.setStyleName("caption-align");
						cbSerial.setRequired(true);
						cbSerial.setImmediate(true);
						
						cbSerial.addValueChangeListener(new Property.ValueChangeListener() {
							
							@Override
							public void valueChange(ValueChangeEvent event) {
								if(cbSerial.isValid()){
									valid_mac = true;
								}else{
									valid_mac = false;
								}
							}
						});
						
						addComponent(cbSerial);
						
						if(item.getItemProperty("regime").getValue().equals("COMODATO")){
							txtMacOpcional = new TextField("Endereço MAC/Radius");	
							txtMacOpcional.setStyleName("caption-align");						
							txtMacOpcional.setId("txtMacOpcional");		

							JavaScript.getCurrent().execute("$('#txtMacOpcional').mask('AA:AA:AA:AA:AA:AA')");
							
							addComponent(txtMacOpcional);
							
							//Se tiver preenchido, cadastra esse mAc no Radius, se não estiver preenchido. controla pelo mac acima
						}
					}else{
						txtSerial = new TextField("Endereço MAC");
						txtSerial.setStyleName("caption-align");
						txtSerial.setRequired(true);
						txtSerial.setId("txtSerial");						
						//01:01:01:01:01:01
						JavaScript.getCurrent().execute("$('#txtSerial').mask('AA:AA:AA:AA:AA:AA')");
						
						txtSerial.setImmediate(true);
						txtSerial.addTextChangeListener(new FieldEvents.TextChangeListener() {
							
							@Override
							public void textChange(TextChangeEvent event) {
								
								if(event.getText() != null && !event.getText().equals("") && !event.getText().isEmpty() && Validator.MacAddress(event.getText())){
									CredenciaisAcessoDAO caDAO = new CredenciaisAcessoDAO();
									boolean permite = caDAO.checkSerialProprio(event.getText());
									
									if(!permite){
										txtSerial.addStyleName("invalid-cpf");
										valid_mac = false;										
										Notify.Show("Serial Inválido, Necessário Comprar Material", Notify.TYPE_ERROR);
									}else{
										txtSerial.removeStyleName("invalid-cpf");
										valid_mac = true;
									}
								}else{
									txtSerial.addStyleName("invalid-cpf");
									valid_mac = false;									
								}
								
							}
						});
						
						txtSerial.addBlurListener(new FieldEvents.BlurListener() {
							
							@Override
							public void blur(BlurEvent event) {
								if(txtSerial.getValue() !=null && !txtSerial.getValue().isEmpty() && !txtSerial.getValue().equals("") && 
										!Validator.MacAddress(txtSerial.getValue())){
									
									Notify.Show("Serial Inválido, Formato de MAC Inválido", Notify.TYPE_ERROR);
								}
							}
						});
						
						
						
						addComponent(txtSerial);
						if(item.getItemProperty("endereco_mac").getValue() != null){
							txtSerial.setValue(item.getItemProperty("endereco_mac").getValue().toString());
							if(Validator.MacAddress(txtSerial.getValue())){
								valid_mac = true;
							}else{
								valid_mac = false;
							}
						}
					}					
					
				}
			});

	}
	
	
	private JPAContainer<SerialProduto> buildContainerSeriais(Integer codProduto){
		containerSeriais = JPAContainerFactory.make(SerialProduto.class, ConnUtil.getEntity());
		containerSeriais.addContainerFilter(Filters.eq("produto", new Produto(codProduto)));
		containerSeriais.addContainerFilter(Filters.eq("status", "ATIVO"));
		
		return containerSeriais;
	}

	@Override
	public Button buildBtSalvar() {
		
		btSalvar = new Button("OK", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				concluirLiberacao();
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

	private void concluirLiberacao() {
		
		if(item.getItemProperty("regime").getValue().equals("COMODATO")){
			
			if(fieldGroup.getField("login").getValue() == null || fieldGroup.getField("login").getValue().equals("")){
				((TextField)fieldGroup.getField("login")).setValue(codAcesso.toString());
				 valid_usuario = true;
			}
			
			if(fieldGroup.getField("senha").getValue() == null || fieldGroup.getField("senha").getValue().equals("")){					
				((TextField)fieldGroup.getField("senha")).setValue(codAcesso.toString());
				valid_senha = true;
			}

			if(fieldGroup.getField("signal_strength").getValue() == null || fieldGroup.getField("signal_strength").getValue().equals("")){
				Validar_signal = true;
			}
			
			if(fieldGroup.getField("endereco_ip").getValue() == null || fieldGroup.getField("endereco_ip").getValue().toString().equals("")){
				valid_ip = true;
			}
			
			if(fieldGroup.isValid() && cbSerial.isValid() && valid_usuario && cbInterface.isValid() && valid_senha && valid_mac && Validar_signal && valid_ip){
				try {	
					item.getItemProperty("status_2").setValue("ATIVO");
					fieldGroup.commit();
					item.getItemProperty("material").setValue(material_selecionado);
					
					if(txtMacOpcional.getValue() != null && !txtMacOpcional.getValue().equals("")){
						item.getItemProperty("endereco_mac").setValue(txtMacOpcional.getValue().toString());
						item.getItemProperty("onu_serial").setValue(cbSerial.getItem(cbSerial.getValue()).getItemProperty("serial").getValue().toString());
					}else{						
						item.getItemProperty("endereco_mac").setValue(cbSerial.getItem(cbSerial.getValue()).getItemProperty("serial").getValue().toString());
					}
					
					item.getItemProperty("interfaces").setValue(cbInterface.getItem(cbInterface.getValue()).getItemProperty("name").getValue());
					
					if(fieldGroup.getField("signal_strength").getValue() == null || fieldGroup.getField("signal_strength").getValue().equals("")){
						item.getItemProperty("signal_strength").setValue("-120..120");	
					}					
					
					Integer codSerial = Integer.parseInt(cbSerial.getItem(cbSerial.getValue()).getItemProperty("id").getValue().toString());
											
					fireEvent(new LiberarCredenciaisAcessoEvent(getUI(), item,codSerial,null, true));								
					
				} catch (Exception e) {											
					e.printStackTrace();
					Notify.Show("ERRO: "+e.getLocalizedMessage(), Notify.TYPE_ERROR);	
				}
			}else{
				Notify.Show_Invalid_Submit_Form();
			}
		
		}else if(item.getItemProperty("regime").getValue().equals("PROPRIO (PARCIAL)")){
			if(fieldGroup.getField("login").getValue() == null || fieldGroup.getField("login").getValue().equals("")){
				((TextField)fieldGroup.getField("login")).setValue(codAcesso.toString());
				 valid_usuario = true;
			}
			
			if(fieldGroup.getField("senha").getValue() == null || fieldGroup.getField("senha").getValue().equals("")){					
				((TextField)fieldGroup.getField("senha")).setValue(codAcesso.toString());
				valid_senha = true;
			}

			if(fieldGroup.getField("signal_strength").getValue() == null || fieldGroup.getField("signal_strength").getValue().equals("")){
				Validar_signal = true;
			}
			
			if(fieldGroup.getField("endereco_ip").getValue() == null || fieldGroup.getField("endereco_ip").getValue().toString().equals("")){
				valid_ip = true;
			}
			
			if(fieldGroup.isValid() && cbSerialMaterialComodato.isValid() && valid_usuario && cbInterface.isValid() && valid_senha && 
					valid_mac && valid_serial_material_comodato && Validar_signal && valid_ip){
				try {	
					item.getItemProperty("status_2").setValue("ATIVO");
					fieldGroup.commit();
					item.getItemProperty("onu").setValue(material_selecionado_comodato);
					item.getItemProperty("material").setValue(material_selecionado);
					item.getItemProperty("onu_serial").setValue(cbSerialMaterialComodato.getItem(cbSerialMaterialComodato.getValue()).getItemProperty("serial").getValue().toString());
					item.getItemProperty("endereco_mac").setValue(txtSerial.getValue());
					item.getItemProperty("interfaces").setValue(cbInterface.getItem(cbInterface.getValue()).getItemProperty("name").getValue());
					
					if(fieldGroup.getField("signal_strength").getValue() == null || fieldGroup.getField("signal_strength").getValue().equals("")){
						item.getItemProperty("signal_strength").setValue("-120..120");	
					}					
					
					Integer codSerial = Integer.parseInt(cbSerialMaterialComodato.getItem(cbSerialMaterialComodato.getValue()).getItemProperty("id").getValue().toString());
											
					fireEvent(new LiberarCredenciaisAcessoEvent(getUI(), item,codSerial,null, true));								
					
				} catch (Exception e) {										
					e.printStackTrace();
					Notify.Show("ERRO: "+e.getLocalizedMessage(), Notify.TYPE_ERROR);	
				}
			}else{
				Notify.Show_Invalid_Submit_Form();
			}
		}else if(item.getItemProperty("regime").getValue().equals("COMODATO (TOTAL)")){
			if(fieldGroup.getField("login").getValue() == null || fieldGroup.getField("login").getValue().equals("")){
				((TextField)fieldGroup.getField("login")).setValue(codAcesso.toString());
				 valid_usuario = true;
			}
			
			if(fieldGroup.getField("senha").getValue() == null || fieldGroup.getField("senha").getValue().equals("")){					
				((TextField)fieldGroup.getField("senha")).setValue(codAcesso.toString());
				valid_senha = true;
			}

			if(fieldGroup.getField("signal_strength").getValue() == null || fieldGroup.getField("signal_strength").getValue().equals("")){
				Validar_signal = true;
			}
			
			if(fieldGroup.getField("endereco_ip").getValue() == null || fieldGroup.getField("endereco_ip").getValue().toString().equals("")){
				valid_ip = true;
			}
			
			if(fieldGroup.isValid() && cbSerialMaterialComodato.isValid() && valid_usuario && cbInterface.isValid() && valid_senha && 
					valid_mac && valid_serial_material_comodato && Validar_signal && valid_ip){
				try {	
					item.getItemProperty("status_2").setValue("ATIVO");
					fieldGroup.commit();
					item.getItemProperty("onu").setValue(material_selecionado_comodato);
					item.getItemProperty("material").setValue(material_selecionado);
					item.getItemProperty("onu_serial").setValue(cbSerialMaterialComodato.getItem(cbSerialMaterialComodato.getValue()).getItemProperty("serial").getValue().toString());
					item.getItemProperty("endereco_mac").setValue(cbSerial.getItem(cbSerial.getValue()).getItemProperty("serial").getValue().toString());
					item.getItemProperty("interfaces").setValue(cbInterface.getItem(cbInterface.getValue()).getItemProperty("name").getValue());
					
					if(fieldGroup.getField("signal_strength").getValue() == null || fieldGroup.getField("signal_strength").getValue().equals("")){
						item.getItemProperty("signal_strength").setValue("-120..120");	
					}					
					
					Integer codSerial = Integer.parseInt(cbSerialMaterialComodato.getItem(cbSerialMaterialComodato.getValue()).getItemProperty("id").getValue().toString());
					Integer codMac = Integer.parseInt(cbSerial.getItem(cbSerial.getValue()).getItemProperty("id").getValue().toString());
											
					fireEvent(new LiberarCredenciaisAcessoEvent(getUI(), item,codSerial,codMac, true));								
					
				} catch (Exception e) {										
					e.printStackTrace();
					Notify.Show("ERRO: "+e.getLocalizedMessage(), Notify.TYPE_ERROR);	
				}
			}else{
				Notify.Show_Invalid_Submit_Form();
			}
		}else{
			
			if(fieldGroup.getField("login").getValue() == null || fieldGroup.getField("login").getValue().equals("")){
				((TextField)fieldGroup.getField("login")).setValue(codAcesso.toString());
				 valid_usuario = true;
			}
			
			if(fieldGroup.getField("senha").getValue() == null || fieldGroup.getField("senha").getValue().equals("")){					
				((TextField)fieldGroup.getField("senha")).setValue(codAcesso.toString());
				valid_senha = true;
			}	
			
			if(fieldGroup.getField("endereco_ip").getValue() == null || fieldGroup.getField("endereco_ip").getValue().toString().equals("")){
				valid_ip = true;
			}
												
			if(fieldGroup.isValid() && valid_usuario && cbInterface.isValid() && valid_senha && txtSerial.isValid() && valid_mac && valid_ip){
				try {	
					item.getItemProperty("status_2").setValue("ATIVO");
					fieldGroup.commit();
					item.getItemProperty("material").setValue(material_selecionado);
					item.getItemProperty("endereco_mac").setValue(txtSerial.getValue());
					item.getItemProperty("interfaces").setValue(cbInterface.getItem(cbInterface.getValue()).getItemProperty("name").getValue());
					
					if(fieldGroup.getField("signal_strength").getValue() == null || fieldGroup.getField("signal_strength").getValue().equals("")){
						item.getItemProperty("signal_strength").setValue("-120..120");
					}
												
					fireEvent(new LiberarCredenciaisAcessoEvent(getUI(), item,null,null, true));								
					
				} catch (Exception e) {											
					e.printStackTrace();
					Notify.Show("ERRO: "+e.getLocalizedMessage(), Notify.TYPE_ERROR);		
				}
			}else{
				Notify.Show_Invalid_Submit_Form();
			}
		}
		
	}
	@Override
	public Button buildBtCancelar() {
		btCancelar = new Button("Cancelar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(!fieldGroup.isModified()){
					fieldGroup.discard();				
					fireEvent(new LiberarCredenciaisAcessoEvent(getUI(), item,null,null, false));					
				}else{
					GenericDialog gDialog = new GenericDialog("Confirme para Continuar!", "Deseja Salvar as Informações Alteradas?", true, true);
					gDialog.setCaptionCANCEL("Sair sem Salvar!");
					gDialog.setCaptionOK("Salvar");
					
					gDialog.addListerner(new GenericDialog.DialogListerner() {
						
						@Override
						public void onClose(DialogEvent event) {
							if(event.isConfirm()){
								concluirLiberacao();
							}else{							
								fieldGroup.discard();				
								fireEvent(new TransportadoraEvent(getUI(), item, false));
								
							}
						}
					});					
					
					getUI().addWindow(gDialog);
					
				}				
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
	
	
	public void addListerner(LiberarCredenciaisAcessoListerner target){
		try {
			Method method = LiberarCredenciaisAcessoListerner.class.getDeclaredMethod("onClose", LiberarCredenciaisAcessoEvent.class);
			addListener(LiberarCredenciaisAcessoEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(LiberarCredenciaisAcessoListerner target){
		removeListener(LiberarCredenciaisAcessoEvent.class, target);
	}
	public static class LiberarCredenciaisAcessoEvent extends Event{
		
		private Item item;
		private boolean confirm;
		private Date dataPrimeiroBoleto;
		private Integer codSerial;
		private Integer codMac;
		
		public LiberarCredenciaisAcessoEvent(Component source, Item item,Integer codSerial,Integer codMac, boolean confirm) {
			super(source);
			this.item = item;
			this.confirm = confirm;
			this.codSerial = codSerial;
			this.codMac = codMac;
		}
		
		public Integer getCodSerial(){
			return codSerial;
		}
		public Integer getCodMac(){
			return codMac;
		}
		
		public Item getItem() {
			return item;
		}	

		public boolean isConfirm() {
			return confirm;
		}	
				
	}
	public interface LiberarCredenciaisAcessoListerner extends Serializable{
		public void onClose(LiberarCredenciaisAcessoEvent event);
	}
}

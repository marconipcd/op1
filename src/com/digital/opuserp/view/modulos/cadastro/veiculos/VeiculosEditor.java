package com.digital.opuserp.view.modulos.cadastro.veiculos;

import java.io.Serializable;
import java.lang.reflect.Method;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.Fornecedor;
import com.digital.opuserp.interfaces.GenericEditor;
import com.digital.opuserp.util.CnpjUtil;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.util.ValidarCPF;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

public class VeiculosEditor extends Window implements GenericEditor{
	
		Item item;
		Button btSalvar;
		Button btCancelar;
		FormLayout flPrincipal;
		FieldGroup fieldGroup;
		VerticalLayout vlRoot;
		
		ComboBox boxMarca;
		TextField tfModelo;
		
		VerticalLayout hlTeste;
		
		Integer codFornecedorDefault = 0;
		boolean cnpjCpfValid;
		
		String tipoVeiculo = "";
		
		ShortcutListener sl_esc;
		
		private String width_cod_veiculo;
		private String width_status;
		private String width_tipo;
		private String width_marca;
		private String width_modelo;
		private String width_cor;
		private String width_opcionais;
		private String width_chassi;
		private String width_placa;
		private String width_combustivel;
		private String width_ano_fab;
		private String width_ano_modelo;
		private String width_cpf_cnpj_proprietario;
		private String width_ultimaRev;
		private String width_nome_proprietario;
		private String width_razao_social;
		private String width_seguradora;
		private String width_venc_seguro;
		private String width_data_garantia;
		private String width_venc_ipva_impostos;
		private String width_km_atual;
		private String width_data_ultima_revisao;
		private String width_revisao_geral;
		
		
		
		public VeiculosEditor(Item item, String title, boolean modal) {
			this.item = item;
			
			configLayout();	
			
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
					hlButtons.setSpacing(true);
					hlButtons.setMargin(true);
					hlButtons.setStyleName("hl_buttons_bottom");
					hlButtons.addComponent(buildBtCancelar());
					hlButtons.addComponent(buildBtSalvar());
					
					addComponent(hlButtons);
					setComponentAlignment(hlButtons, Alignment.BOTTOM_RIGHT);					
					
				}
			});
			
			sl_esc = buildShortCurListerner();
			
			buildLayout();
		
		}
		
		private ShortcutListener buildShortCurListerner(){
			 sl_esc = new ShortcutListener("Cancelar",
		                ShortcutAction.KeyCode.ENTER, null) {
		            @Override
		            public void handleAction(Object sender, Object target) {
		                btCancelar.click();
		            }
		        };
		        
		     return sl_esc;
		}
		
		private void configLayout(){
			//1366x768
			//if(OpusERP4UI.browser.getScreenWidth() >= 1024 && OpusERP4UI.browser.getScreenHeight() >= 768){
					setWidth("800px");
					setHeight("690px");
					
					
					
					width_cod_veiculo = "90px";
					width_status = "130px";
					
					width_tipo = "329px";
					
					width_marca = "166px";
					width_modelo = "425px";
					
					width_cor = "166px";
					width_opcionais = "512px";
					
					width_chassi = "325px";
					width_placa = "120px";
					
					width_combustivel = "200px";
					width_ano_fab = "90px";
					width_ano_modelo = "90px";
					
					width_cpf_cnpj_proprietario = "150px";
					width_nome_proprietario = "555px";
					
					width_seguradora = "555px";
					
					width_venc_seguro = "100px";
					width_data_garantia = "100px";
					width_venc_ipva_impostos = "100px";
					
					width_km_atual = "150px";					
					width_revisao_geral = "100px";
					width_data_ultima_revisao = "100px";
					
					
			
				
				
			//}
		}	
		
		public void buildLayout(){	
			
			boxMarca = new ComboBox("Marca");
			tfModelo = new TextField("Modelo");
			
			fieldGroup = new FieldGroup(item);	
			fieldGroup.bind(boxMarca, "marca");
			boxMarca.setEnabled(false);
			fieldGroup.bind(tfModelo, "modelo");
			
	
		vlRoot.addComponent(new HorizontalLayout(){
			{
				
				final TextField tfCod = new TextField();						
				fieldGroup.bind(tfCod, "cod_veiculo");
				tfCod.setCaption("Código do Veiculo");
				tfCod.setRequired(true);
				tfCod.setNullRepresentation("");
				tfCod.setImmediate(true);
				tfCod.focus();
				tfCod.setWidth(width_cod_veiculo);
				tfCod.setMaxLength(10);
				tfCod.setStyleName("caption-align-veiculos");
				
				addComponent(new FormLayout(){
					{
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom");
						addComponent(tfCod);
					}
				});
				
				
				addComponent(new FormLayout() {
					{
						setStyleName("form-cutom");
						setMargin(true);
						setSpacing(true);

						ComboBox tfCod = new ComboBox("Status");
						tfCod.setNullSelectionAllowed(false);
						tfCod.addItem("ATIVO");
						tfCod.addItem("INATIVO");
						tfCod.setWidth(width_status);
						tfCod.setRequired(true);

						addComponent(tfCod);

						fieldGroup.bind(tfCod, "status");
					}
				});
			}
		});	
				
				final ComboBox boxTipo = new ComboBox();
				fieldGroup.bind(boxTipo, "tipo");
				boxTipo.setCaption("Tipo");
				boxTipo.setWidth(width_tipo);
				boxTipo.setNullSelectionAllowed(false);
				boxTipo.addItem("Automovel");
				boxTipo.addItem("Motocicleta");
				boxTipo.setStyleName("caption-align-veiculos");
				boxTipo.setRequired(true);
				boxTipo.setImmediate(true);
				boxTipo.addValueChangeListener(new Property.ValueChangeListener() {
					
					@Override
					public void valueChange(ValueChangeEvent event) {
						
						tipoVeiculo = ((ComboBox)event.getProperty()).getValue().toString();
						boxMarca.removeAllItems();
						vlRoot.replaceComponent(hlTeste, buildHorizontalLayout());
			
					}
				});
				
				try{
					if(item.getItemProperty("tipo").getValue() != null){
						tipoVeiculo = item.getItemProperty("tipo").getValue().toString();
					}
				}catch(Exception e){
					System.out.println("Linha 275 VeiculosEditor ta Dando ERRO!");
				}
							
				vlRoot.addComponent(new FormLayout(){
					{
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom");
						addComponent(boxTipo);
					}
					
				});
				
				
				vlRoot.addComponent(buildHorizontalLayout());
				
				
			
				final TextField tfCor = new TextField();
				fieldGroup.bind(tfCor, "cor");
				tfCor.setCaption("Cor");
				tfCor.setRequired(true);
				tfCor.setNullRepresentation("");
				tfCor.setStyleName("caption-align-veiculos");
				tfCor.setWidth(width_cor);
				tfCor.setMaxLength(50);
				tfCor.setImmediate(true);
				
				vlRoot.addComponent(new FormLayout(){
					{
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom");
						addComponent(tfCor);
					}
				});
				
				final TextField tfOp = new TextField();
				fieldGroup.bind(tfOp, "opcionais");
				tfOp.setCaption("Opcionais");
				tfOp.setRequired(true);
				tfOp.setStyleName("caption-align-veiculos");
				tfOp.setNullRepresentation("");
				tfOp.setWidth(width_opcionais);
				tfOp.setMaxLength(200);
				tfOp.setImmediate(true);
				
				vlRoot.addComponent(new FormLayout(){
					{
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom");
						addComponent(tfOp);
					}
				});
				
				
				
				vlRoot.addComponent(new HorizontalLayout(){
					{
						
						final TextField tfChassi = new TextField();
						fieldGroup.bind(tfChassi, "chassi");
						tfChassi.setCaption("Chassi");
						tfChassi.setWidth(width_chassi);
						tfChassi.setRequired(true);
						tfChassi.setStyleName("caption-align-veiculos");
						tfChassi.setNullRepresentation("");
						tfChassi.setMaxLength(50);
						tfChassi.setImmediate(true);
						
						addComponent(new FormLayout(){
							{
								setMargin(true);
								setSpacing(true);
								setStyleName("form-cutom");
								addComponent(tfChassi);
							}
						});
						
						final TextField tfPlaca = new TextField();
						fieldGroup.bind(tfPlaca, "placa");
						tfPlaca.setCaption("Placa");
						tfPlaca.setWidth(width_placa);
						tfPlaca.setRequired(true);
						tfPlaca.setNullRepresentation("");
						tfPlaca.setMaxLength(10);
						tfPlaca.setImmediate(true);
						
						addComponent(new FormLayout(){
							{
								setMargin(true);
								setSpacing(true);
								setStyleName("form-cutom");
								addComponent(tfPlaca);
							}
						});			
					}
				});		
				
				
				final ComboBox boxComb = new ComboBox("Combustível");
				fieldGroup.bind(boxComb, "combustivel");
				boxComb.setCaption("Combustível");
				boxComb.setRequired(true);
				boxComb.setNullSelectionAllowed(false);
				boxComb.setWidth(width_combustivel);
				boxComb.addItem("Alcool");	
				boxComb.addItem("Biodiesel");	
				boxComb.addItem("Diesel");	
				boxComb.addItem("Gasolina");			
				boxComb.addItem("Gasolina/Alcool");			
				boxComb.addItem("Gás");							 
				boxComb.setStyleName("caption-align-veiculos");
				boxComb.setImmediate(true);
				
				vlRoot.addComponent(new FormLayout(){
					{
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom");
						addComponent(boxComb);
					}
				});	
				
				
				vlRoot.addComponent(new HorizontalLayout(){
					{
					
						final TextField tfAnoFab = new TextField();
						fieldGroup.bind(tfAnoFab, "ano_fab");
						tfAnoFab.setCaption("Ano de Fabricação");
						tfAnoFab.setRequired(true);
						tfAnoFab.setWidth(width_ano_fab);
						tfAnoFab.setStyleName("caption-align-veiculos");
						tfAnoFab.setNullRepresentation("");
						tfAnoFab.setMaxLength(4);
						tfAnoFab.setImmediate(true);
						
						addComponent(new FormLayout(){
							{
								setMargin(true);
								setSpacing(true);
					//			setStyleName("form-cutom");
								addComponent(tfAnoFab);
							}
						});
					
						final TextField tfAnoMod = new TextField();
						fieldGroup.bind(tfAnoMod, "ano_modelo");
						tfAnoMod.setCaption("Ano do Modelo");
						tfAnoMod.setRequired(true);
						tfAnoMod.setWidth(width_ano_modelo);
						tfAnoMod.setNullRepresentation("");
						tfAnoMod.setMaxLength(4);
						tfAnoMod.setImmediate(true);
						
						addComponent(new FormLayout(){
							{
								setMargin(true);
								setSpacing(true);
							//	setStyleName("form-cutom");
								addComponent(tfAnoMod);
							}
						});				
					}
				});
				
			//	final Label lbInfoValid = new Label();
			vlRoot.addComponent(new HorizontalLayout(){
				{
					
					final TextField tfCpfPropri = new TextField("CPF/CNPJ Proprietário");
					fieldGroup.bind(tfCpfPropri, "cpf_cnpj_proprietario");
					tfCpfPropri.setRequired(true);
					tfCpfPropri.setWidth(width_cpf_cnpj_proprietario);
					tfCpfPropri.setStyleName("caption-align-veiculos");
					tfCpfPropri.setNullRepresentation("");
					tfCpfPropri.setMaxLength(14);
					tfCpfPropri.setImmediate(true);
					tfCpfPropri.setTextChangeEventMode(TextChangeEventMode.LAZY);				
					
					tfCpfPropri.addListener(new FieldEvents.TextChangeListener() {
						
						@Override
						public void textChange(TextChangeEvent event) {
							
							if(!event.getText().isEmpty()){
								
								if(event.getText().length() == 14){
									if(!CnpjUtil.validar(event.getText())){
										//CNPJ Inválido
										cnpjCpfValid = false;
										event.getComponent().setStyleName("textfield-invalid-cpf");
//									lbInfoValid.setValue("CNPJ Inválido");
//									lbInfoValid.setStyleName("veiculo-cnpj_invalido");
									}else{
										//CNPJ Válido
										cnpjCpfValid = true;
										event.getComponent().setStyleName("textfield-valid-cpf");
//									lbInfoValid.setValue("CNPJ Válido");
//									lbInfoValid.setStyleName("veiculo-cnpj_valido");
									}	
								}else if(event.getText().length() == 11){
									
									if(!ValidarCPF.cpfValido(event.getText())){
										//CPF Inválido
										cnpjCpfValid = false;
										event.getComponent().setStyleName("textfield-invalid-cpf");
//									lbInfoValid.setValue("CPF Inválido");
//									lbInfoValid.setStyleName("veiculo-cnpj_invalido");		
									}else {
										//CPF Válido
										cnpjCpfValid = true;
										event.getComponent().setStyleName("textfield-valid-cpf");
//									lbInfoValid.setValue("CPF Válido");
//									lbInfoValid.setStyleName("veiculo-cnpj_valido");		
									}							
								}else{
									//CPF/CNPJ Inválido
									cnpjCpfValid = false;	
									event.getComponent().setStyleName("textfield-invalid-cpf");
//								lbInfoValid.setValue("CPF/CNPJ Inválido");
//								lbInfoValid.setStyleName("veiculo-cnpj_invalido");
								}
							}else{
								event.getComponent().setStyleName("textfield-invalid-cpf");
							}
						}
					});
					
					tfCpfPropri.addListener(new FieldEvents.BlurListener() {
						
						@Override
						public void blur(BlurEvent event) {
							if(cnpjCpfValid == false){
								Notification.show("CPF/CNPJ Inválido!");
							}						
						}
					});
					
					
					tfCpfPropri.addListener(new FieldEvents.BlurListener() {
						
						@Override
						public void blur(BlurEvent event) {
							((TextField) fieldGroup.getField("nome_proprietario")).focus();
						}
					});
					
					addComponent(new FormLayout(){
						{
							setStyleName("form-cutom-new");
							setMargin(true);
							addComponent(tfCpfPropri);
						}										
					});	
					
					if(item.getItemProperty("id").getValue() != null){
						cnpjCpfValid = true;
						tfCpfPropri.setStyleName("textfield-valid-cpf");
					}
					
					//	vlRoot.addComponent(lbInfoValid);
					
					
					final Button btCpf = new Button();
					btCpf.setStyleName(BaseTheme.BUTTON_LINK);
					btCpf.setIcon(new ThemeResource("icons/browser-16.png"));
					btCpf.setDescription("Pesquisar CPF na Receita Federal");
					
					BrowserWindowOpener openCpf = new BrowserWindowOpener("http://www.receita.fazenda.gov.br/aplicacoes/atcta/cpf/consultapublica.asp");
					openCpf.setFeatures("height=600,width=800");
					openCpf.extend(btCpf);
					
					addComponent(new FormLayout(){
						{
							setStyleName("form-cutom-new");	
							addComponent(btCpf);
						}
					});
					
					
					final Button btCnpj = new Button();
					btCnpj.setStyleName(BaseTheme.BUTTON_LINK);
					btCnpj.setIcon(new ThemeResource("icons/browser-16.png"));
					btCnpj.setDescription("Pesquisar CNPJ na Receita Federal");
					
					BrowserWindowOpener openCnpj = new BrowserWindowOpener("http://www.receita.fazenda.gov.br/pessoajuridica/cnpj/cnpjreva/cnpjreva_solicitacao.asp");
					openCnpj.setFeatures("height=600,width=800");
					openCnpj.extend(btCnpj);
					
					addComponent(new FormLayout(){
						{
							setStyleName("form-cutom-new_hide_error_cell");	
							addComponent(btCnpj);
						}
					});
					
					
					
				}
			});
				
			
//				final TextField tfPropri = new TextField("Proprietário");
//				fieldGroup.bind(tfPropri, "nome_proprietario");
//				tfPropri.setRequired(true);
//				tfPropri.setNullRepresentation("");
//				tfPropri.setStyleName("caption-align-veiculos");
//				tfPropri.setImmediate(true);
//				tfPropri.setMaxLength(100);
//				tfPropri.setWidth(width_nome_proprietario);
				vlRoot.addComponent(new FormLayout(){
					{
						setMargin(true);
						setStyleName("form-cutom");
//						setSpacing(true);
						addComponent(((TextField) fieldGroup.buildAndBind("Proprietário", "nome_proprietario")));
						((TextField) fieldGroup.getField("nome_proprietario")).setRequired(true);
						((TextField) fieldGroup.getField("nome_proprietario")).setNullRepresentation("");
						((TextField) fieldGroup.getField("nome_proprietario")).setStyleName("caption-align-veiculos");
						((TextField) fieldGroup.getField("nome_proprietario")).setImmediate(true);
						((TextField) fieldGroup.getField("nome_proprietario")).setMaxLength(100);
						((TextField) fieldGroup.getField("nome_proprietario")).setWidth(width_nome_proprietario);
					}
				});
				
				
				FormLayout fmrSeguradora = new FormLayout(){
				{

					final ComboBox boxSeg = new ComboBox("Seguradora", buildFornecedorDefault());
					boxSeg.setItemCaptionPropertyId("razao_social");
					boxSeg.setRequired(false);
					boxSeg.setNullSelectionAllowed(true);
					boxSeg.setImmediate(true);			
					boxSeg.setStyleName("caption-align-veiculos");
					boxSeg.setWidth(width_seguradora);
					boxSeg.addListener(new Property.ValueChangeListener() {
						
					@Override
					public void valueChange(ValueChangeEvent event) {
						try{
							codFornecedorDefault = (Integer)boxSeg.getItem(boxSeg.getValue()).getItemProperty("id").getValue();
						}catch(Exception e){
							System.out.println("Fornecedor não Encontrado!");
						}
						
				}
			});
					setMargin(true);
					setSpacing(true);
					setStyleName("form-cutom");
					fieldGroup.bind(boxSeg, "seguradora");				
					addComponent(boxSeg);
					setExpandRatio(boxSeg, 2);
				}
			};				
			vlRoot.addComponent(fmrSeguradora);
				
				
			final DateField vencSegu = new DateField("Vencimento do Seguro");				
			fieldGroup.bind(vencSegu, "venc_seguro");
			vencSegu.setRequired(false);
			vencSegu.setImmediate(true);
			vencSegu.setStyleName("caption-align-veiculos");		
			vencSegu.setWidth(width_venc_seguro);
			vencSegu.setDateFormat("dd/MM/yyyy");
			
			vlRoot.addComponent(new HorizontalLayout(){
				{
					addComponent(new FormLayout(){
						{
							setMargin(true);
							setSpacing(true);
							setStyleName("form-cutom");
							addComponent(vencSegu);
							
						}
					});	
				}
			});
			
			final DateField DataGaran = new DateField("Garantia");				
			fieldGroup.bind(DataGaran, "data_garantia");
			DataGaran.setRequired(true);
			DataGaran.setStyleName("caption-align-veiculos");
			DataGaran.setImmediate(true);
			DataGaran.setWidth(width_data_garantia);
			DataGaran.setDateFormat("dd/MM/yyyy");
			
			vlRoot.addComponent(new FormLayout(){
				{
					setMargin(true);
					setSpacing(true);
					setStyleName("form-cutom-new");
					addComponent(DataGaran);
				}
			});
			
			
			final DateField vencIpva = new DateField("Vencimento do IPVA");				
			fieldGroup.bind(vencIpva, "venc_ipva_impostos");
			vencIpva.setRequired(true);
			vencIpva.setStyleName("caption-align-veiculos");
			vencIpva.setImmediate(true);
			vencIpva.setWidth(width_venc_ipva_impostos);
			vencIpva.setDateFormat("dd/MM/yyyy");
			
			vlRoot.addComponent(new FormLayout(){
				{
					setMargin(true);
					setSpacing(true);
					setStyleName("form-cutom");
					addComponent(vencIpva);
				}
			});											
				
			 
			vlRoot.addComponent(new HorizontalLayout(){
				{
					
					final DateField DataUltRev = new DateField("Última Revisão");				
					fieldGroup.bind(DataUltRev, "data_ultima_revisao");
					DataUltRev.setRequired(true);
					DataUltRev.setImmediate(true);
					DataUltRev.setWidth(width_data_ultima_revisao);
					DataUltRev.setStyleName("caption-align-veiculos");
					DataUltRev.setDateFormat("dd/MM/yyyy");
					
					
					addComponent(new FormLayout(){
						{
							setMargin(true);
							setSpacing(true);
						//	setStyleName("form-cutom");
							setStyleName("veiculos-ultRevisao");
							addComponent(DataUltRev);
						}
					});
					
					final Property<Integer> integerProperty2 = (Property<Integer>) item.getItemProperty("revisao_geral");				
					final TextField tfProxRev = new TextField("Próxima Revisão(Km)",integerProperty2);
					fieldGroup.bind(tfProxRev, "revisao_geral");
					tfProxRev.setRequired(true);
					tfProxRev.setNullRepresentation("");
					tfProxRev.setImmediate(true);
					tfProxRev.setMaxLength(30);
					tfProxRev.setWidth(width_revisao_geral);
					
					addComponent(new FormLayout(){
						{
							setMargin(true);
							setSpacing(true);
						//	setStyleName("form-cutom");
							addComponent(tfProxRev);
						}
					});				
				}
			});
			
			
			final Property<Integer> integerProperty = (Property<Integer>) item.getItemProperty("km_atual");							
			final TextField tfKm = new TextField("Km Atual",integerProperty);
			fieldGroup.bind(tfKm, "km_atual");
			tfKm.setRequired(true);
			tfKm.setStyleName("caption-align-veiculos");
			tfKm.setWidth(width_km_atual);
			tfKm.setNullRepresentation("");
			tfKm.setMaxLength(30);
			tfKm.setImmediate(true);
			
			vlRoot.addComponent(new FormLayout(){
				{
					setMargin(true);
					setSpacing(true);
					setStyleName("form-cutom");
					addComponent(tfKm);
				}
			});
			
			
			vlRoot.addComponent(new FormLayout(){
				{
					setMargin(true);
					setSpacing(true);
					setStyleName("form-cutom");
					
					TextArea txtArea = new TextArea("Obs.:");
					txtArea.setStyleName("caption-align-veiculos");
					txtArea.setHeight("50px");
					txtArea.setWidth("300px");
					
					fieldGroup.bind(txtArea, "obs");		
					
					addComponent(txtArea); 
				}
			});
		}

			

		private VerticalLayout buildHorizontalLayout(){
			hlTeste = new VerticalLayout(){
				
				{
					boxMarca.setRequired(true);
					boxMarca.setWidth(width_marca);
					boxMarca.setImmediate(true);
					boxMarca.setStyleName("caption-align-veiculos");
					
					if(tipoVeiculo != ""){						

						if(tipoVeiculo.equals("Automovel")){

						boxMarca.setEnabled(true);
						
						boxMarca.addItem("Aston Martin");
						boxMarca.addItem("Audi");
						boxMarca.addItem("BMW");
						boxMarca.addItem("GM/Chevrolet");
						boxMarca.addItem("Chrysler");
						boxMarca.addItem("Citroën");
						boxMarca.addItem("Dodge");
						boxMarca.addItem("Ferrari");
						boxMarca.addItem("Fiat");
						boxMarca.addItem("Ford");
						boxMarca.addItem("Honda");
						boxMarca.addItem("Hyundai");
						boxMarca.addItem("Iveco");
						boxMarca.addItem("Jac Motors");
						boxMarca.addItem("Jaguar");
						boxMarca.addItem("Jeep");
						boxMarca.addItem("Kia");
						boxMarca.addItem("Lamborghini");
						boxMarca.addItem("Land Rover");
						boxMarca.addItem("Maserati");
						boxMarca.addItem("Mercedes-Benz");	
						boxMarca.addItem("MG Motors");	
						boxMarca.addItem("Mini");	
						boxMarca.addItem("Nissan");	
						boxMarca.addItem("Peugeot");	
						boxMarca.addItem("Porsche");	
						boxMarca.addItem("Renault");	
						boxMarca.addItem("Subaru");	
						boxMarca.addItem("Suzuki");	
						boxMarca.addItem("Toyota");	
						boxMarca.addItem("Troller");	
						boxMarca.addItem("Volkswagen");	
						boxMarca.addItem("Volvo");
						
					}else if(tipoVeiculo.equals("Motocicleta")){
				

						
						boxMarca.setEnabled(true);
						
						boxMarca.addItem("BMW");
						boxMarca.addItem("Dafra");
						boxMarca.addItem("Ducati");
						boxMarca.addItem("Harley-Davidson");
						boxMarca.addItem("Honda");							
						boxMarca.addItem("Kawasaki");	
						boxMarca.addItem("Kasinski");
						boxMarca.addItem("Yamaha");
						boxMarca.addItem("Suzuki");
						boxMarca.addItem("Sundown");
						boxMarca.addItem("Shineray");
						
					}else{
						boxMarca.setEnabled(false);
						boxMarca.addItem("");
					}
				}
					boxMarca.setNullSelectionAllowed(false);
					addComponent(new FormLayout(){
						{
							setMargin(true);
							setSpacing(true);
							setStyleName("form-cutom");
							addComponent(boxMarca);
							setExpandRatio(boxMarca, 2);
						}
						
					});
					
					

					tfModelo.setCaption("Modelo");
					tfModelo.setRequired(true);
					tfModelo.setWidth(width_modelo);
					tfModelo.setMaxLength(100);
					tfModelo.setStyleName("caption-align-veiculos");
					tfModelo.setNullRepresentation("");
					tfModelo.setImmediate(true);
					
					addComponent(new FormLayout(){
						{
							setMargin(true);
							setSpacing(true);
							setStyleName("form-cutom");
							addComponent(tfModelo);
						}
					});		
				}			
			};
		
			return hlTeste;			
		}
		
		public JPAContainer<Fornecedor> buildFornecedorDefault(){
			JPAContainer<Fornecedor> containerFornecedor = JPAContainerFactory.make(Fornecedor.class, ConnUtil.getEntity());
			containerFornecedor.addContainerFilter(Filters.eq("empresa", OpusERP4UI.getEmpresa()));
			return containerFornecedor;
		}


		
			@Override
			public Button buildBtSalvar() {
				btSalvar = new Button("OK", new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						if(fieldGroup.isValid() && cnpjCpfValid == true){
							try {
								
								if(item.getItemProperty("id").getValue() == null){
									fieldGroup.commit();				
									fireEvent(new VeiculoEvent(getUI(), item, true));
									Notification.show("Veiculo Cadastrado com Sucesso!");
								}else{
									if(fieldGroup.isModified()){
										fieldGroup.commit();				
										fireEvent(new VeiculoEvent(getUI(), item, true));
										Notification.show("Veiculo Alterado com Sucesso!");	
									}
																	
								}
								
								close();
							} catch (CommitException e) {

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
							
							Notification.show("Não é Possivel Salvar, Verifique os Campos Obrigatórios/Inválidos e Tente Novamente!");
						}	
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
				return btSalvar;
			}

			@Override
			public Button buildBtCancelar() {
				btCancelar = new Button("Cancelar", new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						
						if(!fieldGroup.isModified()){
							fieldGroup.discard();				
							fireEvent(new VeiculoEvent(getUI(), item, false));
							close();
						}else{
							GenericDialog gDialog = new GenericDialog("Confirme para Continuar!", "Deseja Salvar as Informações Alteradas?", true, true);
							gDialog.setCaptionCANCEL("Sair sem Salvar!");
							gDialog.setCaptionOK("Salvar");
							
							gDialog.addListerner(new GenericDialog.DialogListerner() {
								
								@Override
								public void onClose(DialogEvent event) {
									if(event.isConfirm()){
										
										if(fieldGroup.isValid()){
											try {
												fieldGroup.commit();
												fireEvent(new VeiculoEvent(getUI(), item, true));	
												
												if(item.getItemProperty("id").getValue() == null){
													Notification.show("Veiculo Cadastrado com Sucesso!");
												}else{
													Notification.show("Veiculo Alterado com Sucesso!");
													 }
	
												close();
											} catch (Exception e) {
												e.printStackTrace();
												System.out.println("Erro ao cadastrar ou alterar : "+e.getMessage()+".\n Causado por: "+e.getCause());
												Notification.show("Não foi Possivel Salvar as Alterações!");
											}
										}else{
											Notification.show("Não é Possivel Salvar, Verifique os Campos Obrigatórios/Inválidos e Tente Novamente!");
										}
									}else{							
										fieldGroup.discard();				
										fireEvent(new VeiculoEvent(getUI(), item, false));
										close();						
									}
								}
							});					
							btCancelar.addShortcutListener(sl_esc);
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
				
				return btCancelar;
			}
			
			
			public void addListerner(VeiculoListerner target){
				try {
					Method method = VeiculoListerner.class.getDeclaredMethod("onClose", VeiculoEvent.class);
					addListener(VeiculoEvent.class, target, method);
				} catch (Exception e) {
					System.out.println("Método não Encontrado!");
				}
			}
			public void removeListerner(VeiculoListerner target){
				removeListener(VeiculoEvent.class, target);
			}
			public static class VeiculoEvent extends Event{
				
				private Item item;
				private boolean confirm;
				
				public VeiculoEvent(Component source, Item item, boolean confirm) {
					super(source);
					this.item = item;
					this.confirm = confirm;			
				}

				public Item getItem() {
					return item;
				}	

				public boolean isConfirm() {
					return confirm;
				}		
			}
			public interface VeiculoListerner extends Serializable{
				public void onClose(VeiculoEvent event);
			}
		

}

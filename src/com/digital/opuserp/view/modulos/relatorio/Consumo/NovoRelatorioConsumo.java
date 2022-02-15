	package com.digital.opuserp.view.modulos.relatorio.Consumo;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.RadAcct;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.view.modulos.relatorio.SearchParameters;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.filter.Compare.Greater;
import com.vaadin.data.util.filter.Compare.GreaterOrEqual;
import com.vaadin.data.util.filter.Compare.Less;
import com.vaadin.data.util.filter.Compare.LessOrEqual;
import com.vaadin.data.util.filter.Like;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Reindeer;

public class NovoRelatorioConsumo extends Window {

	private JPAContainer<RadAcct> container;

	private String valorFiltro;
	private String valorOperador;
	private VerticalLayout vlFiltros;
	private HorizontalLayout hlFiltro;
	private ComboBox cbFiltro;
	private ComboBox cbOperador;
	private TextField tfValorFiltro;
	private HorizontalLayout hlFiltroRoot;
	private List<SearchParameters> listaParametros = new ArrayList<SearchParameters>();
	private ComboBox cbOrdenacao;
	private ComboBox cbTipoTabela;
	private ComboBox cbOrientacao;
	private ComboBox cbResumo;
	
	private PopupDateField dFValorFiltro;
	private ComboBox cBValorFiltro;
	
	private Button btAdd;	
	private Button btSalvar;
		
	private VerticalLayout vlRoot;
	
	private String ordenacao;
	private String orientacao;
	private String tipo;
	private String resumo;
	private String valor = "";
	
	public NovoRelatorioConsumo(String title, boolean modal){
			
		setWidth("890px");
		
		container = buildJpaContainer();
		
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
	
	
	
	public NovoRelatorioConsumo(String title, boolean modal,String tipo,String orientacao,String ordenacao,String resumo, List<SearchParameters> listaParametros){
		
		this.ordenacao = ordenacao;
		this.tipo = tipo;
		this.orientacao = orientacao;
		this.resumo = resumo;
		
		this.listaParametros = listaParametros;
		
		
		
		setWidth("890px");
		container = buildJpaContainer();
		
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
	
	
	
	public JPAContainer<RadAcct> buildJpaContainer(){
		container = JPAContainerFactory.make(RadAcct.class, ConnUtil.getEntity());
		container.addContainerFilter(Filters.not(new Equal("acctstoptime",(null))));
		container.sort(new String[]{"radacctid"}, new boolean[]{false});
		container.applyFilters();
				
		return container;
	}

	
	public void buildLayout(){
					
		vlRoot.addComponent(
			new FormLayout(){					
			{
				setStyleName("form-cutom");
				setMargin(true);
				setSpacing(true);
													
				cbTipoTabela = new ComboBox("Tipo");
				cbTipoTabela.setWidth("175px");
				cbTipoTabela.focus();
				cbTipoTabela.setNullSelectionAllowed(false);
				cbTipoTabela.addItem("COLUNA ÚNICA");
				cbTipoTabela.addItem("MULTI COLUNA");
//				cbTipoTabela.addItem("DETALHAMENTO");
				cbTipoTabela.setStyleName("caption-align");
				cbTipoTabela.setRequired(true);
				
				if(tipo != null)
				{
					cbTipoTabela.select(tipo);
				}
			
				addComponent(cbTipoTabela);
			}
		});
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
														
					cbOrientacao = new ComboBox("Orientação");
					cbOrientacao.setWidth("175px");
					cbOrientacao.setNullSelectionAllowed(false);
					cbOrientacao.addItem("RETRATO");
					cbOrientacao.addItem("PAISAGEM");
					cbOrientacao.setStyleName("caption-align");
					cbOrientacao.setRequired(true);
					
					if(orientacao != null){
						cbOrientacao.select(orientacao);						
					}
				
					addComponent(cbOrientacao);
				}
		});
		
		
		
		vlRoot.addComponent(
			new FormLayout(){					
			{
				setStyleName("form-cutom");
				setMargin(true);
				setSpacing(true);
		
				cbOrdenacao = new ComboBox("Ordenação");
				cbOrdenacao.setWidth("175px");
				cbOrdenacao.setNullSelectionAllowed(false);
				cbOrdenacao.setRequired(true);				
				
				cbOrdenacao.addItem("Código");				
				cbOrdenacao.addItem("Login");			
				cbOrdenacao.addItem("Data Inicio");
				cbOrdenacao.addItem("Data Final");
				cbOrdenacao.addItem("Tempo Total");				
				cbOrdenacao.addItem("Upload");
				cbOrdenacao.addItem("Download");
				cbOrdenacao.addItem("Causa do Término");
				cbOrdenacao.addItem("Ip");
								
				cbOrdenacao.setStyleName("caption-align");

				if(ordenacao != null){
					cbOrdenacao.select(ordenacao);
				}
			
				addComponent(cbOrdenacao);
			}
		});

		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
														
					cbResumo = new ComboBox("Resumo por");
					cbResumo.setWidth("175px");
					cbResumo.setNullSelectionAllowed(false);
//					cbResumo.addItem("Código");				
					cbResumo.addItem("Login");			
					cbResumo.addItem("Data Inicio");
					cbResumo.addItem("Data Final");
					cbResumo.addItem("Tempo Total");				
					cbResumo.addItem("Upload");
					cbResumo.addItem("Download");
					cbResumo.addItem("Causa do Término");
					cbResumo.addItem("Ip");
					
					cbResumo.setStyleName("caption-align");
					cbResumo.addStyleName("margin-bottom-20");
					
					if(resumo != null){
						cbResumo.select(resumo);						
					}
				
					addComponent(cbResumo);
				}
		});
					
														
		TabSheet ts_principal = new TabSheet();
	    TabSheet.Tab t;
	    t = ts_principal.addTab(new FormLayout(){
	    		{
		    		setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
					
					valorFiltro = "Nome/Razão Social";
					valorOperador = "IGUAL";
									
									
					// Filtros
					vlFiltros = new VerticalLayout();
					vlFiltros.addStyleName("margin-20");
					vlFiltros.setSizeUndefined();

					
					if(listaParametros != null && listaParametros.size() >0){
						
						for (SearchParameters s : listaParametros) {
							
							hlFiltro = new HorizontalLayout();
							hlFiltro.addComponent(buildCbFiltro(s.getCampo()));
							hlFiltro.addComponent(buildCbOperador(s.getOperador()));
	
							if(selectHeader(s.getCampo()).equals("Causa do Término") || selectHeader(s.getCampo()).equals("Natureza Itens") || selectHeader(s.getCampo()).equals("Tipo")){		
								hlFiltro.addComponent(buildcbValorFiltro(s.getValor(),selectHeader(s.getCampo()),true));							
							}else if(selectHeader(s.getCampo()).equals("Data Inicio")||selectHeader(s.getCampo()).equals("Data Final")){
								hlFiltro.addComponent(buildDfValorFiltro(s.getValor(), selectHeader(s.getCampo()),true));								
							}else{
								hlFiltro.addComponent(buildTfValorFiltro(s.getValor(),true));
							}
							
							
							hlFiltro.addComponent(buildBtAdd(true, s.getId()));
							
							hlFiltroRoot = new HorizontalLayout();
							hlFiltroRoot.setWidth("100%");
							
							vlFiltros.addComponent(hlFiltro);
						}
							
							hlFiltro = new HorizontalLayout();
							hlFiltro.addComponent(buildCbFiltro(null));
							hlFiltro.addComponent(buildCbOperador(null));
							hlFiltro.addComponent(buildTfValorFiltro(null,false));
							hlFiltro.addComponent(buildBtAdd(false, null));
															
							hlFiltroRoot = new HorizontalLayout();
							hlFiltroRoot.setWidth("100%");
	
							vlFiltros.addComponent(hlFiltro);
						
					}else{
						hlFiltro = new HorizontalLayout();
						hlFiltro.addComponent(buildCbFiltro(null));
						hlFiltro.addComponent(buildCbOperador(null));
						hlFiltro.addComponent(buildTfValorFiltro(null,false));
						hlFiltro.addComponent(buildBtAdd(false, null));
														
						hlFiltroRoot = new HorizontalLayout();
						hlFiltroRoot.setWidth("100%");

						vlFiltros.addComponent(hlFiltro);
					}
					
					
					hlFiltroRoot.addComponent(vlFiltros);
					//hlFiltroRoot.setComponentAlignment(vlFiltros, Alignment.TOP_LEFT);
					
									
					addComponent(hlFiltroRoot);			
				
				}
				
	    	
	    }, "Filtro");
	
		vlRoot.addComponent(ts_principal);
			
		
	}
	
	private PopupDateField buildDfValorFiltro(String v, String coluna, final boolean editing){
		dFValorFiltro = new PopupDateField();
		dFValorFiltro.setWidth("380px");
		dFValorFiltro.setImmediate(true);
		dFValorFiltro.addStyleName("margin-right-20");
		dFValorFiltro.setLenient(true);
		dFValorFiltro.setTextFieldEnabled(true);

		
//		if(!coluna.equals("Data de Nascimento")){
			dFValorFiltro.setDateFormat("dd/MM/yyyy HH:mm:ss");
			dFValorFiltro.setResolution(DateField.RESOLUTION_HOUR);
			dFValorFiltro.setResolution(DateField.RESOLUTION_MIN);
//		}else{
//			dFValorFiltro.setDateFormat("dd/MM/yyyy");
//			
//		}
		
		dFValorFiltro.addValueChangeListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(cbFiltro.getValue() != null){
					
					if(btAdd != null && dFValorFiltro.getValue() != null && !editing){
						btAdd.setEnabled(true);
						String idButton = String.valueOf(listaParametros.size())+dFValorFiltro.getValue().toString(); 
						btAdd.setId(idButton);
						btAdd.setEnabled(true);
					}
					
				}else{
					if(btAdd != null){
						btAdd.setEnabled(false);
					}
				}				
			}
		});
		
		return dFValorFiltro;
	}
	
	private ComboBox buildcbValorFiltro(String v, String coluna,final boolean editing) {	
		cBValorFiltro = new ComboBox();
		cBValorFiltro.setWidth("404px");
		cBValorFiltro.setImmediate(true);
		cBValorFiltro.setNullSelectionAllowed(false);
		cBValorFiltro.setTextInputAllowed(false);

		
		
		if(coluna.equals("Causa do Término")){
			cBValorFiltro.addItem("User-Request");
			cBValorFiltro.addItem("Lost-Carrier");
			cBValorFiltro.addItem("Lost-Service");
			cBValorFiltro.addItem("Idle-Timeout");
			cBValorFiltro.addItem("Session-Timeout");
			cBValorFiltro.addItem("Admin-Reset");
			cBValorFiltro.addItem("Admin-Reboot");
			cBValorFiltro.addItem("Port-Error");
			cBValorFiltro.addItem("NAS-Error");
			cBValorFiltro.addItem("NAS-Request");
			cBValorFiltro.addItem("NAS-Reboot");
			cBValorFiltro.addItem("Port-Unneeded");
			cBValorFiltro.addItem("Port-Preempted");
			cBValorFiltro.addItem("Port-Suspended");
			cBValorFiltro.addItem("Service-Unavailable");
			cBValorFiltro.addItem("Callback");
			cBValorFiltro.addItem("User-Error");
			cBValorFiltro.addItem("Host-Request");		
		}
				
		cBValorFiltro.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				if(cbFiltro.getValue() != null){
					if(btAdd != null && !editing){
						btAdd.setEnabled(true);
						String idButton = String.valueOf(listaParametros.size())+cBValorFiltro.getValue().toString(); 
						btAdd.setId(idButton);
						btAdd.setEnabled(true);
					}
					
				}else{
					if(btAdd != null){
						btAdd.setEnabled(false);
					}
				}
			}
			
			
		});	
		
		
		if(v != null){
			cBValorFiltro.select(v);
			cBValorFiltro.setReadOnly(true);			
		}
				
		return cBValorFiltro;
	}

	private TextField buildTfValorFiltro(String v, final boolean editing) {	
		tfValorFiltro = new TextField();
		tfValorFiltro.setWidth("404px");
		tfValorFiltro.setTextChangeEventMode(TextChangeEventMode.LAZY);
		tfValorFiltro.setImmediate(true);
		tfValorFiltro.setTextChangeEventMode(TextChangeEventMode.LAZY);
		tfValorFiltro.addTextChangeListener(new FieldEvents.TextChangeListener() {
			
			@Override
			public void textChange(TextChangeEvent event) {
				if(cbFiltro.getValue() != null){
					String coluna = cbFiltro.getValue().toString();
					if(!event.getText().isEmpty() && !editing){
											
						String idButton = String.valueOf(listaParametros.size())+((TextField) event.getComponent()).getValue(); 
						btAdd.setId(idButton);
						btAdd.setEnabled(true);
						
					}else{
						btAdd.setEnabled(false);
					}
				}else{
					btAdd.setEnabled(false);
				}
				
			}
		});
	
		tfValorFiltro.addListener(new FieldEvents.BlurListener() {
			
			@Override
			public void blur(BlurEvent event) {
				
				if(cbFiltro.getValue() != null){
					String coluna = cbFiltro.getValue().toString();
					if(!tfValorFiltro.getValue().isEmpty()){
												
						String idButton = String.valueOf(listaParametros.size())+((TextField) event.getComponent()).getValue(); 
						btAdd.setId(idButton);
						btAdd.setEnabled(true);
						
					}else{
						btAdd.setEnabled(false);
					}
				}else{
					btAdd.setEnabled(false);
				}
			}
		});
		
		
		if(v != null){
			tfValorFiltro.setValue(v);
			tfValorFiltro.setEnabled(false);
		}
				
		return tfValorFiltro;
	}
	private ComboBox buildCbOperador(String v) {
		cbOperador = new ComboBox();
		cbOperador.setNullSelectionAllowed(false);
		
		cbOperador.setImmediate(true);		
		cbOperador.addListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				valorOperador = event.getProperty().toString();
			}
		});
		
		if(v != null){
			cbOperador.addItem(v);
			cbOperador.select(v);
			cbOperador.setEnabled(false);
		}
		
		return cbOperador;
	}
	private ComboBox buildCbFiltro(final String v) {
		cbFiltro = new ComboBox();
//		cbFiltro.focus();
		cbFiltro.setNullSelectionAllowed(false);
		cbFiltro.addItem("Código");				
		cbFiltro.addItem("Login");			
		cbFiltro.addItem("Data Inicio");
		cbFiltro.addItem("Data Final");
		cbFiltro.addItem("Tempo Total");				
		cbFiltro.addItem("Upload");
		cbFiltro.addItem("Download");
		cbFiltro.addItem("Causa do Término");
		cbFiltro.addItem("Ip");
		
		cbFiltro.setImmediate(true);
		cbFiltro.setWidth("146px");
		
		
		cbFiltro.addValueChangeListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				
				if(v == null){
					String coluna = cbFiltro.getValue().toString();
					Class<?> type = container.getType(selectFiltro(coluna)); 				
					
					if(cbOperador != null){
						cbOperador.removeAllItems();
						if(type == String.class){					
							cbOperador.addItem("CONTEM");
							cbOperador.addItem("NAO CONTEM");
							cbOperador.addItem("IGUAL");
							cbOperador.addItem("DIFERENTE");
							
							cbOperador.select("CONTEM");
							
							
							if(coluna.equals("Causa do Término")){
								if(hlFiltro.getComponentIndex(tfValorFiltro) > 0){
									hlFiltro.replaceComponent(tfValorFiltro, buildcbValorFiltro(v, coluna,false));
								}else if(hlFiltro.getComponentIndex(dFValorFiltro) > 0){
									hlFiltro.replaceComponent(dFValorFiltro, buildcbValorFiltro(v, coluna,false));
								}else{
									hlFiltro.replaceComponent(cBValorFiltro, buildcbValorFiltro(v, coluna,false));
								}
	
									
							}else{								
								if(hlFiltro.getComponentIndex(cBValorFiltro) > 0){
									hlFiltro.replaceComponent(cBValorFiltro, buildTfValorFiltro(v,false));
								}else if(hlFiltro.getComponentIndex(dFValorFiltro) > 0){
									hlFiltro.replaceComponent(dFValorFiltro, buildTfValorFiltro(v,false));
								}else{
									hlFiltro.replaceComponent(tfValorFiltro, buildTfValorFiltro(v,false));
								}
							}	
						}else if(type == Integer.class){
							cbOperador.addItem("IGUAL");
							cbOperador.addItem("DIFERENTE");
							cbOperador.addItem("MAIOR QUE");
							cbOperador.addItem("MENOR QUE");
							cbOperador.addItem("MAIOR IGUAL QUE");
							cbOperador.addItem("MENOR IGUAL QUE");
							
							
							cbOperador.select("IGUAL");
							
							if(hlFiltro.getComponentIndex(cBValorFiltro) > 0){
								hlFiltro.replaceComponent(cBValorFiltro, buildTfValorFiltro(v,false));
							}else if(hlFiltro.getComponentIndex(dFValorFiltro) > 0){
								hlFiltro.replaceComponent(dFValorFiltro, buildTfValorFiltro(v,false));
							}else{
								hlFiltro.replaceComponent(tfValorFiltro, buildTfValorFiltro(v,false));
							}
						}else if(type == Float.class){
							cbOperador.addItem("IGUAL");
							cbOperador.addItem("DIFERENTE");
							cbOperador.addItem("MAIOR QUE");
							cbOperador.addItem("MENOR QUE");
							cbOperador.addItem("MAIOR IGUAL QUE");
							cbOperador.addItem("MENOR IGUAL QUE");
							
							
							cbOperador.select("IGUAL");
							
							if(hlFiltro.getComponentIndex(cBValorFiltro) > 0){
								hlFiltro.replaceComponent(cBValorFiltro, buildTfValorFiltro(v,false));
							}else if(hlFiltro.getComponentIndex(dFValorFiltro) > 0){
								hlFiltro.replaceComponent(dFValorFiltro, buildTfValorFiltro(v,false));
							}else{
								hlFiltro.replaceComponent(tfValorFiltro, buildTfValorFiltro(v,false));
							}
						}else if(type == Long.class){
							cbOperador.addItem("IGUAL");
							cbOperador.addItem("DIFERENTE");
							cbOperador.addItem("MAIOR QUE");
							cbOperador.addItem("MENOR QUE");
							cbOperador.addItem("MAIOR IGUAL QUE");
							cbOperador.addItem("MENOR IGUAL QUE");
							
							
							cbOperador.select("IGUAL");
							
							if(hlFiltro.getComponentIndex(cBValorFiltro) > 0){
								hlFiltro.replaceComponent(cBValorFiltro, buildTfValorFiltro(v,false));
							}else if(hlFiltro.getComponentIndex(dFValorFiltro) > 0){
								hlFiltro.replaceComponent(dFValorFiltro, buildTfValorFiltro(v,false));
							}else{
								hlFiltro.replaceComponent(tfValorFiltro, buildTfValorFiltro(v,false));
							}
						}else if(type == Date.class){
							cbOperador.addItem("IGUAL");
							cbOperador.addItem("DIFERENTE");
							cbOperador.addItem("MAIOR QUE");
							cbOperador.addItem("MENOR QUE");
							cbOperador.addItem("MAIOR IGUAL QUE");
							cbOperador.addItem("MENOR IGUAL QUE");
							
							cbOperador.select("IGUAL");
							
							if(hlFiltro.getComponentIndex(cBValorFiltro) > 0){
								hlFiltro.replaceComponent(cBValorFiltro, buildDfValorFiltro(v,coluna,false));
							}else if(hlFiltro.getComponentIndex(dFValorFiltro) > 0){
								hlFiltro.replaceComponent(dFValorFiltro, buildDfValorFiltro(v,coluna,false));
							}else{
								hlFiltro.replaceComponent(tfValorFiltro, buildDfValorFiltro(v,coluna,false));								
							}
						}	
					
						
					}
				}
			}
		});
		
		if(v != null){
			cbFiltro.select(selectHeader(v));
			cbFiltro.setEnabled(false); 
		}

		
		
	
		return cbFiltro;
	}
	public String selectFiltro(String s) {
		
		String filtro = "";
			

		if(s.equals("Código")){
			filtro = "radacctid";												
		}else if(s.equals("Login")){
			filtro = "username";					
		}else if(s.equals("Data Inicio")){
			filtro = "acctstarttime";					
		}else if(s.equals("Data Final")){
			filtro = "acctstoptime";					
		}else if(s.equals("Tempo Total")){
			filtro = "acctsessiontime";					
		}else if(s.equals("Upload")){
			filtro = "acctinputoctets";					
		}else if(s.equals("Download")){
			filtro = "acctoutputoctets";		
		}else if(s.equals("Causa do Término")){
			filtro = "acctterminatecause";		
		}else if(s.equals("Ip")){
			filtro = "framedipaddress";		
		}		
		return filtro;
	}
	
	public String selectHeader(String s) {
		
		String filtro = "";
		

		if(s.equals("radacctid")){
			filtro = "Código";												
		}else if(s.equals("username")){
			filtro = "Login";					
		}else if(s.equals("acctstarttime")){
			filtro = "Data Inicio";					
		}else if(s.equals("acctstoptime")){
			filtro = "Data Final";					
		}else if(s.equals("acctsessiontime")){
			filtro = "Tempo Total";					
		}else if(s.equals("acctinputoctets")){
			filtro = "Upload";					
		}else if(s.equals("acctoutputoctets")){
			filtro = "Download";		
		}else if(s.equals("acctterminatecause")){
			filtro = "Causa do Término";		
		}else if(s.equals("framedipaddress")){
			filtro = "Ip";		
		}	
		return filtro;
	}								
	
	
	private Button buildBtAdd(boolean editing, String id) {
		btAdd = new Button("Add",new Button.ClickListener() {
					
				@Override
				public void buttonClick(ClickEvent event) {
					hlFiltro = new HorizontalLayout();
					
					if(event.getButton().getCaption().toString().equals("Add")){
						 if(cbFiltro.getValue().toString().equals("Causa do Término")){
							cbFiltro.setEnabled(false);
							cbOperador.setEnabled(false);
							cBValorFiltro.setEnabled(false);
		
							String idSearch = String.valueOf(listaParametros.size())+cBValorFiltro.getValue().toString();
							listaParametros.add(new SearchParameters(idSearch, selectFiltro(cbFiltro.getValue().toString()), cbOperador.getValue().toString(), cBValorFiltro.getValue().toString()));
							
							btAdd.setCaption("Remover");
							hlFiltro.addComponent(buildCbFiltro(null));
							hlFiltro.addComponent(buildCbOperador(null));
							hlFiltro.addComponent(buildTfValorFiltro(null,false));
							hlFiltro.addComponent(buildBtAdd(false, null));
							vlFiltros.addComponent(hlFiltro);		
						}else if(cbFiltro.getValue().toString().equals("Data Inicio")||cbFiltro.getValue().toString().equals("Data Final")){
							cbFiltro.setEnabled(false);
							cbOperador.setEnabled(false);
							dFValorFiltro.setEnabled(false);
														
							String idSearch = String.valueOf(listaParametros.size())+dFValorFiltro.getValue().toString();
							
							SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
							listaParametros.add(new SearchParameters(idSearch, selectFiltro(cbFiltro.getValue().toString()), cbOperador.getValue().toString(), sdf.format(dFValorFiltro.getValue())));
							
							
							btAdd.setCaption("Remover");
							hlFiltro.addComponent(buildCbFiltro(null));
							hlFiltro.addComponent(buildCbOperador(null));
							hlFiltro.addComponent(buildTfValorFiltro(null,false));
							hlFiltro.addComponent(buildBtAdd(false, null));
							vlFiltros.addComponent(hlFiltro);									
						}else{
							
							if(tfValorFiltro.getValue() != null && !tfValorFiltro.getValue().toString().equals("") && !tfValorFiltro.getValue().toString().isEmpty()){							
								
								String coluna = cbFiltro.getValue().toString();
																
								cbFiltro.setEnabled(false);
								cbOperador.setEnabled(false);
								tfValorFiltro.setEnabled(false);
																
								String idSearch = String.valueOf(listaParametros.size())+tfValorFiltro.getValue();
								listaParametros.add(new SearchParameters(idSearch, selectFiltro(cbFiltro.getValue().toString()), cbOperador.getValue().toString(), tfValorFiltro.getValue()));
									
								btAdd.setCaption("Remover");									
								hlFiltro.addComponent(buildCbFiltro(null));
								hlFiltro.addComponent(buildCbOperador(null));
								hlFiltro.addComponent(buildTfValorFiltro(null,false));
								hlFiltro.addComponent(buildBtAdd(false, null));
								vlFiltros.addComponent(hlFiltro);
								
							}
						}
					}else{
						
						Integer i = 0;	
						for(SearchParameters sp: listaParametros){
							i++;
							if(sp.getId().equals(event.getButton().getId())){
								listaParametros.remove(i-1);
								break;
							}
						}
						
						
						hlFiltro = new HorizontalLayout();
						hlFiltro.addComponent(buildCbFiltro(null));
						hlFiltro.addComponent(buildCbOperador(null));
						hlFiltro.addComponent(buildTfValorFiltro(null,false));
						hlFiltro.addComponent(buildBtAdd(false, null));						
                        						
							
						vlFiltros.removeComponent(event.getButton().getParent());
						//vlFiltros.replaceComponent(event.getButton().getParent(), hlFiltro);
						
						
						for (int j = 0; j < vlFiltros.getComponentCount(); j++) {
							
						
							Object c = vlFiltros.getComponent(j);							
							
							if(vlFiltros.getComponentIndex((Component) c) == vlFiltros.getComponentCount()-1){
								vlFiltros.replaceComponent((Component) c, hlFiltro);
								break;
							}
						}
						
					}
					
					fireEvent(new AddFiltroConsumoEvent(getUI(), listaParametros));
				}
				
			});
		btAdd.setEnabled(false);
		btAdd.setStyleName(Reindeer.BUTTON_SMALL);
		
		if(editing){
			btAdd.setEnabled(true);
			btAdd.setId(id);
			btAdd.setCaption("Remover");
		}
		
		return btAdd;
	}
		
	public Button buildBtSalvar() {
		
		btSalvar = new Button("OK", new Button.ClickListener() {
			
			
			public void buttonClick(ClickEvent event) {
				if(cbOrdenacao.isValid() && cbTipoTabela.isValid() && cbResumo.isValid() && listaParametros.size() > 0){
					fireEvent(new RelatorioConsumoEvent(getUI(), true, listaParametros, cbTipoTabela.getValue().toString(), cbOrdenacao.getValue().toString(), cbOrientacao.getValue().toString(), cbResumo.getValue().toString()));		
					close();
				}else{
					
						if(cbFiltro.getValue()==null || cbFiltro.getValue().equals("")){
							cbFiltro.addStyleName("invalid-txt");
						}else{
							cbFiltro.removeStyleName("invalid-txt");
						}
						if(cbOperador.getValue()==null || cbOperador.getValue().equals("")){
							cbOperador.addStyleName("invalid-txt");
						}else{
							cbOperador.removeStyleName("invalid-txt");
						}
						if(tfValorFiltro.getValue()==null || tfValorFiltro.getValue().equals("")){
							tfValorFiltro.addStyleName("invalid-txt");
						}else{
							tfValorFiltro.removeStyleName("invalid-txt");
						}
											
						if(!cbOrdenacao.isValid()){
							cbOrdenacao.addStyleName("invalid-txt");
						}else{
							cbOrdenacao.removeStyleName("invalid-txt");
						}
						if(!cbTipoTabela.isValid()){
							cbTipoTabela.addStyleName("invalid-txt");
						}else{
							cbTipoTabela.removeStyleName("invalid-txt");
						}
						if(!cbOrientacao.isValid()){
							cbOrientacao.addStyleName("invalid-txt");
						}else{
							cbOrientacao.removeStyleName("invalid-txt");
						}
						if(!cbResumo.isValid()){
							cbResumo.addStyleName("invalid-txt");
						}else{
							cbResumo.removeStyleName("invalid-txt");
						}
						
				
					Notify.Show_Invalid_Submit_Form();
				}
			}
		});
		

		ShortcutListener slbtOK = new ShortcutListener("Ok", ShortcutAction.KeyCode.ENTER,null) {
			
			
			public void handleAction(Object sender, Object target) {
				btSalvar.click();
			}
		};
		
		btSalvar.addShortcutListener(slbtOK);
		
		btSalvar.setStyleName("default");
		return btSalvar;
	}

	Button btCancelar;
	public Button buildBtCancelar() {
		btCancelar = new Button("Cancelar", new Button.ClickListener() {
			
			
			public void buttonClick(ClickEvent event) {
				fireEvent(new RelatorioConsumoEvent(getUI(), false, null,null,null, null, null));
				close();	
			}
		});
		
		ShortcutListener slbtCancelar = new ShortcutListener("Cancelar", ShortcutAction.KeyCode.ESCAPE,null) {
			
			
			public void handleAction(Object sender, Object target) {
				btCancelar.click();
			}
		};
		
		btCancelar.addShortcutListener(slbtCancelar);
		
		
		return btCancelar;
	}
	
	
	public void addListerner(RelatorioConsumoListerner target){
		try {
			Method method = RelatorioConsumoListerner.class.getDeclaredMethod("onClose", RelatorioConsumoEvent.class);
			addListener(RelatorioConsumoEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(RelatorioConsumoListerner target){
		removeListener(RelatorioConsumoEvent.class, target);
	}
	
	
	public void addListerner(AddFiltroConsumoListerner target){
		try {
			Method method = AddFiltroConsumoListerner.class.getDeclaredMethod("onClose", AddFiltroConsumoEvent.class);
			addListener(AddFiltroConsumoEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(AddFiltroConsumoListerner target){
		removeListener(AddFiltroConsumoEvent.class, target);
	}
	
	
	public static class AddFiltroConsumoEvent extends Event{

		private List<SearchParameters> filtros;
		
		
		public AddFiltroConsumoEvent(Component source, List<SearchParameters> filtros) {
			super(source);
			
			
			this.filtros = filtros;
		}
		
		public List<SearchParameters> getFiltros(){
			return filtros;
		}
		
		
		
	}
	public interface AddFiltroConsumoListerner extends Serializable{
		public void onClose(AddFiltroConsumoEvent event);
	}
	
	
	public static class RelatorioConsumoEvent extends Event{
		
		
		private boolean confirm;
		private List<SearchParameters> parametros;
		private String tipo;
		private String ordenacao;
		private String orientacao;
		private String resumo;
		
		public RelatorioConsumoEvent(Component source, boolean confirm, List<SearchParameters> parametros, String tipo, String ordenacao, 
				String orientacao, String resumo) {
			super(source);
			this.confirm = confirm;
			this.parametros =  parametros;
			this.tipo = tipo;
			this.ordenacao = ordenacao;
			this.orientacao = orientacao;
			this.resumo = resumo;
		}

		public String getResumo(){
			return resumo;
		}
		
		public String getOrientacao(){
			return orientacao;
		}
		
		public List<SearchParameters> getParametros() {
			return parametros;
		}	
		
		public String getTipo(){
			return tipo;
		}
		
		public String getOrdenacao(){
			return ordenacao;
		}

		public boolean isConfirm() {
			return confirm;
		}	
		
		
	}
	public interface RelatorioConsumoListerner extends Serializable{
		public void onClose(RelatorioConsumoEvent event);
	}

	
	
	public void adicionarFiltro(){
				
		for(SearchParameters sp:listaParametros){
			
				if(sp.getOperador().equals("IGUAL")){
					
					if(container.getType(sp.getCampo()) == String.class){
						
						container.addContainerFilter(new Like(sp.getCampo(), sp.getValor(), true));
						
					}else if(container.getType(sp.getCampo()) == Date.class){
						String date = sp.getValor();
						Date dtValor = new Date(Date.parse(date.substring(3, 6)+date.substring(0, 3)+date.substring(6,10)));
						container.addContainerFilter(new Equal(sp.getCampo(), dtValor));
						
					}else if(container.getType(sp.getCampo()) == Integer.class){
												
						container.addContainerFilter(new Equal(sp.getCampo(), Integer.parseInt(sp.getValor())));
						
					}else if(container.getType(sp.getCampo()) == Long.class){
						Integer valor = Integer.parseInt(sp.getValor().toString());
						valor = valor*1024;
						container.addContainerFilter(new Equal(sp.getCampo(), Long.parseLong(valor.toString())));
					}
					
				}else if(sp.getOperador().equals("DIFERENTE")){
					
					if(container.getType(sp.getCampo()) == String.class){
						container.addContainerFilter(Filters.not(Filters.eq(sp.getCampo(), sp.getValor(), true)));						
						
						
					}else if(container.getType(sp.getCampo()) == Date.class){
						String date = sp.getValor();
						Date dtValor = new Date(Date.parse(date.substring(3, 6)+date.substring(0, 3)+date.substring(6,10)));
						container.addContainerFilter(Filters.not(Filters.eq(sp.getCampo(), dtValor)));
						
					}else if(container.getType(sp.getCampo()) == Integer.class){
												
						container.addContainerFilter(Filters.not(Filters.eq(sp.getCampo(), Integer.parseInt(sp.getValor()))));
						
					}else if(container.getType(sp.getCampo()) == Long.class){
						Integer valor = Integer.parseInt(sp.getValor().toString());
						valor = valor*1024;
						container.addContainerFilter(Filters.not(Filters.eq(sp.getCampo(), Long.parseLong(valor.toString()))));
					}
					
				}else if(sp.getOperador().equals("CONTEM")){
					
					if(container.getType(sp.getCampo()) == String.class){
						container.addContainerFilter(sp.getCampo(), "%"+sp.getValor()+"%", false, false);
					}
					
				}else if(sp.getOperador().equals("NAO CONTEM")){
					
					if(container.getType(sp.getCampo()) == String.class){
						container.addContainerFilter(Filters.not(Filters.like(sp.getCampo(),"%"+sp.getValor()+"%", true)));			
					}
					
				}else if(sp.getOperador().equals("MAIOR QUE")){
					
//					if(container.getType(sp.getCampo()) == Date.class && !sp.getCampo().equals("data")){
						if(container.getType(sp.getCampo()) == Date.class){
						
						try {
							String date = sp.getValor();
							SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
							Date dtValor = sdf.parse(date);
							container.addContainerFilter(new Greater(sp.getCampo(), dtValor));
							
						}catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}					
					}
					
					
					
					if(container.getType(sp.getCampo()) == Integer.class){
						container.addContainerFilter(new Greater(sp.getCampo(), Integer.parseInt(sp.getValor())));					
					}
					
					if(container.getType(sp.getCampo()) == Long.class){
						Integer valor = Integer.parseInt(sp.getValor().toString());
						valor = valor*1024;
						container.addContainerFilter(new Greater(sp.getCampo(), Long.parseLong(valor.toString())));					
					}
					
				}else if(sp.getOperador().equals("MENOR QUE")){
					
//					if(container.getType(sp.getCampo()) == Date.class && !sp.getCampo().equals("data")){
						if(container.getType(sp.getCampo()) == Date.class){
						
						try {
							String date = sp.getValor();
							SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
							Date dtValor = sdf.parse(date);						
							container.addContainerFilter(new Less(sp.getCampo(), dtValor));
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
					
					if(container.getType(sp.getCampo()) == Integer.class){
						container.addContainerFilter(new Less(sp.getCampo(), Integer.parseInt(sp.getValor())));					
					}
					if(container.getType(sp.getCampo()) == Long.class){
						Integer valor = Integer.parseInt(sp.getValor().toString());
						valor = valor*1024;
						container.addContainerFilter(new Less(sp.getCampo(), Long.parseLong(valor.toString())));					
					}
				}else if(sp.getOperador().equals("MAIOR IGUAL QUE")){
//					if(container.getType(sp.getCampo()) == Date.class && !sp.getCampo().equals("data")){
						if(container.getType(sp.getCampo()) == Date.class){
						
						try {
							String date = sp.getValor();
							SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
							Date dtValor = sdf.parse(date);
							container.addContainerFilter(new GreaterOrEqual(sp.getCampo(), dtValor));
							
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}						
					}

					if(container.getType(sp.getCampo()) == Integer.class){
						container.addContainerFilter(new GreaterOrEqual(sp.getCampo(), Integer.parseInt(sp.getValor())));					
					}
					
					if(container.getType(sp.getCampo()) == Long.class){
						Integer valor = Integer.parseInt(sp.getValor().toString());
						valor = valor*1024;
						container.addContainerFilter(new GreaterOrEqual(sp.getCampo(), Long.parseLong(valor.toString())));					
					}
				}else if(sp.getOperador().equals("MENOR IGUAL QUE")){
					
//					if(container.getType(sp.getCampo()) == Date.class && !sp.getCampo().equals("data")){
						if(container.getType(sp.getCampo()) == Date.class){
						
						
						try {
							String date = sp.getValor();
							SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyY HH:mm:ss");							
							Date dtValor = sdf.parse(date);
							container.addContainerFilter(new LessOrEqual(sp.getCampo(), dtValor));						
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
					if(container.getType(sp.getCampo()) == Integer.class){
						container.addContainerFilter(new LessOrEqual(sp.getCampo(), Integer.parseInt(sp.getValor())));					
					}
					if(container.getType(sp.getCampo()) == Long.class){
						Integer valor = Integer.parseInt(sp.getValor().toString());
						valor = valor*1024;
						container.addContainerFilter(new LessOrEqual(sp.getCampo(), Long.parseLong(valor.toString())));					
					}
				}
		}		
		
				
	}
}



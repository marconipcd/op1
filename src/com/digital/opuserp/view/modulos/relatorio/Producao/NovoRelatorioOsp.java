package com.digital.opuserp.view.modulos.relatorio.Producao;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.ServicoDAO;
import com.digital.opuserp.domain.Osp;
import com.digital.opuserp.domain.Servico;
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

public class NovoRelatorioOsp extends Window {

	private String valorFiltro;
	private String valorOperador;
	private VerticalLayout vlFiltros;
	private HorizontalLayout hlFiltro;
	private ComboBox cbFiltro;
	private ComboBox cbOperador;
	private TextField tfValorFiltro;
	private HorizontalLayout hlFiltroRoot;
	private List<SearchParameters> listaParametros = new ArrayList<SearchParameters>();
	private JPAContainer<Osp> container;
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
	
	
	public NovoRelatorioOsp(String title, boolean modal){
			
		setWidth("890px");
		
		container = buildContainer();
		
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
	
	
	
	public NovoRelatorioOsp(String title, boolean modal,String tipo,String orientacao,String ordenacao,String resumo, List<SearchParameters> listaParametros){
		
		this.ordenacao = ordenacao;
		this.tipo = tipo;
		this.orientacao = orientacao;
		this.resumo = resumo;
		
		this.listaParametros = listaParametros;
		
		
		
		setWidth("890px");
		container = buildContainer();
		
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
	
	
	
	public JPAContainer<Osp> buildContainer(){
		container = JPAContainerFactory.makeBatchable(Osp.class, ConnUtil.getEntity());
		container.setAutoCommit(false);
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
		
		container.addNestedContainerProperty("cliente.nome_razao");		
		container.addNestedContainerProperty("servico.nome");		
		container.addContainerFilter(Filters.not(Filters.eq("servico", null)));
		
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
				cbOrdenacao.addItem("Cliente");
				cbOrdenacao.addItem("Código");				
				cbOrdenacao.addItem("Servico");				
				cbOrdenacao.addItem("Descrição");
				cbOrdenacao.addItem("Observação");
				cbOrdenacao.addItem("Data Agendado");
				cbOrdenacao.addItem("Data Previsão Termino");				
				cbOrdenacao.addItem("Data Termino");				
				cbOrdenacao.addItem("Operador Abertura");
				cbOrdenacao.addItem("Operador Produção");
				
				cbOrdenacao.addItem("Motivo Cancelamento");
				cbOrdenacao.addItem("Status");
				cbOrdenacao.addItem("Setor");
				cbOrdenacao.addItem("Comprador");
				
				
				
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
					cbResumo.setRequired(true);
					cbResumo.addItem("Cliente");
					cbResumo.addItem("Código");				
					cbResumo.addItem("Servico");				
					cbResumo.addItem("Descrição");
					cbResumo.addItem("Observação");
					cbResumo.addItem("Data Agendado");
					cbResumo.addItem("Data Previsão Termino");					
					cbResumo.addItem("Data Termino");					
					cbResumo.addItem("Operador Abertura");
					cbResumo.addItem("Operador Produção");
					
					cbResumo.addItem("Motivo Cancelamento");
					cbResumo.addItem("Status");
					cbResumo.addItem("Setor");
					cbResumo.addItem("Comprador");
					
					
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
	
							if(selectHeader(s.getCampo()).equals("Servico")){		
								hlFiltro.addComponent(buildcbValorFiltro(s.getValor(),selectHeader(s.getCampo()),true));		
								
								
	    
							}else if(selectHeader(s.getCampo()).equals("Data Agendado") || selectHeader(s.getCampo()).equals("Data Previsão Termino")|| selectHeader(s.getCampo()).equals("Data Termino")){							
								
								hlFiltro.addComponent(buildDfValorFiltro(s.getValor(), selectHeader(s.getCampo()),true, selectHeader(s.getCampo()).equals("Data Previsão Termino") || selectHeader(s.getCampo()).equals("Data Termino") ? true : false));								
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
	
	private PopupDateField buildDfValorFiltro(String v,String coluna, final boolean editing, boolean time){
		dFValorFiltro = new PopupDateField();
		dFValorFiltro.setWidth("380px");
		dFValorFiltro.setImmediate(true);
		dFValorFiltro.addStyleName("margin-right-20");
		dFValorFiltro.setLenient(true);
		dFValorFiltro.setTextFieldEnabled(true);

		if(time){
			dFValorFiltro.setDateFormat("dd/MM/yyyy HH:mm:ss");		
			dFValorFiltro.setResolution(DateField.RESOLUTION_HOUR);
			dFValorFiltro.setResolution(DateField.RESOLUTION_MIN);
		}else{		
			dFValorFiltro.setDateFormat("dd/MM/yyyy");		
			//dFValorFiltro.setResolution(DateField.RESOLUTION_HOUR);
			//dFValorFiltro.setResolution(DateField.RESOLUTION_MIN);
		}
		
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
		
		if(v != null){
			
			try{
				 			
				
				SimpleDateFormat sdf;
				
				if(coluna.equals("Data Previsão Termino")){
					sdf =  new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
				}else{
					sdf =  new SimpleDateFormat("dd/MM/yyyy");
				}
				
				dFValorFiltro.setValue(sdf.parse(v));
				dFValorFiltro.setReadOnly(true);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		return dFValorFiltro;
	}
	
	private ComboBox buildcbValorFiltro(String v, String coluna,final boolean editing) {	
		cBValorFiltro = new ComboBox();
		cBValorFiltro.setWidth("404px");
		cBValorFiltro.setImmediate(true);
		cBValorFiltro.setNullSelectionAllowed(false);
		cBValorFiltro.setTextInputAllowed(false);
		
				
		if(coluna.equals("Status")){
			cBValorFiltro.addItem("ABERTO");
			cBValorFiltro.addItem("CANCELADO");
			cBValorFiltro.addItem("FECHADO");
			cBValorFiltro.addItem("PRONTO");
			cBValorFiltro.addItem("ENCAMINHADO");
			cBValorFiltro.addItem("APROVACAO");			
		}
		
		if(coluna.equals("Servico")){
			
			List<Servico> servicos = ServicoDAO.getServicos();
			
			for (Servico servico : servicos) {				
				cBValorFiltro.addItem(servico.getNome());						
			}		
		}
		if(coluna.equals("Setor")){			
			cBValorFiltro.addItem("EXPEDICAO");
			cBValorFiltro.addItem("CRIACAO");
			cBValorFiltro.addItem("ACABAMENTO");
			cBValorFiltro.addItem("IMPRESSAO");
			cBValorFiltro.addItem("PRE-IMPRESSAO");
			cBValorFiltro.addItem("QUALIDADE");
		}
		
		cBValorFiltro.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				if(cbFiltro.getValue() != null){
					if(btAdd != null){
						btAdd.setEnabled(true);
						
						String idButton = "";
						if(cbFiltro.getValue().toString().equals("Status")){
							String valor = "";
							if(cBValorFiltro.getValue().toString().equals("ABERTO")){
								valor = "A";
							}else if(cBValorFiltro.getValue().toString().equals("CANCELAMENTO")){
								valor = "C";
							}else if(cBValorFiltro.getValue().toString().equals("FECHADO")){
								valor = "F";
							}else if(cBValorFiltro.getValue().toString().equals("PRONTO")){
								valor = "P";
							}else if(cBValorFiltro.getValue().toString().equals("ENCAMINHADO")){
								valor = "E";
							}else if(cBValorFiltro.getValue().toString().equals("APROVACAO")){
								valor = "AP";
							}
							idButton = String.valueOf(listaParametros.size())+valor;
						}else{
							idButton = String.valueOf(listaParametros.size())+cBValorFiltro.getValue().toString();
						}
						
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
					if(!event.getText().isEmpty()){
						 if(container.getType(selectFiltro(coluna)).equals(Date.class)){
							
							try{
								SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyy HH:mm:ss");		
								sdf.parse(event.getText());
								
								String idButton = String.valueOf(listaParametros.size())+((TextField) event.getComponent()).getValue(); 
								btAdd.setId(idButton);
								btAdd.setEnabled(true);
							}catch(Exception e){
								
								
								btAdd.setEnabled(false);
							}
						}else{
						
							String idButton = String.valueOf(listaParametros.size())+((TextField) event.getComponent()).getValue(); 
							btAdd.setId(idButton);
							btAdd.setEnabled(true);
						}
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
						if(container.getType(selectFiltro(coluna)).equals(Date.class)){							
							
							try{
								SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyy HH:mm:ss");		
								sdf.parse(tfValorFiltro.getValue());
								
								String idButton = String.valueOf(listaParametros.size())+((TextField) event.getComponent()).getValue(); 
								btAdd.setId(idButton);
								btAdd.setEnabled(true);
							}catch(Exception e){
								
								Notify.Show("Data Fora do Padrão - dd/mm/aaaa", Notify.TYPE_ERROR);
								btAdd.setEnabled(false);
							}
						}else{
						
							String idButton = String.valueOf(listaParametros.size())+((TextField) event.getComponent()).getValue(); 
							btAdd.setId(idButton);
							btAdd.setEnabled(true);
						}
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
		cbFiltro.addItem("Cliente");
		cbFiltro.addItem("Código");				
		cbFiltro.addItem("Servico");				
		cbFiltro.addItem("Descrição");
		cbFiltro.addItem("Observação");
		cbFiltro.addItem("Data Agendado");
		cbFiltro.addItem("Data Previsão Termino");		
		cbFiltro.addItem("Data Termino");		
		cbFiltro.addItem("Operador Abertura");
		cbFiltro.addItem("Operador Produção");
		
		cbFiltro.addItem("Motivo Cancelamento");
		cbFiltro.addItem("Status");
		cbFiltro.addItem("Setor");
		cbFiltro.addItem("Comprador");
		
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
							
							
							if(coluna.equals("Servico") || coluna.equals("Setor") || coluna.equals("Status")){

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
						}else if(type == Date.class){
							cbOperador.addItem("IGUAL");
							cbOperador.addItem("DIFERENTE");
							cbOperador.addItem("MAIOR QUE");
							cbOperador.addItem("MENOR QUE");
							cbOperador.addItem("MAIOR IGUAL QUE");
							cbOperador.addItem("MENOR IGUAL QUE");
							
							cbOperador.select("IGUAL");
							
							if(hlFiltro.getComponentIndex(cBValorFiltro) > 0){
								hlFiltro.replaceComponent(cBValorFiltro, buildDfValorFiltro(v,coluna,false,coluna.equals("Data Previsão Termino") || coluna.equals("Data Termino") ? true : false));
							}else if(hlFiltro.getComponentIndex(dFValorFiltro) > 0){
								hlFiltro.replaceComponent(dFValorFiltro, buildDfValorFiltro(v,coluna,false,coluna.equals("Data Previsão Termino") || coluna.equals("Data Termino")? true : false));
							}else{
								hlFiltro.replaceComponent(tfValorFiltro, buildDfValorFiltro(v,coluna,false,coluna.equals("Data Previsão Termino") || coluna.equals("Data Termino")? true : false));								
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
		if(s.equals("Cliente")){
			filtro = "cliente.nome_razao";							
		}else if(s.equals("Código")){
			filtro = "id";					
		}else if(s.equals("Servico")){
			filtro = "servico.nome";					
		}else if(s.equals("Descrição")){
			filtro = "descricao_servico";					
		}else if(s.equals("Observação")){
			filtro = "observacao";	
		}else if(s.equals("Data Agendado")){
			filtro = "data_agendado";								
		}else if(s.equals("Data Previsão Termino")){
			filtro = "data_previsao_termino";					
		}else if(s.equals("Data Termino")){
			filtro = "data_termino";					
		}else if(s.equals("Operador Abertura")){
			filtro = "operador_abertura";		
		}else if(s.equals("Operador Produção")){
			filtro = "operador_producao";		
		}else if(s.equals("Entregar")){
			filtro = "entregar";		
		}else if(s.equals("Motivo Cancelamento")){
			filtro = "motivo_cancelamento";		
		}else if(s.equals("Status")){
			filtro = "status";		
		}else if(s.equals("Setor")){
			filtro = "setor";		
		}else if(s.equals("Comprador")){
			filtro = "comprador";		
		}
		
		return filtro;
	}
	
	public String selectHeader(String s) {
		
		String filtro = "";
		if(s.equals("cliente.nome_razao")){
			filtro = "Cliente";							
		}else if(s.equals("id")){
			filtro = "Código";					
		}else if(s.equals("servico.nome")){
			filtro = "Servico";					
		}else if(s.equals("descricao_servico")){
			filtro = "Descrição";					
		}else if(s.equals("observacao")){
			filtro = "Observação";	
		}else if(s.equals("data_agendado")){
			filtro = "Data Agendado";								
		}else if(s.equals("data_previsao_termino")){
			filtro = "Data Previsão Termino";					
		}else if(s.equals("data_termino")){
			filtro = "Data Termino";					
		}else if(s.equals("operador_abertura")){
			filtro = "Operador Abertura";		
		}else if(s.equals("operador_producao")){
			filtro = "Operador Produção";		
		}else if(s.equals("entregar")){
			filtro = "Entregar";		
		}else if(s.equals("motivo_cancelamento")){
			filtro = "Motivo Cancelamento";		
		}else if(s.equals("status")){
			filtro = "Status";		
		}else if(s.equals("setor")){
			filtro = "Setor";		
		}else if(s.equals("comprador")){
			filtro = "Comprador";		
		}
		
		return filtro;
	}
	private Button buildBtAdd(boolean editing, String id) {
		btAdd = new Button("Add",new Button.ClickListener() {
					
				@Override
				public void buttonClick(ClickEvent event) {
					hlFiltro = new HorizontalLayout();
					
					if(event.getButton().getCaption().toString().equals("Add")){
						 if(cbFiltro.getValue().toString().equals("Servico") || cbFiltro.getValue().toString().equals("Setor")|| cbFiltro.getValue().toString().equals("Status")){
							cbFiltro.setEnabled(false);
							cbOperador.setEnabled(false);
							cBValorFiltro.setEnabled(false);
														
							if(cbFiltro.getValue().toString().equals("Servico") || cbFiltro.getValue().toString().equals("Setor")){
								String idSearch = String.valueOf(listaParametros.size())+cBValorFiltro.getValue().toString();
								listaParametros.add(new SearchParameters(idSearch, selectFiltro(cbFiltro.getValue().toString()), cbOperador.getValue().toString(), cBValorFiltro.getValue().toString()));
							}else{
								String valor = "";
								if(cBValorFiltro.getValue().toString().equals("ABERTO")){
									valor = "A";
								}else if(cBValorFiltro.getValue().toString().equals("CANCELAMENTO")){
									valor = "C";
								}else if(cBValorFiltro.getValue().toString().equals("FECHADO")){
									valor = "F";
								}else if(cBValorFiltro.getValue().toString().equals("PRONTO")){
									valor = "P";
								}else if(cBValorFiltro.getValue().toString().equals("ENCAMINHADO")){
									valor = "E";
								}else if(cBValorFiltro.getValue().toString().equals("APROVACAO")){
									valor = "AP";
								}
								String idSearch = String.valueOf(listaParametros.size())+valor;
								listaParametros.add(new SearchParameters(idSearch, selectFiltro(cbFiltro.getValue().toString()), cbOperador.getValue().toString(), valor));
							}
							
							btAdd.setCaption("Remover");
							hlFiltro.addComponent(buildCbFiltro(null));
							hlFiltro.addComponent(buildCbOperador(null));
							hlFiltro.addComponent(buildTfValorFiltro(null,false));
							hlFiltro.addComponent(buildBtAdd(false, null));
							vlFiltros.addComponent(hlFiltro);		
							
							
						}else if(cbFiltro.getValue().toString().equals("Data Agendado")||cbFiltro.getValue().toString().equals("Data Previsão Termino")||cbFiltro.getValue().toString().equals("Data Termino")){
							cbFiltro.setEnabled(false);
							cbOperador.setEnabled(false);
							dFValorFiltro.setEnabled(false);
														
							String idSearch = String.valueOf(listaParametros.size())+dFValorFiltro.getValue().toString();
							
							SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
							if(cbFiltro.getValue().toString().equals("Data Previsão Termino") || cbFiltro.getValue().toString().equals("Data Termino")){
								sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
							}
							
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
					
					fireEvent(new AddFiltroOspEvent(getUI(), listaParametros));
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
					fireEvent(new RelatorioOspEvent(getUI(), true, listaParametros, cbTipoTabela.getValue().toString(), cbOrdenacao.getValue().toString(), cbOrientacao.getValue().toString(), cbResumo.getValue().toString()));		
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
						
				
					
					Notify.Show("Não é Possivel Gerar Relatório, Verifique os Campos Obrigatórios", Notify.TYPE_ERROR);
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
				fireEvent(new RelatorioOspEvent(getUI(), false, null,null,null, null, null));
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
	
	
	public void addListerner(RelatorioOspListerner target){
		try {
			Method method = RelatorioOspListerner.class.getDeclaredMethod("onClose", RelatorioOspEvent.class);
			addListener(RelatorioOspEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(RelatorioOspListerner target){
		removeListener(RelatorioOspEvent.class, target);
	}
	
	
	public void addListerner(AddFiltroOspListerner target){
		try {
			Method method = AddFiltroOspListerner.class.getDeclaredMethod("onClose", AddFiltroOspEvent.class);
			addListener(AddFiltroOspEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(AddFiltroOspListerner target){
		removeListener(AddFiltroOspEvent.class, target);
	}
	
	
	public static class AddFiltroOspEvent extends Event{

		private List<SearchParameters> filtros;
		
		
		public AddFiltroOspEvent(Component source, List<SearchParameters> filtros) {
			super(source);
			
			
			this.filtros = filtros;
		}
		
		public List<SearchParameters> getFiltros(){
			return filtros;
		}
		
		
		
	}
	public interface AddFiltroOspListerner extends Serializable{
		public void onClose(AddFiltroOspEvent event);
	}
	
	
	public static class RelatorioOspEvent extends Event{
		
		
		private boolean confirm;
		private List<SearchParameters> parametros;
		private String tipo;
		private String ordenacao;
		private String orientacao;
		private String resumo;
		
		public RelatorioOspEvent(Component source, boolean confirm, List<SearchParameters> parametros, String tipo, String ordenacao, 
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
	public interface RelatorioOspListerner extends Serializable{
		public void onClose(RelatorioOspEvent event);
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
					}
					
				}else if(sp.getOperador().equals("DIFERENTE")){
					
					if(container.getType(sp.getCampo()) == String.class){
						container.addContainerFilter(Filters.not(Filters.eq(sp.getCampo(), sp.getValor(), true)));						
						
						
					}else if(container.getType(sp.getCampo()) == Date.class){
						String date = sp.getValor();
						Date dtValor = new Date(Date.parse(date.substring(3, 6)+date.substring(0, 3)+date.substring(6,10)));
						container.addContainerFilter(Filters.not(Filters.eq(sp.getCampo(), dtValor)));
						
					}else if(container.getType(sp.getCampo()) == Integer.class){
												
						//container.addContainerFilter(new Equal(sp.getCampo(), Integer.parseInt(sp.getValor())));
						container.addContainerFilter(Filters.not(Filters.eq(sp.getCampo(), Integer.parseInt(sp.getValor()))));
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
					
//					if(container.getType(sp.getCampo()) == Date.class && !sp.getCampo().equals("data_encaminhamento")){
					if(container.getType(sp.getCampo()) == Date.class){
						
						try {
							String date = sp.getValor();
							SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyy HH:mm:ss");
							Date dtValor = sdf.parse(date);
							container.addContainerFilter(new Greater(sp.getCampo(), dtValor));
							
						}catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}					
					}
					
//					if(container.getType(sp.getCampo()) == Date.class && sp.getCampo().equals("data_encaminhamento")){
//						
//						try {
//							String date = sp.getValor();
//							SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyy HH:mm:ss");
//							Date dtValor = sdf.parse(date);
//							container.addContainerFilter(new Greater(sp.getCampo(), dtValor));
//							
//						}catch (Exception e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}					
//					}
					
					if(container.getType(sp.getCampo()) == Integer.class){
						container.addContainerFilter(new Greater(sp.getCampo(), Integer.parseInt(sp.getValor())));					
					}
					
				}else if(sp.getOperador().equals("MENOR QUE")){
					
//					if(container.getType(sp.getCampo()) == Date.class && !sp.getCampo().equals("data_encaminhamento")){
						if(container.getType(sp.getCampo()) == Date.class){
						
						try {
							String date = sp.getValor();
							SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyy");
							Date dtValor = sdf.parse(date);						
							container.addContainerFilter(new Less(sp.getCampo(), dtValor));
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
//					if(container.getType(sp.getCampo()) == Date.class && sp.getCampo().equals("data_encaminhamento")){
//						
//						try {
//							String date = sp.getValor();
//							SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyy HH:mm:ss");
//							Date dtValor = sdf.parse(date);						
//							container.addContainerFilter(new Less(sp.getCampo(), dtValor));
//						} catch (Exception e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//					}
					
					if(container.getType(sp.getCampo()) == Integer.class){
						container.addContainerFilter(new Less(sp.getCampo(), Integer.parseInt(sp.getValor())));					
					}
				}else if(sp.getOperador().equals("MAIOR IGUAL QUE")){
					if(container.getType(sp.getCampo()) == Date.class && !sp.getCampo().equals("data_encaminhamento")){
						
						try {
							String date = sp.getValor();
							SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyy HH:mm:ss");
							Date dtValor = sdf.parse(date);
							container.addContainerFilter(new GreaterOrEqual(sp.getCampo(), dtValor));
							
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}						
					}
					
					if(container.getType(sp.getCampo()) == Date.class && sp.getCampo().equals("data_encaminhamento")){
						
						try {
							String date = sp.getValor();
							SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyy HH:mm:ss");
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
				}else if(sp.getOperador().equals("MENOR IGUAL QUE")){
					
					if(container.getType(sp.getCampo()) == Date.class && !sp.getCampo().equals("data_encaminhamento")){
						
						
						try {
							String date = sp.getValor();
							SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyy HH:mm:ss");							
							Date dtValor = sdf.parse(date);
							container.addContainerFilter(new LessOrEqual(sp.getCampo(), dtValor));						
						} catch (ParseException e) {
							e.printStackTrace();
						}
						
					}
					
					if(container.getType(sp.getCampo()) == Date.class && sp.getCampo().equals("data_encaminhamento")){
						
						
						try {
							String date = sp.getValor();
							SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyy HH:mm:ss");							
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
				}
		}		
		
				
	}
}



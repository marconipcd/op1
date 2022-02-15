package com.digital.opuserp.view.modulos.relatorio.Roterizacao;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.GrupoOseDAO;
import com.digital.opuserp.dao.SubGrupoDAO;
import com.digital.opuserp.dao.TipoSubGrupoDAO;
import com.digital.opuserp.domain.GrupoOse;
import com.digital.opuserp.domain.Ose;
import com.digital.opuserp.domain.SubGrupoOse;
import com.digital.opuserp.domain.TipoSubGrupoOse;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.view.modulos.relatorio.SearchParameters;
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
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Reindeer;

public class NovoRelatorioOse extends Window {

	private String valorFiltro;
	private String valorOperador;
	private VerticalLayout vlFiltros;
	private HorizontalLayout hlFiltro;
	private ComboBox cbFiltro;
	private ComboBox cbOperador;
	private TextField tfValorFiltro;
	private HorizontalLayout hlFiltroRoot;
	private List<SearchParameters> listaParametros = new ArrayList<SearchParameters>();
	private JPAContainer<Ose> container;
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
	
	
	public NovoRelatorioOse(String title, boolean modal){
			
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
	
	
	
	public NovoRelatorioOse(String title, boolean modal,String tipo,String orientacao,String ordenacao,String resumo, List<SearchParameters> listaParametros){
		
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
	
	
	
	public JPAContainer<Ose> buildContainer(){
		container = JPAContainerFactory.makeBatchable(Ose.class, ConnUtil.getEntity());
		container.setAutoCommit(false);
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
		
		container.addNestedContainerProperty("subgrupo.nome");
		container.addNestedContainerProperty("grupo.nome");
		container.addNestedContainerProperty("tipo_subgrupo.nome");
		container.addNestedContainerProperty("cliente.id");
		container.addNestedContainerProperty("cliente.nome_razao");
		container.addNestedContainerProperty("veiculo_id.cod_veiculo");
			
		
		container.addContainerFilter(Filters.not(Filters.eq("subgrupo", null)));
		container.addContainerFilter(Filters.not(Filters.eq("grupo", null)));
		container.addContainerFilter(Filters.not(Filters.eq("tipo_subgrupo", null)));

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
				cbOrdenacao.addItem("Grupo");				
				cbOrdenacao.addItem("SubGrupo");		
				cbOrdenacao.addItem("Tipo SubGrupo");			
				cbOrdenacao.addItem("Endereço");
				cbOrdenacao.addItem("Bairro");
				cbOrdenacao.addItem("Cidade");	
				cbOrdenacao.addItem("Data Previsão");
				cbOrdenacao.addItem("Data Encaminhado");
				cbOrdenacao.addItem("Data Abertura");
				cbOrdenacao.addItem("Data Conclusão");
				cbOrdenacao.addItem("Data Fechamento");				
				cbOrdenacao.addItem("Contato");
				cbOrdenacao.addItem("Status");		
				cbOrdenacao.addItem("Operador");
				cbOrdenacao.addItem("Operador Abertura");
				cbOrdenacao.addItem("Técnico");		
				cbOrdenacao.addItem("Conclusão");
				cbOrdenacao.addItem("Serviço");
				cbOrdenacao.addItem("Problema");
				cbOrdenacao.addItem("Veiculo");		
				cbOrdenacao.addItem("Material");
				cbOrdenacao.addItem("Plano");
				cbOrdenacao.addItem("Concentrador");
				cbOrdenacao.addItem("Base");
				cbOrdenacao.addItem("Turno");
				
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
					cbResumo.addItem("Grupo");				
					cbResumo.addItem("SubGrupo");		
					cbResumo.addItem("Tipo SubGrupo");			
					cbResumo.addItem("Endereço");
					cbResumo.addItem("Bairro");
					cbResumo.addItem("Cidade");	
					cbResumo.addItem("Data Previsão");
					cbResumo.addItem("Data Encaminhado");
					cbResumo.addItem("Data Abertura");
					cbResumo.addItem("Data Fechamento");
					cbResumo.addItem("Data Conclusão");			
					cbResumo.addItem("Contato");
					cbResumo.addItem("Status");		
					cbResumo.addItem("Operador");
					cbResumo.addItem("Operador Abertura");
					cbResumo.addItem("Técnico");		
					cbResumo.addItem("Conclusão");
					cbResumo.addItem("Serviço");
					cbResumo.addItem("Problema");
					cbResumo.addItem("Veiculo");		
					cbResumo.addItem("Material");
					cbResumo.addItem("Plano");
					cbResumo.addItem("Concentrador");
					cbResumo.addItem("Base");
					cbResumo.addItem("Turno");
					cbResumo.addItem("Contrato");
					
					
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
	
							if(selectHeader(s.getCampo()).equals("Veiculo")||selectHeader(s.getCampo()).equals("Status")||selectHeader(s.getCampo()).equals("Turno")||selectHeader(s.getCampo()).equals("Grupo")||selectHeader(s.getCampo()).equals("SubGrupo")||selectHeader(s.getCampo()).equals("Tipo SubGrupo")){		
								hlFiltro.addComponent(buildcbValorFiltro(s.getValor(),selectHeader(s.getCampo()),true));							
							}else if(selectHeader(s.getCampo()).equals("Data Previsão") || selectHeader(s.getCampo()).equals("Data Encaminhado")|| selectHeader(s.getCampo()).equals("Data Abertura")|| selectHeader(s.getCampo()).equals("Data Conclusão") || selectHeader(s.getCampo()).equals("Data Fechamento") ){
								
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
	
	private PopupDateField buildDfValorFiltro(String v,String coluna, final boolean editing){
		dFValorFiltro = new PopupDateField();
		dFValorFiltro.setWidth("380px");
		dFValorFiltro.setImmediate(true);
		dFValorFiltro.addStyleName("margin-right-20");
		dFValorFiltro.setLenient(true);
		dFValorFiltro.setTextFieldEnabled(true);

		dFValorFiltro.setDateFormat("dd/MM/yyyy HH:mm:ss");		
		dFValorFiltro.setResolution(DateField.RESOLUTION_HOUR);
		dFValorFiltro.setResolution(DateField.RESOLUTION_MIN);
		
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
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"); 				
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
			cBValorFiltro.addItem("CONCLUIDO");
			cBValorFiltro.addItem("FECHADO");
			cBValorFiltro.addItem("ENCAMINHADO");
			cBValorFiltro.addItem("EM ANDAMENTO");
			
		}else if(coluna.equals("Turno")){	
			cBValorFiltro.addItem("MANHA");
			cBValorFiltro.addItem("TARDE");
			cBValorFiltro.addItem("INTEGRAL");
						
		}else if(coluna.equals("Grupo")){		
			
			List<GrupoOse> grupoOse = GrupoOseDAO.getGrupoOse();
		
			for (GrupoOse grupoOse2 : grupoOse) {
				cBValorFiltro.addItem(grupoOse2.getNome());
			}		
		}else if(coluna.equals("SubGrupo")){		
			
			List<SubGrupoOse> subGrupoOse = SubGrupoDAO.getSubGrupoOse();
			
			for (SubGrupoOse subGrupoOse2 : subGrupoOse) {
				cBValorFiltro.addItem(subGrupoOse2.getNome());
			}	
		}else if(coluna.equals("Tipo SubGrupo")){		
			
			List<TipoSubGrupoOse> tipoSubGrupoOse = TipoSubGrupoDAO.getTipoSubGrupoOse();
				
			for (TipoSubGrupoOse tipoSubGrupoOse2 : tipoSubGrupoOse) {
				cBValorFiltro.addItem(tipoSubGrupoOse2.getNome());
			}	
		}
		
		cBValorFiltro.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				if(cbFiltro.getValue() != null){
					if(btAdd != null){
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
								
								Notification.show("Data Fora do Padrão - dd/mm/aaaa", Type.ERROR_MESSAGE);
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
		cbFiltro.addItem("Grupo");				
		cbFiltro.addItem("SubGrupo");		
		cbFiltro.addItem("Tipo SubGrupo");			
		cbFiltro.addItem("Endereço");
		cbFiltro.addItem("Bairro");
		cbFiltro.addItem("Cidade");	
		cbFiltro.addItem("Data Previsão");
		cbFiltro.addItem("Data Encaminhado");
		cbFiltro.addItem("Data Abertura");
		cbFiltro.addItem("Data Conclusão");
		cbFiltro.addItem("Data Fechamento");		
		cbFiltro.addItem("Contato");
		cbFiltro.addItem("Status");		
		cbFiltro.addItem("Operador");
		cbFiltro.addItem("Operador Abertura");
		cbFiltro.addItem("Técnico");		
		cbFiltro.addItem("Conclusão");
		cbFiltro.addItem("Serviço");
		cbFiltro.addItem("Problema");
		cbFiltro.addItem("Veiculo");		
		cbFiltro.addItem("Material");
		cbFiltro.addItem("Plano");
		cbFiltro.addItem("Concentrador");
		cbFiltro.addItem("Base");
		cbFiltro.addItem("Turno");
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
							
							
							if(coluna.equals("Veiculo") || coluna.equals("Status") || coluna.equals("Turno") || coluna.equals("Grupo") || coluna.equals("SubGrupo") || coluna.equals("Tipo SubGrupo")){

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
			
		if(s.equals("Cliente")){
			filtro = "cliente.nome_razao";							
		}else if(s.equals("Código")){
			filtro = "id";					
		}else if(s.equals("SubGrupo")){
			filtro = "subgrupo.nome";					
		}else if(s.equals("Grupo")){
			filtro = "grupo.nome";					
		}else if(s.equals("Tipo SubGrupo")){
			filtro = "tipo_subgrupo.nome";				
		}else if(s.equals("Contato")){
			filtro = "contato";					
		}else if(s.equals("Status")){
			filtro = "status";					
		}else if(s.equals("Data Previsão")){
			filtro = "data_ex";	
		}else if(s.equals("Data Encaminhado")){
			filtro = "data_encaminhamento";	
		}else if(s.equals("Data Abertura")){
			filtro = "data_abertura";	
		}else if(s.equals("Data Conclusão")){
			filtro = "data_conclusao";				
		}else if(s.equals("Data Fechamento")){
			filtro = "data_fechamento";			
		}else if(s.equals("Data Inicio Tratamento")){
			filtro = "data_inicio_tratamento";					
		}else if(s.equals("Tempo Total Tratamento")){
			filtro = "tempo_total_tratamento";					
		}else if(s.equals("Motivo Reagendamento")){
			filtro = "motivo_reagendamento";					
		}else if(s.equals("Operador")){
			filtro = "operador";							
		}else if(s.equals("Conclusão")){
			filtro = "conclusao";		
		}else if(s.equals("Técnico")){
			filtro = "tecnico";		
		}else if(s.equals("Ausente")){
			filtro = "ausente";		
		}else if(s.equals("Prioridade")){
			filtro = "prioridade";		
		}else if(s.equals("Nota Fiscal")){
			filtro = "nota_fiscal";		
		}else if(s.equals("Serviço")){
			filtro = "tipo_servico";		
		}else if(s.equals("Problema")){
			filtro = "problema";		
		}else if(s.equals("Veiculo")){
			filtro = "veiculo_id.cod_veiculo";		
		}else if(s.equals("Operador Abertura")){
			filtro = "operador_abertura";		
		}else if(s.equals("Material")){
			filtro = "material";		
		}else if(s.equals("Plano")){
			filtro = "plano";		
		}else if(s.equals("Concentrador")){
			filtro = "concentrador";			
		}else if(s.equals("Base")){
			filtro = "base";	
		}else if(s.equals("Turno")){
			filtro = "turno";		
		}else if(s.equals("Endereço")){
			filtro = "endereco";				
		}else if(s.equals("Bairro")){
			filtro = "bairro";			
		}else if(s.equals("Cidade")){
			filtro = "cidade";	
		}else if(s.equals("Contrato")){
			filtro = "contrato.id";	
		}
//		}else if(s.equals("Pais")){
//			filtro = "end.pais";			
//		}else if(s.equals("Complemento")){
//			filtro = "end.complemento";			
//		}else if(s.equals("Referencia")){
//			filtro = "end.referencia";	
//		}	
				
		return filtro;
	}
	
	public String selectHeader(String s) {
		
		String filtro = "";
		
		if(s.equals("cliente.nome_razao")){
			filtro = "Cliente";							
		}else if(s.equals("id")){
			filtro = "Código";					
		}else if(s.equals("subgrupo.nome")){
			filtro = "SubGrupo";					
		}else if(s.equals("grupo.nome")){
			filtro = "Grupo";					
		}else if(s.equals("tipo_subgrupo.nome")){
			filtro = "Tipo SubGrupo";				
		}else if(s.equals("contato")){
			filtro = "Contato";					
		}else if(s.equals("status")){
			filtro = "Status";					
		}else if(s.equals("data_ex")){
			filtro = "Data Previsão";	
		}else if(s.equals("data_encaminhamento")){
			filtro = "Data Encaminhado";	
		}else if(s.equals("data_abertura")){
			filtro = "Data Abertura";	
		}else if(s.equals("data_conclusao")){
			filtro = "Data Conclusão";				
		}else if(s.equals("data_fechamento")){
			filtro = "Data Fechamento";			
		}else if(s.equals("data_inicio_tratamento")){
			filtro = "Data Inicio Tratamento";					
		}else if(s.equals("tempo_total_tratamento")){
			filtro = "Tempo Total Tratamento";					
		}else if(s.equals("motivo_reagendamento")){
			filtro = "Motivo Reagendamento";					
		}else if(s.equals("operador")){
			filtro = "Operador";							
		}else if(s.equals("conclusao")){
			filtro = "Conclusão";		
		}else if(s.equals("tecnico")){
			filtro = "Técnico";		
		}else if(s.equals("ausente")){
			filtro = "Ausente";		
		}else if(s.equals("prioridade")){
			filtro = "Prioridade";		
		}else if(s.equals("nota_fiscal")){
			filtro = "Nota Fiscal";		
		}else if(s.equals("tipo_servico")){
			filtro = "Serviço";		
		}else if(s.equals("problema")){
			filtro = "Problema";		
		}else if(s.equals("veiculo_id.cod_veiculo")){
			filtro = "Veiculo";		
		}else if(s.equals("operador_abertura")){
			filtro = "Operador Abertura";		
		}else if(s.equals("material")){
			filtro = "Material";		
		}else if(s.equals("plano")){
			filtro = "Plano";		
		}else if(s.equals("concentrador")){
			filtro = "Concentrador";			
		}else if(s.equals("base")){
			filtro = "Base";	
		}else if(s.equals("turno")){
			filtro = "Turno";				
		}else if(s.equals("end.cep")){
			filtro = "CEP";			
		}else if(s.equals("endereco")){
			filtro = "Endereço";			
		}else if(s.equals("end.numero")){
			filtro = "Número";			
		}else if(s.equals("bairro")){
			filtro = "Bairro";			
		}else if(s.equals("cidade")){
			filtro = "Cidade";			
		}else if(s.equals("end.pais")){
			filtro = "Pais";			
		}else if(s.equals("end.complemento")){
			filtro = "Complemento";			
		}else if(s.equals("end.referencia")){
			filtro = "Referencia";			
		}else if(s.equals("contrato.id")){
			filtro = "Contrato";			
		}		
		
		
		return filtro;
	}
	private Button buildBtAdd(boolean editing, String id) {
		btAdd = new Button("Add",new Button.ClickListener() {
					
				@Override
				public void buttonClick(ClickEvent event) {
					hlFiltro = new HorizontalLayout();
					
					if(event.getButton().getCaption().toString().equals("Add")){
						 if(cbFiltro.getValue().toString().equals("Veiculo") || cbFiltro.getValue().toString().equals("Status") || cbFiltro.getValue().toString().equals("Turno") || cbFiltro.getValue().toString().equals("Grupo") || cbFiltro.getValue().toString().equals("SubGrupo") || cbFiltro.getValue().toString().equals("Tipo SubGrupo")){
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
						}else if(cbFiltro.getValue().toString().equals("Data Previsão")||cbFiltro.getValue().toString().equals("Data Encaminhado")||cbFiltro.getValue().toString().equals("Data Abertura")||cbFiltro.getValue().toString().equals("Data Conclusão") ||cbFiltro.getValue().toString().equals("Data Fechamento")){
							
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
					
					fireEvent(new AddFiltroOseEvent(getUI(), listaParametros));
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
					fireEvent(new RelatorioOseEvent(getUI(), true, listaParametros, cbTipoTabela.getValue().toString(), cbOrdenacao.getValue().toString(), cbOrientacao.getValue().toString(), cbResumo.getValue().toString()));		
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
						
				
					Notification.show("Não é Possivel Gerar Relatório, Verifique os Campos Obrigatórios", Type.ERROR_MESSAGE);
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
				fireEvent(new RelatorioOseEvent(getUI(), false, null,null,null, null, null));
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
	
	
	public void addListerner(RelatorioOseListerner target){
		try {
			Method method = RelatorioOseListerner.class.getDeclaredMethod("onClose", RelatorioOseEvent.class);
			addListener(RelatorioOseEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(RelatorioOseListerner target){
		removeListener(RelatorioOseEvent.class, target);
	}
	
	
	public void addListerner(AddFiltroOseListerner target){
		try {
			Method method = AddFiltroOseListerner.class.getDeclaredMethod("onClose", AddFiltroOseEvent.class);
			addListener(AddFiltroOseEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(AddFiltroOseListerner target){
		removeListener(AddFiltroOseEvent.class, target);
	}
	
	
	public static class AddFiltroOseEvent extends Event{

		private List<SearchParameters> filtros;
		
		
		public AddFiltroOseEvent(Component source, List<SearchParameters> filtros) {
			super(source);
			
			
			this.filtros = filtros;
		}
		
		public List<SearchParameters> getFiltros(){
			return filtros;
		}
		
		
		
	}
	public interface AddFiltroOseListerner extends Serializable{
		public void onClose(AddFiltroOseEvent event);
	}
	
	
	public static class RelatorioOseEvent extends Event{
		
		
		private boolean confirm;
		private List<SearchParameters> parametros;
		private String tipo;
		private String ordenacao;
		private String orientacao;
		private String resumo;
		
		public RelatorioOseEvent(Component source, boolean confirm, List<SearchParameters> parametros, String tipo, String ordenacao, 
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
	public interface RelatorioOseListerner extends Serializable{
		public void onClose(RelatorioOseEvent event);
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
							SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyy HH:mm:ss");
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
							// TODO Auto-generated catch block
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



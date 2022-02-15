package com.digital.opuserp.view.modulos.ordemServico.roteirizacao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.joda.time.DateTime;
import org.vaadin.addons.idle.Idle;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.AlertaPendenciaDAO;
import com.digital.opuserp.dao.AlteracoesCrmDAO;
import com.digital.opuserp.dao.AlteracoesOseDAO;
import com.digital.opuserp.dao.ArquivosOseDAO;
import com.digital.opuserp.dao.ClienteDAO;
import com.digital.opuserp.dao.ContasReceberDAO;
import com.digital.opuserp.dao.CrmDAO;
import com.digital.opuserp.dao.EnderecoDAO;
import com.digital.opuserp.dao.GerenciarModuloDAO;
import com.digital.opuserp.dao.LogDAO;
import com.digital.opuserp.dao.OseDAO;
import com.digital.opuserp.domain.AlteracoesCrm;
import com.digital.opuserp.domain.AlteracoesOse;
import com.digital.opuserp.domain.ArquivosOse;
import com.digital.opuserp.domain.ArquivosOse2;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.ContasReceber;
import com.digital.opuserp.domain.Crm;
import com.digital.opuserp.domain.Endereco;
import com.digital.opuserp.domain.GrupoOse;
import com.digital.opuserp.domain.LogAcoes;
import com.digital.opuserp.domain.Ose;
import com.digital.opuserp.domain.SubGrupoOse;
import com.digital.opuserp.domain.Usuario;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.DataUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.util.Real;
import com.digital.opuserp.view.modulos.acesso.contratos_acesso.HistoricoChamados;
import com.digital.opuserp.view.modulos.ordemServico.roteirizacao.AtribuirTecnicoEditor.AtribuirTecnicoEvent;
import com.digital.opuserp.view.modulos.ordemServico.roteirizacao.FecharEditor.FecharRoteirizacaoEvent;
import com.digital.opuserp.view.modulos.ordemServico.roteirizacao.MateriaisAlocadosView.MateriaisEvent;
import com.digital.opuserp.view.modulos.ordemServico.roteirizacao.ReagendarEditor.ReagendarRoteirizacaoEvent;
import com.digital.opuserp.view.modulos.ordemServico.roteirizacao.RoteirizacaoEditor.RoteirizacaoEvent;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.converter.StringToDoubleConverter;
import com.vaadin.data.util.filter.Like;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

public class RoteirizacaoView extends VerticalLayout {
	
	JPAContainer<Ose> container;
	
	Table tb;
	TextField tfBusca;
	
	private Button btNovo;
	private Button btOcorrencia;
	private Button btAtribuir;
	//private Button btAtribuirTecnico;
	//private Button btConcluir;
	private Button btFechar;
	private Button btVisualizar;	
	private Button btReagendar;	
	private Button btDocumentos;
	private Button btExcluir;
	private Button btHistorico;
	private Button btRefresh;
	private Button btLog;
	
	HorizontalLayout hlFloat;
	
	private Label lbRegistros;
	
	ShortcutListener slNovo;
	ShortcutListener slEditar;
	
	private Integer codSubModulo;
	GerenciarModuloDAO gmDAO;
	
	ComboBox cbGrupos;
		
	public RoteirizacaoView(boolean act){		
		
		if(act){
			setSizeFull();
			gmDAO = new GerenciarModuloDAO();
			
			HorizontalLayout hlButons = new HorizontalLayout();
			hlButons.setWidth("100%");
			
			hlButons.addComponent(BuildbtRefresh());
			hlButons.setExpandRatio(btRefresh, 1.f);
			
			hlButons.addComponent(BuildbtNovo());
			hlButons.addComponent(BuildbtAtribuir());
			//hlButons.addComponent(BuildbtAtribuirTecnico());
			hlButons.addComponent(BuildbtOcorrencia());
			hlButons.addComponent(BuildbtReagendar());
			//hlButons.addComponent(BuildbtConcluir());
			hlButons.addComponent(BuildbtSubFechar());
			hlButons.addComponent(BuildbtVisualizar());
			hlButons.addComponent(BuildbtImprimir());
			hlButons.addComponent(BuildbtHistorico());
			hlButons.addComponent(BuildbtExcluir());
			//hlButons.addComponent(BuildbtMateriais());
			hlButons.addComponent(buildBtLog());
			
			
			addComponent(hlButons);
			setComponentAlignment(hlButons, Alignment.TOP_RIGHT);
			
			addComponent(new HorizontalLayout(){
				{
					setWidth("100%");
					addComponent(BuildCbStatus());
					addComponent(BuildCbGrupos());
					addComponent(buildDfPrevisao());
					addComponent(buildTfbusca());
					setExpandRatio(tfBusca, 1.0f);
				}
			});
			
			addComponent(buildTbGeneric());
			
			hlFloat = new HorizontalLayout();
			hlFloat.setWidth("100%");
			hlFloat.addComponent(buildLbRegistros());
			Label lbLegend = new Label("F2 - Novo Cadastro | ENTER - Visualizar");
			lbLegend.setWidth("285px");
			hlFloat.addComponent(lbLegend);
			hlFloat.setComponentAlignment(lbRegistros, Alignment.BOTTOM_LEFT);
			hlFloat.setComponentAlignment(lbLegend, Alignment.BOTTOM_RIGHT);
			
			
			addComponent(hlFloat);
			
			setExpandRatio(tb, 1);
			
			
			Idle.track(OpusERP4UI.getCurrent(), 300000, new Idle.Listener() {
				
	            @Override
	            public void userInactive() {
	            	if(OpusERP4UI.getCurrent().getUI().getWindows().size() == 0){
						refresh_now();
					}	            	
	            }
	
	            @Override
	            public void userActive() {
	            	
	            }
	        });
					
				
		}
	}
	
	public void refresh_now(){
		replaceComponent(tb, buildTbGeneric());
		setExpandRatio(tb, 1);		
				
		hlFloat.replaceComponent(lbRegistros, buildLbRegistros());
		
		btExcluir.setEnabled(false);
		//btFechar.setEnabled(false);
		
		btVisualizar.setEnabled(false);
		btHistorico.setEnabled(false);
		btDocumentos.setEnabled(false);
		//btEncaminhar.setEnabled(false);
		btOcorrencia.setEnabled(false);	
		btLog.setEnabled(false);	
		
		if(tfBusca.getValue() != null){
			addFilter(tfBusca.getValue());
		}
	}
	
	private ComboBox cbStatus;
	private ComboBox BuildCbStatus(){
		cbStatus = new ComboBox();
		cbStatus.setNullSelectionAllowed(false);
		cbStatus.setTextInputAllowed(false);
		cbStatus.addItem("PENDENTES");
		cbStatus.addItem("CONCLUIDAS");
		cbStatus.addItem("FECHADAS");
		cbStatus.addItem("ARQUIVADAS");
		
		cbStatus.select("PENDENTES");
		
		cbStatus.addValueChangeListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {

				if(getComponentIndex(tb) > -1){					
					replaceComponent(tb, buildTbGeneric());
					setExpandRatio(tb, 1);
				}
			//	refresh_now();
				
				if(lbRegistros != null && hlFloat != null){
					hlFloat.replaceComponent(lbRegistros, buildLbRegistros());
				}		
			}
		});
		
		return cbStatus;
	}
	
	DateField dfPrevisao;
	private DateField buildDfPrevisao(){
		
		dfPrevisao = new DateField();
		dfPrevisao.setDateFormat("dd/MM/yyyy");
		dfPrevisao.setImmediate(true);
		
		dfPrevisao.addValueChangeListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				addFilter(tfBusca.getValue());
			}
		});
		
		return dfPrevisao;
	}
	
	private ComboBox BuildCbGrupos(){
		
		BeanItemContainer<GrupoOse> grupos = new BeanItemContainer<>(GrupoOse.class);

		GrupoOse tipo2 = new GrupoOse(null,OpusERP4UI.getEmpresa().getId(), "TODOS",null,null);
		grupos.addBean(tipo2);
		for (GrupoOse tipo : getGrupos()) {
			grupos.addBean(tipo);
		}
		
		cbGrupos = new ComboBox(null, grupos);
		cbGrupos.setItemCaptionPropertyId("nome");
		cbGrupos.setNullSelectionAllowed(false);
		cbGrupos.setTextInputAllowed(false);
		cbGrupos.setImmediate(true);
		
		
		cbGrupos.addValueChangeListener(new Property.ValueChangeListener() {
			
			
			public void valueChange(ValueChangeEvent event) {
				
				if(getComponentIndex(tb) > -1){					
					replaceComponent(tb, buildTbGeneric());
					setExpandRatio(tb, 1);
				}
			//	refresh_now();
				
				if(lbRegistros != null && hlFloat != null){
					hlFloat.replaceComponent(lbRegistros, buildLbRegistros());
				}
			}
		});
		cbGrupos.select(tipo2);
				
		return cbGrupos;
		
	}
	private List<GrupoOse> getGrupos(){
		EntityManager em = ConnUtil.getEntity();
		
		Query q = em.createQuery("select t from GrupoOse t where t.empresa_id =:empresa_id", GrupoOse.class);
		q.setParameter("empresa_id", OpusERP4UI.getEmpresa().getId());
		
		return q.getResultList();		
		
	}
	
	public void refresh_qtd(){
		hlFloat.replaceComponent(lbRegistros, buildLbRegistros());
	}
	
	public void refresh(){
		replaceComponent(tb, buildTbGeneric());
		setExpandRatio(tb, 1);		
		//btFechar.setEnabled(false);
		btExcluir.setEnabled(false);
	}
	
	public void buildShortcurEvents(Component c){
		
		if(c instanceof RoteirizacaoView)
		{
			btNovo.addShortcutListener(buildShortCutNovo());
			btFechar.addShortcutListener(buildShortCutEditar());
		}else{
			if(btNovo != null || slNovo != null){				
				btNovo.removeShortcutListener(slNovo);
			}
			
			if(btFechar != null || slEditar != null){
				btFechar.removeShortcutListener(slEditar);			
			}
		}
	}
	
	public Label buildLbRegistros(){
		lbRegistros = new Label(String.valueOf(container.size()) + " Registros Encontrados");
		return lbRegistros;
	}
	
	public ShortcutListener buildShortCutNovo(){
		slNovo = new ShortcutListener("Novo",ShortcutAction.KeyCode.F2,null) {
			
			
			public void handleAction(Object sender, Object target) {
				btNovo.click();
			}
		};
		return slNovo;
	}
	public ShortcutListener buildShortCutEditar(){
		slEditar = new ShortcutListener("Editar",ShortcutAction.KeyCode.ENTER,null) {
			
			
			public void handleAction(Object sender, Object target) {
				btVisualizar.click();
			}
		};
		return slEditar;
	}
	public JPAContainer<Ose> buildContainer(){
		container = JPAContainerFactory.makeBatchable(Ose.class, ConnUtil.getEntity());
		container.setAutoCommit(false);
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
		
		if(cbGrupos.getValue() != null && !cbGrupos.getItem(cbGrupos.getValue()).getItemProperty("nome").getValue().equals("TODOS")){
			container.addContainerFilter(Filters.eq("grupo", cbGrupos.getItem(cbGrupos.getValue()).getItemProperty("id").getValue()));
		}
		
		container.addContainerFilter(Filters.not(Filters.eq("subgrupo", null)));
		container.addContainerFilter(Filters.not(Filters.eq("grupo", null)));
		container.addContainerFilter(Filters.not(Filters.eq("tipo_subgrupo", null)));
		if(cbStatus != null && cbStatus.getValue() != null){
			if(cbStatus.getValue().toString().equals("PENDENTES")){
				container.addContainerFilter(Filters.not(Filters.eq("status", "FECHADO")));
				container.addContainerFilter(Filters.not(Filters.eq("status", "CONCLUIDO")));
				container.addContainerFilter(Filters.not(Filters.eq("status", "ARQUIVADO")));
			}
			if(cbStatus.getValue().toString().equals("CONCLUIDAS")){
				container.addContainerFilter(Filters.eq("status", "CONCLUIDO"));
			}
			if(cbStatus.getValue().toString().equals("FECHADAS")){
				container.addContainerFilter(Filters.eq("status", "FECHADO"));
			}
			if(cbStatus.getValue().toString().equals("ARQUIVADAS")){
				container.addContainerFilter(Filters.eq("status", "ARQUIVADO"));
			}
		}
		
		if(dfPrevisao != null && dfPrevisao.getValue() != null){
			try{
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
				
				DateTime dt = new DateTime(dfPrevisao.getValue());
				dt.minusDays(1);
				
				container.addContainerFilter(Filters.gteq("data_ex", sdf.parse(DataUtil.formatDateBra(dt.toDate())+" 00:00:00")));
				container.addContainerFilter(Filters.lteq("data_ex", sdf.parse(DataUtil.formatDateBra(dfPrevisao.getValue())+" 23:59:00")));
			}catch(Exception e){
				e.printStackTrace();
			}
		}
						
		container.sort(new Object[]{"data_ex"}, new boolean[]{true});
		
		container.addNestedContainerProperty("cliente.nome_razao");
		container.addNestedContainerProperty("grupo.nome");
		container.addNestedContainerProperty("subgrupo.nome");
		container.addNestedContainerProperty("tipo_subgrupo.nome");
		container.addNestedContainerProperty("end.cidade");
		container.addNestedContainerProperty("end.bairro");
		container.addNestedContainerProperty("veiculo_id.cod_veiculo");
		container.addNestedContainerProperty("cto.identificacao");
		container.addNestedContainerProperty("contrato.regime");
		
		if(tfBusca != null && tfBusca.getValue() != null && !tfBusca.getValue().isEmpty() && !tfBusca.getValue().equals("")){
			String s = tfBusca.getValue();
			Object[] collums = tb.getVisibleColumns();		
			List<Filter> filtros = new ArrayList<Filter>();		
			
			try {
				Integer cod = Integer.parseInt(s);
				
				for(Object c:collums){		 			
					if(!c.toString().equals("Up") && !c.toString().equals("tempo_atendimento") && !tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == Integer.class){					   	
						filtros.add(new Like(c.toString(), "%"+cod+"%", false));
					}	
				}
				
			} catch (Exception e) {
				
				try{
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
					Date date = sdf.parse(s);
					
					for(Object c:collums){
						
						if(!c.toString().equals("Up") && !c.toString().equals("tempo_atendimento") && !tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == Date.class){					   	
							filtros.add(Filters.eq(c.toString(), date));
						}	
						
						
					}
				}catch(Exception e2){
					
					for(Object c:collums){
						//{"id","P","data_ex","turno","grupo.nome","subgrupo.nome","tipo_subgrupo.nome","cliente.nome_razao","end.cidade","end.bairro","prioridade","tecnico","veiculo_id.cod_veiculo","obs","concentrador","base","plano","material"});
						if(!c.toString().equals("Up") && !c.toString().equals("Up") && !c.toString().equals("tempo_atendimento") && !c.equals("P") && !c.equals("veiculo_id.cod_veiculo") && !c.equals("end.cidade") && !c.equals("end.bairro")
								
								
								&& !tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == String.class){					   	
							filtros.add(new Like(c.toString(), "%"+s+"%", false));
						}	
						
					}
				}

			}

			
			container.addContainerFilter(Filters.or(filtros));
		}
	
		return container;
	}

	
	public Table buildTbGeneric() {
		tb = new Table(null, buildContainer()){
			
			protected String formatPropertyValue(Object rowId, Object colId,
					Property<?> property) {
				
				Object v = property.getValue();
			
				if (v instanceof Date) {
				         Date dateValue = (Date) v;
				         
				         if(colId.equals("data_ex")){
				        	 return new SimpleDateFormat("dd/MM/yyyy HH:mm").format(dateValue);
				         }else{
				        	 return new SimpleDateFormat("dd/MM/yyyy").format(dateValue);
				         }
				}
				
				if(colId.toString().equals("turno")){
					if(v != null && v.toString() != null && !v.toString().equals("") && !v.toString().isEmpty()){
						return v.toString().substring(0, 1).toUpperCase();
					}
				}
				
				if(colId.equals("valor") && tb.getItem(rowId).getItemProperty(colId).getValue() != null){					
					return "R$ "+tb.getItem(rowId).getItemProperty(colId).getValue().toString();
				}					
				
				return super.formatPropertyValue(rowId, colId, property);
			}
		};		
		
		tb.setMultiSelect(true); 
		tb.setSizeFull();
		tb.setSelectable(true);
		tb.setColumnCollapsingAllowed(true);
		
		tb.addGeneratedColumn("Up", new Table.ColumnGenerator() {
			
			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {

				//Image img = new Image(null, new ThemeResource("icons/check-32-inative.png"));
				
				if(source.getItem(itemId).getItemProperty("arquivo_upload").getValue() != null){		
					
					if(source.getItem(itemId).getItemProperty("arquivo_upload").getValue().toString().equals("0")){
						return "";
					}else{					
						return source.getItem(itemId).getItemProperty("arquivo_upload").getValue().toString();
					}
				}else{					
					return new Label("<div style='text-align:center;width: 100%;color: red;font-size: 14px;font-weight: bold;'>!</div>",ContentMode.HTML);					
				}
				
			}
		});
		
		tb.setColumnAlignment("Up", Align.CENTER);
		
		tb.addGeneratedColumn("P", new Table.ColumnGenerator() {
			
			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				
				Label img;
				SubGrupoOse subgrupo = (SubGrupoOse)source.getItem(itemId).getItemProperty("subgrupo").getValue();
				
				if(subgrupo != null && subgrupo.getPrioridade() != null && subgrupo.getPrioridade().equals(1)){					
					img = new Label("*", ContentMode.HTML);
				}else if(subgrupo != null && subgrupo.getPrioridade() != null && subgrupo.getPrioridade().equals(2)){					
					img = new Label("**", ContentMode.HTML);
				}else if(subgrupo != null && subgrupo.getPrioridade() != null && subgrupo.getPrioridade().equals(3)){					
					img = new Label("***", ContentMode.HTML);
				}else if(subgrupo != null && subgrupo.getPrioridade() != null && subgrupo.getPrioridade().equals(4)){					
					img = new Label("****", ContentMode.HTML);
				}else{
					img = new Label("", ContentMode.HTML);
				}
				
				return img;
			}
		});
		
		tb.setColumnAlignment("Propriedade", Align.CENTER);
		tb.setColumnAlignment("turno", Align.CENTER);
		
		tb.setVisibleColumns(new Object[] {"id","Up","P","data_ex","turno","grupo.nome","subgrupo.nome","tipo_subgrupo.nome","cto.identificacao","cliente.nome_razao","end.cidade","end.bairro",
				"equipe","tecnico","veiculo_id.cod_veiculo","obs","concentrador","base","plano","material","valor","status","data_conclusao","tempo_atendimento","tempo_total_atendimento","contrato.regime"});
		
			
		tb.setColumnHeader("id", "OS");
		tb.setColumnHeader("contrato.regime", "Regime");		
		tb.setColumnHeader("data_ex", "Previsão");
		tb.setColumnHeader("turno", "Turno");
		tb.setColumnHeader("tecnico", "Técnico");
		tb.setColumnHeader("equipe", "Equipe");
		tb.setColumnHeader("cliente.nome_razao", "Nome");
		tb.setColumnHeader("obs", "Obs");
		tb.setColumnHeader("base", "Base");
		tb.setColumnHeader("material", "Material");
		tb.setColumnHeader("concentrador", "Concentrador");
		tb.setColumnHeader("plano", "Plano");
		tb.setColumnHeader("grupo.nome", "Grupo");
		tb.setColumnHeader("subgrupo.nome", "SubGrupo");
		tb.setColumnHeader("tipo_subgrupo.nome", "Tipo");
		tb.setColumnHeader("cto.identificacao", "CTO");
		tb.setColumnHeader("end.cidade", "Cidade");
		tb.setColumnHeader("end.bairro", "Bairro");
		tb.setColumnHeader("veiculo_id.cod_veiculo", "Veículo");
		tb.setColumnHeader("valor", "Valor");
		tb.setColumnHeader("status", "Status");
		tb.setColumnHeader("data_conclusao", "Conclusão");
		tb.setColumnHeader("tempo_atendimento", "Tempo atendimento");
		tb.setColumnHeader("tempo_total_atendimento", "Tempo total de atendimento");
		
		tb.setColumnCollapsed("base", true);
		tb.setColumnCollapsed("material", true);
		tb.setColumnCollapsed("concentrador", true);
		tb.setColumnCollapsed("plano", true);
		tb.setColumnCollapsed("end.cidade", true);
		tb.setColumnCollapsed("obs", true);
		tb.setColumnCollapsed("data_fechamento", true);
		tb.setColumnCollapsed("valor", false);
		tb.setColumnCollapsed("status", true);
		tb.setColumnCollapsed("data_conclusao", true);
		tb.setColumnCollapsed("tempo_total_atendimento", true);
		
		tb.setColumnAlignment("valor", Align.RIGHT);
		tb.setColumnWidth("turno", 20);
		
		tb.setConverter("id", new StringToDoubleConverter() );
		
		tb.setImmediate(true);
		tb.addValueChangeListener(new Property.ValueChangeListener() {
			
			public void valueChange(ValueChangeEvent event) {
				if(event.getProperty().getValue() != null){
					
					
					btExcluir.setEnabled(true);
					btReagendar.setEnabled(true);
					//btEncaminhar.setEnabled(false);
					//btFechar.setEnabled(false);
					btVisualizar.setEnabled(true);
					btLog.setEnabled(true);
					btDocumentos.setEnabled(true);
					btHistorico.setEnabled(true);
					btOcorrencia.setEnabled(false);
					
					final Set<Object> selecteds = (Set<Object>)tb.getValue();
					
					if(selecteds.size() > 0 && tb.getItem(selecteds.toArray()[0]).getItemProperty("status").getValue() != null && tb.getItem(selecteds.toArray()[0]).getItemProperty("status").getValue().equals("ABERTO")){						
						btAtribuir.setEnabled(true);					
						btOcorrencia.setEnabled(true);
						//btAtribuirTecnico.setEnabled(true);
					}
					
					if(selecteds.size() > 0 && tb.getItem(selecteds.toArray()[0]).getItemProperty("status").getValue() != null && tb.getItem(selecteds.toArray()[0]).getItemProperty("status").getValue().equals("EM ANDAMENTO")){						
						//btFechar.setEnabled(true);		
						btOcorrencia.setEnabled(true);
						//btAtribuirTecnico.setEnabled(true);
					}
					
					if(selecteds.size() > 0 && tb.getItem(selecteds.toArray()[0]).getItemProperty("status").getValue() != null && tb.getItem(selecteds.toArray()[0]).getItemProperty("status").getValue().equals("CONCLUIDO")){						
						//btFechar.setEnabled(true);				
						btOcorrencia.setEnabled(true);
					}
					
							
					
				}else{

					btHistorico.setEnabled(false);
					btExcluir.setEnabled(false);
					btReagendar.setEnabled(false);
					//btFechar.setEnabled(false);
					
					//btEncaminhar.setEnabled(false);
					btVisualizar.setEnabled(false);
					btOcorrencia.setEnabled(false);
					btDocumentos.setEnabled(false);
					btLog.setEnabled(false);
				}	
				tb.removeStyleName("corrige-tamanho-table");
				tb.addStyleName("corrige-tamanho-table");
			}
		});
		
		tb.setCellStyleGenerator(new Table.CellStyleGenerator() {
			
			
			public String getStyle(Table source, Object itemId, Object propertyId) {

				 if (propertyId == null){
	                    return "row-header-default"; // Will not actually be visible
				 }else{
	               
	                Item item = source.getItem(itemId);
	                
	                if(item != null && item.getItemProperty("status") != null && item.getItemProperty("status").getValue() != null){
		                String status = item.getItemProperty("status").getValue().toString();
		               
		                if(status.equals("ENCAMINHADO")){
		                	return "row-header-osi-encaminhado";    
		                }else if(status.equals("EM ANDAMENTO")){
		                	return "row-header-osi-encaminhado";    
		                }else if(status.equals("FECHADO")){
		                	return "row-header-osi-entregue";       
		                }else if(status.equals("CONCLUIDO")){
		                	 return "row-header-osi-fechado";
		                }else{
		                	try{
			                	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			                	Date data_ex = (Date)source.getItem(itemId).getItemProperty("data_ex").getValue();
			                	
			                	if(!sdf.parse(sdf.format(data_ex)).after(sdf.parse(sdf.format(new Date())))){
			                		return "row-header-atrasado";
			                	}else{		                		
			                		return "row-header-default";
			                	}
		                	}catch(Exception e){
		                		e.printStackTrace();
		                		return "row-header-default";
		                	}
		                	
		                }
	                
	                }else{
	                	 return "row-header-default";
	                }
				 }
			}
		});
				
		tb.addItemClickListener(new ItemClickEvent.ItemClickListener() {
			
			
			public void itemClick(ItemClickEvent event) {
				if(event.isDoubleClick()){					
					final Set<Object> selecteds = (Set<Object>)tb.getValue();
					
					if(selecteds.size() == 1){
						tb.select(selecteds.toArray()[0]);
						btVisualizar.click();						
					}
				}
			}
		});
				
		return tb;
	}
	

	
	public TextField buildTfbusca() {
		tfBusca = new TextField();
		tfBusca.setWidth("100%");
		tfBusca.setTextChangeEventMode(TextChangeEventMode.LAZY);
		tfBusca.focus();
		tfBusca.setInputPrompt("Buscar...");
		tfBusca.addListener(new FieldEvents.TextChangeListener() {
			
			
			public void textChange(TextChangeEvent event) {
				addFilter(event.getText());
			}
		});
		return tfBusca;
	}

	
	public void addFilter(String s) {
		container.removeAllContainerFilters();
		
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
		
		
		if(cbGrupos.getValue() != null && !cbGrupos.getItem(cbGrupos.getValue()).getItemProperty("nome").getValue().equals("TODOS")){
			container.addContainerFilter(Filters.eq("grupo", cbGrupos.getItem(cbGrupos.getValue()).getItemProperty("id").getValue()));
		}
		container.addContainerFilter(Filters.not(Filters.eq("subgrupo", null)));
		container.addContainerFilter(Filters.not(Filters.eq("grupo", null)));
		container.addContainerFilter(Filters.not(Filters.eq("tipo_subgrupo", null)));
		
		if(cbStatus != null && cbStatus.getValue() != null){
			if(cbStatus.getValue().toString().equals("PENDENTES")){
				container.addContainerFilter(Filters.not(Filters.eq("status", "FECHADO")));
				container.addContainerFilter(Filters.not(Filters.eq("status", "CONCLUIDO")));
				container.addContainerFilter(Filters.not(Filters.eq("status", "ARQUIVADO")));
			}
			if(cbStatus.getValue().toString().equals("CONCLUIDAS")){
				container.addContainerFilter(Filters.eq("status", "CONCLUIDO"));
			}
			if(cbStatus.getValue().toString().equals("FECHADAS")){
				container.addContainerFilter(Filters.eq("status", "FECHADO"));
			}
			if(cbStatus.getValue().toString().equals("ARQUIVADAS")){
				container.addContainerFilter(Filters.eq("status", "ARQUIVADO"));
			}
		}
		
		if(dfPrevisao.getValue() != null){
			try{
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
				
				DateTime dt = new DateTime(dfPrevisao.getValue());
				dt.minusDays(1);
				
				container.addContainerFilter(Filters.gteq("data_ex", sdf.parse(DataUtil.formatDateBra(dt.toDate())+" 00:00:00")));
				container.addContainerFilter(Filters.lteq("data_ex", sdf.parse(DataUtil.formatDateBra(dfPrevisao.getValue())+" 23:59:00")));
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		Object[] collums = tb.getVisibleColumns();		
		List<Filter> filtros = new ArrayList<Filter>();		
		
		try {
			Integer cod = Integer.parseInt(s);
			
			for(Object c:collums){		 			
				if(!c.toString().equals("Up") && !c.toString().equals("contrato.regime") && !c.toString().equals("cto.identificacao") && !c.toString().equals("tempo_total_atendimento") && !c.toString().equals("tempo_atendimento") &&  !tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == Integer.class ){					   	
					filtros.add(new Like(c.toString(), "%"+cod+"%", false));
				}	
			}
			
		} catch (Exception e) {
			
			try{
				
				for(Object c:collums){
					
					if(!c.toString().equals("Up") &&!c.toString().equals("contrato.regime") && !c.toString().equals("cto.identificacao") && !c.toString().equals("tempo_total_atendimento") && !c.toString().equals("tempo_atendimento") && !tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == Date.class ){	
						SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
						filtros.add(Filters.eq(c.toString(), sdf.parse(s+":00")));
					}	
					
					
				}
			}catch(Exception e2){
				
				for(Object c:collums){
					if(!c.toString().equals("Up") &&!c.toString().equals("contrato.regime") && !c.toString().equals("cto.identificacao") && !c.toString().equals("Up") && !c.toString().equals("tempo_total_atendimento") && !c.toString().equals("tempo_atendimento") && !c.equals("P") && !c.equals("veiculo_id.cod_veiculo") && !c.equals("end.cidade") && !c.equals("end.bairro")						
							&& !tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == String.class  ){					   	
						filtros.add(new Like(c.toString(), "%"+s+"%", false));
					}						
				}
			}

		}

		
		container.addContainerFilter(Filters.or(filtros));
		container.sort(new Object[]{"data_ex"}, new boolean[]{true});

		container.applyFilters();
		
		hlFloat.replaceComponent(lbRegistros, buildLbRegistros());
	}
	
	Window winSubDocumentos;
	private void buildSubDocumentos(ClickEvent event) {
		winSubDocumentos= new Window("Escolha uma das Opções:");
        VerticalLayout l = new VerticalLayout();
        //l.setMargin(true); 
        //l.setSpacing(true);
        winSubDocumentos.setContent(l);
        winSubDocumentos.setWidth("300px");
        winSubDocumentos.addStyleName("notifications");
        winSubDocumentos.setClosable(false);
        winSubDocumentos.setResizable(false);
        winSubDocumentos.setDraggable(false);
        winSubDocumentos.setPositionX(event.getClientX() - event.getRelativeX());
        winSubDocumentos.setPositionY(event.getClientY() - event.getRelativeY());
        winSubDocumentos.setCloseShortcut(KeyCode.ESCAPE, null);
        
        Button btSubImprimir = new Button("Imprimir Ordem de Serviço", new Button.ClickListener() {
			
			public void buttonClick(ClickEvent event) {
						if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Visualizar Protocolo"))				
						{
						
							final Set<Object> selecteds = (Set<Object>)tb.getValue();
							
							if(selecteds.size() == 1){
							
								EntityItem<Ose> eiOse =(EntityItem<Ose>)tb.getItem(selecteds.toArray()[0]);
								Integer cod  = Integer.parseInt(tb.getItem(selecteds.toArray()[0]).getItemProperty("id").getValue().toString());
								
								try {
									//-----INSTANCIA UMA NOVA JANELA E ADICIONA SUAS PROPRIEDADES
									Window win = new Window("Protocolo de Roterização");
									win.setWidth("800px");
									win.setHeight("600px");
									win.setResizable(true);
									win.center();
									win.setModal(true);
									win.setStyleName("disable_scroolbar");
									
									StreamResource resource;
									//resource = new StreamResource(new ExportProtocolo(cod), "ORDEM DE SERVICO "+String.valueOf(cod)+".pdf");
									resource = new StreamResource(new OrdemServicoForm(cod), "OS "+eiOse.getEntity().getGrupo().getNome()+" "+String.valueOf(cod)+"-"+eiOse.getEntity().getCliente().getNome_razao()+".pdf");
									resource.getStream();
									resource.setMIMEType("application/pdf");
									resource.setCacheTime(0);
									
									Embedded e = new Embedded();
									e.setSizeFull();
									e.setType(Embedded.TYPE_BROWSER);
									e.setSource(resource);
									
									win.setContent(e);
									getUI().addWindow(win);								
									
									win.addCloseListener(new Window.CloseListener() {
																			
										public void windowClose(CloseEvent e) {
											tb.focus();
										}
									});
							
								} catch (Exception e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							}
					}else{
						Notify.Show("Você não Possui Permissão para Visualizar Protocolo OS",Notify.TYPE_ERROR);
					}				
			}
		});       
        btSubImprimir.setPrimaryStyleName("btSubMenu");
        
        Button btSubDocumentos = new Button("Todos os documentos", new Button.ClickListener() {
			
			public void buttonClick(ClickEvent event) {
				
				final Set<Object> selecteds = (Set<Object>)tb.getValue();
				
				if(selecteds.size() == 1){
					
					EntityItem<Ose> eiArquivos = (EntityItem<Ose>)tb.getItem(selecteds.toArray()[0]);
										
					ArquivosOseUpload au = new ArquivosOseUpload("Documentos", true, eiArquivos.getEntity(),codSubModulo);
					au.addCloseListener(new Window.CloseListener() {
						
						@Override
						public void windowClose(CloseEvent e) {
							
						}
					});
					
					au.addCloseListener(new Window.CloseListener() {
						
						@Override
						public void windowClose(CloseEvent e) {
							refresh();
						}
					});
					
					getUI().addWindow(au); 
				}
			}
		});      
		btSubDocumentos.setPrimaryStyleName("btSubMenu");
		                          
        l.addComponent(btSubImprimir);
        l.addComponent(btSubDocumentos);        
	}
	
	
	Window winSubAtribuir;
	private void buildSubAtribuir(ClickEvent event) {
		winSubAtribuir = new Window("Escolha uma das Opções:");
        VerticalLayout l = new VerticalLayout();
        //l.setMargin(true); 
        //l.setSpacing(true);
        winSubAtribuir.setContent(l);
        winSubAtribuir.setWidth("300px");
        winSubAtribuir.addStyleName("notifications");
        winSubAtribuir.setClosable(false);
        winSubAtribuir.setResizable(false);
        winSubAtribuir.setDraggable(false);
        winSubAtribuir.setPositionX(event.getClientX() - event.getRelativeX());
        winSubAtribuir.setPositionY(event.getClientY() - event.getRelativeY());
        winSubAtribuir.setCloseShortcut(KeyCode.ESCAPE, null);
        
        Button btSubEncaminhar = new Button("Encaminhar", new Button.ClickListener() {
			
			
			public void buttonClick(ClickEvent event) {
				closeAllWindows();
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Encaminhar"))				
				{
					
					//Implementar Confirmação
					final Set<Object> selecteds = (Set<Object>)tb.getValue();
					
					boolean veiculoAtribuido = false;

					if(selecteds.size() == 1){
						if(tb.getValue() != null && tb.getItem(selecteds.toArray()[0]).getItemProperty("veiculo_id").getValue() != null){
							veiculoAtribuido = true;
						}else{
							Notify.Show("Não é possível Encaminhar uma Os antes de atribuir um Técnico e um Veículo !", Notify.TYPE_ERROR);
						}
					}
					
					if(selecteds.size() == 1 && veiculoAtribuido){

					
						Ose ose = OseDAO.find((Integer)tb.getItem(selecteds.toArray()[0]).getItemProperty("id").getValue());
							
						ose.setData_encaminhamento(new Date());
						ose.setStatus("EM ANDAMENTO");								
						
						OseDAO.save(ose);
						
						AlteracoesOseDAO.add(new AlteracoesOse(null, "ENCAMINHADA para "+ose.getTecnico(), ose, OpusERP4UI.getUsuarioLogadoUI(), new Date()));
						AlertaPendenciaDAO.removePendencia(codSubModulo, ose.getId());
						
						refresh();						
						Notify.Show("OS Encaminhada com Sucesso!", Notify.TYPE_SUCCESS);
						
					}
				}else{
					Notify.Show("Você não Possui Permissão para Encaminhar OS",Notify.TYPE_ERROR);
				}
			}
		});
        btSubEncaminhar.setEnabled(false);
        btSubEncaminhar.setPrimaryStyleName("btSubMenu");
        
        Button btSubAtribuirTecnico = new Button("Atribuir Técnico", new Button.ClickListener() {
			
			
			public void buttonClick(ClickEvent event) {
				closeAllWindows();
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Atribuir Técnico"))				
				{
					
					final Set<Object> selecteds = (Set<Object>)tb.getValue();
					
					if(selecteds.size() > 0){
												
						
						final AtribuirTecnicoEditor atribuirTecnico = new AtribuirTecnicoEditor("Atribuir Técnico", true);
						atribuirTecnico.addListerner(new AtribuirTecnicoEditor.AtribuirTecnicoListerner() {
							
							@Override
							public void onClose(AtribuirTecnicoEvent event) {
								
								if(event.isConfirm()){
									Usuario u = event.getUsuario();
									Usuario uAux = event.getUaux();
									
									for (Object itemId: selecteds) {
										Integer cod_os = (Integer)tb.getItem(itemId).getItemProperty("id").getValue();
									
										boolean check = OseDAO.atribuirTecnico(u,uAux, cod_os,event.getVeiculo(),event.getEquipe(), event.getAtribuicao());
										
										if(check){
											refresh();
											Notify.Show("Os atribuida com sucesso!", Notify.TYPE_SUCCESS);
										}
									}									
								}
								
								atribuirTecnico.close();
							}
						});
							
						
						getUI().addWindow(atribuirTecnico); 
					}
				}else{
					Notify.Show("Você não Possui Permissão para Encaminhar OS",Notify.TYPE_ERROR);
				}
			}
		});
        btSubAtribuirTecnico.setEnabled(false);
		btSubAtribuirTecnico.setPrimaryStyleName("btSubMenu");
		
	
        
        final Set<Object> selecteds = (Set<Object>)tb.getValue();
        
        if(selecteds.size() > 0 && tb.getItem(selecteds.toArray()[0]).getItemProperty("status").getValue() != null && tb.getItem(selecteds.toArray()[0]).getItemProperty("status").getValue().equals("ABERTO")){						
        	btSubEncaminhar.setEnabled(true);		
        	btSubAtribuirTecnico.setEnabled(true);
		}
        
        if(selecteds.size() > 0 && tb.getItem(selecteds.toArray()[0]).getItemProperty("status").getValue() != null && tb.getItem(selecteds.toArray()[0]).getItemProperty("status").getValue().equals("EM ANDAMENTO")){						
			btSubAtribuirTecnico.setEnabled(true);
		}
               
        
            
        l.addComponent(btSubAtribuirTecnico);        
        l.addComponent(btSubEncaminhar);
	}
	
	Window winSubFechar;
	private void buildSubFechar(ClickEvent event) {
		winSubFechar = new Window("Escolha uma das Opções:");
        VerticalLayout l = new VerticalLayout();
        //l.setMargin(true);
        //l.setSpacing(true);
        winSubFechar.setContent(l);
        winSubFechar.setWidth("300px");
        winSubFechar.addStyleName("notifications");
        winSubFechar.setClosable(false);
        winSubFechar.setResizable(false);
        winSubFechar.setDraggable(false);
        winSubFechar.setPositionX(event.getClientX() - event.getRelativeX());
        winSubFechar.setPositionY(event.getClientY() - event.getRelativeY());
        winSubFechar.setCloseShortcut(KeyCode.ESCAPE, null);
        
        Button btSubConcluir = new Button("Concluir", new Button.ClickListener() {
			
			
			public void buttonClick(ClickEvent event) {
				closeAllWindows();
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Concluir"))				
				{
					final Set<Object> selecteds = (Set<Object>)tb.getValue();
					
					if(selecteds.size() == 1){

						GenericDialog gd = new GenericDialog("Confirme para Contitnuar", "Deseja Realmente Concluir ?", true, true);
						gd.addListerner(new GenericDialog.DialogListerner() {
							
							@Override
							public void onClose(DialogEvent event) {
								if(event.isConfirm()){
									
									
									EntityItem<Ose> entityItem = (EntityItem<Ose>)tb.getItem(selecteds.toArray()[0]);
									Ose ose = entityItem.getEntity();								
									ose.setStatus("CONCLUIDO");								
									ose.setOperador(OpusERP4UI.getUsuarioLogadoUI().getUsername());		
									ose.setData_conclusao(new Date());							
									
									OseDAO.save(ose);
									
									AlertaPendenciaDAO.removePendencia(codSubModulo, ose.getId());	
									AlteracoesOseDAO.add(new AlteracoesOse(null, "CONCLUIDA", ose, OpusERP4UI.getUsuarioLogadoUI(), new Date()));
									refresh();
									
									Notify.Show("OS CONCLUÍDA com Sucesso!", Notify.TYPE_SUCCESS);
									
									
									refresh_qtd();
								}
							}
						});
						getUI().addWindow(gd);
					
					}
				
				}else{				
					Notify.Show("Você não Possui Permissão para Fechar OS",Notify.TYPE_ERROR);				
				}
				
			}
		});		
        btSubConcluir.setEnabled(false);
        btSubConcluir.setPrimaryStyleName("btSubMenu");
        
        Button btSubFechar = new Button("Fechar", new Button.ClickListener() {
			
			
			public void buttonClick(ClickEvent event) {
				closeAllWindows();
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Fechar"))				
				{
					final Set<Object> selecteds = (Set<Object>)tb.getValue();
					
					if(selecteds.size() == 1){

						Ose ose = OseDAO.find((Integer)tb.getItem(selecteds.toArray()[0]).getItemProperty("id").getValue());
						
						final FecharEditor fecharEditor = new FecharEditor("Fechar OS", true, ose);
						fecharEditor.addListerner(new FecharEditor.FecharRoteirizacaoListerner() {
							
							
							public void onClose(FecharRoteirizacaoEvent event) {
								if(event.isConfirm()){
									Ose ose = OseDAO.find((Integer)tb.getItem(selecteds.toArray()[0]).getItemProperty("id").getValue());
									ose.setConclusao(event.getConclusao());
									ose.setProblema(event.getProblema());
									ose.setStatus("FECHADO");
									ose.setData_fechamento(new Date());
									ose.setOperador(OpusERP4UI.getUsuarioLogadoUI().getUsername());
									
									OseDAO.save(ose);				
									if(ose.getSubgrupo().getGerar_crm() != null && ose.getSubgrupo().getGerar_crm().equals("SIM") && ose.getCliente().getAgendar_crm().equals("SIM")){
										Crm crm = CrmDAO.saveCrm(new Crm(null, OpusERP4UI.getEmpresa().getId(), ose.getSubgrupo().getSetor(), ose.getCliente(), ose.getSubgrupo().getCrm_assunto(), ose.getSubgrupo().getCrm_forma_contato(), ose.getCliente().getContato(), "ROTINA", 
												ose.getSubgrupo().getCrm_assunto().getConteudo()+"\n\nOS Nº: "+ose.getId().toString()+"\nOBSERVAÇÃO: "+ose.getObs()+" \nCONCLUSÃO: "+ose.getConclusao(), 
												new DateTime().plusDays(1).toDate(), null, new Date(), null, "AGENDADO", "OpusERP4", ose,"NIVEL I"));
										Usuario opus = ConnUtil.getEntity().find(Usuario.class, 100);									 
										AlteracoesCrmDAO.save(new AlteracoesCrm(null, "CADASTRO DE CRM", crm, opus, new Date()));
									}
									
									//Vincular Produto com OS
									OseDAO.vincularProduto(ose,event.getItens());
																	
									AlteracoesOseDAO.add(new AlteracoesOse(null, "FECHADA", ose, OpusERP4UI.getUsuarioLogadoUI(), new Date()));
									
									refresh();
									
									Notify.Show("OS FECHADA com Sucesso!", Notify.TYPE_SUCCESS);
									fecharEditor.close();
									
									refresh_qtd();
								}						
							}
						});
						
						fecharEditor.addCloseListener(new Window.CloseListener() {
							
							
							public void windowClose(CloseEvent e) {
								tb.focus();
							}
						});
						
						getUI().addWindow(fecharEditor);
					}
				}else{				
					Notify.Show("Você não Possui Permissão para Fechar OS",Notify.TYPE_ERROR);				
				}
				
			}
		});		
		btSubFechar.setEnabled(false);
		btSubFechar.setPrimaryStyleName("btSubMenu");
		
		Button btSubArquivar = new Button("Arquivar", new Button.ClickListener() {
			
			
			public void buttonClick(ClickEvent event) {
				closeAllWindows();
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), 
						OpusERP4UI.getUsuarioLogadoUI().getId(), "Arquivar"))				
				{
					
					final Set<Object> selecteds = (Set<Object>)tb.getValue();			
					
					boolean tem_arquivos = false;
					if(selecteds.size() == 1){
						Ose os_selecionada = null;
						EntityItem<Ose> eiOse = (EntityItem<Ose>)tb.getItem(selecteds.toArray()[0]);
						os_selecionada = eiOse.getEntity();
						
						if(os_selecionada != null){						
							List<ArquivosOse2> arquivos = ArquivosOseDAO.listarArquivos2(os_selecionada);
							
							if(arquivos != null && arquivos.size() > 0){
								tem_arquivos = true;
							}
						}						
					}
					
					
					
					if(tem_arquivos){
						
							GenericDialog gd = new GenericDialog("Confirme para continuar", "Quer realmente arquivar está os?", true, true);
							gd.addListerner(new GenericDialog.DialogListerner() {
								
								@Override
								public void onClose(DialogEvent event) {
									if(event.isConfirm()){
										if(selecteds.size() == 1){
										
											Ose ose = OseDAO.find((Integer)tb.getItem(selecteds.toArray()[0]).getItemProperty("id").getValue());					
											ose.setStatus("ARQUIVADO");
																							
											OseDAO.save(ose);		
											
											refresh();
											
											Notify.Show("OS Arquivada com Sucesso!", Notify.TYPE_SUCCESS);
											
											AlteracoesOseDAO.add(new AlteracoesOse(null, "ARQUIVADA", ose, OpusERP4UI.getUsuarioLogadoUI(), new Date()));
											
											
											refresh_qtd();
										}
									}
								}
							});
							
							getUI().addWindow(gd);
					
					}else{
						Notify.Show("É Necessário enviar arquivos antes de arquivar esta Os!",Notify.TYPE_ERROR);	
					}
					
					
				}else{				
					Notify.Show("Você não Possui Permissão para Arquivar OS",Notify.TYPE_ERROR);				
				}
				
			}
		});		
		btSubArquivar.setEnabled(false);
		btSubArquivar.setPrimaryStyleName("btSubMenu");
                      
        final Set<Object> selecteds = (Set<Object>)tb.getValue();
        
        if(selecteds.size() > 0 && tb.getItem(selecteds.toArray()[0]).getItemProperty("status").getValue() != null && tb.getItem(selecteds.toArray()[0]).getItemProperty("status").getValue().equals("EM ANDAMENTO")){
        	btSubConcluir.setEnabled(true); 
        }
        if(selecteds.size() > 0 && tb.getItem(selecteds.toArray()[0]).getItemProperty("status").getValue() != null && tb.getItem(selecteds.toArray()[0]).getItemProperty("status").getValue().equals("CONCLUIDO")){						
        	btSubFechar.setEnabled(true);										
		}
        if(selecteds.size() > 0 && tb.getItem(selecteds.toArray()[0]).getItemProperty("status").getValue() != null && tb.getItem(selecteds.toArray()[0]).getItemProperty("status").getValue().equals("FECHADO")){						
        	btSubArquivar.setEnabled(true);										
		}
             
        l.addComponent(btSubConcluir);
        l.addComponent(btSubFechar);
        l.addComponent(btSubArquivar);     
	}

	private void closeAllWindows(){
		if(winSubAtribuir != null){ winSubAtribuir.close();}
		if(winSubFechar != null){winSubFechar.close();}
		//if(winSubMenuNovo != null){winSubMenuNovo.close();}
	}
	
	private Component BuildbtAtribuir() {
		btAtribuir = new Button("Atribuir", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				 if (winSubAtribuir != null && winSubAtribuir.getUI() != null)
					 winSubAtribuir.close();
	             else {
	            	 
	            	 if(winSubRelatorio != null && winSubRelatorio.getUI() != null){
	            		 winSubRelatorio.close();
	            	 }
	            	 
	            	 if(winSubMateriais != null && winSubMateriais.getUI() != null){
	            		 winSubMateriais.close();
	            	 }
	            	 
	            	 if(winSubAtribuir != null && winSubAtribuir.getUI() != null){
	            		 winSubAtribuir.close();
	            	 }
	            	 
	            	 if(winSubFechar != null && winSubFechar.getUI() != null){
	            		 winSubFechar.close();
	            	 }
	            	 
	            	 if(winSubDocumentos != null && winSubDocumentos.getUI() != null){
	            		 winSubDocumentos.close();
	            	 }
	            	 
	            	 
				     buildSubAtribuir(event);
				     
					 getUI().addWindow(winSubAtribuir);
					 winSubAtribuir.focus();
	                 ((CssLayout) getUI().getContent()).addLayoutClickListener(new LayoutClickListener() {
	                             
	               			@Override
	                        public void layoutClick(LayoutClickEvent event) {
	               				winSubAtribuir.close();
	                            ((CssLayout) getUI().getContent()).removeLayoutClickListener(this);
	                        }
	                 });
	             }					
			}
		});
		btAtribuir.setEnabled(false);
		return btAtribuir;
	}
	
	private Component BuildbtSubFechar() {
		btFechar = new Button("Status", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				 if (winSubFechar != null && winSubFechar.getUI() != null)
					 winSubFechar.close();
	             else {
	            	 
	            	 if(winSubRelatorio != null && winSubRelatorio.getUI() != null){
	            		 winSubRelatorio.close();
	            	 }
	            	 
	            	 if(winSubMateriais != null && winSubMateriais.getUI() != null){
	            		 winSubMateriais.close();
	            	 }
	            	 
	            	 if(winSubFechar != null && winSubFechar.getUI() != null){
	            		 winSubFechar.close();
	            	 }
	            	 
	            	 if(winSubAtribuir != null && winSubAtribuir.getUI() != null){
	            		 winSubAtribuir.close();
	            	 }
	            	 
	            	 
				     buildSubFechar(event);
				     
					 getUI().addWindow(winSubFechar);
					 winSubFechar.focus();
	                 ((CssLayout) getUI().getContent()).addLayoutClickListener(new LayoutClickListener() {
	                             
	               			@Override
	                        public void layoutClick(LayoutClickEvent event) {
	               				winSubFechar.close();
	                            ((CssLayout) getUI().getContent()).removeLayoutClickListener(this);
	                        }
	                 });
	             }					
			}
		});
		btFechar.setEnabled(true);
		return btFechar;
	}
	
	
	
	public Button BuildbtNovo() {
		btNovo = new Button("Novo", new Button.ClickListener() {
			
			
			public void buttonClick(ClickEvent event) {
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Cadastrar"))				
				{
				
					final BeanItem<Ose> item = new BeanItem<Ose>(new Ose());
					
					final RoteirizacaoEditor roteirizacaoEditor = new RoteirizacaoEditor(item, "Abrir Novo Chamado", true);
					roteirizacaoEditor.addListerner(new RoteirizacaoEditor.RoteirizacaoListerner() {
						
						
						public void onClose(RoteirizacaoEvent event) {
							if(event.isConfirm()){
								
								try{
									
									Ose ose = item.getBean();									
									ose.setValor(Real.formatDbToString(String.valueOf(ose.getTipo_subgrupo().getValor())));
									OseDAO.save(ose);
																		
									//Atualiza Cliente
									Cliente cliente = (Cliente)item.getItemProperty("cliente").getValue();								
									ClienteDAO.saveCliente(cliente);
									
									//Atualiza Endereço
									EnderecoDAO.save((Endereco)item.getItemProperty("end").getValue());									
									
									String valor = Real.formatDbToString(String.valueOf(ose.getTipo_subgrupo().getValor()));									
										
									if(ose.getContrato() != null && ose.getContrato().getInstalacao_gratis() != null && ose.getContrato().getInstalacao_gratis().equals("SIM") && ose.getGrupo().getNome().equals("INSTALACAO")){
										valor = "0,00";
									}
									
									
									if(!valor.equals("0,00") && !valor.equals("null") && cliente != null && ose != null 
											&& ose.getData_ex() != null){
										ConnUtil.getEntity().getTransaction().begin();
										SimpleDateFormat sdf = new  SimpleDateFormat("yy");
										Date dataAtual = new Date();
										
										ContasReceber c = null;
										if(ose.getContrato() != null && ose.getContrato().getId() != null){
																			
											//while(c == null){
												try{
													
													Integer qtd = 1;
													
													if(event.getQtd() != null){
														qtd = Integer.parseInt(event.getQtd());
													}
													
													for (int i = 0; i < qtd; i++) {
														String vlr =Real.formatDbToString(String.valueOf(Real.formatStringToDBDouble(valor) / qtd));
														Integer l = i+1;
														ContasReceberDAO.gerarBoletos(new ContasReceber(cliente,ose.getContrato().getId()+"-"+"OS"+ose.getId().toString()+"-"+String.format("%01d", l)+"/"+qtd,vlr, new Date(), ose.getVencimento(), "ABERTO", "BOLETO","SERVICO"));
													}
													
												}catch(Exception e){
													System.out.println("Erro ao Tentar Gerar Boleto de Roteirização!");
													e.printStackTrace();
												}
											//}
																				
										}else{
											
											//while(c == null){
												try{
													c = ContasReceberDAO.gerarBoletos(new ContasReceber(cliente,"OS"+ose.getId().toString(),valor, new Date(), ose.getVencimento(), "ABERTO", "BOLETO","SERVICO"));
												}catch(Exception e){
													System.out.println("Erro ao Tentar Gerar Boleto de Roteirização!");
													e.printStackTrace();
													
												}
											//}
										}
										ConnUtil.getEntity().getTransaction().commit();;
										
										
										if(c != null){
											SimpleDateFormat sdfData = new SimpleDateFormat("dd/MM/yyyy");
											Notification.show("Título Gerado", "Código: "+c.getId().toString()+"\n"+
																			   "Nº Documento: "+c.getN_doc()+"\n"+
													                           "Vencimento: "+sdfData.format(c.getData_vencimento()),Type.ERROR_MESSAGE);
										}
									}
									
									AlteracoesOseDAO.add(new AlteracoesOse(null, "ABERTA", new Ose((Integer)event.getItem().getItemProperty("id").getValue()), OpusERP4UI.getUsuarioLogadoUI(), new Date()));
									
									roteirizacaoEditor.close();
									Notify.Show("Os Aberta com Sucesso!", Notify.TYPE_SUCCESS);
									
									
                                    Endereco end = (Endereco)item.getItemProperty("end").getValue();								
									
//									String rBusca = ConteleUtil.getPoi(cliente.getId().toString());
//									Gson gson = new GsonBuilder().create();
//									contelePois poi = gson.fromJson(rBusca, contelePois.class);
																		
									//System.out.println(poi.getPois().get(0).getId());					
																		
//									if(poi != null && poi.getPois() != null && poi.getPois().size() == 0){
//										
////										//Cadastra Cliente no Contele
//										String rCadastro = ConteleUtil.postPoi(
//												cliente.getId().toString(), //CustomID
//												cliente.getNome_razao(), 
//												cliente.getNome_fantasia() != null && cliente.getNome_fantasia() != "" ? cliente.getNome_fantasia() : "", 
//														cliente.getDoc_cpf_cnpj(), 
//														"positioned", 
//														end.getBairro(), 
//														end.getEndereco(), 
//														end.getUf(), 
//														end.getCep(), 
//														end.getNumero(), 
//														end.getComplemento(), 
//														end.getCidade(), 
//														cliente.getDdd_fone1()+cliente.getTelefone1(), 
//														cliente.getEmail() != null && cliente.getEmail() != "" ? cliente.getEmail() : "");
//										
//										System.out.println(rCadastro);
//									
//										rBusca = ConteleUtil.getPoi(cliente.getId().toString());									
//										poi = gson.fromJson(rBusca, contelePois.class);
//										
//									}
								
									
//									if(poi != null && poi.getPois() != null){
//									
//										//Cadastra Tarefa
//										SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//										String datetime = sdf.format(ose.getData_ex());
//																			
//										String rTask = ConteleUtil.postTask(poi.getPois().get(0).getCustomId(), poi.getPois().get(0).getId(), datetime, "America/Recife",ose.getId().toString());
//										System.out.println(rTask);
//									}
									
									refresh();
								}catch(Exception e){
									e.printStackTrace();
									Notify.Show(e.getMessage(), Notify.TYPE_ERROR);
								}
							}
						}
					});
					
					roteirizacaoEditor.addCloseListener(new Window.CloseListener() {
						
						
						public void windowClose(CloseEvent e) {
							tb.focus();
						}
					});
					
					getUI().addWindow(roteirizacaoEditor);		
				}else{
					Notify.Show("Você não Possui Permissão para Cadastrar OS",Notify.TYPE_ERROR);
				}
			}
		});

		return btNovo;
	}

	public Button BuildbtOcorrencia(){
		btOcorrencia = new Button("Ocorrência", new Button.ClickListener() {
			
			
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Ocorrencia"))				
				{
				
					final Set<Object> selecteds = (Set<Object>)tb.getValue();
					
					if(selecteds.size() == 1){
					
						Integer cod_tabela = Integer.parseInt(tb.getItem(selecteds.toArray()[0]).getItemProperty("id").getValue().toString());
						final InformarOcorrenciaEditor informarOcorrenciaEditor = new InformarOcorrenciaEditor(tb.getItem(selecteds.toArray()[0]), "Ocorrência", true,cod_tabela);
						
						
						getUI().addWindow(informarOcorrenciaEditor);
						
					}
				}else{
					Notify.Show("Você não Possui Permissão para Cadastrar Ocorrência",Notify.TYPE_ERROR);
				}
			}
		});
		
		btOcorrencia.setEnabled(false);		
		return btOcorrencia;
	}
	
	
	public Button BuildbtImprimir(){
		btDocumentos = new Button("Documentos", new Button.ClickListener() {
			
			
			public void buttonClick(ClickEvent event) {
				if (winSubDocumentos != null && winSubDocumentos.getUI() != null)
					winSubDocumentos.close();
	             else {
	            	 
	            	 if(winSubRelatorio != null && winSubRelatorio.getUI() != null){
	            		 winSubRelatorio.close();
	            	 }
	            	 
	            	 if(winSubMateriais != null && winSubMateriais.getUI() != null){
	            		 winSubMateriais.close();
	            	 }
	            	 
	            	 if(winSubAtribuir != null && winSubAtribuir.getUI() != null){
	            		 winSubAtribuir.close();
	            	 }
	            	 
	            	 if(winSubFechar != null && winSubFechar.getUI() != null){
	            		 winSubFechar.close();
	            	 }
	            	 
	            	 
				     buildSubDocumentos(event);
				     
					 getUI().addWindow(winSubDocumentos);
					 winSubDocumentos.focus();
	                 ((CssLayout) getUI().getContent()).addLayoutClickListener(new LayoutClickListener() {
	                             
	               			@Override
	                        public void layoutClick(LayoutClickEvent event) {
	               				winSubDocumentos.close();
	                            ((CssLayout) getUI().getContent()).removeLayoutClickListener(this);
	                        }
	                 });
	             }									
			}
		});
		
		btDocumentos.setEnabled(false);
		
		
		return btDocumentos;
	}
	
	public Button BuildbtHistorico(){
		btHistorico = new Button("Histórico", new Button.ClickListener() {
			
			
			public void buttonClick(ClickEvent event) {
				
				final Set<Object> selecteds = (Set<Object>)tb.getValue();
				
				if(selecteds.size() == 1){
				
					getUI().addWindow(new HistoricoChamados(true, true, 
						((Cliente)tb.getItem(selecteds.toArray()[0]).getItemProperty("cliente").getValue()).getId()));
				
				}
							
			}
		});
		
		btHistorico.setEnabled(false);
		
		
		return btHistorico;
	}
		
	public Button BuildbtReagendar(){
		btReagendar = new Button("Reagendar", new Button.ClickListener() {
			
			
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Reagendar"))				
				{
					final Set<Object> selecteds = (Set<Object>)tb.getValue();
					
					if(selecteds.size() == 1){
				
						final ReagendarEditor reagendarEditor = new ReagendarEditor("Reagendar", true);
						reagendarEditor.addListerner(new ReagendarEditor.ReagendarRoteirizacaoListerner() {
							
							
							public void onClose(ReagendarRoteirizacaoEvent event) {
								if(event.isConfirm()){
									Ose ose = OseDAO.find((Integer)tb.getItem(selecteds.toArray()[0]).getItemProperty("id").getValue());
									
									DateTime dt1 = new DateTime(ose.getContrato() !=  null && ose.getContrato().getData_instalacao() != null ? ose.getContrato().getData_instalacao() : new Date());
									DateTime dt2 = new DateTime(event.getNovaData());
									
									if(ose.getGrupo().getNome().equals("INSTALACAO")){
										if(dt1.toDate().equals(dt2.toDate())){
											
											SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
											
											ose.setData_ex(event.getNovaData());							
											ose.setStatus("ABERTO");
											ose.setTecnico(null);
											ose.setVeiculo_id(null);
											ose.setMotivo_reagendamento(event.getMotivo());
											ose.setTurno(event.getTurno());
											ose.setOperador(OpusERP4UI.getUsuarioLogadoUI().getUsername());
											ose.setData_encaminhamento(null); 
											
											OseDAO.save(ose);
											
											AlteracoesOseDAO.add(new AlteracoesOse(null, "REAGENDADA para "+sdf.format(ose.getData_ex())+" - "+ose.getMotivo_reagendamento(), ose, OpusERP4UI.getUsuarioLogadoUI(), new Date()));
											AlertaPendenciaDAO.removePendencia(codSubModulo, ose.getId());
											refresh();
											
											Notify.Show("OS Reagendada com Sucesso!", Notify.TYPE_SUCCESS);
											reagendarEditor.close();
										}else{
											Notify.Show("Não é possível reagendar, data diferente da instalação!", Notify.TYPE_ERROR);
										}
									}else{
										SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
										
										ose.setData_ex(event.getNovaData());							
										ose.setStatus("ABERTO");
										ose.setTecnico(null);
										ose.setVeiculo_id(null);
										ose.setMotivo_reagendamento(event.getMotivo());
										ose.setTurno(event.getTurno());
										ose.setOperador(OpusERP4UI.getUsuarioLogadoUI().getUsername());
										ose.setData_encaminhamento(null); 
										
										OseDAO.save(ose);
										
										AlteracoesOseDAO.add(new AlteracoesOse(null, "REAGENDADA para "+sdf.format(ose.getData_ex())+" - "+ose.getMotivo_reagendamento(), ose, OpusERP4UI.getUsuarioLogadoUI(), new Date()));
										AlertaPendenciaDAO.removePendencia(codSubModulo, ose.getId());
										refresh();
										
										Notify.Show("OS Reagendada com Sucesso!", Notify.TYPE_SUCCESS);
										reagendarEditor.close();
									}
									
									
								}
							}
						});
						
						reagendarEditor.addCloseListener(new Window.CloseListener() {
							
							
							public void windowClose(CloseEvent e) {
								tb.focus();
							}
						});
						
						getUI().addWindow(reagendarEditor);
					}
				}else{				
					Notify.Show("Você não Possui Permissão para Reagendar OS",Notify.TYPE_ERROR);				
				}
				
			}
		});
		
		return btReagendar;
	}
	
	public Button BuildbtVisualizar(){
		btVisualizar = new Button("Visualizar", new Button.ClickListener() {
			
			
			public void buttonClick(ClickEvent event) {
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Visualizar"))				
				{
					final Set<Object> selecteds = (Set<Object>)tb.getValue();
					
					if(selecteds.size() == 1){
				
						RoteirizacaoEditor roteirizacaoEditor = new RoteirizacaoEditor(tb.getItem(selecteds.toArray()[0]), "Visualizar", true);
						
						roteirizacaoEditor.addCloseListener(new Window.CloseListener() {
							
							
							public void windowClose(CloseEvent e) {
								tb.focus();
							}
						});
						
						
						getUI().addWindow(roteirizacaoEditor);
					}
				
				}else{				
					Notify.Show("Você não Possui Permissão para Visualizar OS",Notify.TYPE_ERROR);				
				}
				
				
			}
		});
		btVisualizar.setEnabled(false);
		
		return btVisualizar;
	}
	
	
	//public Button BuildbtConcluir(){
		
		
		
		//return btConcluir;
	//}
	//public Button BuildbtFechar(){
		
		
		
	//	return btFechar;
	//}

	Window winSubMateriais;
	Window winSubRelatorio;
	
	Button btRelatorio;
	public Button BuildbtMateriais(){
		
		btRelatorio = new Button("Materiais", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if (winSubMateriais != null && winSubMateriais.getUI() != null)
					winSubMateriais.close();
	             else {
	            	 
	            	 if(winSubRelatorio!= null && winSubRelatorio.getUI() != null){
	            		 winSubRelatorio.close();
	            	 }
	            	 
	            	 if(winSubMateriais != null && winSubMateriais.getUI() != null){
	            		 winSubMateriais.close();
	            	 }
	            	 
	            	 if(winSubFechar != null && winSubFechar.getUI() != null){
	            		 winSubFechar.close();
	            	 }
	            	 
	            	 if(winSubAtribuir != null && winSubAtribuir.getUI() != null){
	            		 winSubAtribuir.close();
	            	 }
	            	 
	            	 
	            	 buildSubMateriais(event);
				     
					 getUI().addWindow(winSubMateriais);
					 winSubMateriais.focus();
	                 ((CssLayout) getUI().getContent()).addLayoutClickListener(new LayoutClickListener() {
	                             
	               			@Override
	                        public void layoutClick(LayoutClickEvent event) {
	               				winSubMateriais.close();
	                            ((CssLayout) getUI().getContent()).removeLayoutClickListener(this);
	                        }
	                 });
	             }
				
				
				

				
			}
		});
		
		return btRelatorio;
	}
	
	private void buildSubRelatorio(ClickEvent event) {
		winSubRelatorio = new Window("Informe as Datas:");
        VerticalLayout l = new VerticalLayout();
        l.setMargin(true);
        
        winSubRelatorio.setContent(l);
        winSubRelatorio.setWidth("230px");
        winSubRelatorio.addStyleName("notifications");
        winSubRelatorio.setClosable(false);
        winSubRelatorio.setResizable(false);
        winSubRelatorio.setDraggable(false);
        winSubRelatorio.setPositionX((event.getClientX() - event.getRelativeX())+100);
        winSubRelatorio.setPositionY(event.getClientY() - event.getRelativeY());
        winSubRelatorio.setCloseShortcut(KeyCode.ESCAPE, null);
        
        
	      final DateField dtInicial = new DateField("Inicial");
	      dtInicial.setWidth("190px");
	      
	      final DateField dtFinal   = new DateField("Final");
	      dtFinal.setWidth("190px");
	      
	      final Button btGerarRelatorio = new Button("Gerar", new Button.ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					
					if(dtInicial.getValue() != null && dtFinal.getValue() != null){
						try {
							
							Window window = new Window();
							window.setCaption("Relatório de uso de Materiais");
					        window.setWidth("800px");
					        window.setHeight("600px");
					        window.setResizable(true);
					        window.center();
					        window.setModal(true);
					        window.setStyleName("disable_scroolbar");
					        			       
					        StreamResource resource = new StreamResource(new RelatorioUsoMateriais(dtInicial.getValue(), dtFinal.getValue(), null), "Relatorio_de_Uso_de_Materiais.pdf");
					        resource.getStream();			        
					        resource.setMIMEType("application/pdf");			        
					        
					        Embedded e = new Embedded();
					        e.setSizeFull();
					        e.setType(Embedded.TYPE_BROWSER); 
					        e.setSource(resource);
					        
					        window.setContent(e);
					        getUI().addWindow(window);
					
						} catch (Exception e1) {
					
							e1.printStackTrace();
						}
					}
				}
			});
	      btGerarRelatorio.setStyleName("default");
	      
	      l.addComponent(dtInicial);
	      l.addComponent(dtFinal);
	      l.addComponent(new HorizontalLayout(){
	      	{
	      		setWidth("100%");
	      		
	      		addComponent(btGerarRelatorio);
	      		setComponentAlignment(btGerarRelatorio, Alignment.MIDDLE_RIGHT);
	      	}
	      }); 
        
	}
	
	private void buildSubMateriais(ClickEvent event) {
		winSubMateriais = new Window("Escolha uma opção:");
        VerticalLayout l = new VerticalLayout();
        //l.setMargin(true);
        
        winSubMateriais.setContent(l);
        winSubMateriais.setWidth("230px");
        winSubMateriais.addStyleName("notifications");
        winSubMateriais.setClosable(false);
        winSubMateriais.setResizable(false);
        winSubMateriais.setDraggable(false);
        winSubMateriais.setPositionX((event.getClientX() - event.getRelativeX())+100);
        winSubMateriais.setPositionY(event.getClientY() - event.getRelativeY());
        winSubMateriais.setCloseShortcut(KeyCode.ESCAPE, null);
        
        
        
        Button btRelatorio = new Button("Relatorio", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Relatorio de Materiais"))				
				{
				
					 if (winSubRelatorio != null && winSubRelatorio.getUI() != null)
						 winSubRelatorio.close();
		             else {
		            	 
		            	
		            	 if(winSubRelatorio != null && winSubRelatorio.getUI() != null){
		            		 winSubRelatorio.close();
		            	 }
		            	// if(winSubMateriais != null && winSubMateriais.getUI() != null){
		            	//	 winSubMateriais.close();
		            	 //}
		            	 
		            	 if(winSubFechar != null && winSubFechar.getUI() != null){
		            		 winSubFechar.close();
		            	 }
		            	 
		            	 if(winSubAtribuir != null && winSubAtribuir.getUI() != null){
		            		 winSubAtribuir.close();
		            	 }
		            	 
		            	 
		            	 buildSubRelatorio(event);
					     
						 getUI().addWindow(winSubRelatorio);
						 winSubRelatorio.focus();
		                 ((CssLayout) getUI().getContent()).addLayoutClickListener(new LayoutClickListener() {
		                             
		               			@Override
		                        public void layoutClick(LayoutClickEvent event) {
		               				winSubRelatorio.close();
		                            ((CssLayout) getUI().getContent()).removeLayoutClickListener(this);
		                        }
		                 });
		             }
				 
				}else{				
					Notify.Show("Você não Possui Permissão para Visualizar o relatório de Materiais",Notify.TYPE_ERROR);				
				}
			}
		});
        
        Button btAlocarMaterial = new Button("Alocar Material", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Alocar Materiais"))				
				{
					MateriaisAlocadosView matAloc = new MateriaisAlocadosView(true, true);
					
					matAloc.addListerner(new MateriaisAlocadosView.MateriaisAlocadosListerner() {
						
						@Override
						public void onSelected(MateriaisEvent event) {
							
						}
					});
					
					getUI().addWindow(matAloc);
					
				}else{				
					Notify.Show("Você não Possui Permissão para Alocar Materiais",Notify.TYPE_ERROR);				
				}
				 

			}
		});
        
        
        btRelatorio.setPrimaryStyleName("btSubMenu");
        btAlocarMaterial.setPrimaryStyleName("btSubMenu");
        
        l.addComponent(btRelatorio);
        l.addComponent(btAlocarMaterial); 
        

        
	}
	
	public Button BuildbtExcluir() {
		btExcluir = new Button("Excluir", new Button.ClickListener() {
			
			
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Excluir"))				
				{
					final Set<Object> selecteds = (Set<Object>)tb.getValue();
					
					if(selecteds.size() == 1){
						Integer cod_tabela = Integer.parseInt(tb.getItem(selecteds.toArray()[0]).getItemProperty("id").getValue().toString());
						final Ose ose = OseDAO.find(cod_tabela);
						
						List<Crm> crms = CrmDAO.getCrmByOse(ose);
	
						if(ContasReceberDAO.procurarBoletosOse(ose)==null && crms.size() == 0){
											
							GenericDialog gDialog = new GenericDialog("Confirme para Continuar!", "Você deseja realmente Excluir a OS Selecionada?", true, true);
							gDialog.addListerner(new GenericDialog.DialogListerner() {
								
								
								public void onClose(DialogEvent event) {
									if(event.isConfirm()){
										
										//Verificar se existem arquivos upload
										ArquivosOseDAO.excluirArquivos(ose);
										
										//Verificar se existem Produto 
										OseDAO.estornarProdutosOse(ose);								
										
										Integer cod_tabela = Integer.parseInt(tb.getItem(selecteds.toArray()[0]).getItemProperty("id").getValue().toString());
										container.removeItem(selecteds.toArray()[0]);
										container.commit();
										
										AlertaPendenciaDAO.removePendencia(codSubModulo, cod_tabela);								
										
										Notify.Show("OS Excluída Com Sucesso!", Notify.TYPE_SUCCESS);
										
										refresh_qtd();
										
										LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Excluiu uma OS"));
									}							
								}
							}); 
							gDialog.addCloseListener(new Window.CloseListener() {
								
								
								public void windowClose(CloseEvent e) {
									tb.focus();
								}
							});
						
							getUI().addWindow(gDialog);
						}else{
							Notify.Show("OSE Possui Boleto(s) e/ou CRM(s) Vinculado(s)",Notify.TYPE_ERROR);	
							
						}
					}
				}else{				
					Notify.Show("Você não Possui Permissão para Excluir OS",Notify.TYPE_ERROR);				
				}
				
			}
		});
		btExcluir.setEnabled(false);
		
		
		return btExcluir;
	}
	private Button BuildbtRefresh(){
		btRefresh = new Button("Atualizar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				refresh_now();	
			}
		});
		
		return btRefresh;
	}

	public Button buildBtLog(){
		btLog = new Button("Log", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				final Set<Object> selecteds = (Set<Object>)tb.getValue();
				
				if(selecteds.size() == 1){
					Integer cod_tabela = Integer.parseInt(tb.getItem(selecteds.toArray()[0]).getItemProperty("id").getValue().toString());

					HistoricoAlteracoes historicoAlteracoes = new HistoricoAlteracoes(true, true, cod_tabela);
					getUI().addWindow(historicoAlteracoes);
				}
			}
		});
		btLog.setEnabled(false);
		return btLog;
	}
	
	public Integer getCodSubModulo() {
		return codSubModulo;
	}

	public void setCodSubModulo(Integer codSubModulo) {
		this.codSubModulo = codSubModulo;
	}
	
	
}


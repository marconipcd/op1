package com.digital.opuserp.view.modulos.ordemServico.roteirizacao;

import java.lang.reflect.Method;

import com.digital.opuserp.dao.OseDAO;
import com.digital.opuserp.domain.Ceps;
import com.digital.opuserp.domain.MateriaisAlocados;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.util.Real;
import com.digital.opuserp.view.modulos.ordemServico.roteirizacao.AlocarMaterialEditor.AlocarMaterialEvent;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class MateriaisAlocadosView extends Window {

	JPAContainer<MateriaisAlocados> container;
	
	TextField tfBusca;
	Table tb;
	Button btSalvar;
	Button btCancelar;
	
	
	public MateriaisAlocadosView(boolean modal, boolean center){
		
		super("Materiais Alocados");
		setStyleName("disable_scroolbar");
		setWidth("830px");
		setHeight("360px");
		//setIcon(new ThemeResource("icons/search-32.png"));
		setModal(modal);
		if(center){
			center();
		}
		
		setClosable(false);
						
				
		setContent(new VerticalLayout(){
			{
				setSizeFull();
				setMargin(true);
				
				HorizontalLayout hlButons = new HorizontalLayout();
				hlButons.setWidth("100%");
				
				hlButons.addComponent(BuildbtRefresh());
				hlButons.setExpandRatio(btRefresh, 1.f);
				
				hlButons.addComponent(buildBtNovo());
				hlButons.addComponent(buildbtProtocolo());
				hlButons.addComponent(buildBtExcluir());
				
				addComponent(hlButons);
				setComponentAlignment(hlButons, Alignment.TOP_RIGHT);
				
				
				addComponent(buildTb());
				setExpandRatio(tb, 2);
				
				HorizontalLayout hlButtons = new HorizontalLayout();
				hlButtons.setSpacing(true);
				hlButtons.setMargin(true);
				hlButtons.setStyleName("hl_buttons_bottom");
				hlButtons.addComponent(buildBtCancelar());
				//hlButtons.addComponent(buildBtSalvar());
				
				addComponent(hlButtons);
				setComponentAlignment(hlButtons, Alignment.BOTTOM_RIGHT);
				
			}
		});
	}
	
	Button btRefresh;
	
	private Button BuildbtRefresh(){
		btRefresh = new Button("Atualizar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				container.refresh();
			}
		});
		
		return btRefresh;
	}
	
	Button btNovo;
	Button btImprimir;
	Button btExcluir;
	
	private Button buildBtNovo(){
		btNovo = new Button("Novo", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				final AlocarMaterialEditor alocMaterial = new AlocarMaterialEditor("Alocar Material", true);
				alocMaterial.addListerner(new AlocarMaterialEditor.AlocarMaterialListerner() {
					
					@Override
					public void onClose(AlocarMaterialEvent event) {
						
						if(event.isConfirm()){
							
							try{
						
								boolean check = OseDAO.alocarMaterial(event.getTecnico(), event.getMateriaisList());
								
								if(check){
									alocMaterial.close();
									Notify.Show("Material Alocado com Sucesso!", Notify.TYPE_SUCCESS);
									
									container.refresh();
									
									

								}
							}catch (Exception e) {
								e.printStackTrace();
							} 
						}
							
					}
				});
				
				getUI().addWindow(alocMaterial); 
				
			}
		});
		
		return btNovo;
	}
	private Button buildbtProtocolo(){
		btImprimir = new Button("Protocolo", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(tb.getValue() != null){
					try{
						
						EntityItem<MateriaisAlocados> entityItem = (EntityItem<MateriaisAlocados>)tb.getItem(tb.getValue());
						MateriaisAlocados materiais = entityItem.getEntity();
						
						Window win = new Window("Protocolo de Retirada de Material");
						win.setWidth("800px");
						win.setHeight("600px");
						win.setResizable(true);
						win.center();
						win.setModal(true);
						win.setStyleName("disable_scroolbar");
						
						StreamResource resource;
						resource = new StreamResource(new ProtocoloRetiradaMaterial(materiais.getId(),materiais.getTecnico(),materiais.getData_alocacao()), "Procolodo de Retirada de Material");
						resource.getStream();
						resource.setMIMEType("application/pdf");
						resource.setCacheTime(0);
						
						Embedded e = new Embedded();
						e.setSizeFull();
						e.setType(Embedded.TYPE_BROWSER);
						e.setSource(resource);
						
						win.setContent(e);
						getUI().addWindow(win);
						
					}catch(Exception e){
						e.printStackTrace();
					}
					
				}
				

			}
		});
		
		btImprimir.setEnabled(false);
		return btImprimir;
	}
	private Button buildBtExcluir(){
		btExcluir = new Button("Excluir", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(tb.getValue() != null){
					
					GenericDialog gd = new GenericDialog("Confirme para continuar!", "Deseja realmente excluir esse registro ?", true, true);
					gd.addListerner(new GenericDialog.DialogListerner() {
						
						@Override
						public void onClose(DialogEvent event) {
							
							if(event.isConfirm()){
								
								EntityItem<MateriaisAlocados> entityItem = (EntityItem<MateriaisAlocados>)tb.getItem(tb.getValue());
								MateriaisAlocados materiais = entityItem.getEntity();
								
								
								boolean check = OseDAO.removeMaterialAlocado(materiais.getId());
																
								if(check){
									Notify.Show("Registro excluído com sucesso!", Notify.TYPE_SUCCESS);
									container.refresh();
								}
							}
							
						}
					});
					
					
					getUI().addWindow(gd); 
					
				}
			}
		});
		
		btExcluir.setEnabled(false); 
		return btExcluir;
	}
	
	
	private Button buildBtCancelar(){
		btCancelar = new Button("Fechar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				close();
			}
		});
		
		return btCancelar;
	}
	
	public JPAContainer<MateriaisAlocados> buildJpaContainer(){
		container = JPAContainerFactory.make(MateriaisAlocados.class, ConnUtil.getEntity());
		container.addNestedContainerProperty("tecnico.username");
		//container.addNestedContainerProperty("material.nome");
		
		return container;
	}
			
	public Table buildTb(){
		tb = new Table(null, buildJpaContainer()){
			
			protected String formatPropertyValue(Object rowId, Object colId,
					Property<?> property) {
				
				Object v = property.getValue();
											
				if(colId.equals("qtd") && tb.getItem(rowId).getItemProperty(colId).getValue() != null){					
					return Real.formatDbToString(tb.getItem(rowId).getItemProperty(colId).getValue().toString());
				}					
				
				return super.formatPropertyValue(rowId, colId, property);
			}
		};		
		tb.setWidth("100%");
		tb.setHeight("230px");
		tb.setSelectable(true);
		tb.setColumnHeader("id", "Cod");
		tb.setColumnHeader("tecnico.username", "Tecnico");
		//tb.setColumnHeader("material.nome", "Material");
		tb.setColumnHeader("qtd", "Qtd");
		tb.setColumnHeader("data_alocacao", "Data");
		
		tb.setColumnAlignment("qtd", Align.RIGHT);

		
		tb.setVisibleColumns(new Object[]{"id","tecnico.username","data_alocacao"});
		
		
		tb.addValueChangeListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(tb.getValue() != null){
					btExcluir.setEnabled(true);
					btImprimir.setEnabled(true);
				}else{
					btExcluir.setEnabled(false);
					btImprimir.setEnabled(false);
				}
			}
		});
		
		return tb;
	}
	
	
	
	
	
	public void addListerner(MateriaisAlocadosListerner target){
		try {
			Method method = MateriaisAlocadosListerner.class.getDeclaredMethod("onSelected",MateriaisEvent.class);
			addListener(MateriaisEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(MateriaisAlocadosListerner target){
		removeListener(MateriaisEvent.class, target);
	}
	public static class MateriaisEvent extends Event{

		private Ceps ceps;
		
		public MateriaisEvent(Component source, Ceps cep) {
			super(source);		
			this.ceps  = cep;
		}

		public Ceps getCep() {
			return ceps;
		}		
	}
	public interface MateriaisAlocadosListerner{
		public void onSelected(MateriaisEvent event);
	}
}

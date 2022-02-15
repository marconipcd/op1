package com.digital.opuserp.view.modulos.acesso.contratos_acesso;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.AcessoCliente;
import com.digital.opuserp.domain.Pendencias;
import com.digital.opuserp.interfaces.GenericEditor;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Item;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Reindeer;

public class PendenciasEditor extends Window implements GenericEditor {

		
	private static final long serialVersionUID = 1L;
	
	Item item;
	Button btSalvar; 
	Button btCancelar;
	FormLayout flPrincipal;
	
	VerticalLayout vlRoot;
	
	HorizontalLayout hlFloat;
		
	
	TextArea txtObs;
	
	JPAContainer<Pendencias> container; 
	
	public PendenciasEditor(Item item, String title, boolean modal){
		this.item = item;
		
		setWidth("848px");
		setHeight("549px");
		
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
				
				
				addComponent(hlButtons);
				setComponentAlignment(hlButtons, Alignment.BOTTOM_RIGHT);
			}
		});
		

		buildLayout();
	}

	

	private JPAContainer<Pendencias> buildContainer(){
		container = JPAContainerFactory.make(Pendencias.class, ConnUtil.getEntity());
		
		container.addContainerFilter(Filters.eq("contrato", item.getItemProperty("id").getValue()));
		container.applyFilters();
		
		return container;
	}
	
	Table tb;
	private Table buildTable(){
		
		tb = new Table(null, buildContainer());
		tb.setSizeFull();
		tb.setVisibleColumns(new Object[]{"pendencia", "operador","data"});
		
		tb.setColumnHeader("pendencia", "Pendência");
		tb.setColumnHeader("status", "Status");
		tb.setColumnHeader("operador", "Operador");
		tb.setColumnHeader("data", "Data");
		
		tb.addGeneratedColumn("Conclusao", new Table.ColumnGenerator() {
			
			@Override
			public Object generateCell(final Table source, final Object itemId,final Object columnId) {
				if(source.getItem(itemId).getItemProperty("status").getValue().toString().equals("FECHADO")){
					
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
					Date data_conclusao = (Date)source.getItem(itemId).getItemProperty("data_conclusao").getValue();					
					
					return "CONCLUÍDO por: "+source.getItem(itemId).getItemProperty("operador_conclusao").getValue().toString()+" | "+sdf.format(data_conclusao);
				}else{
					return "ABERTO";
				}
			}
		});
		
		tb.addGeneratedColumn("x", new Table.ColumnGenerator() {
			
			@Override
			public Object generateCell(final Table source, final Object itemId, Object columnId) {

				String status = source.getItem(itemId).getItemProperty("status").getValue().toString();
				if(status.equals("ABERTO")){
				
					Button btConcluir = new Button("Concluir", new Button.ClickListener() {
						
						@Override
						public void buttonClick(ClickEvent event) {
							Item item = source.getItem(itemId);
							EntityManager em = ConnUtil.getEntity();
							Pendencias pendencia = em.find(Pendencias.class, item.getItemProperty("id").getValue());
							pendencia.setStatus("FECHADO");
							pendencia.setOperador_conclusao(OpusERP4UI.getUsuarioLogadoUI().getUsername());
							pendencia.setData_conclusao(new Date());
							
							em.getTransaction().begin();
							em.merge(pendencia);
							em.getTransaction().commit();
							
							Query q = em.createQuery("select p from Pendencias p where p.contrato=:contrato and p.status='ABERTO' ", Pendencias.class);
							q.setParameter("contrato", pendencia.getContrato());
							
							if(q.getResultList().size() == 0){
								AcessoCliente contrato = pendencia.getContrato();
								contrato.setTem_pendencia("NAO");
								
								em.getTransaction().begin();
								em.merge(contrato);
								em.getTransaction().commit();
							}
							
							vlRoot.replaceComponent(tb, buildTable());
							
							Notify.Show("Ok, marcado como concluído",  Notify.TYPE_SUCCESS);
						}
					});
					
					btConcluir.setStyleName(Reindeer.BUTTON_LINK);
					return btConcluir;
				}else{
					return null;
				}
				
			}
		});
		
		return tb;
	}
	
	
	
	VerticalLayout vl1 = new VerticalLayout();
	
	private VerticalLayout buildVlAddNew(){
		
		VerticalLayout vlAddNew = new VerticalLayout();
		
		vlAddNew.addComponent(new FormLayout() {
			{
				setMargin(true);
				setSpacing(true);
				
				addComponent(new Label("<span style='background: #ececec; border-top-left-radius: 14px; border-top-right-radius: 14px; width: 176px;text-align: center; font-family: monospace; font-size: 1.2em; padding: 5px 17px;'>Nova Pendência</span>", ContentMode.HTML));
	
			}
		});		
		vlAddNew.addComponent(new FormLayout() {
				{
					setMargin(true);
					setSpacing(true);
					setStyleName("form-cutom");
		
					txtObs = new TextArea("Obs.:");				
					//txtObs.setStyleName("caption-align-acesso");			
					txtObs.setWidth("741px");
					txtObs.setHeight("80px");
					addComponent(txtObs);		
					
//					if(item != null && item.getItemProperty("ultima_pendencia").getValue() != null){
//						txtObs.setValue(item.getItemProperty("ultima_pendencia").getValue().toString());
//					}
		
				}
			});
			
			final Button btSalvar = new Button("Salvar", new Button.ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					concluirCadastro();
				}
			});
			btSalvar.setStyleName("default");
			vlAddNew.addComponent(new HorizontalLayout(){
				{
					setWidth("100%");
					addComponent(btSalvar); 
					setComponentAlignment(btSalvar, Alignment.MIDDLE_RIGHT);
				}
			});
			
			
			return vlAddNew;
	}
	

	public void buildLayout(){
		
		
		//Adicionar nova Pendência
		
		Button btNovaPendencia = new Button("Nova Pendência", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				vl1.removeAllComponents();
				vl1.addComponent(buildVlAddNew());
				setHeight("694px");
			}
		});
		btNovaPendencia.setDisableOnClick(true); 
		btNovaPendencia.setStyleName(Reindeer.BUTTON_SMALL);
		
		//Table
		vl1.setWidth("100%");
		vlRoot.addComponent(vl1); 
		vlRoot.addComponent(btNovaPendencia);
		vlRoot.addComponent(buildTable());
		
		

		

	
	}

	

	@Override
	public Button buildBtCancelar() {
		btCancelar = new Button("Fechar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {						
								
						fireEvent(new PendenciasEditorEvent(getUI(), item, false));
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
	
	private void concluirCadastro(){
								
		if(!txtObs.getValue().equals("")){
						
			EntityManager em = ConnUtil.getEntity();
			
			EntityItem<AcessoCliente> entityItemContrato = (EntityItem)item;
			AcessoCliente contrato = entityItemContrato.getEntity();
			contrato.setTem_pendencia("SIM");
			contrato.setUltima_pendencia(txtObs.getValue());
			
			Pendencias pendencia = new Pendencias();
			pendencia.setPendencia(txtObs.getValue());
			pendencia.setData(new Date());
		    pendencia.setOperador(OpusERP4UI.getUsuarioLogadoUI().getUsername());
		    pendencia.setContrato(contrato); 
		    pendencia.setStatus("ABERTO");
						
			em.getTransaction().begin();
			em.persist(pendencia);			
			em.merge(contrato);
			em.getTransaction().commit();
				
				
			fireEvent(new PendenciasEditorEvent(getUI(), item, true));

			
		}else{
			
			 
			    if(!txtObs.isValid()){
			    	txtObs.addStyleName("invalid-txt");
			    }else{
			    	txtObs.removeStyleName("invalid-txt");  	
			    }
			    
			    
			    Notify.Show_Invalid_Submit_Form();
		}		
	}
	
	
	public void addListerner(PendenciasEditorListerner target){
		try {
			Method method = PendenciasEditorListerner.class.getDeclaredMethod("onClose", PendenciasEditorEvent.class);
			addListener(PendenciasEditorEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(PendenciasEditorListerner target){
		removeListener(PendenciasEditorEvent.class, target);
	}
	public static class PendenciasEditorEvent extends Event{
		
		private Item item;
		private boolean confirm;
				
		public PendenciasEditorEvent(Component source, Item item, boolean confirm) {
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
	public interface PendenciasEditorListerner extends Serializable{
		public void onClose(PendenciasEditorEvent event);
	}
	@Override
	public Button buildBtSalvar() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	
	
	


}

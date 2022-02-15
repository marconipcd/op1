package com.digital.opuserp.view.modulos.estoque.grupo;

import java.lang.reflect.Method;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.EmpresaDAO;
import com.digital.opuserp.domain.Empresa;
import com.digital.opuserp.domain.EmpresaGrupoProduto;
import com.digital.opuserp.domain.GrupoProduto;
import com.digital.opuserp.util.ConnUtil;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.data.Item;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class AddEmpGrpEditor  extends Window {
	
	JPAContainer<Empresa> container;
	
	TextField tfBusca;
	Table tbEmpresas;
	Button btSalvar;
	Button btCancelar;
	GrupoProduto grupoProduto;
	
	
	public AddEmpGrpEditor(boolean modal, boolean center, GrupoProduto grupoProduto){
		
		
		super("Selecione uma Empresa");
		this.grupoProduto = grupoProduto;
		
		setWidth("1000px");
		setHeight("360px");
		setIcon(new ThemeResource("icons/search-32.png"));
		setModal(modal);
		if(center){
			center();
		}
		
		setClosable(false);
				
				
				
		setContent(new VerticalLayout(){
			{
				setSizeFull();
				setMargin(true);
				
				//addComponent(buildTextField());
				addComponent(buildTb());
				setExpandRatio(tbEmpresas, 2);
				
				HorizontalLayout hlButtons = new HorizontalLayout();
				hlButtons.setSpacing(true);
				hlButtons.setMargin(true);
				hlButtons.setStyleName("hl_buttons_bottom");
				hlButtons.addComponent(buildBtCancelar());
				hlButtons.addComponent(buildBtSelecionar());
				
				addComponent(hlButtons);
				setComponentAlignment(hlButtons, Alignment.BOTTOM_RIGHT);
				
			}
		});
	}

	
	
	
	
	public Table buildTb(){
		
		
		EntityManager em = ConnUtil.getEntity();
		Query q =em.createQuery("select e from Empresa e where e.grupo=:grupo", Empresa.class);
		q.setParameter("grupo",OpusERP4UI.getEmpresa().getGrupo());
		List<Empresa> empresas = q.getResultList();
		
		
		tbEmpresas = new Table(null);
		
		tbEmpresas.setWidth("100%");
		tbEmpresas.setHeight("230px");
		tbEmpresas.setSelectable(true);

		tbEmpresas.addContainerProperty("Cod", Integer.class, null);
		tbEmpresas.addContainerProperty("Empresa", String.class, "");
		
		for (Empresa empresa : empresas) {
			Query q2 = em.createQuery("select e from EmpresaGrupoProduto e where e.empresa=:empresa and e.grupoProduto=:grupoProduto", EmpresaGrupoProduto.class);
			q2.setParameter("empresa", empresa);
			q2.setParameter("grupoProduto", grupoProduto);
			
			if(q2.getResultList().size() == 0){				
				tbEmpresas.addItem(new Object[]{empresa.getId(), empresa.getRazao_social()}, tbEmpresas.getItemIds().size()+1);
			}
			
		}
		
				
		tbEmpresas.addListener(new ItemClickListener() {
				
			@Override
			public void itemClick(ItemClickEvent event) {
				if(event.isDoubleClick()){
					btSalvar.click();					
				}
			}
		});
		return tbEmpresas;
	}
	

	

	

	private Button buildBtSelecionar() {
		btSalvar = new Button("Ok", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
					
				if(tbEmpresas.getValue()!= null){
					Item item = tbEmpresas.getItem(tbEmpresas.getValue());			
					Empresa empresa = EmpresaDAO.find(Integer.parseInt(tbEmpresas.getItem(tbEmpresas.getValue()).getItemProperty("Cod").getValue().toString()));
					
					fireEvent(new AddEmpresaGrupoProdutoEvent(getUI(), empresa));
					close();
				}	
			}	
		});
		
		ShortcutListener slbtOK = new ShortcutListener("Ok",ShortcutAction.KeyCode.ENTER, null) {

			@Override
			public void handleAction(Object sender, Object target) {
				btSalvar.click();
			}
		};

		btSalvar.addShortcutListener(slbtOK);

		
		return btSalvar;
	}

	private Button buildBtCancelar() {
		btCancelar = new Button("Cancelar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				close();
			}	
		});
		
			ShortcutListener clTb = new ShortcutListener("Cancelar", ShortcutAction.KeyCode.ESCAPE, null) {
			
			@Override
			public void handleAction(Object sender, Object target) {
				btCancelar.click();
			}
		};
		btCancelar.addShortcutListener(clTb);
		
		return btCancelar;
	}

	
	
	public void addListerner(AddEmpresaGrupoProdutoListerner target){
		try {
			Method method = AddEmpresaGrupoProdutoListerner.class.getDeclaredMethod("onSelected",AddEmpresaGrupoProdutoEvent.class);
			addListener(AddEmpresaGrupoProdutoEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(AddEmpresaGrupoProdutoListerner target){
		removeListener(AddEmpresaGrupoProdutoEvent.class, target);
	}
	public static class AddEmpresaGrupoProdutoEvent extends Event{

		private Empresa Empresa;
		
		
		public AddEmpresaGrupoProdutoEvent(Component source, Empresa Empresa) {
			super(source);		
			this.Empresa  = Empresa;
			
		}

		public Empresa getEmpresa() {
								
			return Empresa;
		}		
	}
	public interface AddEmpresaGrupoProdutoListerner{
		public void onSelected(AddEmpresaGrupoProdutoEvent event);
	}
}



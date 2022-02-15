package com.digital.opuserp.view.modulos.configuracoes.config_sistema.usuario;

import java.util.List;

import com.digital.opuserp.dao.LoginDAO;
import com.digital.opuserp.dao.Menu;
import com.digital.opuserp.dao.ModuloUsuarioDAO;
import com.digital.opuserp.dao.SubModuloUsuarioDAO;
import com.digital.opuserp.domain.Empresa;
import com.digital.opuserp.domain.EmpresasUsuario;
import com.digital.opuserp.domain.Modulo;
import com.digital.opuserp.domain.ModuloUsuario;
import com.digital.opuserp.domain.ModulosEmpresa;
import com.digital.opuserp.domain.SubModulo;
import com.digital.opuserp.domain.SubModuloUsuario;
import com.digital.opuserp.domain.Usuario;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class GerenciarModulos extends Window {

	Table tbEmpresas;
	TextField tfBuscaEmpresa;
	BeanItemContainer<Empresa> empresasUsers;
	Usuario user;
	
	Table tbModulo;
	TextField tfBuscaModulo;
	BeanItemContainer<Modulo> modulosEmpresas;
	Empresa empresa;
	
	Table tbSubModulo;
	TextField tfBuscaSubModulo;
	BeanItemContainer<SubModulo> submodulo;
	Modulo modulo;
	
//	Table tbSetores;
//	BeanItemContainer<Setores> setores;
//	Setores setor;
	
	
	
	private Integer codEmpresa;

	
	public GerenciarModulos(String title, boolean modal, Usuario user){
		this.user = user;
				
		setCaption(title);
		setModal(modal);
		center();
		setWidth("98%");
		setStyleName("windowCadastro");
				
		 tbEmpresas = buildTbEmpresas();
		 tfBuscaEmpresa = builTfBusca();
		 tbModulo = buildTbModulo();
		 tfBuscaModulo = buildTfBuscarModulo();
		 tfBuscaSubModulo = buildTfBuscarSubModulo();
		 tbSubModulo = buildTbSubModulo();
//		 tbSetores = buildTbSetores();
	
				
		VerticalLayout vlRoot = new VerticalLayout();
		vlRoot.addComponent(tfBuscaEmpresa);
		vlRoot.addComponent(tbEmpresas);
		vlRoot.addComponent(tfBuscaModulo);
		vlRoot.addComponent(tbModulo);
		vlRoot.addComponent(tfBuscaSubModulo);
		vlRoot.addComponent(tbSubModulo);
	
		vlRoot.setExpandRatio(tbEmpresas, 1);
		vlRoot.setExpandRatio(tbModulo, 1);

		setContent(vlRoot);
		vlRoot.setMargin(true);		

	}
	
	public Table buildTbEmpresas(){
		
		empresasUsers = new BeanItemContainer<Empresa>(Empresa.class);
		

		LoginDAO lDAO = new LoginDAO();
		List<EmpresasUsuario> empresas = lDAO.getEmpresasUsuario(user);
		
		for(EmpresasUsuario e:empresas){
			empresasUsers.addBean(e.getEmpresa());
		}		
		
		
		tbEmpresas = new Table(null, empresasUsers);
		tbEmpresas.setSelectable(true);
		tbEmpresas.setVisibleColumns(new Object[]{"cnpj","razao_social","nome_fantasia"});
		tbEmpresas.setColumnCollapsingAllowed(true);
		tbEmpresas.setColumnHeader("cnpj", "CNPJ");
		tbEmpresas.setColumnHeader("razao_social", "Razão Social");
		tbEmpresas.setColumnHeader("nome_fantasia", "Nome Fantasia");
		tbEmpresas.setColumnCollapsible("nome_fantasia", false);
		tbEmpresas.setWidth("100%");
		tbEmpresas.setHeight("135px");
		tbEmpresas.setImmediate(true);
		tbEmpresas.addListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				
				
				if(tbEmpresas.getValue() !=  null){
					
					codEmpresa = Integer.parseInt(tbEmpresas.getItem(tbEmpresas.getValue()).getItemProperty("id").toString());
					
					Menu menu = new Menu();
					List<ModulosEmpresa> modEmp = menu.getModulosdasEmpresa(codEmpresa);
					
					modulosEmpresas.removeAllItems();
					for(ModulosEmpresa e:modEmp){
						modulosEmpresas.addBean(e.getModulo());
					}	
					
//					List<setoresUsuario> setorEmp = menu.getSetoresdasEmpresa(codEmpresa);
//					
//					setores.removeAllItems();
//					for(setoresUsuario e:setorEmp){
//						Setores.addBean(e.get);
//					}	
					
					
					 
					 tbModulo.setImmediate(true);
					 tbModulo.removeGeneratedColumn("Adicinar/Remover");
					 tbModulo.addGeneratedColumn("Adicinar/Remover", new Table.ColumnGenerator() {
						
						@Override
						public Object generateCell(Table source, Object itemId, Object columnId) {
							
							final Integer codModulo = Integer.parseInt(source.getItem(itemId).getItemProperty("id").toString());
							
							Menu menu = new Menu();
							List<ModuloUsuario> result = menu.getModulosUsuario(user.getId(),codEmpresa);
							
							boolean check = false;
							for(ModuloUsuario e:result){
								
								if(codModulo == e.getModulo().getId()){
									check = true;
									break;
								}
							}
							
							final CheckBox checkModulo = new CheckBox();
							checkModulo.setValue(check);
							checkModulo.setImmediate(true);
							checkModulo.addListener(new Property.ValueChangeListener() {
								
								@Override
								public void valueChange(ValueChangeEvent event) {
									
									ModuloUsuarioDAO modUserDao = new ModuloUsuarioDAO();
									
									if(checkModulo.getValue().booleanValue()){
										modUserDao.adicionarRegistro(codEmpresa,codModulo,user.getId());
									}else{
										modUserDao.deletarRegistro(codEmpresa, codModulo,user.getId());
									}									
								}
							});
							return checkModulo;
						}
					});
				}else{
					modulosEmpresas.removeAllItems();
					submodulo.removeAllItems();
				}
				
			}
		});		
		return tbEmpresas;
	}
	
	
	public TextField builTfBusca(){
		tfBuscaEmpresa = new TextField();
		tfBuscaEmpresa.setWidth("100%");
		tfBuscaEmpresa.setTextChangeEventMode(TextChangeEventMode.LAZY);
		tfBuscaEmpresa.focus();
		tfBuscaEmpresa.setInputPrompt("Buscar...");
		tfBuscaEmpresa.setCaption("Empresas");
		
		tfBuscaEmpresa.addListener(new FieldEvents.TextChangeListener() {
			
			@Override
			public void textChange(TextChangeEvent event) {
				addFilter(event.getText());	
			}
		});
		
		return tfBuscaEmpresa;
	}
	
	private void addFilter(String s){
		empresasUsers.removeAllContainerFilters();
		empresasUsers.addContainerFilter(Filters.or(
					Filters.like("razao_social", "%"+s+"%", false),
					Filters.like("nome_fantasia", "%"+s+"%", false),
					Filters.like("cnpj", "%"+s+"%", false)
				));		
	}
	
	
	
	// table Modulos
	public Table buildTbModulo(){
		
		modulosEmpresas = new BeanItemContainer<Modulo>(Modulo.class);
				
		tbModulo = new Table(null, modulosEmpresas);
		tbModulo.setSelectable(true);
		tbModulo.setVisibleColumns(new Object[]{"nome_modulo","descricao"});
		tbModulo.setColumnCollapsingAllowed(true);
		tbModulo.setColumnHeader("nome_modulo", "Nome");
		tbModulo.setColumnHeader("descricao", "Descrição");
		tbModulo.setColumnCollapsible("nome_modulo", false);
		tbModulo.setWidth("100%");
		tbModulo.setHeight("135px");
		tbModulo.setImmediate(true);
		tbModulo.addListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				
				if(tbModulo.getValue() !=  null ){
					
					final Integer codModulo = Integer.parseInt(tbModulo.getItem(tbModulo.getValue()).getItemProperty("id").toString());
					
					Menu menu = new Menu();
					List<SubModulo> subModEmp = menu.getSubModulos(codModulo);
					
					submodulo.removeAllItems();
					for(SubModulo e:subModEmp){
						submodulo.addBean(e);
					}		
				
					
					 
					 tbSubModulo.setImmediate(true);
					 tbSubModulo.removeGeneratedColumn("Adicinar/Remover");
					 tbSubModulo.addGeneratedColumn("Adicinar/Remover", new Table.ColumnGenerator() {
						
						@Override
						public Object generateCell(Table source, Object itemId, Object columnId) {
							
							final Integer codSubModulo = Integer.parseInt(source.getItem(itemId).getItemProperty("id").toString());
							
							Menu menu = new Menu();
							List<SubModuloUsuario> result = menu.getSubModuloUsuario(user.getId(), codEmpresa);
							
							boolean check = false;
							for(SubModuloUsuario e:result){
								
								if(codSubModulo == e.getSubmodulo().getId()){
									check = true;
									break;
								}
							}
							
							final CheckBox checkModulo = new CheckBox();
							checkModulo.setValue(check);
							checkModulo.setImmediate(true);
							checkModulo.addListener(new Property.ValueChangeListener() {
								
								@Override
								public void valueChange(ValueChangeEvent event) {
									
									SubModuloUsuarioDAO subModUserDao = new SubModuloUsuarioDAO();
									
									if(checkModulo.getValue().booleanValue()){
										subModUserDao.adicionarRegistro(codEmpresa, codSubModulo, user.getId());
									}else{
										subModUserDao.deletarRegistro(codEmpresa, codSubModulo, user.getId());
									}									
								}
							});
							return checkModulo;
						}
					});
				}else{
					submodulo.removeAllItems();
				}				
			}
		});				
		return tbModulo;
	}
	
	public TextField buildTfBuscarModulo(){		
		tfBuscaModulo = new TextField();
		tfBuscaModulo.setWidth("100%");
		tfBuscaModulo.setTextChangeEventMode(TextChangeEventMode.LAZY);
		tfBuscaModulo.focus();
		tfBuscaModulo.setInputPrompt("Buscar...");
		tfBuscaModulo.setCaption("Modulos");
		
		tfBuscaModulo.addListener(new FieldEvents.TextChangeListener() {
			
			
			public void textChange(TextChangeEvent event) {
				addFilterModulos(event.getText());
				
			}
		});		
	return tfBuscaModulo;	
	
	}
	
	private void addFilterModulos(String s){
		modulosEmpresas.removeAllContainerFilters();
		modulosEmpresas.addContainerFilter(Filters.or(
						Filters.like("nome_modulo", "%"+s+"%", false),
						Filters.like("descricao", "%"+s+"%", false)
				));
	}
	

	// table SubModulos
		public Table buildTbSubModulo(){
			
			submodulo = new BeanItemContainer<SubModulo>(SubModulo.class);
			
			tbSubModulo = new Table(null, submodulo);
			tbSubModulo.setSelectable(true);
			tbSubModulo.setVisibleColumns(new Object[]{"nome","descricao"});
			tbSubModulo.setColumnHeader("nome", "Nome");
			tbSubModulo.setColumnHeader("descricao", "Descrição");
			tbSubModulo.setColumnCollapsingAllowed(true);
			tbSubModulo.setColumnCollapsible("nome", false);
			tbSubModulo.setWidth("100%");
			tbSubModulo.setHeight("135px");
			
			
			return tbSubModulo;
		}
		
		public TextField buildTfBuscarSubModulo(){		
			tfBuscaSubModulo = new TextField();
			tfBuscaSubModulo.setWidth("100%");
			tfBuscaSubModulo.setTextChangeEventMode(TextChangeEventMode.LAZY);
			tfBuscaSubModulo.focus();
			tfBuscaSubModulo.setInputPrompt("Buscar...");
			tfBuscaSubModulo.setCaption("SubModulos");
			
			
			tfBuscaSubModulo.addListener(new FieldEvents.TextChangeListener() {
				
				
				public void textChange(TextChangeEvent event) {
					addFilterSubModulos(event.getText());
					
				}
			});		
		return tfBuscaSubModulo;	
		
		}
		
		private void addFilterSubModulos(String s){
			submodulo.removeAllContainerFilters();
			submodulo.addContainerFilter(Filters.or(
							Filters.like("nome", "%"+s+"%", false),
							Filters.like("descricao", "%"+s+"%", false)
					));

		}
	
//	public  Table buildTbSetores(){
//		
//		tbSetores = new Table(null, setores);
//		tbSetores.setSelectable(true);
//		tbSetores.setVisibleColumns(new Object[]{"nome",null});
//		tbSetores.setColumnHeader("nome", "Nome");
//		tbSetores.setColumnCollapsingAllowed(true);
//		tbSetores.setColumnCollapsible("nome", false);
////		tbSetores.setWidth("100%");
//		tbSetores.setHeight("135px");
//		
//		
//		
//		return tbSetores;	
//	}
	
	
	
	
	
	
	
	
	
	
}




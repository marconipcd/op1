package com.digital.opuserp.view.modulos.configuracoes.config_sistema.usuario;

import java.util.List;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.EmpresaUsuarioDAO;
import com.digital.opuserp.dao.LoginDAO;
import com.digital.opuserp.domain.Empresa;
import com.digital.opuserp.domain.EmpresasUsuario;
import com.digital.opuserp.domain.Usuario;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class GerenciarEmpresa extends Window {

	Table tbEmpresas;
	TextField tfBuscaEmpresa;
	BeanItemContainer<Empresa> empresasUsers;
	Usuario user;
	Label alerta;
	
	public GerenciarEmpresa(String title, boolean modal, final Usuario user){
		this.user = user;
		
		setCaption(title);
		setModal(modal);
		center();

		
		
//		final HorizontalLayout hlAlerta = new HorizontalLayout();
//		hlAlerta.addComponent(buildAlerta());
		
		
		setContent(new VerticalLayout(){
			{				
				setMargin(true);
				setSpacing(true);
				
				if(user.getId() == OpusERP4UI.getUsuarioLogadoUI().getId()){
					addComponent(buildAlerta());
					addComponent(builTfBusca());
					addComponent(buildTbEmpresas());
					
				}else{
					addComponent(builTfBusca());
					addComponent(buildTbEmpresas());
				}				
			}
		});
	}
	
	
	public Label buildAlerta(){ 
		alerta = new Label("ATENÇÂO VOCÊ ESTÁ GERENCIANDO SUAS PROPRIAS EMPRESAS!"); 
		alerta.setStyleName("alerta");
		alerta.setWidth("100%");
		alerta.setHeight("30px");
		
		
		
		return alerta;
	}
	
	
	public Table buildTbEmpresas(){	
		
		
		empresasUsers = new BeanItemContainer<Empresa>(Empresa.class);
		
		 if(!OpusERP4UI.getUsuarioLogadoUI().getFuncao().equals("admin")){
		        
		        LoginDAO lDAO = new LoginDAO();
		        List<EmpresasUsuario> result = lDAO.getEmpresasUsuario(OpusERP4UI.getUsuarioLogadoUI());
		        for(EmpresasUsuario e:result){
		        	empresasUsers.addBean(e.getEmpresa());
		        }
	        
	        }else{
	        	LoginDAO lDAO = new LoginDAO();
	 	        List<Empresa> result = lDAO.getEmpresas();
	 	        if(result != null){
	 	        	for(Empresa e:result){
	 	        		empresasUsers.addBean(e);
	 	        	}
	 	        }
	        }
		
		
//		Login lDAO = new Login();
//		List<EmpresasUsuario> empresas = lDAO.getEmpresasUsuario(OpusERP4UI.USUARIO_LOGADO);

//		for(EmpresasUsuario e:empresas){
//			empresasUsers.addBean(e.getEmpresa());
//		}		
				
		tbEmpresas = new Table(null, empresasUsers);
		tbEmpresas.setVisibleColumns(new Object[]{"cnpj","razao_social","nome_fantasia"});
		tbEmpresas.setColumnCollapsingAllowed(true);
		tbEmpresas.setColumnHeader("cnpj", "CNPJ");
		tbEmpresas.setColumnHeader("razao_social", "Razão Social");
		tbEmpresas.setColumnHeader("nome_fantasia", "Nome Fantasia");
		tbEmpresas.setColumnCollapsible("razao_social", false);
	//	tbEmpresas.setSizeFull();
		tbEmpresas.setSizeUndefined();
		tbEmpresas.setHeight("200px");
		tbEmpresas.addGeneratedColumn("Permissões", new Table.ColumnGenerator() {
			
			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				
				final Integer codAtual = Integer.parseInt(source.getItem(itemId).getItemProperty("id").getValue().toString());
				
				LoginDAO lDAO = new LoginDAO();
				List<EmpresasUsuario> result = lDAO.getEmpresasUsuario(user);
				
				boolean check = false;
				for(EmpresasUsuario e:result){
					
					if(codAtual == e.getEmpresa().getId()){
						check = true;
						break;
					}
				}
				
				final CheckBox chkPermissao = new CheckBox();	
				chkPermissao.setValue(check);
				chkPermissao.setImmediate(true);
				
				if(codAtual == OpusERP4UI.getEmpresa().getId() && user.getId() == OpusERP4UI.getUsuarioLogadoUI().getId()){
					chkPermissao.setEnabled(false);
				}else{
					chkPermissao.setEnabled(true);
				}
				
				chkPermissao.addListener(new Property.ValueChangeListener() {
									
					@Override
					public void valueChange(ValueChangeEvent event) {
								
						EmpresaUsuarioDAO euDAO = new EmpresaUsuarioDAO();
												
						if(chkPermissao.getValue().booleanValue()){
							euDAO.adicionaRegistro(user.getId(), codAtual);						
						}else{							
							euDAO.deletarRegistro(user.getId(), codAtual);							
						}
					}
				});
				return chkPermissao;
			}
		});
		
		return tbEmpresas;
	}
	public TextField builTfBusca(){
		tfBuscaEmpresa = new TextField();
		tfBuscaEmpresa.setWidth("100%");
		tfBuscaEmpresa.setTextChangeEventMode(TextChangeEventMode.LAZY);
		tfBuscaEmpresa.focus();
		tfBuscaEmpresa.setInputPrompt("Busca...");
		
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
	
}

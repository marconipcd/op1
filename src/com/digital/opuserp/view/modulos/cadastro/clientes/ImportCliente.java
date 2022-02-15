package com.digital.opuserp.view.modulos.cadastro.clientes;

import java.util.Date;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.ClienteDAO;
import com.digital.opuserp.dao.EnderecoDAO;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.Empresa;
import com.digital.opuserp.domain.Endereco;
import com.digital.opuserp.domain.EnderecoCobranca;
import com.digital.opuserp.domain.EnderecoEntrega;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.view.modulos.cadastro.clientes.ClienteEditor.ClienteEvent;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.filter.Like;
import com.vaadin.data.util.filter.Or;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class ImportCliente extends Window{
	
	Table tbCliente;
	TextField tfBusca;
	
	boolean cnpjCadast;
	
	JPAContainer<Cliente> containerCliente;
	JPAContainer<Endereco> containerEndereco;
	JPAContainer<EnderecoEntrega> containerEndEntrega;
	JPAContainer<EnderecoCobranca> containerEndCobranca;
	Integer codSubModulo;
	Cliente clienteNovo;
	
	public ImportCliente(String title, Boolean modal,Integer codSubModulo){
		setCaption(title);
		setModal(modal);
		setResizable(false);
		center();
		
		this.codSubModulo = codSubModulo;
		tfBusca = buildTfBusca();
		tbCliente = buildTbCliente();
		
		
		VerticalLayout vlRoot = new VerticalLayout();
		vlRoot.setMargin(true);
		vlRoot.setSpacing(true);
		
		vlRoot.addComponent(tfBusca);
		vlRoot.addComponent(tbCliente);
		
		ShortcutListener close = new ShortcutListener("Fechar", ShortcutAction.KeyCode.ESCAPE, null) {
			
			@Override
			public void handleAction(Object sender, Object target) {
				close();
			}
		};
		addShortcutListener(close);
		
		setContent(vlRoot);
	}
	
	
	public JPAContainer<Cliente> buildContainerCliente(){
		containerCliente = new JPAContainerFactory().makeBatchable(Cliente.class, ConnUtil.getEntity());
		containerCliente.setAutoCommit(false);
		containerCliente.addNestedContainerProperty("empresa.nome_fantasia");
		containerCliente.addContainerFilter(Filters.not(Filters.eq("empresa", OpusERP4UI.getEmpresa().getId())));
		return containerCliente;
	}
	
	public TextField buildTfBusca(){
		tfBusca = new TextField();
		tfBusca.setWidth("100%");
		tfBusca.focus();
		tfBusca.setTextChangeEventMode(TextChangeEventMode.LAZY);
		tfBusca.addListener(new FieldEvents.TextChangeListener() {
			
			@Override
			public void textChange(TextChangeEvent event) {
				addFilter(event.getText());
			}
		});
		
		return tfBusca;
	}
	
	public Table buildTbCliente(){
		tbCliente = new Table(null, buildContainerCliente()){
			@Override
			protected String formatPropertyValue(Object rowId, Object colId,Property<?> property) {
				
				if(colId.equals("id")){
					
					if(tbCliente.getItem(rowId).getItemProperty("id").getValue() != null){						
						return ((Integer)tbCliente.getItem(rowId).getItemProperty("id").getValue()).toString();				
					}					
				} else{
					return super.formatPropertyValue(rowId, colId, property);
				}
				
				
				return "";
				
				
				
			}
		};
		tbCliente.setWidth("970px");
		tbCliente.setHeight("230px");
		tbCliente.setSelectable(true);
		
		tbCliente.setVisibleColumns(new Object[]{"id", "nome_razao", "doc_cpf_cnpj", "status", "empresa.nome_fantasia", "data_alteracao"});
		
		tbCliente.setColumnHeader("id", "Código");
		tbCliente.setColumnHeader("nome_razao", "Nome");
		tbCliente.setColumnHeader("doc_cpf_cnpj", "CPF/CNPJ");
		tbCliente.setColumnHeader("status", "Status");
		tbCliente.setColumnHeader("empresa.nome_fantasia", "Empresa");
		tbCliente.setColumnHeader("data_alteracao", "Data de Alteração");
		
		tbCliente.setImmediate(true);	
		
		tbCliente.addListener(new ItemClickEvent.ItemClickListener() {
			
			@Override
			public void itemClick(ItemClickEvent event) {
				try{
					if(event.isDoubleClick() && tbCliente.getValue() != null){
						final Integer codCliente = Integer.parseInt(tbCliente.getItem(tbCliente.getValue()).getItemProperty("id").getValue().toString());			
			
						final String cpfatual = tbCliente.getItem(tbCliente.getValue()).getItemProperty("doc_cpf_cnpj").getValue().toString();
						
						ClienteDAO cDAO = new ClienteDAO();
						Cliente c = cDAO.cpfCnpjCadastrado(OpusERP4UI.getEmpresa(),cpfatual);						
						if (c != null && c.getId() != codCliente) {
								Notify.Show("O Cliente já pertence a esta empresa!", Notify.TYPE_WARNING);
						}else{						
							importarCliente(codCliente);
							close();												
						}									
					}
				}catch(Exception e){					
					e.printStackTrace();
				}
				
			}
		});
		
		ShortcutListener importa = new ShortcutListener("Enter", ShortcutAction.KeyCode.ENTER, null) {
			
			@Override
			public void handleAction(Object sender, Object target) {
				try{
					final Integer codCliente = Integer.parseInt(tbCliente.getItem(tbCliente.getValue()).getItemProperty("id").getValue().toString());
					
					final String cpfatual = tbCliente.getItem(tbCliente.getValue()).getItemProperty("doc_cpf_cnpj").getValue().toString();
					
					ClienteDAO cDAO = new ClienteDAO();
					Cliente c = cDAO.cpfCnpjCadastrado(OpusERP4UI.getEmpresa(),cpfatual);
					
					if (c != null && c.getId() != codCliente) {
							Notification.show("O Cliente já pertence a esta empresa!");												
						}else{						
							importarCliente(codCliente);
							close();
						}
					
				}catch(Exception e){
					System.out.println("Erro ao pegar cod do Cliente : "+e.getMessage());
				}
			}
		};
		tbCliente.addShortcutListener(importa);
		
		return tbCliente;
	}
	
	public Cliente ConfigCliente(Cliente cliente){
		cliente.setDoc_cpf_cnpj(tbCliente.getItem(tbCliente.getValue()).getItemProperty("doc_cpf_cnpj").toString());
		cliente.setDoc_rg_insc_estadual(tbCliente.getItem(tbCliente.getValue()).getItemProperty("doc_rg_insc_estadual").toString());
		cliente.setTratamento(tbCliente.getItem(tbCliente.getValue()).getItemProperty("tratamento").toString());
		cliente.setComo_quer_ser_chamado(tbCliente.getItem(tbCliente.getValue()).getItemProperty("como_quer_ser_chamado").toString());
		cliente.setNome_razao(tbCliente.getItem(tbCliente.getValue()).getItemProperty("nome_razao").toString());
		cliente.setNome_fantasia(tbCliente.getItem(tbCliente.getValue()).getItemProperty("nome_fantasia").toString());
		cliente.setContato(tbCliente.getItem(tbCliente.getValue()).getItemProperty("contato").toString());
		cliente.setSexo(tbCliente.getItem(tbCliente.getValue()).getItemProperty("sexo").toString());
		cliente.setEndereco_principal((Endereco)tbCliente.getItem(tbCliente.getValue()).getItemProperty("endereco_principal").getValue());
		
		cliente.setDdd_fone1(tbCliente.getItem(tbCliente.getValue()).getItemProperty("ddd_fone1").toString());
		cliente.setTelefone1(tbCliente.getItem(tbCliente.getValue()).getItemProperty("telefone1").toString());
		cliente.setDdd_fone2(tbCliente.getItem(tbCliente.getValue()).getItemProperty("ddd_fone2").toString());
		cliente.setTelefone2(tbCliente.getItem(tbCliente.getValue()).getItemProperty("telefone2").toString());
		cliente.setDdd_cel1(tbCliente.getItem(tbCliente.getValue()).getItemProperty("ddd_cel1").toString());
		cliente.setCelular1(tbCliente.getItem(tbCliente.getValue()).getItemProperty("celular1").toString());
		cliente.setDdd_cel2(tbCliente.getItem(tbCliente.getValue()).getItemProperty("ddd_cel2").toString());
		cliente.setCelular2(tbCliente.getItem(tbCliente.getValue()).getItemProperty("celular2").toString());
		cliente.setEmail(tbCliente.getItem(tbCliente.getValue()).getItemProperty("email").toString());	
		cliente.setData_nascimento((Date)tbCliente.getItem(tbCliente.getValue()).getItemProperty("data_nascimento").getValue());
		
		cliente.setEmailAlternativo(tbCliente.getItem(tbCliente.getValue()).getItemProperty("emailAlternativo").toString());
		if(tbCliente.getItem(tbCliente.getValue()).getItemProperty("sexo") != null){
			cliente.setSite(tbCliente.getItem(tbCliente.getValue()).getItemProperty("site").toString());
		}
		//if(tbCliente.getItem(tbCliente.getValue()).getItemProperty("como_nos_conheceu") != null){
//			cliente.setComo_nos_conheceu(tbCliente.getItem(tbCliente.getValue()).getItemProperty("como_nos_conheceu").toString());
	//	}
		if(tbCliente.getItem(tbCliente.getValue()).getItemProperty("obs") != null){
			cliente.setObs(tbCliente.getItem(tbCliente.getValue()).getItemProperty("obs").toString());
		}
		
		return cliente;
	}
	
	public void importarCliente(Integer codCliente){
		
		// BUSCA O ENDERECO PELO CLIENTE
		final EnderecoDAO endDao = new EnderecoDAO();
				
		tbCliente.getItem(tbCliente.getValue()).getItemProperty("id").setValue(null);
		Integer codEmpresa = Integer.parseInt(OpusERP4UI.getEmpresa().getId().toString());
		tbCliente.getItem(tbCliente.getValue()).getItemProperty("empresa").setValue(new Empresa(codEmpresa));
		
		Cliente cliente = new Cliente();
		
		cliente = ConfigCliente(cliente);
		
			
		final BeanItem<Cliente> ClienteItem = new BeanItem<Cliente>(cliente);
		
		ClienteItem.getItemProperty("status").setValue("INATIVO");
		
		ClienteEditor editor = new ClienteEditor(ClienteItem, "Importar Cliente", true, codCliente,codSubModulo);
		
		// CLIENTE
		editor.addListerner(new ClienteEditor.ClientesListerner() {
			
			@Override
			public void onClose(ClienteEvent event) {
				try{
					Cliente addCliente = ClienteItem.getBean();
					addCliente.setEmpresa(new Empresa(OpusERP4UI.getEmpresa().getId()));
					ClienteDAO clDao = new ClienteDAO();
					
					clienteNovo = clDao.saveImporteCliente(addCliente,event.getEnderecos());
										
					
				}catch(Exception e){
					e.printStackTrace();
					System.out.println("Erro ao comitar container de Cliente: "+e.getMessage());
					System.out.println("Causado por: "+e.getCause());
				}
			}
		});
		ClienteDAO clDao = new ClienteDAO();
		final Integer proxCodCliente = Integer.parseInt(clDao.getNextID());
		
		
		
		getUI().addWindow(editor);
		
	}
	
	public void addFilter(String s){
		containerCliente.removeAllContainerFilters();
		containerCliente.addContainerFilter(Filters.not(Filters.eq("empresa", OpusERP4UI.getEmpresa().getId())));
		containerCliente.addContainerFilter(
				new Or(
						new Like("id", s, false),
						new Like("nome_razao", "%"+s+"%", false),
						new Like("doc_cpf_cnpj", s, false),
						new Like("status", s+"%", false),
						new Like("empresa.nome_fantasia", "%"+s+"%", false)
				)); 
		containerCliente.applyFilters();
	}
	

	
}

package com.digital.opuserp.view.modulos.cadastro.fornecedores;

import java.util.ArrayList;
import java.util.List;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.FornecedorDAO;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.Cnae;
import com.digital.opuserp.domain.Empresa;
import com.digital.opuserp.domain.Fornecedor;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.view.modulos.cadastro.fornecedores.FornecedorEditor.FornecedorEvent;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Property;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.filter.Like;
import com.vaadin.data.util.filter.Not;
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

public class ImportarFornecedor extends Window{
	
	Table tbFornecedor;
	TextField tfBusca;
	Integer codFornecedor;
	BeanItem<Fornecedor> fornecedorItem;
	boolean cnpjCadast;
	
	JPAContainer<Fornecedor> container;
	
	Fornecedor novoFornecedor;
	
	public ImportarFornecedor (String title, Boolean modal){
		setCaption(title);
		setModal(modal);
		setResizable(false);
		center();
		
		
		tfBusca = buildTfBusca();
		tbFornecedor = buildTbFornecedor();
		
		
		VerticalLayout vlRoot = new VerticalLayout();
		vlRoot.setMargin(true);
		vlRoot.setSpacing(true);
		
		vlRoot.addComponent(tfBusca);
		vlRoot.addComponent(tbFornecedor);
		
		ShortcutListener close = new ShortcutListener("Fechar", ShortcutAction.KeyCode.ESCAPE, null) {
			
			@Override
			public void handleAction(Object sender, Object target) {
				close();
			}
		};
		addShortcutListener(close);
		
		setContent(vlRoot);

	}
	
	
	public JPAContainer<Fornecedor> buildContainerFornecedor(){
		container = new JPAContainerFactory().makeBatchable(Fornecedor.class, ConnUtil.getEntity());
		container.setAutoCommit(false);
		container.addNestedContainerProperty("empresa.nome_fantasia");
		container.addContainerFilter(Filters.not(Filters.eq("empresa", OpusERP4UI.getEmpresa().getId())));
		return container;
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
	
	public Table buildTbFornecedor(){
		
		tbFornecedor = new Table(null, buildContainerFornecedor()){
			@Override
			protected String formatPropertyValue(Object rowId, Object colId,
					Property<?> property) {
				
				if(colId.equals("id")){
						
						if(tbFornecedor.getItem(rowId).getItemProperty("id").getValue() != null){						
							return ((Integer)tbFornecedor.getItem(rowId).getItemProperty("id").getValue()).toString();				
						}					
				} else{
						return super.formatPropertyValue(rowId, colId, property);
				}
				return ""; 
				
			}
		};
		
		tbFornecedor.setWidth("950px");
		tbFornecedor.setHeight("230px");
		tbFornecedor.setSelectable(true);
		
		tbFornecedor.setVisibleColumns(new Object[]{"id", "razao_social", "cnpj", "status","empresa.nome_fantasia", "data_alteracao"});
		
		tbFornecedor.setColumnHeader("id", "Código");
		tbFornecedor.setColumnHeader("razao_social", "Nome");
		tbFornecedor.setColumnHeader("cnpj", "CNPJ");
		tbFornecedor.setColumnHeader("status", "Status");
		tbFornecedor.setColumnHeader("empresa.nome_fantasia", "Empresa");
		tbFornecedor.setColumnHeader("data_alteracao", "Data de Alteração");
		//tbFornecedor.setConverter("id", null);
			
		
		tbFornecedor.addListener(new ItemClickEvent.ItemClickListener() {
			
			@Override
			public void itemClick(ItemClickEvent event) {							
				try{
					if(event.isDoubleClick()){
						
						if(tbFornecedor.getValue() != null){
							codFornecedor = Integer.parseInt(tbFornecedor.getValue().toString());			
				
							final String cpfatual = tbFornecedor.getItem(tbFornecedor.getValue()).getItemProperty("cnpj").getValue().toString();
							
							FornecedorDAO cDAO = new FornecedorDAO();
							cnpjCadast = cDAO.cpfCnpjCadastrado(OpusERP4UI.getEmpresa(), cpfatual);
							if(cnpjCadast == false){						
								Notification.show("O fornecedor já pertence a esta empresa!");
							}else{						
								importarFornecedor(codFornecedor);
								close();
							}						
						}
					}
				}catch(Exception e){
					System.out.println("Erro ao pegar cod do Fornecedor : "+e.getMessage()); e.printStackTrace();
				}
			}
		});
		
		ShortcutListener importa = new ShortcutListener("Enter", ShortcutAction.KeyCode.ENTER, null) {
			
			@Override
			public void handleAction(Object sender, Object target) {
				try{
					
					if(tbFornecedor.getValue() != null){
					
						codFornecedor = Integer.parseInt(tbFornecedor.getValue().toString());
						final String cpfatual = tbFornecedor.getItem(tbFornecedor.getValue()).getItemProperty("cnpj").getValue().toString();
						
						FornecedorDAO fDAO = new FornecedorDAO();
						cnpjCadast = fDAO.cpfCnpjCadastrado(OpusERP4UI.getEmpresa(), cpfatual);
						if(cnpjCadast == false){						
							//Impossível importar fornecedor selecionado
							Notification.show("O fornecedor já pertence a esta empresa!");
						}else{						
							importarFornecedor(codFornecedor);
							close();
						}
					}
				}catch(Exception e){
					System.out.println("Erro ao pegar cod do Fornecedor : "+e.getMessage());
				}
			}
		};
		tbFornecedor.addShortcutListener(importa);
		
		return tbFornecedor;
	}

	public Fornecedor ConfigFornecedor(Fornecedor fornecedor){
		// FORNECEDOR
		fornecedor.setCnpj(tbFornecedor.getItem(tbFornecedor.getValue()).getItemProperty("cnpj").toString());
		fornecedor.setRazao_social(tbFornecedor.getItem(tbFornecedor.getValue()).getItemProperty("razao_social").toString());
		fornecedor.setStatus(tbFornecedor.getItem(tbFornecedor.getValue()).getItemProperty("status").toString());
		fornecedor.setInsc_estadual(tbFornecedor.getItem(tbFornecedor.getValue()).getItemProperty("insc_estadual").toString());
		fornecedor.setFantasia(tbFornecedor.getItem(tbFornecedor.getValue()).getItemProperty("fantasia").toString());
		fornecedor.setFornecedor(true);
		
		//ENDERECO
		fornecedor.setCep(tbFornecedor.getItem(tbFornecedor.getValue()).getItemProperty("cep").toString());
		fornecedor.setEndereco(tbFornecedor.getItem(tbFornecedor.getValue()).getItemProperty("endereco").toString());
		fornecedor.setNumero(tbFornecedor.getItem(tbFornecedor.getValue()).getItemProperty("numero").toString());
		fornecedor.setBairro(tbFornecedor.getItem(tbFornecedor.getValue()).getItemProperty("bairro").toString());
		fornecedor.setCidade(tbFornecedor.getItem(tbFornecedor.getValue()).getItemProperty("cidade").toString());
		fornecedor.setUf(tbFornecedor.getItem(tbFornecedor.getValue()).getItemProperty("uf").toString());
		fornecedor.setPais(tbFornecedor.getItem(tbFornecedor.getValue()).getItemProperty("pais").toString());
		fornecedor.setComplemento(tbFornecedor.getItem(tbFornecedor.getValue()).getItemProperty("complemento").toString());
		fornecedor.setPonto_referencia(tbFornecedor.getItem(tbFornecedor.getValue()).getItemProperty("ponto_referencia").toString());
		
		//FONE
		fornecedor.setDdd_fone1(tbFornecedor.getItem(tbFornecedor.getValue()).getItemProperty("ddd_fone1").toString());
		fornecedor.setFone1(tbFornecedor.getItem(tbFornecedor.getValue()).getItemProperty("fone1").toString());
		fornecedor.setDdd_fone2(tbFornecedor.getItem(tbFornecedor.getValue()).getItemProperty("ddd_fone2").toString());
		fornecedor.setFone2(tbFornecedor.getItem(tbFornecedor.getValue()).getItemProperty("fone2").toString());
		fornecedor.setDdd_fone3(tbFornecedor.getItem(tbFornecedor.getValue()).getItemProperty("ddd_fone3").toString());
		fornecedor.setFone3(tbFornecedor.getItem(tbFornecedor.getValue()).getItemProperty("fone3").toString());
		fornecedor.setFone0800(tbFornecedor.getItem(tbFornecedor.getValue()).getItemProperty("fone0800").toString());
		
		//DADOS		
		fornecedor.setEmail(tbFornecedor.getItem(tbFornecedor.getValue()).getItemProperty("email").toString());	
		fornecedor.setHome_page(tbFornecedor.getItem(tbFornecedor.getValue()).getItemProperty("home_page").toString());
//		fornecedor.setLimite_de_credito((Float) tbFornecedor.getItem(tbFornecedor.getValue()).getItemProperty("limite_de_credito").getValue());
		fornecedor.setMsn(tbFornecedor.getItem(tbFornecedor.getValue()).getItemProperty("msn").toString());
		fornecedor.setObservacao(tbFornecedor.getItem(tbFornecedor.getValue()).getItemProperty("observacao").toString());
		fornecedor.setPrincipais_produtos(tbFornecedor.getItem(tbFornecedor.getValue()).getItemProperty("principais_produtos").toString());
		fornecedor.setTipo_de_frete(tbFornecedor.getItem(tbFornecedor.getValue()).getItemProperty("tipo_de_frete").toString());
		fornecedor.setCnae_fiscal((Cnae) tbFornecedor.getItem(tbFornecedor.getValue()).getItemProperty("cnae_fiscal").getValue());
		
		
		//CONTATOS
		fornecedor.setDtpvendas(tbFornecedor.getItem(tbFornecedor.getValue()).getItemProperty("dtpvendas").toString());
		fornecedor.setDtpvendas2(tbFornecedor.getItem(tbFornecedor.getValue()).getItemProperty("dtpvendas2").toString());
		fornecedor.setContato_dpt1(tbFornecedor.getItem(tbFornecedor.getValue()).getItemProperty("contato_dpt1").toString());
		fornecedor.setContato_dpt2(tbFornecedor.getItem(tbFornecedor.getValue()).getItemProperty("contato_dpt2").toString());
		fornecedor.setDdd_tel_principal_dpt1(tbFornecedor.getItem(tbFornecedor.getValue()).getItemProperty("ddd_tel_principal_dpt1").toString());
		fornecedor.setDdd_tel_principal_dpt2(tbFornecedor.getItem(tbFornecedor.getValue()).getItemProperty("ddd_tel_principal_dpt2").toString());
		fornecedor.setDdd_tel_alternativo_dpt1(tbFornecedor.getItem(tbFornecedor.getValue()).getItemProperty("ddd_tel_alternativo_dpt1").toString());
		fornecedor.setDdd_tel_alternativo_dpt2(tbFornecedor.getItem(tbFornecedor.getValue()).getItemProperty("ddd_tel_alternativo_dpt2").toString());
		fornecedor.setTel_principal_dpt1(tbFornecedor.getItem(tbFornecedor.getValue()).getItemProperty("tel_principal_dpt1").toString());
		fornecedor.setTel_principal_dpt2(tbFornecedor.getItem(tbFornecedor.getValue()).getItemProperty("tel_principal_dpt2").toString());
		fornecedor.setTel_alternativo_dpt1(tbFornecedor.getItem(tbFornecedor.getValue()).getItemProperty("tel_alternativo_dpt1").toString());
		fornecedor.setTel_alternativo_dpt2(tbFornecedor.getItem(tbFornecedor.getValue()).getItemProperty("tel_alternativo_dpt2").toString());
		fornecedor.setSkype_dpt1(tbFornecedor.getItem(tbFornecedor.getValue()).getItemProperty("skype_dpt1").toString());
		fornecedor.setSkype_dpt2(tbFornecedor.getItem(tbFornecedor.getValue()).getItemProperty("skype_dpt2").toString());
		fornecedor.setEmail_dpt1(tbFornecedor.getItem(tbFornecedor.getValue()).getItemProperty("email_dpt1").toString());
		fornecedor.setEmail_dpt2(tbFornecedor.getItem(tbFornecedor.getValue()).getItemProperty("email_dpt2").toString());
		
		// REPRESENTANTE
		fornecedor.setRepresentante(tbFornecedor.getItem(tbFornecedor.getValue()).getItemProperty("representante").toString());
		fornecedor.setDdd_fone1_representante(tbFornecedor.getItem(tbFornecedor.getValue()).getItemProperty("ddd_fone1_representante").toString());
		fornecedor.setFone1_representante(tbFornecedor.getItem(tbFornecedor.getValue()).getItemProperty("fone1_representante").toString());
		fornecedor.setDdd_fone2_representante(tbFornecedor.getItem(tbFornecedor.getValue()).getItemProperty("ddd_fone2_representante").toString());
		fornecedor.setFone2_representante(tbFornecedor.getItem(tbFornecedor.getValue()).getItemProperty("fone2_representante").toString());
		fornecedor.setDdd_fone3_representante(tbFornecedor.getItem(tbFornecedor.getValue()).getItemProperty("ddd_fone3_representante").toString());
		fornecedor.setFone3_representante(tbFornecedor.getItem(tbFornecedor.getValue()).getItemProperty("fone3_representante").toString());
		fornecedor.setDdd_fone4_representante(tbFornecedor.getItem(tbFornecedor.getValue()).getItemProperty("ddd_fone4_representante").toString());
		fornecedor.setFone4_representante(tbFornecedor.getItem(tbFornecedor.getValue()).getItemProperty("fone4_representante").toString());
		fornecedor.setEmail_representante(tbFornecedor.getItem(tbFornecedor.getValue()).getItemProperty("email_representante").toString());
		fornecedor.setMsn_representante(tbFornecedor.getItem(tbFornecedor.getValue()).getItemProperty("msn_representante").toString());
		fornecedor.setObservacao_representante(tbFornecedor.getItem(tbFornecedor.getValue()).getItemProperty("observacao_representante").toString());
		fornecedor.setSite_representante(tbFornecedor.getItem(tbFornecedor.getValue()).getItemProperty("site_representante").toString());
			
		return fornecedor;
	}
	
	public void importarFornecedor(Integer codFornecedor){


		tbFornecedor.getItem(tbFornecedor.getValue()).getItemProperty("id").setValue(null);
		Integer codEmpresa = Integer.parseInt(OpusERP4UI.getEmpresa().getId().toString());
		tbFornecedor.getItem(tbFornecedor.getValue()).getItemProperty("empresa").setValue(new Empresa(codEmpresa));
		
		Fornecedor fornecedor = new Fornecedor();	
		fornecedor = ConfigFornecedor(fornecedor);

		FornecedorEditor editor = new FornecedorEditor(fornecedor, "Importar Fornecedor", true);
		
		
		editor.addListerner(new FornecedorEditor.FornecedorListerner() {
			
			@Override
			public void onClose(FornecedorEvent event) {
				try {
					Fornecedor addFornecedor = event.getFornecedor();
					addFornecedor.setEmpresa(new Empresa(OpusERP4UI.getEmpresa().getId()));
					FornecedorDAO fDao = new FornecedorDAO();
					
					novoFornecedor = fDao.saveFornecedor(addFornecedor);
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("Erro ao comitar container de Fornecedor: "+e.getMessage());
					System.out.println("Causado por: "+e.getCause());					
				}				
			}
		});
		
//		FornecedorDAO fDao = new FornecedorDAO();
//		final Integer proxCodFornecedor = Integer.parseInt(fDao.getNextID());

		getUI().addWindow(editor);
		
	}
	
//	public void addFilter(String s){
//		containerFornecedor.removeAllContainerFilters();
//		containerFornecedor.addContainerFilter(new Not(new Equal("empresa", OpusERP4UI.getEmpresa().getId())));
//		containerFornecedor.addContainerFilter(
//				Filters.or(
//						new Equal("id", s),
//						new Like("razao_social", "%"+s+"%", false),
//						new Like("fantasia", "%"+s+"%", false),
//						new Like("cnpj", s, false),
//						new Like("status", s+"%", false)
//				));
//		containerFornecedor.applyFilters();
//		
//	}
	
	public void addFilter(String s) {
		container.removeAllContainerFilters();
		container.addContainerFilter(new Not(new Equal("empresa", OpusERP4UI.getEmpresa().getId())));
		//container.addContainerFilter(Filters.eq("empresaId", OpusERP4UI.getEmpresa().getId()));
		//container.addContainerFilter(Filters.eq("status", "ATIVO"));
		
		Object[] collums = tbFornecedor.getVisibleColumns();		
		List<Filter> filtrosInternos = new ArrayList<Filter>();		

		try {
			Integer cod = Integer.parseInt(s);
			
			for(Object c:collums){		 			
				if(!tbFornecedor.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == Integer.class){					   	
					filtrosInternos.add(new Like(c.toString(), "%"+cod+"%", false));
				}			
				
				if(!tbFornecedor.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == String.class){					   	
					filtrosInternos.add(new Like(c.toString(), "%"+s+"%", false));
				}	
			}
			
		} catch (Exception e) {
			
			for(Object c:collums){		 			
				if(!tbFornecedor.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == String.class){					   	
					filtrosInternos.add(new Like(c.toString(), "%"+s+"%", false));
				}	
			}
		}
		
		
		container.addContainerFilter(Filters.or(filtrosInternos));
		container.applyFilters();	
		
		//container.sort(new String[]{"nome"},new boolean[]{true});
	}

}

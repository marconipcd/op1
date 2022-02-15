package com.digital.opuserp.view.modulos.configuracoes.config_sistema.usuario;

import java.util.List;

import javax.persistence.EntityManager;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.GerenciarModuloDAO;
import com.digital.opuserp.dao.SetoresDAO;
import com.digital.opuserp.domain.Empresa;
import com.digital.opuserp.domain.EmpresasUsuario;
import com.digital.opuserp.domain.Modulo;
import com.digital.opuserp.domain.ModulosEmpresa;
import com.digital.opuserp.domain.Setores;
import com.digital.opuserp.domain.SubModulo;
import com.digital.opuserp.domain.Usuario;
import com.digital.opuserp.util.ConnUtil;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class PermissoesGeraisEditor extends Window {
	
	
	//Usuário Logado
	Usuario usuario_selecionado;
	
	//Containers	
	BeanItemContainer<Empresa> empresasUsers;
	BeanItemContainer<Setores> setor_empresa;
	BeanItemContainer<Modulo> modulo_empresa;
	BeanItemContainer<SubModulo> submodulo_empresa;
		
	//Tables
	Table tbEmpresas;
	TreeTable tb;
	Table tbModulos;
	Table tbSubModulos;
	Table tbPermissao;
	
	VerticalLayout vlRoot;
	HorizontalLayout hl1;
	VerticalLayout hl1_1;
	HorizontalLayout hl2;
	
	GerenciarModuloDAO gmDAO;

	public PermissoesGeraisEditor(String title, boolean isModal, Usuario usuario_selecionado){
		setCaption(title);
		setModal(isModal);
		this.usuario_selecionado = usuario_selecionado;
		setWidth("973px");
		setHeight("946px");
		
		gmDAO = new GerenciarModuloDAO();
		
		vlRoot = new VerticalLayout();
		vlRoot.setSizeFull();
		vlRoot.setMargin(true);
		vlRoot.setSpacing(true);
		
		
		setContent(vlRoot);
		buildLayout();
	}
	
	private void buildLayout(){
		
		vlRoot.addComponent(buildTbEmpresas());
		hl1 = new HorizontalLayout(){
			{
				setWidth("100%");
				addComponent(buildTbModulos());
				
				hl1_1 = new VerticalLayout(){
					{
						addComponent(buildTbSetores());
						
						final ComboBox cbPermitirVisualizarTodosCrms = new ComboBox("Visualizar CRMs de todos os usuários ?");
						cbPermitirVisualizarTodosCrms.addItem("SIM");
						cbPermitirVisualizarTodosCrms.addItem("NAO");
						cbPermitirVisualizarTodosCrms.setNullSelectionAllowed(false);
						
						final Usuario u  = usuario_selecionado;
						
						cbPermitirVisualizarTodosCrms.addValueChangeListener(new Property.ValueChangeListener() {
							
							@Override
							public void valueChange(ValueChangeEvent event) {
								
								EntityManager em = ConnUtil.getEntity();
								
								
								if(cbPermitirVisualizarTodosCrms.getValue().toString().equals("SIM")){
									u.setVisualizar_todos_crm(1);
								}else{
									u.setVisualizar_todos_crm(0);
								}
								
								em.getTransaction().begin();
								em.merge(u);
								em.getTransaction().commit();
								
								//Atualiza usuário logado
								usuario_selecionado = u;
								if(usuario_selecionado == OpusERP4UI.getUsuarioLogadoUI()){
									OpusERP4UI.setUsuarioLogadoUI(usuario_selecionado);									
								}
							}
						});
						
						if(u.getVisualizar_todos_crm() == 1){
							cbPermitirVisualizarTodosCrms.select("SIM");
						}
						if(u.getVisualizar_todos_crm() == 0){
							cbPermitirVisualizarTodosCrms.select("NAO");
						}
						
						
						
						addComponent(cbPermitirVisualizarTodosCrms);
					}
				};
				
				addComponent(hl1_1);
			}
		};
		
		hl2 = new HorizontalLayout(){
			{
				setWidth("100%");
				addComponent(buildTbSubModulos());
				addComponent(buildTbPermissoes());
			}
		};
		
		vlRoot.addComponent(hl1);
		vlRoot.addComponent(hl2);
		
	}	
	private Table buildTbEmpresas(){
		
		tbEmpresas = new Table("Empresas", buildContainerEmpresas());
		tbEmpresas.setSelectable(true);
		tbEmpresas.setVisibleColumns(new Object[]{"cnpj","razao_social","nome_fantasia"});
		tbEmpresas.setColumnCollapsingAllowed(true);
		tbEmpresas.setColumnHeader("cnpj", "CNPJ");
		tbEmpresas.setColumnHeader("razao_social", "Razão Social");
		tbEmpresas.setColumnHeader("nome_fantasia", "Nome Fantasia");		
		tbEmpresas.setColumnCollapsible("razao_social", false);
		tbEmpresas.setColumnCollapsed("nome_fantasia", true);
		tbEmpresas.setWidth("100%");
		tbEmpresas.setHeight("130px");
		tbEmpresas.setImmediate(true);
		tbEmpresas.addListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				
				hl1_1.replaceComponent(tb, buildTbSetores());
				hl1.replaceComponent(tbModulos, buildTbModulos());
				
			}
		});
		
		
		return tbEmpresas;
	}
	private BeanItemContainer<Empresa> buildContainerEmpresas(){
		empresasUsers = new BeanItemContainer<Empresa>(Empresa.class);		

		List<EmpresasUsuario> empresas = gmDAO.getEmpresasUsuarios(usuario_selecionado);				
		
		for(EmpresasUsuario e:empresas){
			empresasUsers.addBean(e.getEmpresa());
		}
		
		return empresasUsers;
	}
	private BeanItemContainer<Setores> buildContainerSetores(){
		setor_empresa = new BeanItemContainer<Setores>(Setores.class);		

		if(tbEmpresas.getValue() != null){
			
			
			List<Setores> result = gmDAO.getSetores((Integer) tbEmpresas.getItem(tbEmpresas.getValue()).getItemProperty("id").getValue());				
			setor_empresa.removeAllItems();
			for(Setores s:result){
				setor_empresa.addBean(s);
			}
		}else{
			setor_empresa.removeAllItems();
		}
		
		return setor_empresa;
	}
	private BeanItemContainer<Modulo> buildContainerModulos(){
		modulo_empresa = new BeanItemContainer<Modulo>(Modulo.class);		

		if(tbEmpresas.getValue() != null){
			
			
			List<ModulosEmpresa> result = gmDAO.getModulos((Integer) tbEmpresas.getItem(tbEmpresas.getValue()).getItemProperty("id").getValue());				
			modulo_empresa.removeAllItems();
			for(ModulosEmpresa s:result){
				modulo_empresa.addBean(s.getModulo());
			}
			if(submodulo_empresa != null){
				submodulo_empresa.removeAllItems();
			}
			if(tbPermissao != null){
				tbPermissao.removeAllItems();
			}
		}else{
			modulo_empresa.removeAllItems();
			if(submodulo_empresa != null){
				submodulo_empresa.removeAllItems();
			}
			if(tbPermissao != null){
				tbPermissao.removeAllItems();
			}
		}
		
		return modulo_empresa;
	}
	private BeanItemContainer<SubModulo> buildContainerSubModulos(){
		submodulo_empresa = new BeanItemContainer<SubModulo>(SubModulo.class);		

		if(tbModulos.getValue() != null && ((CheckBox)tbModulos.getColumnGenerator("x").generateCell(tbModulos, tbModulos.getValue(), "x")).getValue()){
			
			List<SubModulo> result = gmDAO.getSubModulos((Integer) tbModulos.getItem(tbModulos.getValue()).getItemProperty("id").getValue());				
			submodulo_empresa.removeAllItems();
			for(SubModulo s:result){
				submodulo_empresa.addBean(s);
			}
			if(tbPermissao != null){
				tbPermissao.removeAllItems();
			}
		}else{
			submodulo_empresa.removeAllItems();
			if(tbPermissao != null){
				tbPermissao.removeAllItems();
			}
		}
		
		return submodulo_empresa;
	}
	private TreeTable buildTbSetores(){
		tb = new TreeTable("Setores");
		
		tb.addContainerProperty("Cod", Integer.class, "");
		tb.addContainerProperty("Nome", String.class, "");
		
		//tb.setVisibleColumns(new Object[]{"Nome"});
		
		tb.setSelectable(true);				
		tb.setWidth("100%");
		tb.setHeight("175px");
		tb.setImmediate(true);
		
		
		
		
		
		List<Setores> setores = null;
		if(tbEmpresas.getValue() != null){
			setores = SetoresDAO.getSetoresRoot((Integer) tbEmpresas.getItem(tbEmpresas.getValue()).getItemProperty("id").getValue());
		}
		
		Integer i = 0;
		Integer rootPai;
		Integer rootFilho;
		Integer rootNeto;
		Integer rootBisNeto;
		Integer rootTataraneto;
		
		if(setores != null){
			for (Setores setor : setores) {
				tb.addItem(new Object[]{setor.getId(), setor.getNome()}, i);
				
				List<Setores> setores_filhos = SetoresDAO.getSetoresChild(setor);
				if(setores_filhos.size() > 0){
					tb.setChildrenAllowed(i, true);
					rootPai = i;
					for (Setores setores2 : setores_filhos) {
						i++;
						tb.addItem(new Object[]{setores2.getId(), setores2.getNome()}, i);
						tb.setParent(i, rootPai);
						
						List<Setores> setores_netos = SetoresDAO.getSetoresChild(setores2);
						if(setores_netos.size()>0){
							tb.setChildrenAllowed(i, true);
							rootFilho = i;
							
							for (Setores setores3 : setores_netos) {
								i++;
								tb.addItem(new Object[]{setores3.getId(), setores3.getNome()}, i);
								tb.setParent(i, rootFilho);
								
								List<Setores> setores_bisnetos = SetoresDAO.getSetoresChild(setores3);
								
								if(setores_bisnetos.size()>0){
									tb.setChildrenAllowed(i, true);
									rootNeto = i;
									
									for (Setores setores4 : setores_bisnetos) {
										i++;
										tb.addItem(new Object[]{setores4.getId(), setores4.getNome()}, i);
										tb.setParent(i, rootNeto);
										
										List<Setores> setores_tataranetos = SetoresDAO.getSetoresChild(setores4);
										
										if(setores_tataranetos.size() >0){
											tb.setChildrenAllowed(i, true);
											rootBisNeto = i;
											
											for (Setores setores5 : setores_tataranetos) {
												i++;
												tb.addItem(new Object[]{setores5.getId(), setores5.getNome()}, i);
												tb.setChildrenAllowed(i, false);
												tb.setParent(i, rootBisNeto);
											}
										}else{
											tb.setChildrenAllowed(i, false);
										}
									}
								}else{
									tb.setChildrenAllowed(i, false);
								}
							}
						}else{
							tb.setChildrenAllowed(i, false);
						}
					}
				}else{
					tb.setChildrenAllowed(i, false);
				}
				
				i++;
			}
		}
		
		tb.addGeneratedColumn("x", new Table.ColumnGenerator() {
			
			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				
				final Integer codSetor = Integer.parseInt(source.getItem(itemId).getItemProperty("Cod").getValue().toString());
				final Integer codEmpresa = (Integer) tbEmpresas.getItem(tbEmpresas.getValue()).getItemProperty("id").getValue();
				final Integer codUsuario = usuario_selecionado.getId();
				
				boolean check = gmDAO.checkSetoresUsuarioEmpresa(codUsuario, codEmpresa, codSetor);					
				
				CheckBox chk = new CheckBox();
				chk.setValue(check);
				chk.addListener(new Property.ValueChangeListener() {
					
					@Override
					public void valueChange(ValueChangeEvent event) {
						gmDAO.checkSetoresUsuarioEmpresaInsert(codUsuario, codEmpresa, codSetor);						
					}
				});
				
				return chk;
			}
		});
		//tb.setColumnExpandRatio("Nome", 1.0f);
		
		return tb;
	}
	private Table buildTbModulos(){
		tbModulos = new Table("Modulos", buildContainerModulos());
		tbModulos.setSelectable(true);
		tbModulos.setVisibleColumns(new Object[]{"nome_modulo"});		
		tbModulos.setWidth("100%");
		tbModulos.setHeight("175px");
		tbModulos.setImmediate(true);
		tbModulos.setColumnHeader("nome_modulo", "Nome");
		tbModulos.addGeneratedColumn("x", new Table.ColumnGenerator() {
			
			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				
				final Integer codModulo = Integer.parseInt(source.getItem(itemId).getItemProperty("id").getValue().toString());
				final Integer codEmpresa = (Integer) tbEmpresas.getItem(tbEmpresas.getValue()).getItemProperty("id").getValue();
				final Integer codUsuario = usuario_selecionado.getId();
				
				boolean check = gmDAO.checkModuloUsuarioEmpresa(codUsuario, codEmpresa, codModulo);				
				
				CheckBox chk = new CheckBox();
				chk.setValue(check);
				chk.setImmediate(true);
				chk.addListener(new Property.ValueChangeListener() {
					
					@Override
					public void valueChange(ValueChangeEvent event) {
						gmDAO.checkModuloUsuarioEmpresaInsert(codUsuario, codEmpresa, codModulo);
						//hl1.replaceComponent(tbModulos, buildTbModulos());
						hl2.replaceComponent(tbSubModulos, buildTbSubModulos());
						hl2.replaceComponent(tbPermissao, buildTbPermissoes());
					}
				});
				
				return chk;
			}
		});
		tbModulos.setColumnExpandRatio("nome_modulo", 1.0f);
		tbModulos.addListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {				
				hl2.replaceComponent(tbSubModulos, buildTbSubModulos());				
			}
		});
		
		return tbModulos;
	}
	private Table buildTbSubModulos(){
		tbSubModulos = new Table("SubModulos", buildContainerSubModulos());
		tbSubModulos.setSelectable(true);
		tbSubModulos.setVisibleColumns(new Object[]{"nome"});		
		tbSubModulos.setWidth("100%");
		tbSubModulos.setHeight("130px");
		tbSubModulos.setImmediate(true);
		tbSubModulos.setColumnHeader("nome", "Nome");
		tbSubModulos.addGeneratedColumn("x", new Table.ColumnGenerator() {
			
			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				
				final Integer codSubModulo = Integer.parseInt(source.getItem(itemId).getItemProperty("id").getValue().toString());
				final Integer codEmpresa = (Integer) tbEmpresas.getItem(tbEmpresas.getValue()).getItemProperty("id").getValue();
				final Integer codUsuario = usuario_selecionado.getId();
				
				boolean check = gmDAO.checkSubModuloUsuarioEmpresa(codUsuario, codEmpresa, codSubModulo);				
				
				CheckBox chk = new CheckBox();
				chk.setValue(check);
				chk.addListener(new Property.ValueChangeListener() {
					
					@Override
					public void valueChange(ValueChangeEvent event) {						
						gmDAO.checkSubModuloUsuarioEmpresaInsert(codUsuario, codEmpresa, codSubModulo);						
						//hl2.replaceComponent(tbSubModulos, buildTbSubModulos());
						hl2.replaceComponent(tbPermissao, buildTbPermissoes());
					}
				});
				
				return chk;
			}
		});
		tbSubModulos.setColumnExpandRatio("nome", 1.0f);
		tbSubModulos.addListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				addPermissions();
			}
		});
		
		return tbSubModulos;
	}
	private void addPermissions(){
		tbPermissao.removeAllItems();
		if(tbSubModulos.getValue() != null && ((CheckBox)tbSubModulos.getColumnGenerator("x").generateCell(tbSubModulos, tbSubModulos.getValue(), "x")).getValue()){
			
			Integer codSubModulo = (Integer) tbSubModulos.getItem(tbSubModulos.getValue()).getItemProperty("id").getValue();
			String submodulo = tbSubModulos.getItem(tbSubModulos.getValue()).getItemProperty("nome").getValue().toString();
			
			if(submodulo.equals("Clientes")){
				tbPermissao.addItem(new Object[]{"Cadastrar"}, new Integer(1));
				tbPermissao.addItem(new Object[]{"Alterar"}, new Integer(2));
				tbPermissao.addItem(new Object[]{"Excluir"}, new Integer(3));
				tbPermissao.addItem(new Object[]{"Importar"}, new Integer(4));
				tbPermissao.addItem(new Object[]{"Exportar Ficha do Cliente"}, new Integer(5));	
				tbPermissao.addItem(new Object[]{"Adicionar Endereço"}, new Integer(6));
				tbPermissao.addItem(new Object[]{"Editar Endereço"}, new Integer(7));
				tbPermissao.addItem(new Object[]{"Excluir Endereço"}, new Integer(8));
				tbPermissao.addItem(new Object[]{"Black List"}, new Integer(9));
				tbPermissao.addItem(new Object[]{"Edicao Rapida"}, new Integer(10));
				tbPermissao.addItem(new Object[]{"Editar Outras Info."}, new Integer(11));
				
			}else if(submodulo.equals("CEPs")){
				tbPermissao.addItem(new Object[]{"Cadastrar"}, new Integer(1));
				tbPermissao.addItem(new Object[]{"Alterar"}, new Integer(2));
				tbPermissao.addItem(new Object[]{"Excluir"}, new Integer(3));						
			}else if(submodulo.equals("Categorias")){
				tbPermissao.addItem(new Object[]{"Cadastrar"}, new Integer(1));
				tbPermissao.addItem(new Object[]{"Alterar"}, new Integer(2));
				tbPermissao.addItem(new Object[]{"Excluir"}, new Integer(3));						
			}else if(submodulo.equals("Transportadoras")){
				tbPermissao.addItem(new Object[]{"Cadastrar"}, new Integer(1));
				tbPermissao.addItem(new Object[]{"Alterar"}, new Integer(2));
				tbPermissao.addItem(new Object[]{"Excluir"}, new Integer(3));						
			}else if(submodulo.equals("Fornecedores")){
				tbPermissao.addItem(new Object[]{"Cadastrar"}, new Integer(1));
				tbPermissao.addItem(new Object[]{"Alterar"}, new Integer(2));
				tbPermissao.addItem(new Object[]{"Excluir"}, new Integer(3));	
				tbPermissao.addItem(new Object[]{"Importar"}, new Integer(4));	
				tbPermissao.addItem(new Object[]{"Historico Compras"}, new Integer(5));
			}else if(submodulo.equals("Veiculos")){
				tbPermissao.addItem(new Object[]{"Cadastrar"}, new Integer(1));
				tbPermissao.addItem(new Object[]{"Alterar"}, new Integer(2));
				tbPermissao.addItem(new Object[]{"Excluir"}, new Integer(3));						
			}else if(submodulo.equals("Setores")){
				tbPermissao.addItem(new Object[]{"Cadastrar"}, new Integer(1));
				tbPermissao.addItem(new Object[]{"Alterar"}, new Integer(2));
				tbPermissao.addItem(new Object[]{"Excluir"}, new Integer(3));						
			}else if(submodulo.equals("Como Nos Conheceu")){
				tbPermissao.addItem(new Object[]{"Cadastrar"}, new Integer(1));
				tbPermissao.addItem(new Object[]{"Alterar"}, new Integer(2));
				tbPermissao.addItem(new Object[]{"Excluir"}, new Integer(3));						
			}else if(submodulo.equals("Contratos de Acesso")){
				tbPermissao.addItem(new Object[]{"Cadastrar Novo Contrato"}, new Integer(1));
				tbPermissao.addItem(new Object[]{"Comprar Creditos"}, new Integer(2));
				tbPermissao.addItem(new Object[]{"Liberar Credenciais"}, new Integer(3));
				tbPermissao.addItem(new Object[]{"Liberar Boletos"}, new Integer(4));
				tbPermissao.addItem(new Object[]{"Imprimir Contrato"}, new Integer(5));
				tbPermissao.addItem(new Object[]{"Renovar Contrato"}, new Integer(6));
				tbPermissao.addItem(new Object[]{"Alterar Plano"}, new Integer(7));
				tbPermissao.addItem(new Object[]{"Alterar Credenciais"}, new Integer(8));
				tbPermissao.addItem(new Object[]{"Alterar Concentrador"}, new Integer(9));
				tbPermissao.addItem(new Object[]{"Alterar Material"}, new Integer(10));
				tbPermissao.addItem(new Object[]{"Alterar Regime Proprio"}, new Integer(11));
				tbPermissao.addItem(new Object[]{"Alterar Regime Comodato"}, new Integer(12));
				tbPermissao.addItem(new Object[]{"Alterar Regime Comodato (Total)"}, new Integer(13));
				tbPermissao.addItem(new Object[]{"Alterar Titularidade"}, new Integer(14));
				tbPermissao.addItem(new Object[]{"Alterar Vencimento"}, new Integer(15));
				tbPermissao.addItem(new Object[]{"Alterar Endereco"}, new Integer(16));
				tbPermissao.addItem(new Object[]{"Encerrar Contrato"}, new Integer(17));
				tbPermissao.addItem(new Object[]{"Autocensura"}, new Integer(18));
				tbPermissao.addItem(new Object[]{"Pendencia Contrato"}, new Integer(19));	
				tbPermissao.addItem(new Object[]{"Histórico de Acesso"}, new Integer(20));
				tbPermissao.addItem(new Object[]{"Histórico de Chamados"}, new Integer(21));
				tbPermissao.addItem(new Object[]{"Histórico de Contatos"}, new Integer(22));
				tbPermissao.addItem(new Object[]{"Histórico de Pagamentos"}, new Integer(23));
				tbPermissao.addItem(new Object[]{"Visualizar Contrato"}, new Integer(24));
				tbPermissao.addItem(new Object[]{"Alterar Fiador"}, new Integer(25));
				tbPermissao.addItem(new Object[]{"Remover Material"}, new Integer(26));
				tbPermissao.addItem(new Object[]{"Alterar Tipo Nfe"}, new Integer(27));
				tbPermissao.addItem(new Object[]{"Alterar Ip Fixo"}, new Integer(28));
				tbPermissao.addItem(new Object[]{"Suspender"}, new Integer(29));
				tbPermissao.addItem(new Object[]{"Reativar"}, new Integer(30));
				tbPermissao.addItem(new Object[]{"Bloquear/Desbloquear"}, new Integer(31));
				tbPermissao.addItem(new Object[]{"Agendar Bloqueio/Desbloqueio"}, new Integer(32));
				tbPermissao.addItem(new Object[]{"Funcoes Administrativas"}, new Integer(33));
				tbPermissao.addItem(new Object[]{"Desbloqueio 72h"}, new Integer(34));
				tbPermissao.addItem(new Object[]{"PreContrato"}, new Integer(35));
				tbPermissao.addItem(new Object[]{"Upload arquivo"}, new Integer(36));
				tbPermissao.addItem(new Object[]{"Excluir arquivo"}, new Integer(37));
				tbPermissao.addItem(new Object[]{"Visualizar arquivo"}, new Integer(38));
				tbPermissao.addItem(new Object[]{"Liberar Cartao Cliente"}, new Integer(39));
			}else if(submodulo.equals("Bases")){
				tbPermissao.addItem(new Object[]{"Cadastrar"}, new Integer(1));
				tbPermissao.addItem(new Object[]{"Alterar"}, new Integer(2));
				tbPermissao.addItem(new Object[]{"Excluir"}, new Integer(3));	
			}else if(submodulo.equals("OLTs")){
				tbPermissao.addItem(new Object[]{"Cadastrar"}, new Integer(1));
				tbPermissao.addItem(new Object[]{"Alterar"}, new Integer(2));
				tbPermissao.addItem(new Object[]{"Excluir"}, new Integer(3));	
			}else if(submodulo.equals("Planos de Acesso")){
				tbPermissao.addItem(new Object[]{"Cadastrar"}, new Integer(1));
				tbPermissao.addItem(new Object[]{"Alterar"}, new Integer(2));
				tbPermissao.addItem(new Object[]{"Excluir"}, new Integer(3));	
			}else if(submodulo.equals("Concentradores")){
				tbPermissao.addItem(new Object[]{"Cadastrar"}, new Integer(1));
				tbPermissao.addItem(new Object[]{"Alterar"}, new Integer(2));
				tbPermissao.addItem(new Object[]{"Excluir"}, new Integer(3));
				tbPermissao.addItem(new Object[]{"Visualizar Interfaces"}, new Integer(4));
				tbPermissao.addItem(new Object[]{"Cadastrar Usuarios"}, new Integer(5));		
			}else if(submodulo.equals("Caixas de Atendimento")){
				tbPermissao.addItem(new Object[]{"Cadastrar"}, new Integer(1));
				tbPermissao.addItem(new Object[]{"Alterar"}, new Integer(2));
				tbPermissao.addItem(new Object[]{"Excluir"}, new Integer(3));
				tbPermissao.addItem(new Object[]{"Copiar"}, new Integer(4));	
			}else if(submodulo.equals("Servicos")){
				tbPermissao.addItem(new Object[]{"Cadastrar"}, new Integer(1));
				tbPermissao.addItem(new Object[]{"Alterar"}, new Integer(2));
				tbPermissao.addItem(new Object[]{"Excluir"}, new Integer(3));				
			}else if(submodulo.equals("Contas a Receber")){
				tbPermissao.addItem(new Object[]{"Cadastrar"}, new Integer(1));
				tbPermissao.addItem(new Object[]{"Alterar"}, new Integer(2));
				tbPermissao.addItem(new Object[]{"Excluir"}, new Integer(3));	
				tbPermissao.addItem(new Object[]{"Negativar"}, new Integer(4));
				tbPermissao.addItem(new Object[]{"Baixar"}, new Integer(5));
				tbPermissao.addItem(new Object[]{"Baixar com Haver"}, new Integer(6));
				tbPermissao.addItem(new Object[]{"Extornar"}, new Integer(7));
				tbPermissao.addItem(new Object[]{"Extornar Retroativo"}, new Integer(8));
				tbPermissao.addItem(new Object[]{"Imprimir"}, new Integer(9));
				tbPermissao.addItem(new Object[]{"Imprimir Recibo"}, new Integer(10));
				tbPermissao.addItem(new Object[]{"Enviar Arq. Retorno"}, new Integer(11));
				tbPermissao.addItem(new Object[]{"Arquivo Remessa"}, new Integer(12));
				tbPermissao.addItem(new Object[]{"Gerar Nosso Número"}, new Integer(13));
				tbPermissao.addItem(new Object[]{"Reemitir Boleto"}, new Integer(14));
				tbPermissao.addItem(new Object[]{"Cancelar Transação GerenciaNet"}, new Integer(15));
				tbPermissao.addItem(new Object[]{"Detalhes Transação GerenciaNet"}, new Integer(16));
				tbPermissao.addItem(new Object[]{"Ocorrencia"}, new Integer(17));				
			}else if(submodulo.equals("Baixa Rapida")){
				tbPermissao.addItem(new Object[]{"Cadastrar"}, new Integer(1));
				tbPermissao.addItem(new Object[]{"Alterar"}, new Integer(2));
				tbPermissao.addItem(new Object[]{"Excluir"}, new Integer(3));	
				tbPermissao.addItem(new Object[]{"Negativar"}, new Integer(4));
				tbPermissao.addItem(new Object[]{"Baixar"}, new Integer(5));
				tbPermissao.addItem(new Object[]{"Baixar com Haver"}, new Integer(6));
				tbPermissao.addItem(new Object[]{"Extornar"}, new Integer(7));
				tbPermissao.addItem(new Object[]{"Extornar Retroativo"}, new Integer(8));
				tbPermissao.addItem(new Object[]{"Imprimir"}, new Integer(9));
				tbPermissao.addItem(new Object[]{"Imprimir Recibo"}, new Integer(10));
				tbPermissao.addItem(new Object[]{"Enviar Arq. Retorno"}, new Integer(11));
				tbPermissao.addItem(new Object[]{"Arquivo Remessa"}, new Integer(12));
				tbPermissao.addItem(new Object[]{"Gerar Nosso Número"}, new Integer(13));
				tbPermissao.addItem(new Object[]{"Reemitir Boleto"}, new Integer(14));
			}else if(submodulo.equals("Contas a Pagar")){
				tbPermissao.addItem(new Object[]{"Cadastrar"}, new Integer(1));
				tbPermissao.addItem(new Object[]{"Alterar"}, new Integer(2));
				tbPermissao.addItem(new Object[]{"Excluir"}, new Integer(3));	
				tbPermissao.addItem(new Object[]{"Estornar"}, new Integer(4));			
				tbPermissao.addItem(new Object[]{"Historico"}, new Integer(5));		
				tbPermissao.addItem(new Object[]{"Baixar"}, new Integer(6));
				tbPermissao.addItem(new Object[]{"Adiar"}, new Integer(7));
			}else if(submodulo.equals("Haver")){
				tbPermissao.addItem(new Object[]{"Cadastrar Haver"}, new Integer(1));
				tbPermissao.addItem(new Object[]{"Cadastrar Indicação"}, new Integer(2));
				tbPermissao.addItem(new Object[]{"Visualizar"}, new Integer(3));
				tbPermissao.addItem(new Object[]{"Historico"}, new Integer(4));	
				tbPermissao.addItem(new Object[]{"Imprimir"}, new Integer(5));
				tbPermissao.addItem(new Object[]{"Liberar"}, new Integer(6));
				tbPermissao.addItem(new Object[]{"Pagar"}, new Integer(7));
			}else if(submodulo.equals("Seriais")){
				tbPermissao.addItem(new Object[]{"Cadastrar"}, new Integer(1));
				tbPermissao.addItem(new Object[]{"Alterar"}, new Integer(2));
				tbPermissao.addItem(new Object[]{"Excluir"}, new Integer(3));
				tbPermissao.addItem(new Object[]{"Devolucao"}, new Integer(4));	
				tbPermissao.addItem(new Object[]{"Log"}, new Integer(5));
			}else if(submodulo.equals("Marcas")){
				tbPermissao.addItem(new Object[]{"Cadastrar"}, new Integer(1));
				tbPermissao.addItem(new Object[]{"Alterar"}, new Integer(2));
				tbPermissao.addItem(new Object[]{"Excluir"}, new Integer(3));					
			}else if(submodulo.equals("Tabelas de Preço")){
				tbPermissao.addItem(new Object[]{"Cadastrar"}, new Integer(1));
				tbPermissao.addItem(new Object[]{"Alterar"}, new Integer(2));
				tbPermissao.addItem(new Object[]{"Excluir"}, new Integer(3));					
			}else if(submodulo.equals("Grupos de Produto")){
				tbPermissao.addItem(new Object[]{"Cadastrar"}, new Integer(1));
				tbPermissao.addItem(new Object[]{"Alterar"}, new Integer(2));
				tbPermissao.addItem(new Object[]{"Excluir"}, new Integer(3));					
			}else if(submodulo.equals("Produtos")){
				tbPermissao.addItem(new Object[]{"Cadastrar"}, new Integer(1));
				tbPermissao.addItem(new Object[]{"Visualizar"}, new Integer(2));
				tbPermissao.addItem(new Object[]{"Excluir"}, new Integer(3));
				tbPermissao.addItem(new Object[]{"Log"}, new Integer(4));
				tbPermissao.addItem(new Object[]{"Historico de Entradas"}, new Integer(6));
				tbPermissao.addItem(new Object[]{"Historico de Saidas"}, new Integer(7));
				tbPermissao.addItem(new Object[]{"Copiar"}, new Integer(8));
				tbPermissao.addItem(new Object[]{"Adicionar Estoque"},new Integer(9));				
				tbPermissao.addItem(new Object[]{"Subtrair Estoque"}, new Integer(10));				
				tbPermissao.addItem(new Object[]{"Alterar Codigo de Barras"},new Integer(11));				
				tbPermissao.addItem(new Object[]{"Alterar NCM"}, new Integer(12));				
				tbPermissao.addItem(new Object[]{"Editar"}, new Integer(13));
				tbPermissao.addItem(new Object[]{"GerarDBF"}, new Integer(14));
				tbPermissao.addItem(new Object[]{"Exportar"}, new Integer(15));			
			}else if(submodulo.equals("Tipo Item")){
				tbPermissao.addItem(new Object[]{"Cadastrar"}, new Integer(1));				
				tbPermissao.addItem(new Object[]{"Editar"}, new Integer(2));
				tbPermissao.addItem(new Object[]{"Excluir"}, new Integer(3));							
			}else if(submodulo.equals("Config. de Assuntos")){
				tbPermissao.addItem(new Object[]{"Cadastrar"}, new Integer(1));
				tbPermissao.addItem(new Object[]{"Alterar"}, new Integer(2));
				tbPermissao.addItem(new Object[]{"Pesquisar"}, new Integer(3));
				tbPermissao.addItem(new Object[]{"Excluir"}, new Integer(4));
			}else if(submodulo.equals("Assistência Técnica")){
				tbPermissao.addItem(new Object[]{"Cadastrar"}, new Integer(1));
				tbPermissao.addItem(new Object[]{"Diagnostico"}, new Integer(2));
				tbPermissao.addItem(new Object[]{"Autorizacao"}, new Integer(3));
				tbPermissao.addItem(new Object[]{"Encaminhar"}, new Integer(4));
				tbPermissao.addItem(new Object[]{"Entregar"}, new Integer(5));
				tbPermissao.addItem(new Object[]{"Imprimir"}, new Integer(6));
				tbPermissao.addItem(new Object[]{"Concluir"}, new Integer(7));
				tbPermissao.addItem(new Object[]{"Visualizar"}, new Integer(8));
				tbPermissao.addItem(new Object[]{"Ocorrencia"}, new Integer(9));
				tbPermissao.addItem(new Object[]{"Excluir"}, new Integer(10));
			}else if(submodulo.equals("Roteirização")){
				tbPermissao.addItem(new Object[]{"Cadastrar"}, new Integer(1));
				tbPermissao.addItem(new Object[]{"Encaminhar"}, new Integer(2));
				tbPermissao.addItem(new Object[]{"Atribuir Técnico"}, new Integer(3));
				tbPermissao.addItem(new Object[]{"Atribuir Equipe"}, new Integer(4));
				tbPermissao.addItem(new Object[]{"Ocorrencia"}, new Integer(5));
				tbPermissao.addItem(new Object[]{"Visualizar Protocolo"}, new Integer(6));
				tbPermissao.addItem(new Object[]{"Reagendar"}, new Integer(7));
				tbPermissao.addItem(new Object[]{"Visualizar"}, new Integer(8));
				tbPermissao.addItem(new Object[]{"Concluir"}, new Integer(9));
				tbPermissao.addItem(new Object[]{"Fechar"}, new Integer(10));
				tbPermissao.addItem(new Object[]{"Relatorio de Materiais"}, new Integer(11));
				tbPermissao.addItem(new Object[]{"Alocar Materiais"}, new Integer(12));
				tbPermissao.addItem(new Object[]{"Excluir"}, new Integer(13));
				tbPermissao.addItem(new Object[]{"Upload arquivo"}, new Integer(14));
				tbPermissao.addItem(new Object[]{"Excluir arquivo"}, new Integer(15));
				tbPermissao.addItem(new Object[]{"Visualizar arquivo"}, new Integer(16));
				tbPermissao.addItem(new Object[]{"Arquivar"}, new Integer(17));
			}else if(submodulo.equals("Planos de Manutenção")){
				tbPermissao.addItem(new Object[]{"Cadastrar"}, new Integer(1));
				tbPermissao.addItem(new Object[]{"Editar"}, new Integer(2));			
				tbPermissao.addItem(new Object[]{"Excluir"}, new Integer(3));
			}else if(submodulo.equals("Contratos de Manutenção")){
				tbPermissao.addItem(new Object[]{"Cadastrar"}, new Integer(1));					
				tbPermissao.addItem(new Object[]{"Alterar Plano"}, new Integer(2));
				tbPermissao.addItem(new Object[]{"Excluir"}, new Integer(3));
			}else if(submodulo.equals("Preferências")){
				tbPermissao.addItem(new Object[]{"Definir Tabela de Preço"}, new Integer(1));
				tbPermissao.addItem(new Object[]{"Definir Taxa Boleto"}, new Integer(2));
				tbPermissao.addItem(new Object[]{"Definir Limite de Crédito"}, new Integer(3));			
			}else if(submodulo.equals("Contratos de Cobrança")){
				tbPermissao.addItem(new Object[]{"Cadastrar"}, new Integer(1));
				tbPermissao.addItem(new Object[]{"Alterar"}, new Integer(2));
				tbPermissao.addItem(new Object[]{"Excluir"}, new Integer(3));			

			}else if(submodulo.equals("Relatórios de Contas à Pagar")){
				tbPermissao.addItem(new Object[]{"Gerar PDF"}, new Integer(1));
				tbPermissao.addItem(new Object[]{"Gerar Grafico"}, new Integer(2));		
			}else if(submodulo.equals("Relatórios de Contas à Receber")){
				tbPermissao.addItem(new Object[]{"Gerar PDF"}, new Integer(1));
				tbPermissao.addItem(new Object[]{"Gerar Grafico"}, new Integer(2));		
			}else if(submodulo.equals("Relatórios de Consumo")){
				tbPermissao.addItem(new Object[]{"Gerar PDF"}, new Integer(1));
				tbPermissao.addItem(new Object[]{"Gerar Grafico"}, new Integer(2));		
			}else if(submodulo.equals("Relatórios de Clientes")){
				tbPermissao.addItem(new Object[]{"Gerar PDF"}, new Integer(1));
				tbPermissao.addItem(new Object[]{"Gerar Grafico"}, new Integer(2));		
			}else if(submodulo.equals("Relatórios de Pedidos")){
				tbPermissao.addItem(new Object[]{"Gerar PDF"}, new Integer(1));
				tbPermissao.addItem(new Object[]{"Gerar Grafico"}, new Integer(2));		
			}else if(submodulo.equals("Relatórios de Compras")){
				tbPermissao.addItem(new Object[]{"Gerar PDF"}, new Integer(1));
				tbPermissao.addItem(new Object[]{"Gerar Grafico"}, new Integer(2));		
			}else if(submodulo.equals("Contatos")){
				tbPermissao.addItem(new Object[]{"Novo"}, new Integer(1));
				tbPermissao.addItem(new Object[]{"Analisar"}, new Integer(2));
				tbPermissao.addItem(new Object[]{"Visualizar"}, new Integer(3));
				tbPermissao.addItem(new Object[]{"Excluir"}, new Integer(4));
				tbPermissao.addItem(new Object[]{"Histórico de CRM"}, new Integer(5));
				tbPermissao.addItem(new Object[]{"Log Alterações"}, new Integer(6));
				tbPermissao.addItem(new Object[]{"PreTratar"}, new Integer(7));
				tbPermissao.addItem(new Object[]{"Ocorrencias"}, new Integer(8));
				tbPermissao.addItem(new Object[]{"Registrar Atendimento"}, new Integer(9));				
			}else if(submodulo.equals("Relatórios de Acesso")){
				tbPermissao.addItem(new Object[]{"Gerar PDF"}, new Integer(1));
				tbPermissao.addItem(new Object[]{"Gerar Grafico"}, new Integer(2));	
			}else if(submodulo.equals("Relatórios de CRM")){
				tbPermissao.addItem(new Object[]{"Gerar PDF"}, new Integer(1));
				tbPermissao.addItem(new Object[]{"Gerar Grafico"}, new Integer(2));	
			}else if(submodulo.equals("Relatórios de Pesquisa")){
				tbPermissao.addItem(new Object[]{"Gerar PDF"}, new Integer(1));
				tbPermissao.addItem(new Object[]{"Gerar Grafico"}, new Integer(2));	

			}else if(submodulo.equals("Pré-venda")){
				tbPermissao.addItem(new Object[]{"Cadastrar"}, new Integer(1));
				tbPermissao.addItem(new Object[]{"Editar"}, new Integer(2));
				tbPermissao.addItem(new Object[]{"ConverterPedido"}, new Integer(3));
				tbPermissao.addItem(new Object[]{"Finalizar"}, new Integer(4));
				tbPermissao.addItem(new Object[]{"Imprimir"}, new Integer(5));
				tbPermissao.addItem(new Object[]{"Pdf"}, new Integer(6));
				tbPermissao.addItem(new Object[]{"Cancelar"}, new Integer(7));
				tbPermissao.addItem(new Object[]{"Cancelar Retroativo"}, new Integer(8));
				tbPermissao.addItem(new Object[]{"Alterar Propriedade"}, new Integer(9));
				tbPermissao.addItem(new Object[]{"Visualizar Logs"}, new Integer(10));
				
			}else if(submodulo.equals("Compras")){
				tbPermissao.addItem(new Object[]{"Cadastrar"}, new Integer(1));
				tbPermissao.addItem(new Object[]{"Editar"}, new Integer(2));				
				tbPermissao.addItem(new Object[]{"Finalizar"}, new Integer(3));			
				tbPermissao.addItem(new Object[]{"Cancelar"}, new Integer(4));				
			}else if(submodulo.equals("Natureza da Operacao")){
				tbPermissao.addItem(new Object[]{"Cadastrar"}, new Integer(1));
				tbPermissao.addItem(new Object[]{"Editar"}, new Integer(2));				
				tbPermissao.addItem(new Object[]{"Excluir"}, new Integer(6));				
													
			}else if(submodulo.equals("Relatórios de Roteirização")){
				tbPermissao.addItem(new Object[]{"Gerar PDF"}, new Integer(1));
				tbPermissao.addItem(new Object[]{"Gerar Grafico"}, new Integer(2));
			}else if(submodulo.equals("Relatórios de Produção")){
				tbPermissao.addItem(new Object[]{"Gerar PDF"}, new Integer(1));
				tbPermissao.addItem(new Object[]{"Gerar XLS"}, new Integer(2));
				tbPermissao.addItem(new Object[]{"Gerar Grafico"}, new Integer(3));
			}else if(submodulo.equals("Relatórios de Produto")){
				tbPermissao.addItem(new Object[]{"Gerar PDF"}, new Integer(1));
				tbPermissao.addItem(new Object[]{"Gerar Grafico"}, new Integer(2));
			}else if(submodulo.equals("Produção")){
				tbPermissao.addItem(new Object[]{"Importar"}, new Integer(1));
				tbPermissao.addItem(new Object[]{"Editar"}, new Integer(2));
				tbPermissao.addItem(new Object[]{"Encaminhar"}, new Integer(3));
				tbPermissao.addItem(new Object[]{"Ocorrencia"}, new Integer(4));
				tbPermissao.addItem(new Object[]{"Concluir"}, new Integer(5));
				tbPermissao.addItem(new Object[]{"Reagendar"}, new Integer(6));
				tbPermissao.addItem(new Object[]{"Finalizar"}, new Integer(7));
				tbPermissao.addItem(new Object[]{"Entregar"}, new Integer(8));
				tbPermissao.addItem(new Object[]{"Visualizar"}, new Integer(9));
				tbPermissao.addItem(new Object[]{"Excluir"}, new Integer(10));
				tbPermissao.addItem(new Object[]{"Visualizar Entregues"}, new Integer(11));
				tbPermissao.addItem(new Object[]{"Alterar Setor"}, new Integer(12));
				tbPermissao.addItem(new Object[]{"Solicitar Aprovação"}, new Integer(13));
				tbPermissao.addItem(new Object[]{"Reprovar"}, new Integer(14));
			}else if(submodulo.equals("Central de Assinantes")){
				tbPermissao.addItem(new Object[]{"Cadastrar Lista Autocensura"}, new Integer(1));
				tbPermissao.addItem(new Object[]{"Editar Lista Autocensura"}, new Integer(2));
				tbPermissao.addItem(new Object[]{"Alterou uma Lista de Autocensura"}, new Integer(3));
				tbPermissao.addItem(new Object[]{"Excluir Lista Autocensura"}, new Integer(4));
				tbPermissao.addItem(new Object[]{"Definir Credenciais"}, new Integer(5));
				tbPermissao.addItem(new Object[]{"Palavra Autocensura"}, new Integer(6));
				
			}else if(submodulo.equals("Controle Título")){
				tbPermissao.addItem(new Object[]{"Cadastrar"}, new Integer(1));
				tbPermissao.addItem(new Object[]{"Alterar"}, new Integer(2));
				tbPermissao.addItem(new Object[]{"Excluir"}, new Integer(3));
			}else if(submodulo.equals("Config-Ordens de Serviço")){
				tbPermissao.addItem(new Object[]{"Novo"}, new Integer(1));
				tbPermissao.addItem(new Object[]{"Editar"}, new Integer(2));
				tbPermissao.addItem(new Object[]{"Excluir"}, new Integer(3));
				tbPermissao.addItem(new Object[]{"Limite Diario"}, new Integer(4));
			}else if(submodulo.equals("Notas")){
				tbPermissao.addItem(new Object[]{"Gerar NFe"}, new Integer(1));
				tbPermissao.addItem(new Object[]{"Relatorio"}, new Integer(2));
				tbPermissao.addItem(new Object[]{"Imprimir"}, new Integer(3));
				tbPermissao.addItem(new Object[]{"Cancelar"}, new Integer(4));
				tbPermissao.addItem(new Object[]{"Enviar Email"}, new Integer(5));
			}else if(submodulo.equals("Arquivo Remessa")){
				tbPermissao.addItem(new Object[]{"Gerar Arquivo Remessa"}, new Integer(1));
				tbPermissao.addItem(new Object[]{"Alterar"}, new Integer(2));
				tbPermissao.addItem(new Object[]{"Excluir"}, new Integer(3));				
			}else if(submodulo.equals("RMA")){
				tbPermissao.addItem(new Object[]{"Cadastrar"}, new Integer(1));
				tbPermissao.addItem(new Object[]{"Status"}, new Integer(2));
				tbPermissao.addItem(new Object[]{"Log"}, new Integer(3));
				tbPermissao.addItem(new Object[]{"Espelho"}, new Integer(4));				
				tbPermissao.addItem(new Object[]{"Excluir"}, new Integer(5));
				
			}else if(submodulo.equals("NFe Mod-21")){
				tbPermissao.addItem(new Object[]{"Gerar"}, new Integer(1));
				tbPermissao.addItem(new Object[]{"Cancelar"}, new Integer(2));
				tbPermissao.addItem(new Object[]{"Excluir"}, new Integer(3));
				tbPermissao.addItem(new Object[]{"Relatorio"}, new Integer(4));				
				tbPermissao.addItem(new Object[]{"Enviar Email"}, new Integer(5));
				tbPermissao.addItem(new Object[]{"Imprimir"}, new Integer(6));
				
			}else if(submodulo.equals("Materiais Alocados")){
				tbPermissao.addItem(new Object[]{"Alocar"}, new Integer(1));
				tbPermissao.addItem(new Object[]{"Estornar"}, new Integer(2));
				tbPermissao.addItem(new Object[]{"Relatorio de Materiais"}, new Integer(3));
			}else if(submodulo.equals("Gerenciamento Estoque")){
				tbPermissao.addItem(new Object[]{"Transferencia"}, new Integer(1));
				tbPermissao.addItem(new Object[]{"Aceitar"}, new Integer(2));
				tbPermissao.addItem(new Object[]{"Cancelar"}, new Integer(3));
			}else if(submodulo.equals("Indicacoes Visao Geral")){
				tbPermissao.addItem(new Object[]{"Vincular Boleto"}, new Integer(1));
				tbPermissao.addItem(new Object[]{"Creditar"}, new Integer(2));
				
			}
			
			
			
			
			
			
			
			
			
			
			
			
			
			
		}
	}
	private Table buildTbPermissoes(){
		tbPermissao = new Table("Permissoes");
		tbPermissao.setSelectable(false);				
		tbPermissao.setWidth("100%");
		tbPermissao.setHeight("130px");
		tbPermissao.setImmediate(true);		
		tbPermissao.addContainerProperty("nome", String.class,null);
		//tbPermissao.addContainerProperty("codsubmodulo", Integer.class,null);	
		tbPermissao.setColumnHeader("nome", "Nome");
		tbPermissao.addGeneratedColumn("x", new Table.ColumnGenerator() {
			
			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				
				final Integer codSubModulo = (Integer) tbSubModulos.getItem(tbSubModulos.getValue()).getItemProperty("id").getValue();
				final Integer codEmpresa = (Integer) tbEmpresas.getItem(tbEmpresas.getValue()).getItemProperty("id").getValue();
				final Integer codUsuario = usuario_selecionado.getId();
				
				if(source.getItem(itemId).getItemProperty("nome").getValue() != null){
					final String permissao = source.getItem(itemId).getItemProperty("nome").getValue().toString();
					
					boolean check = gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, codEmpresa, codUsuario, permissao);				
					
					CheckBox chk = new CheckBox();
					chk.setValue(check);
					chk.addListener(new Property.ValueChangeListener() {
						
						@Override
						public void valueChange(ValueChangeEvent event) {						
							gmDAO.checkPermissaoEmpresaSubModuloUsuarioInsert(codSubModulo, codEmpresa, codUsuario, permissao);						
							//hl2.replaceComponent(tbPermissao, buildTbPermissoes());
						}
					});
					
					return chk;
				}else{
					return new CheckBox();
				}
				
			}
		});
		tbPermissao.setColumnExpandRatio("nome", 1.0f);
		addPermissions();
		
		return tbPermissao;
	}
}

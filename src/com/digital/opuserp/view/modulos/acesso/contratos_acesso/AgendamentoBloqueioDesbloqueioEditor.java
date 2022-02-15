package com.digital.opuserp.view.modulos.acesso.contratos_acesso;

import java.util.Date;

import javax.persistence.EntityManager;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.AgendaBloqueioDesbloqueioDAO;
import com.digital.opuserp.domain.AcessoCliente;
import com.digital.opuserp.domain.AgendamentoBloqueioDesbloqueio;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.view.modulos.acesso.contratos_acesso.NovoAgendamentoEditor.NovoAgendamentoBloqueioDesbloqueioEvent;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.util.BeanItem;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.Reindeer;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class AgendamentoBloqueioDesbloqueioEditor extends Window {

	AcessoCliente contrato;
	
	TextField tfBusca;
	Table tbAgendamentos;
	Button btSalvar;
	Button btCancelar;
	
	Integer codContrato;
	
	public AgendamentoBloqueioDesbloqueioEditor(boolean modal, boolean center, final AcessoCliente contrato){
		
		super("Agendamentos de Bloqueio");
		
		this.contrato = contrato;
		setWidth("830px");
		setHeight("360px");
		
		setModal(modal);
		if(center){
			center();
		}
		
		setClosable(false);
				  						
		setContent(new VerticalLayout(){
			{
				setSizeFull();
				setMargin(true);
				
				Button btAdicionar = new Button("Novo", new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						final BeanItem<AgendamentoBloqueioDesbloqueio> bItemNew = new BeanItem<AgendamentoBloqueioDesbloqueio>(new AgendamentoBloqueioDesbloqueio());
						final NovoAgendamentoEditor novoEditor = new NovoAgendamentoEditor(bItemNew, "Agendar novo Bloqueio/Desbloqueio", true);
						
						novoEditor.addListerner(new NovoAgendamentoEditor.NovoAgendamentoBloqueioDesbloqueioListerner() {
							
							@Override
							public void onClose(NovoAgendamentoBloqueioDesbloqueioEvent event) {
								if(event.isConfirm()){
									
									AgendamentoBloqueioDesbloqueio agendBloqueioDesbloqueio = bItemNew.getBean();
									agendBloqueioDesbloqueio.setContrato(contrato);
									agendBloqueioDesbloqueio.setData_cadastro(new Date());
									agendBloqueioDesbloqueio.setStatus("PENDENTE");
									agendBloqueioDesbloqueio.setUsuario(OpusERP4UI.getUsuarioLogadoUI().getUsername());
									
									EntityManager em = ConnUtil.getEntity();
									em.getTransaction().begin();
									em.merge(agendBloqueioDesbloqueio);
									em.getTransaction().commit();
									
									novoEditor.close();
									containerAgendamento.refresh();
								}
							}
						});
						
						getUI().addWindow(novoEditor); 
					}
				});
				
				btAdicionar.setStyleName("default");
				addComponent(btAdicionar); 
								
				addComponent(buildTbChamados());
				setExpandRatio(tbAgendamentos, 2);
				
				HorizontalLayout hlButtons = new HorizontalLayout(); 
				hlButtons.setSpacing(true);
				hlButtons.setMargin(true);
				hlButtons.setStyleName("hl_buttons_bottom");
				 
				hlButtons.addComponent(buildBtCancelar());
				btCancelar.focus();
				//hlButtons.addComponent(buildBtSalvar());
				
				HorizontalLayout Hlrodape = new HorizontalLayout();
				Hlrodape.setWidth("100%");				
				Hlrodape.addComponent(hlButtons);				
				Hlrodape.setComponentAlignment(hlButtons, Alignment.BOTTOM_RIGHT);
				
				addComponent(Hlrodape);
				
			}

		});
	}
	
	JPAContainer<AgendamentoBloqueioDesbloqueio> containerAgendamento;
	
	private JPAContainer<AgendamentoBloqueioDesbloqueio> buildContainer(){
		containerAgendamento = JPAContainerFactory.make(AgendamentoBloqueioDesbloqueio.class, ConnUtil.getEntity());
		containerAgendamento.addContainerFilter(Filters.eq("contrato", contrato));
		return containerAgendamento;
	}

	public Table buildTbChamados(){
		tbAgendamentos = new Table(null, buildContainer());
		tbAgendamentos.setSizeFull();
		tbAgendamentos.setSelectable(true);
		
		tbAgendamentos.setVisibleColumns(new Object[] {"id","tipo","data_agendado","usuario","status","data_cadastro"});
		
		tbAgendamentos.setColumnHeader("id", "Cod");
		tbAgendamentos.setColumnHeader("tipo", "Tipo");
		tbAgendamentos.setColumnHeader("data_agendado", "Data Execução");
		tbAgendamentos.setColumnHeader("usuario", "Usuário");
		tbAgendamentos.setColumnHeader("status", "Status");
		tbAgendamentos.setColumnHeader("data_cadastro", "Data Cadastro");
		
		tbAgendamentos.setColumnCollapsingAllowed(true); 
		tbAgendamentos.setColumnCollapsed("id", true);
		
		tbAgendamentos.addGeneratedColumn("e", new Table.ColumnGenerator() {
			
			@Override
			public Object generateCell(final Table source, final Object itemId, Object columnId) {

				Button btExcluir = new Button("excluir", new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						
						GenericDialog gd = new GenericDialog("Confirme para continua!", "Deseja realmente excluir este Agendamento ?", true, true);
						gd.addListerner(new GenericDialog.DialogListerner() {
							
							@Override
							public void onClose(DialogEvent event) {
								if(event.isConfirm()){
									
									EntityItem<AgendamentoBloqueioDesbloqueio> entityAgendamento = (EntityItem<AgendamentoBloqueioDesbloqueio>) source.getItem(itemId);
									AgendamentoBloqueioDesbloqueio agendaBloqueDesblo = entityAgendamento.getEntity();
									
									boolean check = AgendaBloqueioDesbloqueioDAO.remover(agendaBloqueDesblo);
									
									if(check){
										containerAgendamento.refresh();
										Notify.Show("Agendamento de Bloqueio/Desbloqueio removido!", Notify.TYPE_SUCCESS);
									}else{
										Notify.Show("Erro ao tentar remover este agendamento...", Notify.TYPE_ERROR);	
									}
								}
							}
						});	
						
						getUI().addWindow(gd); 
					}
				});
				
				btExcluir.setStyleName(Reindeer.BUTTON_LINK);				
				return btExcluir;
			}
		});
		
			
		return tbAgendamentos;
	}
	
	private Button buildBtCancelar() {
		btCancelar = new Button("Fechar", new Button.ClickListener() {
			
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

}

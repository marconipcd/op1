package com.digital.opuserp.view.home.apps.plano_acao;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.ConfigPlanoAcaoDAO;
import com.digital.opuserp.dao.PlanoAcaoDAO;
import com.digital.opuserp.dao.PrefeDashDAO;
import com.digital.opuserp.domain.ConfigPlanoAcao;
import com.digital.opuserp.domain.OcorrenciaPlanoAcao;
import com.digital.opuserp.domain.PlanoAcao;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Container;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.filter.Like;
import com.vaadin.server.FileResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.themes.Reindeer;

public class PlanoAcaoApp  extends CssLayout{
	
    private Button btMaximize;
    private Button btClose;
    private Image logo;
    
    private ConfigPlanoAcao config;

	public PlanoAcaoApp(){	
		
		addStyleName("layout-panel");
		setSizeFull();
				
		buildLayout();
		status = "PENDENTES";
	}	
	
	private void formNovaConfig(){
		
		VerticalLayout vl_cadastro = new VerticalLayout();
		vl_cadastro.setWidth("95%");		
		vl_cadastro.setSpacing(false); 
		vl_cadastro.setMargin(true);
		vl_cadastro.setStyleName("border-form-plano-acao");
		
		final Window w_config_plano_acao = new Window("Configurar novo Plano de Ação");
		w_config_plano_acao.center();
		w_config_plano_acao.setModal(true);		
		w_config_plano_acao.setWidth("559px");
		w_config_plano_acao.setHeight("231px");
		w_config_plano_acao.setContent(vl_cadastro);
		w_config_plano_acao.setResizable(false);
		w_config_plano_acao.setClosable(false); 
		
		w_config_plano_acao.addCloseListener(new Window.CloseListener() {
			
			@Override
			public void windowClose(CloseEvent e) {
				buildLayout();
			}
		});
		
		final TextField txtNomePlano = new TextField("Nome Plano");
		txtNomePlano.setStyleName("caption-align-plano-acao");
		txtNomePlano.setWidth("90%");
			
		FormLayout frm_txtOrigem = new FormLayout(){					
			{
				setStyleName("form-cutom");
				setMargin(true);
				setSpacing(true);	
				
				addComponent(txtNomePlano);
			}
		};
		vl_cadastro.addComponent(frm_txtOrigem);
		
		final TextField txtNomeGestor = new TextField("Nome Gestor");
		txtNomeGestor.setStyleName("caption-align-plano-acao");
		txtNomeGestor.setWidth("90%");
			
		FormLayout frm_txtGestor = new FormLayout(){					
			{
				setStyleName("form-cutom");
				setMargin(true);
				setSpacing(true);	
				
				addComponent(txtNomeGestor);
			}
		};
		vl_cadastro.addComponent(frm_txtGestor);
		
		final TextField txtDataCriacao = new TextField("Data Criação");
		txtDataCriacao.setStyleName("caption-align-plano-acao");
		txtDataCriacao.setWidth("90%");
			
		FormLayout frm_txtDataCriacao = new FormLayout(){					
			{
				setStyleName("form-cutom");
				setMargin(true);
				setSpacing(true);	
				
				addComponent(txtDataCriacao);
			}
		};
		vl_cadastro.addComponent(frm_txtDataCriacao);
		
		HorizontalLayout hlButton = new HorizontalLayout();
		hlButton.setStyleName("hl_buttons_bottom");
		hlButton.setSpacing(true);
		hlButton.setMargin(true);
			
		Button btOk = new Button("Ok", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
					
				ConfigPlanoAcao config = new ConfigPlanoAcao(null, 
						txtNomePlano.getValue(), 
						txtNomeGestor.getValue(), 
						txtDataCriacao.getValue(), 
						OpusERP4UI.getUsuarioLogadoUI());
				
				boolean check = ConfigPlanoAcaoDAO.save(config);
			
				if(check){
					Notify.Show("Plano de Ação configurado com Sucesso!", Notify.TYPE_SUCCESS);
					w_config_plano_acao.close();
				}
			}
		});
		btOk.addStyleName("default");
		hlButton.addComponent(btOk);
			
		hlButton.setComponentAlignment(btOk, Alignment.MIDDLE_RIGHT);
			
		vl_cadastro.addComponent(hlButton);
		vl_cadastro.setComponentAlignment(hlButton, Alignment.MIDDLE_RIGHT);
		
		if(w_config_plano_acao != null){
			//buildLayout();
			getUI().addWindow(w_config_plano_acao);
			txtNomePlano.focus();
		}
	}

	VerticalLayout vlRoot;
	private void buildLayout() {
		
		config = ConfigPlanoAcaoDAO.getInfo(OpusERP4UI.getUsuarioLogadoUI());
		removeAllComponents();
		
		vlRoot = new VerticalLayout();
		vlRoot.setCaption("PLANO DE AÇÃO");
		vlRoot.setSizeFull();
		
		//Cabecalho
		vlRoot.addComponent(new HorizontalLayout(){
			{
				setWidth("100%"); 
				setHeight("103px");
				
				setMargin(false);
				setStyleName("cab-plano-cao");
				
				
				addComponent(getLogo());
				addComponent(getCriacaoGestor());
				addComponent(getTitulo());
				addComponent(getTotalizadores());
				
				this.setExpandRatio(vl_logo, 0.15f);
				this.setExpandRatio(vl_criacao_gestor, 0.10f);
				this.setExpandRatio(vl_titulo, 0.45f);
				this.setExpandRatio(vl_totalizadores, 0.30f);				
			}
		});
		
		vlRoot.addComponent(getListagem());
		vlRoot.setExpandRatio(vlListagem, 1f);
		
		addComponent(vlRoot);
		addComponent(buildBtMaximize());
        addComponent(buildBtClose());       	
	}
	
	VerticalLayout vl_logo;
	File file;
	
	private void abrir_janela_detalhes(PlanoAcao plano_acao){
		
		List<OcorrenciaPlanoAcao> detalhesPlanoAcao = PlanoAcaoDAO.getDetalhes(plano_acao);
				
		VerticalLayout vl_detalhe_plano_acao = new VerticalLayout();
		vl_detalhe_plano_acao.setWidth("95%");		
		vl_detalhe_plano_acao.setSpacing(false); 
		vl_detalhe_plano_acao.setMargin(true);
		vl_detalhe_plano_acao.setStyleName("border-form-plano-acao");
		
		final Window w_detalhe_plano = new Window("Detalhes do Plano de Ação");
		w_detalhe_plano.center();
		w_detalhe_plano.setModal(true);		
		w_detalhe_plano.setWidth("643px");
		w_detalhe_plano.setHeight("481px");
		w_detalhe_plano.setContent(vl_detalhe_plano_acao);
		w_detalhe_plano.setResizable(false); 
		
		w_detalhe_plano.addCloseListener(new Window.CloseListener() {
			
			@Override
			public void windowClose(CloseEvent e) {
				buildLayout();
			}
		});
		
		final TextArea taObs = new TextArea("Ob.:");
		taObs.setStyleName("caption-align-plano-acao");
		taObs.setWidth("90%");
		taObs.setHeight("70px");
		
		FormLayout frm_taAcao = new FormLayout(){					
			{
				setStyleName("form-cutom");
				setMargin(true);
				setSpacing(true);	
				
				addComponent(taObs);
			}
		};
		vl_detalhe_plano_acao.addComponent(frm_taAcao);		
			
		final TextField txtImagem = new TextField("Imagem");
		txtImagem.setStyleName("caption-align-plano-acao");
		txtImagem.setWidth("90%");
				
		FormLayout frm_txtImagem = new FormLayout(){					
			{
				setStyleName("form-cutom");
				setMargin(true);
				setSpacing(true);	
				
				addComponent(txtImagem);
			}
		};
		
		vl_detalhe_plano_acao.addComponent(frm_txtImagem);
		
		
		final TextField txtBusca = new TextField("");
		txtBusca.setInputPrompt("busca...");
		txtBusca.setStyleName("caption-align-plano-acao");
		txtBusca.setWidth("90%");
				
		FormLayout frm_txtBusca = new FormLayout(){					
			{
				setStyleName("form-cutom");
				setMargin(true);
				setSpacing(true);	
				
				addComponent(txtBusca);
			}
		};
		
		vl_detalhe_plano_acao.addComponent(frm_txtBusca);
		
		
		JPAContainer<OcorrenciaPlanoAcao> container = JPAContainerFactory.make(OcorrenciaPlanoAcao.class, ConnUtil.getEntity());
		
		Table tbDetalhes = new Table(null, container);
		tbDetalhes.setVisibleColumns(new Object[]{"data","detalhe"});
		tbDetalhes.setWidth("100%");
		tbDetalhes.setHeight("90px");
		vl_detalhe_plano_acao.addComponent(tbDetalhes);
		
			
		HorizontalLayout hlButton = new HorizontalLayout();
		hlButton.setStyleName("hl_buttons_bottom");
		hlButton.setSpacing(true);
		hlButton.setMargin(true);
		
		Button btOk = new Button("Ok", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
			}
		});
		btOk.addStyleName("default");
		hlButton.addComponent(btOk);
		
		Button btCancelar = new Button("Cancelar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				w_detalhe_plano.close();
			}
		});
		hlButton.addComponent(btCancelar);
		
		hlButton.setComponentAlignment(btOk, Alignment.MIDDLE_RIGHT);
		hlButton.setComponentAlignment(btCancelar, Alignment.MIDDLE_RIGHT);
		
		vl_detalhe_plano_acao.addComponent(hlButton);
		vl_detalhe_plano_acao.setComponentAlignment(hlButton, Alignment.MIDDLE_RIGHT);
		
		getUI().addWindow(w_detalhe_plano);
		taObs.focus();
		
	}
	
	private void abrir_janela_editar(Item item){
		
		EntityItem<PlanoAcao> ei = (EntityItem<PlanoAcao>)item;
		final PlanoAcao p_acao = ei.getEntity();
		
		VerticalLayout vl_cadastro = new VerticalLayout();
		vl_cadastro.setWidth("95%");
		//vl_cadastro.setHeight("95%");
		vl_cadastro.setSpacing(false); 
		vl_cadastro.setMargin(true);
		vl_cadastro.setStyleName("border-form-plano-acao");
		
		final Window w_cadastrar_nova_acao = new Window("Editar Ação");
		w_cadastrar_nova_acao.center();
		w_cadastrar_nova_acao.setModal(true);		
		w_cadastrar_nova_acao.setWidth("643px");
		w_cadastrar_nova_acao.setHeight("481px");
		w_cadastrar_nova_acao.setContent(vl_cadastro);
		w_cadastrar_nova_acao.setResizable(false); 
		
		w_cadastrar_nova_acao.addCloseListener(new Window.CloseListener() {
			
			@Override
			public void windowClose(CloseEvent e) {
				buildLayout();
			}
		});
		
		final TextArea taAcao = new TextArea("Ação");
		taAcao.setStyleName("caption-align-plano-acao");
		taAcao.setWidth("90%");
		taAcao.setHeight("70px");
		taAcao.setValue(p_acao.getAcao());
		
		FormLayout frm_taAcao = new FormLayout(){					
			{
				setStyleName("form-cutom");
				setMargin(true);
				setSpacing(true);	
				
				addComponent(taAcao);
			}
		};
		vl_cadastro.addComponent(frm_taAcao);		
		
		final TextField txtOrigem = new TextField("Origem");
		txtOrigem.setStyleName("caption-align-plano-acao");
		txtOrigem.setWidth("90%");
		txtOrigem.setValue(p_acao.getOrigem_acao());
		
		FormLayout frm_txtOrigem = new FormLayout(){					
			{
				setStyleName("form-cutom");
				setMargin(true);
				setSpacing(true);	
				
				addComponent(txtOrigem);
			}
		};
		vl_cadastro.addComponent(frm_txtOrigem);
			
		final TextArea taComo = new TextArea("Como");
		taComo.setStyleName("caption-align-plano-acao");
		taComo.setWidth("90%");
		taComo.setHeight("50px");
		taComo.setValue(p_acao.getComo());
		
		FormLayout frm_taComo = new FormLayout(){					
			{
				setStyleName("form-cutom");
				setMargin(true);
				setSpacing(true);	
				
				addComponent(taComo);
			}
		};
		vl_cadastro.addComponent(frm_taComo);
				
		final TextArea taObjetivo = new TextArea("Objetivo");
		taObjetivo.setStyleName("caption-align-plano-acao");
		taObjetivo.setWidth("90%");
		taObjetivo.setHeight("50px");
		taObjetivo.setValue(p_acao.getObjetivo());
		
		FormLayout frm_taObjetivo = new FormLayout(){					
			{
				setStyleName("form-cutom");
				setMargin(true);
				setSpacing(true);	
				
				addComponent(taObjetivo);
			}
		};
		
		vl_cadastro.addComponent(frm_taObjetivo);
				
		final TextField txtResponsavel = new TextField("Responsável");
		txtResponsavel.setStyleName("caption-align-plano-acao");
		txtResponsavel.setWidth("90%");
		txtResponsavel.setValue(p_acao.getResponsavel());
		
		FormLayout frm_txtResponsavel = new FormLayout(){					
			{
				setStyleName("form-cutom");
				setMargin(true);
				setSpacing(true);	
				
				addComponent(txtResponsavel);
			}
		};
		
		vl_cadastro.addComponent(frm_txtResponsavel);
				
		final TextField txtOnde = new TextField("Onde");
		txtOnde.setStyleName("caption-align-plano-acao");
		txtOnde.setWidth("90%");
		txtOnde.setValue(p_acao.getOnde());
		
		FormLayout frm_txtOnde = new FormLayout(){					
			{
				setStyleName("form-cutom");
				setMargin(true);
				setSpacing(true);	
				
				addComponent(txtOnde);
			}
		};
		
		vl_cadastro.addComponent(frm_txtOnde);
				
		final DateField dfPrazo = new DateField("Prazo");
		dfPrazo.setStyleName("caption-align-plano-acao");
		dfPrazo.setWidth("30%");
		dfPrazo.setValue(p_acao.getData_prazo());
		
		FormLayout frm_dfPrazo = new FormLayout(){					
			{
				setStyleName("form-cutom");
				setMargin(true);
				setSpacing(true);	
				
				addComponent(dfPrazo);
			}
		};
		
		vl_cadastro.addComponent(frm_dfPrazo);
		
		final DateField dfConclusao = new DateField("Conclusao");
		dfConclusao.setStyleName("caption-align-plano-acao");
		dfConclusao.setWidth("30%");
		dfConclusao.setValue(p_acao.getData_conclusao());
		
		FormLayout frm_dfConclusao = new FormLayout(){					
			{
				setStyleName("form-cutom");
				setMargin(true);
				setSpacing(true);	
				
				addComponent(dfConclusao);
			}
		};
		
		vl_cadastro.addComponent(frm_dfConclusao);
		
		HorizontalLayout hlButton = new HorizontalLayout();
		hlButton.setStyleName("hl_buttons_bottom");
		hlButton.setSpacing(true);
		hlButton.setMargin(true);
		
		Button btOk = new Button("Ok", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				p_acao.setAcao(taAcao.getValue());
				p_acao.setOrigem_acao(txtOrigem.getValue());
				p_acao.setComo(taComo.getValue());
				p_acao.setObjetivo(taObjetivo.getValue());
				p_acao.setResponsavel(txtResponsavel.getValue());
				p_acao.setOnde(txtOnde.getValue());
				p_acao.setData_prazo(dfPrazo.getValue());
				p_acao.setData_conclusao(dfConclusao.getValue());
				
				if(p_acao.getData_conclusao() != null){
					p_acao.setStatus("Concluido");
				}else{
					p_acao.setStatus("Pendente");
				}
								
				boolean check = PlanoAcaoDAO.salvar(p_acao);
				
				if(check){
					Notify.Show("Plano de Ação atualizado com Sucesso!", Notify.TYPE_SUCCESS);
					w_cadastrar_nova_acao.close();
				}
			}
		});
		btOk.addStyleName("default");
		hlButton.addComponent(btOk);
		
		Button btCancelar = new Button("Cancelar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				w_cadastrar_nova_acao.close();
			}
		});
		hlButton.addComponent(btCancelar);
		
		hlButton.setComponentAlignment(btOk, Alignment.MIDDLE_RIGHT);
		hlButton.setComponentAlignment(btCancelar, Alignment.MIDDLE_RIGHT);
		
		vl_cadastro.addComponent(hlButton);
		vl_cadastro.setComponentAlignment(hlButton, Alignment.MIDDLE_RIGHT);
		
		getUI().addWindow(w_cadastrar_nova_acao);
		taAcao.focus();
		
	}
	
	private void abrir_janela_novo(){
		
		VerticalLayout vl_cadastro = new VerticalLayout();
		vl_cadastro.setWidth("95%");
		//vl_cadastro.setHeight("95%");
		vl_cadastro.setSpacing(false); 
		vl_cadastro.setMargin(true);
		vl_cadastro.setStyleName("border-form-plano-acao");
		
		final Window w_cadastrar_nova_acao = new Window("Cadastrar nova Ação");
		w_cadastrar_nova_acao.center();
		w_cadastrar_nova_acao.setModal(true);		
		w_cadastrar_nova_acao.setWidth("643px");
		w_cadastrar_nova_acao.setHeight("431px");
		w_cadastrar_nova_acao.setContent(vl_cadastro);
		w_cadastrar_nova_acao.setResizable(false); 
		
		w_cadastrar_nova_acao.addCloseListener(new Window.CloseListener() {
			
			@Override
			public void windowClose(CloseEvent e) {
				buildLayout();
			}
		});
		
		final TextArea taAcao = new TextArea("Ação");
		taAcao.setStyleName("caption-align-plano-acao");
		taAcao.setWidth("90%");
		taAcao.setHeight("70px");
		
		FormLayout frm_taAcao = new FormLayout(){					
			{
				setStyleName("form-cutom");
				setMargin(true);
				setSpacing(true);	
				
				addComponent(taAcao);
			}
		};
		vl_cadastro.addComponent(frm_taAcao);		
		
		final TextField txtOrigem = new TextField("Origem");
		txtOrigem.setStyleName("caption-align-plano-acao");
		txtOrigem.setWidth("90%");
		
		FormLayout frm_txtOrigem = new FormLayout(){					
			{
				setStyleName("form-cutom");
				setMargin(true);
				setSpacing(true);	
				
				addComponent(txtOrigem);
			}
		};
		vl_cadastro.addComponent(frm_txtOrigem);
			
		final TextArea taComo = new TextArea("Como");
		taComo.setStyleName("caption-align-plano-acao");
		taComo.setWidth("90%");
		taComo.setHeight("50px");
		
		FormLayout frm_taComo = new FormLayout(){					
			{
				setStyleName("form-cutom");
				setMargin(true);
				setSpacing(true);	
				
				addComponent(taComo);
			}
		};
		vl_cadastro.addComponent(frm_taComo);
				
		final TextArea taObjetivo = new TextArea("Objetivo");
		taObjetivo.setStyleName("caption-align-plano-acao");
		taObjetivo.setWidth("90%");
		taObjetivo.setHeight("50px");
		
		FormLayout frm_taObjetivo = new FormLayout(){					
			{
				setStyleName("form-cutom");
				setMargin(true);
				setSpacing(true);	
				
				addComponent(taObjetivo);
			}
		};
		
		vl_cadastro.addComponent(frm_taObjetivo);
				
		final TextField txtResponsavel = new TextField("Responsável");
		txtResponsavel.setStyleName("caption-align-plano-acao");
		txtResponsavel.setWidth("90%");
		
		FormLayout frm_txtResponsavel = new FormLayout(){					
			{
				setStyleName("form-cutom");
				setMargin(true);
				setSpacing(true);	
				
				addComponent(txtResponsavel);
			}
		};
		
		vl_cadastro.addComponent(frm_txtResponsavel);
				
		final TextField txtOnde = new TextField("Onde");
		txtOnde.setStyleName("caption-align-plano-acao");
		txtOnde.setWidth("90%");
		
		FormLayout frm_txtOnde = new FormLayout(){					
			{
				setStyleName("form-cutom");
				setMargin(true);
				setSpacing(true);	
				
				addComponent(txtOnde);
			}
		};
		
		vl_cadastro.addComponent(frm_txtOnde);
				
		final DateField dfPrazo = new DateField("Prazo");
		dfPrazo.setStyleName("caption-align-plano-acao");
		dfPrazo.setWidth("30%");
		
		FormLayout frm_dfPrazo = new FormLayout(){					
			{
				setStyleName("form-cutom");
				setMargin(true);
				setSpacing(true);	
				
				addComponent(dfPrazo);
			}
		};
		
		vl_cadastro.addComponent(frm_dfPrazo);
		
		HorizontalLayout hlButton = new HorizontalLayout();
		hlButton.setStyleName("hl_buttons_bottom");
		hlButton.setSpacing(true);
		hlButton.setMargin(true);
		
		Button btOk = new Button("Ok", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				PlanoAcao pa = new PlanoAcao(null, taAcao.getValue(), 
						txtOrigem.getValue(), taObjetivo.getValue() ,taComo.getValue() , txtResponsavel.getValue(), 
						txtOnde.getValue(), new Date(), dfPrazo.getValue(), null, "Pendente", OpusERP4UI.getUsuarioLogadoUI());
				
				boolean check = PlanoAcaoDAO.salvar(pa);
				
				if(check){
					Notify.Show("Plano de Ação cadastrado com Sucesso!", Notify.TYPE_SUCCESS);
					w_cadastrar_nova_acao.close();
				}
			}
		});
		btOk.addStyleName("default");
		hlButton.addComponent(btOk);
		
		Button btCancelar = new Button("Cancelar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				w_cadastrar_nova_acao.close();
			}
		});
		hlButton.addComponent(btCancelar);
		
		hlButton.setComponentAlignment(btOk, Alignment.MIDDLE_RIGHT);
		hlButton.setComponentAlignment(btCancelar, Alignment.MIDDLE_RIGHT);
		
		vl_cadastro.addComponent(hlButton);
		vl_cadastro.setComponentAlignment(hlButton, Alignment.MIDDLE_RIGHT);
		
		getUI().addWindow(w_cadastrar_nova_acao);
		taAcao.focus();
		
	}
	
	VerticalLayout vlListagem;
	Table tb; 
	ComboBox cbStatus;
	ComboBox cbDatas;
	TextField txtBusca;
	
	
	String status = "";
	String busca = "";
	
	private Component getListagem(){
		vlListagem = new VerticalLayout();			
		vlListagem.setSizeFull();
				
		HorizontalLayout hl_barra = new HorizontalLayout();
		hl_barra.setStyleName("cab-plano-cao");
		hl_barra.setWidth("100%");

		Button btNovo = new Button("Novo", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(config != null){
					abrir_janela_novo();
				}else{
					formNovaConfig();
				}
			}
		});
		btNovo.setStyleName("default");
		
		cbStatus = new ComboBox();
		cbStatus.setHeight("31px");
		cbStatus.setNullSelectionAllowed(true);
		cbStatus.setTextInputAllowed(false); 
		cbStatus.addItem("PENDENTES");
		cbStatus.addItem("CONCLUÍDOS");
		
		if(status != null && !status.equals("")){
			cbStatus.select(status); 
		}
		
		cbStatus.setImmediate(true);
		cbStatus.addValueChangeListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				status = cbStatus.getValue() != null  ? cbStatus.getValue().toString() : "";
				addFilter();
			}
		});
		
		 
		cbDatas = new ComboBox();
		cbDatas.setHeight("31px");
		cbDatas.setNullSelectionAllowed(true);
		cbDatas.setTextInputAllowed(false); 
		cbDatas.addItem("DATA ABERTURA");
		cbDatas.addItem("DATA PRAZO");
		cbDatas.addItem("DATA CONCLUSAO");
		
		DateField df_data = new DateField();
		df_data.setHeight("31px");
		df_data.setStyleName("data-plano-busca");
		
		txtBusca = new TextField();
		txtBusca.setDescription("buscar..");
		txtBusca.setWidth("100%");
		txtBusca.setHeight("31px");
		if(busca != null && !busca.equals("")){
			txtBusca.setValue(busca); 
		}
		txtBusca.addValueChangeListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				busca = txtBusca.getValue() != null ? txtBusca.getValue() : "";
				addFilter();
			}
		});
		
		//txtBusca.add
			
		hl_barra.addComponent(btNovo);
		hl_barra.addComponent(cbStatus);
		//hl_barra.addComponent(cbDatas);
		//hl_barra.addComponent(df_data);
		hl_barra.addComponent(txtBusca);
		hl_barra.setExpandRatio(txtBusca, 1f);
		
		
		
		tb = new Table(null, buildContainer()){
			@Override
			protected String formatPropertyValue(Object rowId, Object colId,
					Property<?> property) {
				
				if(colId.equals("data_abertura") || colId.equals("data_prazo") || colId.equals("data_conclusao")){
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
					
					Date d = (Date)tb.getItem(rowId).getItemProperty(colId).getValue();
					if(d != null){
						return sdf.format(d);
					}else{
						return "";
					}
					
				}
				
				if(colId.equals("status")){
					
					if(tb.getItem(rowId).getItemProperty(colId).getValue().equals("Pendente")){
						
						DateTime dt1 = new DateTime((Date)tb.getItem(rowId).getItemProperty("data_prazo").getValue());
						
						if(dt1.isAfter(new DateTime())){
							return "No Prazo";
						}else{
							return "Atrasado";
						}
					}		
										
				}
				
								
				return super.formatPropertyValue(rowId, colId, property);			
			}
		};
		tb.setStyleName("listagem-plano-acao");
		tb.setSizeFull();
		tb.setVisibleColumns(new Object[] {
				"id","acao","origem_acao","objetivo","como",
				"responsavel","onde","data_abertura","data_prazo",
				"data_conclusao","status"});
		
		tb.setColumnHeader("id", "COD");
		tb.setColumnHeader("acao", "AÇÃO");
		tb.setColumnHeader("origem_acao", "ORIGEM");
		tb.setColumnHeader("objetivo", "OBJETIVO");
		tb.setColumnHeader("como", "COMO");
		tb.setColumnHeader("responsavel", "RESPONSÁVEL");
		tb.setColumnHeader("onde", "ONDE");
		tb.setColumnHeader("data_abertura", "DATA ABERTURA");
		tb.setColumnHeader("data_prazo", "PRAZO");
		tb.setColumnHeader("data_conclusao", "DATA CONCLUSÃO");
		tb.setColumnHeader("status", "STATUS");
		
		tb.setColumnExpandRatio("id", 0.03f);
		tb.setColumnExpandRatio("acao", 0.15f);
		tb.setColumnExpandRatio("origem_acao",0.06f);
		tb.setColumnExpandRatio("objetivo", 0.10f);
		tb.setColumnExpandRatio("como", 0.10f);
		tb.setColumnExpandRatio("responsavel",0.10f);
		tb.setColumnExpandRatio("onde", 0.1f);
		tb.setColumnExpandRatio("data_abertura",0.11f);
		tb.setColumnExpandRatio("data_prazo",0.07f);
		tb.setColumnExpandRatio("data_conclusao", 0.12f);
		tb.setColumnExpandRatio("status", 0.07f);
		tb.setColumnExpandRatio("e", 0.08f);
		
		tb.addGeneratedColumn("e", new Table.ColumnGenerator() {
			
			@Override
			public Object generateCell(final Table source,final Object itemId,final  Object columnId) {
							
				VerticalLayout hl = new VerticalLayout();
				
				Button btEditar = new Button("Editar", new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						abrir_janela_editar(source.getItem(itemId));
					}
				});
				btEditar.addStyleName(Reindeer.BUTTON_SMALL);
				
				Button btExcluir = new Button("Excluir", new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						
						GenericDialog gd = new GenericDialog("Confirme para continuar", "Deseja realmente excluir este registro ?", true, true);
						gd.addListerner(new GenericDialog.DialogListerner() {
							
							@Override
							public void onClose(DialogEvent event) {
								if(event.isConfirm()){
									container.removeItem(itemId);
									buildLayout();
								}
							}
						});
						
						getUI().addWindow(gd); 
					}
				});
				btExcluir.addStyleName(Reindeer.BUTTON_SMALL);
				
				
				Button btDetalhes = new Button("Detalhes", new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						abrir_janela_detalhes(null);
					}
				});
				btDetalhes.addStyleName(Reindeer.BUTTON_SMALL);
								
				
				hl.addComponent(btEditar);
				hl.addComponent(btExcluir);
				//hl.addComponent(btDetalhes);
				
				return hl;
			}
		});
		
		tb.setCellStyleGenerator(new Table.CellStyleGenerator() {
			
			@Override
			public String getStyle(Table source, Object itemId, Object propertyId) {
				
				if(source.getItem(itemId).getItemProperty("status") != null && propertyId != null && 
						source.getItem(itemId).getItemProperty("status").getValue().toString().equals("Concluido") && propertyId.equals("status")){
					return "row-plano-acao-status-concluido";
				}
				
				if(source.getItem(itemId).getItemProperty("status") != null && propertyId != null && 
						source.getItem(itemId).getItemProperty("status").getValue().toString().equals("Pendente") && propertyId.equals("status")){
					
					DateTime dt1 = new DateTime((Date)tb.getItem(itemId).getItemProperty("data_prazo").getValue());
					
					if(dt1.isAfter(new DateTime())){
						return "row-plano-acao-status-no-prazo";
					}else{
						return "row-plano-acao-status-atrasado";
					}
				}	
								
				return null;
			}
		});
		
		vlListagem.addComponent(hl_barra);
		vlListagem.addComponent(tb); 
		
		vlListagem.setExpandRatio(tb, 1f);
		
		//atualizar_totalizadores();
		
		addFilter();
				
		return vlListagem;
		
	}
	
	private void addFilter(){
		container.setApplyFiltersImmediately(false);
		container.removeAllContainerFilters();
		container.addContainerFilter(Filters.eq("operador", OpusERP4UI.getUsuarioLogadoUI()));
		
		if(status != null){
			if(status.equals("PENDENTES")){
				container.addContainerFilter(Filters.eq("status", "Pendente"));
			}
			if(status.equals("CONCLUÍDOS")){
				container.addContainerFilter(Filters.eq("status", "Concluido"));
			}
		}
		
		if(busca != null){
			String s = busca;
			
			Object[] collums = tb.getVisibleColumns();		
			List<Filter> filtros = new ArrayList<Filter>();			
				
			for(Object c:collums){		 			
				if(!c.toString().equals("e") && container.getType(c.toString()) == String.class ){					   	
					filtros.add(new Like(c.toString(), "%"+s+"%", false));
				}	
			}
				
			container.addContainerFilter(Filters.or(filtros));			
		}
		
		container.applyFilters();
	}


	JPAContainer<PlanoAcao> container; 
	private Container buildContainer() {
		container = JPAContainerFactory.make(PlanoAcao.class, ConnUtil.getEntity());
		container.addContainerFilter(Filters.eq("operador", OpusERP4UI.getUsuarioLogadoUI()));
		container.addContainerFilter(Filters.eq("status", "Pendente"));
				
		container.sort(new String[] {"data_prazo"}, new boolean[]{true});
						
		return container;
	}

	private Component getLogo(){
		try{
			
			//
				
			vl_logo = new VerticalLayout();		
			vl_logo.setHeight("100%");
			vl_logo.setStyleName("vl_plano_acao_logo");
			
			String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();			
			
			file = new File(basepath + "/WEB-INF/uploads/logo.jpeg");			
			writeFile(file, (byte[]) OpusERP4UI.getEmpresa().getLogo_empresa());			
								
			logo = new Image(null, new FileResource(file));
			logo.setWidth("100%");
			vl_logo.addComponent(logo);
			
			return vl_logo;
		}catch(Exception e){
			e.printStackTrace();
			
			return null;
		}
	}
	
	VerticalLayout vl_criacao_gestor;
	private Component getCriacaoGestor(){
		try{
			
			vl_criacao_gestor = new VerticalLayout();	
			vl_criacao_gestor.setHeight("96%");
			
			String data = config != null && config.getData_criacao() != null ? config.getData_criacao() : "";
			String gestor = config != null && config.getNome_gestor() != null ? config.getNome_gestor() : "";
			
			 
			Label lb1 = new Label(""
					+ "<div style='border: 2px solid #000; font-size: 16px;   text-align: center; border-left: 0; border-bottom: 0;height: 100%;'>"
					+ "<h3 style='margin: 0; font-weight: bold; color: #000;font-size: 1em;' >Criado em: <br/> "+data+" </h3>"
					+ "</div>",ContentMode.HTML);
			lb1.setHeight("100%");
			vl_criacao_gestor.addComponent(lb1);
			
			Label lb2 = new Label(""
					+ "<div style='border: 2px solid #000; font-size: 16px;   text-align: center; border-left: 0;height: 100%;' >"
					+ "<h3 style='margin: 0; font-weight: bold; color: #000;font-size: 1em;'>Gestor: <br/>  "+gestor+"</h3>"
					+ "</div>",ContentMode.HTML);
			lb2.setHeight("100%");			
			vl_criacao_gestor.addComponent(lb2);
			
			vl_criacao_gestor.setExpandRatio(lb1, 0.46f);
			vl_criacao_gestor.setExpandRatio(lb2, 0.46f);
			
			return vl_criacao_gestor;
		}catch(Exception e){
			e.printStackTrace();
			
			return null;
		}
	}
	
	VerticalLayout vl_titulo;
	private Component getTitulo(){
		try{
			
			String nome = config != null && config.getNome_plano() != null ? config.getNome_plano() : "";
			
			vl_titulo = new VerticalLayout();	
			vl_titulo.setHeight("96%");
			
			 Label lb1 = new Label(""
					+ "<div style='border: 2px solid #000; font-size: 16px;   text-align: center; border-left: 0; border-bottom: 0; height:100%;background-color: #a2d7f3;color: #000;'>"
					+ "<h1 style=' font-weight: bold;  font-size: 1.9em; width: 100%;  padding-top: 0.1em;  font-family: Verdana; margin: 0;'>PLANO DE AÇÃO </h1>"
				+ "</div>",ContentMode.HTML);
			lb1.setHeight("100%");
			
			vl_titulo.addComponent(lb1);

			Label lb2 = new Label(""
					+ "<div style='border: 2px solid #000; font-size: 16px;   text-align: center; border-left: 0; height:100%;'>"
					+ "<h1 style=' font-weight: bold;  font-size: 1.9em; width: 100%;  padding-top: 0.2em;  font-family: Verdana; margin: 0;color: #080362;'>"+nome+"</h1>",ContentMode.HTML);
			lb2.setHeight("100%");
			
			
			vl_titulo.addComponent(lb2);
			
			vl_titulo.setExpandRatio(lb1, 0.46f);
			vl_titulo.setExpandRatio(lb2, 0.46f);
			
			return vl_titulo;
		}catch(Exception e){
			e.printStackTrace();
			
			return null;
		}
	}
		
	VerticalLayout vl_totalizadores;
	private Component getTotalizadores(){
		try{			
			vl_totalizadores = new VerticalLayout();		
			vl_totalizadores.setHeight("100%");
			
			DecimalFormat df2 = new DecimalFormat("#.##");
			
			Integer total = PlanoAcaoDAO.getSizeGeral();
			Integer concluidas = PlanoAcaoDAO.getSizeConcluido();
			Integer no_prazo = PlanoAcaoDAO.getSizeNoPrazo();
			Integer atrasadas = PlanoAcaoDAO.getSizeAtrasado();
			
			Integer perc_criado = 100;
			String perc_concluidas = df2.format(new Float(((double)(new Float(concluidas) / new Float(total))) * 100));
			String perc_no_prazo = df2.format(new Float(((double)(new Float(no_prazo) / new Float(total))) * 100));
			String perc_atrasadas = df2.format(new Float(((double)(new Float(atrasadas) / new Float(total))) * 100));
			
			Label lb1 = new Label(
					"<table style='border: 2px solid #000;   width: 100%;  border-left: 0; color:#000;'>"
						+ "<tr style='background-color:#fff;'>"
							+ "<td style='width:290px;text-align:center;font-weight: bold;'>AÇÕES CRIADAS</td>"
							+ "<td style='width:40px;text-align:center;font-weight: bold;'>"+total+"</td>"
							+ "<td style='text-align:center;font-weight: bold;'>"+perc_criado+"%</td>"
						+ "</tr>"					
					+ "</table>",ContentMode.HTML);
			vl_totalizadores.addComponent(lb1);
			
			Label lb2 = new Label(
					"<table style='border: 2px solid #000;   width: 100%;  border-left: 0;border-top:0;color:#000;'>"					
							+ "<tr style='background-color:#07d307;'>"
								+ "<td style='width:290px;text-align:center;font-weight: bold;'>AÇÕES CONCLUÍDAS</td>"
								+ "<td style='width:40px;text-align:center;font-weight: bold;'>"+concluidas+"</td>"
								+ "<td style='text-align:center;font-weight: bold;'>"+perc_concluidas+"%</td>"
							+ "</tr>"				
						+ "</table>",ContentMode.HTML);
			
			vl_totalizadores.addComponent(lb2);
			
			Label lb3 = new Label(
					"<table style='border: 2px solid #000;   width: 100%;  border-left: 0;border-top:0;color:#000;'>"						
							+ "<tr style='background-color:yellow;'>"
								+ "<td style='width:290px;text-align:center;font-weight: bold;'>AÇÕES NO PRAZO</td>"
								+ "<td style='width:40px;text-align:center;font-weight: bold;'>"+no_prazo+"</td>"
								+ "<td style='text-align:center;font-weight: bold;'>"+perc_no_prazo+"%</td>"
							+ "</tr>"				
						+ "</table>",ContentMode.HTML);
			vl_totalizadores.addComponent(lb3);
			
			Label lb4 = new Label(
					"<table style='border: 2px solid #000;   width: 100%;  border-left: 0;border-top:0;color:#000;'>"
							+ "<tr style='background-color:red;'>"
								+ "<td style='width:290px;text-align:center;font-weight: bold;'>AÇÕES ATRASADAS</td>"
								+ "<td style='width:40px;text-align:center;font-weight: bold;'>"+atrasadas+"</td>"
								+ "<td style='text-align:center;font-weight: bold;'>"+perc_atrasadas+"%</td>"
							+ "</tr>"
						+ "</table>",ContentMode.HTML);
			
			vl_totalizadores.addComponent(lb4);
			
			vl_totalizadores.setExpandRatio(lb1, 0.25f);
			vl_totalizadores.setExpandRatio(lb2, 0.25f);
			vl_totalizadores.setExpandRatio(lb3, 0.25f);
			vl_totalizadores.setExpandRatio(lb4, 0.25f);
						
			return vl_totalizadores;
		}catch(Exception e){
			e.printStackTrace();
			
			return null;
		}
	}
	
	
	private Button buildBtClose(){
    	btClose = new Button();
    	btClose.setWidth("16px");
    	//btClose.addStyleName("configure");
    	btClose.addStyleName("btClose");
    	btClose.setIcon(new ThemeResource("icons/icon_close.png"));
    	btClose.addStyleName("icon-only");
    	btClose.addStyleName("borderless");
    	btClose.setDescription("Fechar");
    	btClose.addStyleName("small");  
    	btClose.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	  PrefeDashDAO.remove(PlanoAcaoApp.class.toString());
                  ((GridLayout)getParent()).removeComponent(btClose.getParent());   
            }
        });
        
        return btClose;
    }
    
    Window winMaximize;
    
    private Button buildBtMaximize(){
    	
    	btMaximize = new Button();
    	btMaximize.setWidth("16px");
    	//btMaximize.addStyleName("configure");
    	btMaximize.addStyleName("btMaximize");
    	btMaximize.setIcon(new ThemeResource("icons/icon_maximize.png"));
    	btMaximize.addStyleName("icon-only");
    	btMaximize.addStyleName("borderless");
    	btMaximize.setDescription("Maximizar");
    	btMaximize.addStyleName("small");
    	btMaximize.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                winMaximize = new Window("PLANO DE AÇÃO");
                winMaximize.setHeight("624px");
                winMaximize.setWidth("989px");                
                winMaximize.setModal(true);
                winMaximize.center();
                winMaximize.setContent(vlRoot);
                
                winMaximize.addCloseListener(new Window.CloseListener() {
					
					@Override
					public void windowClose(CloseEvent e) {						
						buildLayout();
					}
				});
                
                setLayoutModal(winMaximize); 
                
                getUI().addWindow(winMaximize);                
            }
        });
        
        return btMaximize;
    }
	
    private void setLayoutModal(Window w){
    	
    }
	
    public void writeFile(File file, byte[] data) throws IOException
	 {
	   OutputStream fo = new FileOutputStream(file);
	   // Write the data
	   fo.write(data);
	   // flush the file (down the toilet)
	   fo.flush();
	   // Close the door to keep the smell in.
	   fo.close();
	 }
	
}

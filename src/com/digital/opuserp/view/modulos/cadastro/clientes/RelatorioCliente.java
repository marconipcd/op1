package com.digital.opuserp.view.modulos.cadastro.clientes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.util.ConnUtil;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

public class RelatorioCliente extends Window {

	JPAContainer<Cliente> container = JPAContainerFactory.make(Cliente.class, ConnUtil.getEntity());
	
	ComboBox cbOrderDirection;
	ComboBox cbOrder;
	TextField tfValorFitlro;
	ComboBox cbFiltro;
	DateField cbDataFinal;
	DateField cbDataInicial;
	ComboBox cbDatas;
	
	String width_filtro;
	String width_ordenacao;
	
	
	public RelatorioCliente(){
		super("Relatório de Cliente2");
		setResizable(false);
		configLayout();
		initLayout();
	}
	
	private void configLayout() {
		// 1366x768
		//if (OpusERP4UI.browser.getScreenWidth() >= 1024 && OpusERP4UI.browser.getScreenHeight() >= 768) {
			setWidth("500px");
			setHeight("246px");

			 width_filtro = "145px";
			 width_ordenacao = "145px";

		//}
	}
	
	private void initLayout(){
		setContent(new VerticalLayout(){
			{			
				
				setMargin(true);
			
						setWidth("100%");
						addComponent(new FormLayout(){
							 {
								 setStyleName("form-cutom");
								 cbDatas = new ComboBox("Data");
								 cbDatas.setWidth("145px");
								 cbDatas.addItem("CADASTRO");
								 cbDatas.addItem("ALTERACAO");		
								 cbDatas.setStyleName("caption-align-relatorio-cliente");
								 addComponent(cbDatas);
							 }
						 });
						 
						
						
				addComponent(new HorizontalLayout(){
					{
						setWidth("100%");
						addComponent(new FormLayout(){
							 {
								 setStyleName("form-cutom");
								 cbDataInicial = new DateField("Data Inicial");		
								 cbDataInicial.setWidth("145px");
								 cbDataInicial.setStyleName("caption-align-relatorio-cliente");
								 addComponent(cbDataInicial);
							 }
						 });
						 
						
					}
				});
				
				
				addComponent(new HorizontalLayout(){
					{
						setWidth("100%");
						addComponent(new FormLayout(){
							 {
								 setStyleName("form-cutom");
								 cbDataFinal = new DateField("Data Final");	
								 cbDataFinal.setWidth("145px");
								 cbDataFinal.setStyleName("caption-align-relatorio-cliente");
								 addComponent(cbDataFinal);
							 }
						 });
						 
						 
					}
				});
				
				
				addComponent(new HorizontalLayout(){
					{
						//setWidth("100%");
						addComponent(new FormLayout(){
							 {
								 cbFiltro = new ComboBox("Filtro");		
								 cbFiltro.setWidth(width_filtro);
								 Collection<String> campos = container.getContainerPropertyIds();								 
								 for(String campo:campos){
									 if(container.getType(campo).equals(String.class) && checkExceptionCollums(campo)){										 
										 cbFiltro.addItem(replaceCollum(campo));
									 }
								 }
								 
								 
								 addComponent(cbFiltro);
								 setStyleName("form-cutom");
								 cbFiltro.setStyleName("caption-align-relatorio-cliente");
							 }
						 });
						 
						 addComponent(new FormLayout(){
							 {
								 setStyleName("form-cutom");
								 tfValorFitlro = new TextField();		
								 tfValorFitlro.setWidth("145px");
								 addComponent(tfValorFitlro);
								 tfValorFitlro.setStyleName("auxfield");
							 }
						 });
					}
				});
				
				
				addComponent(new HorizontalLayout(){
					{
						//setWidth("100%");
						addComponent(new FormLayout(){
							 {
								 setStyleName("form-cutom");
								 cbOrder = new ComboBox("Ordenação");
								 cbOrder.setWidth(width_ordenacao);
								 Collection<String> campos = container.getContainerPropertyIds();								 
								 for(String campo:campos){
									 if(container.getType(campo).equals(String.class) && checkExceptionCollums(campo)){										 
										 cbOrder.addItem(replaceCollum(campo));
									 }
								 }
								 
								 
								 cbOrder.setStyleName("caption-align-relatorio-cliente");
								 addComponent(cbOrder);
								 
							 }
						 });
						 
						 addComponent(new FormLayout(){
							 {
								 setStyleName("form-cutom");
								 cbOrderDirection = new ComboBox();	
								 cbOrderDirection.setWidth("145px");
								 cbOrderDirection.addItem("ASCENDENTE");
								 cbOrderDirection.addItem("DESCENDENTE");
								 cbOrderDirection.setStyleName("auxfield");
								 addComponent(cbOrderDirection);
							 }
						 });
					}
				});
				
				
				 
				 
				 addComponent(new HorizontalLayout(){
					 {
						 setWidth("100%");
						 setStyleName("hl_button_relatorio_cliente");
						 Button btExportar = new Button("Exportar", new Button.ClickListener() {
							
							@Override
							public void buttonClick(ClickEvent event) {
								try{
									//Notification.show("Implementar");
									Window winRelatorio = new Window("Relatório de Clientes");
									winRelatorio.setResizable(false);
									winRelatorio.setModal(true);
									winRelatorio.setWidth("800px");
									winRelatorio.setHeight("600px");
									winRelatorio.center();
									winRelatorio.setStyleName("disable_scroolbar");
									
									
									//Parametros
									String data = cbDatas.getItem(cbDatas.getValue()).toString();
									Date dataInit = new Date();
									Date dataFinal =new Date();
									String filtro = cbFiltro.getItem(cbFiltro.getValue()).toString();
									String valorFiltro = tfValorFitlro.getValue();
									String order = cbOrder.getItem(cbOrder.getValue()).toString();
									String orderDirection = cbOrder.getItem(cbOrder.getValue()).toString();
									
									
									
									
									StreamResource sr = new StreamResource(new RelatorioClienteExport(data,dataInit, dataFinal, filtro,valorFiltro, order, orderDirection), "RelatorioCliente.pdf");
									sr.setMIMEType("application/pdf");
									sr.getStream();
									Embedded pdf = new Embedded(null, sr);
									pdf.setType(Embedded.TYPE_BROWSER);
									pdf.setSizeFull();
									
									winRelatorio.setContent(pdf);
									
									getUI().addWindow(winRelatorio);									
								}catch(Exception e){
									e.printStackTrace();
								}
							}
						});
						 btExportar.setStyleName("default");
						 
						 addComponent(btExportar);
						 setComponentAlignment(btExportar, Alignment.BOTTOM_RIGHT);
					 }
				 });
			}
		});
	}
	
	
	private boolean checkExceptionCollums(String s){
		
		//Excessões
		List<String> collums = new ArrayList<>();
		collums.add("tipo_pessoa");
		//Excessões
		
		boolean b = true;
		for(String c:collums){
			if(s.equals(c)){
				b = false;
				break;
			}
		}
		
		
		return b;		
	}
	
	
	private String replaceCollum(String s){
		
		String key = s;
		String r = null;
		
		switch (key) {
		
			case "doc_rg_insc_estadual":
				r = "RG/Insc. Estadual";
			break;
			
			case "sexo":
				r = "Sexo";
			break;
			
			case "celular1":
				r = "Telefone Alternativo 3";
			break;

			case "forma_pgto_padrao":
				r = "Forma Pgto. Padrão";
			break;
			
			case "obs":
				r = "Observação";
			break;
			
			case "telefone2":
				r = "Telefone Alternativo 1";
			break;
			
			case "contato":
				r = "Contato";
			break;
			
			case "ddd_fone1":
				r = "DDD Telefone Principal";
			break;
			
			case "ddd_fone2":
				r = "DDD Telefone Alternativo 1";
			break;
			
			case "ddd_cel1":
				r = "DDD Telefone Alternativo 2";
			break;
			
			case "ddd_cel2":
				r = "DDD Telefone Alternativo 3";
			break;
			
			case "msn":
				r = "MSN";
			break;
			
			case "tipo_pessoa":
				r = "Tipo Pessoa";
			break;
			
			case "status":
				r = "Status";
			break;
			
			case "transportadora_padrao":
				r = "Transportadora Padrão";
			break;
			
			case "nome_razao":
				r = "Nome/Razão Social";
			break;
			
			case "telefone1":
				r = "Telefone 1";
			break;
			
			case "doc_cpf_cnpj":
				r = "CPF/CNPJ";
			break;
			
			case "como_nos_conheceu":
				r = "Como nos Conheceu";
			break;
			
			case "emailAlternativo":
				r = "Email Alternativo";
			break;
			
			case "nome_fantasia":
				r = "Nome Fantasia";
			break;
			
			case "email":
				r = "E-mail";
			break;
			
			case "site":
				r = "Site";
			break;
			
			case "celular2":
				r = "Telefone Alternativo 4";
			break;
			
			case "tratamento":
				r = "Tratamento";
			break;
			
			case "origem":
				r = "Origem";
			break;
			
			default :
				r = "Não Localizado:"+s;
			break;
		}
		
		return r;
	}
}

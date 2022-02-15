package com.digital.opuserp.view.modulos.acesso.contratos_acesso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.JodaTimePermission;

import com.digital.opuserp.domain.AcessoCliente;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.ContasReceber;
import com.digital.opuserp.util.ConnUtil;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Property;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class HistoricoCredito extends Window {
		
	Window window;		
	VerticalLayout vl;
	
	AcessoCliente contrato;
	
	public HistoricoCredito(boolean modal, boolean center, AcessoCliente contrato){
		
		super("Histórico de Crédito");
		
		this.contrato = contrato;
		
		setWidth("1100px");
		setHeight("600px");
		
		setModal(modal);
		if(center){
			center();
		}
		
		setClosable(true);
						
		vl = new VerticalLayout(){
			{
					//setSizeFull();
					setMargin(true);
					
					HorizontalLayout hlButtons2 = new HorizontalLayout();
					hlButtons2.setSpacing(true);
					hlButtons2.setMargin(true);
					hlButtons2.setStyleName("hl_buttons_bottom");
	
					addComponent(hlButtons2);
					setComponentAlignment(hlButtons2, Alignment.TOP_RIGHT);
					
					addComponent(buildLayout());
									
			}
			
				
		};
		setContent(vl);
	}
	
	 Table tbBoletosAberto;
	private Component buildLayout(){
		
		VerticalLayout vlRoot = new VerticalLayout(){
			{
				addComponent(new FormLayout(){
					{
						Cliente c = contrato.getCliente();
						
						DateField dfClienteDesde = new DateField("Cliente desde");	
						dfClienteDesde.setDateFormat("dd/MM/yyyy");
						dfClienteDesde.setValue(c.getData_cadastro());
						dfClienteDesde.setReadOnly(true);
						
						addComponent(dfClienteDesde); 
						
						TextField txtConsultaSpcSerasa = new TextField("Consulta SPC/SERASA");
						txtConsultaSpcSerasa.setValue(c.getCredito_cliente());
						txtConsultaSpcSerasa.setReadOnly(true);
						
						if(c.getCredito_cliente().equals("INADIMPLENTE")){
							txtConsultaSpcSerasa.addStyleName("invalid-cpf");	
						}
						
						
						addComponent(txtConsultaSpcSerasa); 
						
						JPAContainer<ContasReceber> container = JPAContainerFactory.make(ContasReceber.class, ConnUtil.getEntity());					
						//container.addContainerFilter(Filters.lt("data_vencimento", new Date()));
						container.addContainerFilter(Filters.not(Filters.eq("status", "FECHADO")));
						container.addContainerFilter(Filters.not(Filters.eq("status", "EXCLUIDO")));
						container.addContainerFilter(Filters.eq("cliente.doc_cpf_cnpj", c.getDoc_cpf_cnpj()));
						container.sort(new Object[]{"data_vencimento"}, new boolean[]{true});
						
						tbBoletosAberto = new Table("Boletos em Aberto", container){
							@Override
							protected String formatPropertyValue(Object rowId, Object colId,
									Property<?> property) {
								
								if(colId.equals("data_vencimento")){									
									if(tbBoletosAberto.getItem(rowId).getItemProperty("data_vencimento").getValue() != null){						
										
										SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
										Date dataVenc= (Date)tbBoletosAberto.getItem(rowId).getItemProperty("data_vencimento").getValue();
										
										return sdf.format(dataVenc);				
									}					
								}
								
								if(colId.equals("valor_titulo")){									
									if(tbBoletosAberto.getItem(rowId).getItemProperty("valor_titulo").getValue() != null){			
										
										String valor=tbBoletosAberto.getItem(rowId).getItemProperty("valor_titulo").getValue().toString();										
										return 	"R$ "+valor;	
									}					
								}
								
								if(colId.equals("status")) {
									
									DateTime dt1 = new DateTime(tbBoletosAberto.getItem(rowId).getItemProperty("data_vencimento").getValue());
									DateTime dt2 = new DateTime(new Date());
									
									
									if(dt1.isBefore(dt2) && tbBoletosAberto.getItem(rowId).getItemProperty("status").getValue().equals("ABERTO")){
										return "VENCIDO";
									}else{
										return tbBoletosAberto.getItem(rowId).getItemProperty("status").getValue().toString();
									}
									
								}
								
								
								return super.formatPropertyValue(rowId, colId, property);			
							}
						};
						
						
						tbBoletosAberto.setWidth("800px");
						tbBoletosAberto.setHeight("300px");
						tbBoletosAberto.setVisibleColumns(new Object[]{"n_doc","data_vencimento","valor_titulo","status"});
						tbBoletosAberto.setColumnHeader("n_doc", "N. Documento");
						tbBoletosAberto.setColumnHeader("data_vencimento", "Data Vencimento");
						tbBoletosAberto.setColumnHeader("valor_titulo", "Valor Título");
						tbBoletosAberto.setColumnHeader("status", "Status");
						
						tbBoletosAberto.setCellStyleGenerator(new Table.CellStyleGenerator() {
							
							@Override
							public String getStyle(final Table source, final Object itemId, Object propertyId) {

								Date dataVencimento = (Date) source.getItem(itemId).getItemProperty("data_vencimento").getValue();
								
								DateTime dt1 = new DateTime(dataVencimento);
								DateTime dt2 = new DateTime(new Date());
								
								if(dt1.isBefore(dt2)){
									
									return "row-header-indicacao-em-analise";   
								}
								
								return null;
							}
						});
						
						addComponent(tbBoletosAberto); 
						
						TextField txtMediaAtrasoContrato = new TextField("Média de atraso todos os contratos");
						
						Integer mediaAtraso = calcularMediaAtraso();
						txtMediaAtrasoContrato.setValue(mediaAtraso > 0 ? mediaAtraso.toString()+" DIAS" : mediaAtraso.toString());
						txtMediaAtrasoContrato.setReadOnly(true);
						
						if(mediaAtraso > 0){
							txtMediaAtrasoContrato.addStyleName("invalid-cpf");	
						}
						
						addComponent(txtMediaAtrasoContrato); 
						
					}					
				});
			}
		};
		
		
		
		
		return vlRoot;	
		
	}
	
	private Integer calcularMediaAtraso(){
		
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createQuery("select c from ContasReceber c where c.cliente.doc_cpf_cnpj=:c  ", ContasReceber.class);
		q.setParameter("c", contrato.getCliente().getDoc_cpf_cnpj());
				
		List<ContasReceber> boletos = q.getResultList();
				
		Integer somaAtrasos = 0;
		Integer qtd_boletos = 0;
		for (ContasReceber boleto: boletos) {
			if(boleto.getStatus().equals("FECHADO")){			
				Integer qtdDiasAtrasado = Days.daysBetween(new DateTime(boleto.getData_vencimento()),new DateTime(boleto.getData_pagamento())).getDays();
				somaAtrasos = somaAtrasos+qtdDiasAtrasado;
				qtd_boletos = qtd_boletos+1;
			}
		}
		
		Integer media_atraso = 0;
		
		if(somaAtrasos > 0 && qtd_boletos > 0){
			media_atraso =	somaAtrasos / qtd_boletos;
		}
	
		if(media_atraso < 0){
			media_atraso = 0;
		}
		
		return media_atraso;
		
	}
	
	


	

	
	
	
	
	
	
}

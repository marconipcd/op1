package com.digital.opuserp.view.modulos.acesso.contratos_acesso;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Days;

import com.digital.opuserp.dao.ContasReceberDAO;
import com.digital.opuserp.dao.PlanoAcessoDAO;
import com.digital.opuserp.domain.AlteracoesContrato;
import com.digital.opuserp.domain.ContasReceber;
import com.digital.opuserp.domain.PlanoAcesso;
import com.digital.opuserp.util.DataUtil;
import com.digital.opuserp.util.Real;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class HistoricoPagamentos extends Window {

	JPAContainer<AlteracoesContrato> container;
	
	TextField tfBusca;
	Table tbBoletos;
	Button btSalvar;
	Button btCancelar;
	
	Integer codContrato;
	
	Label lbVlrPagos = new Label("Pagos: 0,00");
	Label lbVlraPagar = new Label("A Pagar: 0,00");
	Label lbVlrVencidos = new Label("Vencidos: 0,00");
	//Label lbVlrMedioAtraso = new Label("Media Atraso: 0"); 
	
	public HistoricoPagamentos(boolean modal, boolean center, Integer codContrato){
		
		super("Histórico de Pagamento");
		
		this.codContrato = codContrato;
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
				
				addComponent(buildTbBoletos());
				setExpandRatio(tbBoletos, 2);
				
				HorizontalLayout hlResumo = new HorizontalLayout();
				hlResumo.addComponent(lbVlrPagos);
				hlResumo.addComponent(lbVlraPagar);
				hlResumo.addComponent(lbVlrVencidos);
			    //hlResumo.addComponent(lbVlrMedioAtraso);
				
				
				HorizontalLayout hlButtons = new HorizontalLayout();
				hlButtons.setSpacing(true);
				hlButtons.setMargin(true);
				hlButtons.setStyleName("hl_buttons_bottom");
				 
				hlButtons.addComponent(buildBtCancelar());
				btCancelar.focus();
				//hlButtons.addComponent(buildBtSalvar());
				
				HorizontalLayout Hlrodape = new HorizontalLayout();
				Hlrodape.setWidth("100%");
				
				Hlrodape.addComponent(hlResumo);
				Hlrodape.addComponent(hlButtons);
				
				Hlrodape.setComponentAlignment(hlResumo, Alignment.BOTTOM_LEFT);
				Hlrodape.setComponentAlignment(hlButtons, Alignment.BOTTOM_RIGHT);
				
				addComponent(Hlrodape);
				
			}

		});
	}
	
//	public JPAContainer<AlterarcoesContrato> buildJpaContainer(){
//		container = JPAContainerFactory.make(AlterarcoesContrato.class, ConnUtil.getEntityManager());
//		container.addContainerFilter(Filters.eq("contrato", new AcessoCliente(codContrato)));
//		container.addNestedContainerProperty("operador.username");
//		
//		container.sort(new String []{"id"}, new boolean[] {false});
//		return container;
//	}
	
	public Table buildTbBoletos(){
		tbBoletos = new Table();
		tbBoletos.setSizeFull();
		tbBoletos.setSelectable(true);
		
		tbBoletos.addContainerProperty("Cod", BigInteger.class, null);
		tbBoletos.addContainerProperty("Cod Cartao", String.class, "");
		tbBoletos.addContainerProperty("Ndoc", String.class, "");
		tbBoletos.addContainerProperty("Valor", String.class, "");
		tbBoletos.addContainerProperty("Vencimento", String.class, "");
		tbBoletos.addContainerProperty("Pagamento", String.class, "");
		tbBoletos.addContainerProperty("Valor Pgto.", String.class, "");
		tbBoletos.addContainerProperty("Forma Pgto.", String.class, "");
		tbBoletos.addContainerProperty("Status", String.class, "");
		tbBoletos.addContainerProperty("Plano", String.class, "");
		
		tbBoletos.setColumnAlignment("Valor", Align.RIGHT);
		tbBoletos.setColumnAlignment("Valor Pgto.", Align.RIGHT);
		
		tbBoletos.setColumnCollapsingAllowed(true); 
		tbBoletos.setColumnCollapsed("Status", true);
		
		tbBoletos.setCellStyleGenerator(new Table.CellStyleGenerator() {
			
			@Override
			public String getStyle(Table source, Object itemId, Object propertyId) {
				
				if(source.getItem(itemId).getItemProperty("Status").getValue().equals("FECHADO")){
					return "row-header-encerrado";
				}
				
				if(source.getItem(itemId).getItemProperty("Status").getValue().equals("ABERTO")){
					
					Date dataVencimento = DataUtil.stringToDate(source.getItem(itemId).getItemProperty("Vencimento").getValue().toString());
					Date dataHoje = new Date();
					
					if(dataVencimento.before(dataHoje)){
						return "row-header-bloqueado";
					}else{
						return "row-header-default";
					}
				}
				
				
				return "row-header-default";
			}
		});
		
		List<ContasReceber> boletos = ContasReceberDAO.procurarTodosBoletosDoAcessoPorContrato(codContrato);
		
		float vlBoletoAberto = 0;
		float vlBoletoPago = 0;
		float vlBoletoVencido = 0;
		
		double mediaDiasAT = 0;
		Integer dias1 = 0;
		Integer dias2 = 0;
		Integer dias = 0;
		Integer diasEmAtrazo   = 0;
		Integer boletosAbertos = 0;
		Integer boletosFechado = 0;
		Integer boletosVencido = 0;
		
		if(boletos != null && boletos.size() > 0){
			Integer i = 0;
			for (ContasReceber contasReceber : boletos) {
				PlanoAcesso plano = null;
				if(contasReceber.getPlano_contrato() != null){
					plano = PlanoAcessoDAO.find(contasReceber.getPlano_contrato());
				}
				String nomePlano = plano != null && plano.getNome() != null ? plano.getNome() : "SEM PLANO";
				
				tbBoletos.addItem(new Object[]{new BigInteger(String.valueOf(contasReceber.getId())), contasReceber.getCodigo_cartao(),contasReceber.getN_doc(),"R$ "+contasReceber.getValor_titulo(), DataUtil.formatDateBra(contasReceber.getData_vencimento()),DataUtil.formatDateBra(contasReceber.getData_pagamento()),contasReceber.getValor_pagamento() != null ? "R$ "+contasReceber.getValor_pagamento() : "", contasReceber.getForma_pgto(), contasReceber.getStatus(),nomePlano}, i);
				i++;
				
				
				//Contabiliza Resumos				
				if(contasReceber.getStatus().equals("ABERTO")){					 
					 vlBoletoAberto = vlBoletoAberto+Float.parseFloat(Real.formatStringToDB(contasReceber.getValor_titulo()));
					 boletosAbertos++;
				}
				if(contasReceber.getStatus().equals("FECHADO")&& contasReceber.getValor_pagamento()!=null){
					 vlBoletoPago = vlBoletoPago+Float.parseFloat(Real.formatStringToDB(contasReceber.getValor_pagamento()));		
					 boletosFechado++;
				}
				 DateTime dt1 = new DateTime(contasReceber.getData_vencimento());
				 DateTime dt2 = new DateTime(contasReceber.getData_pagamento());
				 
				 if(new DateTime(new Date()).compareTo(dt1) > 0 && contasReceber.getStatus().equals("ABERTO")){
					
					 vlBoletoVencido = vlBoletoVencido+Float.parseFloat(Real.formatStringToDB(contasReceber.getValor_titulo()));	
					 boletosVencido++;
				 }
				 
				 dias = Days.daysBetween(dt1, dt2).getDays();
				 if(dias > 0 ){	
					 dias1 = dias;
					 dias2 = dias2 + dias1;
					 diasEmAtrazo ++;				 
				 }else{
					 dias1 = 0;
				 }
				 
				 double d = 0;
				 double m = 0;
				 
					
				 if(diasEmAtrazo == 0){
					 mediaDiasAT = (float) 0;
				 }else{
				    d =	Double.parseDouble(String.valueOf(dias));
					m =	Double.parseDouble(String.valueOf(boletosFechado));
						
					mediaDiasAT = (d/m);
				 }
				//Contabiliza Resumos		

			}
			
			java.text.DecimalFormat df = new java.text.DecimalFormat("###,##0.00");
			lbVlrPagos = new Label("<strong>Pagos:</strong> "+Real.formatDbToString(String.valueOf(vlBoletoPago)),ContentMode.HTML);
			lbVlraPagar = new Label(" | <strong>A Pagar:</strong> "+Real.formatDbToString(String.valueOf(vlBoletoAberto)), ContentMode.HTML);
			lbVlrVencidos = new Label(" | <strong>Vencidos:</strong> "+Real.formatDbToString(String.valueOf(vlBoletoVencido)), ContentMode.HTML);
			//lbVlrMedioAtraso = new Label(" | <strong>Média Atraso:</strong> "+df.format(mediaDiasAT), ContentMode.HTML);
			
			
			tbBoletos.sort(new Object[]{"Cod"}, new boolean[]{true});
		}
		
		return tbBoletos;
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

package com.digital.opuserp.view.modulos.estoque.produto.historico;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.EcfPreVendaCabecalho;
import com.digital.opuserp.domain.EcfPreVendaDetalhe;
import com.digital.opuserp.domain.Produto;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.Real;
import com.vaadin.data.Property;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class HistoricoSaidas extends Window {
	
	Table tbCompras;
	Button btFechar;
	private Label lbRegistros;
	Integer codProduto;
	Integer qtdRegistros = 0;
	Float qtdUnidade = new Float(0);
	HorizontalLayout hlFloat;
	HorizontalLayout hlRoot;
	
	public HistoricoSaidas(boolean modal, boolean center, Integer codProduto){
		
		super("Histórico de Saídas");
		
		this.codProduto = codProduto;
		setWidth("1065px");
		setHeight("515px");
		
		setModal(modal);
		if(center){
			center();
		}
		
		setClosable(false);
		setResizable(false);			
				
		setContent(new VerticalLayout(){
			{
				setSizeFull();
				setMargin(true);

				addComponent(buildtbComprasGeneric());
				//addComponent();	
				
				setExpandRatio(tbCompras, 2);

				HorizontalLayout hlButtons = new HorizontalLayout();
				hlButtons.setSpacing(true);
				hlButtons.setMargin(true);
				hlButtons.setWidth("100%");
				hlButtons.setStyleName("hl_buttons_bottom");
				hlButtons.addComponent(BuildLbRegistros());
				hlButtons.addComponent(buildBtCancelar());
															
				addComponent(hlButtons);
				hlButtons.setComponentAlignment(lbRegistros, Alignment.BOTTOM_LEFT);
				hlButtons.setComponentAlignment(btFechar, Alignment.BOTTOM_RIGHT);
				
			}

		});
	}
	
	public Label BuildLbRegistros(){
		Double valorDouble = Double.parseDouble(String.valueOf(qtdUnidade));															  
		DecimalFormat df = new DecimalFormat();  
		df.applyPattern("#,##0");  					
		String valorFormatado = df.format(valorDouble);
		
		lbRegistros = new Label(qtdRegistros+" Registros Encontrados | Total de "+valorFormatado+" Itens Encontrados.");
		return lbRegistros;
	}
	
	public Table buildtbComprasGeneric() {
		
		tbCompras = new Table(){
			@Override
			protected String formatPropertyValue(Object rowId, Object colId,
					Property<?> property) {
								
				
				if(tbCompras.getType(colId).equals(Date.class)){
					
					if(tbCompras.getItem(rowId).getItemProperty(colId).getValue() != null){
						SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
						
						String data = tbCompras.getItem(rowId).getItemProperty(colId).getValue().toString();
						return sdf.format(tbCompras.getItem(rowId).getItemProperty(colId).getValue());
					}else{
						return null;
					}
					
					
				}else{
					if(tbCompras.getItem(rowId).getItemProperty(colId).getValue() != null && !tbCompras.getItem(rowId).getItemProperty(colId).getValue().equals("")){
						return super.formatPropertyValue(rowId, colId, property);
					}else{
						return null;						
					}
				}	
			}
		};
		
		tbCompras.setSelectable(true);
		tbCompras.setWidth("100%");
		tbCompras.setHeight("411px");
		tbCompras.addContainerProperty("Natureza Operação", String.class, "");
		tbCompras.addContainerProperty("Status", String.class, "");
		tbCompras.addContainerProperty("Pedido/OS", String.class, "");
		tbCompras.addContainerProperty("Cliente", String.class, "");
		tbCompras.addContainerProperty("Valor Venda", String.class, "");
		tbCompras.addContainerProperty("Quantidade", Float.class, "");
		tbCompras.addContainerProperty("Unidade", String.class, "");
		tbCompras.addContainerProperty("Data Venda", Date.class, null);

		tbCompras.setColumnWidth("Natureza Operação", 100);
		tbCompras.setColumnWidth("Pedido/OS", 100);
		tbCompras.setColumnWidth("Cliente", 380);
		tbCompras.setColumnWidth("Valor Venda", 110);
		tbCompras.setColumnWidth("Quantidade", 80);
		tbCompras.setColumnWidth("Unidade", 60);
		tbCompras.setColumnWidth("Data Venda", 110);
		
		tbCompras.setColumnAlignment("Valor Venda", Align.RIGHT);	
		tbCompras.setColumnAlignment("Quantidade", Align.CENTER);	
	
		tbCompras.setVisibleColumns(new Object[]{"Natureza Operação","Status","Pedido/OS","Cliente","Valor Venda","Quantidade","Unidade","Data Venda"});
		
		
		tbCompras.sort(new Object[]{"Data Venda"}, new boolean[]{false});
		
		tbCompras.setImmediate(true);

		try {

			List<EcfPreVendaCabecalho> ecfCab = null;

			EntityManager em = ConnUtil.getEntity();
			Query q = em.createQuery("select c from EcfPreVendaDetalhe c where c.produtoId =:CodProduto order by c.id desc",EcfPreVendaDetalhe.class);
			q.setParameter("CodProduto", codProduto);
			List<EcfPreVendaDetalhe> ecfDetalhe = null;
			
			if(q.getResultList().size() > 0){
				ecfDetalhe = q.getResultList();
			}
			
			Integer i = 0;
			String RowAnterior = "";
			Integer RowAnteriorId = 0;
			Integer parentId = null;
				
			if(ecfDetalhe!=null){
				for(EcfPreVendaDetalhe ecfDet: ecfDetalhe){

					Query cab = em.createQuery("select c from EcfPreVendaCabecalho c where c.id =:id and c.empresa_id =:empresa_id and  c.tipo !='ORCAMENTO' and c.naturezaOperacao.descricao != 'DEVOLUCAO EM GARANTIA' order by c.id desc",EcfPreVendaCabecalho.class);
					cab.setParameter("id", ecfDet.getEcfPreVendaCabecalhoId());					
					cab.setParameter("empresa_id", OpusERP4UI.getEmpresa().getId());					
					
					if(cab.getResultList().size() > 0){
						ecfCab = cab.getResultList();
					}

					if(ecfCab!=null){
						for(EcfPreVendaCabecalho movCab: ecfCab){
							
							String id = "--";
							String cliente ="CORRECAO DE ESTOQUE";
							String valor ="--";
						
							if(movCab.getTipo().equals("PEDIDO") && ecfDet != null && ecfDet.getValorTotal() != null){					
								id = String.valueOf(movCab.getId());
								cliente = movCab.getCliente() != null ? movCab.getCliente().getNome_razao() : "--";
								valor = "R$ "+ecfDet.getValorTotal() != null ? Real.formatDbToString(String.valueOf(ecfDet.getValorTotal())) : "0,00";
							}
							
							if(movCab.getTipo().equals("PEDIDO OS") && ecfDet != null){
								id = movCab.getN_os() != null ? movCab.getN_os().toString() : "--";
								cliente = movCab.getCliente() != null ? movCab.getCliente().getNome_razao() : "--";
							}
							
							
							if(movCab.getTipo().equals("RMA")){
								cliente ="RMA";
							}
							
							if(movCab.getTipo().equals("MIGRACAO")){
								id = String.valueOf(movCab.getId());
								cliente = movCab.getCliente() != null ? movCab.getCliente().getNome_razao() : "--";
							}
								
							if(RowAnterior != null && movCab.getId() != null && !movCab.getId().equals(RowAnteriorId)){
								RowAnteriorId = movCab.getId();
								
								Produto prod = em.find(Produto.class, codProduto);

								tbCompras.addItem(new Object[]{movCab.getNaturezaOperacao() != null ? movCab.getNaturezaOperacao().getDescricao() : "", movCab.getSituacao(),id,cliente,valor,ecfDet.getQuantidade(),
								prod.getUnidadeProduto().getNome(),movCab.getData()}, i);
								i++;
								qtdRegistros++;
								qtdUnidade = qtdUnidade + ecfDet.getQuantidade();
							}
						}
					}
				}
			  }
		} catch (Exception e) {
			e.printStackTrace();
		}

		return tbCompras;
	}
	
		
	private Button buildBtCancelar() {
		btFechar = new Button("Fechar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				close();
			}	
		});
		
		ShortcutListener cltbCompras = new ShortcutListener("btFechar", ShortcutAction.KeyCode.ESCAPE, null) {
			
			@Override
			public void handleAction(Object sender, Object target) {
				btFechar.click();
			}
		};
		btFechar.focus();
		btFechar.addShortcutListener(cltbCompras);
		return btFechar;
	}

}




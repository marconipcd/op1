package com.digital.opuserp.view.modulos.estoque.produto.historico;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.AlteracoesProdutoDAO;
import com.digital.opuserp.domain.AlteracoesProduto;
import com.digital.opuserp.domain.MovimentoEntCabecalho;
import com.digital.opuserp.domain.MovimentoEntDetalhe;
import com.digital.opuserp.domain.Produto;
import com.digital.opuserp.domain.UnidadeProduto;
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

public class HistoricoEntradas extends Window {
	
	Table tbCompras;
	Button btFechar;
	private Label lbRegistros;
	Integer codProduto;
	Integer qtdRegistros = 0;
	Float qtdUnidade = new Float(0);
	HorizontalLayout hlFloat;
	HorizontalLayout hlRoot;
	
	public HistoricoEntradas(boolean modal, boolean center, Integer codProduto){
		
		super("Histórico de Entradas");
		
		this.codProduto = codProduto;
		setWidth("960px");
		setHeight("515px");
		
		
		setModal(modal);
		if(center){
			center();
		}
		
		setClosable(false);
		setResizable(true);				
				
		setContent(new VerticalLayout(){
			{
				setSizeFull();
				setMargin(true);

				addComponent(buildtbComprasGeneric());
				addComponent(BuildLbRegistros());	
				
				setExpandRatio(tbCompras, 2);

				HorizontalLayout hlButtons = new HorizontalLayout();
				hlButtons.setSpacing(true);
				hlButtons.setMargin(true);
				hlButtons.setStyleName("hl_buttons_bottom");
				hlButtons.addComponent(buildBtCancelar());
				
				hlRoot = new HorizontalLayout();
				hlRoot.addComponent(hlButtons);											
				addComponent(hlRoot);
				setComponentAlignment(hlRoot, Alignment.BOTTOM_RIGHT);
				
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
		tbCompras.setSizeFull();
		tbCompras.addContainerProperty("NF",String.class, "");
		tbCompras.addContainerProperty("Fornecedor", String.class, "");
		tbCompras.addContainerProperty("Valor Custo", String.class, "");
		tbCompras.addContainerProperty("Qtd. Anterior", String.class, "");
		tbCompras.addContainerProperty("Quantidade", Float.class, "");
		tbCompras.addContainerProperty("Unidade", String.class, "");
		tbCompras.addContainerProperty("Data Compra", Date.class, null);
		tbCompras.addContainerProperty("Data Validade", Date.class, null);

		tbCompras.setColumnWidth("NF", 100);
		tbCompras.setColumnWidth("Fornecedor", 380);
		tbCompras.setColumnExpandRatio("Fornecedor", 1f);
		tbCompras.setColumnWidth("Valor Custo", 110);
		tbCompras.setColumnWidth("Qtd. Anterior", 100);
		tbCompras.setColumnWidth("Quantidade", 80);
		tbCompras.setColumnWidth("Unidade", 60);
		tbCompras.setColumnWidth("Data Compra", 110);
		tbCompras.setColumnWidth("Data Validade", 110);
		
		tbCompras.setColumnAlignment("Valor Custo", Align.RIGHT);	
		tbCompras.setColumnAlignment("Quantidade", Align.CENTER);
	
		tbCompras.setVisibleColumns(new Object[]{"NF","Fornecedor","Valor Custo","Qtd. Anterior","Quantidade","Unidade","Data Compra","Data Validade"});
		
		
		tbCompras.sort(new Object[]{"Data Compra"}, new boolean[]{false});
		
		tbCompras.setImmediate(true);

		try {
			
			List<MovimentoEntCabecalho> movimentoCab = null;

			EntityManager em = ConnUtil.getEntity();
			Query q = em.createQuery("select c from MovimentoEntDetalhe c where c.produtoId =:CodProduto order by c.id desc",MovimentoEntDetalhe.class);
			q.setParameter("CodProduto", codProduto);
			List<MovimentoEntDetalhe> movimentoDetalhe = null;
			
			if(q.getResultList().size() > 0){
				movimentoDetalhe = q.getResultList();
			}
			
			Integer j = 0;
			Integer i = 0;
			String RowAnterior = "";
			Integer RowAnteriorId = 0;
			Integer parentId = null;
				
			List<AlteracoesProduto> altProd = null;
			altProd = AlteracoesProdutoDAO.findalteracao(codProduto);
			
			if(movimentoDetalhe != null){
				for(MovimentoEntDetalhe movDet: movimentoDetalhe){

					Query cab = em.createQuery("select c from MovimentoEntCabecalho c where c.id =:id and c.empresa_id =:empresa_id and c.situacao='F'order by c.id desc",MovimentoEntCabecalho.class);
					cab.setParameter("id", movDet.getMovimentoEntCabecalhoId()); 
					cab.setParameter("empresa_id", OpusERP4UI.getEmpresa().getId());					
					
					if(cab.getResultList().size() > 0){
						movimentoCab = cab.getResultList();
					}
					
					Produto prod = em.find(Produto.class, codProduto);

					if(movimentoCab!=null){
						for(MovimentoEntCabecalho movCab: movimentoCab){
							
							String Nf = "--";
							String fornecedor ="CORRECAO DE ESTOQUE";
							String valor ="--";

							if(movCab.getTipo() != null && !movCab.getTipo().equals("CORRECAO")){	
							
								if(!movCab.getTipo().equals("DEVOLUCAO") && !movCab.getTipo().equals("CANCELAMENTO") && 
										!movCab.getTipo().equals("MIGRACAO")){
									
									Nf = String.valueOf(movCab.getCodNf());
									fornecedor = movCab.getFornecedor().getRazao_social();
									
									if(movDet.getValorCusto() != null){
										valor = "R$ "+Real.formatDbToString(String.valueOf(movDet.getValorCusto()));
									}
								}else{
									
									if(movCab.getTipo().equals("DEVOLUCAO")){
										Nf = "--";
										fornecedor ="DEVOLUCAO";
										valor ="--";
									}
									
									if(movCab.getTipo().equals("CANCELAMENTO")){
										Nf = "--";
										fornecedor ="CANCELAMENTO";
										valor ="--";
									}
									
									if(movCab.getTipo().equals("MIGRACAO")){
										Nf = "--";
										fornecedor = movCab.getFornecedor().getRazao_social();
										valor ="--";
									}
								}
								
							}
							
														
							if(RowAnterior != null && movCab.getId() != null && !movCab.getId().equals(RowAnteriorId)){
								RowAnteriorId = movCab.getId();

								tbCompras.addItem(new Object[]{Nf,fornecedor,valor,movDet.getQuantidade_anterior() != null ? movDet.getQuantidade_anterior().toString() : "--",movDet.getQuantidade(),
								prod.getUnidadeProduto().getNome(),movCab.getDataHora(), movCab.getData_validade()}, i);
								i++;
								qtdRegistros++;
								qtdUnidade = qtdUnidade + movDet.getQuantidade();

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



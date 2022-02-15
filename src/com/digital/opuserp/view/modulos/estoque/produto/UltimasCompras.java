package com.digital.opuserp.view.modulos.estoque.produto;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

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

public class UltimasCompras extends Window {
	
	Table tbCompras;
	Button btFechar;
	private Label lbRegistros;
	Integer codProduto;
	Integer qtdRegistros = 0;
	Float qtdUnidade = new Float(0);
	HorizontalLayout hlFloat;
	HorizontalLayout hlRoot;
	
	public UltimasCompras(boolean modal, boolean center, Integer codProduto){
		
		super("Últimas Compras");
		
		this.codProduto = codProduto;
		setWidth("960px");
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
		tbCompras.setWidth("100%");
		tbCompras.setHeight("360px");
		tbCompras.addContainerProperty("NF", Integer.class, "");
		tbCompras.addContainerProperty("Fornecedor", String.class, "");
		tbCompras.addContainerProperty("Valor Custo", String.class, "");
		tbCompras.addContainerProperty("Quantidade", Float.class, "");
		tbCompras.addContainerProperty("Unidade", String.class, "");
		tbCompras.addContainerProperty("Data Compra", Date.class, null);

		tbCompras.setColumnWidth("NF", 100);
		tbCompras.setColumnWidth("Fornecedor", 380);
		tbCompras.setColumnWidth("Valor Custo", 110);
		tbCompras.setColumnWidth("Quantidade", 80);
		tbCompras.setColumnWidth("Unidade", 60);
		tbCompras.setColumnWidth("Data Compra", 110);
		
		tbCompras.setColumnAlignment("Valor Custo", Align.RIGHT);	
		tbCompras.setColumnAlignment("Quantidade", Align.CENTER);
	
		tbCompras.setVisibleColumns(new Object[]{"NF","Fornecedor","Valor Custo","Quantidade","Unidade","Data Compra"});
		
		
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
				
			List<AlteracoesProduto> altProd = null;
			altProd = AlteracoesProdutoDAO.findalteracao(codProduto);
			
			if(movimentoDetalhe != null){
				for(MovimentoEntDetalhe movDet: movimentoDetalhe){

					Query cab = em.createQuery("select c from MovimentoEntCabecalho c where c.id =:id and c.situacao='F'order by c.id desc",MovimentoEntCabecalho.class);
					cab.setParameter("id", movDet.getMovimentoEntCabecalhoId());
					
					
					
					if(cab.getResultList().size() > 0){
						movimentoCab = cab.getResultList();
					}
					
					Produto prod = em.find(Produto.class, codProduto);

					if(movimentoCab!=null){
						for(MovimentoEntCabecalho movCab: movimentoCab){
						
								if(RowAnterior != null && movCab.getId() != null && !movCab.getId().equals(RowAnteriorId)){
									RowAnteriorId = movCab.getId();
	
									if(movCab.getTipo()== null || !movCab.getTipo().equals("CORRECAO")){	
									
									tbCompras.addItem(new Object[]{movCab.getCodNf(),movCab.getFornecedor().getRazao_social(),
									"R$ "+Real.formatDbToString(String.valueOf(movDet.getValorCusto())),movDet.getQuantidade(),
									prod.getUnidadeProduto().getNome(),movCab.getDataHora()}, i);
									i++;
									qtdRegistros++;
									qtdUnidade = qtdUnidade + movDet.getQuantidade();
								}
								
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



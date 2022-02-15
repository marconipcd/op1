package com.digital.opuserp.view.modulos.financeiro.contasReceber;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.Query;

import org.cups4j.CupsPrinter;
import org.cups4j.PrintJob;
import org.cups4j.PrintRequestResult;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.Months;
import org.json.JSONArray;
import org.json.JSONObject;

import br.com.caelum.stella.boleto.Datas;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.AlertaContasTrocaDAO;
import com.digital.opuserp.dao.AlteracoesContasReceberDAO;
import com.digital.opuserp.dao.ClienteDAO;
import com.digital.opuserp.dao.ContasReceberDAO;
import com.digital.opuserp.dao.ContratosAcessoDAO;
import com.digital.opuserp.dao.CredenciaisAcessoDAO;
import com.digital.opuserp.dao.CreditoClienteDAO;
import com.digital.opuserp.dao.GerarNNDAO;
import com.digital.opuserp.dao.GerenciarModuloDAO;
import com.digital.opuserp.dao.HaverDAO;
import com.digital.opuserp.dao.LogDAO;
import com.digital.opuserp.dao.OseDAO;
import com.digital.opuserp.dao.OsiDAO;
import com.digital.opuserp.dao.PlanoAcessoDAO;
import com.digital.opuserp.domain.AcessoCliente;
import com.digital.opuserp.domain.AlteracoesContasReceber;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.ContaBancaria;
import com.digital.opuserp.domain.ContasReceber;
import com.digital.opuserp.domain.ControleTitulo;
import com.digital.opuserp.domain.Endereco;
import com.digital.opuserp.domain.HaverCab;
import com.digital.opuserp.domain.HaverDetalhe;
import com.digital.opuserp.domain.LogAcoes;
import com.digital.opuserp.domain.Ose;
import com.digital.opuserp.domain.Osi;
import com.digital.opuserp.domain.ParametrosBoleto;
import com.digital.opuserp.domain.PlanoAcesso;
import com.digital.opuserp.util.CheckNdocUtil;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.util.Real;
import com.digital.opuserp.util.ScpUtil;
import com.digital.opuserp.util.boletos.boleto.Banco;
import com.digital.opuserp.util.boletos.boleto.Boleto;
import com.digital.opuserp.util.boletos.boleto.Emissor;
import com.digital.opuserp.util.boletos.boleto.Sacado;
import com.digital.opuserp.util.boletos.boleto.bancos.BancoDoBrasil;
import com.digital.opuserp.util.boletos.boleto.bancos.Sicredi;
import com.digital.opuserp.view.modulos.financeiro.contasReceber.BaixarComHaverEditor.BaixarTituloHaverEvent;
import com.digital.opuserp.view.modulos.financeiro.contasReceber.BaixarTituloEditor.BaixarTituloEvent;
import com.digital.opuserp.view.modulos.financeiro.contasReceber.ConfirBaixaArqRet.BaixaEvent;
import com.digital.opuserp.view.modulos.financeiro.contasReceber.EditarTituloEditor.EditarTituloEvent;
import com.digital.opuserp.view.modulos.financeiro.contasReceber.NegativarTituloEditor.NegativarTituloEvent;
import com.digital.opuserp.view.modulos.financeiro.contasReceber.NovoTituloEditor.NovoTituloEvent;
import com.digital.opuserp.view.modulos.financeiro.contasReceber.OcorrenciaContasReceberEditor.OcorreciaContasReceberEvent;
import com.digital.opuserp.view.modulos.financeiro.contasReceber.UploadLiquidaEditor.UploadLiquidadoEvent;
import com.digital.opuserp.view.util.Notify;
import com.github.wolfie.refresher.Refresher;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItem;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutAction.ModifierKey;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.MultiSelectMode;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

public class ContasReceberView extends VerticalLayout {

	//JPAContainer<Child> container;
	
	TreeTable tb;
	TextField tfBusca;
	ComboBox cbStatus;
	ComboBox cbControle;
	Button btNovo;
	Button btEditar;
	Button btExcluir;
	Button btNegativar;
	Button btBaixar;
	Button btBaixarComHaver;
	Button btReabrir;
	Button btImprimir;
	Button btImprimirRecibo;
	Button btArqRetorno;
	Button btOcorrencia;
	
	private Button btLog;
	
	Integer qtdRegistros = 0;
	
	String valorTitulo;
	
	boolean liquid;

	ShortcutListener slNovo;
	ShortcutListener slEditar;
	ShortcutListener slBaixar;
	
	HorizontalLayout hlFloat;
	private Label lbRegistros;
	
	GerenciarModuloDAO gmDAO;
	private Integer codSubModulo;
	
	boolean excluirDefinitivamente = false;
	
	Refresher refresher;
	
	
	
	public ContasReceberView(boolean act){
		
		if(act){
			addExtension(buildRefresher());	
			setSizeFull();
			
			gmDAO = new GerenciarModuloDAO();
			
			HorizontalLayout hlButons = new HorizontalLayout();
			hlButons.addComponent(BuildbtNovo());
			//hlButons.addComponent(BuildbtGerarNossoNumero());
			//hlButons.addComponent(BuildbtReemitir());
			hlButons.addComponent(BuildbtEditar());
			hlButons.addComponent(BuildbtExcluir());
			hlButons.addComponent(BuildbtNegativar());
			hlButons.addComponent(BuildbtBaixar());
			hlButons.addComponent(BuildbtBaixarComHaver());
			hlButons.addComponent(BuildbtReabrir());
			hlButons.addComponent(BuildbtImprimirRecibo());
			hlButons.addComponent(BuildbtOcorrencia());
			hlButons.addComponent(BuildbtImprimir());
			hlButons.addComponent(BuildbtArquivos());
			hlButons.addComponent(BuildbtGerenciaNet());
			hlButons.addComponent(BuildbtLog());
			
			//hlButons.addComponent();
			
			addComponent(hlButons);
			setComponentAlignment(hlButons, Alignment.TOP_RIGHT);
			
			addComponent(new HorizontalLayout(){
				{
					setWidth("100%");
					addComponent(buildComboStatus());
					addComponent(buildComboControle());
					addComponent(buildTfbusca());
					setExpandRatio(tfBusca, 1.0f);
				}
			});
			
			addComponent(buildTbGeneric());
			refresh(tfBusca.getValue());
			
			
			hlFloat = new HorizontalLayout();
			hlFloat.setWidth("100%");
			hlFloat.addComponent(BuildLbRegistros(qtdRegistros, "0,00","0,00"));
			Label lbLegend = new Label("F2 - Novo Cadastro | ENTER - Selecionar | Ctrl+B - Baixar");
			lbLegend.setWidth("395px");
			hlFloat.addComponent(lbLegend);
			hlFloat.setComponentAlignment(lbRegistros, Alignment.BOTTOM_LEFT);
			hlFloat.setComponentAlignment(lbLegend, Alignment.BOTTOM_RIGHT);
			
			
			addComponent(hlFloat);
			
			
			setExpandRatio(tb, 1);
			
			checarBaixas();
		}
	}
	
	private Button BuildbtOcorrencia() {
		btOcorrencia= new Button("Ocorrência", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				try{
					final EntityManager em = ConnUtil.getEntity();					
					final Set<Object> selecteds = (Set<Object>)tb.getValue();				
							
					if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), 
							OpusERP4UI.getUsuarioLogadoUI().getId(), "Ocorrencia"))				
					{
						try{
							if(tb.getItem(selecteds.toArray()[0]).getItemProperty("Cod.").getValue() != null){
								
								Integer codigo_contas = Integer.parseInt(tb.getItem(selecteds.toArray()[0]).getItemProperty("Cod.").getValue().toString());
								ContasReceber contas = em.find(ContasReceber.class, codigo_contas);
								
								final OcorrenciaContasReceberEditor o_contas = new OcorrenciaContasReceberEditor("Ocorrências", true, contas);
								o_contas.addListerner(new OcorrenciaContasReceberEditor.OcorreciaContasReceberListerner() {
									
									@Override
									public void onClose(OcorreciaContasReceberEvent event) {
										o_contas.close();
									}
								});
								
								getUI().addWindow(o_contas); 
							}
							
						}catch(Exception e){
							e.printStackTrace();
						}
					}else{
						Notification.show("Atenção", "Você não Informar ocorrências em um Boleto", Type.ERROR_MESSAGE);
					}
				
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		});
		btOcorrencia.setEnabled(false);
		return btOcorrencia;
	}

	public Refresher buildRefresher(){
		
		
		refresher = new Refresher();
		refresher.setRefreshInterval(30000);
		refresher.addListener(new Refresher.RefreshListener() {
			
			@Override
			public void refresh(Refresher source) {
								
				
				checarBaixas();
				 
			}
		});
		
		return refresher;
	}
	
	public Label BuildLbRegistros(Integer qtd, String valorReceber, String valorPago){
		lbRegistros = new Label(qtd.toString() + " Registros Encontrados | "+" À Receber: "+valorReceber+" | Pagos: "+valorPago);
		return lbRegistros;
	}
	
	
	public Table buildTbGeneric() {
				
		tb = new TreeTable(){
			@Override
			protected String formatPropertyValue(Object rowId, Object colId,
					Property<?> property) {
								
				
				if(tb.getType(colId).equals(Date.class)){
					
					if(tb.getItem(rowId).getItemProperty(colId).getValue() != null){
						SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
						
						String data = tb.getItem(rowId).getItemProperty(colId).getValue().toString();
						return sdf.format(tb.getItem(rowId).getItemProperty(colId).getValue());
					}else{
						return null;
					}
					
				}else{
					if(tb.getItem(rowId).getItemProperty(colId).getValue() != null && !tb.getItem(rowId).getItemProperty(colId).getValue().equals("")){
						return super.formatPropertyValue(rowId, colId, property);
					}else{
						
						return null;						
					}
				}
			}
		};
		
		tb.setSelectable(true);
		tb.setColumnCollapsingAllowed(true);
		tb.setMultiSelect(true);
		tb.setMultiSelectMode(MultiSelectMode.DEFAULT);
		
		tb.addContainerProperty("Cliente", String.class, "");
		tb.addContainerProperty("Cod. Cliente", Integer.class, "");
		tb.addContainerProperty("Cod.", String.class, "");
		tb.addContainerProperty("Controle", String.class, "");
		tb.addContainerProperty("N.Doc", String.class, "");
		//tb.addContainerProperty("N.Numero", String.class, "");
		tb.addContainerProperty("N.Numero Sicred", String.class, "");
		tb.addContainerProperty("Transação GerenciaNet", String.class, "");
		tb.addContainerProperty("Emissão", Date.class, null);
		tb.addContainerProperty("Valor", String.class, "");
		tb.addContainerProperty("Vencimento", Date.class, null);
		tb.addContainerProperty("Status", String.class, "");
		tb.addContainerProperty("B", String.class, "");		
		tb.addContainerProperty("Valor Pago", String.class, "");
		tb.addContainerProperty("Pagamento", Date.class, null);
		tb.addContainerProperty("Forma Pagamento", String.class, "");
		tb.addContainerProperty("Valor Lançamento", String.class, "");
		tb.addContainerProperty("Valor Tarifa", String.class, "");		
				
		tb.setColumnCollapsed("Cod. Cliente", true);		
		tb.setColumnAlignment("Valor", Align.RIGHT);
		tb.setColumnAlignment("Valor Pago", Align.RIGHT);	
		
		tb.setColumnWidth("Cliente", 350);
		tb.setColumnWidth("Cod.", 55);
		tb.setColumnWidth("N.Doc", 110);
		tb.setColumnWidth("Emissão", 80);
		tb.setColumnWidth("Vencimento", 80);
		tb.setColumnWidth("Status", 70);
		tb.setColumnWidth("Valor", 100);
		tb.setColumnWidth("Valor Lançamento", 100);
		tb.setColumnWidth("Valor Tarifa", 100);
		tb.setColumnWidth("Valor Pago", 100);
		
		tb.setVisibleColumns(new Object[]{"Cliente","Cod. Cliente","Cod.","Controle","N.Doc","N.Numero Sicred","Transação GerenciaNet","Emissão","Valor","Vencimento","Status","B","Valor Pago","Pagamento","Forma Pagamento","Valor Lançamento","Valor Tarifa"});
				
		tb.setImmediate(true);
		
		tb.addItemClickListener(new ItemClickEvent.ItemClickListener() {
			
			@Override
			public void itemClick(ItemClickEvent event) {
				((Table) event.getSource()).select(event.getItemId());
				if(event.isDoubleClick()){
					if(event.getItem() != null){
						addFilter(event.getItem().getItemProperty("Cliente").getValue().toString());
					}
				}
				
			}
		});
		
		tb.addValueChangeListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(event.getProperty().getValue() != null){
					
					
					Set<Object> selected = (Set<Object>)tb.getValue();
					
					if(tb.getValue() != null && selected.size() == 1 && tb.getItem(selected.toArray()[0]) != null){						
						tfBusca.setValue(tb.getItem(selected.toArray()[0]).getItemProperty("Cliente").getValue().toString());						
					}
					
					if(selected.size()== 1 && tb.getItem(selected.toArray()[0]).getItemProperty("Status").getValue() != null){
						
						btOcorrencia.setEnabled(true);
					
						if(tb.getItem(selected.toArray()[0]).getItemProperty("Status") != null && tb.getItem(selected.toArray()[0]).getItemProperty("Status").getValue().equals("FECHADO")){					
							
							btReabrir.setEnabled(true);
							btBaixar.setEnabled(false);
							btBaixarComHaver.setEnabled(false);
							btNegativar.setEnabled(false);
							btImprimirRecibo.setEnabled(true);
							btImprimir.setEnabled(false);
							//btLog.setEnabled(true);
							
						}else if(tb.getItem(selected.toArray()[0]).getItemProperty("Status") != null && tb.getItem(selected.toArray()[0]).getItemProperty("Status").getValue().equals("ABERTO")){
							btReabrir.setEnabled(false);
							btBaixar.setEnabled(true);
							btBaixarComHaver.setEnabled(true);
							btImprimirRecibo.setEnabled(false);
							btImprimir.setEnabled(true);
						//	btLog.setEnabled(true);
							
							if(!tb.getItem(selected.toArray()[0]).getItemProperty("Status").getValue().equals("NEGATIVADO")){
								btNegativar.setEnabled(true);
								btLog.setEnabled(true);
							}else{
								btBaixar.setEnabled(true);
								btBaixarComHaver.setEnabled(true);
								btNegativar.setEnabled(false);
						//		btLog.setEnabled(true);
								btImprimir.setEnabled(true);
							}
						}
					
//   					if(tb.getItem(selected.toArray()[0]).getItemProperty("Forma Pagamento").getValue() != null && !tb.getItem(selected.toArray()[0]).getItemProperty("Forma Pagamento").getValue().equals("")){	
//								
//	     				    if(tb.getItem(selected.toArray()[0]).getItemProperty("Forma Pagamento").getValue().equals("HAVER")){
//									btReabrir.setEnabled(false);
//						    }
//
//						}
					
					}else{
						btReabrir.setEnabled(false);
						btBaixar.setEnabled(true);
						btBaixarComHaver.setEnabled(true);
						btNegativar.setEnabled(false);
						btOcorrencia.setEnabled(false);
					//	btLog.setEnabled(true);
						//btImprimirRecibo.setEnabled(false);
					}
					
					btEditar.setEnabled(true);					
					btExcluir.setEnabled(true);
					btImprimirRecibo.setEnabled(true);
					//btLog.setEnabled(true);
					btOcorrencia.setEnabled(true);
					
				}else{
							
					btReabrir.setEnabled(false);
					btBaixar.setEnabled(false);
					btBaixarComHaver.setEnabled(false);
					btNegativar.setEnabled(false);					
					btEditar.setEnabled(false);
					btImprimir.setEnabled(false);
					btExcluir.setEnabled(false);
					btImprimirRecibo.setEnabled(false);
					//btLog.setEnabled(false);
					btOcorrencia.setEnabled(false);
				}				
			}
		});
		
		return tb;
	}
	
	private void refresh(String valorBusca){
				
		if(tb != null)

		tb.removeAllItems();
		qtdRegistros = 0;
		
		Float qtdRegistrosAReceber = 0.00f;
		Float qtdRegistrosPagos = 0.00f;
		
		List<ContasReceber> result;
		if(cbControle.getValue() != null){
			result = ClienteDAO.listarClientesTitulosContasReceber(cbStatus.getValue().toString(), valorBusca,cbControle.getItem(cbControle.getValue()).getItemProperty("nome").getValue().toString(),null);
		}else{
			result = ClienteDAO.listarClientesTitulosContasReceber(cbStatus.getValue().toString(), valorBusca,null,null);
		}
		
		if(result != null && result.size() > 0){
			Integer i = 0;
			String RowAnterior = "";
			Integer RowAnteriorId = 0;
			Integer parentId = null;
			for(ContasReceber c:result){	       

				
					if(i==0){
						RowAnterior = c.getCliente().getNome_razao();
						RowAnteriorId = c.getCliente().getId();
					}
					if(RowAnterior != null && c.getCliente().getNome_razao() != null && c.getCliente().getNome_razao().equals(RowAnterior) && c.getCliente().getId().equals(RowAnteriorId) && i == 0){				
						tb.addItem(new Object[]{c.getCliente().getNome_razao(),
								null,
								null,
								null,
								null,
								null,								
								null,
								null,
								null,
								null,
								null,
								null,
								null,
								null, 
								null, 
								null,								
								null}, i);
						tb.setChildrenAllowed(i, true);
						parentId = i;
						i++;
						

						tb.addItem(new Object[]{
								c.getCliente().getNome_razao(),
								c.getCliente().getId(),
								c.getId().toString(), 
								c.getControle(),
							    c.getN_doc(),							    
							    c.getN_numero_sicred(),
							    c.getTransacao_gerencianet(),
								c.getData_emissao(),
								c.getValor_titulo(),
								c.getData_vencimento(),
								c.getStatus(),
								c.getBloqueado(),
								c.getValor_pagamento(),
								c.getData_pagamento(),
								c.getForma_pgto(), 
								c.getValor_lancamento() != null ? c.getValor_lancamento() : "0,00", 
								c.getValor_tarifa() != null ? c.getValor_tarifa() : "0,00"}, i);
						tb.setChildrenAllowed(i, false);						
						tb.setParent(i, parentId);
						
						qtdRegistros++;												
					}
					
					if(RowAnterior != null && c.getCliente().getNome_razao() != null && c.getCliente().getNome_razao().equals(RowAnterior)  && c.getCliente().getId().equals(RowAnteriorId) && i > parentId){				
						
						
						tb.addItem(new Object[]{
								c.getCliente().getNome_razao(),
								c.getCliente().getId(),
								c.getId().toString(), 
								c.getControle(),
							    c.getN_doc(),							    
							    c.getN_numero_sicred(),
							    c.getTransacao_gerencianet(),
								c.getData_emissao(),
								c.getValor_titulo(),
								c.getData_vencimento(),
								c.getStatus(),
								c.getBloqueado(),
								c.getValor_pagamento(),
								c.getData_pagamento(),
								c.getForma_pgto(), 
								c.getValor_lancamento() != null ? c.getValor_lancamento() : "0,00", 
								c.getValor_tarifa() != null ? c.getValor_tarifa() : "0,00"}, i);
						
						tb.setChildrenAllowed(i, false);						
						tb.setParent(i, parentId);
						i++;
						qtdRegistros++;
						
						if(c.getStatus().equals("FECHADO")){
							if(c.getValor_pagamento() != null && !c.getValor_pagamento().isEmpty()){
								qtdRegistrosPagos = qtdRegistrosPagos+Float.parseFloat(Real.formatStringToDB(c.getValor_pagamento()));
							}
						}else{
							
							if(!c.getStatus().equals("EXCLUIDO")){
							 if(c.getValor_titulo() != null && !c.getValor_titulo().isEmpty() && !c.getValor_titulo().equals("null")){
								qtdRegistrosAReceber = qtdRegistrosAReceber+Float.parseFloat(Real.formatStringToDB(c.getValor_titulo()));
							 }
							}							
						}
					}
					
					if(RowAnterior != null && c.getCliente().getNome_razao() != null && !c.getCliente().getId().equals(RowAnteriorId)){
						RowAnterior = c.getCliente().getNome_razao();
						RowAnteriorId = c.getCliente().getId();
						
						
						tb.addItem(new Object[]{c.getCliente().getNome_razao(),								
								null,
								null,
								null,
								null,
								null,
								null,
								null,
								null,								
								null,
								null,
								null,
								null, 
								null, 
								null,
								null,
								null}, i);
						
						tb.setChildrenAllowed(i, true);						
						parentId = i;
						i++;
						
						tb.addItem(new Object[]{
								c.getCliente().getNome_razao(),
								c.getCliente().getId(),
								c.getId().toString(), 
								c.getControle(),
							    c.getN_doc(),							  
							    c.getN_numero_sicred(),
							    c.getTransacao_gerencianet(),
								c.getData_emissao(),
								c.getValor_titulo(),
								c.getData_vencimento(),
								c.getStatus(),
								c.getBloqueado(),
								c.getValor_pagamento(),
								c.getData_pagamento(),
								c.getForma_pgto(), 
								c.getValor_lancamento() != null ? c.getValor_lancamento() : "0,00", 
								c.getValor_tarifa() != null ? c.getValor_tarifa() : "0,00"}, i);
						tb.setChildrenAllowed(i, false);						
						tb.setParent(i, parentId);
						i++;
						qtdRegistros++;
						
						
						if(c.getStatus().equals("FECHADO")){
							if(c.getValor_pagamento() != null && !c.getValor_pagamento().isEmpty()){
								qtdRegistrosPagos = qtdRegistrosPagos+Float.parseFloat(Real.formatStringToDB(c.getValor_pagamento()));
							}
						}else{							
							if(!c.getStatus().equals("EXCLUIDO")){
								 if(c.getValor_titulo() != null && !c.getValor_titulo().isEmpty()){
									qtdRegistrosAReceber = qtdRegistrosAReceber+Float.parseFloat(Real.formatStringToDB(c.getValor_titulo()));
								 }
							}							
						}
					}
					
					
			}
		}
		
			
		tb.setSizeFull();
		
		
		
		if(tb.getContainerDataSource().size() == 11){
			tb.select(1);
		}
		
		
		if(tb.getContainerDataSource().rootItemIds().size() == 1){
			Collection<?> itemIds = tb.getContainerDataSource().getItemIds();
			for (Object object : itemIds) {
				System.out.println(object);
				tb.setCollapsed(object, false);
				break;
			}
			
		}
		
		if(qtdRegistros > 0){
			qtdRegistros--;
		}
		if(lbRegistros != null){
			hlFloat.replaceComponent(lbRegistros, 
					BuildLbRegistros(qtdRegistros, 
							Real.formatDbToString(qtdRegistrosAReceber.toString()), Real.formatDbToString(qtdRegistrosPagos.toString())));
		}
	}
	
	public String formatDate(Date d){
		
		if(d != null){
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			return sdf.format(d);
		}else{
			return null;
		}
	}
	public ComboBox buildComboControle(){
		
		JPAContainer<ControleTitulo> containerControle = JPAContainerFactory.makeReadOnly(ControleTitulo.class,ConnUtil.getEntity());
		containerControle.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
		
		cbControle = new ComboBox(null,containerControle);
		//cbControle.setNullSelectionAllowed(false);
		cbControle.setTextInputAllowed(false); 
		cbControle.setItemCaptionPropertyId("nome");
		
//		if(OpusERP4UI.getEmpresa().getId().equals(5)){
//			cbControle.addItem("ESPACO-COWORKING");
//			cbControle.addItem("ESPACO-REUNIAO");
//			cbControle.addItem("ESPACO-TREINAMENTO");
//			cbControle.addItem("PRODUTO");
//			cbControle.addItem("SERVICO");
//			cbControle.addItem("OUTROS");
//		}else{
//			cbControle.addItem("ACESSO-POS");
//			cbControle.addItem("ACESSO-PRE");
//			cbControle.addItem("ASSISTENCIA");
//			cbControle.addItem("ALUGUEL");					
//			cbControle.addItem("PRODUTO");
//			cbControle.addItem("SERVICO");
//			cbControle.addItem("HAVER");
//		}
		
		
		
		cbControle.setImmediate(true);
		
		cbControle.addListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				
				if(tfBusca.getValue() != null && !tfBusca.getValue().isEmpty()){
					addFilter(tfBusca.getValue());
				}
			}
		});	
		
		return cbControle;
	}
	
	public ComboBox buildComboStatus(){
		cbStatus = new ComboBox();
		cbStatus.setNullSelectionAllowed(false);
		cbStatus.setTextInputAllowed(false);
		
		EntityManager em = ConnUtil.getEntity();		
		Query q = em.createNativeQuery("SELECT ac.STATUS_2 FROM contas_receber as ac WHERE EMPRESA_ID = :codEmpresa GROUP BY STATUS_2");
		q.setParameter("codEmpresa", OpusERP4UI.getEmpresa().getId());
		
		List<Object> result = q.getResultList();
		
		cbStatus.addItem("TODOS");
		cbStatus.addItem("VENCIDOS");
		for(Object o:result){
			cbStatus.addItem(o.toString());
		}
		
		
		cbStatus.setImmediate(true);
		cbStatus.select("VENCIDOS");
		
		cbStatus.addValueChangeListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				
				if(tfBusca.getValue() != null && !tfBusca.getValue().isEmpty()){
					addFilter(tfBusca.getValue());
				}else{
					if(cbStatus.getValue().equals("VENCIDOS")){
						addFilter("");
					}
				}
			}
		});	
		
		return cbStatus;	
	}
	
	public TextField buildTfbusca() {
		tfBusca = new TextField();
		tfBusca.setWidth("100%");
		tfBusca.setTextChangeEventMode(TextChangeEventMode.LAZY);
		tfBusca.setInputPrompt("BUSCAR...");
		tfBusca.focus();	
		tfBusca.setImmediate(true);
		
		tfBusca.addShortcutListener(new ShortcutListener("Enter", ShortcutAction.KeyCode.ENTER,null) {
			
			@Override
			public void handleAction(Object sender, Object target) {
				
					if(tfBusca.getValue() != null && !tfBusca.getValue().isEmpty()){
						addFilter(tfBusca.getValue());				
					}else{
						if(cbStatus.getValue().equals("VENCIDOS")){
							addFilter("");
						}
					}				
			}
		});		

		return tfBusca;
	}

	
	public void addFilter(Integer s) {
		// TODO Auto-generated method stub
		
	}

	
	public void addFilter(String s) {	
		refresh(s);
	}
	
	//private void refresh(String s){
	//	replaceComponent(tb,buildTbGeneric(s));
	//	setExpandRatio(tb, 1);
	//}
	
	Button btReemitir;
	public Button BuildbtReemitir(){
		btReemitir = new Button("Reemitir", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Reemitir Boleto"))				
				{									
					GenericDialog gd = new GenericDialog("Confirme para continuar!", "Deseja realmente Reemitir Boleto ?", true, true);
					gd.addListerner(new GenericDialog.DialogListerner() {
						
						@Override
						public void onClose(DialogEvent event) {
							
							if(event.isConfirm()){
								for(Object object: (Set<Object>)tb.getValue()){
									Integer codBoleto = Integer.parseInt(tb.getItem(object).getItemProperty("Cod.").getValue().toString());
									
									//Gerar Nosso Número
									boolean check = ContasReceberDAO.reemitirBoleto(codBoleto);
									
									if(check){
										Notify.Show("Boleto Reemitido com Sucesso: "+codBoleto.toString(), Notify.TYPE_SUCCESS);
										addFilter(tfBusca.getValue());				
									}else{
										Notify.Show("Não é possível reemitir este boleto! "+codBoleto.toString(), Notify.TYPE_ERROR);
									}
								}
							}							
						}
					});
					
					getUI().addWindow(gd);
				}else{
					Notification.show("Atenção", "Você não Possui Permissão para Reemitir boleto", Type.ERROR_MESSAGE);
				}
			}
		});
		return btReemitir;
	}
	
	

	Button btGerarNossoNumero;
	public Button BuildbtGerarNossoNumero() {
		btGerarNossoNumero = new Button("Gerar NN", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Gerar Nosso Número"))				
				{
									
					GenericDialog gd = new GenericDialog("Confirme para continuar!", "Deseja realmente gerar Nosso Número para os boletos selecionados ?", true, true);
					gd.addListerner(new GenericDialog.DialogListerner() {
						
						@Override
						public void onClose(DialogEvent event) {
							
							if(event.isConfirm()){
								List<Integer> codigos = new ArrayList<>();
								
								for(Object object: (Set<Object>)tb.getValue()){
									Integer codBoleto = Integer.parseInt(tb.getItem(object).getItemProperty("Cod.").getValue().toString());
									codigos.add(codBoleto);
								}
								
								GerarNNDAO.gerar(codigos); 
								refresh(tfBusca.getValue());
							}
							
						}
					});
					
					getUI().addWindow(gd);
				}else{
					Notification.show("Atenção", "Você não Possui Permissão para Gerar Nosso Número", Type.ERROR_MESSAGE);
				}
			}
		});
		return btGerarNossoNumero;
	}
	
	
	public Button BuildbtNovo() {
		btNovo = new Button("Novo", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Cadastrar"))				
				{
					
					final BeanItem<ContasReceber> newItem = new BeanItem<ContasReceber>(new ContasReceber());
				
					NovoTituloEditor novoTitulo = new NovoTituloEditor("Novo Título", true);
					novoTitulo.addListerner(new NovoTituloEditor.NovoTituloListerner() {
					
					@Override
					public void onClose(NovoTituloEvent event) {
						if(event.isConfirm()){
							
							ContasReceber cr = newItem.getBean();
							EntityManager em = ConnUtil.getEntity();
							try {
								
								em.getTransaction().begin();
								
								ContasReceberDAO contaDAO = new ContasReceberDAO();
																 
									if(event.getIntervalo().equals("MENSAL")){
										for (int i = 0; i < Integer.parseInt(event.getQtd()); i++) {
										    System.out.println("Este é um teste Mensal para o System.out.println");
											LocalDate localDate = new LocalDate(event.getDataVenciemento());
											LocalDate moreDays = localDate.plusMonths(i);	
											Date dataBoleto = moreDays.toDate();
											
											contaDAO.gerarBoletos(new ContasReceber(new Cliente(event.getCodCliente()), event.getnDoc()+"-"+String.format("%02d", i+1)+"/"+String.format("%02d",Integer.parseInt(event.getQtd())), 
													event.getValor(), new Date(), dataBoleto,"ABERTO", "BOLETO", event.getControle()));
										}
									}
									
									if(event.getIntervalo().equals("SEMANAL")){
										for (int i = 0; i < Integer.parseInt(event.getQtd()); i++) {
											
											LocalDate localDate = new LocalDate(event.getDataVenciemento());
											LocalDate moreDays = localDate.plusWeeks(i);	
											Date dataBoleto = moreDays.toDate();
											
											contaDAO.gerarBoletos(new ContasReceber(new Cliente(event.getCodCliente()), event.getnDoc()+"-"+String.format("%02d", i+1)+"/"+String.format("%02d",Integer.parseInt(event.getQtd())), 
													event.getValor(), new Date(), dataBoleto,"ABERTO", "BOLETO", event.getControle()));
										}
									}
								
								
									//Atualiza Saldo de Crédito do Cliente
									CreditoClienteDAO creditoDAO = new CreditoClienteDAO();
									creditoDAO.setSaldo(event.getCodCliente(), new Double(Real.formatStringToDB(event.getValor())));
								
								em.getTransaction().commit();								
								
								Cliente c = em.find(Cliente.class, event.getCodCliente());
								tfBusca.setValue(c.getNome_razao());
								
								refresh(c.getNome_razao());
								
								LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Cadastrou uma Novo Boleto"));
								
							} catch (Exception e) {
								
								if(em.getTransaction().isActive()){
									em.getTransaction().rollback();
								}
								
								e.printStackTrace();
							}
						}
					}
				});
				
				
				novoTitulo.addCloseListener(new Window.CloseListener() {
										
					@Override
					public void windowClose(CloseEvent e) {						
						tb.focus();						
					}
				});
				
				getUI().addWindow(novoTitulo);		
				
				}else{
					Notification.show("Atenção", "Você não Possui Permissão para Gerar um Novo Boleto", Type.ERROR_MESSAGE);
				}
			}
		});
		return btNovo;
	}

	
	public Button BuildbtEditar() {
		btEditar= new Button("Editar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				Set<Object> selecteds = (Set<Object>)tb.getValue();
				if(tb.getItem(selecteds.toArray()[0]).getItemProperty("Cod.").getValue() != null){
						final Integer codContaReceber = Integer.parseInt(tb.getItem(selecteds.toArray()[0]).getItemProperty("Cod.").getValue().toString());
						boolean allowNfe = ContasReceberDAO.allowEdit(ContasReceberDAO.find(codContaReceber));
						boolean allowGerenciaNet = tb.getItem(selecteds.toArray()[0]).getItemProperty("Transação GerenciaNet").getValue() != null ? false : true;
						
						if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), 
						OpusERP4UI.getUsuarioLogadoUI().getId(), "Alterar") && allowNfe && allowGerenciaNet)	{
							
							editarCliente();				
						}else{
							
							if(!allowNfe){
								Notify.Show("Nota fiscal já foi gerada, não é possivel editar boleto", Notify.TYPE_ERROR);
							}else if(!allowGerenciaNet){
								Notify.Show("Você precisa cancelar a transação do GerenciaNet antes de editar este boleto.", Notify.TYPE_ERROR);
							}else{
								Notify.Show("Você não Possui Permissão para Alterar um Boleto", Notify.TYPE_ERROR);
							}
							
						}
				}
			}
		});
		btEditar.setEnabled(false);
		return btEditar;
	}


	private void editarCliente(){
		Set<Object> selecteds = (Set<Object>)tb.getValue();
		if(selecteds.size() == 1 && tb.getItem(selecteds.toArray()[0]).getItemProperty("Cod.").getValue() != null){
		
			try {
				DateFormat formatter = new SimpleDateFormat("dd/MM/yy");
								
				final Integer codContaReceber = Integer.parseInt(tb.getItem(selecteds.toArray()[0]).getItemProperty("Cod.").getValue().toString());
				String nomeRazao = tb.getItem(selecteds.toArray()[0]).getItemProperty("Cliente").getValue().toString();
				Integer codCliente = (Integer) tb.getItem(selecteds.toArray()[0]).getItemProperty("Cliente.id").getValue();
				valorTitulo = tb.getItem(selecteds.toArray()[0]).getItemProperty("Valor").getValue().toString();
				
				String valorPagamento;
				if(tb.getItem(selecteds.toArray()[0]).getItemProperty("Valor Pago").getValue() != null){
					valorPagamento = tb.getItem(selecteds.toArray()[0]).getItemProperty("Valor Pago").getValue().toString();
				}else{
					valorPagamento = null;
				}
				
				String nDoc = tb.getItem(selecteds.toArray()[0]).getItemProperty("N.Doc").getValue().toString();
				
				final Date dataVencimento = (Date)tb.getItem(selecteds.toArray()[0]).getItemProperty("Vencimento").getValue();
				
				Date dataPagamento;
				if(tb.getItem(selecteds.toArray()[0]).getItemProperty("Pagamento").getValue() != null){
					dataPagamento  = (Date)tb.getItem(selecteds.toArray()[0]).getItemProperty("Pagamento").getValue();
				}else{
					dataPagamento  = null;
				}
				
				String controle = "";
				if(tb.getItem(selecteds.toArray()[0]).getItemProperty("Controle").getValue() != null){
					controle = tb.getItem(selecteds.toArray()[0]).getItemProperty("Controle").getValue().toString();
				}
				EntityManager em = ConnUtil.getEntity();
				
				ContasReceber cr = null;
				Query q = em.createQuery("select c from ContasReceber c where c.id =:cod", ContasReceber.class);
				q.setParameter("cod", codContaReceber);
				
				if(q.getResultList().size() == 1){
					cr = (ContasReceber)q.getSingleResult();
				}
				
				EditarTituloEditor editarTituloEditor = new EditarTituloEditor("Editar Título", true, nomeRazao, valorTitulo, valorPagamento, 
						nDoc, dataVencimento, dataPagamento,controle,cr.getCliente().getId());
				
				if(cr != null){
					
					editarTituloEditor.addListerner(new EditarTituloEditor.EditarTituloListerner() {
						
						@Override
						public void onClose(EditarTituloEvent event) {
							if(event.isConfirm()){
								
								EntityManager em = ConnUtil.getEntity();
								
								try {
									ContasReceber cr = em.find(ContasReceber.class, codContaReceber);
									if(cr != null){
										
										cr.setValor_titulo(event.getValorTitulo());
										cr.setValor_pagamento(event.getValorPagamento());
										cr.setN_doc(event.getnDoc());
										cr.setData_pagamento(event.getDataPagamento());
										cr.setData_vencimento(event.getDataVenciemento());
										cr.setControle(event.getControle());
										
										//REENVIAR REMESSA DE BOLETO
										//SOLICITAR ALTERAÇÃO DE DATA DE VENCIMENTO
//										if(cr.getRemessaEnviada() != null && cr.getRemessaEnviada().equals("SIM") && dataVencimento != event.getDataVenciemento()){										
//											cr.setRemessaEnviada(null);
//											cr.setComandoRemessa("06");											
//										}
										
//										if(dataVencimento != event.getDataVenciemento() && cr.getTransacao_gerencianet() != null && !cr.getTransacao_gerencianet().equals("")){
//											alterarVenciementoGerenciaNet(cr.getTransacao_gerencianet(), event.getDataVenciemento());
//										}
																			
										em.getTransaction().begin();
										em.merge(cr);
										em.getTransaction().commit();
																			
										ConnUtil.getEntity().getTransaction().begin();
										CreditoClienteDAO.atualizarSaldo(ContasReceberDAO.find(codContaReceber).getCliente().getId(),Real.formatStringToDBDouble(event.getValorTitulo()),Real.formatStringToDBDouble(valorTitulo));
										ConnUtil.getEntity().getTransaction().commit();
										
										refresh(cr.getCliente().getNome_razao());
										
										LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Editou um Boleto"));
										AlteracoesContasReceberDAO.save(new AlteracoesContasReceber(null, "EDITOU UM BOLETO", cr,OpusERP4UI.getEmpresa(), OpusERP4UI.getUsuarioLogadoUI(), new Date()));
										
									}
								} catch (Exception e) {
									
									if(em.getTransaction().isActive()){
										em.getTransaction().rollback();
									}
									
									Notification.show("Não Foi Possivel Editar o Título, Tente Novamente!");
									e.printStackTrace();										
								}
								
							}								
						}
					});
					
					editarTituloEditor.addCloseListener(new Window.CloseListener() {
						
						@Override
						public void windowClose(CloseEvent e) {						
							tb.focus();						
						}
					});
					
					
					getUI().addWindow(editarTituloEditor);
				
				}
				
			} catch (Exception e) {
				
				Notification.show("Não Foi Possivel Editar o Título, Tente Novamente!");
				e.printStackTrace();
			}
		
		}
	}
			
	public Button BuildbtExcluir() {
		btExcluir= new Button("Excluir", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Excluir"))				
				{
					
					GenericDialog gd = null;
					EntityManager em = ConnUtil.getEntity();
					excluirDefinitivamente = false;
					
					for(Object object: (Set<Object>)tb.getValue()){
						
						if(tb.getItem(object).getItemProperty("Cod.").getValue() != null){
								//final ContasReceber cr = em.find(ContasReceber.class, Integer.parseInt(tb.getItem(object).getItemProperty("Cod.").getValue().toString()));
								
								ContasReceber cr = null;
								
								Query q = em.createQuery("select c from ContasReceber c where c.id =:cod", ContasReceber.class);
								q.setParameter("cod", Integer.parseInt(tb.getItem(object).getItemProperty("Cod.").getValue().toString()));
								
								if(q.getResultList().size() == 1){
									cr = (ContasReceber)q.getSingleResult();
								}
								
								
								if(cr != null && cr.getStatus() != null && cr.getStatus().equals("EXCLUIDO")){
									excluirDefinitivamente = true;
									break;
								}
						}
					}
					
					
					if(!excluirDefinitivamente){
						gd = new GenericDialog("Confirme para Continuar!", "Você deseja realmente excluir o(s) Boleto(s) Selecionado(s) ?", "SIM", "NÃO", "250px", "151px", true, true);
					}else{
						gd = new GenericDialog("Confirme para Continuar!", "Um ou mais Boleto(s) Selecionados já estão Excluídos deseja Remove-los Definitivamente ?", "SIM", "NÃO", "250px", "151px", true, true);
					}
				
								
					gd.addListerner(new GenericDialog.DialogListerner() {
						
						@Override
						public void onClose(DialogEvent event) {
							if(event.isConfirm()){						
								
									final EntityManager em = ConnUtil.getEntity();
								try {
									
										final Set<Object> selecteds = (Set<Object>)tb.getValue();
										em.getTransaction().begin();	
										for (Object object : selecteds) {
												
												if(tb.getItem(object).getItemProperty("Cod.").getValue() != null){
											
														ContasReceber cr = null;
													
														//final ContasReceber cr = em.find(ContasReceber.class, Integer.parseInt(tb.getItem(object).getItemProperty("Cod.").getValue().toString()));
														Query q2 = em.createQuery("select cr from ContasReceber cr where cr.id=:cod", ContasReceber.class);
														q2.setParameter("cod", Integer.parseInt(tb.getItem(object).getItemProperty("Cod.").getValue().toString()));
														
														if(q2.getResultList().size() == 1){
															cr = (ContasReceber)q2.getSingleResult();
														}
														
														if(cr != null){		
															
															if(cr.getStatus().equals("EXCLUIDO") && excluirDefinitivamente && cr.getRemessaEnviada() == null){
																//Retirado temporariamente a opção de excluir definitivamente os boleto
																//em.remove(cr);
															}else{														
																cr.setStatus("EXCLUIDO");	
																cr.setData_exclusao(new Date());
																em.merge(cr);
																em.merge((new AlteracoesContasReceber(null, "EXCLUIU UM BOLETO", cr,OpusERP4UI.getEmpresa(), OpusERP4UI.getUsuarioLogadoUI(), new Date())));
																
																//Pedir cancelamento da transação
																if(cr.getTransacao_gerencianet() != null){
																	cancelarTransacaoSemTransacao(cr.getTransacao_gerencianet());
																}
																
															}
															
															if(cr.getForma_pgto() != null && cr.getForma_pgto().equals("HAVER")){
																HaverCab haver = HaverDAO.buscarHaver(cr.getCliente());
																haver.setValor_total(haver.getValor_total()+Real.formatStringToDBDouble(cr.getValor_pagamento()));
																em.merge(haver);
																
																Query q = em.createQuery("select  h from HaverDetalhe h where h.n_doc=:n_doc", HaverDetalhe.class);
																q.setParameter("n_doc", cr.getId().toString());
																
															    for (HaverDetalhe haver_det : (List<HaverDetalhe>)q.getResultList()) {
																	haver_det.setStatus("INATIVO");
															    	em.merge(haver_det);
																}
															}
														}
												}
										}
										
										em.getTransaction().commit();
										tfBusca.setValue(tb.getItem(selecteds.toArray()[0]).getItemProperty("Cliente").getValue().toString());
										refresh(tfBusca.getValue());
										Notification.show("Boleto(s) Excluído(s) com Sucesso");
										
										//Impressão, Baixa, Exclusão, Reabertura
										//------------------------------------------------------------------------------------------------------------------//
										//boolean checkCancelar = cancelarTransacao(crAtualizado.getTransacao_gerencianet());
										//if(checkCancelar){
										//		Notify.Show("Transação marcada como pago no gerencianet.", Notify.TYPE_NOTICE);
										//}
										
										tb.focus();
										
										LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Excluiu um ou Mais Boleto(s)"));
										//	AlteracoesContasReceberDAO.save(new AlteracoesContasReceber(null, "EXCLUIU UM BOLETO", cr,OpusERP4UI.getEmpresa(), OpusERP4UI.getUsuarioLogadoUI(), new Date()));

									
									} catch (Exception e) {
										
										if(em.getTransaction().isActive()){
											em.getTransaction().rollback();
										}
										
										Notification.show("Não foi Possivel Concluir a Exclusão de Boletos");
										e.printStackTrace();
										
										tb.focus();
									}
									
									
								
							}
						}
					});
					
					
					gd.addCloseListener(new Window.CloseListener() {
						
						@Override
						public void windowClose(CloseEvent e) {						
							tb.focus();						
						}
					});
					
					getUI().addWindow(gd);
					
				}else{
					Notification.show("Atenção", "Você não Possui Permissão para Excluir um Boleto", Type.ERROR_MESSAGE);
				}
				
			}
		});
		btExcluir.setEnabled(false);
		return btExcluir;
	}
	
		
	public Button BuildbtNegativar() {
		btNegativar= new Button("Negativar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
					
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Negativar"))				
				{
					NegativarTituloEditor gd = new NegativarTituloEditor("Negativar Título(s)",true);
					gd.addListerner(new NegativarTituloEditor.NegativarTituloListerner() {
						
						@Override
						public void onClose(NegativarTituloEvent event) {
							if(event.isConfirm()){								
								
								
									EntityManager em = ConnUtil.getEntity();						
									
									
									
									try {
									
										final Set<Object> selecteds = (Set<Object>)tb.getValue();
										em.getTransaction().begin();
										for (Object object : selecteds) {
											
												
												ContasReceber cr = em.find(ContasReceber.class, Integer.parseInt(tb.getItem(object).getItemProperty("Cod.").getValue().toString()));
												cr.setStatus(event.getStatusNegativado());
												em.merge(cr);	
												em.merge((new AlteracoesContasReceber(null, "NEGATIVOU UM BOLETO", cr,OpusERP4UI.getEmpresa(), OpusERP4UI.getUsuarioLogadoUI(), new Date())));
												//AlteracoesContasReceberDAO.save(new AlteracoesContasReceber(null, "NEGATIVOU UM BOLETO", cr,OpusERP4UI.getEmpresa(), OpusERP4UI.getUsuarioLogadoUI(), new Date()));

										}
										
										em.getTransaction().commit();
										tfBusca.setValue(tb.getItem(selecteds.toArray()[0]).getItemProperty("Cliente").getValue().toString());
										refresh(tfBusca.getValue());										
										Notification.show("Boleto(s) Negativado com Sucesso !");
										
										LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Negativou um ou Mais Boleto(s)"));
									
									} catch (Exception e) {
										
										if(em.getTransaction().isActive()){
											em.getTransaction().rollback();
										}
										
										Notification.show("Não foi Possivel Concluir a Negativado de Boleto(s)");
										//e.printStackTrace();
									}			
									
									
								
							}
						}
					});
					
					
					gd.addCloseListener(new Window.CloseListener() {
						
						@Override
						public void windowClose(CloseEvent e) {						
							tb.focus();						
						}
					});
					
					getUI().addWindow(gd);
					
					}else{
						Notification.show("Atenção", "Você não Possui Permissão para Negativar um Boleto", Type.ERROR_MESSAGE);
					}
				
				
			}
		});
		btNegativar.setEnabled(false);
		return btNegativar;
	}
	
	public Button BuildbtBaixarComHaver(){
		btBaixarComHaver = new Button("Baixar com Haver", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Baixar com Haver"))				
				{
					final Set<Object> selecteds = (Set<Object>)tb.getValue();
					if(selecteds.size() == 1){
						if(tb.getItem(selecteds.toArray()[0]).getItemProperty("Cod.").getValue() != null){
								final Integer codContaReceber = Integer.parseInt(tb.getItem(selecteds.toArray()[0]).getItemProperty("Cod.").getValue().toString());
								ultimoBoletoSelecionado = codContaReceber;
								Date dataVencimento = (Date)tb.getItem(selecteds.toArray()[0]).getItemProperty("Vencimento").getValue();						
								String valorTitulo = tb.getItem(selecteds.toArray()[0]).getItemProperty("Valor").getValue().toString();
								final ContasReceber cr = ContasReceberDAO.find(Integer.parseInt(tb.getItem(selecteds.toArray()[0]).getItemProperty("Cod.").getValue().toString()));
								
								BaixarComHaverEditor baixaHaver = new BaixarComHaverEditor("Baixar com Haver", true, codContaReceber, dataVencimento, valorTitulo, cr.getCliente());
								baixaHaver.addListerner(new BaixarComHaverEditor.BaixarTituloHaverListerner() {
									
									@Override
									public void onClose(BaixarTituloHaverEvent event) {
										if(event.isConfirm()){
																		
											//Baixa Boleto
											boolean check = ContasReceberDAO.baixarBoleto(codContaReceber, event.getValorHaver(),"0,00","0,00", "HAVER", "manual", new Date(), new Date(), Real.formatStringToDBDouble(event.getValorHaver()), new Double("0.00"));	
											
											//Veririfica Opções de Baixa de Contratos Pre-Pagos
											boolean check2 = CredenciaisAcessoDAO.ConcluiRecargaPrePaga(codContaReceber);
																				
											//Verificar Desbloqueio
											boolean check3 = CredenciaisAcessoDAO.ConcluiDesbloqueioAcesso(codContaReceber);	
											
											boolean checkCancelar = cancelarTransacao(cr.getTransacao_gerencianet());
											if(checkCancelar){
												Notify.Show("Transação marcada como pago no gerencianet.", Notify.TYPE_NOTICE);
											}
											
											//Verifica se Foi Recarregado Pré-Pago
											if(check2){
												System.out.println("Contrato Pre-Pago Recarregado!");
											}else{
												System.out.println("Nenhum Contrato Pre-Pago");										
											}
											
											//Verifica se Foi Baixado
											if(check){	
												
												EntityManager em = ConnUtil.getEntity();
												HaverCab haver = event.getHaver();
												
												em.getTransaction().begin();
												em.persist(new HaverDetalhe(null, haver, "SAIDA", Real.formatStringToDBDouble(event.getValorHaver()), "BOLETO", codContaReceber.toString(), "", "", new Date(), OpusERP4UI.getUsuarioLogadoUI().getUsername(),"ATIVO"));
												em.merge(haver);
												em.getTransaction().commit();
												
												GenericDialog gdConfirm = new GenericDialog("Boleto Baixado com Sucesso!", "Deseja Imprimir Recibo ?", true, true);
												gdConfirm.addListerner(new GenericDialog.DialogListerner() {
													
													@Override
													public void onClose(DialogEvent event) {
														if(event.isConfirm()){
																											
															btImprimirRecibo.click();													
															
															refresh(ContasReceberDAO.find(codContaReceber).getCliente().getNome_razao());										
															LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Baixou um Boleto"));
														}else{
															refresh(ContasReceberDAO.find(codContaReceber).getCliente().getNome_razao());										
															LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Baixou um Boleto"));
														}
													}
												});
												
												getUI().addWindow(gdConfirm);
											}else{
												Notification.show("Erro", "Não foi Possivel Baixar o Boleto", Type.ERROR_MESSAGE);
											}
											
											
										}
									}
								});
								
								getUI().addWindow(baixaHaver); 
						}
					}else{
						Notification.show("Atenção", "Você não Possui Permissão para Baixar um Boleto", Type.ERROR_MESSAGE);
					}
				}
			}
		});
		
		btBaixarComHaver.setEnabled(false);
		
		return btBaixarComHaver;
	}
	
	public Button BuildbtBaixar() {
		btBaixar= new Button("Baixar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), 
						OpusERP4UI.getUsuarioLogadoUI().getId(), "Baixar"))				
				{				
						final Set<Object> selecteds = (Set<Object>)tb.getValue();
						if(selecteds.size() == 1){
							try {
								if(tb.getItem(selecteds.toArray()[0]).getItemProperty("Cod.").getValue() != null){
									DateFormat formatter = new SimpleDateFormat("dd/MM/yy");
									final Integer codContaReceber = Integer.parseInt(tb.getItem(selecteds.toArray()[0]).getItemProperty("Cod.").getValue().toString());			
									ultimoBoletoSelecionado = codContaReceber;
									Date dataVencimento = (Date)tb.getItem(selecteds.toArray()[0]).getItemProperty("Vencimento").getValue();						
									final String valorTitulo = tb.getItem(selecteds.toArray()[0]).getItemProperty("Valor").getValue().toString();
														
									//Checar troca
									boolean alertTroca = AlertaContasTrocaDAO.check(codContaReceber);
									if(alertTroca){
										Notification.show("ATENÇÃO", "Um ou mais boletos deste cliente precisam ser trocado !", Type.WARNING_MESSAGE);
									}
									
									BaixarTituloEditor baixarTituloEditor = new BaixarTituloEditor("Baixar Título", true, codContaReceber, dataVencimento, valorTitulo);						
									baixarTituloEditor.addListerner(new BaixarTituloEditor.BaixarTituloListerner() {
										
										@Override
										public void onClose(BaixarTituloEvent event) {
											if(event.isConfirm()){
																					
												//Baixa Boleto
												boolean check = ContasReceberDAO.baixarBoletoProcessoCompleto(codContaReceber, event.getValor(),"0,00","0,00", event.getFormaPgto(), "manual", new Date(), new Date(), event.getValor_recebido(), event.getValor_troco());	
												
												final ContasReceber crAtualizado = ContasReceberDAO.find(codContaReceber);								
												
												if(check && crAtualizado!= null && crAtualizado.getStatus().equals("FECHADO")){	
													
													GenericDialog gdConfirm = new GenericDialog("Boleto Baixado com Sucesso!", "Deseja Imprimir Recibo ?", true, true);
													gdConfirm.addListerner(new GenericDialog.DialogListerner() {
														
														@Override
														public void onClose(DialogEvent event) {
															if(event.isConfirm()){																				
																btImprimirRecibo.click();																									
															}
															refresh(ContasReceberDAO.find(codContaReceber).getCliente().getNome_razao());										
														}
													});
													
													getUI().addWindow(gdConfirm);
													
													if(crAtualizado.getTransacao_gerencianet() != null && !crAtualizado.getTransacao_gerencianet().equals("")){
														boolean checkCancelar = cancelarTransacao(crAtualizado.getTransacao_gerencianet());
														if(checkCancelar){
															Notify.Show("Transação marcada como pago no gerencianet.", Notify.TYPE_NOTICE);
														}
													}
													
												}else{
													Notification.show("Erro", "Não foi Possivel Baixar o Boleto, Tente novamente!", Type.ERROR_MESSAGE);
												}
											}
										}
									});
									
									getUI().addWindow(baixarTituloEditor);
								
								}	
								
							} catch (Exception e) {
								tb.focus();	
								Notification.show("Não Foi Possivel Obter informações Necessarias para Inciar uma Baixa de Título, Tente Novamente!");
								e.printStackTrace();
							}					
						}	
				
				
				}else{
					Notification.show("Atenção", "Você não Possui Permissão para Baixar um Boleto", Type.ERROR_MESSAGE);
				}
			}
		});
		btBaixar.setEnabled(false);
		
		btBaixar.addShortcutListener(
                new Button.ClickShortcut(btBaixar, KeyCode.B, ModifierKey.CTRL));		
		
		return btBaixar;
	}
	
	Integer ultimoBoletoSelecionado;
	
	public Button BuildbtReabrir() {
		btReabrir= new Button("Re-Abrir", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				try{
					final EntityManager em = ConnUtil.getEntity();
					
					final Set<Object> selecteds = (Set<Object>)tb.getValue();
					final ContasReceber cr = em.find(ContasReceber.class, Integer.parseInt(tb.getItem(selecteds.toArray()[0]).getItemProperty("Cod.").getValue().toString()));
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					Date d = sdf.parse(sdf.format(new Date()));
					
					String permissao = cr.getData_pagamento().equals(d) ? "Extornar" : "Extornar Retroativo";
							
					if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), permissao))				
					{
							
							
							if(selecteds.size() == 1){
								GenericDialog gd = new GenericDialog("Confirme para Continuar!", "Você deseja realmente Re-Abrir o Boleto Selecionado ?", true, true);
								gd.addListerner(new GenericDialog.DialogListerner() {
									
									@Override
									public void onClose(DialogEvent event) {
										if(event.isConfirm()){						
											
												
											try {
												
												em.getTransaction().begin();
												
												if(cr.getForma_pgto().equals("HAVER")){
													HaverCab haver = HaverDAO.buscarHaver(cr.getCliente());
													haver.setValor_total(haver.getValor_total()+Real.formatStringToDBDouble(cr.getValor_pagamento()));
													HaverDAO.cancelaHaverDetalhe(cr.getId());
												}
													
												boolean reimprimir =false;
												
												if(cr.getTransacao_gerencianet() != null && !cr.getTransacao_gerencianet().equals("")){
													reimprimir = true;
													cr.setTransacao_gerencianet(null);
													cr.setN_numero_gerencianet(null); 
												}
												
												String valorPagamento = cr.getValor_pagamento();
												cr.setStatus("ABERTO");
												cr.setForma_pgto(null);
												cr.setData_pagamento(null);
												cr.setTipo_baixa(null);	
												cr.setValor_pagamento(null);
												cr.setBloquear(null);
												cr.setBloqueado(null);
												cr.setDesbloqueado(null);
												cr.setDesbloquear(null);
																								
												em.merge(cr);	
												em.merge(new AlteracoesContasReceber(null, "RE-ABRIU UM BOLETO", cr,OpusERP4UI.getEmpresa(), OpusERP4UI.getUsuarioLogadoUI(), new Date()));
	
												em.getTransaction().commit();
												
												
												if(reimprimir){
													gerarCarne();
												}
													
//												List<HaverBoleto> haverBoleto = HaverBoletoDAO.findAll(cr);
//												for (HaverBoleto hb : haverBoleto) {																																	
//													hb.getHaver().setValor_disponivel(hb.getHaver().getValor_disponivel()+hb.getValor_utilizado());	
//													//hb.getHaver().setValor_disponivel(hb.getHaver().getValor_disponivel()+Real.formatStringToDBDouble(tb.getItem(selecteds.toArray()[0]).getItemProperty("Valor Pago").getValue().toString()));
//													hb.getHaver().setStatus("DISPONÍVEL");
//													HaverDAO.save(hb.getHaver());
//													HaverBoletoDAO.remove(hb);
//													AlteracoesHaverDAO.add(new AlteracoesHaver(null, "REABERTO BOLETO Nº "+cr.getId().toString(), hb.getHaver(), hb.getHaver().getValor(),hb.getValor_utilizado(),hb.getHaver().getValor_disponivel(), OpusERP4UI.getUsuarioLogadoUI(), new Date()));
//												}
												
												//Verificar se o Cliente Utiliza Crédito e se o Boleto não é de Acesso
												String saldo = CreditoClienteDAO.getSaldo(cr.getCliente().getId());
												
												if(saldo != null && !CheckNdocUtil.checkNdocAcesso(cr.getN_doc())){
													
													ConnUtil.getEntity().getTransaction().begin();
													CreditoClienteDAO.setSaldo(cr.getCliente().getId(),Real.formatStringToDBDouble(valorPagamento));
													ConnUtil.getEntity().getTransaction().commit();
												}
												
												
												//Verificar se é um boleto vincunlado ao Contrato Pré-pago!
												if (CheckNdocUtil.checkNdocAcesso(cr.getN_doc())) {
											    	
											    	String numeroDoc = cr.getN_doc();
	
													Integer codAcesso = Integer.parseInt(numeroDoc.split("/")[0].toString());		    	
											    	AcessoCliente acessoCliente = ContratosAcessoDAO.find(codAcesso);
											    	
											    	if(acessoCliente != null && acessoCliente.getContrato().getTipo_contrato().equals("PRE-PAGO")){
											    		ConnUtil.getEntity().getTransaction().begin();
											    		acessoCliente.setData_venc_contrato( new DateTime(acessoCliente.getData_venc_contrato()).minusMonths(1).toDate());
											    		ConnUtil.getEntity().merge(acessoCliente);
											    		ConnUtil.getEntity().getTransaction().commit();
											    	}
												}
												
												
												tfBusca.setValue(cr.getCliente().getNome_razao());
												
												refresh(tfBusca.getValue());
												
												Notification.show("Boleto Re-Aberto com Sucesso !");
												
												LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Re-Abriu Boleto"));
											} catch (Exception e) {
												
												if(em.getTransaction().isActive()){
													em.getTransaction().rollback();
												}
												
												Notification.show("Não foi Possivel Concluir a Re-Abertura do Título");
												//e.printStackTrace();
											}
										}
									}
								});
								
								gd.addCloseListener(new Window.CloseListener() {
									
									@Override
									public void windowClose(CloseEvent e) {						
										tb.focus();						
									}
								});
								
								getUI().addWindow(gd);
								
							
						}
						
					}else{
						Notification.show("Atenção", "Você não Possui Permissão para Re-Abrir um Boleto", Type.ERROR_MESSAGE);
					}
				
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		});
		btReabrir.setEnabled(false);
		return btReabrir;
	}
	

	Window winSubMenuArquivo;
	Window winSubMenuImprimir;
	Window winSubMenuGerenciaNet;
	
	private void buildSubMenuArquivo(ClickEvent event) {
		winSubMenuArquivo = new Window("Escolha uma das Opções:");
        VerticalLayout l = new VerticalLayout();

        winSubMenuArquivo.setContent(l);
        winSubMenuArquivo.setWidth("300px");
        winSubMenuArquivo.addStyleName("notifications");
        winSubMenuArquivo.setClosable(false);
        winSubMenuArquivo.setResizable(false);
        winSubMenuArquivo.setDraggable(false);
        winSubMenuArquivo.setPositionX(event.getClientX() - event.getRelativeX());
        winSubMenuArquivo.setPositionY(event.getClientY() - event.getRelativeY());
        winSubMenuArquivo.setCloseShortcut(KeyCode.ESCAPE, null);

        Button btArquivoRetorno = new Button("Retorno BB", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Enviar Arq. Retorno"))				
				{
					
					UploadLiquidaEditor upload = new UploadLiquidaEditor("Liquidar Boletos", true);					
					upload.addListerner(new UploadLiquidaEditor.UploadLiquidadoListerner() {
						
						@Override
						public void onClose(UploadLiquidadoEvent event) {
									
							final ConfirBaixaArqRet caixaConfirmacao = new ConfirBaixaArqRet(true, true,event.getFileName(),"BB");
							caixaConfirmacao.addListerner(new ConfirBaixaArqRet.BaixaListerner() {
								
								@Override
								public void onClose(BaixaEvent event) {
									caixaConfirmacao.close();
								}
							});
							
							getUI().addWindow(caixaConfirmacao);	
				
						}
					});
					
					getUI().addWindow(upload);		
					
				}else{
					Notification.show("Atenção", "Você não Possui Permissão para Enviar um Arq. de Retorno", Type.ERROR_MESSAGE);
				}
			}	
		});       
        btArquivoRetorno.setPrimaryStyleName("btSubMenu");
        
        Button btArquivoRetornoSicred = new Button("Retorno Sicred", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Enviar Arq. Retorno"))				
				{
					
					UploadLiquidaEditor upload = new UploadLiquidaEditor("Liquidar Boletos", true);					
					upload.addListerner(new UploadLiquidaEditor.UploadLiquidadoListerner() {
						
						@Override
						public void onClose(UploadLiquidadoEvent event) {
									
							final ConfirBaixaArqRet caixaConfirmacao = new ConfirBaixaArqRet(true, true,event.getFileName(),"SICRED");
							caixaConfirmacao.addListerner(new ConfirBaixaArqRet.BaixaListerner() {
								
								@Override
								public void onClose(BaixaEvent event) {
									caixaConfirmacao.close();
								}
							});
							
							getUI().addWindow(caixaConfirmacao);	
				
						}
					});					
					getUI().addWindow(upload);					
				}else{
					Notification.show("Atenção", "Você não Possui Permissão para Enviar um Arq. de Retorno", Type.ERROR_MESSAGE);
				}
			}	
		});       
        btArquivoRetornoSicred.setPrimaryStyleName("btSubMenu");
        
        Button btArquivoRemessa = new Button("Remessa", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Arquivo Remessa"))				
				{
					ContasReceberDAO.gerarArquivoRemessa();
				}else{
					Notification.show("Atenção", "Você não Possui Permissão para Baixar Arquivo Remessa", Type.ERROR_MESSAGE);
				}
			}	
		});       
        btArquivoRemessa.setPrimaryStyleName("btSubMenu");
	
        l.addComponent(btArquivoRetorno);
        l.addComponent(btArquivoRetornoSicred);
       // l.addComponent(btArquivoRemessa);
    
 	}
	
	
	private void buildSubMenuImprimir(ClickEvent event) {
		winSubMenuImprimir = new Window("Escolha uma das Opções:");
        VerticalLayout l = new VerticalLayout();

        winSubMenuImprimir.setContent(l);
        winSubMenuImprimir.setWidth("300px");
        winSubMenuImprimir.addStyleName("notifications");
        winSubMenuImprimir.setClosable(false);
        winSubMenuImprimir.setResizable(false);
        winSubMenuImprimir.setDraggable(false);
        winSubMenuImprimir.setPositionX(event.getClientX() - event.getRelativeX());
        winSubMenuImprimir.setPositionY(event.getClientY() - event.getRelativeY());
        winSubMenuImprimir.setCloseShortcut(KeyCode.ESCAPE, null);
     
        Button btImprimirNormal = new Button("Sem código de barras", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				gerarCarneSemCodBarras();
			}
		});			      
        btImprimirNormal.setPrimaryStyleName("btSubMenu");
        
        Button btImprimirGerenciaNet = new Button("Com código de barras GerenciaNet", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				gerarCarne();
			}
		});
        btImprimirGerenciaNet.setPrimaryStyleName("btSubMenu");
        
        l.addComponent(btImprimirNormal);
        l.addComponent(btImprimirGerenciaNet);

 	}
	
	
	private void gerarCarneSemCodBarras(){
		
		try{
			
			Set<Object> selecteds = (Set<Object>)tb.getValue();
			if(selecteds.size() > 0){
				
				
				
				List<String> links= new ArrayList<>();
				EntityManager em = ConnUtil.getEntity();
				
				
				for (Object o: selecteds.toArray()) {
					
					if(tb.getItem(o).getItemProperty("Cod.").getValue() != null){
						String cod_boleto = tb.getItem(o).getItemProperty("Cod.").getValue().toString();					
						links.add("URLhttp://172.17.0.13/boletoSemCodigo/index.php?b="+cod_boleto);
					}
				}
				
				
				if(links.size() > 0){
					
					String urls = "";
					for (String s: links) {
						urls += s;
					}
					
					final String url = "http://172.17.0.13/boletoSemCodigo/montarBoletos.php?url="+urls;

					Window wVideo  = new Window("Imprimir boletos");
			        wVideo.setResizable(false); 
			        wVideo.setModal(true);
			        wVideo.center();
			        wVideo.setWidth("942px");
			        wVideo.setHeight("680px");
			        
			        wVideo.setContent(new VerticalLayout(){
			        	{
			        		setMargin(true);
			        		setSpacing(true);
			        		
			        		setWidth("940px");
					        setHeight("633px");
			        		
			        		String link = url;
			        		
			        		final Embedded video = new Embedded(null,new ExternalResource(link));
					        video.setType(Embedded.TYPE_BROWSER);
					        video.setSizeFull();

			        		addComponent(video);
			        		
			        	}
			        });
			        
			        getUI().addWindow(wVideo);
					
					
					
					refresh(tfBusca.getValue());
					
				}else{
					Notify.Show("Não foi possível imprimir nenhum boleto!", Notify.TYPE_ERROR);
				}
				
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	
//Backup Função GerarCarne	
	private void gerarCarne(){
		
		try{
			
			Set<Object> selecteds = (Set<Object>)tb.getValue();
			if(selecteds.size() > 0){
				
				List<String> links= new ArrayList<>();
				EntityManager em = ConnUtil.getEntity();
				
				
				for (Object o: selecteds.toArray()) {
					if(tb.getItem(o).getItemProperty("Cod.").getValue() != null){
					String codCliente = tb.getItem(o).getItemProperty("Cod.").getValue().toString();
					ContasReceber boleto = ContasReceberDAO.find(Integer.parseInt(codCliente));
					
					if(boleto.getTransacao_gerencianet() != null && !boleto.getTransacao_gerencianet().equals("")){
						
						if(boleto.getN_numero_gerencianet() != null && !boleto.getN_numero_gerencianet().equals("")){
							links.add("URLhttp://172.17.0.13/boletoNovo/boletoUnico.php?url2="+boleto.getN_numero_gerencianet());
						}else{
							//Pegar a URL
							String detalhes = detalharTransacao(boleto.getTransacao_gerencianet());
							if(detalhes != null && !detalhes.equals("")){
								em.getTransaction().begin();
								boleto.setN_numero_gerencianet(detalhes);
								em.merge(boleto);
								em.getTransaction().commit();
								
								links.add("URLhttp://172.17.0.13/boletoNovo/boletoUnico.php?url2="+boleto.getN_numero_gerencianet());
							}
						}
					}else{
						
							boolean atrasado = false;
							double valor_corrigido = 0;
							if(new DateTime(boleto.getData_vencimento()).isBeforeNow()){
								atrasado = true;								
								valor_corrigido = calcularAtraso(boleto.getId());								
							}
					
							SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");							
							String descricao = "Boleto";           
				            
							String valor = boleto.getValor_titulo().replace(",", "").replace(".", "");	
				            if(atrasado){
				            	valor = Real.formatDbToString(String.valueOf(valor_corrigido)).replace(",", "").replace(".", "");
				            }
				            
				            String nome_cliente =retirarAcentos(boleto.getCliente().getNome_razao());            
				            String cpf = boleto.getCliente().getDoc_cpf_cnpj();
				            String telefone = boleto.getCliente().getDdd_fone1()+""+boleto.getCliente().getTelefone1();
				            String email = boleto.getCliente().getEmail() != null ? boleto.getCliente().getEmail() : "";            
				            String vencimento = sdf.format(boleto.getData_vencimento());
				            String codBoleto = boleto.getId().toString();
				            
				            String url = "http://172.17.0.13/boleto/api.php";
		
				            HttpURLConnection httpClient = (HttpURLConnection) new URL(url).openConnection();
		
				            //add reuqest header
				            httpClient.setRequestMethod("POST");
				            httpClient.setRequestProperty("User-Agent", "Mozilla/5.0");
				            httpClient.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
				            
				            String codContrato = "";
				            if(ContasReceberDAO.ChecarPadraoAcessoNDoc(boleto.getN_doc())){
				            	codContrato = boleto.getN_doc().split("/")[0].toString();	
				            }
		
				            String urlParameters = "funcao=gerarBoletos&"
				            		+ "descricao="+descricao+"&"
				            		+ "valor="+valor+"&"
				            		+ "quantidade=1&"
				            		+ "nome_cliente="+nome_cliente+"&"
				            		+ "cpf="+cpf+"&"
				            		+ "telefone="+telefone+"&"
				            		+ "vencimento="+vencimento+"&"
				            		+ "codBoleto="+codBoleto+"&"
				            		+ "email="+email+"&"
				            		+ "codContrato="+codContrato;
	        
				            httpClient.setDoOutput(true);
				            try (DataOutputStream wr = new DataOutputStream(httpClient.getOutputStream())) {
				                wr.writeBytes(urlParameters);
				                wr.flush();
				            }

				            try (BufferedReader in = new BufferedReader(
				                    new InputStreamReader(httpClient.getInputStream()))) {
		
				                String line;
				                StringBuilder response = new StringBuilder();
		
				                while ((line = in.readLine()) != null) {
				                    response.append(line);
				                }
		
				                System.out.println(response.toString());
				                
				                org.json.JSONObject json = new org.json.JSONObject(response.toString());		                
				    	        org.json.JSONObject jsonData = new org.json.JSONObject(json.get("data").toString());
				    	         
				    	        String link = jsonData.get("link").toString();
				    	        String transacao = jsonData.get("charge_id").toString();
				    	        
				    	        System.out.println(link);
				    	        System.out.println(transacao);
		
				    	        boleto.setN_numero_gerencianet(link); 
				    	        boleto.setTransacao_gerencianet(transacao);
				    	        
				    	        em.getTransaction().begin();
				    	        em.merge(boleto);
				    	        em.getTransaction().commit();
				    	        
				    	        links.add("URLhttp://172.17.0.13/boletoNovo/boletoUnico.php?url2="+boleto.getN_numero_gerencianet());
				    	        
				            }
		            
		            
					}
				}
				}
				
				
				if(links.size() > 0){
					
					String urls = "";
					for (String s: links) {
						urls += s;
					}
					
					final String url = "http://172.17.0.13/boletoNovo/pegaCodigoHtml2.php?url="+urls;

					Window wVideo  = new Window("Imprimir boletos");
			        wVideo.setResizable(false); 
			        wVideo.setModal(true);
			        wVideo.center();
			        wVideo.setWidth("942px");
			        wVideo.setHeight("680px");
			        
			        wVideo.setContent(new VerticalLayout(){
			        	{
			        		setMargin(true);
			        		setSpacing(true);
			        		
			        		setWidth("940px");
					        setHeight("633px");
			        		
			        		String link = url;
			        		
			        		final Embedded video = new Embedded(null,new ExternalResource(link));
					        video.setType(Embedded.TYPE_BROWSER);
					        video.setSizeFull();

			        		addComponent(video);
			        		
			        	}
			        });
			        
			        getUI().addWindow(wVideo);
					
					
					
					refresh(tfBusca.getValue());
					
				}else{
					Notify.Show("Não foi possível imprimir nenhum boleto!", Notify.TYPE_ERROR);
				}
				
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private ContaBancaria getContaBancaria(Integer codContaReceber){
		EntityManager em = ConnUtil.getEntity();
		
		
		String controle =  ContasReceberDAO.find(codContaReceber).getControle();
		Query qControle = em.createQuery("select c from ControleTitulo c where c.nome=:nome and c.empresa_id =:empresa", ControleTitulo.class);
		qControle.setParameter("nome", controle);
		qControle.setParameter("empresa", OpusERP4UI.getEmpresa().getId());
		
		ContaBancaria contaBancaria = ((ControleTitulo)qControle.getSingleResult()).getConta_bancaria();
		
		return contaBancaria;
	}
	
	private double calcularAtraso(Integer codContaReceber){
		
		ContasReceber boleto = ContasReceberDAO.find(codContaReceber);
		PlanoAcesso plano = boleto != null && boleto.getPlano_contrato() != null ? PlanoAcessoDAO.find(boleto.getPlano_contrato()) : null; 
		String valorTitulo = boleto.getValor_titulo();

		ContaBancaria contaBancaria = getContaBancaria(codContaReceber);
		
		if(contaBancaria != null){
			
			DateTime dt1 = new DateTime(boleto.getData_vencimento());
			DateTime dt2 = new DateTime(new Date());
	 
			Integer dias = Days.daysBetween(dt1, dt2).getDays();
			Integer meses = Months.monthsBetween(dt1, dt2).getMonths();
			
			Double juros;
			Double multa;
			double taxa_negativacao;
			double  atualizacao_monetaria;
			
			Double jurosTotais;
			if(dias > Integer.parseInt(contaBancaria.getTolerancia_juros_multa()))
			{
				juros = ((Double.parseDouble(contaBancaria.getJuros()) * dias));
				multa = Double.parseDouble(contaBancaria.getMulta());					
				
//				tfDesconto.setReadOnly(false);
//				tfDesconto.setValue("0,00");
//				tfDesconto.setReadOnly(true);
			}else{
				juros = 0.00;
				multa = 0.00;
			}
			
			if(dias > 364){
				taxa_negativacao = contaBancaria.getTaxa_negativacao();
				double taxa_monetaria = contaBancaria.getAtualizacao_monetaria() * meses;
				double vlr_taxa_monetraria = Real.formatStringToDBDouble(boleto.getValor_titulo()) * taxa_monetaria / 100;					
				atualizacao_monetaria = vlr_taxa_monetraria;
				
				if(!boleto.getStatus().equals("NEGATIVADO")){
					taxa_negativacao = 0;
				}						
			}else{
				atualizacao_monetaria = 0.00;
				taxa_negativacao = 0.00;
			}
			
			jurosTotais = juros + multa;
			
			Float valorDocTotal = Float.parseFloat(Real.formatStringToDB(valorTitulo)) + Float.parseFloat(Real.formatStringToDB(contaBancaria.getTaxa_boleto()));
			Double acrescimo = jurosTotais * valorDocTotal/100;
			
			Double vlrJuros1 = Double.parseDouble(juros.toString()) * Double.parseDouble(Real.formatStringToDB(valorTitulo))/100;
			Double vlrJuros = vlrJuros1;
			
			Double vlrMulta1 = Double.parseDouble(multa.toString()) * Double.parseDouble(Real.formatStringToDB(valorTitulo))/100;				
			Double vlrMulta = vlrMulta1;
			
			Double novoValorDoc =  valorDocTotal + acrescimo;
			
//			tfDiasEmAtraso.setReadOnly(false);
//			tfDiasEmAtraso.setValue(dias > 0 ? dias.toString() : "0");
//			tfDiasEmAtraso.setReadOnly(true);
//			
//			tfJuros.setReadOnly(false);
			
			Double jurosFinal = juros;
			Double vlrJurosArredondado = vlrJuros;
//			tfJuros.setValue(Real.formatDbToString(vlrJurosArredondado.toString()));
//			tfJuros.setReadOnly(true);
//			
//			tfMulta.setReadOnly(false);
//			tfMulta.setValue(Real.formatDbToString(vlrMulta.toString()));
//			tfMulta.setReadOnly(true);				
			
			EntityManager em = ConnUtil.getEntity();
			Query qPb = em.createQuery("select pb from ParametrosBoleto pb where pb.cliente_id = :codCliente", ParametrosBoleto.class);
			qPb.setParameter("codCliente",boleto.getCliente().getId());			
			ParametrosBoleto pb = null;
			boolean cobrarTaxa = true;
			if(qPb.getResultList().size() > 0){
				pb = (ParametrosBoleto) qPb.getSingleResult();
				
				if(!pb.getCobrar_taxa_bancaria()){
					cobrarTaxa = false;
				}
			}
			
			boolean taxBoleto =plano != null  &&  plano.getTaxa_boleto().equals("NAO")  ? false : true;		
			if(!cobrarTaxa && taxBoleto){
				taxBoleto = !taxBoleto;
			}
			
			if(!taxBoleto){
//				tfTaxa.setReadOnly(false);
//				tfTaxa.setValue("0,00");
//				tfTaxa.setReadOnly(true);				
			}
			
			Double taxaBoleto = taxBoleto ?  Double.parseDouble(contaBancaria.getTaxa_boleto()) : 0;
			Double valorFinal = Double.parseDouble(Real.formatStringToDB(valorTitulo)) + taxaBoleto  + vlrJuros1 + vlrMulta1 + taxa_negativacao + atualizacao_monetaria;
							
			//Double desconto = tfDesconto.getValue() != null && !tfDesconto.getValue().equals("") && !tfDesconto.getValue().equals("0,00") ? Real.formatStringToDBDouble(tfDesconto.getValue()) : 0;
			
			
			return valorFinal;
			
//			tfValorPagmento.setValue(Real.formatDbToString(valorFinal1.toString()));			
//			
//			txtTaxaNegativacao.setReadOnly(false);
//			txtTaxaNegativacao.setValue(Real.formatDbToString(String.valueOf(taxa_negativacao)));
//			txtTaxaNegativacao.setReadOnly(true);
//							
//			txtValorAtualizacaoMonetaria.setReadOnly(false);
//			txtValorAtualizacaoMonetaria.setValue(Real.formatDbToString(String.valueOf(atualizacao_monetaria)));
//			txtValorAtualizacaoMonetaria.setReadOnly(true);
//			
//			lbJuros.setValue(contaBancaria.getJuros()+"% ao dia");
//			lbMulta.setValue(Real.formatDbToString(contaBancaria.getMulta())+"%");
//			lbAtualizacaoMonetaria.setValue(Real.formatDbToString(String.valueOf(contaBancaria.getAtualizacao_monetaria()))+"% ao mês");

			
		}
		
		return 0;
		
		
	}
	
	private String retirarAcentos(String s){
				
		s = s.replaceAll(",","");
		
		s = s.replaceAll("Ç","C");
		s = s.replaceAll("É","E");
		s = s.replaceAll("Í","I");
		s = s.replaceAll("Ó","O");
		s = s.replaceAll("Ú","U");
		s = s.replaceAll("Á","A");
		
		s = s.replaceAll("Ã","A");
		s = s.replaceAll("Õ","O");
		
		
		return s;
	}
	
	private boolean marcarTransacaoComoPago(String transacao) {
		
		if(transacao != null && !transacao.equals("")){
		
				try{
					
		            String url = "http://172.17.0.13/boleto/marcarComoPago.php";

		            HttpURLConnection httpClient = (HttpURLConnection) new URL(url).openConnection();

		            //add reuqest header
		            httpClient.setRequestMethod("POST");
		            httpClient.setRequestProperty("User-Agent", "Mozilla/5.0");
		            httpClient.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		            
		            
		            String urlParameters = "cod="+Integer.parseInt(transacao);
		            		
		            httpClient.setDoOutput(true);
		            try (DataOutputStream wr = new DataOutputStream(httpClient.getOutputStream())) {
		                wr.writeBytes(urlParameters);
		                wr.flush();
		            }

            		
		            try (BufferedReader in = new BufferedReader(
		                    new InputStreamReader(httpClient.getInputStream()))) {

		                String line;
		                StringBuilder response = new StringBuilder();

		                while ((line = in.readLine()) != null) {
		                    response.append(line);
		                }

		                if(response.toString() != null && !response.toString().equals("")){
		                	org.json.JSONObject json = new org.json.JSONObject(response.toString());    
		                	System.out.println(json);
		                }
		                
		                if(httpClient.getResponseCode() == 200){
		                	
		                	boolean check = ContasReceberDAO.retirarTransacaoBoleto(transacao);
		                	
		                	if(check){
		                		Notify.Show("Boleto cancelado com Sucesso!", Notify.TYPE_SUCCESS);
		                	}else{
		                		Notify.Show("Transação não encontrada no banco de dados..", Notify.TYPE_WARNING);
		                	}
		                }
		            }
					
					
					
					return true;
						        
					    
			        
			        
					
					}catch(Exception e){
						e.printStackTrace();
						return false;
					}
		}
		
		return false;
    }
	
	private boolean alterarVenciementoGerenciaNet(String transacao, Date novaData) {
		
		if(transacao != null && !transacao.equals("")){
		
				try{
					
		            String url = "http://172.17.0.13/boleto/alterarVencimento.php";

		            HttpURLConnection httpClient = (HttpURLConnection) new URL(url).openConnection();

		            //add reuqest header
		            httpClient.setRequestMethod("POST");
		            httpClient.setRequestProperty("User-Agent", "Mozilla/5.0");
		            httpClient.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		            
		            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		            String dtnova = sdf.format(novaData);
		            
		            String urlParameters = 
		            		"cod="+Integer.parseInt(transacao)+"&"+
		            		"vencimento="+dtnova;
		            		
		            httpClient.setDoOutput(true);
		            try (DataOutputStream wr = new DataOutputStream(httpClient.getOutputStream())) {
		                wr.writeBytes(urlParameters);
		                wr.flush();
		            }

            		
		            try (BufferedReader in = new BufferedReader(
		                    new InputStreamReader(httpClient.getInputStream()))) {

		                String line;
		                StringBuilder response = new StringBuilder();

		                while ((line = in.readLine()) != null) {
		                    response.append(line);
		                }

		             //   if(response.toString() != null && !response.toString().equals("")){
		                	//org.json.JSONObject json = new org.json.JSONObject(response.toString());    
		                	//System.out.println(json);
		               // }
		                
		                if(httpClient.getResponseCode() == 200){
		                	
		                	Notify.Show("Vencimento alterado no GerenciaNet!", Notify.TYPE_SUCCESS);
		                	
		                }
		            }
					
					
					
					return true;
						        
					    
			        
			        
					
					}catch(Exception e){
						e.printStackTrace();
						return false;
					}
		}
		
		return false;
    }
	
	private boolean detalhesTransacao(String transacao) {
		
		if(transacao != null && !transacao.equals("")){
			
			try{
				
	            String url = "http://172.17.0.13/boleto/detalhesTransacao.php";

	            HttpURLConnection httpClient = (HttpURLConnection) new URL(url).openConnection();

	            //add reuqest header
	            httpClient.setRequestMethod("POST");
	            httpClient.setRequestProperty("User-Agent", "Mozilla/5.0");
	            httpClient.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
	            
	            String urlParameters = "cod="+Integer.parseInt(transacao);
	            		
	            httpClient.setDoOutput(true);
	            try (DataOutputStream wr = new DataOutputStream(httpClient.getOutputStream())) {
	                wr.writeBytes(urlParameters);
	                wr.flush();
	            }

        		
	            try (BufferedReader in = new BufferedReader(new InputStreamReader(httpClient.getInputStream()))) {

	                String line;
	                StringBuilder response = new StringBuilder();

	                while ((line = in.readLine()) != null) {
	                    response.append(line);
	                }

	                if(response.toString() != null && !response.toString().equals("")){
	                	JSONObject json = new JSONObject(response.toString());
	                	
	                	final String vencimento = json.getJSONObject("data").getJSONObject("payment").getJSONObject("banking_billet").get("expire_at").toString();
	                	final String codigo_barras = json.getJSONObject("data").getJSONObject("payment").getJSONObject("banking_billet").get("barcode").toString();
	                	final String criado = json.getJSONObject("data").getJSONObject("payment").get("created_at").toString();
	                	final String status = json.getJSONObject("data").get("status").toString();
	                			                	
	                	final JSONArray arrayHistory = json.getJSONObject("data").getJSONArray("history");
	                	
//	                	System.out.println("Vencimento:"+vencimento);
//	                	System.out.println("Código Barras:"+codigo_barras);
//	                	System.out.println("Criado:"+criado);
//	                	System.out.println("Status:"+status);
	                	
	                	
	                	
	                	
	                	Window janelaDetalhe = new Window("Detalhes", new VerticalLayout(){
	                		{
	                			setSizeFull();     				                			
	                			setMargin(true);
	                			addStyleName("border-form");
	                			
	                			{
	                				addComponent(new FormLayout(){					
	                					{
	                						
	                						setMargin(true);
	                						setSpacing(true);
	                						addStyleName("form-cutom");						
	                									
	                						TextField txtDataCadastro = new TextField("Data de cadastro");
	                						txtDataCadastro.setValue(criado);
	                						txtDataCadastro.setEnabled(false);
	                						
	                						addComponent(txtDataCadastro);
	                						
	                						TextField txtVencimento = new TextField("Data de vencimento");
	                						txtVencimento.setValue(vencimento);
	                						txtVencimento.setEnabled(false);
	                						
	                						addComponent(txtVencimento);
	                							                						
	                						TextField txtCodBarras = new TextField("Código de barras");
	                						txtCodBarras.setValue(codigo_barras);
	                						txtCodBarras.setEnabled(false);
	                						
	                						addComponent(txtCodBarras);
	                						
	                						TextField txtStatus = new TextField("Status");
	                						txtStatus.setValue(status); 
	                						txtStatus.setEnabled(false);
	                						
	                						addComponent(txtStatus);
	                						
	                						Table tbHistory = new Table();
	                						tbHistory.setWidth("634px");
	                						tbHistory.setHeight("269px");
	                						
	                						tbHistory.addContainerProperty("Mensagem", String.class, "");
	                						tbHistory.addContainerProperty("Data", String.class, "");
	                						
	                						tbHistory.setVisibleColumns(new Object[]{"Mensagem","Data"}); 
	                						
	                						for(int i=0; i<arrayHistory.length(); i++){
	                							
	                	                		String msg = arrayHistory.getJSONObject(i).get("message").toString();
	                	                		String data = arrayHistory.getJSONObject(i).get("created_at").toString();
	                	                		
	                	                		tbHistory.addItem(new Object[]{msg,data}, tbHistory.getItemIds().size()+1);
	                	                		
	                	                	}
	                						
	                						addComponent(tbHistory);
	                						
	                					}
	                				});
	                			}
	                		}
	                	});
	                	
	                	
	                	janelaDetalhe.setWidth("817px");
	                	janelaDetalhe.setHeight("452px");
	                	
	                	janelaDetalhe.setModal(true);
	                	janelaDetalhe.center();
	                	getUI().addWindow(janelaDetalhe); 
	                	
	                }               
	            }
				
				return true;
				
				}catch(Exception e){
					e.printStackTrace();
					return false;
				}
	}
		
		return false;
    }
	
	
	private boolean cancelarTransacaoSemTransacao(String transacao) {
		
		if(transacao != null && !transacao.equals("")){
		
				try{
					
		            String url = "http://172.17.0.13/boleto/testeCancelamentoTransacao.php";

		            HttpURLConnection httpClient = (HttpURLConnection) new URL(url).openConnection();

		            //add reuqest header
		            httpClient.setRequestMethod("POST");
		            httpClient.setRequestProperty("User-Agent", "Mozilla/5.0");
		            httpClient.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		            
		            
		            String urlParameters = "cod="+Integer.parseInt(transacao);
		            		
		            httpClient.setDoOutput(true);
		            try (DataOutputStream wr = new DataOutputStream(httpClient.getOutputStream())) {
		                wr.writeBytes(urlParameters);
		                wr.flush();
		            }

            		
		            try (BufferedReader in = new BufferedReader(new InputStreamReader(httpClient.getInputStream()))) {

		                String line;
		                StringBuilder response = new StringBuilder();

		                while ((line = in.readLine()) != null) {
		                    response.append(line);
		                }
		                if(response.toString() != null && !response.toString().equals("")){
		                	org.json.JSONObject json = new org.json.JSONObject(response.toString());    
		                	System.out.println(json);
		                }		                
		                if(httpClient.getResponseCode() == 200){
		                	
		                	boolean check = ContasReceberDAO.retirarTransacaoBoletoSemTrasaction(transacao);
		                	
		                	if(check){
		                		Notify.Show("Boleto cancelado com Sucesso!", Notify.TYPE_SUCCESS);
		                	}else{
		                		Notify.Show("Transação não encontrada no banco de dados..", Notify.TYPE_WARNING);
		                	}
		                }
		            }
					
					
					
					return true;
						        
					    
			        
			        
					
					}catch(Exception e){
						e.printStackTrace();
						return false;
					}
		}
		
		return false;
    }
	
	private boolean cancelarTransacao(String transacao) {
		
		if(transacao != null && !transacao.equals("")){
		
				try{
					
		            String url = "http://172.17.0.13/boleto/testeCancelamentoTransacao.php";

		            HttpURLConnection httpClient = (HttpURLConnection) new URL(url).openConnection();

		            //add reuqest header
		            httpClient.setRequestMethod("POST");
		            httpClient.setRequestProperty("User-Agent", "Mozilla/5.0");
		            httpClient.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		            
		            
		            String urlParameters = "cod="+Integer.parseInt(transacao);
		            		
		            httpClient.setDoOutput(true);
		            try (DataOutputStream wr = new DataOutputStream(httpClient.getOutputStream())) {
		                wr.writeBytes(urlParameters);
		                wr.flush();
		            }

            		
		            try (BufferedReader in = new BufferedReader(
		                    new InputStreamReader(httpClient.getInputStream()))) {

		                String line;
		                StringBuilder response = new StringBuilder();

		                while ((line = in.readLine()) != null) {
		                    response.append(line);
		                }

		                if(response.toString() != null && !response.toString().equals("")){
		                	org.json.JSONObject json = new org.json.JSONObject(response.toString());    
		                	System.out.println(json);
		                }
		                
		                if(httpClient.getResponseCode() == 200){
		                	
		                	boolean check = ContasReceberDAO.retirarTransacaoBoleto(transacao);
		                	
		                	if(check){
		                		Notify.Show("Boleto cancelado com Sucesso!", Notify.TYPE_SUCCESS);
		                	}else{
		                		Notify.Show("Transação não encontrada no banco de dados..", Notify.TYPE_WARNING);
		                	}
		                }
		            }
					
					
					
					return true;
						        
					    
			        
			        
					
					}catch(Exception e){
						e.printStackTrace();
						return false;
					}
		}
		
		return false;
    }
	
	private String detalharTransacao(String transacao) {
		
		try{
		String url = "https://digitalonline.com.br/2viaEmissaoVariosBoletos/boleto/api.php";
		
        HttpsURLConnection httpClient = (HttpsURLConnection) new URL(url).openConnection();

        //add reuqest header
        httpClient.setRequestMethod("POST");
        httpClient.setRequestProperty("User-Agent", "Mozilla/5.0");
        httpClient.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

        String urlParameters = 
        		"funcao=detalharTransacao&"
        		+ "transacao="+transacao;
        
        httpClient.setDoOutput(true);
        try (DataOutputStream wr = new DataOutputStream(httpClient.getOutputStream())) {
            wr.writeBytes(urlParameters);
            wr.flush();
        }

		
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(httpClient.getInputStream()))) {

            String line;
            StringBuilder response = new StringBuilder();

            while ((line = in.readLine()) != null) {
                response.append(line);
            }

            org.json.JSONObject json = new org.json.JSONObject(response.toString());		                
	        org.json.JSONObject jsonData = new org.json.JSONObject(json.get("data").toString());
	         
	        String retorno = new org.json.JSONObject(new org.json.JSONObject(jsonData.get("payment").toString()).get("banking_billet").toString()).get("link").toString();  
	    
	        return retorno;	        
        }
        
        
		
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

	private void buildSubMenuLogs(ClickEvent event) {
		winSubMenuLog = new Window("Escolha uma das Opções:");
        VerticalLayout l = new VerticalLayout();

        winSubMenuLog.setContent(l);
        winSubMenuLog.setWidth("300px");
        winSubMenuLog.addStyleName("notifications");
        winSubMenuLog.setClosable(false);
        winSubMenuLog.setResizable(false);
        winSubMenuLog.setDraggable(false);
        winSubMenuLog.setPositionX(event.getClientX() - event.getRelativeX());
        winSubMenuLog.setPositionY(event.getClientY() - event.getRelativeY());
        winSubMenuLog.setCloseShortcut(KeyCode.ESCAPE, null);

        
        
        Button btLog1 = new Button("Log", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(tb.getValue() != null){					
					
					
					
					Set<Object> selecteds = (Set<Object>)tb.getValue();
					if(selecteds.size() == 1 && tb.getItem(selecteds.toArray()[0]).getItemProperty("Cod.").getValue() != null){
											
					Integer codContaReceber = Integer.parseInt(tb.getItem(selecteds.toArray()[0]).getItemProperty("Cod.").getValue().toString());						

					HistoricoDeLog historicoAlteracoes = new HistoricoDeLog(true, true,codContaReceber);
					
					getUI().addWindow(historicoAlteracoes);
				  }
				}
			}	
		});       
        btLog1.setPrimaryStyleName("btSubMenu");
        
        btLog2 = new Button("Erros", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				checarBaixas();
				
				Window winErros = new Window("Erros de Baixa");
				winErros.setWidth("552px");
				winErros.setHeight("207px");
				winErros.setClosable(true);
				winErros.setModal(true);
				winErros.center();
				
				
				Table tb1 = new Table();
				tb1.setSizeFull();
				tb1.addContainerProperty("Cod. Boleto", Integer.class, null);
				tb1.addContainerProperty("Cliente", String.class, null);
				tb1.setColumnWidth("Cod. Boleto", 120);
				
				for (Integer codBoleto : boletosComFalhaDeBaixa) {
					EntityManager em = ConnUtil.getEntity();
					ContasReceber C = em.find(ContasReceber.class, codBoleto);
					
					tb1.addItem(new Object[]{C.getId(), C.getCliente().getNome_razao()}, tb1.getItemIds().size()+1);
				}
				
				
				winErros.setContent(tb1);				
				getUI().addWindow(winErros);
				
			}	
		});       
        btLog2.setPrimaryStyleName("btSubMenu");
        
       
	
        l.addComponent(btLog1);
        l.addComponent(btLog2);
    
 	}
	
	Button btLog2;
	
	private Component BuildbtArquivos() {
		btArqRetorno = new Button("Arquivo", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (winSubMenuArquivo != null && winSubMenuArquivo.getUI() != null)
					winSubMenuArquivo.close();
	             else {
	            	 
	            	 fecharTodosSubMenusMenos(winSubMenuArquivo);
	             
				     buildSubMenuArquivo(event);
				     
					 getUI().addWindow(winSubMenuArquivo);
					 winSubMenuArquivo.focus();
	                 ((CssLayout) getUI().getContent()).addLayoutClickListener(new LayoutClickListener() {
	                             
	               			@Override
	                        public void layoutClick(LayoutClickEvent event) {
	               				winSubMenuArquivo.close();
	                            ((CssLayout) getUI().getContent()).removeLayoutClickListener(this);
	                        }
	                 });
	             }					
			}
		});
		
		return btArqRetorno;
	}
	
	private Button BuildbtImprimirRecibo(){
		btImprimirRecibo = new Button("Recibo", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				
					final Set<Object> selecteds = (Set<Object>)tb.getValue();
					if(selecteds.size() == 1){
						
						if(tb.getItem(selecteds.toArray()[0]).getItemProperty("Cod.").getValue() != null){
								final Integer codContaReceber = Integer.parseInt(tb.getItem(selecteds.toArray()[0]).getItemProperty("Cod.").getValue().toString());			
								ultimoBoletoSelecionado = codContaReceber;
												
								ImprimirRecibo(ultimoBoletoSelecionado);
								
								GenericDialog cd2 = new GenericDialog("Confirme para Continuar!", "Re-Imprimir ?", true, true);
								cd2.addListerner(new GenericDialog.DialogListerner() {
									
									@Override
									public void onClose(DialogEvent event) {
										if(event.isConfirm()){
											ImprimirRecibo(ultimoBoletoSelecionado);
										}
									}
								});
								
								getUI().addWindow(cd2);
						}
					}else{
						Notify.Show("Selecione um boleto", Notify.TYPE_WARNING);
					}
			
			}
		});
		btImprimirRecibo.setEnabled(false);	
		
		return btImprimirRecibo;
	}
	
	private void ImprimirRecibo(Integer codBoleto){
		if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Imprimir Recibo"))				
		{					
	
				if(OpusERP4UI.getEmpresa().getId().equals(4)){
					try{
						InputStream stream = new ByteArrayInputStream(ContasReceberDAO.getLayoutRecibo(ContasReceberDAO.find(codBoleto)).getBytes());  
									        	   
						
						String urlPrint = "";
						if(!OpusERP4UI.getEmpresa().getId().equals(3)){
							urlPrint = "http://192.168.20.145:631/printers/BEMA_PRODUCAO";
						}else{
							urlPrint = "http://192.168.20.145:631/printers/BEMA_GRAFICA";
						}
						
						CupsPrinter printer = new CupsPrinter(new URL(urlPrint), "BEMA_PRODUCAO", true);		        	    
						
						HashMap<String, String> attributes = new HashMap<String, String>();	    	   
							        	    		        	    		        	    
						PrintJob printJob = new PrintJob.Builder(stream)
													.jobName("OpusERP4")
													.userName(OpusERP4UI.getUsuarioLogadoUI().getUsername())
													.copies(1)  	        	    							
													.build();
						
						PrintRequestResult printRequestResult = printer.print(printJob);	
						
						if(printRequestResult.isSuccessfulResult()){
							Notification.show("Recibo Esta Sendo Impresso");
						}else{
							Notification.show("ERRO","Houve um Problema ao Tentar Imprimir, Espere um Momento e Tente Novamente", Type.ERROR_MESSAGE);
						}
						
						LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Imprimiu um Recibo de Um Boleto"));
						AlteracoesContasReceberDAO.save(new AlteracoesContasReceber(null, "IMPRIMIU RECIBO DE UM BOLETO", ContasReceberDAO.find(codBoleto),OpusERP4UI.getEmpresa(), OpusERP4UI.getUsuarioLogadoUI(), new Date()));
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			
				
				if(OpusERP4UI.getEmpresa().getId().equals(1) || OpusERP4UI.getEmpresa().getId().equals(3) || OpusERP4UI.getEmpresa().getId().equals(5)
						|| OpusERP4UI.getEmpresa().getId().equals(6)){
					try{
						
						ContasReceber c = ContasReceberDAO.find(codBoleto);
						SimpleDateFormat formHora= new SimpleDateFormat("hh:mm:ss");
						SimpleDateFormat formHoraArq= new SimpleDateFormat("hhmmss");
						
						String nomeArquivo = "/DJMONITOR/RECIBO"+formHoraArq.format(new Date())+".txt";
						File f = new File(nomeArquivo);
						//File f = new File("C:\\RECIBOS\\RECIBO"+formHoraArq.format(new Date())+".txt");

						BufferedWriter br = new BufferedWriter(new FileWriter(f));  
						 					
						if(f.canWrite()){
						
							StringBuilder s = new StringBuilder();
							String newLine = System.getProperty("line.separator");
								
						    //-INICIO---REGISTRO PRE		
							SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyyHHmmss");
							
								
//							s.append("                         d i g i t a l "+newLine);
//							s.append("   Rua Adjar Maciel, 35 Centro Belo Jardim/PE CEP: 55.150-040"+newLine);
//							s.append("        CNPJ: 07.578.965/0001-05     IE: 18.3.050.0329"+newLine);
							
							if(OpusERP4UI.getEmpresa().getId().equals(1) || OpusERP4UI.getEmpresa().getId().equals(3)){
									s.append("                          d i g i t a l");
									s.append(newLine);
									s.append("      Rua Adjar Maciel, 35 Centro Belo Jardim/PE CEP: 55.150-040");
									s.append(newLine);					
									s.append("            CNPJ: 07.578.965/0001-05 IE: 18.3.050.0329");
									s.append(newLine);
							}else if(OpusERP4UI.getEmpresa().getId().equals(5)){
									s.append("                   E S P A C O D I G I T A L");
									s.append(newLine);
									s.append("  Rua Padre Cicero, 07 Sao Pedro Belo Jardim/PE CEP: 55.150-340");
									s.append(newLine);
									s.append("             CNPJ: 33.056.081/0001-09 IE: 081948581");
									s.append(newLine);
							}
																									
							s.append(newLine);
							
							s.append("                      RECIBO DE PAGAMENTO"+newLine);
							
							
							String hora = formHora.format(new Date());
							
							SimpleDateFormat form = new SimpleDateFormat("dd/MM/yyyy");
							String hoje = form.format(new Date());
							String vencimento = form.format(c.getData_vencimento());
							String pagamento = "";
							
							if(c.getData_pagamento() != null){
								pagamento = form.format(c.getData_pagamento());
							}
							
							s.append("----------------------------------------------------------------"+newLine);
							s.append("                EMISSAO: "+hoje+" HORA: "+hora+ ""+newLine);											
							s.append("----------------------------------------------------------------"+newLine);			
							s.append("Cliente..: "+c.getCliente().getNome_razao());			
							s.append(newLine);	
							s.append("Cod Boleto: "+c.getId().toString());
							s.append(newLine);
							s.append("N. Doc.: "+c.getN_doc());
							s.append(newLine);
							s.append("N. Numero.: "+c.getN_numero());
							s.append(newLine);
							s.append("Data Venc.: "+vencimento+", "+"Data Pag..: "+pagamento);									
							s.append(newLine);
							s.append("Valor.: "+c.getValor_titulo()+", "+"Valor Pgto.: "+c.getValor_pagamento());								
							s.append(newLine);
							s.append("Forma Pgto.:"+c.getForma_pgto()+", "+"Tipo.: "+c.getTipo_baixa());
							s.append(newLine);									
							s.append("----------------------------------------------------------------"+newLine);
							
							
							
							br.write(s.toString());  
							br.close();							

														
							//if(OpusERP4UI.getEmpresa().getId().equals(1)){
								ScpUtil.transferir("root", "192.168.21.13", "managerdigi_SB@18", nomeArquivo, "/dados/PRINT/DIGITAL/RECIBO"+formHoraArq.format(new Date())+".txt");
							//}else if(OpusERP4UI.getEmpresa().getId().equals(3)){
							//	ScpUtil.transferir("root", "192.168.21.13", "managerdigi_SB@18", nomeArquivo, "/dados/PRINT/DIGITALGRAFICA/RECIBO"+formHoraArq.format(new Date())+".txt");
							//}
							
							//f.delete();
						}else{
							Notify.Show("Não é Possivel Gravar o Arquivo", Notify.TYPE_ERROR);
						}
					}catch(Exception e){
						e.printStackTrace();
						Notify.Show("ERRO: "+e.getMessage(), Notify.TYPE_ERROR);
						Notify.Show("ERRO: "+e.getCause(), Notify.TYPE_ERROR);
					}
				
				}
			
		}else{				
			Notification.show("Atenção", "Você não Possui Permissão para Imprimir Recibos", Type.ERROR_MESSAGE);				
		}
		
		
	}
	
	Button btGerenciaNet;
	
	private void buildSubMenuGerenciaNet(ClickEvent event) {
		winSubMenuGerenciaNet = new Window("Escolha uma das Opções:");
        VerticalLayout l = new VerticalLayout();

        winSubMenuGerenciaNet.setContent(l);
        winSubMenuGerenciaNet.setWidth("300px");
        winSubMenuGerenciaNet.addStyleName("notifications");
        winSubMenuGerenciaNet.setClosable(false);
        winSubMenuGerenciaNet.setResizable(false);
        winSubMenuGerenciaNet.setDraggable(false);
        winSubMenuGerenciaNet.setPositionX(event.getClientX() - event.getRelativeX());
        winSubMenuGerenciaNet.setPositionY(event.getClientY() - event.getRelativeY());
        winSubMenuGerenciaNet.setCloseShortcut(KeyCode.ESCAPE, null);
     
        Button btGerenciaCancelarBoleto = new Button("Cancelar boleto", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, 
						OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Cancelar Transação GerenciaNet"))				
				{
				
					Set<Object> selecteds = (Set<Object>)tb.getValue();
					if(selecteds.size() > 0){
						
						for (Object o: selecteds.toArray()) {
						
							if(tb.getItem(o).getItemProperty("Transação GerenciaNet").getValue() != null){
									String codBoleto = tb.getItem(o).getItemProperty("Cod.").getValue().toString();
									ContasReceber boleto = ContasReceberDAO.find(Integer.parseInt(codBoleto));
								
									boolean check = cancelarTransacao(boleto.getTransacao_gerencianet());
									
									if(check){
										refresh(tfBusca.getValue());
									}
							}
							
							
						}
					}
				
				}else{
					Notification.show("Atenção", "Você não Possui Permissão para cancelar boletos.", Type.ERROR_MESSAGE);
				}
				
			}
		});			      
        btGerenciaCancelarBoleto.setPrimaryStyleName("btSubMenu");
        
        Button btGerenciaDetalheTransacao = new Button("Detalhe transação", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, 
						OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Detalhes Transação GerenciaNet"))				
				{
				
						Set<Object> selecteds = (Set<Object>)tb.getValue();
						if(selecteds.size() == 1){
							
							for (Object o: selecteds.toArray()) {
							
								if(tb.getItem(o).getItemProperty("Transação GerenciaNet").getValue() != null){
										
										String codBoleto = tb.getItem(o).getItemProperty("Cod.").getValue().toString();
										ContasReceber boleto = ContasReceberDAO.find(Integer.parseInt(codBoleto));
									
										detalhesTransacao(boleto.getTransacao_gerencianet());
								}																
							}
						}
				}else{
					Notification.show("Atenção", "Você não Possui Permissão para visualizar detalhes da transação", Type.ERROR_MESSAGE);
				}
			}
		});			      
        btGerenciaDetalheTransacao.setPrimaryStyleName("btSubMenu");
                
        l.addComponent(btGerenciaCancelarBoleto);
        l.addComponent(btGerenciaDetalheTransacao);

 	}
	
	
	public Button BuildbtGerenciaNet(){
		btGerenciaNet = new Button("GerenciaNet", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (winSubMenuGerenciaNet != null && winSubMenuGerenciaNet.getUI() != null)
					winSubMenuGerenciaNet.close();
	             else {
	            	 
	            	fecharTodosSubMenusMenos(winSubMenuGerenciaNet);
	            
				     buildSubMenuGerenciaNet(event);
				     
					 getUI().addWindow(winSubMenuGerenciaNet);
					 winSubMenuGerenciaNet.focus();
	                 ((CssLayout) getUI().getContent()).addLayoutClickListener(new LayoutClickListener() {
	                             
	               			@Override
	                        public void layoutClick(LayoutClickEvent event) {
	               				winSubMenuGerenciaNet.close();
	                            ((CssLayout) getUI().getContent()).removeLayoutClickListener(this);
	                        }
	                 });
	             }
			}

		});
		
		return btGerenciaNet;
	}
	private void fecharTodosSubMenusMenos(Window winSubMenu) {

		List<Window> subMenus = new ArrayList<>();
		subMenus.add(winSubMenuGerenciaNet);
		subMenus.add(winSubMenuArquivo);
		subMenus.add(winSubMenuImprimir);
		subMenus.add(winSubMenuLog);
		
		for (Window window : subMenus) {
			
			if(window != winSubMenu){
					
					if(window != null && window.getUI() != null){
						window.close();
					}
				
			}
		}
		
	}
	
	public Button BuildbtImprimir() {
		btImprimir= new Button("Imprimir", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if (winSubMenuImprimir != null && winSubMenuImprimir.getUI() != null)
					winSubMenuImprimir.close();
	             else {
	            	 
	            	 fecharTodosSubMenusMenos(winSubMenuImprimir); 
	            	 
				     buildSubMenuImprimir(event);
				     
					 getUI().addWindow(winSubMenuImprimir);
					 winSubMenuImprimir.focus();
	                 ((CssLayout) getUI().getContent()).addLayoutClickListener(new LayoutClickListener() {
	                             
	               			@Override
	                        public void layoutClick(LayoutClickEvent event) {
	               				winSubMenuImprimir.close();
	                            ((CssLayout) getUI().getContent()).removeLayoutClickListener(this);
	                        }
	                 });
	             }	
				
			}
		});
		btImprimir.setEnabled(false);
		return btImprimir;
	}
	
	private void imprimirBoletoNormal(){
		boolean filtroDataImprimir = true;
		if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Imprimir"))				
		{
			
			try {
										
				Set<Object> selecteds = (Set<Object>)tb.getValue();
				if(selecteds.size() > 0){
												
					EntityManager em = ConnUtil.getEntity();
						
					List<Boleto> boletosSelecionados = new ArrayList<>();
					for (Object o: selecteds.toArray()) {
						
						String n_numero2 =  tb.getItem(o).getItemProperty("N.Numero Sicred").getValue() != null ? tb.getItem(o).getItemProperty("N.Numero Sicred").getValue().toString() : null;
						if(n_numero2 != null && !n_numero2.substring(0, 4).equals("18/3") && !n_numero2.substring(0, 4).equals("19/3") && !n_numero2.substring(0, 4).equals("20/3")){
							filtroDataImprimir = false;
						}
						
						String controle =  tb.getItem(o).getItemProperty("Controle").getValue().toString();
						Query qControle = em.createQuery("select c from ControleTitulo c where c.nome=:nome and c.empresa_id =:empresa", ControleTitulo.class);
						qControle.setParameter("nome", controle);
						qControle.setParameter("empresa", OpusERP4UI.getEmpresa().getId());
														
						ContaBancaria cb = null;
//						if(qControle.getResultList().size() ==1){
//							cb = ((ControleTitulo)qControle.getSingleResult()).getConta_bancaria_bkp();
//						}
																											
						Integer anoEmissao = Integer.parseInt(tb.getItem(o).getItemProperty("Emissão").getValue().toString().substring(0, 4).toString());
						Integer mesEmissao = Integer.parseInt(tb.getItem(o).getItemProperty("Emissão").getValue().toString().substring(5, 7).toString());
						Integer diaEmissao = Integer.parseInt(tb.getItem(o).getItemProperty("Emissão").getValue().toString().substring(8, 10).toString());
						
						Integer anoVencimento = Integer.parseInt(tb.getItem(o).getItemProperty("Vencimento").getValue().toString().substring(0, 4).toString()); 
						Integer mesVencimento = Integer.parseInt(tb.getItem(o).getItemProperty("Vencimento").getValue().toString().substring(5, 7).toString()); 
						Integer diaVencimento = Integer.parseInt(tb.getItem(o).getItemProperty("Vencimento").getValue().toString().substring(8, 10).toString());
															
						String nossoNumero = "";
							
//							 	if(tb.getItem(o).getItemProperty("N.Numero").getValue() != null){
//							 		nossoNumero = tb.getItem(o).getItemProperty("N.Numero").getValue().toString();
//						        	
//						        	
//						        	if(tb.getItem(o).getItemProperty("Controle").getValue().toString().equals("ACESSO-PRE") || 
//						        			tb.getItem(o).getItemProperty("Controle").getValue().toString().equals("ACESSO-POS") || 
//						        			tb.getItem(o).getItemProperty("Controle").getValue().toString().equals("ALUGUEL") ||
//						        			tb.getItem(o).getItemProperty("Controle").getValue().toString().equals("ASSISTENCIA")){
//						        		
//						        		//banco= new BancoDoBrasil();
//						        		cb = em.find(ContaBancaria.class, 2);
//						        		
//						        	}else{
//						        		cb = em.find(ContaBancaria.class, 2);
//						        	}
//						        }
						        
						        if(tb.getItem(o).getItemProperty("N.Numero Sicred").getValue() != null){
						        	nossoNumero = tb.getItem(o).getItemProperty("N.Numero Sicred").getValue().toString();
						        	
						        	if(tb.getItem(o).getItemProperty("Controle").getValue().toString().equals("ACESSO-PRE") || 
						        			tb.getItem(o).getItemProperty("Controle").getValue().toString().equals("ACESSO-POS") || 
						        			tb.getItem(o).getItemProperty("Controle").getValue().toString().equals("ALUGUEL") ||
						        			tb.getItem(o).getItemProperty("Controle").getValue().toString().equals("ASSISTENCIA") ||
						        			tb.getItem(o).getItemProperty("Controle").getValue().toString().equals("ESPACO-REUNIAO")){
						        		
						        		//banco= new Sicredi();
						        		cb = em.find(ContaBancaria.class, 5);
						        		
						        	}else{
						        		cb = em.find(ContaBancaria.class, 2);
						        	}
						        }
						        
						        
						        if(cb == null){
						        	cb = em.find(ContaBancaria.class, 2);
						        }
							
							
							String nomeCliente = tb.getItem(o).getItemProperty("Cliente").getValue().toString();
							String nDoc =  tb.getItem(o).getItemProperty("N.Doc").getValue().toString();

						
							Query qPb = em.createQuery("select pb from ParametrosBoleto pb where pb.cliente_id = :codCliente", ParametrosBoleto.class);
							qPb.setParameter("codCliente",Integer.parseInt(tb.getItem(o).getItemProperty("Cod. Cliente").getValue().toString()) );			
							ParametrosBoleto pb = null;
							boolean cobrarTaxa = true;
							if(qPb.getResultList().size() > 0){
								pb = (ParametrosBoleto) qPb.getSingleResult();
								
								if(!pb.getCobrar_taxa_bancaria()){
									cobrarTaxa = false;
								}
							}
							
							if(qPb.getResultList().size() == 0){
								cobrarTaxa = false;
							}
							
							Double vlrBoleto = null; 
							Double valorBoleto = null;
							
							boolean taxBoleto = true;
							
							try{
								ContasReceber cr = ContasReceberDAO.find(Integer.parseInt(tb.getItem(o).getItemProperty("Cod.").getValue().toString()));									
								String codContrato = cr.getN_doc().split("/")[0].toString();									
								PlanoAcesso oPlanoBoleto = PlanoAcessoDAO.find(cr.getPlano_contrato());
								taxBoleto =oPlanoBoleto != null  &&  oPlanoBoleto.getTaxa_boleto().equals("NAO") ? false : true;		
							}catch(Exception e){
								
							}
							
							if(cobrarTaxa){
								if(!taxBoleto){
									cobrarTaxa = false;
								}
							}
							
							if(cobrarTaxa){
								vlrBoleto = Double.parseDouble(Real.formatStringToDB(tb.getItem(o).getItemProperty("Valor").getValue().toString()));
								valorBoleto = vlrBoleto + new Double(cb.getTaxa_boleto());										
							}else{
								valorBoleto = Double.parseDouble(Real.formatStringToDB(tb.getItem(o).getItemProperty("Valor").getValue().toString()));										
							}

							//Sacado
							Cliente sacadoCliente = em.find(Cliente.class, Integer.parseInt(tb.getItem(o).getItemProperty("Cod. Cliente").getValue().toString()));
							
							Endereco enderecoSacado = null;

							String[] os = nDoc.split("OS");
							Ose ose =null;	
							Osi osi =null;	
							if(os.length > 1 && controle.equals("SERVICO")){
								ose = OseDAO.find(Integer.parseInt(os[1].split("-")[0]));
							}else if(os.length > 1 && controle.equals("ASSISTENCIA")){
								osi = OsiDAO.find(Integer.parseInt(os[1]));
							}
							
							if(!ContasReceberDAO.allowNdocManual(nDoc)){
							//	String codContrato = nDoc.split("/")[0].toString();
								enderecoSacado = sacadoCliente.getEndereco_principal();
								
							}else if(ose!=null){
								enderecoSacado = ose.getEnd();
							}else if(osi!=null){
								enderecoSacado = osi.getEnd();										
							}else{
								//Endereco
								Query qEndereco = em.createQuery("select e from Endereco e where e.clientes =:cliente and e.principal =:principal", Endereco.class);
								qEndereco.setParameter("cliente", sacadoCliente);
								qEndereco.setParameter("principal", true);
								
								
								if(qEndereco.getResultList().size() == 1){
									enderecoSacado = (Endereco)qEndereco.getSingleResult();
								}
							}
							
							if(sacadoCliente != null && enderecoSacado != null){
				
								Datas datas = Datas.novasDatas()
										.comDocumento(diaEmissao, mesEmissao, anoEmissao)
										.comProcessamento(Calendar.getInstance().get(Calendar.DATE), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.YEAR))
										.comVencimento(diaVencimento, mesVencimento, anoVencimento);  
								
								SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
								
								Date dt1 = sdf.parse(diaEmissao+"/"+mesEmissao+"/"+anoEmissao);
								Date dt2 = sdf.parse("27/09/2018");
								
								//if(dt1.before(dt2)){
								//	filtroDataImprimir = false;
								//}

								Emissor emissor= null; 
								
								
								if(cb != null && cb.getId() == 5){
								
								//sicred
									emissor = Emissor.novoEmissor()  
								        .comCedente(OpusERP4UI.getEmpresa().getRazao_social())  
								        .comAgencia(Integer.parseInt(cb.getAgencia_banco())).comDigitoAgencia('8')  
								        .comContaCorrente(cb.getCod_beneficiario())  
								        .comNumeroConvenio(cb.getConvenio() != null && !cb.getConvenio().equals("") ? Integer.parseInt(cb.getConvenio()) : 0).comDigitoContaCorrente('8')									           
								        .comCarteira(cb.getCarteira() != null && !cb.getCarteira().equals("") ? Integer.parseInt(cb.getCarteira()) : 0)  
								        .comNossoNumero(nossoNumero)
								        .comPostoBeneficiario(cb.getPosto_beneficiario())
								        .comEndereco(OpusERP4UI.getEmpresa().getEndereco()+", "+OpusERP4UI.getEmpresa().getNumero()+" "+OpusERP4UI.getEmpresa().getBairro()+" - "+OpusERP4UI.getEmpresa().getCidade()+" - "+OpusERP4UI.getEmpresa().getUf());
								
								
								}
								
								
								
								
								
								if(cb != null && cb.getId() == 	2){
								//bb
								
								emissor = Emissor.novoEmissor()  
							            .comCedente(OpusERP4UI.getEmpresa().getRazao_social())  
							            .comAgencia(Integer.parseInt(cb.getAgencia_banco())).comDigitoAgencia('8')  
							            .comContaCorrente(cb.getN_conta())  
							            .comNumeroConvenio(Integer.parseInt(cb.getConvenio())).comDigitoContaCorrente('8')									           
							            .comCarteira(Integer.parseInt(cb.getCarteira()))  
							            .comNossoNumero(nossoNumero)
							            .comEndereco(OpusERP4UI.getEmpresa().getEndereco()+", "+OpusERP4UI.getEmpresa().getNumero()+" "+OpusERP4UI.getEmpresa().getBairro()+" - "+OpusERP4UI.getEmpresa().getCidade()+" - "+OpusERP4UI.getEmpresa().getUf());  
								}
								
								
								

						        Sacado sacado = Sacado.novoSacado()  
						        		.comNome(nomeCliente)  
						        		.comCpf(sacadoCliente.getDoc_cpf_cnpj())  
						        		.comEndereco(enderecoSacado.getEndereco()+","+enderecoSacado.getNumero())  
						        		.comBairro(enderecoSacado.getBairro())  
						        		.comCep(enderecoSacado.getCep())  
						        		.comCidade(enderecoSacado.getCidade())  
						        		.comUf(enderecoSacado.getUf())  
						        		.comReferencia(enderecoSacado.getReferencia())
						        		.comComplemento(enderecoSacado.getComplemento());

						        
						        Banco banco = null;
						        
//						        if(cb.getCod_banco().equals("748")){								        
//						        	banco= new Sicredi();
//						        }
//						        
//						        if(cb.getCod_banco().equals("001")){								        
//						        	banco= new BancoDoBrasil();
//						        }
						        
						        
						        String n_numero = "";
//						        if(tb.getItem(o).getItemProperty("N.Numero").getValue() != null){
//						        	n_numero = tb.getItem(o).getItemProperty("N.Numero").getValue().toString();
//						        	
//						        	
//						        	if(tb.getItem(o).getItemProperty("Controle").getValue().toString().equals("ACESSO-PRE") || 
//						        			tb.getItem(o).getItemProperty("Controle").getValue().toString().equals("ACESSO-POS") || 
//						        			tb.getItem(o).getItemProperty("Controle").getValue().toString().equals("SERVICO")|| 
//						        			tb.getItem(o).getItemProperty("Controle").getValue().toString().equals("PRODUTO")||								        			
//						        			tb.getItem(o).getItemProperty("Controle").getValue().toString().equals("ALUGUEL") ||
//						        			tb.getItem(o).getItemProperty("Controle").getValue().toString().equals("ASSISTENCIA")){
//						        		
//						        		banco= new BancoDoBrasil();
//						        		cb = em.find(ContaBancaria.class, 2);
//						        		
//						        	}
//						        }
						        
						        if(tb.getItem(o).getItemProperty("N.Numero Sicred").getValue() != null){
						        	n_numero = tb.getItem(o).getItemProperty("N.Numero Sicred").getValue().toString();
						        	
						        	if(tb.getItem(o).getItemProperty("Controle").getValue().toString().equals("ACESSO-PRE") || 
						        			tb.getItem(o).getItemProperty("Controle").getValue().toString().equals("ACESSO-POS") || 
						        			tb.getItem(o).getItemProperty("Controle").getValue().toString().equals("SERVICO") ||								        			
						        			tb.getItem(o).getItemProperty("Controle").getValue().toString().equals("PRODUTO")||								        			
						        			tb.getItem(o).getItemProperty("Controle").getValue().toString().equals("ALUGUEL") ||
						        			tb.getItem(o).getItemProperty("Controle").getValue().toString().equals("ASSISTENCIA") ||
						        			tb.getItem(o).getItemProperty("Controle").getValue().toString().equals("ESPACO-REUNIAO")){
						        		
						        		banco= new Sicredi();
						        		cb = em.find(ContaBancaria.class, 5);
						        		
						        	}
						        }
						        
						        
						        if(banco == null){
						        	banco= new BancoDoBrasil();
					        		cb = em.find(ContaBancaria.class, 2);
						        }

						        ContasReceber b= null;
						        
						        Query q = em.createQuery("select cr from ContasReceber cr where cr.id=:cod", ContasReceber.class);
						        q.setParameter("cod", Integer.parseInt(tb.getItem(o).getItemProperty("Cod.").getValue().toString()));
						        
						        if(q.getResultList().size() == 1){
						        	b = (ContasReceber)q.getSingleResult();
						        }
						        
								Boleto boleto1 = Boleto.novoBoleto()
										.comCodBoleto(b.getId())
							            .comBanco(banco)  
							            .comDatas(datas)						              
							            .comEmissor(emissor)  
							            .comSacado(sacado)  
							            .comValorBoleto(valorBoleto)  
							            .comNumeroDoDocumento(nDoc)  
							            .comInstrucoes(cb.getInstrucoes1(), cb.getInstrucoes2(), cb.getInstrucoes3())							            
							            .comDescricoes(cb.getDemonstrativo1(), cb.getDemonstrativo2(), cb.getDemonstrativo3())
							            .comNossoNumero(n_numero)
							            .comContaBancaria(cb) 
							            .comQtd(b.getQuantidade()).comCnpj(OpusERP4UI.getEmpresa().getCnpj()).comControle(tb.getItem(o).getItemProperty("Controle").getValue().toString());		
								
								
								boletosSelecionados.add(boleto1);		
								
							}

						
						                                                 
						final Integer codContaReceber = Integer.parseInt(tb.getItem(o).getItemProperty("Cod.").getValue().toString());
						ContasReceber cr = ContasReceberDAO.find(codContaReceber);
						AlteracoesContasReceberDAO.save(new AlteracoesContasReceber(null, "IMPRIMIU UM BOLETO", cr,OpusERP4UI.getEmpresa(), OpusERP4UI.getUsuarioLogadoUI(), new Date()));
						
					}
					
					Window window;
					window = new Window();
					window.setCaption("Imprimir Boletos");
					window.setWidth("800px");
					window.setHeight("600px");
					window.setResizable(true);
					window.center();
					window.setModal(true);
					window.setStyleName("disable_scroolbar");		
					window.setCloseShortcut(KeyCode.ESCAPE, null);
				
					if(boletosSelecionados.size() > 0 && filtroDataImprimir){

						StreamResource resource = new StreamResource(new ImprimirBoletoBeta(boletosSelecionados), "Boletos.pdf");
						resource.getStream();			        
						resource.setMIMEType("application/pdf");		
						resource.setCacheTime(0);
						
						Embedded e = new Embedded();
						e.setSizeFull();
						e.setType(Embedded.TYPE_BROWSER); 
						e.setSource(resource);			     
						window.setContent(e);						
					
						getUI().addWindow(window);
						window.focus();
					}else{
						if(!filtroDataImprimir){
							Notify.Show("Um ou mais boleto(s) precisam ser reemitido(s)", Notify.TYPE_ERROR);
						}
					}
					
					window.addCloseListener(new Window.CloseListener() {
						
						@Override
						public void windowClose(CloseEvent e) {						
							tb.focus();						
						}
					});
					
					LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Imprimiu um ou mais Boleto(s)"));
				
				
				}
			} catch (Exception e) {
				e.printStackTrace();
				Notify.Show("ERRO: "+e.getLocalizedMessage(), Notify.TYPE_ERROR);
			}
		}else{
			Notification.show("Atenção", "Você não Possui Permissão para Imprimir Boletos", Type.ERROR_MESSAGE);
		}
	}
	
	

	Window winSubMenuLog;
	private Component BuildbtLog() {
		btLog = new Button("Log", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
	
				
				if (winSubMenuLog != null && winSubMenuLog.getUI() != null)
					winSubMenuLog.close();
	             else {
	            	 
	            	fecharTodosSubMenusMenos(winSubMenuLog); 
	            	
	            	 buildSubMenuLogs(event);
				     
					 getUI().addWindow(winSubMenuLog);
					 winSubMenuLog.focus();
	                 ((CssLayout) getUI().getContent()).addLayoutClickListener(new LayoutClickListener() {
	                             
	               			@Override
	                        public void layoutClick(LayoutClickEvent event) {
	               				winSubMenuLog.close();
	                            ((CssLayout) getUI().getContent()).removeLayoutClickListener(this);
	                        }
	                 });
	             }		
				
			}
		});
		btLog.setEnabled(true);	
		
		
		
		
		
		return btLog;
	}
	
	List<Integer> boletosComFalhaDeBaixa = new ArrayList<>();
	public void checarBaixas(){
		boletosComFalhaDeBaixa = new ArrayList<>();
		
		
		EntityManager em = ConnUtil.getEntity();
		Query q  = em.createNativeQuery("SELECT acr.CONTA_RECEBER_ID, acr.TIPO FROM alteracoes_conta_Receber acr WHERE acr.DATA_ALTERACAO >= '2018-07-01 01:00:00' AND acr.TIPO LIKE '%BAIXOU%' AND EXISTS(SELECT * FROM contas_receber c where c.ID = acr.CONTA_RECEBER_ID AND c.STATUS_2 = 'ABERTO' ) ");
		
		
		for (Object[] o : (List<Object[]>)q.getResultList()) {
			Integer codBoleto = ((Integer)o[0]);
			Query q2 = em.createNativeQuery("SELECT * FROM alteracoes_conta_Receber acr WHERE acr.TIPO LIKE '%RE-ABRIU%' AND acr.CONTA_RECEBER_ID=:boleto");
			q2.setParameter("boleto", codBoleto);
			
			if(q2.getResultList().size() == 0){
				boletosComFalhaDeBaixa.add(codBoleto);
			}
		 
		}
		
		if(boletosComFalhaDeBaixa !=  null && boletosComFalhaDeBaixa.size() > 0){
			btLog.addStyleName("falhas");
			
			if(btLog2 != null){
				btLog2.addStyleName("falhas");
			}
		}else{
			btLog.removeStyleName("falhas");
			if(btLog2 != null){
				btLog2.removeStyleName("falhas");
			}
		}
	}
	
	public void buildShortcurEvents(Component c){
		
		if(c instanceof ContasReceberView)
		{
			btNovo.addShortcutListener(buildShortCutNovo());
//			btEditar.addShortcutListener(buildShortCutEditar());
			btBaixar.addShortcutListener(buildShortCutBaixar());
		}else{
			if(btNovo != null || slNovo != null){				
				btNovo.removeShortcutListener(slNovo);
			}
			
//			if(btEditar != null || slEditar != null){
//				btEditar.removeShortcutListener(slEditar);			
//			}
			
			if(btBaixar != null || slBaixar != null){
				btBaixar.removeShortcutListener(slBaixar);			
			}
		}
	}
	
	public ShortcutListener buildShortCutNovo(){
		slNovo = new ShortcutListener("Novo",ShortcutAction.KeyCode.F2,null) {
			
			@Override
			public void handleAction(Object sender, Object target) {
				btNovo.click(); 
			}
		};
		return slNovo;
	}
	
	public ShortcutListener buildShortCutEditar(){
		slEditar = new ShortcutListener("Editar",ShortcutAction.KeyCode.ENTER,null) {
			
			public void handleAction(Object sender, Object target) {				
					btEditar.click();
			}
		};
		return slEditar;
	}
	
	public ShortcutListener buildShortCutBaixar(){
		slBaixar = new Button.ClickShortcut(btBaixar, KeyCode.B, ModifierKey.CTRL) {		
			
			@Override
			public void handleAction(Object sender, Object target) {
				btBaixar.click();
			}
		};
		return slBaixar;
	}

	public Integer getCodSubModulo() {
		return codSubModulo;
	}

	public void setCodSubModulo(Integer codSubModulo) {
		this.codSubModulo = codSubModulo;
	}
	
	
}

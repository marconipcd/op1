package com.digital.opuserp.view.modulos.nfe.modelo_21.notas;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.digital.opuserp.dao.CfopDAO;
import com.digital.opuserp.dao.ContasReceberDAO;
import com.digital.opuserp.dao.ContratosAcessoDAO;
import com.digital.opuserp.dao.NfeDAO;
import com.digital.opuserp.domain.AcessoCliente;
import com.digital.opuserp.domain.Cfop;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.ContasReceber;
import com.digital.opuserp.domain.NfeMestre;
import com.digital.opuserp.interfaces.GenericEditor;
import com.digital.opuserp.util.Real;
import com.digital.opuserp.util.StringUtil;
import com.digital.opuserp.view.modulos.cadastro.clientes.ClienteEditor.ClientesListerner;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.server.Page.Styles;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Table.ColumnReorderEvent;
import com.vaadin.ui.Table.ColumnResizeEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class NotasEditor extends Window implements GenericEditor {

	Item item;
	Button btSalvar;
	Button btCancelar;
	FormLayout flPrincipal;
	FieldGroup fieldGroup;
	VerticalLayout vlRoot;
	String  cepAtual;
		
	public NotasEditor(Item item, String title, boolean modal){
	
		this.item = item;
		
		Styles styles = Page.getCurrent().getStyles();					        
        styles.add(".dashboard input.v-textfield-readonly { background-color: #FFF; }");
		
		configLayout();
		
		setCaption(title);
		setModal(modal);
		setResizable(false);
		setClosable(false);
		center();
		
		vlRoot = new VerticalLayout();	
		vlRoot.setWidth("100%");
		vlRoot.setMargin(true);
		vlRoot.setStyleName("border-form");
		
		setContent(new VerticalLayout(){
			{
				setWidth("100%");
				setMargin(true);
				addComponent(vlRoot);
				
				HorizontalLayout hl_footer = new HorizontalLayout();
				hl_footer.setWidth("100%");
				
				HorizontalLayout hlButtons = new HorizontalLayout();
				hlButtons.setSpacing(true);
				hlButtons.setMargin(true);
				hlButtons.setStyleName("hl_buttons_bottom");
				hlButtons.addComponent(buildBtSelectAll());
				hlButtons.addComponent(buildBtCancelar());
				hlButtons.addComponent(buildBtSalvar());
				
				hl_footer.addComponent(buildQtd());
				hl_footer.setComponentAlignment(lbqtd, Alignment.MIDDLE_LEFT);
				
				hl_footer.addComponent(hlButtons);
				hl_footer.setComponentAlignment(hlButtons, Alignment.MIDDLE_RIGHT);
				
				addComponent(hl_footer);
			}
		});
			
		buildLayout();
	}
	
	Label lbqtd;
	private Label buildQtd(){
		lbqtd = new Label("0 registros encontrados");
		
		return lbqtd;
	}
	
	private void configLayout(){
				setWidth("950px");
				setHeight("604px");					
	}
	
	public void buildLayout(){	
		vlRoot.addComponent(dfAnoMes());
		vlRoot.addComponent(buildTfBusca());
		vlRoot.addComponent(buildTbContratos(txBusca.getValue()));		
		
		lbqtd.setValue(tb.getContainerDataSource().size()+" registros encontrado");
	}
	
	TextField txBusca;
	private TextField buildTfBusca(){
		txBusca = new TextField();
		txBusca.setWidth("100%");
		
		txBusca.addTextChangeListener(new FieldEvents.TextChangeListener() {
			
			@Override
			public void textChange(TextChangeEvent event) {
				//addfilter(event.getText())
				
				vlRoot.replaceComponent(tb, buildTbContratos(event.getText()));
				lbqtd.setValue(tb.getContainerDataSource().size()+" registros encontrado");
			}
		});
		
		return txBusca;
	}
	
	DateField df;
	public DateField dfAnoMes(){
		
		df = new DateField();
		df.setResolution(DateField.RESOLUTION_MONTH);		
		df.setDateFormat("MMMM/yyyy");
		 
		df.setValue(new Date());
		
		df.addValueChangeListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				vlRoot.removeComponent(tb);
				vlRoot.addComponent(buildTbContratos(txBusca.getValue()));
				
				lbqtd.setValue(tb.getContainerDataSource().size()+" registros encontrado");
			}
		});
		
		return df;
	}
			
	Table tb;
	public Table buildTbContratos(String s){
		tb = new Table(){
			
			@Override
			protected String formatPropertyValue(Object rowId, Object colId,
					Property<?> property) {
				
//				if(colId.equals("Cliente") && tb.getItem(rowId).getItemProperty("Cliente").getValue() != null){				
//											
//						return ((Cliente)tb.getItem(rowId).getItemProperty("Cliente").getValue()).getNome_razao();				
//										
//				}
				
//				if(colId.equals("Código contrato") && tb.getItem(rowId).getItemProperty("Código contrato").getValue() != null){					
//											
//						return ((AcessoCliente)tb.getItem(rowId).getItemProperty("Código contrato").getValue()).getId().toString();				
//										
//				}
				
				if(colId.equals("Data da mensalidade") && tb.getItem(rowId).getItemProperty("Data da mensalidade").getValue() != null){					
					SimpleDateFormat form = new SimpleDateFormat("dd/MM/yyyy");	
					return form.format(((Date)tb.getItem(rowId).getItemProperty("Data da mensalidade").getValue()));				
									
			}
				
				
				
				
				
				return super.formatPropertyValue(rowId, colId, property);			
			}
		};
		
		tb.setSizeFull();
		tb.setColumnCollapsingAllowed(true);
		tb.setSelectable(true);
		
		tb.addContainerProperty("Código do contrato", String.class, null);
		tb.addContainerProperty("Código contrato", AcessoCliente.class, null);
		tb.addContainerProperty("Cliente", Cliente.class, null);
		tb.addContainerProperty("Nome Cliente", String.class, null);
		tb.addContainerProperty("Data Mensalidade", ContasReceber.class, null);
		tb.addContainerProperty("Data da mensalidade", Date.class, null);
		tb.addContainerProperty("Valor Nota", String.class, null);
				
		tb.addContainerProperty("Gerar", CheckBox.class, false);		
	    tb.setColumnWidth("Gerar", 50);
	    
	    tb.setColumnCollapsed("Cliente", true);
	    tb.setColumnCollapsed("Código contrato", true);
	    tb.setColumnCollapsed("Data Mensalidade", true);
	    tb.setColumnWidth("Cliente", 0);
	    tb.setColumnWidth("Código contrato", 0);
	    tb.setColumnWidth("Data Mensalidade", 0);
	    
	    tb.addColumnResizeListener(new Table.ColumnResizeListener() {
			
			@Override
			public void columnResize(ColumnResizeEvent event) {
					tb.setColumnCollapsed("Cliente", true);
					tb.setColumnCollapsed("Código contrato", true);
					tb.setColumnCollapsed("Data Mensalidade", true);
			}
		});
	    
	    tb.addColumnReorderListener(new Table.ColumnReorderListener() {
			
			@Override
			public void columnReorder(ColumnReorderEvent event) {				
				tb.setColumnCollapsed("Cliente", true);				
				tb.setColumnCollapsed("Código contrato", true);
				tb.setColumnCollapsed("Data Mensalidade", true);
			}
		});
	    
	    tb.addItemClickListener(new ItemClickEvent.ItemClickListener() {
			
			@Override
			public void itemClick(ItemClickEvent event) {
				tb.setColumnCollapsed("Cliente", true);				
				tb.setColumnCollapsed("Código do contrato", true);
				tb.setColumnCollapsed("Data Mensalidade", true);
			}
		});
	    
		List<AcessoCliente> contratos = ContratosAcessoDAO.getContratosNfe(s);
		
		Integer i = 0;
		for (AcessoCliente acessoCliente : contratos) {	
			
			try{
				SimpleDateFormat anoMes = new SimpleDateFormat("yyMM");
								
				SimpleDateFormat form = new SimpleDateFormat("dd/MM/yyyy");	
				ContasReceber boleMensal = ContasReceberDAO.procurarBoletoMensal(acessoCliente.getId(), anoMes.format(df.getValue() != null ? df.getValue() : new Date()));
				boolean allow = acessoCliente.getEmitir_nfe_c_boleto_aberto().equals("NAO") &&  boleMensal.getStatus().equals("ABERTO") ? false : true;   
				
				if(boleMensal != null && allow){		
					try{
						Date dataVenc = boleMensal.getData_vencimento();				
						
						boolean gerado = NfeDAO.verifica_se_gerado_mes(acessoCliente, anoMes.format(dataVenc));
						
						if(!gerado){					
														
							tb.addItem(new Object[]{acessoCliente.getId().toString(),acessoCliente,acessoCliente.getCliente(), acessoCliente.getCliente().getNome_razao(), boleMensal,boleMensal.getData_vencimento(),boleMensal.getValor_titulo(), new CheckBox()}, i);						
						}
					
					}catch(Exception e){
						e.printStackTrace();
					}
				}
				i++;
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		return tb;
	}

	
	
	@Override
	public Button buildBtSalvar() {
		btSalvar = new Button("OK", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
			
				Object[] items = tb.getItemIds().toArray();
				
				List<NfeMestre> listMestre = new ArrayList<>();
				for (Object object : items) {
					try{
						if(((CheckBox)tb.getItem(object).getItemProperty("Gerar").getValue()).getValue()){
							
							Cliente cliente = (Cliente)tb.getItem(object).getItemProperty("Cliente").getValue();								
							AcessoCliente contrato =  (AcessoCliente)tb.getItem(object).getItemProperty("Código contrato").getValue();
							ContasReceber boleto =  (ContasReceber)tb.getItem(object).getItemProperty("Data Mensalidade").getValue();
							
							Cfop cfop = new Cfop();
							if(contrato != null && contrato.getCfop_nfe() != null){
								cfop = CfopDAO.find(contrato.getCfop_nfe());						
							}
													
							SimpleDateFormat form = new SimpleDateFormat("dd/MM/yyyy");
							SimpleDateFormat anoMes = new SimpleDateFormat("yyMM");
						
							
							Double valor = Real.formatStringToDBDouble(tb.getItem(object).getItemProperty("Valor Nota").getValue().toString());
							
							String cod_iden  = StringUtil.md5(cliente.getDoc_cpf_cnpj()+"21"+String.valueOf(valor)+"0,00"+"0,00");
							
							double vlr_desconto;
							double vlr_boleto = Real.formatStringToDBDouble(boleto.getValor_titulo());
							double vlr_plano  = Real.formatStringToDBDouble(contrato.getPlano().getValor());
							
							if(vlr_boleto != vlr_plano){
								//vlr_desconto = new Double("0,00");
								
								double vlr_desconto_calc = Real.formatStringToDBDouble(contrato.getPlano().getDesconto());
								double vlr_boleto_calc = vlr_boleto;
								double vlr_plano_calc = vlr_plano;
								
								double perc_vlr_boleto = (vlr_boleto_calc / vlr_plano_calc) * 100;
								double vlr_desconto_pro_rata = (vlr_desconto_calc * perc_vlr_boleto) / 100;
								
								vlr_desconto = vlr_desconto_pro_rata;
								
							}else{
								vlr_desconto = Real.formatStringToDBDouble(contrato.getPlano().getDesconto());
							}
							
							NfeMestre nfeM = new NfeMestre( null,cliente,contrato ,boleto,cfop,new Date(),boleto.getData_vencimento(),	contrato.getContrato().getClasse_consumo(),"4","00",
									cliente.getId().toString(),"21","000", cod_iden	, Real.formatStringToDBDouble(boleto.getValor_titulo()), 
									Real.formatStringToDBDouble(boleto.getValor_titulo())-vlr_desconto,	0,0,	0,	
									vlr_desconto,"N",	anoMes.format(boleto.getData_vencimento()),"1",cliente.getTelefone1(),
									StringUtil.md5(cliente.getDoc_cpf_cnpj()+cliente.getDoc_rg_insc_estadual()+
									cliente.getNome_razao()+"PE"+contrato.getContrato().getClasse_consumo()+"4"+"00"+
									cliente.getId().toString()+new Date()+"21"+"000")+cod_iden+valor+0+0+0+0+"N"+anoMes.format(boleto.getData_vencimento())+"1", false);
													
							NfeDAO.save(nfeM);		

//							NfeItem nfeI = new NfeItem(null, nfeM, "4", "00", new Date(), "21", "000", nfeM.getId().toString(), "001", contrato.getPlano().getServico_plano().getId().toString(), contrato.getPlano().getServico_plano().getDescricao(), "0104", 
//									"SERV", 1,1, valor, new Double("0"),new Double("0"),new Double("0"),new Double("0"),new Double("0"),new Double("0"),new Double("0"), "N",anoMes.format(d), StringUtil.md5("34eiwfj904839089085"));
//													
//							NfeDAO.save(nfeI);
						}
					}catch(Exception e){
						e.printStackTrace();
					}
				}
				
				fireEvent(new NotasEvent(getUI(), item, true));
				close();				
			}
		});
		
		
		ShortcutListener slbtOK = new ShortcutListener("Ok", ShortcutAction.KeyCode.ENTER,null) {
			
			@Override
			public void handleAction(Object sender, Object target) {
				btSalvar.click();
			}
		};
		
		btSalvar.addShortcutListener(slbtOK);
		
		btSalvar.setStyleName("default");
		return btSalvar;
	}

	@Override
	public Button buildBtCancelar() {
		btCancelar = new Button("Cancelar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				close();			
			}
		});
				
		ShortcutListener slbtCancelar = new ShortcutListener("Cancelar", ShortcutAction.KeyCode.ESCAPE,null) {
			
			@Override
			public void handleAction(Object sender, Object target) {
				btCancelar.click();
			}
		};
		btCancelar.addShortcutListener(slbtCancelar);
		
		return btCancelar;
	}
	
	
	Button btSelectAll;
	public Button buildBtSelectAll() {
		btSelectAll = new Button("Selecionar Todos", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
						
				for (Object itemId: tb.getItemIds().toArray()) {
					
					if(btSelectAll.getCaption().equals("Selecionar Todos")){
						((CheckBox)tb.getItem(itemId).getItemProperty("Gerar").getValue()).setValue(true);
						
					}else{
						((CheckBox)tb.getItem(itemId).getItemProperty("Gerar").getValue()).setValue(false);
						
					}
				}				
				
				if(btSelectAll.getCaption().equals("Selecionar Todos")){
					btSelectAll.setCaption("Limpar Seleção");
				}else{
					btSelectAll.setCaption("Selecionar Todos");
				}
			}
		});
		
		
					
		return btSelectAll;
	}
	
	public void addListerner(NotasListerner target){
		try {
			Method method = NotasListerner.class.getDeclaredMethod("onClose", NotasEvent.class);
			addListener(NotasEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(ClientesListerner target){
		removeListener(NotasEvent.class, target);
	}
	public static class NotasEvent extends Event{
		
		private Item item;
		private boolean confirm;
		
		public NotasEvent(Component source, Item item, boolean confirm) {
			super(source);
			this.item = item;
			this.confirm = confirm;			
		}

		public Item getItem() {
			return item;
		}	

		public boolean isConfirm() {
			return confirm;
		}		
	}
	public interface NotasListerner extends Serializable{
		public void onClose(NotasEvent event);
	}
	
	
}

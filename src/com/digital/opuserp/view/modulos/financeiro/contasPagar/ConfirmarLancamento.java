package com.digital.opuserp.view.modulos.financeiro.contasPagar;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.joda.time.DateTime;

import com.digital.opuserp.dao.GerenciarModuloDAO;
import com.digital.opuserp.interfaces.GenericEditor;
import com.digital.opuserp.util.Real;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class ConfirmarLancamento extends Window implements GenericEditor{

	Table tb;
	Button btSalvar;
	Button btCancelar;
	
	Integer codConta;
	Integer qtdRegistros = 0;
	private Label lbRegistros;
	HorizontalLayout hlFloatTb;
	HorizontalLayout hlRoot;
	HorizontalLayout hlFloat;
	
	GerenciarModuloDAO gmDAO;
	private Integer codSubModulo;
	private Date data;
	private Integer qtd;
	private String valor;
	private String intervalo;
	boolean gerar = true;
	
	public ConfirmarLancamento(boolean modal, boolean center,Integer qtd,Date data, String valor,String intervalo){
		
		super("Confirmar Lançamentos?");
		this.qtd = qtd;
		this.data = data;
		this.valor = valor;
		this.intervalo = intervalo;
		setWidth("530px");
		setHeight("350px");
		
		
		setModal(modal);
		if(center){
			center();
		}
		
		setClosable(false);

		gmDAO = new GerenciarModuloDAO();
		
		setContent(new VerticalLayout(){
			{
				setSizeFull();
				setMargin(true);
				
				addComponent(buildTbGeneric());
				addComponent(BuildLbRegistros(qtdRegistros));				
				
				setExpandRatio(tb, 2);
				
				HorizontalLayout hlButtons = new HorizontalLayout();
				hlButtons.setSpacing(true);
				hlButtons.setMargin(true);
				hlButtons.setStyleName("hl_buttons_bottom");
				hlButtons.addComponent(buildBtSalvar());
				hlButtons.addComponent(buildBtCancelar());
		
			
	
				hlRoot = new HorizontalLayout();
				hlRoot.addComponent(hlButtons);											
				addComponent(hlRoot);
				setComponentAlignment(hlRoot, Alignment.BOTTOM_RIGHT);
						
			}
		});
		
	}
	
	public Label BuildLbRegistros(Integer qtd){
		Double vl = Real.formatStringToDBDouble(valor);
		vl = vl * qtd;			
		String vlTotal = Real.formatDbToString(String.valueOf(vl).toString());
		
		lbRegistros = new Label(qtd.toString() + " Registros Encontrados | "+"Valor Total R$ "+vlTotal);
		return lbRegistros;
	}

	public Table buildTbGeneric() {
		
		tb = new Table();
		tb.setWidth("100%");
		tb.setHeight("200px");
		tb.addContainerProperty("Parcela", String.class, "");
		tb.addContainerProperty("Vencimento", String.class,"");
		tb.addContainerProperty("Valor", String.class, "");
		
		tb.setColumnAlignment("Valor", Align.RIGHT);

		tb.setColumnWidth("Parcela", 150);
		tb.setColumnWidth("Vencimento", 150);
		tb.setColumnWidth("Valor", 150);
		

		tb.setVisibleColumns(new Object[]{"Parcela","Vencimento","Valor"});			
		tb.setImmediate(true);
		
		try {
				Integer quantidade = 1;				
				String parcela ="";
				String part1 = "";
				String part2 = "";
				
				DateTime dt = new DateTime(data);
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				
				for(int c = 0; c < qtd; c++){	
					
					if(quantidade < 10){
						part1 = "0"+quantidade.toString();
					}else{
						part1 = quantidade.toString();
					}
					if(qtd < 10 ){
						part2 = "0"+qtd.toString();						
					}else{
						part2 = qtd.toString();
					}
					parcela = part1+"/"+part2;

					String dtForm = "";
						if(intervalo.equals("ANUAL")){
							dtForm = sdf.format(dt.plusYears(c).toDate());	
						}else if(intervalo.equals("MENSAL")){
							dtForm = sdf.format(dt.plusMonths(c).toDate());							
						}else if(intervalo.equals("QUINZENAL")){
							dtForm = sdf.format(dt.plusWeeks(c).toDate());		
							dt = dt.plusWeeks(1);
						}else{
							dtForm = sdf.format(dt.plusWeeks(c).toDate());																			
						}
			
					tb.addItem(new Object[]{parcela,dtForm,"R$ "+valor}, tb.getItemIds().size()+1);
						quantidade++;
						qtdRegistros++;				
					}
							
		} catch (Exception e) {
			e.printStackTrace();
		}

		return tb;
	}
	
	
	public Button buildBtSalvar() {
		btSalvar = new Button("Salvar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {				
				
				fireEvent(new ConfirmEvent(event.getComponent().getParent(), true));		
				close();	
			}			
		});
		
		btSalvar.focus();
		btSalvar.addStyleName("default");
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
		
		ShortcutListener clTb = new ShortcutListener("Cancelar",ShortcutAction.KeyCode.ESCAPE,null) {
			
			@Override
			public void handleAction(Object sender, Object target) {
				btCancelar.click();				
			}
		};
		btCancelar.addShortcutListener(clTb);
		return btCancelar;
	}


	public void addListerner(ConfirmListerner target){
		try {
			Method method = ConfirmListerner.class.getDeclaredMethod("onClose", ConfirmEvent.class);
			addListener(ConfirmEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	
	public void removeListerner(ConfirmListerner target){
		removeListener(ConfirmEvent.class, target);
	}
	
	public static class ConfirmEvent extends Event{
		
		private boolean confirm;
		
		public ConfirmEvent(Component source, boolean confirm) {
			super(source);
			
			this.confirm = confirm;		
			
		}

		public boolean isConfirm() {
			return confirm;
		}

	
		
	}
	public interface ConfirmListerner extends Serializable{
		public void onClose(ConfirmEvent event);
	}

	
	
	public Integer getCodSubModulo() {
		return codSubModulo;
	}



	public void setCodSubModulo(Integer codSubModulo) {
		this.codSubModulo = codSubModulo;
	}


}

package com.digital.opuserp.view.modulos.nfe.modelo_21.notas;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import br.com.caelum.stella.boleto.Datas;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.AlertaPendenciaDAO;
import com.digital.opuserp.dao.NfeDAO;
import com.digital.opuserp.dao.PlanoAcessoDAO;
import com.digital.opuserp.domain.AcessoCliente;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.ContaBancaria;
import com.digital.opuserp.domain.ContasReceber;
import com.digital.opuserp.domain.ControleTitulo;
import com.digital.opuserp.domain.NfeMestre;
import com.digital.opuserp.domain.ParametrosBoleto;
import com.digital.opuserp.domain.PlanoAcesso;
import com.digital.opuserp.interfaces.GenericEditor;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.EmailUtil;
import com.digital.opuserp.util.Real;
import com.digital.opuserp.util.StringUtil;
import com.digital.opuserp.util.boletos.boleto.Banco;
import com.digital.opuserp.util.boletos.boleto.Boleto;
import com.digital.opuserp.util.boletos.boleto.Emissor;
import com.digital.opuserp.util.boletos.boleto.EmissorNovo;
import com.digital.opuserp.util.boletos.boleto.Sacado;
import com.digital.opuserp.util.boletos.boleto.bancos.BancoDoBrasil;
import com.digital.opuserp.view.modulos.cadastro.clientes.ClienteEditor.ClientesListerner;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.Page;
import com.vaadin.server.Page.Styles;
import com.vaadin.server.StreamResource;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnReorderEvent;
import com.vaadin.ui.Table.ColumnResizeEvent;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class EnvioEmail extends Window implements GenericEditor {

	
	Button btSalvar;
	Button btCancelar;
	FormLayout flPrincipal;
	FieldGroup fieldGroup;
	VerticalLayout vlRoot;
	String  cepAtual;
	Integer codSubModulo;
		
	public EnvioEmail(String title, boolean modal, Integer codSubModulo){
	
		this.codSubModulo = codSubModulo;
		
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
	
	Label lbqtd;
	private Label buildQtd(){
		lbqtd = new Label("0 registros encontrados");
		
		return lbqtd;
	}
	
	private void configLayout(){
				setWidth("1200px");
				setHeight("565px");					
	}
	
	public void buildLayout(){	
		vlRoot.addComponent(buildDataMes());
		vlRoot.addComponent(buildTfBusca());
		vlRoot.addComponent(buildTbContratos(txBusca.getValue()));		
		
		lbqtd.setValue(tb.getContainerDataSource().size()+" registros encontrado");
	}
	
	DateField df;
	public DateField buildDataMes(){
		
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
				
				if(colId.equals("Cliente") && tb.getItem(rowId).getItemProperty("Cliente").getValue() != null){				
											
						return ((Cliente)tb.getItem(rowId).getItemProperty("Cliente").getValue()).getNome_razao();				
										
				}
				
				if(colId.equals("Código contrato") && tb.getItem(rowId).getItemProperty("Código contrato").getValue() != null){					
											
						return ((AcessoCliente)tb.getItem(rowId).getItemProperty("Código contrato").getValue()).getId().toString();				
										
				}
				
				if(colId.equals("Código") && tb.getItem(rowId).getItemProperty("Código").getValue() != null){					
					
					return ((NfeMestre)tb.getItem(rowId).getItemProperty(colId).getValue()).getId().toString();				
									
				}
			
				
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
			tb.addContainerProperty("Código", NfeMestre.class, null);
			tb.addContainerProperty("Cod NF", String.class, null);			
			tb.addContainerProperty("Código contrato", AcessoCliente.class, null);
			tb.addContainerProperty("Código do contrato", String.class, null);					
			tb.addContainerProperty("Cliente", Cliente.class, null);
			tb.addContainerProperty("Nome do cliente", String.class, null);			
		tb.addContainerProperty("Email", String.class, "");		
			tb.addContainerProperty("Data Mensalidade", ContasReceber.class, null);
			tb.addContainerProperty("Data da mensalidade", Date.class, null);
		tb.addContainerProperty("Valor Nota", String.class, null);				
		tb.addContainerProperty("Gerar", CheckBox.class, false);		
	    tb.setColumnWidth("Gerar", 50);
	    
	    tb.setColumnCollapsed("Código", true);
	    tb.setColumnCollapsed("Cliente", true);
	    tb.setColumnCollapsed("Código contrato", true);
	    tb.setColumnCollapsed("Data Mensalidade", true);
	    tb.setColumnWidth("Código", 0);
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
	    	    		
	    
	    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM");
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	    
	    
	    
	    
	    
	    Date data1 = null;
	    Date data2 = null;
	    
	    try{
	    	data1 = sdf.parse(sdf1.format(df.getValue())+"-01");
	    	data2 = sdf.parse(sdf1.format(df.getValue())+"-31");
	    }catch(Exception e){
	    	e.printStackTrace();
	    }
	    
		List<NfeMestre> contratos = NfeDAO.getNfeParaEnviarEmail(data1, data2,s);
		
		Integer i = 0;
		for (NfeMestre nfe : contratos) {	
			
			try{
				
				if(!nfe.getContrato().getStatus_2().equals("ENCERRADO")){								
					tb.addItem(new Object[]{nfe,nfe.getId().toString(),nfe.getContrato(),nfe.getContrato().getId().toString(),nfe.getCliente(), nfe.getCliente().getNome_razao(), nfe.getCliente().getEmail(), nfe.getContas_receber(),nfe.getContas_receber().getData_vencimento(),nfe.getContas_receber().getValor_titulo(), new CheckBox()}, i);						
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
							
								sendEmail((NfeMestre)tb.getItem(object).getItemProperty("Código").getValue(),(ContasReceber)tb.getItem(object).getItemProperty("Data Mensalidade").getValue(),(AcessoCliente)tb.getItem(object).getItemProperty("Código contrato").getValue());

						}
					}catch(Exception e){
						e.printStackTrace();
					}
				}
				
				fireEvent(new NotasEvent(getUI(),  true));
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
		
		
		private boolean confirm;
		
		public NotasEvent(Component source, boolean confirm) {
			super(source);
			
			this.confirm = confirm;			
		}

	

		public boolean isConfirm() {
			return confirm;
		}		
	}
	public interface NotasListerner extends Serializable{
		public void onClose(NotasEvent event);
	}
	private Boleto getBoleto(ContasReceber cr,AcessoCliente contrato){
		EntityManager em = ConnUtil.getEntity();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		
		String controle =  cr.getControle();
		Query qControle = em.createQuery("select c from ControleTitulo c where c.nome=:nome and c.empresa_id =:empresa", ControleTitulo.class);
		qControle.setParameter("nome", controle);
		qControle.setParameter("empresa", OpusERP4UI.getEmpresa().getId());
		
		ContaBancaria cb = null;
		if(qControle.getResultList().size() ==1){
			cb = ((ControleTitulo)qControle.getSingleResult()).getConta_bancaria();
		}

		
		Query qPb = em.createQuery("select pb from ParametrosBoleto pb where pb.cliente_id = :codCliente", ParametrosBoleto.class);
		qPb.setParameter("codCliente",cr.getCliente().getId() );			
		ParametrosBoleto pb = null;
		boolean cobrarTaxa = true;
		if(qPb.getResultList().size() > 0){
			pb = (ParametrosBoleto) qPb.getSingleResult();
			
			if(!pb.getCobrar_taxa_bancaria()){
				cobrarTaxa = false;
			}
		}
		
		Double vlrBoleto = null; 
		Double valorBoleto = null;
		
		boolean taxBoleto = true;
		
		try{
					
			String codContrato = cr.getN_doc().split("/")[0].toString();									
			 
			taxBoleto =contrato != null  &&  contrato.getPlano().getTaxa_boleto().equals("NAO") ? false : true;		
		}catch(Exception e){
			
		}
		
		
		if(cobrarTaxa){
			if(!taxBoleto){
				cobrarTaxa = false;
			}
		}
		
		if(cobrarTaxa){
			vlrBoleto = Double.parseDouble(Real.formatStringToDB(cr.getValor_titulo()));
			valorBoleto = vlrBoleto + new Double(cb.getTaxa_boleto());										
		}else{
			valorBoleto = Double.parseDouble(Real.formatStringToDB(cr.getValor_titulo()));										
		}
		
		
		
		Integer anoEmissao = Integer.parseInt(sdf.format(cr.getData_emissao()).substring(0, 4).toString());
		Integer mesEmissao = Integer.parseInt(sdf.format(cr.getData_emissao()).substring(5, 7).toString());
		Integer diaEmissao = Integer.parseInt(sdf.format(cr.getData_emissao()).substring(8, 10).toString());
	
		Integer anoVencimento = Integer.parseInt(sdf.format(cr.getData_vencimento()).substring(0, 4).toString()); 
		Integer mesVencimento = Integer.parseInt(sdf.format(cr.getData_vencimento()).substring(5, 7).toString()); 
		Integer diaVencimento = Integer.parseInt(sdf.format(cr.getData_vencimento()).substring(8, 10).toString());		
		String nossoNumero = cb.getConvenio()+cr.getN_numero();
		
		Datas datas = Datas.novasDatas()
				.comDocumento(diaEmissao, mesEmissao, anoEmissao)
				.comProcessamento(Calendar.getInstance().get(Calendar.DATE), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.YEAR))
				.comVencimento(diaVencimento, mesVencimento, anoVencimento);  
		
		
		Emissor emissor = Emissor.novoEmissor()  
	            .comCedente(OpusERP4UI.getEmpresa().getRazao_social())  
	            .comAgencia(Integer.parseInt(cb.getAgencia_banco())).comDigitoAgencia('8')  
	            .comContaCorrente(cb.getN_conta())  
	            .comNumeroConvenio(Integer.parseInt(cb.getConvenio())) .comDigitoContaCorrente('8')   
	            .comCarteira(Integer.parseInt(cb.getCarteira()))  
	            .comNossoNumero(nossoNumero)
	            .comEndereco(OpusERP4UI.getEmpresa().getEndereco()+", "+OpusERP4UI.getEmpresa().getNumero()+" "+OpusERP4UI.getEmpresa().getBairro()+" - "+OpusERP4UI.getEmpresa().getCidade()+" - "+OpusERP4UI.getEmpresa().getUf());
	
		//Sacado
        Sacado sacado = Sacado.novoSacado()  
        		.comNome(cr.getCliente().getNome_razao())  
        		.comCpf(cr.getCliente().getDoc_cpf_cnpj())  
        		.comEndereco(cr.getCliente().getEndereco_principal().getEndereco()+","+cr.getCliente().getEndereco_principal().getNumero())  
        		.comBairro(cr.getCliente().getEndereco_principal().getBairro())  
        		.comCep(cr.getCliente().getEndereco_principal().getCep())  
        		.comCidade(cr.getCliente().getEndereco_principal().getCidade())  
        		.comUf(cr.getCliente().getEndereco_principal().getUf())  
        		.comReferencia(cr.getCliente().getEndereco_principal().getReferencia())
        		.comComplemento(cr.getCliente().getEndereco_principal().getComplemento());

        Banco banco = new BancoDoBrasil();				        

        
		Boleto boleto = Boleto.novoBoleto()  
	            .comBanco(banco)  
	            .comDatas(datas)						              
	            .comEmissor(emissor)  
	            .comSacado(sacado)  
	            .comValorBoleto(valorBoleto)  
	            .comNumeroDoDocumento(cr.getN_doc())  
	            .comInstrucoes(cb.getInstrucoes1(), cb.getInstrucoes2(), cb.getInstrucoes3())							            
	            .comDescricoes(cb.getDemonstrativo1(), cb.getDemonstrativo2(), cb.getDemonstrativo3())
	            .comNossoNumero(cr.getN_numero())
	            .comQtd(cr.getQuantidade());		
		
	
		
		
		boleto.setStatus(cr.getStatus());
		
		return boleto;
	}
	private void sendEmail(NfeMestre nfe,ContasReceber cr,AcessoCliente contrato){
		String pathFull = "";			
		try {
			
			
			String nomeFile = "NF SERVICO COMUNICACAO "+StringUtil.preencheCom(nfe.getId().toString(), "0", 9, 1)+" - "+nfe.getCliente().getNome_razao()+".pdf";
			PlanoAcesso plano = PlanoAcessoDAO.find(cr.getPlano_contrato());
			StreamResource resource = new StreamResource(new ImprimirBoletoFatura(getBoleto(cr, contrato),nfe,plano), nomeFile);
			resource.getStream();			        
			resource.setMIMEType("application/pdf");		
			resource.setCacheTime(0);
			
			
			
			InputStream inputStream = resource.getStreamSource().getStream();
			OutputStream outputStream = null;

			try {							
		 
				String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();			
				pathFull = basepath+"/WEB-INF/uploads/"+nomeFile+".pdf";
				outputStream = new FileOutputStream(new File(pathFull));
		 
				int read = 0;
				byte[] bytes = new byte[1024];
		 
				while ((read = inputStream.read(bytes)) != -1) {
					outputStream.write(bytes, 0, read);
				}
		 
				System.out.println("Done!");
		 
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (outputStream != null) {
					try {
						// outputStream.flush();
						outputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
		 
				}
			}
			
			
			
			Cliente c =nfe.getCliente();
			if(c.getEmail() != null && !c.getEmail().equals("") && EmailUtil.validate(c.getEmail())){
				boolean check = EmailUtil.Send(c.getNome_razao(), c.getEmail(),c.getEmailAlternativo(), pathFull);
				if(check){
					
				
					NfeMestre n = NfeDAO.find(nfe.getId());
					n.setEmail_enviado(true); 
					NfeDAO.save(n);
					
					//container.refresh();
					AlertaPendenciaDAO.removePendencia(codSubModulo, nfe.getId()); 		
					Notify.Show("Nota fiscal enviada com sucesso!", Notify.TYPE_SUCCESS);
				}else{
					Notify.Show("Nota fiscal não foi enviada, verifique se o email informado é válido.", Notify.TYPE_ERROR);
				}
			}else{
				Notify.Show("Nenhum endereço de email válido foi localizado!", Notify.TYPE_ERROR);
			}
			
				
			
		} catch (Exception e) {
			e.printStackTrace();
		}					
	}
	
}

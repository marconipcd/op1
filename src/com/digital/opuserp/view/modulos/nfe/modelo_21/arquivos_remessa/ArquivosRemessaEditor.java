package com.digital.opuserp.view.modulos.nfe.modelo_21.arquivos_remessa;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.digital.opuserp.dao.NfeDAO;
import com.digital.opuserp.domain.NfeMestre;
import com.digital.opuserp.interfaces.GenericEditor;
import com.digital.opuserp.util.Real;
import com.digital.opuserp.util.StringUtil;
import com.digital.opuserp.view.modulos.cadastro.clientes.ClienteEditor.ClientesListerner;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FileResource;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinService;
import com.vaadin.server.Page.Styles;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class ArquivosRemessaEditor extends Window implements GenericEditor {

	Item item;
	Button btSalvar;
	Button btCancelar;
	FormLayout flPrincipal;
	FieldGroup fieldGroup;
	VerticalLayout vlRoot;
	String  cepAtual;
		
	public ArquivosRemessaEditor(Item item, String title, boolean modal){
	
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
						
				HorizontalLayout hlButtons = new HorizontalLayout();
				hlButtons.setSpacing(true);
				hlButtons.setMargin(true);
				hlButtons.setStyleName("hl_buttons_bottom");
				hlButtons.addComponent(buildBtCancelar());
				hlButtons.addComponent(buildBtSalvar());
				
				addComponent(hlButtons);
				setComponentAlignment(hlButtons, Alignment.BOTTOM_RIGHT);
			}
		});
			
		buildLayout();
	}
	
	private void configLayout(){
				setWidth("360px");
				setHeight("212px");					
	}
	
	public void buildLayout(){		
		
		fieldGroup = new FieldGroup(item);
		
		vlRoot.addComponent(new FormLayout(){
			{
				setMargin(true);
				setSpacing(true);
				setStyleName("form-cutom");
				
				ComboBox cbMesReferencia = new ComboBox("Mês de Referência");
				cbMesReferencia.setStyleName("caption-align-base");
				
				cbMesReferencia.addItem("Janeiro");
				cbMesReferencia.addItem("Fevereiro");
				cbMesReferencia.addItem("Março");
				cbMesReferencia.addItem("Abril");
				cbMesReferencia.addItem("Maio");
				cbMesReferencia.addItem("Junho");
				cbMesReferencia.addItem("Julho");
				cbMesReferencia.addItem("Agosto");
				cbMesReferencia.addItem("Setembro");
				cbMesReferencia.addItem("Outubro");
				cbMesReferencia.addItem("Novembro");
				cbMesReferencia.addItem("Dezembro");
				
				cbMesReferencia.setRequired(true); 
				cbMesReferencia.setNullSelectionAllowed(false);
				cbMesReferencia.focus();
				
				fieldGroup.bind(cbMesReferencia, "mes_referencia");				
				addComponent(cbMesReferencia);
			}
		});
		
		vlRoot.addComponent(new FormLayout(){
			{
				setMargin(true);
				setSpacing(true);
				setStyleName("form-cutom");
				
				ComboBox cbAnoReferencia = new ComboBox("Ano de Referência");
				cbAnoReferencia.setStyleName("caption-align-base");
				
				cbAnoReferencia.addItem("2012");
				cbAnoReferencia.addItem("2013");
				cbAnoReferencia.addItem("2014");
				cbAnoReferencia.addItem("2015");
				cbAnoReferencia.addItem("2016");
				cbAnoReferencia.addItem("2017");
				cbAnoReferencia.addItem("2018");
				cbAnoReferencia.addItem("2019");
				cbAnoReferencia.addItem("2020");
				cbAnoReferencia.addItem("2021");
				cbAnoReferencia.addItem("2022");
				cbAnoReferencia.addItem("2023");
				
				cbAnoReferencia.setRequired(true); 
				cbAnoReferencia.setNullSelectionAllowed(false);
				
				fieldGroup.bind(cbAnoReferencia, "ano_referencia");
				
				addComponent(cbAnoReferencia);
			}
		});
		
		vlRoot.addComponent(new FormLayout(){
			{
				setMargin(true);
				setSpacing(true);
				setStyleName("form-cutom");
				
				ComboBox cbFinalidade = new ComboBox("Finalidade");
				cbFinalidade.setStyleName("caption-align-base");
				
				cbFinalidade.addItem("Normal");
				cbFinalidade.addItem("Substituição");
				cbFinalidade.setRequired(true);
				cbFinalidade.setNullSelectionAllowed(false);
				
				fieldGroup.bind(cbFinalidade, "finalidade");
				
				addComponent(cbFinalidade);				
			}
		});
		
	}
			
	private File gerarArquivo(String ano_ref, String mes_ref){
		try{
			SimpleDateFormat sdfAno = new SimpleDateFormat("yy");
			File f;		

			String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();			
			 f = new File(basepath + "/WEB-INF/uploads/PE000"+sdfAno.format(new Date())+getCodMes(mes_ref)+"NM.001");
			 
			 //FileDownloader fileDownloader = new FileDownloader(new FileResource(f));
			 
			BufferedWriter br = new BufferedWriter(new FileWriter(f));  

			
			if(f.canWrite()	){
			
				StringBuilder sb = new StringBuilder();
				String quebra = "\n";
						
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				
				
				List<NfeMestre> nfes = NfeDAO.getNfe(ano_ref+getCodMes(mes_ref));
				for (NfeMestre nfeMestre : nfes) {
					
						String cpf_cnpj = StringUtil.preencheCom(nfeMestre.getCliente().getDoc_cpf_cnpj(), "0", 14, 1);
						String ie = StringUtil.preencheCom(nfeMestre.getCliente().getDoc_rg_insc_estadual().replaceAll(" ", "") == null || nfeMestre.getCliente().getDoc_rg_insc_estadual().replaceAll(" ", "").equals("") || nfeMestre.getCliente().getDoc_rg_insc_estadual().replaceAll(" ", "").equals("0") ? "ISENTO" : nfeMestre.getCliente().getDoc_rg_insc_estadual()," ",14,2);
						String razao_social = StringUtil.preencheCom(nfeMestre.getCliente().getNome_razao().length() > 35 ? nfeMestre.getCliente().getNome_razao().substring(0, 35) :nfeMestre.getCliente().getNome_razao() , " ", 35, 2);
						String uf = nfeMestre.getContrato().getEndereco().getUf();
						String classe_consumo = getCodigoClasseConsumo(nfeMestre.getContrato().getPlano().getContrato_acesso().getClasse_consumo());
						String fase_tipo_utilizacao_e_grupo_tensao = "400";
						String codigo_identificacao_cliente =StringUtil.preencheCom(nfeMestre.getCliente().getId().toString()," ",12,2);
						String data_emissao = sdf.format(nfeMestre.getData_emissao());
						String modelo_serie = "21000";
						String numero = StringUtil.preencheCom(nfeMestre.getId().toString(), "0", 9, 1);
						String valor_total = StringUtil.preencheCom(Real.formatDbToString(String.valueOf(nfeMestre.getTotal_nota())).replace(",", ""),"0",12,1);		  
						String valor_bc_icms = StringUtil.preencheCom(Real.formatDbToString(String.valueOf(nfeMestre.getTotal_nota())).replace(",", ""),"0",12,1);
						String valor_icms = "000000000000";
						String valor_op_isentas = StringUtil.preencheCom(Real.formatDbToString(String.valueOf(nfeMestre.getTotal_nota())).replace(",", ""),"0",12,1);
						String outros_valores = "000000000000";
						String cod_aut_dig_doc_fis = StringUtil.md5(cpf_cnpj+numero+valor_total+valor_bc_icms+valor_icms);
					    String situacao_doc = "N";
					    String ano_mes = sdfAno.format(nfeMestre.getData_emissao())+getCodMes(mes_ref);
					    String refe_item_nf = StringUtil.preencheCom("1", "0", 9, 1);
					    String numero_terminal_telefonico = StringUtil.preencheCom(nfeMestre.getCliente().getDdd_fone1()+nfeMestre.getCliente().getTelefone1(), "", 12, 2);
						String brancos_reserv = "       ";
						String cod_aut_dig_registro = StringUtil.md5(cpf_cnpj+ie+razao_social+uf+classe_consumo+fase_tipo_utilizacao_e_grupo_tensao+
								codigo_identificacao_cliente+data_emissao+modelo_serie+numero+cod_aut_dig_doc_fis+valor_total+valor_bc_icms+valor_icms+valor_op_isentas+outros_valores+situacao_doc+ano_mes+refe_item_nf+numero_terminal_telefonico+
								brancos_reserv);
								
						
						
						sb.append(cpf_cnpj);
						sb.append(ie);
						sb.append(razao_social);
						sb.append(uf);
						sb.append(classe_consumo);
						sb.append(fase_tipo_utilizacao_e_grupo_tensao);
						sb.append(codigo_identificacao_cliente);
						sb.append(data_emissao);
						sb.append(modelo_serie);
						sb.append(numero);
						sb.append(cod_aut_dig_doc_fis);
						sb.append(valor_total);
						sb.append(valor_bc_icms);
						sb.append(valor_icms);
						sb.append(valor_op_isentas);
						sb.append(outros_valores);						
						sb.append(situacao_doc);
						sb.append(ano_mes);
						sb.append(refe_item_nf);
						sb.append(numero_terminal_telefonico);
						sb.append(brancos_reserv);
						sb.append(cod_aut_dig_registro);
						
						sb.append(quebra);
				}
				
			
				br.write(sb.toString());  
				br.close();  
				
			
				return f;
			}else{
				Notify.Show("Não é Possivel Gravar o Arquivo", Notify.TYPE_ERROR);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	private String getCodMes(String nomeMes){
		if(nomeMes != null){
			switch (nomeMes) {
			case "Janeiro":
				return "01";
			case "Fevereiro":
				return "02";
			case "Março":
				return "03";
			case "Abril":
				return "04";
			case "Maio":
				return "05";
			case "Junho":
				return "06";
			case "Julho":
				return "07";
			case "Agosto":
				return "08";
			case "Setembro":
				return "09";
			case "Outubro":
				return "10";
			case "Novembro":
				return "11";
			case "Dezembro":
				return "12";			
			}
		}
		return "0";
			
	
	}
	private String getCodigoClasseConsumo(String nomeClasseConsumo){
		
		if(nomeClasseConsumo != null){
			switch (nomeClasseConsumo) {
			case "Comercial":
				return "1";
			case "Poder Público":
				return "2";
			case "Residencial":
				return "3";
			case "Telefone Público":
				return "4";
			case "Telefone Semi-Público":
				return "5";
			case "Grande Cliente":
				return "6";
				
			}
		}
		return "0";
	}
	
	@Override
	public Button buildBtSalvar() {
		btSalvar = new Button("OK", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
			
				try {
					fieldGroup.commit();
					//File fMestre = gerarArquivo(item.getItemProperty("ano_referencia").getValue().toString(),item.getItemProperty("mes_referencia").getValue().toString());
					//item.getItemProperty("nfe_mestre").setValue(readImageOldWay(fMestre)); 
					fireEvent(new ArquivosRemessaEvent(getUI(), item, true));
					close();
				} catch (Exception e) {
					e.printStackTrace();
				}				
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
	
	public byte[] readImageOldWay(File file) throws IOException
	{
	  //Logger.getLogger(Main.class.getName()).log(Level.INFO, "[Open File] " + file.getAbsolutePath());
	  if(file != null){
		  
		  InputStream is = new FileInputStream(file);
		  // Get the size of the file
		  long length = file.length();
		  // You cannot create an array using a long type.
		  // It needs to be an int type.
		  // Before converting to an int type, check
		  // to ensure that file is not larger than Integer.MAX_VALUE.
		  if (length > Integer.MAX_VALUE)
		  {
			  // File is too large
		  }
		  // Create the byte array to hold the data
		  byte[] bytes = new byte[(int) length];
		  // Read in the bytes
		  int offset = 0;
		  int numRead = 0;
		  while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0)
		  {
			  offset += numRead;
		  }
		  // Ensure all the bytes have been read in
		  if (offset < bytes.length)
		  {
			  throw new IOException("Could not completely read file " + file.getName());
		  }
		  // Close the input stream and return bytes
		  is.close();
		  return bytes;
	  }
	return null;
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
	
	public void addListerner(ArquivosRemessaListerner target){
		try {
			Method method = ArquivosRemessaListerner.class.getDeclaredMethod("onClose", ArquivosRemessaEvent.class);
			addListener(ArquivosRemessaEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(ClientesListerner target){
		removeListener(ArquivosRemessaEvent.class, target);
	}
	public static class ArquivosRemessaEvent extends Event{
		
		private Item item;
		private boolean confirm;
		
		public ArquivosRemessaEvent(Component source, Item item, boolean confirm) {
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
	public interface ArquivosRemessaListerner extends Serializable{
		public void onClose(ArquivosRemessaEvent event);
	}
	
	
}

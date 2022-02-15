package com.digital.opuserp.view.modulos.nfe.modelo_21.arquivos_remessa;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.codec.digest.DigestUtils;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.GerenciarModuloDAO;
import com.digital.opuserp.dao.LogDAO;
import com.digital.opuserp.dao.NfeDAO;
import com.digital.opuserp.domain.ArquivoRemessaNfe;
import com.digital.opuserp.domain.LogAcoes;
import com.digital.opuserp.domain.NfeMestre;
import com.digital.opuserp.interfaces.GenericView;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.Criptografia;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.util.Real;
import com.digital.opuserp.util.StringUtil;
import com.digital.opuserp.view.modulos.cadastro.ceps.CepEditor;
import com.digital.opuserp.view.modulos.cadastro.ceps.CepEditor.CepEvent;
import com.digital.opuserp.view.modulos.nfe.modelo_21.arquivos_remessa.ArquivosRemessaEditor.ArquivosRemessaEvent;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.filter.Like;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.themes.Reindeer;

public class ArquivosRemessaView extends VerticalLayout implements GenericView {

	JPAContainer<ArquivoRemessaNfe> container;
	
	Table tb;
	TextField tfBusca;
	Button btNovo;
	Button btEditar;
	Button btExcluir;
	HorizontalLayout hlFloat;
	
	private Label lbRegistros;
	
	ShortcutListener slNovo;
	ShortcutListener slEditar;
	
	private Integer codSubModulo;
	GerenciarModuloDAO gmDAO;
	

	public ArquivosRemessaView(boolean act){
	
		if(act){
				setSizeFull();				
						
				gmDAO = new GerenciarModuloDAO();
				
				HorizontalLayout hlButons = new HorizontalLayout();
				hlButons.addComponent(BuildbtTransferencia());
				hlButons.addComponent(BuildbtAceitar());
				hlButons.addComponent(BuildbtCancelar());
				
				addComponent(hlButons);
				setComponentAlignment(hlButons, Alignment.TOP_RIGHT);
				
				addComponent(buildTfbusca());
				
				addComponent(buildTbGeneric());
				addComponent(buildLbRegistros());
				
				hlFloat = new HorizontalLayout();
				hlFloat.setWidth("100%");
				hlFloat.addComponent(buildLbRegistros());
				Label lbLegend = new Label("F2 - Novo Cadastro | ENTER - Editar");
				lbLegend.setWidth("250px");
				//hlFloat.addComponent(lbLegend);
				hlFloat.setComponentAlignment(lbRegistros, Alignment.BOTTOM_LEFT);
				//hlFloat.setComponentAlignment(lbLegend, Alignment.BOTTOM_RIGHT);
							
				addComponent(hlFloat);				
				setExpandRatio(tb, 1);
		}
	}
	
	public void refresh(){
		replaceComponent(tb, buildTbGeneric());
		setExpandRatio(tb, 1);	
		addFilter(tfBusca.getValue());
		btEditar.setEnabled(false);
		btExcluir.setEnabled(false);
	}
	
	public Label buildLbRegistros(){
		lbRegistros = new Label(String.valueOf(container.size()) + " Registros Encontrados");
		return lbRegistros;
	}
	public void buildShortcurEvents(Component c){
		
		if(c instanceof ArquivosRemessaView)
		{
			btNovo.addShortcutListener(buildShortCutNovo());
			btEditar.addShortcutListener(buildShortCutEditar());
		}else{
			if(btNovo != null || slNovo != null){				
				btNovo.removeShortcutListener(slNovo);
			}
			
			if(btEditar != null || slEditar != null){
				btEditar.removeShortcutListener(slEditar);			
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
			
			@Override
			public void handleAction(Object sender, Object target) {
				btEditar.click();
			}
		};
		return slEditar;
	}
	
	public JPAContainer<ArquivoRemessaNfe> buildContainer(){
		container = JPAContainerFactory.makeBatchable(ArquivoRemessaNfe.class, ConnUtil.getEntity());		
		container.setAutoCommit(false);
		
		return container;
	}
	
	@Override
	public Table buildTbGeneric() {
		tb = new Table(null, buildContainer()){
			
			@Override
			protected String formatPropertyValue(Object rowId, Object colId,
					Property<?> property) {
				
				return super.formatPropertyValue(rowId, colId, property);			
			}
		};
		tb.setSizeFull();
		tb.setSelectable(true);
		tb.setColumnCollapsingAllowed(true);
		tb.setVisibleColumns(new Object[]{"id","descricao","data_cadastro"});	
		
		tb.setColumnCollapsed("id", true);	
				
		tb.setColumnHeader("id", "Código");
		tb.setColumnHeader("descricao", "Descrição");
		tb.setColumnHeader("data_cadastro", "Data Cadastro");
		
		tb.setColumnWidth("data_cadastro", 144);
								
		tb.setImmediate(true);
		
		tb.addListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(event.getProperty().getValue() != null){
					//btEditar.setEnabled(true);
					btExcluir.setEnabled(true);
				}else{
					//btEditar.setEnabled(false);
					btExcluir.setEnabled(false);
				}
			}
		});

		tb.addGeneratedColumn("*",new Table.ColumnGenerator() {
			
			@Override
			public Object generateCell(final Table source, final Object itemId, final Object columnId) {
				
				
				Button btBaixar = new Button("Baixar", new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						
						 gerarZipFile(source, itemId, columnId);
				        
					}
				});
				btBaixar.setStyleName(Reindeer.BUTTON_LINK);
				
				return btBaixar;
			}
		});
		tb.setColumnWidth("*", 57);
		tb.setColumnAlignment("*", Align.CENTER);
		
		
		return tb;
	}
	private void gerarZipFile(final Table source, final Object itemId, Object columnId){
		SimpleDateFormat sdfAno = new SimpleDateFormat("yy");						
	       
        try{
        	
        	String anoReferencia = source.getItem(itemId).getItemProperty("ano_referencia").getValue().toString();
        	
        	String arquivoContador = "CONTADOR"+anoReferencia.substring(2)+getNumerMonth(source.getItem(itemId).getItemProperty("mes_referencia").getValue().toString())+".txt";
        	String MestreName  = "PE"+OpusERP4UI.getEmpresa().getCnpj()+"21"+"A00"+anoReferencia.substring(2)+getNumerMonth(source.getItem(itemId).getItemProperty("mes_referencia").getValue().toString())+"N01M.001";
        	String ItemName = "PE"+OpusERP4UI.getEmpresa().getCnpj()+"21"+"A00"+anoReferencia.substring(2)+getNumerMonth(source.getItem(itemId).getItemProperty("mes_referencia").getValue().toString())+"N01I.001";
        	String DadosName = "PE"+OpusERP4UI.getEmpresa().getCnpj()+"21"+"A00"+anoReferencia.substring(2)+getNumerMonth(source.getItem(itemId).getItemProperty("mes_referencia").getValue().toString())+"N01D.001";
        	
        	InputStream isContador = new ByteArrayInputStream(createFileContatos(source.getItem(itemId).getItemProperty("ano_referencia").getValue().toString(), source.getItem(itemId).getItemProperty("mes_referencia").getValue().toString()));
        	
        	
        	InputStream isM = new ByteArrayInputStream(createFileNfeMestre(source.getItem(itemId).getItemProperty("ano_referencia").getValue().toString(), source.getItem(itemId).getItemProperty("mes_referencia").getValue().toString()));
        	InputStream isI = new ByteArrayInputStream(createFileNfeItem(source.getItem(itemId).getItemProperty("ano_referencia").getValue().toString(), source.getItem(itemId).getItemProperty("mes_referencia").getValue().toString()));
        	
        	byte[] conteudoDados = createFileNfeDados(source.getItem(itemId).getItemProperty("ano_referencia").getValue().toString(), source.getItem(itemId).getItemProperty("mes_referencia").getValue().toString());
        	InputStream isD = new ByteArrayInputStream(conteudoDados);
        	
        	
        	String anoRef = source.getItem(itemId).getItemProperty("ano_referencia").getValue().toString();
        	String mesRef = getCodMes( source.getItem(itemId).getItemProperty("mes_referencia").getValue().toString());
        	String finalidade =source.getItem(itemId).getItemProperty("finalidade").getValue().toString();
	        String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();	
	        final File zipFile = new File(basepath + "/WEB-INF/uploads/"+anoRef+mesRef+finalidade+".zip");
	        ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile)));
	        
	        
	        ZipEntry ze4 = new ZipEntry(arquivoContador);
	        zos.putNextEntry(ze4);
	        byte[] bytes = new byte[258];				        
	        int count4 = isContador.read(bytes);
	        while (count4 > -1)
	        {
	            zos.write(bytes, 0, count4);
	            count4 = isContador.read(bytes);
	        }
	        isContador.close();
	        zos.closeEntry();
	        
	        ZipEntry ze = new ZipEntry(MestreName);
	        zos.putNextEntry(ze);
	       			        
	        int count = isM.read(bytes);
	        while (count > -1)
	        {
	            zos.write(bytes, 0, count);
	            count = isM.read(bytes);
	        }
	        isM.close();
	        zos.closeEntry();
	        
	        ZipEntry ze1 = new ZipEntry(ItemName);
	        zos.putNextEntry(ze1);
	        int count1 = isI.read(bytes);
	        while (count1 > -1)
	        {
	            zos.write(bytes, 0, count1);
	            count1 = isI.read(bytes);
	        }
	        isI.close();					        
	        zos.closeEntry();
	        
	        ZipEntry ze2 = new ZipEntry(DadosName);
	        zos.putNextEntry(ze2);
	        int count2 = isD.read(bytes);
	        while (count2 > -1)
	        {
	            zos.write(bytes, 0, count2);
	            count2 = isD.read(bytes);
	        }
	        isD.close();					        
	        zos.closeEntry();
	        
	        zos.close();
	        
	        StreamSource ssZip = new StreamSource() {
	            InputStream is = new ByteArrayInputStream(readImageOldWay(zipFile)); 
	            @Override
	            public InputStream getStream() {
	                return is;
	            }
	        };
	        
	        StreamResource srZip = new StreamResource(ssZip, zipFile.getName());					        
	        Page.getCurrent().open(srZip, "_blank", true);
	        
	        
	        File file = new File("F:\\TESTE_NFE\\"+DadosName);
	        file.createNewFile();
	        
	        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "Cp1252"));
	        writer.write(createFileNfeDados_String(source.getItem(itemId).getItemProperty("ano_referencia").getValue().toString(), source.getItem(itemId).getItemProperty("mes_referencia").getValue().toString()));
	        
        
	        

        }catch(Exception e){
        	e.printStackTrace();
        }
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
	
	public byte[] createFileContatos(String ano_ref, String mes_ref){
			
		try{
			StringBuilder sb = new StringBuilder();
			String quebra = "\r\n";
			
			List<NfeMestre> nfes = NfeDAO.getNfe(ano_ref+getCodMes(mes_ref));
			
			SimpleDateFormat sdfAno = new SimpleDateFormat("dd/MM/yyyy");	
	
			sb.append("01");																								//Identificador
			sb.append("0000061");    																						//Código da Empresa
			sb.append(OpusERP4UI.getEmpresa().getCnpj());																	//CNPJ da Empresa		
			sb.append("01/09/2017");																						//Data Inicial das Notas Fiscais		
			sb.append("31/09/2017");																						//Data Final das Notas Fiscais		
			sb.append("N");																									//Valor Fixo "N"		
			sb.append("03");																								//Tipo da Nota		
			sb.append("00000");																								//Constante	
			sb.append("0"); 																								//Sistema		
			sb.append("17");																								//Valor Fixo 
			sb.append(quebra);
			
			Integer i = 1;
			for (NfeMestre nfe : nfes) {
				sb.append("02");											 												//Identificador
				sb.append(StringUtil.preencheCom(String.valueOf(i++), "0", 7, 1));											//Sequencial
				sb.append("0000061");    								     												//Código da Empresa
				sb.append(StringUtil.preencheCom(nfe.getCliente().getDoc_cpf_cnpj().length() > 14 ? 
						nfe.getCliente().getDoc_cpf_cnpj().substring(0, 14) : 
							nfe.getCliente().getDoc_cpf_cnpj(), " ", 14, 2)); 												//Inscrição do Cliente
				sb.append(StringUtil.preencheCom("21", "0", 7, 1));		   								     				//Código da Espécie
				sb.append("00");    										 												//Código da Exclusão da Dief
				sb.append(StringUtil.preencheCom("50", "0", 7, 1));															//Código do Acumulador
				sb.append(StringUtil.preencheCom(nfe.getContrato().getCfop_nfe().toString(), "0", 7, 1)); 				 	//CFOP
				sb.append(nfe.getCliente().getEndereco_principal().getUf()); 												//Sigla do Estado do Cliente
				sb.append("00");    										 												//Seguimento			
				sb.append(StringUtil.preencheCom(nfe.getId().toString(), "0", 7, 1)); 										//Número do Documento			
				sb.append(StringUtil.preencheCom(nfe.getSerie(), "0", 7, 1));         						 				//Serie
				sb.append(StringUtil.preencheCom(nfe.getId().toString(), "0", 7, 1));   						 			//Documento Final		
				sb.append(sdfAno.format(nfe.getData_prestacao()));  		 												//Data Saída
				sb.append(sdfAno.format(nfe.getData_emissao()));			 												//Data Emissão
			

				sb.append(StringUtil.preencheCom(String.format("%.2f",nfe.getBc_icms()).replace(",", ""), "0", 13, 1));		//Valor Contábil
				sb.append("0000000000000");																					//Valor  da Exclusão da Dief
				sb.append("                              "); 			     												//Reservado | ModalidadeFrete			
				sb.append("0002333");    										     										//Código Municipio
				sb.append("   ");    										     											//Fato Gerador da CRF //Fato Gerador da CRFOP //Fato Gerador da IRRFP			   										     												
				sb.append("1");    										     												//Tipo de Receita
				sb.append(" ");
				sb.append("0000000");
				sb.append("0000000");
				sb.append("00000000");
				sb.append(sdfAno.format(nfe.getData_emissao()));
				sb.append("0000000");
				sb.append("0000000000000");
				sb.append("0000000000000");
				sb.append("0000000000000");
				sb.append(StringUtil.preencheCom(String.format("%.2f",nfe.getBc_icms()).replace(",", ""), "0", 13, 1));
				sb.append("0");
				sb.append("0000000000000");
				sb.append("0000000000000");
				sb.append("0000000000000");
				sb.append("0000000000000");
				sb.append("0000000");
				sb.append("0000000");
				sb.append("0000000");
				sb.append("0000000");
				sb.append("00");
				sb.append("                                                  ");
				sb.append("                    ");
				sb.append("                    ");
				sb.append("                                                                                                                                                                                                                                                                                                            ");
				sb.append("                                            ");
				sb.append("      ");
				sb.append("E");
				sb.append("0");
				sb.append("                                            ");
				sb.append("                                                ");
				sb.append("0000000");
				sb.append("                                                                                                                                                                                                                                                               ");
				sb.append("0000000");
				sb.append("0000");
				sb.append("0000000000000");
				sb.append("0000000000000");
				sb.append("00000");
				sb.append("00000");
				sb.append("0000");
				sb.append("             ");
				
				
				sb.append(quebra);
				
				//if(i == 3){
				//	break;
				//}
			}
			
			return sb.toString().getBytes("UTF-8");
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public byte[] createFileNfeMestre(String ano_ref, String mes_ref){	
		StringBuilder sb = new StringBuilder();
		String quebra = "\r\n";
		
		
				
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat sdfAno = new SimpleDateFormat("yy");
		
		
		List<NfeMestre> nfes = NfeDAO.getNfe(ano_ref+getCodMes(mes_ref));
		Integer i = 0;
		for (NfeMestre nfeMestre : nfes) {
			
				i++;
				String cpf_cnpj = StringUtil.preencheCom(nfeMestre.getCliente().getDoc_cpf_cnpj(), "0", 14, 1);
				String ie =nfeMestre.getCliente().getDoc_cpf_cnpj().length() > 11 ? StringUtil.preencheCom(nfeMestre.getCliente().getDoc_rg_insc_estadual() == null || nfeMestre.getCliente().getDoc_rg_insc_estadual().replaceAll(" ", "") == null || nfeMestre.getCliente().getDoc_rg_insc_estadual().replaceAll(" ", "").equals("") || nfeMestre.getCliente().getDoc_rg_insc_estadual().replaceAll(" ", "").equals("0") ? "ISENTO" : nfeMestre.getCliente().getDoc_rg_insc_estadual()," ",14,2) : StringUtil.preencheCom("ISENTO", " ", 14, 2);
				String razao_social = StringUtil.preencheCom(nfeMestre.getCliente().getNome_razao().length() > 35 ? nfeMestre.getCliente().getNome_razao().substring(0, 35) :nfeMestre.getCliente().getNome_razao() , " ", 35, 2);
				String uf = nfeMestre.getContrato().getEndereco().getUf();
				String classe_consumo = "0";
				String fase_tipo_utilizacao_e_grupo_tensao = "400";
				String codigo_identificacao_cliente =StringUtil.preencheCom(nfeMestre.getCliente().getId().toString()," ",12,2);
				String data_emissao = sdf.format(nfeMestre.getData_emissao());
				String modelo_serie = "21A00";
				String numero = StringUtil.preencheCom(nfeMestre.getId().toString(), "0", 9, 1);
				
				//Código de Autenticação Digital do documento fiscal
				String valor_total = StringUtil.preencheCom(Real.formatDbToString(String.valueOf(nfeMestre.getBc_icms())).replace(",", ""),"0",12,1);		  
				String valor_bc_icms = StringUtil.preencheCom(Real.formatDbToString(String.valueOf(nfeMestre.getBc_icms())).replace(",", ""),"0",12,1);
				String valor_icms_destacado = "000000000000";
				String valor_op_isentas = "000000000000";
				String outros_valores = "000000000000";
				String situacao_doc = nfeMestre.getSituacao_doc();
				String ano_mes = sdfAno.format(nfeMestre.getData_emissao())+getCodMes(mes_ref);
				String refe_item_nf = StringUtil.preencheCom(i.toString(), "0", 9, 1);
				String numero_terminal_telefonico = StringUtil.preencheCom(nfeMestre.getCliente().getDdd_fone1()+nfeMestre.getCliente().getTelefone1(), " ", 12, 2);				
				String indicacao_tipo_info = cpf_cnpj.length() == 11  ? "2" : "1";
				
				
				String tipo_cliente = indicacao_tipo_info.equals("2")  ? "03" : "01";
				
				String subClasseConsumo = "00";
				String cnpjEmitente = OpusERP4UI.getEmpresa().getCnpj();
				String numeroCodFaturaComercial = StringUtil.preencheCom(nfeMestre.getContas_receber().getId().toString()," ",20,2);
				String valorTotalFaturaComercial = valor_total;
				
				String dataLeituraAnterior = "00000000";
				String dataLeituraAtua = "00000000";
				String brancos_reserv1 = StringUtil.preencheCom(" ", " ", 50, 2);
				String brancos_reserv2 = "00000000";
				String infAdicionais =  StringUtil.preencheCom(" ", " ",30, 2);
				String brancos_reserv = StringUtil.preencheCom(" ", " ", 5, 2);
				
				
				
				String cod_aut_dig_doc_fis = DigestUtils.md5Hex(cpf_cnpj+numero+valor_total+valor_bc_icms+valor_icms_destacado+data_emissao+cnpjEmitente).toUpperCase();
				String cod_aut_dig_registro =DigestUtils.md5Hex(cpf_cnpj+ie+razao_social+uf+classe_consumo+fase_tipo_utilizacao_e_grupo_tensao+
						codigo_identificacao_cliente+data_emissao+modelo_serie+numero+cod_aut_dig_doc_fis+valor_total+valor_bc_icms+valor_icms_destacado+valor_op_isentas+outros_valores+
						situacao_doc+ano_mes+refe_item_nf+numero_terminal_telefonico+indicacao_tipo_info+tipo_cliente+subClasseConsumo+numero_terminal_telefonico+cnpjEmitente+numeroCodFaturaComercial+
						valorTotalFaturaComercial+dataLeituraAnterior+dataLeituraAtua+brancos_reserv1+brancos_reserv2+infAdicionais+brancos_reserv).toUpperCase();
						
								
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
				sb.append(valor_icms_destacado);
				sb.append(valor_op_isentas);
				sb.append(outros_valores);						
				sb.append(situacao_doc);
				sb.append(ano_mes);
				sb.append(refe_item_nf);
				sb.append(numero_terminal_telefonico);
				sb.append(indicacao_tipo_info);
				sb.append(tipo_cliente);
				sb.append(subClasseConsumo);
				sb.append(numero_terminal_telefonico);
				sb.append(cnpjEmitente);
				sb.append(numeroCodFaturaComercial);
				sb.append(valorTotalFaturaComercial);
				sb.append(dataLeituraAnterior);
				sb.append(dataLeituraAtua);
				sb.append(brancos_reserv1);
				sb.append(brancos_reserv2);
				sb.append(infAdicionais);				
				sb.append(brancos_reserv);				
				sb.append(cod_aut_dig_registro);
				
				sb.append(quebra);
				
		}

		return sb.toString().getBytes();
	}
	public byte[] createFileNfeItem(String ano_ref, String mes_ref){
		StringBuilder sb = new StringBuilder();
		String quebra = "\r\n";
				
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat sdfAno = new SimpleDateFormat("yy");
		
		
		List<NfeMestre> nfes = NfeDAO.getNfe(ano_ref+getCodMes(mes_ref));
		for (NfeMestre nfeMestre : nfes) {
			
				String cpf_cnpj = StringUtil.preencheCom(nfeMestre.getCliente().getDoc_cpf_cnpj(), "0", 14, 1);
				String uf = nfeMestre.getContrato().getEndereco().getUf();				
				String classe_consumo = "0";
				String fase_tipo_utilizacao_e_grupo_tensao = "400";
				String data_emissao = sdf.format(nfeMestre.getData_emissao());
				String modelo_serie = "21A00";
				String numero = StringUtil.preencheCom(nfeMestre.getId().toString(), "0", 9, 1);
				String cfop = nfeMestre.getNatOpe().getId().toString();
				String item = "001";
				if(nfeMestre.getContrato() != null && nfeMestre.getContrato().getPlano() != null && nfeMestre.getContrato().getPlano().getServico_plano() != null){
				}else{
					System.out.println(nfeMestre.getId());
				}
				String cod_item =  StringUtil.preencheCom(nfeMestre.getContrato() != null && nfeMestre.getContrato().getPlano() != null && nfeMestre.getContrato().getPlano().getServico_plano() != null ? nfeMestre.getContrato().getPlano().getServico_plano().getId().toString() : "", " ", 10, 2);
				String descricao_item = removerAcentos(StringUtil.preencheCom(nfeMestre.getContrato().getPlano().getServico_plano().getDescricao().length() > 40 ? nfeMestre.getContrato().getPlano().getServico_plano().getDescricao().substring(0, 40) : nfeMestre.getContrato().getPlano().getServico_plano().getDescricao(), " ", 40, 2));
				String cod_class_item = "0104";
				String unidade =StringUtil.preencheCom(nfeMestre.getContrato().getPlano().getServico_plano().getUnidade(), " ", 6, 2);
				String qtd_contratada_e_qtd_fornecida = "000000001000000000001000";
				String valor_total = StringUtil.preencheCom(Real.formatDbToString(String.valueOf(nfeMestre.getBc_icms())).replace(",", ""),"0",11,1);
				String desconto_redutores =  "00000000000";
				String acresc_despesas_ace = "00000000000";
				String bc_icms  = valor_total;
				String icms = "00000000000";
				String operacoe_isentas   = "00000000000";
				String outros_valores = "00000000000";
				
			
				String aliquota_icms = "0000";
				String situacao  =nfeMestre.getSituacao_doc();
				String ano_mes_ref = sdfAno.format(nfeMestre.getData_emissao())+getCodMes(mes_ref);
				String numero_contrato = StringUtil.preencheCom(nfeMestre.getContrato().getId().toString(), " ",15,2);
				String qtd_faturada = "000000000000";
				String tarifa_aplicada = "00000000000";
				String aliquota_pis_pasep = "000000";
				String pis_pasep = "00000000000";
				String aliquota_cofins = "000000";
				String cofins = "00000000000";
				String indicador_desconto_judicial = " ";
				String tipo_isencao_reducao_base_calculo = "00";
				String brancos = "     ";
				String cod_aut_dig_regis = DigestUtils.md5Hex(cpf_cnpj+uf+classe_consumo+fase_tipo_utilizacao_e_grupo_tensao+data_emissao+modelo_serie+numero+cfop+item+cod_item+
						descricao_item+cod_class_item+unidade+qtd_contratada_e_qtd_fornecida+valor_total+desconto_redutores+acresc_despesas_ace+bc_icms+icms+operacoe_isentas+
						outros_valores+aliquota_icms+situacao+ano_mes_ref+numero_contrato+qtd_faturada+tarifa_aplicada+aliquota_pis_pasep+pis_pasep+aliquota_cofins+cofins+
						indicador_desconto_judicial+tipo_isencao_reducao_base_calculo+brancos).toUpperCase() ;
								
				sb.append(cpf_cnpj);
				sb.append(uf);
				sb.append(classe_consumo);
				sb.append(fase_tipo_utilizacao_e_grupo_tensao);
				sb.append(data_emissao);
				sb.append(modelo_serie);
				sb.append(numero);
				sb.append(cfop);
				sb.append(item);
				sb.append(cod_item);
				sb.append(descricao_item);
				sb.append(cod_class_item);
				sb.append(unidade);
				sb.append(qtd_contratada_e_qtd_fornecida);
				sb.append(valor_total);
				sb.append(desconto_redutores);
				sb.append(acresc_despesas_ace);
				sb.append(bc_icms);
				sb.append(icms);
				sb.append(operacoe_isentas);
				sb.append(outros_valores);
				sb.append(aliquota_icms);
				sb.append(situacao);
				sb.append(ano_mes_ref);
				sb.append(numero_contrato);
				sb.append(qtd_faturada);
				sb.append(tarifa_aplicada);
				sb.append(aliquota_pis_pasep);
				sb.append(pis_pasep);
				sb.append(aliquota_cofins);
				sb.append(cofins);
				sb.append(indicador_desconto_judicial);
				sb.append(tipo_isencao_reducao_base_calculo);
				sb.append(brancos);
				sb.append(cod_aut_dig_regis);
				
				sb.append(quebra);
				
		}

		return sb.toString().getBytes();
	}
	public byte[] createFileNfeDados(String ano_ref, String mes_ref) throws UnsupportedEncodingException, NoSuchAlgorithmException{
		
		StringBuilder sb = new StringBuilder();
		
		String quebra = "\r\n";
				
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat sdfAno = new SimpleDateFormat("yy");
		
		 
		List<NfeMestre> nfes = NfeDAO.getNfe(ano_ref+getCodMes(mes_ref));
		for (NfeMestre nfeMestre : nfes) {
			
				
				String cpf_cnpj = StringUtil.preencheCom(nfeMestre.getCliente().getDoc_cpf_cnpj(), "0", 14, 1);
				String ie =nfeMestre.getCliente().getDoc_cpf_cnpj().length() > 11 ? StringUtil.preencheCom(nfeMestre.getCliente().getDoc_rg_insc_estadual() == null || nfeMestre.getCliente().getDoc_rg_insc_estadual().replaceAll(" ", "") == null || nfeMestre.getCliente().getDoc_rg_insc_estadual().replaceAll(" ", "").equals("") || nfeMestre.getCliente().getDoc_rg_insc_estadual().replaceAll(" ", "").equals("0") ? "ISENTO" : nfeMestre.getCliente().getDoc_rg_insc_estadual()," ",14,2) : StringUtil.preencheCom("ISENTO", " ", 14, 2);
				String razao_social = StringUtil.preencheCom(nfeMestre.getCliente().getNome_razao().length() > 35 ? nfeMestre.getCliente().getNome_razao().substring(0, 35) :nfeMestre.getCliente().getNome_razao() , " ", 35, 2);
				String logradouro =  removerAcentos(StringUtil.preencheCom(nfeMestre.getContrato().getEndereco().getEndereco().length() > 45 ? nfeMestre.getContrato().getEndereco().getEndereco().substring(0, 45) : nfeMestre.getContrato().getEndereco().getEndereco(), " ", 45, 2));
				logradouro = StringUtil.preencheCom(logradouro.replaceAll("^\\s+", ""), " ", 45, 2);
				String numero = StringUtil.preencheCom(nfeMestre.getContrato().getEndereco().getNumero(), "0", 5, 1);
				String complemento = StringUtil.preencheCom(nfeMestre.getContrato().getEndereco().getComplemento().length() > 15 ? removerAcentos(nfeMestre.getContrato().getEndereco().getComplemento().substring(0, 15)) : removerAcentos(nfeMestre.getContrato().getEndereco().getComplemento()), " ", 15, 2);
				complemento = StringUtil.preencheCom(complemento.replaceAll("^\\s+", ""), " ", 15, 2);
				
				
				String cep = StringUtil.preencheCom(nfeMestre.getContrato().getEndereco().getCep(), "0", 8, 1);
				String bairro = removerAcentos(StringUtil.preencheCom(nfeMestre.getContrato().getEndereco().getBairro().length() > 15 ? nfeMestre.getContrato().getEndereco().getBairro().substring(0, 15) : nfeMestre.getContrato().getEndereco().getBairro(), " ", 15, 2));
				String municipio = removerAcentos(StringUtil.preencheCom(nfeMestre.getContrato().getEndereco().getCidade().length() > 30 ? nfeMestre.getContrato().getEndereco().getCidade().substring(0, 30) : nfeMestre.getContrato().getEndereco().getCidade(), " ", 30, 2));
				municipio =StringUtil.preencheCom( getCidade(municipio.toLowerCase()), " ", 30, 2);
				
				String uf = nfeMestre.getContrato().getEndereco().getUf();
				
				String dddtel = nfeMestre.getCliente().getDdd_fone1()+nfeMestre.getCliente().getTelefone1();				 
				String telefone_contato = StringUtil.preencheCom(dddtel.length() > 12 ? dddtel.substring(0, 12) : dddtel, " ", 12, 2);
				String cod_identificacao_consumidor_assinante = StringUtil.preencheCom(nfeMestre.getCliente().getId().toString(), " ", 12, 2);				
				String numero_terminal_telefonico = StringUtil.preencheCom(nfeMestre.getCliente().getDdd_fone1()+nfeMestre.getCliente().getTelefone1(), " ", 12, 2);
				String uf_habilitacao_terminal_telefonico = nfeMestre.getContrato().getEndereco().getUf();
				String data_emissao  = sdf.format(nfeMestre.getData_emissao());
				String modelo_e_serie = "21A00";				
				String numero9 = StringUtil.preencheCom(nfeMestre.getId().toString(), "0", 9, 1);
				String cod_municipio = "2601706";			
				//String mmm = municipio;
				if(municipio.equals(StringUtil.preencheCom( "Sao Bento Do Una", " ", 30, 2))){
					
					municipio = StringUtil.preencheCom("São Bento do Una" , " ", 30, 2);
					cod_municipio = "2613008";
				}
				
				if(municipio.equals(StringUtil.preencheCom( "Sanharo", " ", 30, 2))){
					municipio = StringUtil.preencheCom( "Sanharó", " ", 30, 2);
					cod_municipio = "2612406";
				}
				
				if(municipio.equals(StringUtil.preencheCom( "Serra Do Vento (belo Jardim)", " ", 30, 2))){
					municipio = StringUtil.preencheCom( "Belo Jardim", " ", 30, 2);
					cod_municipio = "2601706";
				}
				
				if(municipio.equals(StringUtil.preencheCom( "Salgueiro", " ", 30, 2))){					
					cod_municipio = "2612208";
				}
				
				if(municipio.equals(StringUtil.preencheCom( "Fortaleza", " ", 30, 2))){					
					cod_municipio = "2304400";
				}
				
				String brancos = "     ";
				
			
				
				StringBuilder sb2 = new StringBuilder();
				sb2.append(cpf_cnpj);
				sb2.append(ie);
				sb2.append(razao_social);
				sb2.append(logradouro);
				sb2.append(numero);
				sb2.append(complemento);
				sb2.append(cep);
				sb2.append(bairro);
				sb2.append(municipio);
				sb2.append(uf);
				sb2.append(telefone_contato);
				sb2.append(cod_identificacao_consumidor_assinante);
				sb2.append(numero_terminal_telefonico);
				sb2.append(uf_habilitacao_terminal_telefonico);
				sb2.append(data_emissao);
				sb2.append(modelo_e_serie);
				sb2.append(numero9);
				sb2.append(cod_municipio);
				sb2.append(brancos);
				
             
              
				String cod_aut_dig_reg2 = Criptografia.criptografar(sb2.toString());
				//String cod_aut_dig_reg2 =DigestUtils.md5Hex(sb2.toString()).toUpperCase();
				
				if(!municipio.equals(StringUtil.preencheCom( "Belo Jardim", " ", 30, 2))){
					System.out.println(cod_aut_dig_reg2);
				}
				

				sb.append(cpf_cnpj);
				sb.append(ie);
				sb.append(razao_social);
				sb.append(logradouro);
				sb.append(numero);
				sb.append(complemento);
				sb.append(cep);
				sb.append(bairro);
				sb.append(municipio);
				sb.append(uf);
				sb.append(telefone_contato);
				sb.append(cod_identificacao_consumidor_assinante);				
				sb.append(numero_terminal_telefonico);
				sb.append(uf_habilitacao_terminal_telefonico);
				sb.append(data_emissao);
				sb.append(modelo_e_serie);
				sb.append(numero9);
				sb.append(cod_municipio);
				sb.append(brancos);
				sb.append(cod_aut_dig_reg2.toUpperCase());			
				sb.append(quebra);
				
		}

		return sb.toString().getBytes(StandardCharsets.ISO_8859_1);
	}
	
	public String createFileNfeDados_String(String ano_ref, String mes_ref) {
		
		StringBuilder sb = new StringBuilder();
		
		String quebra = "\r\n";
				
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat sdfAno = new SimpleDateFormat("yy");
		
		 
		List<NfeMestre> nfes = NfeDAO.getNfe(ano_ref+getCodMes(mes_ref));
		for (NfeMestre nfeMestre : nfes) {
			
				
				String cpf_cnpj = StringUtil.preencheCom(nfeMestre.getCliente().getDoc_cpf_cnpj(), "0", 14, 1);
				String ie =nfeMestre.getCliente().getDoc_cpf_cnpj().length() > 11 ? StringUtil.preencheCom(nfeMestre.getCliente().getDoc_rg_insc_estadual() == null || nfeMestre.getCliente().getDoc_rg_insc_estadual().replaceAll(" ", "") == null || nfeMestre.getCliente().getDoc_rg_insc_estadual().replaceAll(" ", "").equals("") || nfeMestre.getCliente().getDoc_rg_insc_estadual().replaceAll(" ", "").equals("0") ? "ISENTO" : nfeMestre.getCliente().getDoc_rg_insc_estadual()," ",14,2) : StringUtil.preencheCom("ISENTO", " ", 14, 2);
				String razao_social = StringUtil.preencheCom(nfeMestre.getCliente().getNome_razao().length() > 35 ? nfeMestre.getCliente().getNome_razao().substring(0, 35) :nfeMestre.getCliente().getNome_razao() , " ", 35, 2);
				String logradouro =  removerAcentos(StringUtil.preencheCom(nfeMestre.getContrato().getEndereco().getEndereco().length() > 45 ? nfeMestre.getContrato().getEndereco().getEndereco().substring(0, 45) : nfeMestre.getContrato().getEndereco().getEndereco(), " ", 45, 2));
				logradouro = StringUtil.preencheCom(logradouro.replaceAll("^\\s+", ""), " ", 45, 2);
				String numero = StringUtil.preencheCom(nfeMestre.getContrato().getEndereco().getNumero(), "0", 5, 1);
				String complemento = StringUtil.preencheCom(nfeMestre.getContrato().getEndereco().getComplemento().length() > 15 ? removerAcentos(nfeMestre.getContrato().getEndereco().getComplemento().substring(0, 15)) : removerAcentos(nfeMestre.getContrato().getEndereco().getComplemento()), " ", 15, 2);
				complemento = StringUtil.preencheCom(complemento.replaceAll("^\\s+", ""), " ", 15, 2);
				
				
				String cep = StringUtil.preencheCom(nfeMestre.getContrato().getEndereco().getCep(), "0", 8, 1);
				String bairro = removerAcentos(StringUtil.preencheCom(nfeMestre.getContrato().getEndereco().getBairro().length() > 15 ? nfeMestre.getContrato().getEndereco().getBairro().substring(0, 15) : nfeMestre.getContrato().getEndereco().getBairro(), " ", 15, 2));
				String municipio = removerAcentos(StringUtil.preencheCom(nfeMestre.getContrato().getEndereco().getCidade().length() > 30 ? nfeMestre.getContrato().getEndereco().getCidade().substring(0, 30) : nfeMestre.getContrato().getEndereco().getCidade(), " ", 30, 2));
				municipio =StringUtil.preencheCom( getCidade(municipio.toLowerCase()), " ", 30, 2);
				
				String uf = nfeMestre.getContrato().getEndereco().getUf();
				
				String dddtel = nfeMestre.getCliente().getDdd_fone1()+nfeMestre.getCliente().getTelefone1();				 
				String telefone_contato = StringUtil.preencheCom(dddtel.length() > 12 ? dddtel.substring(0, 12) : dddtel, " ", 12, 2);
				String cod_identificacao_consumidor_assinante = StringUtil.preencheCom(nfeMestre.getCliente().getId().toString(), " ", 12, 2);				
				String numero_terminal_telefonico = StringUtil.preencheCom(nfeMestre.getCliente().getDdd_fone1()+nfeMestre.getCliente().getTelefone1(), " ", 12, 2);
				String uf_habilitacao_terminal_telefonico = nfeMestre.getContrato().getEndereco().getUf();
				String data_emissao  = sdf.format(nfeMestre.getData_emissao());
				String modelo_e_serie = "21A00";				
				String numero9 = StringUtil.preencheCom(nfeMestre.getId().toString(), "0", 9, 1);
				String cod_municipio = "2601706";			
				//String mmm = municipio;
				if(municipio.equals(StringUtil.preencheCom( "Sao Bento Do Una", " ", 30, 2))){
					
					municipio = StringUtil.preencheCom("São Bento do Una" , " ", 30, 2);
					cod_municipio = "2613008";
				}
				
				if(municipio.equals(StringUtil.preencheCom( "Sanharo", " ", 30, 2))){
					municipio = StringUtil.preencheCom( "Sanharó", " ", 30, 2);
					cod_municipio = "2612406";
				}
				
				if(municipio.equals(StringUtil.preencheCom( "Serra Do Vento (belo Jardim)", " ", 30, 2))){
					municipio = StringUtil.preencheCom( "Belo Jardim", " ", 30, 2);
					cod_municipio = "2601706";
				}
				
				if(municipio.equals(StringUtil.preencheCom( "Salgueiro", " ", 30, 2))){					
					cod_municipio = "2612208";
				}
				
				if(municipio.equals(StringUtil.preencheCom( "Fortaleza", " ", 30, 2))){					
					cod_municipio = "2304400";
				}
				
				String brancos = "     ";
				
			
				
				StringBuilder sb2 = new StringBuilder();
				sb2.append(cpf_cnpj);
				sb2.append(ie);
				sb2.append(razao_social);
				sb2.append(logradouro);
				sb2.append(numero);
				sb2.append(complemento);
				sb2.append(cep);
				sb2.append(bairro);
				sb2.append(municipio);
				sb2.append(uf);
				sb2.append(telefone_contato);
				sb2.append(cod_identificacao_consumidor_assinante);
				sb2.append(numero_terminal_telefonico);
				sb2.append(uf_habilitacao_terminal_telefonico);
				sb2.append(data_emissao);
				sb2.append(modelo_e_serie);
				sb2.append(numero9);
				sb2.append(cod_municipio);
				sb2.append(brancos);
				
             
              
				String cod_aut_dig_reg2 = Criptografia.criptografar(sb2.toString());
				//String cod_aut_dig_reg2 =DigestUtils.md5Hex(sb2.toString()).toUpperCase();
				
				if(!municipio.equals(StringUtil.preencheCom( "Belo Jardim", " ", 30, 2))){
					System.out.println(cod_aut_dig_reg2);
				}
				

				sb.append(cpf_cnpj);
				sb.append(ie);
				sb.append(razao_social);
				sb.append(logradouro);
				sb.append(numero);
				sb.append(complemento);
				sb.append(cep);
				sb.append(bairro);
				sb.append(municipio);
				sb.append(uf);
				sb.append(telefone_contato);
				sb.append(cod_identificacao_consumidor_assinante);				
				sb.append(numero_terminal_telefonico);
				sb.append(uf_habilitacao_terminal_telefonico);
				sb.append(data_emissao);
				sb.append(modelo_e_serie);
				sb.append(numero9);
				sb.append(cod_municipio);
				sb.append(brancos);
				sb.append(cod_aut_dig_reg2.toUpperCase());			
				sb.append(quebra);
				
		}

		return sb.toString();
	}
	
	private String getCidade(String texto){
		
        String[] partes = texto.split(" ");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < partes.length; i++) {
            String word = partes[i];
            word = word.substring(0, 1).toUpperCase() + word.substring(1);
            sb.append(" ").append(word);
        }
        
        return sb.toString().replaceFirst(" ", "");
	}
	
	
	public static String removerAcentos(String str) {
	    return Normalizer.normalize(str, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
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
	public void writeFile(File file, byte[] data) throws IOException
	 {
	   OutputStream fo = new FileOutputStream(file);
	   // Write the data
	   fo.write(data);
	   // flush the file (down the toilet)
	   fo.flush();
	   // Close the door to keep the smell in.
	   fo.close();
	 }
	

	@Override
	public TextField buildTfbusca() {
		tfBusca = new TextField();
		tfBusca.setWidth("100%");
		tfBusca.setTextChangeEventMode(TextChangeEventMode.LAZY);
		tfBusca.focus();
		tfBusca.addListener(new FieldEvents.TextChangeListener() {
			
			@Override
			public void textChange(TextChangeEvent event) {
				addFilter(event.getText());
			}
		});
		return tfBusca;
	}

	@Override
	public void addFilter(String s) {

		container.removeAllContainerFilters();
		
		Object[] collums = tb.getVisibleColumns();		
		List<Filter> filtros = new ArrayList<Filter>();		
		
		for(Object c:collums){		 
			
			if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == String.class){					   	
				filtros.add(new Like(c.toString(), "%"+s+"%", false));
			}			
		}
		
		
		container.addContainerFilter(Filters.or(filtros));
		container.applyFilters();
		hlFloat.replaceComponent(lbRegistros, buildLbRegistros());
	}

	@Override
	public Button BuildbtTransferencia() {
		btNovo = new Button("Novo", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Gerar Arquivo Remessa"))				
				{					
						final BeanItem<ArquivoRemessaNfe> newItem = new BeanItem<ArquivoRemessaNfe>(new ArquivoRemessaNfe());
										
						ArquivosRemessaEditor cEditor = new ArquivosRemessaEditor(newItem, "Gerar arquivo de remessa", true);
						cEditor.addListerner(new ArquivosRemessaEditor.ArquivosRemessaListerner() {
							
							@Override
							public void onClose(ArquivosRemessaEvent event) {
								if(event.isConfirm()){
																	
									    ArquivoRemessaNfe arNfe  = newItem.getBean();
									    arNfe.setData_cadastro(new Date());
									    arNfe.setDescricao("Arquivos de Remessa Ref.:"+getNumerMonth(arNfe.getMes_referencia())+"/"+arNfe.getAno_referencia()+" - Status: Normal");
									    
									    
									    if(NfeDAO.getNfe(arNfe.getAno_referencia()+getCodMes(arNfe.getMes_referencia())).size() > 0){
									    	
									    	container.addEntity(arNfe);
									    	container.commit();										
									    	
									    	tb.focus();
									    	container.refresh();
									    	LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Gerou Nfe"));
									    	Notify.Show("Arquivo remessa gerado com sucesso!", Notify.TYPE_SUCCESS);
									    }else{
									    	container.discard();
									    	tb.discard();
									    	
									    	Notify.Show("Arquivo remessa  não foi gerado.", Notify.TYPE_WARNING);
									    	Notify.Show("Não ha notas geradas para o periodo selecionado!", Notify.TYPE_WARNING);
									    }
									    
									    
																		
								}
							}
						});
						
						cEditor.addCloseListener(new Window.CloseListener() {
							
							@Override
							public void windowClose(CloseEvent e) {
								tb.focus();							
							}
						});
						
						getUI().addWindow(cEditor);
				}else{					
					Notify.Show("Você nãio tem permissão para gerar arquivo de remessa", Notify.TYPE_ERROR);
				}
							
			}
			
		});
		
		return btNovo;
	}
	
	public String getNumerMonth(String month){
		
		switch (month) {
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
		
		return "";
	}

	@Override
	public Button BuildbtAceitar() {
		btEditar = new Button("Editar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				

				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Alterar"))				
				{
				
					CepEditor cEditor = new CepEditor(tb.getItem(tb.getValue()), "Editar CEP", true);
					cEditor.addListerner(new CepEditor.CepListerner() {
						
						@Override
						public void onClose(CepEvent event) {
							if(event.isConfirm()){
								container.commit();
								tb.focus();
								
								LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Editou um CEP"));
							}
						}
					});
					
					cEditor.addCloseListener(new Window.CloseListener() {
						
						@Override
						public void windowClose(CloseEvent e) {
							tb.focus();
							//refresh();
						}
					});
					
					getUI().addWindow(cEditor);
				
				}else{
					
					Notify.Show("Você não possui permissão para editar uma nfe", Notify.TYPE_ERROR);
				}
			}
		});
		btEditar.setEnabled(false);
		
		return btEditar;
	}

	@Override
	public Button BuildbtCancelar() {
		btExcluir = new Button("Excluir", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Excluir"))				
				{
					GenericDialog gDialog = new GenericDialog("Confirme para continuar!", "Você deseja realmente excluir a nfe selecionada?", true, true);
						gDialog.addListerner(new GenericDialog.DialogListerner() {
							
							@Override
							public void onClose(DialogEvent event) {
								if(event.isConfirm()){
									container.removeItem(tb.getValue());
									container.commit();									
									Notify.Show("Nfe excluída com Sucesso!", Notify.TYPE_SUCCESS);
									tb.focus();
									
									LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Excluiu uma nfe"));
							  }
							}
						});
						
						gDialog.addCloseListener(new Window.CloseListener() {
							
							@Override
							public void windowClose(CloseEvent e) {
								tb.focus();
							}
						});
					
					getUI().addWindow(gDialog);
				}else{					
					Notify.Show("Você não possui permissão para excluir uma nfe", Notify.TYPE_ERROR);
				}
			}
		});
		btExcluir.setEnabled(false);
		return btExcluir;
	}
	public Integer getCodSubModulo() {
		return codSubModulo;
	}

	public void setCodSubModulo(Integer codSubModulo) {
		this.codSubModulo = codSubModulo;
	}
}

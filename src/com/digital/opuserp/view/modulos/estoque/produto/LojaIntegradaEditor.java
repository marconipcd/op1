package com.digital.opuserp.view.modulos.estoque.produto;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.digital.opuserp.dao.GerenciarModuloDAO;
import com.digital.opuserp.dao.LojaIntegradaDAO;
import com.digital.opuserp.dao.PedidoDAO;
import com.digital.opuserp.dao.ProdutoDAO;
import com.digital.opuserp.domain.Produto;
import com.digital.opuserp.util.Real;
import com.digital.opuserp.view.util.Notify;
import com.digital.opuserp.view.util.ParameterStringBuilder;
import com.google.gson.JsonObject;
import com.vaadin.data.Item;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class LojaIntegradaEditor extends Window {

	
	VerticalLayout vlRoot = new VerticalLayout();
	private Button btSalvar;
	private Button btCancelar;
	private GerenciarModuloDAO gmDAO;
	Integer codSubModulo;
	
	private TextField txtCod;
	private TextField txtNome;
	private TextField txtGtin;
	private TextField txtPrecoVenda;
	private TextField txtPrecoPromocional;
	private ComboBox cbProdutoAtivado;
	private ComboBox cbProdutoDestaque;
	private TextArea txtDescricao;
	private TextField txtMarca;
	private TextField  txtUrlYoutube;
	
	Produto p;
	
	public LojaIntegradaEditor(String title, Produto p, boolean modal, Integer codSubModulo){
		
		this.p = p;	
		this.codSubModulo = codSubModulo;	
		
		setWidth("568px");
		setHeight("510px");
		
		//Styles style = Page.getCurrent().getStyles();		
		//style.add(".dashboard .v-filterselect-suggestpopup-sim-nao > .popupContent  > .v-filterselect-suggestmenu{width:93px;}");		
		
		gmDAO = new GerenciarModuloDAO();
		
		setCaption(title);
		setModal(modal);
		//setResizable(false);
		setClosable(false);
		center();
		
		vlRoot = new VerticalLayout();	
		vlRoot.setWidth("100%");
		vlRoot.setMargin(true);
				
		setContent(new VerticalLayout(){
			{
				setWidth("100%");
				//setHeight("620px");
				setMargin(true);

				addComponent(vlRoot);
				
				HorizontalLayout hlButtons = new HorizontalLayout();
				hlButtons.setStyleName("hl_buttons_bottom");
				hlButtons.setSpacing(true);
				hlButtons.setMargin(true);
				hlButtons.addComponent(buildBtCancelar());
				hlButtons.addComponent(buildBtSalvar());						
			
				addComponent(hlButtons);
				setComponentAlignment(hlButtons, Alignment.BOTTOM_RIGHT);
			}

		});

			buildLayout();
		
		

	}

	
	
	
	public void buildLayout(){

		txtCod = new TextField("Id Externo");
		txtCod.setWidth("60px");
		txtCod.setValue(p.getId().toString());
		txtCod.setEnabled(false);
		txtCod.setStyleName("caption-align");
				
		vlRoot.addComponent(new FormLayout(){
			{
				setMargin(true);
				setSpacing(true);
				setStyleName("form-cutom");
				addComponent(txtCod);
			}
		});
				
		txtNome = new TextField("Nome");		
		txtNome.setWidth("360px");
		txtNome.setValue(p.getNome_produto_loja() != null ? p.getNome_produto_loja() : p.getNome());		
		txtNome.setStyleName("caption-align");
		txtNome.setReadOnly(true); 
				
		vlRoot.addComponent(new FormLayout(){
			{
				setMargin(true);
				setSpacing(true);
				setStyleName("form-cutom");
				addComponent(txtNome);
			}
		});
				
		txtGtin = new TextField("Código de Barras");
		txtGtin.setValue(p.getgTin());
		txtGtin.setEnabled(false);
		txtGtin.setStyleName("caption-align");
		
		vlRoot.addComponent(new FormLayout(){
			{
				setMargin(true);
				setSpacing(true);
				setStyleName("form-cutom");
				addComponent(txtGtin);
			}
		});
				
		txtPrecoVenda = new TextField("Preço de Venda");
		txtPrecoVenda.setValue(Real.formatDbToString(p.getValorVenda().toString()));
		txtPrecoVenda.setReadOnly(false);
		txtPrecoVenda.setStyleName("caption-align");
		txtPrecoVenda.setId("txtPrecoVenda");
		txtPrecoVenda.setReadOnly(true); 
		
		JavaScript.getCurrent().execute("$('#txtPrecoVenda').maskMoney({decimal:',',thousands:'.'})");
		
		vlRoot.addComponent(new FormLayout(){
			{
				setMargin(true);
				setSpacing(true);
				setStyleName("form-cutom");
				addComponent(txtPrecoVenda);
			}
		});
		
		txtPrecoPromocional = new TextField("Preço promocional");
		txtPrecoPromocional.setValue(p.getPreco_promocional() != null ? Real.formatDbToString(p.getPreco_promocional().toString()) : "0,00");
		txtPrecoPromocional.setStyleName("caption-align");
		txtPrecoPromocional.setId("txtPrecoPromocional");
		txtPrecoPromocional.setReadOnly(true); 
		
		JavaScript.getCurrent().execute("$('#txtPrecoPromocional').maskMoney({decimal:',',thousands:'.'})");
		
		vlRoot.addComponent(new FormLayout(){
			{
				setMargin(true);
				setSpacing(true);
				setStyleName("form-cutom");
				addComponent(txtPrecoPromocional);
			}
		});
		
		cbProdutoAtivado = new ComboBox("Produto Ativado");		
		cbProdutoAtivado.setWidth("70px");
		cbProdutoAtivado.setStyleName("caption-align");
		cbProdutoAtivado.setNullSelectionAllowed(false);
		cbProdutoAtivado.addItem("SIM");
		cbProdutoAtivado.addItem("NAO");
		cbProdutoAtivado.select(p.getProduto_ativado() != null ? p.getProduto_ativado() : "NAO");
		
		vlRoot.addComponent(new FormLayout(){
			{
				setMargin(true);
				setSpacing(true);
				setStyleName("form-cutom");
				addComponent(cbProdutoAtivado);
			}
		});
		
		cbProdutoDestaque = new ComboBox("Produto Destaque");
		cbProdutoDestaque.setWidth("70px");
		cbProdutoDestaque.setStyleName("caption-align");
		cbProdutoDestaque.setNullSelectionAllowed(false);
		cbProdutoDestaque.addItem("SIM");
		cbProdutoDestaque.addItem("NAO");
		cbProdutoDestaque.select(p.getProduto_destaque() != null ? p.getProduto_destaque() : "NAO");
		
		vlRoot.addComponent(new FormLayout(){
			{
				setMargin(true);
				setSpacing(true);
				setStyleName("form-cutom");
				addComponent(cbProdutoDestaque);
			}
		});
				
		txtDescricao = new TextArea("Descrição");
		txtDescricao.setWidth("290px");
		txtDescricao.setHeight("100px");
		txtDescricao.setValue(p.getDesc_produto() != null ? p.getDesc_produto() : "");
		txtDescricao.setStyleName("caption-align");
		
		vlRoot.addComponent(new FormLayout(){
			{
				setMargin(true);
				setSpacing(true);
				setStyleName("form-cutom");
				addComponent(txtDescricao);
			}
		});
		
		final TextField txtEstoque = new TextField("Qtd.: Estoque");
		txtEstoque.setValue(p.getQtdEstoque().toString());
		txtEstoque.setStyleName("caption-align");
		txtEstoque.setReadOnly(true); 
		txtEstoque.setWidth("70px"); 
		
		vlRoot.addComponent(new FormLayout(){
			{
				setMargin(true);
				setSpacing(true);
				setStyleName("form-cutom");
				addComponent(txtEstoque); 
			}
		});
		
		txtMarca = new TextField("Marca");
		txtMarca.setWidth("290px");
		txtMarca.setValue(p.getMarcasId() != null ? p.getMarcasId().getNome() : "");
		txtMarca.setStyleName("caption-align");
		txtMarca.setReadOnly(true); 
		
		vlRoot.addComponent(new FormLayout(){
			{
				setMargin(true);
				setSpacing(true);
				setStyleName("form-cutom");
				addComponent(txtMarca); 
			}
		});
		
		
		txtUrlYoutube = new TextField("Url");
		txtUrlYoutube.setWidth("290px");
		txtUrlYoutube.setValue(p.getUrl_video_youtube() != null ? p.getUrl_video_youtube() : "");
		txtUrlYoutube.setStyleName("caption-align");
		
		vlRoot.addComponent(new FormLayout(){
			{
				setMargin(true);
				setSpacing(true);
				setStyleName("form-cutom");
				addComponent(txtUrlYoutube); 
			}
		});
		
		
	}
		
	
	private Button buildBtCancelar(){
		btCancelar = new Button("Cancelar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				fireEvent(new LojaIntegradaEvent(getUI(), false));
				close();
			}
		});
		
		return btCancelar;
	}
	private Button buildBtSalvar(){
		btSalvar = new Button("Salvar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
					try {
						
						if(txtPrecoPromocional.getValue() !=  null && cbProdutoAtivado.getValue() != null && cbProdutoDestaque.getValue() != null ){					
							String cod_loja = null;
							if(p.getCod_pro_loja() == null){
							//	cod_loja = LojaIntegradaDAO.cadastrarProduto(txtCod.getValue(), txtNome.getValue(), txtDescricao.getValue(), cbProdutoAtivado.getValue().toString(), cbProdutoDestaque.getValue().toString(), p.getGrupoId().getId().toString(), p.getGrupoId().getNome_grupo());
								
								if(cod_loja != null){
									//LojaIntegradaDAO.alterarProduto(p.getCod_pro_loja(), txtNome.getValue(), txtDescricao.getValue(), cbProdutoAtivado.getValue().toString(), cbProdutoDestaque.getValue().toString(), p.getGrupoId().getId().toString(), p.getGrupoId().getNome_grupo(), txtUrlYoutube.getValue());
									//LojaIntegradaDAO.alterarPrecos(cod_loja, Real.formatStringToDB(txtPrecoVenda.getValue()), Real.formatStringToDB(txtPrecoVenda.getValue()), Real.formatStringToDB(txtPrecoPromocional.getValue()));
								}			
							}else{
								//cod_loja = LojaIntegradaDAO.alterarProduto(p.getCod_pro_loja(), txtNome.getValue(), txtDescricao.getValue(), cbProdutoAtivado.getValue().toString(), cbProdutoDestaque.getValue().toString(), p.getGrupoId().getId().toString(), p.getGrupoId().getNome_grupo(), txtUrlYoutube.getValue());
								
								//if(cod_loja != null){
								//LojaIntegradaDAO.alterarPrecos(p.getCod_pro_loja(), Real.formatStringToDB(txtPrecoVenda.getValue()), Real.formatStringToDB(txtPrecoVenda.getValue()), Real.formatStringToDB(txtPrecoPromocional.getValue()));
								//}											
							}
							
							if(cod_loja != null || p.getCod_pro_loja() != null){								
								PedidoDAO.sincronizarEstoque(p.getCod_pro_loja() !=  null ? p.getCod_pro_loja() : cod_loja, p.getQtdEstoque().toString());
								
								//Atualiza Banco de Dados
								p.setPreco_promocional(Real.formatStringToDBFloat(txtPrecoPromocional.getValue()));
								p.setProduto_ativado(cbProdutoAtivado.getValue().toString());
								p.setProduto_destaque(cbProdutoDestaque.getValue().toString());
								p.setUrl_video_youtube(txtUrlYoutube.getValue().toString());

								p.setDesc_produto(txtDescricao.getValue());
								p.setSic_loja(true);
								if(cod_loja != null)
								{
									p.setCod_pro_loja(cod_loja);									
								}
								
								ProdutoDAO.alterar(p);
							}
							 
							close();
						}
					} catch (Exception e) {			
						e.printStackTrace();			 
					}
			}
		});
		
		return btSalvar;
	}
	
	
	
	
	
	public void addListerner(LojaIntegradaListerner target){
		try {
			Method method = LojaIntegradaListerner.class.getDeclaredMethod("onClose", LojaIntegradaEvent.class);
			addListener(LojaIntegradaEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(LojaIntegradaListerner target){
		removeListener(LojaIntegradaEvent.class, target);
	}
	public static class LojaIntegradaEvent extends Event{
			
		public LojaIntegradaEvent(Component source, boolean confirm) {
			super(source);			
		}

	}
	public interface LojaIntegradaListerner extends Serializable{
		public void onClose(LojaIntegradaEvent event);
	}
	
}

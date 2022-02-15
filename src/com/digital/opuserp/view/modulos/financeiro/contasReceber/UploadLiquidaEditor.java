package com.digital.opuserp.view.modulos.financeiro.contasReceber;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;

import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.util.comp.RealTextField.RealTextField;
import com.digital.opuserp.view.modulos.cadastro.clientes.ClienteEditor.ClientesListerner;
import com.digital.opuserp.view.modulos.configuracoes.config_sistema.empresa.EmpresaEditor.ImageUploader;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class UploadLiquidaEditor extends Window {

	
	private File file;
	
	Button btSalvar; 
	Button btCancelar;
	FormLayout flPrincipal;
	FieldGroup fieldGroup;
	VerticalLayout vlRoot;
	
	ComboBox cbCliente;
	RealTextField tfValor;
	TextField tfNDoc;
	DateField dfVencimento;
	TextField tfQAtd;
	TextField tfDescricaoCliente;
	TextField tfCodCliente;
	
	Cliente Clientelecionado;
	ComboBox cbControle;
	ComboBox cbIntervalo;
	
	boolean data_venc_valid = false;
	boolean valor_valid = true;
	boolean nDoc_valid = false;
	
	
	public UploadLiquidaEditor(String title, boolean modal){
		setWidth("550px");
		setHeight("175px");
		
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
				hlButtons.setStyleName("hl_buttons_bottom");
				hlButtons.setSpacing(true);
				hlButtons.setMargin(true);
				hlButtons.addComponent(buildBtCancelar());
				//hlButtons.addComponent(buildBtSalvar());
				
				addComponent(hlButtons);
				setComponentAlignment(hlButtons, Alignment.BOTTOM_RIGHT);
			}
		});
		buildLayout();
	}
	
	
	
	public void buildLayout(){
						
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom-new");
					setMargin(true);
					setSpacing(true);						
					
					Uploader uploader = new Uploader();
					final Upload upload = new Upload(null,null);
					
					upload.setButtonCaption("Upload");				
					upload.setReceiver(uploader);
					upload.addSucceededListener(uploader);
					
					addComponent(upload);			
							
				}
		});
		
		
	}

	public Button buildBtSalvar() {
		btSalvar = new Button("OK", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {

			}
		});		
		btSalvar.addStyleName("default");
		
		ShortcutListener clTb = new ShortcutListener("OK", ShortcutAction.KeyCode.ENTER, null) {
			
			@Override
			public void handleAction(Object sender, Object target) {
				btSalvar.click();
			}
		};
		btSalvar.addShortcutListener(clTb);
		
		return btSalvar;
	}

	
	public Button buildBtCancelar() {
		btCancelar = new Button("Cancelar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				close();				
			}
		});
		ShortcutListener clTb = new ShortcutListener("Cancelar", ShortcutAction.KeyCode.ESCAPE, null) {
			
			@Override
			public void handleAction(Object sender, Object target) {
				btCancelar.click();
			}
		};
		btCancelar.addShortcutListener(clTb);
		
		return btCancelar;
	}
	
	public void addListerner(UploadLiquidadoListerner target){
		try {
			Method method = UploadLiquidadoListerner.class.getDeclaredMethod("onClose", UploadLiquidadoEvent.class);
			addListener(UploadLiquidadoEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(ClientesListerner target){
		removeListener(UploadLiquidadoEvent.class, target);
	}
	public static class UploadLiquidadoEvent extends Event{
			
		private boolean confirm;
		private String fileName;
		
		public UploadLiquidadoEvent(Component source, boolean confirm, String fileName) {
			super(source);			
			this.confirm = confirm;				
			this.fileName = fileName;
		}	
		
		public String getFileName(){
			return this.fileName;
		}
		
	}
	public interface UploadLiquidadoListerner extends Serializable{
		public void onClose(UploadLiquidadoEvent event);
	}
	public class Uploader implements Receiver, SucceededListener{
		
		
		
		@Override
		public OutputStream receiveUpload(String filename, String mimeType) {
			
			FileOutputStream fos = null;
			String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();	
			try {
				
				if(new File(basepath + "/WEB-INF/uploads/" + filename).exists()){
					new File(basepath + "/WEB-INF/uploads/" + filename).delete();
				}
				
				file = new File(basepath + "/WEB-INF/uploads/" + filename);
				fos = new FileOutputStream(file);
				
				
			} catch (Exception e) {
				e.printStackTrace();
				new Notification("O arquivo nao pode ser aberto! ", e.getMessage(),Notification.Type.ERROR_MESSAGE).show(Page.getCurrent());				
				return null; 
			}
			
			return fos;
		}

		@Override
		public void uploadSucceeded(SucceededEvent event) {
			fireEvent(new UploadLiquidadoEvent(getUI(),true, event.getFilename()));										
			close();
			
		}

	}
	

}
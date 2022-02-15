package com.digital.opuserp.view.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;

import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.FileResource;
import com.vaadin.server.Page;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class UploadImagemUtil extends Window {
	
	
	
	Button btSalvar;
	Button btCancelar;
	FormLayout flPrincipal;
	FieldGroup fieldGroup;
	VerticalLayout vlRoot;
	
	
	private Image img;
	public File file;
	
	public UploadImagemUtil(String title, boolean modal){
		
		String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
		file = new File(basepath + "/WEB-INF/uploads/logo.jpeg");
		
		
		//setWidth("532px");
		//setHeight("394px");
				
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
				hlButtons.addComponent(buildBtCancelar());
				hlButtons.addComponent(buildBtSalvar());
				
				addComponent(hlButtons);
				setComponentAlignment(hlButtons, Alignment.BOTTOM_RIGHT);
			}
		});
		
		
		
		buildLayout();
	}
	
	
	
	private TextField tfCodPedido;
	
	public void buildLayout(){
		
	
		
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
						
					final Upload imageUploaded = new Upload(null,null);
					imageUploaded.setButtonCaption("Upload");	
					
					
					ImageUploader uploader = new ImageUploader();
					imageUploaded.setReceiver(uploader);
					imageUploaded.addSucceededListener(uploader);
					
					
					img = new Image(null, new ThemeResource("img/default_logo.png"));
													
					
					//logo.setWidth("300px");
					//logo.setHeight("70px");
					
					Panel pnLogo = new Panel("Imagem");
					pnLogo.setStyleName("pn-img-osp");
					pnLogo.setSizeUndefined();	
					pnLogo.setContent(new VerticalLayout(){
						{
							addComponent(img);
							addComponent(imageUploaded);
							
							setComponentAlignment(img, Alignment.MIDDLE_CENTER);
							setComponentAlignment(imageUploaded, Alignment.MIDDLE_CENTER);
						}
					});
					
					addComponent(pnLogo);
					
				}
		});
	
		
		
		
		
		
	}
	
	
	
	
	public Button buildBtSalvar() {
		btSalvar = new Button("Salvar", new Button.ClickListener() {
			
			
			public void buttonClick(ClickEvent event) {
				if(file != null && nameImagemUploaded != null && !nameImagemUploaded.equals("") && !nameImagemUploaded.isEmpty()){
					try {
																
						fireEvent(new UploadImagemEvent(getUI(), readImageOldWay(file),nameImagemUploaded, true));						
						
					} catch (Exception e) {
						Notify.Show("ERRO: "+e.getLocalizedMessage(), Notify.TYPE_ERROR);

						e.printStackTrace();
					}
					
				}else{					 
					 
					 Notify.Show_Invalid_Submit_Form();

				}
				
			}
		});
		btSalvar.setStyleName("default");
		
		ShortcutListener clTb = new ShortcutListener("Salvar", ShortcutAction.KeyCode.ENTER, null) {
			
			
			public void handleAction(Object sender, Object target) {
				btSalvar.click();
			}
		};
		btSalvar.addShortcutListener(clTb);		
		return btSalvar;
	}

	
	public Button buildBtCancelar() {
		btCancelar = new Button("Cancelar", new Button.ClickListener() {
			
			
			public void buttonClick(ClickEvent event) {
							
				fireEvent(new UploadImagemEvent(getUI(), null,null, false));
				close();			
			}
		});
		
		ShortcutListener clTb = new ShortcutListener("Cancelar", ShortcutAction.KeyCode.ESCAPE, null) {
			
			
			public void handleAction(Object sender, Object target) {
				btCancelar.click();
			}
		};
		btCancelar.addShortcutListener(clTb);
		
		return btCancelar;
	}
	
	
	public void addListerner(UploadImagemListerner target){
		try {
			Method method = UploadImagemListerner.class.getDeclaredMethod("onClose", UploadImagemEvent.class);
			addListener(UploadImagemEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(UploadImagemListerner target){
		removeListener(UploadImagemEvent.class, target);
	}
	public static class UploadImagemEvent extends Event{
		
		private byte[] file;
		private String nome;
		private boolean confirm;
		
		public UploadImagemEvent(Component source, byte[] file,String nome, boolean confirm) {
			super(source);
			this.file = file;
			this.confirm = confirm;		
			this.nome = nome;
		}

		public String getNome(){
			return nome;
		}
		
		public byte[] getFile() {
			return file;
		}	
		public boolean isConfirm() {
			return confirm;
		}		
	}
	public interface UploadImagemListerner extends Serializable{
		public void onClose(UploadImagemEvent event);
	}
	
	private String nameImagemUploaded;
	
	public class ImageUploader implements Receiver, SucceededListener{
		
		
		
		@Override
		public OutputStream receiveUpload(String filename, String mimeType) {
			
			FileOutputStream fos = null;
			String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();	
			try {
				
				file = new File(basepath + "/WEB-INF/uploads/" + filename);
				fos = new FileOutputStream(file);
				
			} catch (FileNotFoundException e) {
				new Notification("O arquivo nao pode ser aberto! ", e.getMessage(),
	                    Notification.Type.ERROR_MESSAGE)
	                .show(Page.getCurrent());
				return null;
			}
			
			return fos;
		}

		@Override
		public void uploadSucceeded(SucceededEvent event) {
			img.setSource(new FileResource(file));
			nameImagemUploaded = event.getFilename();
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

}


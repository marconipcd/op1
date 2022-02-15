package com.digital.opuserp.view.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import com.digital.opuserp.view.util.UploadImagemUtil.UploadImagemEvent;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.FileResource;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class FileUploadUtil extends Window {
	
	Button btSalvar;
	Button btCancelar;
	FormLayout flPrincipal;
	FieldGroup fieldGroup;
	VerticalLayout vlRoot;
	
	FileResource arquivo;
	Embedded embedded;
	public File file;
	
	String pasta;
	
	public FileUploadUtil(String title, boolean modal, String pasta){
		
		this.pasta = pasta;
		setWidth("426px");
		setHeight("152px");		
				
		setCaption(title);
		setModal(modal);
		setResizable(true);
		setClosable(true);
		center();
		
		vlRoot = new VerticalLayout(){
			{
				final Upload fileUpload = new Upload(null,null);				
				fileUpload.setButtonCaption("Upload");	
								
				ImageUploader uploader = new ImageUploader();
				
				fileUpload.setReceiver(uploader);
				fileUpload.addSucceededListener(uploader);
				
				addComponent(fileUpload);
			}
		};	
		vlRoot.setSizeFull();
		vlRoot.setMargin(true);
		vlRoot.setStyleName("border-form");
		
		setContent(new VerticalLayout(){
			{
				setSizeFull();
				setMargin(true);
				addComponent(vlRoot);
			}
		});
			
	}
	
	
	public Button buildBtSalvar() {
		btSalvar = new Button("Salvar", new Button.ClickListener() {
			
			
			public void buttonClick(ClickEvent event) {

				boolean check = false;

				try{
					long size = Files.size(Paths.get(file.getAbsolutePath()));
					
					if(size <= 2000){
						check = true;
					}else{
						check = false;
					}
				}catch(Exception e){
					e.printStackTrace();
					
					check=false;
				}
				
				
				if(file != null && nameImagemUploaded != null && !nameImagemUploaded.equals("") && !nameImagemUploaded.isEmpty() && check){
					try {																
						//fireEvent(new FileUploadUtilEvent(getUI(), readImageOldWay(file),nameImagemUploaded,null, true,file));								
					} catch (Exception e) {
						Notify.Show("ERRO: "+e.getLocalizedMessage(), Notify.TYPE_ERROR);

						e.printStackTrace();
					}
					
				}else{					 
					 
					 Notify.Show_Invalid_Submit_Form();
					 
					 if(!check){
						 Notify.Show("Não é possível enviar arquivo maior que 2MB", Notify.TYPE_ERROR);
					 }

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
	
	
	public void addListerner(FileUploadUtilListerner target){
		try {
			Method method = FileUploadUtilListerner.class.getDeclaredMethod("onClose", FileUploadUtilEvent.class);
			addListener(FileUploadUtilEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(FileUploadUtilListerner target){
		removeListener(FileUploadUtilEvent.class, target);
	}
	public static class FileUploadUtilEvent extends Event{
		
		private File arquivo;
		private byte[] file;
		private String nome;
		private boolean confirm;
		private String link;
		
		public FileUploadUtilEvent(Component source, byte[] file,String nome, boolean confirm, String link) {
			super(source);
			this.file = file;
			this.confirm = confirm;		
			this.nome = nome;			
			this.link = link;
		}
		
		public String getLink(){
			return link;
		}

		public String getNome(){
			return nome;
		}
		
		public byte[] getFile() {
			return file;
		}
		
		public File getArquivo() {
			return arquivo;
		}
		public boolean isConfirm() {
			return confirm;
		}		
	}
	public interface FileUploadUtilListerner extends Serializable{
		public void onClose(FileUploadUtilEvent event);
	}
	
	private String nameImagemUploaded;
	
	public class ImageUploader implements Receiver, SucceededListener{
						
		@Override
		public OutputStream receiveUpload(String filename, String mimeType) {
			
			boolean check = true;
			FileOutputStream fos = null;
				try {

						
						String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
						
						file = new File(basepath + "/WEB-INF/uploads/" + filename);		
//						long size = Files.size(Paths.get(file.getAbsolutePath()));
//						
//						if(size <= 5000000){
//							check = true;
//						}else{
//							check = false;
//						}
								
						fos = new FileOutputStream(file);
							
					if(mimeType.equals("application/pdf") && check){
						return fos;
					}else{
						
						if(!mimeType.equals("application/pdf")){
							Notify.Show("Não é possível fazer upload de arquivos que não são PDF!", Notify.TYPE_ERROR);
						}
						
						if(!check){
							Notify.Show("Não é possível fazer upload de arquivos maiores de 2 Mb", Notify.TYPE_ERROR);
						}
						
						return null;
					}
					
				} catch (Exception e) {
					new Notification("O arquivo nao pode ser aberto! ", e.getMessage(), Notification.Type.ERROR_MESSAGE).show(Page.getCurrent());
					return null;
				}
				
				//return fos;
			
		}

		@Override
		public void uploadSucceeded(SucceededEvent event) {
							
			try{
								
				 OkHttpClient client = new OkHttpClient().newBuilder().build();
				 MediaType mediaType = MediaType.parse("text/plain");
			     RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("File",file.getAbsolutePath(),
			     RequestBody.create(MediaType.parse("application/octet-stream"),file))
					      .addFormDataPart("tag",file.getName())
					      .addFormDataPart("pasta",pasta)
					      .build();
					    Request request = new Request.Builder()
					      .url("http://172.17.0.90:8080/uploadFile")
					      .method("POST", body)
					      .build();
					    Response response = client.newCall(request).execute();
					    					    
			    String link = response.body().string();				
				fireEvent(new FileUploadUtilEvent(getUI(), readImageOldWay(file),file.getName(),true,link));

				Notify.Show("Upload feito com sucesso!", Notify.TYPE_SUCCESS);
				close();
			}catch(Exception e){
				e.printStackTrace();
			}
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


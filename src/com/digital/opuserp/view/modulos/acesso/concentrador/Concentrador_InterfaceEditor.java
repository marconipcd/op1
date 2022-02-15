package com.digital.opuserp.view.modulos.acesso.concentrador;

import java.lang.reflect.Method;

import com.digital.opuserp.util.MikrotikUtil;
import com.digital.opuserp.view.modulos.acesso.concentrador.ConcentradorEditor.ConcentradorEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class Concentrador_InterfaceEditor extends Window
{
	
	String usuario;
	String senha;
	String ip;
	Integer porta;
	String id;
	String comment;
	
	public Concentrador_InterfaceEditor(String usuario, String senha, String ip, Integer porta, String id, String comment ){
		super("Eiditar Interface");
		center();
		
		this.usuario = usuario;
		this.senha = senha;
		this.ip= ip;
		this.porta = porta;
		this.id = id;
		this.comment = comment;
		
		setContent(new VerticalLayout(){
			{
				setSizeFull();
				final TextField tfComentario = new TextField("Comentário");
				tfComentario.setValue(Concentrador_InterfaceEditor.this.comment);
				
				addComponent(new FormLayout(){
					{
						addComponent(tfComentario);
					}
				});
				
				addComponent(new FormLayout(){
					{
						addComponent(new Button("Salvar",new Button.ClickListener() {
							
							@Override
							public void buttonClick(ClickEvent event) {
								
								if(tfComentario.getValue() != null){
								
									MikrotikUtil.editInterface(
											Concentrador_InterfaceEditor.this.usuario, 
											Concentrador_InterfaceEditor.this.senha, 
											Concentrador_InterfaceEditor.this.ip, 
											Concentrador_InterfaceEditor.this.porta, 
											Concentrador_InterfaceEditor.this.id, 
											tfComentario.getValue());
									
									fireEvent(new InterfaceEditorEvent(getUI()));
									close();
									
								}
							}
						}));
					}
				});
			}
		});
	}
	
	public void addListerner(InterfaceEditorListerner target){
		try {
			Method method = InterfaceEditorListerner.class.getDeclaredMethod("onSave", InterfaceEditorEvent.class);
			addListener(InterfaceEditorEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(InterfaceEditorListerner target){
		removeListener(ConcentradorEvent.class, target);
	}
	public static class InterfaceEditorEvent extends Event{

		public InterfaceEditorEvent(Component arg0) {
			super(arg0);			
		}		
	}
	public interface InterfaceEditorListerner{
		public void onSave(InterfaceEditorEvent event);
	}
	

}

package com.digital.opuserp.view.modulos.crm.crm;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;

import com.digital.opuserp.dao.PerguntaDAO;
import com.digital.opuserp.domain.Crm;
import com.digital.opuserp.domain.CrmPesquisaRel;
import com.digital.opuserp.domain.Perguntas;
import com.digital.opuserp.domain.Respostas;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class CrmPesquisaEditor extends Window {
	
	
	EntityManager em = ConnUtil.getEntity();
	
	Button btSalvar;
	Button btCancelar;
	Button btTratar;
	Button btReagendar;
	
	FormLayout flPrincipal;	
	VerticalLayout vlRoot;
	Integer minus = 0;
	
	Crm crm;
	
	boolean visualizar;
	
	public CrmPesquisaEditor(Crm crm, String title, boolean modal,final boolean visualizar){
		
		this.visualizar = visualizar; 
		this.crm = crm; 
		if(crm == null){
			Notify.Show("Não foi possivel Selecionar CRM", Notify.TYPE_WARNING);
			close();
		}
		
		//setHeight("589px");
				
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
				
				if(visualizar){
					hlButtons.addComponent(buildBtCancelar());
				}else{
					hlButtons.addComponent(buildBtCancelar());
					hlButtons.addComponent(buildBtGravar());
				}
								
				HorizontalLayout hlBtRoot = new HorizontalLayout();
				hlBtRoot.setWidth("100%");
								
				hlBtRoot.addComponent(hlButtons);
												
				hlBtRoot.setComponentAlignment(hlButtons, Alignment.BOTTOM_RIGHT);
				
				addComponent(hlBtRoot);
			}
		});
		
		if(visualizar){
			buildLayoutVisualizar();
		}else{
			buildLayout();
		}
	}
	
	Map<String, String> mapRespostas = new HashMap<>();
	Integer sizePerguntas = 0;
	private void buildLayout(){
		
		List<Perguntas> perguntas = PerguntaDAO.findByAssunto(crm.getCrm_assuntos());
		sizePerguntas = perguntas.size();
		for (final Perguntas p : perguntas) {
			
				if(p.getStatus().equals("ATIVO")){
					vlRoot.addComponent(
							new FormLayout(){					
								{
									setStyleName("form-cutom");
									setMargin(true);
									setSpacing(true);
									
									Label lbPergunta = new Label(p.getPergunta());
									addComponent(lbPergunta);						
								}
							});
						
						vlRoot.addComponent(
							new FormLayout(){					
								{
									setStyleName("form-cutom");
									setMargin(true);
									setSpacing(true);
									
									if(p.isPre_respostas()){
										final ComboBox cbRespostas = new ComboBox("Opções");
										cbRespostas.setTextInputAllowed(false); 
										cbRespostas.setStyleName("caption-align-acesso");
										cbRespostas.setRequired(true);
										cbRespostas.setId(p.getId().toString());
										cbRespostas.setNullSelectionAllowed(false); 
										
										List<Respostas> respostas = PerguntaDAO.findByPergunta(p);
										for (Respostas r : respostas) {								
											cbRespostas.addItem(r.getResposta());
										}
										
										cbRespostas.addValueChangeListener(new Property.ValueChangeListener() {
											
											@Override
											public void valueChange(ValueChangeEvent event) {
												
												if(mapRespostas.containsKey(cbRespostas.getId().toString())){
													mapRespostas.remove(cbRespostas.getId().toString());
												}
												
												if(cbRespostas.getValue() != null){									
													mapRespostas.put(cbRespostas.getId().toString(), cbRespostas.getValue().toString());
												}									
											}
										});
										
										addComponent(cbRespostas);		
									}else{
										final TextArea txtObs = new TextArea("Resposta");
										//txtObs.setRequired(true);
										txtObs.setWidth("360px");
										txtObs.setHeight("150px");
										txtObs.setStyleName("caption-align-acesso");
										txtObs.setId(p.getId().toString());
										txtObs.setImmediate(true); 
										txtObs.addListener(new FieldEvents.TextChangeListener() {
											
											@Override
											public void textChange(TextChangeEvent event) {
												if(mapRespostas.containsKey(txtObs.getId().toString())){
													mapRespostas.remove(txtObs.getId().toString());
												}
												
												if(txtObs.getValue() != null){									
													mapRespostas.put(txtObs.getId().toString(), event.getText());
												}		
												
											}
										});
										
										minus = minus+1;
										addComponent(txtObs); 										
									}
								}
							});
				}
			
				
		}
		
		
	}
	
		private void buildLayoutVisualizar(){
			

			
			List<Perguntas> perguntas = PerguntaDAO.findByAssunto(crm.getCrm_assuntos());
			sizePerguntas = perguntas.size();
			for (final Perguntas p : perguntas) {
				
					if(p.getStatus().equals("ATIVO")){
						vlRoot.addComponent(
								new FormLayout(){					
									{
										setStyleName("form-cutom");
										setMargin(true);
										setSpacing(true);
										
										Label lbPergunta = new Label(p.getPergunta());
										addComponent(lbPergunta);						
									}
								});
							
							vlRoot.addComponent(
								new FormLayout(){					
									{
										setStyleName("form-cutom");
										setMargin(true);
										setSpacing(true);
										
										if(p.isPre_respostas()){
											final ComboBox cbRespostas = new ComboBox("Opções");
											cbRespostas.setTextInputAllowed(false); 
											cbRespostas.setStyleName("caption-align-acesso");
											cbRespostas.setRequired(true);
											cbRespostas.setId(p.getId().toString());
											cbRespostas.setNullSelectionAllowed(false); 
											
											List<Respostas> respostas = PerguntaDAO.findByPergunta(p);
											
											for (Respostas r : respostas) {								
												cbRespostas.addItem(r.getResposta());
	
												CrmPesquisaRel perguntasRep = PerguntaDAO.findByCodCrmPerg(crm.getId(),p.getId());
												if(perguntasRep!=null && r.getResposta().equals(perguntasRep.getResposta())){
								
													cbRespostas.select(r.getResposta());
												}else{
													cbRespostas.setValue("");
												}												
											}
//											
//											cbRespostas.addValueChangeListener(new Property.ValueChangeListener() {
//												
//												@Override
//												public void valueChange(ValueChangeEvent event) {
//													
//													if(mapRespostas.containsKey(cbRespostas.getId().toString())){
//														mapRespostas.remove(cbRespostas.getId().toString());
//													}
//													
//													if(cbRespostas.getValue() != null){									
//														mapRespostas.put(cbRespostas.getId().toString(), cbRespostas.getValue().toString());
//													}									
//												}
//											});
											cbRespostas.setReadOnly(true);
											
											addComponent(cbRespostas);		
										}else{
											final TextArea txtObs = new TextArea("Resposta");
											//txtObs.setRequired(true);
											txtObs.setWidth("360px");
											txtObs.setHeight("150px");
											txtObs.setStyleName("caption-align-acesso");
											txtObs.setId(p.getId().toString());
											txtObs.setImmediate(true); 
//											txtObs.addListener(new FieldEvents.TextChangeListener() {
//												
//												@Override
//												public void textChange(TextChangeEvent event) {
//													if(mapRespostas.containsKey(txtObs.getId().toString())){
//														mapRespostas.remove(txtObs.getId().toString());
//													}
//													
//													if(txtObs.getValue() != null){									
//														mapRespostas.put(txtObs.getId().toString(), event.getText());
//													}		
//													
//												}
//											});
											CrmPesquisaRel perguntasRep = PerguntaDAO.findByCodCrmPerg(crm.getId(),p.getId());
											if(perguntasRep!=null){
												txtObs.setValue(perguntasRep.getResposta());
											}
											txtObs.setReadOnly(true);
											
											minus = minus+1;
											addComponent(txtObs); 										
										}
									}
								});
					}
				
					
			}
			
			
		}
	
	
	private Button btGravar;
	private Button buildBtGravar(){
		btGravar = new Button("Concluir", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				Integer totalPerguntas = sizePerguntas-minus;
				if(mapRespostas != null && totalPerguntas <= mapRespostas.size()){
					
					
					
					for (Entry<String, String> entry : mapRespostas.entrySet()) {
			    
					    String key = entry.getKey();
						String resposta = entry.getValue();
						
//						System.out.println("Pergunta: "+key);
//						System.out.println("Resposta: "+resposta);
						
						Perguntas pergunta = PerguntaDAO.findPerguntaById(Integer.parseInt(key)); 
						if(resposta != null && !resposta.isEmpty() && !resposta.equals("")){
							PerguntaDAO.saveRel(new CrmPesquisaRel(null, crm, Integer.parseInt(key), pergunta.getPergunta() , resposta, new Date()));
						}
					}
					
					fireEvent(new CrmPesquisarEditorEvent(getUI(), true));
					close();
					
				}else{
					Notify.Show("Porfavor Informe respostas em Todas as Perguntas!", Notify.TYPE_WARNING);
				}
			}
		});
		
		btGravar.addStyleName("default");
		
		return btGravar;
	}
	
	private Button buildBtCancelar(){
		btCancelar = new Button("Voltar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				close();
			}
		});
		
		return btCancelar;
	}
	
	public void addListerner(CrmPesquisarEditorListerner target){
		try {
			Method method = CrmPesquisarEditorListerner.class.getDeclaredMethod("onClose", CrmPesquisarEditorEvent.class);
			addListener(CrmPesquisarEditorEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(CrmPesquisarEditorListerner target){
		removeListener(CrmPesquisarEditorEvent.class, target);
	}
	public static class CrmPesquisarEditorEvent extends Event{
		
		
		private boolean confirm;
		
		public CrmPesquisarEditorEvent(Component source, boolean confirm) {
			super(source);		
			this.confirm = confirm;			
		}
		public boolean isConfirm() {
			return confirm;
		}		
	}
	public interface CrmPesquisarEditorListerner extends Serializable{
		public void onClose(CrmPesquisarEditorEvent event);
	}
}

package com.digital.opuserp.view.util;

import com.vaadin.ui.JavaScript;

public class Notify {

	public static String TYPE_ERROR = "error";
	public static String TYPE_SUCCESS = "success";
	public static String TYPE_NOTICE = "notice";
	public static String TYPE_WARNING = "warning";
	
	public static void ShowOnDeskManutencao(String titulo, String msg){
		
		StringBuilder strComando = new StringBuilder();
		strComando.append("$.testeNotify('");
		strComando.append(titulo.toString());
		strComando.append("');");
				
		JavaScript.getCurrent().execute(strComando.toString());
	}
	
	
	public static void Show(String msg,String type){

		if(type.equals(TYPE_ERROR)){
			
			JavaScript.getCurrent().execute("$.jnotify('"+msg+"','error');");
		}
		
		if(type.equals(TYPE_SUCCESS)){
			JavaScript.getCurrent().execute("$.jnotify('"+msg+"','success');");
		}
		
		if(type.equals(TYPE_NOTICE)){
			JavaScript.getCurrent().execute("$.jnotify('"+msg+"','notice');");
		}
		
		if(type.equals(TYPE_WARNING)){
			JavaScript.getCurrent().execute("$.jnotify('"+msg+"','warning');");
		}

	}
	
	public static void Show_Invalid_Submit_Form(){

		Show("Não é Possivel Salvar, Verifique os Campos Obrigatórios/Inválidos e Tente Novamente!", Notify.TYPE_ERROR);

	}
}

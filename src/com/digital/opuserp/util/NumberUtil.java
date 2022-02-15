package com.digital.opuserp.util;

import java.text.DecimalFormat;

public class NumberUtil {

	
	public static String formatarFloat(float numero){  
		  String retorno = "";  
		  DecimalFormat formatter = new DecimalFormat("#.00");  
	  try{  
		    retorno = formatter.format(numero);  
	  }catch(Exception ex){  
		    System.err.println("Erro ao formatar numero: " + ex);  
	  }  
		  return retorno;  
	} 
}

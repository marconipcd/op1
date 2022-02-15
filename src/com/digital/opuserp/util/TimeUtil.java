package com.digital.opuserp.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class TimeUtil {

	
	public static String getDurationBreakdown(long millis)
    {
        if(millis < 0)
        {
            throw new IllegalArgumentException("Duration must be greater than zero!");
        }

        long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
        
        StringBuilder sb = new StringBuilder(64);
        
        if(days > 0){
        	sb.append(days);
        	
        	if(days == 1){
        		sb.append(" dia  ");
        	}else{
        		sb.append(" dias  ");
        	}
        }
        
//        
        String hora;
        if(hours < 10){
        	hora = "0"+String.valueOf(hours);
        }else{
        	hora = String.valueOf(hours);
        }
        
        String minutos;
        if(minutes < 10){
        	minutos ="0"+String.valueOf(minutes);
        }else{
        	minutos = String.valueOf(minutes);
        }
        
        String segundos;
        if(seconds < 10){
        	segundos = "0"+String.valueOf(seconds);
        }else{
        	segundos = String.valueOf(seconds);
        }
        
          sb.append(hora+":");        	
        sb.append(minutos+":");        	
        sb.append(segundos);        	
        

        return(sb.toString());
    }

	public static String formataTempo(int segundos)  
	{   
		int segund = segundos % 60;
		int minutos = segundos / 60;   
		int minuto = minutos % 60;    
		int hora = minutos / 60;   	  
		int hh = hora % 24;   		
		int dia = hora / 24;   		
	  
	  String s = String.valueOf(segund);
	  String d = String.valueOf(dia);
	  String h = String.valueOf(hh);
	  String m = String.valueOf(minuto);

	  	String time = d+"d "+h+"h "+m+"min";
		
	  return time;  
	    
	}  
	
}

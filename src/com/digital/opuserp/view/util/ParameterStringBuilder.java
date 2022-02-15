package com.digital.opuserp.view.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Map;


public  class ParameterStringBuilder {
	    public static String getParamsString(Map<String, Object> params) 
	      throws UnsupportedEncodingException{
	        StringBuilder result = new StringBuilder();
	 
	        for (Map.Entry<String, Object> entry : params.entrySet()) {
	          result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
	          result.append("=");

	          if(entry.getKey().equals("categorias")){
	        	  result.append(entry.getValue().toString());
	          }else{	        	  
	        	  result.append(URLEncoder.encode((String)entry.getValue(), "UTF-8"));
	          }

	          result.append("&");
	        }
	 
	        String resultString = result.toString();
	        return resultString.length() > 0 ? resultString.substring(0, resultString.length() - 1) : resultString;
	    }
	}
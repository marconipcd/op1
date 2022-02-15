package com.digital.opuserp.util;

public class IpUtil {

	public static String  getGateway(String enderecoIp){
		String[] split = enderecoIp.split("[.]");
		
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < split.length; i++) {
		    
		    if (i == split.length - 1) {
		    	sb.append(String.valueOf(Integer.parseInt(split[i])-1));
		    }else{
		    	sb.append(split[i]+".");
		    }
		}
		
		return  sb.toString();
	}
}

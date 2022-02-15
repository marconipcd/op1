package com.digital.opuserp.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.validator.routines.InetAddressValidator;


public class Validator {
	
	public static boolean IPAddress( String address ){

		  return InetAddressValidator.getInstance().isValid(address);

	}
	
	public static boolean MacAddress(String mac){
		
	      Pattern pattern = Pattern.compile("^([0-9A-F]{2}[:-]){5}([0-9A-F]{2})$");
	      Matcher matcher = pattern.matcher(mac);
	 
	      if (matcher.matches()) {
	    	  return true;
	      }
	      else
	      {
	    	  return false;
	      }		
	}
}

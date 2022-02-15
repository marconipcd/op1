package com.digital.opuserp.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignalStrengthUtil {

	public SignalStrengthUtil() {

	}
	
	public static boolean validSignalStrength(String signal_strength){
		
		boolean valid = false;
		 Pattern pattern = Pattern.compile("-[^0\\W][0-9]{0,3}[.][.][^0\\W][0-9]{0,3}");
		 Pattern pattern2 = Pattern.compile("[0]{1,1}[.][.][^0\\W][0-9]{0,3}");
		 
	     Matcher matcher = pattern.matcher(signal_strength);		
	     Matcher matcher2 = pattern2.matcher(signal_strength);		
	     
		if(matcher.matches()){	
			Integer n = Integer.parseInt(signal_strength.split("\\.+")[0].toString().replaceAll("-",""));
			Integer n2 = Integer.parseInt(signal_strength.split("\\.+")[1].toString());
			if(n !=0 && n <= 120 && n2!=0 && n2 <= 120){	
				valid = true;
			}else{
				valid = false;	
			}
		}else if(matcher2.matches()){	
				Integer n = Integer.parseInt(signal_strength.split("\\.+")[0].toString().replaceAll("-",""));
				Integer n2 = Integer.parseInt(signal_strength.split("\\.+")[1].toString());
				if(n <= 120 && n2 != 0 && n2 <= 120){
					valid = true;
				}else{
					valid = false;	
				}
		}else{
			valid = false;										
			}
		
		return valid;
	}

}

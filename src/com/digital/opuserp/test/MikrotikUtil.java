package com.digital.opuserp.test;

import java.util.List;
import java.util.Map;

import me.legrange.mikrotik.ApiConnection;
import me.legrange.mikrotik.MikrotikApiException;
import me.legrange.mikrotik.ResultListener;
import br.com.eits.m4j.api.Mikrotik;

public class MikrotikUtil {

	
	static Mikrotik router;
	
	public static void main(String[] args)  {
		
		try{
			connect("rbdigital", "mkcolorau", "179.127.32.67");	
			
			List<Map<String, String>>  lisMap =   con.execute("/interface/wireless/registration-table/print");
			   for (Map<String, String> map : lisMap) {
				   System.out.println(map);
			   }
						
			disconnect();	
		}catch(Exception e){
			e.printStackTrace();
		}

	
	 }
	private static void test() throws MikrotikApiException, InterruptedException {
	   List<Map<String, String>>  lisMap =   con.execute("/interface/wireless/registration-table/print where mac-address=E8:94:F6:3A:27:99");
	   for (Map<String, String> map : lisMap) {
		   System.out.println(map);
	   }
	       // let it run for 60 seconds 
	       
	 }
	
	
	
	private static ApiConnection con;
	
	private static void connect(String user, String pass, String host) throws Exception {
        con = ApiConnection.connect(host);
        con.login(user, pass);
    }

    private static void disconnect() throws Exception {
       // con.disconnect();
    }

}

package com.digital.opuserp.view.util;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.json.simple.JSONObject;

public class ConteleUtil {

//	public static String postPoi(String customId, String name, String corporateName, String cnpj_cpf, String status, String neighborhood, String street, 
//			String state, String zipcode, String number, String complement, String city, String phone, String email) throws Exception{
//		 
//		JSONObject poi1 = new JSONObject();
//			
//		 poi1.put("customId", customId);
//	     poi1.put("name", name);
//	     poi1.put("corporateName", corporateName);
//	     poi1.put("cnpj_cpf", cnpj_cpf);
//	     poi1.put("status", status);
//	     
//	     
//	     JSONObject address = new JSONObject();
//	     address.put("neighborhood", neighborhood);
//	     address.put("street", street);
//	     address.put("state", state);
//	     address.put("zipcode", zipcode);
//	     address.put("number", number);
//	     address.put("complement", complement);
//	     address.put("city", city);
//	     
//	     poi1.put("address", address);
//	     
//	     
//	     JSONObject contacts1 = new JSONObject();
//	     contacts1.put("name", name);
//	     contacts1.put("phone", phone);
//	     contacts1.put("email", email);
//	       
//	     List<Object> contatos = new ArrayList<>();
//	     contatos.add(contacts1);
//	     	     
//	     poi1.put("contacts", contatos);
//	
//	    OkHttpClient client = new OkHttpClient().newBuilder().build();
//	    MediaType mediaType = MediaType.parse("application/json");	  
//	    RequestBody body = RequestBody.create(mediaType, poi1.toJSONString());
//	    Request request = new Request.Builder()
//	      .url("https://integration.contelege.com.br/v2/pois")
//	      .method("POST", body)
//	      .addHeader("x-api-key", "CzPZPqyk795PJ7JmesjYF3qGFPWUQDEO1Ml2ov6o")
//	      .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJjbGllbnRJZCI6IjczNjZiMDdmLWViYjAtNDZlZS04ZmE2LWNjZmYwMGQ5Y2M2MyIsImNyZWF0ZWRBdCI6IjIwMjEtMDYtMDlUMTE6MTg6NDIuMjcyWiIsImlkIjoiMjg5MmVjMDYtYTQ0ZC00ZDM3LWExZTktMDI3MjM0ZTlmNGE2IiwiaXNzIjoiY29udGVsZS1nZS1hcGkiLCJ1c2VySWQiOiIxOTEzZmFjYS1mNTlhLTRmNDctYmNmMi03NTJmNTJkN2M0YjQiLCJ1c2VyVHlwZSI6ImFkbWluIiwiaWF0IjoxNjIzMjM3NTIyfQ.kYfqWKS8zUGDZQZXscE3OQuFU7fvkB7JWAENwDaNXpc")
//	      .addHeader("Content-Type", "application/json")
//	      .build();
//	    
//	    Response response = client.newCall(request).execute();
//	    return response.body().string();
//	}
//	public static String getPoi(String customId) throws Exception{
//		
//			
//	    OkHttpClient client = new OkHttpClient().newBuilder().build();
//	    MediaType mediaType = MediaType.parse("application/json");	  
//	   
//	    Request request = new Request.Builder()
//	      .url("https://integration.contelege.com.br/v2/pois?customId="+customId)
//	      
//	      .addHeader("x-api-key", "CzPZPqyk795PJ7JmesjYF3qGFPWUQDEO1Ml2ov6o")
//	      .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJjbGllbnRJZCI6IjczNjZiMDdmLWViYjAtNDZlZS04ZmE2LWNjZmYwMGQ5Y2M2MyIsImNyZWF0ZWRBdCI6IjIwMjEtMDYtMDlUMTE6MTg6NDIuMjcyWiIsImlkIjoiMjg5MmVjMDYtYTQ0ZC00ZDM3LWExZTktMDI3MjM0ZTlmNGE2IiwiaXNzIjoiY29udGVsZS1nZS1hcGkiLCJ1c2VySWQiOiIxOTEzZmFjYS1mNTlhLTRmNDctYmNmMi03NTJmNTJkN2M0YjQiLCJ1c2VyVHlwZSI6ImFkbWluIiwiaWF0IjoxNjIzMjM3NTIyfQ.kYfqWKS8zUGDZQZXscE3OQuFU7fvkB7JWAENwDaNXpc")
//	      .addHeader("Content-Type", "application/json")
//	      .build();
//	    
//	    Response response = client.newCall(request).execute();
//	    return response.body().string();
//	}
//	
//	
//	public static String postTask(String customPoiId,String poiId, String datetime, String timezone, String os) throws Exception{
//		JSONObject task = new JSONObject();
//		
//		//Adminsitrador
//		//1913faca-f59a-4f47-bcf2-752f52d7c4b4
//		//Danilo
//		//3fe41069-565a-4c1e-9f9d-04e8a8fb2042
//		
//		 task.put("customPoiId", customPoiId);
//		 task.put("poiId", poiId);
//		 task.put("datetime", datetime);
//		 task.put("timezone", timezone);
//		 task.put("userId", "1913faca-f59a-4f47-bcf2-752f52d7c4b4");
//		 task.put("withTime", false);
//		 task.put("status", "pending");
//		 task.put("os", os);
//		 
//		 System.out.println(task);
//
//	   	
//	    OkHttpClient client = new OkHttpClient().newBuilder().build();
//	    MediaType mediaType = MediaType.parse("application/json");	  
//	    RequestBody body = RequestBody.create(mediaType, task.toJSONString());
//	    Request request = new Request.Builder()
//	      .url("https://integration.contelege.com.br/v2/tasks")
//	      .method("POST", body)
//	      .addHeader("x-api-key", "CzPZPqyk795PJ7JmesjYF3qGFPWUQDEO1Ml2ov6o")
//	      .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJjbGllbnRJZCI6IjczNjZiMDdmLWViYjAtNDZlZS04ZmE2LWNjZmYwMGQ5Y2M2MyIsImNyZWF0ZWRBdCI6IjIwMjEtMDYtMDlUMTE6MTg6NDIuMjcyWiIsImlkIjoiMjg5MmVjMDYtYTQ0ZC00ZDM3LWExZTktMDI3MjM0ZTlmNGE2IiwiaXNzIjoiY29udGVsZS1nZS1hcGkiLCJ1c2VySWQiOiIxOTEzZmFjYS1mNTlhLTRmNDctYmNmMi03NTJmNTJkN2M0YjQiLCJ1c2VyVHlwZSI6ImFkbWluIiwiaWF0IjoxNjIzMjM3NTIyfQ.kYfqWKS8zUGDZQZXscE3OQuFU7fvkB7JWAENwDaNXpc")
//	      .addHeader("Content-Type", "application/json")
//	      .build();
//	    
//	    Response response = client.newCall(request).execute();
//	    return response.body().string();
//	}
//	public static void main(String[] args) {
//		try {
////			String r = get("10419");			
////			ObjectMapper mapper = new ObjectMapper();
////			String jsonInString = r;
////			contelePois poi = mapper.readValue(jsonInString, contelePois.class); //Faz a c
////			
////			
////			System.out.println(poi.getPoi().size()); 
////			
////			if(poi.getPoi().size() > 0){
////				System.out.println(poi.getPoi().get(0).getName());
////			}
////			
////			
//			
//			
//		} catch (Exception e) {
//			// TODO Auto-generaited catch block
//			e.printStackTrace();
//		}
//		
//	}

}

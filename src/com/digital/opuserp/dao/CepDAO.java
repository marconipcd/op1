package com.digital.opuserp.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import com.digital.opuserp.domain.Ceps;
import com.digital.opuserp.util.ConnUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.vaadin.data.util.BeanItemContainer;

public class CepDAO {

	public static String  save(Ceps cep){
		
		try{
			Gson gson = new Gson();
			String cep_json = gson.toJson(cep);
						
			OkHttpClient client = new OkHttpClient().newBuilder().build();
			MediaType mediaType = MediaType.parse("application/json");
		    RequestBody body = RequestBody.create(mediaType, cep_json);
				    
		    Request request = new Request.Builder()
				      .url("http://localhost:8081/cep/save")
				      .method("POST", body)
				      .addHeader("Content-Type", "application/json")
				      .build();
				    Response response = client.newCall(request).execute();
				    return response.body().string();
		    		    
		}catch(Exception e){
			e.printStackTrace();
			
			return null;
		}
	}
	
	
	public static BeanItemContainer<Ceps> getCeps(){
		
		BeanItemContainer<Ceps> container = new BeanItemContainer<>(Ceps.class);
		
		try{
	        OkHttpClient client = new OkHttpClient().newBuilder().build();
		    Request request = new Request.Builder()
			      .url("http://172.17.0.90:8081/cep/findAll")
			      .method("GET", null)
			      .build();
		    
		    Response response = client.newCall(request).execute();
		    String retorno = response.body().string();
		    
		    ObjectMapper mapper = new ObjectMapper();
		    List<Ceps> ceps = mapper.readValue(retorno, new TypeReference<List<Ceps>>(){});
		    
		    for (Ceps ceps2 : ceps) {					
		    	container.addBean(ceps2);
			}
		    
		    return container;
		    
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

    public static Ceps getCep(String cep){
    	EntityManager em = ConnUtil.getEntity();
    	
    	try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Ceps> query = cb.createQuery(Ceps.class);
			Root<Ceps> root = query.from(Ceps.class);
			query.where(
					cb.equal(root.get("cep"), cb.parameter(String.class, "cep"))
					);
			
			TypedQuery<Ceps> q = em.createQuery(query);
			q.setParameter("cep", cep);
			if(q.getResultList().size() > 0){
				return (Ceps) q.getResultList().get(0);
			}
				return null;
		} catch (Exception e) {
			System.out.println("Erro ao buscar CEP: "+e.getMessage()+".\n Causado por: "+e.getCause());
			return null;
		}
    	
    }
  
    
}

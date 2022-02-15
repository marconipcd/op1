package com.digital.opuserp.dao;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.persistence.EntityManager;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import com.digital.opuserp.domain.AcessoCliente;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.ittv.customer;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.view.util.Notify;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class IttvDAO {
	
	
	private static String cadastrarCustomer(AcessoCliente contrato, String usuario) throws Exception{
		String url = "http://172.17.0.71/ittv_api/cadastrar.php";
		HttpURLConnection httpClient = (HttpURLConnection) new URL(url).openConnection();
		
        httpClient.setRequestMethod("POST");
        httpClient.setRequestProperty("User-Agent", "Mozilla/5.0");
        httpClient.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        
        String urlParameters = "email="+contrato.getCliente().getEmail()+"&"
        		+ "nome="+contrato.getCliente().getNome_razao()+"&"
        		+ "telefone=("+contrato.getCliente().getDdd_fone1()+")"+contrato.getCliente().getTelefone1()+"&"
        		+ "username="+usuario+"&"
        		+ "cep="+contrato.getCliente().getEndereco_principal().getCep()+"&"
        		+ "endereco="+contrato.getCliente().getEndereco_principal().getEndereco()+"&"
        		+ "cidade="+contrato.getCliente().getEndereco_principal().getCidade();
        		

        httpClient.setDoOutput(true);
        try (DataOutputStream wr = new DataOutputStream(httpClient.getOutputStream())) {
            wr.writeBytes(urlParameters);
            wr.flush();
        }

        try (BufferedReader in = new BufferedReader(new InputStreamReader(httpClient.getInputStream()))) {

            String line;
            StringBuilder response = new StringBuilder();

            while ((line = in.readLine()) != null) {
                response.append(line);
            }

            org.json.JSONObject json = new org.json.JSONObject(response.toString());		                

            if(!json.isNull("error")){
            	System.out.println("ERRO: "+json.get("error").toString());
            	
            	Notify.Show(json.get("error").toString(), Notify.TYPE_ERROR); 
            	
            	return null;
            }else{
            	
            	if(!json.isNull("_id")){
            		String id= json.get("_id").toString();
            		  
            		//Salva ID do ITTV no Contrato
            		contrato.setIttv_id(id);
            		AcessoDAO.save(contrato);
            		
            		return id;
            	}            	
            }
      
        }
        
        return null;
	}
	
	public static String alterarSenha(String cod_customer, String senha) throws Exception{
		String url = "http://172.17.0.71/ittv_api/alterarSenha.php";
		HttpURLConnection httpClient = (HttpURLConnection) new URL(url).openConnection();
		
        httpClient.setRequestMethod("POST");
        httpClient.setRequestProperty("User-Agent", "Mozilla/5.0");
        httpClient.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        
        String urlParameters = "id="+cod_customer+"&"+"senha="+senha;
        		
        httpClient.setDoOutput(true);
        try (DataOutputStream wr = new DataOutputStream(httpClient.getOutputStream())) {
            wr.writeBytes(urlParameters);
            wr.flush();
        }

        try (BufferedReader in = new BufferedReader(new InputStreamReader(httpClient.getInputStream()))) {

            String line;
            StringBuilder response = new StringBuilder();

            while ((line = in.readLine()) != null) {
                response.append(line);
            }

//            org.json.JSONObject json = new org.json.JSONObject(response.toString());		                
//
//            if(!json.isNull("error")){
//            	System.out.println("ERRO: "+json.get("error").toString());
//            	Notify.Show(json.get("error").toString(), Notify.TYPE_ERROR);
//            	
//            	return null;
//            }else{            	
//            	if(!json.isNull("message")){
//            		String msg= json.get("message").toString();
//            		Notify.Show(msg, Notify.TYPE_SUCCESS);
//            		            		
//            		return msg;
//            	}            	
//            }      
        }
        
        return null;
	}
	
	public static String atualizarStatus(String cod_customer, String status) throws Exception{
		String url = "http://172.17.0.71/ittv_api/updateCustomer.php";
		HttpURLConnection httpClient = (HttpURLConnection) new URL(url).openConnection();
		
        httpClient.setRequestMethod("POST");
        httpClient.setRequestProperty("User-Agent", "Mozilla/5.0");
        httpClient.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        
        String urlParameters = "idCustomer="+cod_customer+"&"+"status="+status;
        		
        httpClient.setDoOutput(true);
        try (DataOutputStream wr = new DataOutputStream(httpClient.getOutputStream())) {
            wr.writeBytes(urlParameters);
            wr.flush();
        }

        try (BufferedReader in = new BufferedReader(new InputStreamReader(httpClient.getInputStream()))) {

            String line;
            StringBuilder response = new StringBuilder();

            while ((line = in.readLine()) != null) {
                response.append(line);
            }

 
        }
        
        return null;
	}
	
	public static String liberarPlanoIttv(String cod_customer, String plano) throws Exception{
		String url = "http://172.17.0.71/ittv_api/createOrder.php";
		HttpURLConnection httpClient = (HttpURLConnection) new URL(url).openConnection();
		
        httpClient.setRequestMethod("POST");
        httpClient.setRequestProperty("User-Agent", "Mozilla/5.0");
        httpClient.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        
        String urlParameters = "idCustomer="+cod_customer+"&"+"idProduct="+plano;
        		
        httpClient.setDoOutput(true);
        try (DataOutputStream wr = new DataOutputStream(httpClient.getOutputStream())) {
            wr.writeBytes(urlParameters);
            wr.flush();
        }

        try (BufferedReader in = new BufferedReader(new InputStreamReader(httpClient.getInputStream()))) {

            String line;
            StringBuilder response = new StringBuilder();

            while ((line = in.readLine()) != null) {
                response.append(line);
            }

 
        }
        
        return null;
	}
	
	public static void liberarNovoUsuario(AcessoCliente contrato, String usuario, String senha) throws Exception{
		
		//---------------------------------------------------
		
			EntityManager em = ConnUtil.getEntity();
			
			em.getTransaction().begin();
			
			contrato.setU_ittv(usuario);
			contrato.setS_ittv(senha);
			em.merge(contrato);
			
			Cliente c  = contrato.getCliente();
			em.merge(c);
			
			em.getTransaction().commit();
		
		//---------------------------------------------------
		
		if(contrato.getIttv_id() == null || contrato.getIttv_id().equals("")){
			String  cod_customer = cadastrarCustomer(contrato, usuario);
			
			if(cod_customer != null){
				String msg = alterarSenha(cod_customer, senha);			
				
				//Liberar plano ITTV
				String plano_ittv = contrato.getPlano().getPlano_ittv();
				String rPlano = liberarPlanoIttv(cod_customer, plano_ittv);
				System.out.println(rPlano);	
			}
        }else{
        	String msg = alterarSenha(contrato.getIttv_id(), senha);
        	String plano_ittv = contrato.getPlano().getPlano_ittv();
        	String rPlano = liberarPlanoIttv(contrato.getIttv_id(), plano_ittv);
        	atualizarStatus(contrato.getIttv_id(), "ACTIVE");
        	
        	System.out.println(rPlano);
        }
		
		//Redirecionar para Login ITTVs
		//https://tv.ittv.com.br/login
	}
	
	public static void liberarNovoUsuario2(AcessoCliente contrato, String usuario, String senha) throws Exception{
					
		String email = contrato.getCliente().getEmail();
		String nome = contrato.getCliente().getNome_razao();
		String telefone = "("+contrato.getCliente().getDdd_fone1()+")"+contrato.getCliente().getTelefone1();		
		String cep = contrato.getCliente().getEndereco_principal().getCep();
		String endereco = contrato.getCliente().getEndereco_principal().getEndereco();
		String cidade = contrato.getCliente().getEndereco_principal().getCidade();
		
				OkHttpClient client = new OkHttpClient().newBuilder().build();
				
			    MediaType mediaType = MediaType.parse("text/plain");
			    RequestBody body = RequestBody.create(mediaType, "");
			    Request request = new Request.Builder()
			      .url("http://172.17.0.71/ittv_api/cadastrar.php?"
			      		+ "email="+email+"&"
			      		+ "nome="+nome+"&"
			      		+ "telefone="+telefone+"&"
			      		+ "username="+usuario+"&"
			      		+ "cep="+cep+"&"
			      		+ "endereco="+endereco+"&"
			      		+ "cidade="+cidade+"")
			      .method("POST", body)
			      .build();
			    Response response = client.newCall(request).execute();
			
			    Gson gson = new GsonBuilder().create();
			    customer customer = gson.fromJson(response.body().toString(), customer.class);
			    
			    System.out.println(customer);
	}

}

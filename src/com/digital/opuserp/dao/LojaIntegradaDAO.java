package com.digital.opuserp.dao;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.json.JSONObject;

import com.digital.opuserp.domain.CatLojaProduto;
import com.digital.opuserp.domain.Produto;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.view.util.Notify;
import com.digital.opuserp.view.util.ParameterStringBuilder;

public class LojaIntegradaDAO {

	
//	public static String alterarProduto(String ie,String n, String dc, String a, String d, String codGrupo, String nomeGrupo, String url_youtube, String codProduto){
//		try{
//			
//			EntityManager em = ConnUtil.getEntity();
//			Query q = em.createQuery("select c from CatLojaProduto c where c.produto=:p", CatLojaProduto.class);
//			q.setParameter("p", new Produto(Integer.parseInt(codProduto)));
//			String categorias = new String();
//			Integer i = 0;
//			
//			boolean allowCat = true;
//			for (CatLojaProduto c: (List<CatLojaProduto>)q.getResultList()) {
//								
//				String categoria = listarCategorias(c.getCategoria().getId().toString());
//				
//				if(i > 0){
//					categorias = categorias + ",";
//				}
//				if(categoria == null || categoria.equals("")){ 
//					//cadastra nova categoria
//					String cat = cadastrarCategoria(c.getCategoria().getNome(), c.getCategoria().getId().toString());
//					
//					if(cat != null){
//						categorias = categorias+"/api/v1/categoria/"+cat;
//					}else{
//						allowCat = false;
//					}
//				}else{
//					categorias= categorias+"/api/v1/categoria/"+categoria;					
//				}
//				
//				
//				i++;
//			}
//			
//			if(allowCat){
//				
//					String id_externo = ie;
//					String nome = n;
//					String descricao_completa = dc;
//					String ativo = a;
//					String destaque = d;
//					
//					URL url = new URL("http://179.127.32.7:8989/testeAPI.php");
//					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//					conn.setDoOutput(true);
//					conn.setRequestMethod("POST");
//					
//					Map<String, Object> parameters = new HashMap<>();
//						
//					//String			
//					parameters.put("funcao", "alterarProduto");		
//					parameters.put("codProduto", id_externo);
//					parameters.put("nome", nome);		
//					parameters.put("ativo", ativo);
//					parameters.put("destaque", destaque);
//					parameters.put("categorias", categorias.replaceAll(",","|")); 
//					parameters.put("url_video_youtube", url_youtube != null ? url_youtube : "");
//					
//					conn.setDoOutput(true);
//					DataOutputStream out = new DataOutputStream(conn.getOutputStream());
//					String url1 = ParameterStringBuilder.getParamsString(parameters);
//					out.writeBytes(url1);
//					out.flush();
//					out.close();
//			
//					if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
//						Notify.Show("Erro:"+conn.getResponseMessage(), Notify.TYPE_ERROR);
//						throw new RuntimeException("Erro ao acessar o webservice : " + conn.getResponseMessage());
//					}
//			
//					BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
//			
//					String retorno;											
//					String cod_loja = null;
//					while ((retorno = br.readLine()) != null) {
//						
//						System.out.println(retorno); 
//						//JSONObject jsonObject = new JSONObject("{"+retorno.split("[{]+")[1]);	
//						//cod_loja = retorno.split(",")[11].split(":")[1];
//						//String cod = 
//						
//						//Notify.Show(retorno, Notify.TYPE_NOTICE); 
//					}
//			
//					conn.disconnect();
//					
//					return cod_loja;
//			}else{
//				return null;
//			}
//		}catch(Exception e){
//			e.printStackTrace();
//			return null;
//		}
//		
//	}
//	
//	public static void alterarPrecos(String cod_loja, String cheio,String custo, String promocional){
//		try{
//			URL url = new URL("http://179.127.32.7:8989/testeAPI.php");
//			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//			conn.setDoOutput(true);
//			conn.setRequestMethod("POST");
//			
//			Map<String, Object> parameters = new HashMap<>();
//								
//			parameters.put("funcao", "alterarPrecos");			
//			parameters.put("codProduto", cod_loja);
//			parameters.put("cheio", cheio);
//			parameters.put("custo", custo);
//			parameters.put("promocional", promocional);
//			
//			conn.setDoOutput(true);
//			DataOutputStream out = new DataOutputStream(conn.getOutputStream());
//			out.writeBytes(ParameterStringBuilder.getParamsString(parameters));
//			out.flush();
//			out.close();
//			
//			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
//				Notify.Show("Erro:"+conn.getResponseMessage(), Notify.TYPE_ERROR);
//				throw new RuntimeException("Erro ao acessar o webservice : " + conn.getResponseMessage());
//			}
//	
//			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
//	
//			String retorno;											
//			//String cod_loja = null;
//			while ((retorno = br.readLine()) != null) {
//				
//				//JSONObject jsonObject = new JSONObject("{"+retorno.split("[{]+")[1]);	
//				//cod_loja = String.valueOf((Integer)jsonObject.get("id"));		
//				//Notify.Show(retorno, Notify.TYPE_NOTICE); 
//				System.out.println(retorno); 
//			}
//	
//			conn.disconnect();
//			
//			//return cod_loja;
//			
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//	}
//	
//	public static String listarCategorias(String codGrupo){ 
//		try{
//			URL url = new URL("http://172.17.0.71/testeAPI.php");
//			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//			conn.setDoOutput(true);
//			conn.setRequestMethod("POST");
//			
//			Map<String, Object> parameters = new HashMap<>();
//											
//			parameters.put("funcao", "procurarCategoria");		
//			parameters.put("codExterno", codGrupo);
//					
//			conn.setDoOutput(true);
//			DataOutputStream out = new DataOutputStream(conn.getOutputStream());
//			out.writeBytes(ParameterStringBuilder.getParamsString(parameters));
//			out.flush();
//			out.close();
//	
//			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
//				Notify.Show("Erro:"+conn.getResponseMessage(), Notify.TYPE_ERROR);
//				throw new RuntimeException("Erro ao acessar o webservice : " + conn.getResponseMessage());
//			}
//	
//			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
//	
//			String retorno;		
//			String result = "";
//			while ((retorno = br.readLine()) != null) {
//				
//				result = retorno;
//				
//				System.out.println(retorno); 
//			}
//	
//			conn.disconnect();
//			
//			String seoCategoria = "";
//			if(result != null && !result.equals("") && !result.contains("string(0)")){
//				if(result.split(":").length >= 3){
//					seoCategoria = result.split(":")[3].toString().split(",")[0];
//				}else{
//					seoCategoria = null;
//				}
//			}
//			
//			if(seoCategoria  != null){
//				return seoCategoria.replaceAll("\\\"", "").replaceAll(" ", "");
//			}else{
//				return null;
//			}
//		}catch(Exception e){
//			e.printStackTrace();
//			
//			return null;
//		}
//	}
//	
//	public static String cadastrarCategoria(String nomeCategoria, String codCategoria){
//		
//		try{
//			URL url = new URL("http://179.127.32.7:8989/testeAPI.php");
//			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//			conn.setDoOutput(true);
//			conn.setRequestMethod("POST");
//			
//			Map<String, Object> parameters = new HashMap<>();
//											
//			parameters.put("funcao", "cadastrarCategoria");		
//			parameters.put("codCategoria", codCategoria);
//			parameters.put("nome", nomeCategoria);
//					
//			conn.setDoOutput(true);
//			DataOutputStream out = new DataOutputStream(conn.getOutputStream());
//			out.writeBytes(ParameterStringBuilder.getParamsString(parameters));
//			out.flush();
//			out.close();
//	
//			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
//				Notify.Show("Erro:"+conn.getResponseMessage(), Notify.TYPE_ERROR);
//				throw new RuntimeException("Erro ao acessar o webservice : " + conn.getResponseMessage());
//			}
//	
//			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
//	
//			String retorno;		
//			String result = "";
//			while ((retorno = br.readLine()) != null) {
//				
//				result = retorno;
//				
//				System.out.println(retorno); 
//			}
//	
//			conn.disconnect();
//			
//			String seoCategoria = "";
//			if(result != null){
//				if(result.split(":").length >= 3){				
//					seoCategoria = result.split(":")[3].toString().split(",")[0];
//				}else{
//					seoCategoria  = null;
//				}
//			}
//			
//			if(seoCategoria != null){
//				return seoCategoria.replaceAll("\\\"", "");
//			}else{
//				return null;
//			}
//		}catch(Exception e){
//			e.printStackTrace();
//			
//			return null;
//		}
//	}
	public static String buscarProduto(String produto_id){
		return "";
	}
	
//	public static String cadastrarProduto(String ie,String n, String dc, String a, String d, String codGrupo, String nomeGrupo, String codProduto){
//		try{
//			
//			EntityManager em = ConnUtil.getEntity();
//			Query q = em.createQuery("select c from CatLojaProduto c where c.produto=:p", CatLojaProduto.class);
//			q.setParameter("p", new Produto(Integer.parseInt(codProduto)));
//			String categorias = new String();
//			Integer i = 0;
//			
//			boolean allowCat = true;
//			for (CatLojaProduto c: (List<CatLojaProduto>)q.getResultList()) {
//								
//				String categoria = listarCategorias(c.getCategoria().getId().toString());
//				
//				if(i > 0){
//					categorias = categorias + ",";
//				}
//				if(categoria == null || categoria.equals("")){ 
//					//cadastra nova categoria
//					String cat = cadastrarCategoria(c.getCategoria().getNome(), c.getCategoria().getId().toString());
//					
//					if(cat != null){
//						categorias = categorias+"/api/v1/categoria/"+cat;
//					}else{
//						allowCat = false;
//					}
//				}else{
//					categorias= categorias+"/api/v1/categoria/"+categoria;					
//				}
//				
//				
//				i++;
//			}
//			
//			
//			if(allowCat){
//					String id_externo = ie;
//					String nome = n;
//					String descricao_completa = dc;
//					String ativo = a;
//					String destaque = d;
//					
//					URL url = new URL("http://179.127.32.7:8989/testeAPI.php");
//					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//					conn.setDoOutput(true);
//					conn.setRequestMethod("POST");
//					
//					Map<String, Object> parameters = new HashMap<>();
//								
//					parameters.put("funcao", "cadastrarProduto");			
//					parameters.put("id_externo", id_externo);
//					parameters.put("nome", nome);
//					parameters.put("descricao_completa", descricao_completa);
//					parameters.put("ativo", ativo);
//					parameters.put("destaque", destaque);
//					parameters.put("categorias", categorias.replaceAll(",","|")); 
//					
//					//parameters.put("marca", m);
//					
//					conn.setDoOutput(true);
//					DataOutputStream out = new DataOutputStream(conn.getOutputStream());
//					out.writeBytes(ParameterStringBuilder.getParamsString(parameters));
//					out.flush();
//					out.close();
//			
//					if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
//						Notify.Show("Erro:"+conn.getResponseMessage(), Notify.TYPE_ERROR);
//						throw new RuntimeException("Erro ao acessar o webservice : " + conn.getResponseMessage());
//					}
//			
//					BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
//			
//					String retorno;											
//					String cod_loja = null;
//					while ((retorno = br.readLine()) != null) {
//						
//						JSONObject jsonObject = new JSONObject("{"+retorno.split("[{]+")[1]);	
//						cod_loja = String.valueOf((Integer)jsonObject.get("id"));			
//						//Notify.Show(retorno, Notify.TYPE_NOTICE); 
//						System.out.println(retorno); 
//					}
//			
//					conn.disconnect();					
//					return cod_loja;
//			
//			}else{
//				return null;
//			}
//		}catch(Exception e){
//			e.printStackTrace();
//			return null;
//		}
//	}
}

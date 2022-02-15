package com.digital.opuserp.util;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;

import com.digital.opuserp.dao.CepDAO;
import com.digital.opuserp.domain.Ceps;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyReplacer;

/**
 *
 * @author Marconi
 */
public class CepUtil {
	
	EntityManager em;
	
    private String cep;

    public CepUtil() {
    	
    	em = ConnUtil.getEntity();
    }

    
   
    public Ceps pegarCep(String cep){
        this.cep = cep;  
        //System.out.println("O CEP é:"+cep);
        String urlString = "http://viacep.com.br/ws/"+cep+"/xml/";


		try {
			// Objeto URL
			URL url = new URL(urlString);

			// Objeto HttpURLConnection
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			// Método
			//connection.setRequestProperty("Request-Method", "GET");
			connection.setRequestProperty("content-type", "text/plain; charset=utf-8");
			connection.setRequestProperty("Accept-Charset", "UTF-8");

			// Váariavel do resultado
			connection.setDoInput(true);
			connection.setDoOutput(false);

			// Faz a conexão
			connection.connect();

			// Abre a conexão
			//BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
			

			// Lê a conexão
			StringBuffer newData = new StringBuffer();
			String s = "";
			while (null != ((s = br.readLine()))) {
				newData.append(s);
			}
			br.close(); 
			
						XStream xstream = new XStream();  		
						xstream.alias ("xmlcep", Cep.class);
						xstream.setClassLoader(Cep.class.getClassLoader());
						Cep cepSerializado = (Cep)xstream.fromXML(newData.toString());
						
						
						
                        
                               String cepReturn1 = cepSerializado.getCep();
                               String cepReturn2 = cepReturn1.replaceAll(" ", "").replaceAll("-", "");
                               Ceps cepReturn = new Ceps(cepReturn2,
                            		   cepSerializado.getLogradouro().toUpperCase(),cepSerializado.getBairro().toUpperCase(), 
                            		  cepSerializado.getLocalidade().toUpperCase(), cepSerializado.getUf().toUpperCase(),  "BRASIL");
                               if(cepReturn.getCep().equals(cep)){
                            	  
                            	   CepDAO cpDao = new CepDAO();
                                   Ceps cepLocal =  cpDao.getCep(cep);
         	
                            	   try {
                            		   
                            		   // Inicia uma transação com o banco de dados.
                            		   em.getTransaction().begin();
                            		  // verifica se o cep informado já está cadastrado
                            		   if(cepLocal != null ){
                            			   //Atualizar cep
                            			   em.remove(cepLocal);
                              			   em.merge(cepReturn);
                            		   }else{
                            			   //cadastrar cep
                            			   em.persist(cepReturn);
                            		   		} 
                            		   
                            		   try{
                            			   em.getTransaction().commit();
	                           	       }catch(Exception e){
	                           	    		e.printStackTrace();
	                           	    		System.out.println("Erro ao comitar as informações-> Cep: "+e.getMessage());
	                           	       }

                            		} catch(Exception e) {
                            		   e.printStackTrace();
                            		   System.out.println("Erro ao atualizar ou cadastrar Cep: "+e.getMessage());
                            		}
                            	   
  
                               
                            	   return cepReturn;
                            	   
                               }else{
                            	   CepDAO cpDao = new CepDAO();
                            	   return cpDao.getCep(cep);                           	   
                               }


		} catch (Exception e) {
               e.printStackTrace();
		}
		return null;
        
    }
    
    
    
    
    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }
    
    
    
    
}

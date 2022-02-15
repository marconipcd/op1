package com.digital.opuserp.test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import com.digital.opuserp.domain.CidadeIbge;

public class IbgeTest {
	static EntityManager em;
	static EntityManagerFactory emf;
	public static void main(String[] args) {
		
		emf = Persistence.createEntityManagerFactory("OpusERP4");
		em = emf.createEntityManager();		
		
		try{
			FileInputStream stream = new FileInputStream("D:/dtb_2013.csv");
			InputStreamReader reader = new InputStreamReader(stream,Charset.forName("ISO-8859-1"));
			BufferedReader br = new BufferedReader(reader);
			String linha = br.readLine();
			em.getTransaction().begin();
			while(linha != null) {
			 
				String[] s = linha.split(";");
				
				String cidade = s[7];
				String codigo = s[6];
				
				System.out.println(cidade+":"+codigo);
				
				Query q = em.createQuery("select c from CidadeIbge c where c.cidade=:cidade", CidadeIbge.class);
				q.setParameter("cidade", cidade);
				
				if(q.getResultList().size() == 0){
					em.persist(new CidadeIbge(null, cidade, codigo));
				}
				
				
			   linha = br.readLine();
			}
			em.getTransaction().commit();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}

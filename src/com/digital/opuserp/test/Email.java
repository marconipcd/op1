package com.digital.opuserp.test;



import java.net.URL;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

import com.digital.opuserp.domain.AcessoCliente;
import com.digital.opuserp.domain.Produto;

public class Email {

	static EntityManager em;
	static EntityManagerFactory emf;
	public static void main(String[] args) {
		
		emf = Persistence.createEntityManagerFactory("OpusERP4");
		em = emf.createEntityManager();		
		
		Query q = em.createQuery("select a from AcessoCliente a where a.status_2 = 'ATIVO' and a.cliente.email not like '%digitalonline.com.br%'  and a.id > 1159",AcessoCliente.class);
		List<AcessoCliente> listContratos = q.getResultList();
		
		int qtd = 0;
		System.out.println(q.getResultList().size());
		for (AcessoCliente acessoCliente : listContratos) {			
		
			if(acessoCliente.getCliente().getEmail() != null && acessoCliente.getCliente().getEmail() != ""){
				qtd++;
				try {
					HtmlEmail email = new HtmlEmail();
					email.setHostName("smtp.gmail.com");
					email.setSmtpPort(587);
					email.setStartTLSRequired(true);
					email.setAuthentication("centraldoassinante@digitalonline.com.br", "aQsW2435");
					email.setFrom("suporte@digitalonline.com.br", "DIGITAL");
					
				//	acessoCliente.getCliente().getEmail().con
					email.addTo(acessoCliente.getCliente().getEmail(), acessoCliente.getCliente().getNome_razao());
					
					
					
					//email.addTo("ademir@digitalonline.com.br", "Ademir Pinto");
					email.setSubject("AVISO DE INTERRUPÇÃO PROGRAMADA");
					 
					// adiciona uma imagem ao corpo da mensagem e retorna seu id
					URL url = new URL("http://179.127.32.7/aviso/logobloqueado.png");
					String cid = email.embed(url, "digital logo");
					 
					// configura a mensagem para o formato HTML
					email.setHtmlMsg(
							"<html>"
							+ "<style>	"
							+ "body {background: #00ADFF;margin: 0;padding: 20px;text-align: center;font-family: Arial, Helvetica, sans-serif;font-size: 14px;color: #666666;}"
							+ ".error_page {width: 812px;padding: 50px;margin: auto;}"
							+ ".error_page h1{margin-top: 38px;}"
							+ ".error_page span{font-size: 12px;}"					
							+ "</style>"
							+ ""
							+ "<body style='background: #00ADFF;margin: 0;padding: 20px;text-align: center;font-family: Arial, Helvetica, sans-serif;font-size: 14px;color: #666666;' class='login'>"
								+ "<div style='width: 812px;padding: 50px;margin: auto;' class='error_page'>"
								+ "<h1 style='margin-top: 38px;'>AVISO DE INTERRUPÇÃO PROGRAMADA</h1></br>"
								+ "<h3 style='color: #FFF;'>Comunicamos que no próximo Domingo dia 29 de março de 2015 das 07:00h às 10:00h da manhã, o serviço de internet ficará indisponível para manutenção e melhorias no sistema de transmissão de internet.</h3></br>"
								+ "<p style='color:#fff;'>Para mais informações ligue: (81) 3726-3125 ou 0800 081 3125 <br/>Ou se preferir pelos celulares: (81) 9627-0025 (tim), (81) 9166-2583 (claro), (81) 8828-1126 (oi).</p>		    "
								+ "<br><br><br><br><br><br>"
								+ "<a href='http://digitalonline.com.br/'>  	<img src=\"cid:"+cid+"\">  </a>"
							+ "</body>"					
							+ "</html>");
							
					 
					// configure uma mensagem alternativa caso o servidor não suporte HTML
					email.setTextMsg("Seu servidor de e-mail não suporta mensagem HTML");
					 
					// envia o e-mail
					email.send();
					System.out.println("Enviado para :"+acessoCliente.getCliente().getEmail());
				} catch (Exception e) {
					
					e.printStackTrace();
				}
				
				
			}
		}
		System.out.println(qtd);

	}
	
	

}

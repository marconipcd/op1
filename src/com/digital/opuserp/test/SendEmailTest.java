package com.digital.opuserp.test;

import org.apache.commons.mail.HtmlEmail;


public class SendEmailTest {

		public static void main(String [] args)
	   {
			try{
				HtmlEmail email = new HtmlEmail();
				email.setHostName("smtp.gmail.com"); // o servidor SMTP para envio do e-mail
				email.setSmtpPort(465);
				email.setDebug(true);				
				email.setSSLOnConnect(true);
				email.addTo("marconipcd1@gmail.com", "Cesar Marconi"); //destinatário
				email.setFrom("nfe@digitalonline.com.br", "Digital"); // remetente
				email.setSubject("Mensagem de Teste"); // assunto do e-mail
				email.setMsg("<html>"
										+ "<br/><span style='font-family:Arial; font-size:12px;'>Prezado Nome do cliente,</span> "
										+ "<br/><br/> <span style='font-family:Arial; font-size:12px;'>Obrigado por escolher os serviços da DIGITAL. </span>"
										+ "<br/><br/> <span style='font-family:Arial; font-size:12px;'>Segue em anexo, Nota Fiscal Avulsa de Serviço de Comunicação integrada com boleto. </span>"
										+ "<br/><span style='font-family:Arial; font-size:12px;'>Se já recebeu o boleto anteriormente, considerar apenas a Nota Fiscal. </span>"
										+ "<br/><br/><span style='font-family:Arial; font-size:12px;'>*Este e-mail foi enviado automaticamente pelo Sistema de Notas Fiscais Eletrônicas (NF-e) da DIGITAL.</span> "
										+ "<br/><span style='font-family:Arial; font-size:12px;'>Favor não responder. Em caso de dúvidas, entre em contato com financeiro@digitalonline.com.br.</span>"
										+ "</html>"); 
				
				
				email.setAuthentication("nfe@digitalonline.com.br", "notasfiscais_envioautomatico");
				email.send(); //envia o e-mail
			}catch(Exception e){
				e.printStackTrace();
			}
			
	   }
}

package com.digital.opuserp.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.internet.InternetAddress;

import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.HtmlEmail;

import com.digital.opuserp.dao.NfeDAO;
import com.digital.opuserp.domain.ConfigNfe;
import com.jcabi.immutable.Array;



public class EmailUtil {

	private static Pattern pattern;
	private static Matcher matcher;
 
	private static final String EMAIL_PATTERN = 
		"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
		+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
 
	public EmailUtil() {
	}
 
	public static boolean validate(final String hex) {
		if(hex != null && !hex.isEmpty()){
			pattern = Pattern.compile(EMAIL_PATTERN);
			matcher = pattern.matcher(hex);
			return matcher.matches();
		}
		
		return false;
 
	}
	
	public static boolean Send(String nomeCliente, String endEmail,String endEmailAlternativo, String pathFull){
		try{
			
			ConfigNfe valueDefaultNfe = NfeDAO.getDefaultValue();	
			
			HtmlEmail email = new HtmlEmail();
			email.setHostName(valueDefaultNfe.getServer_email()); 
			email.setSmtpPort(Integer.parseInt(valueDefaultNfe.getPorta_email()));
			//email.setDebug(true);				
			email.setSSLOnConnect(true);
			email.addTo(endEmail,nomeCliente); 
			if(endEmailAlternativo != null){
				email.addTo(endEmailAlternativo,nomeCliente);	
			}
			
			
			email.setFrom(valueDefaultNfe.getLogin(), "DIGITAL"); 
			email.setSubject("Nota Fiscal Serviço de Comunicação");
			email.setCharset("UTF-8");
			
			
			List<InternetAddress> lIa = new ArrayList<>();
			lIa.add(new InternetAddress(valueDefaultNfe.getEmail_copia_oculta()));
			
			email.setCc(lIa);
			
			
			EmailAttachment attachment = new EmailAttachment();
			attachment.setPath(pathFull);
			attachment.setDisposition(EmailAttachment.ATTACHMENT);			
			attachment.setDescription("Nota Fiscal");
			//attachment.setName("John");
			
			email.attach(attachment);
			
			email.setMsg(valueDefaultNfe.getMsg_email()); 
			
			
			email.setAuthentication(valueDefaultNfe.getLogin(),valueDefaultNfe.getSenha());
			
			email.send(); //envia o e-mail
			
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
}

package com.digital.opuserp.dao;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;
import javax.persistence.EntityManager;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.LogError;
import com.digital.opuserp.domain.PrintLog;
import com.digital.opuserp.util.ConnUtil;

public class LogErrorDAO {

	
	public static void add(LogError log){
		EntityManager em = ConnUtil.getEntity();
		try{
			
				em.getTransaction().begin();
				em.persist(log);
				em.getTransaction().commit();
				
				
				Toolkit toolkit = Toolkit.getDefaultToolkit();
				Dimension screenSize = toolkit.getScreenSize();
				Rectangle screenRect = new Rectangle(0,0,screenSize.width, screenSize.height+100);
				
				Robot robot = new Robot();
				BufferedImage screenCapturedImage = robot.createScreenCapture(screenRect);
					
				String nomeFile;
				if(OpusERP4UI.getCurrent().getPage().getWebBrowser().isLinux()){
				
					nomeFile = "/var/tmp/screen_log_"+log.getFuncao()+".jpg";
					
				}else{
					nomeFile = "%temp%"+log.getFuncao()+".jpg";
				}
				
				File file = new File(nomeFile);
				ImageIO.write( screenCapturedImage, "jpg", file );
				
				Path path = Paths.get(nomeFile);
				byte[] imageInByte = Files.readAllBytes(path);
						
				
				em.getTransaction().begin();
				em.persist(new PrintLog(null, imageInByte, log));
				em.getTransaction().commit();			
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("Não Foi Possivel Registrar o Log de ERRO!");
		}
	}
}

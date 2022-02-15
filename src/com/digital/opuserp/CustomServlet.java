package com.digital.opuserp;

import javax.servlet.ServletException;

import com.vaadin.server.CustomizedSystemMessages;
import com.vaadin.server.ServiceException;
import com.vaadin.server.SessionInitEvent;
import com.vaadin.server.SessionInitListener;
import com.vaadin.server.SystemMessages;
import com.vaadin.server.SystemMessagesInfo;
import com.vaadin.server.SystemMessagesProvider;
import com.vaadin.server.VaadinServlet;

public class CustomServlet extends VaadinServlet{
	
	

	private static class MySystemMessagesProvider implements SystemMessagesProvider {

        @Override
        public SystemMessages getSystemMessages(
                SystemMessagesInfo systemMessagesInfo) {
            CustomizedSystemMessages messages = new CustomizedSystemMessages();
            
            messages.setInternalErrorCaption("ERRO INTERNO");
            messages.setInternalErrorMessage("Houve um Erro Interno, Tente Reiniciar a Aplicação!");
            messages.setSessionExpiredCaption("SESSÃO EXPIROU");
            messages.setSessionExpiredMessage("A Sua Sessão Expirou! Reinicie a Aplicação!");
          //  messages.setOutOfSyncCaption("ERRO DE LEITURA");
         //   messages.setOutOfSyncMessage("Houve um Erro de Leitura durante a Execução da Aplicação!");
            messages.setCommunicationErrorCaption("ERRO DE COMUNICAÇÃO");
            messages.setCommunicationErrorMessage("Houve um Erro de Comunicação com A Aplicação, Espere um Pouco e Reinicie a Aplicação!");
            messages.setCookiesDisabledCaption("ERRO OS COOKIES DO NAVEGADOR ESTA DESABILITADO");
            messages.setCookiesDisabledMessage("Por favor Habilite os Cookies do Seu Navegador e Reinicie a Aplicação!");
            messages.setAuthenticationErrorCaption("ERRO DE AUTENTICAÇÃO");
            messages.setAuthenticationErrorMessage("Não Foi Possivel Obter as Chaves Necessárias para Autenticar a Aplicação!");
                 
            return messages;
        }
        
    }

    @Override
    public void servletInitialized() throws ServletException {
        super.servletInitialized();
        
        getService().addSessionInitListener(new SessionInitListener() {

            public void sessionInit(SessionInitEvent event)
                    throws ServiceException {
                event.getService().setSystemMessagesProvider(new MySystemMessagesProvider());
            }
            
        });
        
        
        
    }

	
    
    

	
}

package com.digital.opuserp.util;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import com.digital.opuserp.dao.ContasDAO;
import com.digital.opuserp.dao.FornecedorDAO;
import com.digital.opuserp.domain.ContasPagar;

public class OpenCSVParser {
	
	public static void main(String[] args) {
		
		try{
			parseCSVToCodigosReferencia();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public static void parseCSVToCodigosReferencia() throws IOException {
        
		CSVFormat format = CSVFormat.RFC4180.withHeader().withDelimiter(';');

        CSVParser parser = new CSVParser(new FileReader("/home/marconi/Downloads/CONTA_PAGAR.csv"), format);
         
        List<ContasPagar> contas = new ArrayList<ContasPagar>();
        
     EntityManager em = ConnUtil.getEntity();
     em.getTransaction().begin();
        for(CSVRecord record : parser){        
        	
        	ContasPagar conta = new ContasPagar();
        	
        	if(record.get("EMPRESA").equals("DIGITAL")){
        		conta.setEmpresa_id(1);
        	}else{
        		conta.setEmpresa_id(3);
        	}
        	       	
        	conta.setTermo(record.get("TERMO"));
        	conta.setTipo(record.get("TIPO"));
        	conta.setConta(ContasDAO.getContaById(record.get("CONTA")));        	
        	conta.setFornecedor(FornecedorDAO.findbyCnpj(record.get("FORNECEDOR"), conta.getEmpresa_id()));        	
        	conta.setDescricao(record.get("DESCRICAO"));
        	
        	try{
        		conta.setParcela(record.get("PARCELA").substring(0, 2)+"/"+record.get("PARCELA").substring(2, 4));
        	}catch(Exception e){
        		conta.setParcela("1");
        	}
        	try{
        		if(!record.get("VENCIMENTO").equals("") && !record.get("VENCIMENTO").isEmpty()){
        			conta.setData_vencimento(DataUtil.stringToDate(record.get("VENCIMENTO")));
        		}
        	}catch(Exception e){
        		//conta.setData_vencimento(null);
        	}
        
        	try{
	        	if(!record.get("VALOR").replace("R$","").replace("-", "").equals("") && !record.get("VALOR").replace("R$","").replace("-", "").isEmpty()){
	        		conta.setValor_titulo(Real.formatStringToDBDouble(record.get("VALOR").replace("R$","").replace("-", "")));
	        	}
        	}catch(Exception e){
        		conta.setValor_titulo(new Double("0"));
        	}
        	
        	conta.setStatus(record.get("STATUS"));
        	
        	if(!record.get("DATA_PGTO").equals("")){
        		conta.setData_pago(DataUtil.stringToDate(record.get("DATA_PGTO")));
        	}
        	
        	try{
	        	if(!record.get("VALOR_PGTO").replace("R$","").replace("-", "").equals("") && !record.get("VALOR_PGTO").replace("R$","").replace("-", "").isEmpty()){
	        		conta.setValor_pagamento(Real.formatStringToDBDouble(record.get("VALOR_PGTO").replace("R$","").replace("-", "")));
	        	}
        	}catch(Exception e){
        		conta.setValor_pagamento(new Double("0"));
        	}
        	
        //	conta.setForma_pgto(ContaDAO.getContaById(record.get("FORMA_PGTO")));
        	
        	try{
        		if(!record.get("CADASTRO").equals("") && !record.get("CADASTRO").isEmpty()){
        			conta.setData_cadastro(DataUtil.stringToDate(record.get("CADASTRO")));
        		}else{
        			conta.setData_cadastro(new Date());
        		}
        	}catch(Exception e){
        		conta.setData_cadastro(new Date());
        	}
        	        	
        	System.out.println(conta);        	        	
        	em.persist(conta);        	
        }
     em.getTransaction().commit();

        
        parser.close();
    }
}

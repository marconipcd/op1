package com.digital.opuserp.test;

import com.digital.opuserp.dao.GerenciaNet;

public class GerenciaTest {

	public static void main(String[] args) {
		
		try{
			GerenciaNet gerenciaDAO = new GerenciaNet();
		}catch(Exception e){
			System.out.println(e.toString());
		}
	}

}

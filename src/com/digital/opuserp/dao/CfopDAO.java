package com.digital.opuserp.dao;

import javax.persistence.EntityManager;

import com.digital.opuserp.domain.Cfop;
import com.digital.opuserp.util.ConnUtil;

public class CfopDAO {

	public  static Cfop find(Integer id){
		EntityManager em  = ConnUtil.getEntity();		
		
		try{
			if(id != null && id > 0){
				Cfop cfop = em.find(Cfop.class, id);
				return cfop;
			}
			
			return null;
		
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
}

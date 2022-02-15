package com.digital.opuserp.dao;

import javax.persistence.EntityManager;

import com.digital.opuserp.util.ConnUtil;

public class FiadorDAO {
	EntityManager em;
	
	public FiadorDAO(){
		em = ConnUtil.getEntity();
	}
	
	
}

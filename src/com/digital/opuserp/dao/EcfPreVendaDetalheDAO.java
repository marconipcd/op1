package com.digital.opuserp.dao;

import javax.persistence.EntityManager;

import com.digital.opuserp.domain.EcfPreVendaDetalhe;
import com.digital.opuserp.util.ConnUtil;

public class EcfPreVendaDetalheDAO {

	public static EcfPreVendaDetalhe find(Integer id){
		EntityManager em = ConnUtil.getEntity();
		return em.find(EcfPreVendaDetalhe.class, id);
	}
}

package com.digital.opuserp.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.domain.EcfPreVendaCabecalho;
import com.digital.opuserp.domain.EcfPreVendaDetalhe;
import com.digital.opuserp.util.ConnUtil;

public class ItensCompraDAO {

	public static List<EcfPreVendaDetalhe> getItens(EcfPreVendaCabecalho compra){
		EntityManager em =  ConnUtil.getEntity();
		Query q = em.createQuery("select i from EcfPreVendaDetalhe i where i.ecfPreVendaCabecalhoId=:item", EcfPreVendaDetalhe.class);
		q.setParameter("item", compra.getId());
		
		return q.getResultList();
	}
}

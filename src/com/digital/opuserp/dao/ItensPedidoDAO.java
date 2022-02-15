package com.digital.opuserp.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.domain.EcfPreVendaCabecalho;
import com.digital.opuserp.domain.EcfPreVendaDetalhe;
import com.digital.opuserp.util.ConnUtil;

public class ItensPedidoDAO {

	public static List<EcfPreVendaDetalhe> getItens(EcfPreVendaCabecalho pedido){
		EntityManager em =  ConnUtil.getEntity();
		Query q = em.createQuery("select i from EcfPreVendaDetalhe i where i.ecfPreVendaCabecalhoId=:item", EcfPreVendaDetalhe.class);
		q.setParameter("item", pedido.getId());
		
		return q.getResultList();
	}
}

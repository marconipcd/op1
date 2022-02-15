package com.digital.opuserp.dao;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.EcfPreVendaCabecalho;
import com.digital.opuserp.domain.FormasPgto;
import com.digital.opuserp.domain.HaverCab;
import com.digital.opuserp.domain.HaverDetalhe;
import com.digital.opuserp.domain.TotaisPedido;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.view.modulos.pedido.pedidoProduto.TotaisHash;

public class TotaisPedidoDAO {

	public static void save(HashMap<Integer, TotaisHash> totais_forma_pgto,EcfPreVendaCabecalho pedido, HaverCab haver, double haver_valor){
		if(totais_forma_pgto != null && pedido != null){
			EntityManager em = ConnUtil.getEntity();

			em.getTransaction().begin();

			if(totais_forma_pgto != null && totais_forma_pgto.size() > 0){
				for(Entry<Integer, TotaisHash> entry : totais_forma_pgto.entrySet()) {
					FormasPgto formaPgto = FormaPgtoDAO.find(entry.getKey());
					TotaisHash t = entry.getValue();
					Double valorPgto = t.getValor();
					Integer qtd = t.getQtdParc();
					
					em.persist(new TotaisPedido(null, pedido, formaPgto, valorPgto, qtd));
				}
			}
			
			if(haver != null && haver_valor > 0){
				
				em.persist(new TotaisPedido(null, pedido, haver, haver_valor, 1));
				em.persist(new HaverDetalhe(null, haver, "SAIDA", haver_valor, "PEDIDO", pedido.getId().toString(), "VENDA", "", new Date(), OpusERP4UI.getUsuarioLogadoUI().getUsername(),"ATIVO"));
				em.merge(haver);

//				haver.setValor_total(haver.getValor_total()-haver_valor);
//				em.merge(haver);
			}

			em.getTransaction().commit();
		}
	}
	
	public static List<TotaisPedido> getTotais(EcfPreVendaCabecalho pedido){
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createQuery("select t from TotaisPedido t where t.pedido=:pedido", TotaisPedido.class);
		q.setParameter("pedido", pedido);
		
		return q.getResultList();
	}
}

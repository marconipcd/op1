package com.digital.opuserp.dao;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.util.ConnUtil;

public class AlertaContasTrocaDAO {

	public static boolean check(Integer codBoleto){
		
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createNativeQuery("SELECT cr.ID FROM alteracoes_conta_Receber acr, contas_receber cr, clientes c, enderecos_principais ep  WHERE "
				+ "c.ID = ep.CLIENTES_ID and acr.CONTA_RECEBER_ID = cr.ID and c.ID = cr.CLIENTES_ID AND `TIPO` = 'IMPRIMIU UM BOLETO' AND acr.EMPRESA_ID = 1 "
				+ "AND cr.STATUS_2 = 'ABERTO' and acr.DATA_ALTERACAO >= '2018-08-22' and acr.DATA_ALTERACAO <= '2018-09-15' AND cr.ID =:codBoleto "
				+ "AND cr.DATA_EMISSAO <= '2018-09-27' GROUP BY cr.ID "
				+ "ORDER BY cr.DATA_VENCIMENTO ASC");
		
		q.setParameter("codBoleto", codBoleto);
		
		
		if(q.getResultList().size() > 0){
			return true;
		}else{
			return false;
		}
	}
}

package com.digital.opuserp.dao;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.Parametizacao;
import com.digital.opuserp.util.ConnUtil;

public class ParametizacaoDAO {

	
		public static String getValueDefault(Integer cod_submodulo, String chave){
			EntityManager em = ConnUtil.getEntity();
			Query q = em.createQuery("select p from Parametizacao p where p.cod_empresa=:empresa and p.cod_submodulo=:submodulo and p.chave_campo=:chave", Parametizacao.class);
			q.setParameter("empresa", OpusERP4UI.getEmpresa().getId());
			q.setParameter("submodulo", cod_submodulo);
			q.setParameter("chave", chave);
			
			if(q.getResultList().size() == 1){
				Parametizacao param = (Parametizacao)q.getSingleResult();
				return param.getValor_padrao();
			}
			
			return null;
		}
}

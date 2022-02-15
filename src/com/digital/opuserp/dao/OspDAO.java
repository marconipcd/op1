package com.digital.opuserp.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.AlteracoesOsp;
import com.digital.opuserp.domain.ArquivosOsp;
import com.digital.opuserp.domain.Osp;
import com.digital.opuserp.util.ConnUtil;

public class OspDAO {
	
	public static List<Osp> getOspPendentes(){
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createQuery("select o from Osp o where o.empresa_id=:empresa and o.data_previsao_termino<=:data and o.status='A' and o.setor != 'EXPEDICAO'", Osp.class);
		q.setParameter("empresa", OpusERP4UI.getEmpresa().getId());
		q.setParameter("data", new Date());
		
		return q.getResultList();
	}
	
	public static void removeRegistrosDependentes(Osp osp){
		List<ArquivosOsp> files = ArquivosOspDAO.getFiles(osp);
		if(files != null){
			for (ArquivosOsp arquivosOsp : files) {
				ArquivosOspDAO.remove(arquivosOsp);
			}
		}
		
		List<AlteracoesOsp> alteracoesOsp = AlteracoesOspDAO.getAltercoesOsp(osp.getId());
		
		if(alteracoesOsp != null){
			for (AlteracoesOsp alteracoesOsp2 : alteracoesOsp) {
				AlteracoesOspDAO.remove(alteracoesOsp2);			
			}
		}
	}

	public static Osp save(Osp osp){
		
		try {
			EntityManager em = ConnUtil.getEntity();
			em.getTransaction().begin();
			
			if(osp.getId() != null){
				em.merge(osp);
			}else{
				em.persist(osp);
			}
			
			em.getTransaction().commit();
			
			return osp;	
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	public static Osp find(Integer id){
		EntityManager em = ConnUtil.getEntity();
		return em.find(Osp.class, id);
	}
}


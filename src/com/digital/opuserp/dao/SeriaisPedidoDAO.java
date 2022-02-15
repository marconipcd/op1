package com.digital.opuserp.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.AlteracoesSerial;
import com.digital.opuserp.domain.EcfPreVendaDetalhe;
import com.digital.opuserp.domain.Produto;
import com.digital.opuserp.domain.SeriaisCompra;
import com.digital.opuserp.domain.SeriaisPedido;
import com.digital.opuserp.domain.SerialProduto;
import com.digital.opuserp.util.ConnUtil;

public class SeriaisPedidoDAO {

	public static void save(EcfPreVendaDetalhe itemPedido, List<SerialProduto> seriais){
		
		try{
			EntityManager em = ConnUtil.getEntity();
			
			em.getTransaction().begin();
			for (SerialProduto serialProduto : seriais) {
				em.persist(new SeriaisPedido(null, serialProduto, itemPedido));
			}
			em.getTransaction().commit();
		
		}catch(Exception e){			
			e.printStackTrace();
		}
	}
	
	public static List<SeriaisPedido> getSeriais(EcfPreVendaDetalhe itemPedido){
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createQuery("select sp from SeriaisPedido sp where sp.itemPedido = :item", SeriaisPedido.class);
		q.setParameter("item", itemPedido);
		return q.getResultList();
	}
	
	public static boolean removeByItem(EcfPreVendaDetalhe itemPedido){
		try{
			EntityManager em = ConnUtil.getEntity();
			Query q = em.createQuery("select sp from SeriaisPedido sp where sp.itemPedido = :item", SeriaisPedido.class);
			q.setParameter("item", itemPedido);
			
			
			if(q.getResultList().size() > 0){	
				
				for (SeriaisPedido serialPedido : (List<SeriaisPedido>)q.getResultList()) {
					
					//Volta Serial
					SerialProduto s = serialPedido.getSerial();
					
					//boolean comodato = ContratosAcessoDAO.getContratoPorMac(s.getSerial());
					//if(comodato){
					//	s.setStatus("COMODATO");
					//}else{
						s.setStatus("ATIVO");						
					//}
					
					SerialProdutoDAO.saveSemTransaction(s);			
					em.persist(new AlteracoesSerial(null, "PRE-VENDA ESTORNO "+itemPedido.getEcfPreVendaCabecalhoId(), s,OpusERP4UI.getEmpresa(), OpusERP4UI.getUsuarioLogadoUI(), new Date()));

					//Remove o Vinculo com Item
					em.remove(serialPedido);				
				}
			}

			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public static SeriaisPedido find(Integer cod){
		
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createQuery("select s from SeriaisPedido s where s.serial = :cod", SeriaisPedido.class);
		q.setParameter("cod", new SerialProduto(cod));
		
		if(q.getResultList().size() == 1){
			return (SeriaisPedido) q.getSingleResult();
		}else{
			return null;
		}
		
	}
	
}

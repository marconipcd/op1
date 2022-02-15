package com.digital.opuserp.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.domain.EcfPreVendaDetalhe;
import com.digital.opuserp.domain.MovimentoEntDetalhe;
import com.digital.opuserp.domain.Produto;
import com.digital.opuserp.domain.SeriaisCompra;
import com.digital.opuserp.domain.SeriaisPedido;
import com.digital.opuserp.domain.SerialProduto;
import com.digital.opuserp.util.ConnUtil;

public class SeriaisCompraDAO {

	public static void save(MovimentoEntDetalhe itemPedido, SerialProduto serial){
		
		try{
			EntityManager em = ConnUtil.getEntity();
			
			em.getTransaction().begin();			
			em.persist(new SeriaisCompra(null, serial, itemPedido));			
			em.getTransaction().commit();
		
		}catch(Exception e){			
			e.printStackTrace();
		}
	}
	
	public static boolean removeByItem(MovimentoEntDetalhe item){
		try{
			EntityManager em = ConnUtil.getEntity();
			Query q = em.createQuery("select sp from SeriaisCompra sp where sp.itemPedido = :item", SeriaisCompra.class);
			q.setParameter("item", item);
			
			if(q.getResultList().size() > 0){

				for (SeriaisCompra serialCompra : (List<SeriaisCompra>)q.getResultList()) {
					
					//Remove o Vinculo com Item
					em.remove(serialCompra);
					
					//Volta Serial
					SerialProduto s = serialCompra.getSerial();
					em.remove(s);

				}
			}

			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	
	public static SeriaisCompra find(Integer cod){
		
			EntityManager em = ConnUtil.getEntity();
			Query q = em.createQuery("select s from SeriaisCompra s where s.serial = :cod", SeriaisCompra.class);
			q.setParameter("cod", new SerialProduto(cod));
			
			if(q.getResultList().size() == 1){
				return (SeriaisCompra) q.getSingleResult();
			}else{
				return null;
			}
			
		}
	
	
}

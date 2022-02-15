package com.digital.opuserp.view.modulos.financeiro.haver;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.Haver;
import com.digital.opuserp.domain.HaverEmpresas;
import com.vaadin.addon.jpacontainer.QueryModifierDelegate;

public class QueryModifierCustom implements QueryModifierDelegate {

	@Override
	public void filtersWereAdded(CriteriaBuilder builder, CriteriaQuery<?> criteriaQuery) {
		
		
	}

	@Override
	public void filtersWillBeAdded(CriteriaBuilder builder, CriteriaQuery<?> criteriaQuery,
			List<Predicate> arg2) {
		   
	}

	@Override
	public void orderByWasAdded(CriteriaBuilder arg0, CriteriaQuery<?> arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void orderByWillBeAdded(CriteriaBuilder arg0, CriteriaQuery<?> arg1,
			List<Order> arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void queryHasBeenBuilt(CriteriaBuilder builder, CriteriaQuery<?> criteriaQuery) {
		 
	}

	@Override
	public void queryWillBeBuilt(CriteriaBuilder builder, CriteriaQuery<?> criteriaQuery) {
		
//		Root<Haver> haver = criteriaQuery.from(Haver.class);
//        Root<HaverEmpresas> haverEmpresa = criteriaQuery.from(HaverEmpresas.class);
//      
////        criteriaQuery.where(builder.equal(haver, haverEmpresa.get("haver")));
//        criteriaQuery.where(builder.equal(haverEmpresa.get("empresa"), OpusERP4UI.getEmpresa()));
	}

}

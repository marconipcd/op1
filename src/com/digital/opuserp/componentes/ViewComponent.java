package com.digital.opuserp.componentes;

import org.apache.poi.ss.formula.functions.T;

import com.digital.opuserp.dao.GerenciarModuloDAO;
import com.digital.opuserp.domain.FichaCegaCab;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class ViewComponent extends VerticalLayout {

	private JPAContainer<T> container;
	
	private Table tb;
	private TextField tfBusca;
	
	private Button btNovo;
	private Button btEditar;
	private Button btExcluir;
		
	private Label lbRegistros;
	
	private HorizontalLayout hlFloat;	
	
	private ShortcutListener slNovo;
	private ShortcutListener slEditar;
	
	private Integer codSubModulo;
	private GerenciarModuloDAO gmDAO;
	
	private ComboBox cbStatus;
}

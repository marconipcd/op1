package com.digital.opuserp.dao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.AcessoCliente;
import com.digital.opuserp.domain.AlteracoesEstoqueMovel;
import com.digital.opuserp.domain.AlteracoesOse;
import com.digital.opuserp.domain.ChecklistOsEquipamento;
import com.digital.opuserp.domain.ChecklistOsInstalacao;
import com.digital.opuserp.domain.ChecklistOsLocalizacao;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.EcfPreVendaCabecalho;
import com.digital.opuserp.domain.EcfPreVendaDetalhe;
import com.digital.opuserp.domain.EstoqueMovel;
import com.digital.opuserp.domain.GrupoOse;
import com.digital.opuserp.domain.LogProduto;
import com.digital.opuserp.domain.MateriaisAlocados;
import com.digital.opuserp.domain.MateriaisAlocadosDetalhe;
import com.digital.opuserp.domain.NaturezaOperacao;
import com.digital.opuserp.domain.Ose;
import com.digital.opuserp.domain.OseProduto;
import com.digital.opuserp.domain.Produto;
import com.digital.opuserp.domain.SubGrupoOse;
import com.digital.opuserp.domain.Usuario;
import com.digital.opuserp.domain.Veiculos;
import com.digital.opuserp.util.ConnUtil;

public class OseDAO {
	
	public static boolean alocarMaterial(Usuario tecnico, List<Object[]> materiaisList){
		try{
			EntityManager em = ConnUtil.getEntity();
			
			em.getTransaction().begin();
			
			MateriaisAlocados materialCab = new MateriaisAlocados();
			materialCab.setTecnico(tecnico);
			materialCab.setData_alocacao(new Date());
			materialCab.setQtd(0); 
			
			
			em.persist(materialCab);
			for (Object[] objects : materiaisList) {
				
				MateriaisAlocadosDetalhe materialDet = new MateriaisAlocadosDetalhe(null, materialCab.getId(), Integer.parseInt(String.valueOf(objects[0])),Integer.parseInt(String.valueOf(objects[2])));
				em.persist(materialDet);
				//System.out.println(objects);
			}
			
			em.getTransaction().commit();
						
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean removeMaterialAlocado(Integer id){
		try{
			EntityManager em = ConnUtil.getEntity();
			
			Query q1 = em.createQuery("select m from MateriaisAlocadosDetalhe m where m.materiais_alocados_cab =:cod", MateriaisAlocadosDetalhe.class);
			q1.setParameter("cod", id);
			
			em.getTransaction().begin();
			for (MateriaisAlocadosDetalhe det : (List<MateriaisAlocadosDetalhe>)q1.getResultList()) {
				em.remove(det);
			}
			
			
			MateriaisAlocados cab = em.find(MateriaisAlocados.class, id);			
			if(cab != null){
				em.remove(cab); 
			}
			
			em.getTransaction().commit();
			
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public static List<MateriaisAlocadosDetalhe> getMateriais(Integer codMAterialCab){
		try{
			EntityManager em = ConnUtil.getEntity();
			
			Query q = em.createQuery("select m from MateriaisAlocadosDetalhe m where m.materiais_alocados_cab =:cod", MateriaisAlocadosDetalhe.class);
			q.setParameter("cod", codMAterialCab);
						
			return q.getResultList();
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public static boolean excluirAlocarMaterial(MateriaisAlocados material){
		try{
			
			EntityManager em = ConnUtil.getEntity();
			
			em.getTransaction().begin();
			em.remove(material);
			em.getTransaction().commit();
			
			
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public static Integer getOsePorClienteSubGrupo(Cliente cliente, SubGrupoOse subgrupo, Date data){
		
		try{
		
			SimpleDateFormat sdf = new SimpleDateFormat("M");
			
			EntityManager em = ConnUtil.getEntity();
			Query q = em.createNativeQuery("select * from ose where CLIENTES_ID =:cliente and SUBGRUPO_ID =:subgrupo and month(DATA_EX) =:mes", Ose.class);
			q.setParameter("mes",sdf.format(data));
			q.setParameter("cliente", cliente.getId());
			q.setParameter("subgrupo", subgrupo.getId());
			
			return q.getResultList().size();
			
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public static boolean atribuirTecnico(Usuario u, Usuario uAux, Integer ose, Veiculos veiculo, String equipe, Date dataAtribuicao){
		
		try{
		
			EntityManager em = ConnUtil.getEntity();
			
			Ose o = em.find(Ose.class, ose);
			o.setTecnico(u.getUsername());
			o.setAuxiliar(uAux.getUsername());
			o.setVeiculo_id(veiculo);
			o.setEquipe(equipe);
			o.setData_atribuicao(dataAtribuicao);
			o.setStatus("ABERTO");
			
			AlteracoesOse ocorrencia =  new AlteracoesOse(null, "ATRIBUIU: "+equipe+" - "+u.getUsername()+" - "+" - "+uAux.getUsername() +" - "+veiculo.getCod_veiculo()+"-"+veiculo.getModelo(), new Ose(ose), OpusERP4UI.getUsuarioLogadoUI(), new Date());
						
			em.getTransaction().begin();
			em.merge(o);
			em.persist(ocorrencia); 
			em.getTransaction().commit();
			
			return true;
		
		}catch(Exception e){
			return false;
		}
		
		
		
	}
	
	public static boolean atribuirTecnico(Usuario u, Integer ose, Veiculos veiculo, String equipe, Date dataAtribuicao){
		
		try{
		
			EntityManager em = ConnUtil.getEntity();
			
			Ose o = em.find(Ose.class, ose);
			o.setTecnico(u.getUsername());
			o.setVeiculo_id(veiculo);
			o.setEquipe(equipe);
			o.setData_atribuicao(dataAtribuicao);
			o.setStatus("ABERTO");
			
			AlteracoesOse ocorrencia =  new AlteracoesOse(null, "ATRIBUIU: "+equipe+" - "+u.getUsername()+" - "+veiculo.getCod_veiculo()+"-"+veiculo.getModelo(), new Ose(ose), OpusERP4UI.getUsuarioLogadoUI(), new Date());
						
			em.getTransaction().begin();
			em.merge(o);
			em.persist(ocorrencia); 
			em.getTransaction().commit();
			
			return true;
		
		}catch(Exception e){
			return false;
		}
		
		
		
	}
	
	
	public static boolean encaminharOseValid(Usuario u,Usuario uAux, Veiculos v){
		
		EntityManager em = ConnUtil.getEntity();

		Query q = em.createQuery("select o from Ose o where o.tecnico =:tecnico and o.auxiliar =:aux and o.status = 'EM ANDAMENTO'", Ose.class);
		q.setParameter("tecnico", u.getUsername());
		q.setParameter("aux", uAux.getUsername());
				
		Query q2 = em.createQuery("select o from Ose o where o.veiculo_id =:veiculo and o.status = 'EM ANDAMENTO'", Ose.class);		
		q2.setParameter("veiculo", v);
				
		boolean valid = true;
		if(q.getResultList().size() > 0){
			valid = false;
		}
		
		if(q2.getResultList().size() > 0){
			valid = false;
		}
		
		
		
		return valid;
	}
	
	public static boolean encaminharOseValid(Usuario u, Veiculos v){
		
		EntityManager em = ConnUtil.getEntity();

		
		
		Query q = em.createQuery("select o from Ose o where o.tecnico =:tecnico and o.status = 'EM ANDAMENTO'", Ose.class);
		q.setParameter("tecnico", u.getUsername());
		
		
		Query q2 = em.createQuery("select o from Ose o where o.veiculo_id =:veiculo and o.status = 'EM ANDAMENTO'", Ose.class);		
		q2.setParameter("veiculo", v);
		
		
		boolean valid = true;
		if(q.getResultList().size() > 0){
			valid = false;
		}
		
		if(q2.getResultList().size() > 0){
			valid = false;
		}
		
		
		
		return valid;
	}
	
	
	public static void estornarProdutosOse(Ose ose){
		
		try{
		
			EntityManager em = ConnUtil.getEntity();
			
			Query q = em.createQuery("select op from OseProduto op where op.ose =:ose", OseProduto.class);
			q.setParameter("ose", ose);
			
			em.getTransaction().begin();
				
				for (OseProduto oProduto : (List<OseProduto>)q.getResultList()) {
					
					EstoqueMovel p = oProduto.getProduto();
										
					p.setQtd(new Float(p.getQtd()+oProduto.getQtd()));			
					em.merge(p);
					
					em.remove(oProduto);
					
					AlteracoesEstoqueMovelDAO.save(new AlteracoesEstoqueMovel(null, p, OpusERP4UI.getUsuarioLogadoUI(), "DESVINCULADO DE OS("+oProduto.getOse().getId()+") EXCLUÍDA", new Date(), new Float(oProduto.getQtd())));
				}
				
				
				
			
			em.getTransaction().commit();
		
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public static void vincularProduto(Ose ose, List<OseProduto> itens){
		EntityManager em = ConnUtil.getEntity();
		
		Cliente cliente = ose.getCliente();
		em.getTransaction().begin();
		

		
		for (OseProduto oseProduto : itens) {
			
			EstoqueMovel p = em.find(EstoqueMovel.class, oseProduto.getProduto().getId());						
			p.setQtd(p.getQtd() - new Float(oseProduto.getQtd()));
			
			em.merge(p);
			
			oseProduto.setProduto(p);
			em.persist(oseProduto); 
			
			AlteracoesEstoqueMovelDAO.save(new AlteracoesEstoqueMovel(null, p, OpusERP4UI.getUsuarioLogadoUI(), "VINCULOU A OS: "+oseProduto.getOse().getId(), new Date(),new Float(oseProduto.getQtd())));
		}
		
		em.getTransaction().commit();
	}

	public static void save(Ose ose){
		EntityManager em = ConnUtil.getEntity();
		em.getTransaction().begin();
		
		if(ose.getId() != null){
			em.merge(ose);
		}else{
			em.persist(ose);
		}
		
		em.getTransaction().commit();
	}
	
	public static void remove(Ose ose){
		EntityManager em = ConnUtil.getEntity();
		
		em.getTransaction().begin();
		em.remove(ose);
		em.getTransaction().commit();
	}
	
	public static Ose findByContrato(AcessoCliente contrato){
		EntityManager em = ConnUtil.getEntity();
		
		Query q = em.createQuery("select o from Ose o where o.contrato=:c and o.grupo=:g", Ose.class);
		q.setParameter("c", contrato);
		q.setParameter("g", new GrupoOse(2));
		
		return (Ose)q.getSingleResult();
	}
	
	public static Ose find(Integer id){
		EntityManager em = ConnUtil.getEntity();
		return em.find(Ose.class, id);
	}
	
	public static List<ChecklistOsEquipamento> getAllCheckListEquipamento(){
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createQuery("select o from ChecklistOsEquipamento o where o.empresa_id=:empresa", ChecklistOsEquipamento.class);
		q.setParameter("empresa", OpusERP4UI.getEmpresa().getId());
		
		return q.getResultList();
	}
	
	public static List<ChecklistOsLocalizacao> getAllCheckListLocalizacao(){
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createQuery("select o from ChecklistOsLocalizacao o where o.empresa_id=:empresa", ChecklistOsLocalizacao.class);
		q.setParameter("empresa", OpusERP4UI.getEmpresa().getId());
		
		return q.getResultList();
	}
	
	public static List<ChecklistOsInstalacao> getAllCheckListInstalacaoRadio(){
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createQuery("select o from ChecklistOsInstalacao o where o.empresa_id=:empresa and o.tipo = 'RADIO'", ChecklistOsInstalacao.class);
		q.setParameter("empresa", OpusERP4UI.getEmpresa().getId());
		
		return q.getResultList();
	}
	public static List<ChecklistOsInstalacao> getAllCheckListInstalacaoCabo(){
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createQuery("select o from ChecklistOsInstalacao o where o.empresa_id=:empresa and o.tipo = 'CABO'", ChecklistOsInstalacao.class);
		q.setParameter("empresa", OpusERP4UI.getEmpresa().getId());
		
		return q.getResultList();
	}
	public static List<ChecklistOsInstalacao> getAllCheckListInstalacaoFibra(){
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createQuery("select o from ChecklistOsInstalacao o where o.empresa_id=:empresa and o.tipo = 'FIBRA'", ChecklistOsInstalacao.class);
		q.setParameter("empresa", OpusERP4UI.getEmpresa().getId());
		
		return q.getResultList();
	}
	
	public static List<Ose> getOsePendentes(){
		
		try{
			EntityManager em = ConnUtil.getEntity();
			Query q = em.createQuery("select o from Ose o where o.empresa_id=:empresa and o.data_ex <=:data and o.status='ABERTO'", Ose.class);
			q.setParameter("empresa", OpusERP4UI.getEmpresa().getId());
			
			SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MMM-dd");
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			
			String data = sdf1.format(new Date()).toString();
			String hora = sdf.format(sdf.parse("19:00:00")).toString();
			
			q.setParameter("data", sdf0.parse(data+" "+hora));

			return q.getResultList();
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		
	}
	
	public static boolean allowQtdToday(GrupoOse grupo, Date data){
		try{
			EntityManager em = ConnUtil.getEntity();
			Query q = em.createQuery("select o from Ose o where o.grupo=:grupo and  DATE_FORMAT(o.data_ex,  '%Y-%m-%d')=:data", Ose.class);
			q.setParameter("grupo", grupo);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			q.setParameter("data", sdf.format(data));
			
			if(q.getResultList().size() < grupo.getLimite_diario()){
				return true;
			}else{
				return false;
			}
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
}

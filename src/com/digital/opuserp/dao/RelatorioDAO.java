package com.digital.opuserp.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.RelatorioPre;
import com.digital.opuserp.domain.RelatorioPreColunas;
import com.digital.opuserp.domain.RelatorioPreFiltro;
import com.digital.opuserp.util.ConnUtil;

public class RelatorioDAO {

	EntityManager em;
	
	public RelatorioDAO(){
		em = ConnUtil.getEntity();
	}
	
	
	
	public void addRelatorio(RelatorioPre relatorio, List<RelatorioPreFiltro> filtros, List<RelatorioPreColunas> colunas){
		
		try{						
			if(relatorio.getId() == null){
				em.getTransaction().begin();
				em.persist(relatorio);
				
				for (RelatorioPreFiltro relatorioPreFiltro : filtros) {
					relatorioPreFiltro.setRelatorioPre(relatorio);
					em.persist(relatorioPreFiltro);
				}
				
				for (RelatorioPreColunas relatorioPreColunas : colunas) {
					relatorioPreColunas.setRelatorioPre(relatorio);
					em.persist(relatorioPreColunas);				
				}
				
				em.getTransaction().commit();
			}else{
				
				//RelatorioPre relatorioUp = em.find(RelatorioPre.class, relatorio.getId());
														
				//Apaga Filtros e Colunas
				apagarFiltros(relatorio);
				apagarColunas(relatorio);	
				
				em.getTransaction().begin();
				em.merge(relatorio);						
					
					//Cadastra Filtros e Colunas Novamente
					for (RelatorioPreFiltro relatorioPreFiltro : filtros) {
						relatorioPreFiltro.setRelatorioPre(relatorio);
						em.persist(relatorioPreFiltro);
					}
					
					for (RelatorioPreColunas relatorioPreColunas : colunas) {
						relatorioPreColunas.setRelatorioPre(relatorio);
						em.persist(relatorioPreColunas);				
					}
										
				
				em.getTransaction().commit();

			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
	}
	
	public boolean apagarRelatorio(Integer codRelatorio){
		try{
			RelatorioPre relatorioUp = find(codRelatorio);
			if(relatorioUp != null){
				
				apagarFiltros(relatorioUp);
				apagarColunas(relatorioUp);
				
				em.getTransaction().begin();
				em.remove(relatorioUp);
				em.getTransaction().commit();
				
				return true;
			}
			
			return false;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	private void apagarColunas(RelatorioPre relatorioUp){
		
		try{
			List<RelatorioPreColunas> relatoriosColunas = getColunas(relatorioUp);
			if(relatoriosColunas != null){
				em.getTransaction().begin();
				
				for(RelatorioPreColunas colunasInLine:relatoriosColunas){
					em.remove(colunasInLine);
				}
				
				em.getTransaction().commit();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void apagarFiltros(RelatorioPre relatorioUp){
		
		try{
			List<RelatorioPreFiltro> relatoriosFiltros = getFiltros(relatorioUp);
			if(relatoriosFiltros != null){
				em.getTransaction().begin();
				
				for(RelatorioPreFiltro filtroInLine:relatoriosFiltros){
					em.remove(filtroInLine);
				}
				
				em.getTransaction().commit();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public List<RelatorioPreFiltro> getFiltros(RelatorioPre relatorio){
		
		Query q  = em.createQuery("select r from RelatorioPreFiltro r where r.relatorioPre = :codRelatorio", RelatorioPreFiltro.class);
		q.setParameter("codRelatorio", relatorio);
		
		if(q.getResultList().size() > 0){
			
			List<RelatorioPreFiltro> filtros = q.getResultList();
			return filtros;
			
		}else{
			return null;
		}		
	}
	
	public List<RelatorioPreColunas> getColunas(RelatorioPre relatorio){
		
		Query q  = em.createQuery("select r from RelatorioPreColunas r where r.relatorioPre = :codRelatorio", RelatorioPreColunas.class);
		q.setParameter("codRelatorio", relatorio);
		
		if(q.getResultList().size() > 0){
			
			List<RelatorioPreColunas> filtros = q.getResultList();
			return filtros;
			
		}else{
			return null;
		}		
	}
	
	
	public RelatorioPre findByName(String nome){
		Query q = em.createQuery("select r from RelatorioPre r where r.nome_relatorio = :nomePlano", RelatorioPre.class);
		q.setParameter("nomePlano", nome);
		
		if(q.getResultList().size() == 0){
			return (RelatorioPre) q.getSingleResult();
		}else{
			return null;
		}
	}
	
	public boolean validarNome(String nome, Integer codSubModulo){
		Query q = em.createQuery("select r from RelatorioPre r where r.codSubModulo =:codSubModulo and r.nome_relatorio = :nomePlano and r.codUsuario =:codUser and r.codEmpresa =:codEmpresa", RelatorioPre.class);
		q.setParameter("nomePlano", nome);
		q.setParameter("codUser",OpusERP4UI.getUsuarioLogadoUI());
		q.setParameter("codEmpresa",OpusERP4UI.getEmpresa().getId());
		q.setParameter("codSubModulo",codSubModulo);
		
		if(q.getResultList().size()>0){
			return false;
		}else{
			return true;
		}
	}
	
	public RelatorioPre find(Integer codRelatorio){
		RelatorioPre relatorio = em.find(RelatorioPre.class, codRelatorio);
		return relatorio;
	}
}

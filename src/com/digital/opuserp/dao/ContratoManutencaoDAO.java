package com.digital.opuserp.dao;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Months;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.ContratoManutencao;
import com.digital.opuserp.domain.ContratosAcesso;
import com.digital.opuserp.domain.PlanoAcesso;
import com.digital.opuserp.domain.PlanosManutencao;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.Real;

public class ContratoManutencaoDAO {

	public static boolean cadastrar(PlanosManutencao plano_manutencao, Cliente cliente, Date data_venc_contrato, Date data_primeiro_boleto, String valor_primeiro_boleto){
		
		try{
			EntityManager em = ConnUtil.getEntity();
			
			ContratoManutencao contrato = new ContratoManutencao(null, OpusERP4UI.getEmpresa().getId(), cliente, plano_manutencao, data_venc_contrato, new Date(), new Date());
			
			em.getTransaction().begin();
			em.persist(contrato);
			
			Integer meses = Months.monthsBetween(new DateTime(data_primeiro_boleto), new DateTime(data_venc_contrato)).getMonths();
						
			ContasReceberDAO.gerarBoletosManutencao(contrato.getId(),cliente.getId(), Real.formatDbToString(String.valueOf(plano_manutencao.getValor_mensal())), data_primeiro_boleto, meses, "MANUTENCAO",valor_primeiro_boleto);
			
			em.getTransaction().commit();
						
			return true;
		}catch(Exception e){
			e.printStackTrace();
			
			return false;
		}
		
	}
	
	public static boolean remove(ContratoManutencao contrato){
		
		try{
			EntityManager em = ConnUtil.getEntity();
			
			em.getTransaction().begin();
			em.remove(contrato);
			em.getTransaction().commit();
			
			return true;
			
		}catch(Exception e){
			e.printStackTrace();
			
			return false;
		}
		
	}
	
	
	
	
	public static ContratoManutencao getContratoPorCliente(Cliente c){
		EntityManager em = ConnUtil.getEntity();
		
		Query q = em.createQuery("select cm from ContratoManutencao cm where cm.cliente =:cliente and cm.empresa_id=:empresa", ContratoManutencao.class);
		q.setParameter("cliente", c);
		q.setParameter("empresa", OpusERP4UI.getEmpresa().getId());
		
		if(q.getResultList().size() == 1){
			return (ContratoManutencao)q.getSingleResult();
		}
		
		return null;
	}
	
	public static String calcularValorPrimeiroBoleto(PlanosManutencao plano, Date dataPrimeiroBoleto)
	{
		try{							
				String valorBoleto = null;
												
				Date dataProxVencBoleto = dataPrimeiroBoleto;
				DateTime dataAnterior = new DateTime(dataProxVencBoleto).minusMonths(1);
				
				Integer qtdDias = Days.daysBetween(dataAnterior, new DateTime(dataProxVencBoleto)).getDays(); 
				Integer qtdDiasUtilizados = Days.daysBetween(new DateTime(new Date()), new DateTime(dataProxVencBoleto)).getDays();
									
				double vlrDoDiaPlano = plano.getValor_mensal();
				double vlrDosDiasTotal = vlrDoDiaPlano * qtdDiasUtilizados / qtdDias;
				
				valorBoleto = String.valueOf(vlrDosDiasTotal);
				
				return valorBoleto;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}			
	}
}

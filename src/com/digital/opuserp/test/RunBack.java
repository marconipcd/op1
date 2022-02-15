package com.digital.opuserp.test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import com.digital.opuserp.dao.CfopDAO;
import com.digital.opuserp.domain.AcessoCliente;
import com.digital.opuserp.domain.Cfop;
import com.digital.opuserp.domain.ContasReceber;
import com.digital.opuserp.domain.NfeMestre;
import com.digital.opuserp.util.Real;
import com.digital.opuserp.util.StringUtil;

public class RunBack implements Runnable {

	@Override
	public void run() {
		
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("OpusERP4");
		EntityManager em = emf.createEntityManager();
		
		Query qcontratos = em.createQuery("select c from AcessoCliente c where c.emitir_nfe_automatico = 'SIM' and c.status_2 != 'CANCELADO' and c.status_2 != 'PENDENTE_INSTALACAO'", AcessoCliente.class);
		List<AcessoCliente> lista_de_contratos = qcontratos.getResultList();
		
		
		em.getTransaction().begin();
		for (AcessoCliente contrato : lista_de_contratos) {

			SimpleDateFormat sdfAnoMes = new SimpleDateFormat("yyMM");
			ContasReceber boleto = null;
			
			String regexNova = "^"+contrato.getId().toString()+"/[0-9]{2}-[0-9]{2}/[0-9]{2}";
			String regexAntiga = "^"+contrato.getId().toString()+"/[0-9]{2}/[0-9]{2}";
			
			Query qn = em.createNativeQuery("select * from contas_receber cr where "+				
					"cr.status_2 != 'EXCLUIDO' "+			
					"and cr.n_doc REGEXP :rNova "+ 
					"and DATE_FORMAT(cr.data_vencimento, '%y%m') =:anoMes "+
					
					"or "+
					"cr.status_2 != 'EXCLUIDO' "+				
					"and cr.n_doc REGEXP :rAntiga "+
					"and DATE_FORMAT(cr.data_vencimento, '%y%m') =:anoMes"
					
					, ContasReceber.class);
				
			qn.setParameter("rNova", regexNova);
			qn.setParameter("rAntiga", regexAntiga);
			qn.setParameter("anoMes", sdfAnoMes.format(new Date()));
			
			if(qn.getResultList().size()  == 1){
				boleto = (ContasReceber)qn.getSingleResult();
			}
			
			if(boleto != null && boleto.getStatus().equals("ABERTO")){
				if(contrato.getEmitir_nfe_c_boleto_aberto().equals("SIM")){
					boolean gerado = false;
					
					Query q = em.createQuery("select nfe From NfeMestre nfe where nfe.contrato =:contrato and nfe.contas_receber=:boleto", NfeMestre.class);
					q.setParameter("contrato", contrato);
					q.setParameter("boleto", boleto);
					
					if(q.getResultList().size() > 0){
						gerado=  true;
					}
					
					if(!gerado){
						
						Cfop cfop = new Cfop();
						if(contrato != null && contrato.getCfop_nfe() != null){
							cfop = em.find(Cfop.class, contrato.getCfop_nfe());				
						}
											
						Double valor = Real.formatStringToDBDouble(boleto.getValor_titulo());
						String cod_iden  = StringUtil.md5(contrato.getCliente().getDoc_cpf_cnpj()+"21"+String.valueOf(valor)+"0,00"+"0,00");
						
						double vlr_desconto;
						double vlr_boleto = Real.formatStringToDBDouble(boleto.getValor_titulo());
						double vlr_plano  = Real.formatStringToDBDouble(contrato.getPlano().getValor());
						
						if(vlr_boleto != vlr_plano){
							vlr_desconto = new Double("0.00");
						}else{
							vlr_desconto = Real.formatStringToDBDouble(contrato.getPlano().getDesconto());
						}
						
						NfeMestre nfeM = new NfeMestre( null,contrato.getCliente(),contrato ,boleto,cfop,new Date(),boleto.getData_vencimento(),	contrato.getContrato().getClasse_consumo(),"4","00",
								contrato.getCliente().getId().toString(),"21","000", cod_iden	, Real.formatStringToDBDouble(boleto.getValor_titulo()), 
								Real.formatStringToDBDouble(boleto.getValor_titulo())-Real.formatStringToDBDouble(contrato.getPlano().getDesconto()),	0,0,	0,	
								vlr_desconto,"N",	sdfAnoMes.format(boleto.getData_vencimento()),"1",contrato.getCliente().getTelefone1(),
								StringUtil.md5(contrato.getCliente().getDoc_cpf_cnpj()+contrato.getCliente().getDoc_rg_insc_estadual()+
										contrato.getCliente().getNome_razao()+"PE"+contrato.getContrato().getClasse_consumo()+"4"+"00"+
										contrato.getCliente().getId().toString()+new Date()+"21"+"000")+cod_iden+valor+0+0+0+0+"N"+sdfAnoMes.format(boleto.getData_vencimento())+"1", false);
						
						
						em.persist(nfeM); 
					}
				}
			}else{
				boolean gerado = false;
				
				Query q = em.createQuery("select nfe From NfeMestre nfe where nfe.contrato =:contrato and nfe.contas_receber=:boleto", NfeMestre.class);
				q.setParameter("contrato", contrato);
				q.setParameter("boleto", boleto);
				
				if(q.getResultList().size() > 0){
					gerado=  true;
				}
				
				if(!gerado){
					
					Cfop cfop = new Cfop();
					if(contrato != null && contrato.getCfop_nfe() != null){								
						cfop = em.find(Cfop.class, contrato.getCfop_nfe());
					}
					
					if(boleto != null && boleto.getValor_titulo() != null){
						Double valor = Real.formatStringToDBDouble(boleto.getValor_titulo());
						String cod_iden  = StringUtil.md5(contrato.getCliente().getDoc_cpf_cnpj()+"21"+String.valueOf(valor)+"0,00"+"0,00");
						
						NfeMestre nfeM = new NfeMestre( null,contrato.getCliente(),contrato ,boleto,cfop,new Date(),boleto.getData_vencimento(),	contrato.getContrato().getClasse_consumo(),"4","00",
								contrato.getCliente().getId().toString(),"21","000", cod_iden	, Real.formatStringToDBDouble(boleto.getValor_titulo()), 
								Real.formatStringToDBDouble(boleto.getValor_titulo())-Real.formatStringToDBDouble(contrato.getPlano().getDesconto()),	0,0,	0,	
								Real.formatStringToDBDouble(contrato.getPlano().getDesconto()),"N",	sdfAnoMes.format(boleto.getData_vencimento()),"1",contrato.getCliente().getTelefone1(),
								StringUtil.md5(contrato.getCliente().getDoc_cpf_cnpj()+contrato.getCliente().getDoc_rg_insc_estadual()+
								contrato.getCliente().getNome_razao()+"PE"+contrato.getContrato().getClasse_consumo()+"4"+"00"+
								contrato.getCliente().getId().toString()+new Date()+"21"+"000")+cod_iden+valor+0+0+0+0+"N"+sdfAnoMes.format(boleto.getData_vencimento())+"1", false);
						
						
						em.persist(nfeM); 
					}
				}
			}
		}
		System.out.println("Rotina de notas fiscais concluído!");
		em.getTransaction().commit();
		
		
	}

}

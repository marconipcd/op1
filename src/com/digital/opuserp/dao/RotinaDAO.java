package com.digital.opuserp.dao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.joda.time.DateTime;
import org.joda.time.Days;

import com.digital.opuserp.domain.AcessoCliente;
import com.digital.opuserp.domain.Cfop;
import com.digital.opuserp.domain.ContasReceber;
import com.digital.opuserp.domain.NfeMestre;
import com.digital.opuserp.domain.RadReply;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.Real;
import com.digital.opuserp.util.StringUtil;

public class RotinaDAO {

//	public static void RotinaAniversariantes(){
//		
//		List<Cliente> c = ClienteDAO.getAniversariantes();
//
//		CrmFormasContato formaContato = CrmDAO.getFormaDefault();
//		CrmAssunto assunto = CrmDAO.getAssuntofault();
//		Setores setor = CrmDAO.getSetorDefault();
//		
//		if(formaContato != null && assunto != null && setor != null && c.size() > 0){
//			for (Cliente cliente : c) {
//				
//				if(!CrmDAO.checkExists(setor, cliente,assunto,formaContato,"ROTINA")){
//					CrmDAO.saveCrm(new Crm(null, OpusERP4UI.getEmpresa().getId(), setor, cliente, assunto, formaContato, cliente.getContato(), "ROTINA", assunto.getConteudo(), new Date(), new Date(), new Date(), null, "AGENDADO", "OpusERP4"));
//					System.out.println("Crm Gerado para o Cliente: "+cliente.getNome_razao());
//					System.out.println("================================================");
//				}
//			}
//		}else{
//			if(formaContato == null){
//				Notify.Show("Rotina de Aniversariantes não Pode Continuar: Cadastre uma Forma de Contato com o Nome TELEFONE " ,  Notify.TYPE_ERROR);
//			}
//			
//			if(assunto == null){
//				Notify.Show("Rotina de Aniversariantes não Pode Continuar: Cadastre um Assunto com o Nome FELIZ ANIVERSARIO " ,  Notify.TYPE_ERROR);
//			}
//			
//			if(setor == null){
//				Notify.Show("Rotina de Aniversariantes não Pode Continuar: Cadastre um Setor com o Nome RELACIONAMENTO " ,  Notify.TYPE_ERROR);
//			}
//		}
//	}
		
	/**
	 * VERIFICA SE A EMPRESA ATUAL TEM MÓDULO FINANCEIRO
	 * VERIRICA SE A EMPRESA ATUAL TEM MÓDULO DE ACESSO
	 * PROCURA BOLETOS DE ACESSO ATRASADOS PARA MARCAR PARA BLOQUEAR
	 * PROCURA BOLETOS DE ACESSO MARCADOS PARA BLOQUEAR E BLOQUEIA O ACESSO E BOLETO
	 */
	public static void BloqueioAcessoClientes(){ 
		
		EntityManager em = ConnUtil.getEntity();
		try{

	
//		EntityManager em = ConnUtil.getEntityManager();
		
		if(ModuloEmpresaDAO.checkModuloEmpresa(Modulo.COM.FINANCEIRO) && ModuloEmpresaDAO.checkModuloEmpresa(Modulo.COM.ACESSO)){
			
			
			List<ContasReceber> boletosAtrasados = ContasReceberDAO.getBoletosAtrasados();
			
			//Verifica Boletos Atrasados e Marca para Bloquear
			em.getTransaction().begin();
			for (ContasReceber contasReceber : boletosAtrasados) {
				
				String numeroDoc = contasReceber.getN_doc();
				Integer codAcesso = Integer.parseInt(numeroDoc.split("/")[0].toString());				
				AcessoCliente acesso = em.find(AcessoCliente.class,codAcesso);
				
				
				if(acesso != null && acesso.getContrato().getTipo_contrato().equals("POS-PAGO")){
					
					Integer qtdDiasAtrasados = Days.daysBetween(new DateTime(contasReceber.getData_vencimento()), new DateTime()).getDays();
					
					if(qtdDiasAtrasados >= acesso.getContrato().getBloqueio()){				
						
							contasReceber.setBloquear("S");
							em.merge(contasReceber);
						
					}
				}
			}	
			
			em.getTransaction().commit();
			
			
			
			List<ContasReceber> boletosParaBloquear = ContasReceberDAO.getBoletosParaBloquear();
			for (ContasReceber contasReceber : boletosParaBloquear) {
				
				String numeroDoc = contasReceber.getN_doc();
				Integer codAcesso = Integer.parseInt(numeroDoc.split("/")[0].toString());				
				AcessoCliente acesso = em.find(AcessoCliente.class,codAcesso);
				
				if(acesso != null){
					
					if(acesso.getStatus_2().equals("ATIVO")){
						CredenciaisAcessoDAO.bloquearContratoCliente(acesso.getId());
						
						em.getTransaction().begin();
						contasReceber.setBloqueado("S");
						em.merge(contasReceber);
						em.getTransaction().commit();
						
						System.out.println(acesso.getId()+" : BLOQUEADO");
					}else if(acesso.getStatus_2().equals("BLOQUEADO")){						
						
						em.getTransaction().begin();
						contasReceber.setBloqueado("S");
						em.merge(contasReceber);
						em.getTransaction().commit();
						
						System.out.println(acesso.getId()+" : BLOQUEADO");
						
					}		
					 
				}
			}
			
			
			//EXECUTA BLOQUEIO POR VENCIMENTO DO CONTRATO DE ACESSO
			List<AcessoCliente> contratosVencidos  = CredenciaisAcessoDAO.ProcurarContratosVencidos();
			if(contratosVencidos.size() >0){
				for (AcessoCliente acessoCliente : contratosVencidos) {
					CredenciaisAcessoDAO.bloquearContratoCliente(acessoCliente.getId());
					
					System.out.println("CONTRATO "+acessoCliente.getId()+" BLOQUEADO POR VENCIMENTO DE CONTRATO");
				}
			}
			
			//EXECUTA BLOQUEIO TOTAL DO CONTRATO
			List<RadReply> loginsBloqueados = ContratosAcessoDAO.getLoginsBloqueado();
			for (RadReply radReply : loginsBloqueados) {
				try{
								
					List<ContasReceber> boletosBloqueados = ContasReceberDAO.getBoletosBloqueado(Integer.parseInt(radReply.getUsername()));
					for (ContasReceber contasReceber2 : boletosBloqueados) {
						
						Integer qtdDiasAtrasados = Days.daysBetween(new DateTime(contasReceber2.getData_vencimento()), new DateTime()).getDays();
						AcessoCliente acesso = em.find(AcessoCliente.class,Integer.parseInt(radReply.getUsername()));
						
						if(qtdDiasAtrasados >= acesso.getContrato().getBloqueio_total()){
							CredenciaisAcessoDAO.bloquearContratoClienteTotal(acesso.getId());					
						}
					}
				}catch(Exception e){
					System.out.println("Contrato: "+radReply.getUsername()+ "Não encontrado!");
				}
			}
					
		}
		
		}catch(Exception e){
			e.printStackTrace();		
			em.getTransaction().rollback();
		}
			
	}
	
	
	/**
	 *  	- busca todos os contratos que estão marcados para serem emitidos automaticos que não sejam cancelado nem estejam pendente de instalacao
	 *  	- procura boleto mensal do contrato
	 *  	- verifica se boleto esta aberto e caso esteja verifica no contrato se deve gerar com boleto aberto
	 *  	- verifica se nota fiscal mensal já foi gerada
	 *  	- gera nova notafiscal 
	 */
	public  static void RotinaNfeAutomatica(){
		EntityManager em = ConnUtil.getEntity();
		
		Query qcontratos = em.createQuery("select c from AcessoCliente c where c.emitir_nfe_automatico = 'SIM' and c.status_2 != 'CANCELADO' and c.status_2 != 'PENDENTE_INSTALACAO'", AcessoCliente.class);
		List<AcessoCliente> lista_de_contratos = qcontratos.getResultList();
		
		
		em.getTransaction().begin();
		for (AcessoCliente contrato : lista_de_contratos) {
			SimpleDateFormat sdfAnoMes = new SimpleDateFormat("yyMM");
			ContasReceber boleto = ContasReceberDAO.procurarBoletoMensal(contrato.getId(), sdfAnoMes.format(new Date()));
			if(boleto != null && boleto.getStatus().equals("ABERTO")){
				if(contrato.getEmitir_nfe_c_boleto_aberto().equals("SIM")){
					boolean gerado = NfeDAO.verifica_nfe_por_boleto(contrato, boleto);
					if(!gerado){
						
						Cfop cfop = new Cfop();
						if(contrato != null && contrato.getCfop_nfe() != null){
							cfop = CfopDAO.find(contrato.getCfop_nfe());						
						}
											
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
			}else{
				boolean gerado = NfeDAO.verifica_nfe_por_boleto(contrato, boleto);
				if(!gerado){
					
					Cfop cfop = new Cfop();
					if(contrato != null && contrato.getCfop_nfe() != null){
						cfop = CfopDAO.find(contrato.getCfop_nfe());						
					}
					
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
		em.getTransaction().commit();
		
		
	}
//	private static void RefreshPendenciaContasPagar(){
//		
//		String desc = "RUNNING_CONTAS_PAGAR";
//		boolean status = StatusRotinasDAO.checkRunning(desc);
//		if(!status){
//			StatusRotinas statusR = StatusRotinasDAO.setRunning(desc);
//				
//			List<ContasPagar> contasList = ContasPagarDAO.getTitulosPendentes();
//			Integer codSubModulo = SubModuloDAO.findToId("Contas a Pagar");
//			
//			if(contasList != null){
//				for (ContasPagar conta : contasList) {
//					AlertaPendenciaDAO.addPendencia(codSubModulo, conta.getId(), "Conta a Pagar Pendente!");	
//				}
//				
//				if(statusR != null){
//					StatusRotinasDAO.setStop(statusR);
//				}
//			}
//		}		
//	}	
//	private static void RefreshPendenciaProducao(){
//		
//		String desc = "RUNNING_PRODUCAO";
//		boolean status = StatusRotinasDAO.checkRunning(desc);
//		if(!status){
//			StatusRotinas statusR = StatusRotinasDAO.setRunning(desc);
//				
//			List<Osp> ospList = OspDAO.getOspPendentes();
//			Integer codSubModulo = SubModuloDAO.findToId("Produção");
//			
//			for (Osp osp : ospList) {
//				AlertaPendenciaDAO.addPendencia(codSubModulo, osp.getId(), "Osp Pendente!");	
//			}
//			
//			if(statusR != null){
//				StatusRotinasDAO.setStop(statusR);
//			}
//		}		
//	}	
//	private static void RefreshPendenciaAssistenciaTecnica(){
//		
//		String desc = "RUNNING_ASSISTEC";
//		boolean status = StatusRotinasDAO.checkRunning(desc);
//		if(!status){
//			StatusRotinas statusR = StatusRotinasDAO.setRunning(desc);
//				
//			List<Osi> osiList = OsiDAO.getOsiPendentes();
//			Integer codSubModulo = SubModuloDAO.findToId("Assistência Técnica");
//			
//			for (Osi osi : osiList) {
//				AlertaPendenciaDAO.addPendencia(codSubModulo, osi.getId(), "Osi Pendente!");	
//			}
//			
//			if(statusR != null){
//				StatusRotinasDAO.setStop(statusR);
//			}
//		}		
//	}
//	private static void RefreshPendenciaRoteirizacao(){
//		
//		String desc = "RUNNING_ROTEIRIZACAO";
//		boolean status = StatusRotinasDAO.checkRunning(desc);
//		if(!status){
//			StatusRotinas statusR = StatusRotinasDAO.setRunning(desc);
//				
//			List<Ose> oseList = OseDAO.getOsePendentes();
//			Integer codSubModulo = SubModuloDAO.findToId("Roteirização");
//			
//			for (Ose ose : oseList) {
//				AlertaPendenciaDAO.addPendencia(codSubModulo, ose.getId(), "Roteirização Pendente!");	
//			}
//			
//			if(statusR != null){
//				StatusRotinasDAO.setStop(statusR);
//			}
//		}		
//	}
//	private static void RefreshPendenciaCrm(){
		
//		String desc = "RUNNING_CRM";
//		boolean status = StatusRotinasDAO.checkRunning(desc);
//		if(!status){
//			StatusRotinas statusR = StatusRotinasDAO.setRunning(desc);
//				
//			List<Crm> crmList = CrmDAO.getCrmsPendentes();
//			Integer codSubModulo = SubModuloDAO.findToId("Contatos");
//			
//			for (Crm crm : crmList) {
//				AlertaPendenciaDAO.addPendencia(codSubModulo, crm.getId(), "CRM Pendente!");	
//			}
//			
//			if(statusR != null){
//				StatusRotinasDAO.setStop(statusR);
//			}
//		}		
//	}
	
}

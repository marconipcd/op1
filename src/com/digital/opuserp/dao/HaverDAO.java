package com.digital.opuserp.dao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.Haver;
import com.digital.opuserp.domain.HaverCab;
import com.digital.opuserp.domain.HaverDetalhe;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.Real;

public class HaverDAO {

	public static HaverCab buscarHaver(Cliente c){
		
		if(c != null){
			EntityManager em = ConnUtil.getEntity();
			
			Query q = em.createQuery("select  h from HaverCab h where h.cliente.doc_cpf_cnpj=:cliente", HaverCab.class);
			q.setParameter("cliente", c.getDoc_cpf_cnpj());
			
			if(q.getResultList().size() == 1){
				HaverCab haver = (HaverCab)q.getSingleResult();
				return haver;
			}
			
			return null;
		}else{			
			return null;		
		}			
	}
	public static void cancelaHaverDetalhe(Integer codBoleto){
		EntityManager em = ConnUtil.getEntity();
		
		Query q = em.createQuery("select  h from HaverDetalhe h where h.n_doc=:n_doc", HaverDetalhe.class);
		q.setParameter("n_doc", codBoleto.toString());
		
		em.getTransaction().begin();
	    for (HaverDetalhe haver_det : (List<HaverDetalhe>)q.getResultList()) {
			haver_det.setStatus("INATIVO");
	    	em.merge(haver_det);
		}
	    em.getTransaction().commit();		
	}
	
	public static HaverCab save(HaverCab s){
		try{
			EntityManager em = ConnUtil.getEntity();
			
			em.getTransaction().begin();
			s.setData_alteracao(new Date());
			em.merge(s);
			em.getTransaction().commit();
			
			return s;
		}catch(Exception e){
			e.printStackTrace();
			
			return null;
		}
	}
	
	public static HaverDetalhe saveDet(HaverDetalhe s){
		try{
			EntityManager em = ConnUtil.getEntity();
			
			em.getTransaction().begin();			
			em.merge(s);
			em.getTransaction().commit();
			
			return s;
		}catch(Exception e){
			e.printStackTrace();
			
			return null;
		}
	}
	
	
	public static HaverDetalhe add(HaverDetalhe s){
		try{
			EntityManager em = ConnUtil.getEntity();
			
			em.getTransaction().begin();
			em.persist(s);
			em.getTransaction().commit();
			
			return s;
		}catch(Exception e){
			e.printStackTrace();
			
			return null;
		}
	}
	
	public static String getNextID(){
		try{
			EntityManager em = ConnUtil.getEntity();	
			Query q = em.createNativeQuery("SHOW TABLE STATUS LIKE 'haver'");	
			Object result = q.getSingleResult();
			Object[] ob = (Object[]) result;
			String cod = ob[10].toString();
			
			return cod;
		}catch (Exception e){
			e.printStackTrace();
			System.out.println("Erro ao tentar pegar o próximo ID do Haver: "+e.getMessage());
			return null;
		}
	}
	
	public static Haver find(Integer cod){
		EntityManager em = ConnUtil.getEntity();
		return em.find(Haver.class, cod);
	}
	public static HaverCab find2(Integer cod){
		EntityManager em = ConnUtil.getEntity();
		return em.find(HaverCab.class, cod);
	}
	
	public static List<Haver> search(String ndoc){
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createQuery("select h from Haver h where h.nDoc = :n_doc", Haver.class);
		q.setParameter("n_doc", ndoc);
		
		return q.getResultList();
	}
	
	public static String getLayoutReciboPagamento(HaverDetalhe c, double valor){
		
		//Empresa empresa = EmpresaDAO.find(c.getEmpresa_id());
		
		if(c != null){
			StringBuilder s = new StringBuilder();
			
			String ABRE_CENTRALIZAR = new String(new char[]{27,97,1});
			String FECHA_CENTRALIZAR = new String(new char[]{27,97,0});
			
			String ABRE_NEGRITO = new String(new char[]{27,69});
			String FECHA_NEGRITO = new String(new char[]{27,70});
			
			String ABRE_CONDENSED = new String(new char[]{27,15});
			String FECHA_CONDENSED = new String(new char[]{27,15,18});
			
			char GUILHOTINA27 = 27;
			char GUILHOTINA_TOTAL = 119;
			char GUILHOTINA_PARCIAL = 109;
			
			String quebra = "\n";
		
			//s.append(ABRE_CENTRALIZAR);
			s.append(ABRE_NEGRITO);			
			s.append("                       RECIBO");			
			s.append(FECHA_NEGRITO);
			//s.append(FECHA_CENTRALIZAR);
			
			SimpleDateFormat formHora= new SimpleDateFormat("dd/MM/yyyy");
			
			s.append(quebra);
			s.append(quebra);
			s.append(quebra);
			s.append("NUMERO: "+ABRE_NEGRITO+c.getId()+FECHA_NEGRITO);			
			s.append(quebra);	
			s.append("DATA DE EMISSAO: "+ABRE_NEGRITO+formHora.format(new Date())+FECHA_NEGRITO);
			s.append(quebra);
			s.append("EMITIDO POR: "+ABRE_NEGRITO+OpusERP4UI.getUsuarioLogadoUI().getUsername()+FECHA_NEGRITO);
			s.append(quebra);
			s.append(quebra);
			s.append("VALOR:" +ABRE_NEGRITO+"R$"+Real.formatDbToString(String.valueOf(valor))+FECHA_NEGRITO);
			
			s.append(quebra);
			s.append(quebra);
			s.append(ABRE_NEGRITO);
			s.append("Eu, "+c.getHaverCab().getCliente().getNome_razao()+",");
			s.append(quebra);
			s.append("declaro ter recebido da empresa "+OpusERP4UI.getEmpresa().getNome_fantasia()+", o valor acima citado.");
			s.append(quebra);
			s.append(FECHA_NEGRITO);
			s.append(quebra);
			s.append("REFERENTE A: "+ABRE_NEGRITO+"PAGAMENTO ESTORNADO"+FECHA_NEGRITO);			
			s.append(quebra);
			s.append(quebra);		
			s.append(quebra);
			s.append(quebra);
			s.append(quebra);

			s.append("--------------------------------------------------\n");
			
			s.append(ABRE_CENTRALIZAR);			
			s.append("Recebido por");
			s.append(FECHA_CENTRALIZAR);
			
			
			s.append(quebra);
			s.append(quebra);
			s.append(quebra);
			s.append(FECHA_CONDENSED);
			s.append(GUILHOTINA27);
			s.append(GUILHOTINA_TOTAL);
			
						
			return s.toString();
		
		}else{
			return "haver não encontrado";
		}
	}
	
	public static String getLayoutRecibo(Haver c){
		
		//Empresa empresa = EmpresaDAO.find(c.getEmpresa_id());
		
		if(c != null){
			StringBuilder s = new StringBuilder();
			
			char CENTER27 = 27;			
			char CENTER97 = 97;
			char CENTER1 = 1;
			char CENTER0 = 0;
			char NEGRITO27 = 27;
			char NEGRITO69 = 69;
			char NEGRITO70 = 70;
			char CONDENSED27 = 27;
			char CONDENSED15 = 15;
			char FECHA_CONDENSED = 18;
			char GUILHOTINA27 = 27;
			char GUILHOTINA_TOTAL = 119;
			char GUILHOTINA_PARCIAL = 109;
			
			
			
			String quebra = "\n";
			
			s.append(NEGRITO27);
			s.append(NEGRITO69);
			s.append(CONDENSED27);
			s.append(CONDENSED15);
			s.append(CENTER27);
			s.append(CENTER97);
			s.append(CENTER1);
			s.append("d i g i t a l\n");
			s.append(CENTER27);
			s.append(CENTER97);
			s.append(CENTER0);
			
			s.append(CENTER27);
			s.append(CENTER97);
			s.append(CENTER1);
			//s.append(empresa.getRazao_social()+"\n");
			s.append(CENTER27);
			s.append(CENTER97);
			s.append(CENTER0);
			
			s.append(CENTER27);
			s.append(CENTER97);
			s.append(CENTER1);
			//s.append(empresa.getCnpj()+"\n");
			s.append(CENTER27);
			s.append(CENTER97);
			s.append(CENTER0);
			
			s.append(CENTER27);
			s.append(CENTER97);
			s.append(CENTER1);
			//s.append(empresa.getEndereco()+", "+empresa.getNumero()+" "+empresa.getBairro()+" "+empresa.getCidade()+"\n");
			s.append(CENTER27);
			s.append(CENTER97);
			s.append(CENTER0);
			
			s.append(CENTER27);
			s.append(CENTER97);
			s.append(CENTER1);
			//s.append("("+empresa.getDdd_fone1()+") "+empresa.getFone1()+"\n");
			s.append(CENTER27);
			s.append(CENTER97);
			s.append(CENTER0);
			
			s.append(CENTER27);
			s.append(CENTER97);
			s.append(CENTER1);
			//.append(empresa.getSite()+"\n");
			s.append(CENTER27);
			s.append(CENTER97);
			s.append(CENTER0);
			
			
			s.append(NEGRITO27);
			s.append(NEGRITO70);
			s.append("\n");
			s.append("\n");
			

			s.append(CENTER27);
			s.append(CENTER97);
			s.append(CENTER1);
			s.append("CREDITO EM HAVER \n");
			s.append(CENTER27);
			s.append(CENTER97);
			s.append(CENTER0);
			
			SimpleDateFormat formHora= new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			
			
			s.append("CODIGO: "+c.getId());			
			s.append(quebra);	
			s.append("DATA DE EMISSAO: "+formHora.format(c.getData_emissao()));
			s.append(quebra);
			s.append("EMITIDO POR: "+c.getUsuario());
			s.append(quebra);
			s.append(quebra);
			s.append("VALOR: "+Real.formatDbToString(String.valueOf(c.getValor())));
			s.append(quebra);
			s.append("DOCUMENTO: "+c.getDoc());
			s.append(quebra);
			s.append("NUMERO DOC: "+c.getnDoc());
			s.append(quebra);
			s.append(quebra);
			s.append("Referente a: DEPOSITO DE CHEQUE");
			s.append(quebra);
			s.append(quebra);
			s.append(quebra);
			s.append("OBS: E INDISPENSAVEL A APRESENTACAO DESTE CUPOM");
			s.append(quebra);
			s.append("PARA USO DO CREDITO.");			
			s.append(quebra);
			s.append(quebra);
			s.append(quebra);
			s.append(CENTER27);
			s.append(CENTER97);
			s.append(CENTER1);
			s.append("-------------------------------------------------------\n");
			s.append(CENTER27);
			s.append(CENTER97);
			s.append(CENTER0);
			s.append(CENTER27);
			s.append(CENTER97);
			s.append(CENTER1);
			s.append("Autorizado por");
			s.append(CENTER27);
			s.append(CENTER97);
			s.append(CENTER0);
			s.append(quebra);
			s.append(quebra);
			s.append(quebra);
			s.append(FECHA_CONDENSED);
			s.append(GUILHOTINA27);
			s.append(GUILHOTINA_TOTAL);
			
						
			return s.toString();
		
		}else{
			return "boleto não encontrado!";
		}
	}
}

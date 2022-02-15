package com.digital.opuserp.dao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.AlteracoesAssistencia;
import com.digital.opuserp.domain.ConfigOsi;
import com.digital.opuserp.domain.Osi;
import com.digital.opuserp.domain.ServicosItensOsi;
import com.digital.opuserp.util.ConnUtil;

public class OsiDAO {
	
	public static List<Osi> getOsiPendentes(){
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createQuery("select o from Osi o where o.empresa_id=:empresa and o.data_agendamento<=:data and o.status='ABERTO'", Osi.class);
		q.setParameter("empresa", OpusERP4UI.getEmpresa().getId());
		q.setParameter("data", new Date());
		
		return q.getResultList();		
	}
	
	public static String getNextID(){
		try{
			EntityManager em = ConnUtil.getEntity();
			Query q = em.createNativeQuery("SHOW TABLE STATUS LIKE 'osi'");	
			Object result = q.getSingleResult();
			Object[] ob = (Object[]) result;
			String cod = ob[10].toString();
			
			return cod;
		}catch (Exception e){
			e.printStackTrace();
			System.out.println("Erro ao tentar pegar o próximo ID do Acesso: "+e.getMessage());
			return null;
		}
	}
	
	public static void add(Osi osi){
		try{
			EntityManager em = ConnUtil.getEntity();
			em.getTransaction().begin();
			
			if(osi.getId() != null && em.find(Osi.class, osi.getId()) != null){
				em.merge(osi);
			}else{
				em.persist(osi);
			}
			em.getTransaction().commit();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public static Osi find(Integer cod){
		EntityManager em = ConnUtil.getEntity();
		return em.find(Osi.class, cod);
	}
	
	public static void addServico(ServicosItensOsi s){
		EntityManager em = ConnUtil.getEntity();
		em.getTransaction().begin();
		em.persist(s);
		em.getTransaction().commit();
	}
	
	public static List<ServicosItensOsi> getServicos(Integer codOsi){
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createQuery("select s from ServicosItensOsi s where s.osi =:osi", ServicosItensOsi.class);
		q.setParameter("osi", new Osi(codOsi));
		
		return q.getResultList();
	}
	
	public static ConfigOsi getConfigOsi(Integer EnpresaID){
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createQuery("select s from ConfigOsi s where s.empresa_id =:empresa_id", ConfigOsi.class);
		q.setParameter("empresa_id",EnpresaID);
		ConfigOsi confOsi = null;
		if(q.getSingleResult() != null){
			confOsi =  (ConfigOsi) q.getSingleResult();
		}
		return confOsi;
	}
	
	public static List<AlteracoesAssistencia> getOcorrencias(Osi osi){
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createQuery("select s from AlteracoesAssistencia s where s.osi =:osi ", AlteracoesAssistencia.class);
		q.setParameter("osi", osi);
				
		return q.getResultList();
	}
	
	public static String getLayoutOsi(Osi c){
		
		ConfigOsi confOsi = getConfigOsi( OpusERP4UI.getEmpresa().getId());
		
		
		if(c != null){
			StringBuilder s = new StringBuilder();
			
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
			
			
			s.append(CONDENSED27);
			s.append(CONDENSED15);
			s.append("           d i g i t a l \n");
			s.append("      Rua Adjar Maciel, 35 Centro\n");
			s.append("    Belo Jardim/PE CEP: 55.150-040\n");
			s.append("       CNPJ: 07.578.965/0001-05 \n");
			s.append("           IE: 18.3.050.0329 \n");
			s.append("       www.digitalonline.com.br \n");
			s.append("          Fone: (81)3726.3125\n");
			
			
			s.append("\n");
			s.append("\n");
			
			
//			s.append("           "+(char)27+(char)69+" RECIBO DE PAGAMENTO "+(char)27+(char)70+(char)10+"\n");
			//s.append("           "+(char)29 + 249+32+0+27+116+8+ 15+" RECIBO DE PAGAMENTO "+FECHANEGRITO+"\n");
			
			s.append("           ORDEM DE SERVICO \n");
			
			SimpleDateFormat formHora= new SimpleDateFormat("HH:mm:ss");
			String hora = formHora.format(new Date());
			
			SimpleDateFormat form = new SimpleDateFormat("dd/MM/yyyy");
			String hoje = form.format(new Date());
			
			
			s.append("---------------------------------------\n");
			s.append("OS : "+c.getId().toString()+" EMISSAO: "+hoje+" HORA: "+hora+ "\n");
					
			s.append(quebra);
			s.append("              INFORMACOES\n");
			s.append("---------------------------------------\n");			
			s.append("Cliente..: "+c.getCliente().getNome_razao());			
			s.append(quebra);	
			s.append("PRODUTO..: "+c.getEquipamento());
			s.append(quebra);
			
			if(c.getAcessorios() != null){
				s.append("ACESSORIOS..: "+c.getAcessorios());
			}else{
				s.append("ACESSORIOS..: ");
			}
			s.append(quebra);
			s.append("OBS..: "+c.getObservacao());
			s.append(quebra);
			s.append("Recebido Por.: "+c.getOperador());
			s.append(quebra);
			s.append(quebra);
			s.append(quebra);
			s.append(quebra);
			s.append("Atencao:");
			s.append(quebra);
			s.append("1. Os equipamentos reparados terao garantia de 90 (noventa) dias   ");
			s.append(quebra);
			s.append("sobre os servicos executados a contar a partir da Conclusao da");
			s.append(quebra);
			s.append("Ordem de Servico, nao incluindo mau uso e alteracoes");
			s.append(quebra);
			s.append("nas caracteristicas do equipamento;");
			s.append(quebra);
			s.append("2. Nao estao cobertos pela garantia, defeitos provenientes");
			s.append(quebra);
			s.append("de dano eletrico, problemas gerados por softwares ou erros de operacao;");
			s.append(quebra);
			s.append("3. O equipamento so sera entregue mediante a apresentacao deste documento;");
			s.append(quebra);
			s.append("4. Nao nos responsabilizamos por programas instalados em seu equipamento.");
			s.append(quebra);
			s.append("5. A nao retirada do equipamento no prazo de 90 (noventa) dias ");
			s.append(quebra);
			s.append("implicara na venda do equipamento para custear despesas do ");
			s.append(quebra);
			s.append("servico e armazenamento.");
			s.append(quebra);
			s.append("A DIGITAL se isenta de toda e qualquer responsabilidade dos");
			s.append(quebra);
			s.append("programas instalados em seu micro, portanto aconselhamos que");
			s.append(quebra);
			s.append("o cliente mantenha seu backup atualizado.");
			s.append(quebra);
			s.append("* Sera cobrado taxa de Laudo Tecnico caso o orcamento");
			s.append(quebra);
			s.append("nao seja aceito valor de R$ "+confOsi.getValor_laudo());
			s.append(quebra);
			s.append(quebra);
			s.append(quebra);
			s.append("---------------------------------------\n");
			s.append("                Assinatura");
			s.append(quebra);
			s.append(FECHA_CONDENSED);
			s.append(GUILHOTINA27);
			s.append(GUILHOTINA_TOTAL);
			
						
			
			
			return s.toString();
		}else{
			return "Osi Não Encontrada!";
		}
				
		
	}
}

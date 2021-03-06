package com.digital.opuserp.test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import com.digital.opuserp.domain.ContaBancaria;
import com.digital.opuserp.domain.ContasReceber;
import com.digital.opuserp.domain.ControleTitulo;

public class GerarNN {

	static	EntityManagerFactory emf = Persistence.createEntityManagerFactory("OpusERP4");
	static EntityManager em = emf.createEntityManager();		
	static ContaBancaria cb = null;
	
	public static void main(String[] args) {
		
		//Configura Controle
		Query qControleNovo = em.createQuery("select c from ControleTitulo c where c.nome=:nome and c.empresa_id =:empresa", ControleTitulo.class);
		qControleNovo.setParameter("nome", "ACESSO-POS");
		qControleNovo.setParameter("empresa", 1);		
		
		if(qControleNovo.getResultList().size() == 1){			
			ControleTitulo ct = (ControleTitulo)qControleNovo.getSingleResult();
			cb = ct.getConta_bancaria_bkp();
		}
		
		List<Integer> codigos = new ArrayList<>();
		codigos.add(418100);
		codigos.add(418101);
		codigos.add(418102);
		codigos.add(418103);
		codigos.add(418104);
		codigos.add(418105);
		codigos.add(418106);
		codigos.add(418107);
		codigos.add(418108);
		codigos.add(418109);
		codigos.add(418110);
		codigos.add(418111);
		
		
		for (Integer codigo : codigos) {
			gerarNN(codigo);
		}
		
	}
	public static void gerarNN(Integer codBoleto){
		try{
			
			ContasReceber boleto = em.find(ContasReceber.class, codBoleto);
			
			if(boleto != null){
				if(boleto.getN_numero_sicred() == null || boleto.getN_numero_sicred().isEmpty() || boleto.getN_numero_sicred().equals("")){
					
					
					
					String NumeroNovo = "";
					if(cb != null){
						if(cb.getNome_banco().equals("SICRED")){					
							NumeroNovo = calcularNossoNumeroSicred(boleto.getCliente().getId(),cb);				
						}
						
						if(cb.getNome_banco().equals("BANCO DO BRASIL")){
							//NumeroNovo = calcularNossoNumero(boleto.getCliente().getId());
						}
					}
					
					
					boleto.setN_numero_sicred(NumeroNovo);
					em.getTransaction().begin();
					em.merge(boleto);
					em.getTransaction().commit();
					
					
				}
				
				
			}
			
			
		
		}catch(Exception e){
			e.printStackTrace();
			
		}
	}
	
	public static String calcularNossoNumeroSicred(Integer codCliente,ContaBancaria cb){
		
		
		try{
			
			Query q1 = em.createNativeQuery("SELECT N_NUMERO_SICRED FROM `contas_receber` WHERE N_NUMERO_SICRED IS NOT NULL");
			List<String> lista1 = q1.getResultList();
			
			Query q2 = em.createNativeQuery("SELECT N_NUMERO_SICRED_ANTIGO FROM `contas_receber` WHERE N_NUMERO_SICRED_ANTIGO IS NOT NULL");
			List<String> lista2 = q2.getResultList();
			
			StringBuilder numero = new StringBuilder(); 
			Integer QtdZeros = 5;
			Integer Qtd = 1;
			
			while(true){
				
				numero = new StringBuilder();
				
				SimpleDateFormat sdf = new SimpleDateFormat("YY");
				
				 //-//------//--  Padr??o : AA/B XXXXX-D  --//-------------
				//-//------//-----------------------------------------------------------------------------------------------------------------------//--
			   	
				// agencia(AAAA)+posto(PP)+conta(NNNN)+ano(YY)+byte(B)+sequencia(SSSSS)
				
				String caract1 = sdf.format(new Date()).toString()+"/3";
				String caract2 = String.format("%0"+QtdZeros.toString()+"d", Qtd);
				int digito = getMod11(cb.getAgencia_banco()+cb.getPosto_beneficiario()+cb.getCod_beneficiario()+caract1.replace("/", "")+caract2);
				if(digito == 10 || digito == 11){
					digito = 0;
				}
				String caract3 = "-"+digito;
				
				numero.append(caract1);
				numero.append(caract2);
				numero.append(caract3);
				
				//Query consultaNNumero = em.createQuery("select cr from ContasReceber cr where cr.n_numero_sicred = :codNossoNumero", ContasReceber.class);
				//consultaNNumero.setParameter("codNossoNumero", numero.toString());
				
				//Query consultaNNumero2 = em.createQuery("select cr from ContasReceber cr where cr.n_numero_sicred_antigo = :codNossoNumero", ContasReceber.class);
				//consultaNNumero2.setParameter("codNossoNumero", numero.toString());
				
				boolean exists = false;
				//if(consultaNNumero.getResultList().size() == 0 && consultaNNumero2.getResultList().size() == 0){
				if(!lista1.contains(numero.toString()) && !lista2.contains(numero.toString())){
					exists = true;
				}else{
					Qtd++;
				}
				
				boolean valid = false;
				if(numero.toString().length() == 11){
					valid = true;
				}else{
					if(numero.toString().length() > 11){
						QtdZeros--;
					}else{
						QtdZeros++;
					}
				}
				
				if(valid && exists){
					break;
				}
					
			}
			
			return numero.toString();
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
public static int getMod11(String num){
	    
	    String[] numeros,parcial;
	    numeros = new String[num.length()+1];
	    parcial = new String[num.length()+1];
	    
	    int peso = 2;
	    
	    for (int i = num.length(); i > 0; i--) {
	    	
	    	if(peso > 9){
	    		peso = 2;
	    	}
	    
	        numeros[i] = num.substring(i-1,i);	    
	        String r1 = String.valueOf(Integer.parseInt(numeros[i]) * peso);
	        
	        parcial[i] = r1;
	       
	        peso++;
	    }
	    
	    int resultado_soma = 0;	    
	    for (int i = parcial.length-1; i > 0; i--) {
			
	    	resultado_soma = resultado_soma + Integer.parseInt(parcial[i]);
		}	    	    
	    int divisao = resultado_soma / 11;	    
	    int digito =11 - (resultado_soma - (Integer.parseInt(String.valueOf(divisao).split(",")[0]) * 11)) ;	    
	    
	    System.out.println("Divisao: "+divisao);	    
	    return digito == 10 && digito == 11 ? 0 : digito;
	}

//Na mesma noite se prepara para passar o Vau de Jaboque
//Faz suas mulheres, seus filhos, seus servos e tudo quanto tem passar e fica s??
//Quando ele pensou que estava s?? aparece um var??o e luta com ele
//O anjo n??o consegue prevalecer contra ele e toca na sua coxa


//Nasce, Segurando o calcanhar de seu irm??os
//Recebe o nome de Jac?? de acordo com as circunst??ncias
//Esa?? era o preferido do PAI
//Jac?? era o preferido da M??e
//Jac?? consegue a primogenitura de Esa?? em troca de um prato de comida
//Isaque j?? velho orienta Esa?? a trazer um guizado para que coma e sua alma o aben??oe
//Rebeca ouve a instru????o de Isaque a Esa??
//Chama Jac?? e lhe passa a inten????o de Isaque de Aben??oar Jac??
//Rebeca induz Jac?? a se passar por Esa?? para receber a ben????o
//Isaque  n??o reconhece a voz como sendo a voz de Esa??, mais se convence quando toca seu corpo, porque Jac?? Utilizava roupas de esa??
//Isaque aben??oa Jac?? e ele sai de sua presen??a
//Esa?? chega e tamb??m prepara um guizado e leva ao seu pai
//Esa?? descobre que Jac?? havia recebido a ben????o antes
//Esa?? jura seu irm??o de Morte e pretende mata-lo ap??s a morte de Isaque
//Rebeca e Isaque fazem Jac?? partir por uns dias para casa de seu parente Lab??o
//Isaque parte em fuga e s?? para a noite para dormir
//Pega uma pedra para fazer de travesseiro e dorme
//Sonha com uma escada que ia do c??u a terra e anjos deciam e subiam
//Deus reafirma a promessa que fez a Abra??o e Isaque e promete aquela terra a Jac??
//Jac?? faz um voto ao SENHOR naquele lugar
//Jac?? chega nas terras de Lab??o, se apaixona por raquel
//Jac?? trabalha por Raquel 7 anos e ?? enganado por Lab??o e recebe Leia
//Trabalha mais 7 anos por Raquel
//Lab??o procura enganar Jac?? diversas vezes mudando seu sal??rio
//Deus aben??oa Jac?? naquele lugar e ap??s 20 anos Deus manda-o de volta para as terras de seus pais
//No caminho antes de chegar Jac?? se lembra de Esa?? e manda menssageiros para aplacar a ira de seu irm??o
//OS menssageiros voltam com a not??cia que Esa?? vem ao encontro de Jac?? com 400 homens
//Jac?? se angustia e come??a a temer seu irm??o
//Utiliza uma estrat??gia de separar tudo em dois bandos para que se por ventura Esa?? encontrasse um deles o outro escapasse
//Temendo e angust??ado junta tudo que tem e passa o vau de jaboque e fica s??...

//Essa ?? a situa????o que Jac?? passa o Vau de Jaboque
//Todo mundo um dia vai passar pelo Vau de Jaboque
//Vau de Jaboque ?? lugar de tratamento espir??tual
//Vau de Jaboque ?? lugar onde Deus nos confronta
//Vau de Jaboque ?? onde temos experi??ncias com Deus


//Alerta e confirma????o simultaneo
//a = corte da curta com a m??dia
//c = corte da m??dia com a longa

}

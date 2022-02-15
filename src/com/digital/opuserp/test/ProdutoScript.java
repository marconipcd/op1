package com.digital.opuserp.test;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import com.digital.opuserp.domain.Produto;
import com.digital.opuserp.util.Real;
import com.digital.opuserp.util.StringUtil;
import com.digital.opuserp.util.Utils;

public class ProdutoScript {

	static EntityManager em;
	static EntityManagerFactory emf;
	public static void main(String[] args) {
		
		
		
		
			gerarTxt();
	
	}
	
	private static void corrigirCustosProduto(){
		emf = Persistence.createEntityManagerFactory("OpusERP4");
		em = emf.createEntityManager();		
		
		Query q = em.createQuery("select p from Produto p ",Produto.class);
		List<Produto> listProduto = q.getResultList();
		
		for (Produto produto : listProduto) {
			
			double valor_custo = produto.getValorCusto() != null ? produto.getValorCusto() : 0;
			double valor_ipi = produto.getTaxaIpi();
			double valor_frete = produto.getFrete() != null ? produto.getFrete() : 0;
			double valor_diferenca_aliq = produto.getDiferenca_aliquota() != null ? produto.getDiferenca_aliquota() : 0;
			double valor_venda = produto.getValorVenda() != null ? produto.getValorVenda() : 0;
			
			double valor_custo_total = 0;
			double valor_lucro = 0;
			double valor_margem_lucro = 0;
			
			if(valor_custo > 0){				
			    double percentuais = (valor_ipi+valor_frete+valor_diferenca_aliq) / 100.0; 
			    valor_custo_total = valor_custo + (percentuais * valor_custo);			    
			}
			
			if(valor_custo_total > 0 && valor_venda > 0){
				valor_lucro = valor_venda-valor_custo_total;
				valor_margem_lucro = ((valor_venda/valor_custo_total)-1) * 100;
			}
			
			System.out.println("PRODUTO:"+produto.getId().toString()+" VALOR_CUSTO:"+valor_custo+" VALOR_CUSTO_TOTAL:"+valor_custo_total+" VALOR_VENDA:"+valor_venda+" VALOR_LUCRO:"+valor_lucro+" VALOR_MARGEM_LUCRO:"+valor_margem_lucro);
			System.out.println("==============================================================");
			

			try{
				produto.setCusto_total(Float.parseFloat(String.valueOf(valor_custo_total)));
				produto.setLucro(Float.parseFloat(String.valueOf(valor_lucro)));
				produto.setMargemLucro(Float.parseFloat(String.valueOf(valor_margem_lucro)));

				em.getTransaction().begin();
				em.merge(produto);
				em.getTransaction().commit();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
	}
	
	private static void gerarTxt(){
		emf = Persistence.createEntityManagerFactory("OpusERP4");
		em = emf.createEntityManager();		
		
		Query q = em.createQuery("select p from Produto p where p.status = 'ATIVO' and p.empresaId=1",Produto.class);
		List<Produto> listProduto = q.getResultList();
		
		FileWriter arq;
		try {
			arq = new FileWriter("C:\\TESTE\\PRODUTO.txt");
			PrintWriter gravarArq = new PrintWriter(arq);
			//gravarArq.print("LIMPAR");
			//gravarArq.print("\n");

			for (Produto produto : listProduto) {
				
				String valorVenda = produto.getValorVenda().toString().replaceAll(".", "");
				
				Double valorDouble = Double.parseDouble(produto.getValorVenda().toString());															  
				DecimalFormat df = new DecimalFormat();  
				df.applyPattern("#,##0.000");  			
						
				
				
				//Campos
				//----------------
				String codExterno = StringUtil.preencheCom(produto.getId().toString()," ", 20, 2);
				String codBarras = StringUtil.preencheCom(produto.getgTin().toString()," ", 20, 2);
				String descricao;
				
				if(produto.getNome().length() > 40){
					descricao = StringUtil.preencheCom(produto.getNome().toString().substring(0, 40)," ",40,2);
				}else{
					descricao = StringUtil.preencheCom(produto.getNome().toString()," ",40,2);
				}
				
				String complemento = StringUtil.preencheCom(""," ",20,2);
				String unidade = StringUtil.preencheCom(produto.getUnidadeProduto().getNome()," ",4,2);
				String valorFormatado = df.format(valorDouble);
				String desconto = "000000";
				String situacaoTributaria = "N";
				String icms = "0000";
				String obsPopUp = StringUtil.preencheCom("", " ", 65, 2);
				String calculaQtd = "N";
				String bloqueiaQtdFracionada = "N";
				String bloqueiaQtd = "N";
				String arredonda = "S";
				String producaoPropria = "N";
				String codGrupo = StringUtil.preencheCom(produto.getGrupoId().getId().toString(), " ", 6, 2);
				String descricaoGrupo = StringUtil.preencheCom(produto.getGrupoId().getNome_grupo(), " ", 30, 2);
				String codDepartamento = "      ";
				String descDepartamento = "                              ";
				String codMarca  = StringUtil.preencheCom(produto.getMarcasId().getId().toString(), " ", 6, 2);
				String descMarca  = StringUtil.preencheCom(produto.getMarcasId().getNome(), " ", 30, 2);
				String codTipoVasil  = StringUtil.preencheCom("", " ", 6, 2);
				String descVasiliame  = StringUtil.preencheCom("", " ", 30, 2);
				String codAnimacao = "0           ";
				//String flag = StringUtil.preencheCom("FLAG", " ", 6, 2);
				String ncm = StringUtil.preencheCom(produto.getNcm(), " ", 20, 2);
				String codTipoDescAdd = "000000";
				String gTinContabil = StringUtil.preencheCom("", " ", 20, 2);
				String gTinExTipi = StringUtil.preencheCom("", " ", 20, 2);
				String gTinTributavel = StringUtil.preencheCom("", " ", 20, 2);
				String ids = "000000000000000000000000000000000000000000000000";
				String kit = "N";
				String qtdEstoque = "       0.000";
				String prazoDevolucao = "000";
				String cest = "       ";
				String controlaEstoque = "N";
				String restante = "         N00.00";
				
				
				
				
				gravarArq.print(codExterno);
				gravarArq.print(codBarras);
				gravarArq.print(descricao);
				gravarArq.print(complemento);
				gravarArq.print(unidade);
				gravarArq.print(StringUtil.preencheCom(valorFormatado.replace(",", ""), "0", 12, 1));
				gravarArq.print(desconto);
				gravarArq.print(situacaoTributaria);
				gravarArq.print(icms);
				gravarArq.print(obsPopUp);
				gravarArq.print(calculaQtd);
				gravarArq.print(bloqueiaQtdFracionada);
				gravarArq.print(bloqueiaQtd);
				gravarArq.print(arredonda);
				gravarArq.print(producaoPropria);
				gravarArq.print(codGrupo);
				gravarArq.print(descricaoGrupo);
				gravarArq.print(codDepartamento);
				gravarArq.print(descDepartamento);
				gravarArq.print(codMarca);
				gravarArq.print(descMarca);
				gravarArq.print(codTipoVasil);
				gravarArq.print(descVasiliame);
				gravarArq.print(codAnimacao);
				//gravarArq.print(flag);
				gravarArq.print(ncm);
				gravarArq.print(codTipoDescAdd);
				gravarArq.print(gTinContabil);				
				gravarArq.print(gTinExTipi);
				gravarArq.print(gTinTributavel);
				gravarArq.print(ids);
				gravarArq.print(kit);
				gravarArq.print(qtdEstoque);
				gravarArq.print(prazoDevolucao);
				gravarArq.print(cest);
				gravarArq.print(controlaEstoque);
				gravarArq.print(restante);
				
				
				
								

				
				gravarArq.print("\n");
			}
		
		} catch (IOException e) {

			e.printStackTrace();
		}
	}
}

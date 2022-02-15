package com.digital.opuserp.test;

import java.nio.charset.Charset;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import com.digital.opuserp.domain.ContasReceber;
import com.digital.opuserp.domain.Produto;
import com.hexiong.jdbf.DBFReader;
import com.hexiong.jdbf.DBFWriter;
import com.hexiong.jdbf.JDBFException;
import com.hexiong.jdbf.JDBField;

public class DBFTest {
	
	
	static EntityManager em;
	static EntityManagerFactory emf;
	
	public static void main(String[] args) {
		
		emf = Persistence.createEntityManagerFactory("OpusERP4");
		em = emf.createEntityManager();		
		
//		try {
//					JDBField[] fields = {
//								new JDBField("CODBARRA", 'C', 50, 0),
//								new JDBField("DESCRICAO", 'C', 200, 0),
//								new JDBField("UNIDADE", 'C', 8, 0),
//								new JDBField("EMBALAGEM", 'C', 8, 0),
//								new JDBField("CODGRUPO", 'C', 8, 0),
//								new JDBField("ALIQUOTA", 'F', 8, 0),
//								new JDBField("TIPOTRIB", 'C', 8, 0),
//								new JDBField("PRECOVENDA", 'F', 8, 0),
//								new JDBField("DESCMAXIMO", 'C', 8, 0),
//								new JDBField("CODRETAG", 'C', 8, 0),
//								new JDBField("SITUACAO", 'C', 8, 0),
//								new JDBField("PESADO", 'C', 8, 0),
//								new JDBField("UNIDADE2", 'C', 8, 0),
//								new JDBField("EMBALAGEM2", 'C', 8, 0),
//								new JDBField("PRECO2", 'C', 8, 0),
//								new JDBField("UNIDADE3", 'C', 8, 0),
//								new JDBField("EMBALAGEM3", 'C', 8, 0),
//								new JDBField("PRECO3", 'C', 8, 0),
//								new JDBField("UNIDADE4", 'C', 8, 0),
//								new JDBField("EMBALAGEM4", 'C', 8, 0),
//								new JDBField("PRECO4", 'C', 8, 0),
//								new JDBField("ESTLIMITE", 'C', 8, 0),
//								new JDBField("CODBARRA1", 'C', 8, 0),
//								new JDBField("CODBARRA2", 'C', 8, 0),
//								new JDBField("CODBARRA3", 'C', 8, 0),
//								new JDBField("CODBARRA4", 'C', 8, 0),
//								new JDBField("CODBARRA5", 'C', 8, 0)
//					};
//					
//					DBFWriter write = new DBFWriter("./DINCASH.DBF", fields);
//					
////					/Query q = em.createQuery("select cr from ContasReceber cr where cr.n_doc =:codOsi and cr.controle = 'ASSISTENCIA'", ContasReceber.class);
//					Query q = em.createQuery("select p from Produto p where p.empresaId = 4", Produto.class);
//					List<Produto> produtos= q.getResultList();
//					
//					for (Produto p : produtos) {
//						write.addRecord(new Object[]{
//								p.getgTin(), 
//								p.getNome(), 
//								p.getUnidadeProduto() != null ? p.getUnidadeProduto().getNome() : "UN",
//								"0.0",
//								p.getGrupoId() != null ? p.getGrupoId().getId().toString() : "001",
//								p.getDiferenca_aliquota(),
//								p.getIat(),
//								p.getValorVenda(),
//								"0.00",
//								"0001",
//								"A",
//								"N",
//								"",
//								"0.00",
//								"0.00",
//								"",
//								"",
//								"0.00",
//								"0.00",
//								"",
//								"0.00",
//								"0.00",
//								"0",
//								"",
//								"",
//								"",
//								""
//								});
//					}
//						
//				    
//					
//					write.close();
//		} catch (JDBFException e) {
//			e.printStackTrace();
//		}
		
		
		try {
			DBFReader dbfreader = new DBFReader("./DINCASH.DBF");
			int i;
			for (i=0; i<dbfreader.getFieldCount(); i++) {
			  System.out.print(dbfreader.getField(i).getName()+"  ");
			}
			
			System.out.print("\n");
			
			for(i = 0; dbfreader.hasNextRecord(); i++){
			    Object aobj[] = dbfreader.nextRecord(Charset.forName("GBK"));
			    
			    for (int j=0; j<aobj.length; j++)
			    	System.out.print(aobj[j]+"  |  ");
			    	System.out.print("\n");
			    	//break;
			}

			System.out.println("Total Count: " + i);
		} catch (JDBFException e) {
			e.printStackTrace();
		}
		
		
		
		
		
		
		
		
		
		
				
		
	}

	
}

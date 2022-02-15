package com.digital.opuserp.test.nfe;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.digital.opuserp.test.nfe.consrecinfe.TNFe.InfNFe.Det.Imposto.ICMS.ICMS10;
import com.digital.opuserp.test.nfe.consrecinfe.TNFe.InfNFe.Det.Imposto.PIS.PISAliq;
import com.digital.opuserp.test.nfe.envinfe.ObjectFactory;
import com.digital.opuserp.test.nfe.envinfe.TEnderEmi;
import com.digital.opuserp.test.nfe.envinfe.TEndereco;
import com.digital.opuserp.test.nfe.envinfe.TEnviNFe;
import com.digital.opuserp.test.nfe.envinfe.TNFe;
import com.digital.opuserp.test.nfe.envinfe.TNFe.InfNFe;
import com.digital.opuserp.test.nfe.envinfe.TNFe.InfNFe.Dest;
import com.digital.opuserp.test.nfe.envinfe.TNFe.InfNFe.Det;
import com.digital.opuserp.test.nfe.envinfe.TNFe.InfNFe.Det.Imposto;
import com.digital.opuserp.test.nfe.envinfe.TNFe.InfNFe.Det.Imposto.COFINS;
import com.digital.opuserp.test.nfe.envinfe.TNFe.InfNFe.Det.Imposto.COFINS.COFINSNT;
import com.digital.opuserp.test.nfe.envinfe.TNFe.InfNFe.Det.Imposto.ICMS;
import com.digital.opuserp.test.nfe.envinfe.TNFe.InfNFe.Det.Imposto.ICMS.ICMSSN500;
import com.digital.opuserp.test.nfe.envinfe.TNFe.InfNFe.Det.Imposto.PIS;
import com.digital.opuserp.test.nfe.envinfe.TNFe.InfNFe.Det.Imposto.PIS.PISNT;
import com.digital.opuserp.test.nfe.envinfe.TNFe.InfNFe.Det.Prod;
import com.digital.opuserp.test.nfe.envinfe.TNFe.InfNFe.Emit;
import com.digital.opuserp.test.nfe.envinfe.TNFe.InfNFe.Ide;
import com.digital.opuserp.test.nfe.envinfe.TNFe.InfNFe.InfAdic;
import com.digital.opuserp.test.nfe.envinfe.TNFe.InfNFe.Total;
import com.digital.opuserp.test.nfe.envinfe.TNFe.InfNFe.Total.ICMSTot;
import com.digital.opuserp.test.nfe.envinfe.TNFe.InfNFe.Transp;
import com.digital.opuserp.test.nfe.envinfe.TNFe.InfNFe.Transp.Transporta;
import com.digital.opuserp.test.nfe.envinfe.TNFe.InfNFe.Transp.Vol;
import com.digital.opuserp.test.nfe.envinfe.TUf;
import com.digital.opuserp.test.nfe.envinfe.TUfEmi;




public class NfeTest {

	  public static void main(String[] args) {  
	        try {  
	            TNFe nFe = new TNFe();  
	            InfNFe infNFe = new InfNFe();  
	      
	            infNFe.setId("NFe42110403452234000145550010000000281765232800");  
	            infNFe.setVersao("2.00");  
	      
	            infNFe.setIde(dadosDeIdentificacao());  
	            infNFe.setEmit(dadosDoEmitente());  
	            infNFe.setDest(dadosDoDestinatario());  
	              
	            infNFe.getDet().add(dadosDoProduto());  
	              
	            infNFe.setTotal(totaisDaNFe());  
	            //infNFe.setTransp(dadosDoTransporte());  
	            infNFe.setInfAdic(informacoesAdicionais());  
	              
	            nFe.setInfNFe(infNFe);  
	  
	            TEnviNFe enviNFe = new TEnviNFe();  
	            enviNFe.setVersao("2.00");  
	            enviNFe.setIdLote("0000001");  
	            enviNFe.getNFe().add(nFe);  
	              
	            info("XML EnviNF-e: " + strValueOf(enviNFe));  
	        } catch (Exception e) {  
	            error(e.toString());  
	        }  
	    }  
	  
	  /** 
	     * B - Identificação da Nota Fiscal eletrônica 
	     * @return 
	     */  
	    private static Ide dadosDeIdentificacao() {  
	        Ide ide = new Ide();  
	        ide.setCUF("26");  //Código UF
	        ide.setCNF("76523280");  // Código Numérico que compõe a Chave de Acesso
	        ide.setNatOp("venda");  //Descrição da Natureza da  Operação Ex.: venda,compra,transferencia,devolução,importação,consignação,remessa	        
	        ide.setIndPag("0");  //Indicador da forma de   pagamento Ex.: 0- pagamento à vista, 1- pagamento à prazo, 2- outros
	        ide.setMod("55");  // Código do modelo fiscal 55 subtitui o modelo 1 e A1
	        ide.setSerie("1");  //Série do documento fiscal
	        ide.setNNF("101");  // Número do documento fiscal
	        ide.setDEmi("2014-09-24");  // Data de emissão
	        ide.setDSaiEnt("2014-09-25");  //Data de saída ou entrada
	        ide.setHSaiEnt("14:00:00");  //Hora de saída ou entrada
	        ide.setTpNF("1");  //Tipo de Operação Ex.: 0-entrada / 1-saída
	        ide.setCMunFG("2601706");  //Código do Municipio	        
	        ide.setTpImp("2");  //Formato de Impressão do  DANFE 1-retrato 2-paisagem
	        ide.setTpEmis("1");  //Tipo de Emissão da NF-e Ex.: 1 – Normal,2 – Contingência FS,3 – Contingência SCAN,4 – Contingência DPEC,5 – Contingência FS-DA 
	        ide.setCDV("0");  //Dígito Verificador da Chave de  Acesso da NF-e 
	        ide.setTpAmb("2");  //Identificação do Ambiente Ex.: 1-Produção, 2- Homologação
	        ide.setFinNFe("1");  //Finalidade de emissão da NFe Ex.: 1-normal, 2-complementar,3-ajuste
	        ide.setProcEmi("0");  //Sempre 0
	        ide.setVerProc("3.0");  
	        return ide;  
	    }  
	    /** 
	     * C - Identificação do Emitente da Nota Fiscal eletrônica 
	     * @return 
	     */  
	    private static Emit dadosDoEmitente() {  
	        Emit emit = new Emit();  
	        emit.setCNPJ("07578965000105");  
	        emit.setXNome("ADEMIR DE SOUZA PINTO FILHO - INFORMATICA - ME");  
	        emit.setXFant("DIGITAL");  
	  
	        TEnderEmi enderEmit = new TEnderEmi();   
	        enderEmit.setXLgr("RUA ADJAR MACIEL");  
	        enderEmit.setNro("35");  
	        enderEmit.setXBairro("CENTRO");  
	        enderEmit.setCMun("2601706");  
	        enderEmit.setXMun("BELO JARDIM");  
	        enderEmit.setUF(TUfEmi.valueOf("PE"));  
	        enderEmit.setCEP("55150040");  
	        enderEmit.setCPais("1058");  
	        enderEmit.setXPais("BRASIL");  
	        enderEmit.setFone("8137261827");  
	        
	        emit.setEnderEmit(enderEmit);  
	  
	        emit.setIE("032984693");  
	        emit.setCRT("1");   //Código de Regime Tributário 
	        return emit;  
	    }  
	  
	    /** 
	     * E - Identificação do Destinatário da Nota Fiscal eletrônica 
	     * @return 
	     */  
	    private static Dest dadosDoDestinatario() {  
	        Dest dest = new Dest();  
	        //dest.setCNPJ("12345123000133");  
	        //dest.setIE("464646464");  
	        dest.setCPF("05872109407");
	        dest.setXNome("Marconi César Pereira da Silva");  
	          
	        TEndereco enderDest = new TEndereco();   
	        enderDest.setXLgr("Vila Dr. Fernando de Abreu Qd. C");  
	        enderDest.setNro("3");  
	        enderDest.setXBairro("São Pedro");  
	        enderDest.setCMun("2601706");  
	        enderDest.setXMun("Belo Jardim");  
	        enderDest.setUF(TUf.valueOf("PE"));  
	        enderDest.setCEP("55155660");  
	        enderDest.setCPais("1058");  
	        enderDest.setXPais("Brasil");  
	        enderDest.setFone("8137261827");  
	        dest.setEnderDest(enderDest);  
	  
	        dest.setEmail("marconipcd@gmail.com");  
	        return dest;  
	    }  
	  
	    /** 
	     * H - Detalhamento de Produtos e Serviços da NF-e 
	     * I - Produtos e Serviços da NF-e 
	     * M - Tributos incidentes no Produto ou Serviço 
	     * V - Informações adicionais 
	     * @return 
	     */  
	    private static Det dadosDoProduto() {  
	        Det det = new Det();  
	        det.setNItem("1");  
	  
	        /** 
	         * Dados do Produro 
	         */  
	        Prod prod = new Prod();  
	        prod.setCProd("3403");  //Código do produto ou serviço
	        prod.setCEAN("7898945001915");  //Código de barras
	        prod.setXProd("ADAPTADOR CONVERSOR HDMI X DVI"); //Descrição do Produto  
	        prod.setNCM("85444200");  //Código NCM
	        prod.setCFOP("5102");  //Utilizar código da tabela de CFOP
	        prod.setUCom("UND");  //Unidade Comercial
	        prod.setQCom("2.0000");  //Quantidade Comercial
	        prod.setVUnCom("90.0000");  //Valor Unitário de Comércialização
	        prod.setVProd("180.00");  //Valor Total Bruto dos Produtos 
	        prod.setCEANTrib(""); //Código de Barras Tributável  
	        prod.setUTrib("UND");  //Unidade Tributável
	        prod.setQTrib("2.0000");  //Quantidade Tributável
	        prod.setVUnTrib("90.0000");  //Valor Unitário Tributável
	        //prod.setVFrete(value); //Valor do Frete
	        //prod.setVSeg(value); //Valor do Seguro
	        //prod.setVDesc(value); //Valor do Desconto
	        //prod.setVOutro(value); //Valor de outras despesas
	        prod.setIndTot("1");  //Indica se O Valor do Item compoe a nota 0-não, 1-sim
	        det.setProd(prod);  
	          
	        /** 
	         * Impostos da NF-e 
	         */  
	        Imposto imposto = new Imposto();
	        //imposto.setCOFINS(value);
	        //imposto.setCOFINSST(value);
	        //imposto.setICMS(value);
	        //imposto.setII(value);
	        //imposto.setIPI(value);
	        //imposto.setISSQN(value);
	        //imposto.setPIS(value);
	        //imposto.setPISST(value);
	        
	        
	          
//	        ICMS icms = new ICMS();  
//	        
//	        ICMSSN500 icmssn500 = new ICMSSN500();  
//	        icmssn500.setOrig("0");  
//	        icmssn500.setCSOSN("500");  
//	        icmssn500.setVBCSTRet("0.00");  
//	        icmssn500.setVICMSSTRet("0.00");
//	        
//	        ICMS10 teste = new ICMS10();
//	        teste.set
//	        
//	        icms.setICMSSN500(icmssn500);  
//	          
//	        PIS pis = new PIS();  
//	        PISNT pisnt = new PISNT();  
//	        pisnt.setCST("07");  
//	        pis.setPISNT(pisnt);  
//	  
//	        COFINS cofins = new COFINS();  
//	        COFINSNT cofinsnt = new COFINSNT();  
//	        cofinsnt.setCST("07");	        
//	        cofins.setCOFINSNT(cofinsnt);
//	       
//	          
//	        
//	        imposto.setICMS(icms);  
//	        imposto.setPIS(pis);  
//	        imposto.setCOFINS(cofins);  
//	        
	          
	        det.setImposto(imposto);  
	          
	        /** 
	         * Informações adicionais do Produto. 
	         */  
	        det.setInfAdProd("Nota Fiscal Eletronica de Exemplo");  
	  
	        return det;  
	    }  
	  
	    /** 
	     * W - Valores Totais da NF-e 
	     * @return 
	     */  
	    private static Total totaisDaNFe() {  
	        Total total = new Total();  
	  
	        ICMSTot icmstot = new ICMSTot();  
	        icmstot.setVBC("0.00");  
	        icmstot.setVICMS("0.00");  
	        icmstot.setVBCST("0.00");  
	        icmstot.setVST("0.00");  
	        icmstot.setVProd("180.00");  
	        icmstot.setVFrete("0.00");  
	        icmstot.setVSeg("0.00");  
	        icmstot.setVDesc("0.00");  
	        icmstot.setVII("0.00");  
	        icmstot.setVIPI("0.00");  
	        icmstot.setVPIS("0.00");  
	        icmstot.setVCOFINS("0.00");  
	        icmstot.setVOutro("0.00");  
	        icmstot.setVNF("180.00");  
	        total.setICMSTot(icmstot);  
	  
	        return total;  
	    }  
	  
	    /** 
	     * X - Informações do Transporte da NF-e 
	     * @return 
	     */  
	    private static Transp dadosDoTransporte() {  
	        Transp transp = new Transp();  
	        transp.setModFrete("0");  
	          
	        /** 
	         * Dados da Transportadora. 
	         */  
	        Transporta transporta = new Transporta();  
	        transporta.setCNPJ("34523233000123");  
	        transporta.setXNome("JavaC - Java Communuty");  
	        transporta.setIE("121212121");  
	        transporta.setXEnder("AV. www.javac.com.br");  
	        transporta.setXMun("Java");  
	        transporta.setUF(TUf.valueOf("SC"));          
	        transp.setTransporta(transporta);  
	          
	        /** 
	         * Dados de Volumes. 
	         */  
	        Vol vol = new Vol();  
	        vol.setQVol("0");  
	        vol.setNVol("0");  
	        vol.setPesoL("0.000");  
	        vol.setPesoB("0.000");  
	        transp.getVol().add(vol);  
	  
	        return transp;  
	    }  
	  
	    /** 
	     * Z - Informações Adicionais da NF-e 
	     * @return 
	     */  
	    private static InfAdic informacoesAdicionais() {  
	        InfAdic infAdic = new InfAdic();  
	        infAdic.setInfCpl("Informacoes Adicionais da NF-e.");  
	        return infAdic;  
	    }  
	  
	    /** 
	     * Método que Converte o Objeto em String. 
	     * @param consStatServ 
	     * @return 
	     * @throws JAXBException 
	     */  
	    private static String strValueOf(TEnviNFe enviNFe) throws JAXBException {  
	        JAXBContext context = JAXBContext.newInstance(TEnviNFe.class);  
	        Marshaller marshaller = context.createMarshaller(); 
	        
	        JAXBElement<TEnviNFe> element = new ObjectFactory().createEnviNFe((enviNFe));  
	        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.FALSE);  
	        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);  
	  
	        StringWriter sw = new StringWriter();  
	        marshaller.marshal(element, sw);  
	  
	        String xml = sw.toString();  
	        xml = xml.replaceAll("xmlns:ns2=\"http://www.w3.org/2000/09/xmldsig#\" ", "");  
	        xml = xml.replaceAll("<NFe>", "<NFe xmlns=\"http://www.portalfiscal.inf.br/nfe\">");  
	          
	        return xml;  
	    }  
	  
	    /** 
	     * Log ERROR. 
	     * @param error 
	     */  
	    private static void error(String error) {  
	        System.out.println("| ERROR: " + error);  
	    }  
	  
	    /** 
	     * Log INFO. 
	     * @param info 
	     */  
	    private static void info(String info) {  
	        System.out.println("| INFO: " + info);  
	    }  
}

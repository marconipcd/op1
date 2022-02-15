package com.digital.opuserp.util;

import java.io.IOException;
import java.io.InputStream;

import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.util.boletos.BoletoTransformerUtil;
import com.digital.opuserp.util.boletos.transform.PDFBoletoWriterUtil;

import br.com.caelum.stella.boleto.Boleto;
import br.com.caelum.stella.boleto.exception.GeracaoBoletoException;
import br.com.caelum.stella.boleto.transformer.BoletoTransformer;
import br.com.caelum.stella.boleto.transformer.BoletoWriter;
import br.com.caelum.stella.boleto.transformer.PDFBoletoWriter;

public class GeradorBoletoUtil {

	
	private final Boleto[] boletos;
	
	public GeradorBoletoUtil(Boleto... boletos) {
		this.boletos = boletos;
	}
	
	/**
	 * Devolve um array de bytes representando o PDF desse boleto ja gerado.
	 */
	public byte[] geraPDF() {
		return gera(new PDFBoletoWriterUtil());
	}
	
	/**
	 * Devolve o array de bytes do boleto escrito pelo writer indicado.
	 * 
	 * @param writer
	 * @return
	 */
	private byte[] gera(BoletoWriter writer) {
		BoletoTransformerUtil transformer = new BoletoTransformerUtil(writer);

		InputStream is = transformer.transform(this.boletos);

		byte[] b;
		try {
			b = new byte[is.available()];
			is.read(b);

		} catch (NumberFormatException e) {
			throw new GeracaoBoletoException("Erro na geração do boleto em HTML", e);
		} catch (IOException e) {
			throw new GeracaoBoletoException("Erro na geração do boleto em HTML", e);
		} finally {
			//tentaFecharOFileOutput(is);
		}
		return b;
	}
}

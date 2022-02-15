package com.digital.opuserp.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="venda_servico_cabecalho")
public class VendaServicoCabecalho {

		@Id
		@GeneratedValue(strategy=GenerationType.AUTO)
		@Column(name="ID", unique=true)
		private Integer ID;
		@Column(name="EMPRESA_ID")
		private Integer EMPRESA_ID;
		
		@Column(name="CLIENTES_ID")
		private Integer CLIENTES_ID;
		
		@Column(name="FORMAS_PAGAMENTO_ID")
		private Integer FORMAS_PAGAMENTO_ID;
		
		@Column(name="OSP_ID")
		private Integer OSP_ID;
		
		@Column(name="DATA_VENDA")
		private Date DATA_VENDA;
		
		@Column(name="HORA_VENDA")
		private Date HORA_VENDA;
		
		@Column(name="SITUACAO")
		private String SITUACAO;
		
		@Column(name="TOTAL_DESC")
		private Float TOTAL_DESC;
		
		@Column(name="TOTAL_ACREC")
		private Float TOTAL_ACREC;
		
		@Column(name="SUBTOTAL")
		private Float SUBTOTAL;
		
		@Column(name="VALOR_TOTAL")
		private Float VALOR_TOTAL;
		
		@Column(name="VENDEDOR")
		private String VENDEDOR;
		
		public VendaServicoCabecalho(){
			
		}

		public VendaServicoCabecalho(Integer iD, Integer eMPRESA_ID,
				Integer cLIENTES_ID, Integer fORMAS_PAGAMENTO_ID,
				Integer oSP_ID, Date dATA_VENDA, Date hORA_VENDA,
				String sITUACAO, Float tOTAL_DESC, Float tOTAL_ACREC,
				Float sUBTOTAL, Float vALOR_TOTAL, String vENDEDOR) {
			super();
			ID = iD;
			EMPRESA_ID = eMPRESA_ID;
			CLIENTES_ID = cLIENTES_ID;
			FORMAS_PAGAMENTO_ID = fORMAS_PAGAMENTO_ID;
			OSP_ID = oSP_ID;
			DATA_VENDA = dATA_VENDA;
			HORA_VENDA = hORA_VENDA;
			SITUACAO = sITUACAO;
			TOTAL_DESC = tOTAL_DESC;
			TOTAL_ACREC = tOTAL_ACREC;
			SUBTOTAL = sUBTOTAL;
			VALOR_TOTAL = vALOR_TOTAL;
			VENDEDOR = vENDEDOR;
		}

		public Integer getID() {
			return ID;
		}

		public void setID(Integer iD) {
			ID = iD;
		}

		public Integer getEMPRESA_ID() {
			return EMPRESA_ID;
		}

		public void setEMPRESA_ID(Integer eMPRESA_ID) {
			EMPRESA_ID = eMPRESA_ID;
		}

		public Integer getCLIENTES_ID() {
			return CLIENTES_ID;
		}

		public void setCLIENTES_ID(Integer cLIENTES_ID) {
			CLIENTES_ID = cLIENTES_ID;
		}

		public Integer getFORMAS_PAGAMENTO_ID() {
			return FORMAS_PAGAMENTO_ID;
		}

		public void setFORMAS_PAGAMENTO_ID(Integer fORMAS_PAGAMENTO_ID) {
			FORMAS_PAGAMENTO_ID = fORMAS_PAGAMENTO_ID;
		}

		public Integer getOSP_ID() {
			return OSP_ID;
		}

		public void setOSP_ID(Integer oSP_ID) {
			OSP_ID = oSP_ID;
		}

		public Date getDATA_VENDA() {
			return DATA_VENDA;
		}

		public void setDATA_VENDA(Date dATA_VENDA) {
			DATA_VENDA = dATA_VENDA;
		}

		public Date getHORA_VENDA() {
			return HORA_VENDA;
		}

		public void setHORA_VENDA(Date hORA_VENDA) {
			HORA_VENDA = hORA_VENDA;
		}

		public String getSITUACAO() {
			return SITUACAO;
		}

		public void setSITUACAO(String sITUACAO) {
			SITUACAO = sITUACAO;
		}

		public Float getTOTAL_DESC() {
			return TOTAL_DESC;
		}

		public void setTOTAL_DESC(Float tOTAL_DESC) {
			TOTAL_DESC = tOTAL_DESC;
		}

		public Float getTOTAL_ACREC() {
			return TOTAL_ACREC;
		}

		public void setTOTAL_ACREC(Float tOTAL_ACREC) {
			TOTAL_ACREC = tOTAL_ACREC;
		}

		public Float getSUBTOTAL() {
			return SUBTOTAL;
		}

		public void setSUBTOTAL(Float sUBTOTAL) {
			SUBTOTAL = sUBTOTAL;
		}

		public Float getVALOR_TOTAL() {
			return VALOR_TOTAL;
		}

		public void setVALOR_TOTAL(Float vALOR_TOTAL) {
			VALOR_TOTAL = vALOR_TOTAL;
		}

		public String getVENDEDOR() {
			return VENDEDOR;
		}

		public void setVENDEDOR(String vENDEDOR) {
			VENDEDOR = vENDEDOR;
		}
		
}

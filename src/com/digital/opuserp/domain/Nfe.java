package com.digital.opuserp.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="nfe")
public class Nfe {

		@Id
		@Column(name="ID", nullable=false)
		@GeneratedValue(strategy=GenerationType.AUTO)
		Integer id;		
		@Column(name="EMPRESA_ID")
		Integer empresa_id;		
		@Column(name="MODELO")
		String modelo;
		@Column(name="SERIE")
		String serie;
		@Column(name="NUMERO")
		String numero;
		@Column(name="DATA_EMISSAO")
		@Temporal(TemporalType.TIMESTAMP)
		Date data_emissao;
		@Column(name="TIPO_DOCUMENTO")
		String tipo_documento;
		@Column(name="FORMA_PAGAMENTO")
		String forma_pagamento;
		@Column(name="FORMA_EMISSAO")
		String forma_emissao;
		@Column(name="FINALIDADE_EMISSAO")
		String finalidade_emissao;
		@Column(name="CONSUMIDOR")
		String consumidor_final;
		@Column(name="DESTINO_OPERACAO")
		String destino_operacao;
		@Column(name="TIPO_ATENDIMENTO")
		String tipo_atendimento;		
		@Column(name="UF")
		String uf;
		@Column(name="MUNICIPIO")
		String municipio;
		@OneToOne
		@JoinColumn(name="PEDIDO")
		EcfPreVendaCabecalho pedido;
		
		@OneToOne
		@JoinColumn(name="NATUREZA_OPERACAO")
		NaturezaOperacao naturezaOperacao;
		
		public Nfe(){
			
		}

		public Nfe(Integer id, String modelo, String serie, String numero,
				Date data_emissao, String tipo_documento,
				String forma_pagamento, String forma_emissao,
				String finalidade_emissao, String consumidor_final,
				String destino_operacao, String tipo_atendimento,
				String uf, String municipio,
				EcfPreVendaCabecalho pedido, NaturezaOperacao naturezaOperacao) {
			super();
			this.id = id;
			this.modelo = modelo;
			this.serie = serie;
			this.numero = numero;
			this.data_emissao = data_emissao;
			this.tipo_documento = tipo_documento;
			this.forma_pagamento = forma_pagamento;
			this.forma_emissao = forma_emissao;
			this.finalidade_emissao = finalidade_emissao;
			this.consumidor_final = consumidor_final;
			this.destino_operacao = destino_operacao;
			this.tipo_atendimento = tipo_atendimento;			
			this.uf = uf;
			this.municipio = municipio;
			this.pedido = pedido;
			this.naturezaOperacao = naturezaOperacao;
		}

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public String getModelo() {
			return modelo;
		}

		public void setModelo(String modelo) {
			this.modelo = modelo;
		}

		public String getSerie() {
			return serie;
		}

		public void setSerie(String serie) {
			this.serie = serie;
		}

		public String getNumero() {
			return numero;
		}

		public void setNumero(String numero) {
			this.numero = numero;
		}

		public Date getData_emissao() {
			return data_emissao;
		}

		public void setData_emissao(Date data_emissao) {
			this.data_emissao = data_emissao;
		}

		public String getTipo_documento() {
			return tipo_documento;
		}

		public void setTipo_documento(String tipo_documento) {
			this.tipo_documento = tipo_documento;
		}

		public String getForma_pagamento() {
			return forma_pagamento;
		}

		public void setForma_pagamento(String forma_pagamento) {
			this.forma_pagamento = forma_pagamento;
		}

		public String getForma_emissao() {
			return forma_emissao;
		}

		public void setForma_emissao(String forma_emissao) {
			this.forma_emissao = forma_emissao;
		}

		public String getFinalidade_emissao() {
			return finalidade_emissao;
		}

		public void setFinalidade_emissao(String finalidade_emissao) {
			this.finalidade_emissao = finalidade_emissao;
		}

		public String getConsumidor_final() {
			return consumidor_final;
		}

		public void setConsumidor_final(String consumidor_final) {
			this.consumidor_final = consumidor_final;
		}

		public String getDestino_operacao() {
			return destino_operacao;
		}

		public void setDestino_operacao(String destino_operacao) {
			this.destino_operacao = destino_operacao;
		}

		public String getTipo_atendimento() {
			return tipo_atendimento;
		}

		public void setTipo_atendimento(String tipo_atendimento) {
			this.tipo_atendimento = tipo_atendimento;
		}

		public String getUf() {
			return uf;
		}

		public void setUf(String uf) {
			this.uf = uf;
		}

		public String getMunicipio() {
			return municipio;
		}

		public void setMunicipio(String municipio) {
			this.municipio = municipio;
		}

		public EcfPreVendaCabecalho getPedido() {
			return pedido;
		}

		public void setPedido(EcfPreVendaCabecalho pedido) {
			this.pedido = pedido;
		}

		public NaturezaOperacao getNaturezaOperacao() {
			return naturezaOperacao;
		}

		public void setNaturezaOperacao(NaturezaOperacao naturezaOperacao) {
			this.naturezaOperacao = naturezaOperacao;
		}

		public Integer getEmpresa_id() {
			return empresa_id;
		}

		public void setEmpresa_id(Integer empresa_id) {
			this.empresa_id = empresa_id;
		}
		
		

				
}

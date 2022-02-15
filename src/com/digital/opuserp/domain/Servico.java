package com.digital.opuserp.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import com.digital.opuserp.OpusERP4UI;

@Entity
@Table(name="servicos")
public class Servico {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID", unique=true, nullable=false)
	private Integer id;
	
	@Column(name="EMPRESA_ID", nullable=false)
	private Integer empresa_id;
	
	@Column(name="CODIGO_INTERNO", nullable=true)
	private String codigo_interno;
	
	@Column(name="NOME")
	private String nome;	
	
	@Column(name="DESCRICAO")
	private String descricao;
	
	@Column(name="VALOR_CUSTO")
	private String valor_custo;
	
	@Column(name="VALOR_VENDA")
	private String valor_venda;
	
	@Column(name="VALOR_LUCRO")
	private String valor_lucro;
	
	@Column(name="PERC_LUCRO")
	private String perc_lucro;
	
	@Column(name="FRACIONAR")
	private Integer fracionar;
	
	@Column(name="DATA_CADASTRO")
	private Date data_cadastro;
	
	@Column(name="STATUS")
	private String status;
	
	@Column(name="PRODUCAO")
	private String producao;
	
	@OneToOne
	@JoinColumn(name="grupo")
	private GrupoServico grupo;
	
	@Column(name="COD_FUNC_ULT_ALTER")
	private Integer usuario;
	
	@Column(name="OUTRAS_TB_DESC", nullable=true)
	private String	outras_tb_desc;
	
	@PrePersist
	private void onInsert(){
		data_cadastro = new Date();
		usuario = OpusERP4UI.getUsuarioLogadoUI().getId();
	}
	
	@PreUpdate
	private void onUpdate(){
		usuario = OpusERP4UI.getUsuarioLogadoUI().getId();
	}
	
	public Servico(Integer id){
		this.id = id;
	}
	
	public Servico(){
		
	}

	public Servico(Integer id, Integer empresa_id, String codigo_interno,
			String nome, String descricao, String valor_custo,
			String valor_venda, String valor_lucro, String perc_lucro,
			Integer fracionar, Date data_cadastro, String status) {
		super();
		this.id = id;
		this.empresa_id = empresa_id;
		this.codigo_interno = codigo_interno;
		this.nome = nome;
		this.descricao = descricao;
		this.valor_custo = valor_custo;
		this.valor_venda = valor_venda;
		this.valor_lucro = valor_lucro;
		this.perc_lucro = perc_lucro;
		this.fracionar = fracionar;
		this.data_cadastro = data_cadastro;
		this.status = status;		
	}

	public Servico(Integer id, Integer empresa_id, String codigo_interno,
			String nome, String descricao, String valor_custo,
			String valor_venda, String valor_lucro, String perc_lucro,
			Integer fracionar, Date data_cadastro, String status,String outras_tb_desc) {
		super();
		this.id = id;
		this.empresa_id = empresa_id;
		this.codigo_interno = codigo_interno;
		this.nome = nome;
		this.descricao = descricao;
		this.valor_custo = valor_custo;
		this.valor_venda = valor_venda;
		this.valor_lucro = valor_lucro;
		this.perc_lucro = perc_lucro;
		this.fracionar = fracionar;
		this.data_cadastro = data_cadastro;
		this.status = status;	
		this.outras_tb_desc = outras_tb_desc;
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getEmpresa_id() {
		return empresa_id;
	}

	public void setEmpresa_id(Integer empresa_id) {
		this.empresa_id = empresa_id;
	}

	public String getCodigo_interno() {
		return codigo_interno;
	}

	public void setCodigo_interno(String codigo_interno) {
		this.codigo_interno = codigo_interno;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getValor_custo() {
		return valor_custo;
	}

	public void setValor_custo(String valor_custo) {
		this.valor_custo = valor_custo;
	}

	public String getValor_venda() {
		return valor_venda;
	}

	public void setValor_venda(String valor_venda) {
		this.valor_venda = valor_venda;
	}

	public String getValor_lucro() {
		return valor_lucro;
	}

	public void setValor_lucro(String valor_lucro) {
		this.valor_lucro = valor_lucro;
	}

	public String getPerc_lucro() {
		return perc_lucro;
	}

	public void setPerc_lucro(String perc_lucro) {
		this.perc_lucro = perc_lucro;
	}

	public Integer getFracionar() {
		return fracionar;
	}

	public void setFracionar(Integer fracionar) {
		this.fracionar = fracionar;
	}

	public Date getData_cadastro() {
		return data_cadastro;
	}

	public void setData_cadastro(Date data_cadastro) {
		this.data_cadastro = data_cadastro;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public GrupoServico getGrupo() {
		return grupo;
	}

	public void setGrupo(GrupoServico grupo) {
		this.grupo = grupo;
	}

	public String getProducao() {
		return producao;
	}

	public void setProducao(String producao) {
		this.producao = producao;
	}

	public Integer getUsuario() {
		return usuario;
	}

	public void setUsuario(Integer usuario) {
		this.usuario = usuario;
	}

	public String getOutras_tb_desc() {
		return outras_tb_desc;
	}

	public void setOutras_tb_desc(String outras_tb_desc) {
		this.outras_tb_desc = outras_tb_desc;
	}
	
	
	
}

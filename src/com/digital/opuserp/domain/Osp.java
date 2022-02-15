package com.digital.opuserp.domain;

import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Null;

@Entity
@Table(name="osp")
@Cacheable(value=false)
public class Osp {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID", nullable=false, unique=true)
	private Integer id;
	@Column(name="EMPRESA_ID", nullable=false)
	private Integer empresa_id;
	
	@OneToOne
	@JoinColumn(name="CLIENTES_ID", nullable=true)
	private Cliente cliente;
	
	@Transient
	private String cliente_nome;
	
	@Column(name="VENDA_SERVICO_CABECALHO_ID", nullable=true)
	private Integer venda_servico_cabecalho_id;
	@OneToOne
	@JoinColumn(name="SERVICO_ID", nullable=true)
	private Servico servico;
	@Column(name="DESCRICAO_SERVICO", length=200)
	private String descricao_servico;
	@Column(name="QTD_SERVICO")
	private Double qtd_servico;
	@Column(name="OBSERVACAO", columnDefinition="text")	
	private String observacao;
	@Column(name="ORDEM")
	private Integer ordem;
	@Column(name="IMAGEM", length=50)
	private String imagem;
	@Temporal(TemporalType.DATE)
	@Column(name="DATA_AGENDADO")
	private Date data_agendado;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATA_PREVISAO_TERMINO")
	private Date data_previsao_termino;
	@Temporal(TemporalType.TIME)
	@Column(name="HORA_PREVISAO")
	private Date hora_previsao;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATA_TERMINO")
	private Date data_termino; 
	@Temporal(TemporalType.DATE)
	@Column(name="DATA_ENCAMINHADO")
	private Date data_encaminhado; 
	@Column(name="OPERADOR_ABERTURA", length=50)
	private String operador_abertura;
	@Column(name="OPERADOR_PRODUCAO", length=50)
	private String operador_producao;
	@Column(name="ENTREGAR")
	private boolean entregar;
	@Column(name="MOTIVO_CANCELAMENTO", length=100)
	private String motivo_cancelamento;
	@Column(name="STATUS", length=1)
	private String status;
	@Column(name="SETOR")
	private String setor;
	@Column(name="COMPRADOR")
	private String comprador;
	
	
	
	@Lob
    private byte[] img;
	 
	public Osp(){
		
	}
	
	
	@Transient
	private String coluna;
	@Transient
	private Date coluna_date;
	@Transient
	private Integer coluna_Inter;
	@Transient
	private Long qtd;
	
	
	public Osp(String coluna, Long qtd) {		
		this.coluna = coluna;
		this.qtd = qtd;
	}
	
	public Osp(Date coluna_date, Long qtd) {		
		this.coluna_date = coluna_date;
		this.qtd = qtd;
	}

	public Osp(Integer coluna_Inter, Long qtd) {		
		this.coluna_Inter = coluna_Inter;
		this.qtd = qtd;
	}
	
	
	public String getColuna() {
		return coluna;
	}

	public void setColuna(String coluna) {
		this.coluna = coluna;
	}
	
	public Date getColuna_date() {
		return coluna_date;
	}

	public void setColuna_date(Date coluna) {
		this.coluna_date = coluna;
	}

	
	public Integer getColuna_Inter() {
		return coluna_Inter;
	}

	public void setColuna_Inter(Integer coluna_Inter) {
		this.coluna_Inter = coluna_Inter;
	}

	public Long getQtd() {
		return qtd;
	}

	public void setQtd(Long qtd) {
		this.qtd = qtd;
	}

	public Osp(Integer id){
		this.id = id;
	}
	
	
	public Osp(Integer id, Integer empresa_id, Cliente cliente,
			Integer venda_servico_cabecalho_id, Servico servico_id,
			String descricao_servico, Double qtd_servico, String observacao,
			Integer ordem, String imagem, Date data_agendado,
			Date data_previsao_termino, Date hora_previsao, Date data_termino,
			Date data_encaminhado, String operador_abertura,
			String operador_producao, boolean entregar,
			String motivo_cancelamento, String status) {
		super();
		this.id = id;
		this.empresa_id = empresa_id;
		this.cliente = cliente;
		this.venda_servico_cabecalho_id = venda_servico_cabecalho_id;
		this.servico = servico_id;
		this.descricao_servico = descricao_servico;
		this.qtd_servico = qtd_servico;
		this.observacao = observacao;
		this.ordem = ordem;
		this.imagem = imagem;
		this.data_agendado = data_agendado;
		this.data_previsao_termino = data_previsao_termino;
		this.hora_previsao = hora_previsao;
		this.data_termino = data_termino;
		this.data_encaminhado = data_encaminhado;
		this.operador_abertura = operador_abertura;
		this.operador_producao = operador_producao;
		this.entregar = entregar;
		this.motivo_cancelamento = motivo_cancelamento;
		this.status = status;
	}
	
	public Osp(Integer id, Integer empresa_id, Cliente cliente,
			Integer venda_servico_cabecalho_id, Servico servico_id,
			String descricao_servico, Double qtd_servico, String observacao,
			Integer ordem, String imagem, Date data_agendado,
			Date data_previsao_termino, Date hora_previsao, Date data_termino,
			Date data_encaminhado, String operador_abertura,
			String operador_producao, boolean entregar,
			String motivo_cancelamento, String status, String setor, String comprador) {
		super();
		this.id = id;
		this.comprador = comprador;
		this.empresa_id = empresa_id;
		this.cliente = cliente;
		this.venda_servico_cabecalho_id = venda_servico_cabecalho_id;
		this.servico = servico_id;
		this.descricao_servico = descricao_servico;
		this.qtd_servico = qtd_servico;
		this.observacao = observacao;
		this.ordem = ordem;
		this.imagem = imagem;
		this.data_agendado = data_agendado;
		this.data_previsao_termino = data_previsao_termino;
		this.hora_previsao = hora_previsao;
		this.data_termino = data_termino;
		this.data_encaminhado = data_encaminhado;
		this.operador_abertura = operador_abertura;
		this.operador_producao = operador_producao;
		this.entregar = entregar;
		this.motivo_cancelamento = motivo_cancelamento;
		this.status = status;
		this.setor = setor;
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

	public Cliente getCliente() {
		return cliente ;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public Integer getVenda_servico_cabecalho_id() {
		return venda_servico_cabecalho_id;
	}

	public void setVenda_servico_cabecalho_id(Integer venda_servico_cabecalho_id) {
		this.venda_servico_cabecalho_id = venda_servico_cabecalho_id;
	}

	public Servico getServico() {
		return servico;
	}

	public void setServico_id(Servico servico) {
		this.servico = servico;
	}

	public String getDescricao_servico() {
		return descricao_servico;
	}

	public void setDescricao_servico(String descricao_servico) {
		this.descricao_servico = descricao_servico;
	}

	public Double getQtd_servico() {
		return qtd_servico;
	}

	public void setQtd_servico(Double qtd_servico) {
		this.qtd_servico = qtd_servico;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public Integer getOrdem() {
		return ordem;
	}

	public void setOrdem(Integer ordem) {
		this.ordem = ordem;
	}

	public String getImagem() {
		return imagem;
	}

	public void setImagem(String imagem) {
		this.imagem = imagem;
	}

	public Date getData_agendado() {
		return data_agendado;
	}

	public void setData_agendado(Date data_agendado) {
		this.data_agendado = data_agendado;
	}

	public Date getData_previsao_termino() {
		return data_previsao_termino;
	}

	public void setData_previsao_termino(Date data_previsao_termino) {
		this.data_previsao_termino = data_previsao_termino;
	}

	public Date getHora_previsao() {
		return hora_previsao;
	}

	public void setHora_previsao(Date hora_previsao) {
		this.hora_previsao = hora_previsao;
	}

	public Date getData_termino() {
		return data_termino;
	}

	public void setData_termino(Date data_termino) {
		this.data_termino = data_termino;
	}

	public Date getData_encaminhado() {
		return data_encaminhado;
	}

	public void setData_encaminhado(Date data_encaminhado) {
		this.data_encaminhado = data_encaminhado;
	}

	public String getOperador_abertura() {
		return operador_abertura;
	}

	public void setOperador_abertura(String operador_abertura) {
		this.operador_abertura = operador_abertura;
	}

	public String getOperador_producao() {
		return operador_producao;
	}

	public void setOperador_producao(String operador_producao) {
		this.operador_producao = operador_producao;
	}

	public boolean isEntregar() {
		return entregar;
	}

	public void setEntregar(boolean entregar) {
		this.entregar = entregar;
	}

	public String getMotivo_cancelamento() {
		return motivo_cancelamento;
	}

	public void setMotivo_cancelamento(String motivo_cancelamento) {
		this.motivo_cancelamento = motivo_cancelamento;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSetor() {
		return setor;
	}

	public void setSetor(String setor) {
		this.setor = setor;
	}

	public void setServico(Servico servico) {
		this.servico = servico;
	}

	public byte[] getImg() {
		return img;
	}

	public void setImg(byte[] img) {
		this.img = img;
	}

	public String getComprador() {
		return comprador;
	}

	public void setComprador(String comprador) {
		this.comprador = comprador;
	}

	public String getCliente_nome() {
		return getCliente() != null ? getCliente().getNome_razao() : "";
	}

	public void setCliente_nome(String cliente_nome) {
		this.cliente_nome = cliente_nome;
	}


	
}

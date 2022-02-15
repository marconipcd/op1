package com.digital.opuserp.domain;

import java.util.Date;

import javax.persistence.Cacheable;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@Entity
@Table(name="clientes")
@Cacheable(value=false)
public class Cliente {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID",unique=true, nullable=false, length=10, columnDefinition="SMALLINT(4) UNSIGNED ZEROFILL") 
	private Integer id;	//ok
	@Column(name="DOC_CPF_CNPJ", unique=false, nullable=false)
	private String doc_cpf_cnpj; //ok
	@Column(name="DOC_RG_INSC_ESTADUAL", unique=false, nullable=false)
	private String doc_rg_insc_estadual; //ok
	@Column(name="ORGAO_EMISSOR_RG")
	private String orgao_emissor_rg;
	@Column(name="TRATAMENTO", unique=false, nullable=false)
	private String tratamento; //ok
	
	@Column(name="NOME_RAZAO", unique=false, nullable=false)
	private String nome_razao; //ok
	@Column(name="COMO_QUER_SER_CHAMADO", nullable=true, unique=false)
	private String como_quer_ser_chamado;
	
	
	@Column(name="NOME_FANTASIA", unique=false, nullable=true)
	private String nome_fantasia; //ok
	@Column(name="CONTATO",unique=false, nullable=false)
	private String contato; //ok
	@Column(name="SEXO",unique=false, nullable=false)
	private String sexo; //ok
	
	@Temporal(value=TemporalType.DATE)
	@Column(name="DATA_NASCIMENTO",unique=false, nullable=false)
	private Date data_nascimento; //ok
		
	@Column(name="TELEFONE1",unique=false, nullable=false)
	private String telefone1; //ok
	@Column(name="TELEFONE2",unique=false, nullable=true)
	private String telefone2; //ok
	@Column(name="CELULAR1",unique=false, nullable=true)
	private String celular1; //ok
	@Column(name="CELULAR2",unique=false, nullable=true)
	private String celular2; 
	@Column(name="WHATSAPP",unique=false, nullable=true)
	private String whatsapp; 
	
	@Column(name="EMAIL",unique=false, nullable=true)
	private String email; //ok
	@Column(name="SENHA",unique=false, nullable=true)
	private String senha; 
	@Column(name="MSN",unique=false, nullable=true)
	private String msn; //ok
	@OneToOne
	@JoinColumn(name="COMO_NOS_CONHECEU_ID")
	private ComoNosConheceu como_nos_conheceu; //ok
	@Column(name="OBS",unique=false, nullable=true)
	private String obs; //ok
	@Column(name="FORMA_PGTO_PADRAO",unique=false, nullable=true)
	private String forma_pgto_padrao; //ok
	@Column(name="ORIGEM",unique=false, nullable=true)
	private String origem; //ok
	@Column(name="STATUS_2",unique=false, nullable=false)
	private String status; //ok
	@Column(name="TIPO_PESSOA", nullable=true, unique=false)
	private String tipo_pessoa;
	@Column(name="TRANSPORTADORA_PADRAO", nullable=true, unique=false, length=100)
	private String transportadora_padrao;
	@Column(name="EMAIL_ALTERNATIVO", nullable=true, unique=false, length=100)
	private String emailAlternativo;
	@Column(name="SITE", nullable=true, unique=false, length=100)
	private String site;
	@Column(name="REDE_SOCIAL", nullable=true, unique=false, length=100)
	private String rede_social;
	@Column(name="DDD_FONE1", nullable=true, unique=false, length=50)
	private String ddd_fone1;
	@Column(name="DDD_FONE2", nullable=true, unique=false, length=50)
	private String ddd_fone2;
	@Column(name="DDD_CEL1", nullable=true, unique=false, length=50)
	private String ddd_cel1;
	@Column(name="DDD_CEL2", nullable=true, unique=false, length=50)
	private String ddd_cel2;	
	
	@Column(name="PROFISSAO", nullable=true, unique=false, length=50)
	private String profissao;	
	
	
	
	@Column(name="ALUNO", nullable=true)
	private String aluno;	
	
	@Column(name="CURSO", nullable=true)
	private String curso;	
	
	@Column(name="HORARIO_CURSO", nullable=true)
	private String horario_curso;	
	
	@Column(name="TITULAR_CONTRATO", nullable=true)
	private Integer titular_contrato;	
	
	@Column(name="OPERADOR_CONTATO", nullable=true)
	private String operador_contato;	
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATA_HORA_CONTATO", nullable=true)
	private Date data_hora_contato;	
	
	
	@OneToOne
	@JoinColumn(name="TABELAS_PRECO_ID", nullable=true, unique=false)
	private TabelasPreco tabela_preco;

	@OneToOne
	@JoinColumn(name="EMPRESA_ID")
	private Empresa empresa;
	
	@Column(name="EMPRESA_ANTERIOR")
	private Integer empresa_anterior;
	
	@OneToOne
	@JoinColumn(name="CATEGORIAS_ID")
	private Categorias categoria;
	
	@OneToOne
	@JoinColumn(name="ENDERECO_ID", nullable=true)
	private Endereco endereco_principal;
		
//	@OneToOne(mappedBy="clientes")
//	private Endereco endereco;
	
//	@OneToOne(mappedBy="clientes")
//	private List<Fiadores> fiadores;
	
	@Temporal(value=TemporalType.TIMESTAMP)
    @Column(name="DATA_CADASTRO", nullable = false)
    private Date data_cadastro;   
    @Temporal(value=TemporalType.TIMESTAMP)
    @Column(name="DATA_ALTERACAO", nullable = false)
    private Date data_alteracao;
    @Column(name="AGENDAR_CRM", nullable = false)
    private String agendar_crm;
    
    @Column(name="OPERADOR_CADASTRO", nullable=false)
    private String operador_cadastro;
    
    @Column(name="CREDITO_CLIENTE")
    private String credito_cliente;
    
    @Column(name="BLACK_LIST")
    private String black_list;
    
    @Column(name="BLACK_LIST_OBS")
    private String black_list_obs;
    
    @OneToOne
    @JoinColumn(name="INDICADO_POR", nullable=true)
    private Cliente indicado_por;
    
    
//    @OneToMany(mappedBy="cliente")    
//    private List<ContasReceber> contas_receber;
    
    @PrePersist
    protected void onCreate(){
    	data_alteracao = data_cadastro = new Date();
    }   
    @PreUpdate
    protected void onUpdate(){
    	data_alteracao = new Date();
    }

	
	
	public Cliente(){
		
	}
	
	@Transient
	private String coluna;
	@Transient
	private Date coluna_date;
	@Transient
	private Long qtd;
	
	public Cliente(String coluna, Long qtd) {		
		this.coluna = coluna;
		this.qtd = qtd;
	}
	
	public Cliente(Date coluna_date, Long qtd) {		
		this.coluna_date = coluna_date;
		this.qtd = qtd;
	}
	
	public Cliente( Long qtd) {		
		//this.coluna = coluna;
		this.qtd = qtd;
	}
	
	
	public Date getColuna_date() {
		return coluna_date;
	}
	public void setColuna_date(Date coluna_date) {
		this.coluna_date = coluna_date;
	}
	public String getColuna() {
		return coluna;
	}

	public void setColuna(String coluna) {
		this.coluna = coluna;
	}

	public Long getQtd() {
		return qtd;
	}

	public void setQtd(Long qtd) {
		this.qtd = qtd;
	}

	public Cliente(Integer id) {
		super();
		this.id = id;
	}
	
	public Cliente(Integer id, String status) {		
		this.id = id;
		this.status = status;
	}
	
	public Cliente(Integer id, String status, String nome_razao) {		
		this.id = id;
		this.status = status;
		this.nome_razao =  nome_razao;
	}
	

	public Cliente(Integer id,Empresa empresa, String doc_cpf_cnpj,
			String doc_rg_insc_estadual, String tratamento, String nome_razao,String contato,String ddd1, 
			String ddd2, String ddd3, String ddd4,String telefone1, String telefone2,
			String celular1, String celular2, String email, String msn) {
		super();
		this.id = id;
		this.empresa = empresa;
		this.doc_cpf_cnpj = doc_cpf_cnpj;
		this.doc_rg_insc_estadual = doc_rg_insc_estadual;
		this.tratamento = tratamento;
		this.nome_razao = nome_razao;
		this.contato = contato;
		this.ddd_fone1 = ddd1;
		this.ddd_fone2  =ddd2;
		this.ddd_cel1 = ddd3;
		this.ddd_cel2 = ddd4;
		this.telefone1 = telefone1;
		this.telefone2 = telefone2;
		this.celular1 = celular1;
		this.celular2 = celular2;
		this.email = email;
		this.msn = msn;
	}
	
	public Cliente(Integer id, String doc_cpf_cnpj,
			String doc_rg_insc_estadual, String tratamento, String nome_razao,
			String nome_fantasia, String contato, String sexo,
			Date data_nascimento, String telefone1, String telefone2,
			String celular1, String celular2, String email, String msn,
			ComoNosConheceu como_nos_conheceu, String obs, String forma_pgto_padrao,
			String origem, String status, String tipo_pessoa,
			String transportadora_padrao, String emailAlternativo, String site,
			String ddd_fone1, String ddd_fone2, String ddd_cel1,
			String ddd_cel2, String como_quer_ser_chamado,
			TabelasPreco tabela_preco_id, Endereco endereco_id, Empresa empresa,
			Categorias categoria, Date data_cadastro, Date data_alteracao) {
		super();
		this.id = id;
		this.doc_cpf_cnpj = doc_cpf_cnpj;
		this.doc_rg_insc_estadual = doc_rg_insc_estadual;
		this.tratamento = tratamento;
		this.nome_razao = nome_razao;
		this.nome_fantasia = nome_fantasia;
		this.contato = contato;
		this.sexo = sexo;
		this.data_nascimento = data_nascimento;
		this.telefone1 = telefone1;
		this.telefone2 = telefone2;
		this.celular1 = celular1;
		this.celular2 = celular2;
		this.email = email;
		this.msn = msn;
		this.como_nos_conheceu = como_nos_conheceu;
		this.obs = obs;
		this.forma_pgto_padrao = forma_pgto_padrao;
		this.origem = origem;
		this.status = status;
		this.tipo_pessoa = tipo_pessoa;
		this.transportadora_padrao = transportadora_padrao;
		this.emailAlternativo = emailAlternativo;
		this.site = site;
		this.ddd_fone1 = ddd_fone1;
		this.ddd_fone2 = ddd_fone2;
		this.ddd_cel1 = ddd_cel1;
		this.ddd_cel2 = ddd_cel2;
		this.como_quer_ser_chamado = como_quer_ser_chamado;
		this.tabela_preco= tabela_preco_id;
		this.empresa = empresa;
		this.categoria = categoria;
		this.data_cadastro = data_cadastro;
		this.data_alteracao = data_alteracao;
	}
	
	public String getComo_quer_ser_chamado() {
		return como_quer_ser_chamado;
	}
	public void setComo_quer_ser_chamado(String como_quer_ser_chamado) {
		this.como_quer_ser_chamado = como_quer_ser_chamado;
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDoc_cpf_cnpj() {
		return doc_cpf_cnpj;
	}

	public void setDoc_cpf_cnpj(String doc_cpf_cnpj) {
		this.doc_cpf_cnpj = doc_cpf_cnpj;
	}

	public String getDoc_rg_insc_estadual() {
		return doc_rg_insc_estadual;
	}

	public void setDoc_rg_insc_estadual(String doc_rg_insc_estadual) {
		this.doc_rg_insc_estadual = doc_rg_insc_estadual;
	}

	public String getTratamento() {
		return tratamento;
	}

	public void setTratamento(String tratamento) {
		this.tratamento = tratamento;
	}

	public String getNome_razao() {
		return nome_razao;
	}

	public void setNome_razao(String nome_razao) {
		this.nome_razao = nome_razao;
	}

	public String getNome_fantasia() {
		return nome_fantasia;
	}

	public void setNome_fantasia(String nome_fantasia) {
		this.nome_fantasia = nome_fantasia;
	}

	public String getContato() {
		return contato;
	}

	public void setContato(String contato) {
		this.contato = contato;
	}

	public String getSexo() {
		return sexo;
	}

	public void setSexo(String sexo) {
		this.sexo = sexo;
	}

	public Date getData_nascimento() {
		return data_nascimento;
	}

	public void setData_nascimento(Date data_nascimento) {
		this.data_nascimento = data_nascimento;
	}

	public String getTelefone1() {
		return telefone1;
	}

	public void setTelefone1(String telefone1) {
		this.telefone1 = telefone1;
	}

	public String getTelefone2() {
		return telefone2;
	}

	public void setTelefone2(String telefone2) {
		this.telefone2 = telefone2;
	}

	public String getCelular1() {
		return celular1;
	}

	public void setCelular1(String celular1) {
		this.celular1 = celular1;
	}

	public String getCelular2() {
		return celular2;
	}

	public void setCelular2(String celular2) {
		this.celular2 = celular2;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMsn() {
		return msn;
	}

	public void setMsn(String msn) {
		this.msn = msn;
	}

	public ComoNosConheceu getComo_nos_conheceu() {
		return como_nos_conheceu;
	}

	public void setComo_nos_conheceu(ComoNosConheceu como_nos_conheceu) {
		this.como_nos_conheceu = como_nos_conheceu;
	}

	public String getObs() {
		return obs;
	}

	public void setObs(String obs) {
		this.obs = obs;
	}

	public String getForma_pgto_padrao() {
		return forma_pgto_padrao;
	}

	public void setForma_pgto_padrao(String forma_pgto_padrao) {
		this.forma_pgto_padrao = forma_pgto_padrao;
	}

	public String getOrigem() {
		return origem;
	}

	public void setOrigem(String origem) {
		this.origem = origem;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTipo_pessoa() {
		return tipo_pessoa;
	}

	public void setTipo_pessoa(String tipo_pessoa) {
		this.tipo_pessoa = tipo_pessoa;
	}

	public String getTransportadora_padrao() {
		return transportadora_padrao;
	}

	public void setTransportadora_padrao(String transportadora_padrao) {
		this.transportadora_padrao = transportadora_padrao;
	}

	public Date getData_cadastro() {
		return data_cadastro;
	}

	public void setData_cadastro(Date data_cadastro) {
		this.data_cadastro = data_cadastro;
	}

	public Date getData_alteracao() {
		return data_alteracao;
	}

	public void setData_alteracao(Date data_alteracao) {
		this.data_alteracao = data_alteracao;
	}

	public String getEmailAlternativo() {
		return emailAlternativo;
	}

	public void setEmailAlternativo(String emailAlternativo) {
		this.emailAlternativo = emailAlternativo;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public String getDdd_fone1() {
		return ddd_fone1;
	}

	public void setDdd_fone1(String ddd_fone1) {
		this.ddd_fone1 = ddd_fone1;
	}

	public String getDdd_fone2() {
		return ddd_fone2;
	}

	public void setDdd_fone2(String ddd_fone2) {
		this.ddd_fone2 = ddd_fone2;
	}

	public String getDdd_cel1() {
		return ddd_cel1;
	}

	public void setDdd_cel1(String ddd_cel1) {
		this.ddd_cel1 = ddd_cel1;
	}

	public String getDdd_cel2() {
		return ddd_cel2;
	}

	public void setDdd_cel2(String ddd_cel2) {
		this.ddd_cel2 = ddd_cel2;
	}
	public Empresa getEmpresa() {
		return empresa;
	}
	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}
	public Categorias getCategoria() {
		return categoria;
	}
	public void setCategoria(Categorias categoria) {
		this.categoria = categoria;
	}

	public String getSenha() {
		return senha;
	}
	public void setSenha(String senha) {
		this.senha = senha;
	}
	public Endereco getEndereco_principal() {
		return endereco_principal;
	}
	public void setEndereco_principal(Endereco endereco_principal) {
		this.endereco_principal = endereco_principal;
	}
	public TabelasPreco getTabela_preco() {
		return tabela_preco;
	}
	public void setTabela_preco(TabelasPreco tabela_preco) {
		this.tabela_preco = tabela_preco;
	}
	public Integer getEmpresa_anterior() {
		return empresa_anterior;
	}
	public void setEmpresa_anterior(Integer empresa_anterior) {
		this.empresa_anterior = empresa_anterior;
	}
	public String getProfissao() {
		return profissao;
	}
	public void setProfissao(String profissao) {
		this.profissao = profissao;
	}
	public String getAgendar_crm() {
		return agendar_crm;
	}
	public void setAgendar_crm(String agendar_crm) {
		this.agendar_crm = agendar_crm;
	}
	public String getOperador_cadastro() {
		return operador_cadastro;
	}
	public void setOperador_cadastro(String operador_cadastro) {
		this.operador_cadastro = operador_cadastro;
	}
	public String getOrgao_emissor_rg() {
		return orgao_emissor_rg;
	}
	public void setOrgao_emissor_rg(String orgao_emissor_rg) {
		this.orgao_emissor_rg = orgao_emissor_rg;
	}
	public String getCredito_cliente() {
		return credito_cliente;
	}
	public void setCredito_cliente(String credito_cliente) {
		this.credito_cliente = credito_cliente;
	}
	public String getBlack_list() {
		return black_list;
	}
	public void setBlack_list(String black_list) {
		this.black_list = black_list;
	}
	public String getBlack_list_obs() {
		return black_list_obs;
	}
	public void setBlack_list_obs(String black_list_obs) {
		this.black_list_obs = black_list_obs;
	}
	public Cliente getIndicado_por() {
		return indicado_por;
	}
	public void setIndicador_por(Cliente indicado_por) {
		this.indicado_por = indicado_por;
	}
	public String getAluno() {
		return aluno;
	}
	public void setAluno(String aluno) {
		this.aluno = aluno;
	}
	public String getCurso() {
		return curso;
	}
	public void setCurso(String curso) {
		this.curso = curso;
	}
	public String getHorario_curso() {
		return horario_curso;
	}
	public void setHorario_curso(String horario_curso) {
		this.horario_curso = horario_curso;
	}
	public Integer getTitular_contrato() {
		return titular_contrato;
	}
	public void setTitular_contrato(Integer titular_contrato) {
		this.titular_contrato = titular_contrato;
	}
	public String getOperador_contato() {
		return operador_contato;
	}
	public void setOperador_contato(String operador_contato) {
		this.operador_contato = operador_contato;
	}
	public Date getData_hora_contato() {
		return data_hora_contato;
	}
	public void setData_hora_contato(Date data_hora_contato) {
		this.data_hora_contato = data_hora_contato;
	}
	public String getRede_social() {
		return rede_social;
	}
	public void setRede_social(String rede_social) {
		this.rede_social = rede_social;
	}
	public String getWhatsapp() {
		return whatsapp;
	}
	public void setWhatsapp(String whatsapp) {
		this.whatsapp = whatsapp;
	}
	

	
	
	

	
	
	

	
	
	
	
}

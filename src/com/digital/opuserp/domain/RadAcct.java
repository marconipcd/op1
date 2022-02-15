package com.digital.opuserp.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import javax.persistence.Entity;

@Entity
@Table(name="radacct")
public class RadAcct {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="radacctid", unique=true, nullable=false,columnDefinition="bigint(21)")	
	private Integer radacctid; 
	
	@Column(name="acctsessionid")
	private String acctsessionid; 
	
	@Column(name="acctuniqueid")
	private String acctuniqueid; 
	
	@Column(name="username")
	private String username; 
	
	@Column(name="groupname")
	private String groupname; 
	
	@Column(name="realm")
	private String realm; 
	
	@Column(name="nasipaddress")
	private String nasipaddress; 
	
	@Column(name="nasportid")
	private String nasportid; 
	
	@Column(name="nasporttype")
	private String nasporttype; 
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="acctstarttime")
	private Date acctstarttime; 
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="acctstoptime")
	private Date acctstoptime; 
	
	@Column(name="acctsessiontime", columnDefinition="int(12)")
	private Integer acctsessiontime; 
	
	@Column(name="acctauthentic")
	private String acctauthentic; 
	
	@Column(name="connectinfo_start")
	private String connectinfo_start; 
	
	@Column(name="connectinfo_stop")
	private String connectinfo_stop; 
	
	@Column(name="acctinputoctets", columnDefinition="bigint(20)")
	private long acctinputoctets; 
	
	@Column(name="acctoutputoctets", columnDefinition="bigint(20)")
	private long acctoutputoctets; 
	
	@Column(name="calledstationid")
	private String calledstationid; 
	
	@Column(name="callingstationid")
	private String callingstationid; 
	
	@Column(name="acctterminatecause")
	private String acctterminatecause; 
	
	@Column(name="servicetype")
	private String servicetype; 
	
	@Column(name="framedprotocol")
	private String framedprotocol; 
	
	@Column(name="framedipaddress")
	private String framedipaddress; 
	
	@Column(name="acctstartdelay", columnDefinition="int(12)")
	private Integer acctstartdelay; 
	
	@Column(name="acctstopdelay", columnDefinition="int(12)")
	private Integer acctstopdelay; 
	
	@Column(name="xascendsessionsvrkey")
	private String xascendsessionsvrkey;
	
	
	@Transient
	private String coluna;
	@Transient
	private Date coluna_date;
	@Transient
	private Integer coluna_Inter;
	@Transient
	private Long coluna_long;
	@Transient
	private Long qtd;
	
	public RadAcct(String coluna, Long qtd) {		
		this.coluna = coluna;
		this.qtd = qtd;
	}
	
	public RadAcct(Date coluna_date, Long qtd) {		
		this.coluna_date = coluna_date;
		this.qtd = qtd;
	}

	public RadAcct(Integer coluna_Inter, Long qtd) {		
		this.coluna_Inter = coluna_Inter;
		this.qtd = qtd;
	}
	
	public RadAcct(Long coluna_long, Long qtd) {		
		this.coluna_long = coluna_long;
		this.qtd = qtd;
	}

	public String getColuna() {
		return coluna;
	}

	public void setColuna(String coluna) {
		this.coluna = coluna;
	}
	
	public Long getColuna_long() {
		return coluna_long;
	}

	public void setColuna_long(Long coluna_long) {
		this.coluna_long = coluna_long;
	}		
	
	public Date getColuna_date() {
		return coluna_date;
	}

	public void setColuna_date(Date coluna_date) {
		this.coluna_date = coluna_date;
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
	
	public RadAcct(Integer radacctid){
		super();
		this.radacctid = radacctid;
	}
	
	
	public RadAcct(){
		
	}

	public RadAcct(Integer radacctid, String acctsessionid,
			String acctuniqueid, String username, String groupname,
			String realm, String nasipaddress, String nasportid,
			String nasporttype, Date acctstarttime, Date acctstoptime,
			Integer acctsessiontime, String acctauthentic, String connectinfo,
			String connectinfo_stop, long acctinputoctets,
			long acctoutputoctets, String calledstationid,
			String callingstationid, String acctterminatecause,
			String servicetype, String framedprotocol, String framedipaddress,
			Integer acctstartdelay, Integer acctstopdelay,
			String xascendsessionsvrkey) {
		super();
		this.radacctid = radacctid;
		this.acctsessionid = acctsessionid;
		this.acctuniqueid = acctuniqueid;
		this.username = username;
		this.groupname = groupname;
		this.realm = realm;
		this.nasipaddress = nasipaddress;
		this.nasportid = nasportid;
		this.nasporttype = nasporttype;
		this.acctstarttime = acctstarttime;
		this.acctstoptime = acctstoptime;
		this.acctsessiontime = acctsessiontime;
		this.acctauthentic = acctauthentic;
		this.connectinfo_start = connectinfo;
		this.connectinfo_stop = connectinfo_stop;
		this.acctinputoctets = acctinputoctets;
		this.acctoutputoctets = acctoutputoctets;
		this.calledstationid = calledstationid;
		this.callingstationid = callingstationid;
		this.acctterminatecause = acctterminatecause;
		this.servicetype = servicetype;
		this.framedprotocol = framedprotocol;
		this.framedipaddress = framedipaddress;
		this.acctstartdelay = acctstartdelay;
		this.acctstopdelay = acctstopdelay;
		this.xascendsessionsvrkey = xascendsessionsvrkey;
	}

	public Integer getRadacctid() {
		return radacctid;
	}

	public void setRadacctid(Integer radacctid) {
		this.radacctid = radacctid;
	}

	public String getAcctsessionid() {
		return acctsessionid;
	}

	public void setAcctsessionid(String acctsessionid) {
		this.acctsessionid = acctsessionid;
	}

	public String getAcctuniqueid() {
		return acctuniqueid;
	}

	public void setAcctuniqueid(String acctuniqueid) {
		this.acctuniqueid = acctuniqueid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getGroupname() {
		return groupname;
	}

	public void setGroupname(String groupname) {
		this.groupname = groupname;
	}

	public String getRealm() {
		return realm;
	}

	public void setRealm(String realm) {
		this.realm = realm;
	}

	public String getNasipaddress() {
		return nasipaddress;
	}

	public void setNasipaddress(String nasipaddress) {
		this.nasipaddress = nasipaddress;
	}

	public String getNasportid() {
		return nasportid;
	}

	public void setNasportid(String nasportid) {
		this.nasportid = nasportid;
	}

	public String getNasporttype() {
		return nasporttype;
	}

	public void setNasporttype(String nasporttype) {
		this.nasporttype = nasporttype;
	}

	public Date getAcctstarttime() {
		return acctstarttime;
	}

	public void setAcctstarttime(Date acctstarttime) {
		this.acctstarttime = acctstarttime;
	}

	public Date getAcctstoptime() {
		return acctstoptime;
	}

	public void setAcctstoptime(Date acctstoptime) {
		this.acctstoptime = acctstoptime;
	}

	public Integer getAcctsessiontime() {
		return acctsessiontime;
	}

	public void setAcctsessiontime(Integer acctsessiontime) {
		this.acctsessiontime = acctsessiontime;
	}

	public String getAcctauthentic() {
		return acctauthentic;
	}

	public void setAcctauthentic(String acctauthentic) {
		this.acctauthentic = acctauthentic;
	}

	public String getConnectinfo() {
		return connectinfo_start;
	}

	public void setConnectinfo(String connectinfo) {
		this.connectinfo_start = connectinfo;
	}

	public String getConnectinfo_stop() {
		return connectinfo_stop;
	}

	public void setConnectinfo_stop(String connectinfo_stop) {
		this.connectinfo_stop = connectinfo_stop;
	}

	public long getAcctinputoctets() {
		return acctinputoctets;
	}

	public void setAcctinputoctets(Integer acctinputoctets) {
		this.acctinputoctets = acctinputoctets;
	}

	public long getAcctoutputoctets() {
		return acctoutputoctets;
	}

	public void setAcctoutputoctets(Integer acctoutputoctets) {
		this.acctoutputoctets = acctoutputoctets;
	}

	public String getCalledstationid() {
		return calledstationid;
	}

	public void setCalledstationid(String calledstationid) {
		this.calledstationid = calledstationid;
	}

	public String getCallingstationid() {
		return callingstationid;
	}

	public void setCallingstationid(String callingstationid) {
		this.callingstationid = callingstationid;
	}

	public String getAcctterminatecause() {
		return acctterminatecause;
	}

	public void setAcctterminatecause(String acctterminatecause) {
		this.acctterminatecause = acctterminatecause;
	}

	public String getServicetype() {
		return servicetype;
	}

	public void setServicetype(String servicetype) {
		this.servicetype = servicetype;
	}

	public String getFramedprotocol() {
		return framedprotocol;
	}

	public void setFramedprotocol(String framedprotocol) {
		this.framedprotocol = framedprotocol;
	}

	public String getFramedipaddress() {
		return framedipaddress;
	}

	public void setFramedipaddress(String framedipaddress) {
		this.framedipaddress = framedipaddress;
	}

	public Integer getAcctstartdelay() {
		return acctstartdelay;
	}

	public void setAcctstartdelay(Integer acctstartdelay) {
		this.acctstartdelay = acctstartdelay;
	}

	public Integer getAcctstopdelay() {
		return acctstopdelay;
	}

	public void setAcctstopdelay(Integer acctstopdelay) {
		this.acctstopdelay = acctstopdelay;
	}

	public String getXascendsessionsvrkey() {
		return xascendsessionsvrkey;
	}

	public void setXascendsessionsvrkey(String xascendsessionsvrkey) {
		this.xascendsessionsvrkey = xascendsessionsvrkey;
	}

	
	
	
}

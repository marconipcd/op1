package com.digital.opuserp.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class SystemEvents {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID", nullable=false)
	private Integer id;
	
	private Integer CustomerID;
	private Date ReceivedAt;
	private Date DeviceReportedTime;
	private Integer Facility;
	private Integer Priority;
	private String FromHost;
	private String Message;
	private Integer NTSeverity;
	private Integer Importance;
	private String EventSource;
	private String EventUser;
	private Integer EventCategory;
	private Integer EventID;
	private String EventBinaryData;
	private Integer MaxAvailable;
	private Integer CurrUsage;
	private Integer MinUsage;
	private Integer MaxUsage;
	private Integer InfoUnitID;
	private String SysLogTag;
	private String EventLogType;
	private String GenericFileName;
	private Integer SystemID;
	
	public SystemEvents(){
		
	}

	public SystemEvents(Integer id, Integer customerID, Date receivedAt,
			Date deviceReportedTime, Integer facility, Integer priority,
			String fromHost, String message, Integer nTSeverity,
			Integer importance, String eventSource, String eventUser,
			Integer eventCategory, Integer eventID, String eventBinaryData,
			Integer maxAvailable, Integer currUsage, Integer minUsage,
			Integer maxUsage, Integer infoUnitID, String sysLogTag,
			String eventLogType, String genericFileName, Integer systemID) {
		super();
		this.id = id;
		CustomerID = customerID;
		ReceivedAt = receivedAt;
		DeviceReportedTime = deviceReportedTime;
		Facility = facility;
		Priority = priority;
		FromHost = fromHost;
		Message = message;
		NTSeverity = nTSeverity;
		Importance = importance;
		EventSource = eventSource;
		EventUser = eventUser;
		EventCategory = eventCategory;
		EventID = eventID;
		EventBinaryData = eventBinaryData;
		MaxAvailable = maxAvailable;
		CurrUsage = currUsage;
		MinUsage = minUsage;
		MaxUsage = maxUsage;
		InfoUnitID = infoUnitID;
		SysLogTag = sysLogTag;
		EventLogType = eventLogType;
		GenericFileName = genericFileName;
		SystemID = systemID;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getCustomerID() {
		return CustomerID;
	}

	public void setCustomerID(Integer customerID) {
		CustomerID = customerID;
	}

	public Date getReceivedAt() {
		return ReceivedAt;
	}

	public void setReceivedAt(Date receivedAt) {
		ReceivedAt = receivedAt;
	}

	public Date getDeviceReportedTime() {
		return DeviceReportedTime;
	}

	public void setDeviceReportedTime(Date deviceReportedTime) {
		DeviceReportedTime = deviceReportedTime;
	}

	public Integer getFacility() {
		return Facility;
	}

	public void setFacility(Integer facility) {
		Facility = facility;
	}

	public Integer getPriority() {
		return Priority;
	}

	public void setPriority(Integer priority) {
		Priority = priority;
	}

	public String getFromHost() {
		return FromHost;
	}

	public void setFromHost(String fromHost) {
		FromHost = fromHost;
	}

	public String getMessage() {
		return Message;
	}

	public void setMessage(String message) {
		Message = message;
	}

	public Integer getNTSeverity() {
		return NTSeverity;
	}

	public void setNTSeverity(Integer nTSeverity) {
		NTSeverity = nTSeverity;
	}

	public Integer getImportance() {
		return Importance;
	}

	public void setImportance(Integer importance) {
		Importance = importance;
	}

	public String getEventSource() {
		return EventSource;
	}

	public void setEventSource(String eventSource) {
		EventSource = eventSource;
	}

	public String getEventUser() {
		return EventUser;
	}

	public void setEventUser(String eventUser) {
		EventUser = eventUser;
	}

	public Integer getEventCategory() {
		return EventCategory;
	}

	public void setEventCategory(Integer eventCategory) {
		EventCategory = eventCategory;
	}

	public Integer getEventID() {
		return EventID;
	}

	public void setEventID(Integer eventID) {
		EventID = eventID;
	}

	public String getEventBinaryData() {
		return EventBinaryData;
	}

	public void setEventBinaryData(String eventBinaryData) {
		EventBinaryData = eventBinaryData;
	}

	public Integer getMaxAvailable() {
		return MaxAvailable;
	}

	public void setMaxAvailable(Integer maxAvailable) {
		MaxAvailable = maxAvailable;
	}

	public Integer getCurrUsage() {
		return CurrUsage;
	}

	public void setCurrUsage(Integer currUsage) {
		CurrUsage = currUsage;
	}

	public Integer getMinUsage() {
		return MinUsage;
	}

	public void setMinUsage(Integer minUsage) {
		MinUsage = minUsage;
	}

	public Integer getMaxUsage() {
		return MaxUsage;
	}

	public void setMaxUsage(Integer maxUsage) {
		MaxUsage = maxUsage;
	}

	public Integer getInfoUnitID() {
		return InfoUnitID;
	}

	public void setInfoUnitID(Integer infoUnitID) {
		InfoUnitID = infoUnitID;
	}

	public String getSysLogTag() {
		return SysLogTag;
	}

	public void setSysLogTag(String sysLogTag) {
		SysLogTag = sysLogTag;
	}

	public String getEventLogType() {
		return EventLogType;
	}

	public void setEventLogType(String eventLogType) {
		EventLogType = eventLogType;
	}

	public String getGenericFileName() {
		return GenericFileName;
	}

	public void setGenericFileName(String genericFileName) {
		GenericFileName = genericFileName;
	}

	public Integer getSystemID() {
		return SystemID;
	}

	public void setSystemID(Integer systemID) {
		SystemID = systemID;
	}
	
	
	
}

package com.digital.opuserp.domain;

public class EntityTest {

	private Integer id;
	private String file;
	private String edit;
	private String source;
	private String refactor;
	private String navigate;
	private String search;
	private String project;
	private String run;
	private String window;
	private String help;
	
	public EntityTest(){
		
	}
	public EntityTest(Integer id, String file, String edit, String source, String refactor, String navigate, String search, String project, String run, String window, String help){
		this.id = id;
		this.file = file;
		this.edit = edit;
		this.source = source;
		this.refactor = refactor;
		this.navigate = navigate;
		this.search = search;
		this.project = project;
		this.run = run;
		this.window = window;
		this.help = help;
	}
	
	private void setId(Integer id){
		this.id = id;
	}
	private Integer getId(){
		return id;
	}
	private void setFile(String file){
		this.file = file;
	}
	private String getFile(){
		return file;
	}
	private void setEdit(String edit){
		this.edit = edit;
	}
	private String getEdit(){
		return edit;
	}
	
	private void setSource(){
		this.source = source;
	}
	private String getSource(){
		return  source;
	}
	private void setRefactor(String refactor){
		this.refactor = refactor;
	}
	private String getRefactor(){
		return refactor;
	}
	private void setNavigate(){ 
		this.navigate = navigate;
	}
	private String getNavigate(){
		return navigate;
	}
	private void setSearch(String search){
		this.search = search;
	}
	private String getSearch(){
		return search;
	}
	private void setProject(String project){
		this.project = project;
	}
	private String getProject(){
		return project;
	}
	private void setRun(String run){
		this.run = run;
	}
	private String getRun(){
		return run;
	}
	private void setWindow(String window){
		this.window = window;
	}
	private String getWindow(){
		return window;
	}
	private void setHelp(String help){
		this.help = help;
	}
	private String getHelp(){
		return help;
	}
	

	
}



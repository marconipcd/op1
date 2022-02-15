package com.digital.opuserp.domain;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="setores_usuario")
@Cacheable(value=false)
public class SetoresUsuario {
		

	   @Id
	   @GeneratedValue(strategy= GenerationType.AUTO)
	   @Column(name="ID", nullable=false,unique=true)
	   private Integer id;
	   @Column(name="EMPRESA_ID", nullable=false,unique=false)
	   private Integer empresa_id;	   
	   
	   @Column(name="USUARIO_ID", nullable=false,unique=false)
	   private Integer usuario_id;	  
	   
	   @Column(name="SETOR_ID", nullable=false,unique=false)
	   private Integer setor_id;
 
	   
		public SetoresUsuario() {

		}
		
		

		public SetoresUsuario(Integer id, Integer empresa_id,
				Integer usuario_id, Integer setor_id) {
			super();
			this.id = id;
			this.empresa_id = empresa_id;
			this.usuario_id = usuario_id;
			this.setor_id = setor_id;
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

//		public Usuario getUsuario_id() {
//			return usuario_id;
//		}
//
//		public void setUsuario_id(Usuario usuario_id) {
//			this.usuario_id = usuario_id;
//		}
		public Integer getUsuario_id() {
			return usuario_id;
		}
		
		public void setUsuario_id(Integer usuario_id) {
			this.usuario_id = usuario_id;
		}

		public Integer getSetor_id() {
			return setor_id;
		}

		public void setSetor_id(Integer setor_id) {
			this.setor_id = setor_id;
		}


	       
	       
}

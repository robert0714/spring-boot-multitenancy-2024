package com.thomasvitale.multitenant.app.instrumentservice;

import java.util.UUID;

import org.hibernate.annotations.JavaType;
import org.hibernate.annotations.JdbcType;
import org.hibernate.type.descriptor.java.UUIDJavaType;
import org.hibernate.type.descriptor.jdbc.VarcharJdbcType;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
public class Instrument {
  
	@Id
	@Column(name = "id", nullable = false ,columnDefinition="varchar(36)")
	@GeneratedValue(strategy=GenerationType.UUID)
	@JdbcType(VarcharJdbcType.class)
    @JavaType(UUIDJavaType.class) 
	private UUID id;

	@NotEmpty
	private String name;

	private String type; 

}
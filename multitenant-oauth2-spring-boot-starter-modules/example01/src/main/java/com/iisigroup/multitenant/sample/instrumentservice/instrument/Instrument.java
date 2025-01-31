package com.iisigroup.multitenant.sample.instrumentservice.instrument;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Builder
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
public class Instrument {
  
	@Id
	@GeneratedValue(strategy=GenerationType.UUID)
	private UUID id;

	@NotEmpty
	private String name;

	private String type;

	@Column(name = "created_at", nullable = false, updatable = false)
	@CreatedDate
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	private OffsetDateTime createdAt;

	/**
	 * Edit date/time
	 */	
	@Column(name = "updated_at", nullable = false)
	@LastModifiedDate
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	private OffsetDateTime updatedAt;

	/**
	 * Creator Account
	 */
	
	@Column(name = "created_by", nullable = false, updatable = false)
	@CreatedBy
	private String createdBy;

	/**
	 * Editor account
	 */
	
	@Column(name = "updated_by", nullable = false)
	@LastModifiedBy
	private String updatedBy;

}

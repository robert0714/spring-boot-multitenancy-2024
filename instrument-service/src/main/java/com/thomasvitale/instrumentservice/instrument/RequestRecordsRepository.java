package com.thomasvitale.instrumentservice.instrument;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
  

import java.util.UUID;

public interface RequestRecordsRepository extends JpaRepository<RequestRecords, UUID>, JpaSpecificationExecutor<RequestRecords> {
}
package com.thomasvitale.instrumentservice.instrument;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service; 

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AsyncRequestRecordsService {

     
    private final RequestRecordsRepository requestRecordsRepository;

    @Async
    public void save(RequestRecords requestRecords) {
        this.requestRecordsRepository.save(requestRecords);
    }
}

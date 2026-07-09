package com.example.demo.service;


import com.example.demo.entity.IdempotencyKey;
import com.example.demo.enums.IdempotencyStatus;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.IdempotencyKeyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
public class IdempotencyService {

    private IdempotencyKeyRepository idempotencyKeyRepository;

    IdempotencyService(IdempotencyKeyRepository idempotencyKeyRepository)
    {
        this.idempotencyKeyRepository=idempotencyKeyRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Optional<IdempotencyKey> initiateOrGetExisting(String key, String requestHash) {
        if(idempotencyKeyRepository.existsById(key))
        {
            IdempotencyKey idempotencyKey1=idempotencyKeyRepository.findById(key).orElseThrow(()->new ResourceNotFoundException("Key not found neither inserted"));
            if(!idempotencyKey1.getRequestHash().equals(requestHash)){
                throw new RuntimeException("misuse of idempotency key");
            }
            return Optional.of(idempotencyKey1);

        }
        IdempotencyKey idempotencyKey=new IdempotencyKey();
        idempotencyKey.setIdempotencyKey(key);
        idempotencyKey.setRequestHash(requestHash);
        idempotencyKey.setStatus(IdempotencyStatus.PROCESSING);

            idempotencyKeyRepository.saveAndFlush(idempotencyKey);
            return Optional.empty();

    }
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void complete(String key, String responseBody, Integer httpStatus, Long resourceId) {
        IdempotencyKey idempotencyKey=idempotencyKeyRepository.findById(key).orElseThrow(()->new ResourceNotFoundException("Key not found neither inserted"));
        idempotencyKey.setStatus(IdempotencyStatus.COMPLETED);
        idempotencyKey.setResponseBody(responseBody);
        idempotencyKey.setHttpStatus(httpStatus);
        idempotencyKey.setResourceId(resourceId);
        idempotencyKeyRepository.save(idempotencyKey);
    }
}

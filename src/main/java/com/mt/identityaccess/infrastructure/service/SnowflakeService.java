package com.mt.identityaccess.infrastructure.service;

import com.mt.common.snowflake.IdGenerator;
import com.mt.identityaccess.domain.service.UniqueIdGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SnowflakeService implements UniqueIdGeneratorService {
    @Autowired
    private IdGenerator idGenerator;

    @Override
    public Long id() {
        return idGenerator.id();
    }
}

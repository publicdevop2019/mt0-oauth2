package com.mt.common.infrastructure;

import com.mt.common.snowflake.IdGenerator;
import com.mt.common.domain.model.id.UniqueIdGeneratorService;
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

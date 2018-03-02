package com.api.database.repository;

import com.api.database.domain.ClassifierDao;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ClassifierRepository extends CassandraRepository<ClassifierDao, UUID> {
}

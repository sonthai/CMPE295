package com.api.database.repository;

import com.api.database.domain.UserHistoryDao;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserHistoryRepository extends CassandraRepository<UserHistoryDao, UUID> {}

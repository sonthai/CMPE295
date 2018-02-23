package com.api.database.repository;

import com.api.database.domain.UserTable;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends CassandraRepository<UserTable, UUID> {
   // User findUserByUserName(String username);
}

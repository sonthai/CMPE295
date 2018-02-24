package com.api.database.repository;

import com.api.database.domain.UserDao;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRepository extends CassandraRepository<UserDao, String> {
    @Query("select * from user where user_name = ?0")
    List<UserDao> findUserByUserName(String username);
}

package com.api.database.domain;

import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.UUID;

@Table("user")
public class UserTable {
    @PrimaryKeyColumn(name="user_name", type = PrimaryKeyType.PARTITIONED)
    private String username;

    @PrimaryKeyColumn(name = "user_id", ordinal = 1, ordering = Ordering.ASCENDING)
    private UUID id;

    @Column(value = "user_email")
    private String email;

    @Column(value = "user_password")
    private String password;

    public UserTable(){}

    public UserTable(UUID id, String username, String email, String password) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}

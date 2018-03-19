package com.api.database.domain;

import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.UUID;

@Table("user_history")
public class UserHistoryDao {
    public UUID getId() { return id; }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public UserHistoryDao(UUID uuid, String user_id, String product_id) {
        this.id = uuid;
        this.username = user_id;
        this.product = product_id;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    @PrimaryKeyColumn(name = "id", ordinal = 0, ordering = Ordering.ASCENDING)
    private UUID id;

    @PrimaryKeyColumn(name="user_id", type = PrimaryKeyType.PARTITIONED)
    private String username;

    @Column(value = "product_id")
    private String product;
}


package com.api.database.domain;

import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.io.Serializable;
import java.util.UUID;

// This class might be removed later
@Table("classifier")
public class ClassifierDao implements Serializable {
    @PrimaryKeyColumn(name = "id", ordinal = 0, ordering = Ordering.ASCENDING)
    private int id;

    @PrimaryKeyColumn(name = "product_id", type = PrimaryKeyType.PARTITIONED)
    private String product;

    @Column(value = "upper_id")
    private String upper;

    @Column(value = "lower_id")
    private String lower;

    @Column(value = "color_id")
    private String color;

    @Column(value = "brand_id")
    private String brand;

    @Column(value = "style_id")
    private String style;

    @Column(value = "gender_id")
    private String gender;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getUpper() {
        return upper;
    }

    public void setUpper(String upper) {
        this.upper = upper;
    }

    public String getLower() {
        return lower;
    }

    public void setLower(String lower) {
        this.lower = lower;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public ClassifierDao() {}
}

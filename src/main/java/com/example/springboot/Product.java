package com.example.springboot;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonFormat;

@Data
@Entity
class Product {
  @JsonFormat( shape = JsonFormat.Shape.STRING)
  private @Id Long id;
  private String name;
  private String type;
  private String version;
  private Double price;

  Product() {}

  Product(Long id, String name, String type, String version, Double price) {
    this.id = id;
    this.name = name;
    this.type = type;
    this.version = version;
    this.price = price;
  }
}
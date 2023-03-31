package com.example.springboot;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonFormat;

@Data
@Entity
class QA {
  @JsonFormat( shape = JsonFormat.Shape.STRING)
  private @Id Long id;
  private String name;
  private String surname;
  private String version;
  private Integer age;

  QA() {}

  QA(Long id, String name, String surname, String version, Integer age) {
    this.id = id;
    this.name = name;
    this.surname = surname;
    this.version = version;
    this.age = age;
  }
}
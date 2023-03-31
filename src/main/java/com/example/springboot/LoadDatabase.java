package com.example.springboot;

import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
class LoadDatabase {

  @Bean
  CommandLineRunner initDatabase(QARepository repository) {
    return args -> {
      log.info("Preloading " + repository.save(new QA(1L, "Patrick", "Hendron", "1.0.0", 28)));
      log.info("Preloading " + repository.save(new QA(2L, "Ross", "Aubery-Smith", "1.2.3", 21)));
    };
  }
}
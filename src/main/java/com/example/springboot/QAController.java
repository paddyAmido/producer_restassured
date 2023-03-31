package com.example.springboot;

import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
@CrossOrigin(origins = { "*" })
@RequestMapping(value = "/", produces = "application/json; charset=utf-8")
class QAController {

  private final QARepository repository;

  QAController(QARepository repository) {
    this.repository = repository;
  }

  @GetMapping("/QAs")
  List<QA> all() {
    return repository.findAll();
  }

  @PostMapping("/QAs")
  QA newQA(@RequestBody QA newQA) {
    try {
      return repository.save(newQA);
    } catch (Exception e) {
      throw new BadRequestException();
    }
  }

  @GetMapping({ "/QA/{id}" })
  QA one(@PathVariable Long id) {

    return repository.findById(id).orElseThrow(() -> new QANotFoundException(id));
  }

  @PutMapping({ "/QA/{id}" })
  QA replaceQA(@RequestBody QA newQA, @PathVariable Long id) {

    return repository.findById(id).map(QA -> {
      QA.setName(newQA.getName());
      QA.setSurname(newQA.getSurname());
      return repository.save(QA);
    }).orElseGet(() -> {
      newQA.setId(id);
      return repository.save(newQA);
    });
  }

  @DeleteMapping({ "/QA/{id}" })
  void deleteQA(@PathVariable Long id) {
    repository.deleteById(id);
  }
}
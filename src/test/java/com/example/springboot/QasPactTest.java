package com.example.springboot;

import com.atlassian.oai.validator.OpenApiInteractionValidator;
import com.atlassian.oai.validator.report.LevelResolver;
import com.atlassian.oai.validator.report.ValidationReport.Level;
import com.atlassian.oai.validator.restassured.OpenApiValidationFilter;
import com.atlassian.oai.validator.whitelist.ValidationErrorsWhitelist;
import com.atlassian.oai.validator.whitelist.rule.WhitelistRules;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;

import static io.restassured.RestAssured.given;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)

class QaAPITest {
  @LocalServerPort
  int port;

  @Autowired
  QARepository repository;

  File spec = new File("oas/swagger.yml");

  // Use this for "happy path" testing

  // A Filter that performs OpenAPI / Swagger validation on a request/response interaction.
  // This is opening path oas/swagger
  private final OpenApiValidationFilter validationFilter = new OpenApiValidationFilter(spec.getAbsolutePath());

  // Validates a HTTP interaction (request/response pair) with a Swagger v2 / OpenAPI v3 specification.
  private final OpenApiInteractionValidator responseOnlyValidator = OpenApiInteractionValidator
          .createForSpecificationUrl(spec.getAbsolutePath())
          .withLevelResolver(LevelResolver.create().withLevel("validation.request", Level.WARN).build())
          .withWhitelist(ValidationErrorsWhitelist.create().withRule("Ignore request entities", WhitelistRules.isRequest()))
          .build();

  // Use this for "negative scenario" testing
  private OpenApiValidationFilter responseOnlyValidation = new OpenApiValidationFilter(responseOnlyValidator);

  @Test
  public void testCreateQA200() {
    QA qa = new QA(99L, "new QA", "QA category", "v1", 18);

    given().port(port).filter(validationFilter).body(qa).contentType("application/json").when().post("/QAs")
            .then().assertThat().statusCode(200);
  }

  @Test
  public void testCreateQA400() {
    given().port(port).filter(responseOnlyValidation).body("{}").contentType("application/json").when().post("/QAs")
            .then().assertThat().statusCode(400);
  }

  @Test
  public void testListQAs() {
    given().port(port).filter(validationFilter).when().get("/QAs").then().assertThat().statusCode(200);
  }

  @Test
  public void testGetQA200() {
    given().port(port).filter(validationFilter).when().get("/QA/1").then().assertThat().statusCode(200);
  }

  @Test
  public void testGetQA404() {
    given().port(port).filter(validationFilter).when().get("/QA/999").then().assertThat().statusCode(404);
  }

  @Test
  public void testGetQA400() {
    given().port(port).filter(validationFilter).when().get("/QA/notanumber").then().assertThat().statusCode(400);
  }
}
package com.example.springboot;

import au.com.dius.pact.provider.junit5.HttpTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.loader.PactBroker;
import au.com.dius.pact.provider.junitsupport.loader.PactBrokerAuth;
import com.atlassian.oai.validator.OpenApiInteractionValidator;
import com.atlassian.oai.validator.report.LevelResolver;
import com.atlassian.oai.validator.report.ValidationReport.Level;
import com.atlassian.oai.validator.restassured.OpenApiValidationFilter;
import com.atlassian.oai.validator.whitelist.ValidationErrorsWhitelist;
import com.atlassian.oai.validator.whitelist.rule.WhitelistRules;
import org.junit.jupiter.api.BeforeEach;
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
//@Provider("testProvider2")

//@PactBroker(url = "https://paddy.pactflow.io",providerBranch = "master", enablePendingPacts = "true", authentication = @PactBrokerAuth(token = "-ZCU8y7zMH_7dlIJJHpK3Q"))

class ProductsAPITest {
  @LocalServerPort
  int port;

  @Autowired
  ProductRepository repository;

  File spec = new File("oas/swagger.yml");

//  @BeforeEach
//  public void setupTestTarget(PactVerificationContext context) {
//   context.setTarget(new HttpTestTarget("localhost", 8080));
//    System.setProperty("pact.verifier.publishResults", "true");
//  }

  // Use this for "happy path" testing

  //A Filter that performs OpenAPI / Swagger validation on a request/response interaction.
  // This is opening path oas/swagger
  private final OpenApiValidationFilter validationFilter = new OpenApiValidationFilter(spec.getAbsolutePath());

  //Validates a HTTP interaction (request/response pair) with a Swagger v2 / OpenAPI v3 specification.
  private final OpenApiInteractionValidator responseOnlyValidator = OpenApiInteractionValidator
          .createForSpecificationUrl(spec.getAbsolutePath())
          .withLevelResolver(LevelResolver.create().withLevel("validation.request", Level.WARN).build())
          .withWhitelist(ValidationErrorsWhitelist.create().withRule("Ignore request entities", WhitelistRules.isRequest()))
          .build();

  // Use this for "negative scenario" testing
  private OpenApiValidationFilter responseOnlyValidation = new OpenApiValidationFilter(responseOnlyValidator);

  @Test
  public void testCreateProduct200() {
    Product product = new Product(99L, "new product", "product category", "v1", 1.99);

    given().port(port).filter(validationFilter).body(product).contentType("application/json").when().post("/products")
            .then().assertThat().statusCode(200);
  }

  @Test
  public void testCreateProduct400() {
    given().port(port).filter(responseOnlyValidation).body("{}").contentType("application/json").when().post("/products")
            .then().assertThat().statusCode(400);
  }

  @Test
  public void testListProducts() {
    given().port(port).filter(validationFilter).when().get("/products").then().assertThat().statusCode(200);
  }

  @Test
  public void testGetProduct200() {
    given().port(port).filter(validationFilter).when().get("/product/1").then().assertThat().statusCode(200);
  }

  @Test
  public void testGetProduct404() {
    given().port(port).filter(validationFilter).when().get("/product/999").then().assertThat().statusCode(404);
  }

  @Test
  public void testGetProduct400() {
    given().port(port).filter(validationFilter).when().get("/product/notanumber").then().assertThat().statusCode(400);
  }
}
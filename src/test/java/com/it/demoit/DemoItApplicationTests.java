package com.it.demoit;

import java.util.Optional;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DemoItApplicationTests extends AbstractDBTest {

  @LocalServerPort
  int localPort;

  @Autowired
  ItemRepository itemRepository;

  @BeforeEach
  void setupRestAssured() {
    RestAssured.baseURI = "http://localhost";
    RestAssured.port = localPort;
  }

  @Test
  void shouldSaveItem() {
    final var responseBody = RestAssured.given()
      .contentType(ContentType.JSON)
      .body(new Item("abc"))
      .post("/items")
      .then()
      .statusCode(HttpStatus.CREATED.value())
      .body("name", Matchers.equalTo("abc"))
      .body("id", Matchers.notNullValue())
      .extract()
      .body()
      .asString();

    System.out.println(responseBody);

    final Optional<ItemEntity> byId = itemRepository.findById(1L);

    Assertions.assertTrue(byId.isPresent());
  }

}

package com.vsftam.electronicstore;

import com.vsftam.persistence.Product;
import com.vsftam.utils.ProductType;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
class ElectronicStoreApplicationTests {

	private static final String API_ROOT = "http://localhost:8081/api/products";

	@Test
	void contextLoads() {
	}

	@Test
	public void whenGetAllProducts_thenOK() {
		final Response response = RestAssured.get(API_ROOT);
		assertEquals(HttpStatus.OK.value(), response.getStatusCode());
	}

	@Test
	public void whenGetProductByName_thenOK() {
		final Product product = createRandomProduct();
		createProductAsUri(product);

		final Response response = RestAssured.get(API_ROOT + "/name/" + product.getName());
		assertEquals(HttpStatus.OK.value(), response.getStatusCode());
		assertTrue(response.as(List.class)
				.size() > 0);
	}

	@Test
	public void whenGetCreatedProductById_thenOK() {
		final Product product = createRandomProduct();
		final String location = createProductAsUri(product);

		final Response response = RestAssured.get(location);
		assertEquals(HttpStatus.OK.value(), response.getStatusCode());
		assertEquals(product.getName(), response.jsonPath()
				.get("name"));
	}

	@Test
	public void whenGetNotExistProductById_thenNotFound() {
		final Response response = RestAssured.get(API_ROOT + "/" + randomNumeric(4));
		assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode());
	}

	// POST
	@Test
	public void whenCreateNewProduct_thenCreated() {
		final Product product = createRandomProduct();

		final Response response = RestAssured.given()
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.body(product)
				.post(API_ROOT);
		assertEquals(HttpStatus.CREATED.value(), response.getStatusCode());
	}

	@Test
	public void whenInvalidProduct_thenError() {
		final Product product = createRandomProduct();
		product.setName(null);

		final Response response = RestAssured.given()
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.body(product)
				.post(API_ROOT);
		assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
	}

	@Test
	public void whenUpdateCreatedProduct_thenUpdated() {
		final Product product = createRandomProduct();
		final String location = createProductAsUri(product);

		product.setId(Long.parseLong(location.split("api/products/")[1]));
		product.setName("newProductName");
		Response response = RestAssured.given()
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.body(product)
				.put(location);
		assertEquals(HttpStatus.OK.value(), response.getStatusCode());

		response = RestAssured.get(location);
		assertEquals(HttpStatus.OK.value(), response.getStatusCode());
		assertEquals("newProductName", response.jsonPath()
				.get("name"));

	}

	@Test
	public void whenDeleteCreatedProduct_thenOk() {
		final Product product = createRandomProduct();
		final String location = createProductAsUri(product);

		Response response = RestAssured.delete(location);
		assertEquals(HttpStatus.OK.value(), response.getStatusCode());

		response = RestAssured.get(location);
		assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode());
	}

	// helper functions

	private Product createRandomProduct() {
		final Product product = new Product();
		product.setName(randomAlphabetic(10));
		product.setType(ProductType.SMARTPHONE);
		product.setPrice(Double.valueOf(randomNumeric(5)));
		return product;
	}

	private String createProductAsUri(Product product) {
		final Response response = RestAssured.given()
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.body(product)
				.post(API_ROOT);
		return API_ROOT + "/" + response.jsonPath().get("id");
	}

}

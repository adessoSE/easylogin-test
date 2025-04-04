package de.adesso.controller;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import static io.restassured.RestAssured.*;
import io.restassured.http.ContentType;

@QuarkusTest
@TestHTTPEndpoint(EncryptionResource.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EncryptionResourceTest {
    private Map<String, String> jsonAsMap = new HashMap<>();
    private int testPort = 8081;
    private URI uri = URI.create("http://localhost:" + testPort + "/encryption");

    @BeforeEach
    public void init(){
        String publickeyString = "-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnuCQh/38D7n/z/PiXFoSEd1bOWkhNrUvakJJTzrCUuwRcHRqMY+qqmiRDUBEYw5KRvqKegtjx6XVlAd3kglTd/a2n8IX9Y35ceQKpQIa5U/boJrqwMUO43fCNGl7ZAxKb2rc+Z6/VTWn4jZ4cC3O2tgGAACbRAMy8e9Fv+QL4p39q6ldQhUV8FkrGyRK6bvwi6KO6HgvQirhJfgBeM8T86U1pY0jmDXoxOoUfZ9SUWBHHXNnskaLr9r+8jPhFwVZUtHH94ucdWWvi+U1dGJTZHcQwWs07A5NlqUMFVH76rYoZvYMg6VAQ6eBYar1vcfL6T93ebfwMKB7OWLbow6P9wIDAQAB-----END PUBLIC KEY-----";
        
        jsonAsMap.put("publicKey", publickeyString);
    }

    @Test
    @DisplayName("Test valid public key")
    public void testPUTValidPublicKey(){
        given()
            .contentType(ContentType.JSON)
            .body(jsonAsMap)
            .when()
            .put(uri)
            .then()
            .statusCode(202);
    }

    @Test
    @DisplayName("Test invalid public key")
    public void testPUTInvalidPublicKey(){
        String invalidPublicKeyString = "-----BEGIN PUBLIC KEY-----Yar1vcfL6T93ebfwMKB7OWLbow6P9wIDAQAB-----END PUBLIC KEY-----";
        jsonAsMap.put("publicKey", invalidPublicKeyString);

        given()
            .contentType(ContentType.JSON)
            .body(jsonAsMap)
            .when()
            .put(uri)
            .then()
            .statusCode(400);
    }

    @DisplayName("Test illegal methods")
    @Nested
    class NestedIllegalMethodsTest {
        @Test
        public void testIllegalMethodPOST(){
            given()
                .contentType(ContentType.JSON)
                .body(jsonAsMap)
                .when()
                .post(uri)
                .then()
                .statusCode(405);
        }

        @Test
        public void testIllegalMethodDELETE(){
            given()
                .contentType(ContentType.JSON)
                .body(jsonAsMap)
                .when()
                .delete(uri)
                .then()
                .statusCode(405);
        }

        @Test
        public void testIllegalMethodGET(){
            given()
                .contentType(ContentType.JSON)
                .body(jsonAsMap)
                .when()
                .get(uri)
                .then()
                .statusCode(405);
        }
    }
}

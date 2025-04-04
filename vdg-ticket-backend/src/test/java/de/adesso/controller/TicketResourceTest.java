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
@TestHTTPEndpoint(TicketResource.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TicketResourceTest {
    private Map<String, String> jsonAsMap = new HashMap<>();
    private int testPort = 8081;
    private URI uri = URI.create("http://localhost:" + testPort + "/ticket");

    @BeforeEach
    public void init() {
        // The backend don't validate the values. You can enter junk.
        jsonAsMap.put("ticketId", "keystoreString");
        jsonAsMap.put("targetId", "keystoreAlias");
        jsonAsMap.put("targetUserId", "keystorePassword");
        jsonAsMap.put("issuerUserId", "keystoreAliasPassword");
        jsonAsMap.put("userIPAddress", "keystoreDigitalSignature");
        jsonAsMap.put("authLevel", "keystoreString");
        jsonAsMap.put("authMethod", "keystoreAlias");
        jsonAsMap.put("issuerId", "keystorePassword");
    }

    @Test
    public void testPutTicket() {
        given()
                .contentType(ContentType.JSON)
                .body(jsonAsMap)
                .when()
                .put(uri)
                .then()
                .statusCode(202);
    }

    @Test
    public void testGetTicket() {
        given()
            .contentType(ContentType.JSON)
            .body(jsonAsMap)
            .when()
            .get(uri)
            .then()
            .statusCode(200);
    }

    @DisplayName("Test illegal methods")
    @Nested
    class NestedIllegalMethodsTest {
        @Test
        public void testIllegalMethodPOST() {
            given()
                    .contentType(ContentType.JSON)
                    .body(jsonAsMap)
                    .when()
                    .post(uri)
                    .then()
                    .statusCode(405);
        }

        @Test
        public void testIllegalMethodDELETE() {
            given()
                    .contentType(ContentType.JSON)
                    .body(jsonAsMap)
                    .when()
                    .delete(uri)
                    .then()
                    .statusCode(405);
        }
    }
}

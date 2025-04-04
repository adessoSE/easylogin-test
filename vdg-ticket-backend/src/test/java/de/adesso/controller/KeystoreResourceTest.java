package de.adesso.controller;

import java.io.File;
import java.net.URI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import static io.restassured.RestAssured.*;
import io.restassured.http.ContentType;

@QuarkusTest
@TestHTTPEndpoint(KeystoreResource.class)
public class KeystoreResourceTest {
    private int testPort = 8081;
    private URI uri = URI.create("http://localhost:" + testPort + "/keystore");
    private File file;

    @BeforeEach
    public void init(){
        //Setup 
        file = new File(getClass().getClassLoader().getResource("keystoreForTesting").getFile());

    }
        
    @Test
    public void testKeystoreWithCorrectValues(){
        given()
            .multiPart("keystoreFile", file)
            .multiPart("keystoreAlias", "testKeystoreAlias")
            .multiPart("keystorePassword", "password")
            .multiPart("keystoreAliasPassword", "aliaspassword")
            .multiPart("keystoreDigitalSignature", "SHA256withRSA")
            .contentType(ContentType.MULTIPART)
            .when()
            .put(uri)
            .then()
            .statusCode(202);
    }

    @DisplayName("Test Incorrect values")
    @Nested
    class NestedIncorrectValuesTest {
        @Test
        @DisplayName("Incorrect keystore alias")
        public void testKeystoreFileWithIncorrectKeystoreAlias(){
            // In this case, the wrong value is the keystore alias. 
            given()
                .contentType(ContentType.MULTIPART)
                .multiPart("keystoreFile", file)
                .multiPart("keystoreAlias", "testAlias")
                .multiPart("keystorePassword", "password")
                .multiPart("keystoreAliasPassword", "aliaspassword")
                .multiPart("keystoreDigitalSignature", "SHA256withRSA")
                .contentType(ContentType.MULTIPART)
                .when()
                .put(uri)
                .then()
                .statusCode(409);
        }

        @Test
        @DisplayName("Incorrect keystore alias password")
        public void testKeystoreFileWithIncorrectAliasPassword(){
            // In this case, the wrong value is the alias password.
            given()
                .contentType(ContentType.MULTIPART)
                .multiPart("keystoreFile", file)
                .multiPart("keystoreAlias", "testKeystoreAlias")
                .multiPart("keystorePassword", "password")
                .multiPart("keystoreAliasPassword", "password")
                .multiPart("keystoreDigitalSignature", "SHA256withRSA")
                .contentType(ContentType.MULTIPART)
                .when()
                .put(uri)
                .then()
                .statusCode(409);
        }

        @Test
        @DisplayName("Incorrect keystore password")
        public void testKeystoreFileWithIncorrectKestorePassword(){
            // In this case, the wrong value is the keystore password. The backend is not be able to open the keystore file. 
            given()
                .contentType(ContentType.MULTIPART)
                .multiPart("keystoreFile", file)
                .multiPart("keystoreAlias", "testKeystoreAlias")
                .multiPart("keystorePassword", "password1")
                .multiPart("keystoreAliasPassword", "aliaspassword")
                .multiPart("keystoreDigitalSignature", "SHA256withRSA")
                .contentType(ContentType.MULTIPART)
                .when()
                .put(uri)
                .then()
                .statusCode(409);
        }
    }

    @DisplayName("Test illegal methods")
    @Nested
    class NestedIllegalMethodsTest {
        @Test
        public void testIllegalMethodPOST(){
            given()
                .contentType(ContentType.MULTIPART)
                .multiPart("keystoreFile", file)
                .multiPart("keystoreAlias", "testKeystoreAlias")
                .multiPart("keystorePassword", "password")
                .multiPart("keystoreAliasPassword", "aliaspassword")
                .multiPart("keystoreDigitalSignature", "SHA256withRSA")
                .contentType(ContentType.MULTIPART)
                .when()
                .post(uri)
                .then()
                .statusCode(405);
        }

        @Test
        public void testIllegalMethodDELETE(){
            given()
                .contentType(ContentType.MULTIPART)
                .multiPart("keystoreFile", file)
                .multiPart("keystoreAlias", "testKeystoreAlias")
                .multiPart("keystorePassword", "password")
                .multiPart("keystoreAliasPassword", "aliaspassword")
                .multiPart("keystoreDigitalSignature", "SHA256withRSA")
                .contentType(ContentType.MULTIPART)
                .when()
                .delete(uri)
                .then()
                .statusCode(405);
        }
        @Test
        public void testIllegalMethodGET(){
            given()
                .contentType(ContentType.MULTIPART)
                .multiPart("keystoreFile", file)
                .multiPart("keystoreAlias", "testKeystoreAlias")
                .multiPart("keystorePassword", "password")
                .multiPart("keystoreAliasPassword", "aliaspassword")
                .multiPart("keystoreDigitalSignature", "SHA256withRSA")
                .contentType(ContentType.MULTIPART)
                .when()
                .get(uri)
                .then()
                .statusCode(405);
        }
    }
}

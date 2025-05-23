package de.adesso.controller;

import java.io.StringReader;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.jboss.logging.Logger;

import de.adesso.model.EncryptionSingleton;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/encryption")
public class EncryptionResource {

    private static final Logger LOG = Logger.getLogger(EncryptionResource.class);

    @PUT
    @APIResponse(
        responseCode = "202",
        description = "Public key accepted",
        content = @Content(
            mediaType = jakarta.ws.rs.core.MediaType.APPLICATION_JSON,
            schema = @Schema(implementation = EncryptionSingleton.class)
        )
    )
    @APIResponse(
        responseCode = "400",
        description = "Public key not accepted",
        content = @Content(
            mediaType = jakarta.ws.rs.core.MediaType.APPLICATION_JSON,
            schema = @Schema(implementation = EncryptionSingleton.class)
        )
    )
    public Response encryption(String jsonBody ){
        byte[] publicKeyBytes = null;

        JsonReader jsonReader = Json.createReader(new StringReader(jsonBody));
        JsonObject jsonObject = jsonReader.readObject();

        String publicKeyString = jsonObject.getString("publicKey");
        
        // Deletes Begin, End and all white space characters
        publicKeyString = publicKeyString
                            .replace("-----BEGIN PUBLIC KEY-----", "")
                            .replace("-----END PUBLIC KEY-----", "")
                            .replaceAll("\\s+", "");

        try {
            publicKeyBytes = Base64.getDecoder().decode(publicKeyString);
        } catch(IllegalArgumentException e){
            LOG.error("Public key is invalid!");
            return Response.status(Response.Status.BAD_REQUEST).entity("Base64 decoding failed").build();
        }

        try{
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(keySpec);
            
            EncryptionSingleton.getInstance().setPublicKey(publicKey);
        }catch(NoSuchAlgorithmException e){
            LOG.error("The algorithm of the Public Key is not supported!");
            return Response.status(Response.Status.BAD_REQUEST).entity("Algorithm is not supported").build();
        } catch(InvalidKeySpecException e){
            LOG.error("The key specs of the Public key are invalid!");
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid key specs").build();
        }
        LOG.info("Public Key was accepted! info: " + publicKeyString);
        return Response.accepted().build();
    }
}

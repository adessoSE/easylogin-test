package de.adesso.controller;

import java.io.StringReader;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.util.Base64;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import de.adesso.exception.EncryptionException;
import de.adesso.exception.TicketException;
import de.adesso.model.EncryptionSingleton;
import de.adesso.model.KeystoreSingleton;
import de.adesso.model.Ticket;
import de.adesso.model.TicketSingleton;
import de.adesso.service.TicketEncrypter;
import de.adesso.service.TicketGenerator;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;


@Path("/ticket")
public class TicketResource {
  
    @PUT
    @APIResponse(
        responseCode = "202",
        description = "Ticket saved",
        content = @Content(
            mediaType = jakarta.ws.rs.core.MediaType.APPLICATION_JSON,
            schema = @Schema(implementation = TicketSingleton.class)
        )
    )
    @APIResponse(
        responseCode = "400",
        description = "Ticket not saved",
        content = @Content(
            mediaType = jakarta.ws.rs.core.MediaType.APPLICATION_JSON,
            schema = @Schema(implementation = TicketSingleton.class)
        )
    )
    public Response uploadTicket(String jsonBody){
        JsonReader jsonReader = Json.createReader(new StringReader(jsonBody));
        JsonObject jsonObject = jsonReader.readObject();

        TicketSingleton.getInstance().getTicketId(jsonObject.getString("ticketId"));
        TicketSingleton.getInstance().setTargetId(jsonObject.getString("targetId"));
        TicketSingleton.getInstance().setTargetUserId(jsonObject.getString("targetUserId"));
        TicketSingleton.getInstance().setIssuerUserId(jsonObject.getString("issuerUserId"));
        TicketSingleton.getInstance().setUserIPAddress(jsonObject.getString("userIPAddress"));
        TicketSingleton.getInstance().setAuthLevel(jsonObject.getString("authLevel"));
        TicketSingleton.getInstance().setAuthMethod(jsonObject.getString("authMethod"));
        TicketSingleton.getInstance().setIssuerId(jsonObject.getString("issuerId"));

        if (TicketSingleton.getInstance().isTicketSingletonFilled()) {
            return Response.accepted().build();
        }
        return Response.status(Response.Status.BAD_REQUEST).entity("The ticket is not fully completed").build();
    }

    @GET
    @APIResponse(
        responseCode = "200",
        description = "Returns the base64 encoded and RSA encrypted ticket",
        content = @Content(
            mediaType = jakarta.ws.rs.core.MediaType.APPLICATION_JSON,
            schema = @Schema(implementation = TicketSingleton.class)
        )
    )
    @APIResponse(
        responseCode = "409",
        description = "Ticket is invalid",
        content = @Content(
            mediaType = jakarta.ws.rs.core.MediaType.APPLICATION_JSON,
            schema = @Schema(implementation = TicketSingleton.class)
        )
    )
    public Response getTicket(@Context ContainerRequestContext requestContext){
        
        KeyStore keystore = KeystoreSingleton.getInstance().getKeystore();
        String keystoreAlias = KeystoreSingleton.getInstance().getKeystoreAlias();
        String keystoreAliasPassword = KeystoreSingleton.getInstance().getKeystoreAliasPassword();
        String keystoreDigitalSignature = KeystoreSingleton.getInstance().getKeystoreDigitalSignature();

        PrivateKey privateKeyIssuer;
        String xmlTicketString;
        byte[] encryptedTicketBytes;

        try {
            privateKeyIssuer = (PrivateKey) keystore.getKey(keystoreAlias, keystoreAliasPassword.toCharArray());
        } catch (UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException e) {
            // keystore exceptions
           return Response.status(Response.Status.CONFLICT).entity("Keystore alias or password is incorrect").build();        
        }

        try {
            if(!TicketSingleton.getInstance().isTicketSingletonFilled()){
                throw new TicketException("The ticket is not fully completed");
            }

            Ticket ticket = new Ticket(
                TicketSingleton.getInstance().getTicketId(),
                TicketSingleton.getInstance().getTargetId(),
                TicketSingleton.getInstance().getTargetUserId(),
                TicketSingleton.getInstance().getIssuerUserId(),
                TicketSingleton.getInstance().getUserIPAddress(),
                TicketSingleton.getInstance().getAuthLevel(),
                TicketSingleton.getInstance().getAuthMethod(),
                TicketSingleton.getInstance().getIssuerId()
            );
        
            xmlTicketString = TicketGenerator.createTicketElement(ticket, privateKeyIssuer, keystoreDigitalSignature, keystoreAlias);
            
            PublicKey publicKeyController = EncryptionSingleton.getInstance().getPublicKey();

            encryptedTicketBytes = TicketEncrypter.encrypt(xmlTicketString.getBytes(), publicKeyController);
                        
            return Response.ok(Base64.getEncoder().encodeToString(encryptedTicketBytes)).build();
        }catch (EncryptionException e) {
            // Encryption exception
            return Response.status(Response.Status.CONFLICT).entity("Encryption failed").build();
        }catch (TicketException e) {
            // Ticket exception
            return Response.status(Response.Status.CONFLICT).entity("Unable to create ticket").build();
        }
    }
}

package de.adesso.controller;

import java.io.File;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Optional;

import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestForm;

import de.adesso.model.KeystoreSingleton;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/keystore")
public class KeystoreResource {

    private static final Logger LOG = Logger.getLogger(KeystoreResource.class);

    @PUT
    @APIResponse(
        responseCode = "202",
        description = "Keystore accepted",
        content = @Content(
            mediaType = jakarta.ws.rs.core.MediaType.APPLICATION_JSON,
            schema = @Schema(implementation = KeystoreSingleton.class)
        )
    )
    @APIResponse(
        responseCode = "409",
        description = "Keystore is invalid. Check password and alias",
        content = @Content(
            mediaType = jakarta.ws.rs.core.MediaType.APPLICATION_JSON,
            schema = @Schema(implementation = KeystoreSingleton.class)
        )
    )
    public Response uploadKeystore(@RestForm File keystoreFile,
                                @RestForm String keystoreAlias,
                                @RestForm String keystorePassword,
                                @RestForm String keystoreAliasPassword,
                                @RestForm String keystoreDigitalSignature){
        
        KeyStore keystore = null;
        try {
            keystore = KeyStore.getInstance(keystoreFile, keystorePassword.toCharArray());
        } catch (IOException | NoSuchAlgorithmException | CertificateException | KeyStoreException e) {
            // send a 409
            LOG.error("Keystore was rejected! Keystore values are invalid");
            return Response.status(Response.Status.CONFLICT).entity("Keystore values are invalid").build();
        }

        try {
            Optional.ofNullable(keystore.getCertificate(keystoreAlias)).orElseThrow(() -> new KeyStoreException("Wrong keystore alias"));
        }catch(KeyStoreException e){
            // send a 409
            LOG.error("Keystore was rejected! Keystore alias is incorrect");
            return Response.status(Response.Status.CONFLICT).entity("Keystore alias is incorrect").build();
        }

        try {
            Optional.ofNullable(keystore.getKey(keystoreAlias, keystoreAliasPassword.toCharArray()))
                .orElseThrow(() -> new KeyStoreException("Wrong keystore alias or password"));
        }catch (UnrecoverableKeyException e) {
            // send a 409
            LOG.error("Keystore was rejected! Keystore password is incorrect");
            return Response.status(Response.Status.CONFLICT).entity("Keystore password is incorrect").build();
        } catch (NoSuchAlgorithmException | KeyStoreException e) {
            // send a 409
            LOG.error("Keystore was rejected! Keystore values are invalid");
            return Response.status(Response.Status.CONFLICT).entity("Keystore values are invalid").build();
        }
        
        KeystoreSingleton.getInstance().setKeystore(keystore);
        KeystoreSingleton.getInstance().setKeystoreAlias(keystoreAlias);
        KeystoreSingleton.getInstance().setKeystorePassword(keystorePassword);
        KeystoreSingleton.getInstance().setKeystoreAliasPassword(keystoreAliasPassword);
        KeystoreSingleton.getInstance().setKeystoreDigitalSignautre(keystoreDigitalSignature);
    
        if(KeystoreSingleton.getInstance().isKeystoreSingletonFilled()){
            LOG.info("Keystore was accepted!");
            return Response.accepted().entity("Keystore accepted").build();
        }
        LOG.error("Keystore was rejected!");
        return Response.status(Response.Status.CONFLICT).entity("Keystore values are invalid").build();
    }
}

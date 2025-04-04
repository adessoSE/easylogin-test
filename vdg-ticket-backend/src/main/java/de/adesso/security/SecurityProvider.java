package de.adesso.security;

import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import io.quarkus.runtime.Startup;
import jakarta.enterprise.context.ApplicationScoped;

@Startup
@ApplicationScoped
public class SecurityProvider {
    static {
        Security.addProvider(new BouncyCastleProvider());
    }
}

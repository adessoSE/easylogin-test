package de.adesso.model;

import java.security.PublicKey;

public class EncryptionSingleton {
    private static EncryptionSingleton instance;
    PublicKey publicKey;

    private EncryptionSingleton(){};

    public static synchronized EncryptionSingleton getInstance(){
        if (instance == null) {
            instance = new EncryptionSingleton();
        }
        return instance;
    }

    public synchronized void setPublicKey(PublicKey publicKey){
        this.publicKey = publicKey;
    }

    public synchronized PublicKey getPublicKey(){
        return publicKey;
    }

    public synchronized Boolean isEncryptionSingletonFilled(){
        return (getPublicKey() != null);
    }
}

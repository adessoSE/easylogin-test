package de.adesso.model;

import java.security.KeyStore;

public class KeystoreSingleton {
    private static KeystoreSingleton instance;
    private KeyStore keystore;
    private String keystoreAlias;
    private String keystorePassword;
    private String keystoreAliasPassword;
    private String keystoreDigitalSignature;

    private KeystoreSingleton(){};

    public static synchronized KeystoreSingleton getInstance(){
        if (instance == null) {
            instance = new KeystoreSingleton();
        }
        return instance;
    }

    public synchronized void setKeystore(KeyStore keystore){
        this.keystore = keystore;
    }

    public synchronized KeyStore getKeystore(){
        return keystore;
    }

    public synchronized void setKeystoreAlias(String keystoreAlias){
        this.keystoreAlias = keystoreAlias;
    }

    public synchronized String getKeystoreAlias(){
        return keystoreAlias;
    }

    public synchronized void setKeystorePassword(String keystorePassword){
        this.keystorePassword = keystorePassword;
    }

    public synchronized String getKeystorePassword(){
        return keystorePassword;
    }

    public synchronized void setKeystoreAliasPassword(String aliasPassword){
        this.keystoreAliasPassword = aliasPassword;
    }

    public synchronized String getKeystoreAliasPassword(){
        return keystoreAliasPassword;
    }

    public synchronized void setKeystoreDigitalSignautre(String keystoreDigitalSignature){
        this.keystoreDigitalSignature = keystoreDigitalSignature;
    }

    public synchronized String getKeystoreDigitalSignature(){
        return keystoreDigitalSignature;
    }

    public synchronized Boolean isKeystoreSingletonFilled(){
        return (getKeystore() != null 
            && (getKeystoreAlias() != null && getKeystoreAlias() != "") 
            && (getKeystorePassword() != null && getKeystorePassword() != "") 
            && (getKeystoreAliasPassword() != null && getKeystoreAliasPassword() != "") 
            && (getKeystoreDigitalSignature() != null && getKeystoreDigitalSignature() != ""));
    }
}

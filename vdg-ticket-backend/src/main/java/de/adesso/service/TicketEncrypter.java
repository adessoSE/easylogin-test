package de.adesso.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.stream.IntStream;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import de.adesso.exception.EncryptionException;

public class TicketEncrypter {
    public static byte[] decrypt(byte[] ticketString, Cipher decryptionCipher) throws Exception {
    	try {
    		return doEncryption(ticketString, decryptionCipher);
    	} catch (Exception exception) {
			throw new Exception("Encryption failed: " + exception, exception);
		}
    }
    
    public static byte[] encrypt(byte[] ticketString, PublicKey publicKeyController) throws EncryptionException{
		Cipher encryptionCipher;
		try {
			encryptionCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "BC");
			encryptionCipher.init(Cipher.ENCRYPT_MODE, publicKeyController);
			return doEncryption(ticketString, encryptionCipher);
		}catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException e) {
			throw new EncryptionException("Cipher is invalid");
		} catch (RuntimeException e) {
			throw new EncryptionException(e.getMessage());
		}catch (IOException e) {
			throw new EncryptionException("IO failure");
		}
	}
    
    private static byte[] doEncryption(byte[] ticketString, Cipher cipher) throws RuntimeException, IOException {
    	if(ticketString.length == 0 || ticketString == null){
			return null;
		}
		
		int cipherBlockSize = cipher.getBlockSize();
		int numberOfBlocks = (int) Math.ceil((double) ticketString.length / (double) cipherBlockSize);
		
		ArrayList<byte[]> byteArrayList = new ArrayList<byte[]>();

		IntStream.range(0, numberOfBlocks).mapToObj(blockCounter -> {
			int inputLength = Math.min(cipherBlockSize, ticketString.length - blockCounter * cipherBlockSize);
			try {
				return cipher.doFinal(ticketString, blockCounter * cipherBlockSize, inputLength);
			} catch (BadPaddingException | IllegalBlockSizeException e) {
				throw new RuntimeException("Cipher encryption failed");
			}
		}).forEach(byteArrayList::add);

		return combineByteBlocks(byteArrayList);
    }
    
    private static byte[] combineByteBlocks(ArrayList<byte[]> byteBlocks) throws IOException{
    	ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		for (byte[] block : byteBlocks) {
			outputStream.write(block);
		}
		outputStream.close();
        
    	return outputStream.toByteArray();
    }
    
    public static String signature(byte[] message, String signatureAlgorithm, PrivateKey privateKeyIssuer) throws SignatureException {
    	Signature signSignature;
		try {
			signSignature = Signature.getInstance(signatureAlgorithm);
			signSignature.initSign(privateKeyIssuer);
	        signSignature.update(message);
	        byte[] signedTicket = signSignature.sign();

	        return Base64.getEncoder().encodeToString(signedTicket);
		} catch (NoSuchAlgorithmException e) {
			throw new SignatureException("Signature Algorithmn is not supported");
		} catch ( InvalidKeyException e){
			throw new SignatureException("Private key is invalid");
		}
    }
}

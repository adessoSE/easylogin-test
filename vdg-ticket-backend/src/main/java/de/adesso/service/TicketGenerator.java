package de.adesso.service;

import java.security.PrivateKey;
import java.security.SignatureException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import de.adesso.exception.TicketException;
import de.adesso.model.Ticket;

import java.util.Date;

public class TicketGenerator {
    public static String createTicketElement(
			PrivateKey privateKeyIssuerKey, 
			String signatureAlgorithm, 
			String keyId,  // keystore alias
			String ticketId,
			String targetId, 
			String targetUserId, 
			String issuerUserId, 
			String userIPAddress, 
			String authLevel,
			String authMethod, 
			String issuerId) throws TicketException{
		String ticketInfoString = createTicketInfoElement(ticketId,
														targetId, 
														targetUserId, 
														issuerUserId, 
														userIPAddress, 
														authLevel,
														authMethod, 
														issuerId,
														privateKeyIssuerKey);

		return "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"
				+ "<Ticket>"
		        	+ createSignatureElement(ticketInfoString, signatureAlgorithm, keyId, privateKeyIssuerKey)
		        	+ "<TicketInfo>"
		    			+ ticketInfoString
		    		+ "</TicketInfo>"
				+ "</Ticket>";
	}

	public static String createTicketElement(Ticket ticket, PrivateKey privateKeyIssuerKey,String signatureAlgorithm,String keyId) throws TicketException{		
		String ticketInfoString = createTicketInfoElement(
				ticket.getTicketId(),
				ticket.getTargetId(), 
				ticket.getTargetUserId(), 
				ticket.getIssuerUserId(), 
				ticket.getUserIPAddress(), 
				ticket.getAuthLevel(),
				ticket.getAuthMethod(), 
				ticket.getIssuerId(),
				privateKeyIssuerKey);
		return "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"
				+ "<Ticket>"
		        	+ createSignatureElement(ticketInfoString, signatureAlgorithm, keyId, privateKeyIssuerKey)
		        	+ "<TicketInfo>"
		    			+ ticketInfoString
		    		+ "</TicketInfo>"
				+ "</Ticket>";
	}
		
	private  static String createSignatureElement(String ticketInfoString, String signatureAlgorithm, String keyId, PrivateKey privateKeyIssuerKey) throws TicketException{
		return "<Signature>"
        		+ "<SignatureAlgorithm>" + signatureAlgorithm + "</SignatureAlgorithm>"
        		+ "<SignatureValue>" + signingTicketInfoElement(ticketInfoString, signatureAlgorithm, privateKeyIssuerKey) + "</SignatureValue>"
        		+ "<KeyInfo>"
        			+ "<KeyId>" + keyId + "</KeyId>" // Keystore alias
        		+ "</KeyInfo>"
    		+ "</Signature>";
	}
	
	private static String createTicketInfoElement(String ticketId,
												String targetId, 
												String targetUserId, 
												String issuerUserId, 
												String userIPAddress, 
												String authLevel,
												String authMethod, 
												String issuerId,
												PrivateKey privateKeyIssuerKey) {
        return "<TicketId>" + ticketId + "</TicketId>"
    		+ "<TargetId>" + targetId + "</TargetId>"
    		+ "<TargetUserId>" + targetUserId + "</TargetUserId>"
    		+ "<IssuerUserId>" + issuerUserId + "</IssuerUserId>"
    		+ "<UserIPAddress>" + userIPAddress + "</UserIPAddress>"
    		+ "<AuthLevel>" + authLevel + "</AuthLevel>"
    		+ "<AuthMethod>" + authMethod + "</AuthMethod>" // This parameter is actually useless because the controller returns always true 
    		+ "<AuthTimestamp>" + getFormattedCurrentTime() + "</AuthTimestamp>"
			+ "<IssuerId>" + issuerId + "</IssuerId>"
			+ "<IssueTimestamp>" + getFormattedCurrentTime() + "</IssueTimestamp>";
	}
	
	private static String getFormattedCurrentTime() {
        // Time for AuthTimestamp and IssueTimestamp
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS'Z'");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date currentDate = new Date();
        return simpleDateFormat.format(currentDate);
	}
	
	public static String signingTicketInfoElement(String ticketInfoString, String signatureAlgorithm, PrivateKey privateKeyIssuerKey) throws TicketException{
        //Signs the ticketInfo String
        //The return value is already Base64 encoded!
        try {
			return TicketEncrypter.signature(ticketInfoString.getBytes(), 
											signatureAlgorithm, 
											privateKeyIssuerKey);
		} catch (SignatureException e) {
			e.printStackTrace();
			throw new TicketException(e.getMessage());
		}
	}
}

package de.adesso.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class Ticket {
    public String ticketId;
    public String targetId; 
    public String targetUserId; 
    public String issuerUserId; 
    public String userIPAddress; 
    public String authLevel;
    public String authMethod; 
    public String issuerId;
}

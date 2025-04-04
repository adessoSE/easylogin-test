package de.adesso.model;

public class TicketSingleton {
    private static TicketSingleton instance;
    private String ticketId;
    private String targetId; 
    private String targetUserId; 
    private String issuerUserId; 
    private String userIPAddress; 
    private String authLevel;
    private String authMethod; 
    private String issuerId;

    private TicketSingleton(){};

    public static synchronized TicketSingleton getInstance(){
        if (instance == null) {
            instance = new TicketSingleton();
        }
        return instance;
    }

    public synchronized void getTicketId(String ticketId){
        this.ticketId = ticketId;
    }

    public synchronized String getTicketId(){
        return ticketId;
    }

    public synchronized void setTargetId(String targetId){
        this.targetId = targetId;
    }

    public synchronized String getTargetId(){
        return targetId;
    }
    public synchronized void setTargetUserId(String targetUserId){
        this.targetUserId = targetUserId;
    }

    public synchronized String getTargetUserId(){
        return targetUserId;
    }
    public synchronized void setIssuerUserId(String issuerUserId){
        this.issuerUserId = issuerUserId;
    }

    public synchronized String getIssuerUserId(){
        return issuerUserId;
    }
    public synchronized void setUserIPAddress(String userIPAddress){
        this.userIPAddress = userIPAddress;
    }

    public synchronized String getUserIPAddress(){
        return userIPAddress;
    }
    public synchronized void setAuthLevel(String authLevel){
        this.authLevel = authLevel;
    }

    public synchronized String getAuthLevel(){
        return authLevel;
    }
    public synchronized void setAuthMethod(String authMethod){
        this.authMethod = authMethod;
    }

    public synchronized String getAuthMethod(){
        return authMethod;
    }
    public synchronized void setIssuerId(String issuerId){
        this.issuerId = issuerId;
    }

    public synchronized String getIssuerId(){
        return issuerId;
    }

    public Boolean isTicketSingletonFilled(){
        return ((getTicketId() != null && getTicketId() != "")
            && (getTargetId() != null && getTargetId() != "")
            && (getTargetUserId() != null && getTargetUserId() != "")
            && (getIssuerUserId() != null && getIssuerUserId() != "") 
            && (getUserIPAddress() != null && getUserIPAddress() != "")        
            && (getAuthLevel() != null && getAuthLevel() != "") 
            && (getAuthMethod() != null && getAuthMethod() != "")        
            && (getIssuerId() != null && getIssuerId() != ""));
    }

    public Ticket getTicket(){
        return new Ticket(getTicketId(), getTargetId(), getTargetUserId(), getIssuerUserId(), getUserIPAddress(), getAuthLevel(), getAuthMethod(), getIssuerId());
    }
}

package com.pharmacy.auth.dto;

public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private String username;
    private String memberId;
    private Long expirationTime;
    
    public JwtResponse(String token, String username, String memberId, Long expirationTime) {
        this.token = token;
        this.username = username;
        this.memberId = memberId;
        this.expirationTime = expirationTime;
    }
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getMemberId() {
        return memberId;
    }
    
    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }
    
    public Long getExpirationTime() {
        return expirationTime;
    }
    
    public void setExpirationTime(Long expirationTime) {
        this.expirationTime = expirationTime;
    }
}
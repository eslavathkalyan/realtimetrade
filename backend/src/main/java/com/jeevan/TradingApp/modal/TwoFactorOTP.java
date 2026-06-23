package com.jeevan.TradingApp.modal;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Data;

import java.io.Serializable;

@Data
public class TwoFactorOTP implements Serializable {
    private String id;
    private String otp;
    @JsonProperty(access  = JsonProperty.Access.WRITE_ONLY)
    private String jwt;
    
    // Instead of full User entity, we only need the user ID
    private Long userId;
}

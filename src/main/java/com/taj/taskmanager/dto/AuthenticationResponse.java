package com.taj.taskmanager.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AuthenticationResponse {

    private String token;
    private String tokenType = "Bearer ";

    public AuthenticationResponse(String token)  {
        this.token = token;
    }
}

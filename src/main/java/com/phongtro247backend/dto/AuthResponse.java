package com.phongtro247backend.dto;

import com.phongtro247backend.entity.enums.UserRole;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private Long expiresIn;

    // User info
    private Long userId;
    private String email;
    private String fullName;
    private UserRole role;


}

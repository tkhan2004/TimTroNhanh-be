package com.phongtro247backend.dto;

import com.phongtro247backend.entity.enums.UserRole;
import lombok.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserRequest {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String avatarUrl;
    private UserRole role;
}

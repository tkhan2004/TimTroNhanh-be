package com.phongtro247backend.service;

import com.phongtro247backend.dto.AuthResponse;
import com.phongtro247backend.entity.User;

public interface GoogleAuthService {
    String getGoogleLoginUrl();
    AuthResponse handleGoogleCallBack(String code);
    User verifyGoogleToken(String idToken);

}

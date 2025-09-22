package com.phongtro247backend.service;

import com.phongtro247backend.dto.AuthResponse;

public interface GoogleAuthService {
    String getGoogleLoginUrl();
    AuthResponse handleGoogleCallBack(String code);
}

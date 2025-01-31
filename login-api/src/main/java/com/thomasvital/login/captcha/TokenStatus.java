package com.thomasvital.login.captcha;

public enum TokenStatus {

    HEALTHY,  // 效期內，無論token 新舊
    OLD;      // 效期外
}
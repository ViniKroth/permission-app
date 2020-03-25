package com.townsq.vinikroth.permissions.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@RedisHash("User")
public class UserCacheDocument implements Serializable {

    @Id
    private String email;
    private String formattedText;

    public UserCacheDocument(String email, String formattedText) {
        this.email = email;
        this.formattedText = formattedText;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFormattedText() {
        return formattedText;
    }

    public void setFormattedText(String formattedText) {
        this.formattedText = formattedText;
    }
}

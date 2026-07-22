package com.ashish.samvaad.dto;

public class PrivateChatRequest {

    private String email;

    public PrivateChatRequest() {
    }

    public PrivateChatRequest(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
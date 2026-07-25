package com.ashish.samvaad.dto;

public class CreatePrivateRoomRequest {

    private String email;

    public CreatePrivateRoomRequest() {
    }

    public CreatePrivateRoomRequest(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
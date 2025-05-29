package com.tictactoe.model;

public class RematchRequest {
    private String username;
    private boolean agree;

    public RematchRequest() {}

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public boolean isAgree() { return agree; }
    public void setAgree(boolean agree) { this.agree = agree; }
}
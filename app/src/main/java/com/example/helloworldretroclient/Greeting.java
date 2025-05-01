package com.example.helloworldretroclient;

// data class matching JSON
public class Greeting {
    String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    String to;

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}


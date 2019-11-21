package com.example.erro.API;

import java.io.Serializable;

public class GetMessageForm implements Serializable {

    private String code;
    private String username;
    private String password;
    private String gmail;

    public GetMessageForm(
            String code,
            String username,
            String password,
            String gmail) {
        this.code = code;
        this.username = username;
        this.password = password;
        this.gmail = gmail;
    }

    public String getCode() {
        return code;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getgMail() {
        return gmail;
    }

}

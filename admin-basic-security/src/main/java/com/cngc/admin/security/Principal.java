package com.cngc.admin.security;
import lombok.Data;

@Data
public class Principal {
    private String id;
    private String userName;
    public Principal(String id, String userName) {
        this.id = id;
        this.userName = userName;
    }
}

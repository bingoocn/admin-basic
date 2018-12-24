package com.cngc.admin.resource.server.domain;

import lombok.Data;

@Data
public class TokenKey {
    private String alg;
    private String value;
}

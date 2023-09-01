package com.Bridge.bridge.dto.response;

import lombok.Data;

@Data
public class AppleMemberResponse {

    private String subject;

    private String email;

    public AppleMemberResponse(String subject, String email) {
        this.subject = subject;
        this.email = email;
    }
}

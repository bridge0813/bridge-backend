package com.Bridge.bridge.dto.response.apple;

import lombok.Data;

@Data
public class AppleMemberResponse {

    private String subject;


    public AppleMemberResponse(String subject) {
        this.subject = subject;
    }
}

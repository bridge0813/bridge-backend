package com.Bridge.bridge.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChatRoomResponse {

    private String chatRoomId;

    private Long makeUserId;

    private String makeUserPhotoUrl;

    private Long receiveUserId;

    private String receiveUserPhotoUrl;

    @Builder
    public ChatRoomResponse(String chatRoomId, Long makeUserId, String makeUserPhotoUrl, Long receiveUserId, String receiveUserPhotoUrl) {
        this.chatRoomId = chatRoomId;
        this.makeUserId = makeUserId;
        this.makeUserPhotoUrl = makeUserPhotoUrl;
        this.receiveUserId = receiveUserId;
        this.receiveUserPhotoUrl = receiveUserPhotoUrl;
    }
}

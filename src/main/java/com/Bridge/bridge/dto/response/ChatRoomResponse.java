package com.Bridge.bridge.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChatRoomResponse {

    private String chatRoomId;

    private Long makeUserId;

    private Long receiveUserId;

    @Builder
    public ChatRoomResponse(String chatRoomId, Long makeUserId, Long receiveUserId) {
        this.chatRoomId = chatRoomId;
        this.makeUserId = makeUserId;
        this.receiveUserId = receiveUserId;
    }
}

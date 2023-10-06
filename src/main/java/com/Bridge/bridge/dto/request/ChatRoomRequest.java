package com.Bridge.bridge.dto.request;

import com.Bridge.bridge.domain.Chat;
import com.Bridge.bridge.domain.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import static java.util.UUID.randomUUID;

@Data
@NoArgsConstructor
public class ChatRoomRequest {

    private Long makeUserId;

    private Long receiveUserId;

    public Chat toEntity(User receiveUser) {
        return Chat.builder()
                .chatRoomId(randomUUID().toString())
                .roomName(receiveUser.getName())
                .build();
    }

}

package com.Bridge.bridge.dto.response;

import com.Bridge.bridge.domain.Chat;
import com.Bridge.bridge.domain.Message;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
public class ChatListResponse {

    private String roomId;

    private String roomName;

    private String lastMessage;

    private LocalDate lastDate;

    private LocalTime lastTime;

    public ChatListResponse(Chat chat) {
        this.roomId = chat.getChatRoomId();
        this.roomName = chat.getRoomName();
        if(!chat.getMessages().isEmpty()) {
            Message message = chat.getMessages().get(chat.getMessages().size() - 1);
            this.lastMessage = message.getContent();
            this.lastDate = message.getSendDate();
            this.lastTime = message.getSendTime();
        }
    }
}

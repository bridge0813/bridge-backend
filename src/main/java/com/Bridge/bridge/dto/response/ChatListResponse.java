package com.Bridge.bridge.dto.response;

import com.Bridge.bridge.domain.Chat;
import com.Bridge.bridge.domain.Message;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class ChatListResponse {

    private String roomId;

    private String roomName;

    private int notReadMessageCnt;  //안 읽은 메세지 수

    private String lastMessage;

    private LocalDateTime lastTime;

    public ChatListResponse(Chat chat, boolean person) {
        this.roomId = chat.getChatRoomId();
        if (person) {
            this.roomName = chat.getReceiveUser().getName();    // person이 0이면 지원자 이름
        }
        else {
            this.roomName = chat.getMakeUser().getName();       // person이 1이면 모집자 이름
        }
        if(!chat.getMessages().isEmpty()) {
            this.notReadMessageCnt = chat.getMessages().stream()
                    .filter(m -> m.isReadStat()==false)
                    .collect(Collectors.toList()).size();
            Message message = chat.getMessages().get(chat.getMessages().size() - 1);
            this.lastMessage = message.getContent();
            this.lastTime = LocalDateTime.of(message.getSendDate(), message.getSendTime());
        }
    }
}

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

    private String type;

    private LocalDateTime lastTime;

    private Long makerId;

    private Long receiverId;

    public ChatListResponse(Chat chat, boolean person) {
        this.roomId = chat.getChatRoomId();
        this.makerId = chat.getMakeUser().getId();
        this.receiverId = chat.getReceiveUser().getId();
        if (person) {
            Long receiveId = chat.getReceiveUser().getId();
            String receiveName = chat.getReceiveUser().getName();
            this.roomName = receiveName;    // person이 0이면 지원자 이름
            this.notReadMessageCnt = chat.getMessages().stream()
                    .filter(m -> m.getWriterId() == receiveId)
                    .filter(m -> m.isReadStat()==false)
                    .collect(Collectors.toList()).size();
        }
        else {
            Long makerId = chat.getMakeUser().getId();
            String makerName = chat.getMakeUser().getName();
            this.roomName = makerName;       // person이 1이면 모집자 이름
            this.notReadMessageCnt = chat.getMessages().stream()
                    .filter(m -> m.getWriterId() == makerId)
                    .filter(m -> m.isReadStat()==false)
                    .collect(Collectors.toList()).size();
        }
        if(!chat.getMessages().isEmpty()) {
            Message message = chat.getMessages().get(chat.getMessages().size() - 1);
            this.lastMessage = message.getContent();
            this.type = message.getType();
            this.lastTime = message.getSendDateTime();
        }
    }
}

package com.Bridge.bridge.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class ChatHistoryResponse {

    private List<ChatMessageResponse> chatHistory;   //채팅 기록

    public ChatHistoryResponse(List<ChatMessageResponse> chatHistory) {
        this.chatHistory = chatHistory;
    }
}

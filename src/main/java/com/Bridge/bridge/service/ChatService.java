package com.Bridge.bridge.service;

import com.Bridge.bridge.domain.Chat;
import com.Bridge.bridge.domain.Message;
import com.Bridge.bridge.domain.User;
import com.Bridge.bridge.dto.request.ChatMessageRequest;
import com.Bridge.bridge.dto.request.ChatRoomRequest;
import com.Bridge.bridge.dto.response.ChatListResponse;
import com.Bridge.bridge.dto.response.ChatMessageResponse;
import com.Bridge.bridge.dto.response.ChatMessageResponse.SenderType;
import com.Bridge.bridge.exception.notfound.NotFoundChatException;
import com.Bridge.bridge.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ChatService {

    private final ChatRepository chatRepository;

    private final UserService userService;


    /**
     * 채팅방 개설
     */
    @Transactional
    public String createChat(ChatRoomRequest chatRoomRequest) {

        User makeUser = userService.find(chatRoomRequest.getMakeUserId());
        User receiveUser = userService.find(chatRoomRequest.getReceiveUserId());

        Chat newChat = chatRoomRequest.toEntity(receiveUser);

        newChat.setChatUser(makeUser, receiveUser);

        Chat saveChat = chatRepository.save(newChat);

        return saveChat.getChatRoomId();
    }

    /**
     * 채팅방 유저 등록
     * 모집자와 지원자 구분 저장해야함
     */
    public void enterChat() {

    }

    /**
     * 채팅방 목록 조회
     * 내 기준 만든 채팅방? 아니면 내가 참여한 채팅방? 아니면 둘다? (아마도 둘 다 일듯하다)
     */
    public List<ChatListResponse> findAllChat(Long userId) {
        User findUser = userService.find(userId);

        List<ChatListResponse> chatList = findUser.getMadeChat().stream()
                .map(ChatListResponse::new)
                .collect(Collectors.toList());

        chatList.addAll(findUser.getJoinChat().stream()
                .map(ChatListResponse::new)
                .collect(Collectors.toList()));

        return chatList;
    }


    /**
     * 채팅방 조회
     */
    public List<ChatMessageResponse> findChat(String chatRoomId) {
        Chat findChat = chatRepository.findByChatRoomId(chatRoomId)
                .orElseThrow(() -> new NotFoundChatException());

        String sender = findChat.getMakeUser().getName();

        //해당 채팅방에 존재하는 메세지 불러와야 함
        List<ChatMessageResponse> messageList = findChat.getMessages().stream()
                .map(m -> {
                    if (sender.equals(m.getWriter())) {
                        return new ChatMessageResponse(m, SenderType.MAKER);
                    } else {
                        return new ChatMessageResponse(m, SenderType.APPLIER);
                    }
                }).collect(Collectors.toList());

        return messageList;
    }

    /**
     * 채팅방 메세지 저장
     */
    @Transactional
    public void saveMessage(ChatMessageRequest message) {
        Chat findChat = chatRepository.findByChatRoomId(message.getChatRoomId())
                .orElseThrow(() -> new NotFoundChatException());

        Message newMessage = Message.builder()
                .content(message.getMessage())
                .writer(message.getSender())
                .sendDate(LocalDate.now())
                .sendTime(LocalTime.now())
                .chat(findChat)
                .build();

        findChat.getMessages().add(newMessage);
    }

    /**
     * 채팅방 나가기
     */
    @Transactional
    public boolean deleteChat(String chatRoomId) {
        Chat findChat = chatRepository.findByChatRoomId(chatRoomId)
                .orElseThrow(() -> new NotFoundChatException());

        chatRepository.delete(findChat);
        return true;
    }
}

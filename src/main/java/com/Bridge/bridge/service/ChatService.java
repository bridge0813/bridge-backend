package com.Bridge.bridge.service;

import com.Bridge.bridge.domain.Chat;
import com.Bridge.bridge.domain.Message;
import com.Bridge.bridge.domain.User;
import com.Bridge.bridge.dto.request.ChatMessageRequest;
import com.Bridge.bridge.dto.request.ChatRoomRequest;
import com.Bridge.bridge.dto.response.ChatListResponse;
import com.Bridge.bridge.dto.response.ChatMessageResponse;
import com.Bridge.bridge.dto.response.ChatMessageResponse.SenderType;
import com.Bridge.bridge.dto.response.ChatRoomResponse;
import com.Bridge.bridge.exception.notfound.NotFoundChatException;
import com.Bridge.bridge.repository.ChatRepository;
import com.google.firebase.messaging.FirebaseMessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    private final AlarmService alarmService;


    /**
     * 채팅방 개설
     */
    @Transactional
    public ChatRoomResponse createChat(ChatRoomRequest chatRoomRequest) {

        User makeUser = userService.find(chatRoomRequest.getMakeUserId());
        User receiveUser = userService.find(chatRoomRequest.getReceiveUserId());

        Chat newChat = chatRoomRequest.toEntity(receiveUser);

        newChat.setChatUser(makeUser, receiveUser);

        Chat saveChat = chatRepository.save(newChat);

        return ChatRoomResponse.builder()
                .chatRoomId(saveChat.getChatRoomId())
                .receiveUserId(makeUser.getId())
                .receiveUserId(receiveUser.getId())
                .build();
    }

    /**
     * 채팅방 목록 조회
     * 내 기준 만든 채팅방? 아니면 내가 참여한 채팅방? 아니면 둘다? (아마도 둘 다 일듯하다)
     */
    public List<ChatListResponse> findAllChat(Long userId) {
        User findUser = userService.find(userId);

        List<ChatListResponse> chatList = findUser.getMadeChat().stream()
                .map(c -> new ChatListResponse(c, true))
                .collect(Collectors.toList());

        chatList.addAll(findUser.getJoinChat().stream()
                .map(c -> new ChatListResponse(c, false))
                .collect(Collectors.toList()));

        return chatList;
    }


    /**
     * 채팅방 조회
     */
    public List<ChatMessageResponse> findChat(String chatRoomId) {
        //TODO : 채팅방 이름 설정해야함

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
    public boolean saveMessage(ChatMessageRequest message) throws FirebaseMessagingException {
        Chat findChat = chatRepository.findByChatRoomId(message.getChatRoomId())
                .orElseThrow(() -> new NotFoundChatException());

        boolean connectStat = findChat.isConnectStat();

        Message newMessage = Message.builder()
                .messageUuId(message.getMessageId())
                .content(message.getMessage())
                .writer(message.getSender())
                .sendDate(LocalDate.now())
                .sendTime(LocalTime.now())
                .chat(findChat)
                .build();

        if (connectStat == false) { // 나만 접속해 있는 경우 안읽음 처리 저장
            newMessage.changeReadStat();
            // alarmService.getChatAlarm(message);
        }
        findChat.getMessages().add(newMessage);
        return connectStat;
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

    /**
     * 채팅방 접속 상태 변경
     */
    @Transactional
    public boolean changeConnectStat(String chatRoomId) {
        Chat findChat = chatRepository.findByChatRoomId(chatRoomId)
                .orElseThrow(() -> new NotFoundChatException());

        return findChat.changeConnectStat();
    }

    /**
     * 안읽은 메세지 읽음 처리
     */
    @Transactional
    public void readNotReadMessage(String chatRoomId) {
        Chat findChat = chatRepository.findByChatRoomId(chatRoomId)
                .orElseThrow(() -> new NotFoundChatException());

        findChat.getMessages().stream()
                .filter(m -> m.isReadStat() == false)
                .forEach(m -> m.changeReadStat());

    }

    /**
     * 수락 거절 처리
     */
    public ChatMessageRequest changeMessage(ChatMessageRequest message, boolean connectStat) {
        switch (message.getType()) {
            case TALK:
                message.setSendTime(LocalDateTime.now());
                break;
            case ACCEPT:
                message.setMessage("소중한 지원 감사드립니다!\n저희 프로젝트에 참여해주실래요?");
                message.setSendTime(LocalDateTime.now());
                break;
            case REJECT:
                message.setMessage("소중한 지원 감사드립니다!\n아쉽지만 다음 기회에..");
                message.setSendTime(LocalDateTime.now());
                break;
        }

        if (connectStat == false) {
            message.setReadStat(true);
        }
        return message;
    }
}

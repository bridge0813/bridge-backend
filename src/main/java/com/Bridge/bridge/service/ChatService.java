package com.Bridge.bridge.service;

import com.Bridge.bridge.domain.Chat;
import com.Bridge.bridge.domain.Message;
import com.Bridge.bridge.domain.User;
import com.Bridge.bridge.dto.request.ChatMessageRequest;
import com.Bridge.bridge.dto.request.ChatRoomRequest;
import com.Bridge.bridge.dto.response.ChatListResponse;
import com.Bridge.bridge.dto.response.ChatMessageResponse;
//import com.Bridge.bridge.dto.response.ChatMessageResponse.SenderType;
import com.Bridge.bridge.dto.response.ChatRoomResponse;
import com.Bridge.bridge.exception.notfound.NotFoundChatException;
import com.Bridge.bridge.repository.ChatRepository;
import com.google.firebase.messaging.FirebaseMessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ChatService {

    private final ChatRepository chatRepository;

    private final SimpMessagingTemplate simpMessagingTemplate;

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
                .makeUserId(makeUser.getId())
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
//        List<ChatMessageResponse> messageList = findChat.getMessages().stream()
//                .map(m -> {
//                    if (sender.equals(m.getWriter())) {
//                        return new ChatMessageResponse(m, SenderType.MAKER);
//                    } else {
//                        return new ChatMessageResponse(m, SenderType.APPLIER);
//                    }
//                }).collect(Collectors.toList());

        List<ChatMessageResponse> messageList = findChat.getMessages().stream()
                .map(ChatMessageResponse::new)
                .collect(Collectors.toList());

        return messageList;
    }

    /**
     * 채팅방 메세지 저장
     */
    @Transactional
    public ChatMessageRequest saveMessage(ChatMessageRequest message) throws FirebaseMessagingException {
        Chat findChat = chatRepository.findByChatRoomId(message.getChatRoomId())
                .orElseThrow(() -> new NotFoundChatException());

        boolean connectStat = findChat.isConnectStat();

        ChatMessageRequest messageRequest = changeMessage(message, connectStat);

        Message newMessage = Message.builder()
                .messageUuId(messageRequest.getMessageId())
                .content(messageRequest.getMessage())
                .writerId(messageRequest.getSenderId())
                .sendDateTime(messageRequest.getSendTime())
                .type(message.getType().name())
                .chat(findChat)
                .build();

        if (connectStat == false) { // 나만 접속해 있는 경우 안읽음 처리 저장
            newMessage.changeReadStat();
            // alarmService.getChatAlarm(message);
        }
        findChat.getMessages().add(newMessage);
        return messageRequest;
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
    public void readNotReadMessage(String chatRoomId,String userId) {
        Chat findChat = chatRepository.findByChatRoomId(chatRoomId)
                .orElseThrow(() -> new NotFoundChatException());

        //자기것이 아니면 읽음 처리하면 안됌
        findChat.getMessages().stream()
                .filter(m -> m.getWriterId() != Long.parseLong(userId))
                .filter(m -> m.isReadStat() == false)
                .forEach(m -> m.changeReadStat());
    }

    /**
     * 메세지 타입에 따른 전송 메세지 변경 함수
     */
    private ChatMessageRequest changeMessage(ChatMessageRequest message, boolean connectStat) {
        switch (message.getType()) {
            case TALK:
                break;
            case ACCEPT:
                message.setMessage("소중한 지원 감사드립니다!\n저희 프로젝트에 참여해주실래요?");
                break;
            case REJECT:
                message.setMessage("소중한 지원 감사드립니다!\n아쉽지만 다음 기회에..");
                break;
        }
        LocalDateTime sendTime = LocalDateTime.now(ZoneId.of("Asia/Tokyo")).withNano(0);
        message.setSendTime(sendTime);
        if (connectStat == false) {
            message.setReadStat(true);
        }
        log.info("changed message = {}", message.getMessage());
        log.info("message readStat = {}", message.isReadStat());
        return message;
    }

    /**
     * 클라이언트로 메세지 전송 함수
     */
    public void sendMesssage(ChatMessageRequest message) {
        simpMessagingTemplate.convertAndSend("/sub/chat/room/" + message.getChatRoomId(), message);
        log.info("메세지 정상 전송 완료");
    }
}

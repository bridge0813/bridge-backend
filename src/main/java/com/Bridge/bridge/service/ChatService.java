package com.Bridge.bridge.service;

import com.Bridge.bridge.domain.Chat;
import com.Bridge.bridge.domain.Message;
import com.Bridge.bridge.domain.User;
import com.Bridge.bridge.dto.request.ChatMessageRequest;
import com.Bridge.bridge.dto.request.ChatRoomRequest;
import com.Bridge.bridge.dto.response.ChatHistoryResponse;
import com.Bridge.bridge.dto.response.ChatListResponse;
import com.Bridge.bridge.dto.response.ChatMessageResponse;
import com.Bridge.bridge.dto.response.ChatRoomResponse;
import com.Bridge.bridge.exception.notfound.NotFoundChatException;
import com.Bridge.bridge.repository.ChatRepository;
import com.Bridge.bridge.util.Constant;
import com.google.firebase.messaging.FirebaseMessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    private final MessageSender messageSender;

    private final UserService userService;

    private final AlarmService alarmService;


    /**
     * 채팅방 개설
     */
    @Transactional
    public ChatRoomResponse createChat(ChatRoomRequest chatRoomRequest) {

        User makeUser = userService.find(chatRoomRequest.getMakeUserId());
        User receiveUser = userService.find(chatRoomRequest.getReceiveUserId());

        Chat chat = chatRepository.findByMakeUserAndReceiveUser(makeUser, receiveUser)
                .orElseGet(() -> {
                   Chat newChat =  chatRoomRequest.toEntity();
                   newChat.setChatUser(makeUser, receiveUser);
                   return chatRepository.save(newChat);
                });

        return ChatRoomResponse.builder()
                .chatRoomId(chat.getChatRoomId())
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
    public ChatHistoryResponse getChatHistory(String chatRoomId) {

        Chat findChat = chatRepository.findByChatRoomId(chatRoomId)
                .orElseThrow(() -> new NotFoundChatException());

        //해당 채팅방에 존재하는 메세지 불러와야 함
        List<ChatMessageResponse> messageList = findChat.getMessages().stream()
                .map(ChatMessageResponse::new)
                .collect(Collectors.toList());

        return new ChatHistoryResponse(messageList);
    }

    /**
     * 채팅방 메세지 저장
     */
    @Transactional
    public ChatMessageRequest saveMessage(ChatMessageRequest message) throws FirebaseMessagingException {
        Chat findChat = chatRepository.findByChatRoomId(message.getChatRoomId())
                .orElseThrow(() -> new NotFoundChatException());

        //현 채팅방 인원 정보 불러오기
        boolean connectStat = findChat.isConnectStat();

        // 메세지 타입에 따른 메세지 변경 및 시간 입력
        ChatMessageRequest messageRequest = changeMessage(message, connectStat);

        Message newMessage = Message.builder()
                .messageUuId(messageRequest.getMessageId())
                .content(messageRequest.getMessage())
                .writerId(messageRequest.getSenderId())
                .sendDateTime(messageRequest.getSendTime())
                .readStat(messageRequest.isReadStat())
                .type(message.getType().name())
                .chat(findChat)
                .build();

        findChat.getMessages().add(newMessage);

        // 나만 접속해 있는 경우 알람 발송
        if (connectStat == true) {
             alarmService.getChatAlarm(messageRequest);
        }

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
     * 채팅방 접속 상태
     * 1명만 접속해 있는 경우 -> connectStat : True
     * 2명 다 접속해 있는 경우 -> connectStat : False
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

        User findSender = userService.find(Long.parseLong(userId));

        //자기 채팅이 아니면 읽음 처리하면 안됌
        findChat.getMessages().stream()
                .filter(m -> m.getWriterId() != findSender.getId())
                .filter(m -> m.isReadStat() == false)
                .forEach(m -> m.changeReadStat());
    }

    /**
     * 메세지 변경 함수
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

        // 현 인원에 따른 메세지 읽음 여부 변경
        ChatMessageRequest changedMessage = changeReadStat(message, connectStat);

        log.info("message type = {}", changedMessage.getType());
        log.info("changed message = {}", changedMessage.getMessage());
        log.info("message readStat = {}", changedMessage.isReadStat());
        return message;
    }

    /**
     * 메세지 읽음 여부 변경
     */
    private ChatMessageRequest changeReadStat(ChatMessageRequest message, boolean connectStat) {
        if (connectStat == false) {
            message.setReadStat(true);
            return message;
        }
        return message;
    }

    /**
     * 클라이언트로 메세지 전송 함수
     */
    public void sendMesssage(ChatMessageRequest message) {
        messageSender.send(Constant.KAFKA_TOPIC, message);
        log.info("메세지 카프키로 정상 전송 완료");
    }

    /**
     * 채팅 메세지 업데이트 안읽음 -> 읽음
     */
    public void updateChatHistory(String chatRoomId) {
        ChatHistoryResponse chatHistory = getChatHistory(chatRoomId);

        if (!chatHistory.getChatHistory().isEmpty()) {
            ChatMessageRequest messageRequest = ChatMessageRequest.builder()
                    .chatRoomId(chatRoomId)
                    .chatHistory(chatHistory)
                    .build();

            sendMesssage(messageRequest);
        }
    }
}

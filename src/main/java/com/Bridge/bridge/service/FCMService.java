package com.Bridge.bridge.service;

import com.Bridge.bridge.domain.Chat;
import com.Bridge.bridge.domain.User;
import com.Bridge.bridge.dto.request.ChatMessageRequest;
import com.Bridge.bridge.dto.request.NotificationRequestDto;
import com.Bridge.bridge.exception.BridgeException;
import com.Bridge.bridge.exception.notfound.NotFoundChatException;
import com.Bridge.bridge.exception.notfound.NotFoundUserException;
import com.Bridge.bridge.repository.ChatRepository;
import com.Bridge.bridge.repository.UserRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class FCMService {

    private final FirebaseMessaging firebaseMessaging;
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;

    @Transactional
    public void saveDeviceToken(String deviceToken){

        User user = userRepository.findByDeviceToken(deviceToken);

        if(user != null){ // 이미 등록된 유저라면
            user.updateDeviceToken(deviceToken);
            return;
        }

        // 새로운 유저라면 등록하기
        User newUser = new User(deviceToken);
        userRepository.save(newUser);
        return;
    }

    public void sendNotification(NotificationRequestDto notificationRequestDto) throws FirebaseMessagingException {

        // 알림 받을 유저 찾기
        User user = userRepository.findById(notificationRequestDto.getUserID())
                .orElseThrow(() -> new NotFoundUserException());

        // 알림 생성하기
        Notification notification = Notification.builder()
                .setTitle(notificationRequestDto.getTitle())
                .setBody(notificationRequestDto.getBody())
                .build();

        // 알림 메세지 생성하기
        Message message = Message.builder()
                .setToken(user.getDeviceToken())
                .setNotification(notification)
                .build();

        try {
            firebaseMessaging.send(message);
        }
        catch (FirebaseMessagingException e){
            throw new BridgeException(HttpStatus.NOT_ACCEPTABLE, "알림 보내기가 실패하였습니다.", 500);
        }


    }

    // 채팅 받을 시 알림 보내기
    public void getChatAlarm(ChatMessageRequest chatMessageRequest) throws FirebaseMessagingException {
        Chat chat = chatRepository.findByChatRoomId(chatMessageRequest.getChatRoomId())
                .orElseThrow(() -> new NotFoundChatException());

        User sender = userRepository.findByName(chatMessageRequest.getSender());

        if(sender.equals(chat.getMakeUser())){ // 메세지를 보낸 사람이 채팅방을 만든 사람이라면

            // TODO : 알림 객체로 저장하기

            // 알림보내기
            NotificationRequestDto notificationRequestDto = NotificationRequestDto.builder()
                    .userID(chat.getReceiveUser().getId())
                    .title(sender.getName())
                    .body(chatMessageRequest.getMessage())
                    .build();

            sendNotification(notificationRequestDto);
            return;
        }
        // 메세지를 보낸 사람이 채팅방에 초대된 사람이라면

        // TODO : 알림 객체로 저장하기

        // 알림보내기
        NotificationRequestDto notificationRequestDto = NotificationRequestDto.builder()
                .userID(chat.getMakeUser().getId())
                .title(sender.getName())
                .body(chatMessageRequest.getMessage())
                .build();

        sendNotification(notificationRequestDto);
        return;

    }
}

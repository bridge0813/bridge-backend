package com.Bridge.bridge.service;

<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
import com.Bridge.bridge.domain.Chat;
import com.Bridge.bridge.domain.Project;
<<<<<<< HEAD
import com.Bridge.bridge.domain.User;
import com.Bridge.bridge.dto.request.ChatMessageRequest;
import com.Bridge.bridge.dto.request.NotificationRequestDto;
import com.Bridge.bridge.exception.BridgeException;
import com.Bridge.bridge.exception.notfound.NotFoundChatException;
import com.Bridge.bridge.exception.notfound.NotFoundProjectException;
import com.Bridge.bridge.exception.notfound.NotFoundUserException;
import com.Bridge.bridge.repository.ChatRepository;
import com.Bridge.bridge.repository.ProjectRepository;
import com.Bridge.bridge.repository.UserRepository;
=======
import com.Bridge.bridge.dto.request.NotificationRequestDto;
import com.Bridge.bridge.exception.BridgeException;
>>>>>>> 304d99b (FEAT : FCMService 생성)
=======
=======
import com.Bridge.bridge.domain.Chat;
>>>>>>> 782435c (FEAT : 채팅 수신 시 알림 보내기 기능 구현)
=======
>>>>>>> f641338 (FEAT : 지원자 발생 시 알림 보내기 기능 구현)
import com.Bridge.bridge.domain.User;
import com.Bridge.bridge.dto.request.ChatMessageRequest;
import com.Bridge.bridge.dto.request.NotificationRequestDto;
import com.Bridge.bridge.exception.BridgeException;
import com.Bridge.bridge.exception.notfound.NotFoundChatException;
import com.Bridge.bridge.exception.notfound.NotFoundProjectException;
import com.Bridge.bridge.exception.notfound.NotFoundUserException;
import com.Bridge.bridge.repository.ChatRepository;
import com.Bridge.bridge.repository.ProjectRepository;
import com.Bridge.bridge.repository.UserRepository;
>>>>>>> a345cec (FIX : deviceToken 속성 추가)
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
<<<<<<< HEAD
<<<<<<< HEAD
import org.springframework.transaction.annotation.Transactional;
=======
>>>>>>> 304d99b (FEAT : FCMService 생성)
=======
import org.springframework.transaction.annotation.Transactional;
>>>>>>> c4e2fb2 (FEAT : 앱 실행 시 DeviceToken 저장하기 기능 구현)

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class FCMService {

    private final FirebaseMessaging firebaseMessaging;
<<<<<<< HEAD
<<<<<<< HEAD
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final ProjectRepository projectRepository;

    /*
       Func : 알림 받을 디바이스 토큰 저장하기
       Parameter: deviceToken
    */
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

    /*
       Func : FCM 서버로 알림 보내기 -> 실질적으로 알림보내는 함수
       Parameter: NotificationRequestDto -> 알림 수신자, 알림 제목, 알림 내용
    */
    public void sendNotification(NotificationRequestDto notificationRequestDto) throws FirebaseMessagingException {

        // 알림 받을 유저 찾기
        User user = userRepository.findById(notificationRequestDto.getUserID())
                .orElseThrow(() -> new NotFoundUserException());

        // 알림 생성하기
=======

    public void sendNotification(NotificationRequestDto notificationRequestDto) throws FirebaseMessagingException {
>>>>>>> 304d99b (FEAT : FCMService 생성)
=======
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final ProjectRepository projectRepository;

    /*
       Func : 알림 받을 디바이스 토큰 저장하기
       Parameter: deviceToken
    */
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

    /*
       Func : FCM 서버로 알림 보내기 -> 실질적으로 알림보내는 함수
       Parameter: NotificationRequestDto -> 알림 수신자, 알림 제목, 알림 내용
    */
    public void sendNotification(NotificationRequestDto notificationRequestDto) throws FirebaseMessagingException {

        // 알림 받을 유저 찾기
        User user = userRepository.findById(notificationRequestDto.getUserID())
                .orElseThrow(() -> new NotFoundUserException());

        // 알림 생성하기
>>>>>>> a345cec (FIX : deviceToken 속성 추가)
        Notification notification = Notification.builder()
                .setTitle(notificationRequestDto.getTitle())
                .setBody(notificationRequestDto.getBody())
                .build();

<<<<<<< HEAD
<<<<<<< HEAD
        // 알림 메세지 생성하기
        Message message = Message.builder()
                .setToken(user.getDeviceToken())
=======
        Message message = Message.builder()
                .setToken(notificationRequestDto.getDeviceToken())
>>>>>>> 304d99b (FEAT : FCMService 생성)
=======
        // 알림 메세지 생성하기
        Message message = Message.builder()
                .setToken(user.getDeviceToken())
>>>>>>> a345cec (FIX : deviceToken 속성 추가)
                .setNotification(notification)
                .build();

        try {
            firebaseMessaging.send(message);
        }
        catch (FirebaseMessagingException e){
            throw new BridgeException(HttpStatus.NOT_ACCEPTABLE, "알림 보내기가 실패하였습니다.", 500);
        }


    }
<<<<<<< HEAD
<<<<<<< HEAD

    /*
       Func : 지원 결과 알림 생성
       Parameter: userId -> 알림 받을 유저ID
    */
    public void getApplyResultAlarm(Long userId) throws FirebaseMessagingException {
<<<<<<< HEAD

        // TODO : 알람 객체 생성하고 저장

        // 알림보내기
        NotificationRequestDto notificationRequestDto = NotificationRequestDto.builder()
                .userID(userId)
                .title("지원 결과 도착")
                .body("내가 지원한 프로젝트의 결과가 나왔어요. 관리 페이지에서 확인해보세요.")
                .build();

        sendNotification(notificationRequestDto);
    }

    /*
       Func : 채팅 받을 시 알림 생성
       Parameter: ChatMessageRequest -> 채팅방ID, 발신자, 채팅 내용, 메세지 타입
    */
=======

<<<<<<< HEAD
    // 채팅 받을 시 알림 보내기
>>>>>>> 782435c (FEAT : 채팅 수신 시 알림 보내기 기능 구현)
=======
    /*
       Func : 지원 결과 알림 생성
       Parameter: userId -> 알림 받을 유저ID
    */
    public void getApplyAlarm(Long userId) throws FirebaseMessagingException {
=======
>>>>>>> f641338 (FEAT : 지원자 발생 시 알림 보내기 기능 구현)

        // TODO : 알람 객체 생성하고 저장

        // 알림보내기
        NotificationRequestDto notificationRequestDto = NotificationRequestDto.builder()
                .userID(userId)
                .title("지원 결과 도착")
                .body("내가 지원한 프로젝트의 결과가 나왔어요. 관리 페이지에서 확인해보세요.")
                .build();

        sendNotification(notificationRequestDto);
    }

    /*
       Func : 채팅 받을 시 알림 생성
       Parameter: ChatMessageRequest -> 채팅방ID, 발신자, 채팅 내용, 메세지 타입
    */
>>>>>>> 03f71c0 (FIX : 지원 결과 알림 보내기 기능 Controller-Service 분리)
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
<<<<<<< HEAD
<<<<<<< HEAD
=======
>>>>>>> f641338 (FEAT : 지원자 발생 시 알림 보내기 기능 구현)

    /*
       Func : 지원자 발생 시 알림 생성
       Parameter: projectId
    */
    public void getApplyAlarm(Long projectId) throws FirebaseMessagingException {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundProjectException());

        User getAlarmuser = project.getUser();

        // TODO : 알람 객체 생성하고 저장

        // 알림보내기
        NotificationRequestDto notificationRequestDto = NotificationRequestDto.builder()
                .userID(getAlarmuser.getId())
                .title("지원자 등장?")
                .body("내 프로젝트에 누군가 지원했어요 지원자 프로필을 확인하고 채팅을 시작해보세요!")
                .build();

        sendNotification(notificationRequestDto);
    }
<<<<<<< HEAD
=======
>>>>>>> 304d99b (FEAT : FCMService 생성)
=======
>>>>>>> 782435c (FEAT : 채팅 수신 시 알림 보내기 기능 구현)
=======
>>>>>>> f641338 (FEAT : 지원자 발생 시 알림 보내기 기능 구현)
}

package com.Bridge.bridge.service;

import com.Bridge.bridge.domain.Alarm;
import com.Bridge.bridge.domain.Chat;
import com.Bridge.bridge.domain.Project;
import com.Bridge.bridge.domain.User;
import com.Bridge.bridge.dto.request.ChatMessageRequest;
import com.Bridge.bridge.dto.request.NotificationRequestDto;
import com.Bridge.bridge.dto.response.AlarmResponse;
import com.Bridge.bridge.dto.response.AllAlarmResponse;
import com.Bridge.bridge.exception.BridgeException;
import com.Bridge.bridge.exception.badrequest.AlarmDeleteException;
import com.Bridge.bridge.exception.notfound.NotFoundAlarmException;
import com.Bridge.bridge.exception.notfound.NotFoundChatException;
import com.Bridge.bridge.exception.notfound.NotFoundProjectException;
import com.Bridge.bridge.exception.notfound.NotFoundUserException;
import com.Bridge.bridge.repository.AlarmRepository;
import com.Bridge.bridge.repository.ChatRepository;
import com.Bridge.bridge.repository.ProjectRepository;
import com.Bridge.bridge.repository.UserRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlarmService {

    private final FirebaseMessaging firebaseMessaging;
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final ProjectRepository projectRepository;
    private final AlarmRepository alarmRepository;

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

    /*
       Func : 지원 결과 알림 생성
       Parameter: userId -> 알림 받을 유저ID
    */
    @Transactional
    public void getApplyResultAlarm(Long userId) throws FirebaseMessagingException {

        User rcvUser = userRepository.findById(userId)
                .orElseThrow(()->new NotFoundUserException());

        Alarm alarm = Alarm.builder()
                .type("Apply")
                .title("지원 결과 도착")
                .content("내가 지원한 프로젝트의 결과가 나왔어요. 관리 페이지에서 확인해보세요.")
                .rcvUser(rcvUser)
                .build();
        alarmRepository.save(alarm);

        rcvUser.getRcvAlarms().add(alarm);

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
    public void getChatAlarm(ChatMessageRequest chatMessageRequest) throws FirebaseMessagingException {
        Chat chat = chatRepository.findByChatRoomId(chatMessageRequest.getChatRoomId())
                .orElseThrow(() -> new NotFoundChatException());


        User sender = userRepository.findByName(chatMessageRequest.getSender());

        if(sender.equals(chat.getMakeUser())){ // 메세지를 보낸 사람이 채팅방을 만든 사람이라면

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

        // 알림보내기
        NotificationRequestDto notificationRequestDto = NotificationRequestDto.builder()
                .userID(chat.getMakeUser().getId())
                .title(sender.getName())
                .body(chatMessageRequest.getMessage())
                .build();

        sendNotification(notificationRequestDto);
        return;

    }

    /*
       Func : 지원자 발생 시 알림 생성
       Parameter: projectId
    */
    @Transactional
    public void getApplyAlarm(Long projectId) throws FirebaseMessagingException {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundProjectException());

        User getAlarmUser = project.getUser();

        Alarm alarm = Alarm.builder()
                .type("Applier")
                .title("지원자 등장?")
                .content("내 프로젝트에 누군가 지원했어요 지원자 프로필을 확인하고 채팅을 시작해보세요!")
                .rcvUser(getAlarmUser)
                .build();
        alarmRepository.save(alarm);

        List<Alarm> alarms = getAlarmUser.getRcvAlarms();
        alarms.add(alarm);

        // 알림보내기
        NotificationRequestDto notificationRequestDto = NotificationRequestDto.builder()
                .userID(getAlarmUser.getId())
                .title("지원자 등장?")
                .body("내 프로젝트에 누군가 지원했어요 지원자 프로필을 확인하고 채팅을 시작해보세요!")
                .build();

        sendNotification(notificationRequestDto);
    }

    /*
       Func : 알림 전체 목록 조회 기능
       Parameter: userId
       Return : List<AllAlarmResponse>
    */
    public List<AllAlarmResponse> getAllOfAlarms(Long userId){

        // 유저 찾기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundUserException());

        List<Alarm> alarms = alarmRepository.findAllByRcvUser(user);

        if(alarms.equals(null)){
            throw new NotFoundAlarmException();
        }

        return alarms.stream()
                .map((alarm -> AllAlarmResponse.builder()
                        .id(alarm.getId())
                        .title(alarm.getTitle())
                        .content(alarm.getContent())
                        .time(alarm.getSendDateTime())
                        .build()))
                .collect(Collectors.toList());
    }

    /*
       Func : 알림 전체 목록 삭제 기능
       Parameter: userId
       Return : boolean - 전체 삭제 여부
    */
    @Transactional
    public boolean deleteAllAlarms(Long userId){
        // 유저 찾기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundUserException());

        List<Alarm> alarms = alarmRepository.findAllByRcvUser(user);

        alarmRepository.deleteAllByRcvUser(user);
        user.getRcvAlarms().clear();

        int size = alarmRepository.findAllByRcvUser(user).size();

        if(size == 0){ // 모두 삭제되었나 확인용
            return true;
        }
        throw new AlarmDeleteException();
    }

    /*
       Func : 개별 알람 삭제 기능
       Parameter: userId, alarmId
       Return : List<AlarmResponse>
    */
    @Transactional
    public List<AlarmResponse> deleteAlarm(Long userId, Long alarmId){
        // 유저 찾기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundUserException());

        Alarm alarm = alarmRepository.findById(alarmId)
                .orElseThrow(()->new NotFoundAlarmException());

        user.getRcvAlarms().remove(alarm);
        alarmRepository.delete(alarm);

        return user.getRcvAlarms().stream()
                .map(a -> AlarmResponse.builder()
                        .alarmId(a.getId())
                        .title(a.getTitle())
                        .content(a.getContent())
                        .time(a.getSendDateTime().toString())
                        .build())
                .collect(Collectors.toList());
    }
}

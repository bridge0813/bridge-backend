package com.Bridge.bridge.service;

import com.Bridge.bridge.domain.Field;
import com.Bridge.bridge.domain.Profile;
import com.Bridge.bridge.domain.User;
import com.Bridge.bridge.dto.request.UserFieldRequest;
import com.Bridge.bridge.dto.request.UserProfileRequest;
import com.Bridge.bridge.dto.request.UserRegisterRequest;
import com.Bridge.bridge.dto.response.UserProfileResponse;
import com.Bridge.bridge.exception.notfound.NotFoundProfileException;
import com.Bridge.bridge.exception.notfound.NotFoundUserException;
import com.Bridge.bridge.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public void signUpInfo(UserRegisterRequest request) {
        saveField(request.getUserId(), request.getUserField());
        saveProfile(request.getUserId(), request.getUserProfile());
    }

    /**
     * 처음 로그인 시 개인 관심분야 등록
     */
    @Transactional
    public boolean saveField(Long userId, UserFieldRequest request) {

        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundUserException());

        List<Field> fields = request.toEntity();

        fields.stream()
                .forEach(f -> f.updateFieldUser(findUser));

        findUser.getFields().addAll(fields);
        return true;
    }

    /**
     * 처음 로그인 시 개인 프로필 등록 (보류)
     */
    @Transactional
    public boolean saveProfile(Long userId, UserProfileRequest request) {
        if (Objects.isNull(request)) {
            return false;
        }

        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundUserException());

        Profile profile = request.toEntity();
        findUser.updateProfile(profile);
        return true;
    }

    /**
     * 유저 개인 프로필 확인
     */
    public UserProfileResponse getProfile(Long userId) {
        User findUser = find(userId);

        //TODO : 파일 처리

        Profile profile = findUser.getProfile();
        if (profile == null) {
            throw new NotFoundProfileException();
        }

        return UserProfileResponse.builder()
                .name(findUser.getName())
                .selfIntro(profile.getSelfIntro())
                .fields(findUser.getFields().stream()
                        .map(f -> f.getFieldName())
                        .collect(Collectors.toList()))
                .stacks(profile.getSkill())
                .career(profile.getCareer())
                .refLink(profile.getRefLink())
                .refFile("임시 파일명")
                .build();
    }


    /**
     * 유저 찾기 메소드
     */
    public User find(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundUserException());
    }
}

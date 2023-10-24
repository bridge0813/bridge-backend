package com.Bridge.bridge.service;

import com.Bridge.bridge.domain.Field;
import com.Bridge.bridge.domain.File;
import com.Bridge.bridge.domain.Profile;
import com.Bridge.bridge.domain.User;
import com.Bridge.bridge.dto.request.UserFieldRequest;
import com.Bridge.bridge.dto.request.UserProfileRequest;
import com.Bridge.bridge.dto.response.FileResponse;
import com.Bridge.bridge.dto.response.UserProfileResponse;
import com.Bridge.bridge.exception.notfound.NotFoundProfileException;
import com.Bridge.bridge.exception.notfound.NotFoundUserException;
import com.Bridge.bridge.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final FileService fileService;

    /**
     * 처음 로그인 시 개인 관심분야 등록
     */
    @Transactional
    public boolean saveField(UserFieldRequest request) {

        User findUser = find(request.getUserId());

        List<Field> fields = request.toEntity();

        findUser.getFields().addAll(fields);
        return true;
    }

    /**
     * 개인 프로필 등록
     */
    @Transactional
    public boolean saveProfile(Long userId, UserProfileRequest request, MultipartFile profilePhoto, MultipartFile refFile) {

        User findUser = find(userId);

        Profile profile = request.toEntity();
        findUser.updateProfile(profile);

        // 프로필 사진 등록
        if (profilePhoto != null) {
            File saveFile = fileService.uploadFile(profilePhoto);
            File oldFile = profile.setProfilePhoto(saveFile);
            if (oldFile != null) {
                fileService.deleteFile(oldFile.getId());
            }
        }

        // 첨부파일 등록
        if (refFile != null) {
            File saveFile = fileService.uploadFile(refFile);
            File oldFile = profile.setProfilePhoto(saveFile);
            if (oldFile != null) {
                fileService.deleteFile(oldFile.getId());
            }
        }

        return true;
    }

    /**
     * 유저 개인 프로필 확인
     */
    public UserProfileResponse getProfile(Long userId) {
        User findUser = find(userId);

        Profile profile = findUser.getProfile();
        if (profile == null) {
            throw new NotFoundProfileException();
        }

       String photo = null;
        if (profile.getProfilePhoto() != null) {
            photo = profile.getProfilePhoto().getUploadFileUrl();
        }

        String refFile = null;
        String originName = null;
        File findRefFile = profile.getRefFile();
        if (findRefFile != null) {
            refFile = profile.getRefFile().getUploadFileUrl();
            originName = findRefFile.getOriginName();
        }

        return UserProfileResponse.builder()
                .name(findUser.getName())
                .profilePhotoURL(photo)
                .selfIntro(profile.getSelfIntro())
                .fields(findUser.getFields().stream()
                        .map(f -> f.getValue())
                        .collect(Collectors.toList()))
                .stacks(profile.getSkill())
                .career(profile.getCareer())
                .refLink(profile.getRefLink())
                .refFile(new FileResponse(refFile, originName))
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

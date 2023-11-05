package com.Bridge.bridge.service;

import com.Bridge.bridge.domain.Field;
import com.Bridge.bridge.domain.File;
import com.Bridge.bridge.domain.Profile;
import com.Bridge.bridge.domain.User;
import com.Bridge.bridge.dto.request.ProfileUpdateRequest;
import com.Bridge.bridge.dto.request.UserFieldRequest;
import com.Bridge.bridge.dto.request.UserProfileRequest;
import com.Bridge.bridge.dto.response.BookmarkListResponse;
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
     * 처음 로그인 시 개인 관심 분야 등록
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
            updatePhotoFile(profile, profilePhoto);
        }

        // 첨부파일 등록
        if (refFile != null) {
            updateRefFile(profile, refFile);
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
                .stacks(profile.getSkill().stream()
                        .map(s -> s.getValue())
                        .collect(Collectors.toList()))
                .career(profile.getCareer())
                .refLink(profile.getRefLink())
                .refFile(new FileResponse(refFile, originName))
                .build();
    }

    /**
     * 유저 프로필 수정
     */
    @Transactional
    public void updateProfile(Long userId, ProfileUpdateRequest profileUpdateRequest, MultipartFile profilePhoto,
                              MultipartFile refFile) {
        User findUser = find(userId);
        Profile profile = findUser.getProfile();
        profile.updateProfile(profileUpdateRequest);

        //파일 업데이트
        if(profilePhoto != null) {
            updatePhotoFile(profile, profilePhoto);
        }

        if(refFile != null) {
            updateRefFile(profile, refFile);
        }
    }

    /**
     * 프로필 사진 파일 업데이트
     */
    @Transactional
    public void updatePhotoFile(Profile profile, MultipartFile file) {
        File newFile = fileService.uploadFile(file);
        File oldFile = profile.setProfilePhoto(newFile);
        if(oldFile != null) {
            fileService.deleteFile(oldFile.getId());
        }
    }

    /**
     * 북마크한 프로젝트 목록
     */
    public List<BookmarkListResponse> getBookmarkProjects(Long userId) {
        User findUser = find(userId);

        return findUser.getBookmarks().stream()
                .map(b -> new BookmarkListResponse(b.getProject()))
                .collect(Collectors.toList());
    }

    /**
     * 로그아웃
     */
    @Transactional
    public boolean logout(Long userId) {
        User findUser = find(userId);
        findUser.updateRefreshToken(null);
        return true;
    }

    /**
     * 회원 탈퇴
     */
    @Transactional
    public boolean deleteUser(Long userId) {
        User findUser = find(userId);
        userRepository.delete(findUser);
        return true;
    }

    /**
     * 참조파일 업데이트
     */
    @Transactional
    public void updateRefFile(Profile profile, MultipartFile file) {
        File newFile = fileService.uploadFile(file);
        File oldFile = profile.setRefFile(newFile);
        if(oldFile != null) {
            fileService.deleteFile(oldFile.getId());
        }
    }


    /**
     * 유저 찾기 메소드
     */
    public User find(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundUserException());
    }
}

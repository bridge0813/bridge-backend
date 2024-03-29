package com.Bridge.bridge.service;

import com.Bridge.bridge.domain.Field;
import com.Bridge.bridge.domain.File;
import com.Bridge.bridge.domain.Profile;
import com.Bridge.bridge.domain.Stack;
import com.Bridge.bridge.domain.User;
import com.Bridge.bridge.dto.request.FieldUpdateRequest;
import com.Bridge.bridge.dto.request.ProfileUpdateRequest;
import com.Bridge.bridge.dto.request.UserFieldRequest;
import com.Bridge.bridge.dto.request.UserProfileRequest;
import com.Bridge.bridge.dto.response.BookmarkListResponse;
import com.Bridge.bridge.dto.response.FieldAndStackResponse;
import com.Bridge.bridge.dto.response.FileResponse;
import com.Bridge.bridge.dto.response.MyPageResponse;
import com.Bridge.bridge.dto.response.UserProfileResponse;
import com.Bridge.bridge.exception.notfound.NotFoundProfileException;
import com.Bridge.bridge.exception.notfound.NotFoundUserException;
import com.Bridge.bridge.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
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
    public boolean saveProfile(Long userId, UserProfileRequest request, MultipartFile profilePhoto, List<MultipartFile> refFiles) {

        User findUser = find(userId);

        Profile profile = request.toEntity();
        findUser.updateProfile(profile);

        if (request.getName() != null) {
            findUser.updateName(request.getName());
        }

        // 프로필 사진 등록
        if (profilePhoto != null) {
            updatePhotoFile(profile, profilePhoto);
        }

        // 첨부파일 리스트 등록
        if (refFiles != null) {
            List<File> newFiles = refFiles.stream()
                    .map(f -> fileService.uploadFile(f))
                    .collect(Collectors.toList());

            profile.setRefFiles(newFiles);
        }
        return true;
    }

    /**
     * 유저 개인 프로필 확인
     */
    public UserProfileResponse getProfile(Long userId) {
        User findUser = find(userId);

        // 프로필이 없는 경우
        Profile profile = findUser.getProfile();
        if (profile == null) {
            throw new NotFoundProfileException();
        }

        // 프로필 사진 url 불러오기
        String photo = null;
        if (profile.getProfilePhoto() != null) {
            photo = profile.getProfilePhoto().getUploadFileUrl();
        }

        // 첨부파일 리스트 불러오기
        List<FileResponse> fileResponseList = new ArrayList<>();
        List<File> refFiles = profile.getRefFiles();
        if(!refFiles.isEmpty()) {
            refFiles.stream()
                    .forEach(f -> fileResponseList.add(FileResponse.from(f)));
        }

        //필드 + 스택 변환
        List<FieldAndStackResponse> fieldAndStackResponses = new ArrayList<>();
        profile.getFieldAndStacks().stream()
                .forEach(l -> fieldAndStackResponses.add(FieldAndStackResponse.from(l)));

        return UserProfileResponse.builder()
                .name(findUser.getName())
                .profilePhotoURL(photo)
                .selfIntro(profile.getSelfIntro())
                .fieldAndStacks(fieldAndStackResponses)
                .career(profile.getCareer())
                .refLinks(profile.getRefLinks())
                .refFiles(fileResponseList)
                .build();
    }

    /**
     * 유저 관심분야 수정
     */
    @Transactional
    public boolean updateField(FieldUpdateRequest fieldUpdateRequest) {
        User findUser = find(fieldUpdateRequest.getUserId());

        findUser.updateField(fieldUpdateRequest.getFields());

        return true;
    }

    /**
     * 유저 프로필 수정
     */
    @Transactional
    public void updateProfile(Long userId, ProfileUpdateRequest profileUpdateRequest, MultipartFile profilePhoto,
                              List<MultipartFile> refFiles) {
        User findUser = find(userId);
        Profile profile = findUser.getProfile();
        findUser.updateName(profileUpdateRequest.getName());
        List<Long> oldFileIds = profile.updateProfile(profileUpdateRequest);
        if (oldFileIds != null) {
            oldFileIds.stream()
                    .forEach(id -> {
                        File file = fileService.find(id);
                        profile.getRefFiles().remove(file);
                        fileService.deleteFile(id);
                    });
        }

        //프로필 사진 업데이트
        if(profilePhoto != null) {
            updatePhotoFile(profile, profilePhoto);
        }

        // 첨부파일 리스트 업데이트
        if(refFiles != null) {
            updateRefFiles(profile, refFiles);
        }
    }

    /**
     * 프로필 사진 파일 업데이트
     */
    @Transactional
    public void updatePhotoFile(Profile profile, MultipartFile file) {
        File newFile = fileService.uploadFile(file);
        File oldFile = profile.setPhotoFile(newFile);
        if(oldFile != null) {
            fileService.deleteFile(oldFile.getId());
        }
    }

    /**
     * 참조파일 리스트 업데이트
     */
    @Transactional
    public void updateRefFiles(Profile profile, List<MultipartFile> files) {
        List<File> newFiles = files.stream()
                .map(f -> fileService.uploadFile(f))
                .collect(Collectors.toList());
        profile.setRefFiles(newFiles);
    }


    /**
     * 북마크한 프로젝트 목록
     */
    public List<BookmarkListResponse> getBookmarkProjects(Long userId) {
        User findUser = find(userId);

        List<BookmarkListResponse> bookmarkLists = new ArrayList<>();
        findUser.getBookmarks().stream()
                .forEach(b -> bookmarkLists.add(BookmarkListResponse.from(b.getProject())));
        return bookmarkLists;
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
     * 마이페이지
     */
    public MyPageResponse getMyPage(Long userId) {
        User findUser = find(userId);

        // 프로필이 없거나 프로필 사진이 없는 경우
        if (findUser.getProfile() == null || findUser.getProfile().getProfilePhoto() == null) {
            return MyPageResponse.NoProfilePhoto(findUser);
        }

        return MyPageResponse.YesProfilePhoto(findUser, findUser.getProfile().getProfilePhoto().getUploadFileUrl());
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
     * 유저 찾기 메소드
     */
    public User find(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundUserException());
    }
}

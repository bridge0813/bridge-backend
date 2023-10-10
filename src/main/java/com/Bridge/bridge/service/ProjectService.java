package com.Bridge.bridge.service;

import com.Bridge.bridge.domain.ApplyProject;
import com.Bridge.bridge.domain.Part;
import com.Bridge.bridge.domain.User;
import com.Bridge.bridge.dto.request.FilterRequestDto;
import com.Bridge.bridge.dto.response.ApplyProjectResponse;
import com.Bridge.bridge.dto.response.ApplyUserResponse;
import com.Bridge.bridge.dto.response.ProjectListResponseDto;
import com.Bridge.bridge.dto.request.ProjectRequestDto;
import com.Bridge.bridge.dto.response.ProjectResponseDto;
import com.Bridge.bridge.exception.notfound.NotFoundProjectException;
import com.Bridge.bridge.exception.notfound.NotFoundUserException;
import com.Bridge.bridge.repository.ApplyProjectRepository;
import com.Bridge.bridge.repository.PartRepository;
import com.Bridge.bridge.repository.ProjectRepository;
import com.Bridge.bridge.domain.Project;
import com.Bridge.bridge.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional(readOnly = false)
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final PartRepository partRepository;

    private final ApplyProjectRepository applyProjectRepository;


    /*
        Func : 프로젝트 모집글 생성
        Parameter : 프로젝트 입력 폼
        Return : 새로 생성된 프로젝트 ID
    */
    public Long createProject(ProjectRequestDto projectRequestDto){
        try {
            // 모집글 작성한 user 찾기
            User user = userRepository.findById(projectRequestDto.getUserId())
                    .orElseThrow(() -> new NotFoundUserException());

            Project newProject = projectRequestDto.toEntityOfProject(user);

            // 모집 분야, 인원 -> Part entity화 하기
            List<Part> recruit = projectRequestDto.getRecruit().stream()
                            .map((p) -> p.toEntity())
                            .collect(Collectors.toList());

            // Part- Project 매핑
            recruit.stream()
                            .forEach((part -> part.setProject(newProject)));

            // User - Project 매핑
            user.setProject(newProject);

            // 모집글 DB에 저장하기정
            Project save = projectRepository.save(newProject);

            return save.getId();
        }
        catch (Exception e){
            System.out.println(e);
            return null;
        }

    }

    /*
        Func : 프로젝트 모집글 삭제
        Parameter : 프로젝트 모집글 ID
        Return : 삭제 여부 -> HttpStatus
    */
    public Boolean deleteProject(Long projectId, Long userId){
        try {
            // 삭제할 프로젝트 모집글 찾기
            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> new NotFoundProjectException());

            // 삭제할 모집글을 작성한 유저 찾기
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new NotFoundUserException());

            // 해당 모집글 삭제하기
            if (user.getId().equals(project.getUser().getId())) { // 찾은 프로젝트 유저가 삭제를 요청한 유저가 맞는지 확인
                projectRepository.delete(project);

                return true;
            }
            return false;
        }
        catch (Exception e){
            System.out.println(e);
        }
        return null;
    }

    /*
        Func : 프로젝트 모집글 수정
        Parameter : 프로젝트 모집글 수정폼
        Return : PrjectResponseDto -> 수정본
    */
    @Transactional
    public ProjectResponseDto updateProject(Long projectId, ProjectRequestDto projectRequestDto){
        try {
            // 모집글 작성한 user 찾기
            User user = userRepository.findById(projectRequestDto.getUserId())
                    .orElseThrow(() -> new NotFoundUserException());

            // 모집글 찾기
            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> new NotFoundProjectException());


            // 모집글 작성자와 유저가 같은지 확인하기
            if (user.getId().equals(project.getUser().getId())) {
                // 모집 분야, 인원 -> Part entity화 하기
                List<Part> recruit = projectRequestDto.getRecruit().stream()
                        .map((p) -> p.toEntity())
                        .collect(Collectors.toList());

                // 모집분야, 인원 초기화
                partRepository.deleteAll(project.getRecruit());

                // Part- Project 매핑
                Project finalProject = project;
                recruit.stream()
                        .forEach((part -> part.setProject(finalProject)));

                project = project.update(user, projectRequestDto, recruit);

                projectRepository.save(project);

                return project.toDto();
            }
            else {
                throw new NullPointerException("작성자와 요청자가 같지 않습니다.");
            }

        }
        catch (Exception e){
            System.out.println(e + e.getMessage());
        }
        return null;
    }

    /*
        Func : 프로젝트 모집글 검색(제목+내용)
        Parameter : 검색어
        Return : 프로젝트 모집글 List
    */
    public List<ProjectListResponseDto> findByTitleAndContent(String searchWord){

        List<Project> allProject = projectRepository.findAll();

        List<Project> findProject = allProject.stream()
                .filter((project) ->
                { return project.getOverview().contains(searchWord) || project.getTitle().contains(searchWord);
                })
                .collect(Collectors.toList());

        List<ProjectListResponseDto> response = findProject.stream()
                .map((project) -> ProjectListResponseDto.builder()
                        .title(project.getTitle())
                        .startDate(project.getStartDate())
                        .endDate(project.getEndDate())
                        .recruit(project.getRecruit())
                        .build()
                )
                .collect(Collectors.toList());

        return response;
    }

     /*
        Func : 프로젝트 모집글 상세보기
        Parameter : projectID - 모집글 ID
        Return : projectResponse
    */
    public ProjectResponseDto getProject(Long projectId){

        // 해당 모집글 찾기
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundProjectException());

        return project.toDto();
    }

    /*
        Func : 필터링 후 프로젝트 목록 반환
        Parameter : List<String>,
        Return : projectResponse
    */
    public List<ProjectListResponseDto> filterProjectList(FilterRequestDto filterRequestDto){


        List<Part> parts = partRepository.findAllByRecruitSkillInAndAndRecruitPart(filterRequestDto.getSkills(), filterRequestDto.getPart());

        List<Project> projects = projectRepository.findAllByRecruitIn(parts);

        List<ProjectListResponseDto> response = projects.stream()
                .map((project) -> ProjectListResponseDto.builder()
                        .title(project.getTitle())
                        .startDate(project.getStartDate())
                        .endDate(project.getEndDate())
                        .recruit(project.getRecruit())
                        .build()
                )
                .collect(Collectors.toList());

        return response;
    }

    /**
     * 지원한 프로젝트 목록 반환
     */
    public List<ApplyProjectResponse> getApplyProjects(Long userId) {
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundUserException());

        List<ApplyProjectResponse> applyProjects = findUser.getApplyProjects().stream()
                .map(p -> new ApplyProjectResponse(p.getProject()))
                .collect(Collectors.toList());

        return applyProjects;
    }

    /**
     * 프로젝트 지원하기
     */
    @Transactional
    public boolean apply(Long userId, Long projectId) {
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundUserException());

        Project applyProject = projectRepository.findById(projectId)
                        .orElseThrow(() -> new NotFoundProjectException());

        ApplyProject project = new ApplyProject();
        project.setUserAndProject(findUser, applyProject);

        findUser.getApplyProjects().add(project);
        applyProject.getApplyProjects().add(project);

        return true;
    }

    /**
     * 프로젝트 지원 취소하기
     */
    @Transactional
    public boolean cancelApply(Long userId, Long projectId) {
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundUserException());

        Project applyProject = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundProjectException());

        ApplyProject findProject = applyProjectRepository.findByUserAndProject(findUser, applyProject)
                        .orElseThrow(() -> new NotFoundProjectException());

        findUser.getApplyProjects().remove(findProject);
        applyProject.getApplyProjects().remove(findProject);
        applyProjectRepository.deleteByUserAndProject(findUser, applyProject);

        return true;
    }

    /**
     * 프로젝트 지원자 목록
     */
    public List<ApplyUserResponse> getApplyUsers(Long projectId) {
        Project findProject = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundProjectException());

        List<ApplyUserResponse> applyUsers = findProject.getApplyProjects().stream()
                .map(p -> {
                    User user = p.getUser();
                    List<String> fields = user.getFields().stream()
                            .map(f -> f.getFieldName())
                            .collect(Collectors.toList());

                    return ApplyUserResponse.builder()
                            .userId(user.getId())
                            .name(user.getName())
                            .fields(fields)
                            .career(user.getProfile().getCareer())
                            .build();
                })
                .collect(Collectors.toList());

        return applyUsers;
    }
}

package com.Bridge.bridge.service;

import com.Bridge.bridge.domain.Part;
import com.Bridge.bridge.domain.User;
import com.Bridge.bridge.dto.ProjectListDto;
import com.Bridge.bridge.dto.ProjectRequestDto;
import com.Bridge.bridge.dto.response.ProjectResponseDto;
import com.Bridge.bridge.repository.ProjectRepository;
import com.Bridge.bridge.domain.Project;
import com.Bridge.bridge.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    /*
        Func : 프로젝트 모집글 생성
        Parameter : 프로젝트 입력 폼
        Return : 새로 생성된 프로젝트 ID
    */
    public Long createProject(ProjectRequestDto projectRequestDto, Long userId){
        try {
            // 모집글 작성한 user 찾기
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));

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

            // 모집글 DB에 저장하기
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
                    .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 프로젝트 입니다."));

            // 삭제할 모집글을 작성한 유저 찾기
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));

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
    public ProjectResponseDto updateProject(Long projectId, Long userId, ProjectRequestDto projectRequestDto){
        try {
            // 모집글 작성한 user 찾기
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));

            // 모집글 찾기
            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 프로젝트입니다."));

            // 모집글 작성자와 유저가 같은지 확인하기
            if (user.getId().equals(project.getUser().getId())) {
                Project update = projectRequestDto.toEntityOfProject(user);

                projectRepository.save(update);

                return update.toDto();
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
    public List<ProjectListDto> findByTitleAndContent(String searchWord){

        List<Project> allProject = projectRepository.findAll();

        List<Project> findProject = allProject.stream()
                .filter((project) ->
                { return project.getOverview().contains(searchWord) || project.getTitle().contains(searchWord);
                })
                .collect(Collectors.toList());

        List<ProjectListDto> response = findProject.stream()
                .map((project) -> ProjectListDto.builder()
                        .title(project.getTitle())
                        .startDate(project.getStartDate())
                        .endDate(project.getEndDate())
                        .recruit(project.getRecruit())
                        .build()
                )
                .collect(Collectors.toList());

        return response;
    }

}

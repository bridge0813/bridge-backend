package com.Bridge.bridge.service;

import com.Bridge.bridge.domain.Part;
import com.Bridge.bridge.domain.User;
import com.Bridge.bridge.dto.ProjectListDto;
import com.Bridge.bridge.dto.ReqProjectDto;
import com.Bridge.bridge.repository.ProjectRepository;
import com.Bridge.bridge.domain.Project;
import com.Bridge.bridge.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
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
        Return : 생성 여부 -> STRING
    */
    public ResponseEntity createProject(ReqProjectDto reqProjectDto){
        try {
            // 모집글 작성한 user 찾기
            User user = userRepository.findByEmail(reqProjectDto.getUserEmail());

            // 모집 분야, 인원 -> Part entity화 하기
            List<Part> recruit = reqProjectDto.getRecruit().stream()
                    .map((p) -> Part.builder()
                            .recruitPart(p.getRecruitPart())
                            .recruitNum(p.getRecruitNum())
                            .recruitSkill(p.getRecruitSkill())
                            .requirement(p.getRequirement())
                            .build())
                    .collect(Collectors.toList());

            Project newProject = Project.builder()
                    .title(reqProjectDto.getTitle())
                    .overview(reqProjectDto.getOverview())
                    .dueDate(reqProjectDto.getDueDate())
                    .startDate(reqProjectDto.getStartDate())
                    .endDate(reqProjectDto.getEndDate())
                    .recruit(recruit)
                    .tagLimit(reqProjectDto.getTagLimit())
                    .meetingWay(reqProjectDto.getMeetingWay())
                    .stage(reqProjectDto.getStage())
                    .user(user)
                    .build();

            // 모집글 DB에 저장하기
            Project project = projectRepository.save(newProject);

            return new ResponseEntity(HttpStatus.OK);
        }
        catch (Exception e){
            System.out.println(e);
            return new ResponseEntity(HttpStatus.NOT_ACCEPTABLE);
        }

    }

    /*
        Func : 프로젝트 모집글 삭제
        Parameter : 프로젝트 모집글 ID
        Return : 삭제 여부 -> STRING
    */
    public ResponseEntity deleteProject(Long project_id, String useremail){
        try {
            // 삭제할 프로젝트 모집글 찾기
            Project project = projectRepository.findById(project_id).get();

            // 삭제할 모집글을 작성한 유저 찾기
            User user = userRepository.findByEmail(useremail);

            // 해당 모집글 삭제하기
            if (user.getId().equals(project.getUser().getId())) { // 찾은 프로젝트 유저가 삭제를 요청한 유저가 맞는지 확인
                projectRepository.delete(project);
                return new ResponseEntity(HttpStatus.ACCEPTED);
            }
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        catch (Exception e){
            System.out.println(e);
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }


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

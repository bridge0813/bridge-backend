package com.Bridge.bridge.service;

import com.Bridge.bridge.domain.Part;
import com.Bridge.bridge.domain.User;
import com.Bridge.bridge.dto.ProjectListDto;
import com.Bridge.bridge.dto.ReqProjectDto;
import com.Bridge.bridge.repository.ProjectRepository;
import com.Bridge.bridge.domain.Project;
import com.Bridge.bridge.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    /*
        Func : 프로젝트 모집글 검색
        Parameter : 검색어
        Return : 프로젝트 모집글 List
    */
    public String createProject(ReqProjectDto reqProjectDto){
        try {
            // 모집글 작성한 user 찾기
            User user = userRepository.findByEmail(reqProjectDto.getUseremail());

            // 모집 분야, 인원 -> Part entity화 하기
            List<Part> recruit = reqProjectDto.getRecruit().stream()
                    .map((p) -> Part.builder()
                            .recruitPart(String.valueOf(p.get("recruitPart")))
                            .recruitNum(p.get("recruitNum").getAsInt())
                            .recruitSkill((List<String>) p.get("recruitSkill"))
                            .requirement(p.get("requirement").toString())
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

            return "success";
        }
        catch (Exception e){
            System.out.println(e);
            return "failed";
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

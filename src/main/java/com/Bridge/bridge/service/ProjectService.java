package com.Bridge.bridge.service;

import com.Bridge.bridge.domain.*;
import com.Bridge.bridge.dto.request.FilterRequestDto;
import com.Bridge.bridge.dto.response.*;
import com.Bridge.bridge.dto.request.ProjectRequestDto;
import com.Bridge.bridge.repository.*;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import javax.persistence.Tuple;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional(readOnly = false)
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final PartRepository partRepository;
    private final BookmarkRepository bookmarkRepository;
    private final SearchWordRepository searchWordRepository;

    /*
        Func : 프로젝트 모집글 생성
        Parameter : 프로젝트 입력 폼
        Return : 새로 생성된 프로젝트 ID
    */
    public Long createProject(ProjectRequestDto projectRequestDto){
        try {
            // 모집글 작성한 user 찾기
            User user = userRepository.findById(projectRequestDto.getUserId())
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
    public ProjectResponseDto updateProject(Long projectId, ProjectRequestDto projectRequestDto){
        try {
            // 모집글 작성한 user 찾기
            User user = userRepository.findById(projectRequestDto.getUserId())
                    .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));

            // 모집글 찾기
            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 프로젝트입니다."));


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
    public List<ProjectListResponseDto> findByTitleAndContent(Long userId, String searchWord){

        // 모집글 작성한 user 찾기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));

        // 최근 검색어 저장하기
        SearchWord searchWord1 = SearchWord.builder()
                .content(searchWord)
                .history(LocalDateTime.now())
                .user(user)
                .build();
        searchWordRepository.save(searchWord1);

        user.getSearchWords().add(searchWord1);
        userRepository.save(user);

        List<Project> allProject = projectRepository.findAll();

        List<Project> findProject = allProject.stream()
                .filter((project) ->
                { return project.getOverview().contains(searchWord) || project.getTitle().contains(searchWord);
                })
                .collect(Collectors.toList());

        final int[] recruitTotal = {0};

        findProject.stream()
                .forEach((project -> project.getRecruit().stream()
                        .forEach((part -> recruitTotal[0] += part.getRecruitNum()))
                ));

        List<ProjectListResponseDto> response = findProject.stream()
                .map((project) -> ProjectListResponseDto.builder()
                        .projectId(project.getId())
                        .title(project.getTitle())
                        .dueDate(project.getDueDate())
                        .recruitTotalNum(recruitTotal[0])
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
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 프로젝트입니다."));

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

        final int[] recruitTotal = {0};

        projects.stream()
                .forEach((project -> project.getRecruit().stream()
                        .forEach((part -> recruitTotal[0] += part.getRecruitNum()))
                ));

        List<ProjectListResponseDto> response = projects.stream()
                .map((project) -> ProjectListResponseDto.builder()
                        .projectId(project.getId())
                        .title(project.getTitle())
                        .dueDate(project.getDueDate())
                        .recruitTotalNum(recruitTotal[0])
                        .build()
                )
                .collect(Collectors.toList());

        return response;
    }

    /*
        Func : 자신이 작성한 모집글 리스트 보여주기
        Parameter : userId
        Return : List<projectListResponseDto>
    */
    public List<MyProjectResponseDto> findMyProjects(Long userId){
        // 모집글 작성한 user 찾기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));

        // 요청자가 작성한 작성글 모두 불러오기
        List<Project> myProjects = projectRepository.findAllByUser(user);

        // 작성한 모집글이 없다면
        if(myProjects.isEmpty()){
            throw new NullPointerException("작성한 프로젝트가 없습니다.");
        }

        // 총 모집인원
        final int[] recruitTotal = {0};

        // 총 모집인원 구하기
        myProjects.stream()
                .forEach((project -> project.getRecruit().stream()
                        .forEach((part -> recruitTotal[0] += part.getRecruitNum()))
                ));


        List<MyProjectResponseDto> response = new ArrayList<>();

       myProjects.stream()
            .forEach((project -> project.getRecruit().stream()
                        .forEach((part -> response.add(MyProjectResponseDto.builder()
                                    .recruitPart(part.getRecruitPart())
                                    .requirement(part.getRequirement())
                                    .recruitSkill(part.getRecruitSkill())
                                    .recruitNum(part.getRecruitNum())
                                    .build())
                        ))));

        return response;
    }

    /*
        Func : 모든 모집글 리스트 보여주기
        Return : List<projectListResponseDto>
    */
    public List<ProjectListResponseDto> allProjects(){
        List<Project> allProjects = projectRepository.findAll();

        // 작성글이 하나도 없다면
        if(allProjects.isEmpty()){
            throw new NullPointerException("작성한 프로젝트가 없습니다.");
        }

        // 총 모집인원
        final int[] recruitTotal = {0};

        // 총 모집인원 구하기
        allProjects.stream()
                .forEach((project -> project.getRecruit().stream()
                        .forEach((part -> recruitTotal[0] += part.getRecruitNum()))
                ));

        List<ProjectListResponseDto> response = allProjects.stream()
                .map((project -> ProjectListResponseDto.builder()
                        .projectId(project.getId())
                        .title(project.getTitle())
                        .dueDate(project.getDueDate())
                        .recruitTotalNum(recruitTotal[0])
                        .build()
                ))
                .collect(Collectors.toList());

        return response;

    }

    /*
        Func : 내 분야 모집글 리스트 보여주기
        Parameter : String - 모집분야
        Return : List<projectListResponseDto>
    */
    public List<ProjectListResponseDto> findMyPartProjects(String myPart){
        List<Part> parts = partRepository.findAllByRecruitPart(myPart);

        List<Project> myPartProjects = projectRepository.findAllByRecruitIn(parts);

        // 작성글이 하나도 없다면
        if(myPartProjects.isEmpty()){
            throw new NullPointerException("해당 프로젝트가 없습니다.");
        }

        // 총 모집인원
        final int[] recruitTotal = {0};

        // 총 모집인원 구하기
        myPartProjects.stream()
                .forEach((project -> project.getRecruit().stream()
                        .forEach((part -> recruitTotal[0] += part.getRecruitNum()))
                ));

        List<ProjectListResponseDto> response = myPartProjects.stream()
                .map((project -> ProjectListResponseDto.builder()
                        .projectId(project.getId())
                        .title(project.getTitle())
                        .dueDate(project.getDueDate())
                        .recruitTotalNum(recruitTotal[0])
                        .build()
                ))
                .collect(Collectors.toList());

        return response;

    }

    /*
        Func : 모집글 마감 기능
        Parameter : projectId
        Return : ProjectResponseDto
    */
    public ProjectResponseDto closeProject(Long projectId, Long userId){
        // 해당 유저 찾기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("해당 유저가 존재하지 않습니다."));

        // 마감하고자 하는 프로젝트 찾기
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("해당 모집글을 찾을 수 없습니다."));

        LocalDateTime localDateTime = LocalDateTime.now();

        // 포맷
        String formatedNow = localDateTime.format(DateTimeFormatter.ofPattern("YYYYMMDDHHmmss"));

        if (project.getUser().getId().equals(userId)){ // 프로젝트를 작성한 유저인가
            if(project.getDueDate().compareTo(formatedNow)<0){ // 마감시간이 이미 지난 경우
                throw new IllegalStateException("이미 마감이 된 모집글입니다.");
            }
            else {
                project = project.updateDeadline();
                projectRepository.save(project);

                return project.toDto();
            }
        }
        else {
            throw new IllegalStateException("프로젝트 작성자가 아닙니다.");
        }

    }

    /*
        Func : 모집글 스크랩 기능
        Parameter : projectId, userId
        Return : Boolean - 스크랩 여부
    */
    @Transactional
    public BookmarkResponseDto scrap(Long projectId, Long userId){
        // 해당 유저 찾기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("해당 유저가 존재하지 않습니다."));

        // 스크랩 하고자 하는 프로젝트 찾기
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("해당 모집글을 찾을 수 없습니다."));

        Bookmark bookmark = bookmarkRepository.findByProjectAndUser(project, user);

        if (bookmark == null){ // 스크랩 되어 있지 않다면
            Bookmark newBookmark = Bookmark.builder()
                    .user(user)
                    .project(project)
                    .build();

            newBookmark = bookmarkRepository.save(newBookmark);

            // user - bookmark 연관관계 맵핑
            user.setBookmarks(newBookmark);

            // project - bookmark 연관관계 맵핑
            project.setBookmarks(newBookmark);

            return BookmarkResponseDto.builder()
                    .projectId(projectId)
                    .userId(userId)
                    .scrap("스크랩이 설정되었습니다.")
                    .build();
        }
        else {
            bookmarkRepository.delete(bookmark); // 스크랩 해제
            return BookmarkResponseDto.builder()
                    .projectId(projectId)
                    .userId(userId)
                    .scrap("스크랩이 해제되었습니다.")
                    .build();
        }
    }

    /*
        Func : 최근 검색어 조회 기능
        Parameter : userId
        Return : List<SearchWordResponseDto>
    */
    public List<SearchWordResponseDto> resentSearchWord(Long userId){
        // 해당 유저 찾기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("해당 유저가 존재하지 않습니다."));

        return user.getSearchWords().stream()
                .map((searchWord -> SearchWordResponseDto.builder()
                        .searchWordId(searchWord.getId())
                        .searchWord(searchWord.getContent())
                        .build()))
                .collect(Collectors.toList());
    }


}

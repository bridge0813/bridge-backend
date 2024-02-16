package com.Bridge.bridge.service;

import com.Bridge.bridge.domain.*;
import com.Bridge.bridge.dto.request.FilterRequest;
import com.Bridge.bridge.dto.request.ProjectUpdateRequest;
import com.Bridge.bridge.dto.response.*;
import com.Bridge.bridge.dto.request.ProjectRequest;
import com.Bridge.bridge.exception.BridgeException;
import com.Bridge.bridge.exception.conflict.ConflictApplyProjectException;
import com.Bridge.bridge.repository.*;
import com.Bridge.bridge.dto.response.ProjectResponse;
import com.Bridge.bridge.repository.BookmarkRepository;
import com.Bridge.bridge.domain.ApplyProject;
import com.Bridge.bridge.domain.Part;
import com.Bridge.bridge.domain.User;
import com.Bridge.bridge.dto.response.ApplyProjectResponse;
import com.Bridge.bridge.dto.response.ApplyUserResponse;
import com.Bridge.bridge.dto.response.ProjectListResponse;
import com.Bridge.bridge.exception.notfound.NotFoundProjectException;
import com.Bridge.bridge.exception.notfound.NotFoundUserException;
import com.Bridge.bridge.repository.ApplyProjectRepository;
import com.Bridge.bridge.repository.PartRepository;
import com.Bridge.bridge.repository.ProjectRepository;
import com.Bridge.bridge.domain.Project;
import com.Bridge.bridge.repository.UserRepository;
import com.Bridge.bridge.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final PartRepository partRepository;
    private final BookmarkRepository bookmarkRepository;
    private final SearchWordRepository searchWordRepository;
    private final ApplyProjectRepository applyProjectRepository;
    private final JwtTokenProvider jwtTokenProvider;


    /*
        Func : 프로젝트 모집글 생성 - 지민
        Parameter : 프로젝트 입력 폼
        Return : 새로 생성된 프로젝트 ID
    */
    @Transactional
    public Long createProject(ProjectRequest projectRequest){

        // 모집글 작성한 user 찾기
        User user = userRepository.findById(projectRequest.getUserId())
                .orElseThrow(() -> new NotFoundUserException());
        try{
            // 모집 분야, 인원 -> Part entity화 하기
            List<Part> recruit = projectRequest.getRecruit().stream()
                            .map((p) -> p.toEntity())
                            .collect(Collectors.toList());

            Project newProject = projectRequest.toEntityOfProject(user);

            // Part- Project 매핑
            recruit.stream()
                    .forEach((part -> part.setProject(newProject)));

            // User - Project 매핑
            user.setProject(newProject);

            // 모집글 DB에 저장하기정
            Project saveProject = projectRepository.save(newProject);
            return saveProject.getId();
        }
        catch (Exception e){
            System.out.println(e);
            return null;
        }

    }

    /*
        Func : 프로젝트 모집글 삭제 - 지민
        Parameter : 프로젝트 모집글 ID
        Return : 삭제 여부 -> HttpStatus
    */
    @Transactional
    public Boolean deleteProject(Long projectId){

        // 삭제할 프로젝트 모집글 찾기
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundProjectException());
        try {
            // 해당 모집글 삭제하기
            projectRepository.delete(project);
            project.getUser().getProjects().remove(project);

            return true;
        }
        catch (Exception e){
            System.out.println(e);
            return false;
        }
    }

    /*
        Func : 프로젝트 모집글 수정
        Parameter : 프로젝트 모집글 수정폼
        Return : PrjectResponseDto -> 수정본
    */
    @Transactional
    public ProjectResponse updateProject(Long projectId, ProjectUpdateRequest projectUpdateRequest){
        // 모집글 찾기
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundProjectException());

        // 모집 분야, 인원 -> Part entity화 하기
        List<Part> recruit = projectUpdateRequest.getRecruit().stream()
                .map((p) -> p.toEntity())
                .collect(Collectors.toList());

        // 모집분야, 인원 초기화
        partRepository.deleteAll(project.getRecruit());


        project.update(projectUpdateRequest);
        recruit.stream()
               .forEach((part -> part.setProject(project)));

        return project.toDto(true, false);
    }

    /*
        Func : 프로젝트 모집글 검색(제목+내용)
        Parameter : 검색어
        Return : 프로젝트 모집글 List
    */
    @Transactional
    public List<ProjectListResponse> findByTitleAndContent(long userId, String theSearchWord){

        LocalDateTime localDateTime = LocalDateTime.now();

        List<Project> allProject = projectRepository.findAllByDueDateGreaterThanEqualOrderByUploadTime(localDateTime);

        List<Project> findProject = allProject.stream()
                .filter((project) ->

                        project.getOverview().contains(theSearchWord) || project.getTitle().contains(theSearchWord)
                )
                .collect(Collectors.toList());

        System.out.println(findProject.size());
        System.out.println(theSearchWord);
        List<ProjectListResponse> response = new ArrayList<>();

        if (userId == -1){ // 로그인 하지 않은 유저일 경우


            for(int i =0; i<findProject.size(); i++) {
                final int[] total = {0};

                findProject.get(i).getRecruit().stream()
                        .forEach((part -> total[0] += part.getRecruitNum()));

                boolean isScrap = false;

                // 0초일 경우 초 단위가 출력되지 않는 현상을 방지하기 위해
                String duedate = findProject.get(i).getDueDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        + "T"
                        + findProject.get(i).getDueDate().format(DateTimeFormatter.ofPattern("HH:mm:ss"));


                ProjectListResponse projectListResponse = ProjectListResponse.builder()
                        .projectId(findProject.get(i).getId())
                        .title(findProject.get(i).getTitle())
                        .dueDate(duedate)
                        .recruitTotalNum(total[0])
                        .scrap(isScrap)
                        .build();
                response.add(projectListResponse);
            }

            return  response;
        }
        //로그인 한 유저일 경우

        // 모집글 작성한 user 찾기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundUserException());


        // 최근 검색어 저장하기
        SearchWord searchWord = SearchWord.builder()
                .content(theSearchWord)
                .history(LocalDateTime.now())
                .user(user)
                .build();
        searchWordRepository.save(searchWord);

        for(int i =0; i<findProject.size(); i++){
            final int[] total = {0};

            findProject.get(i).getRecruit().stream()
                    .forEach((part -> total[0] += part.getRecruitNum()));

            Bookmark bookmark = bookmarkRepository.findByProjectAndUser(findProject.get(i), user);
            boolean isScrap = false;


            if(bookmark != null){
                isScrap = true;
            }

            // 0초일 경우 초 단위가 출력되지 않는 현상을 방지하기 위해
            String duedate = findProject.get(i).getDueDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    +"T"
                    +findProject.get(i).getDueDate().format(DateTimeFormatter.ofPattern("HH:mm:ss"));


            ProjectListResponse projectListResponse = ProjectListResponse.builder()
                    .projectId(findProject.get(i).getId())
                    .title(findProject.get(i).getTitle())
                    .dueDate(duedate)
                    .recruitTotalNum(total[0])
                    .scrap(isScrap)
                    .build();
            response.add(projectListResponse);
        }

        return response;
    }

     /*
        Func : 프로젝트 모집글 상세보기 - 지민
        Parameter : projectID - 모집글 ID
        Return : projectResponse
    */
    public ProjectResponse getProject(Long userId, Long projectId){

        // 해당 모집글 찾기
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundProjectException());

        if(userId == null){ // 로그인 안 한 유저일 경우
            return project.toDto(false, false);
        }
        else if(!userId.equals(project.getUser().getId()))  { // 로그인한 유저인데 글쓴이가 아닐 경우
            User user = userRepository.findById(userId)
                    .orElseThrow(()->new NotFoundUserException());
            Bookmark bookmark= bookmarkRepository.findByProjectAndUser(project, user);
            if(bookmark == null){
             return project.toDto(false, false);
            }
            return project.toDto(false, true);
        }
        return project.toDto(true, false);
    }

    /*
        Func : 필터링 후 프로젝트 목록 반환 - 지민
        Parameter : List<String>,
        Return : projectResponse
    */
    public List<ProjectListResponse> filterProjectList(FilterRequest filterRequest){

        List<Stack> skills = filterRequest.getSkills().stream()
                .map(s -> Stack.valueOf(s))
                .collect(Collectors.toList());


        Field recruitPart = Field.valueOf(filterRequest.getPart());
        List<Part> parts = partRepository.findAllByRecruitSkillInAndAndRecruitPart(skills, recruitPart);

        LocalDateTime localDateTime = LocalDateTime.now();
        List<Project> projects = projectRepository.findAllByRecruitInAndDueDateGreaterThanEqual(parts, localDateTime);

        List<ProjectListResponse> response = new ArrayList<>();

        if (filterRequest.getUserId() == -1){ // 로그인 하지 않은 유저일 경우
            for(int i =0; i<projects.size(); i++){
                final int[] total = {0};

                projects.get(i).getRecruit().stream()
                        .forEach((part -> total[0] += part.getRecruitNum()));

                boolean isScrap = false;

                // 0초일 경우 초 단위가 출력되지 않는 현상을 방지하기 위해
                String duedate = projects.get(i).getDueDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        +"T"
                        +projects.get(i).getDueDate().format(DateTimeFormatter.ofPattern("HH:mm:ss"));

                ProjectListResponse projectListResponse = ProjectListResponse.builder()
                        .projectId(projects.get(i).getId())
                        .title(projects.get(i).getTitle())
                        .dueDate(duedate)
                        .recruitTotalNum(total[0])
                        .scrap(isScrap)
                        .build();
                response.add(projectListResponse);
            }

            return response;
        }

        // 로그인 한 유저일 경우
        User user = userRepository.findById(filterRequest.getUserId()).orElseThrow(()->new NotFoundUserException());

        for(int i =0; i<projects.size(); i++){
            final int[] total = {0};

            projects.get(i).getRecruit().stream()
                    .forEach((part -> total[0] += part.getRecruitNum()));

            Bookmark bookmark = bookmarkRepository.findByProjectAndUser(projects.get(i), user);
            boolean isScrap = false;


            if(bookmark != null){
                isScrap = true;
            }

            // 0초일 경우 초 단위가 출력되지 않는 현상을 방지하기 위해
            String duedate = projects.get(i).getDueDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    +"T"
                    +projects.get(i).getDueDate().format(DateTimeFormatter.ofPattern("HH:mm:ss"));

            ProjectListResponse projectListResponse = ProjectListResponse.builder()
                    .projectId(projects.get(i).getId())
                    .title(projects.get(i).getTitle())
                    .dueDate(duedate)
                    .recruitTotalNum(total[0])
                    .scrap(isScrap)
                    .build();
            response.add(projectListResponse);
        }

        return response;
    }

    /*
        Func : 자신이 작성한 모집글 리스트 보여주기 - 지민
        Parameter : userId
        Return : List<projectListResponseDto>
    */
    public List<MyProjectResponse> findMyProjects(HttpServletRequest request){

        Long userId = jwtTokenProvider.getUserIdFromRequest(request);

        // 모집글 작성한 user 찾기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundUserException());

        // 요청자가 작성한 작성글 모두 불러오기
        List<Project> myProjects = projectRepository.findAllByUser(user);


        List<MyProjectResponse> response = new ArrayList<>();

        // 작성한 모집글이 없다면
        if(myProjects.isEmpty()){
            return response;
        }

        for(int i =0; i<myProjects.size(); i++){
            final int[] total = {0};

            myProjects.get(i).getRecruit().stream()
                    .forEach((part -> total[0] += part.getRecruitNum()));

            LocalDateTime localDateTime = LocalDateTime.now();
            String status = "모집완료";

            if(myProjects.get(i).getDueDate().isAfter(localDateTime)){ // 마감되지 않았다면
                status = "현재 모집중";
            }

            // 0초일 경우 초 단위가 출력되지 않는 현상을 방지하기 위해
            String duedate = myProjects.get(i).getDueDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    +"T"
                    +myProjects.get(i).getDueDate().format(DateTimeFormatter.ofPattern("HH:mm:ss"));

            MyProjectResponse myProjectResponse = MyProjectResponse.builder()
                    .projectId(myProjects.get(i).getId())
                    .title(myProjects.get(i).getTitle())
                    .overview(myProjects.get(i).getOverview())
                    .dueDate(duedate)
                    .recruitTotalNum(total[0])
                    .status(status)
                    .build();
            response.add(myProjectResponse);
        }

        return response;
    }

    /*
        Func : 모든 모집글 리스트 보여주기 - 지민
        Return : List<projectListResponseDto>
    */
    public List<ProjectListResponse> allProjects(Long userId){

        LocalDateTime localDateTime = LocalDateTime.now();
        List<Project> allProjects = projectRepository.findAllByDueDateGreaterThanEqualOrderByUploadTime(localDateTime);


        List<ProjectListResponse> response = new ArrayList<>();

        // 작성글이 하나도 없다면
        if(allProjects.isEmpty()){
            return response;
        }

        if(userId == null){ // 로그인 안 된 상태
            for(int i =0; i<allProjects.size(); i++){
                final int[] total = {0};

                allProjects.get(i).getRecruit().stream()
                        .forEach((part -> total[0] += part.getRecruitNum()));


                // 0초일 경우 초 단위가 출력되지 않는 현상을 방지하기 위해
                String duedate = allProjects.get(i).getDueDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        +"T"
                        +allProjects.get(i).getDueDate().format(DateTimeFormatter.ofPattern("HH:mm:ss"));

                ProjectListResponse projectListResponse = ProjectListResponse.builder()
                        .projectId(allProjects.get(i).getId())
                        .title(allProjects.get(i).getTitle())
                        .dueDate(duedate)
                        .recruitTotalNum(total[0])
                        .scrap(false)
                        .build();
                response.add(projectListResponse);
            }

            return response;
        }

        // 로그인 된 상태
        User user = userRepository.findById(userId)
                .orElseThrow(()->new NotFoundUserException());

        for(int i =0; i<allProjects.size(); i++){
            final int[] total = {0};

            allProjects.get(i).getRecruit().stream()
                    .forEach((part -> total[0] += part.getRecruitNum()));

            Bookmark bookmark = bookmarkRepository.findByProjectAndUser(allProjects.get(i), user);
            boolean isScrap = false;


            if(bookmark != null){
                isScrap = true;
            }

            // 0초일 경우 초 단위가 출력되지 않는 현상을 방지하기 위해
            String duedate = allProjects.get(i).getDueDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    +"T"
                    +allProjects.get(i).getDueDate().format(DateTimeFormatter.ofPattern("HH:mm:ss"));

            ProjectListResponse projectListResponse = ProjectListResponse.builder()
                    .projectId(allProjects.get(i).getId())
                    .title(allProjects.get(i).getTitle())
                    .dueDate(duedate)
                    .recruitTotalNum(total[0])
                    .scrap(isScrap)
                    .build();
            response.add(projectListResponse);
        }

        return response;

    }

    /*
        Func : 내 분야 모집글 리스트 보여주기 - 지민
        Parameter : String - 모집분야
        Return : List<projectListResponseDto>
    */
    public List<ProjectListResponse> findMyPartProjects(HttpServletRequest request, String myPart){

        Long adminUserId = jwtTokenProvider.getUserIdFromRequest(request);
        User user = userRepository.findById(adminUserId).orElseThrow(()-> new NotFoundUserException());

        List<Part> parts = partRepository.findAllByRecruitPart(Field.valueOf(myPart));

        LocalDateTime localDateTime = LocalDateTime.now();
        List<Project> myPartProjects = projectRepository.findAllByRecruitInAndDueDateGreaterThanEqual(parts, localDateTime);


        List<ProjectListResponse> response = new ArrayList<>();

        // 작성글이 하나도 없다면
        if(myPartProjects.isEmpty()){
            return response;
        }

        for(int i =0; i<myPartProjects.size(); i++){
            final int[] total = {0};

            myPartProjects.get(i).getRecruit().stream()
                    .forEach((part -> total[0] += part.getRecruitNum()));

            Bookmark bookmark = bookmarkRepository.findByProjectAndUser(myPartProjects.get(i), user);
            boolean isScrap = false;


            if(bookmark != null){
                isScrap = true;
            }

            // 0초일 경우 초 단위가 출력되지 않는 현상을 방지하기 위해
            String duedate = myPartProjects.get(i).getDueDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    +"T"
                    +myPartProjects.get(i).getDueDate().format(DateTimeFormatter.ofPattern("HH:mm:ss"));

            ProjectListResponse projectListResponse = ProjectListResponse.builder()
                    .projectId(myPartProjects.get(i).getId())
                    .title(myPartProjects.get(i).getTitle())
                    .dueDate(duedate)
                    .recruitTotalNum(total[0])
                    .scrap(isScrap)
                    .build();
            response.add(projectListResponse);
        }

        return response;

    }

    /*
        Func : 모집글 마감 기능 - 지민
        Parameter : projectId
        Return : ProjectResponseDto
    */
    @Transactional
    public ProjectResponse closeProject(Long projectId){
        // 마감하고자 하는 프로젝트 찾기
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundProjectException());

        LocalDateTime localDateTime = LocalDateTime.now();

        if(project.getDueDate().compareTo(localDateTime)<0){ // 마감시간이 이미 지난 경우
            throw new IllegalStateException("이미 마감이 된 모집글입니다.");
        }
        else {
            project = project.updateDeadline();
            return project.toDto(true, false);
        }
    }

    /*
        Func : 모집글 스크랩 기능 - 지민
        Parameter : projectId, userId
        Return : Boolean - 스크랩 여부
    */
    @Transactional
    public BookmarkResponse scrap(HttpServletRequest request, Long projectId){

        Long userId = jwtTokenProvider.getUserIdFromRequest(request);

        // 해당 유저 찾기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundUserException());

        // 스크랩 하고자 하는 프로젝트 찾기
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundProjectException());

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
            project.increaseBookmarksNum();

            return BookmarkResponse.builder()
                    .projectId(projectId)
                    .userId(userId)
                    .scrap("스크랩이 설정되었습니다.")
                    .build();
        }
        else {
            user.getBookmarks().remove(bookmark);
            bookmarkRepository.delete(bookmark); // 스크랩 해제

            project.decreaseBookmarksNum();

            return BookmarkResponse.builder()
                    .projectId(projectId)
                    .userId(userId)
                    .scrap("스크랩이 해제되었습니다.")
                    .build();
        }
    }

    /*
        Func : 인기글 조회 - 지민
        Parameter :
        Return : List<TopProjectResponseDto>
    */
    public List<TopProjectResponse> topProjects(Long userId){

        LocalDateTime localDateTime = LocalDateTime.now();
        int year = localDateTime.getYear();;
        int month = localDateTime.getMonthValue();
        int day = localDateTime.getDayOfMonth();

        LocalDateTime now = LocalDateTime.of(year, month, day, 0,0,0);

        List<Project> top20 = projectRepository.findTop20ByDueDateGreaterThanEqualOrderByBookmarkNumDesc(now);

        List<TopProjectResponse> topProjectResponses = new ArrayList<>();

        if(userId != null){
            User user = userRepository.findById(userId).orElseThrow(()-> new NotFoundUserException());

            for (int i=0; i<top20.size(); i++){
                final int[] total = {0};

                top20.get(i).getRecruit().stream()
                        .forEach((part -> total[0] += part.getRecruitNum()));

                Bookmark bookmark = bookmarkRepository.findByProjectAndUser(top20.get(i), user);
                boolean isScrap = false;


                if(bookmark != null){
                    isScrap = true;
                }

                // 0초일 경우 초 단위가 출력되지 않는 현상을 방지하기 위해
                String duedate = top20.get(i).getDueDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        +"T"
                        +top20.get(i).getDueDate().format(DateTimeFormatter.ofPattern("HH:mm:ss"));

                topProjectResponses.add(TopProjectResponse.builder()
                        .rank(i+1)
                        .projectId(top20.get(i).getId())
                        .title(top20.get(i).getTitle())
                        .dueDate(duedate)
                        .recruitNum(total[0])
                        .scrap(isScrap)
                        .build());
            }
            return topProjectResponses;
        }

        for (int i=0; i<top20.size(); i++){
            final int[] total = {0};

            top20.get(i).getRecruit().stream()
                    .forEach((part -> total[0] += part.getRecruitNum()));

            // 0초일 경우 초 단위가 출력되지 않는 현상을 방지하기 위해
            String duedate = top20.get(i).getDueDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    +"T"
                    +top20.get(i).getDueDate().format(DateTimeFormatter.ofPattern("HH:mm:ss"));

            topProjectResponses.add(TopProjectResponse.builder()
                    .rank(i+1)
                    .projectId(top20.get(i).getId())
                    .title(top20.get(i).getTitle())
                    .dueDate(duedate)
                    .recruitNum(total[0])
                    .scrap(false)
                    .build());
        }
        return topProjectResponses;


    }

    /*
        Func : 지원한 프로젝트 목록 반환 - 규현
        Parameter : HttpServletRequest
        Return : List<ApplyProjectResponse>
    */
    public List<ApplyProjectResponse> getApplyProjects(HttpServletRequest request) {

        Long userId = jwtTokenProvider.getUserIdFromRequest(request);

        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundUserException());

        List<ApplyProjectResponse> applyProjects = new ArrayList<>();
        findUser.getApplyProjects().stream()
                .forEach(p -> applyProjects.add(ApplyProjectResponse.from(p)));

        return applyProjects;
    }

    /*
        Func : 프로젝트 지원하기 - 규현
        Parameter : HttpServletRequest, projectId
        Return : boolean
    */
    @Transactional
    public boolean apply(HttpServletRequest request, Long projectId) {

        Long userId = jwtTokenProvider.getUserIdFromRequest(request);

        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundUserException());

        Project findProject = projectRepository.findById(projectId)
                        .orElseThrow(() -> new NotFoundProjectException());

        //이미 지원한 경우 예외 반환
        //새로 지원하는 경우 생성
        if (applyProjectRepository.findByUserAndProject(findUser, findProject)
                .isPresent()) {
            throw new ConflictApplyProjectException();
        }

        ApplyProject applyProject = new ApplyProject();
        applyProject.setUserAndProject(findUser, findProject);

        findUser.getApplyProjects().add(applyProject);
        findProject.getApplyProjects().add(applyProject);

        return true;
    }

    /*
        Func : 프로젝트 지원 취소하기 - 규현
        Parameter : HttpServletRequest, projectId
        Return : boolean
    */
    @Transactional
    public boolean cancelApply(HttpServletRequest request, Long projectId) {

        Long userId = jwtTokenProvider.getUserIdFromRequest(request);

        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundUserException());

        Project appliedProject = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundProjectException());

        ApplyProject cancleProject = applyProjectRepository.findByUserAndProject(findUser, appliedProject)
                        .orElseThrow(() -> new NotFoundProjectException());

        try {
            findUser.getApplyProjects().remove(cancleProject);
            appliedProject.getApplyProjects().remove(cancleProject);
            applyProjectRepository.delete(cancleProject);
        } catch (BridgeException e) {
            e.getStackTrace();
        }
        return true;
    }

    /*
        Func : 프로젝트 지원자 목록 - 규현
        Parameter : projectId
        Return : List<ApplyUserResponse>
    */
    public List<ApplyUserResponse> getApplyUsers(Long projectId) {
        Project findProject = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundProjectException());

        List<ApplyUserResponse> applyUsers = new ArrayList<>();
        findProject.getApplyProjects().stream()
                .filter(applyProject -> applyProject.getStage().equals("결과 대기중"))
                .forEach(p -> applyUsers.add(ApplyUserResponse.from(p)));
        return applyUsers;
    }


    /*
        Func : 프로젝트 수락하기 - 규현
        Parameter : projectId, userId
        Return :
    */
    @Transactional
    public void acceptApply(Long projectId, Long userId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundProjectException());

        //지원한 유저
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundUserException());

        ApplyProject applyProject = applyProjectRepository.findByUserAndProject(findUser, project)
                .orElseThrow(() -> new NotFoundProjectException());

        applyProject.changeStage("수락");
    }

    /*
        Func : 프로젝트 거절하기 - 규현
        Parameter : projectId, userId
        Return :
    */
    @Transactional
    public void rejectApply(Long projectId, Long userId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundProjectException());

        //지원한 유저
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundUserException());

        ApplyProject applyProject = applyProjectRepository.findByUserAndProject(findUser, project)
                .orElseThrow(() -> new NotFoundProjectException());

        applyProject.changeStage("거절");
    }

    /*
        Func : 마감 임박 40개 프로젝트 조회 기능 - 지민
        Parameter :
        Return : List<imminentProjectResponseDto>
    */
    public List<imminentProjectResponse> getdeadlineImminentProejcts(Long userId){
        LocalDateTime localDateTime = LocalDateTime.now();

        List<Project> projects = projectRepository.findTop40ByDueDateGreaterThanEqualOrderByDueDate(localDateTime);

        List<imminentProjectResponse> imminentProjectResponses = new ArrayList<>();

        if(userId != null){
            User user = userRepository.findById(userId).orElseThrow(()-> new NotFoundUserException());

            for (int i=0; i<projects.size(); i++){

                final int[] total = {0};

                projects.get(i).getRecruit().stream()
                        .forEach((part -> total[0] += part.getRecruitNum()));

                projects.get(i).getRecruit().stream()
                            .forEach((part -> total[0] += part.getRecruitNum()));

                Bookmark bookmark = bookmarkRepository.findByProjectAndUser(projects.get(i), user);
                boolean isScrap = false;


                if(bookmark != null){
                    isScrap = true;
                }

                // 0초일 경우 초 단위가 출력되지 않는 현상을 방지하기 위해
                String duedate = projects.get(i).getDueDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        +"T"
                        +projects.get(i).getDueDate().format(DateTimeFormatter.ofPattern("HH:mm:ss"));

                imminentProjectResponses.add(imminentProjectResponse.builder()
                        .imminentRank(i+1)
                        .projectId(projects.get(i).getId())
                        .title(projects.get(i).getTitle())
                        .dueDate(duedate)
                        .recruitNum(total[0])
                        .scrap(isScrap)
                        .build());
            }

            return imminentProjectResponses;
        }

        for (int i=0; i<projects.size(); i++){

            final int[] total = {0};

            projects.get(i).getRecruit().stream()
                    .forEach((part -> total[0] += part.getRecruitNum()));

            // 0초일 경우 초 단위가 출력되지 않는 현상을 방지하기 위해
            String duedate = projects.get(i).getDueDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    +"T"
                    +projects.get(i).getDueDate().format(DateTimeFormatter.ofPattern("HH:mm:ss"));

            imminentProjectResponses.add(imminentProjectResponse.builder()
                    .imminentRank(i+1)
                    .projectId(projects.get(i).getId())
                    .title(projects.get(i).getTitle())
                    .dueDate(duedate)
                    .recruitNum(total[0])
                    .scrap(false)
                    .build());
        }

        return imminentProjectResponses;
    }
}

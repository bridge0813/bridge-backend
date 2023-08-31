package com.Bridge.bridge.Service;

import com.Bridge.bridge.Repository.ProjectRepository;
import com.Bridge.bridge.domain.Project;
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

    public List<Project> findByTitleAndContent(String searchWord){ // 모집글 검색 기능(제목+내용)

        List<Project> allProject = projectRepository.findAll();

        List<Project> findProject = allProject.stream()
                .filter((project) ->
                { return project.getOverview().contains(searchWord) || project.getTitle().contains(searchWord);
                })
                .collect(Collectors.toList());

        System.out.println(findProject.size());
        return findProject;
    }

}

package com.mush490.ppmtool.services;

import com.mush490.ppmtool.domain.Backlog;
import com.mush490.ppmtool.domain.Project;
import com.mush490.ppmtool.domain.ProjectTask;
import com.mush490.ppmtool.exceptions.ProjectNotFoundException;
import com.mush490.ppmtool.repositories.BacklogRepository;
import com.mush490.ppmtool.repositories.ProjectRepository;
import com.mush490.ppmtool.repositories.ProjectTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectTaskService {


    @Autowired
    private BacklogRepository backlogRepository;

    @Autowired
    private ProjectTaskRepository projectTaskRepository;

    @Autowired
    private ProjectRepository projectRepository;


    public ProjectTask addProjectTask(String projectIdentifier, ProjectTask projectTask){

        try {
            //Exceptions: Project not found
            if (projectRepository.findByProjectIdentifier(projectIdentifier.toUpperCase()) == null)
                throw new ProjectNotFoundException("Project " + projectIdentifier + " does not exist");

            //PTs to be added to a specific project, project != null, BL exists
            Backlog backlog = backlogRepository.findByProjectIdentifier(projectIdentifier);
            //set the bl to pt
            projectTask.setBacklog(backlog);
            //we want our project sequence to be like this: IDPRO-1  IDPRO-2  ...100 101
            Integer BacklogSequence = backlog.getPTSequence();
            // Update the BL SEQUENCE
            BacklogSequence++;

            backlog.setPTSequence(BacklogSequence);

            //Add Sequence to Project Task
            projectTask.setProjectSequence(backlog.getProjectIdentifier()+"-"+BacklogSequence);
            projectTask.setProjectIdentifier(projectIdentifier);

            //INITIAL priority when priority null

            //INITIAL status when status is null
            if(projectTask.getStatus()==""|| projectTask.getStatus()==null){
                projectTask.setStatus("TO_DO");
            }

            if(projectTask.getPriority()==null){ //In the future we need projectTask.getPriority()== 0 to handle the form
                projectTask.setPriority(3);
            }

            return projectTaskRepository.save(projectTask);
        } catch (Exception e){
            throw new ProjectNotFoundException("Project " + projectIdentifier + " does not exist");
        }

    }
    public ProjectTask updateProjectTask(ProjectTask projectTask, String projectIdentifier, String taskId){

        try {

            ProjectTask projectTaskCurrent = findPTByProjectSequence(projectIdentifier, taskId);

            if (projectTaskCurrent == null)
                throw new ProjectNotFoundException("Project Task for project id: " + projectIdentifier + " and task id " + taskId + " was not found");

            projectTaskCurrent = projectTask;

            return projectTaskRepository.save(projectTaskCurrent);
        } catch (Exception e){
            throw new ProjectNotFoundException("Project " + projectIdentifier + " does not exist");
        }

    }

    public void deleteProjectTask(String projectIdentifier, String taskId){
            ProjectTask projectTaskCurrent = findPTByProjectSequence(projectIdentifier, taskId);

            if (projectTaskCurrent == null)
                throw new ProjectNotFoundException("Project Task for project id: " + projectIdentifier + " and task id " + taskId + " was not found");

            projectTaskRepository.delete(projectTaskCurrent);

    }

    public Iterable<ProjectTask> findBacklogById(String id) {
        Project project = projectRepository.findByProjectIdentifier(id);

        if (project == null)
            throw new ProjectNotFoundException("Project with id: " + id + " was not found");

        return projectTaskRepository.findByProjectIdentifierOrderByPriority(id);
    }

    public ProjectTask findPTByProjectSequence(String id, String sequenceId){
        Project project = projectRepository.findByProjectIdentifier(id);

        if (project == null)
            throw new ProjectNotFoundException("Project with id: " + id + " was not found");

        ProjectTask projectTask = projectTaskRepository.findByProjectIdentifierAndProjectSequence(id, sequenceId);

        if (projectTask == null)
            throw new ProjectNotFoundException("Project Task for project id: " + id + " and sequence id " + sequenceId + " was not found");

        return  projectTask;
    }
}
package com.mush490.ppmtool.web;

import com.mush490.ppmtool.domain.ProjectTask;
import com.mush490.ppmtool.services.MapValidationErrorService;
import com.mush490.ppmtool.services.ProjectTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/backlogs")
@CrossOrigin
public class BacklogController {

    @Autowired
    private ProjectTaskService projectTaskService;

    @Autowired
    private MapValidationErrorService mapValidationErrorService;

    @PostMapping("/{projectId}/tasks")
    public ResponseEntity<?> addPTtoBacklog(@Valid @RequestBody ProjectTask projectTask,
                                            BindingResult result, @PathVariable String projectId){
        //show delete
        //custom exception

        ResponseEntity<?> errorMap = mapValidationErrorService.validate(result);
        if (errorMap != null) return errorMap;

        ProjectTask projectTask1 = projectTaskService.addProjectTask(projectId, projectTask);

        return new ResponseEntity<ProjectTask>(projectTask1, HttpStatus.CREATED);

    }

    @PatchMapping("/{projectId}/tasks/{taskId}")
    public ResponseEntity<?> updateProjectTask(@Valid @RequestBody ProjectTask projectTask,
                                            BindingResult result, @PathVariable String projectId, @PathVariable String taskId){

        ResponseEntity<?> errorMap = mapValidationErrorService.validate(result);
        if (errorMap != null) return errorMap;

        ProjectTask projectTask1 = projectTaskService.updateProjectTask(projectTask, projectId, taskId);

        return new ResponseEntity<ProjectTask>(projectTask1, HttpStatus.OK);
    }

    @DeleteMapping("/{projectId}/tasks/{taskId}")
    public ResponseEntity<?> deleteProjectTask(@PathVariable String projectId, @PathVariable String taskId){

        projectTaskService.deleteProjectTask(projectId, taskId);

        return new ResponseEntity<String>(taskId + " was deleted successfully", HttpStatus.OK);
    }

    @GetMapping("/{projectId}/tasks")
    public Iterable<ProjectTask> getProjectBacklog(@PathVariable String projectId){

        return projectTaskService.findBacklogById(projectId);

    }

    @GetMapping("/{projectId}/tasks/{taskId}")
    public ResponseEntity<?> getProjectBacklog(@PathVariable String projectId, @PathVariable String taskId){
        ProjectTask projectTask = projectTaskService.findPTByProjectSequence(projectId, taskId);
        return new ResponseEntity<ProjectTask>(projectTask, HttpStatus.OK);
    }
}
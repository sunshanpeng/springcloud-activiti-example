package com.platform.activiti.controller;

import com.platform.activiti.dto.TaskDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author sunshanpeng
 * @Date 2018/5/15
 * @Time 17:02
 */
@Api(tags = "task")
@RestController
@RequestMapping("/task")
public class TaskController {

    @Resource
    private TaskService taskService;

    /**
     * 查看指派给个人的任务
     *
     * @param userId
     * @return
     */
    @ApiOperation("查看指派给个人的任务")
    @GetMapping("/getPrivateTodoList")
    public List getPrivateTodoList(String userId) {
        TaskQuery taskQuery = taskService.createTaskQuery();
        List<Task> taskList = taskQuery.taskCandidateOrAssigned(userId).list();
        List<TaskDTO> list = new ArrayList<>();
        if (taskList != null) {
            for (Task task : taskList) {
                list.add(new TaskDTO(task));
            }
        }

        return list;
    }

    /**
     * 查看指派给某个角色的任务
     *
     * @param role
     * @return
     */
    @ApiOperation("查看指派给某个角色的任务")
    @GetMapping("/getPublicTodoList")
    public List getPublicTodoList(String role) {
        TaskQuery taskQuery = taskService.createTaskQuery();
        List<Task> taskList = taskQuery.taskCandidateGroup(role).list();
        List<TaskDTO> list = new ArrayList<>();
        if (taskList != null) {
            for (Task task : taskList) {
                list.add(new TaskDTO(task));
            }
        }

        return list;
    }


    /**
     * 处理任务
     *
     * @param taskId
     */
    @ApiOperation("处理任务")
    @PostMapping("/complete")
    public void complete(String taskId,
                         String userId,
                         @RequestBody Map<String, Object> args) {
        taskService.claim(taskId, userId);
        taskService.complete(taskId, args);
    }

    /**
     * 转办任务
     *
     * @param taskId
     * @param userId
     */
    @ApiOperation("转办任务")
    @PostMapping("/turnTodo")
    public String turnTodo(String taskId, String userId) {
        taskService.setAssignee(taskId, userId);
        return "success";
    }

    /**
     * 签收任务
     *
     * @param taskId
     */
    @ApiOperation("签收任务")
    @PostMapping("/claim")
    public void claim(String taskId,
                         String userId) {
        taskService.claim(taskId, userId);
    }

}

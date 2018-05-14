package com.platform.activiti.controller;

import com.platform.activiti.dto.ProcessDefinitionDTO;
import com.platform.activiti.dto.ProcessInstanceDTO;
import com.platform.activiti.dto.TaskDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Api(description = "Activiti接口", tags = "activiti")
@RestController
public class ActivitiController {

    @Resource
    private RuntimeService runtimeService;

    @Resource
    private IdentityService identityService;

    @Resource
    private TaskService taskService;

    @Resource
    private RepositoryService repositoryService;

    /**
     * 获取已部署的流程实例
     *
     * @return
     */
    @ApiOperation("获取已部署的流程实例")
    @GetMapping("/getProcessDefinitionList")
    public List getProcessDefinitionList() {
        List<ProcessDefinition> listPage = repositoryService.createProcessDefinitionQuery().list();
        List<ProcessDefinitionDTO> list = new ArrayList<>();
        if (listPage != null) {
            for (ProcessDefinition processDefinition : listPage) {
                list.add(new ProcessDefinitionDTO(processDefinition));
            }
        }
        return list;
    }

    /**
     * 新建流程实例（开始流程）
     *
     * @param processDefinitionId
     * @param businessKey
     * @param params 业务参数：比如applyUserId
     */
    @ApiOperation("新建流程实例（开始流程）")
    @PostMapping("/start")
    public void start(String processDefinitionId,
                      @RequestParam(required = false) String businessKey,
                      @RequestBody Map<String, Object> params) {
        runtimeService.startProcessInstanceById(processDefinitionId, businessKey, params);
    }

    /**
     * 查看指派给个人的流程实例
     *
     * @param userId
     * @return
     */
    @ApiOperation("查看指派给个人的流程实例")
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
     * 查看指派给某个角色的流程实例
     *
     * @param role
     * @return
     */
    @ApiOperation("查看指派给某个角色的流程实例")
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
     * 查看参与的流程实例
     *
     * @param userId
     * @return
     */
    @ApiOperation("查看参与的流程实例")
    @GetMapping("/getInvolvedList")
    public List getInvolvedList(String userId) {
        List<ProcessInstance> processInstances = runtimeService.createProcessInstanceQuery().involvedUser(userId).list();
        List<ProcessInstanceDTO> list = new ArrayList<>();
        if (processInstances != null) {
            for (ProcessInstance processInstance : processInstances) {
                list.add(new ProcessInstanceDTO(processInstance));
            }
        }

        return list;
    }

    /**
     * 签收任务
     * 处理角色任务之前需要先签收
     *
     * @param taskId
     * @param userId
     */
    @ApiOperation("签收任务")
    @PostMapping("/claim")
    public void claim(String taskId, String userId) {
        taskService.claim(taskId, userId);
    }

    /**
     * 完成实例
     *
     * @param taskId
     */
    @ApiOperation("完成实例")
    @PostMapping("/complete")
    public void complete(String taskId, @RequestBody Map<String, Object> args) {
        taskService.complete(taskId, args);
    }
}
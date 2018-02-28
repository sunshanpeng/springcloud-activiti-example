package com.platform.business.controller;

import com.platform.business.client.ActivitiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BusinessController {

    @Autowired
    private ActivitiClient activitiClient;


    /**
     * 获取已部署的流程实例
     *
     * @return
     */
    @GetMapping("/getProcessDefinitionList")
    public List getProcessDefinitionList() {
        return activitiClient.getProcessDefinitionList();
    }

    /**
     * 新建流程实例（开始流程）
     *
     * @param processDefinitionId
     * @param userId
     * @param businessKey
     */
    @PostMapping("/start")
    public void start(String processDefinitionId,
               String userId,
               @RequestParam(required = false) String businessKey) {
        activitiClient.start(processDefinitionId, userId, businessKey);
    }

    /**
     * 查看指派给个人的流程实例
     *
     * @param userId
     * @return
     */
    @GetMapping("/getPrivateTodoList")
    public List getPrivateTodoList(String userId) {
        return activitiClient.getPrivateTodoList(userId);
    }

    /**
     * 查看指派给某个角色的流程实例
     *
     * @param role
     * @return
     */
    @GetMapping("/getPublicTodoList")
    public List getPublicTodoList(String role) {
        return activitiClient.getPublicTodoList(role);
    }

    /**
     * 查看参与的流程实例
     *
     * @param userId
     * @return
     */
    @GetMapping("/getInvolvedList")
    public List getInvolvedList(String userId) {
        return activitiClient.getInvolvedList(userId);
    }

    /**
     * 签收任务
     * 处理角色任务之前需要先签收
     *
     * @param taskId
     * @param userId
     */
    @PostMapping("/claim")
    public void claim(String taskId, String userId) {
        activitiClient.claim(taskId, userId);
    }

    /**
     * 完成实例
     *
     * @param taskId
     */
    @PostMapping("/complete")
    public void complete(String taskId) {
        activitiClient.complete(taskId);
    }

}
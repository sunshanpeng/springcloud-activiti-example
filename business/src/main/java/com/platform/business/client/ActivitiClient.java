package com.platform.business.client;

import com.platform.business.client.hystrix.ActivitiClientFailBackFactory;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "activiti", fallbackFactory = ActivitiClientFailBackFactory.class)
public interface ActivitiClient {

    /**
     * 获取已部署的流程实例
     *
     * @return
     */
    @GetMapping("/getProcessDefinitionList")
    List getProcessDefinitionList();

    /**
     * 新建流程实例（开始流程）
     *
     * @param processDefinitionId
     * @param userId
     * @param businessKey
     */
    @RequestMapping(value = "/start", method = RequestMethod.POST)
    void start(@RequestParam("processDefinitionId") String processDefinitionId,
               @RequestParam("userId") String userId,
               @RequestParam("businessKey") String businessKey);

    /**
     * 查看指派给个人的流程实例
     *
     * @param userId
     * @return
     */
    @GetMapping("/getPrivateTodoList")
    List getPrivateTodoList(String userId);

    /**
     * 查看指派给某个角色的流程实例
     *
     * @param role
     * @return
     */
    @GetMapping("/getPublicTodoList")
    List getPublicTodoList(String role);

    /**
     * 查看参与的流程实例
     *
     * @param userId
     * @return
     */
    @GetMapping("/getInvolvedList")
    List getInvolvedList(String userId);

    /**
     * 签收任务
     * 处理角色任务之前需要先签收
     *
     * @param taskId
     * @param userId
     */
    @PostMapping("/claim")
    void claim(@RequestParam("taskId") String taskId,
               @RequestParam("userId") String userId);

    /**
     * 完成实例
     *
     * @param taskId
     */
    @PostMapping("/complete")
    void complete(String taskId);
}

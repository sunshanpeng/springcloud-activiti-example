package com.platform.activiti.controller;

import com.platform.activiti.dto.HistoricProcessInstanceDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;

/**
 * @Author sunshanpeng
 * @Date 2018/5/16
 * @Time 16:56
 */
@Api(tags = "history")
@RestController
@RequestMapping("/history")
@Slf4j
public class HisController {

    @Resource
    private HistoryService historyService;
    @Resource
    private RuntimeService runtimeService;

    @ApiOperation("获取该流程定义已完成的流程实例")
    @GetMapping("/getFinishedProcessInstances")
    public List getFinishedProcessInstances(String processInstanceId) {
        List<HistoricProcessInstance> list  = historyService.createHistoricProcessInstanceQuery()
                .processDefinitionId(processInstanceId).orderByProcessInstanceEndTime().desc().list();
        List<HistoricProcessInstanceDTO> results = new LinkedList<>();
        if (!CollectionUtils.isEmpty(list)) {
            for (HistoricProcessInstance instance : list) {
                results.add(new HistoricProcessInstanceDTO(instance));
            }
        }
        return results;
    }

    @ApiOperation("获取已办理的流程实例")
    @GetMapping("/getDoneProcessInstances")
    public List getDoneProcessInstances(String userId) {
        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().taskAssignee(userId).list();
        if (!CollectionUtils.isEmpty(list)) {
            for (HistoricTaskInstance historicTaskInstance : list) {
                runtimeService.createProcessInstanceQuery().processInstanceId(historicTaskInstance.getProcessInstanceId());
            }
        }
        return list;
    }
}

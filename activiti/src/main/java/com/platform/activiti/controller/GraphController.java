package com.platform.activiti.controller;

import com.platform.activiti.graph.ProcessInstanceDiagramCmd;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.task.Task;
import org.activiti.image.ProcessDiagramGenerator;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;
import org.activiti.spring.ProcessEngineFactoryBean;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author sunshanpeng
 * @Date 2018/5/30
 * @Time 14:28
 */
@RestController
@RequestMapping("/graph")
public class GraphController {

    @Resource
    private TaskService taskService;

    @Resource
    private HistoryService historyService;

    @Resource
    private RepositoryService repositoryService;

    @Resource
    private RuntimeService runtimeService;

    @Resource
    private ProcessEngineConfiguration processEngineConfiguration;

    @Autowired
    private ProcessEngineFactoryBean processEngine;

    @Resource
    private ManagementService managementService;

    @GetMapping(value = "/read")
    public void loadByDeployment(@RequestParam String processDefinitionId,
                                 @RequestParam String bussinessKey,
                                 HttpServletResponse response) throws Exception {
        InputStream resourceAsStream = traceProcessImage(bussinessKey, processDefinitionId);
        byte[] b = new byte[1024];
        int len = -1;
        while ((len = resourceAsStream.read(b, 0, 1024)) != -1) {
            response.getOutputStream().write(b, 0, len);
        }
    }

    @GetMapping(value = "/read1")
    public void loadByDeployment(@RequestParam String processInstanceId,
                                 HttpServletResponse response) throws Exception {
        Command<InputStream> cmd = new ProcessInstanceDiagramCmd(processInstanceId, runtimeService, repositoryService, processEngine, historyService);
        InputStream resourceAsStream = managementService.executeCommand(cmd);
        byte[] b = new byte[1024];
        int len = -1;
        while ((len = resourceAsStream.read(b, 0, 1024)) != -1) {
            response.getOutputStream().write(b, 0, len);
        }
    }

    /**
     * 方法一：生成流程图；带进度:<br>
     * 得到带有高亮节点的流程图
     *
     *            流程实例id
     * @return
     */
    public InputStream traceProcessImage(String bussinessKey,
                                         String processDefinitionId) {
        // 经过的节点
        BpmnModel bpmnModel = null;
        InputStream is = null;
        List<String> activeActivityIds = new ArrayList<>(); // 已完成的节点+当前节点
        List<String> finishedActiveActivityIds = new ArrayList<>(); // 已完成的节点
        List<String> highLightedFlows = new ArrayList<>();
        List<Task> tasks = taskService.createTaskQuery()
                .processInstanceBusinessKey(bussinessKey).list();
        // .processInstanceId(processInstanceId).list();
        for (Task task : tasks) {
            if (task == null)
                throw new IllegalArgumentException("任务不存在！");
            String taskId = task.getId();
            if (StringUtils.isBlank(taskId))
                throw new IllegalArgumentException("任务ID不能为空！");
            // 当前任务节点
            // Task task =
            // taskService.createTaskQuery().taskId(taskId).singleResult();
            // List<String> activeActivityIds =
            // runtimeService.getActiveActivityIds(task.getExecutionId());

            // 必须添加此行才能取到配置文件中的字体，待根本解决问题后删除
            // Context.setProcessEngineConfiguration(processEngineConfiguration);
            // return ProcessDiagramGenerator.generateDiagram(bpmnModel, "PNG",
            // activeActivityIds);

            bpmnModel = repositoryService.getBpmnModel(task
                    .getProcessDefinitionId());
            // 已执行完的任务节点
            List<HistoricActivityInstance> finishedInstances = historyService
                    .createHistoricActivityInstanceQuery()
                    .processInstanceId(task.getProcessInstanceId()).finished()
                    .list();
            for (HistoricActivityInstance hai : finishedInstances) {
                finishedActiveActivityIds.add(hai.getActivityId());
            }

            // 已完成的节点+当前节点
            activeActivityIds.addAll(finishedActiveActivityIds);
            activeActivityIds.addAll(runtimeService.getActiveActivityIds(task
                    .getProcessInstanceId()));
            // 经过的流
            ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) repositoryService
                    .getProcessDefinition(task.getProcessDefinitionId());
            getHighLightedFlows(processDefinitionEntity.getActivities(),
                    highLightedFlows, activeActivityIds);

        }
        ProcessDiagramGenerator pdg = new DefaultProcessDiagramGenerator();
        // 参数activeActivityIds为已完成的节点+当前节点,已完成节点+当前节点都有红色框；
        // finishedActiveActivityIds为已完成的节点，如果输入finishedActiveActivityIds，则当前节点无红色边框
        if (bpmnModel != null) {
            is = pdg.generateDiagram(bpmnModel, "PNG",
                    finishedActiveActivityIds, highLightedFlows,
                    processEngineConfiguration.getActivityFontName(),
                    processEngineConfiguration.getAnnotationFontName(),
                    processEngineConfiguration.getLabelFontName(),
                    processEngineConfiguration.getClassLoader(), 1.0);
        } else {
            // 经过的流
            ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) repositoryService
                    .getProcessDefinition(processDefinitionId);
            List<HistoricProcessInstance> hProcessInstances = historyService
                    .createHistoricProcessInstanceQuery()
                    .processInstanceBusinessKey(bussinessKey).list();
            if (hProcessInstances != null && hProcessInstances.size() > 0) {
                List<HistoricActivityInstance> finishedInstances = historyService
                        .createHistoricActivityInstanceQuery()
                        .processInstanceId(hProcessInstances.get(0).getId())
                        .finished().list();
                for (HistoricActivityInstance hai : finishedInstances) {
                    finishedActiveActivityIds.add(hai.getActivityId());
                }
                getHighLightedFlows(processDefinitionEntity.getActivities(),
                        highLightedFlows, finishedActiveActivityIds);
            }
            bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
            is = pdg.generateDiagram(bpmnModel, "PNG",
                    finishedActiveActivityIds, highLightedFlows,
                    processEngineConfiguration.getActivityFontName(),
                    processEngineConfiguration.getAnnotationFontName(),
                    processEngineConfiguration.getLabelFontName(),
                    processEngineConfiguration.getClassLoader(), 1.0);
        }
        try {
            return is;
        } catch (Exception e) {
            throw new RuntimeException("生成流程图异常！", e);
        } finally {
        }
    }

    /*
     * 递归查询经过的流
     */
    private void getHighLightedFlows(List<ActivityImpl> activityList,
                                     List<String> highLightedFlows,
                                     List<String> historicActivityInstanceList) {
        for (ActivityImpl activity : activityList) {
            if (activity.getProperty("type").equals("subProcess")) {
                // get flows for the subProcess
                getHighLightedFlows(activity.getActivities(), highLightedFlows,
                        historicActivityInstanceList);
            }

            if (historicActivityInstanceList.contains(activity.getId())) {
                List<PvmTransition> pvmTransitionList = activity
                        .getOutgoingTransitions();
                for (PvmTransition pvmTransition : pvmTransitionList) {
                    String destinationFlowId = pvmTransition.getDestination()
                            .getId();
                    if (historicActivityInstanceList
                            .contains(destinationFlowId)) {
                        highLightedFlows.add(pvmTransition.getId());
                    }
                }
            }
        }
    }

}

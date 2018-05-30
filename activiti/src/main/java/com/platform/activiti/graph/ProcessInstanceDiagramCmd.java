package com.platform.activiti.graph;

import com.google.common.collect.Lists;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;
import org.activiti.spring.ProcessEngineFactoryBean;

import java.io.InputStream;
import java.util.*;

/**
 * @Author sunshanpeng
 * @Date 2018/5/30
 * @Time 15:05
 */
public class ProcessInstanceDiagramCmd implements Command<InputStream> {
    protected String processInstanceId;

    private RuntimeService runtimeService;

    private RepositoryService repositoryService;

    private ProcessEngineFactoryBean processEngine;

    private HistoryService historyService;

    public ProcessInstanceDiagramCmd(String processInstanceId, RuntimeService runtimeService, RepositoryService repositoryService, ProcessEngineFactoryBean processEngine, HistoryService historyService) {
        this.processInstanceId = processInstanceId;
        this.runtimeService = runtimeService;
        this.repositoryService = repositoryService;
        this.processEngine = processEngine;
        this.historyService = historyService;
    }

    public InputStream execute(CommandContext commandContext) {
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId).singleResult();

        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();

        if (processInstance == null && historicProcessInstance == null) {
            return null;
        }

        ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) repositoryService
                .getProcessDefinition(processInstance == null ? historicProcessInstance.getProcessDefinitionId() : processInstance.getProcessDefinitionId());

        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition.getId());

        List<String> activeActivityIds = Lists.newArrayList();
        if (processInstance != null) {
            activeActivityIds = runtimeService.getActiveActivityIds(processInstance.getProcessInstanceId());
        } else {
            activeActivityIds.add(historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId).activityType("endEvent")
                    .singleResult().getActivityId());
        }
        // 使用spring注入引擎请使用下面的这行代码
        Context.setProcessEngineConfiguration(processEngine.getProcessEngineConfiguration());

        List<String> highLightedFlows = getHighLightedFlows(processDefinition, processInstanceId);

        InputStream imageStream = new DefaultProcessDiagramGenerator().generateDiagram(bpmnModel, "png", activeActivityIds,
                highLightedFlows);

        return imageStream;
    }

    /**
     * getHighLightedFlows
     *
     * @param processDefinition
     * @param processInstanceId
     * @return
     */
    private List<String> getHighLightedFlows(ProcessDefinitionEntity processDefinition, String processInstanceId) {

        List<String> highLightedFlows = new ArrayList<String>();

//        List<HistoricActivityInstance> historicActivityInstances = historyService.createHistoricActivityInstanceQuery()
//                .processInstanceId(processInstanceId)
//                        //order by startime asc is not correct. use default order is correct.
//                        //.orderByHistoricActivityInstanceStartTime().asc()/*.orderByActivityId().asc()*/
//                .list();
        //上面注释掉的代码是官方的rest方法中提供的方案，可是我在实际测试中有Bug出现，所以做了一下修改。注意下面List内容的排序会影响流程走向红线丢失的问题
        List<HistoricActivityInstance> historicActivityInstances = historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId)
                .orderByHistoricActivityInstanceStartTime().orderByHistoricActivityInstanceEndTime().asc().list();
        LinkedList<HistoricActivityInstance> hisActInstList = new LinkedList<HistoricActivityInstance>();
        hisActInstList.addAll(historicActivityInstances);

        getHighlightedFlows(processDefinition.getActivities(), hisActInstList, highLightedFlows);

        return highLightedFlows;
    }

    /**
     * getHighlightedFlows
     * <p/>
     * code logic:
     * 1. Loop all activities by id asc order;
     * 2. Check each activity's outgoing transitions and eventBoundery outgoing transitions, if outgoing transitions's destination.id is in other executed activityIds, add this transition to highLightedFlows List;
     * 3. But if activity is not a parallelGateway or inclusiveGateway, only choose the earliest flow.
     *
     * @param activityList
     * @param hisActInstList
     * @param highLightedFlows
     */
    private void getHighlightedFlows(List<ActivityImpl> activityList, LinkedList<HistoricActivityInstance> hisActInstList, List<String> highLightedFlows) {

        //check out startEvents in activityList
        List<ActivityImpl> startEventActList = new ArrayList<ActivityImpl>();
        Map<String, ActivityImpl> activityMap = new HashMap<String, ActivityImpl>(activityList.size());
        for (ActivityImpl activity : activityList) {

            activityMap.put(activity.getId(), activity);

            String actType = (String) activity.getProperty("type");
            if (actType != null && actType.toLowerCase().indexOf("startevent") >= 0) {
                startEventActList.add(activity);
            }
        }

        //These codes is used to avoid a bug:
        //ACT-1728 If the process instance was started by a callActivity, it will be not have the startEvent activity in ACT_HI_ACTINST table
        //Code logic:
        //Check the first activity if it is a startEvent, if not check out the startEvent's highlight outgoing flow.
        HistoricActivityInstance firstHistActInst = hisActInstList.getFirst();
        String firstActType = (String) firstHistActInst.getActivityType();
        if (firstActType != null && firstActType.toLowerCase().indexOf("startevent") < 0) {
            PvmTransition startTrans = getStartTransaction(startEventActList, firstHistActInst);
            if (startTrans != null) {
                highLightedFlows.add(startTrans.getId());
            }
        }

        while (!hisActInstList.isEmpty()) {
            HistoricActivityInstance histActInst = hisActInstList.removeFirst();
            ActivityImpl activity = activityMap.get(histActInst.getActivityId());
            if (activity != null) {
                boolean isParallel = false;
                String type = histActInst.getActivityType();
                if ("parallelGateway".equals(type) || "inclusiveGateway".equals(type)) {
                    isParallel = true;
                } else if ("subProcess".equals(histActInst.getActivityType())) {
                    getHighlightedFlows(activity.getActivities(), hisActInstList, highLightedFlows);
                }

                List<PvmTransition> allOutgoingTrans = new ArrayList<PvmTransition>();
                allOutgoingTrans.addAll(activity.getOutgoingTransitions());
                allOutgoingTrans.addAll(getBoundaryEventOutgoingTransitions(activity));
                List<String> activityHighLightedFlowIds = getHighlightedFlows(allOutgoingTrans, hisActInstList, isParallel);
                highLightedFlows.addAll(activityHighLightedFlowIds);
            }
        }
    }

    /**
     * Check out the outgoing transition connected to firstActInst from startEventActList
     *
     * @param startEventActList
     * @param firstActInst
     * @return
     */
    private PvmTransition getStartTransaction(List<ActivityImpl> startEventActList, HistoricActivityInstance firstActInst) {
        for (ActivityImpl startEventAct : startEventActList) {
            for (PvmTransition trans : startEventAct.getOutgoingTransitions()) {
                if (trans.getDestination().getId().equals(firstActInst.getActivityId())) {
                    return trans;
                }
            }
        }
        return null;
    }

    /**
     * getBoundaryEventOutgoingTransitions
     *
     * @param activity
     * @return
     */
    private List<PvmTransition> getBoundaryEventOutgoingTransitions(ActivityImpl activity) {
        List<PvmTransition> boundaryTrans = new ArrayList<PvmTransition>();
        for (ActivityImpl subActivity : activity.getActivities()) {
            String type = (String) subActivity.getProperty("type");
            if (type != null && type.toLowerCase().indexOf("boundary") >= 0) {
                boundaryTrans.addAll(subActivity.getOutgoingTransitions());
            }
        }
        return boundaryTrans;
    }

    /**
     * find out single activity's highlighted flowIds
     *
     * @param pvmTransitionList
     * @param hisActInstList
     * @param isParallel
     * @return
     */
    private List<String> getHighlightedFlows(List<PvmTransition> pvmTransitionList, LinkedList<HistoricActivityInstance> hisActInstList, boolean isParallel) {

        List<String> highLightedFlowIds = new ArrayList<String>();

        PvmTransition earliestTrans = null;
        HistoricActivityInstance earliestHisActInst = null;

        for (PvmTransition pvmTransition : pvmTransitionList) {

            String destActId = pvmTransition.getDestination().getId();
            HistoricActivityInstance destHisActInst = findHisActInst(hisActInstList, destActId);
            if (destHisActInst != null) {
                if (isParallel) {
                    highLightedFlowIds.add(pvmTransition.getId());
                } else if (earliestHisActInst == null || (earliestHisActInst.getId().compareTo(destHisActInst.getId()) > 0)) {
                    earliestTrans = pvmTransition;
                    earliestHisActInst = destHisActInst;
                }
            }
        }

        if ((!isParallel) && earliestTrans != null) {
            highLightedFlowIds.add(earliestTrans.getId());
        }

        return highLightedFlowIds;
    }

    private HistoricActivityInstance findHisActInst(LinkedList<HistoricActivityInstance> hisActInstList, String actId) {
        for (HistoricActivityInstance hisActInst : hisActInstList) {
            if (hisActInst.getActivityId().equals(actId)) {
                return hisActInst;
            }
        }
        return null;
    }
}

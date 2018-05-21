package com.platform.activiti.dto;

import lombok.Data;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.identity.Authentication;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * @Author sunshanpeng
 * @Date 2018/5/16
 * @Time 17:01
 */
@Data
public class HistoricProcessInstanceDTO implements Serializable {

    private String id;
    private String businessKey;
    private String processDefinitionId;
    private String processDefinitionName;
    private String processDefinitionKey;
    private Integer processDefinitionVersion;
    private String deploymentId;
    private Date startTime;
    private Date endTime;
    private Long durationInMillis;
    private String startUserId;
    private String startActivityId;
    private String deleteReason;
    private String superProcessInstanceId;
    private String tenantId;
    private String name;
    private String description;
    Map<String, Object> variables;

    /**
     * 消费方不需要此构造器
     *
     * @param processInstance
     */
    public HistoricProcessInstanceDTO(HistoricProcessInstance processInstance) {
        id = processInstance.getId();
        businessKey = processInstance.getBusinessKey();
        processDefinitionId = processInstance.getProcessDefinitionId();
        processDefinitionName = processInstance.getProcessDefinitionName();
        processDefinitionKey = processInstance.getProcessDefinitionKey();
        processDefinitionVersion = processInstance.getProcessDefinitionVersion();
        deploymentId = processInstance.getDeploymentId();
        startTime = processInstance.getStartTime();
        endTime = processInstance.getEndTime();
        durationInMillis = processInstance.getDurationInMillis();
        startUserId = processInstance.getStartUserId();
        startActivityId = processInstance.getStartActivityId();
        deleteReason = processInstance.getDeleteReason();
        superProcessInstanceId = processInstance.getSuperProcessInstanceId();
        tenantId = processInstance.getTenantId();
        name = processInstance.getName();
        description = processInstance.getDescription();
        variables = processInstance.getProcessVariables();
    }
}

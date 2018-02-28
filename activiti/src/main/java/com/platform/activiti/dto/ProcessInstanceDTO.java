package com.platform.activiti.dto;

import lombok.Data;
import org.activiti.engine.runtime.ProcessInstance;

import java.io.Serializable;
import java.util.Map;

/**
 * activiti.processInstance's dto
 * @see org.activiti.engine.runtime.ProcessInstance
 *
 * @Author 孙善鹏
 * @Date 2018/2/28
 * @Time 16:25
 */
@Data
public class ProcessInstanceDTO implements Serializable {
    private static final long serialVersionUID = -108254392038025246L;

    private String processDefinitionId;

    private String processDefinitionName;

    private String processDefinitionKey;

    private Integer processDefinitionVersion;

    private String deploymentId;

    private String businessKey;

    private Map<String, Object> processVariables;

    private String tenantId;

    private String name;

    private String description;

    private String localizedName;

    private String localizedDescription;

    /**
     * 消费方不需要此构造器
     * @param processInstance
     */
    public ProcessInstanceDTO(ProcessInstance processInstance) {
        this.processDefinitionId = processInstance.getProcessDefinitionId();
        this.processDefinitionName = processInstance.getProcessDefinitionName();
        this.processDefinitionKey = processInstance.getProcessDefinitionKey();
        this.processDefinitionVersion = processInstance.getProcessDefinitionVersion();
        this.deploymentId = processInstance.getDeploymentId();
        this.businessKey = processInstance.getBusinessKey();
        this.processVariables = processInstance.getProcessVariables();
        this.tenantId = processInstance.getTenantId();
        this.name = processInstance.getName();
        this.description = processInstance.getDescription();
        this.localizedName = processInstance.getLocalizedName();
        this.localizedDescription = processInstance.getLocalizedDescription();
    }
}

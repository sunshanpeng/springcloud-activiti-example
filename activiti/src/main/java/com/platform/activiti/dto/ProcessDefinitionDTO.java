package com.platform.activiti.dto;

import lombok.Data;
import org.activiti.engine.repository.ProcessDefinition;

import java.io.Serializable;

/**
 * activiti.processDefinition's dto
 *
 * @Author sunshanpeng
 * @Date 2018/2/28
 * @Time 15:38
 * @see org.activiti.engine.repository.ProcessDefinition
 */
@Data
public class ProcessDefinitionDTO implements Serializable {
    private static final long serialVersionUID = -8903560893133125106L;

    private String id;

    private String category;

    private String name;

    private String key;

    private String description;

    private Integer version;

    private String resourceName;

    private String deploymentId;

    private String diagramResourceName;

    private String tenantId;

    /**
     * 消费方不需要此构造器
     *
     * @param processDefinition
     */
    public ProcessDefinitionDTO(ProcessDefinition processDefinition) {
        this.id = processDefinition.getId();
        this.category = processDefinition.getCategory();
        this.name = processDefinition.getName();
        this.key = processDefinition.getKey();
        this.description = processDefinition.getDescription();
        this.version = processDefinition.getVersion();
        this.resourceName = processDefinition.getResourceName();
        this.deploymentId = processDefinition.getDeploymentId();
        this.diagramResourceName = processDefinition.getDiagramResourceName();
        this.tenantId = processDefinition.getTenantId();
    }

}

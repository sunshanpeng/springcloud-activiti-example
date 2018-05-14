package com.platform.activiti.dto;

import lombok.Data;
import org.activiti.engine.task.Task;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * activiti.task's dto
 *
 * @Author sunshanpeng
 * @Date 2018/2/28
 * @Time 16:02
 * @see org.activiti.engine.task.Task
 */
@Data
public class TaskDTO implements Serializable {
    private static final long serialVersionUID = 2156388370075082440L;
    private String id;

    private String name;

    private String description;

    private Integer priority;

    private String owner;

    private String assignee;

    private String processInstanceId;

    private String executionId;

    private String processDefinitionId;

    private Date createTime;

    private String taskDefinitionKey;

    private Date dueDate;

    private String category;

    private String parentTaskId;

    private String tenantId;

    private String formKey;

    private Map<String, Object> taskLocalVariables;

    private Map<String, Object> processVariables;

    /**
     * 消费方不需要此构造器
     *
     * @param task
     */
    public TaskDTO(Task task) {
        this.id = task.getId();
        this.name = task.getName();
        this.description = task.getDescription();
        this.priority = task.getPriority();
        this.owner = task.getOwner();
        this.assignee = task.getAssignee();
        this.processInstanceId = task.getProcessInstanceId();
        this.executionId = task.getExecutionId();
        this.processDefinitionId = task.getProcessDefinitionId();
        this.createTime = task.getCreateTime();
        this.taskDefinitionKey = task.getTaskDefinitionKey();
        this.dueDate = task.getDueDate();
        this.category = task.getCategory();
        this.parentTaskId = task.getParentTaskId();
        this.tenantId = task.getTenantId();
        this.formKey = task.getFormKey();
        this.taskLocalVariables = task.getTaskLocalVariables();
        this.processVariables = task.getProcessVariables();
    }
}

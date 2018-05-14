package com.platform.activiti.test;

import com.platform.activiti.BaseTest;
import org.activiti.engine.repository.ProcessDefinition;
import org.junit.Test;

/**
 * Created by enmonster on 2018/2/9.
 */
public class StartWorkflowTest extends BaseTest {

    @Test
    public void testStartProcessInstanceById() {
        ProcessDefinition processDefinition
                = repositoryService.createProcessDefinitionQuery().processDefinitionKey("leave").singleResult();
        ProcessDefinition processDefinition1
                = repositoryService.createProcessDefinitionQuery().processDefinitionId("leave:1:10").singleResult();
        System.out.println(processDefinition.getId().equals(processDefinition1.getId()));
        identityService.setAuthenticatedUserId("sun");
        runtimeService.startProcessInstanceById(processDefinition.getId(),"testBusinessKey");
    }


}

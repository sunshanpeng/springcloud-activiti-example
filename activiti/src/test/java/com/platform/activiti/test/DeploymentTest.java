package com.platform.activiti.test;

import com.platform.activiti.BaseTest;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

public class DeploymentTest extends BaseTest {

    @Test
    public void testClasspathDeployment() {
        //相对路径
        String path = "processes/candidateUserInUserTask.bpmn";
        repositoryService.createDeployment().addClasspathResource(path).deploy();

        // 验证流程定义是否部署成功
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
        long count = processDefinitionQuery.processDefinitionKey("candidateUserInUserTask").count();
//        assertEquals(1, count); //每部署一次count+1
    }

    @Test
    public void testInputStreamDeployment() throws FileNotFoundException {
        //绝对路径
        String path = "E:\\workspace\\activiti\\platform-workflow\\target\\classes\\processes\\candidateUserInUserTask.bpmn";
        FileInputStream fis = new FileInputStream(path);
        repositoryService.createDeployment().addInputStream("candidateUserInUserTask.bpmn", fis).deploy();

        // 验证流程定义是否部署成功
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
        long count = processDefinitionQuery.processDefinitionKey("candidateUserInUserTask").count();
//        assertEquals(1, count); //每部署一次count+1
    }

    @Test
    public void testCharsDeployment() {
        String text = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<definitions xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:activiti=\"http://activiti.org/bpmn\" xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\" xmlns:omgdc=\"http://www.omg.org/spec/DD/20100524/DC\" xmlns:omgdi=\"http://www.omg.org/spec/DD/20100524/DI\" typeLanguage=\"http://www.w3.org/2001/XMLSchema\" expressionLanguage=\"http://www.w3.org/1999/XPath\" targetNamespace=\"http://www.kafeitu.me/activiti-in-action\">"
                + "  <process id=\"candidateUserInUserTask\" name=\"candidateUserInUserTask\">"
                + "    <startEvent id=\"startevent1\" name=\"Start\"></startEvent>"
                + "    <userTask id=\"usertask1\" name=\"用户任务包含多个直接候选人\" activiti:candidateUsers=\"jackchen, henryyan\"></userTask>"
                + "    <sequenceFlow id=\"flow1\" name=\"\" sourceRef=\"startevent1\" targetRef=\"usertask1\"></sequenceFlow>"
                + "    <endEvent id=\"endevent1\" name=\"End\"></endEvent>"
                + "    <sequenceFlow id=\"flow2\" name=\"\" sourceRef=\"usertask1\" targetRef=\"endevent1\"></sequenceFlow>"
                + "  </process>"
                + "</definitions>";
        repositoryService.createDeployment().addString("candidateUserInUserTask.bpmn", text).deploy();

        // 验证流程定义是否部署成功
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
        long count = processDefinitionQuery.processDefinitionKey("candidateUserInUserTask").count();
//        assertEquals(1, count); //每部署一次count+1
    }

    /**
     * 查看已部署的流程
     */
    @Test
    public void processDefinition() {
        List<ProcessDefinition> processDefinitions = repositoryService.createProcessDefinitionQuery().list();
        for (ProcessDefinition processDefinition : processDefinitions) {
            System.out.println(processDefinition);
//            runtimeService.startProcessInstanceById(processDefinition.getId());
        }
    }

}

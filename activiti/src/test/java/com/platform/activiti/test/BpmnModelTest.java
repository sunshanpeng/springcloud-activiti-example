package com.platform.activiti.test;

import com.platform.activiti.BaseTest;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.*;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.junit.Test;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author sunshanpeng
 * @Date 2018/6/4
 * @Time 15:51
 */
public class BpmnModelTest extends BaseTest {

    @Resource
    private RepositoryService repositoryService;

    @Test
    public void testBpmnModel() {
        BpmnModel bpmnModel = repositoryService.getBpmnModel("leave4:1:9");
        bpmnModel.getMainProcess();
    }

    @Test
    public void testAddBpmnModel() throws UnsupportedEncodingException {
        BpmnModel bpmnModel = new BpmnModel();
        bpmnModel.setTargetNamespace("http://www.activiti.org/add");
        //namespaceMap
        addNamespaceMap(bpmnModel);
        //flowLocationMap
        //locationMap
        //processes
        addProcesses(bpmnModel);
        //发布流程
        String processName = "testAddBpmnModel.bpmn20.xml";
        byte[] bpmnBytes = new BpmnXMLConverter().convertToXML(bpmnModel);

        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment()
                .name("modelDataName")
                .addString(processName, new String(bpmnBytes, "UTF-8"));

        Deployment deployment = deploymentBuilder.deploy();
    }

    private void addNamespaceMap(BpmnModel bpmnModel) {
        bpmnModel.addNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        bpmnModel.addNamespace("xsd", "http://www.w3.org/2001/XMLSchema");
        bpmnModel.addNamespace("activiti", "http://activiti.org/bpmn");
        bpmnModel.addNamespace("bpmndi", "http://www.omg.org/spec/BPMN/20100524/DI");
        bpmnModel.addNamespace("omgdc", "http://www.omg.org/spec/DD/20100524/DC");
        bpmnModel.addNamespace("omgdi", "http://www.omg.org/spec/DD/20100524/DI");
    }

    private void addProcesses(BpmnModel bpmnModel) {
        Process process = new Process();
        process.setId("testAdd");
        process.setName("测试用代码添加");
        process.setExecutable(true);
        //xmlRowNumber
        //xmlColumnNumber
//        addExecutionListeners(process);
        addFlowElementList(process);
        bpmnModel.addProcess(process);
    }

    private void addExecutionListeners(Process process) {
        ActivitiListener executionListener = new ActivitiListener();
        executionListener.setEvent("end");
        executionListener.setImplementationType("delegateExpression");
        executionListener.setImplementation("${executionEndListener}");
        ArrayList<ActivitiListener> executionListeners = new ArrayList<>();
        executionListeners.add(executionListener);
        //xmlRowNumber
        //xmlColumnNumber
        process.setExecutionListeners(executionListeners);
    }

    private void addFlowElementList(Process process) {
        process.addFlowElement(addStartEvent());
        process.addFlowElement(addSequenceFlow1());
        process.addFlowElement(addUserTaskEvent());
        process.addFlowElement(addSequenceFlow2());
        process.addFlowElement(addEndEvent());
    }

    private FlowElement addStartEvent() {
        StartEvent startEvent = new StartEvent();
        startEvent.setId("start");
        startEvent.setName("开始");
        List<SequenceFlow> sequenceFlows = new ArrayList<>();
        sequenceFlows.add(addSequenceFlow1());
        startEvent.setOutgoingFlows(sequenceFlows);
        return startEvent;
    }

    private FlowElement addUserTaskEvent() {
        UserTask userTask = new UserTask();
        userTask.setAssignee("apeng");
        userTask.setId("leader");
        userTask.setName("领导审批");
        List<SequenceFlow> sequenceFlows = new ArrayList<>();
        sequenceFlows.add(addSequenceFlow1());
        List<SequenceFlow> outgoingFlows = new ArrayList<>();
        outgoingFlows.add(addSequenceFlow2());
        userTask.setIncomingFlows(sequenceFlows);
        userTask.setOutgoingFlows(outgoingFlows);
        return userTask;
    }

    private FlowElement addEndEvent() {
        EndEvent event = new EndEvent();
        event.setId("end");
        event.setName("结束");
        List<SequenceFlow> sequenceFlows = new ArrayList<>();

        sequenceFlows.add(addSequenceFlow2());
        event.setIncomingFlows(sequenceFlows);
        return event;
    }

    private SequenceFlow addSequenceFlow1() {
        SequenceFlow sequenceFlow = new SequenceFlow();
        sequenceFlow.setId("_1");
        sequenceFlow.setSourceRef("start");
        sequenceFlow.setTargetRef("leader");
        return sequenceFlow;
    }

    private SequenceFlow addSequenceFlow2() {
        SequenceFlow sequenceFlow = new SequenceFlow();
        sequenceFlow.setId("_2");
        sequenceFlow.setSourceRef("leader");
        sequenceFlow.setTargetRef("end");
        return sequenceFlow;
    }


}

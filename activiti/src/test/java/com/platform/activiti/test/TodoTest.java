package com.platform.activiti.test;

import com.platform.activiti.BaseTest;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;

import java.util.List;

/**
 * Created by enmonster on 2018/2/9.
 */
public class TodoTest extends BaseTest {

    /**
     * 查询个人待办任务
     */
    @Test
    public void testTodo() {
        Task task = taskService.createTaskQuery().taskAssignee("李四").singleResult();
        System.out.println(task.getId());
        task = taskService.createTaskQuery().taskCandidateOrAssigned("李四").singleResult();
        taskService.createTaskQuery().processDefinitionId("myProcess:1:6").list();
        taskService.createTaskQuery().processDefinitionKey("myProcess").list();
    }

    /**
     * 查询部门待办任务
     */
    @Test
    public void  testManagement() throws Exception{
        //management为handleRequest节点配置的“activiti:candidateGroups”
        List<Task> tasks = taskService.createTaskQuery().taskCandidateGroup("management").list();
        for (Task task : tasks) {
            System.out.println("Task available:===== " + task.getId()+","+task.getName()+","+task.getDescription()+","+task.getOwner());
        }
    }

    /**
     * 参与
     */
    @Test
    public void testInvolved() {
        List<ProcessInstance> list = runtimeService.createProcessInstanceQuery().involvedUser("老板").list();
        System.out.println(list.size());
    }
}

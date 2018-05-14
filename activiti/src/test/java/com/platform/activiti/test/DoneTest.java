package com.platform.activiti.test;

import com.platform.activiti.BaseTest;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.task.Task;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by enmonster on 2018/2/9.
 */
public class DoneTest extends BaseTest {

    @Test
    public void testComplete() {
        Task task = taskService.createTaskQuery().taskAssignee("老板").singleResult();
        taskService.complete(task.getId());
    }

    @Test
    public void testCompleteWithArgs() {
        List<Task> tasks = taskService.createTaskQuery().taskCandidateGroup("management").list();
        Map<String, Object> args = new HashMap<>();
        args.put("key", "value");
        for (Task task : tasks) {
            taskService.complete(task.getId(), args);
        }
    }

    @Test
    public void testOver() {
        HistoricProcessInstanceQuery finished = historyService.createHistoricProcessInstanceQuery().finished();
        System.out.println(finished.count());//查看结束实例的数量
    }

}

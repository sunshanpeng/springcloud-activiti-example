package com.platform.activiti.test;

import com.platform.activiti.BaseTest;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.NativeExecutionQuery;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunshanpeng on 2018/2/12.
 */
public class NativeTaskTest extends BaseTest {

    @Test
    public void testTaskQuery() {
        List<Task> taskList = taskService.createNativeTaskQuery()
                .sql("SELECT * FROM " +
                        managementService.getTableName(Task.class) +
                        " T WHERE T.NAME_ = #{taskName}").parameter("taskName", "部门领导审批").list();
        System.out.println(taskList.size());
    }

    @Test
    public void testFilterQuery() {
        List<Task> taskList = taskService.createNativeTaskQuery()
                .sql("SELECT * FROM " +
                        managementService.getTableName(Task.class) +
                        " T WHERE T.NAME_ like #{taskName}").parameter("taskName", "%部门领导%").list();
        System.out.println(taskList.size());
    }

    @Test
    public void testAssigneeQuery() {
        NativeExecutionQuery nativeExecutionQuery = runtimeService.createNativeExecutionQuery();
        // native query
        String sql = "select RES.* from ACT_RU_EXECUTION RES left join ACT_HI_TASKINST ART on ART.PROC_INST_ID_ = RES.PROC_INST_ID_ "
                + " where ART.ASSIGNEE_ = #{userId} and ACT_ID_ is not null and IS_ACTIVE_ = 1 order by START_TIME_ desc";
        nativeExecutionQuery.parameter("userId", "a");

        List<Execution> executionList = nativeExecutionQuery.sql(sql).listPage(0, 10);
        System.out.println(executionList.size());
    }

    @Test
    public void test() {
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId("myProcess:1:8").singleResult();

    }

    @Test
    public void testGetCompletedNodes() {
        List<String> finishedActiveActivityIds = new ArrayList<>(); // 已完成的节点
        List<HistoricProcessInstance> list = historyService.createHistoricProcessInstanceQuery()
                .processInstanceBusinessKey("1102").list();
        if (!CollectionUtils.isEmpty(list)) {
            List<HistoricActivityInstance> historicActivityInstances
                    = historyService.createHistoricActivityInstanceQuery()
                    .processInstanceId(list.get(0).getId()).finished().list();
            for (HistoricActivityInstance hai : historicActivityInstances) {
                finishedActiveActivityIds.add(hai.getActivityId());
            }
            List<String> activeActivityIds = runtimeService.getActiveActivityIds(list.get(0).getId());
            finishedActiveActivityIds.addAll(activeActivityIds);
        }
    }
}

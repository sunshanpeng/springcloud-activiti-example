package com.platform.business.client.hystrix;

import com.platform.business.client.ActivitiClient;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author 孙善鹏
 * @Date 2018/2/28
 * @Time 12:00
 */
@Component
public class ActivitiClientFailBackFactory implements FallbackFactory<ActivitiClient> {

    @Override
    public ActivitiClient create(Throwable throwable) {
        return new ActivitiClient() {
            @Override
            public List getProcessDefinitionList() {
                return null;
            }

            @Override
            public void start(String processDefinitionId, String userId, String businessKey) {

            }

            @Override
            public List getPrivateTodoList(String userId) {
                return null;
            }

            @Override
            public List getPublicTodoList(String role) {
                return null;
            }

            @Override
            public List getInvolvedList(String userId) {
                return null;
            }

            @Override
            public void claim(String taskId, String userId) {

            }

            @Override
            public void complete(String taskId) {

            }
        };
    }
}

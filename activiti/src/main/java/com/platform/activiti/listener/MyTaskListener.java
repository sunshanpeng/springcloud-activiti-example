package com.platform.activiti.listener;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.impl.persistence.entity.IdentityLinkEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 *
 * @Author sunshanpeng
 * @Date 2018/5/11
 * @Time 17:31
 */
@Slf4j
@Component("taskListener")
public class MyTaskListener implements TaskListener {

    private static final long serialVersionUID = 5450875669116038923L;
    private ConcurrentHashMap<String, String> identityMap = new ConcurrentHashMap<>();

    @Resource
    private TaskService taskService;

    @Resource
    private ScheduledExecutorService delayExecutorService;

    @Override
    public void notify(DelegateTask delegateTask) {
        log.info("GlobalTaskListener.DelegateTask={}",delegateTask);
        List<IdentityLinkEntity> identityLinks = ((TaskEntity) delegateTask).getIdentityLinks();
        if (CollectionUtils.isEmpty(identityLinks) || identityLinks.size() != 1) {
            return;
        }
        String identityId = identityLinks.get(0).getUserId();
        if (identityId == null) {
            return;
        }
        if (identityId.equals(identityMap.get(delegateTask.getExecutionId()))) {
            delayExecutorService.schedule(() -> taskService.complete(delegateTask.getId()), 1, TimeUnit.SECONDS);
        }
        identityMap.put(delegateTask.getExecutionId(), identityId);

    }
}

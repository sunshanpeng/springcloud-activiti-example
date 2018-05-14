package com.platform.activiti.listener;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

/**
 * @Author sunshanpeng
 * @Date 2018/5/11
 * @Time 17:31
 */
@Slf4j
public class GlobalTaskListener implements TaskListener {

    @Override
    public void notify(DelegateTask delegateTask) {
        log.info("GlobalTaskListener.DelegateTask={}",delegateTask);
    }
}

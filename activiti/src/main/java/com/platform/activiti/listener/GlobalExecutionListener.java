package com.platform.activiti.listener;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;

/**
 * @Author sunshanpeng
 * @Date 2018/5/14
 * @Time 14:49
 */
@Slf4j
public class GlobalExecutionListener implements ExecutionListener {
    @Override
    public void notify(DelegateExecution execution) throws Exception {
        log.info("GlobalExecutionListener.DelegateExecution={}", execution);
    }
}

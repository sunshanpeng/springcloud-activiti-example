package com.platform.activiti.service;

import java.util.List;

/**
 * @Author sunshanpeng
 * @Date 2018/5/11
 * @Time 12:03
 */
public interface UserService {

    String getDeptLeaderByUser(String userId);

    List<String> getHR();

}

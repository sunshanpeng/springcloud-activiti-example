package com.platform.activiti.service.impl;

import com.platform.activiti.service.UserService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author sunshanpeng
 * @Date 2018/5/11
 * @Time 12:03
 */
@Service("userService")
public class UserServiceImpl implements UserService {
    @Override
    public String getDeptLeaderByUser(String userId) {
        return "leader0";
    }

    @Override
    public List<String> getHR() {
        List<String> list = new ArrayList<>();
        list.add("hr0");
        list.add("hr1");
        return list;
    }
}

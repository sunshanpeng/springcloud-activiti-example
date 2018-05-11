package com.platform.activiti.service.impl;

import com.platform.activiti.service.UserService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author 孙善鹏
 * @Date 2018/5/11
 * @Time 12:03
 */
@Service("userService")
public class UserServiceImpl implements UserService {
    @Override
    public String getDeptLeaderByUser(String userId) {
        System.out.println("getDeptLeaderByUser===========");
        return "leader";
    }

    @Override
    public List<String> getHR() {
        System.out.println("getHR==========");
        return new ArrayList<String>() {
            @Override
            public boolean add(String s) {
                super.add("hr0");
                return super.add("hr1");
            }
        };
    }
}

# Spring cloud 整合 Activiti

## 版本
Spring cloud : Dalston.RELEASE

Spring boot : 1.5.4.RELEASE

Activiti : 5.22.0

## 工程介绍
1、eureka：服务注册发现

2、activiti：activiti服务提供

3、business：activiti服务消费

4、modeler：activiti-modeler

##  使用方法
1、启动eureka，端口8000

2、启动activiti，端口9003
   
 ```  
  activiti需要依赖数据库，
  如果有mysql数据库则修改activiti服务中application.yml的数据库信息，
  没有则删除datasource相关信息，activiti会使用H2内存数据库 
 ```
   

3、启动business，端口9001
    
4、启动modeler，端口8001
  ```  
  modeler不依赖eureka，可以单独使用 
  ```

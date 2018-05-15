package com.platform.activiti.controller;

import com.platform.activiti.dto.ProcessDefinitionDTO;
import com.platform.activiti.dto.ProcessInstanceDTO;
import com.platform.activiti.dto.TaskDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

@Api(description = "Activiti接口", tags = "activiti")
@RestController
@Slf4j
public class ActivitiController {

    @Resource
    private RuntimeService runtimeService;

    @Resource
    private RepositoryService repositoryService;

    /**
     * 获取已部署的流程实例
     *
     * @return
     */
    @ApiOperation("获取已部署的流程实例")
    @GetMapping("/getProcessDefinitionList")
    public List getProcessDefinitionList() {
        List<ProcessDefinition> listPage = repositoryService.createProcessDefinitionQuery().list();
        List<ProcessDefinitionDTO> list = new ArrayList<>();
        if (listPage != null) {
            for (ProcessDefinition processDefinition : listPage) {
                list.add(new ProcessDefinitionDTO(processDefinition));
            }
        }
        return list;
    }

    /**
     * 新建流程实例（开始流程）
     *
     * @param processDefinitionId
     * @param businessKey
     * @param params 业务参数：比如applyUserId
     */
    @ApiOperation("新建流程实例（开始流程）")
    @PostMapping("/start")
    public void start(String processDefinitionId,
                      @RequestParam(required = false) String businessKey,
                      @RequestBody Map<String, Object> params) {
        runtimeService.startProcessInstanceById(processDefinitionId, businessKey, params);
    }


    /**
     * 查看参与的流程实例
     *
     * @param userId
     * @return
     */
    @ApiOperation("查看参与的流程实例")
    @GetMapping("/getInvolvedList")
    public List getInvolvedList(String userId) {
        List<ProcessInstance> processInstances = runtimeService.createProcessInstanceQuery().involvedUser(userId).list();
        List<ProcessInstanceDTO> list = new ArrayList<>();
        if (processInstances != null) {
            for (ProcessInstance processInstance : processInstances) {
                list.add(new ProcessInstanceDTO(processInstance));
            }
        }

        return list;
    }

    /**
     * 部署流程资源
     */
    @ApiOperation("部署流程资源")
    @PostMapping(value = "/deploy")
    public String deploy(@RequestParam MultipartFile file) {

        // 获取上传的文件名
        String fileName = file.getOriginalFilename();

        try {
            InputStream fileInputStream = file.getInputStream();
            String extension = FilenameUtils.getExtension(fileName);

            // zip或者bar类型的文件用ZipInputStream方式部署
            DeploymentBuilder deployment = repositoryService.createDeployment();
            if (extension.equals("zip") || extension.equals("bar")) {
                ZipInputStream zip = new ZipInputStream(fileInputStream);
                deployment.addZipInputStream(zip);
            } else {
                // 其他类型的文件直接部署
                deployment.addInputStream(fileName, fileInputStream);
            }
            deployment.deploy();
        } catch (Exception e) {
            log.error("error on deploy process, because of file input stream", e);
        }

        return "success";
    }

    /**
     * 删除部署的流程，级联删除流程实例
     *
     * 如果关联删除，则会把相关的流程定义->流程实例->execution&task等全部删除
     *
     * @param deploymentId 流程部署（ACT_RE_DEPLOYMENT）ID
     */
    @DeleteMapping(value = "/deleteDeployment")
    public String deleteDeployment(@RequestParam String deploymentId) {
        repositoryService.deleteDeployment(deploymentId, true);
        return "success";
    }
}
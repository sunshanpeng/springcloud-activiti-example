package com.platform.activiti.controller;

import com.platform.activiti.dto.ProcessDefinitionDTO;
import com.platform.activiti.dto.ProcessInstanceDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.io.FilenameUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

@Api(tags = "activiti")
@RestController
@RequestMapping("/activiti")
@Slf4j
public class ActivitiController {

    @Resource
    private RuntimeService runtimeService;

    @Resource
    private RepositoryService repositoryService;

    private static final String RETURN_SUCCESS = "success";

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
     * 读取资源，通过部署ID
     *
     * @param processDefinitionId 流程定义
     * @param resourceType        资源类型(xml|image)
     * @throws Exception
     */
    @ApiOperation("根据processDefinitionId获取资源文件")
    @GetMapping(value = "/read")
    public void loadByDeployment(@RequestParam String processDefinitionId,
                                 @RequestParam String resourceType,
                                 HttpServletResponse response) throws Exception {
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(processDefinitionId).singleResult();
        String resourceName = "";
        if (resourceType.equals("image")) {
            response.setContentType("image/png");
            resourceName = processDefinition.getDiagramResourceName();
        } else if (resourceType.equals("xml")) {
            resourceName = processDefinition.getResourceName();
        }
        InputStream resourceAsStream = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(), resourceName);
        byte[] b = new byte[1024];
        int len = -1;
        while ((len = resourceAsStream.read(b, 0, 1024)) != -1) {
            response.getOutputStream().write(b, 0, len);
        }
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
    public void start(@RequestParam String processDefinitionId,
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
    public List getInvolvedList(@RequestParam String userId) {
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

        return RETURN_SUCCESS;
    }

    /**
     * 删除部署的流程，级联删除流程实例
     *
     * 如果关联删除，则会把相关的流程定义->流程实例->execution&task等全部删除
     *
     * @param deploymentId 流程部署（ACT_RE_DEPLOYMENT）ID
     */
    @ApiOperation("删除部署的流程")
    @DeleteMapping(value = "/deleteDeployment")
    public String deleteDeployment(@RequestParam String deploymentId) {
        repositoryService.deleteDeployment(deploymentId, true);
        return RETURN_SUCCESS;
    }

    /**
     * 挂起、激活流程实例
     */
    @ApiOperation("挂起、激活流程实例")
    @PutMapping(value = "update/{state}/{processInstanceId}")
    public String updateInstanceState(
            @ApiParam(required = true, allowableValues = "active, suspend")
            @PathVariable("state") String state,
            @PathVariable("processInstanceId") String processInstanceId) {
        switch (state) {
            case "active":
                runtimeService.activateProcessInstanceById(processInstanceId);
                break;
            case "suspend":
                runtimeService.suspendProcessInstanceById(processInstanceId);
                break;
            default:
                throw new HttpServerErrorException(HttpStatus.BAD_REQUEST, "state value must be active | suspend");
        }
        return RETURN_SUCCESS;
    }

    /**
     * 挂起、激活流程定义
     */
    @ApiOperation("挂起、激活流程定义")
    @PutMapping(value = "update/{state}/{processDefinitionId}")
    public String updateDefinitionState(
            @ApiParam(required = true, allowableValues = "active, suspend")
            @PathVariable("state") String state,
            @PathVariable("processDefinitionId") String processDefinitionId) {
        switch (state) {
            case "active":
                repositoryService.activateProcessDefinitionById(processDefinitionId);
                break;
            case "suspend":
                repositoryService.suspendProcessDefinitionById(processDefinitionId);
                break;
            default:
                throw new HttpServerErrorException(HttpStatus.BAD_REQUEST, "state value must be active | suspend");
        }
        return RETURN_SUCCESS;
    }

    @ApiOperation("取消流程")
    @PutMapping("/cancel")
    public String cancel(String processInstanceId, String reason) {
        runtimeService.deleteProcessInstance(processInstanceId, reason);
        return RETURN_SUCCESS;
    }
}
package org.king2.check_service_manage.controller;

import org.king2.check_service_manage.annotations.ResponseEncrypt;
import org.king2.check_service_manage.entity.PortCheckServiceFile;
import org.king2.check_service_manage.entity.StartOrStopPortCheckService;
import org.king2.check_service_manage.entity.UpdatePortCheckService;
import org.king2.check_service_manage.service.PortCheckServiceService;
import org.king2.check_service_manage.utils.ValidateParameterUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.constraints.NotBlank;

/**
 * 描述: 服务服务检测监控管理请求接口控制器
 * @author 刘梓江
 * @Date 2021/5/13 15:09
 */
@Validated
@RestController
@RequestMapping("/port")
public class PortCheckServiceManageController {

    @Autowired
    private PortCheckServiceService portCheckServiceService;

    /**
     * 更新端口检测信息
     * @param portCheckServiceFile
     * @return
     */
    @ResponseEncrypt
    @RequestMapping(value = "/check/info",produces="application/json;charset=utf-8")
    public Object updatePortCheckInfo(@NotBlank(message = "decrypt info is empty !") String decryptJsonInfo){
        PortCheckServiceFile portCheckServiceFile = ValidateParameterUtil.validate(decryptJsonInfo, PortCheckServiceFile.class);
        Object updatePortCheckInfo = portCheckServiceService.updatePortCheckInfo(portCheckServiceFile);
        return updatePortCheckInfo;
    }

    /**
     * 查询指定端口检测服务信息
     * @param serviceTagInfo
     * @return
     */
    @ResponseEncrypt
    @RequestMapping(value = "/find/check/info",produces="application/json;charset=utf-8")
    public Object selectPortCheckServiceByTagInfo(@NotBlank(message = "decrypt info is empty !") String decryptJsonInfo){
        Object selectPortCheckServiceByTagInfo = portCheckServiceService.selectPortCheckServiceByTagInfo(decryptJsonInfo);
        return selectPortCheckServiceByTagInfo;
    }

    /**
     * 查询所有端口检测服务信息
     * @return
     */
    @ResponseEncrypt
    @RequestMapping(value = "/find/check/info/list",produces="application/json;charset=utf-8")
    public Object selectPortCheckServiceList(String decryptJsonInfo){
        Object selectPortCheckServiceList = portCheckServiceService.selectPortCheckServiceList(decryptJsonInfo);
        return selectPortCheckServiceList;
    }

    /**
     * 删除指定端口检测服务信息
     * @param serviceTagInfo
     * @return
     */
    @ResponseEncrypt
    @RequestMapping(value = "/remove/check/service",produces="application/json;charset=utf-8")
    public Object removePortCheckServiceByTagInfo(@NotBlank(message = "decrypt info is empty !") String decryptJsonInfo){
        Object removePortCheckServiceByTagInfo = portCheckServiceService.removePortCheckServiceByTagInfo(decryptJsonInfo);
        return removePortCheckServiceByTagInfo;
    }


    /**
     * 启动或关闭服务端口检测功能
     * @param serviceTagInfo
     * @return
     */
    @ResponseEncrypt
    @RequestMapping(value = "/start-or-stop/check/service",produces="application/json;charset=utf-8")
    public Object startOrStopPortCheckService(@NotBlank(message = "decrypt info is empty !") String decryptJsonInfo){
        StartOrStopPortCheckService operationPortCheckService = ValidateParameterUtil.validate(decryptJsonInfo, StartOrStopPortCheckService.class);
        Object startOrStopPortCheckService = portCheckServiceService.startOrStopPortCheckService(operationPortCheckService);
        return startOrStopPortCheckService;
    }

    /**
     *  更新指定端口检测服务信息
     * @return
     */
    @ResponseEncrypt
    @RequestMapping(value = "/update/check/service",produces="application/json;charset=utf-8")
    public Object updatePortCheckService(@NotBlank(message = "decrypt info is empty !") String decryptJsonInfo){
        UpdatePortCheckService operationPortCheckService = ValidateParameterUtil.validate(decryptJsonInfo, UpdatePortCheckService.class);
        Object updatePortCheckService = portCheckServiceService.updatePortCheckService(operationPortCheckService);
        return updatePortCheckService;
    }

}

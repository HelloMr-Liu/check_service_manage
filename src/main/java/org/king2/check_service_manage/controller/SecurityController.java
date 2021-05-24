package org.king2.check_service_manage.controller;

import org.king2.check_service_manage.annotations.ResponseEncrypt;
import org.king2.check_service_manage.entity.SecurityConfigFile;
import org.king2.check_service_manage.service.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 描述:操作安全信息配置请求接口控制器
 * @author 刘梓江
 * @Date 2021/5/21 15:32
 */
@RestController
@RequestMapping("/security")
public class SecurityController {

    @Autowired
    private SecurityService securityService;

    /**
     * 获取：公匙信息
     * @return
     */
    @ResponseEncrypt
    @RequestMapping(value = "/key",produces="application/json;charset=utf-8")
    public Object getSecurityKey(){
        SecurityConfigFile securityKey = (SecurityConfigFile)securityService.getSecurityKey();
        securityKey.setPrivateKey("");
        return securityKey;
    }
}

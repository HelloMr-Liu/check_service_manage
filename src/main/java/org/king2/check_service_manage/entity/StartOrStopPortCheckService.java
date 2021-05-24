package org.king2.check_service_manage.entity;

import javax.validation.constraints.NotBlank;

/**
 * 描述: 操作开启停止端口检测服务
 * @author 刘梓江
 * @Date 2021/5/23 20:15
 */
public class StartOrStopPortCheckService {

    @NotBlank(message = "端口检测服务标识为空")
    private String serviceTagInfo;

    @NotBlank(message = "端口检测服务服务地址为空")
    private String httpCheckServerUrl;

    public String getServiceTagInfo() {
        return serviceTagInfo;
    }

    public void setServiceTagInfo(String serviceTagInfo) {
        this.serviceTagInfo = serviceTagInfo;
    }

    public String getHttpCheckServerUrl() {
        return httpCheckServerUrl;
    }

    public void setHttpCheckServerUrl(String httpCheckServerUrl) {
        this.httpCheckServerUrl = httpCheckServerUrl;
    }
}

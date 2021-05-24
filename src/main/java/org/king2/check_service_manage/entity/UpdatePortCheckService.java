package org.king2.check_service_manage.entity;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * 描述: 更新端口检测服务信息
 * @author 刘梓江
 * @Date 2021/5/14 15:19
 */
public class UpdatePortCheckService {

    /**
     * 本机描述
     */
    private String hostDesc;

    /**
     * 本地标志
     */
    @Pattern(regexp = "^1|^2$", message = "本地标志参数异常 1:本机  2:远程 ")
    private String isLocal="1";

    /**
     * 更新类型
     */
    @NotBlank(message = "更新类型为空")
    @Pattern(regexp = "^1|^2|^3$", message = "更新类型参数异常 1:新增  2:删除 3:启动检测服务或暂检测服务")
    private String updateType;

    /**
     * 本机访问端口
     */
    @NotBlank(message = "本机访问端口为空")
    private String hostPort;

    /**
     * 本机访问地址
     */
    @NotBlank(message = "本机访问地址为空")
    private String hostAddress;

    /**
     * 服务标识(外网加内网)信息
     */
    @NotBlank(message = "服务检测标识为空")
    private String serviceTagInfo;

    /**
     * 定义当前请求服务地址
     */
    private String httpCheckServerUrl;

    public String getHttpCheckServerUrl() {
        return httpCheckServerUrl;
    }

    public void setHttpCheckServerUrl(String httpCheckServerUrl) {
        this.httpCheckServerUrl = httpCheckServerUrl;
    }

    public String getServiceTagInfo() {
        return serviceTagInfo;
    }

    public void setServiceTagInfo(String serviceTagInfo) {
        this.serviceTagInfo = serviceTagInfo;
    }

    public String getHostDesc() {
        return hostDesc;
    }

    public void setHostDesc(String hostDesc) {
        this.hostDesc = hostDesc;
    }

    public String getUpdateType() {
        return updateType;
    }

    public void setUpdateType(String updateType) {
        this.updateType = updateType;
    }

    public String getIsLocal() {
        return isLocal;
    }

    public void setIsLocal(String isLocal) {
        this.isLocal = isLocal;
    }

    public String getHostPort() {
        return hostPort;
    }

    public void setHostPort(String hostPort) {
        this.hostPort = hostPort;
    }

    public String getHostAddress() {
        return hostAddress;
    }

    public void setHostAddress(String hostAddress) {
        this.hostAddress = hostAddress;
    }
}

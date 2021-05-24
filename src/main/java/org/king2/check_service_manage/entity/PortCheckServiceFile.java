package org.king2.check_service_manage.entity;

import com.alibaba.fastjson.annotation.JSONField;

import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.List;

/**
 * 描述:端口检测服务信息文件
 * @author 刘梓江
 * @Date 2021/5/11 14:56
 */
public class PortCheckServiceFile {

    /**
     * 定义当前请求服务地址
     */
    private String httpCheckServerUrl;

    /**
     * 外网IP
     */
    @NotBlank(message = "外网IP为空")
    private String internetIp;

    /**
     * 本机IP
     */
    @NotBlank(message = "内网IP为空")
    private String intranetIp;

    /**
     * 是否开启端口ip检测
     */
    private Boolean checkServiceStart;

    /**
     * 本机检测服务描述
     */
    private String checkServiceDesc;

    /**
     * 最新一次文件访问时间
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date checkServiceFileAccessTime;

    /**
     * 判断当前服务是否正常
     */
    private Boolean checkServiceWhetherNormal;

    /**
     *本机主机端口检测
     */
    private List<PortCheckServiceResultState> localPortCheck;

    /**
     * 远程主机端口检测
     */
    private List<PortCheckServiceResultState> remotePortCheck;

    public String getHttpCheckServerUrl() {
        return httpCheckServerUrl;
    }

    public void setHttpCheckServerUrl(String httpCheckServerUrl) {
        this.httpCheckServerUrl = httpCheckServerUrl;
    }

    public List<PortCheckServiceResultState> getLocalPortCheck() {
        return localPortCheck;
    }

    public void setLocalPortCheck(List<PortCheckServiceResultState> localPortCheck) {
        this.localPortCheck = localPortCheck;
    }

    public List<PortCheckServiceResultState> getRemotePortCheck() {
        return remotePortCheck;
    }

    public void setRemotePortCheck(List<PortCheckServiceResultState> remotePortCheck) {
        this.remotePortCheck = remotePortCheck;
    }

    public Boolean getCheckServiceStart() {
        return checkServiceStart;
    }

    public void setCheckServiceStart(Boolean checkServiceStart) {
        this.checkServiceStart = checkServiceStart;
    }

    public Date getCheckServiceFileAccessTime() {
        return checkServiceFileAccessTime;
    }

    public void setCheckServiceFileAccessTime(Date checkServiceFileAccessTime) {
        this.checkServiceFileAccessTime = checkServiceFileAccessTime;
    }

    public Boolean getCheckServiceWhetherNormal() {
        return checkServiceWhetherNormal;
    }

    public void setCheckServiceWhetherNormal(Boolean checkServiceWhetherNormal) {
        this.checkServiceWhetherNormal = checkServiceWhetherNormal;
    }

    public String getInternetIp() {
        return internetIp;
    }

    public void setInternetIp(String internetIp) {
        this.internetIp = internetIp;
    }

    public String getIntranetIp() {
        return intranetIp;
    }

    public void setIntranetIp(String intranetIp) {
        this.intranetIp = intranetIp;
    }

    public String getCheckServiceDesc() {
        return checkServiceDesc;
    }

    public void setCheckServiceDesc(String checkServiceDesc) {
        this.checkServiceDesc = checkServiceDesc;
    }
}

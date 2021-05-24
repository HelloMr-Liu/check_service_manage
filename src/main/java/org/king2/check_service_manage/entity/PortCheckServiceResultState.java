package org.king2.check_service_manage.entity;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

/**
 * 描述: 端口检测服务结果状态类
 * @author 刘梓江
 * @Date 2021/5/11 15:34
 */
public class PortCheckServiceResultState {

    /**
     * 状态 true 正常 false 非正常
     */
    private Boolean normal;

    /**
     * 目标信息描述
     */
    private String targetDesc;

    /**
     *操作目标(主机)
     */
    private String operationTargetHost;

    /**
     * 操作目标(端口)
     */
    private String operationTargetPort;

    /**
     * 检测时间
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date checkTime;

    public PortCheckServiceResultState(String operationTargetHost, String operationTargetPort, String targetDesc) {
        this.operationTargetHost = operationTargetHost;
        this.operationTargetPort = operationTargetPort;
        this.targetDesc = targetDesc;
        this.normal=false;
    }

    public PortCheckServiceResultState() {
    }

    public String getOperationTargetHost() {
        return operationTargetHost;
    }

    public void setOperationTargetHost(String operationTargetHost) {
        this.operationTargetHost = operationTargetHost;
    }

    public String getOperationTargetPort() {
        return operationTargetPort;
    }

    public void setOperationTargetPort(String operationTargetPort) {
        this.operationTargetPort = operationTargetPort;
    }

    public Boolean getNormal() {
        return normal;
    }

    public void setNormal(Boolean normal) {
        this.normal = normal;
    }

    public String getTargetDesc() {
        return targetDesc;
    }

    public void setTargetDesc(String targetDesc) {
        this.targetDesc = targetDesc;
    }


    public Date getCheckTime() {
        return checkTime;
    }

    public void setCheckTime(Date checkTime) {
        this.checkTime = checkTime;
    }
}

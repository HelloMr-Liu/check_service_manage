package org.king2.check_service_manage.service;

import org.king2.check_service_manage.entity.PortCheckServiceFile;
import org.king2.check_service_manage.entity.StartOrStopPortCheckService;
import org.king2.check_service_manage.entity.UpdatePortCheckService;

/**
 * 描述: 端口检测服务业务接口
 * @author 刘梓江
 * @Date 2021/5/13 14:55
 */
public interface PortCheckServiceService {


    /**
     * 更新端口检测信息
     * @param portCheckServiceFile
     * @return
     */
    public Object updatePortCheckInfo(PortCheckServiceFile portCheckServiceFile);

    /**
     * 查询指定端口检测服务信息
     * @param serviceTagInfo
     * @return
     */
    public Object selectPortCheckServiceByTagInfo(String serviceTagInfo);

    /**
     * 查询所有端口检测服务信息
     * @return
     */
    public Object selectPortCheckServiceList(String searchContent);

    /**
     * 删除指定端口检测服务信息
     * @param serviceTagInfo
     * @return
     */
    public Object removePortCheckServiceByTagInfo(String serviceTagInfo);


    /**
     * 启动或关闭服务端口检测功能
     * @param startOrStopPortCheckService
     * @return
     */
    public Object startOrStopPortCheckService(StartOrStopPortCheckService startOrStopPortCheckService);

    /**
     *  更新指定端口检测服务信息
     * @return
     */
    public Object updatePortCheckService(UpdatePortCheckService updateServicePortInfo);
}

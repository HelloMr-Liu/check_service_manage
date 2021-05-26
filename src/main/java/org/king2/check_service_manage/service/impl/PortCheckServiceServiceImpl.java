package org.king2.check_service_manage.service.impl;

import com.alibaba.fastjson.JSON;
import org.king2.check_service_manage.config.SystemFilterConfiguration;
import org.king2.check_service_manage.constant.RedisKeyEnum;
import org.king2.check_service_manage.constant.SystemResultCodeEnum;
import org.king2.check_service_manage.entity.*;
import org.king2.check_service_manage.service.SecurityService;
import org.king2.check_service_manage.service.PortCheckServiceService;
import org.king2.check_service_manage.utils.AESUtil;
import org.king2.check_service_manage.utils.ApplicationUtil;
import org.king2.check_service_manage.utils.RSAUtil;
import org.king2.check_service_manage.utils.RedisUtil;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 描述:端口检测服务业务接口实现
 * @author 刘梓江
 * @Date 2021/5/13 14:58
 */
@Service
public class PortCheckServiceServiceImpl implements PortCheckServiceService {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private SecurityService securityService;

    private static final Logger logger= LoggerFactory.getLogger(PortCheckServiceServiceImpl.class);

    @Override
    public Object updatePortCheckInfo(PortCheckServiceFile portCheckServiceFile) {
        String tagInfo=portCheckServiceFile.getInternetIp()+"_"+portCheckServiceFile.getIntranetIp();
        redisUtil.hset(RedisKeyEnum.SERVICE_LIFE_CHECK.name(),tagInfo,JSON.toJSONString(portCheckServiceFile));
        return SystemResultVo.ok(" update success ");
    }

    @Override
    public Object selectPortCheckServiceByTagInfo(String serviceTagInfo) {
        Object hget = redisUtil.hget(RedisKeyEnum.SERVICE_LIFE_CHECK.name(), serviceTagInfo);
        if(hget==null)return SystemResultVo.build(SystemResultCodeEnum.SELECT_FAILURE.STATE," port service no exist ");
        PortCheckServiceFile servicePortCheckInfo = JSON.parseObject(hget.toString(), PortCheckServiceFile.class);
        return SystemResultVo.ok(servicePortCheckInfo);
    }

    @Override
    public Object selectPortCheckServiceList(String searchContent) {
        List<PortCheckServiceFile> servicePortCheckInfos=new ArrayList<>();
        //获取缓存中对应的服务端口检测信息
        Map<Object, Object> updateDevicePriceMap = redisUtil.hmget(RedisKeyEnum.SERVICE_LIFE_CHECK.name());
        if(!CollectionUtils.isEmpty(updateDevicePriceMap)){
            updateDevicePriceMap.forEach(
                    (key,value)->{
                        //遍历转换封装服务端口检测信息
                        PortCheckServiceFile servicePortCheckInfo = JSON.parseObject(value.toString(), PortCheckServiceFile.class);
                        if(StringUtils.isEmpty(searchContent)||servicePortCheckInfo.getInternetIp().contains(searchContent)||servicePortCheckInfo.getIntranetIp().contains(searchContent)){
                            servicePortCheckInfos.add(servicePortCheckInfo);
                        }
                    }
            );
        }
        List<PortCheckServiceFile> sortServicePortCheckInfo=servicePortCheckInfos.stream()
            .sorted(
                    (e1,e2)->{
                        return e1.getIntranetIp().hashCode()-e2.getIntranetIp().hashCode();
                    }
            ).sorted(
                    (e1,e2)->{
                        return e1.getCheckServiceWhetherNormal().hashCode()-e2.getCheckServiceWhetherNormal().hashCode();
                    }
            )
            .collect(Collectors.toList());
        return SystemResultVo.ok(sortServicePortCheckInfo);
    }

    @Override
    public Object removePortCheckServiceByTagInfo(String serviceTagInfo) {
        redisUtil.hdel(RedisKeyEnum.SERVICE_LIFE_CHECK.name(),serviceTagInfo);
        return SystemResultVo.ok("remove success");
    }

    @Override
    public Object startOrStopPortCheckService(StartOrStopPortCheckService startOrStopPortCheckService) {
        UpdatePortCheckService updateServicePortInfo=new UpdatePortCheckService();
        updateServicePortInfo.setUpdateType("3");
        updateServicePortInfo.setServiceTagInfo(startOrStopPortCheckService.getServiceTagInfo());
        updateServicePortInfo.setHttpCheckServerUrl(startOrStopPortCheckService.getHttpCheckServerUrl());
        return updatePortCheckService(updateServicePortInfo);
    }

    @Override
    public Object updatePortCheckService(UpdatePortCheckService updateServicePortInfo) {
        try{
            //redis分布式锁(开锁)
            redissonClient.getReadWriteLock(updateServicePortInfo.getServiceTagInfo()).writeLock().lock();
            //在缓存中的该服务信息
            Object cacheServicePortCheckInfo = redisUtil.hget(RedisKeyEnum.SERVICE_LIFE_CHECK.name(), updateServicePortInfo.getServiceTagInfo());
            if(cacheServicePortCheckInfo!=null){
                PortCheckServiceFile servicePortCheckInfo=JSON.parseObject(cacheServicePortCheckInfo.toString(), PortCheckServiceFile.class);

                //新增操作
                if(updateServicePortInfo.getUpdateType().equals("1")){

                    List<PortCheckServiceResultState> updateInfoList=null;
                    if(updateServicePortInfo.getIsLocal().equals("1")){
                        //新增本地主机端口
                        if(CollectionUtils.isEmpty(servicePortCheckInfo.getLocalPortCheck()))servicePortCheckInfo.setLocalPortCheck(new ArrayList<>());
                        updateInfoList=servicePortCheckInfo.getLocalPortCheck();

                    }else{
                        //新增远程主机端口
                        if(CollectionUtils.isEmpty(servicePortCheckInfo.getRemotePortCheck()))servicePortCheckInfo.setRemotePortCheck(new ArrayList<>());
                        updateInfoList=servicePortCheckInfo.getRemotePortCheck();
                    }
                    updateInfoList.add(new PortCheckServiceResultState(updateServicePortInfo.getHostAddress(),updateServicePortInfo.getHostPort(),updateServicePortInfo.getHostDesc()));

                    //移除操作
                }else if(updateServicePortInfo.getUpdateType().equals("2")){
                    List<PortCheckServiceResultState> removeInfoList=new ArrayList<>();
                    if(updateServicePortInfo.getIsLocal().equals("1")){
                        removeInfoList=servicePortCheckInfo.getLocalPortCheck();
                    }else{
                        removeInfoList=servicePortCheckInfo.getRemotePortCheck();
                    }
                    //移除指定主机端口
                    int removeIndex=-1;
                    for(int index=0;index<removeInfoList.size();index++){
                        PortCheckServiceResultState servicePortCheckState = removeInfoList.get(index);
                        //获取到指定待删除匹配的端口信息
                        if(servicePortCheckState.getOperationTargetPort().equals(updateServicePortInfo.getHostPort())&&
                                servicePortCheckState.getOperationTargetHost().equals(updateServicePortInfo.getHostAddress())){
                            removeIndex=index;
                            break;
                        }
                    }
                    if(removeIndex!=-1)removeInfoList.remove(removeIndex);

                    //启动操作
                }else{
                    servicePortCheckInfo.setHttpCheckServerUrl(updateServicePortInfo.getHttpCheckServerUrl());
                    servicePortCheckInfo.setCheckServiceStart(!servicePortCheckInfo.getCheckServiceStart());
                }

                SecurityConfigFile localSecurityConfig = (SecurityConfigFile)securityService.getSecurityKey();
                //获取对应的安全钥匙信息
                String reponseInfo1 = ApplicationUtil.sendPost("http://"+servicePortCheckInfo.getHttpCheckServerUrl()+"/port_check_service/security/key",null);
                if(!StringUtils.isEmpty(reponseInfo1)){
                    //进行数据解密
                    logger.info("远程检测服务获取安全钥匙信息-响应结果："+reponseInfo1);
                    SecurityConfigFile remoteSecurityConfig = JSON.parseObject(reponseInfo1, SecurityConfigFile.class);

                    //生成一个AES密匙信息,并对明文数据进行加密操作
                    String aesEncryptKey= ApplicationUtil.getStringRandom(16);
                    String encryptJsonInfo= AESUtil.encrypt(JSON.toJSONString(servicePortCheckInfo),aesEncryptKey,aesEncryptKey);

                    //并使用RSA进行对AES密匙加密
                    String encryptAesEncryptKey= RSAUtil.encryptByPublicKey(aesEncryptKey,remoteSecurityConfig.getPublicKey());

                    String reponseInfo2=ApplicationUtil.sendPost("http://"+servicePortCheckInfo.getHttpCheckServerUrl()+"/port_check_service/port/check/service/update/file?encryptJsonInfo="+encryptJsonInfo,encryptAesEncryptKey);
                    //进行数据解密
                    reponseInfo2=reponseInfo2.substring(1,reponseInfo2.length()-1);
                    reponseInfo2=AESUtil.decrypt(reponseInfo2,aesEncryptKey,aesEncryptKey);
                    logger.info("远程检测服务发送检测信息-响应结果："+reponseInfo2);
                }
            }
            return SystemResultVo.ok(" operation ok  wait service refresh ");
        }catch (Exception e){
            logger.warn("更新服务端口配置信息失败!");
            throw new RuntimeException(ApplicationUtil.getExceptionMessage(e));
        }finally {
            if(redissonClient.getReadWriteLock(updateServicePortInfo.getServiceTagInfo()).writeLock().getHoldCount()!=0){
                redissonClient.getReadWriteLock(updateServicePortInfo.getServiceTagInfo()).writeLock().unlock();
            };
        }
    }
}

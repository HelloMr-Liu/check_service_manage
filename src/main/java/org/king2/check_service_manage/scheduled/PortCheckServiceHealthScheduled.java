package org.king2.check_service_manage.scheduled;
import com.alibaba.fastjson.JSON;
import org.king2.check_service_manage.constant.RedisKeyEnum;
import org.king2.check_service_manage.entity.PortCheckServiceFile;
import org.king2.check_service_manage.utils.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 描述:定时端口检测服务健康检测
 * @author 刘梓江
 * @Date 2021/5/11 14:49
 */
@Component
public class PortCheckServiceHealthScheduled {

    private static  final Logger logger= LoggerFactory.getLogger(PortCheckServiceHealthScheduled.class);

    @Autowired
    private RedisUtil redisUtil;

    /**每隔15检测一次**/
    @Scheduled(cron = "*/15 * * * * ?")
    public void servicePortCheck(){
        //logger.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())+" 开启了检查(主机检测端口服务)连通状态操作！");
        AtomicInteger failureServiceNumber= new AtomicInteger();
        //获取缓存中对应的服务检测端口信息
        Map<Object, Object> updateDevicePriceMap = redisUtil.hmget(RedisKeyEnum.SERVICE_LIFE_CHECK.name());
        if(!CollectionUtils.isEmpty(updateDevicePriceMap)){
            updateDevicePriceMap.forEach(
                (key,value)->{
                    //遍历转换封装服务检测端口信息
                    PortCheckServiceFile servicePortCheckFile = JSON.parseObject(value.toString(), PortCheckServiceFile.class);
                    if(servicePortCheckFile.getCheckServiceWhetherNormal()&&new Date().getTime()-servicePortCheckFile.getCheckServiceFileAccessTime().getTime()>50000){
                        //代表当前检测端口服务有问题将状态改成不正常(false)
                        servicePortCheckFile.setCheckServiceWhetherNormal(false);
                        failureServiceNumber.getAndIncrement();
                        //并持久化到缓存中
                        redisUtil.hset(RedisKeyEnum.SERVICE_LIFE_CHECK.name(),servicePortCheckFile.getInternetIp()+"_"+servicePortCheckFile.getIntranetIp(),JSON.toJSONString(servicePortCheckFile));
                    }
                }
            );
        }
        if(failureServiceNumber.intValue()>0){
            logger.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())+"检测出"+failureServiceNumber.intValue()+"个,非正常检测端口服务");
        }
    }

}

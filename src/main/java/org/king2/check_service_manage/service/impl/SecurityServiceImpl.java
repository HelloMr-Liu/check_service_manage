package org.king2.check_service_manage.service.impl;

import com.alibaba.fastjson.JSON;
import org.king2.check_service_manage.config.SecurityKeyFileConfiguration;
import org.king2.check_service_manage.entity.SecurityConfigFile;
import org.king2.check_service_manage.service.SecurityService;
import org.king2.check_service_manage.utils.ApplicationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * 描述:操作安全信息业务接口实现
 * @author 刘梓江
 * @Date 2021/5/21 14:00
 */
@Service
public class SecurityServiceImpl implements SecurityService {

    private static final Logger logger= LoggerFactory.getLogger(SecurityServiceImpl.class);

    @Override
    public Object getSecurityKey() {
        SecurityConfigFile securityConfig=new SecurityConfigFile();
        BufferedReader reader = null;
        //获取安全配置文件信息内容
        File securityKeyConfigFile =new File(new ApplicationHome(getClass()).getSource().getParentFile().getPath()+"//"+ SecurityKeyFileConfiguration.securityKeyConfigFileName);
        if(securityKeyConfigFile.exists()){
            try{
                StringBuilder readContent = new StringBuilder();

                //读取文本内容信息
                reader = new BufferedReader(new FileReader(securityKeyConfigFile));
                String tempStr;
                while ((tempStr = reader.readLine()) != null) {
                    readContent.append(tempStr);
                }
                reader.close();

                if(!StringUtils.isEmpty(readContent.toString())){
                    securityConfig = JSON.parseObject(readContent.toString(), SecurityConfigFile.class);
                }
            }catch (Exception e){
                logger.warn("读取操作安全配置文件信息失败！");
                logger.warn(ApplicationUtil.getExceptionMessage(e));
            }finally {
                try {
                    reader.close();
                } catch (IOException e) {
                    logger.warn("高效输入流关闭失败！");
                    logger.warn(ApplicationUtil.getExceptionMessage(e));
                }
            }
        }
        return securityConfig;
    }
}

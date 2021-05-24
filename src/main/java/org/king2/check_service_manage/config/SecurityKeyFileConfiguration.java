package org.king2.check_service_manage.config;

import com.alibaba.fastjson.JSON;
import org.king2.check_service_manage.entity.SecurityConfigFile;
import org.king2.check_service_manage.utils.ApplicationUtil;
import org.king2.check_service_manage.utils.RSAUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Map;

/**
 * 描述:安全信息配置
 * @author 刘梓江
 * @Date 2021/5/21 13:13
 */
@Configuration
public class SecurityKeyFileConfiguration {

    private static final Logger logger= LoggerFactory.getLogger(SecurityKeyFileConfiguration.class);

    /**安全钥匙配置文件名称**/
    public static String securityKeyConfigFileName="securityKeyConfigFile.txt";

    /**
     * 初始化安全钥匙配置文件信息
     */
    @PostConstruct
    public void initSecurityKeyConfigFile()  {
        FileWriter fw=null;
        BufferedWriter bw=null;
        try{
            File securityKeyConfigFile =new File(new ApplicationHome(getClass()).getSource().getParentFile().getPath()+"//"+securityKeyConfigFileName);
            if(!securityKeyConfigFile.exists()){
                Map<String, Object> securityKey = RSAUtil.genKeyPair();
                String publicKey= RSAUtil.getPublicKey(securityKey);
                String privateKey=  RSAUtil.getPrivateKey(securityKey);
                SecurityConfigFile securityConfig=new SecurityConfigFile(publicKey,privateKey);
                fw = new FileWriter(securityKeyConfigFile.getPath());
                bw = new BufferedWriter(fw);
                bw.write(JSON.toJSONString(securityConfig,true));
                bw.flush();
            }
        }catch (Exception e){
            logger.warn("安全钥匙配置文件配置异常！");
            logger.warn(ApplicationUtil.getExceptionMessage(e));
        }finally {
            if(fw!=null){
                try{
                    fw.close();
                }catch (Exception e){
                    logger.warn("文件输出流关闭失败！");
                }
            }
            if(bw!=null){
                try{
                    bw.close();
                }catch (Exception e){
                    logger.warn("字符输出流关闭失败！");
                }
            }
        }
    }
}

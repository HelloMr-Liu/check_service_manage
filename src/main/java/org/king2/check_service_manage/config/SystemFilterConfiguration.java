package org.king2.check_service_manage.config;

import com.alibaba.fastjson.JSON;
import org.king2.check_service_manage.entity.SecurityConfigFile;
import org.king2.check_service_manage.entity.SystemResultVo;
import org.king2.check_service_manage.service.SecurityService;
import org.king2.check_service_manage.utils.AESUtil;
import org.king2.check_service_manage.utils.ApplicationUtil;
import org.king2.check_service_manage.utils.RSAUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 描述:系统访问过滤器配置
 * @author 刘梓江
 * @Date 2021/3/23 15:45
 */
@Configuration
public class SystemFilterConfiguration implements Filter {

    //存储本次请求线程请求信息
    public static final ThreadLocal<HttpServletRequest> contentRequest = new ThreadLocal<>();
    //存储本次请求线程响应信息
    public static final ThreadLocal<HttpServletResponse> contentResponse = new ThreadLocal<>();
    //存储本次请求AES加密钥匙
    public static final ThreadLocal<String>  requestAesEncryptKey= new ThreadLocal<>();

    @Autowired
    private SecurityService securityService;

    private static final Logger logger= LoggerFactory.getLogger(SystemFilterConfiguration.class);

    @Override
    public void init(FilterConfig filterConfig)  {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException   {
        MyHttpServletRequestConfiguration myHttpServletRequest=new MyHttpServletRequestConfiguration((HttpServletRequest) request);
        contentRequest.set(myHttpServletRequest);
        contentResponse.set((HttpServletResponse) response);

        try {
            //获取当前请求接口路径
            String currentRequestUrl = myHttpServletRequest.getServletPath();
            if(!currentRequestUrl.contains("/security/key")){

                SecurityConfigFile securityConfigFile = (SecurityConfigFile)securityService.getSecurityKey();

                //获取当前请求方式AES密钥(并使用RSA私钥解密)
                String encryptAesEncryptKey = myHttpServletRequest.getHeader("encryptAesEncryptKey");
                if(!StringUtils.isEmpty(encryptAesEncryptKey)){

                    //使用RSA 解密AES加密密匙
                    encryptAesEncryptKey=RSAUtil.decryptByPrivateKey(encryptAesEncryptKey,securityConfigFile.getPrivateKey());

                    //将请求AES密匙存储到本次请求上
                    requestAesEncryptKey.set(encryptAesEncryptKey);

                    //获取当前请求参数加密json信息
                    String encryptJsonInfo = myHttpServletRequest.getParameter("encryptJsonInfo");
                    if(!StringUtils.isEmpty(encryptJsonInfo)){

                        //补充加密信息(由于浏览器传递的+号会默认转换成空格,所以将+好补充回来)
                        encryptJsonInfo=encryptJsonInfo.replaceAll(" ", "+");

                        //将当前加密信息进行解密操作并修改添加到请求参数中
                        String decryptJsonInfo = AESUtil.decrypt(encryptJsonInfo,encryptAesEncryptKey,encryptAesEncryptKey);
                        //封装新参数
                        Map<String, String[]> parameterMap =new HashMap<>(myHttpServletRequest.getParameterMap());
                        parameterMap.put("decryptJsonInfo",new String[]{decryptJsonInfo});
                        logger.info("AES解密接收数据："+decryptJsonInfo);
                        myHttpServletRequest.setParameterMap(parameterMap);
                    }
                    filterChain.doFilter(myHttpServletRequest, response);
                }
            }else{
                filterChain.doFilter(myHttpServletRequest, response);
            }
        } catch (Exception e) {
            try {
                logger.warn(ApplicationUtil.getExceptionMessage(e));
                String data = "\""+( AESUtil.encrypt(JSON.toJSONString(SystemResultVo.error("系统错误！请重新请求试试!!!")),requestAesEncryptKey.get(),requestAesEncryptKey.get()))+"\"";
                OutputStream outputStream= response.getOutputStream();//获取OutputStream输出流
                contentResponse.get().setHeader("content-type", "text/html;charset=UTF-8");//通过设置响应头控制浏览器以UTF-8的编码显示数据，如果不加这句话，那么浏览器显示的将是乱码
                byte[] dataByteArr = data.getBytes("UTF-8");//将字符转换成字节数组，指定以UTF-8编码进行转换
                outputStream.write(dataByteArr);//使用OutputStream流向客户端输出字节数组
                outputStream.flush();
            } catch (Exception ex) {
                logger.warn(ApplicationUtil.getExceptionMessage(ex));
            }
        }finally {
            contentRequest.remove();
            contentResponse.remove();
            requestAesEncryptKey.remove();
        }
    }

    @Override
    public void destroy() {

    }
}


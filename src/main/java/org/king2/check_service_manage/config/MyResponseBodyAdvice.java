package org.king2.check_service_manage.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.king2.check_service_manage.annotations.ResponseEncrypt;
import org.king2.check_service_manage.entity.PortCheckServiceResultState;
import org.king2.check_service_manage.utils.AESUtil;
import org.king2.check_service_manage.utils.ApplicationUtil;
import org.king2.check_service_manage.utils.RSAUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 描述: 二次对响应数据信息操作
 * @author 刘梓江
 * @Date 2021/5/23 21:53
 */
@ControllerAdvice(basePackages = "org.king2.check_service_manage.controller")
public class MyResponseBodyAdvice implements ResponseBodyAdvice {

    private final static Logger logger = LoggerFactory.getLogger(MyResponseBodyAdvice.class);

    @Override
    public boolean supports(MethodParameter methodParameter, Class aClass) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter methodParameter, MediaType mediaType, Class aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse)  {
        try {
            if (methodParameter.getMethod().isAnnotationPresent(ResponseEncrypt.class)) {

                //进行序列化操作配置
                SerializeConfig serializeConfig = SerializeConfig.globalInstance;
                serializeConfig.put(Long.class , ToStringSerializer.instance);
                serializeConfig.put(Long.TYPE , ToStringSerializer.instance);
                List<SerializerFeature> serializerFeatures=new ArrayList<>();
                serializerFeatures.add(SerializerFeature.WriteDateUseDateFormat);
                serializerFeatures.add(SerializerFeature.WriteNullListAsEmpty);             // 将Collection类型字段的字段空值输出为[]
                serializerFeatures.add(SerializerFeature.WriteNullStringAsEmpty);           // 将字符串类型字段的空值输出为空字符串
                serializerFeatures.add(SerializerFeature.DisableCircularReferenceDetect);   // 禁用循环引用
                serializerFeatures.add(SerializerFeature.WriteNullNumberAsZero);            // 将数值类型字段的空值输出为0
                serializerFeatures.add(SerializerFeature.WriteMapNullValue);                // 是否输出值为null的字段,默认为false,我们将它打开
                SerializerFeature[] SerializerFeatureArray=serializerFeatures.toArray(new SerializerFeature[0]);

                //进行序列化数据操作
                String result = JSON.toJSONString(body,serializeConfig,SerializerFeatureArray);

                //进行数据加密操作
                String aesEncryptKey = SystemFilterConfiguration.requestAesEncryptKey.get();
                body = AESUtil.encrypt(result,aesEncryptKey,aesEncryptKey);
                logger.info("AES加密后响应数据："+body);
            }
            return body;
        } catch (Exception e) {
            logger.warn("对方法method :【" + methodParameter.getMethod().getName() + "】返回数据进行加密出现异常："+e.getMessage());
            logger.warn(ApplicationUtil.getExceptionMessage(e));
            throw new RuntimeException(e);
        }
    }

}

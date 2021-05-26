package org.king2.check_service_manage.config;


import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述：二次修改web相关的配置
 * @author 刘梓江
 */
@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    /**重写转换器、换成FastJson转换器**/
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {

        /**
         *   先把JackSon的消息转换器删除.
         *   备注: (1)源码分析可知，返回json的过程为:
         *           Controller调用结束后返回一个数据对象，for循环遍历conventers，找到支持application/json的HttpMessageConverter，然后将返回的数据序列化成json。
         *           具体参考org.springframework.web.servlet.mvc.method.annotation.AbstractMessageConverterMethodProcessor的writeWithMessageConverters方法
         *         (2)由于是list结构，我们添加的fastJson在最后。因此必须要将jackson的转换器删除，不然会先匹配上jackson，导致没使用fastJson
         */
        for (int i = converters.size() - 1; i >= 0; i--) {
            if (converters.get(i) instanceof MappingJackson2HttpMessageConverter) {
                converters.remove(i);
            }
        }

        //JSON实体对象转换器
        FastJsonHttpMessageConverter fastJsonHttpMessageConverter = new FastJsonHttpMessageConverter();
        //自定义fastJson配置
        FastJsonConfig config = new FastJsonConfig();
        SerializeConfig serializeConfig = SerializeConfig.globalInstance;
        serializeConfig.put(Long.class , ToStringSerializer.instance);
        serializeConfig.put(Long.TYPE , ToStringSerializer.instance);
        config.setSerializeConfig(serializeConfig);         //配置Long转String类型 ， 解决前后端交互， id过长，失去精度的问题
        config.setSerializerFeatures(
                SerializerFeature.WriteMapNullValue,        // 是否输出值为null的字段,默认为false,我们将它打开
                SerializerFeature.WriteNullListAsEmpty,     // 将Collection类型字段的字段空值输出为[]
                SerializerFeature.WriteNullStringAsEmpty,   // 将字符串类型字段的空值输出为空字符串
                SerializerFeature.WriteNullNumberAsZero,    // 将数值类型字段的空值输出为0
                SerializerFeature.WriteDateUseDateFormat,
                SerializerFeature.DisableCircularReferenceDetect    // 禁用循环引用
        );
        fastJsonHttpMessageConverter.setFastJsonConfig(config);
        // 添加支持的MediaTypes;不添加时默认为*/*,也就是默认支持全部
        // 但是MappingJackson2HttpMessageConverter里面支持的MediaTypes为application/json
        // 参考它的做法, fastjson也只添加application/json的MediaType
        List<MediaType> fastMediaTypes = new ArrayList<>();
        fastMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
        fastMediaTypes.add(MediaType.valueOf(MediaType.TEXT_HTML_VALUE + ";charset=UTF-8"));
        fastMediaTypes.add(MediaType.valueOf(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8"));
        fastJsonHttpMessageConverter.setSupportedMediaTypes(fastMediaTypes);

        //解决返回字符串带双引号字符串转换器
        StringHttpMessageConverter stringConverter = new StringHttpMessageConverter();
        converters.add(stringConverter);
        converters.add(fastJsonHttpMessageConverter);
    }

}

package org.king2.check_service_manage.utils;

import com.alibaba.fastjson.JSON;
import org.springframework.util.CollectionUtils;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

/**
 * 描述:校验参数信息工具类
 * @author 刘梓江
 * @Date 2021/5/23 17:51
 */
public class ValidateParameterUtil {


    /**
     * 验证json参数
     * @param json
     * @param clazz
     * @param groups
     * @return
     */
    public static<T> T validate(String json,Class<T> clazz, Class<?>... groups) {
        if(json == null || "".equals(json) || "null".equals(json)) {
            json ="{}";
        }
        T object = null;
        try {
            object = JSON.parseObject(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException("传递参数的json格式不正确");
        }
        if(object == null) {
            throw new RuntimeException("传递参数的json格式不正确");
        }
        validate(object,groups);
        return object;
    }

    /**
     * 验证对象参数
     * @param <T>
     * @param object
     * @param groups
     */
    public static <T> void validate(T object, Class<?>... groups) {
        //获得验证器
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        //执行验证
        Set<ConstraintViolation<T>> constraintViolations = validator.validate(object,groups);
        //如果有验证信息，则取出来包装成异常返回
        if (CollectionUtils.isEmpty(constraintViolations)) {
            return;
        }
        throw new ConstraintViolationException(constraintViolations);
    }
}

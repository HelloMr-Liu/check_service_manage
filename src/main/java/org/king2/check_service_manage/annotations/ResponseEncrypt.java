package org.king2.check_service_manage.annotations;

import java.lang.annotation.*;

/**
 * 描述：进行响应加密
 * @author 刘梓江
 * @date 2021/5/23 22:08
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(value= ElementType.METHOD)
public @interface ResponseEncrypt {
}

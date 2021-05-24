package org.king2.check_service_manage.entity;

import org.king2.check_service_manage.constant.SystemResultCodeEnum;

import java.io.Serializable;

/**
 * 描述：系统全局响应结果
 * @author 刘梓江
 * @date   2020/5/21 11:44
 */
public class SystemResultVo implements Serializable {

    private SystemResultVo() {
    }
    private SystemResultVo(Integer code, String message, Object result){
        this.code=code;
        this.message=message;
        this.result=result;
    }

    /**
     * 响应中的状态码
     */
    private Integer code;

    /**
     * 响应中的状态码描述
     */
    private String  message;

    /**
     * 响应中的数据结果
     */
    private Object result;

    /**
     * 构建响应成功体并带结果信息
     * @return
     */
    public static SystemResultVo ok(){
        return ok(null);
    }

    /**
     * 构建响应成功体并带结果信息
     * @param object 响应结果
     * @return
     */
    public static SystemResultVo ok(Object object){
        return new SystemResultVo(SystemResultCodeEnum.SUCCESS.STATE,SystemResultCodeEnum.SUCCESS.MESS,object);
    }

    /**
     * 构建响应错误体并带结果信息
     * @param object 响应结果
     * @return
     */
    public static SystemResultVo error(Object object){
        return new SystemResultVo(SystemResultCodeEnum.ERROR.STATE,SystemResultCodeEnum.ERROR.MESS,object);
    }

    /**
     * 构建响应体结果信息,不包含状态码信息
     * @param code      状态码
     * @param object    响应结果
     * @return
     */
    public static SystemResultVo build(Integer code, Object object){
        return new SystemResultVo(code,"no code message",object);
    }

    /**
     * 构建响应体结果信息
     * @param code      状态码
     * @param message   状态信息
     * @param object    响应结果
     * @return
     */
    public static SystemResultVo build(Integer code, String message, Object object){
        return new SystemResultVo(code,message,object);
    }


    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}

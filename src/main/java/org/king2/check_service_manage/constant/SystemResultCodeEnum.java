package org.king2.check_service_manage.constant;

/**
 * 描述：系统响应状态码信息
 *
 * @author 刘梓江
 * @date 2020/6/9  14:14
 */
public enum SystemResultCodeEnum {

    ERROR(500,"system error contact admin !"),
    SUCCESS(200,"operation success !"),
    CHECK_ERROR(100,"data validation error !"),

    SELECT_FAILURE(600," query failure !");


    public Integer STATE;           //反馈的状态码
    public String MESS;             //反馈的描述信息
    SystemResultCodeEnum(Integer state, String mess){
        this.STATE=state;
        this.MESS=mess;
    }
}
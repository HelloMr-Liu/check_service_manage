package org.king2.check_service_manage.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 描述: 显示页面请求接口控制
 * @author 刘梓江
 * @Date 2021/4/8 9:11
 */
@Controller
@RequestMapping("/html/page")
public class HtmlController {

    /**
     * 查看端口检测服务页面
     * @return
     */
    @RequestMapping("/port/check")
    public Object portCheck(){
        return "PortCheck";
    }

}

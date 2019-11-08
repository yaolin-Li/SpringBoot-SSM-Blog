package com.blog.controller;

import com.blog.entity.Bloger;
import com.blog.service.BlogerService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 转到templates/background/login.html，这个就是后台登录的页面。
 * 看到55行	<form id="fm" action="/bloger/login.do" method="post" onsubmit="return checkForm()">
 * 说明这个表单在提交之后转到的是/bloger/login
 * Created by ldb on 2016/10/29.
 */
@Controller
@RequestMapping("/bloger")
public class BlogerController {

    @Resource
    private BlogerService blogerService;

    @RequestMapping("/login")
    public String login(@RequestParam(value="userName")String userName, @RequestParam(value="password")String password, HttpServletRequest request){
        HttpSession session=request.getSession();
        Bloger bloger=new Bloger(userName,password);
        /**
         * 下面是使用Subject来进行登录验证。它比较复杂，我还没研究透。就简单说说每一步实现的功能。
         * 首先是创建Subject,Subject(主体)： 用于记录当前的操作用户，Subject在shiro中是一个接口，接口中定义了很多认证授相关的方法，外部程序通过subject进行认证授权，而subject是通过SecurityManager安全管理器进行认证授权
         * shiro的配置函数在com.blog.realm.ShiroRealm文件中。
         * UsernamePasswordToken 是用来存储用户和密码的
         * 主动调用subject.login方法时，会间接调用我们自己实现的realm的doGetAuthenticationInfo，doGetAuthenticationInfo在com.blog.realm.ShiroRealm中
         * 验证成功后添加到session里
         *
         */
        Subject subject= SecurityUtils.getSubject();
        UsernamePasswordToken token=new UsernamePasswordToken(bloger.getUserName(), bloger.getPassword());
        try{
            subject.login(token);
            session.setAttribute("currentBloger",bloger);
            return "background/main";
        }catch(Exception e){
            e.printStackTrace();
            request.setAttribute("bloger", bloger);
            request.setAttribute("error", "用户名或密码错误");
            return "background/login";
        }

    }

}

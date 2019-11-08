package com.blog.controller;


import com.blog.entity.Blog;
import com.blog.entity.PageBean;
import com.blog.service.BlogService;
import com.blog.service.BlogTagService;
import com.blog.service.BlogTypeService;
import com.blog.util.DateUtil;
import com.blog.util.PageUtil;
import com.blog.util.StringUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * RequestMapping是一个用来处理请求地址映射的注解，可用于类或方法上。用于类上，表示类中的所有响应请求的方法都是以该地址作为父路径。
 * 也就是说放在控制类上面的@RequestMapping("/blog")规定了该类下的路径都为/blog/**(**就是类里的方法上面备注的路径），想要访问/show
 * 就得输入/blog/show
 * Created by ldb on 2016/9/22.
 */
@Controller
@RequestMapping("/blog")
public class BlogController {

    @Resource
    private BlogService blogService;

    @Resource
    private BlogTypeService blogTypeService;


    @Resource
    private BlogTagService blogTagService;


    /**
     * 看这个函数之间，先打开Index.html文件，看到31行
     * <li><a href="javascript:window.open('/blog/show.do?id=2','newwindow')" th:title="网站源码下载" >网站源码下载</a></li>
     *  window.open('page.html','newwindow') 弹出新窗口的命令；
     * 　　'page.html' 弹出窗口的文件名；
     * 　　'newwindow' 弹出窗口的名字（不是文件名），非必须，可用空''代替；
     * 可以看出来，在点击这个链接之后，会打开/blog/show这个地址，传入的参数是id = 2。打开/blog/show这个地址便是运行下面的函数
     *
     * @param page
     * @param id
     * @param request
     * @return
     */
    @RequestMapping("/show")
    public String show(@RequestParam(value="page",required=false)String page,@RequestParam(value="id")String id,HttpServletRequest request){
    	HttpSession session=request.getSession();
    	//使用了一个组件，组件在util文件夹下，当page为空时设置page为1
    	if(StringUtil.isEmpty(page)){
    		page="1";
    	}
        /**
         * 通过id，从数据库中获取博客信息，访问量加一，将读取到的blog添加到request里。
         */
    	Blog blog=blogService.getBlogById(Integer.parseInt(id));
    	blogService.updateBlogReadNum(Integer.parseInt(id));
    	request.setAttribute("blog", blog);
    	//转到静态资源文件夹下的blogShow.html
    	return "foreground/blog/blogShow";
    }

    /**
     * 这个函数让我对复用有了新的认识。（虽然我也不知道这样写是好事还是坏事：D）这个函数的每个参数都是可以为空的
     * 观察index.html中第32行
     * <li th:each="blogType:${application.blogTypeList}"><a th:href="'javascript:window.open(\'/blog/list.do?typeId='+${blogType.id}+'\',\'newwindow\')'" th:title="${blogType.typeName}" th:text="${blogType.typeName}"></a></li>
     * th:xxx是thmleaf的写法，th:each="blogType:${application.blogTypeList}"类似一个foreach循环，循环读取读取application.blogTypeList，每次读取的信息都存入blogType中。application就是InitComponent.java里写的一个可以调用Spring里的bean的变量
     * window.open(\'/blog/list.do?typeId='+${blogType.id}+'\',\'newwindow\')与上面哪个类似，在这里是运行下面的函数，并且传入参数typeId.
     *
     * @param typeId
     * @param tagId
     * @param publishTime
     * @param page
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping("/list")
    public String list(@RequestParam(required=false,value="typeId")String typeId,@RequestParam(required=false,value="tagId")String tagId,@RequestParam(required=false,value="publishTime")String publishTime,@RequestParam(value="page",required=false)String page, HttpServletRequest request) throws Exception{
        if(StringUtil.isEmpty(page)){
            page="1";
        }
        String pageCode=null;
        int totalNum=0;
        PageBean pageBean=new PageBean(Integer.parseInt(page),6);
        Map<String,Object> map=new HashMap<>();
        map.put("start", pageBean.getStart());
        map.put("pageSize", pageBean.getPageSize());
        /**
         * 拿到tagId之后，通过tagId去获得一个字符串，该字符串包含生成文章的对应代码。
         */
        if(StringUtil.isNotEmpty(typeId)){
            map.put("typeId", typeId);
            totalNum=blogService.getBlogTotal(map);
            pageCode=PageUtil.getPageCode("blog/list.do?typeId="+typeId, Integer.parseInt(page), totalNum, pageBean.getPageSize());
        }
        if(StringUtil.isNotEmpty(tagId)){
            map.put("tagId", tagId);
            totalNum=blogService.getBlogTotal(map);
            pageCode=PageUtil.getPageCode("blog/list.do?tagId="+tagId, Integer.parseInt(page), totalNum, pageBean.getPageSize());
        }
        if(StringUtil.isNotEmpty(publishTime)){
            String publishTimeStr= DateUtil.formatStrToSQL(publishTime, "yyyy年MM月","yyyy-MM");
            map.put("publishTime", publishTimeStr);
            totalNum=blogService.getBlogTotal(map);
            pageCode= PageUtil.getPageCode("blog/list.do?publishTime="+publishTime, Integer.parseInt(page), totalNum, pageBean.getPageSize());
        }
        List<Blog> blogList=blogService.findBlogList(map);
        for(Blog b:blogList){
            Map<String,Object> m=new HashMap<>();
            m.put("blogId", b.getId());
            b.setContent(StringUtil.Html2Text(b.getContent()));
        }
        HttpSession session=request.getSession();
        session.setAttribute("blogList", blogList);
        session.removeAttribute("pageCode");
        session.setAttribute("pageCode", pageCode);
        return "index";
    }

}

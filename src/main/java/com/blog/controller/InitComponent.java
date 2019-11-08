package com.blog.controller;

import com.blog.entity.*;
import com.blog.service.*;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.List;

/**
 * 在 Servlet API 中有一个 ServletContextListener 接口，它能够监听 ServletContext 对象的生命周期，实际上就是监听 Web 应用的生命周期。
 * 当Servlet 容器启动或终止Web 应用时，会触发ServletContextEvent 事件，该事件由ServletContextListener 来处理。
 * Created by ldb on 2016/10/30.
 */
@Component
public class InitComponent implements ServletContextListener,ApplicationContextAware{

    private static ApplicationContext applicationContext;


    @SuppressWarnings("static-access")
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext=applicationContext;
    }

    /**
     * 当Servlet 容器启动Web 应用时调用该方法。在调用完该方法之后，容器再对Filter 初始化
     * 并且对那些在Web 应用启动时就需要被初始化的Servlet 进行初始化。
     * 在服务启动时，将数据库中的数据加载进内存，并将其赋值给一个属性名，其它的 Servlet 就可以通过 getAttribute 进行属性值的访问。
     *
     * 某些特殊的情况下，Bean需要实现某个功能，但该功能必须借助于Spring容器才能实现，此时就必须让该Bean先获取Spring容器，然后借助于
     * Spring容器实现该功能。为了让Bean获取它所在的Spring容器，可以让该Bean实现ApplicationContextAware接口。
     *
     * @param servletContextEvent
     */
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext application=servletContextEvent.getServletContext();
        BlogService blogService=(BlogService)applicationContext.getBean("blogService");
        BlogTypeService blogTypeService=(BlogTypeService)applicationContext.getBean("blogTypeService");
        BlogTagService blogTagService=(BlogTagService)applicationContext.getBean("blogTagService");
        BlogerService blogerService=(BlogerService)applicationContext.getBean("blogerService");
        LinkService linkService=(LinkService)applicationContext.getBean("linkService");

        /**
         * Spring容器会检测容器中的所有Bean，如果发现某个Bean实现了ApplicationContextAware接口，Spring容器会在创建该Bean之后，
         * 自动调用该Bean的setApplicationContextAware()方法，调用该方法时，会将容器本身作为参数传给该方法——该方法中的实现部分将
         * Spring传入的参数（容器本身）赋给该类对象的applicationContext实例变量，因此接下来可以通过该applicationContext实例变量来访问容器本身。
         *
         */
        List<Link> linkList=linkService.findLinkList();
        application.setAttribute("linkList",linkList);

        List<Blog> recommendBlogList=blogService.findRecommendBlogList();
        application.setAttribute("recommendBlogList",recommendBlogList);


        List<BlogType> blogTypeList=blogTypeService.findBlogTypeList();
        application.setAttribute("blogTypeList",blogTypeList);

        List<BlogTag> blogTagList=blogTagService.findBlogTagList();
        application.setAttribute("blogTagList",blogTagList);

        Bloger bloger=blogerService.getBloger();
        application.setAttribute("bloger",bloger);

    }


    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }


}

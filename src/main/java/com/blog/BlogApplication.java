package com.blog;

import com.blog.entity.Blog;
import com.blog.entity.BlogAdvice;
import com.blog.entity.PageBean;
import com.blog.service.BlogAdviceService;
import com.blog.service.BlogService;
import com.blog.service.BlogerService;
import com.blog.util.PageUtil;
import com.blog.util.StringUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication
@Controller
/**
 * 这里在启动类里添加了控制器。一开始看呆了，这两个还能放一起？结果上网一看还真能。。。
 * 启动器和控制器可以放在一起也可以分开。分开写的话控制器一般放在controller包下。
 */
public class BlogApplication {
    /**
     * "@Autowired"与"@Resource"都可以用来装配bean. 都可以写在字段上,或写在setter方法上。
     * 只是@AutoWried按by type自动注入，而@Resource默认按by Name自动注入。
     * 具体到代码就是说，如果你需要用xml来配置Bean。使用@Resource要求bean标签有name属性。
     * 如果不用xml来配置Bean，这两个是差不多的。
     */
	@Resource
	private BlogService blogService;

	@Resource
	private BlogerService blogerService;

	@Resource
	private BlogAdviceService blogAdviceService;
    //引入三个Bean

	public static void main(String[] args) {
		SpringApplication.run(BlogApplication.class, args);
	}

    /**
     * 做映射，当浏览器地址输入localhost:8080/的时候使用这个控制器的goIndex方法。@RequestMapping("/")指的就是8080后面的"/"；
     * GET和POST请求传的参数会自动转换赋值到@RequestParam 所注解的变量上。
     * @param page
     * @param request
     * @return
     */
	@RequestMapping("/")
	public String goIndex(@RequestParam(value="page",required=false)String page, HttpServletRequest request){
	if(StringUtil.isEmpty(page)){
			page="1";
		}
		PageBean pageBean=new PageBean(Integer.parseInt(page),Integer.parseInt("6"));
		/**
		 * PageBean是写在entity里的工具，第一个参数是page,第二个参数是pageSize，目前还不知道是干嘛用的，继续往下看
		 * pageBean.getStart()返回的是(page-1)*pageSize;猜测可能是首页中的目录？
		 *
		 */
		Map<String,Object> map=new HashMap<>();
		map.put("start", pageBean.getStart());
		map.put("pageSize", pageBean.getPageSize());
		/**
		 * blogService.getBlogTotal获取所有的博客文章，返回文章的数量
		 * PageUtil是一个用来分页的工具，文章数量超过pageSize（默认为6的时候）进行分页。返回的是构成页面的html代码写成的字符串。
		 * blogService.findBlogList(map)找到所有文章的id/生成时间/标题等信息
		 * 在一个for循环，里通过刚刚获取的id来得到文章的内容，并且放入到blogList中。相当于在原来只有id/生成时间/标题等信息的blogList
		 * 中加入了内容
		 */
		int totalNum=blogService.getBlogTotal(new HashMap<String,Object>());
		String pageCode= PageUtil.getPageCode("/?a=2", Integer.parseInt(page), totalNum, pageBean.getPageSize());
		List<Blog> blogList=blogService.findBlogList(map);
		for(Blog blog:blogList){
			Map<String,Object> m=new HashMap<>();
			m.put("blogId", blog.getId());
			blog.setContent(StringUtil.Html2Text(blog.getContent()));
		}
		/**
		 * 下面开始加载文章的评论，过程与上述差不多，从数据库读取了博客评论，点击数，和热评（通过阅读数排序，然后取前几个）
		 *
		 */
		Map<String,Object> blogAdviceMap=new HashMap<String,Object>();
		blogAdviceMap.put("start", 0);
		blogAdviceMap.put("pageSize", 3);
		List<BlogAdvice> newsestBlogAdviceList=blogAdviceService.findBlogAdviceList(blogAdviceMap);
		List<Blog> dateCountList=blogService.findDateCountList();
		List<Blog> hotBlogList=blogService.findHotBlogList();
		List<Blog> technologyBlogList=blogService.findTechnologyBlogList();
		/**
		 * 把评价信息存入session
		 * HttpSession：通俗的理解应该是基于HTTP协议而产生的服务器级别的对象。其独立于客户端发的请求，并不是客户端每一次的请求便会创建此对象，也不是客户端关闭了就会被注销。
		 */
		HttpSession session=request.getSession();
		session.setAttribute("hotBlogList",hotBlogList);
		session.setAttribute("technologyBlogList",technologyBlogList);
		session.setAttribute("blogList",blogList);
		session.setAttribute("newsestBlogAdviceList",newsestBlogAdviceList);
		session.setAttribute("dateCountList",dateCountList);
		//session.setAttribute("blogListTemp", "foreground/blog/blogList");
		session.setAttribute("pageCode", pageCode);
		/**
		 * 总点击量加一
		 */
		blogerService.updateWebClick();
		/**
		 * 跳转到静态资源存放位置：src/main/resources/templates
		 * 找到目录下的index.html静态资源
		 */
		return "index";
	}

	/**
	 * 当用户在地址栏输入localhost:8080/background的时候转到src/main/resources/templates/background/login
	 * 进入博客管理系统，默认的管理员账号密码可以在数据库中的t_blogadvice表里看到，username:ldb password:123456
	 * 数据库就是项目文件里的db_blog.sql，把它导入到mysql里
	 * @return
	 */
	@RequestMapping("/background")
	public String goLogin(){
		return "background/login";
	}



}

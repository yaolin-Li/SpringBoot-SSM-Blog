# SpringBoot-SSM-Blog
从零开始学习一位大佬的SpringBoot项目，争取每个语句块都写上注释，方便大家一起来学习。
来源:https://github.com/llldddbbb/Blog

1.拿到一个项目我习惯先从启动器开始看
    11/7:学习BlogApplication.java内容，完成代码备注.
    11/8:完成BlogApplication.java学习,这个文件用来实现客户和管理员接入映射.
2.接下来看controller文件夹下的文件
    先从用户这边的开始看
        InitComponent.java,
        BlogController,
        BlogerController,
        ShiroRealm,
        11/9:从BlogerController.java跳转到BlogAdminController.java,
        从这里开始，就到了与管理员功能相关的接口了。简单浏览了一遍，感觉已经没有了技术上的变化，都是重复的数据读写。
        出现了一种用json格式返回数据的写法。大体上就是把数据用json格式包装起来，然后直接发送到前端页面。再在页面里对json数据
        进行解析和使用。
3.后端就暂时到这，开始结合前端页面来看这个项目。
    首先就是用户能接触到的页面templates.foreground文件下的静态资源。然后是管理员使用的页面。
    这些页面做的很好，但是我觉得这些都是模板，想要用的时候其实可以直接拿来用的。所以就不做过多的解释了。
    
    
    这是我第一个认真看完的项目，本来是想着看完之后自己也依葫芦画瓢做一个博客。但是突然发现这个上传这个代码的大佬其实还上传了后续版本。
    既然要用就用最新的技术和写法。我接下来会去学习这个大佬写的第二代博客。

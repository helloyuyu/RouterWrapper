**为集成ARouter模块生成所有的路由界面路由调用的同一入口**
<code>Navigator.class</code>\
**用于模块内调用和对外的文档抽取**

###引入
**AndroidStudio**
```groovy
dependencies {
    annotationProcessor 'com.helloyuyu:routerwrapper-compiler:xxx'
    //引入一定要在下面这句Arouter依赖的之前
    //annotationProcessor 'com.alibaba:arouter-compiler:xxx'
}
```
指定Navigator的最终生成位于包名下\
替换'com.xxx.xxx'文件输出在哪个包名下一般为模块包名就可以
```groovy
android {
    defaultConfig {
        javaCompileOptions {
                annotationProcessorOptions {
                    arguments = [optionsOutputPackageName: "com.xxx.xxx"]
                }
        }
    }
}
```
###使用
在RebuildProject后 调用Navigator 静态方法对应的Activity
```java
    Navigator.yourRouteActivity().navigation();
```
Navigator静态方法会返回对应Postcard

**注意**\
使用 @Autowired 注解注入的字段
```java
    /**
     * 消息
     */
    @Autowired(name = "message_string",required = true)
    String message;
    
    @Autowired(name = "test_bean")
    TestBean testBean;
```
如果不是必须的
会组合非必须的字段生成多个静态方法
```java
/**
 * 模块路由表 */
public class Navigator {
  /**
   * 路由描述: 测试路由的
   *
   *  @author xjs
   *
   * {@link com.helloyuyu.routerwrapper.demo.ui.TestBuilderActivity}
   * @param message 消息
   */
  public static Postcard testBuilderActivity(String message) {
    return ARouter.getInstance().build("/test/test_route").withString("message_string",message);}

  /**
   * 路由描述: 测试路由的
   *
   *  @author xjs
   *
   * {@link com.helloyuyu.routerwrapper.demo.ui.TestBuilderActivity}
   * @param message 消息
   * @param testBean testBean
   */
  public static Postcard testBuilderActivity1(String message, TestBean testBean) {
    return ARouter.getInstance().build("/test/test_route").withString("message_string",message).withObject("test_bean",testBean);}
```
####联系作者
如有问题或其他方面请邮件\
email : jsxie1024@163.com





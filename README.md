**为集成ARouter模块生成所有的路由界面路由调用的同一入口**
<code>Navigator.class</code>\
**用于模块内调用和对外的文档抽取**

####引入
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



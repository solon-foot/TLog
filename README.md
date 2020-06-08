# TLog

#### 介绍
Android 简单的日志格式打印

1. 对Collection、Map array等打印友好
2. 直接定位到log打印所在的行号

#### 软件架构
软件架构说明


#### 安装教程

1. Add it in your root build.gradle at the end of repositories:
~~~
    allprojects {
        repositories {
            ...
            maven { url 'https://jitpack.io' }
        }
    }
~~~

2.Add the dependency

~~~
    dependencies {
        implementation 'com.github.solon-foot:TLog:Tag'
    }

~~~

#### 使用说明

~~~
TLog.init("APP_NAME", Log.INFO);
TLog.e(Object... args)
~~~


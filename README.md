[![](https://jitpack.io/v/solon-foot/Tlog.svg)](https://jitpack.io/#solon-foot/Tlog)
# TLog

## 介绍
Android 简单的日志格式打印

1. 对Collection、Map array等打印友好
2. log 显示行号,可以点击跳转到代码



## 使用教程

在root build.gradle中添加jitpack
~~~
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
~~~
使用java8 编译
```
android {
    
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
}
```

#### 基础依赖
~~~
implementation 'com.github.solon-foot.Tlog:base-log:1.1.1'
~~~
#### 使用网络日志
```
debugImplementation 'com.github.solon-foot.Tlog:http-log:1.1.1'
//暂时不知道如何将依赖包打包进去,需要手动添加如下依赖
debugImplementation 'org.java-websocket:Java-WebSocket:1.3.9'
debugImplementation 'org.nanohttpd:nanohttpd:2.3.1'

```
重要:
* 确保电脑和Android 设备在同一个网络中
* 如果通过USB使用,请运行 `adb forward tcp:8080 tcp:8080`(既然使用了adb了,还是使用logcat吧)
* 默认端口`8080`如需修改,请添加
```
debug {
    resValue("integer", "LOG_PORT_NUMBER", "8888")
    //如需关闭ip通知,添加如下
    resValue("bool", "SHOW_DEVICE_IP", "false")
}
```
#### 使用浮窗日志
```
debugImplementation'com.github.solon-foot.Tlog:view-log:1.1.1'
```
别忘记打开悬浮窗权限

## 使用代码参考
~~~
//设置log 的TAG和显示级别
TLog.init("APP_NAME", Log.INFO);
TLog.e(Object... args)

//自定义对象的解析,例如需要int 以16进制展示
TLog.register((sb,o)=>{
    if(!(o instanceof Interger)){
        return false;
    }
    sb.append(Interger.toHexString((Interger) o));
    return true;
}));
~~~


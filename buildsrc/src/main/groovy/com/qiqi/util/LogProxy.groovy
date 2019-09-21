package com.qiqi.util

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method

/**
 * Created by mingzhihuang on 2016/5/24.
 */
public class LogProxy implements InvocationHandler {
    private java.lang.Object obj;

    public LogProxy(HookProjectTaskInterface real) {
        super();
        this.obj = real;
    }

    @Override
    java.lang.Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        doBefore(method);
        java.lang.Object result = method.invoke(obj, args);
        doAfter(method);
        return result;
    }
    private  void doBefore(Method method){
        if (method.getName().startsWith("prepare") || method.getName().startsWith("make")){
            Log.PrePare(method.getName())
        }
    }

    private  void doAfter(Method method){

    }
}

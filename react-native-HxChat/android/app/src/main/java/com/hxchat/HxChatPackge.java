package com.hxchat;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.JavaScriptModule;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class HxChatPackage implements ReactPackage {
    @Override
    public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
        return Arrays.<NativeModule>asList(new HxChatMoudel(reactContext));
    }

    //一般情况createJSModules（）的返回值都是空集合。
    @Override
    public List<Class<? extends JavaScriptModule>> createJSModules() {

        return Collections.emptyList();
    }

    @Override
    public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {

        //返回值需要修改
        return Collections.emptyList();
    }
}
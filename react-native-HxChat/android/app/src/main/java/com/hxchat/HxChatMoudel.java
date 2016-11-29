package com.hxchat;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.common.MapBuilder;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;

public class HxChatMoudel extends ReactContextBaseJavaModule {
    EMMessageListener msgListener;


         public HxChatMoudel(ReactApplicationContext reactContext) {

             super(reactContext);

         }
        @Override
        public String getName() {
            return "HxChat";
        }

    @ReactMethod
    public void initHx(){

        EMOptions options = new EMOptions();
        // 默认添加好友时，是不需要验证的，改成需要验证
        options.setAcceptInvitationAlways(false);
        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid);
        // 如果APP启用了远程的service，此application:onCreate会被调用2次
        // 为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1次
        // 默认的APP会在以包名为默认的process name下运行，如果查到的process name不是APP的process name就立即返回

        if (processAppName == null ||!processAppName.equalsIgnoreCase(this.getPackageName())) {
            Log.e("已经初始化", "enter the service process!");

            // 则此application::onCreate 是被service 调用的，直接返回
            return;
        }
        //初始化
        EMClient.getInstance().init(this, options);
    }
    public String getAppName(int pID) {
        String processName = null;
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = this.getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
                // Log.d("Process", "Error>> :"+ e.toString());
            }
        }
        return processName;
    }

    @ReactMethod
    public void login(String userName,String password){
        EMClient.getInstance().login(userName,password,new EMCallBack() {//回调
            @Override
            public void onSuccess() {
                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();
                Log.d("main", "登录聊天服务器成功！");
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
                Log.d("main", "登录聊天服务器失败！");
            }
        });
    }

    @ReactMethod
    public void register(String userName,String password){
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    EMClient.getInstance().createAccount(userName, password);
                    Log.e("注册:","注册成功");
                } catch (HyphenateException e) {
                    Log.e("注册：", "注册失败"+"}"+e.toString());
                }
            }
        });
    }
    @ReactMethod
    public void exitHx(){
        EMClient.getInstance().logout(true);
    }
    @ReactMethod
    public void sendMessage(String messageContent,String toUserName){
        //创建一条文本消息，content为消息文字内容，toChatUsername为对方用户或者群聊的id，后文皆是如此
        EMMessage message = EMMessage.createTxtSendMessage(messageContent, toUserName);

        //如果是群聊，设置chattype，默认是单聊
//				if (chatType == CHATTYPE_GROUP)
        message.setChatType(EMMessage.ChatType.Chat);
        message.setMessageStatusCallback(new EMCallBack() {

            @Override
            public void onSuccess() {
                Log.e("消息：", "消息发送成功！");
            }

            @Override
            public void onProgress(int arg0, String arg1) {
                Log.e("消息：", "正在发送");

            }

            @Override
            public void onError(int arg0, String arg1) {
                Log.e("消息：", "发送失败"+"||"+arg1);

            }
        });
        //发送消息
        EMClient.getInstance().chatManager().sendMessage(message);
    }
    @ReactMethod
    public String receiveMessage(){
       msgListener = new EMMessageListener() {
            @Override
            public void onMessageReceived(List<EMMessage> messages) {
                //收到消息
                for(final EMMessage message : list){
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            Log.e("收到消息", ((EMTextMessageBody)message.getBody()).getMessage());
                            return ((EMTextMessageBody)message.getBody()).getMessage();

                        }
                    });
                }
            }

            @Override
            public void onCmdMessageReceived(List<EMMessage> messages) {
                //收到透传消息
            }

            @Override
            public void onMessageReadAckReceived(List<EMMessage> messages) {
                //收到已读回执
            }

            @Override
            public void onMessageDeliveryAckReceived(List<EMMessage> message) {
                //收到已送达回执
            }

            @Override
            public void onMessageChanged(EMMessage message, Object change) {
                //消息状态变动
            }
        };
    }
    @ReactMethod
    public void addMessageListener(){
        EMClient.getInstance().chatManager().addMessageListener(msgListener);
    }
    @ReactMethod
    public void removeMessageListener(){
        EMClient.getInstance().chatManager().removeMessageListener(msgListener);
    }

}
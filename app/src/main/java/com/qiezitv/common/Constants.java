package com.qiezitv.common;

/**
 * APP常量
 */
public class Constants {

    /**
     * 全局临时token
     */
    public static String ACCESS_TOKEN = null;

    /**
     * 启动app时SplashActivity画面停止时间 milliseconds
     */
    public static final int SPLASH_STOP_TIME = 1000;
    /**
     * sp文件名称
     */
    public static final String SP_FILE_NAME = "com_fjnu_qiezi_sp";

    //--------------------------  http 请求部分 -----------------------------
    /**
     * 服务器默认IP地址
     */
    public static final String SERVER_DEFAULT_IP = "www.qiezizhibo.com";
    /**
     * http连接超时时间
     */
    public static final int CONNECT_TIMEOUT = 20;
    /**
     * http读超时时间
     */
    public static final int READ_TIMEOUT = 30;
    /**
     * http写超时时间
     */
    public static final int WRITE_TIMEOUT = 30;


    //--------------------------- sp key --------------------
    /**
     * 登录账号
     */
    public static final String SP_LOGIN_USER = "sp_login_user";
    /**
     * 是否记住密码
     */
    public static final String SP_IS_REMEMBER = "sp_remember";
    /**
     * 登录密码
     */
    public static final String SP_LOGIN_PASSWORD = "sp_login_password";
    /**
     * 是否自动登录
     */
    public static final String SP_IS_AUTO_LOGIN = "sp_is_auto_login";
    /**
     * 登录AccessToken
     */
    public static final String SP_ACCESS_TOKEN = "sp_access_token";

    public static class MatchType {
        public static final int TIME_LINE = 1;
        public static final int STATISTIC = 2;
        public static final int PLAYER_LIST = 3;
        public static final int CHATTING_ROOM = 4;
        public static final int HIGHLIGHT = 5;
    }

    public static class VideoQuality {
        public static final int LOW = 0;
        public static final int MID = 1;
        public static final int HIGH = 2;
    }
    public class ActivityQuality {
        public static final int UNKNOW = -1;
        public static final int NORMAL = 0;
        public static final int BAD = 1;
        public static final int NOTBAD = 2;
    }
}

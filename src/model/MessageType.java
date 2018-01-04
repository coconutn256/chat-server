package model;

public final class MessageType {
    public static final int HEARTBEAT = 0;  //来自客户端的心跳检测
    public static final int TEXT = 1;
    public static final int SEND_FILE = 2;  //收到、发出文件
    public static final int RECV_FILE = 3;  //确认接收文件
    public static final int REGISTER = 4;
    public static final int LOGIN = 5;
    public static final int LOGIN_FAIL_PASSERROR = 6;
    public static final int LOGIN_FAIL_ALREADYONLINE = 7;
    public static final int LOGIN_SUCESS = 8;
    public static final int REGISTER_FAIL = 9;
    public static final int REGISTER_SUCESS = 10;
    public static final int ADD_FRIEND = 11;       //客户端发送：请求加好友，服务端发送：来自他人的好友请求
    public static final int DEL_FRIEND = 12;       //单向操作
    public static final int CONFIRM_FRIEND = 13;   //客户端发送，接收好友请求
    public static final int LOGOUT = 14;
    public static final int LOAD_LOG = 15;
}

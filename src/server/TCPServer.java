package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tools.JsonUtils;
import data.mysql.MysqlDatabase;
import model.*;
import model.UsrInfo;
import org.json.JSONArray;
import org.json.JSONObject;
import runable.Start;

import static tools.ByteUtils.addBytes;
import static tools.ByteUtils.byteArrayToInt;
import static tools.ByteUtils.intToByteArray;

public class TCPServer extends ServerSocket {
    private static final int SERVER_PORT = 7777;
    private static final int TIMEOUT = 4000;
    private MysqlDatabase mysqlDatabase;

    public TCPServer() throws IOException {
        super(SERVER_PORT);
        try {
            System.out.println("server started successfully.");
            mysqlDatabase = new MysqlDatabase();
            while (true) {
                Socket socket = accept();
                new CreateServerThread(socket);//当有请求时，启一个线程处理
            }
        } catch (IOException e) {
            System.out.println("Creat server falied: " + e.toString());
            //TODO:错误信息可视化
        } finally {
            close();
        }
    }

    //线程类
    class CreateServerThread extends Thread {
        private Socket client;
        private BufferedReader bufferedReader;
        private PrintWriter printWriter;

        public CreateServerThread(Socket s) throws IOException {
            client = s;
            bufferedReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            printWriter = new PrintWriter(client.getOutputStream(), true);
            System.out.println("Client(" + getName() + ") come in...");

            start();
        }

        public void run() {
            try {
                DataInputStream dis = new DataInputStream(client.getInputStream());
                DataOutputStream dos = new DataOutputStream(client.getOutputStream());
                long startTime = System.currentTimeMillis();

                String uid = null;
                String targetId, passwd, content;
                Map<String, String> tempMap;
                Message message;

                while (true) {
                    byte[] head = new byte[4];
                    dis.read(head);
                    byte[] data = new byte[byteArrayToInt(head)];
                    dis.read(data);

                    JSONObject json = new JSONObject(data);
                    System.out.println(json.toString());
                    printWriter.close();
                    bufferedReader.close();

                    int type = Integer.parseInt(json.getString("type"));
                    JSONArray jsonArray = new JSONArray();
                    switch (type) {
                        //收到心跳，重置计时
                        case MessageType.HEARTBEAT:
                            startTime = System.currentTimeMillis();
                            break;
                        //登录、注册操作
                        case MessageType.LOGIN:
                            uid = json.getString("uid");
                            passwd = json.getString("password");
                            if (passwd.equals(mysqlDatabase.getPasswordByUid(uid)) && !Start.onlineList.contains(uid)) {
                                //登录成功
                                tempMap = new HashMap<String, String>();
                                tempMap.put("type", String.valueOf(MessageType.LOGIN_SUCESS));
                                jsonArray.put(tempMap);
                                Start.onlineList.add(uid);
                                //好友列表(未按最近消息排序)
                                List<Friend> recentMessage = mysqlDatabase.getFriendsByUid(uid);
                                for (Friend friend : recentMessage) {
                                    UsrInfo tempusr = mysqlDatabase.getUsrInfoByUid(friend.friend_id);
                                    tempMap = new HashMap<String, String>();
                                    tempMap.put("frend_id", friend.friend_id);
                                    tempMap.put("name", mysqlDatabase.getUsrInfoByUid(friend.friend_id).name);
                                    tempMap.put("remark", friend.remark);
                                    tempMap.put("tag", friend.tag);
                                    jsonArray.put(tempMap);
                                }
                            } else if (Start.onlineList.contains(uid)) {
                                //已经在线
                                tempMap = new HashMap<String, String>();
                                tempMap.put("type", String.valueOf(MessageType.LOGIN_FAIL_ALREADYONLINE));
                                jsonArray.put(tempMap);
                            } else {
                                //账号密码错误
                                tempMap = new HashMap<String, String>();
                                tempMap.put("type", String.valueOf(MessageType.LOGIN_FAIL_PASSERROR));
                                jsonArray.put(tempMap);
                            }
                            startTime = System.currentTimeMillis();
                            break;
                        //登出
                        case MessageType.LOGOUT:
                            uid = json.getString("uid");
                            Start.onlineList.remove(uid);
                            startTime = System.currentTimeMillis();
                            break;
                        //注册操作
                        case MessageType.REGISTER:
                            uid = json.getString("uid");
                            passwd = json.getString("password");
                            if (mysqlDatabase.getUsrInfoByUid(uid) != null) {
                                tempMap = new HashMap<String, String>();
                                tempMap.put("type", String.valueOf(MessageType.REGISTER_FAIL));
                                jsonArray.put(tempMap);
                            } else {
                                try {
                                    mysqlDatabase.addUsrInfo(new UsrInfo(uid, passwd));
                                    tempMap = new HashMap<String, String>();
                                    tempMap.put("type", String.valueOf(MessageType.REGISTER_SUCESS));
                                    jsonArray.put(tempMap);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            startTime = System.currentTimeMillis();
                            break;

                        //好友操作
                        case MessageType.ADD_FRIEND:
                            //将请求存入内存并发送给对象
                            uid = json.getString("uid");
                            targetId = json.getString("targetId");
                            message = JsonUtils.JsonToMessage(json);
                            message.uid = targetId;
                            message.targetId = uid;
                            Start.UnsendMessage.get(targetId).add(message);
                            startTime = System.currentTimeMillis();
                            break;
                        case MessageType.CONFIRM_FRIEND:
                            uid = json.getString("uid");
                            targetId = json.getString("targetId");
                            try {
                                mysqlDatabase.addFriend(uid, targetId);
                                //TODO:向双方发送添加成功的信息
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case MessageType.DEL_FRIEND:
                            uid = json.getString("uid");
                            targetId = json.getString("targetId");
                            try {
                                mysqlDatabase.delFriend(uid, targetId);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            startTime = System.currentTimeMillis();
                            break;
                        case MessageType.TEXT:
                            uid = json.getString("uid");
                            targetId = json.getString("targetId");
                            message = JsonUtils.JsonToMessage(json);

                            mysqlDatabase.addMessage(message);

                            message.uid = targetId;
                            message.targetId = uid;
                            Start.UnsendMessage.get(targetId).add(message);
                            startTime = System.currentTimeMillis();
                            break;
                        case MessageType.SEND_FILE:
                            uid = json.getString("uid");
                            targetId = json.getString("targetId");
                            message = JsonUtils.JsonToMessage(json);

                            mysqlDatabase.addMessage(message);

                            message.uid = targetId;
                            message.targetId = uid;
                            Start.UnsendMessage.get(targetId).add(message);
                            //TODO:将文件存到本地
                            startTime = System.currentTimeMillis();
                            break;
                        case MessageType.RECV_FILE:
                            //TODO:发送文件到客户端
                            break;
                        case MessageType.LOAD_LOG:
                            uid = json.getString("uid");
                            targetId = json.getString("targetId");
                            List<Message> messageList = mysqlDatabase.getRecentMessage(uid,targetId);
                            for(Message m:messageList){
                                jsonArray.put(JsonUtils.MessageToJson(m));
                            }
                            startTime = System.currentTimeMillis();
                            break;
                    }

                    //响应超时,去除在线状态，关闭tcp
//                    if (System.currentTimeMillis() - startTime > TIMEOUT) {
//                        if (Start.onlineList.contains(uid))
//                            Start.onlineList.remove(uid);
//                        client.close();
//                    }

                    //发送jsonArrary,清空未发送消息
                    if (!Start.UnsendMessage.get(uid).isEmpty()) {
                        for (Message m : Start.UnsendMessage.get(uid)) {
                            jsonArray.put(JsonUtils.MessageToJson(m));
                            //TODO:对于文件传输的处理（TYPE为FILE）
                        }
                    }
                    byte[] jsonByte = jsonArray.toString().getBytes();
                    int jsonSize = jsonByte.length;
                    dos.write(addBytes(intToByteArray(jsonSize),jsonByte));
                    dos.flush();
                    Start.UnsendMessage.get(uid).clear();

                    //接收来自客户端的文件模块，需要文件名及大小
//                    byte[] buf = new byte[1024];
//                    int passlen = 0;
//                    String[] temp = json.getString("content").split(File.separator);
//                    String savePath = "./" + temp[temp.length - 1];
//                    DataOutputStream fileout = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(savePath)));
//                    long len = Integer.parseInt(json.getString("content").split(",")[1]);
//                    long passedlen = 0;
//                    while (true) {
//                        int read = 0;
//                        if (dis != null)
//                            read = dis.read(buf);
//                        if (read == -1)
//                            break;
//                        passedlen += read;
//                        fileout.write(buf, 0, read);
//                        System.out.println("接收进度：" + passedlen / len * 100 + "%");
//                    }
//                    System.out.println("接收完成");
//                    fileout.close();
//                    dis.close();


                    //向客户端发送文件模块，需要文件名及大小
//                if (type == MessageType.RECV_FILE) {
//                    String filepath = json.getString("content").split(",")[0];
//                    byte[] bufArray = new byte[1024];
//                    FileInputStream fileInputStream = new FileInputStream(filepath);
//                    while (true) {
//                        int read = 0;
//                        if (fileInputStream != null)
//                            read = fileInputStream.read(bufArray);
//                        if (read == -1)
//                            break;
//                        dos.write(bufArray, 0, read);
//                    }
//                    dos.flush();
//                    fileInputStream.close();
//                }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

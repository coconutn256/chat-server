package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import data.Json.JsonUtils;
import data.mysql.MysqlDatabase;
import model.*;
import model.UsrInfo;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TCPServer extends ServerSocket {
    private static final int SERVER_PORT = 7777;
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
                System.out.println(dis.available());
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] bytes = new byte[1024];
                int n;
                while ((n = dis.read(bytes)) != -1) {
                    System.out.println(new String(bytes));
                    baos.write(bytes);
                    baos.flush();
                }
                JSONObject json = new JSONObject(baos.toString());
                System.out.println(json.toString());
                printWriter.close();
                bufferedReader.close();
                baos.close();

                int type = Integer.parseInt(json.getString("type"));
                DataOutputStream dos = new DataOutputStream(client.getOutputStream());
                JSONArray jsonArray = new JSONArray();
                String uid, targetId, passwd;
                Map<String, String> tempMap;
                switch (type) {
                    //登录、注册操作
                    case MessageType.LOGIN:
                        uid = json.getString("uid");
                        passwd = json.getString("password");
                        if (passwd.equals(mysqlDatabase.getPasswordByUid(uid))) {
                            tempMap = new HashMap<String, String>();
                            tempMap.put("type", String.valueOf(MessageType.LOGIN_SUCESS));
                            jsonArray.put(tempMap);

                            List<Friend> recentMessage = mysqlDatabase.getFriendsByUid(uid);
                            for(Friend friend:recentMessage){
                                UsrInfo tempusr = mysqlDatabase.getUsrInfoByUid(friend.friend_id);
                                tempMap = new HashMap<String, String>();
                                tempMap.put("frend_id", friend.friend_id);
                                tempMap.put("remark",friend.remark);
                                tempMap.put("tag",friend.tag);
                                jsonArray.put(tempMap);
                            }
                        } else {
                            tempMap = new HashMap<String, String>();
                            tempMap.put("type", String.valueOf(MessageType.LOGIN_FAIL));
                            jsonArray.put(tempMap);
                        }
                        break;
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
                        break;

                    //好友操作
                    case MessageType.ADD_FRIEND:
                        uid = json.getString("uid");
                        targetId = json.getString("targetId");
                        //TODO:将请求存入数据库并发送给对象
                        break;
                    case MessageType.CONFIRM_FRIEND:
                        uid = json.getString("uid");
                        targetId = json.getString("targetId");
                        try{
                            mysqlDatabase.addFriend(uid,targetId);
                            //TODO:向双方发送添加成功的信息
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        break;
                    case MessageType.DEL_FRIEND:
                        uid = json.getString("uid");
                        targetId = json.getString("targetId");
                        try{
                            mysqlDatabase.delFriend(uid,targetId);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        break;
                }

                //TODO:发送模块
//                if (type == Message.RECVFILE) {
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
//                dos.close();
            } catch (Exception e) {
                e.printStackTrace();
                //TODO:错误信息可视化(发送/接收失败)
            } finally {
                if (client != null) {
                    try {
                        client.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}

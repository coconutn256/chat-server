package data.mysql;

import model.*;

import javax.xml.transform.Result;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MysqlDatabase {
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/chat_client";
    static final String USER = "root";
    static final String PASSWORD = "root";

    Connection conn = null;
    Statement stmt = null;

    public MysqlDatabase() {
        try {
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            stmt = conn.createStatement();
            stmt.execute("SET SQL_SAFE_UPDATES = 0;");
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getPasswordByUid(String uid) {
        String sql = "select password from usrinfo where uid = \"" + uid + "\";";
        String result = null;
        try {
            ResultSet rs = stmt.executeQuery(sql);
            rs.next();
            result = rs.getString("password");
            rs.close();
            return result;
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            return null;
        }
    }

    public UsrInfo getUsrInfoByUid(String uid) {
        UsrInfo usrInfo = new UsrInfo();
        usrInfo.uid = uid;
        String sql = "select name,avator from usrinfo where uid = \"" + uid + "\";";
        try {
            ResultSet rs = stmt.executeQuery(sql);
            rs.next();
            usrInfo.name = rs.getString("name");
            usrInfo.avator = rs.getString("avator");
            rs.close();
            return usrInfo;
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return null;
        }
    }


    public int addUsrInfo(UsrInfo usrInfo) {
        try {
            String sql = "select * from usrinfo where uid = \'" + usrInfo.uid + "\';";
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next())
                return -1;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            String sql = "insert into usrinfo(uid,name,avator,password) values (\'" + usrInfo.uid + "\',\'" + usrInfo.name +
                    "\',\'" + usrInfo.avator + "\',\'" + usrInfo.password + "\');";
            stmt.executeQuery(sql);
            return 0;
        } catch (SQLException e) {
            //TODO:
            e.printStackTrace();
        }
        return -1;
    }

    public int setUsrInfo(UsrInfo usrInfo) {
        try {
            String sql = "select * from usrinfo where uid = \'" + usrInfo.uid + "\';";
            ResultSet rs = stmt.executeQuery(sql);
            if (!rs.next())
                return -1;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            String sql = "update usrinfo set name=\'" + usrInfo.name +
                    "\',avator=\'" + usrInfo.avator + "\',password=\'" + usrInfo.password + "\' where uid=\'" + usrInfo.uid + "\';";
            stmt.executeQuery(sql);
            return 0;
        } catch (SQLException e) {
            //TODO:
            e.printStackTrace();
        }
        return -1;
    }


    public List<Friend> getFriendsByUid(String uid) {
        String sql = "select * from friends where uid = \"" + uid + "\";";
        List<Friend> results = new ArrayList<Friend>();
        try {
            ResultSet rs = stmt.executeQuery(sql);
            Friend friend;
            while (rs.next()) {
                friend = new Friend();
                friend.uid = uid;
                friend.friend_id = rs.getString("friend_id");
                friend.remark = rs.getString("remark");
                friend.tag = rs.getString("tag");
                results.add(friend);
            }
            rs.close();
            return results;
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return null;
        }
    }

    public void addFriend(String uid, String friend_id) {
        String sql1 = "insert into friends(id,uid,friend_id) values (\'" + getRowNum("friends") + "\',\'" + uid + "\',\'" + friend_id + "\');";
        String sql2 = "insert into friends(id,uid,friend_id) values (\'" + getRowNum("friends")+1 + "\',\'" + friend_id + "\',\'" + uid + "\');";
        try {
            stmt.execute(sql1);
            stmt.execute(sql2);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delFriend(String uid, String friend_id) {
        String sql = "DELETE FROM friends WHERE uid = " + uid + " and friend_id = " + friend_id + " ;";
        try {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setFriend(String uid, String friend_id, String remark, String tag) {
        String sql = "update friends set remark=\'" + remark + "\',tag=\'" + tag + "\';";
        try {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //按时间倒序排列
    public List<Message> getRecentMessage(String uid, String targetId) {
        String sql = "select * from message where uid = \"" +
                uid + "\" and target_id = + \"" + targetId + "\" or uid = \"" +
                targetId + "\" and target_id = \"" + uid + "\" order by date DESC " + ";";
        List<Message> results = new ArrayList<Message>();
        try {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Message message = new Message();
                message.uid = rs.getString("uid");
                message.targetId = rs.getString("target_id");
                message.type = rs.getInt("type");
                message.content = rs.getString("content");
                message.dateTime = rs.getTimestamp("date");
                results.add(message);
            }
            rs.close();
            return results;
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return results;
        }
    }

    public void addMessage(Message message){
        String sql = "insert into message(uid,target_id,type,content,date) values (\'" + message.uid + "\',\'" + message.targetId + "\',\'" + message.type +
                "\',\'" + message.content + "\',\'" + message.dateTime + "\');";
        try {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getRowNum(String table) {
        String sql = "select id from " + table + " order by id desc;";
        try {
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                int num = rs.getInt(1);
                rs.close();
                return num + 1;
            } else {
                rs.close();
                return 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }

    }

    protected void finalize() {
        try {
            if (stmt != null) stmt.close();
        } catch (SQLException se2) {
            ;
        }
        try {
            if (conn != null) conn.close();
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }
}

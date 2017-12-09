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
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return result;
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
            return usrInfo;
        }
    }

    public List<String> getFriendsByUid(String uid) {
        String sql = "select * from friends where uid = \"" + uid + "\";";
        List<String> results = new ArrayList<String>();
        try {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                results.add(rs.getString("friend_id"));
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

    //按时间倒序排列
    public List<Message> getRecentMessage(String uid, String targetId) {
        String sql = "select * from message where uid = \"" +
                uid + "\" and target_id = + \"" + targetId + "\" or uid = \""  +
                targetId + "\" and target_id = \"" + uid + "\" order by date DESC " + ";";
        List<Message> results = new ArrayList<Message>();
        try {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Message message = new Message();
                message.uid = uid;
                message.targetId = targetId;
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

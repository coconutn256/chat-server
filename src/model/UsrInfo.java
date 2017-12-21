package model;

import java.util.List;

public class UsrInfo {
    public String uid;
    public String name;
    public String avator;
    public String password;
    public List<String> friends;
    public List<String> groups;

    public UsrInfo(){
        uid = null;
        name = null;
        avator = null;
        password = null;
    }

    public UsrInfo(String uid,String name){
        this.uid = uid;
        this.name = name;
    }


}

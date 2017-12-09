package model;

import java.util.List;

public class UsrInfo {
    public String uid;
    public String name;
    public String avator;
    public List<String> friends;
    public List<String> groups;

    public UsrInfo(){

    }

    public UsrInfo(String uid,String name){
        this.uid = uid;
        this.name = name;
    }


}

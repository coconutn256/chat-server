import data.*;
import data.mysql.MysqlDatabase;
import model.Message;

import java.util.List;
import java.util.Scanner;

public class Test {
    public static void main(String args[]){
        MysqlDatabase db = new MysqlDatabase();
        System.out.println(db.getPasswordByUid("test1"));
        System.out.println(db.getUsrInfoByUid("test1").name);
        List<Message> ls = db.getRecentMessage("test1","test2");
        List<String> ls2 = db.getFriendsByUid("test1");
        System.out.println(ls.get(0).content);
        System.out.println(ls2.get(0));

        Scanner scanner = new Scanner(System.in);
    }
}

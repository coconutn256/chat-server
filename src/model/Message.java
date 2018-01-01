package model;

import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.Date;

public class Message {
    public String uid;
    public String targetId;
    public Timestamp dateTime;
    public int type;
    public String content;

    public Message(){

    }
}

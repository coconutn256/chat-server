package runable;

import model.Message;
import server.TCPServer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Start {
    public static List<String> onlineList = new ArrayList<>();
    public static Map<String,List<Message>> UnsendMessage = new HashMap<String,List<Message>>();
    public static void main(String[] args){
        while (true) {
            try {
                TCPServer tcpServer = new TCPServer();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
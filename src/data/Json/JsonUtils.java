package data.Json;

import model.Message;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class JsonUtils {
    public static Message JsonToMessage(JSONObject json) {
        Message message = new Message();
        try {
            message.uid = json.getString("uid");
            message.targetId = json.getString("targetId");
            message.content = json.getString("content");
            message.type = Integer.parseInt(json.getString("type"));
            message.dateTime = Timestamp.valueOf(json.getString("dateTime"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return message;
    }

    @Nullable
    public static JSONObject MessageToJson(Message message) {
        JSONObject json;
        try {
            Map<String, String> map = new HashMap<String, String>();
            map.put("uid", message.uid);
            map.put("targetId", message.targetId);
            map.put("content", message.content);
            map.put("type", String.valueOf(message.type));
            map.put("dateTime", message.dateTime.toString());
            json = new JSONObject(map);
            return json;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

package com.clover.remote.client.lib.example.model;

import com.clover.remote.client.messages.BaseResponse;
import com.clover.sdk.JSONifiable;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.Date;

public class POSMessage {
    public final static Gson gson;

    static {
        Gson plainGson = new Gson();
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        builder.serializeNulls();
        // The com.clover.remote.client messages don't jsonify nicely due to some cyclic recursion in the
        // com.clover.sdk.JSONifiable class that results in a stack overflow when gson tries to stringify the object
        // We resolve this by specifying a custom serializer that will properly serialize the problematic class
        builder.registerTypeHierarchyAdapter(JSONifiable.class, new JsonSerializer<JSONifiable>() {
            @Override
            public JsonElement serialize(JSONifiable src, Type typeOfSrc, JsonSerializationContext context) {
                // Proper serialization of the com.clover.sdk.JSONifiable class is achieved by stringifying the
                // org.json.JSONObject the class has, and then converting that string json to a com.google.gson.JsonObject
                return plainGson.fromJson(src.getJSONObject().toString(), JsonObject.class);
            }
        });
        gson = builder.create();
    }

    public enum MessageSrc {
        DEVICE,
        POS
    }
    public final MessageSrc src;
    public final Date time;
    private final Object message;
    private final String msgName;

    public POSMessage(Object msg) {
        this(msg.getClass().getSimpleName(), msg instanceof BaseResponse ? MessageSrc.DEVICE : MessageSrc.POS, msg);
    }
    public POSMessage(String msgName) {
        this(msgName, MessageSrc.POS, new Object());
    }
    public POSMessage(String msgName, MessageSrc src, Object msg) {
        this.time = Calendar.getInstance().getTime();
        this.src = src;
        this.message = msg;
        this.msgName = msgName;
    }

    @Override
    public String toString() {
        return msgName + gson.toJson(message);
    }
}

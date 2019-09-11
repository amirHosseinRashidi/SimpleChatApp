package com.example.chatapp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.Date;

public class SocketHelper {
    private static final String TAG = "SocketHelper";
    private ChatReceiveDataListeners chatReceiveDataListeners;
    private Socket socket;

    {
        try {
            socket = IO.socket("http://192.168.1.133:3000");
            Log.i(TAG, "instance initializer: " + "aa");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

    public SocketHelper(final Activity activity, final ChatReceiveDataListeners chatReceiveDataListeners) {
        socket.connect();

        this.chatReceiveDataListeners = chatReceiveDataListeners;


        socket.on("user-joined", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i(TAG, "call: " + args[0].toString());
                        onUserJoined((JSONObject) args[0]);
                    }
                });
            }
        });

        socket.on("typing", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onUserTyping((JSONObject) args[0]);
                    }
                });
            }
        });
        socket.on("stop-typing", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        chatReceiveDataListeners.onUserStopTyping();
                    }
                });
            }
        });

        socket.on("user-disconnected", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onUserDisconnected((JSONObject) args[0]);
                    }
                });
            }
        });

        socket.on("message", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onMessageReceived((JSONObject) args[0]);
                    }
                });
            }
        });


    }

    public void sendMessage(Message message) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("text", message.getText());
            jsonObject.put("user_name", message.getUserName());
            jsonObject.put("date", message.getDate());
            jsonObject.put("image", message.getImage());
            socket.emit("message", jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendUserJoined(String name) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_name", name);
            socket.emit("user-joined", jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendImageMessage(Message message) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("image", encodeImage(message.getImage()));
            jsonObject.put("user_name", message.getUserName());
            jsonObject.put("text", "");
            socket.emit("message", jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String encodeImage(String path) {
        File imagefile = new File(path);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(imagefile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Bitmap bm = BitmapFactory.decodeStream(fis);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encImage;
    }

    public void sendUserTyping(String name) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_name", name);
            socket.emit("user-typing", jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendUserStopTyping(String name) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_name", name);
            socket.emit("user-typing", jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendUserDisconnected(String name) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_name", name);
            socket.emit("user-disconnected", jsonObject);
            socket.disconnect();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void onMessageReceived(JSONObject jsonObject) {
        try {
            Date date = new Date();
            String text = jsonObject.getString("text");
            String userName = jsonObject.getString("user_name");
            String image = jsonObject.getString("image");
            chatReceiveDataListeners.onMessageReceived(new Message(text, userName, image));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void onUserJoined(JSONObject user) {
        try {
            String userName = user.getString("user_name");
            chatReceiveDataListeners.onUserJoined(userName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void onUserTyping(JSONObject user) {
        try {
            String userName = user.getString("user_name");
            chatReceiveDataListeners.onUserTyping(userName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void onUserDisconnected(JSONObject user) {
        try {
            String userName = user.getString("user_name");
            chatReceiveDataListeners.onUserDisconnected(userName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}

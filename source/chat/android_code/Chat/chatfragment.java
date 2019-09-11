package homeandroidappsite_map.mygooglemap.Chat;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

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
import java.util.ArrayList;
import java.util.List;

import homeandroidappsite_map.mygooglemap.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class chatfragment extends Fragment {
    private static final String TAG = "chatfragment";
    RecyclerView recyclerview;
    EditText Et_sendmsg;
    ImageView Ig_send,Ig_attachfile;
    String imageurlStroge;
    RecyclerView.Adapter adapter;
    List<Message> list=new ArrayList<>();
    List<String> listcheck=new ArrayList<>();
    recyclerview_adapter recyclerview_adapter;
    private Socket socket;
    {
        try {
            socket= IO.socket("http://dl.homeandroid.ir:3000");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }


    public chatfragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_chatfragment, container, false);
        getCast(view);
        return  view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        socket.connect();
        socket.on("message", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {

                try {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            JSONObject jsonObject=(JSONObject) args[0];
                            try {
                                Recivemessage(jsonObject.getString("text"),null);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                AddImage(decodeImage(jsonObject.getString("image")),null);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });
                }catch (NullPointerException e){
                    Log.e(TAG, "call: "+e.toString() );
                }

            }
        });
    }


    void Recivemessage(String msg,String usercheck){
        list.add(new Message.Builder(Message.type_message).message(msg).build());
        listcheck.add(usercheck);
        adapter=new recyclerview_adapter(list,listcheck);
        adapter.notifyItemInserted(adapter.getItemCount()-1);
        recyclerview.scrollToPosition(adapter.getItemCount()-1);
    }

    private void getCast(View view) {
        recyclerview=view.findViewById(R.id.recyclerview);
        Et_sendmsg=view.findViewById(R.id.Et_sendmsg);
        Ig_send=view.findViewById(R.id.Ig_send);
        Ig_attachfile=view.findViewById(R.id.Ig_attachfile);
        Setupview_recyclerview();

        Ig_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Et_sendmsg.getText().toString().isEmpty()) {
                    Sendmsg();
                }

            }
        });
        Ig_attachfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Getpermissionfile()){
                    GetgalleyOpen();
                }

            }
        });


    }

    boolean Getpermissionfile()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    1001);
           return  false;
        }
        else
        {
            return true;
        }
    }


    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1001) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                GetgalleyOpen();
            }
        }
    }

    void SendImage(String url)
    {
        JSONObject sendimage=new JSONObject();
        try {
            sendimage.put("image",encodeImage(url));
            Bitmap bitmap=decodeImage(sendimage.getString("image"));
            AddImage(bitmap,"me");
            socket.emit("message",sendimage);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void AddImage(Bitmap bitmap,String usercheck)
    {
        list.add(new Message.Builder(Message.type_message).message(bitmap).build());
        listcheck.add(usercheck);
        adapter=new recyclerview_adapter(list,listcheck);
        adapter.notifyItemInserted(adapter.getItemCount()-1);
        recyclerview.scrollToPosition(adapter.getItemCount()-1);
    }

    void GetgalleyOpen()
    {
        Intent intent=new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,100);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100 &&resultCode==Activity.RESULT_OK && data!=null ){
            Uri fileselectgalley=data.getData();
            String[] imageurl={MediaStore.Images.Media.DATA};
            Cursor cursor=getActivity().getContentResolver().query(fileselectgalley,imageurl,null,null,null);
            cursor.moveToFirst();
            int index=cursor.getColumnIndex(imageurl[0]);
            imageurlStroge=cursor.getString(index);
            cursor.close();
           SendImage(imageurlStroge);
        }

    }

    private String encodeImage(String path)
    {
        File imagefile = new File(path);
        FileInputStream fis = null;
        try{
            fis = new FileInputStream(imagefile);
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }
        Bitmap bm = BitmapFactory.decodeStream(fis);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encImage;

    }

    private Bitmap decodeImage(String data)
    {
        byte[] b = Base64.decode(data,Base64.DEFAULT);
        Bitmap bmp = BitmapFactory.decodeByteArray(b,0,b.length);
        return bmp;
    }


    void Setupview_recyclerview(){
        recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerview.setAdapter(adapter);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        adapter=new recyclerview_adapter(list,listcheck);
    }

    void Sendmsg()
    {
        String check=Et_sendmsg.getText().toString().trim();
        Et_sendmsg.setText("");
        Recivemessage(check,"me");
        JSONObject sendmsg=new JSONObject();
        try {
            sendmsg.put("text",check);
            socket.emit("message",sendmsg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        socket.disconnect();
    }
}

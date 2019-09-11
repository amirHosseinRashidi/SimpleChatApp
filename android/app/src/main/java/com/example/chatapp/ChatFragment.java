package com.example.chatapp;

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
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.api_service.RetroAdapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatFragment extends Fragment implements ChatReceiveDataListeners {
    private static final String TAG = "ChatFragment";
    private static final int SELECT_IMAGE = 10;
    private Context context;
    long last_text_edit = 0;


    private RecyclerView rvChats;
    private TextView tvIsTyping;
    private EditText etMessage;
    private Button btnSend;
    private ImageButton btnSelectImage;


    private String userName;
    private List<Message> messageList = new ArrayList<>();

    private ChatRvAdapter adapter;
    private SocketHelper socketHelper;
    private String imageUrlStroge;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userName = getArguments().getString("username");
        context = getContext();
        adapter = new ChatRvAdapter(context, messageList, userName);
        socketHelper = new SocketHelper(getActivity(), this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_IMAGE && resultCode == Activity.RESULT_OK) {
            try {

                Log.i("AAA", "onActivityResult: " + FileUtil.from(getContext(), data.getData()).getPath());
                imageUrlStroge = FileUtil.from(getContext(), data.getData()).getPath();

                socketHelper.sendImageMessage(new Message("", userName, imageUrlStroge));
                messageList.add(new Message("", userName, imageUrlStroge));
                adapter.notifyDataSetChanged();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chat, container, false);
        btnSend = v.findViewById(R.id.btn_send);
        etMessage = v.findViewById(R.id.et_message);
        rvChats = v.findViewById(R.id.rv_chats);
        btnSelectImage = v.findViewById(R.id.ib_chat_selectImage);
        tvIsTyping = v.findViewById(R.id.tv_userIsTyping);

        rvChats.setAdapter(adapter);
        rvChats.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, true));
        socketHelper.sendUserJoined(userName);
        getMessages();


        btnSelectImage.setOnClickListener(view -> {
            selectImageIntent();
        });

        btnSend.setOnClickListener(v1 -> {
            Date date = new Date();
            socketHelper.sendMessage(new Message(etMessage.getText().toString(), userName, date.getTime()));
            messageList.add(0, new Message(etMessage.getText().toString(), userName, date.getTime()));
            adapter.notifyDataSetChanged();
            etMessage.setText("");
        });
        getUserTyping();
        return v;
    }

    boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                getActivity().requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, SELECT_IMAGE);
            }
        } else {
            return true;
        }
        return false;
    }

    private void selectImageIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select an image"), SELECT_IMAGE

        );
    }

    private void getMessages() {
        RetroAdapter.getApiService().getMessagesList().enqueue(new Callback<ArrayList<Message>>() {
            @Override
            public void onResponse(Call<ArrayList<Message>> call, Response<ArrayList<Message>> response) {
                if (response.body() == null) {
                    Log.i(TAG, "onResponse: " + response.message());
                    return;
                }
                messageList.clear();
                messageList.addAll(response.body());
                Collections.reverse(messageList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<ArrayList<Message>> call, Throwable t) {

            }
        });
    }


    @Override
    public void onUserJoined(final String name) {

        getActivity().runOnUiThread(() -> Toast.makeText(context, name + " joined chat", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onUserDisconnected(String name) {
        Toast.makeText(context, name + " disconnected", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUserTyping(String user) {
        tvIsTyping.setText(user + "is typing . . .");
        tvIsTyping.setVisibility(View.VISIBLE);
    }

    @Override
    public void onUserStopTyping() {
        tvIsTyping.setVisibility(View.GONE);
    }

    @Override
    public void onMessageReceived(Message message) {
        messageList.add(0, message);
        adapter.notifyDataSetChanged();
    }


    void getUserTyping() {
        final long delay = 1000; // 1 seconds after user stops typing
        final Handler handler = new Handler();

        final Runnable input_finish_checker = new Runnable() {
            public void run() {
                if (System.currentTimeMillis() > (last_text_edit + delay - 500)) {
                    socketHelper.sendUserTyping(userName);
                }
                socketHelper.sendUserStopTyping(userName);
            }
        };

        etMessage.addTextChangedListener(new TextWatcher() {
                                             @Override
                                             public void beforeTextChanged(CharSequence s, int start, int count,
                                                                           int after) {
                                             }

                                             @Override
                                             public void onTextChanged(final CharSequence s, int start, int before,
                                                                       int count) {
                                                 //You need to remove this to run only once
                                                 handler.removeCallbacks(input_finish_checker);
                                             }

                                             @Override
                                             public void afterTextChanged(final Editable s) {
                                                 //avoid triggering event when text is empty
                                                 if (s.length() > 0) {
                                                     last_text_edit = System.currentTimeMillis();
                                                     handler.postDelayed(input_finish_checker, delay);
                                                 } else {

                                                 }
                                             }
                                         }

        );
    }

    @Override
    public void onDetach() {
        super.onDetach();
        socketHelper.sendUserDisconnected(userName);
    }
}

package com.example.chatapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class ChatRvAdapter extends RecyclerView.Adapter<ChatRvAdapter.ViewHolder> {

    private Context context;
    private List<Message> messageList;
    String userName;

    public ChatRvAdapter(Context context, List<Message> messageList, String userName) {
        this.context = context;
        this.messageList = messageList;
        this.userName = userName;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_chat, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.message.setText(messageList.get(position).getUserName() + ": " + messageList.get(position).getText());


        if (messageList.get(position).getImage().equals("")) {
            holder.image.setVisibility(View.GONE);
        } else {
            holder.image.setVisibility(View.VISIBLE);
            holder.message.setVisibility(View.GONE);

            Bitmap bitmap = decodeImage(messageList.get(position).getImage());


            holder.image.setImageBitmap(bitmap);
        }

        if (messageList.get(position).getUserName().equals(userName)) {
            holder.message.setBackgroundColor(context.getResources().getColor(R.color.colorMessageSender));
            holder.message.setTextColor(context.getResources().getColor(android.R.color.white));
            holder.container.setGravity(Gravity.RIGHT);
        } else {
            holder.message.setBackgroundColor(context.getResources().getColor(android.R.color.white));
            holder.message.setTextColor(context.getResources().getColor(R.color.colorText));
            holder.container.setGravity(Gravity.LEFT);
        }

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+4:30"));
        calendar.setTimeInMillis(messageList.get(position).getDate());
        holder.date.setText(calendar.get(Calendar.YEAR) + "/" + calendar.get(Calendar.MONTH) + "/" + calendar.get(Calendar.DAY_OF_MONTH) + "  " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE));
    }

    private Bitmap decodeImage(String data) {
        byte[] b = Base64.decode(data, Base64.DEFAULT);
        Bitmap bmp = BitmapFactory.decodeByteArray(b, 0, b.length);

        return bmp;
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView message, date;
        LinearLayout container;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.tv_iChat_message);
            image = itemView.findViewById(R.id.iv_iChat_image);
            date = itemView.findViewById(R.id.tv_iChat_date);
            container = itemView.findViewById(R.id.ll_chat_container);
        }
    }
}

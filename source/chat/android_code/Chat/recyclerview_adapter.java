package homeandroidappsite_map.mygooglemap.Chat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import homeandroidappsite_map.mygooglemap.R;

public class recyclerview_adapter extends RecyclerView.Adapter<recyclerview_adapter.viewholder> {
    private static final String TAG = "recyclerview_adapter";
    List<Message> list;
    List<String> listcheck;
    Context context;

    public recyclerview_adapter(List<Message> list, List<String> listcheck) {
        this.list = list;
        this.listcheck = listcheck;

    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.items,parent,false);
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {
        final Message message=list.get(position);
        if(listcheck.get(position)=="me"){

            if(message.getBitmapimage()==null) {
                ViewGroup.MarginLayoutParams marginLayoutParams =
                        (ViewGroup.MarginLayoutParams) holder.linearLayout().getLayoutParams();
                marginLayoutParams.setMargins(100, 5, 5, 5);
                holder.linearLayout().setLayoutParams(marginLayoutParams);
                holder.Set_Message(message.getMsg());
                holder.linearLayout2().setBackgroundResource(R.drawable.shape_green);
            }
            else
            {
                LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(500,500);
                holder.liner2.setLayoutParams(layoutParams);
            }

            holder.linearLayout().setGravity(Gravity.RIGHT);
            holder.Setimage(message.getBitmapimage());

        }else {
            if(message.getBitmapimage()==null) {
                ViewGroup.MarginLayoutParams marginLayoutParams =
                        (ViewGroup.MarginLayoutParams) holder.linearLayout().getLayoutParams();
                marginLayoutParams.setMargins(5, 5, 100, 5);
                holder.linearLayout().setLayoutParams(marginLayoutParams);
                holder.Set_Message(message.getMsg());
                holder.linearLayout2().setBackgroundResource(R.drawable.shape_white);
            }
            else
            {
                LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(500,500);
                holder.liner2.setLayoutParams(layoutParams);
                holder.Setimage(message.getBitmapimage());
            }
            holder.linearLayout().setGravity(Gravity.LEFT);


        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    class viewholder extends RecyclerView.ViewHolder{
       LinearLayout liner,liner2;
       TextView Tv_chat;
       ImageView Im_post;
        public viewholder(View view) {
            super(view);
            liner=view.findViewById(R.id.liner);
            liner2=view.findViewById(R.id.liner2);
            Tv_chat=view.findViewById(R.id.Tv_chat);
            Im_post=view.findViewById(R.id.Im_post);

        }

        public void Set_Message(String str){
           if(str==null)return;
            Tv_chat.setText(str);
        }
        public void Setimage(Bitmap bitmap)
        {
            Im_post.setImageBitmap(bitmap);
        }
        public LinearLayout linearLayout2(){
            return liner2;
        }

        public LinearLayout linearLayout(){
            return liner;
        }
    }
}

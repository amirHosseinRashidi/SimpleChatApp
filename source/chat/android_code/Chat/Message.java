package homeandroidappsite_map.mygooglemap.Chat;

import android.graphics.Bitmap;

public class Message {

    static final int type_message=0;
    String msg;
    int mtype;
    Bitmap bitmapimage;

    public int getMtype() {
        return mtype;
    }

    public String getMsg() {
        return msg;
    }

    public Bitmap getBitmapimage() {
        return bitmapimage;
    }

    public static class Builder
    {
       private final int Mtype;
        String msg;
        Bitmap bitmapimage;

        public Builder(int type){
            this.Mtype=type;
        }
        public Builder message(String s)
        {
           this.msg=s;
           return this;
        }
        public Builder message(Bitmap bitmap)
        {
            this.bitmapimage=bitmap;
            return this;
        }

        public Message build()
        {
            Message message=new Message();
            message.msg=msg;
            message.mtype=Mtype;
            message.bitmapimage=bitmapimage;
            return message;
        }

    }


}

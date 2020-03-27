package co.uk.depotnet.onsa.modals;

import android.text.TextUtils;

public class MyModel {
    String name;
    String img;
    String time;
    String comment;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isGreaterThan(int inTime){
        if(TextUtils.isEmpty(time)){
            return false;
        }
try {
    String t = time.split(",")[1].split(":")[0].trim();

    int tim = Integer.parseInt(t);
    return tim >= inTime;
}catch (Exception e){
            return true;
}
    }

    public boolean isLessThan(int inTime){
        if(TextUtils.isEmpty(time)){
            return false;
        }

        String t= time.split(",")[1].split(":")[0].trim();

        int tim = Integer.parseInt(t);
        return  tim <= inTime;
    }

    public boolean containsTime(String date){
        if(TextUtils.isEmpty(time)){
            return false;
        }


        return  time.contains(date);
    }

    public String getComment() {
        return comment;
    }

    public boolean containsComment(String text){
        if(TextUtils.isEmpty(comment)){
            return false;
        }


        return  comment.contains(text);
    }

    public boolean containsName(String text){
        if(TextUtils.isEmpty(name)){
            return false;
        }


        return  name.contains(text);
    }



    @Override
    public String toString() {
        return name+"--"+comment+"--"+time;
    }
}

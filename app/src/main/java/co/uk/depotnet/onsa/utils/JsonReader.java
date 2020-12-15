package co.uk.depotnet.onsa.utils;

import android.content.Context;
import androidx.annotation.NonNull;

import com.google.gson.Gson;

import co.uk.depotnet.onsa.modals.forms.Amends;
import co.uk.depotnet.onsa.modals.forms.Form;
import co.uk.depotnet.onsa.modals.forms.FormItem;
import co.uk.depotnet.onsa.modals.forms.Photo;
import co.uk.depotnet.onsa.modals.forms.Screen;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class JsonReader {

    @NonNull
    public static <T> ArrayList<T> loadFormJSON(Context context,
                                                Class<T> typeClass,
                                                String fileName) {


        String json = null;
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
            Gson gson;
            gson = new Gson();
            Type type = new ListOfJson<T>(typeClass);
            return gson.fromJson(json, type);

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    @NonNull
    public static Form loadForm(Context context,
                                  String fileName) {

        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");


            Gson gson;
            gson = new Gson();
            Type type = Form.class;
            Form form = gson.fromJson(json, type);

            initForm(form);

            return form;

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return null;
    }


    private static void initForm(Form form){
        if(form == null){
            return;
        }

        ArrayList<Screen> screens = form.getScreens();

        if(screens != null && !screens.isEmpty()){
            for (Screen s: screens) {
                initScreen(s);
            }
        }


    }

    private static void initScreen(Screen screen){
        if(screen == null){
            return;
        }
        ArrayList<FormItem> formItems = screen.getItems();

        if(formItems != null && !formItems.isEmpty()){
            for (FormItem fi: formItems) {
                initFormItem(fi);
            }
        }
    }

    private static void initFormItem(FormItem formItem){
        if(formItem == null){
            return;
        }

        if(formItem.getFormType() == FormItem.TYPE_PHOTO || formItem.getFormType() == FormItem.TYPE_TAKE_PHOTO){
            ArrayList<Photo> photos = new ArrayList<>(formItem.getPhotoSize());
            String photoId = formItem.getPhotoId();
            int photoRequired = formItem.getPhotoRequired();
            int photoSize = formItem.getPhotoSize();
            String title = formItem.getTitle();

            for(int i = 0 ; i < photoSize ; i++){
                Photo photo = new Photo();
                photo.setPhoto_id(photoId);
                photo.setTitle(title+" "+(i+1));
                photo.setOptional(photoRequired <= i);
                photos.add(photo);
            }

            formItem.setPhotos(photos);
        }

        ArrayList<FormItem> enables = formItem.getEnables();
        ArrayList<FormItem> falseEnables = formItem.getFalseEnables();
        ArrayList<FormItem> naEnables = formItem.getNaEnables();
        ArrayList<FormItem> dialogItems = formItem.getDialogItems();

        if(enables != null && !enables.isEmpty()){
            for (FormItem fi: enables) {
                initFormItem(fi);
            }
        }

        if(falseEnables != null && !falseEnables.isEmpty()){
            for (FormItem fi: falseEnables) {
                initFormItem(fi);
            }
        }

        if(naEnables != null && !naEnables.isEmpty()){
            for (FormItem fi: naEnables) {
                initFormItem(fi);
            }
        }

        if(dialogItems != null && !dialogItems.isEmpty()){
            for (FormItem fi: dialogItems) {
                initFormItem(fi);
            }
        }
    }

    @NonNull
    public static Screen loadScreen(Context context,
                                  String fileName) {
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");
            Gson gson;
            gson = new Gson();
            return gson.fromJson(json, Screen.class);

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return null;
    }


    @NonNull
    public static Amends loadAmends(Context context,
                                    String fileName) {
        String json = null;
        Amends amends = null;
        ArrayList<FormItem> formItems;
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
            Gson gson;
            gson = new Gson();
// Type type = new ListOfJson<>(typeClass);
            Type type = Amends.class;
            amends = gson.fromJson(json, type);
            formItems = amends.getDialogItems();
            for (FormItem formItem :formItems)
            {
                initFormItem(formItem);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return amends;
    }

}

package com.xcomp.livestreamlamp.Entity;

import com.xcomp.livestreamlamp.Utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class SessionModel {
    private String session_id;
    private String start_time;
    private String end_time;
    private String session_infor;
    private int page_count;
    private ArrayList<ImageModel> imageModels = null;

    public SessionModel(String session_id, String start_time, String end_time, String session_info, int page_count) {
        this.session_id = session_id;
        this.start_time = start_time;
        this.end_time = end_time;
        this.session_infor = session_info;
        this.page_count = page_count;
    }

    public String getStart_time() {
        return start_time;
    }
    public String getEnd_time() {
        return end_time;
    }
    public String getSession_infor() {
        return session_infor;
    }
    public int getPage_count() {
        return page_count;
    }
    public void setImageModels(ArrayList<ImageModel> imageModels) {
        this.imageModels = imageModels;
    }
    public ArrayList<ImageModel> getImageModels() {
        return imageModels;
    }
    public int getImageModelsCount() {
        if(imageModels == null) {
            return 0;
        }
        else {
            return imageModels.size();
        }
    }

    public static SessionModel createImageModelFromJsonObject(JSONObject userData)  {
            SessionModel sessionModel = new SessionModel(
                    Utils.getStringFromJSonObject(userData,"_id"),
                    Utils.formatDate(Utils.getStringFromJSonObject(userData,"start_moment")),
                    Utils.formatDate(Utils.getStringFromJSonObject(userData,"end_moment")),
                    Utils.getStringFromJSonObject(userData, "session_info"),
                    Utils.getIntFromJSonObject(userData, "page_count")
            );
            try {
                JSONArray imageModels = userData.getJSONArray("image_models");
                if (imageModels != null) {
                    ArrayList<ImageModel> listImageModel = new ArrayList<>(0);
                    for (int i=0; i < imageModels.length(); i++ ){
                        JSONObject jsonObject = imageModels.getJSONObject(i);
                        ImageModel imageModel = ImageModel.createImageModelFromJsonObject(jsonObject);
                        listImageModel.add(imageModel);
                    }
                    sessionModel.setImageModels(listImageModel);
                }
            }
            catch (Exception e) { }
            return sessionModel;
    }
}

package com.xcomp.livestreamlamp.Entity;

import com.xcomp.livestreamlamp.Utils.Utils;

import org.json.JSONObject;

public class ImageModel {
    private String imageName;
    private String createdDate;

    public ImageModel(String imageName, String createdDate) {
        this.imageName = imageName;
        this.createdDate = createdDate;
    }

    public String getImageName() {
        return imageName;
    }
    public String getCreatedDate() {
        return createdDate;
    }

    public static ImageModel createImageModelFromJsonObject(JSONObject userData)  {
            ImageModel userInfo = new ImageModel(
                    Utils.getStringFromJSonObject(userData,"name"),
                    Utils.formatDate(Utils.getStringFromJSonObject(userData,"Created_date"))
            );
            return userInfo;
    }
}

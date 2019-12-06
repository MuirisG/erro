package com.example.erro.API;

import java.io.Serializable;

public class GetMessageForm implements Serializable {

    private String code;
    private String zone;
    private String directorate;
    private String surface;
    private String area;
    private String locationX;
    private String locationY;
    private String gmail;

    public GetMessageForm(
            String code,
            String zone,
            String directorate,
            String surface,
            String area,
            String locationX,
            String locationY,
            String gmail) {
        this.code = code;
        this.zone = zone;
        this.directorate = directorate;
        this.surface = surface;
        this.area = area;
        this.locationX = locationX;
        this.locationY = locationY;
        this.gmail = gmail;
    }

    public String getCode() {
        return code;
    }

    public String getZone() {
        return zone;
    }

    public String getDirectorate() {
        return directorate;
    }

    public String getSurface() {
        return surface;
    }

    public String getArea() {
        return area;
    }

    public String getLocationX() {
        return locationX;
    }

    public String getLocationY() {
        return locationY;
    }

    public String getGmail() {
        return gmail;
    }

}

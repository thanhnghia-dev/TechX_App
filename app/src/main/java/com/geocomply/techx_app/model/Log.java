package com.geocomply.techx_app.model;

import java.io.Serializable;

public class Log implements Serializable {
    private int id;
    private int userId;
    private String level;
    private String ip;
    private String source;
    private String content;
    private int status;

    public static String INFO = "INFO";
    public static String ALERT = "ALERT";
    public static String WARNING = "WARNING";
    public static String DANGER = "DANGER";
    public static int SUCCESS = 0;
    public static int FAILED = 1;

    public Log() {
    }

    public Log(int userId, String level, String ip, String source, String content, int status) {
        this.userId = userId;
        this.level = level;
        this.ip = ip;
        this.source = source;
        this.content = content;
        this.status = status;
    }

    public Log(String level, String ip, String source, String content, int status) {
        this.level = level;
        this.ip = ip;
        this.source = source;
        this.content = content;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

}

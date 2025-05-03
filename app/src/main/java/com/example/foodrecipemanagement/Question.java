package com.example.foodrecipemanagement;

public class Question {
    private int id;
    private String content;
    private String userName;
    private int communityId;

    public Question(int id, String content, String userName, int communityId) {
        this.id = id;
        this.content = content;
        this.userName = userName;
        this.communityId = communityId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public String getUserName() {
        return userName;
    }

    public int getCommunityId() {
        return communityId;
    }

    public void setContent(String content) { this.content = content; }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
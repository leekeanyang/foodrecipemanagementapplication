package com.example.foodrecipemanagement;

public class Reply {
    private int id;
    private String content;
    private String userName;
    private int questionId;

    public Reply(int id, String content, String userName, int questionId) {
        this.id = id;
        this.content = content;
        this.userName = userName;
        this.questionId = questionId;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public int getQuestionId() { return questionId; }
}
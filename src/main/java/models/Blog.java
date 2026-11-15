package models;

import java.time.LocalDateTime;

public class Blog {
    private final int blogId;
    private final String title;
    private final String content;
    private final LocalDateTime createdAt;

    public Blog(int blogId, String title, String content, LocalDateTime createdAt) {
        this.blogId = blogId; this.title = title; this.content = content; this.createdAt = createdAt;
    }
    public int getBlogId() { return blogId; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    @Override 
    public String toString() { return title + "  ·  " + (createdAt == null ? "" : createdAt.toLocalDate()); }
}

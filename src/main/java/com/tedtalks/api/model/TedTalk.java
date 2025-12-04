package com.tedtalks.api.model;

public class TedTalk {
    private String title;
    private String author;
    private String date;
    private long views;
    private long likes;
    private String link;

    public TedTalk() {}

    public TedTalk(String title, String author, String date, long views, long likes, String link) {
        this.title = title;
        this.author = author;
        this.date = date;
        this.views = views;
        this.likes = likes;
        this.link = link;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public long getViews() { return views; }
    public void setViews(long views) { this.views = views; }

    public long getLikes() { return likes; }
    public void setLikes(long likes) { this.likes = likes; }

    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }

    @Override
    public String toString() {
        return "TedTalk{" +
                "title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", date='" + date + '\'' +
                ", views=" + views +
                ", likes=" + likes +
                ", link='" + link + '\'' +
                '}';
    }
}

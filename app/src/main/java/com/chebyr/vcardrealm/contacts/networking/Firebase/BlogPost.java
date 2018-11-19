package com.chebyr.vcardrealm.contacts.networking.Firebase;

/**
 * Created by Murloc Nightcrawler on 1/29/2016.
 */
public class BlogPost {
    private String author;
    private String title;
    public BlogPost() {
        // empty default constructor, necessary for Firebase to be able to deserialize blog posts
    }
    public String getAuthor() {
        return author;
    }
    public String getTitle() {
        return title;
    }
}
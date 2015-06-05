package io.wallfly.lockdapp.models;

/**
 * Created by JoshuaWilliams on 6/4/15.
 *
 * @version 1.0
 *
 * This class is for libraries that we use in Lockd.
 */
public class Library {
    private String title;
    private String author;
    private String description;
    private String link;

    public Library(String title, String author, String description, String link){
        setTitle(title);
        setAuthor(author);
        setDescription(description);
        setLink(link);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}

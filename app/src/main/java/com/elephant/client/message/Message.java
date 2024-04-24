package com.elephant.client.message;


//@Data
//@ToString
//@AllArgsConstructor
public class Message {
    private Integer id;
    private String str;
    private String author;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", str='" + str + '\'' +
                ", author='" + author + '\'' +
                '}';
    }

    public Message(Integer id, String str, String author) {
        this.id = id;
        this.str = str;
        this.author = author;
    }

    public Message() {
    }
}
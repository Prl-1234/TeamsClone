package com.example.teamsclone;

public class Chat_View {
    private String name1;
    private String photo;

    public Chat_View(String name1, String photo) {
        this.name1 = name1;
        this.photo = photo;
    }
    public Chat_View( ){
    }

    public String getName1() {
        return name1;
    }

    public void setName1(String name1) {
        this.name1 = name1;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    @Override
    public String toString() {
        return "Chat_View{" +
                "name1='" + name1 + '\'' +
                ", photo='" + photo + '\'' +
                '}';
    }
}

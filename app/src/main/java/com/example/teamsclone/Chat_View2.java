package com.example.teamsclone;

public class Chat_View2 {
    private String name2;
    private String photo;

    public Chat_View2(String name2, String photo) {
        this.name2 = name2;
        this.photo = photo;
    }

    public Chat_View2() {
    }

    public String getName2() {
        return name2;
    }

    public void setName2(String name2) {
        this.name2 = name2;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    @Override
    public String toString() {
        return "Chat_View2{" +
                "name2='" + name2 + '\'' +
                ", photo='" + photo + '\'' +
                '}';
    }
}

package com.example.jeran.splittr.helper;

/**
 * Created by Abhi on 19-Mar-18.
 */

public class FriendListViewDataModel {
    private String friendPic;
    private String name;
    private String friendEmail;

    public FriendListViewDataModel(String friendPic, String name, String friendEmail)
    {
        this.friendPic = friendPic;
        this.name = name;
        this.friendEmail = friendEmail;
    }

    public String getFriendPic() {
        return friendPic;
    }

    public String getname() {
        return name;
    }

    public String getFriendEmail() {
        return friendEmail;
    }
}

package com.example.jeran.splittr.helper;

public class LinkUtils {
    // This URL changes based on where your server is
    // For AWS server it will be the link to your AWS EC2 instance
    // For localhost it will be your IPv4 address.
    // AWS EC2 Server
    private static String wsLink = "http://18.221.6.228/splittr/ws/";

    public static final String REGISTRATION_URL = wsLink + "registration.php";
    public static final String LOGIN_URL = wsLink + "login.php";
    public static final String LIST_USERS_URL = wsLink + "listUsers.php";
    public static final String ADD_FRIENDS_URL = wsLink + "addFriend.php";
    public static final String LIST_FRIENDS_URL = wsLink + "listFriends.php";
    public static final String ADD_ITEM_URL = wsLink + "addItem.php";
    public static final String GET_SUMMARY_URL = wsLink + "getSummary.php";
    public static final String SETTLE_UP_URL = wsLink + "settleUp.php";
    public static final String SAVE_GCM_TOKEN_URL = wsLink + "saveGcmToken.php";
    public static final String DELETE_TOKEN_URL = wsLink + "deleteGcmToken.php";
}

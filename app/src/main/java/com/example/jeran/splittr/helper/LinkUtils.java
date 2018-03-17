package com.example.jeran.splittr.helper;

public class LinkUtils {

    // This URL changes based on where your server is
    // For AWS server it will be the link to your AWS EC2 instance
    // For localhost it will be your IPv4 address.
//    private static String wsLink = "http://192.168.67.2/workspace/splittr/ws/";
//    private static String wsLink = "http://192.168.67.2/workspace/splittr/ws/";
//    private static String wsLink = "http://172.20.116.218/workspace/splittr/ws/";
    private static String wsLink = "http://10.0.0.207/workspace/splittr/ws/";
    public static final String REGISTRATION_URL = wsLink + "registration.php";
    public static final String LOGIN_URL = wsLink + "login.php";
    public static final String LIST_USERS_URL = wsLink + "listUsers.php";
    public static final String ADD_FRIENDS_URL = wsLink + "addFriend.php";
    public static final String LIST_FRIENDS_URL = wsLink + "listFriends.php";
    public static final String ADD_ITEM_URL = wsLink + "addItem.php";
    public static final String GET_SUMMARY_URL = wsLink + "getSummary.php";
    public static final String SETTLE_UP_URL = wsLink + "settleUp.php";
    public static final String SAVE_GCM_TOKEN_URL = wsLink + "saveGcmToken.php";;
}

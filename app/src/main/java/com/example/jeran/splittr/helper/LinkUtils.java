package com.example.jeran.splittr.helper;

/**
 * Created by dmevada on 3/15/18.
 */

public class LinkUtils {

    // This URL changes based on where your server is
    // For AWS server it will be the link to your AWS EC2 instance
    // For localhost it will be your IPv4 address.
//    private static String wsLink = "http://192.168.67.2/workspace/splittr/ws/";
//    private static String wsLink = "http://192.168.67.2/workspace/splittr/ws/";
    private static String wsLink = "http://172.20.116.218/workspace/splittr/ws/";
    public static final String gcmToken = "fcg5P2VOikI:APA91bGU8jWyQlBLBDHiOwgfcqje32ixECx6I-aZBGTLvJrus2qEoelmNf_-aCnKTod03l7l30qUCkERgSYYOvu20Epi987yD5iKwIygAgDtxgPncOfcxQRA0XF_5N8pUR9LVnAy0aLG";

    public static final String REGISTRATION_URL = wsLink + "registration.php";
    public static final String LOGIN_URL = wsLink + "login.php";
    public static final String LIST_USERS_URL = wsLink + "listUsers.php";

}

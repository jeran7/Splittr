package com.example.jeran.splittr.helper;

/**
 * Created by dmevada on 3/15/18.
 */

public class LinkUtils {

    // This URL changes based on where your server is
    // For AWS server it will be the link to your AWS EC2 instance
    // For localhost it will be your IPv4 address.
    private static String wsLink = "http://192.168.67.2/workspace/splittr/ws/";
    public static final String REGISTRATION_URL = wsLink + "registration.php";
}

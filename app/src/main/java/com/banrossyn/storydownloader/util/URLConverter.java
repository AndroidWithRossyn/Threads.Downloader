package com.banrossyn.storydownloader.util;

import android.util.Log;

import java.util.Arrays;

public class URLConverter {

    public static String convertURL(String url) {
        try {
            Log.d("downloadflow", "orgurl: " + url);
            // Extract the thread ID from the URL
            String lastId = extractIDFromURL(url);

            Log.d("downloadflow", "url after extract: " + url);

            // Check if both threadId and igshid are available
            if (lastId != null && !lastId.contains("@")) {
                // Construct the new URL
                return "https://www.threads.net/t/" + lastId + "/";
            } else if (lastId != null && lastId.contains("@")) {
                // Construct the new URL
                return "https://www.threads.net/" + lastId;
            } else {
                // Handle the case where threadId or igshid couldn't be extracted
                // You can throw an exception, log a message, or return null, depending on your use case.
                // For simplicity, I'm returning an empty string here.
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String extractIDFromURL(String url) {
        String id = "";

        if (url != null) {
            if (url.contains("/post/")) {
                String[] parts = url.split("/post/");
                if (parts.length > 1) {
                    String secondPart = parts[1];
                    int endIndex = secondPart.indexOf('/');
                    if (endIndex != -1) {
                        id = secondPart.substring(0, endIndex);
                    } else {
                        id = secondPart;
                    }
                    Log.d("downloadflow", "url has /post/ :" + id);
                }
            } else if (url.contains("/t/")) {
                String[] parts = url.split("/t/");
                if (parts.length > 1) {
                    String secondPart = parts[1];
                    int endIndex = secondPart.indexOf('?');
                    if (endIndex != -1) {
                        id = secondPart.substring(0, endIndex);
                    } else {
                        id = secondPart;
                    }
                    Log.d("downloadflow", "url has /t/ :" + id);
                }
            } else if (url.contains("/@")) {
                String[] parts = url.split("/@");

                if (parts.length > 1) {
                    String secondPart = parts[1];
                    int endIndex = secondPart.indexOf('/');
                    if (endIndex != -1) {
                        id = "@" + secondPart.substring(0, endIndex);
                    } else {
                        id = "@" + secondPart;
                    }
                }
            }
        }

        return id;
    }

}
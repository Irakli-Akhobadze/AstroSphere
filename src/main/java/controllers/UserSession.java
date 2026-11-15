package controllers;

public class UserSession {
    private static int currentUserId = -1;
    private static String currentUserName = "";

    public static void setUser(int userId, String userName) {
        currentUserId = userId;
        currentUserName = userName == null ? "" : userName;
    }

    public static int getCurrentUserId() { return currentUserId; }
    public static String getCurrentUserName() { return currentUserName; }

    public static void clear() {
        currentUserId = -1;
        currentUserName = "";
    }
}

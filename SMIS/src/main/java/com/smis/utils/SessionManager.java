package com.smis.utils;

import com.smis.models.User;

/**
 * Holds the currently logged-in user for the entire session.
 * Access from anywhere: SessionManager.getCurrentUser()
 */
public class SessionManager {
    private static User currentUser = null;

    public static void setCurrentUser(User user) { currentUser = user; }
    public static User getCurrentUser() { return currentUser; }
    public static void clearSession() { currentUser = null; }
    public static boolean isLoggedIn() { return currentUser != null; }
}

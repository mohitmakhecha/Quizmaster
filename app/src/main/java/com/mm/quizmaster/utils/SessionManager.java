package com.mm.quizmaster.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.mm.quizmaster.constants.AppConstants;

/**
 * Session Manager Class
 * Manages user session using SharedPreferences
 */
public class SessionManager {
    
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(AppConstants.PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    /**
     * Create login session
     */
    public void createLoginSession(int userId, String userName, String userEmail, String userRole) {
        editor.putBoolean(AppConstants.KEY_IS_LOGGED_IN, true);
        editor.putInt(AppConstants.KEY_USER_ID, userId);
        editor.putString(AppConstants.KEY_USER_NAME, userName);
        editor.putString(AppConstants.KEY_USER_EMAIL, userEmail);
        editor.putString(AppConstants.KEY_USER_ROLE, userRole);
        editor.apply();
    }

    /**
     * Check if user is logged in
     */
    public boolean isLoggedIn() {
        return prefs.getBoolean(AppConstants.KEY_IS_LOGGED_IN, false);
    }

    /**
     * Get user ID
     */
    public int getUserId() {
        return prefs.getInt(AppConstants.KEY_USER_ID, -1);
    }

    /**
     * Get user name
     */
    public String getUserName() {
        return prefs.getString(AppConstants.KEY_USER_NAME, "");
    }

    /**
     * Get user email
     */
    public String getUserEmail() {
        return prefs.getString(AppConstants.KEY_USER_EMAIL, "");
    }

    /**
     * Get user role
     */
    public String getUserRole() {
        return prefs.getString(AppConstants.KEY_USER_ROLE, "");
    }

    /**
     * Clear session (logout)
     */
    public void logout() {
        editor.clear();
        editor.apply();
    }
}

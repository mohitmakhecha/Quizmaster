package com.mm.quizmaster.constants;

/**
 * Application-wide constants
 * Contains all constant values used throughout the app
 */
public class AppConstants {
    
    // Super Admin Credentials (Hardcoded)
    public static final String SUPER_ADMIN_EMAIL = "mohit@admin.com";
    public static final String SUPER_ADMIN_PASSWORD = "mohit@123";
    
    // Quiz Configuration
    public static final int QUESTIONS_PER_QUIZ = 10;
    public static final int SECONDS_PER_QUESTION = 30;
    public static final int TOTAL_QUIZ_TIME_MINUTES = 5;
    public static final int MARKS_PER_QUESTION = 1;
    public static final int PASSING_PERCENTAGE = 40;
    
    // Difficulty Levels
    public static final String LEVEL_EASY = "Easy";
    public static final String LEVEL_MEDIUM = "Medium";
    public static final String LEVEL_HARD = "Hard";
    
    // User Roles
    public static final String ROLE_STUDENT = "student";
    public static final String ROLE_ADMIN = "admin";
    public static final String ROLE_SUPER_ADMIN = "super_admin";
    
    // Pass/Fail Status
    public static final String STATUS_PASS = "Pass";
    public static final String STATUS_FAIL = "Fail";
    
    // Answer Options
    public static final String OPTION_A = "A";
    public static final String OPTION_B = "B";
    public static final String OPTION_C = "C";
    public static final String OPTION_D = "D";
    
    // SharedPreferences Keys
    public static final String PREF_NAME = "QuizMasterPrefs";
    public static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    public static final String KEY_USER_ID = "userId";
    public static final String KEY_USER_NAME = "userName";
    public static final String KEY_USER_EMAIL = "userEmail";
    public static final String KEY_USER_ROLE = "userRole";
    
    // Certificate Directory
    public static final String CERTIFICATE_DIR = "QuizMaster/Certificates";
    
    // Validation
    public static final int MIN_NAME_LENGTH = 3;
    public static final int MIN_PASSWORD_LENGTH = 6;
    public static final int MIN_QUESTION_LENGTH = 10;
    
    // Semesters
    public static final int TOTAL_SEMESTERS = 6;
}

package com.mm.quizmaster.database;

import android.provider.BaseColumns;

/**
 * Database Contract Class
 * Defines table and column names for the database schema
 */
public final class DatabaseContract {
    
    // Private constructor to prevent instantiation
    private DatabaseContract() {}
    
    /* Users Table */
    public static class UserEntry implements BaseColumns {
        public static final String TABLE_NAME = "users";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_EMAIL = "email";
        public static final String COLUMN_PASSWORD = "password";
        public static final String COLUMN_CREATED_AT = "created_at";
    }
    
    /* Admins Table */
    public static class AdminEntry implements BaseColumns {
        public static final String TABLE_NAME = "admins";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_EMAIL = "email";
        public static final String COLUMN_PASSWORD = "password";
        public static final String COLUMN_CREATED_BY = "created_by";
        public static final String COLUMN_CREATED_AT = "created_at";
    }
    
    /* Subjects Table */
    public static class SubjectEntry implements BaseColumns {
        public static final String TABLE_NAME = "subjects";
        public static final String COLUMN_SUBJECT_NAME = "subject_name";
        public static final String COLUMN_SEMESTER = "semester";
        public static final String COLUMN_CREATED_AT = "created_at";
    }
    
    /* Questions Table */
    public static class QuestionEntry implements BaseColumns {
        public static final String TABLE_NAME = "questions";
        public static final String COLUMN_QUESTION_TEXT = "question_text";
        public static final String COLUMN_OPTION_A = "option_a";
        public static final String COLUMN_OPTION_B = "option_b";
        public static final String COLUMN_OPTION_C = "option_c";
        public static final String COLUMN_OPTION_D = "option_d";
        public static final String COLUMN_CORRECT_ANSWER = "correct_answer";
        public static final String COLUMN_SUBJECT_ID = "subject_id";
        public static final String COLUMN_DIFFICULTY_LEVEL = "difficulty_level";
        public static final String COLUMN_MARKS = "marks";
        public static final String COLUMN_CREATED_BY = "created_by";
        public static final String COLUMN_CREATED_AT = "created_at";
    }
    
    /* Results Table */
    public static class ResultEntry implements BaseColumns {
        public static final String TABLE_NAME = "results";
        public static final String COLUMN_USER_ID = "user_id";
        public static final String COLUMN_SUBJECT_ID = "subject_id";
        public static final String COLUMN_SEMESTER = "semester";
        public static final String COLUMN_DIFFICULTY_LEVEL = "difficulty_level";
        public static final String COLUMN_TOTAL_QUESTIONS = "total_questions";
        public static final String COLUMN_CORRECT_ANSWERS = "correct_answers";
        public static final String COLUMN_WRONG_ANSWERS = "wrong_answers";
        public static final String COLUMN_SCORE = "score";
        public static final String COLUMN_PERCENTAGE = "percentage";
        public static final String COLUMN_PASS_FAIL = "pass_fail";
        public static final String COLUMN_CERTIFICATE_ID = "certificate_id";
        public static final String COLUMN_QUIZ_DATE = "quiz_date";
    }
    
    /* Quiz Answers Table */
    public static class QuizAnswerEntry implements BaseColumns {
        public static final String TABLE_NAME = "quiz_answers";
        public static final String COLUMN_RESULT_ID = "result_id";
        public static final String COLUMN_QUESTION_ID = "question_id";
        public static final String COLUMN_SELECTED_ANSWER = "selected_answer";
        public static final String COLUMN_IS_CORRECT = "is_correct";
    }
}

package com.mm.quizmaster.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.mm.quizmaster.constants.AppConstants;
import com.mm.quizmaster.database.DatabaseContract.*;
import com.mm.quizmaster.models.*;
import com.mm.quizmaster.utils.PasswordUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Database Helper Class
 * Manages SQLite database creation, upgrades, and all CRUD operations
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "QuizMaster.db";
    private static final int DATABASE_VERSION = 3;
    
    private Context context;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create all tables
        createUsersTable(db);
        createAdminsTable(db);
        createSubjectsTable(db);
        createQuestionsTable(db);
        createResultsTable(db);
        createQuizAnswersTable(db);
        
        // Insert sample data
        insertSampleSubjects(db);
        insertSampleQuestions(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop all tables
        db.execSQL("DROP TABLE IF EXISTS " + QuizAnswerEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ResultEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + QuestionEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SubjectEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + AdminEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + UserEntry.TABLE_NAME);
        
        // Recreate tables
        onCreate(db);
    }

    // ==================== TABLE CREATION ====================
    
    private void createUsersTable(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + UserEntry.TABLE_NAME + " (" +
                UserEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                UserEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                UserEntry.COLUMN_EMAIL + " TEXT UNIQUE NOT NULL, " +
                UserEntry.COLUMN_PASSWORD + " TEXT NOT NULL, " +
                UserEntry.COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP)";
        db.execSQL(CREATE_USERS_TABLE);
    }

    private void createAdminsTable(SQLiteDatabase db) {
        String CREATE_ADMINS_TABLE = "CREATE TABLE " + AdminEntry.TABLE_NAME + " (" +
                AdminEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                AdminEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                AdminEntry.COLUMN_EMAIL + " TEXT UNIQUE NOT NULL, " +
                AdminEntry.COLUMN_PASSWORD + " TEXT NOT NULL, " +
                AdminEntry.COLUMN_CREATED_BY + " TEXT DEFAULT 'Super Admin', " +
                AdminEntry.COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP)";
        db.execSQL(CREATE_ADMINS_TABLE);
    }

    private void createSubjectsTable(SQLiteDatabase db) {
        String CREATE_SUBJECTS_TABLE = "CREATE TABLE " + SubjectEntry.TABLE_NAME + " (" +
                SubjectEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                SubjectEntry.COLUMN_SUBJECT_NAME + " TEXT NOT NULL, " +
                SubjectEntry.COLUMN_SEMESTER + " INTEGER NOT NULL, " +
                SubjectEntry.COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "UNIQUE(" + SubjectEntry.COLUMN_SUBJECT_NAME + ", " + SubjectEntry.COLUMN_SEMESTER + "))";
        db.execSQL(CREATE_SUBJECTS_TABLE);
    }

    private void createQuestionsTable(SQLiteDatabase db) {
        String CREATE_QUESTIONS_TABLE = "CREATE TABLE " + QuestionEntry.TABLE_NAME + " (" +
                QuestionEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                QuestionEntry.COLUMN_QUESTION_TEXT + " TEXT NOT NULL, " +
                QuestionEntry.COLUMN_OPTION_A + " TEXT NOT NULL, " +
                QuestionEntry.COLUMN_OPTION_B + " TEXT NOT NULL, " +
                QuestionEntry.COLUMN_OPTION_C + " TEXT NOT NULL, " +
                QuestionEntry.COLUMN_OPTION_D + " TEXT NOT NULL, " +
                QuestionEntry.COLUMN_CORRECT_ANSWER + " TEXT NOT NULL CHECK(" + QuestionEntry.COLUMN_CORRECT_ANSWER + " IN ('A','B','C','D')), " +
                QuestionEntry.COLUMN_SUBJECT_ID + " INTEGER NOT NULL, " +
                QuestionEntry.COLUMN_DIFFICULTY_LEVEL + " TEXT NOT NULL CHECK(" + QuestionEntry.COLUMN_DIFFICULTY_LEVEL + " IN ('Easy','Medium','Hard')), " +
                QuestionEntry.COLUMN_MARKS + " INTEGER DEFAULT 1, " +
                QuestionEntry.COLUMN_CREATED_BY + " INTEGER, " +
                QuestionEntry.COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (" + QuestionEntry.COLUMN_SUBJECT_ID + ") REFERENCES " + SubjectEntry.TABLE_NAME + "(" + SubjectEntry._ID + ") ON DELETE CASCADE)";
        db.execSQL(CREATE_QUESTIONS_TABLE);
    }

    private void createResultsTable(SQLiteDatabase db) {
        String CREATE_RESULTS_TABLE = "CREATE TABLE " + ResultEntry.TABLE_NAME + " (" +
                ResultEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ResultEntry.COLUMN_USER_ID + " INTEGER NOT NULL, " +
                ResultEntry.COLUMN_SUBJECT_ID + " INTEGER NOT NULL, " +
                ResultEntry.COLUMN_SEMESTER + " INTEGER NOT NULL, " +
                ResultEntry.COLUMN_DIFFICULTY_LEVEL + " TEXT NOT NULL, " +
                ResultEntry.COLUMN_TOTAL_QUESTIONS + " INTEGER NOT NULL, " +
                ResultEntry.COLUMN_CORRECT_ANSWERS + " INTEGER NOT NULL, " +
                ResultEntry.COLUMN_WRONG_ANSWERS + " INTEGER NOT NULL, " +
                ResultEntry.COLUMN_SCORE + " INTEGER NOT NULL, " +
                ResultEntry.COLUMN_PERCENTAGE + " REAL NOT NULL, " +
                ResultEntry.COLUMN_PASS_FAIL + " TEXT NOT NULL CHECK(" + ResultEntry.COLUMN_PASS_FAIL + " IN ('Pass','Fail')), " +
                ResultEntry.COLUMN_CERTIFICATE_ID + " TEXT, " +
                ResultEntry.COLUMN_QUIZ_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (" + ResultEntry.COLUMN_USER_ID + ") REFERENCES " + UserEntry.TABLE_NAME + "(" + UserEntry._ID + ") ON DELETE CASCADE, " +
                "FOREIGN KEY (" + ResultEntry.COLUMN_SUBJECT_ID + ") REFERENCES " + SubjectEntry.TABLE_NAME + "(" + SubjectEntry._ID + ") ON DELETE CASCADE)";
        db.execSQL(CREATE_RESULTS_TABLE);
    }

    private void createQuizAnswersTable(SQLiteDatabase db) {
        String CREATE_QUIZ_ANSWERS_TABLE = "CREATE TABLE " + QuizAnswerEntry.TABLE_NAME + " (" +
                QuizAnswerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                QuizAnswerEntry.COLUMN_RESULT_ID + " INTEGER NOT NULL, " +
                QuizAnswerEntry.COLUMN_QUESTION_ID + " INTEGER NOT NULL, " +
                QuizAnswerEntry.COLUMN_SELECTED_ANSWER + " TEXT, " +
                QuizAnswerEntry.COLUMN_IS_CORRECT + " INTEGER NOT NULL CHECK(" + QuizAnswerEntry.COLUMN_IS_CORRECT + " IN (0,1)), " +
                "FOREIGN KEY (" + QuizAnswerEntry.COLUMN_RESULT_ID + ") REFERENCES " + ResultEntry.TABLE_NAME + "(" + ResultEntry._ID + ") ON DELETE CASCADE, " +
                "FOREIGN KEY (" + QuizAnswerEntry.COLUMN_QUESTION_ID + ") REFERENCES " + QuestionEntry.TABLE_NAME + "(" + QuestionEntry._ID + ") ON DELETE CASCADE)";
        db.execSQL(CREATE_QUIZ_ANSWERS_TABLE);
    }

    // ==================== USER CRUD ====================
    
    public long addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UserEntry.COLUMN_NAME, user.getName());
        values.put(UserEntry.COLUMN_EMAIL, user.getEmail());
        values.put(UserEntry.COLUMN_PASSWORD, PasswordUtils.hashPassword(user.getPassword()));
        return db.insert(UserEntry.TABLE_NAME, null, values);
    }

    public User getUserByEmailPassword(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String hashedPassword = PasswordUtils.hashPassword(password);
        
        Cursor cursor = db.query(UserEntry.TABLE_NAME, null,
                UserEntry.COLUMN_EMAIL + "=? AND " + UserEntry.COLUMN_PASSWORD + "=?",
                new String[]{email, hashedPassword}, null, null, null);
        
        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(UserEntry._ID)));
            user.setName(cursor.getString(cursor.getColumnIndexOrThrow(UserEntry.COLUMN_NAME)));
            user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(UserEntry.COLUMN_EMAIL)));
            cursor.close();
        }
        return user;
    }

    public boolean checkEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(UserEntry.TABLE_NAME, new String[]{UserEntry._ID},
                UserEntry.COLUMN_EMAIL + "=?", new String[]{email}, null, null, null);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    public User getUserById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(UserEntry.TABLE_NAME, null,
                UserEntry._ID + "=?", new String[]{String.valueOf(userId)}, null, null, null);
        
        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(UserEntry._ID)));
            user.setName(cursor.getString(cursor.getColumnIndexOrThrow(UserEntry.COLUMN_NAME)));
            user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(UserEntry.COLUMN_EMAIL)));
            cursor.close();
        }
        return user;
    }

    // ==================== ADMIN CRUD ====================
    
    public long addAdmin(Admin admin) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(AdminEntry.COLUMN_NAME, admin.getName());
        values.put(AdminEntry.COLUMN_EMAIL, admin.getEmail());
        values.put(AdminEntry.COLUMN_PASSWORD, PasswordUtils.hashPassword(admin.getPassword()));
        values.put(AdminEntry.COLUMN_CREATED_BY, admin.getCreatedBy());
        return db.insert(AdminEntry.TABLE_NAME, null, values);
    }

    public Admin getAdminByEmailPassword(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String hashedPassword = PasswordUtils.hashPassword(password);
        
        Cursor cursor = db.query(AdminEntry.TABLE_NAME, null,
                AdminEntry.COLUMN_EMAIL + "=? AND " + AdminEntry.COLUMN_PASSWORD + "=?",
                new String[]{email, hashedPassword}, null, null, null);
        
        Admin admin = null;
        if (cursor != null && cursor.moveToFirst()) {
            admin = new Admin();
            admin.setId(cursor.getInt(cursor.getColumnIndexOrThrow(AdminEntry._ID)));
            admin.setName(cursor.getString(cursor.getColumnIndexOrThrow(AdminEntry.COLUMN_NAME)));
            admin.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(AdminEntry.COLUMN_EMAIL)));
            admin.setCreatedBy(cursor.getString(cursor.getColumnIndexOrThrow(AdminEntry.COLUMN_CREATED_BY)));
            cursor.close();
        }
        return admin;
    }

    public List<Admin> getAllAdmins() {
        List<Admin> admins = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(AdminEntry.TABLE_NAME, null, null, null, null, null, null);
        
        if (cursor.moveToFirst()) {
            do {
                Admin admin = new Admin();
                admin.setId(cursor.getInt(cursor.getColumnIndexOrThrow(AdminEntry._ID)));
                admin.setName(cursor.getString(cursor.getColumnIndexOrThrow(AdminEntry.COLUMN_NAME)));
                admin.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(AdminEntry.COLUMN_EMAIL)));
                admin.setCreatedBy(cursor.getString(cursor.getColumnIndexOrThrow(AdminEntry.COLUMN_CREATED_BY)));
                admins.add(admin);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return admins;
    }

    public void deleteAdmin(int adminId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(AdminEntry.TABLE_NAME, AdminEntry._ID + "=?", new String[]{String.valueOf(adminId)});
    }

    /**
     * Check login for student users
     * Returns User object if credentials are valid, null otherwise
     */
    public User checkLogin(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String hashedPassword = PasswordUtils.hashPassword(password);
        
        Cursor cursor = db.query(UserEntry.TABLE_NAME, null,
                UserEntry.COLUMN_EMAIL + "=? AND " + UserEntry.COLUMN_PASSWORD + "=?",
                new String[]{email, hashedPassword}, null, null, null);
        
        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(UserEntry._ID)));
            user.setName(cursor.getString(cursor.getColumnIndexOrThrow(UserEntry.COLUMN_NAME)));
            user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(UserEntry.COLUMN_EMAIL)));
            user.setRole("student"); // Users are students
            cursor.close();
        }
        return user;
    }

    public boolean checkAdminEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(AdminEntry.TABLE_NAME, new String[]{AdminEntry._ID},
                AdminEntry.COLUMN_EMAIL + "=?", new String[]{email}, null, null, null);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    // ==================== SUBJECT CRUD ====================
    
    public long addSubject(Subject subject) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SubjectEntry.COLUMN_SUBJECT_NAME, subject.getSubjectName());
        values.put(SubjectEntry.COLUMN_SEMESTER, subject.getSemester());
        return db.insert(SubjectEntry.TABLE_NAME, null, values);
    }

    public int updateSubject(Subject subject) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SubjectEntry.COLUMN_SUBJECT_NAME, subject.getSubjectName());
        values.put(SubjectEntry.COLUMN_SEMESTER, subject.getSemester());
        return db.update(SubjectEntry.TABLE_NAME, values, SubjectEntry._ID + "=?", new String[]{String.valueOf(subject.getId())});
    }

    public List<Subject> getSubjectsBySemester(int semester) {
        List<Subject> subjects = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(SubjectEntry.TABLE_NAME, null,
                SubjectEntry.COLUMN_SEMESTER + "=?", new String[]{String.valueOf(semester)},
                null, null, SubjectEntry.COLUMN_SUBJECT_NAME + " ASC");
        
        if (cursor.moveToFirst()) {
            do {
                Subject subject = new Subject();
                subject.setId(cursor.getInt(cursor.getColumnIndexOrThrow(SubjectEntry._ID)));
                subject.setSubjectName(cursor.getString(cursor.getColumnIndexOrThrow(SubjectEntry.COLUMN_SUBJECT_NAME)));
                subject.setSemester(cursor.getInt(cursor.getColumnIndexOrThrow(SubjectEntry.COLUMN_SEMESTER)));
                subjects.add(subject);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return subjects;
    }

    public List<Subject> getAllSubjects() {
        List<Subject> subjects = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(SubjectEntry.TABLE_NAME, null, null, null, null, null,
                SubjectEntry.COLUMN_SEMESTER + " ASC, " + SubjectEntry.COLUMN_SUBJECT_NAME + " ASC");
        
        if (cursor.moveToFirst()) {
            do {
                Subject subject = new Subject();
                subject.setId(cursor.getInt(cursor.getColumnIndexOrThrow(SubjectEntry._ID)));
                subject.setSubjectName(cursor.getString(cursor.getColumnIndexOrThrow(SubjectEntry.COLUMN_SUBJECT_NAME)));
                subject.setSemester(cursor.getInt(cursor.getColumnIndexOrThrow(SubjectEntry.COLUMN_SEMESTER)));
                subjects.add(subject);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return subjects;
    }

    public void deleteSubject(int subjectId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(SubjectEntry.TABLE_NAME, SubjectEntry._ID + "=?", new String[]{String.valueOf(subjectId)});
    }

    public Subject getSubjectById(int subjectId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(SubjectEntry.TABLE_NAME, null,
                SubjectEntry._ID + "=?", new String[]{String.valueOf(subjectId)}, null, null, null);
        
        Subject subject = null;
        if (cursor != null && cursor.moveToFirst()) {
            subject = new Subject();
            subject.setId(cursor.getInt(cursor.getColumnIndexOrThrow(SubjectEntry._ID)));
            subject.setSubjectName(cursor.getString(cursor.getColumnIndexOrThrow(SubjectEntry.COLUMN_SUBJECT_NAME)));
            subject.setSemester(cursor.getInt(cursor.getColumnIndexOrThrow(SubjectEntry.COLUMN_SEMESTER)));
            cursor.close();
        }
        return subject;
    }

    // ==================== QUESTION CRUD ====================
    
    public long addQuestion(Question question) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(QuestionEntry.COLUMN_QUESTION_TEXT, question.getQuestionText());
        values.put(QuestionEntry.COLUMN_OPTION_A, question.getOptionA());
        values.put(QuestionEntry.COLUMN_OPTION_B, question.getOptionB());
        values.put(QuestionEntry.COLUMN_OPTION_C, question.getOptionC());
        values.put(QuestionEntry.COLUMN_OPTION_D, question.getOptionD());
        values.put(QuestionEntry.COLUMN_CORRECT_ANSWER, question.getCorrectAnswer());
        values.put(QuestionEntry.COLUMN_SUBJECT_ID, question.getSubjectId());
        values.put(QuestionEntry.COLUMN_DIFFICULTY_LEVEL, question.getDifficultyLevel());
        values.put(QuestionEntry.COLUMN_MARKS, question.getMarks());
        values.put(QuestionEntry.COLUMN_CREATED_BY, question.getCreatedBy());
        return db.insert(QuestionEntry.TABLE_NAME, null, values);
    }

    public List<Question> getRandomQuestions(int subjectId, String difficultyLevel, int limit) {
        List<Question> questions = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        String query = "SELECT * FROM " + QuestionEntry.TABLE_NAME +
                " WHERE " + QuestionEntry.COLUMN_SUBJECT_ID + "=? AND " +
                QuestionEntry.COLUMN_DIFFICULTY_LEVEL + "=? ORDER BY RANDOM() LIMIT ?";
        
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(subjectId), difficultyLevel, String.valueOf(limit)});
        
        if (cursor.moveToFirst()) {
            do {
                questions.add(getQuestionFromCursor(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return questions;
    }

    public List<Question> getAllQuestions() {
        List<Question> questions = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(QuestionEntry.TABLE_NAME, null, null, null, null, null, null);
        
        if (cursor.moveToFirst()) {
            do {
                questions.add(getQuestionFromCursor(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return questions;
    }

    public List<Question> getQuestionsBySubject(int subjectId) {
        List<Question> questions = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(QuestionEntry.TABLE_NAME, null,
                QuestionEntry.COLUMN_SUBJECT_ID + "=?", new String[]{String.valueOf(subjectId)},
                null, null, null);
        
        if (cursor.moveToFirst()) {
            do {
                questions.add(getQuestionFromCursor(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return questions;
    }

    /**
     * Get questions by subject and difficulty level
     * Used for quiz generation
     */
    public List<Question> getQuestions(int subjectId, String difficultyLevel) {
        List<Question> questions = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(QuestionEntry.TABLE_NAME, null,
                QuestionEntry.COLUMN_SUBJECT_ID + "=? AND " + QuestionEntry.COLUMN_DIFFICULTY_LEVEL + "=?",
                new String[]{String.valueOf(subjectId), difficultyLevel},
                null, null, "RANDOM()", "10"); // Get 10 random questions
        
        if (cursor.moveToFirst()) {
            do {
                questions.add(getQuestionFromCursor(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return questions;
    }

    public void deleteQuestion(int questionId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(QuestionEntry.TABLE_NAME, QuestionEntry._ID + "=?", new String[]{String.valueOf(questionId)});
    }

    public Question getQuestionById(int questionId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(QuestionEntry.TABLE_NAME, null,
                QuestionEntry._ID + "=?", new String[]{String.valueOf(questionId)}, null, null, null);
        
        Question question = null;
        if (cursor != null && cursor.moveToFirst()) {
            question = getQuestionFromCursor(cursor);
            cursor.close();
        }
        return question;
    }

    private Question getQuestionFromCursor(Cursor cursor) {
        Question question = new Question();
        question.setId(cursor.getInt(cursor.getColumnIndexOrThrow(QuestionEntry._ID)));
        question.setQuestionText(cursor.getString(cursor.getColumnIndexOrThrow(QuestionEntry.COLUMN_QUESTION_TEXT)));
        question.setOptionA(cursor.getString(cursor.getColumnIndexOrThrow(QuestionEntry.COLUMN_OPTION_A)));
        question.setOptionB(cursor.getString(cursor.getColumnIndexOrThrow(QuestionEntry.COLUMN_OPTION_B)));
        question.setOptionC(cursor.getString(cursor.getColumnIndexOrThrow(QuestionEntry.COLUMN_OPTION_C)));
        question.setOptionD(cursor.getString(cursor.getColumnIndexOrThrow(QuestionEntry.COLUMN_OPTION_D)));
        question.setCorrectAnswer(cursor.getString(cursor.getColumnIndexOrThrow(QuestionEntry.COLUMN_CORRECT_ANSWER)));
        question.setSubjectId(cursor.getInt(cursor.getColumnIndexOrThrow(QuestionEntry.COLUMN_SUBJECT_ID)));
        question.setDifficultyLevel(cursor.getString(cursor.getColumnIndexOrThrow(QuestionEntry.COLUMN_DIFFICULTY_LEVEL)));
        question.setMarks(cursor.getInt(cursor.getColumnIndexOrThrow(QuestionEntry.COLUMN_MARKS)));
        return question;
    }

    public int getQuestionCount(int subjectId, String difficultyLevel) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(QuestionEntry.TABLE_NAME, new String[]{"COUNT(*)"},
                QuestionEntry.COLUMN_SUBJECT_ID + "=? AND " + QuestionEntry.COLUMN_DIFFICULTY_LEVEL + "=?",
                new String[]{String.valueOf(subjectId), difficultyLevel}, null, null, null);
        
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    // ==================== RESULT CRUD ====================
    
    public long saveResult(Result result) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ResultEntry.COLUMN_USER_ID, result.getUserId());
        values.put(ResultEntry.COLUMN_SUBJECT_ID, result.getSubjectId());
        values.put(ResultEntry.COLUMN_SEMESTER, result.getSemester());
        values.put(ResultEntry.COLUMN_DIFFICULTY_LEVEL, result.getDifficultyLevel());
        values.put(ResultEntry.COLUMN_TOTAL_QUESTIONS, result.getTotalQuestions());
        values.put(ResultEntry.COLUMN_CORRECT_ANSWERS, result.getCorrectAnswers());
        values.put(ResultEntry.COLUMN_WRONG_ANSWERS, result.getWrongAnswers());
        values.put(ResultEntry.COLUMN_SCORE, result.getScore());
        values.put(ResultEntry.COLUMN_PERCENTAGE, result.getPercentage());
        values.put(ResultEntry.COLUMN_PASS_FAIL, result.getPassFail());
        values.put(ResultEntry.COLUMN_CERTIFICATE_ID, result.getCertificateId());
        return db.insert(ResultEntry.TABLE_NAME, null, values);
    }

    public List<Result> getResultsByUserId(int userId) {
        List<Result> results = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(ResultEntry.TABLE_NAME, null,
                ResultEntry.COLUMN_USER_ID + "=?", new String[]{String.valueOf(userId)},
                null, null, ResultEntry.COLUMN_QUIZ_DATE + " DESC");
        
        if (cursor.moveToFirst()) {
            do {
                results.add(getResultFromCursor(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return results;
    }

    public Result getResultById(int resultId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(ResultEntry.TABLE_NAME, null,
                ResultEntry._ID + "=?", new String[]{String.valueOf(resultId)}, null, null, null);
        
        Result result = null;
        if (cursor != null && cursor.moveToFirst()) {
            result = getResultFromCursor(cursor);
            cursor.close();
        }
        return result;
    }

    private Result getResultFromCursor(Cursor cursor) {
        Result result = new Result();
        result.setId(cursor.getInt(cursor.getColumnIndexOrThrow(ResultEntry._ID)));
        result.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(ResultEntry.COLUMN_USER_ID)));
        result.setSubjectId(cursor.getInt(cursor.getColumnIndexOrThrow(ResultEntry.COLUMN_SUBJECT_ID)));
        result.setSemester(cursor.getInt(cursor.getColumnIndexOrThrow(ResultEntry.COLUMN_SEMESTER)));
        result.setDifficultyLevel(cursor.getString(cursor.getColumnIndexOrThrow(ResultEntry.COLUMN_DIFFICULTY_LEVEL)));
        result.setTotalQuestions(cursor.getInt(cursor.getColumnIndexOrThrow(ResultEntry.COLUMN_TOTAL_QUESTIONS)));
        result.setCorrectAnswers(cursor.getInt(cursor.getColumnIndexOrThrow(ResultEntry.COLUMN_CORRECT_ANSWERS)));
        result.setWrongAnswers(cursor.getInt(cursor.getColumnIndexOrThrow(ResultEntry.COLUMN_WRONG_ANSWERS)));
        result.setScore(cursor.getInt(cursor.getColumnIndexOrThrow(ResultEntry.COLUMN_SCORE)));
        result.setPercentage(cursor.getDouble(cursor.getColumnIndexOrThrow(ResultEntry.COLUMN_PERCENTAGE)));
        result.setPassFail(cursor.getString(cursor.getColumnIndexOrThrow(ResultEntry.COLUMN_PASS_FAIL)));
        result.setCertificateId(cursor.getString(cursor.getColumnIndexOrThrow(ResultEntry.COLUMN_CERTIFICATE_ID)));
        result.setQuizDate(cursor.getString(cursor.getColumnIndexOrThrow(ResultEntry.COLUMN_QUIZ_DATE)));
        return result;
    }

    // ==================== QUIZ ANSWER CRUD ====================
    
    public long saveQuizAnswer(QuizAnswer answer) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(QuizAnswerEntry.COLUMN_RESULT_ID, answer.getResultId());
        values.put(QuizAnswerEntry.COLUMN_QUESTION_ID, answer.getQuestionId());
        values.put(QuizAnswerEntry.COLUMN_SELECTED_ANSWER, answer.getSelectedAnswer());
        values.put(QuizAnswerEntry.COLUMN_IS_CORRECT, answer.isCorrect() ? 1 : 0);
        return db.insert(QuizAnswerEntry.TABLE_NAME, null, values);
    }

    public List<QuizAnswer> getQuizAnswersByResultId(int resultId) {
        List<QuizAnswer> answers = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(QuizAnswerEntry.TABLE_NAME, null,
                QuizAnswerEntry.COLUMN_RESULT_ID + "=?", new String[]{String.valueOf(resultId)},
                null, null, null);
        
        if (cursor.moveToFirst()) {
            do {
                QuizAnswer answer = new QuizAnswer();
                answer.setId(cursor.getInt(cursor.getColumnIndexOrThrow(QuizAnswerEntry._ID)));
                answer.setResultId(cursor.getInt(cursor.getColumnIndexOrThrow(QuizAnswerEntry.COLUMN_RESULT_ID)));
                answer.setQuestionId(cursor.getInt(cursor.getColumnIndexOrThrow(QuizAnswerEntry.COLUMN_QUESTION_ID)));
                answer.setSelectedAnswer(cursor.getString(cursor.getColumnIndexOrThrow(QuizAnswerEntry.COLUMN_SELECTED_ANSWER)));
                answer.setCorrect(cursor.getInt(cursor.getColumnIndexOrThrow(QuizAnswerEntry.COLUMN_IS_CORRECT)) == 1);
                answers.add(answer);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return answers;
    }

    // ==================== SAMPLE DATA INSERTION ====================
    
    private void insertSampleSubjects(SQLiteDatabase db) {
        // Semester 1
        insertSubject(db, "C Programming", 1);
        insertSubject(db, "Computer Fundamentals", 1);
        insertSubject(db, "Mathematics-I", 1);
        insertSubject(db, "English Communication", 1);
        insertSubject(db, "Digital Electronics", 1);
        
        // Semester 2
        insertSubject(db, "Data Structures", 2);
        insertSubject(db, "C++ Programming", 2);
        insertSubject(db, "Mathematics-II", 2);
        insertSubject(db, "Operating Systems", 2);
        insertSubject(db, "Web Technologies", 2);
        
        // Semester 3
        insertSubject(db, "Java Programming", 3);
        insertSubject(db, "Database Management", 3);
        insertSubject(db, "Computer Networks", 3);
        insertSubject(db, "Software Engineering", 3);
        insertSubject(db, "Python Programming", 3);
        
        // Semester 4
        insertSubject(db, "Android Development", 4);
        insertSubject(db, "Machine Learning", 4);
        insertSubject(db, "Cloud Computing", 4);
        insertSubject(db, "Cyber Security", 4);
        insertSubject(db, "Data Analytics", 4);
        
        // Semester 5
        insertSubject(db, "Artificial Intelligence", 5);
        insertSubject(db, "Blockchain Technology", 5);
        insertSubject(db, "IoT", 5);
        insertSubject(db, "Big Data", 5);
        insertSubject(db, "Mobile App Development", 5);
        
        // Semester 6
        insertSubject(db, "Project Management", 6);
        insertSubject(db, "DevOps", 6);
        insertSubject(db, "Ethical Hacking", 6);
        insertSubject(db, "Advanced Java", 6);
        insertSubject(db, "Final Year Project", 6);
    }

    private void insertSubject(SQLiteDatabase db, String name, int semester) {
        ContentValues values = new ContentValues();
        values.put(SubjectEntry.COLUMN_SUBJECT_NAME, name);
        values.put(SubjectEntry.COLUMN_SEMESTER, semester);
        db.insert(SubjectEntry.TABLE_NAME, null, values);
    }

    private void insertSampleQuestions(SQLiteDatabase db) {
        // Get subject IDs (assuming they're inserted in order)
        // C Programming questions (Subject ID: 1)
        insertQuestion(db, 1, "What is the size of int data type in C?", "2 bytes", "4 bytes", "8 bytes", "1 byte", "B", "Easy", 1);
        insertQuestion(db, 1, "Which header file is used for printf() function?", "stdlib.h", "stdio.h", "string.h", "math.h", "B", "Easy", 1);
        insertQuestion(db, 1, "What is the correct syntax to declare a pointer in C?", "int ptr;", "int *ptr;", "ptr int;", "*int ptr;", "B", "Medium", 1);
        insertQuestion(db, 1, "Which loop is guaranteed to execute at least once?", "for loop", "while loop", "do-while loop", "nested loop", "C", "Easy", 1);
        insertQuestion(db, 1, "What is the output of: printf(\"%d\", 5/2);", "2.5", "2", "3", "Error", "B", "Medium", 1);
        insertQuestion(db, 1, "Which operator is used to access structure members?", "->", ".", "::", "*", "B", "Easy", 1);
        insertQuestion(db, 1, "What does malloc() function return?", "int", "void*", "char*", "NULL", "B", "Medium", 1);
        insertQuestion(db, 1, "Which keyword is used to prevent variable modification?", "static", "const", "volatile", "extern", "B", "Easy", 1);
        insertQuestion(db, 1, "What is recursion in C?", "Loop", "Function calling itself", "Pointer", "Array", "B", "Medium", 1);
        insertQuestion(db, 1, "Which function is used to allocate memory dynamically?", "alloc()", "malloc()", "new()", "create()", "B", "Easy", 1);
        
        // Data Structures questions (Subject ID: 6)
        insertQuestion(db, 6, "Which data structure uses LIFO principle?", "Queue", "Stack", "Tree", "Graph", "B", "Easy", 1);
        insertQuestion(db, 6, "What is the time complexity of binary search?", "O(n)", "O(log n)", "O(n²)", "O(1)", "B", "Medium", 1);
        insertQuestion(db, 6, "Which data structure is used in BFS?", "Stack", "Queue", "Tree", "Heap", "B", "Medium", 1);
        insertQuestion(db, 6, "What is a circular linked list?", "List with cycle", "Last node points to first", "Double linked", "Array based", "B", "Easy", 1);
        insertQuestion(db, 6, "Which sorting algorithm is fastest on average?", "Bubble Sort", "Quick Sort", "Selection Sort", "Insertion Sort", "B", "Hard", 1);
        insertQuestion(db, 6, "What is the height of a binary tree with n nodes?", "n", "log n", "n²", "2n", "B", "Medium", 1);
        insertQuestion(db, 6, "Which data structure is used for recursion?", "Queue", "Stack", "Array", "Linked List", "B", "Easy", 1);
        insertQuestion(db, 6, "What is a hash collision?", "Two keys same hash", "Hash error", "Memory overflow", "Index error", "A", "Medium", 1);
        insertQuestion(db, 6, "Which traversal visits root first?", "Inorder", "Preorder", "Postorder", "Level order", "B", "Easy", 1);
        insertQuestion(db, 6, "What is the space complexity of merge sort?", "O(1)", "O(n)", "O(log n)", "O(n²)", "B", "Hard", 1);
        
        // Java Programming questions (Subject ID: 11)
        insertQuestion(db, 11, "Which keyword is used for inheritance in Java?", "implements", "extends", "inherits", "super", "B", "Easy", 1);
        insertQuestion(db, 11, "What is the default value of boolean in Java?", "true", "false", "0", "null", "B", "Easy", 1);
        insertQuestion(db, 11, "Which method is called when object is created?", "main()", "constructor", "init()", "start()", "B", "Easy", 1);
        insertQuestion(db, 11, "What is polymorphism in Java?", "Many forms", "Inheritance", "Encapsulation", "Abstraction", "A", "Medium", 1);
        insertQuestion(db, 11, "Which collection allows duplicate elements?", "Set", "List", "Map", "Queue", "B", "Easy", 1);
        insertQuestion(db, 11, "What is the parent class of all classes?", "Class", "Object", "System", "Main", "B", "Easy", 1);
        insertQuestion(db, 11, "Which keyword is used for exception handling?", "throw", "try", "catch", "All of these", "D", "Medium", 1);
        insertQuestion(db, 11, "What is method overloading?", "Same name different params", "Different name", "Inheritance", "Interface", "A", "Medium", 1);
        insertQuestion(db, 11, "Which interface is used for sorting?", "Sortable", "Comparable", "Ordered", "Arranged", "B", "Medium", 1);
        insertQuestion(db, 11, "What is a thread in Java?", "Lightweight process", "Heavy process", "Function", "Class", "A", "Hard", 1);
        
        // Python Programming questions (Subject ID: 15)
        insertQuestion(db, 15, "Which keyword is used to define a function in Python?", "function", "def", "func", "define", "B", "Easy", 1);
        insertQuestion(db, 15, "What is the output of: print(type([]))?", "list", "<class 'list'>", "array", "[]", "B", "Easy", 1);
        insertQuestion(db, 15, "Which data type is mutable in Python?", "tuple", "list", "string", "int", "B", "Easy", 1);
        insertQuestion(db, 15, "What is used for single line comment?", "//", "#", "/*", "--", "B", "Easy", 1);
        insertQuestion(db, 15, "Which method is used to add element in list?", "add()", "append()", "insert()", "push()", "B", "Easy", 1);
        insertQuestion(db, 15, "What is lambda in Python?", "Anonymous function", "Loop", "Class", "Module", "A", "Medium", 1);
        insertQuestion(db, 15, "Which keyword is used for exception handling?", "catch", "except", "error", "handle", "B", "Easy", 1);
        insertQuestion(db, 15, "What is list comprehension?", "Compact way to create list", "List method", "Loop", "Function", "A", "Medium", 1);
        insertQuestion(db, 15, "Which module is used for regular expressions?", "regex", "re", "regexp", "pattern", "B", "Medium", 1);
        insertQuestion(db, 15, "What is the difference between list and tuple?", "List mutable tuple immutable", "No difference", "Tuple faster", "List ordered", "A", "Medium", 1);
    }

    private void insertQuestion(SQLiteDatabase db, int subjectId, String question, String optA, String optB, String optC, String optD, String answer, String level, int marks) {
        ContentValues values = new ContentValues();
        values.put(QuestionEntry.COLUMN_SUBJECT_ID, subjectId);
        values.put(QuestionEntry.COLUMN_QUESTION_TEXT, question);
        values.put(QuestionEntry.COLUMN_OPTION_A, optA);
        values.put(QuestionEntry.COLUMN_OPTION_B, optB);
        values.put(QuestionEntry.COLUMN_OPTION_C, optC);
        values.put(QuestionEntry.COLUMN_OPTION_D, optD);
        values.put(QuestionEntry.COLUMN_CORRECT_ANSWER, answer);
        values.put(QuestionEntry.COLUMN_DIFFICULTY_LEVEL, level);
        values.put(QuestionEntry.COLUMN_MARKS, marks);
        values.putNull(QuestionEntry.COLUMN_CREATED_BY); // Sample questions have no creator
        db.insert(QuestionEntry.TABLE_NAME, null, values);
    }
}

package com.mm.quizmaster.models;

/**
 * Result Model Class
 * Represents a quiz result
 */
public class Result {
    private int id;
    private int userId;
    private int subjectId;
    private int semester;
    private String difficultyLevel;
    private int totalQuestions;
    private int correctAnswers;
    private int wrongAnswers;
    private int score;
    private double percentage;
    private String passFail;
    private String certificateId;
    private String quizDate;

    public Result() {}

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getSubjectId() { return subjectId; }
    public void setSubjectId(int subjectId) { this.subjectId = subjectId; }

    public int getSemester() { return semester; }
    public void setSemester(int semester) { this.semester = semester; }

    public String getDifficultyLevel() { return difficultyLevel; }
    public void setDifficultyLevel(String difficultyLevel) { this.difficultyLevel = difficultyLevel; }

    public int getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }

    public int getCorrectAnswers() { return correctAnswers; }
    public void setCorrectAnswers(int correctAnswers) { this.correctAnswers = correctAnswers; }

    public int getWrongAnswers() { return wrongAnswers; }
    public void setWrongAnswers(int wrongAnswers) { this.wrongAnswers = wrongAnswers; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public double getPercentage() { return percentage; }
    public void setPercentage(double percentage) { this.percentage = percentage; }

    public String getPassFail() { return passFail; }
    public void setPassFail(String passFail) { this.passFail = passFail; }

    public String getCertificateId() { return certificateId; }
    public void setCertificateId(String certificateId) { this.certificateId = certificateId; }

    public String getQuizDate() { return quizDate; }
    public void setQuizDate(String quizDate) { this.quizDate = quizDate; }
}

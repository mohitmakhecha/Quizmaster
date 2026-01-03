package com.mm.quizmaster.models;

/**
 * Subject Model Class
 * Represents a subject in the database
 */
public class Subject {
    private int id;
    private String subjectName;
    private int semester;
    private String createdAt;

    // Default constructor
    public Subject() {
    }

    // Constructor with name and semester
    public Subject(String subjectName, int semester) {
        this.subjectName = subjectName;
        this.semester = semester;
    }

    // Constructor with all fields
    public Subject(int id, String subjectName, int semester, String createdAt) {
        this.id = id;
        this.subjectName = subjectName;
        this.semester = semester;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}

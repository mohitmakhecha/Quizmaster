package com.mm.quizmaster.models;

/**
 * QuizAnswer Model Class
 * Represents an individual answer in a quiz
 */
public class QuizAnswer {
    private int id;
    private int resultId;
    private int questionId;
    private String selectedAnswer;
    private boolean isCorrect;

    public QuizAnswer() {}

    public QuizAnswer(int resultId, int questionId, String selectedAnswer, boolean isCorrect) {
        this.resultId = resultId;
        this.questionId = questionId;
        this.selectedAnswer = selectedAnswer;
        this.isCorrect = isCorrect;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getResultId() { return resultId; }
    public void setResultId(int resultId) { this.resultId = resultId; }

    public int getQuestionId() { return questionId; }
    public void setQuestionId(int questionId) { this.questionId = questionId; }

    public String getSelectedAnswer() { return selectedAnswer; }
    public void setSelectedAnswer(String selectedAnswer) { this.selectedAnswer = selectedAnswer; }

    public boolean isCorrect() { return isCorrect; }
    public void setCorrect(boolean correct) { isCorrect = correct; }
}

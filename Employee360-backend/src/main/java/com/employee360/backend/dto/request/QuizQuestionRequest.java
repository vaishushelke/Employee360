
package com.employee360.backend.dto.request;

import java.util.List;

public class QuizQuestionRequest {

    private String questionText;
    private int marks;
    private List<QuizOptionRequest> options;

    public QuizQuestionRequest() {
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public int getMarks() {
        return marks;
    }

    public void setMarks(int marks) {
        this.marks = marks;
    }

    public List<QuizOptionRequest> getOptions() {
        return options;
    }

    public void setOptions(List<QuizOptionRequest> options) {
        this.options = options;
    }
}

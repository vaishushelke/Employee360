
package com.employee360.backend.dto.request;

public class QuizOptionRequest {

    private String optionText;
    private boolean correct;

    public QuizOptionRequest() {
    }

    public String getOptionText() {
        return optionText;
    }

    public void setOptionText(String optionText) {
        this.optionText = optionText;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }
}


package com.employee360.backend.dto.request;

import java.util.List;

public class QuizSubmissionRequest {

    private List<AnswerItem> answers;

    public QuizSubmissionRequest() {
    }

    public List<AnswerItem> getAnswers() {
        return answers;
    }

    public void setAnswers(List<AnswerItem> answers) {
        this.answers = answers;
    }

    public static class AnswerItem {

        private Long questionId;
        private Long selectedOptionId;

        public AnswerItem() {
        }

        public Long getQuestionId() {
            return questionId;
        }

        public void setQuestionId(Long questionId) {
            this.questionId = questionId;
        }

        public Long getSelectedOptionId() {
            return selectedOptionId;
        }

        public void setSelectedOptionId(Long selectedOptionId) {
            this.selectedOptionId = selectedOptionId;
        }
    }
}

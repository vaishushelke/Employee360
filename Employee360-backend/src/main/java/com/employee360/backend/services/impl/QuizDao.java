
package com.employee360.backend.services.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.employee360.backend.dto.request.QuizCreateRequest;
import com.employee360.backend.dto.request.QuizQuestionRequest;
import com.employee360.backend.dto.request.QuizSubmissionRequest;
import com.employee360.backend.dto.response.QuizOptionResponse;
import com.employee360.backend.dto.response.QuizQuestionResponse;
import com.employee360.backend.dto.response.QuizResponse;
import com.employee360.backend.dto.response.QuizResultResponse;
import com.employee360.backend.entities.DepartmentTopic;
import com.employee360.backend.entities.Quiz;
import com.employee360.backend.entities.QuizAnswer;
import com.employee360.backend.entities.QuizAttempt;
import com.employee360.backend.entities.QuizOption;
import com.employee360.backend.entities.QuizQuestion;
import com.employee360.backend.enums.QuizDifficulty;
import com.employee360.backend.enums.QuizStatus;
import com.employee360.backend.exception.ResourceNotFoundException;
import com.employee360.backend.repos.DepartmentTopicRepo;
import com.employee360.backend.repos.QuizAnswerRepo;
import com.employee360.backend.repos.QuizAttemptRepo;
import com.employee360.backend.repos.QuizOptionRepo;
import com.employee360.backend.repos.QuizQuestionRepo;
import com.employee360.backend.repos.QuizRepo;
import com.employee360.backend.services.QuizService;

@Service
@Transactional
public class QuizDao implements QuizService {

    private final QuizRepo quizRepo;
    private final QuizQuestionRepo questionRepo;
    private final QuizOptionRepo optionRepo;
    private final QuizAttemptRepo attemptRepo;
        private final QuizAnswerRepo answerRepo;
        private final DepartmentTopicRepo topicRepo;

    public QuizDao(
            QuizRepo quizRepo,
            QuizQuestionRepo questionRepo,
            QuizOptionRepo optionRepo,
            QuizAttemptRepo attemptRepo,
            QuizAnswerRepo answerRepo,
            DepartmentTopicRepo topicRepo) {

        this.quizRepo = quizRepo;
        this.questionRepo = questionRepo;
        this.optionRepo = optionRepo;
        this.attemptRepo = attemptRepo;
        this.answerRepo = answerRepo;
        this.topicRepo = topicRepo;
    }

    @Override
    public QuizResponse createQuiz(QuizCreateRequest request) {

                if (request == null || request.getTopicId() == null) {
            throw new IllegalArgumentException(
                                        "A learning topic is required for the quiz");
        }

        Quiz quiz = new Quiz();
        quiz.setTitle(request.getTitle().trim());
        quiz.setDescription(request.getDescription());
        quiz.setDurationMinutes(request.getDurationMinutes());
        quiz.setTotalMarks(0);
                quiz.setTopic(getTopicOrThrow(request.getTopicId()));
                quiz.setDifficulty(parseDifficulty(request.getDifficulty()));
                quiz.setActive(false);
        quiz.setCreatedAt(LocalDateTime.now());

        return toQuizResponse(quizRepo.save(quiz));
    }

        @Override
        @Transactional(readOnly = true)
        public List<QuizResponse> getAllQuizzesForAdmin() {
                return quizRepo.findAllByOrderByCreatedAtDesc()
                                .stream()
                                .map(this::toQuizResponse)
                                .toList();
        }

        @Override
        public QuizResponse updateQuiz(Long quizId, QuizCreateRequest request) {
                Quiz quiz = getQuizOrThrow(quizId);
                if (quiz.isArchived()) {
                        throw new IllegalArgumentException("Archived quizzes cannot be changed");
                }
                if (request.getTopicId() == null) {
                        throw new IllegalArgumentException("A learning topic is required for the quiz");
                }
                quiz.setTitle(request.getTitle().trim());
                quiz.setDescription(request.getDescription());
                quiz.setDurationMinutes(request.getDurationMinutes());
                quiz.setTopic(getTopicOrThrow(request.getTopicId()));
                quiz.setDifficulty(parseDifficulty(request.getDifficulty()));
                if (Boolean.TRUE.equals(request.getActive())) {
                        if (questionRepo.findByQuizId(quizId).isEmpty()) {
                                throw new IllegalArgumentException("Add at least one question before publishing this quiz");
                        }
                        quiz.setActive(true);
                } else if (Boolean.FALSE.equals(request.getActive())) {
                        quiz.setActive(false);
                }
                return toQuizResponse(quizRepo.save(quiz));
        }

        @Override
        public void archiveQuiz(Long quizId) {
                Quiz quiz = getQuizOrThrow(quizId);
                quiz.setArchived(true);
                quiz.setActive(false);
                quizRepo.save(quiz);
        }

    @Override
    @Transactional(readOnly = true)
    public List<QuizResponse> getActiveQuizzes() {

        return quizRepo.findByActiveTrueAndArchivedFalse()
                .stream()
                .map(this::toQuizResponse)
                .toList();
    }

        @Override
        @Transactional(readOnly = true)
        public List<QuizQuestionResponse> getQuizQuestionsForAdmin(Long quizId) {
                getQuizOrThrow(quizId);
                return questionRepo.findByQuizId(quizId)
                                .stream()
                                .map(this::toQuestionResponse)
                                .toList();
        }

    @Override
    @Transactional(readOnly = true)
    public QuizResponse getQuizById(Long quizId) {

        return toQuizResponse(getQuizOrThrow(quizId));
    }

    @Override
    public QuizQuestionResponse addQuestion(
            Long quizId,
            QuizQuestionRequest request) {

        if (request == null
                || request.getQuestionText() == null
                || request.getQuestionText().isBlank()
                || request.getMarks() <= 0) {
            throw new IllegalArgumentException(
                    "Valid question text and marks are required");
        }

        if (request.getOptions() == null
                || request.getOptions().size() < 2) {
            throw new IllegalArgumentException(
                    "A question must have at least two options");
        }

        long correctCount = request.getOptions()
                .stream()
                .filter(option -> option.isCorrect())
                .count();

        if (correctCount != 1) {
            throw new IllegalArgumentException(
                    "Each question must have exactly one correct option");
        }

        for (var option : request.getOptions()) {
            if (option == null
                    || option.getOptionText() == null
                    || option.getOptionText().isBlank()) {
                throw new IllegalArgumentException(
                        "Option text cannot be empty");
            }
        }

                Quiz quiz = getQuizOrThrow(quizId);
                if (quiz.isArchived()) {
                        throw new IllegalArgumentException("Archived quizzes cannot receive questions");
                }

        QuizQuestion question = new QuizQuestion();
        question.setQuestionText(request.getQuestionText().trim());
        question.setMarks(request.getMarks());
        question.setQuiz(quiz);

        question = questionRepo.save(question);

        for (var optionRequest : request.getOptions()) {

            QuizOption option = new QuizOption();
            option.setOptionText(optionRequest.getOptionText().trim());
            option.setCorrect(optionRequest.isCorrect());
            option.setQuestion(question);

            optionRepo.save(option);
        }

        int totalMarks = questionRepo.findByQuizId(quizId)
                .stream()
                .mapToInt(QuizQuestion::getMarks)
                .sum();

        quiz.setTotalMarks(totalMarks);
        quizRepo.save(quiz);

        return toQuestionResponse(question);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuizQuestionResponse> getQuizQuestions(Long quizId) {

        Quiz quiz = getQuizOrThrow(quizId);

        if (!quiz.isActive() || quiz.isArchived()) {
            throw new IllegalArgumentException("Quiz is not active");
        }

        return questionRepo.findByQuizId(quizId)
                .stream()
                .map(this::toQuestionResponse)
                .toList();
    }

    @Override
    public QuizResultResponse startQuiz(
            Long quizId,
            String username) {

        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException(
                    "Authenticated username is required");
        }

        Quiz quiz = getQuizOrThrow(quizId);

        if (!quiz.isActive() || quiz.isArchived()) {
            throw new IllegalArgumentException("Quiz is not active");
        }

        List<QuizQuestion> questions =
                questionRepo.findByQuizId(quizId);

        if (questions.isEmpty()) {
            throw new IllegalArgumentException(
                    "Quiz has no questions yet");
        }

        QuizAttempt attempt = new QuizAttempt();
        attempt.setQuiz(quiz);
        attempt.setEmployeeUsername(username);
        attempt.setScore(0);
        attempt.setTotalMarks(
                questions.stream()
                        .mapToInt(QuizQuestion::getMarks)
                        .sum());
        attempt.setPercentage(0.0);
        attempt.setStartedAt(LocalDateTime.now());
        attempt.setStatus(QuizStatus.IN_PROGRESS);

        return toResultResponse(attemptRepo.save(attempt));
    }

    @Override
    public QuizResultResponse submitQuiz(
            Long attemptId,
            QuizSubmissionRequest request,
            String username) {

        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException(
                    "Authenticated username is required");
        }

        QuizAttempt attempt = attemptRepo
                .findByIdAndEmployeeUsername(attemptId, username)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Quiz attempt not found"));

        if (attempt.getStatus() != QuizStatus.IN_PROGRESS) {
            throw new IllegalArgumentException(
                    "This quiz attempt has already been submitted");
        }

        if (request == null || request.getAnswers() == null) {
            throw new IllegalArgumentException("Answers are required");
        }

        Long quizId = attempt.getQuiz().getId();
        List<QuizQuestion> questions =
                questionRepo.findByQuizId(quizId);

        if (request.getAnswers().size() != questions.size()) {
            throw new IllegalArgumentException(
                    "Please submit one answer for every question");
        }

        // Prevent duplicate question IDs in the submitted request.
        long distinctQuestionCount = request.getAnswers()
                .stream()
                .filter(answer -> answer != null)
                .map(QuizSubmissionRequest.AnswerItem::getQuestionId)
                .filter(id -> id != null)
                .distinct()
                .count();

        if (distinctQuestionCount != questions.size()) {
            throw new IllegalArgumentException(
                    "Submit exactly one answer per question");
        }

        int score = 0;

        for (QuizSubmissionRequest.AnswerItem item
                : request.getAnswers()) {

            if (item == null
                    || item.getQuestionId() == null
                    || item.getSelectedOptionId() == null) {
                throw new IllegalArgumentException(
                        "Question and selected option are required");
            }

            QuizQuestion question = questions.stream()
                    .filter(q -> q.getId().equals(item.getQuestionId()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Question does not belong to this quiz"));

            if (answerRepo.existsByAttemptIdAndQuestionId(
                    attemptId, question.getId())) {
                throw new IllegalArgumentException(
                        "An answer already exists for this question");
            }

            QuizOption selectedOption = optionRepo
                    .findById(item.getSelectedOptionId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Selected option not found"));

            if (selectedOption.getQuestion() == null
                    || !selectedOption.getQuestion().getId()
                            .equals(question.getId())) {
                throw new IllegalArgumentException(
                        "Selected option does not belong to this question");
            }

            QuizAnswer answer = new QuizAnswer();
            answer.setAttempt(attempt);
            answer.setQuestion(question);
            answer.setSelectedOption(selectedOption);
            answerRepo.save(answer);

            if (selectedOption.isCorrect()) {
                score += question.getMarks();
            }
        }

        int totalMarks = questions.stream()
                .mapToInt(QuizQuestion::getMarks)
                .sum();

        attempt.setScore(score);
        attempt.setTotalMarks(totalMarks);
        attempt.setPercentage(totalMarks == 0
                ? 0.0
                : score * 100.0 / totalMarks);
        attempt.setSubmittedAt(LocalDateTime.now());
        attempt.setStatus(QuizStatus.SUBMITTED);

        return toResultResponse(attemptRepo.save(attempt));
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuizResultResponse> getMyResults(String username) {

        return attemptRepo.findByEmployeeUsername(username)
                .stream()
                .map(this::toResultResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public QuizResultResponse getMyResultByAttemptId(
            Long attemptId,
            String username) {

        QuizAttempt attempt = attemptRepo
                .findByIdAndEmployeeUsername(attemptId, username)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Quiz result not found"));

        return toResultResponse(attempt);
    }

    private Quiz getQuizOrThrow(Long quizId) {

        return quizRepo.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found"));
    }

        private DepartmentTopic getTopicOrThrow(Long topicId) {
                return topicRepo.findByIdAndArchivedFalse(topicId)
                                .orElseThrow(() -> new ResourceNotFoundException("Learning topic not found"));
        }

        private QuizDifficulty parseDifficulty(String difficulty) {
                if (difficulty == null || difficulty.isBlank()) {
                        throw new IllegalArgumentException("Quiz difficulty is required");
                }
                try {
                        return QuizDifficulty.valueOf(difficulty.trim().toUpperCase(java.util.Locale.ROOT));
                } catch (IllegalArgumentException exception) {
                        throw new IllegalArgumentException("Quiz difficulty is not supported");
                }
        }

    private QuizResponse toQuizResponse(Quiz quiz) {

        return new QuizResponse(
                quiz.getId(),
                quiz.getTitle(),
                quiz.getDescription(),
                quiz.getTotalMarks(),
                quiz.getDurationMinutes(),
                quiz.isActive(),
                quiz.getCreatedAt(),
                quiz.getTopic() == null ? null : quiz.getTopic().getId(),
                quiz.getTopic() == null ? null : quiz.getTopic().getTitle(),
                quiz.getTopic() == null ? null : quiz.getTopic().getDepartment().getId(),
                quiz.getTopic() == null ? null : quiz.getTopic().getDepartment().getName(),
                quiz.getDifficulty() == null ? null : quiz.getDifficulty().name(),
                questionRepo.findByQuizId(quiz.getId()).size(),
                quiz.isArchived());
    }

    private QuizQuestionResponse toQuestionResponse(
            QuizQuestion question) {

        List<QuizOptionResponse> options =
                optionRepo.findByQuestionId(question.getId())
                        .stream()
                        .map(option -> new QuizOptionResponse(
                                option.getId(),
                                option.getOptionText()))
                        .toList();

        return new QuizQuestionResponse(
                question.getId(),
                question.getQuestionText(),
                question.getMarks(),
                options);
    }

    private QuizResultResponse toResultResponse(
            QuizAttempt attempt) {

    	return new QuizResultResponse(
    	        attempt.getId(),
    	        attempt.getQuiz().getId(),
    	        attempt.getQuiz().getTitle(),
    	        attempt.getScore(),
    	        attempt.getTotalMarks(),
    	        attempt.getPercentage(),
    	        attempt.getStatus().name(),
    	        attempt.getStartedAt(),
    	        attempt.getSubmittedAt()
    	);
}
}

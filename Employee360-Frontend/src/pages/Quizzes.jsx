import { useCallback, useEffect, useState } from "react";
import { useSearchParams } from "react-router-dom";
import api from "../api/api";
import ConfirmDialog from "../components/ConfirmDialog";
import LoadingSpinner from "../components/LoadingSpinner";
import useAuth from "../hooks/useAuth";

function getError(error) {
  const data = error.response?.data;
  return typeof data === "string" ? data : data?.message || error.message || "The quiz request failed.";
}

function getDepartmentName(quiz) {
  const department = quiz.departmentName || quiz.department?.name || quiz.department || "";
  return typeof department === "string" ? department : "";
}

function getDifficulty(quiz) {
  return quiz.difficulty || quiz.level || "";
}

function getQuizStatus(quiz) {
  if (quiz.archived) return "ARCHIVED";
  return String(quiz.status || (quiz.active ? "PUBLISHED" : "UNPUBLISHED")).toUpperCase();
}

export default function Quizzes() {
  const { role } = useAuth();
  const isAdmin = String(role || "").replace(/^ROLE_/, "").toUpperCase() === "ADMIN";
  const [searchParams, setSearchParams] = useSearchParams();
  const departmentFilter = searchParams.get("department") || "";
  const [difficultyFilter, setDifficultyFilter] = useState("");
  const [statusFilter, setStatusFilter] = useState("");
  const [quizzes, setQuizzes] = useState([]);
  const [results, setResults] = useState([]);
  const [loading, setLoading] = useState(true);
  const [working, setWorking] = useState(false);
  const [error, setError] = useState("");
  const [notice, setNotice] = useState("");
  const [attempt, setAttempt] = useState(null);
  const [questions, setQuestions] = useState([]);
  const [answers, setAnswers] = useState({});
  const [result, setResult] = useState(null);
  const [catalogTopics, setCatalogTopics] = useState([]);
  const [quizFormOpen, setQuizFormOpen] = useState(false);
  const [editingQuiz, setEditingQuiz] = useState(null);
  const [quizForm, setQuizForm] = useState({ title: "", description: "", durationMinutes: "20", topicId: "", difficulty: "BASIC", active: false });
  const [quizToArchive, setQuizToArchive] = useState(null);
  const [questionQuiz, setQuestionQuiz] = useState(null);
  const [questionPreview, setQuestionPreview] = useState([]);
  const [questionForm, setQuestionForm] = useState({ questionText: "", marks: "1", options: ["", "", "", ""], correctIndex: "0" });
  const departmentOptions = [...new Set(quizzes.map(getDepartmentName).filter(Boolean))].sort((left, right) => left.localeCompare(right));
  const difficultyOptions = [...new Set(quizzes.map(getDifficulty).filter(Boolean))].sort((left, right) => left.localeCompare(right));
  const visibleQuizzes = quizzes.filter((quiz) => {
    const departmentMatches = !departmentFilter || (departmentFilter.toLowerCase() === "it"
      ? /\bit\b|information technology/i.test(getDepartmentName(quiz))
      : getDepartmentName(quiz).toLowerCase() === departmentFilter.toLowerCase());
    const difficultyMatches = !difficultyFilter || getDifficulty(quiz).toLowerCase() === difficultyFilter.toLowerCase();
    const statusMatches = !statusFilter || getQuizStatus(quiz) === statusFilter;
    return departmentMatches && difficultyMatches && statusMatches;
  });
  const hasDepartmentMetadata = quizzes.some((quiz) => getDepartmentName(quiz));

  const updateDepartmentFilter = (value) => {
    setSearchParams((current) => {
      const next = new URLSearchParams(current);
      if (value) next.set("department", value);
      else next.delete("department");
      return next;
    });
  };

  const load = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      const [quizResponse, resultResponse] = await Promise.all([
        api.get(isAdmin ? "/api/quizzes/admin" : "/api/quizzes"),
        api.get("/api/quizzes/my-results")
      ]);
      if (!Array.isArray(quizResponse.data) || !Array.isArray(resultResponse.data)) {
        throw new Error("Quiz API returned an unexpected response.");
      }
      setQuizzes(quizResponse.data);
      setResults(resultResponse.data);
      if (isAdmin) {
        const { data } = await api.get("/api/learning/catalog");
        setCatalogTopics((data.departments || []).flatMap((department) => (department.topics || []).map((topic) => ({
          ...topic,
          departmentId: department.id,
          departmentName: department.name
        }))));
      }
    } catch (requestError) {
      setError(getError(requestError));
    } finally {
      setLoading(false);
    }
  }, [isAdmin]);

  useEffect(() => {
    const timer = window.setTimeout(load, 0);
    return () => window.clearTimeout(timer);
  }, [load]);

  const start = async (quiz) => {
    setError("");
    setNotice("");
    setResult(null);
    setWorking(true);
    try {
      const [attemptResponse, questionsResponse] = await Promise.all([
        api.post(`/api/quizzes/${quiz.id}/start`),
        api.get(`/api/quizzes/${quiz.id}/questions`)
      ]);
      if (!Array.isArray(questionsResponse.data)) throw new Error("Quiz questions were returned in an unexpected format.");
      setAttempt(attemptResponse.data);
      setQuestions(questionsResponse.data);
      setAnswers({});
    } catch (requestError) {
      setError(getError(requestError));
    } finally {
      setWorking(false);
    }
  };

  const submit = async (event) => {
    event.preventDefault();
    if (!attempt?.attemptId) {
      setError("The backend did not return an attempt ID; this attempt cannot be submitted.");
      return;
    }
    if (questions.some((question) => !answers[question.id])) {
      setError("Select an answer for every question before submitting.");
      return;
    }
    setError("");
    setWorking(true);
    try {
      const response = await api.post(`/api/quizzes/attempts/${attempt.attemptId}/submit`, {
        answers: questions.map((question) => ({
          questionId: question.id,
          selectedOptionId: Number(answers[question.id])
        }))
      });
      setResult(response.data);
      setAttempt(null);
      setQuestions([]);
      await load();
    } catch (requestError) {
      setError(getError(requestError));
    } finally {
      setWorking(false);
    }
  };

  const openQuizForm = (quiz = null) => {
    setEditingQuiz(quiz);
    setQuizFormOpen(true);
    setQuizForm(quiz ? {
      title: quiz.title || "",
      description: quiz.description || "",
      durationMinutes: String(quiz.durationMinutes || 20),
      topicId: String(quiz.topicId || ""),
      difficulty: quiz.difficulty || "BASIC",
      active: Boolean(quiz.active)
    } : {
      title: "",
      description: "",
      durationMinutes: "20",
      topicId: departmentFilter ? "" : String(catalogTopics[0]?.id || ""),
      difficulty: "BASIC",
      active: false
    });
    setError("");
    setNotice("");
  };

  const saveQuiz = async (event) => {
    event.preventDefault();
    setWorking(true);
    setError("");
    setNotice("");
    const payload = {
      title: quizForm.title.trim(),
      description: quizForm.description.trim(),
      durationMinutes: Number(quizForm.durationMinutes),
      topicId: Number(quizForm.topicId),
      difficulty: quizForm.difficulty,
      active: quizForm.active
    };
    try {
      const response = editingQuiz
        ? await api.put(`/api/quizzes/${editingQuiz.id}`, payload)
        : await api.post("/api/quizzes", payload);
      setNotice(editingQuiz ? "Quiz updated." : "Quiz created. Add questions before publishing it.");
      setEditingQuiz(response.data);
      setQuizFormOpen(false);
      await load();
    } catch (requestError) {
      setError(getError(requestError));
    } finally {
      setWorking(false);
    }
  };

  const archiveQuiz = async () => {
    if (!quizToArchive) return;
    setWorking(true);
    setError("");
    setNotice("");
    try {
      await api.delete(`/api/quizzes/${quizToArchive.id}`);
      setNotice("Quiz archived. Attempts and answer history were preserved.");
      setQuizToArchive(null);
      if (questionQuiz?.id === quizToArchive.id) setQuestionQuiz(null);
      await load();
    } catch (requestError) {
      setError(getError(requestError));
    } finally {
      setWorking(false);
    }
  };

  const openQuestionManager = async (quiz) => {
    setQuestionQuiz(quiz);
    setQuestionPreview([]);
    setQuestionForm({ questionText: "", marks: "1", options: ["", "", "", ""], correctIndex: "0" });
    setError("");
    try {
      const { data } = await api.get(`/api/quizzes/admin/${quiz.id}/questions`);
      setQuestionPreview(data);
    } catch (requestError) {
      setError(getError(requestError));
    }
  };

  const addQuestion = async (event) => {
    event.preventDefault();
    if (!questionQuiz) return;
    const options = questionForm.options
      .map((optionText, index) => ({ optionText: optionText.trim(), correct: String(index) === questionForm.correctIndex }))
      .filter((option) => option.optionText);
    if (options.length < 2 || options.filter((option) => option.correct).length !== 1) {
      setError("Provide at least two options and select exactly one correct answer.");
      return;
    }
    setWorking(true);
    setError("");
    try {
      await api.post(`/api/quizzes/${questionQuiz.id}/questions`, {
        questionText: questionForm.questionText.trim(),
        marks: Number(questionForm.marks),
        options
      });
      setNotice("Question added. Publish the quiz when it is ready.");
      await openQuestionManager(questionQuiz);
      await load();
    } catch (requestError) {
      setError(getError(requestError));
    } finally {
      setWorking(false);
    }
  };

  const publishQuiz = async () => {
    if (!questionQuiz || questionPreview.length === 0) return;
    setWorking(true);
    setError("");
    setNotice("");
    try {
      const response = await api.put(`/api/quizzes/${questionQuiz.id}`, {
        title: questionQuiz.title,
        description: questionQuiz.description || "",
        durationMinutes: Number(questionQuiz.durationMinutes || 20),
        topicId: Number(questionQuiz.topicId || questionQuiz.topic?.id),
        difficulty: questionQuiz.difficulty || "BASIC",
        active: true
      });
      setQuestionQuiz((current) => current?.id === questionQuiz.id
        ? { ...current, ...response.data, active: true }
        : current);
      setNotice("Quiz published and available to employees.");
      await load();
    } catch (requestError) {
      setError(getError(requestError));
    } finally {
      setWorking(false);
    }
  };

  return <div className="page-enter">
    <div className="page-heading"><div><div className="eyebrow">WORKSPACE / LEARNING</div><h1>Quizzes<span>.</span></h1><p>{isAdmin ? "Create quizzes from learning topics, add questions, and publish them for employees." : departmentFilter ? `${departmentFilter} department quizzes. Start a quiz to load its questions and answer them here.` : "Start an available quiz to answer its questions, then review your results."}</p></div><div className="d-flex gap-2"><button type="button" className="btn btn-outline-secondary" onClick={load} disabled={loading}>Refresh</button>{isAdmin && <button type="button" className="btn btn-gold" onClick={() => openQuizForm()}><i className="bi bi-plus-lg me-1" aria-hidden="true" />Add quiz</button>}</div></div>
    {(error || notice) && <div className={`alert ${error ? "alert-danger" : "alert-success"}`} role={error ? "alert" : "status"}>{error || notice}</div>}
    {isAdmin && quizFormOpen && <section className="panel-card mb-4">
      <form className="resource-form" onSubmit={saveQuiz}>
        <div className="resource-form-heading"><h2>{editingQuiz ? "Edit quiz" : "Create quiz"}</h2><button className="btn-close" type="button" aria-label="Close form" onClick={() => setQuizFormOpen(false)} /></div>
        <div className="row g-3">
          <div className="col-12 col-md-6"><label className="form-label" htmlFor="quiz-title">Quiz title</label><input id="quiz-title" className="form-control" value={quizForm.title} onChange={(event) => setQuizForm((current) => ({ ...current, title: event.target.value }))} maxLength="150" required /></div>
          <div className="col-12 col-md-6"><label className="form-label" htmlFor="quiz-topic">Learning topic</label><select id="quiz-topic" className="form-select" value={quizForm.topicId} onChange={(event) => setQuizForm((current) => ({ ...current, topicId: event.target.value }))} required><option value="">Select topic</option>{catalogTopics.map((topic) => <option key={topic.id} value={topic.id}>{topic.departmentName} / {topic.title}</option>)}</select></div>
          <div className="col-12"><label className="form-label" htmlFor="quiz-description">Description</label><textarea id="quiz-description" className="form-control" rows="2" value={quizForm.description} onChange={(event) => setQuizForm((current) => ({ ...current, description: event.target.value }))} maxLength="2000" /></div>
          <div className="col-12 col-md-4"><label className="form-label" htmlFor="quiz-duration">Duration (minutes)</label><input id="quiz-duration" className="form-control" type="number" min="1" max="600" value={quizForm.durationMinutes} onChange={(event) => setQuizForm((current) => ({ ...current, durationMinutes: event.target.value }))} required /></div>
          <div className="col-12 col-md-4"><label className="form-label" htmlFor="quiz-difficulty">Difficulty</label><select id="quiz-difficulty" className="form-select" value={quizForm.difficulty} onChange={(event) => setQuizForm((current) => ({ ...current, difficulty: event.target.value }))} required>{["BASIC", "MEDIUM", "HARD", "DIFFICULT", "EXPERT"].map((level) => <option key={level} value={level}>{level}</option>)}</select></div>
          {editingQuiz && <div className="col-12 col-md-4"><label className="form-label" htmlFor="quiz-status">Publish status</label><select id="quiz-status" className="form-select" value={quizForm.active ? "true" : "false"} onChange={(event) => setQuizForm((current) => ({ ...current, active: event.target.value === "true" }))}><option value="false">Unpublished</option><option value="true">Published</option></select></div>}
        </div>
        {error && <div className="alert alert-danger mt-3" role="alert">{error}</div>}
        <div className="d-flex justify-content-end mt-3"><button className="btn btn-gold" type="submit" disabled={working}>{working ? "Saving..." : editingQuiz ? "Save changes" : "Create quiz"}</button></div>
      </form>
    </section>}
    {isAdmin && questionQuiz && <section className="panel-card mb-4">
      <div className="panel-heading"><div><h2>Questions: {questionQuiz.title}</h2><p>{questionPreview.length} saved question{questionPreview.length === 1 ? "" : "s"}</p></div><div className="d-flex gap-2">{!questionQuiz.active && <button className="btn btn-sm btn-gold" type="button" onClick={publishQuiz} disabled={working || questionPreview.length === 0}>{working ? "Publishing..." : "Publish quiz"}</button>}<button className="btn btn-sm btn-outline-secondary" type="button" onClick={() => setQuestionQuiz(null)}>Close</button></div></div>
      {questionPreview.length > 0 && <ol className="admin-quiz-question-list">{questionPreview.map((question) => <li key={question.id}><strong>{question.questionText}</strong><small>{question.marks} marks · {(question.options || []).length} options</small><ul>{(question.options || []).map((option) => <li key={option.id}>{option.optionText}</li>)}</ul></li>)}</ol>}
      <form className="resource-form" onSubmit={addQuestion}>
        <div className="row g-3">
          <div className="col-12"><label className="form-label" htmlFor="quiz-question-text">Question</label><textarea id="quiz-question-text" className="form-control" rows="2" value={questionForm.questionText} onChange={(event) => setQuestionForm((current) => ({ ...current, questionText: event.target.value }))} required /></div>
          <div className="col-12 col-md-4"><label className="form-label" htmlFor="quiz-question-marks">Marks</label><input id="quiz-question-marks" className="form-control" type="number" min="1" value={questionForm.marks} onChange={(event) => setQuestionForm((current) => ({ ...current, marks: event.target.value }))} required /></div>
          {questionForm.options.map((option, index) => <div className="col-12 col-md-6" key={index}><label className="form-label" htmlFor={`quiz-option-${index}`}>Option {index + 1}</label><div className="input-group"><input id={`quiz-option-${index}`} className="form-control" value={option} onChange={(event) => setQuestionForm((current) => ({ ...current, options: current.options.map((item, itemIndex) => itemIndex === index ? event.target.value : item) }))} required={index < 2} /><span className="input-group-text"><input className="form-check-input mt-0" type="radio" name="correct-option" aria-label={`Mark option ${index + 1} correct`} checked={questionForm.correctIndex === String(index)} onChange={() => setQuestionForm((current) => ({ ...current, correctIndex: String(index) }))} /></span></div></div>)}
        </div>
        <p className="form-text">Select the radio button beside the correct answer.</p>
        <div className="d-flex justify-content-end"><button className="btn btn-gold" type="submit" disabled={working}>{working ? "Saving..." : "Add question"}</button></div>
      </form>
    </section>}
    {result && <section className="panel-card mb-4" role="status"><div className="panel-heading"><div><h2>Quiz submitted</h2><p>{result.quizTitle || "Your result"}</p></div></div><p className="mb-1">Score: <strong>{result.score} / {result.totalMarks}</strong></p><p className="mb-0">Result: <strong>{Number(result.percentage || 0).toFixed(1)}%</strong></p></section>}
    {attempt ? <section className="panel-card mb-4">
      <div className="panel-heading"><div><h2>Quiz in progress</h2><p>{questions.length} question{questions.length === 1 ? "" : "s"}</p></div><button className="btn btn-sm btn-outline-secondary" type="button" onClick={() => { setAttempt(null); setQuestions([]); setAnswers({}); }}>Exit quiz</button></div>
      {questions.length === 0 ? <div className="empty-state"><strong>No questions are configured for this quiz.</strong></div> : <form onSubmit={submit}>
        {questions.map((question, index) => <fieldset className="quiz-question" key={question.id}>
          <legend>{index + 1}. {question.questionText} <small>({question.marks} marks)</small></legend>
          {(question.options || []).map((option) => <label className="quiz-option" key={option.id}>
            <input className="form-check-input" type="radio" name={`question-${question.id}`} value={option.id} checked={String(answers[question.id] || "") === String(option.id)} onChange={(event) => setAnswers((current) => ({ ...current, [question.id]: event.target.value }))} required />
            <span>{option.optionText}</span>
          </label>)}
        </fieldset>)}
        <button className="btn btn-gold mt-3" type="submit" disabled={working}>{working ? "Submitting..." : "Submit answers"}</button>
      </form>}
    </section> : null}
    <section className="panel-card mb-4">
      <div className="panel-heading"><div><h2>{isAdmin ? "Quiz management" : departmentFilter ? `Available ${departmentFilter} quizzes` : "Available quizzes"}</h2><p>{visibleQuizzes.length} quiz{visibleQuizzes.length === 1 ? "" : "zes"}</p></div></div>
      <div className="quiz-filters mb-3">
        <label><span>Department</span><select className="form-select form-select-sm" value={departmentFilter} onChange={(event) => updateDepartmentFilter(event.target.value)}><option value="">All departments</option>{departmentOptions.map((department) => <option key={department} value={department}>{department}</option>)}</select></label>
        <label><span>Difficulty</span><select className="form-select form-select-sm" value={difficultyFilter} onChange={(event) => setDifficultyFilter(event.target.value)}><option value="">All levels</option>{difficultyOptions.map((difficulty) => <option key={difficulty} value={difficulty}>{difficulty}</option>)}</select></label>
        <label><span>Status</span><select className="form-select form-select-sm" value={statusFilter} onChange={(event) => setStatusFilter(event.target.value)}><option value="">All statuses</option><option value="PUBLISHED">Published</option><option value="UNPUBLISHED">Unpublished</option>{isAdmin && <option value="ARCHIVED">Archived</option>}</select></label>
      </div>
      {loading ? <LoadingSpinner label="Loading quizzes..." /> : visibleQuizzes.length === 0 ? <div className="empty-state"><span className="empty-state-icon"><i className="bi bi-patch-question" /></span><strong>{departmentFilter ? `No ${departmentFilter} quizzes are currently available` : "No quizzes are currently available"}</strong><p>{departmentFilter && !hasDepartmentMetadata ? "The quiz API does not include department tags yet, so department-specific quizzes cannot be identified." : "Active quizzes published by your organization will appear here."}</p></div> :
        <div className="row g-3">{visibleQuizzes.map((quiz) => <div className="col-12 col-md-6 col-xl-4" key={quiz.id}><article className="quiz-card">
          <div className="quiz-card-icon"><i className="bi bi-patch-question" aria-hidden="true" /></div><h3>{quiz.title}</h3><p>{quiz.description || "No description provided."}</p>
          <div className="quiz-meta"><span>{getDepartmentName(quiz) || "Department not set"}</span><span>{quiz.topicTitle || quiz.topic?.title || quiz.topicName || quiz.learningTopic || "Topic not set"}</span><span>{getDifficulty(quiz) || "Level not set"}</span><span>{getQuizStatus(quiz)}</span><span>{quiz.questionCount ?? "—"} questions</span><span><i className="bi bi-award me-1" />{quiz.totalMarks} marks</span>{quiz.durationMinutes > 0 && <span><i className="bi bi-clock me-1" />{quiz.durationMinutes} min</span>}</div>
          {isAdmin ? <div className="quiz-admin-actions">
            <button className="btn btn-sm btn-outline-secondary" type="button" onClick={() => openQuizForm(quiz)} disabled={quiz.archived}><i className="bi bi-pencil me-1" aria-hidden="true" />Edit</button>
            <button className="btn btn-sm btn-outline-secondary" type="button" onClick={() => openQuestionManager(quiz)} disabled={quiz.archived}><i className="bi bi-list-check me-1" aria-hidden="true" />Questions</button>
            {!quiz.archived && <button className="btn btn-sm btn-outline-danger" type="button" onClick={() => setQuizToArchive({ id: quiz.id, title: quiz.title })}><i className="bi bi-archive me-1" aria-hidden="true" />Archive</button>}
          </div> : <button className="btn btn-gold mt-3" type="button" disabled={working || !quiz.active || Boolean(attempt)} onClick={() => start(quiz)}>{quiz.active ? "Start quiz" : "Unavailable"}</button>}
        </article></div>)}</div>}
    </section>
    <section className="panel-card">
      <div className="panel-heading"><div><h2>Your results</h2><p>{results.length} completed attempt{results.length === 1 ? "" : "s"}</p></div></div>
      {results.length === 0 ? <p className="text-secondary mb-0">Completed quiz results will appear here.</p> : <div className="table-responsive"><table className="table align-middle resource-table"><thead><tr><th>Quiz</th><th>Score</th><th>Percentage</th><th>Status</th><th>Submitted</th></tr></thead><tbody>{results.map((item) => <tr key={item.attemptId}><td>{item.quizTitle}</td><td>{item.score} / {item.totalMarks}</td><td>{Number(item.percentage || 0).toFixed(1)}%</td><td>{item.status || "—"}</td><td>{item.submittedAt ? new Date(item.submittedAt).toLocaleString() : "—"}</td></tr>)}</tbody></table></div>}
    </section>
    <ConfirmDialog
      open={Boolean(quizToArchive)}
      title="Archive quiz?"
      message={quizToArchive ? `“${quizToArchive.title}” will no longer be available to employees. Existing attempts and answers will be preserved.` : ""}
      confirmLabel={working ? "Archiving..." : "Archive quiz"}
      onConfirm={archiveQuiz}
      onCancel={() => { if (!working) setQuizToArchive(null); }}
      danger
    />
  </div>;
}

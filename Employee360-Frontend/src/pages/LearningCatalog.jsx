import { useCallback, useEffect, useMemo, useState } from "react";
import { Link } from "react-router-dom";
import api from "../api/api";
import ConfirmDialog from "../components/ConfirmDialog";
import LoadingSpinner from "../components/LoadingSpinner";
import useAuth from "../hooks/useAuth";

const difficultyLabels = {
  BASIC: "Basic",
  MEDIUM: "Medium",
  HARD: "Hard",
  DIFFICULT: "Difficult",
  EXPERT: "Expert"
};

function getError(error) {
  const data = error.response?.data;
  return typeof data === "string" ? data : data?.message || error.message || "The learning catalog could not be loaded.";
}

export default function LearningCatalog() {
  const { role } = useAuth();
  const isAdmin = String(role || "").replace(/^ROLE_/, "").toUpperCase() === "ADMIN";
  const [catalog, setCatalog] = useState(null);
  const [selectedDepartmentId, setSelectedDepartmentId] = useState("");
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [deleting, setDeleting] = useState(false);
  const [error, setError] = useState("");
  const [notice, setNotice] = useState("");
  const [editingTopic, setEditingTopic] = useState(null);
  const [topicFormOpen, setTopicFormOpen] = useState(false);
  const [topicForm, setTopicForm] = useState({ departmentId: "", title: "", outline: "" });
  const [topicToDelete, setTopicToDelete] = useState(null);

  const loadCatalog = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      const { data } = await api.get("/api/learning/catalog");
      if (!Array.isArray(data?.departments)) throw new Error("The learning catalog returned an unexpected response.");
      setCatalog(data);
    } catch (requestError) {
      setError(getError(requestError));
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    const timer = window.setTimeout(loadCatalog, 0);
    return () => window.clearTimeout(timer);
  }, [loadCatalog]);

  const beginCreate = () => {
    setEditingTopic(null);
    setTopicFormOpen(true);
    setTopicForm({ departmentId: selectedDepartmentId || String(catalog?.departments[0]?.id || ""), title: "", outline: "" });
    setError("");
    setNotice("");
  };

  const beginEdit = (department, topic) => {
    setEditingTopic(topic);
    setTopicFormOpen(true);
    setTopicForm({ departmentId: String(department.id), title: topic.title, outline: topic.outline || "" });
    setError("");
    setNotice("");
  };

  const saveTopic = async (event) => {
    event.preventDefault();
    setSaving(true);
    setError("");
    setNotice("");
    try {
      const request = {
        departmentId: Number(topicForm.departmentId),
        title: topicForm.title.trim(),
        outline: topicForm.outline.trim()
      };
      if (editingTopic) {
        await api.put(`/api/learning/catalog/topics/${editingTopic.id}`, request);
        setNotice("Learning topic updated.");
      } else {
        await api.post("/api/learning/catalog/topics", request);
        setNotice("Learning topic added.");
      }
      setEditingTopic(null);
      setTopicFormOpen(false);
      setTopicForm({ departmentId: "", title: "", outline: "" });
      await loadCatalog();
    } catch (requestError) {
      setError(getError(requestError));
    } finally {
      setSaving(false);
    }
  };

  const archiveTopic = async () => {
    if (!topicToDelete) return;
    setDeleting(true);
    setError("");
    setNotice("");
    try {
      await api.delete(`/api/learning/catalog/topics/${topicToDelete.id}`);
      setNotice("Learning topic archived. Its learning slots and planned levels were preserved.");
      setTopicToDelete(null);
      await loadCatalog();
    } catch (requestError) {
      setError(getError(requestError));
    } finally {
      setDeleting(false);
    }
  };

  const visibleDepartments = useMemo(() => {
    const departments = catalog?.departments || [];
    return selectedDepartmentId
      ? departments.filter((department) => String(department.id) === selectedDepartmentId)
      : departments;
  }, [catalog, selectedDepartmentId]);
  const visibleTopics = visibleDepartments.reduce((total, department) => total + department.topics.length, 0);
  const visibleSlots = visibleDepartments.reduce((total, department) => total + department.topics.reduce((topicTotal, topic) => topicTotal + topic.slots.length, 0), 0);
  const plannedQuestions = visibleDepartments.reduce((total, department) => total + department.topics.reduce((topicTotal, topic) => topicTotal + topic.slots.reduce((slotTotal, slot) => slotTotal + slot.levels.reduce((levelTotal, level) => levelTotal + level.plannedQuestionCount, 0), 0), 0), 0);

  return <div className="page-enter">
    <div className="page-heading">
      <div>
        <div className="eyebrow">WORKSPACE / LEARNING</div>
        <h1>Learning catalog<span>.</span></h1>
        <p>Browse learning topics and planned question coverage across departments.</p>
      </div>
      <div className="page-heading-symbol"><i className="bi bi-mortarboard" aria-hidden="true" /></div>
    </div>

    {loading && <LoadingSpinner label="Loading learning catalog..." />}
    {error && <div className="alert alert-danger" role="alert">{error}</div>}
    {!loading && !error && catalog && <>
      <div className="row g-3 mb-4">
        {[
          ["Departments", visibleDepartments.length, "bi-diagram-3"],
          ["Topics", visibleTopics, "bi-journal-bookmark"],
          ["Learning slots", visibleSlots, "bi-grid-3x3"],
          ["Planned questions", plannedQuestions, "bi-patch-question"]
        ].map(([label, value, icon]) => (
          <div className="col-6 col-xl-3" key={label}>
            <div className="stat-card learning-stat">
              <div className="stat-icon"><i className={`bi ${icon}`} aria-hidden="true" /></div>
              <div className="stat-label">{label}</div>
              <div className="stat-value">{Number(value || 0).toLocaleString()}</div>
            </div>
          </div>
        ))}
      </div>

      <div className="learning-catalog-layout">
        <aside className="panel-card learning-department-nav">
          <label className="form-label" htmlFor="learning-department-select">Department</label>
          <select
            id="learning-department-select"
            className="form-select"
            value={selectedDepartmentId}
            onChange={(event) => setSelectedDepartmentId(event.target.value)}
          >
            <option value="">All departments</option>
            {catalog.departments.map((department) => <option key={department.id} value={String(department.id)}>{department.name}</option>)}
          </select>
          <p className="learning-department-hint mt-2">{visibleDepartments.length} department{visibleDepartments.length === 1 ? "" : "s"} in view</p>
        </aside>

        <section className="panel-card learning-topic-panel">
          {(error || notice) && <div className={`alert py-2 ${error ? "alert-danger" : "alert-success"}`} role={error ? "alert" : "status"}>{error || notice}</div>}
          {isAdmin && topicFormOpen && <form className="resource-form mb-3" onSubmit={saveTopic}>
            <div className="resource-form-heading"><h3>{editingTopic ? "Edit learning topic" : "Add learning topic"}</h3><button className="btn-close" type="button" aria-label="Close form" onClick={() => { setTopicFormOpen(false); setEditingTopic(null); setTopicForm({ departmentId: "", title: "", outline: "" }); }} /></div>
            <div className="row g-3">
              <div className="col-12 col-md-6"><label className="form-label" htmlFor="learning-topic-department">Department</label><select id="learning-topic-department" className="form-select" value={topicForm.departmentId} onChange={(event) => setTopicForm((current) => ({ ...current, departmentId: event.target.value }))} required><option value="">Select department</option>{catalog.departments.map((department) => <option key={department.id} value={String(department.id)}>{department.name}</option>)}</select></div>
              <div className="col-12 col-md-6"><label className="form-label" htmlFor="learning-topic-title">Learning title</label><input id="learning-topic-title" className="form-control" value={topicForm.title} onChange={(event) => setTopicForm((current) => ({ ...current, title: event.target.value }))} maxLength="150" required /></div>
              <div className="col-12"><label className="form-label" htmlFor="learning-topic-outline">Description</label><textarea id="learning-topic-outline" className="form-control" rows="3" value={topicForm.outline} onChange={(event) => setTopicForm((current) => ({ ...current, outline: event.target.value }))} maxLength="2000" required /></div>
            </div>
            <div className="d-flex justify-content-end mt-3"><button className="btn btn-gold" type="submit" disabled={saving || deleting}>{saving ? "Saving..." : "Save topic"}</button></div>
          </form>}
          {visibleDepartments.length ? <>
            <div className="panel-heading">
              <div>
                <h2>{selectedDepartmentId ? visibleDepartments[0].name : "All departments"}</h2>
                <p>{selectedDepartmentId ? visibleDepartments[0].description : "Learning topics grouped by department."}</p>
              </div>
              <div className="d-flex align-items-center gap-2"><span className="role-badge">{visibleTopics} topics</span>{isAdmin && <button className="btn btn-sm btn-gold" type="button" onClick={beginCreate}><i className="bi bi-plus-lg me-1" aria-hidden="true" />Add topic</button>}</div>
            </div>
            <div className="learning-department-groups">
              {visibleDepartments.map((department) => <section className="learning-department-group" key={department.id}>
                {!selectedDepartmentId && <div className="learning-department-group-heading"><h3>{department.name}</h3><span>{department.topics.length} topics</span></div>}
                <div className="learning-topic-list">
                  {department.topics.map((topic) => (
                <details className="learning-topic" key={topic.id}>
                  <summary>
                    <span className="learning-topic-number">{String(topic.topicOrder).padStart(2, "0")}</span>
                    <span className="learning-topic-heading">
                      <strong>{topic.title}</strong>
                      <small>{topic.slots.length} learning slots</small>
                    </span>
                    <i className="bi bi-chevron-down" aria-hidden="true" />
                  </summary>
                  <p className="learning-topic-outline">{topic.outline}</p>
                  {isAdmin && <div className="learning-topic-admin-actions"><button className="btn btn-sm btn-outline-secondary" type="button" onClick={() => beginEdit(department, topic)}><i className="bi bi-pencil me-1" aria-hidden="true" />Edit</button><button className="btn btn-sm btn-outline-danger" type="button" onClick={() => setTopicToDelete({ id: topic.id, title: topic.title })}><i className="bi bi-archive me-1" aria-hidden="true" />Archive</button></div>}
                  <div className="learning-slot-grid">
                    {topic.slots.map((slot) => (
                      <article className="learning-slot" key={slot.id}>
                        <strong>{slot.title}</strong>
                        <small>{slot.levels.reduce((count, level) => count + level.plannedQuestionCount, 0)} planned questions</small>
                        <div className="learning-level-list">
                          {slot.levels.map((level) => (
                            <span className={`learning-level level-${level.difficulty.toLowerCase()}`} key={level.difficulty}>
                              {difficultyLabels[level.difficulty] || level.difficulty}
                              <b>{level.plannedQuestionCount}</b>
                            </span>
                          ))}
                        </div>
                      </article>
                    ))}
                  </div>
                </details>
                  ))}
                </div>
              </section>)}
            </div>
          </> : <div className="empty-state"><strong>No department topics are available.</strong></div>}
        </section>
      </div>
      <div className="learning-catalog-note">
        <span><i className="bi bi-info-circle me-2" aria-hidden="true" />This catalog shows planned topics; quiz questions are managed separately.</span>
        <Link className="btn btn-gold btn-sm" to="/quizzes">Go to quizzes <i className="bi bi-arrow-up-right ms-1" aria-hidden="true" /></Link>
      </div>
    </>}
    <ConfirmDialog
      open={Boolean(topicToDelete)}
      title="Archive learning topic?"
      message={topicToDelete ? `“${topicToDelete.title}” will be removed from the active catalog. Its slots and planned levels will be preserved.` : ""}
      confirmLabel={deleting ? "Archiving..." : "Archive topic"}
      onConfirm={archiveTopic}
      onCancel={() => { if (!deleting) setTopicToDelete(null); }}
      danger
    />
  </div>;
}

import { useCallback, useEffect, useMemo, useState } from "react";
import useAuth from "../hooks/useAuth";
import api from "../api/api";
import LoadingSpinner from "./LoadingSpinner";

function messageFrom(error) {
  const response = error.response?.data;
  if (typeof response === "string") return response;
  if (response?.message || response?.error) return response.message || response.error;
  if (response && typeof response === "object") {
    const details = Object.values(response)
      .flatMap((value) => Array.isArray(value) ? value : [value])
      .filter((value) => typeof value === "string" && value.trim());
    if (details.length) return details.join(" ");
  }
  return error.message || "The request could not be completed.";
}

function getUrl(endpoint, item) {
  return typeof endpoint === "function" ? endpoint(item) : endpoint;
}

function getRows(data) {
  if (Array.isArray(data)) return data;
  if (Array.isArray(data?.content)) return data.content;
  throw new Error("The API response was not a list.");
}

export default function ResourcePage({
  title,
  description,
  icon,
  endpoint,
  columns,
  fields = [],
  create,
  update,
  remove,
  allowRoles,
  manageRoles = allowRoles,
  rowActions = [],
  keyField = "id",
  emptyMessage = "No records are available yet."
}) {
  const { role } = useAuth();
  const normalizedRole = (role || "").replace(/^ROLE_/, "").toUpperCase();
  const canView = !allowRoles || allowRoles.includes(normalizedRole);
  const canManage = Boolean(create || update || remove || rowActions.length) && (!manageRoles || manageRoles.includes(normalizedRole));
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const [notice, setNotice] = useState("");
  const [query, setQuery] = useState("");
  const [editing, setEditing] = useState(null);
  const [values, setValues] = useState({});

  const load = useCallback(async () => {
    if (!canView) {
      setLoading(false);
      return;
    }
    setLoading(true);
    setError("");
    try {
      const response = await api.get(endpoint);
      setItems(getRows(response.data));
    } catch (requestError) {
      setError(messageFrom(requestError));
    } finally {
      setLoading(false);
    }
  }, [canView, endpoint]);

  useEffect(() => {
    const timer = window.setTimeout(load, 0);
    return () => window.clearTimeout(timer);
  }, [load]);

  const visibleItems = useMemo(() => {
    const needle = query.trim().toLocaleLowerCase();
    if (!needle) return items;
    return items.filter((item) => columns.some(({ key, searchValue }) =>
      String(searchValue ? searchValue(item) ?? "" : item[key] ?? "").toLocaleLowerCase().includes(needle)
    ));
  }, [columns, items, query]);

  const startCreate = () => {
    setEditing(null);
    setValues(Object.fromEntries(fields.map((field) => [field.name, field.defaultValue ?? ""])));
    setError("");
    setNotice("");
  };

  const startEdit = (item) => {
    setEditing(item);
    setValues(Object.fromEntries(fields.map((field) => [
      field.name,
      item[field.name] == null ? "" : String(item[field.name])
    ])));
    setError("");
    setNotice("");
  };

  const submit = async (event) => {
    event.preventDefault();
    const operation = editing ? update : create;
    if (!operation) return;
    setSaving(true);
    setError("");
    try {
      const body = operation.serialize ? operation.serialize(values) : values;
      await api.request({
        method: operation.method,
        url: getUrl(operation.url, editing),
        data: body
      });
      setEditing(null);
      setValues({});
      setNotice(editing ? "Record updated." : "Record created.");
      await load();
    } catch (requestError) {
      setError(messageFrom(requestError));
    } finally {
      setSaving(false);
    }
  };

  const deleteItem = async (item) => {
    if (!remove || !window.confirm("Delete this record? This action cannot be undone.")) return;
    setError("");
    setNotice("");
    try {
      await api.delete(getUrl(remove, item));
      setNotice("Record deleted.");
      await load();
    } catch (requestError) {
      setError(messageFrom(requestError));
    }
  };

  const runRowAction = async (action, item) => {
    setError("");
    setNotice("");
    try {
      const message = await action.run(item);
      if (message == null) return;
      setNotice(message || `${action.label} completed.`);
      await load();
    } catch (requestError) {
      setError(messageFrom(requestError));
    }
  };

  if (!canView) {
    return <section className="panel-card access-notice" role="status">
      <i className={`bi ${icon}`} aria-hidden="true" />
      <h1>{title}</h1>
      <p>Your account role does not have access to this information.</p>
    </section>;
  }

  return <div className="page-enter">
    <div className="page-heading">
      <div><div className="eyebrow">WORKSPACE / {title.toUpperCase()}</div><h1>{title}<span>.</span></h1><p>{description}</p></div>
      <div className="page-heading-symbol"><i className={`bi ${icon}`} aria-hidden="true" /></div>
    </div>
    <section className="panel-card">
      <div className="panel-heading resource-toolbar">
        <div><h2>{title} directory</h2><p>{items.length} record{items.length === 1 ? "" : "s"}</p></div>
        <div className="resource-actions">
          <label className="visually-hidden" htmlFor={`${title}-search`}>Search {title.toLowerCase()}</label>
          <input id={`${title}-search`} className="form-control" type="search" placeholder={`Search ${title.toLowerCase()}...`} value={query} onChange={(event) => setQuery(event.target.value)} />
          {canManage && create && <button className="btn btn-gold" type="button" onClick={startCreate}><i className="bi bi-plus-lg me-1" />Add {title.slice(0, -1) || title}</button>}
        </div>
      </div>
      {notice && <div className="alert alert-success py-2" role="status">{notice}</div>}
      {error && <div className="alert alert-danger py-2" role="alert">{error}</div>}
      {canManage && fields.length > 0 && (editing || values && Object.keys(values).length > 0) &&
        <form className="resource-form" onSubmit={submit}>
          <div className="resource-form-heading"><h3>{editing ? `Edit ${title.slice(0, -1).toLowerCase()}` : `Add ${title.slice(0, -1).toLowerCase()}`}</h3><button type="button" className="btn-close" aria-label="Close form" onClick={() => { setEditing(null); setValues({}); }} /></div>
          <div className="row g-3">
            {fields.map((field) => <div className={field.wide ? "col-12" : "col-12 col-md-6"} key={field.name}>
              <label className="form-label" htmlFor={`${title}-${field.name}`}>{field.label}</label>
              {field.type === "select" ?
                <select id={`${title}-${field.name}`} className="form-select" required={field.required} value={values[field.name] ?? ""} onChange={(event) => setValues((current) => ({ ...current, [field.name]: event.target.value }))}>
                  <option value="">Select {field.label.toLowerCase()}</option>
                  {field.options.map((option) => <option value={option.value} key={option.value}>{option.label}</option>)}
                </select> :
                field.type === "textarea" ?
                  <textarea id={`${title}-${field.name}`} className="form-control" rows="3" maxLength={field.maxLength} required={field.required} value={values[field.name] ?? ""} onChange={(event) => setValues((current) => ({ ...current, [field.name]: event.target.value }))} /> :
                  <input id={`${title}-${field.name}`} className="form-control" type={field.type || "text"} min={field.min} step={field.step} max={field.max} maxLength={field.maxLength} required={field.required} value={values[field.name] ?? ""} onChange={(event) => setValues((current) => ({ ...current, [field.name]: event.target.value }))} />}
            </div>)}
          </div>
          <div className="d-flex gap-2 justify-content-end mt-3">
            <button type="button" className="btn btn-outline-secondary" onClick={() => { setEditing(null); setValues({}); }}>Cancel</button>
            <button type="submit" className="btn btn-gold" disabled={saving}>{saving ? "Saving..." : "Save"}</button>
          </div>
        </form>}
      {loading ? <LoadingSpinner label={`Loading ${title.toLowerCase()}...`} /> :
        items.length === 0 ? <div className="empty-state"><span className="empty-state-icon"><i className={`bi ${icon}`} /></span><strong>{emptyMessage}</strong></div> :
          visibleItems.length === 0 ? <div className="empty-state"><strong>No matching records</strong><p>Try a different search.</p></div> :
            <div className="table-responsive"><table className="table align-middle resource-table">
              <thead><tr>{columns.map((column) => <th key={column.key}>{column.label}</th>)}{canManage && (update || remove || rowActions.length > 0) && <th scope="col">Actions</th>}</tr></thead>
              <tbody>{visibleItems.map((item, index) => <tr key={item[keyField] ?? index}>
                {columns.map((column) => <td key={column.key}>{column.render ? column.render(item) : String(item[column.key] ?? "—")}</td>)}
                {canManage && (update || remove || rowActions.length > 0) && <td className="text-nowrap">
                  {update && <button className="btn btn-sm btn-outline-secondary me-2" type="button" onClick={() => startEdit(item)} aria-label={`Edit ${title.slice(0, -1)}`}><i className="bi bi-pencil" /></button>}
                  {remove && <button className="btn btn-sm btn-outline-danger" type="button" onClick={() => deleteItem(item)} aria-label={`Delete ${title.slice(0, -1)}`}><i className="bi bi-trash" /></button>}
                  {rowActions.map((action) => <button className={`btn btn-sm ${action.className || "btn-outline-secondary"} me-2`} type="button" key={action.label} disabled={action.disabled?.(item)} onClick={() => runRowAction(action, item)}>
                    {action.icon && <i className={`bi ${action.icon} me-1`} aria-hidden="true" />}{action.label}
                  </button>)}
                </td>}
              </tr>)}</tbody>
            </table></div>}
    </section>
  </div>;
}

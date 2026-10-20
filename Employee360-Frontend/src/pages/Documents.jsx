import { useCallback, useEffect, useState } from "react";
import api from "../api/api";
import LoadingSpinner from "../components/LoadingSpinner";

const MAX_FILE_SIZE = 10 * 1024 * 1024;
const allowedTypes = new Set([
  "application/pdf",
  "image/jpeg",
  "image/png",
  "application/msword",
  "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
]);

function getError(error) {
  const data = error.response?.data;
  return typeof data === "string" ? data : data?.message || error.message || "The document request failed.";
}

function formatSize(bytes) {
  if (!Number.isFinite(bytes)) return "—";
  if (bytes < 1024) return `${bytes} B`;
  if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`;
  return `${(bytes / (1024 * 1024)).toFixed(1)} MB`;
}

export default function Documents() {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [uploading, setUploading] = useState(false);
  const [deletingId, setDeletingId] = useState(null);
  const [error, setError] = useState("");
  const [notice, setNotice] = useState("");
  const [documentName, setDocumentName] = useState("");
  const [documentType, setDocumentType] = useState("OTHER");
  const [file, setFile] = useState(null);

  const load = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      const response = await api.get("/api/documents");
      if (!Array.isArray(response.data)) throw new Error("The documents endpoint did not return a list.");
      setItems(response.data);
    } catch (requestError) {
      setError(getError(requestError));
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    const timer = window.setTimeout(load, 0);
    return () => window.clearTimeout(timer);
  }, [load]);

  const upload = async (event) => {
    event.preventDefault();
    setError("");
    setNotice("");
    if (!file) {
      setError("Choose a file to upload.");
      return;
    }
    if (file.size > MAX_FILE_SIZE) {
      setError("The file exceeds the 10 MB upload limit.");
      return;
    }
    if (!allowedTypes.has(file.type)) {
      setError("Choose a PDF, JPEG, PNG, DOC, or DOCX file.");
      return;
    }
    const form = new FormData();
    form.append("file", file);
    form.append("documentName", documentName.trim() || file.name);
    form.append("documentType", documentType);
    setUploading(true);
    try {
      await api.post("/api/documents", form, { headers: { "Content-Type": "multipart/form-data" } });
      setNotice("Document uploaded.");
      setDocumentName("");
      setDocumentType("OTHER");
      setFile(null);
      event.currentTarget.reset();
      await load();
    } catch (requestError) {
      setError(getError(requestError));
    } finally {
      setUploading(false);
    }
  };

  const download = async (item) => {
    setError("");
    try {
      const response = await api.get(`/api/documents/${item.id}/download`, { responseType: "blob" });
      const url = URL.createObjectURL(response.data);
      const anchor = document.createElement("a");
      anchor.href = url;
      anchor.download = item.documentName || "employee-document";
      document.body.appendChild(anchor);
      anchor.click();
      anchor.remove();
      URL.revokeObjectURL(url);
    } catch (requestError) {
      setError(getError(requestError));
    }
  };

  const remove = async (item) => {
    if (!window.confirm(`Delete "${item.documentName}"? This cannot be undone.`)) return;
    setError("");
    setNotice("");
    setDeletingId(item.id);
    try {
      await api.delete(`/api/documents/${item.id}`);
      setNotice("Document deleted.");
      await load();
    } catch (requestError) {
      setError(getError(requestError));
    } finally {
      setDeletingId(null);
    }
  };

  return <div className="page-enter">
    <div className="page-heading"><div><div className="eyebrow">WORKSPACE / DOCUMENTS</div><h1>Documents<span>.</span></h1><p>Upload, download, and manage your own employee documents.</p></div><div className="page-heading-symbol"><i className="bi bi-folder2" aria-hidden="true" /></div></div>
    <section className="panel-card mb-4">
      <div className="panel-heading"><div><h2>Upload document</h2><p>PDF, JPEG, PNG, DOC, and DOCX up to 10 MB</p></div></div>
      <form onSubmit={upload} className="resource-form">
        <div className="row g-3">
          <div className="col-12 col-md-6"><label className="form-label" htmlFor="document-name">Document name</label><input className="form-control" id="document-name" value={documentName} onChange={(event) => setDocumentName(event.target.value)} placeholder="Defaults to the file name" maxLength="200" /></div>
          <div className="col-12 col-md-6"><label className="form-label" htmlFor="document-type">Document type</label><select id="document-type" className="form-select" value={documentType} onChange={(event) => setDocumentType(event.target.value)}><option value="RESUME">Resume</option><option value="ID_PROOF">ID proof</option><option value="CERTIFICATE">Certificate</option><option value="HEALTH">Health</option><option value="OTHER">Other</option></select></div>
          <div className="col-12"><label className="form-label" htmlFor="document-file">File</label><input className="form-control" id="document-file" type="file" accept=".pdf,.jpg,.jpeg,.png,.doc,.docx" onChange={(event) => setFile(event.target.files?.[0] || null)} required /></div>
        </div>
        <button className="btn btn-gold mt-3" type="submit" disabled={uploading}>{uploading ? "Uploading..." : "Upload document"}</button>
      </form>
    </section>
    <section className="panel-card">
      <div className="panel-heading"><div><h2>Your documents</h2><p>{items.length} record{items.length === 1 ? "" : "s"}</p></div><button className="btn btn-sm btn-outline-secondary" type="button" onClick={load} disabled={loading}>Refresh</button></div>
      {(error || notice) && <div className={`alert py-2 ${error ? "alert-danger" : "alert-success"}`} role={error ? "alert" : "status"}>{error || notice}</div>}
      {loading ? <LoadingSpinner label="Loading documents..." /> : items.length === 0 ? <div className="empty-state"><span className="empty-state-icon"><i className="bi bi-folder2" /></span><strong>No documents uploaded yet</strong><p>Your uploaded documents will be listed here.</p></div> :
        <div className="table-responsive"><table className="table align-middle resource-table"><thead><tr><th>Document</th><th>Type</th><th>Uploaded</th><th>Size</th><th>Actions</th></tr></thead><tbody>{items.map((item) => <tr key={item.id}>
          <td>{item.documentName || "—"}</td><td>{item.documentType || "—"}</td><td>{item.uploadedAt ? new Date(item.uploadedAt).toLocaleString() : "—"}</td><td>{formatSize(item.fileSize)}</td>
          <td className="text-nowrap"><button className="btn btn-sm btn-outline-secondary me-2" type="button" onClick={() => download(item)}><i className="bi bi-download me-1" />Download</button><button className="btn btn-sm btn-outline-danger" type="button" disabled={deletingId === item.id} onClick={() => remove(item)}><i className="bi bi-trash me-1" />Delete</button></td>
        </tr>)}</tbody></table></div>}
    </section>
  </div>;
}

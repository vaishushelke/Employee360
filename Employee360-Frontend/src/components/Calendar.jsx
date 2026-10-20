import { useMemo, useState } from "react";

function monthMatrix(year, month) {
  const first = new Date(year, month, 1);
  const startDay = first.getDay(); // 0=Sun
  const daysInMonth = new Date(year, month + 1, 0).getDate();
  const weeks = [];
  let week = new Array(7).fill(null);
  let day = 1;
  // fill first week
  for (let i = startDay; i < 7; i++) {
    week[i] = day++;
  }
  weeks.push(week.slice());
  while (day <= daysInMonth) {
    week = new Array(7).fill(null);
    for (let i = 0; i < 7 && day <= daysInMonth; i++) {
      week[i] = day++;
    }
    weeks.push(week.slice());
  }
  return weeks;
}

export default function Calendar({ events = [] }) {
  const today = new Date();
  const [current, setCurrent] = useState({ year: today.getFullYear(), month: today.getMonth() });

  const eventMap = useMemo(() => {
    const map = {};
    for (const ev of events) {
      // expect date in YYYY-MM-DD
      if (!ev || !ev.date) continue;
      map[ev.date] = (map[ev.date] || []).concat(ev);
    }
    return map;
  }, [events]);

  const weeks = useMemo(() => monthMatrix(current.year, current.month), [current]);

  const monthLabel = new Date(current.year, current.month, 1).toLocaleString(undefined, { month: "long", year: "numeric" });

  const prev = () => {
    setCurrent((c) => {
      const m = c.month - 1;
      if (m < 0) return { year: c.year - 1, month: 11 };
      return { year: c.year, month: m };
    });
  };

  const next = () => {
    setCurrent((c) => {
      const m = c.month + 1;
      if (m > 11) return { year: c.year + 1, month: 0 };
      return { year: c.year, month: m };
    });
  };

  const weekdayNames = ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"];

  return (
    <div className="panel-card calendar-panel">
      <div className="panel-heading d-flex align-items-center justify-content-between">
        <div><h3 style={{ margin: 0 }}>Calendar</h3><p className="text-secondary small mb-0">Important events & highlights</p></div>
        <div className="d-flex gap-2 align-items-center">
          <button className="btn btn-sm btn-outline-secondary" type="button" onClick={prev} aria-label="Previous month">‹</button>
          <strong>{monthLabel}</strong>
          <button className="btn btn-sm btn-outline-secondary" type="button" onClick={next} aria-label="Next month">›</button>
        </div>
      </div>
      <div style={{ padding: "0.75rem" }}>
        <div style={{ display: "grid", gridTemplateColumns: "repeat(7, 1fr)", gap: "0.25rem", marginBottom: "0.25rem" }}>
          {weekdayNames.map((w) => <div key={w} style={{ textAlign: "center", fontSize: "0.85rem", color: "#6c757d" }}>{w}</div>)}
        </div>
        <div style={{ display: "grid", gridTemplateColumns: "repeat(7, 1fr)", gap: "0.25rem" }}>
          {weeks.map((week, wi) => week.map((day, di) => {
            const iso = day ? new Date(current.year, current.month, day).toISOString().slice(0, 10) : null;
            const hasEvent = iso && eventMap[iso];
            const isToday = iso === today.toISOString().slice(0, 10);
            return (
              <div key={`${wi}-${di}`} style={{ minHeight: "60px", borderRadius: 6, padding: "0.5rem", background: isToday ? "#f8f9fa" : "transparent", border: "1px solid transparent" }} title={hasEvent ? (hasEvent.map(e => e.title).join(", ")) : undefined}>
                <div style={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start", gap: 8 }}>
                  <div style={{ fontSize: "0.85rem", color: day ? "#212529" : "#adb5bd" }}>{day ?? ""}</div>
                  {hasEvent && <div style={{ background: "#ffc107", color: "#000", padding: "0.15rem 0.45rem", borderRadius: 6, fontSize: "0.75rem" }}> {hasEvent.length} </div>}
                </div>
                {hasEvent && <div style={{ marginTop: 6 }}>
                  {hasEvent.slice(0, 2).map((ev, i) => <div key={i} style={{ fontSize: "0.75rem", whiteSpace: "nowrap", overflow: "hidden", textOverflow: "ellipsis" }}>{ev.title}</div>)}
                  {hasEvent.length > 2 && <div style={{ fontSize: "0.72rem", color: "#6c757d" }}>+{hasEvent.length - 2} more</div>}
                </div>}
              </div>
            );
          }))}
        </div>
      </div>
    </div>
  );
}

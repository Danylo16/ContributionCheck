import { useEffect, useMemo, useState } from "react";

import {
  getImportRecords,
  getImports,
  uploadCsv,
} from "./api";
import type {
  ContributionRecord,
  ImportBatch,
} from "./types";

import "./App.css";

function App() {
  const [imports, setImports] = useState<ImportBatch[]>([]);
  const [selectedId, setSelectedId] = useState<number | null>(
    null,
  );
  const [records, setRecords] = useState<
    ContributionRecord[]
  >([]);
  const [uploading, setUploading] = useState(false);
  const [loadingRecords, setLoadingRecords] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const selectedImport = useMemo(
    () => imports.find((item) => item.id === selectedId),
    [imports, selectedId],
  );

  useEffect(() => {
    void loadImports();
  }, []);

  useEffect(() => {
    if (selectedId === null) {
      setRecords([]);
      return;
    }

    void loadRecords(selectedId);
  }, [selectedId]);

  async function loadImports() {
    try {
      setError(null);

      const data = await getImports();
      setImports(data);

      setSelectedId((currentId) => {
        if (
          currentId !== null &&
          data.some((item) => item.id === currentId)
        ) {
          return currentId;
        }

        return data[0]?.id ?? null;
      });
    } catch (caughtError) {
      setError(getErrorMessage(caughtError));
    }
  }

  async function loadRecords(batchId: number) {
    try {
      setLoadingRecords(true);
      setError(null);

      const data = await getImportRecords(batchId);
      setRecords(data);
    } catch (caughtError) {
      setError(getErrorMessage(caughtError));
    } finally {
      setLoadingRecords(false);
    }
  }

  async function handleFileChange(
    event: React.ChangeEvent<HTMLInputElement>,
  ) {
    const file = event.target.files?.[0];

    if (!file) {
      return;
    }

    try {
      setUploading(true);
      setError(null);

      const createdImport = await uploadCsv(file);

      setImports((current) => [
        createdImport,
        ...current,
      ]);
      setSelectedId(createdImport.id);
    } catch (caughtError) {
      setError(getErrorMessage(caughtError));
    } finally {
      setUploading(false);
      event.target.value = "";
    }
  }

  return (
    <main className="app">
      <header className="page-header">
        <div>
          <p className="eyebrow">Contribution validation</p>
          <h1>ContributionCheck</h1>
          <p className="subtitle">
            Import employee contribution records, validate
            business rules and inspect every issue.
          </p>
        </div>

        <label className={`upload-button ${uploading ? "disabled" : ""}`}>
          <input
            type="file"
            accept=".csv,text/csv"
            disabled={uploading}
            onChange={handleFileChange}
          />
          {uploading ? "Importing…" : "Import CSV"}
        </label>
      </header>

      {error && (
        <div className="error-banner">
          <strong>Request failed</strong>
          <span>{error}</span>
        </div>
      )}

      <div className="workspace">
        <aside className="imports-panel">
          <div className="panel-heading">
            <h2>Imports</h2>
            <span>{imports.length}</span>
          </div>

          <div className="import-list">
            {imports.length === 0 && (
              <p className="empty-message">
                No files imported yet.
              </p>
            )}

            {imports.map((item) => (
              <button
                type="button"
                key={item.id}
                className={
                  item.id === selectedId
                    ? "import-item active"
                    : "import-item"
                }
                onClick={() => setSelectedId(item.id)}
              >
                <div className="import-item-top">
                  <strong>{item.fileName}</strong>
                  <span
                    className={`status status-${item.status.toLowerCase()}`}
                  >
                    {item.status}
                  </span>
                </div>

                <div className="import-meta">
                  <span>{item.totalRecords} records</span>
                  <span>{formatDate(item.createdAt)}</span>
                </div>
              </button>
            ))}
          </div>
        </aside>

        <section className="details-panel">
          {!selectedImport ? (
            <div className="empty-state">
              <h2>Select an import</h2>
              <p>
                Upload a CSV file or select an existing import.
              </p>
            </div>
          ) : (
            <>
              <div className="details-heading">
                <div>
                  <p className="eyebrow">Import #{selectedImport.id}</p>
                  <h2>{selectedImport.fileName}</h2>
                </div>

                <span
                  className={`status status-${selectedImport.status.toLowerCase()}`}
                >
                  {selectedImport.status}
                </span>
              </div>

              <div className="stats-grid">
                 <StatCard
                  label="Total"
                  value={selectedImport.totalRecords}
                />
                <StatCard
                  label="Valid"
                  value={selectedImport.validRecords}
                  variant="valid"
                />
                <StatCard
                  label="Invalid"
                  value={selectedImport.invalidRecords}
                  variant="invalid"
                />
              </div>

              {loadingRecords ? (
                <p className="loading">Loading records…</p>
              ) : (
                <RecordsTable records={records} />
              )}
            </>
          )}
        </section>
      </div>
    </main>
  );
}

interface StatCardProps {
  label: string;
  value: number;
  variant?: "valid" | "invalid";
}

function StatCard({
  label,
  value,
  variant,
}: StatCardProps) {
  return (
    <div className={`stat-card ${variant ?? ""}`}>
      <span>{label}</span>
      <strong>{value}</strong>
    </div>
  );
}

function RecordsTable({
  records,
}: {
  records: ContributionRecord[];
}) {
  if (records.length === 0) {
    return (
      <p className="empty-message">
        This import contains no records.
      </p>
    );
  }

  return (
    <div className="table-wrapper">
      <table>
        <thead>
          <tr>
            <th>Row</th>
            <th>Employee</th>
            <th>Month</th>
            <th>Salary</th>
            <th>Employee contribution</th>
            <th>Employer contribution</th>
            <th>Status</th>
            <th>Issues</th>
          </tr>
        </thead>

        <tbody>
          {records.map((record) => (
            <tr key={record.id}>
              <td>{record.rowNumber}</td>
              <td className="employee-id">
                {record.employeeId}
              </td>
              <td>{record.contributionMonth}</td>
              <td>
                {formatMoney(
                  record.grossSalary,
                  record.currency,
                )}
              </td>
              <td>
                {formatMoney(
                  record.employeeContribution,
                  record.currency,
                )}
              </td>
              <td>
                {formatMoney(
                  record.employerContribution,
                  record.currency,
                )}
              </td>
              <td>
                <span
                  className={`record-status ${record.status.toLowerCase()}`}
                >
                  {record.status}
                </span>
              </td>
              <td className="issues-cell">
                {record.issues.length === 0
                  ? "—"
                  : record.issues.map((issue) => (
                      <div
                        key={issue.id}
                        className="issue"
                        title={issue.code}
                      >
                        {issue.message}
                      </div>
                    ))}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

function formatMoney(value: number, currency: string) {
  return new Intl.NumberFormat("en-AT", {
    style: "currency",
    currency,
  }).format(value);
}

function formatDate(value: string) {
  return new Intl.DateTimeFormat("en-AT", {
    dateStyle: "medium",
    timeStyle: "short",
  }).format(new Date(value));
}

function getErrorMessage(error: unknown) {
  return error instanceof Error
    ? error.message
    : "An unexpected error occurred";
}

export default App;

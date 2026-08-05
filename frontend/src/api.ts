import type {
  ContributionRecord,
  ImportBatch,
} from "./types";

const API_URL =
  import.meta.env.VITE_API_URL ??
  "http://localhost:8080/api";

async function parseResponse<T>(
  response: Response,
): Promise<T> {
  if (response.ok) {
    return response.json() as Promise<T>;
  }

  const error = (await response.json().catch(() => null)) as {
    message?: string;
  } | null;

  throw new Error(
    error?.message ??
      `Request failed with status ${response.status}`,
  );
}

export async function getImports(): Promise<ImportBatch[]> {
  const response = await fetch(`${API_URL}/imports`);
  return parseResponse<ImportBatch[]>(response);
}

export async function getImportRecords(
  batchId: number,
): Promise<ContributionRecord[]> {
  const response = await fetch(
    `${API_URL}/imports/${batchId}/records`,
  );

  return parseResponse<ContributionRecord[]>(response);
}

export async function uploadCsv(
  file: File,
): Promise<ImportBatch> {
  const formData = new FormData();
  formData.append("file", file);

  const response = await fetch(`${API_URL}/imports`, {
    method: "POST",
    body: formData,
  });

  return parseResponse<ImportBatch>(response);
}
export type ImportStatus = "PROCESSING" | "COMPLETED" | "FAILED";
export type RecordStatus = "VALID" | "INVALID";

export interface ImportBatch {
  id: number;
  fileName: string;
  status: ImportStatus;
  totalRecords: number;
  validRecords: number;
  invalidRecords: number;
  createdAt: string;
}

export interface ValidationIssue {
  id: number;
  code: string;
  fieldName: string | null;
  message: string;
}

export interface ContributionRecord {
  id: number;
  rowNumber: number;
  employeeId: string;
  contributionMonth: string;
  grossSalary: number;
  employeeContribution: number;
  employerContribution: number;
  currency: string;
  status: RecordStatus;
  issues: ValidationIssue[];
}
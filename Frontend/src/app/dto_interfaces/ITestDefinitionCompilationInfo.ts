import {ICompilationDiagnostic} from "./ICompilationDiagnostic";

export interface ITestDefinitionCompilationInfo{
  status: string;
  errors: ICompilationDiagnostic[];
  warnings: ICompilationDiagnostic[];
}

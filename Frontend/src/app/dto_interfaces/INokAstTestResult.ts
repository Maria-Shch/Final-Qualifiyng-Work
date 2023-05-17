import {IViolation} from "./IViolation";

export interface INokAstTestResult {
  node: string;
  violations: IViolation[];
}

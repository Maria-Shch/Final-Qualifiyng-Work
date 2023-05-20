import {IViolation} from "./IViolation";

export interface INokAstOrReflexivityTestResult {
  node: string;
  violations: IViolation[];
}

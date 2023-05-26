import {IViolation} from "./IViolation";

export interface INokAstOrReflexivityOrCheckstyleTestResult {
  node: string;
  violations: IViolation[];
}

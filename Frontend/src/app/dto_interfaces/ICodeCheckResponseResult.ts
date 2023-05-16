import {ICodeTestResult} from "./ICodeTestResult";

export interface ICodeCheckResponseResult{
  code: string;
  message: string;
  results: ICodeTestResult[];
}

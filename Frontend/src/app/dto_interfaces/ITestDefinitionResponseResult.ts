import {ITestDefinitionValidationInfo} from "./ITestDefinitionValidationInfo";
import {ITestDefinitionCompilationInfo} from "./ITestDefinitionCompilationInfo";
import {ITestDefinitionTechnicalErrorInfo} from "./ITestDefinitionTechnicalErrorInfo";

export interface ITestDefinitionResponseResult{
  code: string;
  message: string;
  codeTest: string;
  validationInfo: ITestDefinitionValidationInfo;
  compilationInfo: ITestDefinitionCompilationInfo;
  technicalErrorInfo: ITestDefinitionTechnicalErrorInfo;
}

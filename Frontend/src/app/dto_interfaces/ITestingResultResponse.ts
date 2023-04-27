import {IStatus} from "../interfaces/IStatus";

export interface ITestingResultResponse{
  status: IStatus | null;
  testingSuccessfulCompleted: boolean;

  //todo не получилось принять enum с бэка
  appError: string;
}

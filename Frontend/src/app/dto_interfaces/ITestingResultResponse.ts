
import {IStatus} from "../interfaces/IStatus";
export interface ITestingResultResponse{
  status: IStatus;
  testingSuccessfulCompleted: boolean;

  //todo не получилось принять enum с бэка
  testError: string;
}

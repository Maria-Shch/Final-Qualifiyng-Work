import {IStatus} from "../interfaces/IStatus";

export interface ISendingOnReviewOrConsiderationResponse{
  status: IStatus;
  sendingSuccessfulCompleted: boolean;

  //todo не получилось принять enum с бэка
  appError: string;
}

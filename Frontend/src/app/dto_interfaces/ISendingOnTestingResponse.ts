import {IAppError} from "./IAppError";
import {IStatus} from "../interfaces/IStatus";

export interface ISendingOnTestingResponse{
  codeSuccessfulSent: boolean;
  status: IStatus;
  sendingError: IAppError;
}

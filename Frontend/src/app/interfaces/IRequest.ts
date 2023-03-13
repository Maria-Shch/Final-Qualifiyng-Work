import {IStudentTask} from "./IStudentTask";
import {IUser} from "./IUser";
import {IRequestType} from "./IRequestType";
import {IRequestState} from "./IRequestState";
import {IClosingStatus} from "./IClosingStatus";

export interface IRequest{
  id: number;
  studentTask: IStudentTask;
  teacher: IUser;
  requestType: IRequestType;
  requestState: IRequestState;
  studentMsg: string | null;
  creationTime: Date;
  teacherMsg: string | null;
  closingTime: Date | null;
  closingStatus: IClosingStatus | null;
  creationTimeToPrint: string;
  closingTimeToPrint: string;
}

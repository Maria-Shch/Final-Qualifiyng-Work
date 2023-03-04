import {IUser} from "./IUser";
import {ITask} from "./ITask";
import {IStatus} from "./IStatus";

export interface IStudentTask{
  id: number;
  user: IUser;
  task: ITask;
  status: IStatus;
}


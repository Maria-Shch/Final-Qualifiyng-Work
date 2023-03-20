import {IUser} from "../interfaces/IUser";
import {ITask} from "../interfaces/ITask";

export interface IUserStatInfo{
  user: IUser;
  lastSolvedTask: ITask;
  countOfSolvedTasks: number;
}

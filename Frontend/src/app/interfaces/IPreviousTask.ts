import {ITask} from "./ITask";

export interface IPreviousTask {
  id:number;
  task: ITask;
  previousTask: ITask;
}

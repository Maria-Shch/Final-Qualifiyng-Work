import {ITask} from "../interfaces/ITask";

export interface INewTask{
  task: ITask;
  selectedPreviousTaskIds: number[];
}

import {IBlock} from "../interfaces/IBlock";
import {IStudentTask} from "../interfaces/IStudentTask";

export interface IBlockAndStatInfo{
  block: IBlock;
  countOfSolvedTasks: number;
  countOfAllTasks:number;
  studentTaskList: IStudentTask[];
}

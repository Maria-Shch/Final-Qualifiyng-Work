import {IBlock} from "./IBlock";

export interface ITask {
  id:number;
  serialNumber: number;
  name:string;
  description:string;
  manualCheckRequired: boolean;
  block: IBlock;
  previousTasks: ITask[];
}

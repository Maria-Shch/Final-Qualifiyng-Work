import {IBlock} from "./IBlock";
import {IPreviousTask} from "./IPreviousTask";

export interface ITask {
  id:number;
  serialNumber: number;
  name:string;
  description:string;
  manualCheckRequired: boolean;
  block: IBlock;
  previousTasks: IPreviousTask[];
  onTestChecking: boolean;
}

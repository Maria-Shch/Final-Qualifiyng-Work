import {IStatus} from "../interfaces/IStatus";

export interface ITaskOfBlock {
  id: number;
  serialNumber:number;
  name:string;
  status: IStatus;
}

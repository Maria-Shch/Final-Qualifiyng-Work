import {ISimpleTask} from "./ISimpleTask";
import {CheckBoxItemTask} from "../classes/CheckBoxItemTask";

export interface ISimpleBlock{
  serialNumber: number;
  fullname: string;
  simpleTaskList: ISimpleTask[];
  taskCheckBoxList: CheckBoxItemTask[];
}

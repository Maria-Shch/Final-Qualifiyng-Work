import {ISimpleBlock} from "./ISimpleBlock";

export interface ISimpleChapter{
  serialNumber: number;
  fullname: string;
  simpleBlockList: ISimpleBlock[];
}

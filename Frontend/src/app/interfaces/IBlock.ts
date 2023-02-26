import {IChapter} from "./IChapter";

export interface IBlock {
  id:number;
  serialNumber: number;
  name:string;
  chapter: IChapter;
  textTheory: string;
}

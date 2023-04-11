import {IChapter} from "../interfaces/IChapter";
import {IBlockAndStatInfo} from "./IBlockAndStatInfo";

export interface IChapterAndStatInfo{
  chapter: IChapter;
  countOfSolvedTasks: number;
  countOfAllTasks:number;
  blockAndStatInfoList: IBlockAndStatInfo[];
}

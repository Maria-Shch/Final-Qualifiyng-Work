import {IGroup} from "../interfaces/IGroup";
import {IUserStatInfo} from "./IUserStatInfo";

export interface IGroupWithUsersStatInfo{
  group: IGroup;
  userStatInfos: IUserStatInfo[];
}

import {IGroup} from "./IGroup";

export interface IUser {
  id:number;
  name:string;
  lastname:string;
  patronymic:string;
  username:string;
  password:string;
  registrationDate:Date;
  role:string;
  group: IGroup | null;
}

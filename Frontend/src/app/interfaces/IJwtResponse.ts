import {IUser} from "./IUser";

export interface IJwtResponse {
  type:string;
  accessToken:string;
  refreshToken:string;
  user:IUser;
}

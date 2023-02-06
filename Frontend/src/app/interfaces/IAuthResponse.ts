import {IUser} from "./IUser";

export interface IAuthResponse {
  authResponseStatus:string;
  errorMessage:string;
  type:string;
  accessToken:string;
  refreshToken:string;
  user:IUser;
}

import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {AuthorizationService} from "./authorization.service";
import {Observable} from "rxjs";
import {IUser} from "../interfaces/IUser";
import {environment} from "../environments/enviroment";

@Injectable({
  providedIn: 'root'
})
export class UserService {
  constructor(private httpclient: HttpClient, private authService: AuthorizationService) {}
  public isPresent(username: string) : Observable<boolean> {
    return this.httpclient.post<boolean>(environment.apiUrl + '/user/isPresent',
      username,
      {headers: new HttpHeaders({ 'No-Auth': 'True' })}
    );
  }
  public registerNewUser(newUser: IUser) : Observable<IUser> {
    return this.httpclient.post<IUser>(environment.apiUrl + '/user/registerNewUser',
      newUser,
      {headers: new HttpHeaders({ 'No-Auth': 'True' })}
    );
  }
  public forUser() {
    return this.httpclient.get(environment.apiUrl + '/hello/user', {
      responseType: 'text',
    });
  }

  public forAdmin() {
    return this.httpclient.get(environment.apiUrl + '/hello/admin', {
      responseType: 'text',
    });
  }

  public forAll() {
    return this.httpclient.get(environment.apiUrl + '/hello/all', {
      responseType: 'text',
    });
  }

  public roleMatch(allowedRoles: string[]): boolean  {
    let isMatch = false;
    const userRole: any = this.authService.getRole();

    if (userRole != null && userRole) {
      for (let i = 0; i < allowedRoles.length; i++) {
        if (userRole === allowedRoles[i]) {
          isMatch = true;
          return isMatch;
        }
      }
    }
    return isMatch;
  }
}

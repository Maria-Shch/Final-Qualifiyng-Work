import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {AuthorizationService} from "./authorization.service";
import {Observable} from "rxjs";
import {IJwtResponse} from "../interfaces/IJwtResponse";
import {IUsernamePassword} from "../interfaces/IUsernamePassword";

@Injectable({
  providedIn: 'root'
})
export class UserService {

  PATH_OF_API = 'http://localhost:8084';

  requestHeader = new HttpHeaders({ 'No-Auth': 'True' });
  constructor(
    private httpclient: HttpClient,
    private authService: AuthorizationService
  ) {}

  public login(loginData: IUsernamePassword) : Observable<IJwtResponse> {
    return this.httpclient.post<IJwtResponse>(this.PATH_OF_API + '/auth/login', loginData, {
      headers: this.requestHeader,
    });
  }

  public forUser() {
    return this.httpclient.get(this.PATH_OF_API + '/hello/user', {
      responseType: 'text',
    });
  }

  public forAdmin() {
    return this.httpclient.get(this.PATH_OF_API + '/hello/admin', {
      responseType: 'text',
    });
  }

  public forAll() {
    return this.httpclient.get(this.PATH_OF_API + '/hello/all', {
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
        } else {
          return isMatch;
        }
      }
    }
    return isMatch;
  }
}

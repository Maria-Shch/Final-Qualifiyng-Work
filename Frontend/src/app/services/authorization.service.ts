import { Injectable } from '@angular/core';
import {IUsernamePassword} from "../interfaces/IUsernamePassword";
import {Observable} from "rxjs";
import {IAuthResponse} from "../interfaces/IAuthResponse";
import {HttpClient, HttpHeaders} from "@angular/common/http";
@Injectable({
  providedIn: 'root'
})
export class AuthorizationService {

  PATH_OF_API = 'http://localhost:8084';
  constructor(private httpclient: HttpClient) {}

  public login(loginData: IUsernamePassword) : Observable<IAuthResponse> {
      return this.httpclient.post<IAuthResponse>(this.PATH_OF_API + '/auth/login', loginData, {
          headers: new HttpHeaders({ 'No-Auth': 'True' }),
      });
  }

  public async refreshTokens() {
      this.httpclient.post<IAuthResponse>(this.PATH_OF_API + '/auth/token',
        {"refreshToken": this.getRefreshToken()},
        { headers: new HttpHeaders({ 'No-Auth': 'True' }),}
      ).subscribe((data:IAuthResponse) =>{
          this.setAccessToken(data.accessToken);
          console.log("выполнился token");

          this.httpclient.post<IAuthResponse>(this.PATH_OF_API + '/auth/refresh',
            {"refreshToken": this.getRefreshToken()}
          ).subscribe((data:IAuthResponse) =>{
              this.setAccessToken(data.accessToken);
              this.setRefreshToken(data.refreshToken);
              console.log("выполнился refresh");
          });
      });
  }

  public setRole(role: string) {
      localStorage.setItem('role', JSON.stringify(role));
  }

  public getRole(): string | null {
      return JSON.parse(localStorage.getItem('role') || '{}');
  }

  public setAccessToken(accessToken: string) {
      localStorage.setItem('accessToken', accessToken);
  }

  public getAccessToken(): string | null {
      return localStorage.getItem('accessToken');
  }

  public setRefreshToken(refreshToken: string) {
      localStorage.setItem('refreshToken', refreshToken);
  }

  public getRefreshToken(): string | null {
      return localStorage.getItem('refreshToken');
  }

  public clear() {
      localStorage.clear();
  }

  public isLoggedIn() {
      return this.getRole() && this.getAccessToken();
  }
}

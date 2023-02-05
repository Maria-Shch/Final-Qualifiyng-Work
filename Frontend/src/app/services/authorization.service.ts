import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class AuthorizationService {
  constructor() {}

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

  public clear() {
    localStorage.clear();
  }

  public isLoggedIn() {
    return this.getRole() && this.getAccessToken();
  }
}

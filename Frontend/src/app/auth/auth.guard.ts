import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree,} from '@angular/router';
import {Observable} from 'rxjs';
import {AuthorizationService} from "../services/authorization.service";
import {UserService} from "../services/user.service";

@Injectable({
  providedIn: 'root',
})
export class AuthGuard implements CanActivate {
  constructor(
    private authService: AuthorizationService,
    private router: Router,
    private userService: UserService
  ) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot):
    | Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    if (this.authService.getAccessToken() !== null) {
      let role: string[] = route.data['roles'] as Array<string>;

      if (role) {
        const match = this.userService.roleMatch(role);

        if (match) {
          return true;
        } else {
          this.router.navigate(['/forbidden']);
          return false;
        }
      }
    }

    this.router.navigate(['/login']);
    return false;
  }
}

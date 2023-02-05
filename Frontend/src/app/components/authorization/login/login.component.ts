import { Component } from '@angular/core';
import {AuthorizationService} from "../../../services/authorization.service";
import {UserService} from "../../../services/user.service";
import {Router} from "@angular/router";
import {NgForm} from "@angular/forms";
import {IJwtResponse} from "../../../interfaces/IJwtResponse";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  constructor(
    private userService: UserService,
    private authService: AuthorizationService,
    private router: Router
  ) {}

  ngOnInit(): void {}

  login(loginForm: NgForm) {
    this.userService.login(loginForm.value).subscribe(
      (response: IJwtResponse) => {
        this.authService.setRole(response.user.role);
        this.authService.setAccessToken(response.accessToken);

        const role = response.user.role;
        if (role === 'ADMIN') {
          this.router.navigate(['/admin']);
        } else {
          this.router.navigate(['/user']);
        }
      },
      (error) => {
        console.log(error);
      }
    );
  }
}

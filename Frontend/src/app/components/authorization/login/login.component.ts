import {Component} from '@angular/core';
import {AuthorizationService} from "../../../services/authorization.service";
import {Router} from "@angular/router";
import {NgForm} from "@angular/forms";
import {IAuthResponse} from "../../../interfaces/IAuthResponse";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {

  errorMessage:string = "";
  constructor(private authService: AuthorizationService, private router: Router) {}

  ngOnInit(): void {}

  login(loginForm: NgForm) {
    console.log(loginForm.value);
    if(loginForm.value.username.length === 0 ||  loginForm.value.password.length === 0) {
      this.errorMessage = "Заполните все поля";
    }
    else{
      this.authService.login(loginForm.value).subscribe(
        (response: IAuthResponse) => {
          if (response.authResponseStatus === "SUCCESS"){
            this.authService.setRole(response.user.role);
            this.authService.setAccessToken(response.accessToken);
            this.authService.setRefreshToken(response.refreshToken);

            const role = response.user.role;
            if (role === 'ADMIN') {
              this.router.navigate(['/admin']);
            } else {
              this.router.navigate(['/user']);
            }
          }
          if (response.authResponseStatus === "ERROR"){
            this.errorMessage = response.errorMessage;
          }
        },
        (error) => {
          this.router.navigate(['/error']);
          console.log(error);
        }
      );
    }
  }

  hasErrors(): boolean {
    return this.errorMessage.length != 0;
  }
}

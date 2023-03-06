import {Component} from '@angular/core';
import {AuthorizationService} from "../../../services/authorization.service";
import {Router} from "@angular/router";
import {NgForm} from "@angular/forms";
import {IAuthResponse} from "../../../dto_interfaces/IAuthResponse";
import {toErrorPage} from "../../../utils/ToErrorPageFunc";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {

  errorMessage:string = "";
  constructor(
    private authService: AuthorizationService,
    private router: Router
  ) {}

  ngOnInit(): void {}

  login(loginForm: NgForm) {
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
            this.router.navigate(['/chapters']);
          }
          if (response.authResponseStatus === "ERROR"){
            this.errorMessage = response.errorMessage;
          }
        },
      (error)=>{ toErrorPage(error, this.router);});
    }
  }

  hasErrors(): boolean {
    return this.errorMessage.length != 0;
  }
}

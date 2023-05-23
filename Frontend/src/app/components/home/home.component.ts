import {Component, OnInit} from '@angular/core';
import {AuthorizationService} from "../../services/authorization.service";
import {Router} from "@angular/router";
import {UserService} from "../../services/user.service";
import {IUser} from "../../interfaces/IUser";

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit{
  user: IUser | null = null;

  constructor(
    private authService: AuthorizationService,
    private router: Router,
    public userService: UserService
  ) {}

  ngOnInit(): void {
    if (this.authService.isLoggedIn()) {
      this.userService.getUser().subscribe((data: IUser) => {
        this.user = data;
      });
    };
  }

  public isLoggedIn() {
    return this.authService.isLoggedIn();
  }

  public logout() {
    this.authService.clear();
    this.router.navigate(['/home']);
  }
}

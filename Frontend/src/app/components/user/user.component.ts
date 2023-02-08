import {Component} from '@angular/core';
import {UserService} from "../../services/user.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css']
})
export class UserComponent {
  messageUser: any;
  messageAll: any;
  constructor(private userService: UserService, private router: Router) { }

  ngOnInit(): void {
    this.forUser();
  }

  forUser() {
    this.userService.forUser().subscribe(
      (response) => {
        this.messageUser = response;
      },
      (error)=>{
        this.router.navigate(['/error']);
        console.log(error);
      }
    );

    this.userService.forAll().subscribe(
      (response) => {
        this.messageAll = response;
      },
      (error)=>{
        this.router.navigate(['/error']);
        console.log(error);
      }
    );
  }
}

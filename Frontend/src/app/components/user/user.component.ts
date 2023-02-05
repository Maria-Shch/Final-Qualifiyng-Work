import { Component } from '@angular/core';
import {UserService} from "../../services/user.service";

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css']
})
export class UserComponent {
  messageUser: any;
  messageAll: any;
  constructor(private userService: UserService) { }

  ngOnInit(): void {
    this.forUser();
  }

  forUser() {
    this.userService.forUser().subscribe(
      (response) => {
        this.messageUser = response;
      },
      (error)=>{
        console.log(error);
      }
    );

    this.userService.forAll().subscribe(
      (response) => {
        this.messageAll = response;
      },
      (error)=>{
        console.log(error);
      }
    );
  }
}

import { Component } from '@angular/core';
import {UserService} from "../../services/user.service";

@Component({
  selector: 'app-admin',
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.css']
})
export class AdminComponent {
  messageAdmin: any;
  messageAll: any;
  constructor(private userService: UserService) { }
  ngOnInit(): void {
    this.forAdmin();
  }
  forAdmin() {
    this.userService.forAdmin().subscribe(
      (response) => {
        this.messageAdmin = response;
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

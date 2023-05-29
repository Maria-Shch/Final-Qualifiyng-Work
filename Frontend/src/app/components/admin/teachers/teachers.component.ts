import {Component} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {UserService} from "../../../services/user.service";
import {forkJoin} from "rxjs";
import {IUser} from "../../../interfaces/IUser";

@Component({
  selector: 'app-teachers',
  templateUrl: './teachers.component.html',
  styleUrls: ['./teachers.component.css']
})
export class TeachersComponent {
  popupInfo: string = '';
  showPopup: boolean = false;

  employees: IUser[] =[];
  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private userService: UserService
  ) {
  }

  ngOnInit(): void {
    forkJoin([
      this.userService.getAdmin(),
      this.userService.getTeachers()
    ]).subscribe(value => {
      this.employees = [];
      this.employees = this.employees.concat(value[0], value[1]);
    });
  }

  revokeTeacherAuthority(teacher: IUser) {
    this.userService.revokeTeacherAuthority(teacher.id).subscribe((data: boolean) => {
      this.popupInfo = teacher.lastname + ' ' + teacher.name.charAt(0) + '. ' + teacher.patronymic.charAt(0) + '. снят(-а) с должности преподавателя.\n ' +
        'Все группы под его(её) управлением назначены администратору. \n Все необработанные запросы от студентов назначены администратору.';
      this.showPopup = true;
      this.ngOnInit();
    });
  }

  onChanged($event: boolean) {
    this.showPopup = false;
  }
}

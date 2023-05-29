import {Component, OnInit} from '@angular/core';
import {IUser} from "../../../interfaces/IUser";
import {ActivatedRoute, Router} from "@angular/router";
import {UserService} from "../../../services/user.service";
import {StudentTaskService} from "../../../services/student-task.service";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {IStudentProgress} from "../../../dto_interfaces/IStudentProgress";

@Component({
  selector: 'app-student-account',
  templateUrl: './student-account.component.html',
  styleUrls: ['./student-account.component.css']
})
export class StudentAccountComponent implements OnInit{
  user: IUser | null = null;
  currUser: IUser | null = null;
  teacher: string = '';
  panelOpenState = false;
  studentProgress: IStudentProgress | null = null;
  popupInfo: string = '';
  showPopup: boolean = false;

  personalDataForm: FormGroup = new FormGroup({
    name: new FormControl<string>('', [Validators.required]),
    lastname: new FormControl<string>('', [Validators.required]),
    patronymic: new FormControl<string>('', [Validators.required]),
    username: new FormControl<string>('', [Validators.required, Validators.email])
  });

  constructor(
    private router:Router,
    private route: ActivatedRoute,
    private userService: UserService,
    private studentTaskService: StudentTaskService
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      // @ts-ignore
      this.userService.getUserById(this.route.snapshot.paramMap.get("id")).subscribe((data: IUser) => {
        this.user = data;

        this.personalDataForm = new FormGroup({
          name: new FormControl<string>(this.user!.name, [Validators.required]),
          lastname: new FormControl<string>(this.user!.lastname, [Validators.required]),
          patronymic: new FormControl<string>(this.user!.patronymic, [Validators.required]),
          username: new FormControl<string>(this.user!.username, [Validators.required, Validators.email])
        });

        this.studentTaskService.getStudentProgress(this.user?.id).subscribe((data: IStudentProgress) =>{
          this.studentProgress = data;
        });
        this.userService.getTeacherByStudentId(this.user?.id!).subscribe((data: IUser) => {
          this.teacher = data.lastname + ' ' + data.name.charAt(0) + '. ' + data.patronymic.charAt(0) + '.';
        });
      });
    });

    this.userService.getUser().subscribe((data: IUser) => {
      this.currUser = data;
    });
  }

  grantTeacherAuthority(user: IUser) {
    this.userService.grantTeacherAuthority(user.id).subscribe((data: boolean) => {
      this.popupInfo = user.lastname + ' ' + user.name.charAt(0) + '. ' + user.patronymic.charAt(0) + '. назначен(-а) на должность преподавателя.';
      this.showPopup = true;
    });
  }

  onChanged($event: boolean) {
    this.showPopup = false;
    this.router.navigate([`/teacher/${this.user?.id}`]);
  }
}


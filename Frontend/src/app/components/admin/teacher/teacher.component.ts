import {Component, OnInit} from '@angular/core';
import {IUser} from "../../../interfaces/IUser";
import {IStudentProgress} from "../../../dto_interfaces/IStudentProgress";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {ActivatedRoute, Router} from "@angular/router";
import {UserService} from "../../../services/user.service";
import {StudentTaskService} from "../../../services/student-task.service";

@Component({
  selector: 'app-teacher',
  templateUrl: './teacher.component.html',
  styleUrls: ['./teacher.component.css']
})
export class TeacherComponent implements OnInit{
  user: IUser | null = null;
  teacher: string = '';
  panelOpenState = false;
  studentProgress: IStudentProgress | null = null;
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
  }

}

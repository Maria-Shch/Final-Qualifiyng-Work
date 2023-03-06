import {Component, OnInit} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {IGroup} from "../../interfaces/IGroup";
import {GroupService} from "../../services/group.service";
import {UserService} from "../../services/user.service";
import {Router} from "@angular/router";
import {FormUtils} from "../../utils/FormUtils";
import {IUser} from "../../interfaces/IUser";
import {toErrorPage} from "../../utils/ToErrorPageFunc";

@Component({
  selector: 'app-account',
  templateUrl: './account.component.html',
  styleUrls: ['./account.component.css']
})
export class AccountComponent implements OnInit{

  user: IUser | null = null;
  teacher: string = '';
  hasBeenSubmitted: boolean= false;
  isRepeatedUsername: boolean = false;
  personalDataForm: FormGroup = new FormGroup({
    name: new FormControl<string>('', [Validators.required]),
    lastname: new FormControl<string>('', [Validators.required]),
    patronymic: new FormControl<string>('', [Validators.required]),
    username: new FormControl<string>('', [Validators.required, Validators.email])
  });

  constructor(
    private groupService: GroupService,
    private userService: UserService,
    private router:Router
  ) {}

  ngOnInit(): void {
    this.userService.getUser().subscribe((data: IUser) => {
      this.user = data;

      this.personalDataForm = new FormGroup({
        name: new FormControl<string>(this.user!.name, [Validators.required]),
        lastname: new FormControl<string>(this.user!.lastname, [Validators.required]),
        patronymic: new FormControl<string>(this.user!.patronymic, [Validators.required]),
        username: new FormControl<string>(this.user!.username, [Validators.required, Validators.email])
      });
    });

    this.userService.getTeacher().subscribe((data: IUser) => {
      this.teacher = data.lastname + ' ' + data.name.charAt(0) + '. ' + data.patronymic.charAt(0) + '.';
    });
  }

  onSubmit() {
    this.hasBeenSubmitted = true;
    if (FormUtils.getControlErrors(this.personalDataForm) == null){
      this.userService.isPresent(this.personalDataForm.value.username).subscribe((isPresent: boolean) => {
        if (isPresent && this.user?.username != this.personalDataForm.value.username)
          this.isRepeatedUsername = isPresent;
        else {
          if(this.personalDataForm.value.name != this.user?.name ||
            this.personalDataForm.value.lastname != this.user?.lastname ||
            this.personalDataForm.value.patronymic != this.user?.patronymic ||
            this.personalDataForm.value.username != this.user?.username){
            let updatedUser = this.personalDataForm.value as IUser;
            updatedUser.id = <number>this.user?.id;
            this.userService.updateEditableUserdata(updatedUser).subscribe(
            (data: IUser) => {
              this.user = data;
              alert("Ваши персональные данные обновлены");
              this.ngOnInit();
            },
            (error) => {
              toErrorPage(error, this.router);
            });
          }
        }
      });
    }
  }

  get name(){
    return this.personalDataForm.controls.name as FormControl;
  }
  get lastname(){
    return this.personalDataForm.controls.lastname as FormControl;
  }
  get patronymic(){
    return this.personalDataForm.controls.patronymic as FormControl;
  }
  get username(){
    return this.personalDataForm.controls.username as FormControl;
  }
}

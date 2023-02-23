import {Component, OnInit} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {GroupService} from "../../../services/group.service";
import {IGroup} from "../../../interfaces/IGroup";
import {FormUtils} from "../../../utils/FormUtils";
import {UserService} from "../../../services/user.service";
import {IUser} from "../../../interfaces/IUser";
import {Router} from "@angular/router";

@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.css']
})
export class RegistrationComponent implements OnInit {

  hasBeenSubmitted: boolean= false;
  isRepeatedUsername: boolean = false;
  groups: IGroup[] = [];
  registrationForm: FormGroup = new FormGroup({
    name: new FormControl<string>('', [Validators.required]),
    lastname: new FormControl<string>('', [Validators.required]),
    patronymic: new FormControl<string>('', [Validators.required]),
    username: new FormControl<string>('', [Validators.required, Validators.email]),
    group: new FormControl<IGroup | null>(null),
    password: new FormControl<string>('', [Validators.required]),
    confirmPassword: new FormControl<string>('', [Validators.required]),
  });

  constructor(
    private groupService: GroupService,
    private userService: UserService,
    private router:Router
  ) {}

  ngOnInit(): void {
    this.groupService.getGroups().subscribe((data: IGroup[]) =>{
      this.groups = data;
    });
  }

  onSubmit() {
    this.hasBeenSubmitted = true;
    if(FormUtils.getControlErrors(this.registrationForm) == null && this.isPasswordsMatch()){
      this.userService.isPresent(this.registrationForm.value.username).subscribe((isPresent: boolean) => {
        this.isRepeatedUsername = isPresent;
        if(!isPresent){
          let newUser = this.registrationForm.value as IUser;
          if(newUser.group != null){
            newUser.group = this.groupService.setParamsToNullExcludeId(newUser.group);
          }
          this.userService.registerNewUser(newUser).subscribe(
          (data :IUser) =>{
            this.router.navigate(['/login']);
          },
          (error)=>{
            this.router.navigate(['/error']);
            console.log(error);
          });
        }
      });
    }
  }

  get name(){
    return this.registrationForm.controls.name as FormControl;
  }
  get lastname(){
    return this.registrationForm.controls.lastname as FormControl;
  }
  get patronymic(){
    return this.registrationForm.controls.patronymic as FormControl;
  }
  get username(){
    return this.registrationForm.controls.username as FormControl;
  }
  get password(){
    return this.registrationForm.controls.password as FormControl;
  }
  get confirmPassword(){
    return this.registrationForm.controls.confirmPassword as FormControl;
  }
  isPasswordsMatch() : boolean {
    return this.registrationForm.controls.password.value == this.registrationForm.controls.confirmPassword.value;
  }
}

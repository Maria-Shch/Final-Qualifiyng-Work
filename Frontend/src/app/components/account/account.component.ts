import {Component, OnInit} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {GroupService} from "../../services/group.service";
import {UserService} from "../../services/user.service";
import {Router} from "@angular/router";
import {FormUtils} from "../../utils/FormUtils";
import {IUser} from "../../interfaces/IUser";
import {toErrorPage} from "../../utils/ToErrorPageFunc";
import {RequestService} from "../../services/request.service";
import {IEventHistory} from "../../interfaces/IEventHistory";

@Component({
  selector: 'app-account',
  templateUrl: './account.component.html',
  styleUrls: ['./account.component.css']
})
export class AccountComponent implements OnInit{

  user: IUser | null = null;
  teacher: string = '';
  admin: string = '';
  hasBeenSubmitted: boolean= false;
  isRepeatedUsername: boolean = false;
  requestHistories: IEventHistory[] = [];
  currentPageOfHistory: number = 0;
  showModal: boolean = false;
  historyInModal: IEventHistory | undefined | null = null;
  isPresentOtherHistories: boolean = false;

  personalDataForm: FormGroup = new FormGroup({
    name: new FormControl<string>('', [Validators.required]),
    lastname: new FormControl<string>('', [Validators.required]),
    patronymic: new FormControl<string>('', [Validators.required]),
    username: new FormControl<string>('', [Validators.required, Validators.email])
  });

  constructor(
    private groupService: GroupService,
    private userService: UserService,
    private router:Router,
    private requestService: RequestService
  ) {}

  ngOnInit(): void {
    this.userService.getUser().subscribe((data: IUser) => {
      this.user = data;

      if (this.user?.role != 'USER'){
        this.userService.getAdmin().subscribe((data: IUser) => {
          this.admin = data.lastname + ' ' + data.name.charAt(0) + '. ' + data.patronymic.charAt(0) + '.';
        });
      }

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

    this.requestService.getHistoryOfRequests(this.currentPageOfHistory).subscribe((data: IEventHistory[]) => {
      this.requestHistories = data;
      if (data.length < 5 || data.length == 0 || data == null) {
        this.isPresentOtherHistories = false;
      } else {
        this.isPresentOtherHistories = true;
      }
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

  openModalWithInfo(id: number) {
    this.historyInModal = this.requestHistories.find(x => x.id == id);
    this.showModal = true;
  }

  closeModal() {
    this.showModal = false;
  }

  loadHistories() {
    this.requestService.getHistoryOfRequests(this.currentPageOfHistory + 1).subscribe((data: IEventHistory[]) => {
      console.log(data);
      console.log(data.length == 0);
      console.log(data == null);
      for (let i = 0; i < data.length; i++) {
        this.requestHistories.push(data[i]);
      }
      if (data.length < 5 || data.length == 0) {
        this.isPresentOtherHistories = false;
      } else {
        this.isPresentOtherHistories = true;
      }
      this.currentPageOfHistory+=1;
    });
  }
}

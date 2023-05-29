import {Component, OnInit} from '@angular/core';
import {IUser} from "../../../interfaces/IUser";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {ActivatedRoute, Router} from "@angular/router";
import {UserService} from "../../../services/user.service";
import {GroupService} from "../../../services/group.service";
import {IGroup} from "../../../interfaces/IGroup";
import {ILevelOfEdu} from "../../../interfaces/ILevelOfEdu";
import {IProfile} from "../../../interfaces/IProfile";
import {IFaculty} from "../../../interfaces/IFaculty";
import {IFormOfEdu} from "../../../interfaces/IFormOfEdu";
import {IYear} from "../../../interfaces/IYear";
import {forkJoin} from "rxjs";
import {FormUtils} from "../../../utils/FormUtils";

@Component({
  selector: 'app-edit-group',
  templateUrl: './edit-group.component.html',
  styleUrls: ['./edit-group.component.css']
})
export class EditGroupComponent implements OnInit{

  group: IGroup | null = null;
  levelsOfEdu: ILevelOfEdu[] = [];
  profiles: IProfile[] = [];
  faculties: IFaculty[] = [];
  formsOfEdu: IFormOfEdu[] =[];
  years: IYear[] = [];
  teachers: IUser[] = [];
  resultGroupName: string = '';
  quantityCourseNumber: number | null = null;
  courseNumberList: number[] = [];
  quantityGroupNumber: number | null = null;
  maxQuantityOfProfiles: number | null = null;
  groupNumberList: number[] = [];
  showModalAddNewProfile: boolean = false;
  newProfiles: number[] = [];
  showModalAddNewFaculty: boolean = false;
  newFacultyFormHasBeenSubmitted: boolean = false;
  showModalAddNewYear: boolean = false;
  newYearFormHasBeenSubmitted: boolean = false;
  newYearName: string = '';
  popupInfo: string = '';
  showPopup: boolean = false;

  editingGroupForm: FormGroup = new FormGroup({
    levelOfEdu: new FormControl<ILevelOfEdu | null>(this.group?.levelOfEdu!),
    profile: new FormControl<IProfile | null>(this.group?.profile!),
    faculty: new FormControl<IFaculty | null>(this.group?.faculty!),
    formOfEdu: new FormControl<IFormOfEdu | null>(this.group?.formOfEdu!),
    courseNumber: new FormControl<number | null>(this.group?.courseNumber!),
    groupNumber: new FormControl<number | null>(this.group?.groupNumber!),
    year: new FormControl<IYear | null>(this.group?.year!),
    teacher: new FormControl<IUser | null>(this.group?.teacher!)
  });

  newProfileForm: FormGroup = new FormGroup({
    name: new FormControl<string | null>(null)
  });
  newFacultyForm: FormGroup = new FormGroup({
    name: new FormControl<string | null>(null, [Validators.required])
  });
  newYearForm: FormGroup = new FormGroup({
    name: new FormControl<string | null>(this.newYearName, [Validators.required])
  });

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private groupService: GroupService,
    private userService: UserService
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      // @ts-ignore
      this.groupService.getGroupById(this.route.snapshot.paramMap.get("id")).subscribe((data: IGroup) => {
        this.group = data;
      });
    });

    forkJoin([
      this.groupService.getLevelsOfEdu(),
      this.groupService.getProfiles(),
      this.groupService.getFaculties(),
      this.groupService.getFormsOfEdu(),
      this.groupService.getYears(),
      this.userService.getTeachersWithAdmin(),
      this.groupService.getQuantityCourseNumber(),
      this.groupService.getQuantityGroupNumber(),
      this.groupService.getMaxQuantityOfProfiles()
    ]).subscribe(value => {
      this.levelsOfEdu= value[0];
      this.profiles = value[1];
      this.faculties = value[2];
      this.formsOfEdu = value[3];
      this.years = value[4];
      this.teachers = value[5];
      this.quantityCourseNumber = value[6];
      this.quantityGroupNumber = value[7];
      this.maxQuantityOfProfiles = value[8];

      for (let i = 1; i <= this.quantityCourseNumber; i++) {
        this.courseNumberList.push(i);
      }

      for (let i = 1; i <= this.quantityGroupNumber; i++) {
        this.groupNumberList.push(i);
      }

      this.initEditingGroupForm();
      this.fillNewProfiles();
      this.changeGroupResultNameOnChangeGroupParam();
    });

  }

  initEditingGroupForm(){
    this.editingGroupForm = new FormGroup({
      levelOfEdu: new FormControl<ILevelOfEdu | null>(this.levelsOfEdu.filter(x => x.id == this.group?.levelOfEdu?.id)[0]),
      profile: new FormControl<IProfile | null>(this.profiles.filter(x => x.id == this.group?.profile?.id)[0]),
      faculty: new FormControl<IFaculty | null>(this.faculties.filter(x => x.id == this.group?.faculty?.id)[0]),
      formOfEdu: new FormControl<IFormOfEdu | null>(this.formsOfEdu.filter(x => x.id == this.group?.formOfEdu?.id)[0]),
      courseNumber: new FormControl<number | null>(this.group?.courseNumber!),
      groupNumber: new FormControl<number | null>(this.group?.groupNumber!),
      year: new FormControl<IYear | null>(this.years.filter(x => x.id == this.group?.year?.id)[0]),
      teacher: new FormControl<IUser | null>(this.teachers.filter(x => x.id == this.group?.teacher?.id)[0])
    });
  }

  fillNewProfiles(){
    this.newProfiles = [];
    let presentProfiles = this.profiles.filter(x => x.name != 'Без профиля');
    // @ts-ignore
    for (let i = presentProfiles.length + 1; i <= this.maxQuantityOfProfiles; i++) {
      this.newProfiles.push(i);
    }
    this.newProfileForm = new FormGroup({
      name: new FormControl<number | null>(this.newProfiles[0])
    });
  }

  changeGroupResultNameOnChangeGroupParam(){
    let newGroup = this.editingGroupForm?.value as IGroup;
    this.resultGroupName = newGroup.levelOfEdu!.letter;

    if (newGroup.profile?.name != 'Без профиля') {
      // @ts-ignore
      this.resultGroupName = this.resultGroupName + newGroup.profile?.name;
    }

    this.resultGroupName = this.resultGroupName + '-' + newGroup.faculty?.name;

    if (newGroup.formOfEdu?.letter != null) {
      this.resultGroupName = this.resultGroupName + newGroup.formOfEdu?.letter;
    }

    this.resultGroupName = this.resultGroupName + '-' + newGroup.courseNumber + newGroup.groupNumber +
      ' (' + newGroup.year?.name + ')';
  }

  onSubmitEditingGroupForm() {
    let editedGroup = this.editingGroupForm.value as IGroup;
    // @ts-ignore
    editedGroup.id = this.group?.id;
    this.groupService.updateGroup(editedGroup).subscribe((data: IGroup) =>{
      this.group = data;
      this.popupInfo = "Новое название группы: " + data.name + ' (' + data.year?.name + ')\n' +
        "Преподаватель: " + data.teacher?.lastname + ' ' + data.teacher?.name.charAt(0) + '. ' + data.teacher?.patronymic.charAt(0) + '.' ;
      this.showPopup = true;
    });
  }

  onSubmitNewProfileForm() {
    let newProfile = this.newProfileForm.value as IProfile;
    this.showModalAddNewProfile = false;
    this.groupService.addNewProfile(newProfile).subscribe((data: IProfile[]) =>{
      this.profiles = data;
      this.fillNewProfiles();
      this.initEditingGroupForm();
    });
  }

  onSubmitNewFacultyForm() {
    this.newFacultyFormHasBeenSubmitted = true;
    if(FormUtils.getControlErrors(this.newFacultyForm) == null) {
      let newFaculty = this.newFacultyForm.value as IFaculty;
      this.showModalAddNewFaculty = false;
      this.groupService.addNewFaculty(newFaculty).subscribe((data: IFaculty[]) =>{
        this.faculties = data;
        this.initEditingGroupForm();
      });
    }
  }

  get name(){
    return this.newFacultyForm.controls.name as FormControl;
  }

  onSubmitNewYearForm() {
    this.newYearFormHasBeenSubmitted = true;
    if(FormUtils.getControlErrors(this.newYearForm) == null) {
      let newYear = this.newYearForm.value as IYear;
      console.log(newYear);
      this.showModalAddNewYear = false;
      this.groupService.addNewYear(newYear).subscribe((data: IYear[]) =>{
        this.years = data;
        this.initEditingGroupForm();
      });
    }
  }

  get newYear(){
    return this.newYearForm.controls.name as FormControl;
  }

  checkNewYearName() {
    if (this.newYearForm.value.name.length == 4) {
      this.newYearName = this.newYearForm.value.name + '/';
    }
  }

  onChanged($event: boolean) {
    this.showPopup = false;
  }
}

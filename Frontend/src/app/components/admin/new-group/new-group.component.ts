import {Component, OnInit} from '@angular/core';
import {GroupService} from "../../../services/group.service";
import {UserService} from "../../../services/user.service";
import {ILevelOfEdu} from "../../../interfaces/ILevelOfEdu";
import {IProfile} from "../../../interfaces/IProfile";
import {IFaculty} from "../../../interfaces/IFaculty";
import {IUser} from "../../../interfaces/IUser";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {IGroup} from "../../../interfaces/IGroup";
import {IYear} from "../../../interfaces/IYear";
import {IFormOfEdu} from "../../../interfaces/IFormOfEdu";
import {CheckboxItem} from "../../../classes/CheckboxItem";
import {forkJoin} from "rxjs";
import {FormUtils} from "../../../utils/FormUtils";
import {Router} from "@angular/router";
import {toErrorPage} from "../../../utils/ToErrorPageFunc";

@Component({
  selector: 'app-new-group',
  templateUrl: './new-group.component.html',
  styleUrls: ['./new-group.component.css']
})
export class NewGroupComponent implements OnInit{
  levelsOfEdu: ILevelOfEdu[] = [];
  profiles: IProfile[] = [];
  faculties: IFaculty[] = [];
  formsOfEdu: IFormOfEdu[] =[];
  years: IYear[] = [];
  teachers: IUser[] = [];
  studentsWithoutGroup: IUser[] = [];
  studentsWithoutGroupOptions = new Array<CheckboxItem>();
  selectedStudentIds: number[] = [];
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
  isRepeatedYear: boolean = false;
  isRepeatedFaculty: boolean = false;
  creatingGroupForm: FormGroup = new FormGroup({
    levelOfEdu: new FormControl<ILevelOfEdu | null>(null),
    profile: new FormControl<IProfile | null>(null),
    faculty: new FormControl<IFaculty | null>(null),
    formOfEdu: new FormControl<IFormOfEdu | null>(null),
    courseNumber: new FormControl<number | null>(1),
    groupNumber: new FormControl<number | null>(1),
    year: new FormControl<IYear | null>(null),
    teacher: new FormControl<IUser | null>(null)
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
    private groupService: GroupService,
    private userService: UserService,
    private router:Router
  ) {}

  ngOnInit(): void {
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

      this.fillNewProfiles();
      this.initCreatingGroupForm();
      this.changeGroupResultNameOnChangeGroupParam();
    });

    this.userService.getStudentsWithoutGroup().subscribe((data: IUser[]) => {
      this.studentsWithoutGroup = data;
      this.studentsWithoutGroupOptions = data.map(x => new CheckboxItem(x.id, x.lastname + ' ' + x.name, false));
    });
  }

  initCreatingGroupForm(){
    this.creatingGroupForm = new FormGroup({
      levelOfEdu: new FormControl<ILevelOfEdu | null>(this.levelsOfEdu.filter(x => x.name=='Бакалавриат')[0]),
      profile: new FormControl<IProfile | null>(this.profiles[0]),
      faculty: new FormControl<IFaculty | null>(this.faculties.filter(x => x.name=='ИФСТ')[0]),
      formOfEdu: new FormControl<IFormOfEdu | null>(this.formsOfEdu.filter(x => x.name=='Очная')[0]),
      courseNumber: new FormControl<number | null>(this.courseNumberList[0]),
      groupNumber: new FormControl<number | null>(this.groupNumberList[0]),
      year: new FormControl<IYear | null>(this.years[this.years.length-1]),
      teacher: new FormControl<IUser | null>(null)
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

  onChangeSelectedStudents() {
    this.selectedStudentIds = this.studentsWithoutGroupOptions.filter(x => x.checked).map(x => x.value);
  }

  changeGroupResultNameOnChangeGroupParam(){
    let newGroup = this.creatingGroupForm?.value as IGroup;
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

  onSubmitCreatingGroupForm() {
    let newGroup = this.creatingGroupForm.value as IGroup;
    this.groupService.createNewGroup(newGroup, this.selectedStudentIds).subscribe((data: IGroup) =>{
      this.router.navigate(['/allStudentsGroups']);
      alert("Вы создали новую группу: " + data.name + ' (' + data.year?.name + ')\n' +
        "Преподаватель: " + data.teacher?.lastname + ' ' + data.teacher?.name.charAt(0) + '. ' + data.teacher?.patronymic.charAt(0) + '.' );
    });
  }

  onSubmitNewProfileForm() {
    let newProfile = this.newProfileForm.value as IProfile;
    this.showModalAddNewProfile = false;
    this.groupService.addNewProfile(newProfile).subscribe((data: IProfile[]) =>{
      this.profiles = data;
      this.fillNewProfiles();
      this.initCreatingGroupForm();
    });
  }

  onSubmitNewFacultyForm() {
    this.newFacultyFormHasBeenSubmitted = true;
    if(FormUtils.getControlErrors(this.newFacultyForm) == null) {
      this.groupService.isPresentFaculty(this.newFacultyForm.value.name).subscribe((isRepeatedFaculty: boolean) => {
        this.isRepeatedFaculty = isRepeatedFaculty;
        if(!isRepeatedFaculty){
          let newYear = this.newYearForm.value as IYear;
          console.log(newYear);
          this.showModalAddNewYear = false;
          this.groupService.addNewYear(newYear).subscribe((data: IYear[]) =>{
            this.years = data;
            this.initCreatingGroupForm();
          });
        }
      });

    }
  }

  get name(){
    return this.newFacultyForm.controls.name as FormControl;
  }

  onSubmitNewYearForm() {
    this.newYearFormHasBeenSubmitted = true;
    if(FormUtils.getControlErrors(this.newYearForm) == null) {
      this.groupService.isPresentYear(this.newYearForm.value.name).subscribe((isRepeatedYear: boolean) => {
        this.isRepeatedYear = isRepeatedYear;
        if(!isRepeatedYear){
          let newYear = this.newYearForm.value as IYear;
          console.log(newYear);
          this.showModalAddNewYear = false;
          this.groupService.addNewYear(newYear).subscribe((data: IYear[]) =>{
            this.years = data;
            this.initCreatingGroupForm();
          });
        }
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
}

import {Component, OnInit} from '@angular/core';
import {IGroupWithUsersStatInfo} from "../../../dto_interfaces/IGroupWithUsersStatInfo";
import {GroupService} from "../../../services/group.service";
import {CheckboxItem} from "../../../classes/CheckboxItem";
import {IFilterGroups} from "../../../dto_interfaces/IFilterGroups";
import {UserService} from "../../../services/user.service";
import {IYear} from "../../../interfaces/IYear";
import {IFaculty} from "../../../interfaces/IFaculty";
import {IUser} from "../../../interfaces/IUser";
import {IUserStatInfo} from "../../../dto_interfaces/IUserStatInfo";
import {Router} from "@angular/router";
import {ILevelOfEdu} from "../../../interfaces/ILevelOfEdu";
import {IProfile} from "../../../interfaces/IProfile";

@Component({
  selector: 'app-all-students-groups',
  templateUrl: './all-students-groups.component.html',
  styleUrls: ['./all-students-groups.component.css']
})
export class AllStudentsGroupsComponent implements OnInit{
  groupsWithUsersStatInfo: IGroupWithUsersStatInfo[] = [];
  studentsWithoutGroupWithStatInfo: IUserStatInfo[] =[];
  filterGroups: IFilterGroups | null = null;
  yearOptions = new Array<CheckboxItem>();
  facultyOptions = new Array<CheckboxItem>();
  teacherOptions = new Array<CheckboxItem>();
  levelOfEduOptions = new Array<CheckboxItem>();
  profileOptions = new Array<CheckboxItem>();
  showModalEditGroup: boolean = false;
  idGroupSelectedForEditing: number | null = null;
  admin: IUser | null = null;

  constructor(
    private groupService: GroupService,
    private userService: UserService,
    private router:Router
  ) {}

  ngOnInit(): void {
    this.groupService.getGroupsWithUsersStatInfoForAdmin().subscribe((data: IGroupWithUsersStatInfo[]) => {
      this.groupsWithUsersStatInfo = data;
    });

    this.userService.getStudentsWithoutGroupWithStatInfo().subscribe((data: IUserStatInfo[]) => {
      this.studentsWithoutGroupWithStatInfo = data;
    });

    this.userService.getAdmin().subscribe((data: IUser) => {
      this.admin = data;
    });

    this.groupService.getYears().subscribe((data: IYear[]) => {
      this.yearOptions = data.map(x => new CheckboxItem(x.id, x.name, false));
    });
    this.groupService.getFaculties().subscribe((data: IFaculty[]) => {
      this.facultyOptions = data.map(x => new CheckboxItem(x.id, x.name, false));
    });
    this.userService.getTeachers().subscribe((data: IUser[]) => {
      this.teacherOptions = data.map(x => new CheckboxItem(x.id, x.lastname + ' ' + x.name.charAt(0) + '. ' + x.patronymic.charAt(0) + '.', false));
    });
    this.groupService.getLevelsOfEdu().subscribe((data: ILevelOfEdu[]) => {
      this.levelOfEduOptions = data.map(x => new CheckboxItem(x.id, x.name, false));
    });
    this.groupService.getProfiles().subscribe((data: IProfile[]) => {
      this.profileOptions = data.map(x => new CheckboxItem(x.id, x.name, false));
    });
  }

  onChangeFilter(value: any) {
    this.filterGroups = {
      yearIds: this.yearOptions.filter(x => x.checked).map(x => x.value),
      facultyIds: this.facultyOptions.filter(x => x.checked).map(x => x.value),
      teacherIds: this.teacherOptions.filter(x => x.checked).map(x => x.value),
      levelOfEduIds: this.levelOfEduOptions.filter(x => x.checked).map(x => x.value),
      profileIds: this.profileOptions.filter(x => x.checked).map(x => x.value)
    }

    this.groupService.getGroupsAfterFiltering(this.filterGroups).subscribe((data: IGroupWithUsersStatInfo[]) => {
      this.groupsWithUsersStatInfo = data;
    });
  }

  createNewGroup() {
    this.router.navigate(['/newGroup']);
  }

  openModalEditGroup(idGroup: number) {
    this.showModalEditGroup = true;
    this.idGroupSelectedForEditing = idGroup;
  }
}

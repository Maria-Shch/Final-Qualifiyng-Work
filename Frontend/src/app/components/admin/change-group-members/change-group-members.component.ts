import {Component} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {GroupService} from "../../../services/group.service";
import {UserService} from "../../../services/user.service";
import {IGroup} from "../../../interfaces/IGroup";
import {IUser} from "../../../interfaces/IUser";
import {CheckboxItem} from "../../../classes/CheckboxItem";
import {IChangedGroupMembers} from "../../../dto_interfaces/IChangedGroupMembers";

@Component({
  selector: 'app-change-group-members',
  templateUrl: './change-group-members.component.html',
  styleUrls: ['./change-group-members.component.css']
})
export class ChangeGroupMembersComponent {
  group: IGroup | null = null;
  studentsOfGroup: IUser[] = [];
  studentsOfGroupOptions = new Array<CheckboxItem>();
  studentsWithoutGroup: IUser[] = [];
  studentsWithoutGroupOptions = new Array<CheckboxItem>();
  unselectedStudentsOfGroupIds: number[] = [];
  selectedStudentsWithoutGroupIds: number[] = [];
  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private groupService: GroupService,
    private userService: UserService
  ) {
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      // @ts-ignore
      this.groupService.getGroupById(this.route.snapshot.paramMap.get("id")).subscribe((data: IGroup) => {
        this.group = data;
        this.userService.getStudentsByGroupId(this.group?.id!).subscribe((data: IUser[]) => {
          this.studentsOfGroup = data;
          this.studentsOfGroupOptions = data.map(x => new CheckboxItem(x.id, x.lastname + ' ' + x.name, true));
        });
      });
    });

    this.userService.getStudentsWithoutGroup().subscribe((data: IUser[]) => {
      this.studentsWithoutGroup = data;
      this.studentsWithoutGroupOptions = data.map(x => new CheckboxItem(x.id, x.lastname + ' ' + x.name, false));
    });
  }

  onChangeSelectedStudents() {
    this.unselectedStudentsOfGroupIds = this.studentsOfGroupOptions.filter(x => !x.checked).map(x => x.value);
    this.selectedStudentsWithoutGroupIds = this.studentsWithoutGroupOptions.filter(x => x.checked).map(x => x.value);
  }

  saveMembers() {
    let changedGroupMembers = {
      unselectedStudentsOfGroupIds: this.unselectedStudentsOfGroupIds,
      selectedStudentsWithoutGroupIds: this.selectedStudentsWithoutGroupIds
    } as IChangedGroupMembers;
    this.groupService.updateGroupMembers(this.group?.id!, changedGroupMembers).subscribe((data: boolean) => {
      alert("Состав группы был успешно изменён.");
      this.ngOnInit();
    });
  }
}

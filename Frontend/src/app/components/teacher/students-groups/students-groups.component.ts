import {Component, OnInit} from '@angular/core';
import {GroupService} from "../../../services/group.service";
import {IGroupWithUsersStatInfo} from "../../../dto_interfaces/IGroupWithUsersStatInfo";

@Component({
  selector: 'app-students-groups',
  templateUrl: './students-groups.component.html',
  styleUrls: ['./students-groups.component.css']
})
export class StudentsGroupsComponent implements OnInit{
  groupsWithUsersStatInfo: IGroupWithUsersStatInfo[] = [];

  constructor(
    private groupService: GroupService
  ) {}

  ngOnInit(): void {
    this.groupService.getGroupsWithUsersStatInfoForTeacher().subscribe((data: IGroupWithUsersStatInfo[]) => {
      this.groupsWithUsersStatInfo = data;
    });
  }
}

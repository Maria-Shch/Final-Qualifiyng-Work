import {Component, Input} from '@angular/core';
import {IUserStatInfo} from "../../../dto_interfaces/IUserStatInfo";
import {IUser} from "../../../interfaces/IUser";

@Component({
  selector: 'app-students-without-group-drop-down-list',
  templateUrl: './students-without-group-drop-down-list.component.html',
  styleUrls: ['./students-without-group-drop-down-list.component.css']
})
export class StudentsWithoutGroupDropDownListComponent {
  @Input() studentsWithoutGroupWithStatInfo: IUserStatInfo[] = [];
  @Input() admin: IUser | null = null;
  panelOpenState = false;
}

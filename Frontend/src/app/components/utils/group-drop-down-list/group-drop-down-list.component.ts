import {Component, Input} from '@angular/core';
import {IGroupWithUsersStatInfo} from "../../../dto_interfaces/IGroupWithUsersStatInfo";

@Component({
  selector: 'app-group-drop-down-list',
  templateUrl: './group-drop-down-list.component.html',
  styleUrls: ['./group-drop-down-list.component.css']
})
export class GroupDropDownListComponent {
  @Input() groupWithUsersStatInfo: IGroupWithUsersStatInfo | null = null;
  @Input() isAdmin: boolean | null = null;
  panelOpenState = false;
}

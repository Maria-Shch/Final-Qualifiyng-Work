import {Component, EventEmitter, Input, Output} from '@angular/core';
import {CheckboxItem} from "../../../classes/CheckboxItem";

@Component({
  selector: 'app-checkbox-group-users',
  templateUrl: './checkbox-group-users.component.html',
  styleUrls: ['./checkbox-group-users.component.css']
})
export class CheckboxGroupUsersComponent {
  @Input() options = Array<CheckboxItem>();
  @Output() toggle = new EventEmitter<any[]>();

  constructor() { }

  ngOnInit() {}
}

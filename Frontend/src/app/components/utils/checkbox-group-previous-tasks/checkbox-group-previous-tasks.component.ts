import {Component, EventEmitter, Input, Output} from '@angular/core';
import {ISimpleCollection} from "../../../dto_interfaces/ISimpleCollection";

@Component({
  selector: 'app-checkbox-group-previous-tasks',
  templateUrl: './checkbox-group-previous-tasks.component.html',
  styleUrls: ['./checkbox-group-previous-tasks.component.css']
})
export class CheckboxGroupPreviousTasksComponent {
  @Input() simpleCollection: ISimpleCollection | null = null;
  @Output() toggle = new EventEmitter<any[]>();
  panelOpenState: boolean = false;

  constructor() { }

  ngOnInit() {}
}

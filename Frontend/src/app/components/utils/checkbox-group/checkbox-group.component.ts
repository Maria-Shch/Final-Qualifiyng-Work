import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {CheckboxItem} from "../../../classes/CheckboxItem";

@Component({
  selector: 'app-checkbox-group',
  templateUrl: './checkbox-group.component.html',
  styleUrls: ['./checkbox-group.component.css']
})
export class CheckboxGroupComponent implements OnInit {
  @Input() options = Array<CheckboxItem>();
  @Output() toggle = new EventEmitter<any[]>();

  constructor() { }

  ngOnInit() {}
}

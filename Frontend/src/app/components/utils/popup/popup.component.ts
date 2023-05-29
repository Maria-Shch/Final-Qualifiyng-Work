import {Component, EventEmitter, Input, Output} from '@angular/core';

@Component({
  selector: 'app-popup',
  templateUrl: './popup.component.html',
  styleUrls: ['./popup.component.css']
})
export class PopupComponent {
  @Input() info: string = "";

  @Output() onChanged = new EventEmitter<boolean>();

  onClose() {
    this.onChanged.emit(false);
  }
}

import {Component, Input} from '@angular/core';

@Component({
  selector: 'app-previous',
  templateUrl: './previous.component.html',
  styleUrls: ['./previous.component.css']
})
export class PreviousComponent {
  @Input() serialNumberOfCurrentChapter: string = "";
  serialNumberOfPreviousChapter: number | undefined;

  ngOnInit(): void {
    this.serialNumberOfPreviousChapter = parseInt(this.serialNumberOfCurrentChapter) - 1;
  }
}

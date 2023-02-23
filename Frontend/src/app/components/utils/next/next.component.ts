import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'app-next',
  templateUrl: './next.component.html',
  styleUrls: ['./next.component.css']
})
export class NextComponent implements OnInit{
  @Input() serialNumberOfCurrentChapter: string = "";
  serialNumberOfNextChapter: number | undefined;

  ngOnInit(): void {
    this.serialNumberOfNextChapter = parseInt(this.serialNumberOfCurrentChapter) + 1;
  }
}

import {Component, Input} from '@angular/core';
import {ActivatedRoute} from "@angular/router";

@Component({
  selector: 'app-previous',
  templateUrl: './previous.component.html',
  styleUrls: ['./previous.component.css']
})
export class PreviousComponent {
  serialNumberOfCurrentChapter: string = "";
  serialNumberOfPreviousChapter: number | undefined;
  constructor(
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      // @ts-ignore
      this.serialNumberOfCurrentChapter = this.route.snapshot.paramMap.get("serialNumberOfChapter");
      this.serialNumberOfPreviousChapter = parseInt(this.serialNumberOfCurrentChapter) - 1;
    });
  }
}

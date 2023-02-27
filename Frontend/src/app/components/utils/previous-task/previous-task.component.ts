import {Component, Input} from '@angular/core';
import {ActivatedRoute} from "@angular/router";

@Component({
  selector: 'app-previous-task',
  templateUrl: './previous-task.component.html',
  styleUrls: ['./previous-task.component.css']
})
export class PreviousTaskComponent {

  serialNumberOfCurrentChapter: string = "";
  serialNumberOfCurrentBlock: string = "";
  serialNumberOfCurrentTask: string = "";
  serialNumberOfPreviousTask: number | undefined;
  constructor(
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      // @ts-ignore
      this.serialNumberOfCurrentChapter = this.route.snapshot.paramMap.get("serialNumberOfChapter");
      // @ts-ignore
      this.serialNumberOfCurrentBlock = this.route.snapshot.paramMap.get("serialNumberOfBlock");
      // @ts-ignore
      this.serialNumberOfCurrentTask = this.route.snapshot.paramMap.get("serialNumberOfTask");
      this.serialNumberOfPreviousTask = parseInt(this.serialNumberOfCurrentTask) - 1;
    });
  }
}

import {Component} from '@angular/core';
import {ActivatedRoute} from "@angular/router";

@Component({
  selector: 'app-next-task',
  templateUrl: './next-task.component.html',
  styleUrls: ['./next-task.component.css']
})
export class NextTaskComponent {

  serialNumberOfCurrentChapter: string = "";
  serialNumberOfCurrentBlock: string = "";
  serialNumberOfCurrentTask: string = "";
  serialNumberOfNextTask: number | undefined;

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
      this.serialNumberOfNextTask = parseInt(this.serialNumberOfCurrentTask) + 1;
    });
  }
}

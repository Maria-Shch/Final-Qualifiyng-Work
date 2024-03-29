import {Component, Input, OnInit} from '@angular/core';
import {ITask} from "../../../interfaces/ITask";
import {ActivatedRoute, Router} from "@angular/router";
import {TaskService} from "../../../services/task.service";
import {toErrorPage} from "../../../utils/ToErrorPageFunc";

@Component({
  selector: 'app-task-switcher',
  templateUrl: './task-switcher.component.html',
  styleUrls: ['./task-switcher.component.css']
})
export class TaskSwitcherComponent implements OnInit{
  @Input() forUser: boolean = false;
  @Input() forTests: boolean = false;
  serialNumberOfChapter: string = "";
  serialNumberOfBlock: string = "";
  serialNumberOfTask: string = "";
  userId: string = "";
  nextTask: ITask | null = null;
  previousTask: ITask | null = null;
  linkToNextTask: string = "";
  linkToPreviousTask: string = "";

  constructor(
    private route: ActivatedRoute,
    private taskService: TaskService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      // @ts-ignore
      this.serialNumberOfChapter = this.route.snapshot.paramMap.get("serialNumberOfChapter");
      // @ts-ignore
      this.serialNumberOfBlock = this.route.snapshot.paramMap.get("serialNumberOfBlock");
      // @ts-ignore
      this.serialNumberOfTask = this.route.snapshot.paramMap.get("serialNumberOfTask");

      this.taskService.getNextTask(this.serialNumberOfChapter, this.serialNumberOfBlock, this.serialNumberOfTask).subscribe(
      (data: ITask | null) => {
        this.nextTask = data;
        if (data != null){
          this.linkToNextTask = `/chapter/${data.block.chapter.serialNumber}/block/${data.block.serialNumber}/task/${data.serialNumber}`;
          if (this.forUser){
            // @ts-ignore
            this.userId = this.route.snapshot.paramMap.get("id");
            this.linkToNextTask = this.linkToNextTask + `/student/${this.userId}`;
          }
          if (this.forTests){
            this.linkToNextTask = this.linkToNextTask + `/tests`;
          }
        }
      },
      (error)=>{ toErrorPage(error, this.router);});

      this.taskService.getPreviousTask(this.serialNumberOfChapter, this.serialNumberOfBlock, this.serialNumberOfTask).subscribe(
        (data: ITask | null) => {
          this.previousTask = data;
          if (data != null){
            this.linkToPreviousTask = `/chapter/${data.block.chapter.serialNumber}/block/${data.block.serialNumber}/task/${data.serialNumber}`;
            if (this.forUser){
              // @ts-ignore
              this.userId = this.route.snapshot.paramMap.get("id");
              this.linkToPreviousTask = this.linkToPreviousTask + `/student/${this.userId}`;
            }
            if (this.forTests){
              this.linkToPreviousTask = this.linkToPreviousTask + `/tests`;
            }
          }
        },
      (error)=>{ toErrorPage(error, this.router);});
    });
  }
}

import {Component, OnInit} from '@angular/core';
import {ITask} from "../../../interfaces/ITask";
import {ActivatedRoute, Router} from "@angular/router";
import {CollectionOfTasksService} from "../../../services/collection-of-tasks.service";
import {toErrorPage} from "../../../utils/ToErrorPageFunc";

@Component({
  selector: 'app-task-switcher',
  templateUrl: './task-switcher.component.html',
  styleUrls: ['./task-switcher.component.css']
})
export class TaskSwitcherComponent implements OnInit{
  serialNumberOfChapter: string = "";
  serialNumberOfBlock: string = "";
  serialNumberOfTask: string = "";
  nextTask: ITask | null = null;
  previousTask: ITask | null = null;
  linkToNextTask: string = "";
  linkToPreviousTask: string = "";

  constructor(
    private route: ActivatedRoute,
    private collectionOfTasksService: CollectionOfTasksService,
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

      this.collectionOfTasksService.getNextTask(this.serialNumberOfChapter, this.serialNumberOfBlock, this.serialNumberOfTask).subscribe(
      (data: ITask | null) => {
        this.nextTask = data;
        if (data != null){
          this.linkToNextTask = `/chapter/${data.block.chapter.serialNumber}/block/${data.block.serialNumber}/task/${data.serialNumber}`;
        }
      },
      (error)=>{ toErrorPage(error, this.router);});

      this.collectionOfTasksService.getPreviousTask(this.serialNumberOfChapter, this.serialNumberOfBlock, this.serialNumberOfTask).subscribe(
        (data: ITask | null) => {
          this.previousTask = data;
          if (data != null){
            this.linkToPreviousTask = `/chapter/${data.block.chapter.serialNumber}/block/${data.block.serialNumber}/task/${data.serialNumber}`;
          }
        },
      (error)=>{ toErrorPage(error, this.router);});
    });
  }
}

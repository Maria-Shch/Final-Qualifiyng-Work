import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {CollectionOfTasksService} from "../../../services/collection-of-tasks.service";
import {ITaskOfBlock} from "../../../dto_interfaces/ITaskOfBlock";
import {toErrorPage} from "../../../utils/ToErrorPageFunc";

@Component({
  selector: 'app-practice',
  templateUrl: './practice.component.html',
  styleUrls: ['./practice.component.css']
})
export class PracticeComponent implements OnInit{
  serialNumberOfChapter: string = "";
  serialNumberOfBlock: string = "";
  tasksOfBlock: ITaskOfBlock[] = [];
  blockName: string = "";
  isBlockLast: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private collectionOfTasksService: CollectionOfTasksService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      // @ts-ignore
      this.serialNumberOfChapter = this.route.snapshot.paramMap.get("serialNumberOfChapter");
      // @ts-ignore
      this.serialNumberOfBlock = this.route.snapshot.paramMap.get("serialNumberOfBlock");

      this.collectionOfTasksService.getNameOfBlock(this.serialNumberOfChapter, this.serialNumberOfBlock).subscribe(
      (data : string) => {
        this.blockName = data;
      });

      this.collectionOfTasksService.getPractice(this.serialNumberOfChapter, this.serialNumberOfBlock).subscribe(
        (data : ITaskOfBlock[]) => {
        this.tasksOfBlock = data;
      });

      this.collectionOfTasksService.getCountOfBlocks(this.serialNumberOfChapter).subscribe(
        (count: number) => {
          if (this.serialNumberOfBlock === count.toString()) this.isBlockLast = true;
          else this.isBlockLast = false;
        },
      (error)=>{ toErrorPage(error, this.router);});
    });
  }

  toTask(serialNumberOfChapter: string, serialNumberOfBlock: string, serialNumberOfTask: number) {
    this.router.navigate(['/chapter', serialNumberOfChapter, 'block', serialNumberOfBlock, 'task', serialNumberOfTask]);
  }
}

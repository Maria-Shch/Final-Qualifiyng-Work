import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {TaskService} from "../../../services/task.service";
import {ITaskOfBlock} from "../../../dto_interfaces/ITaskOfBlock";
import {toErrorPage} from "../../../utils/ToErrorPageFunc";
import {BlockService} from "../../../services/block.service";
import {IUser} from "../../../interfaces/IUser";
import {AuthorizationService} from "../../../services/authorization.service";
import {UserService} from "../../../services/user.service";
import {IBlock} from "../../../interfaces/IBlock";

@Component({
  selector: 'app-practice',
  templateUrl: './practice.component.html',
  styleUrls: ['./practice.component.css']
})
export class PracticeComponent implements OnInit{
  serialNumberOfChapter: string = "";
  serialNumberOfBlock: string = "";
  tasksOfBlock: ITaskOfBlock[] = [];
  isBlockLast: boolean = false;
  user: IUser | null = null;
  block: IBlock | null = null;

  constructor(
    private route: ActivatedRoute,
    private taskService: TaskService,
    private blockService: BlockService,
    private router: Router,
    private authService: AuthorizationService,
    public userService: UserService
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      // @ts-ignore
      this.serialNumberOfChapter = this.route.snapshot.paramMap.get("serialNumberOfChapter");
      // @ts-ignore
      this.serialNumberOfBlock = this.route.snapshot.paramMap.get("serialNumberOfBlock");

      this.blockService.getBlock(this.serialNumberOfChapter, this.serialNumberOfBlock).subscribe(
      (data : IBlock) => {
        this.block = data;
      });

      this.taskService.getPractice(this.serialNumberOfChapter, this.serialNumberOfBlock).subscribe(
        (data : ITaskOfBlock[]) => {
        this.tasksOfBlock = data;
      });

      this.blockService.getCountOfBlocks(this.serialNumberOfChapter).subscribe(
        (count: number) => {
          if (this.serialNumberOfBlock === count.toString()) this.isBlockLast = true;
          else this.isBlockLast = false;
        },
      (error)=>{ toErrorPage(error, this.router);});

      if (this.authService.isLoggedIn()) {
        this.userService.getUser().subscribe((data: IUser) => {
          this.user = data;
        });
      };
    });
  }

  toTask(serialNumberOfChapter: string, serialNumberOfBlock: string, serialNumberOfTask: number) {
    this.router.navigate(['/chapter', serialNumberOfChapter, 'block', serialNumberOfBlock, 'task', serialNumberOfTask]);
  }

  editTask(taskId: number) {
    this.router.navigate(['/task/edit/', taskId]);
  }

  createNewTask(blockId: number) {
    this.router.navigate(['/newTask/', blockId]);
  }
}

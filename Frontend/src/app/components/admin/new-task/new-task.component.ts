import { Component } from '@angular/core';
import {IChapter} from "../../../interfaces/IChapter";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {ActivatedRoute, Router} from "@angular/router";
import {ChapterService} from "../../../services/chapter.service";
import {BlockService} from "../../../services/block.service";
import {FormUtils} from "../../../utils/FormUtils";
import {IBlock} from "../../../interfaces/IBlock";
import {TaskService} from "../../../services/task.service";
import {ITask} from "../../../interfaces/ITask";

@Component({
  selector: 'app-new-task',
  templateUrl: './new-task.component.html',
  styleUrls: ['./new-task.component.css']
})
export class NewTaskComponent {

  countOfTasks: number | null = null;
  block: IBlock | null = null;
  creatingTaskFormHasBeenSubmitted: boolean = false;
  repeatedNameOfTask: boolean = false;

  creatingTaskForm: FormGroup = new FormGroup({
    name: new FormControl<string | null>(null, [Validators.required]),
    serialNumber: new FormControl<number | null>(null, [Validators.required])
  });

  constructor(
    private route: ActivatedRoute,
    private blockService: BlockService,
    private taskService: TaskService,
    private router:Router
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      // @ts-ignore
      this.blockService.getBlockById(this.route.snapshot.paramMap.get("blockId")).subscribe((data: IBlock) => {
        this.block = data;
        console.log(this.block);
        this.taskService.getCountOfTasks(this.block?.chapter?.serialNumber.toString(), this.block.serialNumber.toString()).subscribe((data: number) => {
          this.countOfTasks = data;
          this.creatingTaskForm = new FormGroup({
            name: new FormControl<string | null>(null, [Validators.required]),
            serialNumber: new FormControl<number | null>(this.countOfTasks+1, [Validators.required])
          });
        });
      });
    });
  }

  onSubmitCreatingTaskForm() {
    this.creatingTaskFormHasBeenSubmitted = true;
    if (FormUtils.getControlErrors(this.creatingTaskForm) == null){
      let newTask = this.creatingTaskForm.value as ITask;
      // @ts-ignore
      newTask.block = this.block;
      this.taskService.checkIsPresentNameOfTask(newTask).subscribe((data: boolean) => {
        this.repeatedNameOfTask = data;
        if (!data){
          this.taskService.createNewTask(newTask).subscribe((data: ITask) =>{
            this.router.navigate(['/chapter', this.block?.chapter?.serialNumber, 'block', this.block?.serialNumber, 'task', data.serialNumber]);
            // alert("Вы создали новую задачу: Задачу " + this.block?.chapter?.serialNumber + '. ' + this.block?.serialNumber + '. ' + data.serialNumber + '. ' + data.name);
          });
        }
      });
    }
  }

  get name(){
    return this.creatingTaskForm.controls.name as FormControl;
  }

  get serialNumber(){
    return this.creatingTaskForm.controls.serialNumber as FormControl;
  }
}

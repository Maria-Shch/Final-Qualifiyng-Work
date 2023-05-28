import {Component} from '@angular/core';
import {IBlock} from "../../../interfaces/IBlock";
import {IChapter} from "../../../interfaces/IChapter";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {ChapterService} from "../../../services/chapter.service";
import {BlockService} from "../../../services/block.service";
import {ActivatedRoute, Router} from "@angular/router";
import {FormUtils} from "../../../utils/FormUtils";
import {ITask} from "../../../interfaces/ITask";
import {TaskService} from "../../../services/task.service";

@Component({
  selector: 'app-edit-task',
  templateUrl: './edit-task.component.html',
  styleUrls: ['./edit-task.component.css']
})
export class EditTaskComponent {
  task: ITask | null = null;
  blocks: IBlock[] =[];
  editingTaskFormHasBeenSubmitted: boolean = false;
  repeatedNameOfTask: boolean = false;

  editingTaskForm: FormGroup = new FormGroup({
    name: new FormControl<string | null>(null, [Validators.required]),
    serialNumber: new FormControl<number | null>(null, [Validators.required]),
    block: new FormControl<IChapter | null>(null, [Validators.required])
  });

  constructor(
    private taskService: TaskService,
    private chapterService: ChapterService,
    private blockService: BlockService,
    private route: ActivatedRoute,
    private router:Router
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.blockService.getAllBlocks().subscribe(
        (data: IBlock[]) => {
          this.blocks = data;

          // @ts-ignore
          this.taskService.getTaskById(this.route.snapshot.paramMap.get("taskId")).subscribe((data: ITask) => {
            this.task = data;
            this.editingTaskForm = new FormGroup({
              name: new FormControl<string | null>(this.task?.name, [Validators.required]),
              serialNumber: new FormControl<number | null>(this.task?.serialNumber, [Validators.required]),
              block: new FormControl<IChapter | null>(this.blocks.filter(x => x.id == this.task?.block?.id)[0], [Validators.required])
            });
          });
        });
    });
  }

  onSubmitEditingTaskForm() {
    this.editingTaskFormHasBeenSubmitted = true;
    if (FormUtils.getControlErrors(this.editingTaskForm) == null){
      let updatedTask = this.editingTaskForm.value as ITask;
      // @ts-ignore
      updatedTask.id = this.task?.id;
      this.taskService.checkIsPresentNameOfTask(updatedTask).subscribe((data: boolean) => {
        this.repeatedNameOfTask = data;
        if (data && updatedTask.name == this.task?.name){
          this.repeatedNameOfTask = false;
        }
        if (!this.repeatedNameOfTask){
          this.taskService.updateTask(updatedTask).subscribe((data: ITask) =>{
            this.router.navigate(['/chapter', data?.block.chapter.serialNumber, 'block', data?.block?.serialNumber, 'practice']);
          });
        }
      });
    }
  }

  get name(){
    return this.editingTaskForm.controls.name as FormControl;
  }

  get serialNumber(){
    return this.editingTaskForm.controls.serialNumber as FormControl;
  }
}

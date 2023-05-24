import {Component} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {Router} from "@angular/router";
import {FormUtils} from "../../../utils/FormUtils";
import {IChapter} from "../../../interfaces/IChapter";
import {TaskService} from "../../../services/task.service";
import {IResponseRepeatedParamsOfChapter} from "../../../dto_interfaces/IResponseRepeatedParamsOfChapter";

@Component({
  selector: 'app-new-chapter',
  templateUrl: './new-chapter.component.html',
  styleUrls: ['./new-chapter.component.css']
})
export class NewChapterComponent {

  creatingChapterFormHasBeenSubmitted: boolean = false;
  checkResult: IResponseRepeatedParamsOfChapter | null = null;

  creatingChapterForm: FormGroup = new FormGroup({
    name: new FormControl<string | null>(null, [Validators.required]),
    serialNumber: new FormControl<number | null>(null, [Validators.required])
  });

  constructor(
    private taskService: TaskService,
    private router:Router
  ) {}

  ngOnInit(): void {

  }

  onSubmitCreatingChapterForm() {
    this.creatingChapterFormHasBeenSubmitted = true;
    if (FormUtils.getControlErrors(this.creatingChapterForm) == null){
      let newChapter = this.creatingChapterForm.value as IChapter;
      this.taskService.checkIsPresentNameOrSerialNumberOfChapter(newChapter).subscribe((data: IResponseRepeatedParamsOfChapter) => {
        this.checkResult = data;
        if (!this.checkResult.repeatedSerialNumber && !this.checkResult.repeatedName){
          this.taskService.createNewChapter(newChapter).subscribe((data: IChapter) =>{
            this.router.navigate(['/chapters']);
            alert("Вы создали новую главу сборника: Глава " + data.serialNumber + '. ' + data.name);
          });
        }
      })
    }
  }

  get name(){
    return this.creatingChapterForm.controls.name as FormControl;
  }

  get serialNumber(){
    return this.creatingChapterForm.controls.serialNumber as FormControl;
  }
}

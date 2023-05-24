import {Component} from '@angular/core';
import {IResponseRepeatedParamsOfChapter} from "../../../dto_interfaces/IResponseRepeatedParamsOfChapter";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {TaskService} from "../../../services/task.service";
import {ActivatedRoute, Router} from "@angular/router";
import {FormUtils} from "../../../utils/FormUtils";
import {IChapter} from "../../../interfaces/IChapter";

@Component({
  selector: 'app-edit-chapter',
  templateUrl: './edit-chapter.component.html',
  styleUrls: ['./edit-chapter.component.css']
})
export class EditChapterComponent {

  chapter: IChapter | null = null;
  editingChapterFormHasBeenSubmitted: boolean = false;
  checkResult: IResponseRepeatedParamsOfChapter | null = null;

  editingChapterForm: FormGroup = new FormGroup({
    name: new FormControl<string | null>(null, [Validators.required]),
    serialNumber: new FormControl<number | null>(null, [Validators.required])
  });

  constructor(
    private taskService: TaskService,
    private route: ActivatedRoute,
    private router:Router
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      // @ts-ignore
      this.taskService.getChapterById(this.route.snapshot.paramMap.get("id")).subscribe((data: IChapter) => {
        this.chapter = data;
        this.editingChapterForm = new FormGroup({
          name: new FormControl<string | null>(this.chapter?.name, [Validators.required]),
          serialNumber: new FormControl<number | null>(this.chapter?.serialNumber, [Validators.required])
        });
      });
    });
  }

  onSubmitEditingChapterForm() {
    this.editingChapterFormHasBeenSubmitted = true;
    if (FormUtils.getControlErrors(this.editingChapterForm) == null){
      let updatedChapter = this.editingChapterForm.value as IChapter;
      // @ts-ignore
      updatedChapter.id = this.chapter?.id;
      this.taskService.checkIsPresentNameOrSerialNumberOfChapter(updatedChapter).subscribe((data: IResponseRepeatedParamsOfChapter) => {
        this.checkResult = data;
        if (data.repeatedSerialNumber && updatedChapter.serialNumber == this.chapter?.serialNumber){
          this.checkResult.repeatedSerialNumber = false;
        }
        if (data.repeatedName && updatedChapter.name == this.chapter?.name){
          this.checkResult.repeatedName = false;
        }
        if (!this.checkResult.repeatedSerialNumber && !this.checkResult.repeatedName){
          this.taskService.updateChapter(updatedChapter).subscribe((data: IChapter) =>{
            this.router.navigate(['/chapters']);
          });
        }
      })
    }
  }

  get name(){
    return this.editingChapterForm.controls.name as FormControl;
  }

  get serialNumber(){
    return this.editingChapterForm.controls.serialNumber as FormControl;
  }
}

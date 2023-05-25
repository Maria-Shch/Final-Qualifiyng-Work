import {Component} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {ActivatedRoute, Router} from "@angular/router";
import {FormUtils} from "../../../utils/FormUtils";
import {IChapter} from "../../../interfaces/IChapter";
import {ChapterService} from "../../../services/chapter.service";

@Component({
  selector: 'app-edit-chapter',
  templateUrl: './edit-chapter.component.html',
  styleUrls: ['./edit-chapter.component.css']
})
export class EditChapterComponent {

  chapter: IChapter | null = null;
  editingChapterFormHasBeenSubmitted: boolean = false;
  repeatedNameOfChapter: boolean = false;

  editingChapterForm: FormGroup = new FormGroup({
    name: new FormControl<string | null>(null, [Validators.required]),
    serialNumber: new FormControl<number | null>(null, [Validators.required])
  });

  constructor(
    private chapterService: ChapterService,
    private route: ActivatedRoute,
    private router:Router
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      // @ts-ignore
      this.chapterService.getChapterById(this.route.snapshot.paramMap.get("id")).subscribe((data: IChapter) => {
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
      this.chapterService.checkIsPresentNameOfChapter(updatedChapter).subscribe((data: boolean) => {
        this.repeatedNameOfChapter = data;
        if (data && updatedChapter.name == this.chapter?.name){
          this.repeatedNameOfChapter = false;
        }
        if (!this.repeatedNameOfChapter){
          this.chapterService.updateChapter(updatedChapter).subscribe((data: IChapter) =>{
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

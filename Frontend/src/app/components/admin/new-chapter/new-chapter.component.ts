import {Component} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {Router} from "@angular/router";
import {FormUtils} from "../../../utils/FormUtils";
import {IChapter} from "../../../interfaces/IChapter";
import {ChapterService} from "../../../services/chapter.service";

@Component({
  selector: 'app-new-chapter',
  templateUrl: './new-chapter.component.html',
  styleUrls: ['./new-chapter.component.css']
})
export class NewChapterComponent {

  countOfChapters: number | null = null;
  creatingChapterFormHasBeenSubmitted: boolean = false;
  repeatedNameOfChapter: boolean = false;

  creatingChapterForm: FormGroup = new FormGroup({
    name: new FormControl<string | null>(null, [Validators.required]),
    serialNumber: new FormControl<number | null>(null, [Validators.required])
  });

  constructor(
    private chapterService: ChapterService,
    private router:Router
  ) {}

  ngOnInit(): void {
    this.chapterService.getCountOfChapters().subscribe((data: number) => {
      this.countOfChapters = data;
      this.creatingChapterForm = new FormGroup({
        name: new FormControl<string | null>(null, [Validators.required]),
        serialNumber: new FormControl<number | null>(this.countOfChapters+1, [Validators.required])
      });
    });
  }

  onSubmitCreatingChapterForm() {
    this.creatingChapterFormHasBeenSubmitted = true;
    if (FormUtils.getControlErrors(this.creatingChapterForm) == null){
      let newChapter = this.creatingChapterForm.value as IChapter;
      this.chapterService.checkIsPresentNameOfChapter(newChapter).subscribe((data: boolean) => {
        this.repeatedNameOfChapter = data;
        if (!data){
          this.chapterService.createNewChapter(newChapter).subscribe((data: IChapter) =>{
            this.router.navigate(['/chapter', data.serialNumber]);
            alert("Вы создали новую главу сборника: Глава " + data.serialNumber + '. ' + data.name);
          });
        }
      });
    }
  }

  get name(){
    return this.creatingChapterForm.controls.name as FormControl;
  }

  get serialNumber(){
    return this.creatingChapterForm.controls.serialNumber as FormControl;
  }
}

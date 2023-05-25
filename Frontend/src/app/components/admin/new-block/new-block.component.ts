import {Component} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {ActivatedRoute, Router} from "@angular/router";
import {FormUtils} from "../../../utils/FormUtils";
import {IChapter} from "../../../interfaces/IChapter";
import {IBlock} from "../../../interfaces/IBlock";
import {BlockService} from "../../../services/block.service";
import {ChapterService} from "../../../services/chapter.service";

@Component({
  selector: 'app-new-block',
  templateUrl: './new-block.component.html',
  styleUrls: ['./new-block.component.css']
})
export class NewBlockComponent {

  countOfBlocks: number | null = null;
  chapter: IChapter | null = null;
  creatingBlockFormHasBeenSubmitted: boolean = false;
  repeatedNameOfBlock: boolean = false;

  creatingBlockForm: FormGroup = new FormGroup({
    name: new FormControl<string | null>(null, [Validators.required]),
    serialNumber: new FormControl<number | null>(null, [Validators.required])
  });

  constructor(
    private route: ActivatedRoute,
    private chapterService: ChapterService,
    private blockService: BlockService,
    private router:Router
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      // @ts-ignore
      this.chapterService.getChapterById(this.route.snapshot.paramMap.get("chapterId")).subscribe((data: IChapter) => {
        this.chapter = data;
        this.blockService.getCountOfBlocks(this.chapter?.serialNumber.toString()).subscribe((data: number) => {
          this.countOfBlocks = data;
          this.creatingBlockForm = new FormGroup({
            name: new FormControl<string | null>(null, [Validators.required]),
            serialNumber: new FormControl<number | null>(this.countOfBlocks+1, [Validators.required])
          });
        });
      });
    });
  }

  onSubmitCreatingBlockForm() {
    this.creatingBlockFormHasBeenSubmitted = true;
    if (FormUtils.getControlErrors(this.creatingBlockForm) == null){
      let newBlock = this.creatingBlockForm.value as IBlock;
      // @ts-ignore
      newBlock.chapter = this.chapter;
      this.blockService.checkIsPresentNameOfBlock(newBlock).subscribe((data: boolean) => {
        this.repeatedNameOfBlock = data;
        if (!data){
          this.blockService.createNewBlock(newBlock).subscribe((data: IBlock) =>{
            this.router.navigate(['/chapter', this.chapter?.serialNumber, 'block', data.serialNumber, 'theory']);
            alert("Вы создали новый блок: Блок " + this.chapter?.serialNumber + '. ' + data.serialNumber + '. ' + data.name);
          });
        }
      });
    }
  }

  get name(){
    return this.creatingBlockForm.controls.name as FormControl;
  }

  get serialNumber(){
    return this.creatingBlockForm.controls.serialNumber as FormControl;
  }
}

import {Component} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {ActivatedRoute, Router} from "@angular/router";
import {FormUtils} from "../../../utils/FormUtils";
import {IChapter} from "../../../interfaces/IChapter";
import {IBlock} from "../../../interfaces/IBlock";
import {BlockService} from "../../../services/block.service";

@Component({
  selector: 'app-new-block',
  templateUrl: './new-block.component.html',
  styleUrls: ['./new-block.component.css']
})
export class NewBlockComponent {

  chapter: IChapter | null = null;
  creatingBlockFormHasBeenSubmitted: boolean = false;

  creatingBlockForm: FormGroup = new FormGroup({
    name: new FormControl<string | null>(null, [Validators.required]),
    serialNumber: new FormControl<number | null>(null, [Validators.required])
  });

  constructor(
    private route: ActivatedRoute,
    private blockService: BlockService,
    private router:Router
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      // @ts-ignore
      this.blockService.getChapterById(this.route.snapshot.paramMap.get("chapterId")).subscribe((data: IChapter) => {
        this.chapter = data;
      });
    });
  }

  onSubmitCreatingBlockForm() {
    this.creatingBlockFormHasBeenSubmitted = true;
    if (FormUtils.getControlErrors(this.creatingBlockForm) == null){
      let newBlock = this.creatingBlockForm.value as IBlock;
      if (this.chapter) {
        newBlock.chapter = this.chapter;
      }
      this.blockService.checkIsPresentNameOrSerialNumberOfBlock(newBlock).subscribe((data: boolean) => {
        // this.checkResult = data;
        // if (!this.checkResult.repeatedSerialNumber && !this.checkResult.repeatedName){
        //   this.blockService.createNewBlock(newBlock).subscribe((data: IBlock) =>{
        //     this.router.navigate(['/chapter/']);
        //     alert("Вы создали новый блок: Блок " + this.chapter?.serialNumber + '. ' + data.serialNumber + '. ' + data.name);
        //   });
        // }
      })
    }
  }

  get name(){
    return this.creatingBlockForm.controls.name as FormControl;
  }

  get serialNumber(){
    return this.creatingBlockForm.controls.serialNumber as FormControl;
  }
}

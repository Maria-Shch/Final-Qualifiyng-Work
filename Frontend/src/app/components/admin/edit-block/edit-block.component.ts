import {Component} from '@angular/core';
import {IChapter} from "../../../interfaces/IChapter";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {ChapterService} from "../../../services/chapter.service";
import {ActivatedRoute, Router} from "@angular/router";
import {FormUtils} from "../../../utils/FormUtils";
import {BlockService} from "../../../services/block.service";
import {IBlock} from "../../../interfaces/IBlock";

@Component({
  selector: 'app-edit-block',
  templateUrl: './edit-block.component.html',
  styleUrls: ['./edit-block.component.css']
})
export class EditBlockComponent {
  block: IBlock | null = null;
  chapters: IChapter[] =[];
  editingBlockFormHasBeenSubmitted: boolean = false;
  repeatedNameOfBlock: boolean = false;

  editingBlockForm: FormGroup = new FormGroup({
    name: new FormControl<string | null>(null, [Validators.required]),
    serialNumber: new FormControl<number | null>(null, [Validators.required]),
    chapter: new FormControl<IChapter | null>(null, [Validators.required])
  });

  constructor(
    private chapterService: ChapterService,
    private blockService: BlockService,
    private route: ActivatedRoute,
    private router:Router
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.chapterService.getChapters().subscribe(
        (data: IChapter[]) => {
          this.chapters = data;

          // @ts-ignore
          this.blockService.getBlockById(this.route.snapshot.paramMap.get("blockId")).subscribe((data: IBlock) => {
            this.block = data;
            this.editingBlockForm = new FormGroup({
              name: new FormControl<string | null>(this.block?.name, [Validators.required]),
              serialNumber: new FormControl<number | null>(this.block?.serialNumber, [Validators.required]),
              chapter: new FormControl<IChapter | null>(this.chapters.filter(x => x.id == this.block?.chapter?.id)[0], [Validators.required])
            });
          });
        });
    });
  }

  onSubmitEditingBlockForm() {
    this.editingBlockFormHasBeenSubmitted = true;
    if (FormUtils.getControlErrors(this.editingBlockForm) == null){
      let updatedBlock = this.editingBlockForm.value as IBlock;
      // @ts-ignore
      updatedBlock.id = this.block?.id;
      this.blockService.checkIsPresentNameOfBlock(updatedBlock).subscribe((data: boolean) => {
        this.repeatedNameOfBlock = data;
        if (data && updatedBlock.name == this.block?.name){
          this.repeatedNameOfBlock = false;
        }
        if (!this.repeatedNameOfBlock){
          this.blockService.updateBlock(updatedBlock).subscribe((data: IBlock) =>{
            this.router.navigate(['/chapter', data?.chapter.serialNumber]);
          });
        }
      });
    }
  }

  get name(){
    return this.editingBlockForm.controls.name as FormControl;
  }

  get serialNumber(){
    return this.editingBlockForm.controls.serialNumber as FormControl;
  }
}

import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {TaskService} from "../../../services/task.service";
import tinymce from "tinymce";
import {IBlock} from "../../../interfaces/IBlock";
import {UserService} from "../../../services/user.service";
import {Observable} from "rxjs";
import {toErrorPage} from "../../../utils/ToErrorPageFunc";
import {BlockService} from "../../../services/block.service";


@Component({
  selector: 'app-theory',
  templateUrl: './theory.component.html',
  styleUrls: ['./theory.component.css']
})
export class TheoryComponent implements OnInit{

  serialNumberOfChapter: string = "";
  serialNumberOfBlock: string = "";
  block: IBlock | null = null;
  isBlockLast: boolean = false;
  isEditing: boolean = false;
  isEmptyTextTheory: boolean = false;

  editorConfig = {
    base_url: '/tinymce',
    suffix: '.min',
    plugins: 'lists link image table wordcount style',
    height: 720,
    toolbar: 'undo redo | styles | fontfamily | fontsize | line_height_formats | forecolor | bold italic | alignleft aligncenter alignright alignjustify | numlist bullist | outdent indent | link image | code',
    init_instance_callback: (editor: { id: any; }) => {
      this.setContent();
    },
  };

  constructor(
    private route: ActivatedRoute,
    private taskService: TaskService,
    private blockService: BlockService,
    private router: Router,
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
        if (document.getElementById('textTheory') != null){
          // @ts-ignore
          document.getElementById('textTheory').innerHTML = this.block.textTheory;
        }
        if (data.textTheory == null ){
          this.isEmptyTextTheory = true;
        }
      });

      this.blockService.getCountOfBlocks(this.serialNumberOfChapter).subscribe(
      (count: number) => {
        if (this.serialNumberOfBlock === count.toString()) this.isBlockLast = true;
        else this.isBlockLast = false;
      },
      (error)=>{ toErrorPage(error, this.router);});
    });
  }

  save(){
    this.sendContent().subscribe();
  }

  saveAndTurnOffEditing() {
    this.sendContent().subscribe((data: IBlock) => {
      this.block = data;
      this.isEditing = false;
      this.ngOnInit();
      if (data.textTheory == null){
        this.isEmptyTextTheory = true;
      } else {
        this.isEmptyTextTheory = false;
      }
    });
  }

  turnOffEditing() {
    this.isEditing = false;
    this.ngOnInit();
  }

  setContent(){
    // @ts-ignore
    tinymce.get('editor').setContent(this.block?.textTheory);
  }

  sendContent() : Observable<IBlock>{
    let theory = tinymce.get('editor')?.getContent();
    // @ts-ignore
    return this.blockService.saveTheoryOfBlock(this.serialNumberOfChapter, this.serialNumberOfBlock, theory);
  }
}

import {Component, Input} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {IBlock} from "../../../interfaces/IBlock";
import {toErrorPage} from "../../../utils/ToErrorPageFunc";
import {BlockService} from "../../../services/block.service";

@Component({
  selector: 'app-block-switcher',
  templateUrl: './block-switcher.component.html',
  styleUrls: ['./block-switcher.component.css']
})
export class BlockSwitcherComponent {
  @Input() section: string = "";
  serialNumberOfChapter: string = "";
  serialNumberOfBlock: string = "";
  nextBlock: IBlock | null = null;
  previousBlock: IBlock | null = null;
  linkToNextBlock: string = "";
  linkToPreviousBlock: string = "";

  constructor(
    private route: ActivatedRoute,
    private blockService: BlockService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      // @ts-ignore
      this.serialNumberOfChapter = this.route.snapshot.paramMap.get("serialNumberOfChapter");
      // @ts-ignore
      this.serialNumberOfBlock = this.route.snapshot.paramMap.get("serialNumberOfBlock");

      this.blockService.getNextBlock(this.serialNumberOfChapter, this.serialNumberOfBlock).subscribe(
        (data: IBlock | null) => {
          this.nextBlock = data;
          if (data != null){
            this.linkToNextBlock = `/chapter/${data.chapter.serialNumber}/block/${data.serialNumber}/${this.section}`;
          }
        },
      (error)=>{ toErrorPage(error, this.router);});

      this.blockService.getPreviousBlock(this.serialNumberOfChapter, this.serialNumberOfBlock).subscribe(
        (data: IBlock | null) => {
          this.previousBlock = data;
          if (data != null){
            this.linkToPreviousBlock = `/chapter/${data.chapter.serialNumber}/block/${data.serialNumber}/${this.section}`;
          }
        },
      (error)=>{ toErrorPage(error, this.router);});
    });
  }
}

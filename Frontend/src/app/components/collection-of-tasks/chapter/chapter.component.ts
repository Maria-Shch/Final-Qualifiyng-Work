import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {IBlock} from "../../../interfaces/IBlock";
import {IChapter} from "../../../interfaces/IChapter";
import {toErrorPage} from "../../../utils/ToErrorPageFunc";
import {BlockService} from "../../../services/block.service";
import {ChapterService} from "../../../services/chapter.service";

@Component({
  selector: 'app-chapter',
  templateUrl: './chapter.component.html',
  styleUrls: ['./chapter.component.css']
})
export class ChapterComponent implements OnInit{
  serialNumberOfChapter: string = "";
  blocks:IBlock[] = [];
  chapter: IChapter | null = null;
  isChapterLast: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private chapterService: ChapterService,
    private blockService: BlockService,
    private router: Router
  ) {}

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      // @ts-ignore
      this.serialNumberOfChapter = this.route.snapshot.paramMap.get("serialNumberOfChapter");

      this.chapterService.getCountOfChapters().subscribe(
      (count: number) => {
        if (this.serialNumberOfChapter === count.toString()) this.isChapterLast = true;
        else this.isChapterLast = false;
      },
      (error)=>{ toErrorPage(error, this.router);});

      this.blockService.getBlocksOfChapter(this.serialNumberOfChapter).subscribe(
      (data: IBlock[]) => {
        this.blocks = data;
        this.chapter = this.blocks[1].chapter;
      },
      (error)=>{ toErrorPage(error, this.router);});
    });
  }

  toTheoryOfBlock(serialNumberOfChapter: any, serialNumberOfBlock: number) {
    this.router.navigate(['/chapter', serialNumberOfChapter, 'block', serialNumberOfBlock, 'theory']);
  }
}


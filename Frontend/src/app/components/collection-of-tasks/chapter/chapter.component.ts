import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {CollectionOfTasksService} from "../../../services/collection-of-tasks.service";
import {IBlock} from "../../../interfaces/IBlock";
import {IChapter} from "../../../interfaces/IChapter";

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
    private collectionOfTasksService: CollectionOfTasksService,
    private router: Router
  ) {}

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      // @ts-ignore
      this.serialNumberOfChapter = this.route.snapshot.paramMap.get("serialNumberOfChapter");

      this.collectionOfTasksService.getCountOfChapters().subscribe(
      (count: number) => {
        if (this.serialNumberOfChapter === count.toString()) this.isChapterLast = true;
        else this.isChapterLast = false;
      },
      (error)=>{
        console.log(error);
        this.router.navigate(['/error']);
      });

      this.collectionOfTasksService.getBlocksOfChapter(this.serialNumberOfChapter).subscribe(
        (data: IBlock[]) => {
          this.blocks = data;
          this.chapter = this.blocks[1].chapter;
        },
        (error)=>{
          console.log(error);
          this.router.navigate(['/error']);
        });
    });
  }

  toTheoryOfBlock(serialNumberOfChapter: any, serialNumberOfBlock: number) {
    this.router.navigate(['/chapter', serialNumberOfChapter, 'block', serialNumberOfBlock, 'theory']);
  }
}


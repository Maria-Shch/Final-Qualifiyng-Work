import {Component} from '@angular/core';
import {Router} from "@angular/router";
import {IChapter} from "../../../interfaces/IChapter";
import {toErrorPage} from "../../../utils/ToErrorPageFunc";
import {ChapterService} from "../../../services/chapter.service";

@Component({
  selector: 'app-chapters',
  templateUrl: './chapters.component.html',
  styleUrls: ['./chapters.component.css']
})
export class ChaptersComponent {
  chapters:IChapter[] = [];
  constructor(
    private chapterService: ChapterService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.chapterService.getChapters().subscribe(
    (data: IChapter[]) => {
      this.chapters = data;
    },
    (error)=>{ toErrorPage(error, this.router);});
  }

  public toChapter(serialNumber: number){
    this.router.navigate(['/chapter', serialNumber]);
  }
}

import {Component} from '@angular/core';
import {Router} from "@angular/router";
import {IChapter} from "../../../interfaces/IChapter";
import {CollectionOfTasksService} from "../../../services/collection-of-tasks.service";

@Component({
  selector: 'app-chapters',
  templateUrl: './chapters.component.html',
  styleUrls: ['./chapters.component.css']
})
export class ChaptersComponent {
  chapters:IChapter[] = [];
  constructor(
    private collectionOfTasksService: CollectionOfTasksService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.collectionOfTasksService.getChapters().subscribe(
    (data: IChapter[]) => {
      this.chapters = data;
    },
    (error)=>{
      console.log(error);
      this.router.navigate(['/error']);
    });
  }

  public toChapter(serialNumber: number){
    this.router.navigate(['/chapter', serialNumber]);
  }
}

import {Component} from '@angular/core';
import {Router} from "@angular/router";
import {IChapter} from "../../../interfaces/IChapter";
import {toErrorPage} from "../../../utils/ToErrorPageFunc";
import {ChapterService} from "../../../services/chapter.service";
import {IUser} from "../../../interfaces/IUser";
import {AuthorizationService} from "../../../services/authorization.service";
import {UserService} from "../../../services/user.service";

@Component({
  selector: 'app-chapters',
  templateUrl: './chapters.component.html',
  styleUrls: ['./chapters.component.css']
})
export class ChaptersComponent {

  chapters:IChapter[] = [];
  user: IUser | null = null;

  constructor(
    private chapterService: ChapterService,
    private authService: AuthorizationService,
    private router: Router,
    public userService: UserService
  ) {}

  ngOnInit(): void {
    this.chapterService.getChapters().subscribe(
    (data: IChapter[]) => {
      this.chapters = data;
    },
    (error)=>{ toErrorPage(error, this.router);});

    if (this.authService.isLoggedIn()) {
      this.userService.getUser().subscribe((data: IUser) => {
        this.user = data;
      });
    };
  }

  public toChapter(serialNumber: number){
    this.router.navigate(['/chapter', serialNumber]);
  }

  createNewChapter() {
    this.router.navigate(['/newChapter']);
  }
}

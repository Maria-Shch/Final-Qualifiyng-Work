import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {CollectionOfTasksService} from "../../../services/collection-of-tasks.service";

@Component({
  selector: 'app-theory',
  templateUrl: './theory.component.html',
  styleUrls: ['./theory.component.css']
})
export class TheoryComponent implements OnInit{

  serialNumberOfChapter: string = "";
  serialNumberOfBlock: string = "";
  blockName: string = "";
  isBlockLast: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private collectionOfTasksService: CollectionOfTasksService,
    private router: Router
  ) {}
  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      // @ts-ignore
      this.serialNumberOfChapter = this.route.snapshot.paramMap.get("serialNumberOfChapter");
      // @ts-ignore
      this.serialNumberOfBlock = this.route.snapshot.paramMap.get("serialNumberOfBlock");

      this.collectionOfTasksService.getNameOfBlock(this.serialNumberOfChapter, this.serialNumberOfBlock).subscribe(
        (data : string) => {
          this.blockName = data;
      });

      this.collectionOfTasksService.getCountOfBlocks(this.serialNumberOfChapter).subscribe(
        (count: number) => {
          if (this.serialNumberOfBlock === count.toString()) this.isBlockLast = true;
          else this.isBlockLast = false;
        },
        (error)=>{
          console.log(error);
          this.router.navigate(['/error']);
        });
    });
  }

}

import {Component} from '@angular/core';
import {ActivatedRoute} from "@angular/router";

@Component({
  selector: 'app-next-block',
  templateUrl: './next-block.component.html',
  styleUrls: ['./next-block.component.css']
})
export class NextBlockComponent {
  serialNumberOfCurrentChapter: string = "";
  serialNumberOfCurrentBlock: string = "";
  serialNumberOfNextBlock: number | undefined;
  constructor(
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      // @ts-ignore
      this.serialNumberOfCurrentChapter = this.route.snapshot.paramMap.get("serialNumberOfChapter");
      // @ts-ignore
      this.serialNumberOfCurrentBlock = this.route.snapshot.paramMap.get("serialNumberOfBlock");
      this.serialNumberOfNextBlock = parseInt(this.serialNumberOfCurrentBlock) + 1;
    });
  }
}

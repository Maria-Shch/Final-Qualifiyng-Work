import {Component, Input} from '@angular/core';
import {ActivatedRoute} from "@angular/router";

@Component({
  selector: 'app-previous-block',
  templateUrl: './previous-block.component.html',
  styleUrls: ['./previous-block.component.css']
})
export class PreviousBlockComponent {

  @Input()
  section: string = "";
  serialNumberOfCurrentChapter: string = "";
  serialNumberOfCurrentBlock: string = "";
  serialNumberOfPreviousBlock: number | undefined;
  constructor(
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      // @ts-ignore
      this.serialNumberOfCurrentChapter = this.route.snapshot.paramMap.get("serialNumberOfChapter");
      // @ts-ignore
      this.serialNumberOfCurrentBlock = this.route.snapshot.paramMap.get("serialNumberOfBlock");
      this.serialNumberOfPreviousBlock = parseInt(this.serialNumberOfCurrentBlock) - 1;
    });
  }
}

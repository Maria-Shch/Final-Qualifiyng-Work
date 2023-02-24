import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from "@angular/router";

@Component({
  selector: 'app-next',
  templateUrl: './next.component.html',
  styleUrls: ['./next.component.css']
})
export class NextComponent implements OnInit{
  serialNumberOfCurrentChapter: string = "";
  serialNumberOfNextChapter: number | undefined;
  constructor(
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      // @ts-ignore
      this.serialNumberOfCurrentChapter = this.route.snapshot.paramMap.get("serialNumberOfChapter");
      this.serialNumberOfNextChapter = parseInt(this.serialNumberOfCurrentChapter) + 1;
    });
  }
}

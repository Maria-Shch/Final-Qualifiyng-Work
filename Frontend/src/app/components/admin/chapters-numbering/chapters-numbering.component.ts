import {Component, OnInit} from '@angular/core';
import {IChapter} from "../../../interfaces/IChapter";
import {toErrorPage} from "../../../utils/ToErrorPageFunc";
import {ChapterService} from "../../../services/chapter.service";
import {Router} from "@angular/router";
import {IRequestUpdateNumbering} from "../../../dto_interfaces/IRequestUpdateNumbering";
import {INumberingPair} from "../../../dto_interfaces/INumberingPair";

@Component({
  selector: 'app-chapters-numbering',
  templateUrl: './chapters-numbering.component.html',
  styleUrls: ['./chapters-numbering.component.css']
})
export class ChaptersNumberingComponent implements OnInit{

  chapters: IChapter[] = [];
  serialNumbers: number[] = [];
  errorRepeatedSerialNumbers: boolean = false;
  repeatedValue: string | null = null;
  popupInfo: string = '';
  showPopup: boolean = false;

  constructor(
    private chapterService: ChapterService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.chapterService.getChapters().subscribe(
      (data: IChapter[]) => {
        this.chapters = data;
        this.serialNumbers = [];
        for (let i = 1; i <= this.chapters.length; i++) {
          this.serialNumbers.push(i);
        }
      },
      (error)=>{ toErrorPage(error, this.router);});
  }

  getValueOfSelect(selectId: number): string | undefined {
    let e = document.getElementById(selectId.toString()) as HTMLInputElement | null;
    return e?.value;
  }

  checkNumbering(): boolean{
    this.errorRepeatedSerialNumbers = false;
    this.repeatedValue = null;
    let selectedSerialNumbers = [] as number[];
    for (let i = 0; i < this.chapters.length; i++) {
      let e = document.getElementById(this.chapters[i].id.toString()) as HTMLInputElement | null;
      // @ts-ignore
      selectedSerialNumbers.push(e?.value);
    }

    for (let i = 0; i < selectedSerialNumbers.length; i++) {
      for (let j = i+1; j <  selectedSerialNumbers.length; j++) {
        if (selectedSerialNumbers[i] == selectedSerialNumbers [j]) {
          this.errorRepeatedSerialNumbers = true;
          this.repeatedValue = selectedSerialNumbers[j].toString();
          return false;
        }
      }
    }
    return true;
  }

  saveNumbering() {
    if (this.checkNumbering()){
      let request = {numberingPairs: []} as IRequestUpdateNumbering;
      for (let i = 0; i < this.chapters.length; i++) {
        let e = document.getElementById(this.chapters[i].id.toString()) as HTMLInputElement | null;
        let newSerialNumber = e?.value as unknown as number;
        if (this.chapters[i].serialNumber != newSerialNumber){
          let pair = {} as INumberingPair;
          pair.objId = this.chapters[i].id;
          pair.newSerialNumber = newSerialNumber;
          request.numberingPairs.push(pair);
        }
      }
      if (request.numberingPairs.length != 0){
        this.chapterService.updateChaptersNumbering(request).subscribe(
          (data: boolean) => {
            this.popupInfo = 'Нумерация глав сборника успешно обновлена.';
            this.showPopup = true;
            this.ngOnInit();
          },
          (error)=>{ toErrorPage(error, this.router);});
      }
    }
  }

  onChanged($event: boolean) {
    this.showPopup = false;
  }
}

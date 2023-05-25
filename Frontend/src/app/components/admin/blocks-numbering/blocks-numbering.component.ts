import {Component} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {toErrorPage} from "../../../utils/ToErrorPageFunc";
import {IRequestUpdateNumbering} from "../../../dto_interfaces/IRequestUpdateNumbering";
import {INumberingPair} from "../../../dto_interfaces/INumberingPair";
import {IBlock} from "../../../interfaces/IBlock";
import {BlockService} from "../../../services/block.service";

@Component({
  selector: 'app-blocks-numbering',
  templateUrl: './blocks-numbering.component.html',
  styleUrls: ['./blocks-numbering.component.css']
})
export class BlocksNumberingComponent {

  blocks: IBlock[] = [];
  serialNumbers: number[] = [];
  errorRepeatedSerialNumbers: boolean = false;
  repeatedValue: string | null = null;

  constructor(
    private blockService: BlockService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      // @ts-ignore
      this.blockService.getBlocksOfChapter(this.route.snapshot.paramMap.get("chapterId")).subscribe(
        (data: IBlock[]) => {
          this.blocks = data;
          this.serialNumbers = [];
          for (let i = 1; i <= this.blocks.length; i++) {
            this.serialNumbers.push(i);
          }
        },(error: any) => {
          toErrorPage(error, this.router);
        });
    });
  }

  getValueOfSelect(selectId: number): string | undefined {
    let e = document.getElementById(selectId.toString()) as HTMLInputElement | null;
    return e?.value;
  }

  checkNumbering(): boolean{
    this.errorRepeatedSerialNumbers = false;
    this.repeatedValue = null;
    let selectedSerialNumbers = [] as number[];
    for (let i = 0; i < this.blocks.length; i++) {
      let e = document.getElementById(this.blocks[i].id.toString()) as HTMLInputElement | null;
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
      for (let i = 0; i < this.blocks.length; i++) {
        let e = document.getElementById(this.blocks[i].id.toString()) as HTMLInputElement | null;
        let newSerialNumber = e?.value as unknown as number;
        if (this.blocks[i].serialNumber != newSerialNumber){
          let pair = {} as INumberingPair;
          pair.objId = this.blocks[i].id;
          pair.newSerialNumber = newSerialNumber;
          request.numberingPairs.push(pair);
        }
      }
      if (request.numberingPairs.length != 0){
        this.blockService.updateBlocksNumbering(request).subscribe(
          (data: boolean) => {
            alert("Нумерация блоков главы успешно обновлена");
            this.ngOnInit();
          },
          (error: any)=>{ toErrorPage(error, this.router);});
      }
    }
  }
}

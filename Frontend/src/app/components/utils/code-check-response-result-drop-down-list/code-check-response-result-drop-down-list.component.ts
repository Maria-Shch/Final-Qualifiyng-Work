import {Component, Input, OnInit} from '@angular/core';
import {ICodeCheckResponseResult} from "../../../dto_interfaces/ICodeCheckResponseResult";
import {INokRunTestResult} from "../../../dto_interfaces/INokRunTestResult";
import {toArray} from "rxjs";
import {INokAstTestResult} from "../../../dto_interfaces/INokAstTestResult";
import {ISimpleNokAstTestResult} from "../../../dto_interfaces/ISimpleNokAstTestResult";

@Component({
  selector: 'app-code-check-response-result-drop-down-list',
  templateUrl: './code-check-response-result-drop-down-list.component.html',
  styleUrls: ['./code-check-response-result-drop-down-list.component.css']
})
export class CodeCheckResponseResultDropDownListComponent implements OnInit{
  @Input() lastTestingResultForStudent: ICodeCheckResponseResult | null = null;
  panelOpenState = false;
  decodedNokRunTestResult: INokRunTestResult | null = null;
  nokAstTestResults: INokAstTestResult[] = [];
  simpleNokAstTestResult: ISimpleNokAstTestResult | null = null;

  isArray(obj : any) {
    return Array.isArray(obj);
  }

  toArray(obj : any){
    return <Array<any>>obj;
  }

  jsonToDisplay(obj: any){
    return JSON.stringify(obj, null, 4);
  }

  ngOnInit(): void {
    if (this.lastTestingResultForStudent?.code != 'CH-000'){
      // @ts-ignore
      for (let i = 0; i < this.lastTestingResultForStudent?.results.length; i++) {
        if (this.lastTestingResultForStudent?.results[i].status == 'NOK' && this.lastTestingResultForStudent.results[i].type =='RUN'){
          let nokRunTestResult = this.lastTestingResultForStudent.results[i].result as unknown as INokRunTestResult;
          this.decodedNokRunTestResult = nokRunTestResult;
          if (nokRunTestResult.error == null){
            let decodedNokRunTestResult = {} as INokRunTestResult;
            decodedNokRunTestResult.expectedValue = atob(nokRunTestResult.expectedValue);
            decodedNokRunTestResult.actualValue = atob(nokRunTestResult.actualValue);
            this.decodedNokRunTestResult = decodedNokRunTestResult;
            this.decodedNokRunTestResult.actualValueArray = this.decodedNokRunTestResult.actualValue.split(/\n/);
            this.decodedNokRunTestResult.expectedValueArray = this.decodedNokRunTestResult.expectedValue.split(/\n/);
          }
        }

        if (this.lastTestingResultForStudent?.results[i].status == 'NOK' && this.lastTestingResultForStudent.results[i].type =='AST'){
          if (this.isArray(this.lastTestingResultForStudent?.results[i].result)){
            let resultArray = this.toArray(this.lastTestingResultForStudent?.results[i].result);
            for (let j = 0; j < resultArray.length; j++) {
              let nokAstTestResult = resultArray[j] as unknown as INokAstTestResult;
              this.nokAstTestResults.push(nokAstTestResult);
              console.log(this.nokAstTestResults);
            }
          } else {
            this.simpleNokAstTestResult = this.lastTestingResultForStudent?.results[i].result as unknown as ISimpleNokAstTestResult;
          }
        }
      }
    }
  }
}

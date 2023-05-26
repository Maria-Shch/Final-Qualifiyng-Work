import {Component, Input, OnInit} from '@angular/core';
import {ICodeCheckResponseResult} from "../../../dto_interfaces/ICodeCheckResponseResult";
import {INokRunTestResult} from "../../../dto_interfaces/INokRunTestResult";
import {INokAstOrReflexivityOrCheckstyleTestResult} from "../../../dto_interfaces/INokAstOrReflexivityOrCheckstyleTestResult";
import {ISimpleNokAstOrReflexivityTestResult} from "../../../dto_interfaces/ISimpleNokAstOrReflexivityTestResult";
import {Buffer} from 'buffer';

@Component({
  selector: 'app-code-check-response-result-drop-down-list',
  templateUrl: './code-check-response-result-drop-down-list.component.html',
  styleUrls: ['./code-check-response-result-drop-down-list.component.css']
})
export class CodeCheckResponseResultDropDownListComponent implements OnInit{
  @Input() lastTestingResultForStudent: ICodeCheckResponseResult | null = null;
  panelOpenState = false;
  decodedNokRunTestResult: INokRunTestResult | null = null;
  nokAstTestResults: INokAstOrReflexivityOrCheckstyleTestResult[] = [];
  nokReflexivityTestResults: INokAstOrReflexivityOrCheckstyleTestResult[] = [];
  nokCheckstyleTestResults: INokAstOrReflexivityOrCheckstyleTestResult[] = [];
  simpleNokAstTestResult: ISimpleNokAstOrReflexivityTestResult | null = null;
  simpleNokReflexivityTestResult: ISimpleNokAstOrReflexivityTestResult | null = null;

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
        if (this.lastTestingResultForStudent?.results[i].status == 'NOK' && this.lastTestingResultForStudent.results[i].type == 'RUN'){
          let nokRunTestResult = this.lastTestingResultForStudent.results[i].result as unknown as INokRunTestResult;
          this.decodedNokRunTestResult = nokRunTestResult;
          if (nokRunTestResult.error == null){
            let decodedNokRunTestResult = {} as INokRunTestResult;
            decodedNokRunTestResult.expectedValue = Buffer.from(nokRunTestResult.expectedValue, "base64").toString();
            decodedNokRunTestResult.actualValue = Buffer.from(nokRunTestResult.actualValue, "base64").toString();
            this.decodedNokRunTestResult = decodedNokRunTestResult;
            this.decodedNokRunTestResult.actualValueArray = this.decodedNokRunTestResult.actualValue.split(/\n/);
            this.decodedNokRunTestResult.expectedValueArray = this.decodedNokRunTestResult.expectedValue.split(/\n/);
          }
        }

        if (this.lastTestingResultForStudent?.results[i].status == 'NOK' &&
            (this.lastTestingResultForStudent.results[i].type == 'AST' ||
            this.lastTestingResultForStudent.results[i].type == 'REFLEXIVITY' ||
            this.lastTestingResultForStudent.results[i].type == 'CHECKSTYLE')){

          if (this.isArray(this.lastTestingResultForStudent?.results[i].result)){
            let resultArray = this.toArray(this.lastTestingResultForStudent?.results[i].result);
            for (let j = 0; j < resultArray.length; j++) {
              let res = resultArray[j] as unknown as INokAstOrReflexivityOrCheckstyleTestResult;
              if (this.lastTestingResultForStudent.results[i].type == 'AST') {
                this.nokAstTestResults.push(res);
              } else if (this.lastTestingResultForStudent.results[i].type == 'REFLEXIVITY'){
                this.nokReflexivityTestResults.push(res);
              } else if (this.lastTestingResultForStudent.results[i].type == 'CHECKSTYLE'){
                this.nokCheckstyleTestResults.push(res);
              }
            }
          } else {
            if (this.lastTestingResultForStudent.results[i].type == 'AST') {
              this.simpleNokAstTestResult = this.lastTestingResultForStudent?.results[i].result as unknown as ISimpleNokAstOrReflexivityTestResult;
            } else if (this.lastTestingResultForStudent.results[i].type == 'REFLEXIVITY'){
              this.simpleNokReflexivityTestResult = this.lastTestingResultForStudent?.results[i].result as unknown as ISimpleNokAstOrReflexivityTestResult;
            }
          }
        }
      }
    }
  }
}

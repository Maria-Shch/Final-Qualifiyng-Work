import {Component, OnInit} from '@angular/core';
import {ITask} from "../../../interfaces/ITask";
import {toErrorPage} from "../../../utils/ToErrorPageFunc";
import {ActivatedRoute, Router} from "@angular/router";
import {TaskService} from "../../../services/task.service";
import {TestingService} from "../../../services/testing.service";
import {ICodeTextArea} from "../../../dto_interfaces/ICodeTextArea";
import {ITestDefinitionResponseResult} from "../../../dto_interfaces/ITestDefinitionResponseResult";

@Component({
  selector: 'app-task-tests',
  templateUrl: './task-tests.component.html',
  styleUrls: ['./task-tests.component.css']
})
export class TaskTestsComponent implements OnInit{

  serialNumberOfChapter: string = "";
  serialNumberOfBlock: string = "";
  serialNumberOfTask: string = "";
  task: ITask | null = null;
  showModalTestsRules: boolean = false;
  panelOpenState = false
  isPresentActualCode: boolean = false;
  actualCodeTestTextArea: ICodeTextArea = {id: 0, content: ""} as ICodeTextArea;
  lastCodeTestTextArea: ICodeTextArea = {id: 1, content: ""} as ICodeTextArea;
  lastTestDefinitionResponseResult: ITestDefinitionResponseResult | null = null;
  showModalCodeSentSuccessfully: boolean = false;
  showModalCodeSentUnsuccessfully: boolean = false;
  showModalActualTest: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private taskService: TaskService,
    private router: Router,
    private testingService: TestingService,
  ) {}

  ngOnInit(): void {
    this.actualCodeTestTextArea = {id: 0, content: ""};
    this.lastCodeTestTextArea = {id: 1, content: ""};

    this.route.paramMap.subscribe(params => {
      // @ts-ignore
      this.serialNumberOfChapter = this.route.snapshot.paramMap.get("serialNumberOfChapter");
      // @ts-ignore
      this.serialNumberOfBlock = this.route.snapshot.paramMap.get("serialNumberOfBlock");
      // @ts-ignore
      this.serialNumberOfTask = this.route.snapshot.paramMap.get("serialNumberOfTask");

      this.taskService.getTask(this.serialNumberOfChapter, this.serialNumberOfBlock, this.serialNumberOfTask).subscribe(
        (data: ITask) => {
          this.task = data;
          if (document.getElementById('description') != null){
            // @ts-ignore
            document.getElementById('description').innerHTML = this.task.description;
          }

          this.testingService.getActualTestClass(this.task?.id).subscribe(
            (data: string) => {
              if(data == null || data.length == 0){
                this.isPresentActualCode = false;
              } else {
                this.isPresentActualCode = true;
                this.setCodeToTextArea(this.actualCodeTestTextArea, data);
              }
            },
            (error)=>{ toErrorPage(error, this.router);});

          this.testingService.getTestDefinitionResponseResult(this.task?.id).subscribe(
            (data: ITestDefinitionResponseResult) => {
              this.lastTestDefinitionResponseResult = data;
              if (this.lastTestDefinitionResponseResult != null && this.lastTestDefinitionResponseResult.code != 'TD-000'){
                this.lastCodeTestTextArea.content = this.lastTestDefinitionResponseResult.codeTest;
              } else {
                this.lastCodeTestTextArea.content = "";
              }
            },
            (error)=>{ toErrorPage(error, this.router);});
        },
        (error)=>{ toErrorPage(error, this.router);});
    });
  }

  setCodeToTextArea(textArea: ICodeTextArea, code: string){
    textArea.content = code;
  }

  saveCode(): string{
    let codeTest = (<HTMLInputElement>document.getElementById("1"))?.value;
    if (codeTest.length > 0) {
      return codeTest;
    } else {
      return "";
    }
  }

  more(id: number) {
    let el = (<HTMLInputElement>document.getElementById(id.toString()));
    el.setAttribute('style', "height:" + String(el.scrollHeight)+"px");

    let more = (<HTMLInputElement>document.getElementById("more"+id));
    more.setAttribute('style', "display:none");

    let less = (<HTMLInputElement>document.getElementById("less"+id));
    less.setAttribute('style', "display:block");
  }

  less(id: number) {
    let el = (<HTMLInputElement>document.getElementById(id.toString()));
    el.setAttribute('style', "height:150px");

    let more = (<HTMLInputElement>document.getElementById("more"+id));
    more.setAttribute('style', "display:block");

    let less = (<HTMLInputElement>document.getElementById("less"+id));
    less.setAttribute('style', "display:none");
  }

  saveCodeAndSend() {
    let codeTest: string = this.saveCode();
    if (codeTest != ""){
      this.testingService.sendCodeTest(this.task!.id, codeTest).subscribe(
        (data: boolean) => {
          if (data){
            this.showModalCodeSentSuccessfully = true;
            this.ngOnInit();
          } else {
            this.showModalCodeSentUnsuccessfully = true;
          }
        },
        (error)=>{ toErrorPage(error, this.router);});
    }
  }
}

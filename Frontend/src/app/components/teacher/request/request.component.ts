import {Component, OnInit} from '@angular/core';
import {RequestService} from "../../../services/request.service";
import {ActivatedRoute, Router} from "@angular/router";
import {IRequest} from "../../../interfaces/IRequest";
import {toErrorPage} from "../../../utils/ToErrorPageFunc";
import {ICodeTextArea} from "../../../dto_interfaces/ICodeTextArea";
import {TaskService} from "../../../services/task.service";
import {ITestingResultResponse} from "../../../dto_interfaces/ITestingResultResponse";
import {TestingService} from "../../../services/testing.service";

@Component({
  selector: 'app-request',
  templateUrl: './request.component.html',
  styleUrls: ['./request.component.css']
})
export class RequestComponent implements OnInit{
  request: IRequest | null = null;
  requestId: number= 0;
  codeTextAreas: ICodeTextArea[] = [];
  counterCodeTextArea: number = 0;
  codes: string[] = [];
  testingResultResponse: ITestingResultResponse | null = null;
  showModalTestsPassedSuccessfully: boolean = false;
  showModalTestsPassedUnsuccessfully: boolean = false;
  constructor(
    private requestService: RequestService,
    private router:Router,
    private route: ActivatedRoute,
    private taskService: TaskService,
    private testingService: TestingService
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      // @ts-ignore
      this.requestId = this.route.snapshot.paramMap.get("id");

      this.requestService.getRequest(this.requestId).subscribe(
      (data: IRequest) => {
        this.request = data;
        // @ts-ignore
        this.taskService.getClassesByStudentTaskId(this.request?.studentTask.id).subscribe(
          (data: string[]) => {
            if (data == null || data.length === 0){
              this.codeTextAreas = [{id: 0, content: ""}];
              this.counterCodeTextArea = this.counterCodeTextArea + 1;
            } else {
              this.setCodeToTextAreas(data);
              this.codes = data;
            }
          },
          (error)=>{ toErrorPage(error, this.router);});
      },
      (error)=>{ toErrorPage(error, this.router);});
    });
  }

  returnSolution() {
    // @ts-ignore
    this.requestService.rejectSolution(this.request?.id, this.getTeacherMsg()).subscribe(
      (data: IRequest) => {
        this.request = data;
      },
      (error)=>{ toErrorPage(error, this.router);});
  }

  confirmSolution() {
    // @ts-ignore
    this.requestService.acceptSolution(this.request?.id, this.getTeacherMsg()).subscribe(
      (data: IRequest) => {
        this.request = data;
      },
      (error)=>{ toErrorPage(error, this.router);});
  }

  getTeacherMsg(): string{
    return (<HTMLInputElement>document.getElementById("teacherMsg"))?.value;
  }

  setCodeToTextAreas(codes: string[]){
    this.codeTextAreas = [];
    for (let i = 0; i < codes.length; i++) {
      this.codeTextAreas.push({id: i, content: codes[i]});
      this.counterCodeTextArea = this.counterCodeTextArea + 1;
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

  testing() {
    let codes: string[] = this.saveCodeToArray();
    // @ts-ignore
    this.testingService.sendOnTestingForTeacher(this.request?.studentTask.id, codes).subscribe(
      (data: ITestingResultResponse) => {
        this.testingResultResponse = data;

        if (this.testingResultResponse.testingSuccessfulCompleted){
          this.openModalTestsPassedSuccessfully();
        } else {
          this.openModalTestsPassedUnsuccessfully();
        }
      },
      (error: any)=>{ toErrorPage(error, this.router);});
  }

  saveCodeToArray(): string[]{
    let codes: string [] = [];
    for (let i = 0; i <= this.counterCodeTextArea; i++) {
      if (document.getElementById(i.toString()) != null){
        let code = (<HTMLInputElement>document.getElementById(i.toString()))?.value;
        if (code.length > 0) codes.push(code);
      }
    }
    return codes;
  }

  removeTextArea(id: number) {
    this.codeTextAreas = this.codeTextAreas.filter(item => item.id !== id);
  }

  addEmptyTextAreaForCode() {
    this.codeTextAreas.push({id: this.counterCodeTextArea, content: ""});
    this.counterCodeTextArea = this.counterCodeTextArea + 1;
  }

  openModalTestsPassedSuccessfully() {
    this.showModalTestsPassedSuccessfully = true;
  }

  openModalTestsPassedUnsuccessfully() {
    this.showModalTestsPassedUnsuccessfully = true;
  }

  closeModalTestsPassedSuccessfully() {
    this.showModalTestsPassedSuccessfully = false;
  }

  closeModalTestsPassedUnsuccessfully() {
    this.showModalTestsPassedUnsuccessfully = false;
  }
}

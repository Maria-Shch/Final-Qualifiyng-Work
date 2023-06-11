import {Component, OnInit} from '@angular/core';
import {RequestService} from "../../../services/request.service";
import {ActivatedRoute, Router} from "@angular/router";
import {IRequest} from "../../../interfaces/IRequest";
import {toErrorPage} from "../../../utils/ToErrorPageFunc";
import {ICodeTextArea} from "../../../dto_interfaces/ICodeTextArea";
import {TaskService} from "../../../services/task.service";
import {TestingService} from "../../../services/testing.service";
import {ISendingOnTestingResponse} from "../../../dto_interfaces/ISendingOnTestingResponse";
import {ICodeCheckResponseResult} from "../../../dto_interfaces/ICodeCheckResponseResult";

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
  sendingOnTestingResponse: ISendingOnTestingResponse | null = null;
  showModalCodeSentSuccessfully: boolean = false;
  showModalCodeSentUnsuccessfully: boolean = false;
  lastTestingResultForTeacher: ICodeCheckResponseResult | null = null;
  arePresentCodesOfTeacher: boolean = false;
  isStudentCode: boolean = true;

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
        this.requestService.getClassesOfStudentByStudentTaskId(this.request?.studentTask.id).subscribe(
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

        this.requestService.arePresentClassesOfTeacherByStudentTaskId(this.request?.studentTask?.id, this.request?.id).subscribe((data: boolean) => {
            this.arePresentCodesOfTeacher = data;
            if (this.arePresentCodesOfTeacher){
              // @ts-ignore
              this.testingService.getTestingResultForTeacher(this.request?.studentTask?.id, this.request?.id).subscribe((data: ICodeCheckResponseResult) => {
                  this.lastTestingResultForTeacher = data;
                },
                (error)=>{ toErrorPage(error, this.router);});
            }
          },
          (error)=>{ toErrorPage(error, this.router);})
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
    this.testingService.sendOnTestingForTeacher(this.request?.studentTask.id, this.request?.id, codes).subscribe(
      (data: ISendingOnTestingResponse) => {
        this.sendingOnTestingResponse = data;

        if (this.sendingOnTestingResponse.codeSuccessfulSent){
          this.openModalCodeSentSuccessfully();

        } else if(!this.sendingOnTestingResponse.codeSuccessfulSent){
          this.openModalCodeSentUnsuccessfully();
        }
      },
      (error)=>{ toErrorPage(error, this.router);});
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

  showStudentCode() {
    // @ts-ignore
    this.requestService.getClassesOfStudentByStudentTaskId(this.request?.studentTask.id).subscribe(
      (data: string[]) => {
        this.isStudentCode = true;
        if (data == null || data.length === 0){
          this.codeTextAreas = [{id: 0, content: ""}];
          this.counterCodeTextArea = this.counterCodeTextArea + 1;
        } else {
          this.setCodeToTextAreas(data);
          this.codes = data;
        }
      },
      (error)=>{ toErrorPage(error, this.router);});
  }

  showTeacherCode() {
// @ts-ignore
    this.requestService.getClassesOfTeacherByStudentTaskId(this.request?.studentTask.id, this.request?.id).subscribe(
      (data: string[]) => {
        this.isStudentCode = false;
        if (data == null || data.length === 0){
          this.codeTextAreas = [{id: 0, content: ""}];
          this.counterCodeTextArea = this.counterCodeTextArea + 1;
        } else {
          this.setCodeToTextAreas(data);
          this.codes = data;
        }
      },
      (error)=>{ toErrorPage(error, this.router);});
  }

  removeTextArea(id: number) {
    this.codeTextAreas = this.codeTextAreas.filter(item => item.id !== id);
  }

  addEmptyTextAreaForCode() {
    this.codeTextAreas.push({id: this.counterCodeTextArea, content: ""});
    this.counterCodeTextArea = this.counterCodeTextArea + 1;
  }

  openModalCodeSentSuccessfully() {
    this.showModalCodeSentSuccessfully = true;
  }

  openModalCodeSentUnsuccessfully() {
    this.showModalCodeSentUnsuccessfully = true;
  }

  closeModalCodeSentSuccessfully() {
    this.showModalCodeSentSuccessfully = false;
  }

  closeModalCodeSentUnsuccessfully() {
    this.showModalCodeSentUnsuccessfully = false;
  }
}

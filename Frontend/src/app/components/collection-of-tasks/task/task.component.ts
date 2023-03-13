import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {TaskService} from "../../../services/task.service";
import {ITask} from "../../../interfaces/ITask";
import tinymce from "tinymce";
import {Observable} from "rxjs";
import {UserService} from "../../../services/user.service";
import {AuthorizationService} from "../../../services/authorization.service";
import {IStatus} from "../../../interfaces/IStatus";
import {IResponseAboutTestingAllowed} from "../../../dto_interfaces/IResponseAboutTestingAllowed";
import {ICodeTextArea} from "../../../dto_interfaces/ICodeTextArea";
import {ITestingResultResponse} from "../../../dto_interfaces/ITestingResultResponse";
import {ISendingOnReviewOrConsiderationResponse} from "../../../dto_interfaces/ISendingOnReviewOrConsiderationResponse";
import {toErrorPage} from "../../../utils/ToErrorPageFunc";
import {TestingService} from "../../../services/testing.service";

@Component({
  selector: 'app-task',
  templateUrl: './task.component.html',
  styleUrls: ['./task.component.css']
})
export class TaskComponent implements OnInit{
  serialNumberOfChapter: string = "";
  serialNumberOfBlock: string = "";
  serialNumberOfTask: string = "";
  task: ITask | null = null;
  isEditingDescriptionByAdmin: boolean = false;
  status: IStatus | null = null;
  responseAboutTestingAllowed: IResponseAboutTestingAllowed | null  = null;
  codeTextAreas: ICodeTextArea[] = [];
  counterCodeTextArea: number = 0;
  testingResultResponse: ITestingResultResponse | null = null;
  showModalOnReview: boolean = false;
  showModalOnConsideration: boolean = false;
  showModalOnCancelReview: boolean = false;
  showModalOnCancelConsideration: boolean = false;
  showModalTestsPassedSuccessfully: boolean = false;
  showModalTestsPassedUnsuccessfully: boolean = false;

  editorConfig = {
    base_url: '/tinymce',
    suffix: '.min',
    plugins: 'lists link image table wordcount style',
    height: 720,
    toolbar: 'undo redo | styles | fontfamily | fontsize | line_height_formats | forecolor | bold italic | alignleft aligncenter alignright alignjustify | numlist bullist | outdent indent | link image | code',
    init_instance_callback: (editor: { id: any; }) => {
      this.setDescriptionToEditor();
    },
  };

  constructor(
    private route: ActivatedRoute,
    private taskService: TaskService,
    private router: Router,
    public userService: UserService,
    private testingService: TestingService,
    public authService: AuthorizationService
  ) {}

  ngOnInit(): void {
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
      },
      (error)=>{ toErrorPage(error, this.router);});

      if (this.authService.isLoggedIn()) {
        this.taskService.getStatusOfTask(this.serialNumberOfChapter,this.serialNumberOfBlock, this.serialNumberOfTask).subscribe(
        (data: IStatus) => {
          this.status = data;
        },
        (error)=>{ toErrorPage(error, this.router);});

        this.testingService.isTestingAllowed(this.serialNumberOfChapter, this.serialNumberOfBlock, this.serialNumberOfTask).subscribe(
        (data: IResponseAboutTestingAllowed) => {
          this.responseAboutTestingAllowed = data;
        },
        (error)=>{ toErrorPage(error, this.router);});

        this.taskService.getClasses(this.serialNumberOfChapter, this.serialNumberOfBlock, this.serialNumberOfTask).subscribe(
        (data: string[]) => {
          if(data == null || data.length === 0){
            this.codeTextAreas = [{id: 0, content: ""}];
            this.counterCodeTextArea = this.counterCodeTextArea + 1;
          } else {
            this.setCodeToTextAreas(data);
          }
        },
        (error)=>{ toErrorPage(error, this.router);});
      }
    });
  }

  saveNewDescription(){
    this.sendNewDescription().subscribe();
  }

  saveAndTurnOffEditing() {
    this.sendNewDescription().subscribe((data: ITask) => {
      this.task = data;
      this.isEditingDescriptionByAdmin = false;
      this.ngOnInit();
    });
  }

  turnOffEditing() {
    this.isEditingDescriptionByAdmin = false;
    this.ngOnInit();
  }

  setDescriptionToEditor(){
    // @ts-ignore
    tinymce.get('editorTask').setContent(this.task?.description);
  }

  sendNewDescription() : Observable<ITask>{
    let description = tinymce.get('editorTask')?.getContent();
    // @ts-ignore
    return this.taskService.saveDescriptionOfTask(this.serialNumberOfChapter, this.serialNumberOfBlock, this.serialNumberOfTask, description);
  }

  addEmptyTextAreaForCode() {
    this.codeTextAreas.push({id: this.counterCodeTextArea, content: ""});
    this.counterCodeTextArea = this.counterCodeTextArea + 1;
  }

  setCodeToTextAreas(codes: string[]){
    this.codeTextAreas = [];
    for (let i = 0; i < codes.length; i++) {
      this.codeTextAreas.push({id: i, content: codes[i]});
      this.counterCodeTextArea = this.counterCodeTextArea + 1;
    }
  }

  saveCodeAndTesting() {
    let codes: string[] = this.saveCodeToArray();
    this.testingService.sendOnTestingForStudent(this.serialNumberOfChapter, this.serialNumberOfBlock, this.serialNumberOfTask, codes).subscribe(
    (data: ITestingResultResponse) => {
      this.status = data.status;
      this.testingResultResponse = data;

      if (this.testingResultResponse.testingSuccessfulCompleted){
        this.openModalTestsPassedSuccessfully();
      } else if(!this.testingResultResponse.testingSuccessfulCompleted){
        this.openModalTestsPassedUnsuccessfully();
      }
    },
    (error)=>{ toErrorPage(error, this.router);});
  }

  openModalDialogAboutReview() {
    this.showModalOnReview = true;
  }

  saveCodeAndSendOnReview(){
    let codes: string[] = this.saveCodeToArray();
    this.taskService.sendOnReview(this.serialNumberOfChapter, this.serialNumberOfBlock, this.serialNumberOfTask, codes).subscribe(
    (data: ISendingOnReviewOrConsiderationResponse) => {
      if(data.sendingSuccessfulCompleted){
        this.status = data.status;
        alert("Вы отправили решение задачи на проверку.");
      } else {
        alert("Не удалось сохранить код вашего решения. Пожалуйста, повторите попытку");
      }
      this.closeModalOnReview();
      this.ngOnInit();
    },
    (error)=>{ toErrorPage(error, this.router);});
  }

  closeModalOnReview() {
    this.showModalOnReview = false;
  }

  openModalDialogAboutConsideration() {
    this.showModalOnConsideration = true;
  }

  saveCodeAndSendOnConsideration(){
    let codes: string[] = this.saveCodeToArray();
    let message = (<HTMLInputElement>document.getElementById("#messageToConsideration"))?.value;
    this.taskService.sendOnConsideration(this.serialNumberOfChapter, this.serialNumberOfBlock, this.serialNumberOfTask, codes, message).subscribe(
    (data: ISendingOnReviewOrConsiderationResponse) => {
      if(data.sendingSuccessfulCompleted){
        this.status = data.status;
        alert("Вы отправили решение задачи на рассмотрение.");
      } else {
        alert("Не удалось сохранить код вашего решения. Пожалуйста, повторите попытку");
      }
      this.closeModalOnConsideration();
      this.ngOnInit();
    },
    (error)=>{ toErrorPage(error, this.router);});
  }

  closeModalOnConsideration() {
    this.showModalOnConsideration = false;
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

  openModalDialogAboutCancelReview() {
    this.showModalOnCancelReview = true;
  }

  cancelReview(){
    this.taskService.cancelReview(this.serialNumberOfChapter, this.serialNumberOfBlock, this.serialNumberOfTask).subscribe(
    (data: any) => {
      alert("Вы отменили запрос на проверку решения.");
      this.ngOnInit();
    },
    (error)=>{ toErrorPage(error, this.router);});
    this.closeModalOnCancelReview();
  }

  closeModalOnCancelReview() {
    this.showModalOnCancelReview = false;
  }

  openModalDialogAboutCancelConsideration() {
    this.showModalOnCancelConsideration = true;
  }

  cancelConsideration(){
    this.taskService.cancelConsideration(this.serialNumberOfChapter, this.serialNumberOfBlock, this.serialNumberOfTask).subscribe(
    (data: any) => {
      alert("Вы отменили запрос на рассмотрение решения.");
      this.ngOnInit();
    },
    (error)=>{ toErrorPage(error, this.router);});
    this.closeModalOnCancelConsideration();
  }

  closeModalOnCancelConsideration() {
    this.showModalOnCancelConsideration = false;
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


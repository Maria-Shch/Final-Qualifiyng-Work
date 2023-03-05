import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {CollectionOfTasksService} from "../../../services/collection-of-tasks.service";
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
  isEditing: boolean = false;
  status: IStatus | null = null;
  responseAboutTestingAllowed: IResponseAboutTestingAllowed | null  = null;
  codeTextAreas: ICodeTextArea[] = [];
  counterCodeTextArea: number = 0;
  testingResultResponse: ITestingResultResponse | null = null;

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
    private collectionOfTasksService: CollectionOfTasksService,
    private router: Router,
    public userService: UserService,
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

      this.collectionOfTasksService.getTask(this.serialNumberOfChapter, this.serialNumberOfBlock, this.serialNumberOfTask).subscribe(
      (data: ITask) => {
        this.task = data;
        if (document.getElementById('description') != null){
          // @ts-ignore
          document.getElementById('description').innerHTML = this.task.description;
        }
      },
      (error)=>{
        console.log(error);
        this.router.navigate(['/error']);
      });

      if (this.authService.isLoggedIn()) {
        this.collectionOfTasksService.getStatusOfTask(this.serialNumberOfChapter,this.serialNumberOfBlock, this.serialNumberOfTask).subscribe(
        (data: IStatus) => {
          this.status = data;
        },
        (error)=>{
          console.log(error);
          this.router.navigate(['/error']);
        });

        this.collectionOfTasksService.isTestingAllowed(this.serialNumberOfChapter, this.serialNumberOfBlock, this.serialNumberOfTask).subscribe(
        (data: IResponseAboutTestingAllowed) => {
          this.responseAboutTestingAllowed = data;
        },
        (error) => {
          console.log(error);
          this.router.navigate(['/error']);
        });

        this.collectionOfTasksService.getClasses(this.serialNumberOfChapter, this.serialNumberOfBlock, this.serialNumberOfTask).subscribe(
        (data: string[]) => {
          if(data == null || data.length === 0){
            this.codeTextAreas = [{id: 0, content: ""}];
            this.counterCodeTextArea = this.counterCodeTextArea + 1;
          } else {
            this.setCodeToTextAreas(data);
          }
        },
        (error) => {
          console.log(error);
          this.router.navigate(['/error']);
        });
      }
    });
  }

  saveNewDescription(){
    this.sendNewDescription().subscribe();
  }

  saveAndTurnOffEditing() {
    this.sendNewDescription().subscribe((data: ITask) => {
      this.task = data;
      this.isEditing = false;
      this.ngOnInit();
    });
  }

  turnOffEditing() {
    this.isEditing = false;
    this.ngOnInit();
  }

  setDescriptionToEditor(){
    // @ts-ignore
    tinymce.get('editorTask').setContent(this.task?.description);
  }

  sendNewDescription() : Observable<ITask>{
    let description = tinymce.get('editorTask')?.getContent();
    // @ts-ignore
    return this.collectionOfTasksService.saveDescriptionOfTask(this.serialNumberOfChapter, this.serialNumberOfBlock, this.serialNumberOfTask, description);
  }

  addEmptyTextArea() {
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
    this.collectionOfTasksService.sendOnTesting(this.serialNumberOfChapter, this.serialNumberOfBlock, this.serialNumberOfTask, codes).subscribe(
    (data: ITestingResultResponse) => {
      this.status = data.status;
      this.testingResultResponse = data;

      if(this.status?.name === 'Прошла тесты'){
        let modal = document.getElementById("#modalTestsPassedSuccessfully");
        modal?.setAttribute('style', "display:block");
      } else if(this.status?.name === 'Не прошла тесты'){
        let modal = document.getElementById("#modalTestsPassedUnsuccessfully");
        modal?.setAttribute('style', "display:block");
      }
    },
    (error) => {
      console.log(error);
      this.router.navigate(['/error']);
    });
  }

  openModalDialogAboutReview() {
    let modalOnReview = document.getElementById("#modalOnReview");
    console.log(modalOnReview);
    modalOnReview?.setAttribute('style', "display:block");
  }

  saveCodeAndSendOnReview(){
    let codes: string[] = this.saveCodeToArray();
    this.collectionOfTasksService.sendOnReview(this.serialNumberOfChapter, this.serialNumberOfBlock, this.serialNumberOfTask, codes).subscribe(
    (data: ISendingOnReviewOrConsiderationResponse) => {
      if(data.sendingSuccessfulCompleted){
        this.status = data.status;
        alert("Вы отправили решение задачи на проверку.");
        this.closeModalOnReview();
      } else {
        alert("Не удалось сохранить код вашего решения. Пожалуйста, повторите попытку");
        this.closeModalOnReview();
      }
      this.ngOnInit();
    },
    (error) => {
      console.log(error);
      this.router.navigate(['/error']);
    });
  }

  closeModalOnReview() {
    let modalOnReview = (<HTMLInputElement>document.getElementById("#modalOnReview"));
    modalOnReview.setAttribute('style', "display:none");
  }

  openModalDialogAboutConsideration() {
    let modalOnReview = document.getElementById("#modalOnConsideration");
    modalOnReview?.setAttribute('style', "display:block");
  }

  saveCodeAndSendOnConsideration(){
    let codes: string[] = this.saveCodeToArray();
    let message = (<HTMLInputElement>document.getElementById("#messageToConsideration"))?.value;
    this.collectionOfTasksService.sendOnConsideration(this.serialNumberOfChapter, this.serialNumberOfBlock, this.serialNumberOfTask, codes, message).subscribe(
    (data: ISendingOnReviewOrConsiderationResponse) => {
      if(data.sendingSuccessfulCompleted){
        this.status = data.status;
        alert("Вы отправили решение задачи на рассмотрение.");
        this.closeModalOnConsideration();
      } else {
        alert("Не удалось сохранить код вашего решения. Пожалуйста, повторите попытку");
        this.closeModalOnConsideration();
      }
      this.ngOnInit();
    },
    (error) => {
      console.log(error);
      this.router.navigate(['/error']);
    });
  }

  closeModalOnConsideration() {
    let modalOnConsideration = (<HTMLInputElement>document.getElementById("#modalOnConsideration"));
    modalOnConsideration.setAttribute('style', "display:none");
  }

  saveCodeToArray(): string[]{
    let codes: string [] = [];
    for (let i = 0; i <= this.counterCodeTextArea; i++) {
      if(document.getElementById(i.toString()) != null){
        let code = (<HTMLInputElement>document.getElementById(i.toString()))?.value;
        if(code.length > 0) codes.push(code);
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
    let modalOnCancelReview = document.getElementById("#modalOnCancelReview");
    modalOnCancelReview?.setAttribute('style', "display:block");
  }

  cancelReview(){
    this.collectionOfTasksService.cancelReview(this.serialNumberOfChapter, this.serialNumberOfBlock, this.serialNumberOfTask).subscribe(
    (data: any) => {
      alert("Вы отменили запрос на проверку решения.");
      this.ngOnInit();
    },
    (error) => {
      console.log(error);
      this.router.navigate(['/error']);
    });
    this.closeModalOnCancelReview();
  }

  closeModalOnCancelReview() {
    let modalOnCancelReview = (<HTMLInputElement>document.getElementById("#modalOnCancelReview"));
    modalOnCancelReview.setAttribute('style', "display:none");
  }


  openModalDialogAboutCancelConsideration() {
    let modalOnCancelConsideration = document.getElementById("#modalOnCancelConsideration");
    modalOnCancelConsideration?.setAttribute('style', "display:block");
  }

  cancelConsideration(){
    this.collectionOfTasksService.cancelConsideration(this.serialNumberOfChapter, this.serialNumberOfBlock, this.serialNumberOfTask).subscribe(
    (data: any) => {
      alert("Вы отменили запрос на рассмотрение решения.");
      this.ngOnInit();
    },
    (error) => {
      console.log(error);
      this.router.navigate(['/error']);
    });
    this.closeModalOnCancelConsideration();
  }

  closeModalOnCancelConsideration() {
    let modalOnCancelConsideration = (<HTMLInputElement>document.getElementById("#modalOnCancelConsideration"));
    modalOnCancelConsideration.setAttribute('style', "display:none");
  }

  closeModalTestsPassedSuccessfully() {
    let modal = (<HTMLInputElement>document.getElementById("#modalTestsPassedSuccessfully"));
    modal.setAttribute('style', "display:none");
  }

  closeModalTestsPassedUnsuccessfully() {
    let modal = (<HTMLInputElement>document.getElementById("#modalTestsPassedUnsuccessfully"));
    modal.setAttribute('style', "display:none");
  }
}


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
      this.setContent();
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

  save(){
    this.sendContent().subscribe();
  }

  saveAndTurnOffEditing() {
    this.sendContent().subscribe((data: ITask) => {
      this.task = data;
      this.isEditing = false;
      this.ngOnInit();
    });
  }

  turnOffEditing() {
    this.isEditing = false;
    this.ngOnInit();
  }

  setContent(){
    // @ts-ignore
    tinymce.get('editorTask').setContent(this.task?.description);
  }

  sendContent() : Observable<ITask>{
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
    let codes: string [] = [];
    for (let i = 0; i <= this.counterCodeTextArea; i++) {
      if(document.getElementById(i.toString()) != null){
        let code = (<HTMLInputElement>document.getElementById(i.toString()))?.value;
        if(code.length > 0) codes.push(code);
      }
    }
    this.collectionOfTasksService.sendOnTesting(this.serialNumberOfChapter, this.serialNumberOfBlock, this.serialNumberOfTask, codes).subscribe(
      (data: ITestingResultResponse) => {
        this.status = data.status;
        this.testingResultResponse = data;
        console.log(this.testingResultResponse);
        console.log(this.status?.name == 'Не прошла тесты');
      },
      (error) => {
        console.log(error);
        this.router.navigate(['/error']);
      });
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


  saveAndSendToReview() {
    
  }

  toConsideration() {

  }

  cancelReview() {

  }


  cancelConsideration() {

  }
}


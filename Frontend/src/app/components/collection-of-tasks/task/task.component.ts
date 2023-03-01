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
import {ICodeTextArea} from "../../../interfaces/ICodeTextArea";

@Component({
  selector: 'app-task',
  templateUrl: './task.component.html',
  styleUrls: ['./task.component.css']
})
export class TaskComponent implements OnInit{
  serialNumberOfChapter: string = "";
  serialNumberOfBlock: string = "";
  serialNumberOfTask: string = "";
  isTaskLast: boolean = false;
  task: ITask | null = null;
  isEditing: boolean = false;
  status: IStatus | null = null;
  responseAboutTestingAllowed: IResponseAboutTestingAllowed | null = null;
  codeTextAreas: ICodeTextArea[] = [{id: 0}];
  counterCodeTextArea: number = 0;

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

      this.collectionOfTasksService.getCountOfTasks(this.serialNumberOfChapter, this.serialNumberOfBlock).subscribe(
      (count: number) => {
        if (this.serialNumberOfTask === count.toString()) this.isTaskLast = true;
        else this.isTaskLast = false;
      },
      (error)=>{
        console.log(error);
        this.router.navigate(['/error']);
      });

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
      }

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
        this.collectionOfTasksService.isTestingAllowed(this.serialNumberOfChapter, this.serialNumberOfBlock, this.serialNumberOfTask).subscribe(
        (data: IResponseAboutTestingAllowed) => {
          this.responseAboutTestingAllowed = data;
          // @ts-ignore
          console.log(this.responseAboutTestingAllowed!.reasonOfProhibition === 'PREVIOUS_TASK_NOT_SOLVED' );
        },
        (error) => {
          console.log(error);
          this.router.navigate(['/error']);
        });
      }

      this.collectionOfTasksService.getPreviousTask(this.serialNumberOfChapter, this.serialNumberOfBlock, this.serialNumberOfTask).subscribe(
      (data: ITask | null) => {
        // console.log(data?.block.chapter.serialNumber + '.' + data?.block.serialNumber + '.' + data?.serialNumber);
      },
      (error) => {
        console.log(error);
        this.router.navigate(['/error']);
      });
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

  addTextArea() {
    this.counterCodeTextArea = this.counterCodeTextArea + 1;
    this.codeTextAreas.push({id: this.counterCodeTextArea});
  }

  saveCodeAndTesting() {

  }

  removeTextArea(id: number) {
    this.codeTextAreas = this.codeTextAreas.filter(item => item.id !== id);
  }

}

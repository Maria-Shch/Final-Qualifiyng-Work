import {Component, OnInit} from '@angular/core';
import {ICodeTextArea} from "../../../dto_interfaces/ICodeTextArea";
import {toErrorPage} from "../../../utils/ToErrorPageFunc";
import {ActivatedRoute, Router} from "@angular/router";
import {UserService} from "../../../services/user.service";
import {IStudentTask} from "../../../interfaces/IStudentTask";
import {StudentTaskService} from "../../../services/student-task.service";
import {RequestService} from "../../../services/request.service";

@Component({
  selector: 'app-student-solution',
  templateUrl: './student-solution.component.html',
  styleUrls: ['./student-solution.component.css']
})
export class StudentSolutionComponent implements OnInit{
  serialNumberOfChapter: string = "";
  serialNumberOfBlock: string = "";
  serialNumberOfTask: string = "";
  userId: string = "";
  studentTask: IStudentTask | null = null;
  codeTextAreas: ICodeTextArea[] = [];
  counterCodeTextArea: number = 0;

  constructor(
    private router:Router,
    private route: ActivatedRoute,
    private userService: UserService,
    private studentTaskService: StudentTaskService,
    private requestService: RequestService
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      // @ts-ignore
      this.userId = this.route.snapshot.paramMap.get("id");
      // @ts-ignore
      this.serialNumberOfChapter = this.route.snapshot.paramMap.get("serialNumberOfChapter");
      // @ts-ignore
      this.serialNumberOfBlock = this.route.snapshot.paramMap.get("serialNumberOfBlock");
      // @ts-ignore
      this.serialNumberOfTask = this.route.snapshot.paramMap.get("serialNumberOfTask");

      this.studentTaskService.getStudentTask(this.serialNumberOfChapter, this.serialNumberOfBlock,
        this.serialNumberOfTask, this.userId).subscribe(
        (data: IStudentTask | null) => {
          this.studentTask = data;
          if (this.studentTask == null) toErrorPage("Не сущствует пользователя с таким id или не существует такой задачи", this.router);

          if (document.getElementById('description') != null){
            // @ts-ignore
            document.getElementById('description').innerHTML = this.studentTask.task.description;
          }

          // @ts-ignore
          this.requestService.getClassesOfStudentByStudentTaskId(this.studentTask?.id).subscribe(
            (data: string[]) => {
              if (data != null){
                this.setCodeToTextAreas(data);
              } else{
                this.counterCodeTextArea = 0;
              }
            },
            (error)=>{ toErrorPage(error, this.router);});
        },
        (error)=>{ toErrorPage(error, this.router);});
    });
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
}

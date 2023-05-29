import {Component} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {toErrorPage} from "../../../utils/ToErrorPageFunc";
import {IRequestUpdateNumbering} from "../../../dto_interfaces/IRequestUpdateNumbering";
import {INumberingPair} from "../../../dto_interfaces/INumberingPair";
import {ITask} from "../../../interfaces/ITask";
import {TaskService} from "../../../services/task.service";

@Component({
  selector: 'app-tasks-numbering',
  templateUrl: './tasks-numbering.component.html',
  styleUrls: ['./tasks-numbering.component.css']
})
export class TasksNumberingComponent {

  tasks: ITask[] = [];
  serialNumbers: number[] = [];
  errorRepeatedSerialNumbers: boolean = false;
  repeatedValue: string | null = null;
  popupInfo: string = '';
  showPopup: boolean = false;

  constructor(
    private taskService: TaskService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      // @ts-ignore
      this.taskService.getTasks(this.route.snapshot.paramMap.get("chapterId"), this.route.snapshot.paramMap.get("blockId")).subscribe(
        (data: ITask[]) => {
          this.tasks = data;
          this.serialNumbers = [];
          for (let i = 1; i <= this.tasks.length; i++) {
            this.serialNumbers.push(i);
          }
        },(error: any) => {
          toErrorPage(error, this.router);
        });
    });
  }

  getValueOfSelect(selectId: number): string | undefined {
    let e = document.getElementById(selectId.toString()) as HTMLInputElement | null;
    return e?.value;
  }

  checkNumbering(): boolean{
    this.errorRepeatedSerialNumbers = false;
    this.repeatedValue = null;
    let selectedSerialNumbers = [] as number[];
    for (let i = 0; i < this.tasks.length; i++) {
      let e = document.getElementById(this.tasks[i].id.toString()) as HTMLInputElement | null;
      // @ts-ignore
      selectedSerialNumbers.push(e?.value);
    }

    for (let i = 0; i < selectedSerialNumbers.length; i++) {
      for (let j = i+1; j <  selectedSerialNumbers.length; j++) {
        if (selectedSerialNumbers[i] == selectedSerialNumbers [j]) {
          this.errorRepeatedSerialNumbers = true;
          this.repeatedValue = selectedSerialNumbers[j].toString();
          return false;
        }
      }
    }
    return true;
  }

  saveNumbering() {
    if (this.checkNumbering()){
      let request = {numberingPairs: []} as IRequestUpdateNumbering;
      for (let i = 0; i < this.tasks.length; i++) {
        let e = document.getElementById(this.tasks[i].id.toString()) as HTMLInputElement | null;
        let newSerialNumber = e?.value as unknown as number;
        if (this.tasks[i].serialNumber != newSerialNumber){
          let pair = {} as INumberingPair;
          pair.objId = this.tasks[i].id;
          pair.newSerialNumber = newSerialNumber;
          request.numberingPairs.push(pair);
        }
      }
      if (request.numberingPairs.length != 0){
        this.taskService.updateTasksNumbering(request).subscribe(
          (data: boolean) => {
            this.popupInfo = "Нумерация задач блока успешно обновлена.";
            this.showPopup = true;
            this.ngOnInit();
          },
          (error: any)=>{ toErrorPage(error, this.router);});
      }
    }
  }

  onChanged($event: boolean) {
    this.showPopup = false;
  }
}

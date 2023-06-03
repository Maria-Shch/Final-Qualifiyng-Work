import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {environment} from "../environments/enviroment";
import {AuthorizationService} from "./authorization.service";
import {ITaskOfBlock} from "../dto_interfaces/ITaskOfBlock";
import {ITask} from "../interfaces/ITask";
import {IStatus} from "../interfaces/IStatus";
import {ISendingOnReviewOrConsiderationResponse} from "../dto_interfaces/ISendingOnReviewOrConsiderationResponse";
import {ISimpleCollection} from "../dto_interfaces/ISimpleCollection";
import {IBlock} from "../interfaces/IBlock";
import {IRequestUpdateNumbering} from "../dto_interfaces/IRequestUpdateNumbering";

@Injectable({
  providedIn: 'root'
})
export class TaskService {
  constructor(
    private httpclient: HttpClient,
    private authService: AuthorizationService
  ) {}

  getPractice(serialNumberOfChapter: string, serialNumberOfBlock: string): Observable<ITaskOfBlock[]> {
    if (this.authService.isLoggedIn()) {
      return this.httpclient.get<ITaskOfBlock[]>(environment.apiUrl +
        `/auth/chapter/${serialNumberOfChapter}/block/${serialNumberOfBlock}/practice`);
    } else {
      return this.httpclient.get<ITaskOfBlock[]>(environment.apiUrl +
        `/chapter/${serialNumberOfChapter}/block/${serialNumberOfBlock}/practice`, {
        headers: new HttpHeaders({ 'No-Auth': 'True' })
      });
    }
  }

  getTask(serialNumberOfChapter: string, serialNumberOfBlock: string, serialNumberOfTask: string) : Observable<ITask> {
    return this.httpclient.get<ITask>(environment.apiUrl +
      `/chapter/${serialNumberOfChapter}/block/${serialNumberOfBlock}/task/${serialNumberOfTask}`, {
      headers: new HttpHeaders({ 'No-Auth': 'True' })
    });
  }

  saveDescriptionOfTask(serialNumberOfChapter: string, serialNumberOfBlock: string, serialNumberOfTask: string, description: string)  : Observable<ITask> {
    return this.httpclient.post<ITask>(environment.apiUrl +
      `/auth/chapter/${serialNumberOfChapter}/block/${serialNumberOfBlock}/task/${serialNumberOfTask}/saveDescription`,
      description);
  }

  getStatusOfTask(serialNumberOfChapter: string, serialNumberOfBlock: string, serialNumberOfTask: string): Observable<IStatus> {
    return this.httpclient.get<IStatus>(environment.apiUrl +
      `/auth/chapter/${serialNumberOfChapter}/block/${serialNumberOfBlock}/task/${serialNumberOfTask}/status`);
  }

  getPreviousTask(serialNumberOfChapter: string, serialNumberOfBlock: string, serialNumberOfTask: string): Observable<ITask | null> {
    return this.httpclient.get<ITask | null>(environment.apiUrl +
      `/chapter/${serialNumberOfChapter}/block/${serialNumberOfBlock}/task/${serialNumberOfTask}/previousTask`, {
      headers: new HttpHeaders({ 'No-Auth': 'True' })
    });
  }

  getNextTask(serialNumberOfChapter: string, serialNumberOfBlock: string, serialNumberOfTask: string): Observable<ITask | null> {
    return this.httpclient.get<ITask | null>(environment.apiUrl +
      `/chapter/${serialNumberOfChapter}/block/${serialNumberOfBlock}/task/${serialNumberOfTask}/nextTask`, {
      headers: new HttpHeaders({ 'No-Auth': 'True' })
    });
  }

  getClasses(serialNumberOfChapter: string, serialNumberOfBlock: string, serialNumberOfTask: string): Observable<string[]> {
    return this.httpclient.get<string[]>(environment.apiUrl +
      `/auth/chapter/${serialNumberOfChapter}/block/${serialNumberOfBlock}/task/${serialNumberOfTask}/getClasses`);
  }

  sendOnReview(serialNumberOfChapter: string, serialNumberOfBlock: string, serialNumberOfTask: string, codes: string[]):
    Observable<ISendingOnReviewOrConsiderationResponse> {
    return this.httpclient.post<ISendingOnReviewOrConsiderationResponse>(environment.apiUrl +
      `/auth/chapter/${serialNumberOfChapter}/block/${serialNumberOfBlock}/task/${serialNumberOfTask}/onReview`,
      codes);
  }

  sendOnConsideration(serialNumberOfChapter: string, serialNumberOfBlock: string, serialNumberOfTask: string,
                      codes: string[], message: string): Observable<ISendingOnReviewOrConsiderationResponse> {
    let rbOnConsideration = {codes, message};
    return this.httpclient.post<ISendingOnReviewOrConsiderationResponse>(environment.apiUrl +
      `/auth/chapter/${serialNumberOfChapter}/block/${serialNumberOfBlock}/task/${serialNumberOfTask}/onConsideration`,
      rbOnConsideration);
  }

  cancelReview(serialNumberOfChapter: string, serialNumberOfBlock: string, serialNumberOfTask: string): Observable<any> {
    return this.httpclient.get<any>(environment.apiUrl +
      `/auth/chapter/${serialNumberOfChapter}/block/${serialNumberOfBlock}/task/${serialNumberOfTask}/cancelReview`);
  }

  cancelConsideration(serialNumberOfChapter: string, serialNumberOfBlock: string, serialNumberOfTask: string): Observable<any> {
    return this.httpclient.get<any>(environment.apiUrl +
      `/auth/chapter/${serialNumberOfChapter}/block/${serialNumberOfBlock}/task/${serialNumberOfTask}/cancelConsideration`);
  }

  setManualCheckValue(taskId: number, manualCheckValue: boolean): Observable<any>{
    return this.httpclient.put<any>(environment.apiUrl +
      `/task/${taskId}/manualCheck/${manualCheckValue}`, null);
  }

  getCountOfTasks(serialNumberOfChapter: string, serialNumberOfBlock: string): Observable<number> {
    return this.httpclient.get<number>(environment.apiUrl +
      `/chapter/${serialNumberOfChapter}/block/${serialNumberOfBlock}/tasks/count`);
  }

  checkIsPresentNameOfTask(newTask: ITask): Observable<boolean> {
    return this.httpclient.post<boolean>(environment.apiUrl + `/check/task`, newTask);
  }

  createNewTask(task: ITask, selectedPreviousTaskIds: number[]): Observable<ITask> {
    let newTask = {
      task : task,
      selectedPreviousTaskIds : selectedPreviousTaskIds
    }
    return this.httpclient.post<ITask>(environment.apiUrl + '/create/task', newTask);
  }

  getSimpleTaskCollection(): Observable<ISimpleCollection> {
    return this.httpclient.get<ISimpleCollection>(environment.apiUrl + `/simpleTaskCollection`);
  }

  getTaskById(taskId: number): Observable<ITask> {
    return this.httpclient.get<ITask>(environment.apiUrl + `/task/${taskId}`);
  }

  updateTask(updatedTask: ITask): Observable<ITask>  {
    return this.httpclient.post<ITask>(environment.apiUrl + `/update/task`, updatedTask);
  }

  getTasks(serialNumberOfChapter: string, serialNumberOfBlock: string): Observable<ITask[]> {
    return this.httpclient.get<ITask[]>(environment.apiUrl +
      `/chapter/${serialNumberOfChapter}/block/${serialNumberOfBlock}/tasks`);
  }

  updateTasksNumbering(request: IRequestUpdateNumbering): Observable<boolean> {
    return this.httpclient.post<boolean>(environment.apiUrl + `/update/tasks/numbering`, request);
  }
}

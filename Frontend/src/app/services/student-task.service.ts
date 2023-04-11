import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {environment} from "../environments/enviroment";
import {IStudentTask} from "../interfaces/IStudentTask";
import {IStudentProgress} from "../dto_interfaces/IStudentProgress";

@Injectable({
  providedIn: 'root'
})
export class StudentTaskService {
  constructor(
    private httpclient: HttpClient
  ) {}

  getStudentTask(serialNumberOfChapter: string, serialNumberOfBlock: string, serialNumberOfTask: string, userId: string): Observable<IStudentTask | null> {
    return this.httpclient.get<IStudentTask | null>(environment.apiUrl + `/chapter/${serialNumberOfChapter}/block/${serialNumberOfBlock}/task/${serialNumberOfTask}/student/${userId}`);
  }

  getStudentProgress(userId: number): Observable<IStudentProgress> {
    return this.httpclient.get<IStudentProgress>(environment.apiUrl + `/student/${userId}/progress`);
  }
}

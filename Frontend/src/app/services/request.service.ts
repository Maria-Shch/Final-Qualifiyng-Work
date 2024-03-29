import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {environment} from "../environments/enviroment";
import {IRequest} from "../interfaces/IRequest";
import {IRequestType} from "../interfaces/IRequestType";
import {IRequestState} from "../interfaces/IRequestState";
import {IFilterRequests} from "../dto_interfaces/IFilterRequests";
import {IEventHistory} from "../interfaces/IEventHistory";

@Injectable({
  providedIn: 'root'
})
export class RequestService {

  constructor(
    private httpclient: HttpClient
  ) {}

  public getCountOfRequestsByTeacher() : Observable<number> {
    return this.httpclient.get<number>(environment.apiUrl + '/request/count');
  }

  public getRequestTypes() : Observable<IRequestType[]> {
    return this.httpclient.get<IRequestType[]>(environment.apiUrl + `/request/types`);
  }

  public getRequestStates() : Observable<IRequestState[]> {
    return this.httpclient.get<IRequestState[]>(environment.apiUrl + `/request/states`);
  }

  public getCountRequestsAfterFiltering(filter: IFilterRequests) : Observable<number> {
    return this.httpclient.post<number>(environment.apiUrl + `/request/count/filter`, filter);
  }

  public getRequests(pageNumber: number, filter: IFilterRequests | null) : Observable<IRequest[]> {
    return this.httpclient.post<IRequest[]>(environment.apiUrl + `/request/get/${pageNumber}`, filter);
  }
  public getRequest(id: number) : Observable<IRequest> {
    return this.httpclient.get<IRequest>(environment.apiUrl + `/request/${id}`);
  }

  public rejectSolution(requestId: number, teacherMsg: string) : Observable<IRequest>{
    return this.httpclient.post<IRequest>(environment.apiUrl + `/request/reject/${requestId}`, teacherMsg);
  }

  public acceptSolution(requestId: number, teacherMsg: string) : Observable<IRequest>{
    return this.httpclient.post<IRequest>(environment.apiUrl + `/request/accept/${requestId}`, teacherMsg);
  }

  public getHistoryOfRequests(pageNumber: number) : Observable<IEventHistory[]>{
    return this.httpclient.get<IEventHistory[]>(environment.apiUrl + `/request/getHistory/${pageNumber}`);
  }

  getClassesOfStudentByStudentTaskId(studentTaskId: number): Observable<string[]> {
    return this.httpclient.get<string[]>(environment.apiUrl + `/request/getClassesOfStudent/${studentTaskId}`);
  }

  getClassesOfTeacherByStudentTaskId(studentTaskId: number, requestId: number): Observable<string[]> {
    return this.httpclient.get<string[]>(environment.apiUrl + `/request/getClassesOfTeacher/${studentTaskId}/${requestId}`);
  }

  arePresentClassesOfTeacherByStudentTaskId(studentTaskId: number, requestId: number): Observable<boolean> {
    return this.httpclient.get<boolean>(environment.apiUrl + `/request/arePresentClassesOfTeacher/${studentTaskId}/${requestId}`);
  }
}

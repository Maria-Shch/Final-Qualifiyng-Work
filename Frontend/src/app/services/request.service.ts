import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {environment} from "../environments/enviroment";
import {IRequest} from "../interfaces/IRequest";

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

  public getRequestsByPageNumber(pageNumber: number) : Observable<IRequest[]> {
    return this.httpclient.get<IRequest[]>(environment.apiUrl + `/request/get/${pageNumber}`);
  }
}

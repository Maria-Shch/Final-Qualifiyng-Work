import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {IResponseAboutTestingAllowed} from "../dto_interfaces/IResponseAboutTestingAllowed";
import {environment} from "../environments/enviroment";
import {ISendingOnTestingResponse} from "../dto_interfaces/ISendingOnTestingResponse";
import {ICodeCheckResponseResult} from "../dto_interfaces/ICodeCheckResponseResult";

@Injectable({
  providedIn: 'root'
})
export class TestingService {

  constructor(
    private httpclient: HttpClient
  ) {}

  isTestingAllowed(serialNumberOfChapter: string, serialNumberOfBlock: string, serialNumberOfTask: string):
    Observable<IResponseAboutTestingAllowed> {
    return this.httpclient.get<IResponseAboutTestingAllowed>(environment.apiUrl +
      `/auth/chapter/${serialNumberOfChapter}/block/${serialNumberOfBlock}/task/${serialNumberOfTask}/isTestingAllowed`);
  }

  sendOnTestingForStudent(serialNumberOfChapter: string, serialNumberOfBlock: string, serialNumberOfTask: string,
                          codes: string[]): Observable<ISendingOnTestingResponse> {
    return this.httpclient.post<ISendingOnTestingResponse>(environment.apiUrl +
      `/auth/chapter/${serialNumberOfChapter}/block/${serialNumberOfBlock}/task/${serialNumberOfTask}/onTestingS`,
      codes);
  }

  getTestingResultForStudent(serialNumberOfChapter: string, serialNumberOfBlock: string, serialNumberOfTask: string): Observable<ICodeCheckResponseResult> {
    return this.httpclient.get<ICodeCheckResponseResult>(environment.apiUrl +
      `/auth/chapter/${serialNumberOfChapter}/block/${serialNumberOfBlock}/task/${serialNumberOfTask}/getTestingResultS`);
  }

  sendOnTestingForTeacher(studentTaskId: number, codes: string[]): Observable<ISendingOnTestingResponse>  {
    return this.httpclient.post<ISendingOnTestingResponse>(environment.apiUrl +
      `/${studentTaskId}/onTestingT`, codes);
  }

  getTestingResultForTeacher(studentTaskId: number): Observable<ICodeCheckResponseResult> {
    return this.httpclient.get<ICodeCheckResponseResult>(environment.apiUrl + `/auth/${studentTaskId}/getTestingResultT`);
  }
}

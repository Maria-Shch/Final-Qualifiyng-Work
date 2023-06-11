import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {IResponseAboutTestingAllowed} from "../dto_interfaces/IResponseAboutTestingAllowed";
import {environment} from "../environments/enviroment";
import {ISendingOnTestingResponse} from "../dto_interfaces/ISendingOnTestingResponse";
import {ICodeCheckResponseResult} from "../dto_interfaces/ICodeCheckResponseResult";
import {ITestDefinitionResponseResult} from "../dto_interfaces/ITestDefinitionResponseResult";

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

  sendOnTestingForTeacher(studentTaskId: number, requestId: number,  codes: string[]): Observable<ISendingOnTestingResponse>  {
    return this.httpclient.post<ISendingOnTestingResponse>(environment.apiUrl +
      `/${studentTaskId}/${requestId}/onTestingT`, codes);
  }

  getTestingResultForTeacher(studentTaskId: number): Observable<ICodeCheckResponseResult> {
    return this.httpclient.get<ICodeCheckResponseResult>(environment.apiUrl + `/${studentTaskId}/getTestingResultT`);
  }

  getActualTestClass(taskId: number): Observable<string> {
    return this.httpclient.get(environment.apiCodetesterUrl + `/get/testClass/${taskId}`, {
      headers: new HttpHeaders({ 'No-Auth': 'True' }),
      responseType: 'text'
    });
  }

  sendCodeTest(taskId: number, codeTest: string): Observable<boolean> {
    return this.httpclient.post<boolean>(environment.apiUrl + `/saveCodeTest/${taskId}`, codeTest);
  }

  getTestDefinitionResponseResult(taskId: number): Observable<ITestDefinitionResponseResult> {
    return this.httpclient.get<ITestDefinitionResponseResult>(environment.apiUrl + `/saveCodeTest/${taskId}/result`);
  }
}

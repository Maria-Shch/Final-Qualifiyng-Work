import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {IResponseAboutTestingAllowed} from "../dto_interfaces/IResponseAboutTestingAllowed";
import {environment} from "../environments/enviroment";
import {ITestingResultResponse} from "../dto_interfaces/ITestingResultResponse";

@Injectable({
  providedIn: 'root'
})
export class TestingService {
  constructor(
    private httpclient: HttpClient
  ) {}
  isTestingAllowed(serialNumberOfChapter: string, serialNumberOfBlock: string, serialNumberOfTask: string): Observable<IResponseAboutTestingAllowed> {
    return this.httpclient.get<IResponseAboutTestingAllowed>(environment.apiUrl +
      `/auth/chapter/${serialNumberOfChapter}/block/${serialNumberOfBlock}/task/${serialNumberOfTask}/isTestingAllowed`);
  }

  sendOnTesting(serialNumberOfChapter: string, serialNumberOfBlock: string, serialNumberOfTask: string, codes: string[]): Observable<ITestingResultResponse> {
    return this.httpclient.post<ITestingResultResponse>(environment.apiUrl +
      `/auth/chapter/${serialNumberOfChapter}/block/${serialNumberOfBlock}/task/${serialNumberOfTask}/onTesting`,
      codes);
  }
}

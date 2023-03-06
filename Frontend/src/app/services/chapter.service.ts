import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {IChapter} from "../interfaces/IChapter";
import {environment} from "../environments/enviroment";

@Injectable({
  providedIn: 'root'
})
export class ChapterService {

  constructor(
    private httpclient: HttpClient
  ) {}

  getChapters(): Observable<IChapter[]> {
    return this.httpclient.get<IChapter[]>(environment.apiUrl + '/chapter/all', {
      headers: new HttpHeaders({ 'No-Auth': 'True' })
    });
  }

  getCountOfChapters(): Observable<number> {
    return this.httpclient.get<number>(environment.apiUrl + '/chapters/count', {
      headers: new HttpHeaders({ 'No-Auth': 'True' })
    });
  }
}

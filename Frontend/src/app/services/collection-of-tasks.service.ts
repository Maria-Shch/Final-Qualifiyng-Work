import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {environment} from "../environments/enviroment";
import {IChapter} from "../interfaces/IChapter";
import {IBlock} from "../interfaces/IBlock";
@Injectable({
  providedIn: 'root'
})
export class CollectionOfTasksService {
  constructor(private httpclient: HttpClient) { }
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

  getBlocksOfChapter(serialNumberOfChapter: string | null): Observable<IBlock[]> {
    return this.httpclient.get<IBlock[]>(environment.apiUrl + `/chapter/${serialNumberOfChapter}/blocks`, {
      headers: new HttpHeaders({ 'No-Auth': 'True' })
    });
  }
}

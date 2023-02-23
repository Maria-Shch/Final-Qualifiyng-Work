import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {environment} from "../environments/enviroment";
import {IChapter} from "../interfaces/IChapter";
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
}

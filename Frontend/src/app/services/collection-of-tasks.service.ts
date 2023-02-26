import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {environment} from "../environments/enviroment";
import {IChapter} from "../interfaces/IChapter";
import {IBlock} from "../interfaces/IBlock";
import {AuthorizationService} from "./authorization.service";
import {ITaskOfBlock} from "../dto_interfaces/ITaskOfBlock";

@Injectable({
  providedIn: 'root'
})
export class CollectionOfTasksService {
  constructor(
    private httpclient: HttpClient,
    private authService: AuthorizationService
  ) { }

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

  getBlocksOfChapter(serialNumberOfChapter: string): Observable<IBlock[]> {
    return this.httpclient.get<IBlock[]>(environment.apiUrl + `/chapter/${serialNumberOfChapter}/blocks`, {
      headers: new HttpHeaders({ 'No-Auth': 'True' })
    });
  }

  getPractice(serialNumberOfChapter: string, serialNumberOfBlock: string): Observable<ITaskOfBlock[]> {
    if (this.authService.isLoggedIn()) {
      return this.httpclient.get<ITaskOfBlock[]>(environment.apiUrl + `/auth/chapter/${serialNumberOfChapter}/block/${serialNumberOfBlock}/practice`);
    } else {
      return this.httpclient.get<ITaskOfBlock[]>(environment.apiUrl + `/chapter/${serialNumberOfChapter}/block/${serialNumberOfBlock}/practice`, {
        headers: new HttpHeaders({ 'No-Auth': 'True' })
      });
    }
  }

  getNameOfBlock(serialNumberOfChapter: string, serialNumberOfBlock: string): Observable<string> {
    return this.httpclient.get(environment.apiUrl + `/chapter/${serialNumberOfChapter}/block/${serialNumberOfBlock}/name`, {
      headers: new HttpHeaders({ 'No-Auth': 'True' }),
      responseType: 'text'
    });
  }

  getCountOfBlocks(serialNumberOfChapter: string): Observable<number> {
    return this.httpclient.get<number>(environment.apiUrl + `/chapters/${serialNumberOfChapter}/blocks/count`, {
      headers: new HttpHeaders({ 'No-Auth': 'True' })
    });
  }

  getBlock(serialNumberOfChapter: string, serialNumberOfBlock: string): Observable<IBlock> {
    return this.httpclient.get<IBlock>(environment.apiUrl + `/chapter/${serialNumberOfChapter}/block/${serialNumberOfBlock}`, {
      headers: new HttpHeaders({ 'No-Auth': 'True' })
    });
  }

  saveTheoryOfBlock(serialNumberOfChapter: string, serialNumberOfBlock: string, textOfTheory: string) : Observable<IBlock> {
    return this.httpclient.post<IBlock>(environment.apiUrl + `/auth/chapter/${serialNumberOfChapter}/block/${serialNumberOfBlock}/saveTheory`,
      textOfTheory);
  }

}

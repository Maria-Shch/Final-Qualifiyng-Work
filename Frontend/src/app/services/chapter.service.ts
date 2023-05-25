import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {IChapter} from "../interfaces/IChapter";
import {environment} from "../environments/enviroment";
import {IRequestUpdateNumbering} from "../dto_interfaces/IRequestUpdateNumbering";

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

  createNewChapter(newChapter: IChapter): Observable<IChapter> {
    return this.httpclient.post<IChapter>(environment.apiUrl + '/create/chapter', newChapter);
  }

  checkIsPresentNameOfChapter(newChapter: IChapter): Observable<boolean> {
    return this.httpclient.post<boolean>(environment.apiUrl + `/check/chapter`, newChapter);
  }

  getChapterById(chapterId: number): Observable<IChapter> {
    return this.httpclient.get<IChapter>(environment.apiUrl + `/chapter/${chapterId}`);
  }

  updateChapter(updatedChapter: IChapter): Observable<IChapter>  {
    return this.httpclient.post<IChapter>(environment.apiUrl + `/update/chapter`, updatedChapter);
  }

  updateChaptersNumbering(request: IRequestUpdateNumbering): Observable<boolean>  {
    return this.httpclient.post<boolean>(environment.apiUrl + `/update/chapters/numbering`, request);
  }
}

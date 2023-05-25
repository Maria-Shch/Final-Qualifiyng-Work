import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {IBlock} from "../interfaces/IBlock";
import {environment} from "../environments/enviroment";

@Injectable({
  providedIn: 'root'
})
export class BlockService {
  constructor(
    private httpclient: HttpClient
  ) {}

  getBlocksOfChapter(serialNumberOfChapter: string): Observable<IBlock[]> {
    return this.httpclient.get<IBlock[]>(environment.apiUrl + `/chapter/${serialNumberOfChapter}/blocks`, {
      headers: new HttpHeaders({ 'No-Auth': 'True' })
    });
  }

  getNameOfBlock(serialNumberOfChapter: string, serialNumberOfBlock: string): Observable<string> {
    return this.httpclient.get(environment.apiUrl +
      `/chapter/${serialNumberOfChapter}/block/${serialNumberOfBlock}/name`, {
      headers: new HttpHeaders({ 'No-Auth': 'True' }),
      responseType: 'text'
    });
  }

  getCountOfBlocks(serialNumberOfChapter: string): Observable<number> {
    return this.httpclient.get<number>(environment.apiUrl +
      `/chapters/${serialNumberOfChapter}/blocks/count`, {
      headers: new HttpHeaders({ 'No-Auth': 'True' })
    });
  }

  getBlock(serialNumberOfChapter: string, serialNumberOfBlock: string): Observable<IBlock> {
    return this.httpclient.get<IBlock>(environment.apiUrl +
      `/chapter/${serialNumberOfChapter}/block/${serialNumberOfBlock}`, {
      headers: new HttpHeaders({ 'No-Auth': 'True' })
    });
  }

  saveTheoryOfBlock(serialNumberOfChapter: string, serialNumberOfBlock: string, textOfTheory: string) : Observable<IBlock> {
    return this.httpclient.post<IBlock>(environment.apiUrl +
      `/auth/chapter/${serialNumberOfChapter}/block/${serialNumberOfBlock}/saveTheory`, textOfTheory);
  }

  getPreviousBlock(serialNumberOfChapter: string, serialNumberOfBlock: string): Observable<IBlock | null> {
    return this.httpclient.get<IBlock | null>(environment.apiUrl +
      `/chapter/${serialNumberOfChapter}/block/${serialNumberOfBlock}/previousBlock`, {
      headers: new HttpHeaders({ 'No-Auth': 'True' })
    });
  }

  getNextBlock(serialNumberOfChapter: string, serialNumberOfBlock: string): Observable<IBlock | null> {
    return this.httpclient.get<IBlock | null>(environment.apiUrl +
      `/chapter/${serialNumberOfChapter}/block/${serialNumberOfBlock}/nextBlock`, {
      headers: new HttpHeaders({ 'No-Auth': 'True' })
    });
  }

  createNewBlock(newBlock: IBlock): Observable<IBlock> {
    return this.httpclient.post<IBlock>(environment.apiUrl + '/create/block', newBlock);
  }

  checkIsPresentNameOrSerialNumberOfBlock(newBlock: IBlock): Observable<boolean> {
    return this.httpclient.post<boolean>(environment.apiUrl +
      `/check/block`, newBlock);
  }
}

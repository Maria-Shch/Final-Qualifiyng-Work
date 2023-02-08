import { Injectable } from '@angular/core';
import {Observable} from "rxjs";
import {IGroup} from "../interfaces/IGroup";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {environment} from "../environments/enviroment";
@Injectable({
  providedIn: 'root'
})
export class GroupService {
  constructor(private httpclient: HttpClient) { }
  getGroups(): Observable<IGroup[]> {
    return this.httpclient.get<IGroup[]>(environment.apiUrl + '/group/all', {
      headers: new HttpHeaders({ 'No-Auth': 'True' })
    });
  }
  setParamsToNullExcludeId(group: IGroup): IGroup{
    group.courseNumber = null;
    group.faculty = null;
    group.formOfEdu = null;
    group.groupNumber = null;
    group.levelOfEdu = null;
    group.name = null;
    group.profile = null;
    group.teacher = null;
    group.year = null;
    return group;
  }
}

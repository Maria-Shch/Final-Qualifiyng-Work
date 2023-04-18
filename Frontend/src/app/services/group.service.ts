import { Injectable } from '@angular/core';
import {Observable} from "rxjs";
import {IGroup} from "../interfaces/IGroup";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {environment} from "../environments/enviroment";
import {IGroupWithUsersStatInfo} from "../dto_interfaces/IGroupWithUsersStatInfo";
import {IRequestType} from "../interfaces/IRequestType";
import {IYear} from "../interfaces/IYear";
import {IFaculty} from "../interfaces/IFaculty";
import {IUser} from "../interfaces/IUser";
import {IUserStatInfo} from "../dto_interfaces/IUserStatInfo";
@Injectable({
  providedIn: 'root'
})
export class GroupService {
  constructor(private httpclient: HttpClient) { }
  getAllGroups(): Observable<IGroup[]> {
    return this.httpclient.get<IGroup[]>(environment.apiUrl + '/group/all', {
      headers: new HttpHeaders({ 'No-Auth': 'True' })
    });
  }

  getGroupsWithUsersStatInfoForTeacher(): Observable<IGroupWithUsersStatInfo[]> {
    return this.httpclient.get<IGroupWithUsersStatInfo[]>(environment.apiUrl + '/group/all/forTeacher');
  }

  getGroupsWithUsersStatInfoForAdmin(): Observable<IGroupWithUsersStatInfo[]> {
    return this.httpclient.get<IGroupWithUsersStatInfo[]>(environment.apiUrl + '/group/all/forAdmin');
  }

  getStudentsWithoutGroupWithStatInfo(): Observable<IUserStatInfo[]>{
    return this.httpclient.get<IUserStatInfo[]>(environment.apiUrl + '/group/all/studentsWithoutGroup/forAdmin');
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

  getYears(): Observable<IYear[]> {
    return this.httpclient.get<IYear[]>(environment.apiUrl + '/group/years');
  }

  getFaculties(): Observable<IFaculty[]> {
    return this.httpclient.get<IFaculty[]>(environment.apiUrl + '/group/faculties');
  }
}

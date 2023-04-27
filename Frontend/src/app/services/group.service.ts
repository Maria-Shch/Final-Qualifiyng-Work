import {Injectable} from '@angular/core';
import {Observable} from "rxjs";
import {IGroup} from "../interfaces/IGroup";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {environment} from "../environments/enviroment";
import {IGroupWithUsersStatInfo} from "../dto_interfaces/IGroupWithUsersStatInfo";
import {IYear} from "../interfaces/IYear";
import {IFaculty} from "../interfaces/IFaculty";
import {IUser} from "../interfaces/IUser";
import {IFilterGroups} from "../dto_interfaces/IFilterGroups";
import {ILevelOfEdu} from "../interfaces/ILevelOfEdu";
import {IProfile} from "../interfaces/IProfile";
import {IFormOfEdu} from "../interfaces/IFormOfEdu";
import {INewGroupWithIdStudents} from "../dto_interfaces/INewGroupWithIdStudents";

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

  getGroupsAfterFiltering(filterGroups: IFilterGroups): Observable<IGroupWithUsersStatInfo[]> {
    return this.httpclient.post<IGroupWithUsersStatInfo[]>(environment.apiUrl + '/group/all/forAdmin', filterGroups);
  }

  getLevelsOfEdu(): Observable<ILevelOfEdu[]> {
    return this.httpclient.get<ILevelOfEdu[]>(environment.apiUrl + '/group/levelsOfEdu');
  }

  getProfiles(): Observable<IProfile[]> {
    return this.httpclient.get<IProfile[]>(environment.apiUrl + '/group/profiles');
  }

  getFormsOfEdu(): Observable<IFormOfEdu[]>  {
    return this.httpclient.get<IFormOfEdu[]>(environment.apiUrl + '/group/formsOfEdu');
  }

  getQuantityCourseNumber(): Observable<number>  {
    return this.httpclient.get<number>(environment.apiUrl + '/group/quantityCourseNumber');
  }

  getQuantityGroupNumber(): Observable<number>  {
    return this.httpclient.get<number>(environment.apiUrl + '/group/quantityGroupNumber');
  }

  getMaxQuantityOfProfiles(): Observable<number>  {
    return this.httpclient.get<number>(environment.apiUrl + '/group/maxQuantityOfProfiles');
  }

  addNewProfile(newProfile: IProfile): Observable<IProfile[]> {
    return this.httpclient.post<IProfile[]>(environment.apiUrl + '/group/new/profile', newProfile);
  }

  addNewFaculty(newFaculty: IFaculty): Observable<IFaculty[]> {
    return this.httpclient.post<IFaculty[]>(environment.apiUrl + '/group/new/faculty', newFaculty);
  }

  addNewYear(newYear: IYear): Observable<IYear[]> {
    return this.httpclient.post<IYear[]>(environment.apiUrl + '/group/new/year', newYear);
  }

  createNewGroup(newGroup: IGroup, studentIds: number[]): Observable<IGroup>  {
    let groupForSend = {} as IGroup;
    groupForSend.levelOfEdu = {id: newGroup.levelOfEdu?.id} as ILevelOfEdu;
    groupForSend.profile = {id: newGroup.profile?.id} as IProfile;
    groupForSend.faculty = {id: newGroup.faculty?.id} as IFaculty;
    groupForSend.formOfEdu = {id: newGroup.formOfEdu?.id} as IFormOfEdu;
    groupForSend.courseNumber = newGroup.courseNumber;
    groupForSend.groupNumber = newGroup.groupNumber;
    groupForSend.year = {id: newGroup.year?.id} as IYear;
    if (newGroup.teacher != null){
      groupForSend.teacher = {id: newGroup.teacher?.id} as IUser;
    }

    let newGroupWithStudents = {} as INewGroupWithIdStudents;
    newGroupWithStudents.group = groupForSend;
    newGroupWithStudents.studentIds = studentIds;
    return this.httpclient.post<IGroup>(environment.apiUrl + '/group/new', newGroupWithStudents);
  }
}

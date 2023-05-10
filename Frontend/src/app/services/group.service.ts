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
import {IChangedGroupMembers} from "../dto_interfaces/IChangedGroupMembers";

@Injectable({
  providedIn: 'root'
})
export class GroupService {
  constructor(private httpclient: HttpClient) { }

  public getGroupById(id: string) : Observable<IGroup> {
    return this.httpclient.get<IGroup>(environment.apiUrl + `/group/${id}`);
  }

  getAllGroups(): Observable<IGroup[]> {
    return this.httpclient.get<IGroup[]>(environment.apiUrl + '/group/all', {
      headers: new HttpHeaders({ 'No-Auth': 'True' })
    });
  }

  getGroupsByTeacher(): Observable<IGroup[]> {
    return this.httpclient.get<IGroup[]>(environment.apiUrl + '/group/all/forTeacher');
  }

  getGroupsWithUsersStatInfoForTeacher(): Observable<IGroupWithUsersStatInfo[]> {
    return this.httpclient.get<IGroupWithUsersStatInfo[]>(environment.apiUrl + '/group/withUserStatInfo/all/forTeacher');
  }

  getGroupsWithUsersStatInfoForAdmin(): Observable<IGroupWithUsersStatInfo[]> {
    return this.httpclient.get<IGroupWithUsersStatInfo[]>(environment.apiUrl + '/group/withUserStatInfo/all/forAdmin');
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
    return this.httpclient.post<IGroupWithUsersStatInfo[]>(environment.apiUrl + '/group/withUserStatInfo/all/forAdmin', filterGroups);
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
    let groupForSend = this.getGroupForSend(newGroup);
    let newGroupWithStudents = {} as INewGroupWithIdStudents;
    newGroupWithStudents.group = groupForSend;
    newGroupWithStudents.studentIds = studentIds;
    return this.httpclient.post<IGroup>(environment.apiUrl + '/group/create', newGroupWithStudents);
  }

  updateGroup(updatedGroup: IGroup): Observable<IGroup>  {
    let groupForSend = this.getGroupForSend(updatedGroup);
    return this.httpclient.post<IGroup>(environment.apiUrl + '/group/update', groupForSend);
  }

  private getGroupForSend(group: IGroup): IGroup{
    let groupForSend = {} as IGroup;
    if (group.id != null){
      groupForSend.id = group.id;
    }
    groupForSend.levelOfEdu = {id: group.levelOfEdu?.id} as ILevelOfEdu;
    groupForSend.profile = {id: group.profile?.id} as IProfile;
    groupForSend.faculty = {id: group.faculty?.id} as IFaculty;
    groupForSend.formOfEdu = {id: group.formOfEdu?.id} as IFormOfEdu;
    groupForSend.courseNumber = group.courseNumber;
    groupForSend.groupNumber = group.groupNumber;
    groupForSend.year = {id: group.year?.id} as IYear;
    if (group.teacher != null){
      groupForSend.teacher = {id: group.teacher?.id} as IUser;
    }
    return groupForSend;
  }

  updateGroupMembers(groupId: number, changedGroupMembers: IChangedGroupMembers): Observable<boolean>  {
    return this.httpclient.post<boolean>(environment.apiUrl + `/group/updateMembers/${groupId}`, changedGroupMembers);
  }

  disbandGroup(groupId: number): Observable<boolean> {
    return this.httpclient.get<boolean>(environment.apiUrl + `/group/disband/${groupId}`);
  }

  deleteGroup(groupId: number): Observable<boolean> {
    return this.httpclient.get<boolean>(environment.apiUrl + `/group/delete/${groupId}`);
  }
}

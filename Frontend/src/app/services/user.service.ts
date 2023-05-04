import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {AuthorizationService} from "./authorization.service";
import {Observable} from "rxjs";
import {IUser} from "../interfaces/IUser";
import {environment} from "../environments/enviroment";
import {IUserStatInfo} from "../dto_interfaces/IUserStatInfo";

@Injectable({
  providedIn: 'root'
})
export class UserService {
  constructor(
    private httpclient: HttpClient,
    private authService: AuthorizationService
  ) {}

  public getUser() : Observable<IUser> {
    return this.httpclient.get<IUser>(environment.apiUrl + '/user/get');
  }

  public getTeacher() : Observable<IUser> {
    return this.httpclient.get<IUser>(environment.apiUrl + '/user/teacher');
  }

  public getTeacherByStudentId(studentId: number) : Observable<IUser> {
    return this.httpclient.get<IUser>(environment.apiUrl + `/user/${studentId}/teacher`);
  }

  public getAdmin() : Observable<IUser> {
    return this.httpclient.get<IUser>(environment.apiUrl + '/user/admin');
  }

  public updateEditableUserdata(user: IUser) : Observable<IUser> {
    return this.httpclient.post<IUser>(environment.apiUrl + '/user/updateEditable', user);
  }

  public isPresent(username: string) : Observable<boolean> {
    return this.httpclient.post<boolean>(environment.apiUrl + '/user/isPresent',
      username,
      {headers: new HttpHeaders({ 'No-Auth': 'True' })}
    );
  }
  public registerNewUser(newUser: IUser) : Observable<IUser> {
    return this.httpclient.post<IUser>(environment.apiUrl + '/user/registerNewUser',
      newUser,
      {headers: new HttpHeaders({ 'No-Auth': 'True' })}
    );
  }

  public roleMatch(allowedRoles: string[]): boolean  {
    let isMatch = false;
    const userRole: any = this.authService.getRole();

    if (userRole != null && userRole) {
      for (let i = 0; i < allowedRoles.length; i++) {
        if (userRole === allowedRoles[i]) {
          isMatch = true;
          return isMatch;
        }
      }
    }
    return isMatch;
  }

  public getUserById(id: string) : Observable<IUser> {
    return this.httpclient.get<IUser>(environment.apiUrl + `/user/get/${id}`);
  }

  getTeachers(): Observable<IUser[]> {
    return this.httpclient.get<IUser[]>(environment.apiUrl + '/user/teachers');
  }

  getTeachersWithAdmin(): Observable<IUser[]> {
    return this.httpclient.get<IUser[]>(environment.apiUrl + '/user/teachersWithAdmin');
  }

  getStudentsWithoutGroupWithStatInfo(): Observable<IUserStatInfo[]>{
    return this.httpclient.get<IUserStatInfo[]>(environment.apiUrl + '/user/studentsWithoutGroupWithStatInfo/forAdmin');
  }

  getStudentsWithoutGroup(): Observable<IUser[]>{
    return this.httpclient.get<IUser[]>(environment.apiUrl + '/user/studentsWithoutGroup');
  }

  getStudentsByGroupId(id: number): Observable<IUser[]> {
    return this.httpclient.get<IUser[]>(environment.apiUrl + `/user/get/all/byGroupId/${id}`);
  }

  revokeTeacherAuthority(teacherId: number) {
    return this.httpclient.get<boolean>(environment.apiUrl + `/user/revokeTeacherAuthority/${teacherId}`);
  }
}

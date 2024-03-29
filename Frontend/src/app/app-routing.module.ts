import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {LoginComponent} from "./components/authorization/login/login.component";
import {HomeComponent} from "./components/home/home.component";
import {AuthGuard} from "./auth/auth.guard";
import {ForbiddenComponent} from "./components/utils/forbidden/forbidden.component";
import {UpdateTokensComponent} from "./components/authorization/update-tokens/update-tokens.component";
import {RegistrationComponent} from "./components/authorization/registration/registration.component";
import {ErrorComponent} from "./components/utils/error/error.component";
import {ChaptersComponent} from "./components/collection-of-tasks/chapters/chapters.component";
import {ChapterComponent} from "./components/collection-of-tasks/chapter/chapter.component";
import {PracticeComponent} from "./components/collection-of-tasks/practice/practice.component";
import {TheoryComponent} from "./components/collection-of-tasks/theory/theory.component";
import {TaskComponent} from "./components/collection-of-tasks/task/task.component";
import {AccountComponent} from "./components/account/account.component";
import {RequestsComponent} from "./components/teacher/requests/requests.component";
import {RequestComponent} from "./components/teacher/request/request.component";
import {StudentsGroupsComponent} from "./components/teacher/students-groups/students-groups.component";
import {StudentSolutionComponent} from "./components/teacher/student-solution/student-solution.component";
import {StudentAccountComponent} from "./components/teacher/student-account/student-account.component";
import {AllStudentsGroupsComponent} from "./components/admin/all-students-groups/all-students-groups.component";
import {NewGroupComponent} from "./components/admin/new-group/new-group.component";
import {EditGroupComponent} from "./components/admin/edit-group/edit-group.component";
import {ChangeGroupMembersComponent} from "./components/admin/change-group-members/change-group-members.component";
import {TeachersComponent} from "./components/admin/teachers/teachers.component";
import {TeacherComponent} from "./components/admin/teacher/teacher.component";
import {NewChapterComponent} from "./components/admin/new-chapter/new-chapter.component";
import {EditChapterComponent} from "./components/admin/edit-chapter/edit-chapter.component";
import {NewBlockComponent} from "./components/admin/new-block/new-block.component";
import {EditBlockComponent} from "./components/admin/edit-block/edit-block.component";
import {ChaptersNumberingComponent} from "./components/admin/chapters-numbering/chapters-numbering.component";
import {BlocksNumberingComponent} from "./components/admin/blocks-numbering/blocks-numbering.component";
import {NewTaskComponent} from "./components/admin/new-task/new-task.component";
import {EditTaskComponent} from "./components/admin/edit-task/edit-task.component";
import {TasksNumberingComponent} from "./components/admin/tasks-numbering/tasks-numbering.component";
import {TaskTestsComponent} from "./components/admin/task-tests/task-tests.component";

const routes: Routes = [
  { path: 'home', component: HomeComponent },
  { path: 'login', component: LoginComponent },
  { path: 'forbidden', component: ForbiddenComponent },
  { path: 'update/tokens', component: UpdateTokensComponent },
  { path: 'registration', component: RegistrationComponent },
  { path: 'error', component: ErrorComponent },
  { path: 'chapters', component: ChaptersComponent },
  { path: 'chapter/:serialNumberOfChapter', component: ChapterComponent },
  { path: 'chapter/:serialNumberOfChapter/block/:serialNumberOfBlock/practice', component: PracticeComponent },
  { path: 'chapter/:serialNumberOfChapter/block/:serialNumberOfBlock/theory', component: TheoryComponent},
  { path: 'chapter/:serialNumberOfChapter/block/:serialNumberOfBlock/task/:serialNumberOfTask', component: TaskComponent},
  { path: 'account', component: AccountComponent, canActivate:[AuthGuard], data:{roles:['USER', 'TEACHER', 'ADMIN']}},
  { path: 'requests', component: RequestsComponent, canActivate:[AuthGuard], data:{roles:['TEACHER', 'ADMIN']}},
  { path: 'request/:id', component: RequestComponent, canActivate:[AuthGuard], data:{roles:['TEACHER', 'ADMIN']}},
  { path: 'studentsGroups', component: StudentsGroupsComponent, canActivate:[AuthGuard], data:{roles:['TEACHER']}},
  { path: 'allStudentsGroups', component: AllStudentsGroupsComponent, canActivate:[AuthGuard], data:{roles:['ADMIN']}},
  { path: 'chapter/:serialNumberOfChapter/block/:serialNumberOfBlock/task/:serialNumberOfTask/student/:id',
    component: StudentSolutionComponent, canActivate:[AuthGuard], data:{roles:['TEACHER', 'ADMIN']}},
  { path: 'user/:id', component: StudentAccountComponent, canActivate:[AuthGuard], data:{roles:['TEACHER', 'ADMIN']}},
  { path: 'newGroup', component: NewGroupComponent, canActivate:[AuthGuard], data:{roles:['ADMIN']}},
  { path: 'group/edit/:id', component: EditGroupComponent, canActivate:[AuthGuard], data:{roles:['ADMIN']}},
  { path: 'group/changeMembers/:id', component: ChangeGroupMembersComponent, canActivate:[AuthGuard], data:{roles:['ADMIN']}},
  { path: 'teachers', component: TeachersComponent, canActivate:[AuthGuard], data:{roles:['ADMIN']}},
  { path: 'teacher/:id', component: TeacherComponent, canActivate:[AuthGuard], data:{roles:['ADMIN']}},
  { path: 'newChapter', component: NewChapterComponent, canActivate:[AuthGuard], data:{roles:['ADMIN']}},
  { path: 'chapter/edit/:chapterId', component: EditChapterComponent, canActivate:[AuthGuard], data:{roles:['ADMIN']}},
  { path: 'newBlock/:chapterId', component: NewBlockComponent, canActivate:[AuthGuard], data:{roles:['ADMIN']}},
  { path: 'block/edit/:blockId', component: EditBlockComponent, canActivate:[AuthGuard], data:{roles:['ADMIN']}},
  { path: 'chapters/numbering', component: ChaptersNumberingComponent, canActivate:[AuthGuard], data:{roles:['ADMIN']}},
  { path: 'chapter/:chapterId/blocks/numbering', component: BlocksNumberingComponent, canActivate:[AuthGuard], data:{roles:['ADMIN']}},
  { path: 'newTask/:blockId', component: NewTaskComponent, canActivate:[AuthGuard], data:{roles:['ADMIN']}},
  { path: 'task/edit/:taskId', component: EditTaskComponent, canActivate:[AuthGuard], data:{roles:['ADMIN']}},
  { path: 'chapter/:chapterId/block/:blockId/tasks/numbering', component: TasksNumberingComponent, canActivate:[AuthGuard], data:{roles:['ADMIN']}},
  { path: 'chapter/:serialNumberOfChapter/block/:serialNumberOfBlock/task/:serialNumberOfTask/tests',
    component: TaskTestsComponent, canActivate:[AuthGuard], data:{roles:['ADMIN']}},
];

@NgModule({
    imports:[RouterModule.forRoot(routes)],
    exports:[RouterModule]
})
export class AppRoutingModule{}

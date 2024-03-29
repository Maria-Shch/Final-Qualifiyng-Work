import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppComponent} from './app.component';
import {RouterModule, RouterOutlet} from "@angular/router";
import {AppRoutingModule} from "./app-routing.module";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {HTTP_INTERCEPTORS, HttpClientModule} from "@angular/common/http";
import {EditorModule, TINYMCE_SCRIPT_SRC} from "@tinymce/tinymce-angular";
import {MatExpansionModule} from '@angular/material/expansion';
import {MatSlideToggleModule} from '@angular/material/slide-toggle';
import {MatDialogModule} from '@angular/material/dialog';

import {LoginComponent} from './components/authorization/login/login.component';
import {HeaderComponent} from './components/utils/header/header.component';
import {HomeComponent} from './components/home/home.component';
import {AuthGuard} from "./auth/auth.guard";
import {AuthInterceptor} from "./auth/auth.interceptor";
import {UserService} from "./services/user.service";
import {ForbiddenComponent} from './components/utils/forbidden/forbidden.component';
import {UpdateTokensComponent} from './components/authorization/update-tokens/update-tokens.component';
import {RegistrationComponent} from './components/authorization/registration/registration.component';
import {ErrorComponent} from './components/utils/error/error.component';
import {ToHomeComponent} from './components/utils/to-home/to-home.component';
import {ChaptersComponent} from './components/collection-of-tasks/chapters/chapters.component';
import {ChapterComponent} from './components/collection-of-tasks/chapter/chapter.component';
import {NextComponent} from './components/utils/next/next.component';
import {PreviousComponent} from './components/utils/previous/previous.component';
import {ToChaptersComponent} from './components/utils/to-chapters/to-chapters.component';
import {PracticeComponent} from './components/collection-of-tasks/practice/practice.component';
import {TheoryComponent} from './components/collection-of-tasks/theory/theory.component';
import {TaskComponent} from './components/collection-of-tasks/task/task.component';
import {TaskSwitcherComponent} from './components/utils/task-switcher/task-switcher.component';
import {BlockSwitcherComponent} from './components/utils/block-switcher/block-switcher.component';
import {AccountComponent} from './components/account/account.component';
import {RequestsComponent} from './components/teacher/requests/requests.component';
import {CheckboxGroupComponent} from './components/utils/checkbox-group/checkbox-group.component';
import {MatRadioModule} from '@angular/material/radio';
import {RequestComponent} from './components/teacher/request/request.component';
import {StudentsGroupsComponent} from './components/teacher/students-groups/students-groups.component';
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {StudentSolutionComponent} from './components/teacher/student-solution/student-solution.component';
import {StudentAccountComponent} from './components/teacher/student-account/student-account.component';
import {AllStudentsGroupsComponent} from './components/admin/all-students-groups/all-students-groups.component';
import {GroupDropDownListComponent} from './components/utils/group-drop-down-list/group-drop-down-list.component';
import {
  StudentsWithoutGroupDropDownListComponent
} from './components/utils/students-without-group-drop-down-list/students-without-group-drop-down-list.component';
import {NewGroupComponent} from './components/admin/new-group/new-group.component';
import {EditGroupComponent} from './components/admin/edit-group/edit-group.component';
import {ChangeGroupMembersComponent} from './components/admin/change-group-members/change-group-members.component';
import {CheckboxGroupUsersComponent} from './components/utils/checkbox-group-users/checkbox-group-users.component';
import {TeachersComponent} from './components/admin/teachers/teachers.component';
import {TeacherComponent} from './components/admin/teacher/teacher.component';
import {FooterComponent} from './components/utils/footer/footer.component';
import {
  CodeCheckResponseResultDropDownListComponent
} from './components/utils/code-check-response-result-drop-down-list/code-check-response-result-drop-down-list.component';
import {NewChapterComponent} from './components/admin/new-chapter/new-chapter.component';
import {EditChapterComponent} from './components/admin/edit-chapter/edit-chapter.component';
import {NewBlockComponent} from './components/admin/new-block/new-block.component';
import {EditBlockComponent} from './components/admin/edit-block/edit-block.component';
import {ChaptersNumberingComponent} from './components/admin/chapters-numbering/chapters-numbering.component';
import {BlocksNumberingComponent} from './components/admin/blocks-numbering/blocks-numbering.component';
import {NewTaskComponent} from './components/admin/new-task/new-task.component';
import {EditTaskComponent} from './components/admin/edit-task/edit-task.component';
import {
  CheckboxGroupPreviousTasksComponent
} from './components/utils/checkbox-group-previous-tasks/checkbox-group-previous-tasks.component';
import { TasksNumberingComponent } from './components/admin/tasks-numbering/tasks-numbering.component';
import { PopupComponent } from './components/utils/popup/popup.component';
import { TaskTestsComponent } from './components/admin/task-tests/task-tests.component';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    HeaderComponent,
    HomeComponent,
    ForbiddenComponent,
    UpdateTokensComponent,
    RegistrationComponent,
    ErrorComponent,
    ToHomeComponent,
    ChaptersComponent,
    ChapterComponent,
    NextComponent,
    PreviousComponent,
    ToChaptersComponent,
    PracticeComponent,
    TheoryComponent,
    TaskComponent,
    TaskSwitcherComponent,
    BlockSwitcherComponent,
    AccountComponent,
    RequestsComponent,
    CheckboxGroupComponent,
    RequestComponent,
    StudentsGroupsComponent,
    StudentSolutionComponent,
    StudentAccountComponent,
    AllStudentsGroupsComponent,
    GroupDropDownListComponent,
    StudentsWithoutGroupDropDownListComponent,
    NewGroupComponent,
    EditGroupComponent,
    ChangeGroupMembersComponent,
    CheckboxGroupUsersComponent,
    TeachersComponent,
    TeacherComponent,
    FooterComponent,
    CodeCheckResponseResultDropDownListComponent,
    NewChapterComponent,
    EditChapterComponent,
    NewBlockComponent,
    EditBlockComponent,
    ChaptersNumberingComponent,
    BlocksNumberingComponent,
    NewTaskComponent,
    EditTaskComponent,
    CheckboxGroupPreviousTasksComponent,
    TasksNumberingComponent,
    PopupComponent,
    TaskTestsComponent
  ],
  imports: [
    BrowserModule,
    RouterOutlet,
    AppRoutingModule,
    FormsModule,
    HttpClientModule,
    RouterModule,
    ReactiveFormsModule,
    EditorModule,
    MatRadioModule,
    MatExpansionModule,
    BrowserAnimationsModule,
    MatSlideToggleModule,
    MatDialogModule
  ],
  providers: [
    AuthGuard,
    {
      provide: HTTP_INTERCEPTORS,
      useClass:AuthInterceptor,
      multi:true
    },
    UserService,
    {
      provide: TINYMCE_SCRIPT_SRC,
      useValue: 'tinymce/tinymce.min.js'
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }

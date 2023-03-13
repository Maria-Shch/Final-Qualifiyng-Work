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
  { path: 'request/:id', component: RequestComponent, canActivate:[AuthGuard], data:{roles:['TEACHER', 'ADMIN']}}
];

@NgModule({
    imports:[RouterModule.forRoot(routes)],
    exports:[RouterModule]
})
export class AppRoutingModule{}

import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {LoginComponent} from "./components/authorization/login/login.component";
import {HomeComponent} from "./components/home/home.component";
import {AdminComponent} from "./components/admin/admin.component";
import {AuthGuard} from "./auth/auth.guard";
import {UserComponent} from "./components/user/user.component";
import {ForbiddenComponent} from "./components/utils/forbidden/forbidden.component";
import {UpdateTokensComponent} from "./components/authorization/update-tokens/update-tokens.component";
import {RegistrationComponent} from "./components/authorization/registration/registration.component";
import {ErrorComponent} from "./components/utils/error/error.component";
import {ChaptersComponent} from "./components/collection-of-tasks/chapters/chapters.component";
import {ChapterComponent} from "./components/collection-of-tasks/chapter/chapter.component";
import {PracticeComponent} from "./components/collection-of-tasks/practice/practice.component";
import {TheoryComponent} from "./components/collection-of-tasks/theory/theory.component";
import {TaskComponent} from "./components/collection-of-tasks/task/task.component";

const routes: Routes = [
    { path: 'home', component: HomeComponent },
    { path: 'admin', component: AdminComponent, canActivate:[AuthGuard], data:{roles:['ADMIN']} },
    { path: 'user', component: UserComponent ,  canActivate:[AuthGuard], data:{roles:['USER']} },
    { path: 'login', component: LoginComponent },
    { path: 'forbidden', component: ForbiddenComponent },
    { path: 'update/tokens', component: UpdateTokensComponent },
    { path: 'registration', component: RegistrationComponent },
    { path: 'error', component: ErrorComponent },
    { path: 'chapters', component: ChaptersComponent },
    { path: 'chapter/:serialNumberOfChapter', component: ChapterComponent },
    { path: 'chapter/:serialNumberOfChapter/block/:serialNumberOfBlock/practice', component: PracticeComponent },
    { path: 'chapter/:serialNumberOfChapter/block/:serialNumberOfBlock/theory', component: TheoryComponent},
    { path: 'chapter/:serialNumberOfChapter/block/:serialNumberOfBlock/task/:serialNumberOfTask', component: TaskComponent}
];

@NgModule({
    imports:[RouterModule.forRoot(routes)],
    exports:[RouterModule]
})
export class AppRoutingModule{}

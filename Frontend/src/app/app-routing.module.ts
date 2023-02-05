import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {LoginComponent} from "./components/authorization/login/login.component";
import {HomeComponent} from "./components/home/home.component";
import {AdminComponent} from "./components/admin/admin.component";
import {AuthGuard} from "./auth/auth.guard";
import {UserComponent} from "./components/user/user.component";
import {ForbiddenComponent} from "./components/forbidden/forbidden.component";

const routes: Routes = [
  { path: 'home', component: HomeComponent },
  { path: 'admin', component: AdminComponent, canActivate:[AuthGuard], data:{roles:['ADMIN']} },
  { path: 'user', component: UserComponent ,  canActivate:[AuthGuard], data:{roles:['USER']} },
  { path: 'login', component: LoginComponent },
  { path: 'forbidden', component: ForbiddenComponent }
];

@NgModule({
  imports:[RouterModule.forRoot(routes)],
  exports:[RouterModule]
})
export class AppRoutingModule{}

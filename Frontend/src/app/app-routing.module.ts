import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {LoginComponent} from "./components/authorization/login/login.component";
import {HomeComponent} from "./components/home/home.component";
import {AdminComponent} from "./components/admin/admin.component";
import {AuthGuard} from "./auth/auth.guard";
import {UserComponent} from "./components/user/user.component";
import {ForbiddenComponent} from "./components/authorization/forbidden/forbidden.component";
import {UpdateTokensComponent} from "./components/authorization/update-tokens/update-tokens.component";

const routes: Routes = [
    { path: 'home', component: HomeComponent },
    { path: 'admin', component: AdminComponent, canActivate:[AuthGuard], data:{roles:['ADMIN']} },
    { path: 'user', component: UserComponent ,  canActivate:[AuthGuard], data:{roles:['USER']} },
    { path: 'login', component: LoginComponent },
    { path: 'forbidden', component: ForbiddenComponent },
    { path: 'update/tokens', component: UpdateTokensComponent }
];

@NgModule({
    imports:[RouterModule.forRoot(routes)],
    exports:[RouterModule]
})
export class AppRoutingModule{}

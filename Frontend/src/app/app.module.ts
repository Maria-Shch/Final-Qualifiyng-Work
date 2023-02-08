import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppComponent } from './app.component';
import {RouterModule, RouterOutlet} from "@angular/router";
import {AppRoutingModule} from "./app-routing.module";
import { LoginComponent } from './components/authorization/login/login.component';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {HTTP_INTERCEPTORS, HttpClientModule} from "@angular/common/http";
import { HeaderComponent } from './components/header/header.component';
import { HomeComponent } from './components/home/home.component';
import { AdminComponent } from './components/admin/admin.component';
import { UserComponent } from './components/user/user.component';
import {AuthGuard} from "./auth/auth.guard";
import {AuthInterceptor} from "./auth/auth.interceptor";
import {UserService} from "./services/user.service";
import { ForbiddenComponent } from './components/utils/forbidden/forbidden.component';
import { UpdateTokensComponent } from './components/authorization/update-tokens/update-tokens.component';
import { RegistrationComponent } from './components/authorization/registration/registration.component';
import { ErrorComponent } from './components/utils/error/error.component';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    HeaderComponent,
    HomeComponent,
    AdminComponent,
    UserComponent,
    ForbiddenComponent,
    UpdateTokensComponent,
    RegistrationComponent,
    ErrorComponent
  ],
  imports: [
    BrowserModule,
    RouterOutlet,
    AppRoutingModule,
    FormsModule,
    HttpClientModule,
    RouterModule,
    ReactiveFormsModule
  ],
  providers: [
    AuthGuard,
    {
      provide: HTTP_INTERCEPTORS,
      useClass:AuthInterceptor,
      multi:true
    },
    UserService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }

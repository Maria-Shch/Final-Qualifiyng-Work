import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppComponent } from './app.component';
import {RouterModule, RouterOutlet} from "@angular/router";
import {AppRoutingModule} from "./app-routing.module";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {HTTP_INTERCEPTORS, HttpClientModule} from "@angular/common/http";
import { EditorModule, TINYMCE_SCRIPT_SRC} from "@tinymce/tinymce-angular";

import { LoginComponent } from './components/authorization/login/login.component';
import { HeaderComponent } from './components/header/header.component';
import { HomeComponent } from './components/home/home.component';
import {AuthGuard} from "./auth/auth.guard";
import {AuthInterceptor} from "./auth/auth.interceptor";
import {UserService} from "./services/user.service";
import { ForbiddenComponent } from './components/utils/forbidden/forbidden.component';
import { UpdateTokensComponent } from './components/authorization/update-tokens/update-tokens.component';
import { RegistrationComponent } from './components/authorization/registration/registration.component';
import { ErrorComponent } from './components/utils/error/error.component';
import { ToHomeComponent } from './components/utils/to-home/to-home.component';
import { ChaptersComponent } from './components/collection-of-tasks/chapters/chapters.component';
import { ChapterComponent } from './components/collection-of-tasks/chapter/chapter.component';
import { NextComponent } from './components/utils/next/next.component';
import { PreviousComponent } from './components/utils/previous/previous.component';
import { ToChaptersComponent } from './components/utils/to-chapters/to-chapters.component';
import { PracticeComponent } from './components/collection-of-tasks/practice/practice.component';
import { TheoryComponent } from './components/collection-of-tasks/theory/theory.component';
import { TaskComponent } from './components/collection-of-tasks/task/task.component';
import { TaskSwitcherComponent } from './components/utils/task-switcher/task-switcher.component';
import { BlockSwitcherComponent } from './components/utils/block-switcher/block-switcher.component';
import { AccountComponent } from './components/account/account.component';
import { RequestsComponent } from './components/teacher/requests/requests.component';
import { CheckboxGroupComponent } from './components/utils/checkbox-group/checkbox-group.component';
import {MatRadioModule} from '@angular/material/radio';
import { RequestComponent } from './components/teacher/request/request.component';

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
    RequestComponent
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
    MatRadioModule
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

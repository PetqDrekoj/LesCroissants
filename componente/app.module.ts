import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { MainPageComponent } from './main-page/main-page.component';
import { LoginPageComponent } from './login-page/login-page.component';
import { RegisterPageComponent } from './register-page/register-page.component';
import {HttpClientModule} from "@angular/common/http";
import {LoginService} from "./login-page/shared/service";
import {RegisterService} from "./register-page/shared/service";
import {ProfilePageComponent} from "./profile-page/profile-page.component";
import { AbstractsComponent } from './main-page/abstracts/abstracts.component';
import { ConferencesComponent } from './main-page/conferences/conferences.component';
import { PapersComponent } from './main-page/papers/papers.component';
import { MembersComponent } from './main-page/members/members.component';
import {MemberService} from "./main-page/members/shared/service";
import {PaperService} from "./main-page/papers/shared/service";
import {ConferenceService} from "./main-page/conferences/shared/service";
import {AbstractService} from "./main-page/abstracts/shared/service";

@NgModule({
  declarations: [
    AppComponent,
    MainPageComponent,
    LoginPageComponent,
    RegisterPageComponent,
    ProfilePageComponent,
    AbstractsComponent,
    ConferencesComponent,
    PapersComponent,
    MembersComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
  ],
  providers: [LoginService, RegisterService, MemberService, PaperService, ConferenceService, AbstractService],
  bootstrap: [AppComponent]
})
export class AppModule { }
import {Component, Input, OnInit} from '@angular/core';
import {Router} from "@angular/router";
import {ConferenceService} from "./conferences/shared/service";
import {AbstractService} from "./abstracts/shared/service";
import {ConferenceUser} from "./conferences/shared/model";

@Component({
  selector: 'app-main-page',
  templateUrl: './main-page.component.html',
  styleUrls: ['./main-page.component.css']
})
export class MainPageComponent implements OnInit {
    conferencesList: ConferenceUser[];
  constructor(private router: Router, private abstractService: AbstractService, private conferenceService: ConferenceService) {
    this.conferencesList = [];
  }

  @Input() option : number = 1;

  ngOnInit(): void {
    if(localStorage.getItem("state")=="true")
      this.conferenceService.getConferencesFromUser().subscribe(c=>{
      this.conferencesList = c;
    });
  }

  register(){
    this.router.navigate(['register']);
  }

  mainpage():void{
    this.option = 1;
  }

    login(){
    this.router.navigate(['login']);
  }

  logout(){
    localStorage.removeItem("username");
    localStorage.setItem("state", "false");
    localStorage.clear();
  }

  isAuthenticated(): boolean{
    if(localStorage.getItem("state")=="true")
      return true;
    return false;
  }

  getUsername(): string{
    return localStorage.getItem("username");
  }

  profile(): void{
    this.router.navigate(["profile"]);
  }

  conferences():void{
    this.option = 2;
  }

  papers():void{
    this.option = 3;
  }

  members():void{
    this.option = 4;
  }

  getOption():number{
    return this.option;
  }

  userIsChairAtAConference():boolean{
    return this.conferencesList.filter(c=>c.title=="Chair").length>0;
  }
  userIsPCMemberAtAConference():boolean{
    return this.conferencesList.filter(c=>c.title=="PCMember").length>0;
  }
  userIsAuthorAtAConference():boolean{
    return this.conferencesList.filter(c=>c.title=="Author").length>0;
  }
  userIsMemberAtAConference():boolean{
    return this.conferencesList.filter(c=>c.title=="Member").length>0;
  }

  populateConferenceList(user_title: string):ConferenceUser[]{
    return this.conferencesList.filter(p=>p.title == user_title);
  }

  changeSelectedConference(conference_id: string):void{
    localStorage.setItem("selected_conference_id",conference_id);
    this.option = 1;
    console.log("dwq")
  }

  insert(): void{

  }
}
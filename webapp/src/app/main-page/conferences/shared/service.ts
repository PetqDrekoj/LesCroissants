import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {Conference, ConferenceDescription, ConferenceUser, JoinConferenceDto} from "./model";
import {MemberService} from "../../members/shared/service";

@Injectable()
export class ConferenceService {

  constructor(private httpClient: HttpClient, private memberService: MemberService) {
  }


  getConferencesFromUser(): Observable<ConferenceUser[]> {
    if(localStorage.getItem("username") != null) {
      let user = localStorage.getItem("username");
      return this.httpClient.post<Array<ConferenceUser>>("http://localhost:8080/api/conferences/", user);
    }
    return null;
  }
  getConferencesChairCoChair(): Observable<ConferenceDescription[]> {
    return this.httpClient.get<Array<ConferenceDescription>>("http://localhost:8080/api/conferencest/",{params:{username:localStorage.getItem("username")}});
  }

  joinConference(conference_id:number):Observable<any>{
    var c = new JoinConferenceDto(localStorage.getItem("username"),conference_id);
    return this.httpClient.post<any>("http://localhost:8080/api/conferencest",c as JoinConferenceDto);
  }

  getConferenceFromName(name:string):Observable<Conference>{
    return this.httpClient.post<Conference>("http://localhost:8080/api/conference",name);
  }

}

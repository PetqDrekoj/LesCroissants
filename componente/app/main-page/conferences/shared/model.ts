export class date{
  day: number;
  month: number;
  year: number;
}

export class Conference{
  id: number;
  name: string;
  abstractDeadline: date;
  paperDeadline: date;
  bidDeadline: date;
  reviewDeadline: date;
  startingDate: date;
  endingDate: date;
  chair_id: number;
  co_chair_id: number;

}

export class ConferenceUser{
  title: string;
  conferenceName: string;

}

export class ConferenceDescription {
  name: string;
  conference: Conference;
  chairName: string;
  co_chairName: string;
}
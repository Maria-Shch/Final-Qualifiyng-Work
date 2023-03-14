import {IEventType} from "./IEventType";
import {IRequest} from "./IRequest";

export interface IEventHistory{
  id: number;
  eventType: IEventType;
  request: IRequest;
  time: Date;
  timeToPrint: string;
}

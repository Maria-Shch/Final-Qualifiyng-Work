import {Component, OnInit} from '@angular/core';
import {IRequest} from "../../../interfaces/IRequest";
import {GroupService} from "../../../services/group.service";
import {Router} from "@angular/router";
import {RequestService} from "../../../services/request.service";
import {IGroup} from "../../../interfaces/IGroup";
import {IRequestType} from "../../../interfaces/IRequestType";
import {IRequestState} from "../../../interfaces/IRequestState";
import {CheckboxItem} from "../../../classes/CheckboxItem";
import {IFilter} from "../../../dto_interfaces/IFilter";

@Component({
  selector: 'app-requests',
  templateUrl: './requests.component.html',
  styleUrls: ['./requests.component.css']
})
export class RequestsComponent implements OnInit{
  countRequests: number = 0;
  countPages: number = 0;
  numberOfCurrentRequestPage = 0;
  requests: IRequest[] = [];
  filter: IFilter | null = null;
  requestTypesOptions = new Array<CheckboxItem>();
  requestStatesOptions = new Array<CheckboxItem>();
  groupsOptions = new Array<CheckboxItem>();
  orders: string[] = ['Сначала новые', 'Сначала старые'];
  order: string = this.orders[0];


  constructor(
    private requestService: RequestService,
    private groupService: GroupService,
    private router:Router
  ) {}

  ngOnInit(): void {
    this.requestService.getCountOfRequestsByTeacher().subscribe((data: number) => {
      this.countRequests = data;
      if (this.countRequests != 0){
        this.countPages = Math.ceil(this.countRequests/10);
        this.requestService.getRequests(this.numberOfCurrentRequestPage, null).subscribe((data: IRequest[]) => {
          this.requests = data;
        });
      }
    });
    this.groupService.getGroups().subscribe((data: IGroup[]) => {
      this.groupsOptions = data.map(x => new CheckboxItem(x.id, x.name, false));
    });
    this.requestService.getRequestTypes().subscribe((data: IRequestType[]) => {
      this.requestTypesOptions = data.map(x => new CheckboxItem(x.id, x.name, false));
    });
    this.requestService.getRequestStates().subscribe((data: IRequestState[]) => {
      this.requestStatesOptions = data.map(x => new CheckboxItem(x.id, x.name, false));
    });
  }

  toRequest(id: number) {

  }

  nextPage(numberOfCurrentRequestPage: number) {
    this.requestService.getRequests(numberOfCurrentRequestPage + 1, this.filter).subscribe((data: IRequest[]) => {
      this.requests = data;
      this.numberOfCurrentRequestPage+=1;
    });
  }

  previousPage(numberOfCurrentRequestPage: number) {
    this.requestService.getRequests(numberOfCurrentRequestPage - 1, this.filter).subscribe((data: IRequest[]) => {
      this.requests = data;
      this.numberOfCurrentRequestPage-=1;
    });
  }

  onChangeFilter(value: any) {
    let ascending: boolean = this.order === 'Сначала новые' ? false : true;
    this.filter = {
      groupIds: this.groupsOptions.filter(x => x.checked).map(x => x.value),
      requestTypeIds: this.requestTypesOptions.filter(x => x.checked).map(x => x.value),
      requestStateIds: this.requestStatesOptions.filter(x => x.checked).map(x => x.value),
      ascending: ascending
    }

    this.requestService.getCountRequestsAfterFiltering(this.filter).subscribe((data: number) => {
      this.countRequests = data;
      this.numberOfCurrentRequestPage = 0;

      if (this.countRequests != 0){
        this.countPages = Math.ceil(this.countRequests/10);
        this.requestService.getRequests(this.numberOfCurrentRequestPage, this.filter!).subscribe((data: IRequest[]) => {
          this.requests = data;
        });
      } else {
        this.requests = [];
        this.countPages = 1;
      }
    });
  }
}

import {Component, OnInit} from '@angular/core';
import {IRequest} from "../../../interfaces/IRequest";
import {GroupService} from "../../../services/group.service";
import {Router} from "@angular/router";
import {RequestService} from "../../../services/request.service";
import {IGroup} from "../../../interfaces/IGroup";
import {IRequestType} from "../../../interfaces/IRequestType";
import {IRequestState} from "../../../interfaces/IRequestState";
import {CheckboxItem} from "../../../classes/CheckboxItem";
import {IFilterRequests} from "../../../dto_interfaces/IFilterRequests";

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
  filterRequests: IFilterRequests | null = null;
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
    this.groupService.getGroupsByTeacher().subscribe((data: IGroup[]) => {
      if(data?.length != 0){
        if (data.at(0)?.teacher?.role == 'ADMIN'){
          this.groupsOptions.push(new CheckboxItem(null, 'Без группы', false));
        }
        this.groupsOptions = this.groupsOptions.concat(data.map(x => new CheckboxItem(x.id, x.name + ' (' + x.year?.name + ')', false)));
      }
    });
    this.requestService.getRequestTypes().subscribe((data: IRequestType[]) => {
      this.requestTypesOptions = data.map(x => new CheckboxItem(x.id, x.name, false));
    });
    this.requestService.getRequestStates().subscribe((data: IRequestState[]) => {
      this.requestStatesOptions = data.map(x => new CheckboxItem(x.id, x.name, false));
    });
  }

  toRequest(id: number) {
    this.router.navigate([`/request/${id}`]);
  }

  nextPage(numberOfCurrentRequestPage: number) {
    this.requestService.getRequests(numberOfCurrentRequestPage + 1, this.filterRequests).subscribe((data: IRequest[]) => {
      this.requests = data;
      this.numberOfCurrentRequestPage+=1;
    });
  }

  previousPage(numberOfCurrentRequestPage: number) {
    this.requestService.getRequests(numberOfCurrentRequestPage - 1, this.filterRequests).subscribe((data: IRequest[]) => {
      this.requests = data;
      this.numberOfCurrentRequestPage-=1;
    });
  }

  onChangeFilter(value: any) {
    let ascending: boolean = this.order !== 'Сначала новые';
    this.filterRequests = {
      groupIds: this.groupsOptions.filter(x => x.checked).map(x => x.value),
      requestTypeIds: this.requestTypesOptions.filter(x => x.checked).map(x => x.value),
      requestStateIds: this.requestStatesOptions.filter(x => x.checked).map(x => x.value),
      ascending: ascending
    }

    this.requestService.getCountRequestsAfterFiltering(this.filterRequests).subscribe((data: number) => {
      this.countRequests = data;
      this.numberOfCurrentRequestPage = 0;

      if (this.countRequests != 0){
        this.countPages = Math.ceil(this.countRequests/10);
        this.requestService.getRequests(this.numberOfCurrentRequestPage, this.filterRequests!).subscribe((data: IRequest[]) => {
          this.requests = data;
        });
      } else {
        this.requests = [];
        this.countPages = 1;
      }
    });
  }
}

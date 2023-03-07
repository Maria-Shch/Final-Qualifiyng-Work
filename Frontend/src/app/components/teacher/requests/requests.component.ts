import {Component, OnInit} from '@angular/core';
import {IRequest} from "../../../interfaces/IRequest";
import {GroupService} from "../../../services/group.service";
import {Router} from "@angular/router";
import {RequestService} from "../../../services/request.service";
import {IGroup} from "../../../interfaces/IGroup";

@Component({
  selector: 'app-requests',
  templateUrl: './requests.component.html',
  styleUrls: ['./requests.component.css']
})
export class RequestsComponent implements OnInit{
  countRequests: number = 0;
  countPages: number = 0;
  numberOfCurrentRequestPage = 1;
  requests: IRequest[] = [];
  groups: IGroup[] = [];

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
        this.requestService.getRequestsByPageNumber(this.numberOfCurrentRequestPage).subscribe((data: IRequest[]) => {
          this.requests = data;
        });
      }
    });
    this.groupService.getGroups().subscribe((data: IGroup[]) => {
      this.groups = data;
    })
  }

  toRequest(id: number) {

  }

  nextPage(numberOfCurrentRequestPage: number) {
    this.requestService.getRequestsByPageNumber(numberOfCurrentRequestPage + 1).subscribe((data: IRequest[]) => {
      this.requests = data;
      this.numberOfCurrentRequestPage+=1;
    });
  }

  previousPage(numberOfCurrentRequestPage: number) {
    this.requestService.getRequestsByPageNumber(numberOfCurrentRequestPage - 1).subscribe((data: IRequest[]) => {
      this.requests = data;
      this.numberOfCurrentRequestPage-=1;
    });
  }
}

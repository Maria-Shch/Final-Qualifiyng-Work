import { Component } from '@angular/core';
import {AuthorizationService} from "./services/authorization.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html'
})
export class AppComponent {
  title = 'Frontend';

  constructor(private authService: AuthorizationService) {
  }

  public isLoggedIn() {
    return this.authService.isLoggedIn();
  }
}

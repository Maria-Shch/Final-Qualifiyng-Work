import {Router} from "@angular/router";

export function toErrorPage(error: any, router: Router){
  console.log(error);
  router.navigate(['/error']);
}

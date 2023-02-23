import {HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest,} from '@angular/common/http';
import {Router} from '@angular/router';
import {catchError} from 'rxjs/operators';
import {Observable, throwError} from 'rxjs';
import {Injectable} from '@angular/core';
import {AuthorizationService} from "../services/authorization.service";

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
    constructor(
      private authService: AuthorizationService,
      private router:Router
    ) {}

    intercept (req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

        if (req.headers.get('No-Auth') === 'True') {
            return next.handle(req.clone());
        }

        let token = this.authService.getAccessToken();

        req = this.addToken(req, token);
        return next.handle(req).pipe(
            catchError(
                (err:HttpErrorResponse) => {
                    console.log(err.status);
                    if(err.status === 401) {
                        this.router.navigate(['/login']);
                    } else if(err.status === 403) {
                        //todo происходит одновременное выполнение метода refreshToken при одновременных http запросах
                        this.authService.refreshTokens().then(value => {
                          this.router.navigate(['/update/tokens']);
                        });
                    }
                    return throwError("Some thing is wrong");
                }
            )
        );
    }
    private addToken(request: HttpRequest<any>, token: string | null) {
        return request.clone(
            {
                setHeaders: {
                    Authorization : `Bearer ${token}`
                }
            }
        );
    }
}

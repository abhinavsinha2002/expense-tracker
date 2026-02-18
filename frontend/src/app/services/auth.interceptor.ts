import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent, HttpErrorResponse } from '@angular/common/http';
import { AuthService } from './auth.service';
import { Router } from '@angular/router';
import { catchError, Observable, throwError } from 'rxjs';

@Injectable()
export class AuthInterceptor implements HttpInterceptor{
    constructor(private auth:AuthService, private router: Router){}
    intercept(req: HttpRequest<any>, next: HttpHandler):Observable<HttpEvent<any>>{
        const token = this.auth.getToken();
        let authReq = req;

        if(token){
            authReq = req.clone({
                setHeaders: { Authorization: `Bearer ${token}` }
            });
        }
        return next.handle(authReq).pipe(
            catchError((error: HttpErrorResponse) => {
                
                // Check if Unauthorized (401) OR if the backend sent HTML (Login Page) by mistake
                const isUnauthorized = error.status === 401;
                const isHtmlResponse = error.status === 200 && typeof error.error === 'string' && error.error.includes('<!DOCTYPE html>');

                if (isUnauthorized || isHtmlResponse) {
                    // Token is invalid/expired -> Clear it
                    localStorage.removeItem('token');
                    localStorage.removeItem('user_data'); // If you store user info
                    
                    // Redirect to Login Page
                    this.router.navigate(['/login']);
                }

                // Propagate other errors
                return throwError(() => error);
            })
        );
    }
}
import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import { environment } from '../../environments/environment';
import { tap, switchMap } from 'rxjs/operators';
import { User } from '../models/user';
import { Observable, of } from 'rxjs';

interface AuthResponse{
    token: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService{
    private base = `${environment.apiBase}/auth`;
    currentUser:User | null =null;

    constructor(private http:HttpClient){
        
        let savedUser = localStorage.getItem('user_data');
        if(!savedUser){
            savedUser = sessionStorage.getItem('user_data');
        }

        const storedToken = this.getToken();

        if (storedToken && savedUser) {
            // Re-create the user object so the UI stays logged in
            this.currentUser = JSON.parse(savedUser);
        }
    }

    getToken():string | null{
        return sessionStorage.getItem('token') || localStorage.getItem('token');
    }

    register(user:User){
        return this.http.post(`${this.base}/register`,user,{
            responseType: 'text'
        });
    }
    login(email:string,password:string,rememberMe:boolean) : Observable<User | null>{
        return this.http.post<AuthResponse>(`${this.base}/login`,{email,password})
            .pipe(
                switchMap(res=>{
                    if(res.token){
                        if(rememberMe){
                            localStorage.setItem('token',res.token);
                            sessionStorage.removeItem('token');
                        }
                        else{
                            sessionStorage.setItem('token',res.token);
                            localStorage.removeItem('token');
                        }
                        return this.getCurrentUser();
                    }
                    return of(null);
                })
            )
    }

    saveToken(token:string){
        localStorage.setItem('token',token);
        sessionStorage.removeItem('token');
        return this.getCurrentUser();
    }

    logout(){
        localStorage.removeItem('token');
        localStorage.removeItem('user_data');
        sessionStorage.removeItem('token');
        sessionStorage.removeItem('user_data');
        this.currentUser = null;
    }

    private setSession(token:string,rememberMe:boolean){
        localStorage.removeItem('token');
        sessionStorage.removeItem('token');

        if(rememberMe){
            localStorage.setItem('token',token);
        }
        else{
            sessionStorage.setItem('token',token);
        }
    }

    isLoggedIn():boolean{
        return !!this.getToken();
    }

    requestPasswordReset(email:string){
        return this.http.post(`${this.base}/reset/request`,{},{
            params:{email},
            responseType:'text'
        });
    }

    resetPassword(token:string,password:string){
        return this.http.post(`${this.base}/reset/confirm`,{},{
            params:{
                token,
                newPassword:password
            },
            responseType:'text'
        });
    }

    verifyAccount(token:string){
        return this.http.get(`${this.base}/verify`,{
            params:{ token },
            responseType: 'text'
        });
    }

    checkAvailability(field:string, value:string){
        return this.http.get<{available:boolean}>(`${this.base}/check-availability?field=${field}&value=${value}`);
    }

    getCurrentUser(){
        return this.http.get<User>(`${this.base}/user/me`).pipe(
            tap((user)=>{
                this.currentUser = user;

                if(localStorage.getItem('token')){
                    localStorage.setItem('user_data',JSON.stringify(user));
                    sessionStorage.removeItem('user_data');
                }
                else{
                    sessionStorage.setItem('user_data',JSON.stringify(user));
                    localStorage.removeItem('user_data');
                }
            })
        )
    }

    validateResetToken(token: string){
        return this.http.get<boolean>(`${this.base}/reset/validate?token=${token}`);
    }
}
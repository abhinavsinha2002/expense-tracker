import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import { environment } from '../../environments/environment';
import { tap } from 'rxjs/operators';
import { User } from '../models/user';

interface AuthResponse{
    token: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService{
    private base = `${environment.apiBase}/auth`;
    currentUser:any=null;
    constructor(private http:HttpClient){
        const storedToken = this.getToken();
        const storedName = this.getUsername();

        if (storedToken && storedName) {
            // Re-create the user object so the UI stays logged in
            this.currentUser = { 
                name: storedName, 
                role: 'User' // You can default this or save role in localStorage too
            };
        }
    }

    getToken():string | null{
        return sessionStorage.getItem('token') || localStorage.getItem('token');
    }

    getUsername():string | null{
        return sessionStorage.getItem('username') || localStorage.getItem('username');
    }

    register(user:User){
        return this.http.post(`${this.base}/register`,user,{
            responseType: 'text'
        });
    }
    login(username:string,password:string,rememberMe:boolean){
        return this.http.post<AuthResponse>(`${this.base}/login`,{username,password})
            .pipe(
                tap(res=>{
                    if(res.token){
                        this.logout();
                        if(rememberMe){
                            localStorage.setItem('token',res.token);
                            localStorage.setItem('username',username);
                        }
                        else{
                            sessionStorage.setItem('token',res.token);
                            sessionStorage.setItem('username',username);

                        }
                        

                        // --- FIX 2: SET USER IMMEDIATELY ON LOGIN ---
                        // This updates the variable so the Sidebar changes instantly
                        this.currentUser = { 
                            name: username, 
                            role: 'User'
                        }
                    }
                })
            );
    }

    logout(){
        localStorage.removeItem('token');
        localStorage.removeItem('username');
        sessionStorage.removeItem('token');
        sessionStorage.removeItem('username');
        this.currentUser = null;
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
}
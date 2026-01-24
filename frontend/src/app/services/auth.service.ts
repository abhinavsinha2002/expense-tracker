import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import { environment } from '../../environments/environment';
import { tap } from 'rxjs/operators';
import { User } from '../models/user';

interface AuthResponse{
    accessToken: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService{
    private base = `${environment.apiBase}/auth`;
    currentUser:any=null;
    constructor(private http:HttpClient){
        const storedToken = localStorage.getItem('token');
        const storedName = localStorage.getItem('username');

        if (storedToken && storedName) {
            // Re-create the user object so the UI stays logged in
            this.currentUser = { 
                name: storedName, 
                role: 'User' // You can default this or save role in localStorage too
            };
        }
    }
    register(user:User){
        return this.http.post(`${this.base}/register`,user,{
            responseType: 'text'
        });
    }
    login(username:string,password:string){
        return this.http.post<AuthResponse>(`${this.base}/login`,{username,password})
            .pipe(
                tap(res=>{
                    if(res.accessToken){
                        localStorage.setItem('token',res.accessToken);
                        localStorage.setItem('username',username);

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
        this.currentUser = null;
    }

    getToken():string | null{
        return localStorage.getItem('token');
    }

    isLoggedIn():boolean{
        return !!this.getToken();
    }

    getUsername():string{
        return localStorage.getItem('username') || '';
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
}
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Observable } from 'rxjs';

@Injectable({providedIn:'root'})
export class CurrencyService{
    private base = `${environment.apiBase}/api/currency`
    constructor(private http:HttpClient){}

    convert(from:string,to:string,amount:number):Observable<number>{
        return this.http.get<number>(`${this.base}/convert`,{
            params:{
                from,
                to,
                amount:amount.toString()
            }
        });
    }

}
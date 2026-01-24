import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Expense } from '../models/expense';
import { Observable } from 'rxjs';

export interface YearlySummary{
    total:number,
    byCategory:{[key:string]:number};
}

@Injectable({providedIn:'root'})
export class ExpenseService{
    private base = `${environment.apiBase}/api/expenses`;
    private csvBase = `${environment.apiBase}/api/csv`;

    constructor(private http:HttpClient){}
    create(dto:Expense):Observable<Expense>{
        return this.http.post<Expense>(this.base,dto);
    }

    list():Observable<Expense[]>{
        return this.http.get<Expense[]>(this.base);
    }

    delete(id:number):Observable<void>{
        return this.http.delete<void>(`${this.base}/${id}`);
    }

    summary(year:number):Observable<YearlySummary>{
        return this.http.get<YearlySummary>(`${this.base}/summary/year/${year}`);
    }

    exportCsv():Observable<Blob>{
        return this.http.get(`${this.csvBase}/export`,{
            responseType:'blob'
        });
    }

    importCsv(file:File):Observable<any>{
        const fd = new FormData();
        fd.append('file',file);

        return this.http.post(`${this.csvBase}/import`,fd,{
            responseType:'text'
        });
    }

}
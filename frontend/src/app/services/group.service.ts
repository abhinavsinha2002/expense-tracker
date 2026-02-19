import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Group, Settlement } from '../models/group'; 
import { Observable } from 'rxjs';

@Injectable({providedIn:'root'})
export class GroupService{
    private base = `${environment.apiBase}/api/groups`;

    constructor(private http: HttpClient) {}

    create(dto:Group):Observable<Group>{
        return this.http.post<Group>(this.base,dto);
    }

    getGroups():Observable<Group[]>{
        return this.http.get<Group[]>(this.base);
    }

    settle(groupId:number):Observable<Settlement[]>{
        return this.http.get<Settlement[]>(`${this.base}/${groupId}/settle`);
    }

    getGroupById(id:number):Observable<Group>{
        return this.http.get<Group>(`${this.base}/${id}`);
    }

    joinGroup(token:string):Observable<any>{
        return this.http.post(`${this.base}/join/${token}`,{});
    }
    
}



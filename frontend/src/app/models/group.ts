import { Expense } from "./expense";
import { User } from "./user";

export interface Group{
    id?:number;
    name:string;
    owner:User;
    currency?:string;
    description?:string;
    members?:User[];
    expenses?:Expense[];
}

export interface Settlement{
    fromUser:string;
    toUser:string;
    amount:number;
}
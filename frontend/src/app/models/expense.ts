import { User } from "./user";

export interface Expense {
  id?: number;
  description: string;
  amount: number;
  date?: string | number[];
  category?: string;
  owner: User
  groupName?:string;
  groupId?: number;
  splits?: { member: string; amount: number }[];
}

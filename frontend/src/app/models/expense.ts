export interface Expense {
  id?: number;
  description: string;
  amount: number;
  date?: string;
  category?: string;
  groupId?: number;
  splits?: { member: string; amount: number }[];
}

export interface ChatMessage{
    sender:string;
    message:string;
    groupId:number;
    timestamp?:string;
}
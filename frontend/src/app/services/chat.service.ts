import { Injectable } from '@angular/core';
import { Client, Message } from '@stomp/stompjs';
import { Subject } from 'rxjs';
import { ChatMessage } from '../models/chat-message';

@Injectable({providedIn:'root'})
export class ChatService{
    private client: Client;

    private messageSubject = new Subject<ChatMessage>();
    public message$ = this.messageSubject.asObservable();

    constructor(){
        this.client = new Client({
            brokerURL:'ws://localhost:8080/ws',
            reconnectDelay:5000,
        });
    }

    joinGroup(groupId:number){
        this.client.onConnect = (frame)=>{
            console.log('Connected! Tuning in to Group ${groupId}');
            this.client.subscribe(`/topic/group/${groupId}`,(message:Message)=>{
                if(message.body){
                    const parsedMessage:ChatMessage = JSON.parse(message.body);
                    this.messageSubject.next(parsedMessage);
                }
            });
        };
        this.client.activate();
    }

    disconnect(){
        this.client.deactivate();
    }

    sendMessage(groupId:number,chatMessage: ChatMessage){
        this.client.publish({
            destination:`/app/chat/${groupId}`,
            body: JSON.stringify(chatMessage)
        });
    }
}
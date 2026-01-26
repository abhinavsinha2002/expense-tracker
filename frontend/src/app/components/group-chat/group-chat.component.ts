import { Component, OnInit, OnDestroy, ViewChild, ElementRef, AfterViewChecked } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ChatService } from '../../services/chat.service'; // Ensure this path is correct
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { ChatMessage } from '../../models/chat-message';
import { Subscription } from 'rxjs';


@Component({
  selector: 'app-group-chat',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatDividerModule
  ],
  templateUrl: './group-chat.component.html',
  styleUrl: './group-chat.component.css'
})
export class GroupChatComponent implements OnInit, OnDestroy, AfterViewChecked{
    messages: ChatMessage[]=[];
    inputMessage ='';
    currentUser = 'me';
    currentGroupId = 1;

    private chatSub!: Subscription;
    // Reference to the chat container for auto-scrolling
  @ViewChild('scrollContainer') private scrollContainer!: ElementRef;

  constructor(private chatService:ChatService){}
  ngOnInit() {
      this.chatService.joinGroup(this.currentGroupId);
      this.chatSub = this.chatService.message$.subscribe((msg:ChatMessage)=>{
        this.messages.push(msg);
      })
  }

  ngAfterViewChecked() {
      this.scrollToBottom();
  }

  sendMessage(){
    if (!this.inputMessage.trim()) return;

    // 1. Construct the payload strictly according to your Interface
    const payload: ChatMessage = {
      sender: this.currentUser,
      message: this.inputMessage,  // Mapped correctly
      groupId: this.currentGroupId,// Mapped correctly
      timestamp: new Date().toLocaleTimeString() // Optional formatting
    };

    // 2. Send via Service
    this.chatService.sendMessage(this.currentGroupId, payload);
    
    // 3. Clear Input
    this.inputMessage = '';
  }

  private scrollToBottom(): void {
    try {
      this.scrollContainer.nativeElement.scrollTop = this.scrollContainer.nativeElement.scrollHeight;
    } catch(err) { }
  }

  ngOnDestroy(): void {
      if(this.chatSub){
        this.chatSub.unsubscribe();
      }
      this.chatService.disconnect();
  }
}


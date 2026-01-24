import { Component, signal } from '@angular/core';
import { RouterLink, RouterOutlet,Router, RouterLinkActive } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { CommonModule, DatePipe } from '@angular/common';
import { AuthService } from './services/auth.service';

@Component({
  selector: 'app-root',
  standalone:true,
  imports: [
    CommonModule,
    RouterOutlet,
    RouterLink,
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    RouterLinkActive,
    DatePipe
  ],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  isDark = true;
  today = new Date();
  currentUser:any = null;
  constructor(public auth: AuthService,private router:Router){}

  toggleTheme(){
    this.isDark = !this.isDark;
  }

  logout(){
    this.auth.logout();
    this.router.navigate(['login']);
  }
}

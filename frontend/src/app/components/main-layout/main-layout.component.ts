import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-main-layout',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule, // Critical for <router-outlet>
    MatIconModule,
    MatButtonModule
  ],
  templateUrl: './main-layout.component.html',
  styleUrl: './main-layout.component.css'
})
export class MainLayoutComponent {
  
  isDark = false;
  today = new Date();

  constructor(public auth: AuthService, private router: Router) {}

  getGreeting(): string {
    const hour = this.today.getHours();
    if (hour < 12) return 'Good Morning';
    if (hour < 18) return 'Good Afternoon';
    return 'Good Evening';
  }

  toggleTheme() {
    this.isDark = !this.isDark;
  }

  logout() {
    this.auth.logout();
    this.router.navigate(['/login']);
  }
}
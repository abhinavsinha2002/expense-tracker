import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-verify',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatButtonModule, MatIconModule, MatProgressSpinnerModule, RouterModule],
  templateUrl: './verify.component.html',
  styleUrl: './verify.component.css'
})
export class VerifyComponent implements OnInit{
    status: 'loading' | 'success' | 'error' = 'loading';
    message = 'Verifying your account...';
    countdown = 5;
    private timer: any;

    constructor(
        private route : ActivatedRoute,
        private auth: AuthService,
        private router: Router,
        private cdr: ChangeDetectorRef
    ){}

    ngOnInit(){
        const token = this.route.snapshot.queryParamMap.get('token');

        if(!token){
            this.handleError('Invalid verification link');
            return;
        }

        setTimeout(() => {
            this.auth.verifyAccount(token).subscribe({
                next: (response) => {
                    this.status = 'success';
                    this.message = 'Your email has been successfully verified.';
                    this.startRedirectTimer();
                    this.cdr.detectChanges();
                },
                error: (err) => {
                    const msg ='Verification link expired or invalid.';
                    this.handleError(msg);
                }
            });
        }, 1000);
          
    }

    private handleError(msg: string) {
        this.status = 'error';
        this.message = msg;
        this.startRedirectTimer(); // We redirect even on error, so they can try logging in or requesting again
        this.cdr.detectChanges();
    }

    startRedirectTimer() {
        this.timer = setInterval(() => {
            this.countdown--;
            if (this.countdown === 0) {
                this.stopTimer();
                this.goToLogin();
            }
            this.cdr.detectChanges();
        }, 1000);
    }

    stopTimer() {
        if (this.timer) {
            clearInterval(this.timer);
        }
    }

    goToLogin(){
        this.router.navigate(['/login']);
    }

    ngOnDestroy() {
        this.stopTimer();
    }

    
}
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

    constructor(
        private route : ActivatedRoute,
        private auth: AuthService,
        private router: Router,
        private cdr: ChangeDetectorRef
    ){}

    ngOnInit(){
        const token = this.route.snapshot.queryParamMap.get('token');
        console.log('Verify Component Init. Token:', token);

        if(!token){
            this.status = 'error';
            this.message = 'Invalid verification link.';
            return;
        }

        this.auth.verifyAccount(token).subscribe({
            next:(response)=>{
                console.log('Success Response:', response); // DEBUG LOG 2
                this.status = 'success';
                this.message = 'Account verified successfully!';
                this.cdr.detectChanges();
            },
            error:(err)=>{
                console.error('Error occurred:', err); // DEBUG LOG 3
                this.status = 'error';
                this.message = typeof err.error === 'string' 
                    ? err.error 
                    : 'Verification failed. The link may be expired.';

                this.cdr.detectChanges();
            }
        });
    }

    goToLogin(){
        this.router.navigate(['/login']);
    }

    
}
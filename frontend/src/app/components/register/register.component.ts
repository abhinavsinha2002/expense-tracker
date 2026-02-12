import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service'; // Adjust path if needed
import { Router, RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { CommonModule } from '@angular/common';
import { environment } from '../../../environments/environment';

@Component({
    selector: 'app-register',
    standalone: true,
    imports: [
        CommonModule,
        FormsModule,
        MatCardModule,
        MatInputModule,
        MatButtonModule,
        MatIconModule,
        MatSnackBarModule,
        MatTooltipModule,
        RouterLink
    ],
    templateUrl: './register.component.html',
    styleUrl: './register.component.css'
})
export class RegisterComponent {
    user = { email: '', password: '', fullName: '' };
    emailAvailable: boolean | null = null;

    isEmailFormatValid = true;

    hasMinLength = false;
    hasNumber = false;
    hasUpper = false;
    hasSymbol = false;
    private base = `${environment.apiBase}`;

    passwordVisible = false;

    constructor(private auth: AuthService, private router: Router, private snackBar: MatSnackBar) { }

    checkEmail() {
        this.emailAvailable = null;
        if(this.user.email.trim().length==0){
            this.isEmailFormatValid = true;
            return;
        }
        const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
        this.isEmailFormatValid = emailRegex.test(this.user.email);
        if (!this.isEmailFormatValid || this.user.email.length < 5) {
            return;
        }
        this.auth.checkAvailability('email', this.user.email).subscribe(res => {
            this.emailAvailable = res.available;
        })
    }

    checkPassword() {
        const p = this.user.password;
        this.hasMinLength = p.length >= 6;
        this.hasNumber = /\d/.test(p);
        this.hasUpper = /[A-Z]/.test(p);
        this.hasSymbol = /[!@#$%^&*(),.?":{}|<>]/.test(p);
    }

    get isFormValid(): boolean {
        return (this.user.fullName.trim().length) > 0 &&
            (this.isEmailFormatValid) &&
            (this.emailAvailable === true) &&
            (this.hasMinLength && this.hasNumber && this.hasUpper && this.hasSymbol);
    }

    register() {
        if (this.isFormValid) {
            this.auth.register(this.user).subscribe({
                next: () => {
                    this.showMessages('Registration successful! Please check your email.')
                    setTimeout(() => {
                        this.router.navigate(['/login']);
                    }, 1500);
                },
                error: (err) => {
                    const msg = err.error || err.message || 'Registration failed';
                    this.showMessages('Error: ' + msg, true);
                }
            });
        }
    }

    loginWithGoogle() {
        window.location.href = `${this.base}/oauth2/authorization/google`;
    }

    loginWithGithub() {
        window.location.href = `${this.base}/oauth2/authorization/github`;
    }


    private showMessages(message: string, isError = false) {
        this.snackBar.open(message, 'Close', {
            duration: 3000,
            horizontalPosition: 'center',
            verticalPosition: 'top',
            panelClass: isError ? ['error-snackbar'] : ['success-snackbar']
        });
    }
}






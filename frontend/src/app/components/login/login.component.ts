import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { Router, RouterLink } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar'; // Import SnackBar
import { CommonModule } from '@angular/common';
import { debounceTime, distinctUntilChanged, switchMap, of } from 'rxjs';
import { ChangeDetectorRef } from '@angular/core';
import confetti from 'canvas-confetti';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatSnackBarModule,
    RouterLink
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent implements OnInit {
  form: FormGroup;
  resetForm: FormGroup;
  isLoginView = true;
  welcomeName: string | null = null;

  hidePassword = true;
  rememberMe = true;
  capsOn = false;
  isLoginError = false;

  private base = `${environment.apiBase}`;

  isCheckingUser = false;
  isLoading = false;
  userExists: boolean | null = null;

  constructor(private fb: FormBuilder, private auth: AuthService, private router: Router, private snackBar: MatSnackBar,private cdr: ChangeDetectorRef) {
    this.form = this.fb.group({
      email: ['', [Validators.required,Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });

    this.resetForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]]
    });
  }

  checkCaps(event: KeyboardEvent) {
    this.capsOn = event.getModifierState && event.getModifierState('CapsLock');
  }
  ngOnInit() {
    this.setupLiveEmailCheck();
  }

  setupLiveEmailCheck() {
    this.form.get('email')?.valueChanges.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap(emailValue => {
        if (!emailValue || emailValue.length < 5 || !emailValue.includes('@')) {
          this.userExists = null;
          return of(null);
        }
        this.isCheckingUser = true;
        return this.auth.checkAvailability('email', emailValue);
      })
    ).subscribe({
      next: (res: any) => {
        this.isCheckingUser = false;
        if (res) {
          this.userExists = !res.available;
          if (this.userExists) {
            const email = this.form.get('email')?.value;
            const namePart = email.split('@')[0];
            this.welcomeName = this.capitalize(namePart);
          }
          else {
            this.welcomeName = null;
          }
        }
      },
      error: () => this.isCheckingUser = false
    });
  }

  loginWithGoogle(){
    window.location.href = `${this.base}/oauth2/authorization/google`;
  }

  loginWithGithub(){
    window.location.href = `${this.base}/oauth2/authorization/github`;
  }


  capitalize(str: string): string {
    if (!str) {
      return '';
    }
    return str.charAt(0).toUpperCase() + str.slice(1);
  }

  private showMessages(message: string, isError = false) {
    this.snackBar.open(message, 'Close', {
      duration: 3000,
      horizontalPosition: 'center',
      verticalPosition: 'top',
      panelClass: isError ? ['error-snackbar'] : ['success-snackbar']
    });
  }

  login() {
    if (this.form.invalid) return;

    // Optional: Block login if user definitely doesn't exist
    if (this.userExists === false) {
      this.showMessages('User does not exist.', true);
      return;
    }
    const v = this.form.value;
    this.auth.login(v.email, v.password, this.rememberMe).subscribe({
      next: () => {
        this.triggerConfetti();

        const pendingToken = localStorage.getItem('pendingInviteToken');
        if(pendingToken){
          localStorage.removeItem('pendingInviteToken');
          this.router.navigate(['/join',pendingToken]);
        }
        else{
          setTimeout(() => {
          this.router.navigate(['/main/']);
          this.showMessages(`Let's manage some money, ${this.welcomeName}!`);
        }, 1000);
        }
        
      },
      error: (err) =>{
        this.showMessages(err.error,true);
        this.triggerCrashEffect();
      }
    });
  }

  triggerCrashEffect(){
    this.isLoginError = true;
    setTimeout(() => {
      this.isLoginError = false;
    }, 500);
  }

  triggerConfetti() {
    const duration = 2000;
    const end = Date.now() + duration;

    (function frame() {
      confetti({
        particleCount: 5,
        angle: 60,
        spread: 55,
        origin: { x: 0 },
        colors: ['#dcb14a', '#ffffff'] // Gold and White
      });
      confetti({
        particleCount: 5,
        angle: 120,
        spread: 55,
        origin: { x: 1 },
        colors: ['#dcb14a', '#ffffff']
      });

      if (Date.now() < end) {
        requestAnimationFrame(frame);
      }
    }());
  }

  togglePasswordVisibility() {
    this.hidePassword = !this.hidePassword;
  }

  toggleRememberMe(e: any) {
    this.rememberMe = e.target.checked;
  }

  requestReset() {
    if (this.resetForm.invalid) {
      this.resetForm.markAllAsTouched();
      return;
    }
    this.isLoading = true;
    this.resetForm.disable();
    const email = this.resetForm.get('email')?.value;
    this.auth.requestPasswordReset(email).subscribe({
      next: (res:any) => {
        this.isLoading = false;
        this.resetForm.enable();
        this.resetForm.reset();
        this.resetForm.get('email')?.setErrors(null);
        const msg = (res.message) || 'Reset link sent if account exists.';
        this.showMessages(msg);

        setTimeout(() => {
            this.isLoginView = true; 
            this.cdr.detectChanges(); 
            }, 3000);
      },
      error: (err) =>{
        this.isLoading = false;
        this.resetForm.enable();
        this.showMessages('Error sending email.', true)
      } 
    })
  }

  toggleView() {
    this.isLoginView = !this.isLoginView;
  }
}
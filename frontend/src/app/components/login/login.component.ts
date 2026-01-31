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
import { HostListener } from '@angular/core';
import confetti from 'canvas-confetti';

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

  errorMessages = [
    "Ouch! That password hurt.",
    "Access Denied, Captain.",
    "Nice try, but no.",
    "Wrong keys! Try again?",
    "That's not the secret code."
  ];

  hidePassword = true;
  rememberMe = true;
  capsOn = false;
  isLoginError = false;

  isCheckingUser = false;
  userExists: boolean | null = null;

  constructor(private fb: FormBuilder, private auth: AuthService, private router: Router, private snackBar: MatSnackBar) {
    this.form = this.fb.group({
      username: ['', Validators.required],
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
    this.setupLiveUsernameCheck();
  }

  setupLiveUsernameCheck() {
    this.form.get('username')?.valueChanges.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap(username => {
        if (!username || username.length < 3) {
          this.userExists = null;
          return of(null);
        }
        this.isCheckingUser = true;
        return this.auth.checkAvailability('username', username);
      })
    ).subscribe({
      next: (res: any) => {
        this.isCheckingUser = false;
        if (res) {
          this.userExists = !res.available;
          if (this.userExists) {
            this.welcomeName = this.capitalize(this.form.get('username')?.value);
          }
          else {
            this.welcomeName = null;
          }
        }
      },
      error: () => this.isCheckingUser = false
    });
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
    this.auth.login(v.username, v.password, this.rememberMe).subscribe({
      next: () => {
        this.triggerConfetti();

        // --- 2. DELAY NAVIGATION SLIGHTLY TO SHOW ANIMATION ---
        setTimeout(() => {
          this.router.navigate(['/main/']);
          this.showMessages(`Let's manage some money, ${v.username}!`);
        }, 1000);
      },
      error: () =>{
        this.triggerCrashEffect();
      }
    });
  }

  triggerCrashEffect(){
    this.isLoginError = true;
    const randomMsg = this.errorMessages[Math.floor(Math.random()*this.errorMessages.length)];
    this.showMessages(randomMsg, true);

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
      return;
    }
    const email = this.resetForm.value.email;
    this.auth.requestPasswordReset(email).subscribe({
      next: () => {
        this.showMessages('Reset link sent to your email!');
        this.toggleView();
      },
      error: () => this.showMessages('Error sending email.', true)
    })
  }

  toggleView() {
    this.isLoginView = !this.isLoginView;
  }
}
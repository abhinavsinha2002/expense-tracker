import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../services/auth.service'; // Adjust path if needed
import { Router, RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { CommonModule } from '@angular/common';
import { User } from '../../models/user';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatSnackBarModule,
    RouterLink
  ],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent{
    form:FormGroup;

    constructor(private fb:FormBuilder,private auth:AuthService,private router:Router,private snackBar:MatSnackBar){
        this.form = this.fb.group({
            username:['',Validators.required],
            email:['',Validators.required,Validators.email],
            password:['',[Validators.required,Validators.minLength(6)]]
        })
    }

    private showMessage(message: string, isError = false) {
    this.snackBar.open(message, 'Close', {
      duration: 3000,
      horizontalPosition: 'center',
      verticalPosition: 'top',
      panelClass: isError ? ['error-snackbar'] : ['success-snackbar']
    });
  }

  register(){
    if(this.form.invalid){
        return;
    }
    const user:User = this.form.value as User;

    this.auth.register(user).subscribe({
        next:()=>{
            this.showMessage('Registration successful! Please login.');
            this.router.navigate(['/login']);
        },
        error:(e)=>{
            const errorMsg = e.error || e.message || 'Registration failed';
            this.showMessage(errorMsg,true);
        }
    })
  }
}
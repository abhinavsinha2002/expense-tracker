import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service'; // Adjust path if needed
import { Router, RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { CommonModule } from '@angular/common';

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
    RouterLink
  ],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent{
    user = {username:'',email:'',password:''};

    usernameAvailable: boolean | null = null;
    emailAvailable: boolean | null = null;

    isEmailFormatValid = true;

    hasMinLength = false;
    hasNumber = false;
    hasUpper = false;
    hasSymbol = false;

    passwordVisible = false;

    constructor(private auth:AuthService,private router:Router){}

    checkUsername(){
        if(this.user.username.length<3){
            this.usernameAvailable = null;
            return;
        }
        this.auth.checkAvailability('username',this.user.username).subscribe(res =>{
            this.usernameAvailable = res.available;
        })
    }

    checkEmail(){
        this.emailAvailable = null;
        const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
        this.isEmailFormatValid = emailRegex.test(this.user.email);
        if(!this.isEmailFormatValid){
            return;
        }
        this.auth.checkAvailability('email',this.user.email).subscribe(res=>{
            this.emailAvailable = res.available;
        })
    }

    checkPassword(){
        const p = this.user.password;
        this.hasMinLength = p.length>=6;
        this.hasNumber = /\d/.test(p);
        this.hasUpper = /[A-Z]/.test(p);
        this.hasSymbol = /[!@#$%^&*(),.?":{}|<>]/.test(p);
    }

    get isFormValid():boolean{
        return (this.usernameAvailable === true) &&
                (this.emailAvailable === true) &&
                (this.hasMinLength && this.hasNumber && this.hasUpper && this.hasSymbol);
    }

  register(){
    if(this.isFormValid){
        this.auth.register(this.user).subscribe({
            next:()=>{
                alert('Registration successful! Please check your email to verify.');
                this.router.navigate(['/login']);
            },
            error:(err)=>alert('Error: '+err.message)
        });
    }
    }
}


    
        
        

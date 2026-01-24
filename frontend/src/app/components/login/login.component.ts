import { Component } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { Router, RouterLink } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar'; // Import SnackBar
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-login',
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
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  form: FormGroup;
  resetForm:FormGroup;
  isLoginView = true;
  
  constructor(private fb: FormBuilder, private auth: AuthService, private router: Router,private snackBar:MatSnackBar) { 
    this.form = this.fb.group({
      username: ['', Validators.required],
      password: ['', Validators.required]
    }); 

    this.resetForm = this.fb.group({
      email:['',[Validators.required,Validators.email]]
    });
  }

  private showMessages(message:string,isError=false){
    this.snackBar.open(message,'Close',{
      duration:3000,
      horizontalPosition:'center',
      verticalPosition:'top',
      panelClass:isError?['error-snackbar']:['success-snackbar']
    });
  }

  login() { 
    if (this.form.invalid) return;

    const v = this.form.value; 
    this.auth.login(v.username, v.password).subscribe({
      next: () => {
        this.router.navigate(['/']);
        this.showMessages('Welcome back!');   
      },
      error: () => this.showMessages('Invalid credentials. Please try again.',true)
    }); 
  }

  requestReset(){
    if(this.resetForm.invalid){
      return;
    }
    const email = this.resetForm.value.email;
    this.auth.requestPasswordReset(email).subscribe({
      next:()=>{
        this.showMessages('Reset link sent to your email!');
        this.toggleView();
      },
      error:()=>this.showMessages('Error sending email.',true)
    })
  }

  toggleView(){
    this.isLoginView = !this.isLoginView;
  }
}
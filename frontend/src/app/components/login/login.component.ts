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
import { debounceTime, distinctUntilChanged, switchMap,of } from 'rxjs';

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

  hidePassword = true;
  rememberMe = true;
  capsOn = false;

  isCheckingUser = false;
  userExists: boolean | null = null;
  
  constructor(private fb: FormBuilder, private auth: AuthService, private router: Router,private snackBar:MatSnackBar) { 
    this.form = this.fb.group({
      username: ['', Validators.required],
      password: ['', [Validators.required,Validators.minLength(6)]]
    }); 

    this.resetForm = this.fb.group({
      email:['',[Validators.required,Validators.email]]
    });
  }

  checkCaps(event:KeyboardEvent){
    this.capsOn = event.getModifierState && event.getModifierState('CapsLock');
  }
  ngOnInit() {
    this.setupLiveUsernameCheck();
  }

  setupLiveUsernameCheck(){
    this.form.get('username')?.valueChanges.pipe(
      debounceTime(50),
      distinctUntilChanged(),
      switchMap(username =>{
        if(!username || username.length<3){
          this.userExists = null;
          return of(null);
        }
        this.isCheckingUser = true;
        return this.auth.checkAvailability('username',username);
      })
    ).subscribe({
      next:(res:any)=>{
        this.isCheckingUser = false;
        if(res){
          this.userExists = !res.available;
        }
      },
      error:()=>this.isCheckingUser = false
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

    // Optional: Block login if user definitely doesn't exist
    if (this.userExists === false) {
      this.showMessages('User does not exist.', true);
      return;
    }
    const v = this.form.value; 
    this.auth.login(v.username, v.password,this.rememberMe).subscribe({
      next: () => {
        this.router.navigate(['/main/']);
        this.showMessages('Welcome back!');   
      },
      error: () => this.showMessages('Invalid credentials. Please try again.',true)
    }); 
  }

  togglePasswordVisibility(){
    this.hidePassword = !this.hidePassword;
  }

  toggleRememberMe(e:any){
    this.rememberMe = e.target.checked;
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
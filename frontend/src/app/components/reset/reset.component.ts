import { Component, OnInit, ChangeDetectorRef, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-reset',
  standalone: true,
  imports: [
    CommonModule, 
    ReactiveFormsModule, 
    MatCardModule, 
    MatButtonModule, 
    MatIconModule, 
    MatProgressSpinnerModule, 
    RouterModule
  ],
  templateUrl: './reset.component.html',
  styleUrl: './reset.component.css'
})
export class ResetComponent implements OnInit, OnDestroy{
    status: 'form' | 'loading' | 'success' | 'error' = 'loading';
    resetForm:FormGroup;
    token: string | null = null;
    message = '';
    hidePassword = true;
    countdown = 5;

    hasMinLength = false;
    hasNumber = false;
    hasUpper = false;
    hasSymbol = false;

    private timer: any;

    constructor(
        private route: ActivatedRoute,
        private auth: AuthService,
        private router: Router,
        private fb: FormBuilder,
        private cdr: ChangeDetectorRef
    ){
        this.resetForm = this.fb.group({
            password: ['',[Validators.required,Validators.minLength(6)]]
        });
    }

    ngOnInit(){
        this.token = this.route.snapshot.queryParamMap.get('token');
        if(!this.token){
            this.handleError('Invalid or missing reset token.');
        }
        else{
            this.verifyTokenOnLoad();
        }

        this.resetForm.get('password')?.valueChanges.subscribe(val=>{
            this.checkPassword(val);
        })
    }

    verifyTokenOnLoad(){
        this.auth.validateResetToken(this.token!).subscribe({
            next:(isValid)=>{
                if(isValid){
                    this.status = 'form';
                }
                else{
                    this.handleError('This reset link has expired or has already been used.')
                }
            },
            error:()=>{
                this.handleError('Unable to verify reset link');
            }
        })
    }

    checkPassword(p: string) {
        if (!p) p = '';
        this.hasMinLength = p.length >= 6;
        this.hasNumber = /\d/.test(p);
        this.hasUpper = /[A-Z]/.test(p);
        this.hasSymbol = /[!@#$%^&*(),.?":{}|<>]/.test(p);
    }

    get isPasswordValid(): boolean {
        return this.hasMinLength && this.hasNumber && this.hasUpper && this.hasSymbol;
    }

    togglePasswordVisibility(){
        this.hidePassword = !this.hidePassword;
    }

    onSubmit(){
        if(this.resetForm.invalid || !this.token || !this.isPasswordValid){
            return;
        }
        this.status = 'loading';
        const newPassword = this.resetForm.get('password')?.value;

        setTimeout(() => {
            this.auth.resetPassword(this.token!, newPassword).subscribe({
                next: (res) => {
                    this.status = 'success';
                    this.message = 'Your password has been successfully reset.';
                    this.startRedirectTimer();
                    this.cdr.detectChanges();
                },
                error: (err) => {
                    const msg = err.error || 'The reset link has expired or is invalid.';
                    this.handleError(msg);
                }
            });
        }, 1000);
    }

    private handleError(msg:string){
        this.status = 'error';
        this.message = msg;
        this.cdr.detectChanges();
    }

    startRedirectTimer(){
        this.timer = setInterval(() => {
            this.countdown--;
            if (this.countdown === 0) {
                this.stopTimer();
                this.goToLogin();
            }
            this.cdr.detectChanges();
        }, 1000);
    }

    stopTimer(){
        if(this.timer){
            clearInterval(this.timer);
        }
    }

    goToLogin(){
        this.stopTimer();
        this.router.navigate(['/login']);
    }

    ngOnDestroy(){
        this.stopTimer();
    }

}
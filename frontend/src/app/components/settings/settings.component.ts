import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDividerModule } from '@angular/material/divider';

@Component({
  selector: 'app-settings',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatCardModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    MatSnackBarModule,
    MatDividerModule
  ],
  templateUrl: './settings.component.html',
  styleUrl: './settings.component.css'
})
export class SettingsComponent{
    email = '';
    isLoading = false;

    constructor(
        public auth: AuthService,
        private snackBar: MatSnackBar
    ){}

    requestReset(){
        if(!this.email || !this.email.includes('@')){
            this.showMessage('Please enter a valid email address',true);
            return;
        }

        this.isLoading = true;
        this.auth.requestPasswordReset(this.email).subscribe({
            next:()=>{
                this.showMessage('Reset link sent to '+this.email);
                this.isLoading = false;
                this.email = '';
            },
            error:(err)=>{
                this.showMessage(err.error?.message || 'Failed to send reset link',true);
                this.isLoading = false;
            }
        })
    }

    private showMessage(msg: string, isError = false) {
    this.snackBar.open(msg, 'Close', {
      duration: 3000,
      panelClass: isError ? 'error-snackbar' : 'success-snackbar',
      horizontalPosition: 'right',
      verticalPosition: 'top'
    });
  }
}
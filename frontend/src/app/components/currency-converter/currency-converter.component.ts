import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CurrencyService } from '../../services/currency.service';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';

@Component({
  selector: 'app-currency-converter',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatSnackBarModule
  ],
  templateUrl: './currency-converter.component.html',
  styleUrl: './currency-converter.component.css'
})
export class CurrencyConverterComponent{
    amount = 1;
    fromCurrency = 'USD';
    toCurrency = 'INR';

    result: number | null = null;
    isConverting = false;

    constructor(
        private svc: CurrencyService,
        private snackBar: MatSnackBar
    ){}

    convert(){
        if(this.amount<=0){
            this.showMessage('Please enter a valid amount',true);
            return;
        }
        this.isConverting = true;
        this.result = null;

        const from = this.fromCurrency.toUpperCase();
        const to = this.toCurrency.toUpperCase();

        this.svc.convert(from,to,this.amount).subscribe({
            next:(data:any)=>{
                this.result = data;
                this.isConverting = false;
            },
            error:(e)=>{
                this.showMessage('Conversion failed. Check currency codes.',true);
                this.isConverting = false;
            }
        });
    }

    swapCurrencies(){
        const temp = this.fromCurrency;
        this.fromCurrency = this.toCurrency;
        this.toCurrency = temp;
        this.result = null;
    }

    private showMessage(msg: string, isError = false){
        this.snackBar.open(msg, 'Close', {
            duration: 3000,
            panelClass: isError ? 'error-snackbar' : 'success-snackbar',
            horizontalPosition: 'right',
            verticalPosition: 'top'
        });
    }
}
    
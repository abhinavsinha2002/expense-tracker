import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ExpenseService } from '../../services/expense.service';
import { Expense } from '../../models/expense'; // Ensure you have this model
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { saveAs } from 'file-saver';
import { LoginComponent } from '../login/login.component';
import { ExpenseFormComponent } from '../expense-form/expense-form.component';

//import { ExpenseFormComponent } from '../expense-form/expense-form.component';

@Component({
    selector: 'app-expenses',
    standalone: true,
    imports: [
        CommonModule,
        MatTableModule,
        MatButtonModule,
        MatIconModule,
        MatDialogModule,
        MatSnackBarModule,
        MatTooltipModule
    ],
    templateUrl: './expenses.component.html',
    styleUrl: './expenses.component.css'
})
export class ExpensesComponent implements OnInit {
    expenses: Expense[] = [];
    displayedColumns: string[] = ['date', 'category', 'description', 'amount', 'actions'];

    @ViewChild('fileInput') fileInput!: ElementRef;

    constructor(
        private svc: ExpenseService,
        private dialog: MatDialog,
        private snackBar: MatSnackBar
    ) { }

    ngOnInit(): void {
        this.loadExpenses();
    }

    loadExpenses() {
        this.svc.list().subscribe({
            next: (data) => this.expenses = data,
            error: () => this.showSnack('Failed to load expenses', true)
        });
    }

    openAddExpense() {
        const dialogRef = this.dialog.open(ExpenseFormComponent, {
            width: '600px',
            panelClass: 'custom-dialog-container', // We'll add this class globally later
            disableClose: true
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.loadExpenses();
                this.showSnack('Expense added successfully!');
            }
        });
    }

    deleteExpense(id: number) {
        if (confirm('Are you sure you want to delete this transaction?')) {
            this.svc.delete(id).subscribe({
                next: () => {
                    this.loadExpenses();
                    this.showSnack('Transaction deleted');
                },
                error: () => this.showSnack('Could not delete transaction', true)
            });
        }
    }

    exportCSV() {
        this.svc.exportCsv().subscribe({
            next: (blob) => {
                saveAs(blob, `expenses_${new Date().toISOString().split('T')[0]}.csv`);
                this.showSnack('Export downloaded');
            },
            error: () => this.showSnack('Export failed', true)
        })
    }

    triggerFileInput() {
        this.fileInput.nativeElement.click();
    }

    onFileSelected(event: any) {
        const file = event.target.files[0];
        if (file) {
            this.svc.importCsv(file).subscribe({
                next: () => {
                    this.showSnack('CSV Imported successfully');
                    this.loadExpenses();
                },
                error: () => this.showSnack('Import failed. Check CSV format.', true)
            });
        }
        // Reset input so same file can be selected again if needed
        event.target.value = '';
    }

    private showSnack(msg: string, isError = false) {
        this.snackBar.open(msg, 'Close', {
            duration: 3000,
            panelClass: isError ? 'error-snackbar' : 'success-snackbar',
            horizontalPosition: 'right',
            verticalPosition: 'top'
        });
    }
}
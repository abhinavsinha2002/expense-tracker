import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormArray, FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { ExpenseService } from '../../services/expense.service';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { Expense } from '../../models/expense';
import { MatDividerModule } from '@angular/material/divider'
import {User  } from '../../models/user';

@Component({
  selector: 'app-expense-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatSnackBarModule,
    MatDividerModule
  ],
  templateUrl: './expense-form.component.html',
  styleUrl: './expense-form.component.css'
})
export class ExpenseFormComponent{
    form:FormGroup;
    isSubmitting = false;
    user?: User;

    constructor(
        private fb:FormBuilder,
        private svc:ExpenseService,
        private dialogRef : MatDialogRef<ExpenseFormComponent>,
        private snackBar:MatSnackBar
    ){
        this.form = this.fb.group({
            description: ['',Validators.required],
            amount:[null,[Validators.required,Validators.min(1)]],
            category:[''],
            groupId:[''],
            splits:this.fb.array([])
        });
    }

    get splits(){
        return this.form.get('splits') as FormArray;
    }

    addSplit(){
        const splitGroup = this.fb.group({
            member:['',Validators.required],
            amount:[null,[Validators.required,Validators.min(1)]]
        });
        this.splits.push(splitGroup);
    }

    removeSplit(index:number){
        this.splits.removeAt(index);
    }

    save(){
        if(this.form.invalid || this.isSubmitting){
            return;
        }

        this.isSubmitting = true;
        const v = this.form.value;
        const dto:Expense = {
            owner: this.user as User,
            description: v.description,
            amount:+v.amount,
            category:v.category || undefined,
            groupId:v.groupId? +v.groupId:undefined,
            splits: v.splits.length>0 ? v.splits : undefined
        };

        this.svc.create(dto).subscribe({
            next: ()=>this.dialogRef.close(true),
            error: (e)=>{
                this.isSubmitting = false;
                this.snackBar.open(e.error?.message || 'Error saving','Close',{
                    panelClass:'error-snackbar',
                    duration:3000
                });
            }
        });
    }

    close(){
        this.dialogRef.close(false);
    }

}
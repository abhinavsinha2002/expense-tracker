import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { GroupService } from '../../services/group.service';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDividerModule } from '@angular/material/divider';
import { MatListModule } from '@angular/material/list';
import { Group, Settlement } from '../../models/group';
import { User } from '../../models/user';

@Component({
  selector: 'app-groups',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatSnackBarModule,
    MatDividerModule,
    MatListModule
  ],
  templateUrl: './groups.component.html',
  styleUrl: './groups.component.css'
})
export class GroupComponent{
    form: FormGroup;
    isCreating = false;

    settleGroupId: number | null = null;
    transactions: Settlement[] = [];
    isSettling = false;

    constructor(
        private fb: FormBuilder,
        private svc: GroupService,
        private SnackBar: MatSnackBar
    ){
        this.form = this.fb.group({
            name: ['',Validators.required],
            description:[''],
            members: ['',Validators.required]
        });
    }

    create(){
        if(this.form.invalid){
            return;
        }
        this.isCreating = true;
        const v = this.form.value;

        const members:string[] = v.members
            .split(',')
            .map((s:string)=>s.trim())
            .filter((s:string)=>s.length > 0);

        if(members.length < 1){
            this.showMessage('Please add at least 1 other member',true);
            this.isCreating = false;
            return;
        }

        const groupPayload:Partial<Group> ={
            name:v.name,
            description:v.description,
            members:members.map(name=>({username:name} as User))
        }

        this.svc.create(groupPayload as Group)
            .subscribe({
            next:()=>{
                this.showMessage('Group created successfully!');
                this.form.reset();
                this.isCreating = false;
            },
            error:(e)=>{
                this.showMessage(e.error?.message || 'Failed to create group', true);
                this.isCreating = false;
            }
        });
    }

    settle(){
        if(!this.settleGroupId){
            this.showMessage('Please enter a Group ID',true);
            return;
        }

        this.isSettling = true;
        this.transactions = [];
        this.svc.settle(this.settleGroupId).subscribe({
            next:(data)=>{
                this.transactions = data;
                this.isSettling = false;
                if(data.length == 0){
                    this.showMessage('No debts found. Everyone is settled up!');
                }
            },
            error:(e)=>{
                this.showMessage(e.error?.message || 'Failed to compute settlements',true);
                this.isSettling = false;
            }
        });
    }

    private showMessage(msg:string, isError = false){
        this.SnackBar.open(msg, 'Close', {
            duration: 3000,
            panelClass: isError ? 'error-snackbar' : 'success-snackbar',
            horizontalPosition: 'right',
            verticalPosition: 'top'
        });
    }
}
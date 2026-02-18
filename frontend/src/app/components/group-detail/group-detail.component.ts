import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { GroupService } from '../../services/group.service';
import { ExpenseService } from '../../services/expense.service';
import { MatTabsModule } from '@angular/material/tabs';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatListModule } from '@angular/material/list';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { Group, Settlement } from '../../models/group';
import { Expense } from '../../models/expense';

@Component({
    selector: 'app-group-detail',
    standalone: true,
    imports: [
        CommonModule,
        MatTabsModule,
        MatIconModule,
        MatButtonModule,
        MatCardModule,
        MatListModule,
        MatProgressBarModule
    ],
    templateUrl: './group-detail.component.html',
    styleUrl: './group-detail.component.css'
})
export class GroupDetailComponent implements OnInit {
    groupId!: number;
    group?: Group;
    expenses: Expense[] = [];
    settlements: Settlement[] = [];
    loading = true;

    constructor(
        private route: ActivatedRoute,
        private groupService: GroupService,
        private expenseService: ExpenseService,
        private cdr: ChangeDetectorRef
    ) { }

    ngOnInit() {
        this.route.paramMap.subscribe(params => {
            const id = params.get('id');
            if (id) {
                this.groupId = +id;
                this.loadGroupData();
            }
        })
    }

    loadGroupData() {
        this.loading = true;
        this.groupService.getGroupById(this.groupId).subscribe({
            next: (data) => {
                this.group = data; // Data is now guaranteed
                this.loading = false; // Loader stops here
                this.cdr.detectChanges();
            },
            error: (err) => {
                console.error('Failed to load group', err);
                this.loading = false;
                this.cdr.detectChanges();
            }
        });
        this.expenseService.getExpensesByGroup(this.groupId).subscribe({
            next: (data) => {
                this.expenses = data;
                this.cdr.detectChanges();
            },
            error: (err) => console.error(err)
        });

        this.groupService.settle(this.groupId).subscribe({
            next: (data) => {
                this.settlements = data;
                this.cdr.detectChanges();
            },
            error: (err) => console.error(err)
        });
    }


    getExpenseDate(date: string | number[] | undefined): Date {
        if (!date) return new Date();

        if (Array.isArray(date)) {
            // Java months are 1-12, JS Date months are 0-11
            return new Date(date[0], date[1] - 1, date[2]);
        }

        return new Date(date);
    }

    getGroupImage(name: string = ''): string {
        const lower = name.toLowerCase();
    if (lower.includes('trip') || lower.includes('travel') || lower.includes('vacation') || lower.includes('goa')) 
      return 'https://images.unsplash.com/photo-1476514525535-07fb3b4ae5f1?q=80&w=2070&auto=format&fit=crop';
    if (lower.includes('home') || lower.includes('house') || lower.includes('rent') || lower.includes('flat')) 
      return 'https://images.unsplash.com/photo-1518780664697-55e3ad937233?q=80&w=2065&auto=format&fit=crop';
    if (lower.includes('food') || lower.includes('dinner') || lower.includes('lunch') || lower.includes('party')) 
      return 'https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?q=80&w=2070&auto=format&fit=crop';
    if (lower.includes('office') || lower.includes('work') || lower.includes('team')) 
      return 'https://images.unsplash.com/photo-1522071820081-009f0129c71c?q=80&w=2070&auto=format&fit=crop';
    return 'https://images.unsplash.com/photo-1550684848-fac1c5b4e853?q=80&w=2070&auto=format&fit=crop';
    }
}
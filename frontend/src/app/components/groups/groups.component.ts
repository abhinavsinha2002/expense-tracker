import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { GroupService } from '../../services/group.service';
import { AuthService } from '../../services/auth.service'; // 1. Import Auth Service
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { RouterModule } from '@angular/router';
import { Group } from '../../models/group';
import { User } from '../../models/user'; // Import User model

@Component({
  selector: 'app-groups',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatSnackBarModule,
    MatTooltipModule
  ],
  templateUrl: './groups.component.html',
  styleUrl: './groups.component.css'
})
export class GroupComponent implements OnInit {
  
  groups: Group[] = [];
  showCreateModal = false;
  isLoading = false;
  isSuccessState = false;
  createdInviteLink = '';

  newGroup = {
    name: '',
    description: '',
    currency: 'INR'
  };

  constructor(
    private svc: GroupService,
    public auth: AuthService, // 2. Inject Auth Service
    private snackBar: MatSnackBar,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.loadGroups();
  }

  loadGroups() {
    this.isLoading = true;
    this.svc.getGroups().subscribe({
      next: (data) => {
        this.groups = data;
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.showMessage('Failed to load groups', true);
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  openModal() {
    this.showCreateModal = true;
    document.body.style.overflow = 'hidden'; // Completely locks background scroll
  }

  createGroup() {
    // ... (Keep existing create logic) ...
    if (!this.newGroup.name) {
      this.showMessage('Group name is required', true);
      return;
    }

    this.isLoading = true;
    
    const payload: Partial<Group> = {
      name: this.newGroup.name,
      description: this.newGroup.description,
      currency: this.newGroup.currency
    };

    this.svc.create(payload as Group).subscribe({
      next: (createdGroup) => {
        this.showMessage('Group created successfully!');
        this.groups.push(createdGroup);
        this.createdInviteLink = `${window.location.origin}/join/${createdGroup.inviteToken}`;
        this.isSuccessState = true;
        this.isLoading = false;

        this.cdr.detectChanges();
      },
      error: (e) => {
        this.showMessage(e.error?.message || 'Failed to create group', true);
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  resetForm() {
    this.newGroup = { name: '', description: '', currency: 'INR' };
  }

  closeModal(){
    this.showCreateModal = false;
    this.isSuccessState = false;
    document.body.style.overflow = 'auto';
    this.resetForm();
  }

  copyLink(){
    navigator.clipboard.writeText(this.createdInviteLink).then(() => {
        this.showMessage('Invite link copied to clipboard!');
    });
  }
  // --- UI HELPERS ---

  // 3. New Helper to filter out the logged-in user
  getOtherMembers(members: User[] | undefined): User[] {
    if (!members) return [];
    const currentUserEmail = this.auth.currentUser?.email;
    return members.filter(m => m.email !== currentUserEmail);
  }

  getGroupImage(name: string): string {
     // ... (Keep existing logic) ...
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

  getAvatarColor(name: string): string {
     // ... (Keep existing logic) ...
    let hash = 0;
    for (let i = 0; i < name.length; i++) {
      hash = name.charCodeAt(i) + ((hash << 5) - hash);
    }
    const c = (hash & 0x00FFFFFF).toString(16).toUpperCase();
    return '#' + '00000'.substring(0, 6 - c.length) + c;
  }

  getInitials(name: string): string {
    return name ? name.substring(0, 2).toUpperCase() : '??';
  }

  private showMessage(msg: string, isError = false) {
    this.snackBar.open(msg, 'Close', {
      duration: 3000,
      panelClass: isError ? 'error-snackbar' : 'success-snackbar',
      horizontalPosition: 'center',
      verticalPosition: 'bottom'
    });
  }
}
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { GroupService } from '../../services/group.service';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AuthService } from '../../services/auth.service';

@Component({
    selector: 'app-join-group',
    standalone: true,
    imports: [CommonModule, MatIconModule],
    templateUrl: './join-group.component.html',
    styleUrl: './join-group.component.css'
})
export class JoinGroupComponent implements OnInit {
    token: string = '';
    isLoading: boolean = false;
    error: string = '';
    isLoggedIn: boolean = false;

    constructor(
        private route: ActivatedRoute,
        private svc: GroupService,
        private router: Router,
        private snackBar: MatSnackBar,
        private auth: AuthService,
        private cdr: ChangeDetectorRef
    ) { }

    ngOnInit() {
        this.token = this.route.snapshot.paramMap.get('token') || '';
        if (!this.token) {
            this.error = 'Invalid invitation link.';
        }

        this.isLoggedIn = this.auth.isLoggedIn();
    }

    handleAction() {
        if (!this.token) {
            return;
        }

        if (this.isLoggedIn) {
            this.isLoading = true;
            this.error = '';

            this.svc.joinGroup(this.token).subscribe({
                next: () => {
                    this.snackBar.open('Successfully joined the group!', 'Close', { duration: 3000, panelClass: 'success-snackbar' });
                    this.router.navigate(['/main/groups']);
                },
                error: (err) => {
                    this.isLoading = false;
                    this.error = err.error || 'Failed to join group. Link may be invalid or expired.';
                }
            });
        }
        else {
            localStorage.setItem('pendingInviteToken', this.token);
            this.router.navigate(['/login']);
        }
    }

    goHome() {
        this.router.navigate([this.isLoggedIn ? '/main' : '/login']);
    }

    goToRegister() {
        if (this.token) {
            localStorage.setItem('pendingInviteToken', this.token);
        }
        this.router.navigate(['/register']);
    }

}
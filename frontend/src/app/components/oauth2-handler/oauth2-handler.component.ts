import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
// Update the import path to the correct location of auth.service.ts
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-oauth2-handler',
  standalone: true,
  template: '<p>Redirecting...</p>' // Simple template
})
export class Oauth2HandlerComponent implements OnInit{
    constructor(
        private route: ActivatedRoute,
        private router: Router,
        private authService : AuthService
    ){}

    ngOnInit(){
        const token = this.route.snapshot.queryParamMap.get('token');
        const error = this.route.snapshot.queryParamMap.get('error');

        if(token){
            this.authService.saveToken(token).subscribe({
                next:()=>{
                    this.router.navigate(['/main']);
                },
                error:()=>{
                    this.router.navigate(['/login'])
                }
            });   
        }
        else{
            this.router.navigate(['/login']);
        }
    }
}
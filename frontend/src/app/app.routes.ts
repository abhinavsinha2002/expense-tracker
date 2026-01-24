import { Routes } from '@angular/router';
import { LoginComponent } from './components//login/login.component';
import { RegisterComponent } from './components/register/register.component';
//import { DashboardComponent } from './components/dashboard.component';
//import { ExpenseListComponent } from './components/expense-list.component';
//import { GroupComponent } from './components/group.component';
//import { GroupChatComponent } from './components/group-chat.component';
//import { CurrencyConverterComponent } from './components/currency-converter.component';
//import { SettingsComponent } from './components/settings.component';
//import { authGuard } from './services/auth.guard';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  /*{ path: '', component: DashboardComponent },
  { path: 'expenses', component: ExpenseListComponent },
  { path: 'groups', component: GroupComponent },
  { path: 'chat', component: GroupChatComponent },
  { path: 'currency', component: CurrencyConverterComponent },
  { path: 'settings', component: SettingsComponent },
  
  */
];

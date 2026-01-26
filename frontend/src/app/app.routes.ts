import { Routes } from '@angular/router';
import { LoginComponent } from './components//login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { ExpensesComponent } from './components/expenses/expenses.component';
import { GroupComponent } from './components/groups/groups.component';
import { GroupChatComponent } from './components/group-chat/group-chat.component';
import { CurrencyConverterComponent } from './components/currency-converter/currency-converter.component';
import { SettingsComponent } from './components/settings/settings.component';
//import { authGuard } from './services/auth.guard';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: '', component: DashboardComponent },
  { path: 'expenses', component: ExpensesComponent },
  { path: 'groups', component: GroupComponent },
  { path: 'chat', component: GroupChatComponent },
  { path: 'currency', component: CurrencyConverterComponent },
  { path: 'settings', component: SettingsComponent }
];

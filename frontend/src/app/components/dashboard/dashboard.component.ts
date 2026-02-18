import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ExpenseService } from '../../services/expense.service';
import { ChartData, ChartOptions, ChartConfiguration } from 'chart.js';
import { BaseChartDirective } from 'ng2-charts'; // Import for Standalone Chart
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { Expense } from '../../models/expense';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    BaseChartDirective, // Required for <canvas baseChart>
    MatCardModule,
    MatIconModule,
    MatButtonModule,
    FormsModule
  ],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit {

  selectedPeriod: '1M' | '3M' | '6M' | '1Y' = '1M';
  breakdownType: 'category' | 'group' = 'category';
  expenses: Expense[] = [];
  totalSpent = 0;
  isLoading = false;

  // 1. Line Chart (Trend)
  // 1. Line Chart (Trend)
  public lineChartData: ChartConfiguration<'line'>['data'] = {
    labels: [],
    datasets: [{ data: [], label: 'Daily Spend', borderColor: '#dcb14a', backgroundColor: 'rgba(220, 177, 74, 0.1)', fill: true, pointBackgroundColor: '#dcb14a' }]
  };

  public lineChartOptions: ChartOptions<'line'> = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: { display: false },
      tooltip: { 
        backgroundColor: 'rgba(0,0,0,0.8)', 
        titleColor: '#dcb14a', 
        bodyColor: '#fff',
        borderColor: 'rgba(255,255,255,0.1)',
        borderWidth: 1
      }
    },
    scales: {
      x: { 
        ticks: { color: '#aaa' }, 
        grid: { color: 'rgba(255,255,255,0.05)' } 
      },
      y: { 
        ticks: { color: '#aaa' }, 
        grid: { color: 'rgba(255,255,255,0.05)' } 
      }
    },
    elements: { line: { tension: 0.4 } }
  };

  // 2. Doughnut Chart (Breakdown)
  public doughnutChartData: ChartConfiguration<'doughnut'>['data'] = {
    labels: [],
    datasets: [{ data: [], backgroundColor: [], borderWidth: 0 }]
  };

  public doughnutChartOptions: ChartOptions<'doughnut'> = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: { 
        position: 'right', 
        labels: { color: '#ccc', font: { size: 12 } } 
      }
    },
    cutout: '70%' // Thinner ring
  };

  constructor(private svc: ExpenseService, private cdr: ChangeDetectorRef) { }

  ngOnInit() {
    this.loadData();
  }

  loadData() {
    this.isLoading = true;
    const end = new Date();
    const start = new Date();
    
    if (this.selectedPeriod === '1M') start.setMonth(start.getMonth() - 1);
    if (this.selectedPeriod === '3M') start.setMonth(start.getMonth() - 3);
    if (this.selectedPeriod === '6M') start.setMonth(start.getMonth() - 6);
    if (this.selectedPeriod === '1Y') start.setFullYear(start.getFullYear() - 1);

    const startStr = start.toISOString().split('T')[0];
    const endStr = end.toISOString().split('T')[0];

    this.svc.getAnalytics(startStr, endStr).subscribe({
      next: (data) => {
        this.expenses = data;
        this.calculateStats();
        this.updateCharts();
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Failed to load analytics', err);
        this.isLoading = false;
      }
    });
  }

  calculateStats() {
    this.totalSpent = this.expenses.reduce((sum, e) => sum + e.amount, 0);
  }

  updateCharts() {
    // --- Logic for Line Chart (Group by Date) ---
    const dateMap: any = {};
    this.expenses.forEach(e => {
        let dateKey = '';
        if (Array.isArray(e.date)) {
            const y = e.date[0];
            const m = e.date[1].toString().padStart(2, '0');
            const d = e.date[2].toString().padStart(2, '0');
            dateKey = `${y}-${m}-${d}`;
        } else {
            dateKey = e.date ? e.date.toString() : 'Unknown';
        }
        dateMap[dateKey] = (dateMap[dateKey] || 0) + e.amount;
    });

    const sortedDates = Object.keys(dateMap).sort();
    const dateValues = sortedDates.map(d => dateMap[d]) as number[];

    this.lineChartData = {
      labels: sortedDates,
      datasets: [{
        data: dateValues,
        label: 'Daily Spend',
        borderColor: '#dcb14a',
        backgroundColor: (context) => {
          const ctx = context.chart.ctx;
          const gradient = ctx.createLinearGradient(0, 0, 0, 400);
          gradient.addColorStop(0, 'rgba(220, 177, 74, 0.4)');
          gradient.addColorStop(1, 'rgba(220, 177, 74, 0)');
          return gradient;
        },
        fill: true,
        pointBackgroundColor: '#dcb14a',
        pointBorderColor: '#fff',
        pointHoverBackgroundColor: '#fff',
        pointHoverBorderColor: '#dcb14a'
      }]
    };

    // --- Logic for Doughnut Chart (Group by Category/Group) ---
    const breakdownMap: any = {};
    this.expenses.forEach(e => {
      // FIX: Use 'category' and 'groupName' (matching the DTO/Interface)
      const key = this.breakdownType === 'category' 
          ? (e.category || 'Uncategorized') 
          : (e.groupName || 'Personal');
          
      breakdownMap[key] = (breakdownMap[key] || 0) + e.amount;
    });

    const labels = Object.keys(breakdownMap);
    const data = Object.values(breakdownMap) as number[];

    const colors = [
      '#dcb14a', // Gold
      '#2c3e50', // Dark Blue
      '#e74c3c', // Soft Red
      '#27ae60', // Green
      '#8e44ad', // Purple
      '#e67e22', // Orange
      '#95a5a6'  // Grey
    ];

  this.doughnutChartData = {
      labels: labels,
      datasets: [{
        data: data,
        backgroundColor: colors,
        hoverOffset: 10,
        borderWidth: 0
      }]
    };
  }

  toggleBreakdown(type: 'category' | 'group') {
    this.breakdownType = type;
    this.updateCharts(); // Re-render doughnut only
  }
}
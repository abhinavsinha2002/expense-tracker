import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ExpenseService } from '../../services/expense.service';
import { ChartData, ChartOptions } from 'chart.js';
import { BaseChartDirective } from 'ng2-charts'; // Import for Standalone Chart
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    BaseChartDirective, // Required for <canvas baseChart>
    MatCardModule,
    MatIconModule,
    MatButtonModule
  ],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit{
    totalSpent = 0;
    budget = 20000;
    budgetLeft = 0;

    pieData:ChartData<'pie',number[],string | string[]>={
        labels: [],
        datasets: [{
            data: [],
            backgroundColor: [
                '#3b82f6', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6', '#ec4899', '#6366f1'
            ],
            hoverBackgroundColor: [
                '#2563eb', '#059669', '#d97706', '#dc2626', '#7c3aed', '#db2777', '#4f46e5'
            ],
        borderWidth: 0
        }]
    };
    pieOptions: ChartOptions<'pie'> = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        position: 'right',
        labels: {
          font: { family: 'Poppins', size: 12 },
          usePointStyle: true,
          color: '#64748b' // Slate color for text
        }
      }
    }
  };

  constructor(private svc:ExpenseService){}

  ngOnInit(){
      this.loadData();
  }

  loadData(){
    const currentYear = new Date().getFullYear();
    this.svc.summary(currentYear).subscribe((res:any)=>{
        this.totalSpent = +res.total || 0;
        this.budgetLeft = this.budget - this.totalSpent;

        const categories = res.byCategory || {};
        this.pieData = {
            ...this.pieData,
            labels:Object.keys(categories),
            datasets:[{
                ...this.pieData.datasets[0],
                data:Object.values(categories).map((v:any)=>+v)
            }]
        };
    });
  }
}
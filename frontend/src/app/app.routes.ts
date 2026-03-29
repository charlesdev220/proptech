import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'search',
    pathMatch: 'full'
  },
  {
    path: 'search',
    loadComponent: () => import('./features/property-list/property-list.component').then(m => m.PropertyListComponent)
  },
  {
    path: 'property/:id',
    loadComponent: () => import('./features/property-detail/property-detail.component').then(m => m.PropertyDetailComponent)
  },
  {
    path: 'profile',
    loadComponent: () => import('./features/user-dashboard/user-dashboard').then(m => m.UserProfileComponent)
  }
];

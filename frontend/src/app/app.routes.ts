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
  }
];

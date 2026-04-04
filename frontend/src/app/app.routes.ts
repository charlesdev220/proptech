import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'search',
    pathMatch: 'full'
  },
  {
    path: 'login',
    loadComponent: () => import('./features/auth/login.component').then(m => m.LoginComponent)
  },
  {
    path: 'search',
    loadComponent: () => import('./features/property-list/property-list.component').then(m => m.PropertyListComponent)
  },
  {
    path: 'publish',
    loadComponent: () => import('./features/property-publish/property-publish.component').then(m => m.PropertyPublishComponent)
  },
  {
    path: 'property/:id',
    loadComponent: () => import('./features/property-detail/property-detail.component').then(m => m.PropertyDetailComponent)
  },
  {
    path: 'favorites',
    loadComponent: () => import('./features/favorite-list/favorite-list.component').then(m => m.FavoriteListComponent)
  },
  {
    path: 'profile',
    loadComponent: () => import('./features/user-dashboard/user-dashboard').then(m => m.UserProfileComponent)
  },
  {
    path: 'saved-searches',
    loadComponent: () => import('./features/saved-searches/saved-search-list.component').then(m => m.SavedSearchListComponent)
  },
  {
    path: 'reviews/token/:token',
    loadComponent: () => import('./features/reviews/review-form.component').then(m => m.ReviewFormComponent)
  },
  {
    path: 'my-reviews',
    loadComponent: () => import('./features/reviews/review-management.component').then(m => m.ReviewManagementComponent)
  }
];

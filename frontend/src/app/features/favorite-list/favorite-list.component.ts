import { ChangeDetectionStrategy, Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { FavoritesService } from '../../core/favorites/favorites.service';
import { AuthService } from '../../core/auth/auth.service';
import { PropertyDTO } from '../../../api/model/propertyDTO';

@Component({
  selector: 'app-favorite-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './favorite-list.component.html',
  styleUrls: ['./favorite-list.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FavoriteListComponent implements OnInit {
  private favoritesService = inject(FavoritesService);
  private authService = inject(AuthService);
  private router = inject(Router);

  favorites = signal<PropertyDTO[]>([]);
  loading = signal(false);

  ngOnInit(): void {
    if (!this.authService.isLoggedIn()) {
      this.router.navigate(['/login']);
      return;
    }

    this.loading.set(true);
    this.favoritesService.getFavorites().subscribe({
      next: (data) => {
        this.favorites.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
      }
    });
  }

  removeFavorite(event: Event, propertyId: string): void {
    event.stopPropagation();
    event.preventDefault();
    this.favoritesService.toggle(propertyId);
    this.favorites.set(this.favorites().filter(p => p.id !== propertyId));
  }
}

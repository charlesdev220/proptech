import { inject, Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { PropertyDTO } from '../../../api/model/propertyDTO';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class FavoritesService {
  private http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/api/v1/favorites`;

  private readonly favoriteIds = signal<Set<string>>(new Set());

  isFavorite(id: string | undefined): boolean {
    if (!id) return false;
    return this.favoriteIds().has(id);
  }

  loadFavoriteIds(): void {
    this.http.get<{ ids: string[] }>(`${this.baseUrl}/ids`).subscribe({
      next: (response) => {
        this.favoriteIds.set(new Set(response.ids));
      },
      error: () => {
        // Silently fail — user might not be authenticated
        this.favoriteIds.set(new Set());
      }
    });
  }

  toggle(propertyId: string): void {
    if (this.isFavorite(propertyId)) {
      this.http.delete(`${this.baseUrl}/${propertyId}`).subscribe({
        next: () => {
          const updated = new Set(this.favoriteIds());
          updated.delete(propertyId);
          this.favoriteIds.set(updated);
        }
      });
    } else {
      this.http.post(`${this.baseUrl}/${propertyId}`, null).subscribe({
        next: () => {
          const updated = new Set(this.favoriteIds());
          updated.add(propertyId);
          this.favoriteIds.set(updated);
        }
      });
    }
  }

  getFavorites(): Observable<PropertyDTO[]> {
    return this.http.get<PropertyDTO[]>(this.baseUrl);
  }

  clearFavorites(): void {
    this.favoriteIds.set(new Set());
  }
}

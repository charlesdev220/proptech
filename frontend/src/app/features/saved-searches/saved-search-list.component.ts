import { ChangeDetectionStrategy, Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AlertasService } from '../../../api/api/alertas.service';
import { SavedSearchDTO } from '../../../api/model/savedSearchDTO';

@Component({
  selector: 'app-saved-search-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './saved-search-list.component.html',
  styleUrls: ['./saved-search-list.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SavedSearchListComponent implements OnInit {
  private alertasService = inject(AlertasService);

  savedSearches = signal<SavedSearchDTO[]>([]);
  loading = signal(false);
  confirmingId = signal<string | null>(null);

  ngOnInit(): void {
    this.loadSearches();
  }

  loadSearches(): void {
    this.loading.set(true);
    this.alertasService.savedSearchesGet().subscribe({
      next: (data) => {
        this.savedSearches.set(data);
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }

  toggleActive(search: SavedSearchDTO): void {
    if (!search.id) return;

    const oldStatus = search.active;
    this.savedSearches.update(list =>
      list.map(s => s.id === search.id ? { ...s, active: !oldStatus } : s)
    );

    this.alertasService.savedSearchesIdPatch(search.id, { active: !oldStatus }).subscribe({
      error: () => {
        this.savedSearches.update(list =>
          list.map(s => s.id === search.id ? { ...s, active: oldStatus } : s)
        );
      }
    });
  }

  deleteSearch(id: string): void {
    this.alertasService.savedSearchesIdDelete(id).subscribe({
      next: () => {
        this.savedSearches.update(list => list.filter(s => s.id !== id));
        this.confirmingId.set(null);
      }
    });
  }
}

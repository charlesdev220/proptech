import { ChangeDetectionStrategy, Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { UsuariosService } from '../../../api/api/usuarios.service';
import { ReseasService } from '../../../api/api/reseas.service';
import { PerfilService } from '../../../api/api/perfil.service';
import { ReputationScoreDTO } from '../../../api/model/reputationScoreDTO';
import { ReviewDTO } from '../../../api/model/reviewDTO';

@Component({
  selector: 'app-review-management',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './review-management.component.html',
  styleUrls: ['./review-management.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReviewManagementComponent implements OnInit {
  private readonly usuariosService = inject(UsuariosService);
  private readonly reseasService = inject(ReseasService);
  private readonly perfilService = inject(PerfilService);

  reputationScore = signal<ReputationScoreDTO | null>(null);
  loading = signal(true);
  confirmingDisputeId = signal<string | null>(null);

  ngOnInit(): void {
    this.perfilService.profileGet().subscribe({
      next: (profile) => {
        if (profile.id) {
          this.usuariosService.usersIdReputationGet(profile.id.toString()).subscribe({
            next: (rep) => {
              this.reputationScore.set(rep);
              this.loading.set(false);
            },
            error: () => this.loading.set(false)
          });
        } else {
          this.loading.set(false);
        }
      },
      error: () => this.loading.set(false)
    });
  }

  getDimensionEntries(review: ReviewDTO): { key: string; value: number }[] {
    if (!review.dimensions) return [];
    return Object.entries(review.dimensions).map(([key, value]) => ({ key, value: value as number }));
  }

  dispute(id: string): void {
    this.reseasService.reviewsIdDisputePatch(id).subscribe({
      next: (updated) => {
        this.reputationScore.update(rep => {
          if (!rep) return rep;
          return {
            ...rep,
            reviews: rep.reviews?.map(r => r.id === id ? { ...r, disputed: true } : r) ?? []
          };
        });
        this.confirmingDisputeId.set(null);
      }
    });
  }
}

import { ChangeDetectionStrategy, Component, OnInit, signal, computed, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { InmueblesService } from '../../../api/api/inmuebles.service';
import { PerfilService } from '../../../api/api/perfil.service';
import { PropertyDetailDTO } from '../../../api/model/propertyDetailDTO';
import { UserProfileDTO } from '../../../api/model/userProfileDTO';
import { toSignal } from '@angular/core/rxjs-interop';
import { FavoritesService } from '../../core/favorites/favorites.service';
import { AuthService } from '../../core/auth/auth.service';

@Component({
  selector: 'app-property-detail',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './property-detail.component.html',
  styleUrls: ['./property-detail.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PropertyDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private inmueblesService = inject(InmueblesService);
  private perfilService = inject(PerfilService);
  favoritesService = inject(FavoritesService);
  authService = inject(AuthService);

  property = signal<PropertyDetailDTO | null>(null);
  userProfile = signal<UserProfileDTO | null>(null);

  isCompatible = computed(() => {
    const min = this.property()?.minSolvencyScore;
    const score = this.userProfile()?.solvencyScore;
    if (min === undefined || min === null) return true;
    if (score === undefined || score === null) return false;
    return score >= min;
  });

  hasSolvencyVerified = computed(() => this.userProfile()?.solvencyScore != null);

  ngOnInit(): void {
    if (this.authService.isLoggedIn()) {
      this.favoritesService.loadFavoriteIds();
      this.perfilService.profileGet().subscribe(profile => {
        this.userProfile.set(profile);
      });
    }

    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.inmueblesService.propertiesIdGet(id).subscribe(data => {
        this.property.set(data);
      });
    }
  }

  toggleFavorite(): void {
    const id = this.property()?.id;
    if (id) {
      this.favoritesService.toggle(id);
    }
  }
}

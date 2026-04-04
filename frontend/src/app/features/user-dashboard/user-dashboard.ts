import { ChangeDetectionStrategy, Component, OnInit, signal, computed, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { PerfilService, UserProfileDTO, TrustScoreDTO } from '../../../api';
import { UsuariosService, ReputationScoreDTO, SolvencyResultDTO } from '../../../api';
import { DocumentUploaderComponent } from './components/document-uploader';

@Component({
  selector: 'app-user-profile',
  standalone: true,
  imports: [CommonModule, RouterModule, DocumentUploaderComponent],
  templateUrl: './user-dashboard.html',
  styleUrls: ['./user-dashboard.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UserProfileComponent implements OnInit {
  private readonly perfilService = inject(PerfilService);
  private readonly usuariosService = inject(UsuariosService);

  // State
  protected readonly profile = signal<UserProfileDTO | null>(null);
  protected readonly score = signal<TrustScoreDTO | null>(null);
  protected readonly reputationScore = signal<ReputationScoreDTO | null>(null);
  protected readonly solvencyStatus = signal<'idle' | 'loading' | 'success' | 'error'>('idle');
  protected readonly solvencyResult = signal<SolvencyResultDTO | null>(null);
  protected readonly selectedFiles = signal<File[]>([]);

  // Computed CSS classes based on Reputation Level
  // Computing Profile Completeness (Step 5.1)
  protected readonly profileCompleteness = computed(() => {
    const p = this.profile();
    const s = this.score();
    let percentage = 0;
    const pendingSteps: { label: string; action: string }[] = [];

    // 1. Basic Profile (Name)
    if (p?.name) {
      percentage += 25;
    } else {
      pendingSteps.push({ label: 'Completar nombre', action: '/profile' });
    }

    // 2. Email Verified
    if (p?.isVerified) {
      percentage += 25;
    } else {
      pendingSteps.push({ label: 'Verificar email', action: '#' });
    }

    // 3. Solvencia verificada
    if (p?.solvencyScore != null) {
      percentage += 25;
    } else {
      pendingSteps.push({ label: 'Verificar solvencia (nóminas + contrato)', action: 'solvency' });
    }

    // 4. Reputation / Social
    if (s?.totalScore && s.totalScore > 0) {
      percentage += 25;
    } else {
      pendingSteps.push({ label: 'Ganar primeros puntos de confianza', action: '/properties' });
    }

    return { percentage, pendingSteps };
  });

  protected readonly reputationClass = computed(() => {
    const level = this.score()?.level?.toUpperCase();
    switch (level) {
      case 'PLATINUM':
        return { 
          container: 'bg-gradient-to-br from-slate-400 via-slate-200 to-slate-400 text-slate-900',
          progress: 'bg-slate-900'
        };
      case 'GOLD':
        return { 
          container: 'bg-gradient-to-br from-amber-400 via-amber-200 to-amber-500 text-amber-900',
          progress: 'bg-amber-900'
        };
      case 'SILVER':
        return { 
          container: 'bg-gradient-to-br from-slate-300 via-slate-100 to-slate-400 text-slate-600',
          progress: 'bg-slate-600'
        };
      case 'BRONZE':
      default:
        return { 
          container: 'bg-gradient-to-br from-orange-400 to-red-500 text-white',
          progress: 'bg-white'
        };
    }
  });

  ngOnInit(): void {
    this.fetchData();
  }

  private fetchData(): void {
    this.perfilService.profileGet().subscribe({
      next: (profile: UserProfileDTO) => {
        this.profile.set(profile);
        if (profile.id) {
          this.usuariosService.usersIdReputationGet(profile.id.toString()).subscribe({
            next: (rep: ReputationScoreDTO) => this.reputationScore.set(rep),
            error: () => {}
          });
        }
      },
      error: (err: any) => console.error('Error fetching profile', err)
    });

    this.perfilService.profileTrustScoreGet().subscribe({
      next: (score: TrustScoreDTO) => this.score.set(score),
      error: (err: any) => console.error('Error fetching score', err)
    });
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files) {
      this.selectedFiles.set(Array.from(input.files));
    }
  }

  submitSolvencyVerification(): void {
    const files = this.selectedFiles();
    if (files.length === 0) return;
    this.solvencyStatus.set('loading');
    this.perfilService.profileSolvencyVerificationPost(files).subscribe({
      next: (result: SolvencyResultDTO) => {
        this.solvencyResult.set(result);
        this.solvencyStatus.set('success');
        this.fetchData();
      },
      error: () => this.solvencyStatus.set('error')
    });
  }

  onUploadSuccess() {
    this.fetchData();
  }
}

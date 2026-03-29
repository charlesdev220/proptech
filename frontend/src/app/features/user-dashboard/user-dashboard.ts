import { Component, OnInit, signal, computed, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PerfilService, UserProfileDTO, TrustScoreDTO } from '../../../api';
import { DocumentUploaderComponent } from './components/document-uploader';

@Component({
  selector: 'app-user-profile',
  standalone: true,
  imports: [CommonModule, DocumentUploaderComponent],
  template: `
    <div class="min-h-screen bg-slate-50 p-6">
      <div class="max-w-4xl mx-auto space-y-8">
        
        <!-- Header / Perfil -->
        <header class="bg-white rounded-3xl p-8 shadow-sm flex flex-col md:flex-row items-center gap-8 border border-white">
          <div class="w-32 h-32 rounded-full bg-gradient-to-tr from-blue-500 to-indigo-600 flex items-center justify-center text-white text-4xl font-bold shadow-xl shadow-blue-100">
            {{ profile()?.name?.charAt(0) || 'U' }}
          </div>
          <div class="flex-grow text-center md:text-left">
            <h1 class="text-3xl font-black text-slate-800">{{ profile()?.name }}</h1>
            <p class="text-slate-500 font-medium">{{ profile()?.email }}</p>
            <div class="mt-4 flex flex-wrap gap-2 justify-center md:justify-start">
              <span class="px-3 py-1 bg-green-50 text-green-600 text-xs font-bold rounded-full border border-green-100 uppercase tracking-tighter">Email Verificado</span>
              <span class="px-3 py-1 bg-blue-50 text-blue-600 text-xs font-bold rounded-full border border-blue-100 uppercase tracking-tighter">Cuenta Standard</span>
            </div>
          </div>
          
          <!-- Badge de Reputación (Trust Score) -->
          <div class="shrink-0">
            <div [class]="reputationClass().container" class="rounded-2xl p-6 text-center shadow-2xl relative overflow-hidden transition-all hover:scale-105">
              <div class="absolute -top-10 -right-10 w-32 h-32 bg-white/10 rounded-full blur-3xl"></div>
              <p class="text-[10px] uppercase font-black tracking-[0.2em] opacity-80 mb-1">Reputación Nivel</p>
              <h2 class="text-2xl font-black">{{ score()?.level || 'RECALCULANDO...' }}</h2>
              <div class="mt-3 bg-white/20 rounded-full h-1.5 w-32 mx-auto">
                <div [class]="reputationClass().progress" class="h-full rounded-full transition-all duration-1000" [style.width.%]="(score()?.totalScore || 0) / 10"></div>
              </div>
              <p class="text-[10px] mt-2 font-bold opacity-90">{{ score()?.totalScore || 0 }} pts</p>
            </div>
          </div>
        </header>

        <!-- Secciones del Dashboard -->
        <div class="grid grid-cols-1 md:grid-cols-3 gap-8">
          
          <!-- Factores de Confianza -->
          <section class="md:col-span-2 space-y-6">
            <div class="bg-white rounded-3xl p-8 border border-slate-100 shadow-sm">
              <h3 class="text-xl font-bold text-slate-800 mb-6 flex items-center gap-2">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-blue-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.040L3 14.535a9.233 9.233 0 004.873 14.535 9.233 9.233 0 004.873-14.535l-.127-8.591z" />
                </svg>
                Factores de Confianza
              </h3>
              
              <div class="space-y-4">
                @for (factor of score()?.factors; track factor.name) {
                  <div class="flex items-center justify-between p-4 rounded-2xl bg-slate-50 border border-slate-100 transition-colors hover:bg-slate-100">
                    <div class="flex items-center gap-4">
                      <div class="w-10 h-10 rounded-xl bg-white flex items-center justify-center text-blue-600 shadow-sm font-bold">
                        {{ factor.points }}
                      </div>
                      <div>
                        <p class="text-sm font-bold text-slate-700 capitalize">{{ factor.name }}</p>
                        <p class="text-xs text-slate-500">Contribuye positivamente a tu perfil</p>
                      </div>
                    </div>
                    <div class="w-2 h-2 rounded-full bg-green-500 animate-pulse"></div>
                  </div>
                } @empty {
                  <div class="text-center py-8">
                    <p class="text-slate-400 font-medium italic">Calculando factores...</p>
                  </div>
                }
              </div>
            </div>
          </section>

          <!-- Sidebar Informativo -->
          <aside class="space-y-6">
            <app-document-uploader (uploadSuccess)="onUploadSuccess()"></app-document-uploader>
          </aside>

        </div>
      </div>
    </div>
  `,
  styles: []
})
export class UserProfileComponent implements OnInit {
  private readonly perfilService = inject(PerfilService);

  // State
  protected readonly profile = signal<UserProfileDTO | null>(null);
  protected readonly score = signal<TrustScoreDTO | null>(null);

  // Computed CSS classes based on Reputation Level
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
      next: (profile: UserProfileDTO) => this.profile.set(profile),
      error: (err: any) => console.error('Error fetching profile', err)
    });

    this.perfilService.profileTrustScoreGet().subscribe({
      next: (score: TrustScoreDTO) => this.score.set(score),
      error: (err: any) => console.error('Error fetching score', err)
    });
  }

  onUploadSuccess() {
    // Si se subió con éxito, refrescamos el factor de perfil para recalcular score.
    this.fetchData();
  }
}

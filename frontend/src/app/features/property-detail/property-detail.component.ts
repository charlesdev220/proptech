import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { InmueblesService } from '../../../api/api/inmuebles.service';
import { PropertyDetailDTO } from '../../../api/model/propertyDetailDTO';
import { toSignal } from '@angular/core/rxjs-interop';
import { FavoritesService } from '../../core/favorites/favorites.service';
import { AuthService } from '../../core/auth/auth.service';

@Component({
  selector: 'app-property-detail',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="min-h-screen bg-gray-50 pb-12">
      <!-- Top Gallery / Header -->
      <div class="grid grid-cols-1 md:grid-cols-2 h-[50vh] gap-1 bg-white overflow-hidden shadow-sm">
        @if (property()?.mediaPreviews?.length) {
          <div class="h-full relative overflow-hidden group">
            <img [src]="property()?.mediaPreviews?.[0]?.url" 
                 class="w-full h-full object-cover transition-transform duration-700 group-hover:scale-105" 
                 alt="Main property image">
          </div>
          <div class="hidden md:grid grid-cols-2 grid-rows-2 h-full gap-1">
            @for (media of property()?.mediaPreviews?.slice(1, 5); track $index) {
              <div class="relative overflow-hidden group">
                <img [src]="media.url" class="w-full h-full object-cover transition-transform duration-500 group-hover:scale-110" alt="Gallery">
              </div>
            }
          </div>
        } @else {
          <div class="col-span-2 flex items-center justify-center bg-gray-200">
             <span class="text-gray-400">Sin imágenes disponibles</span>
          </div>
        }
      </div>

      <!-- Content -->
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 mt-8">
        <div class="flex flex-col lg:flex-row gap-8">
          
          <!-- Left Column (Main Info) -->
          <div class="flex-1 space-y-8 bg-white p-8 rounded-2xl shadow-sm border border-gray-100">
            <!-- Header Title & Price -->
            <div class="flex justify-between items-start border-b pb-6 border-gray-100">
              <div>
                <span class="inline-flex items-center px-3 py-1 rounded-full text-xs font-medium bg-blue-100 text-blue-800 mb-2 uppercase tracking-wider">
                  {{ property()?.type === 'SALE' ? 'En Venta' : 'Alquiler' }}
                </span>
                <h1 class="text-4xl font-bold text-gray-900 tracking-tight">{{ property()?.title }}</h1>
                <p class="text-lg text-gray-500 mt-2 flex items-center">
                  <svg class="w-5 h-5 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"/><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"/></svg>
                  {{ property()?.location?.address }}
                </p>
              </div>
              <div class="text-right">
                <p class="text-h1 font-black text-blue-600">{{ property()?.price | currency:'EUR':'symbol':'1.0-0' }}</p>
                @if (property()?.type === 'RENT') {
                  <p class="text-sm text-gray-500">/ mes</p>
                }
              </div>
            </div>

            <!-- Basic Features Cards -->
            <div class="grid grid-cols-2 sm:grid-cols-4 gap-4 py-6 border-b border-gray-100">
              <div class="flex flex-col items-center p-4 bg-gray-50 rounded-xl">
                 <span class="text-gray-400 text-sm mb-1 uppercase font-semibold">Hab.</span>
                 <span class="text-2xl font-bold text-gray-800">{{ property()?.features?.rooms || '-' }}</span>
              </div>
              <div class="flex flex-col items-center p-4 bg-gray-50 rounded-xl">
                 <span class="text-gray-400 text-sm mb-1 uppercase font-semibold">Baños</span>
                 <span class="text-2xl font-bold text-gray-800">{{ property()?.features?.bathrooms || '-' }}</span>
              </div>
              <div class="flex flex-col items-center p-4 bg-gray-50 rounded-xl">
                 <span class="text-gray-400 text-sm mb-1 uppercase font-semibold">Sup.</span>
                 <span class="text-2xl font-bold text-gray-800">{{ property()?.features?.surface }} <span class="text-sm">m²</span></span>
              </div>
              <div class="flex flex-col items-center p-4 bg-gray-50 rounded-xl">
                 <span class="text-gray-400 text-sm mb-1 uppercase font-semibold">Eficiencia</span>
                 <span class="text-2xl font-bold text-green-600">{{ property()?.features?.energyCertificate || 'N/A' }}</span>
              </div>
            </div>

            <!-- Description -->
            <div>
              <h3 class="text-xl font-bold text-gray-900 mb-4">Descripción del Inmueble</h3>
              <p class="text-gray-600 leading-relaxed whitespace-pre-line">{{ property()?.description }}</p>
            </div>

            <!-- Features List -->
             <div>
              <h3 class="text-xl font-bold text-gray-900 mb-4">Equipamiento y Extras</h3>
              <div class="grid grid-cols-1 md:grid-cols-2 gap-y-3">
                 <div class="flex items-center text-gray-700">
                   <svg class="w-5 h-5 text-green-500 mr-2" fill="currentColor" viewBox="0 0 20 20"><path fill-rule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clip-rule="evenodd"/></svg>
                   Elevador: {{ property()?.features?.hasElevator ? 'Sí' : 'No' }}
                 </div>
                 <div class="flex items-center text-gray-700">
                   <svg class="w-5 h-5 text-green-500 mr-2" fill="currentColor" viewBox="0 0 20 20"><path fill-rule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clip-rule="evenodd"/></svg>
                   Parking: {{ property()?.features?.hasParking ? 'Sí' : 'No' }}
                 </div>
              </div>
            </div>
          </div>

          <!-- Right Column (Owner & Contact) -->
          <div class="lg:w-96 space-y-6">
            <!-- Owner Card -->
            <div class="bg-white p-6 rounded-2xl shadow-sm border border-gray-100 flex flex-col items-center">
              <div class="w-24 h-24 bg-gradient-to-br from-blue-400 to-indigo-600 rounded-full flex items-center justify-center text-white text-3xl font-bold mb-4 shadow-lg ring-4 ring-white">
                {{ property()?.owner?.name?.[0] || 'U' }}
              </div>
              <h3 class="text-xl font-bold text-gray-900">{{ property()?.owner?.name }}</h3>
              <p class="text-sm text-gray-500 mb-4 italic">Anunciante verificado</p>
              
              <!-- Trust Score Badge -->
              <div class="w-full bg-gray-50 px-4 py-3 rounded-xl mb-6 flex items-center justify-between">
                <span class="text-xs font-bold text-gray-400 uppercase">Trust Score</span>
                <div class="flex items-center">
                  <span class="text-lg font-black text-indigo-600 mr-1">{{ property()?.owner?.trustScore }}%</span>
                  <div class="w-2 h-2 rounded-full bg-green-500 animate-pulse"></div>
                </div>
              </div>

              <button class="w-full bg-indigo-600 hover:bg-indigo-700 text-white font-bold py-4 rounded-xl shadow-lg transition-all duration-300 transform hover:-translate-y-1 active:scale-95 mb-3 flex items-center justify-center gap-2">
                <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z"/></svg>
                Contactar ahora
              </button>
              @if (authService.isLoggedIn()) {
                <button (click)="toggleFavorite()"
                  [ngClass]="favoritesService.isFavorite(property()?.id)
                    ? 'w-full font-bold py-4 rounded-xl transition-all duration-200 flex items-center justify-center gap-2 bg-red-50 text-red-600 border border-red-200 hover:bg-red-100'
                    : 'w-full font-bold py-4 rounded-xl transition-all duration-200 flex items-center justify-center gap-2 bg-white text-gray-700 border border-gray-200 hover:bg-gray-50'">
                  <span class="text-xl">{{ favoritesService.isFavorite(property()?.id) ? '\u2764\uFE0F' : '\u2661' }}</span>
                  {{ favoritesService.isFavorite(property()?.id) ? 'Guardado en favoritos' : 'A\u00F1adir a favoritos' }}
                </button>
              }
            </div>

            <!-- Safety Banner -->
            <div class="bg-indigo-900 p-6 rounded-2xl text-white shadow-xl relative overflow-hidden">
               <div class="relative z-10">
                 <h4 class="font-bold mb-2">Reserva Segura</h4>
                 <p class="text-xs text-indigo-200 leading-snug">Este propietario utiliza el sistema de Trust Scoring de PropTech para garantizar transacciones sin riesgos.</p>
               </div>
               <svg class="absolute -right-8 -bottom-8 w-32 h-32 text-indigo-800 opacity-20" fill="currentColor" viewBox="0 0 20 20"><path fill-rule="evenodd" d="M2.166 4.9L10 .155 17.834 4.9a2 2 0 011.166 1.8v3.3c0 5.225-3.34 9.67-8 11.317a11.536 11.536 0 01-8-11.317V6.7a2 2 0 011.166-1.8zm7.834 1.5l1.414 1.414L10 9.242 8.586 7.828 10 6.414z" clip-rule="evenodd"/></svg>
            </div>
          </div>

        </div>
      </div>
    </div>
  `,
  styles: [`
    :host { display: block; }
    .text-h1 { line-height: 1.1; }
  `]
})
export class PropertyDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private inmueblesService = inject(InmueblesService);
  favoritesService = inject(FavoritesService);
  authService = inject(AuthService);

  property = signal<PropertyDetailDTO | null>(null);

  ngOnInit(): void {
    if (this.authService.isLoggedIn()) {
      this.favoritesService.loadFavoriteIds();
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

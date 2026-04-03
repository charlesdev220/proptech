import { Component, inject, signal, OnDestroy } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { LeafletModule } from '@asymmetrik/ngx-leaflet';
import * as L from 'leaflet';
import { environment } from '../../../environments/environment';

// Fix Leaflet default marker icons lost by bundler
delete (L.Icon.Default.prototype as any)._getIconUrl;
L.Icon.Default.mergeOptions({
  iconUrl:       'assets/leaflet/marker-icon.png',
  iconRetinaUrl: 'assets/leaflet/marker-icon-2x.png',
  shadowUrl:     'assets/leaflet/marker-shadow.png',
});

@Component({
  selector: 'app-property-publish',
  standalone: true,
  imports: [ReactiveFormsModule, LeafletModule],
  template: `
    <div class="max-w-3xl mx-auto px-4 py-8 space-y-6">
      <div>
        <h2 class="text-2xl font-black text-slate-800 tracking-tight">Publicar Inmueble</h2>
        <p class="text-slate-500 text-sm mt-1">Completa todos los campos para publicar tu anuncio.</p>
      </div>

      <form [formGroup]="form" (ngSubmit)="onSubmit()" class="space-y-5">

        <!-- Información básica -->
        <div class="bg-white rounded-2xl shadow-sm border border-slate-100 p-6 space-y-4">
          <h3 class="text-sm font-bold text-slate-500 uppercase tracking-widest">Información básica</h3>

          <div>
            <label class="block text-sm font-semibold text-slate-700 mb-1">Título *</label>
            <input type="text" formControlName="title" placeholder="Ej: Ático luminoso en Malasaña"
                   class="w-full px-4 py-2.5 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm transition-all">
          </div>

          <div>
            <label class="block text-sm font-semibold text-slate-700 mb-1">Descripción</label>
            <textarea formControlName="description" rows="3"
                      placeholder="Describe el inmueble: distribución, estado, puntos fuertes..."
                      class="w-full px-4 py-2.5 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm transition-all resize-none"></textarea>
          </div>

          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-semibold text-slate-700 mb-1">Precio (€) *</label>
              <input type="number" formControlName="price" placeholder="1200" min="0"
                     class="w-full px-4 py-2.5 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm transition-all">
            </div>
            <div>
              <label class="block text-sm font-semibold text-slate-700 mb-1">Tipo *</label>
              <select formControlName="type"
                      class="w-full px-4 py-2.5 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm bg-white transition-all">
                <option value="RENT">Alquiler</option>
                <option value="SALE">Venta</option>
              </select>
            </div>
          </div>
        </div>

        <!-- Ubicación -->
        <div class="bg-white rounded-2xl shadow-sm border border-slate-100 p-6 space-y-4">
          <h3 class="text-sm font-bold text-slate-500 uppercase tracking-widest">Ubicación *</h3>

          <div class="flex gap-2">
            <input type="text" [value]="addressInput()"
                   (input)="addressInput.set($any($event.target).value)"
                   (keydown.enter)="geocodeAddress($event)"
                   placeholder="Escribe la dirección y pulsa Enter, o haz clic en el mapa..."
                   class="flex-1 px-4 py-2.5 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm transition-all">
            <button type="button" (click)="useGeolocation()"
                    title="Usar mi ubicación actual"
                    class="px-4 py-2.5 bg-slate-100 hover:bg-blue-50 hover:text-blue-700 text-slate-600 rounded-xl text-sm font-semibold transition-colors flex items-center gap-1.5 whitespace-nowrap">
              📍 Mi ubicación
            </button>
          </div>

          @if (geoError()) {
            <p class="text-red-500 text-xs bg-red-50 px-3 py-2 rounded-lg">{{ geoError() }}</p>
          }

          <div class="h-72 rounded-xl overflow-hidden border border-slate-200 z-0"
               leaflet
               [leafletOptions]="mapOptions"
               (leafletMapReady)="onMapReady($event)">
          </div>

          @if (selectedLat() !== null) {
            <p class="text-xs text-slate-400 font-mono bg-slate-50 px-3 py-1.5 rounded-lg">
              📌 {{ selectedLat()!.toFixed(6) }}, {{ selectedLng()!.toFixed(6) }}
            </p>
          } @else {
            <p class="text-xs text-amber-600 bg-amber-50 px-3 py-1.5 rounded-lg">
              Haz clic en el mapa o usa "Mi ubicación" para marcar la dirección.
            </p>
          }
        </div>

        <!-- Características -->
        <div class="bg-white rounded-2xl shadow-sm border border-slate-100 p-6 space-y-4">
          <h3 class="text-sm font-bold text-slate-500 uppercase tracking-widest">Características</h3>

          <div class="grid grid-cols-3 gap-4">
            <div>
              <label class="block text-sm font-semibold text-slate-700 mb-1">Habitaciones</label>
              <input type="number" formControlName="rooms" min="0" max="20" placeholder="2"
                     class="w-full px-4 py-2.5 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm transition-all">
            </div>
            <div>
              <label class="block text-sm font-semibold text-slate-700 mb-1">Baños</label>
              <input type="number" formControlName="bathrooms" min="0" max="10" placeholder="1"
                     class="w-full px-4 py-2.5 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm transition-all">
            </div>
            <div>
              <label class="block text-sm font-semibold text-slate-700 mb-1">Superficie (m²)</label>
              <input type="number" formControlName="surface" min="0" placeholder="85"
                     class="w-full px-4 py-2.5 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm transition-all">
            </div>
          </div>

          <div class="grid grid-cols-3 gap-4 items-end">
            <div>
              <label class="block text-sm font-semibold text-slate-700 mb-1">Certif. Energético</label>
              <select formControlName="energyCertificate"
                      class="w-full px-4 py-2.5 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm bg-white transition-all">
                <option value="">Sin especificar</option>
                @for (cert of energyCerts; track cert) {
                  <option [value]="cert">{{ cert }}</option>
                }
              </select>
            </div>
            <label class="flex items-center gap-3 cursor-pointer pb-0.5">
              <input type="checkbox" formControlName="hasElevator" class="w-4 h-4 accent-blue-600">
              <span class="text-sm font-semibold text-slate-700">Ascensor</span>
            </label>
            <label class="flex items-center gap-3 cursor-pointer pb-0.5">
              <input type="checkbox" formControlName="hasParking" class="w-4 h-4 accent-blue-600">
              <span class="text-sm font-semibold text-slate-700">Parking</span>
            </label>
          </div>
        </div>

        <!-- Imágenes y Vídeo -->
        <div class="bg-white rounded-2xl shadow-sm border border-slate-100 p-6 space-y-3">
          <h3 class="text-sm font-bold text-slate-500 uppercase tracking-widest">Imágenes / Vídeo</h3>
          <input type="file" multiple (change)="onFileSelected($event)" accept="image/*,video/mp4"
                 class="block w-full text-sm text-slate-600
                        file:mr-4 file:py-2 file:px-4 file:rounded-xl file:border-0
                        file:bg-blue-50 file:text-blue-700 file:font-semibold
                        hover:file:bg-blue-100 transition-all">
          <p class="text-xs text-slate-400">Máx. 5 archivos · Imágenes comprimidas automáticamente (HD 70%) · Vídeos &lt; 15 MB</p>
          @if (errorMessage()) {
            <p class="text-red-500 text-xs bg-red-50 px-3 py-2 rounded-lg">{{ errorMessage() }}</p>
          }
          @if (selectedFiles().length > 0) {
            <p class="text-xs text-green-600 font-medium">{{ selectedFiles().length }} archivo(s) seleccionado(s)</p>
          }
        </div>

        <!-- Submit -->
        <button type="submit"
                [disabled]="form.invalid || isUploading() || selectedLat() === null"
                class="w-full bg-blue-600 text-white py-3.5 rounded-xl font-bold text-sm
                       hover:bg-blue-700 disabled:opacity-50 transition-all
                       active:scale-[0.99] shadow-lg shadow-blue-200">
          {{ isUploading() ? 'Publicando...' : 'Publicar Inmueble' }}
        </button>

      </form>
    </div>
  `
})
export class PropertyPublishComponent implements OnDestroy {
  private fb = inject(FormBuilder);
  private http = inject(HttpClient);
  private router = inject(Router);

  private map!: L.Map;
  private marker: L.Marker | null = null;

  readonly energyCerts = ['A', 'B', 'C', 'D', 'E', 'F', 'G'];

  selectedLat = signal<number | null>(null);
  selectedLng = signal<number | null>(null);
  addressInput = signal('');
  geoError = signal<string | null>(null);
  selectedFiles = signal<File[]>([]);
  isUploading = signal(false);
  errorMessage = signal<string | null>(null);

  mapOptions: L.MapOptions = {
    layers: [
      L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        maxZoom: 18,
        attribution: '© <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
      })
    ],
    zoom: 12,
    center: L.latLng(40.4168, -3.7038)
  };

  form = this.fb.group({
    title:             ['', Validators.required],
    description:       [''],
    price:             [null as number | null, [Validators.required, Validators.min(0)]],
    type:              ['RENT', Validators.required],
    rooms:             [null as number | null],
    bathrooms:         [null as number | null],
    surface:           [null as number | null],
    hasElevator:       [false],
    hasParking:        [false],
    energyCertificate: ['']
  });

  onMapReady(map: L.Map): void {
    this.map = map;
    map.on('click', (e: L.LeafletMouseEvent) => this.placeMarker(e.latlng));
  }

  private placeMarker(latlng: L.LatLng): void {
    if (this.marker) this.marker.remove();
    this.marker = L.marker(latlng).addTo(this.map);
    this.selectedLat.set(latlng.lat);
    this.selectedLng.set(latlng.lng);
    this.geoError.set(null);
    // Reverse geocoding para rellenar la dirección
    this.http.get<{ display_name?: string }>(
      `https://nominatim.openstreetmap.org/reverse?lat=${latlng.lat}&lon=${latlng.lng}&format=json`
    ).subscribe({ next: res => { if (res.display_name) this.addressInput.set(res.display_name); } });
  }

  useGeolocation(): void {
    this.geoError.set(null);
    if (!navigator.geolocation) {
      this.geoError.set('Tu navegador no soporta geolocalización.');
      return;
    }
    navigator.geolocation.getCurrentPosition(
      pos => {
        const latlng = L.latLng(pos.coords.latitude, pos.coords.longitude);
        this.map.setView(latlng, 15);
        this.placeMarker(latlng);
      },
      () => this.geoError.set('No se pudo obtener tu ubicación. Revisa los permisos del navegador.')
    );
  }

  geocodeAddress(event: Event): void {
    event.preventDefault();
    const query = this.addressInput().trim();
    if (!query) return;
    this.geoError.set(null);
    this.http.get<Array<{ lat: string; lon: string }>>(
      `https://nominatim.openstreetmap.org/search?q=${encodeURIComponent(query)}&format=json&limit=1`
    ).subscribe({
      next: results => {
        if (results.length > 0) {
          const latlng = L.latLng(parseFloat(results[0].lat), parseFloat(results[0].lon));
          this.map.setView(latlng, 15);
          this.placeMarker(latlng);
        } else {
          this.geoError.set('Dirección no encontrada. Prueba con otra más específica.');
        }
      }
    });
  }

  onFileSelected(event: Event): void {
    this.errorMessage.set(null);
    const files = Array.from((event.target as HTMLInputElement).files ?? []);
    if (files.length > 5) { this.errorMessage.set('Máximo 5 archivos permitidos.'); return; }
    for (const f of files) {
      if (f.type.startsWith('video/') && f.size > 15 * 1024 * 1024) {
        this.errorMessage.set('El vídeo excede los 15 MB.'); return;
      }
    }
    this.selectedFiles.set(files);
  }

  private async compressImage(file: File): Promise<Blob> {
    if (!file.type.startsWith('image/')) return file;
    return new Promise(resolve => {
      const reader = new FileReader();
      reader.readAsDataURL(file);
      reader.onload = e => {
        const img = new Image();
        img.src = (e.target as FileReader).result as string;
        img.onload = () => {
          const canvas = document.createElement('canvas');
          const maxDim = 1280;
          let { width, height } = img;
          if (width > height && width > maxDim) { height = Math.round(height * maxDim / width); width = maxDim; }
          else if (height > maxDim) { width = Math.round(width * maxDim / height); height = maxDim; }
          canvas.width = width;
          canvas.height = height;
          canvas.getContext('2d')?.drawImage(img, 0, 0, width, height);
          canvas.toBlob(blob => resolve(blob ?? file), 'image/jpeg', 0.70);
        };
      };
    });
  }

  async onSubmit(): Promise<void> {
    if (this.form.invalid || this.selectedLat() === null) return;
    this.isUploading.set(true);
    this.errorMessage.set(null);
    const base = environment.apiUrl;
    const mediaIds: string[] = [];

    try {
      for (const file of this.selectedFiles()) {
        const blob = await this.compressImage(file);
        const fd = new FormData();
        fd.append('file', blob, file.name);
        const res = await this.http.post<{ id?: string }>(`${base}/api/v1/media/upload`, fd).toPromise();
        if (res?.id) mediaIds.push(res.id);
      }

      const v = this.form.value;
      const payload = {
        title:       v.title,
        description: v.description ?? undefined,
        price:       v.price,
        type:        v.type,
        location: {
          latitude:  this.selectedLat(),
          longitude: this.selectedLng(),
          address:   this.addressInput()
        },
        mediaIds,
        features: {
          rooms:             v.rooms ?? undefined,
          bathrooms:         v.bathrooms ?? undefined,
          surface:           v.surface ?? undefined,
          hasElevator:       v.hasElevator,
          hasParking:        v.hasParking,
          energyCertificate: v.energyCertificate || undefined
        }
      };

      await this.http.post(`${base}/api/v1/properties`, payload).toPromise();
      this.router.navigate(['/search']);
    } catch {
      this.errorMessage.set('Error al publicar el inmueble. Inténtalo de nuevo.');
    } finally {
      this.isUploading.set(false);
    }
  }

  ngOnDestroy(): void {
    this.map?.remove();
  }
}

import { ChangeDetectionStrategy, Component, inject, signal, OnDestroy } from '@angular/core';
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
  templateUrl: './property-publish.component.html',
  styleUrls: ['./property-publish.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
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

import { Component, OnInit, signal, effect, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { InmueblesService } from '../../../api/api/inmuebles.service';
import { PropertyDTO } from '../../../api/model/propertyDTO';
import { FavoritesService } from '../../core/favorites/favorites.service';
import { AuthService } from '../../core/auth/auth.service';
import * as L from 'leaflet';
import { LeafletModule } from '@asymmetrik/ngx-leaflet';

// Fix Leaflet default marker icons lost by bundler
delete (L.Icon.Default.prototype as any)._getIconUrl;
L.Icon.Default.mergeOptions({
  iconUrl:       'assets/leaflet/marker-icon.png',
  iconRetinaUrl: 'assets/leaflet/marker-icon-2x.png',
  shadowUrl:     'assets/leaflet/marker-shadow.png',
});
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-property-list',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, LeafletModule],
  templateUrl: './property-list.component.html',
  styleUrl: './property-list.component.css'
})
export class PropertyListComponent implements OnInit {
  private propertyService = inject(InmueblesService);
  private http = inject(HttpClient);
  favoritesService = inject(FavoritesService);
  private authService = inject(AuthService);

  // State (Signals)
  properties = signal<PropertyDTO[]>([]);
  totalElements = signal(0);
  loading = signal(false);

  // Filters (Signals)
  minPrice = signal<number | undefined>(undefined);
  maxPrice = signal<number | undefined>(undefined);
  lat = signal<number | undefined>(40.4168); // Madrid center
  lng = signal<number | undefined>(-3.7038);
  radius = signal<number | undefined>(5000); // 5km default
  page = signal(0);
  size = signal(20);

  // Municipality search
  municipalityQuery = '';
  municipalityError = signal<string | null>(null);

  // Map Instance
  map!: L.Map;
  markers: L.Marker[] = [];
  options: L.MapOptions = {
    layers: [
      L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        maxZoom: 18,
        attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
      })
    ],
    zoom: 12,
    center: L.latLng(40.4168, -3.7038)
  };

  constructor() {
    // Automatically fetch data when filters change
    effect(() => {
      // Track signals
      this.minPrice();
      this.maxPrice();
      this.lat();
      this.lng();
      this.radius();
      this.page();
      this.size();
      
      this.fetchProperties();
    }, { allowSignalWrites: true });
  }

  ngOnInit(): void {
    if (this.authService.isLoggedIn()) {
      this.favoritesService.loadFavoriteIds();
    }
  }

  onMapReady(map: L.Map): void {
    this.map = map;
    
    this.map.on('moveend', () => {
      const center = this.map.getCenter();
      this.lat.set(center.lat);
      this.lng.set(center.lng);
    });
  }

  fetchProperties(): void {
    const latVal = this.lat();
    const lngVal = this.lng();
    const radiusVal = this.radius();

    if (latVal === undefined || lngVal === undefined) return;

    this.loading.set(true);
    this.propertyService.propertiesGet(
      this.page(),
      this.size(),
      this.minPrice(),
      this.maxPrice(),
      latVal,
      lngVal,
      radiusVal
    ).subscribe({
      next: (response: any) => {
        this.properties.set(response.content || []);
        this.totalElements.set(response.totalElements || 0);
        this.updateMapMarkers(response.content || []);
        this.loading.set(false);
      },
      error: (error: any) => {
        console.error('Error fetching properties', error);
        this.loading.set(false);
      }
    });
  }

  updateMapMarkers(props: PropertyDTO[]): void {
    // Clear old markers
    this.markers.forEach(m => m.remove());
    this.markers = [];

    const redIcon = L.divIcon({
      html: `<div style="background:#ef4444;width:14px;height:14px;border-radius:50%;border:2px solid white;box-shadow:0 2px 4px rgba(0,0,0,0.3)"></div>`,
      className: '',
      iconSize: [14, 14],
      iconAnchor: [7, 7]
    });

    // Add new markers
    props.forEach(p => {
      if (p.location?.latitude && p.location?.longitude) {
        const icon = this.favoritesService.isFavorite(p.id)
          ? redIcon
          : new L.Icon.Default();
        const marker = L.marker([p.location.latitude, p.location.longitude], { icon })
          .bindPopup(`<h5>${p.title}</h5><p>${p.price}\u20AC</p>`)
          .addTo(this.map);
        this.markers.push(marker);
      }
    });
  }

  onFilterChange(): void {
    // Handled by effects automatically when signals change
    this.page.set(0);
  }

  searchByMunicipality(event: Event): void {
    event.preventDefault();
    const query = this.municipalityQuery.trim();
    if (!query) return;
    this.municipalityError.set(null);
    this.http.get<Array<{ lat: string; lon: string; boundingbox: string[] }>>(
      `https://nominatim.openstreetmap.org/search?q=${encodeURIComponent(query)}&format=json&limit=1`
    ).subscribe({
      next: results => {
        if (results.length === 0) {
          this.municipalityError.set('Municipio no encontrado.');
          return;
        }
        const { lat, lon, boundingbox } = results[0];
        const centerLat = parseFloat(lat);
        const centerLng = parseFloat(lon);
        // Calcular radio desde el bounding box
        const latSpan = Math.abs(parseFloat(boundingbox[1]) - parseFloat(boundingbox[0]));
        const lngSpan = Math.abs(parseFloat(boundingbox[3]) - parseFloat(boundingbox[2]));
        const radiusMeters = Math.min(Math.max((Math.max(latSpan, lngSpan) / 2) * 111320, 1000), 50000);

        this.lat.set(centerLat);
        this.lng.set(centerLng);
        this.radius.set(Math.round(radiusMeters));
        this.page.set(0);
        this.map?.setView(L.latLng(centerLat, centerLng), 13);
      },
      error: () => this.municipalityError.set('Error al buscar el municipio.')
    });
  }
}

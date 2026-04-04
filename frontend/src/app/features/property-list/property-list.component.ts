import { ChangeDetectionStrategy, Component, OnInit, signal, computed, effect, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { InmueblesService } from '../../../api/api/inmuebles.service';
import { BarriosService } from '../../../api/api/barrios.service';
import { PropertyDTO } from '../../../api/model/propertyDTO';
import { NeighborhoodDTO } from '../../../api/model/neighborhoodDTO';
import { PropertySearchRequest } from '../../../api/model/propertySearchRequest';
import { FavoritesService } from '../../core/favorites/favorites.service';
import { AuthService } from '../../core/auth/auth.service';
import { IsochroneService } from '../../core/isochrone/isochrone.service';
import { GeoJsonGeometry } from '../../../api/model/geoJsonGeometry';
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
  styleUrl: './property-list.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PropertyListComponent implements OnInit {
  private propertyService = inject(InmueblesService);
  private barriosService = inject(BarriosService);
  private http = inject(HttpClient);
  private isochroneService = inject(IsochroneService);
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

  // Search mode signals
  searchMode = signal<'radius' | 'draw' | 'neighborhoods' | 'isochrone'>('radius');
  currentPolygon = signal<GeoJsonGeometry | null>(null);
  selectedNeighborhoodIds = signal<Set<string>>(new Set());
  neighborhoods = signal<NeighborhoodDTO[]>([]);
  isochroneOriginLat = signal<number | null>(null);
  isochroneOriginLng = signal<number | null>(null);
  isochroneMinutes = signal<number>(15);
  isochroneError = signal<string | null>(null);

  // Municipality search
  municipalityQuery = '';
  municipalityError = signal<string | null>(null);

  // Map Instance
  map!: L.Map;
  markers: L.Marker[] = [];
  private drawLayer: L.Layer | null = null;
  private isochroneLayer: L.Layer | null = null;

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
      // Track polygon signal first — if present, polygon search takes precedence
      const polygon = this.currentPolygon();
      if (polygon) {
        this.searchWithPolygon();
        return;
      }

      // Fallback to radius-based search
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
    this.loadNeighborhoods();
  }

  onMapReady(map: L.Map): void {
    this.map = map;

    this.map.on('moveend', () => {
      // Only update lat/lng from map pan when in radius mode and no polygon active
      if (this.searchMode() === 'radius' && !this.currentPolygon()) {
        const center = this.map.getCenter();
        this.lat.set(center.lat);
        this.lng.set(center.lng);
      }
    });

    // Initialize draw control when map is ready
    this.initDrawControl();
  }

  private initDrawControl(): void {
    // leaflet-draw integration — activates after npm install leaflet-draw
    try {
      const L_draw = (L as any);
      if (!L_draw.Control?.Draw) return;

      const drawControl = new L_draw.Control.Draw({
        draw: {
          polygon: true,
          polyline: false,
          rectangle: false,
          circle: false,
          circlemarker: false,
          marker: false
        },
        edit: false
      });

      this.map.addControl(drawControl);

      this.map.on(L_draw.Draw.Event.CREATED, (event: any) => {
        // Remove previous draw layer
        if (this.drawLayer) {
          this.map.removeLayer(this.drawLayer);
        }

        this.drawLayer = event.layer;
        this.map.addLayer(event.layer);

        // Convert drawn polygon to GeoJSON geometry
        const geoJson = (event.layer as L.Polygon).toGeoJSON();
        const geometry = geoJson.geometry as GeoJsonGeometry;
        this.currentPolygon.set(geometry);
      });
    } catch {
      // leaflet-draw not installed yet — draw mode will be unavailable
      console.warn('leaflet-draw not available. Run npm install to enable draw mode.');
    }
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

  private searchWithPolygon(): void {
    const polygon = this.currentPolygon();
    if (!polygon) return;
    this.loading.set(true);

    const searchRequest: PropertySearchRequest = {
      polygon: polygon as any,
      minPrice: this.minPrice(),
      maxPrice: this.maxPrice(),
      page: this.page(),
      size: this.size()
    };

    this.propertyService.propertiesSearchPost(searchRequest).subscribe({
      next: (res) => {
        this.properties.set(res.content ?? []);
        this.totalElements.set(res.totalElements ?? 0);
        this.updateMapMarkers(res.content ?? []);
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }

  loadNeighborhoods(): void {
    this.barriosService.neighborhoodsGet().subscribe({
      next: (data) => this.neighborhoods.set(data ?? []),
      error: (err) => console.error('Error loading neighborhoods', err)
    });
  }

  selectNeighborhood(neighborhood: NeighborhoodDTO): void {
    if (!neighborhood.id) return;
    const current = new Set(this.selectedNeighborhoodIds());

    if (current.has(neighborhood.id)) {
      current.delete(neighborhood.id);
    } else {
      current.add(neighborhood.id);
    }

    this.selectedNeighborhoodIds.set(current);

    if (current.size === 0) {
      this.currentPolygon.set(null);
      return;
    }

    // Build MultiPolygon GeoJSON from selected neighborhoods
    const selected = this.neighborhoods().filter(n => n.id && current.has(n.id));
    const allCoordinates: any[][] = [];

    selected.forEach(n => {
      const polygon = n.polygon;
      if (!polygon || !polygon.coordinates) return;
      if (polygon.type === GeoJsonGeometry.TypeEnum.Polygon) {
        allCoordinates.push(polygon.coordinates);
      } else if (polygon.type === GeoJsonGeometry.TypeEnum.MultiPolygon) {
        (polygon.coordinates as any[]).forEach((coords: any[]) => allCoordinates.push(coords));
      }
    });

    if (allCoordinates.length > 0) {
      this.currentPolygon.set({
        type: GeoJsonGeometry.TypeEnum.MultiPolygon,
        coordinates: allCoordinates
      });
    }
  }

  applyIsochrone(): void {
    const lat = this.isochroneOriginLat();
    const lng = this.isochroneOriginLng();
    const minutes = this.isochroneMinutes();

    if (lat === null || lng === null) {
      this.isochroneError.set('Ingresá las coordenadas de origen.');
      return;
    }

    this.isochroneError.set(null);
    this.loading.set(true);

    this.isochroneService.getIsochrone(lat, lng, minutes).subscribe({
      next: (geometry) => {
        // Remove previous isochrone layer if any
        if (this.isochroneLayer) {
          this.map?.removeLayer(this.isochroneLayer);
        }

        // Visualize isochrone on map
        this.isochroneLayer = L.geoJSON({ type: 'Feature', geometry, properties: {} } as any, {
          style: { color: '#3b82f6', fillOpacity: 0.15, weight: 2 }
        }).addTo(this.map);

        this.currentPolygon.set(geometry);
        this.loading.set(false);
      },
      error: () => {
        this.isochroneError.set('Error al obtener la isócrona. Verificá la API key de OpenRouteService.');
        this.loading.set(false);
      }
    });
  }

  clearPolygon(): void {
    this.currentPolygon.set(null);
    this.selectedNeighborhoodIds.set(new Set());
    this.searchMode.set('radius');

    if (this.drawLayer) {
      this.map?.removeLayer(this.drawLayer);
      this.drawLayer = null;
    }

    if (this.isochroneLayer) {
      this.map?.removeLayer(this.isochroneLayer);
      this.isochroneLayer = null;
    }
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

import { Component, OnInit, signal, effect, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { InmueblesService } from '../../../api/api/inmuebles.service';
import { PropertyDTO } from '../../../api/model/propertyDTO';
import * as L from 'leaflet';
import { LeafletModule } from '@asymmetrik/ngx-leaflet';
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

    // Add new markers
    props.forEach(p => {
      if (p.location?.latitude && p.location?.longitude) {
        const marker = L.marker([p.location.latitude, p.location.longitude])
          .bindPopup(`<h5>${p.title}</h5><p>${p.price}€</p>`)
          .addTo(this.map);
        this.markers.push(marker);
      }
    });
  }

  onFilterChange(): void {
    // Handled by effects automatically when signals change
    this.page.set(0);
  }
}

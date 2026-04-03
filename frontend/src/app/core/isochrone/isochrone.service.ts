import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of, throwError } from 'rxjs';
import { map, tap } from 'rxjs/operators';
import { environment } from '../../../environments/environment';

export interface GeoJsonGeometry {
  type: string;
  coordinates: unknown[];
}

@Injectable({ providedIn: 'root' })
export class IsochroneService {
  private readonly http = inject(HttpClient);
  private readonly cache = new Map<string, GeoJsonGeometry>();

  getIsochrone(lat: number, lng: number, minutes: number, profile = 'driving-car'): Observable<GeoJsonGeometry> {
    const cacheKey = `${lat}_${lng}_${minutes}_${profile}`;
    const cached = this.cache.get(cacheKey);
    if (cached) return of(cached);

    const url = `https://api.openrouteservice.org/v2/isochrones/${profile}`;
    const body = {
      locations: [[lng, lat]],
      range: [minutes * 60]
    };

    return this.http.post<{ features: Array<{ geometry: GeoJsonGeometry }> }>(url, body, {
      headers: { Authorization: environment.openRouteServiceApiKey }
    }).pipe(
      map(response => {
        const polygon = response?.features?.[0]?.geometry;
        if (!polygon) throw new Error('No geometry in isochrone response');
        return polygon;
      }),
      tap(polygon => this.cache.set(cacheKey, polygon))
    );
  }
}

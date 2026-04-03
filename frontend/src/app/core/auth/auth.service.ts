import { inject, Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment';
import { tap, map } from 'rxjs/operators';
import { Observable } from 'rxjs';

interface AuthResponse {
  token: string;
  expiresIn: number;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);

  private readonly TOKEN_KEY = 'proptech_token';

  isLoggedIn = signal<boolean>(!!localStorage.getItem(this.TOKEN_KEY));

  login(email: string, password: string): Observable<void> {
    return this.http.post<AuthResponse>(`${environment.apiUrl}/api/v1/auth/login`, { email, password }).pipe(
      tap(res => {
        localStorage.setItem(this.TOKEN_KEY, res.token);
        this.isLoggedIn.set(true);
      }),
      map(() => void 0)
    );
  }

  register(name: string, email: string, password: string): Observable<void> {
    return this.http.post<AuthResponse>(`${environment.apiUrl}/api/v1/auth/register`, { name, email, password }).pipe(
      tap(res => {
        localStorage.setItem(this.TOKEN_KEY, res.token);
        this.isLoggedIn.set(true);
      }),
      map(() => void 0)
    );
  }

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    this.isLoggedIn.set(false);
    this.router.navigate(['/login']);
  }
}

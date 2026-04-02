import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { NgIf } from '@angular/common';
import { AuthService } from '../../core/auth/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, NgIf, RouterLink],
  template: `
    <div class="min-h-screen bg-gradient-to-br from-slate-50 to-blue-50 flex items-center justify-center p-4">
      <div class="w-full max-w-md">

        <!-- Logo -->
        <div class="text-center mb-8">
          <div class="w-16 h-16 bg-blue-600 rounded-2xl flex items-center justify-center text-white text-3xl shadow-xl shadow-blue-200 mx-auto mb-3">
            P
          </div>
          <h1 class="text-2xl font-black text-slate-800 tracking-tighter uppercase">PropTech</h1>
          <p class="text-slate-500 text-sm mt-1">La plataforma de confianza para el alquiler</p>
        </div>

        <!-- Card -->
        <div class="bg-white rounded-2xl shadow-xl p-8">

          <!-- Tabs -->
          <div class="flex rounded-xl bg-slate-100 p-1 mb-6">
            <button
              (click)="activeTab.set('login')"
              [class]="activeTab() === 'login' ? 'flex-1 py-2 rounded-lg bg-white text-slate-800 font-bold text-sm shadow-sm transition-all' : 'flex-1 py-2 rounded-lg text-slate-500 font-semibold text-sm transition-all hover:text-slate-700'">
              Iniciar sesión
            </button>
            <button
              (click)="activeTab.set('register')"
              [class]="activeTab() === 'register' ? 'flex-1 py-2 rounded-lg bg-white text-slate-800 font-bold text-sm shadow-sm transition-all' : 'flex-1 py-2 rounded-lg text-slate-500 font-semibold text-sm transition-all hover:text-slate-700'">
              Registrarse
            </button>
          </div>

          <!-- Error -->
          <div *ngIf="errorMsg()" class="mb-4 p-3 bg-red-50 border border-red-200 rounded-lg text-red-600 text-sm">
            {{ errorMsg() }}
          </div>

          <!-- Login Form -->
          <form *ngIf="activeTab() === 'login'" [formGroup]="loginForm" (ngSubmit)="onLogin()" class="space-y-4">
            <div>
              <label class="block text-sm font-semibold text-slate-700 mb-1">Email</label>
              <input type="email" formControlName="email" placeholder="tu@email.com"
                class="w-full px-4 py-3 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm transition-all">
            </div>
            <div>
              <label class="block text-sm font-semibold text-slate-700 mb-1">Contraseña</label>
              <input type="password" formControlName="password" placeholder="••••••••"
                class="w-full px-4 py-3 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm transition-all">
            </div>
            <button type="submit" [disabled]="loginForm.invalid || isLoading()"
              class="w-full bg-blue-600 text-white py-3 rounded-xl font-bold text-sm hover:bg-blue-700 disabled:opacity-50 transition-all active:scale-95 shadow-lg shadow-blue-200">
              {{ isLoading() ? 'Entrando...' : 'Iniciar sesión' }}
            </button>
          </form>

          <!-- Register Form -->
          <form *ngIf="activeTab() === 'register'" [formGroup]="registerForm" (ngSubmit)="onRegister()" class="space-y-4">
            <div>
              <label class="block text-sm font-semibold text-slate-700 mb-1">Nombre completo</label>
              <input type="text" formControlName="name" placeholder="Juan Pérez"
                class="w-full px-4 py-3 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm transition-all">
            </div>
            <div>
              <label class="block text-sm font-semibold text-slate-700 mb-1">Email</label>
              <input type="email" formControlName="email" placeholder="tu@email.com"
                class="w-full px-4 py-3 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm transition-all">
            </div>
            <div>
              <label class="block text-sm font-semibold text-slate-700 mb-1">Contraseña</label>
              <input type="password" formControlName="password" placeholder="Mínimo 8 caracteres"
                class="w-full px-4 py-3 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm transition-all">
            </div>
            <button type="submit" [disabled]="registerForm.invalid || isLoading()"
              class="w-full bg-slate-900 text-white py-3 rounded-xl font-bold text-sm hover:bg-slate-800 disabled:opacity-50 transition-all active:scale-95 shadow-lg shadow-slate-200">
              {{ isLoading() ? 'Creando cuenta...' : 'Crear cuenta' }}
            </button>
          </form>

        </div>

        <p class="text-center text-slate-400 text-xs mt-6">
          Al continuar, aceptas nuestros Términos de Servicio y Política de Privacidad.
        </p>
      </div>
    </div>
  `
})
export class LoginComponent {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);

  activeTab = signal<'login' | 'register'>('login');
  isLoading = signal(false);
  errorMsg = signal<string | null>(null);

  loginForm = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', Validators.required]
  });

  registerForm = this.fb.group({
    name: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8)]]
  });

  onLogin(): void {
    if (this.loginForm.invalid) return;
    this.isLoading.set(true);
    this.errorMsg.set(null);
    const { email, password } = this.loginForm.value;
    this.authService.login(email!, password!).subscribe({
      next: () => this.router.navigate(['/search']),
      error: () => {
        this.errorMsg.set('Email o contraseña incorrectos.');
        this.isLoading.set(false);
      }
    });
  }

  onRegister(): void {
    if (this.registerForm.invalid) return;
    this.isLoading.set(true);
    this.errorMsg.set(null);
    const { name, email, password } = this.registerForm.value;
    this.authService.register(name!, email!, password!).subscribe({
      next: () => this.router.navigate(['/search']),
      error: () => {
        this.errorMsg.set('No se pudo crear la cuenta. El email ya puede estar registrado.');
        this.isLoading.set(false);
      }
    });
  }
}

import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
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

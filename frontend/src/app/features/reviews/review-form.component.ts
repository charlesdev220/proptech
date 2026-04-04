import { ChangeDetectionStrategy, Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { ReseasService } from '../../../api/api/reseas.service';
import { ReviewTokenInfoDTO } from '../../../api/model/reviewTokenInfoDTO';
import { ReviewCreateRequest } from '../../../api/model/reviewCreateRequest';

@Component({
  selector: 'app-review-form',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './review-form.component.html',
  styleUrls: ['./review-form.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReviewFormComponent implements OnInit {
  private readonly reseasService = inject(ReseasService);
  private readonly route = inject(ActivatedRoute);

  tokenInfo = signal<ReviewTokenInfoDTO | null>(null);
  loading = signal(true);
  submitting = signal(false);
  submitted = signal(false);
  error = signal<string | null>(null);
  submitError = signal<string | null>(null);
  dimensions = signal<Record<string, number>>({});

  ngOnInit(): void {
    const token = this.route.snapshot.paramMap.get('token');
    if (!token) {
      this.error.set('Token inválido.');
      this.loading.set(false);
      return;
    }
    this.reseasService.reviewsTokenTokenGet(token).subscribe({
      next: (info) => {
        this.tokenInfo.set(info);
        this.loading.set(false);
      },
      error: (err) => {
        const status = err.status;
        if (status === 403) this.error.set('Este enlace no te pertenece.');
        else if (status === 410) this.error.set('Este enlace de valoración ha expirado.');
        else if (status === 409) this.error.set('Ya has utilizado este enlace de valoración.');
        else this.error.set('No se pudo cargar el formulario de valoración.');
        this.loading.set(false);
      }
    });
  }

  setDimension(dim: string, value: number): void {
    this.dimensions.update(d => ({ ...d, [dim]: value }));
  }

  getDimensionValue(dim: string): number {
    return this.dimensions()[dim] ?? 0;
  }

  isFormComplete(): boolean {
    const dims = this.dimensions();
    const expected = this.tokenInfo()?.expectedDimensions ?? [];
    return expected.every(d => dims[d] >= 1 && dims[d] <= 5);
  }

  submit(): void {
    const token = this.route.snapshot.paramMap.get('token');
    if (!token || !this.isFormComplete()) return;

    this.submitting.set(true);
    this.submitError.set(null);

    const req: ReviewCreateRequest = {
      token: token as unknown as string,
      dimensions: this.dimensions()
    };

    this.reseasService.reviewsPost(req).subscribe({
      next: () => {
        this.submitting.set(false);
        this.submitted.set(true);
      },
      error: (err) => {
        this.submitting.set(false);
        const status = err.status;
        if (status === 400) this.submitError.set('Hay un problema con las dimensiones enviadas.');
        else if (status === 409) this.submitError.set('Este enlace ya fue utilizado.');
        else if (status === 410) this.submitError.set('El enlace de valoración ha expirado.');
        else this.submitError.set('Error al enviar. Intentá de nuevo.');
      }
    });
  }
}
